package com.kekonyan.aromatique.state;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import com.kekonyan.aromatique.GameActivity;
import com.kekonyan.aromatique.UI.BasicActivatable;
import com.kekonyan.aromatique.audio.Sfx;
import com.kekonyan.aromatique.core.GameThread;
import com.kekonyan.aromatique.core.IBackable;
import com.kekonyan.aromatique.core.IRenderable;
import com.kekonyan.aromatique.core.IUpdatable;
import com.kekonyan.aromatique.game.level.BasicLevel;
import com.kekonyan.aromatique.model.Container;
import com.kekonyan.aromatique.util.Const;

public class StateManager extends BasicActivatable implements IBackable, IRenderable, IUpdatable, View.OnTouchListener {
    private GameThread gameThread;
    private BasicState currentState, previousState;
    private Context context;
    public enum State {SPLASH, MENU, SETTINGS, STATS, CONTAINER, WARDROBE, PRELOOK, INVENTORY, INTRODUCTION, INFO, LEVEL}
    private boolean isGame, isForwardAnim;
    private float vX, vY;

    GameThread getThread() {
        return gameThread;
    }

    public StateManager(GameThread gameThread) {
        context=gameThread.gameView.getContext();
        this.gameThread = gameThread;
        setIdleState();
        isGame=false;
    }

    @Override
    public void setIdleState() {
        super.setIdleState();
        previousState=null;
    }

    public void setState(State state, boolean isForward){
        isGame=false;
        setActiveState();
        this.isForwardAnim = isForward;
        vY = isForward ? Const.HEIGHT : 0;
        previousState = this.currentState;
        switch(state){
            case SPLASH: currentState =new SplashState(context,this); setIdleState(); break;
            case INTRODUCTION: this.currentState=new IntroductionState(context, this); break;
            case MENU:this.currentState =new MenuState(context,this);break;
            case SETTINGS: this.currentState =new SettingsState(context, this); break;
            case STATS: this.currentState =new StatsState(context,this); break;
            case CONTAINER: this.currentState =new ContainerState(context,this); break;
            case WARDROBE: this.currentState =new WardrobeState(context,this); break;
            case PRELOOK: this.currentState =new PrelookState(context,this); break;
            case INVENTORY: this.currentState =new InventoryState(context,this); break;
            case LEVEL:
                if (currentState instanceof GameState) setIdleState();
                this.currentState =new LevelState(context, this);
                break;
            case INFO: this.currentState=new InfoState(context, this); break;
        }
    }

    void setGameState(BasicLevel level) {
        isGame=true;
        currentState =new GameState(context, this, level);
    }

    void setMenuState() {
        this.currentState =new MenuState(context,this);
    }

    void setOpenState(Container container){
        isGame=false;
        isForwardAnim=true;
        setActiveState();
        previousState=this.currentState;
        currentState =new OpenState(context, this, container, currentState instanceof ContainerState);
    }

    @Override
    public void render(Canvas canvas) {
        if (isActive()){
            if (isForwardAnim){
                previousState.render(canvas);
                canvas.save();
                canvas.translate(0, vY);
                currentState.render(canvas);
                canvas.restore();
            } else {
                currentState.render(canvas);
                canvas.save();
                canvas.translate(0, -vY);
                previousState.render(canvas);
                canvas.restore();
            }
        } else currentState.render(canvas);
    }

    @Override
    public void update(float elapsedTime){
        if (isActive()){
            previousState.update(elapsedTime);
            vY-=elapsedTime*Const.HEIGHT/300;
            if ((vY<=-Const.HEIGHT&&!isForwardAnim)||(vY<=0&&isForwardAnim)) {
                vY = (isForwardAnim?1:-1)*Const.HEIGHT;
                setIdleState();
            }
        }
        currentState.update(elapsedTime);
        if (isGame) GameActivity.playerData.timeInGame+=elapsedTime; else
            GameActivity.playerData.timeInMenu+=elapsedTime;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return currentState.onTouch(view, motionEvent);
    }

    @Override
    public boolean onBackPressed(){
        boolean res=currentState.onBackPressed();
        if (res) GameActivity.sfx.play(Sfx.CLICK_NEGATIVE);
        return res;
    }

}
