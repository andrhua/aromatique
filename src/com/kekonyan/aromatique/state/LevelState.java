package com.kekonyan.aromatique.state;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import com.kekonyan.aromatique.GameActivity;
import com.kekonyan.aromatique.R;
import com.kekonyan.aromatique.UI.Background;
import com.kekonyan.aromatique.UI.Button;
import com.kekonyan.aromatique.UI.NotifyPopup;
import com.kekonyan.aromatique.UI.shimmer.Shimmer;
import com.kekonyan.aromatique.UI.shimmer.ShimmerTextView;
import com.kekonyan.aromatique.audio.Sfx;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.game.level.LevelManager;
import com.kekonyan.aromatique.model.GameLevelPreview;
import com.kekonyan.aromatique.util.Const;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

class LevelState extends BasicState implements IProccessable {
    private Button back;
    private Background background;
    private GameLevelPreview gameLevelPreviews[];
    private NotifyPopup lockedPopup, nextLifePopup;
    private LinearLayout linearLayout;
    private ShimmerTextView textView;
    private Shimmer shimmer;
    private LevelManager levelManager;
    private int selected;

    private enum State{CHOOSING, LOADING}
    private State state;

    @Override
    public void preload() throws IOException {
        int i= Const.BUTTON_BMP;
        back=new Button(Assets.buttons[Assets.Button.BACK.ordinal()],i/2, height-i, i/2, 0, false);
        background=new Background(Assets.backgrounds[Assets.Background.FLOWER.ordinal()]);
        lockedPopup=new NotifyPopup(context.getString(R.string.level_locked));
        nextLifePopup=new NotifyPopup(context.getString(R.string.next_life));
        gameLevelPreviews =new GameLevelPreview[6];
        for (int j=0; j<6; j++){
            gameLevelPreviews[j]=new GameLevelPreview(j);
        }
        state=State.CHOOSING;
        textView=new ShimmerTextView(context);
        shimmer=new Shimmer();
        linearLayout=new LinearLayout(context);
        textView.setText(context.getString(R.string.loading));
        textView.setWidth(width);
        textView.setHeight(height);
        textView.setGravity(Gravity.CENTER);
        textView.setLayerType(View.LAYER_TYPE_HARDWARE,null);
        linearLayout.addView(textView);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, Const.TEXT_HINT);
        textView.setTypeface(Typeface.createFromAsset(GameActivity.assetManager, "font/logo.otf"));
        textView.setTextColor(Color.rgb(50,50,50));
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.measure(width,height);
        linearLayout.layout(0,0,width,height);
    }

    LevelState(Context context, StateManager stateManager) {
        super(context, stateManager);
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
        switch (state){
            case CHOOSING:{
                background.render(canvas);
                canvas.drawText(context.getString(R.string.level_title), width/2, height/8, Assets.headerPaint);
                for (int i=0; i<6; i++) gameLevelPreviews[i].render(canvas);
                back.render(canvas);
                nextLifePopup.render(canvas);
                lockedPopup.render(canvas);
            } break;
            case LOADING:
                canvas.drawColor(GameActivity.resources.getIntArray(R.array.aromaColors)[selected]);
                linearLayout.draw(canvas);
        }

    }

    @Override
    public void update(float elapsedTime) {
        switch (state){
            case CHOOSING: background.update(elapsedTime); break;
            case LOADING: if (levelManager !=null&& levelManager.getStatus()== AsyncTask.Status.FINISHED)
                try {
                    getStateManager().setGameState(levelManager.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int x=(int)motionEvent.getX(), y=(int)motionEvent.getY();
        if (state==State.CHOOSING) switch (motionEvent.getAction()){
            case MotionEvent.ACTION_UP: {
                if (lockedPopup.isActive()) {
                    if (lockedPopup.ok.onTouch(view, motionEvent)) lockedPopup.setIdleState();
                    break;
                }
                if (nextLifePopup.isActive()){
                    if(nextLifePopup.ok.onTouch(view, motionEvent)) nextLifePopup.setIdleState();
                    break;
                }
                if (back.onTouch(view, motionEvent)) {
                    getStateManager().setState(StateManager.State.PRELOOK, false);
                    break;
                }
                process(x,y);
            } break;
            default: {
                if (lockedPopup.isActive()){
                    lockedPopup.onTouch(view, motionEvent);
                    break;
                }
                if (nextLifePopup.isActive()){
                    nextLifePopup.onTouch(view, motionEvent);
                }
                back.onTouch(view,motionEvent);
            }
        }
        return true;
    }

    @Override
    public boolean onBackPressed() {
        switch (state){
            case CHOOSING: {
                if (!lockedPopup.onBackPressed()&&!nextLifePopup.onBackPressed()) getStateManager().setState(StateManager.State.PRELOOK, false);
                return true;
            }
        }
        return false;
    }

    @Override
    public void process(int x, int y) {
        for (int i=0; i<6; i++){
            if (gameLevelPreviews[i].touch(x,y)&&gameLevelPreviews[i].isActive()) {
                selected=i;
                sfx.play(Sfx.CLICK_POSITIVE);
                state=State.LOADING;
                shimmer.setDuration(1000).start(textView);
                levelManager =new LevelManager(LevelManager.Aroma.values()[i]);
                levelManager.execute();
            } else
            if (gameLevelPreviews[i].touch(x,y)) {
                sfx.play(Sfx.CLICK_POSITIVE);
                if (i > 2)
                    nextLifePopup.setActiveState();
                else lockedPopup.setActiveState();
            }
        }
    }
}
