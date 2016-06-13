package com.kekonyan.aromatique.state;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import com.kekonyan.aromatique.R;
import com.kekonyan.aromatique.UI.Button;
import com.kekonyan.aromatique.audio.Music;
import com.kekonyan.aromatique.audio.Sfx;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.core.Control;
import com.kekonyan.aromatique.game.level.BasicLevel;
import com.kekonyan.aromatique.util.Const;

import java.io.IOException;

class GameState extends BasicState {
    private enum State {ACTIVE, PAUSE, LOSE, WIN}
    private State state;
    private BasicLevel level;
    private Button resume, quit, retry;
    private Control control;
    private Paint header;

    @Override
    public void preload() throws IOException {
        header=new Paint(Assets.headerPaint);
        header.setColor(Color.WHITE);
        Paint regular = new Paint(Assets.regularPaint);
        regular.setColor(Color.WHITE);
        quit=new Button(context.getString(R.string.Quit), 2*width/3, 8*height/15, regular, Color.WHITE, Color.rgb(195,195,195), false);
        resume=new Button(context.getString(R.string.resume), width/3, 8*height/15, regular, Color.WHITE, Color.rgb(195,195,195), true);
        retry=new Button(context.getString(R.string.retry), width/3, 8*height/15, regular, Color.WHITE, Color.rgb(195,195,195), true);
        control=playerData.control;
        control.reset();
        state=State.ACTIVE;
        music.setMode(Music.Mode.GAME);
    }
        GameState(Context context, StateManager stateManager, BasicLevel level) {
            super(context, stateManager);
            this.level=level;
    }

    @Override
    public void render(Canvas canvas) {
        level.render(canvas);
        canvas.drawBitmap(Assets.Game.objects[Assets.Game.Object.PREQUARK.ordinal()], 0, 0, null);
        canvas.drawText("x".concat(String.valueOf(level.collectedPrequarks)), 3 * Const.PPM / 2, height / 20, Assets.hintPaint);
        control.render(canvas);
        switch (state) {
            case PAUSE: {
                canvas.drawColor(Color.argb(60, 0, 0, 0));
                canvas.drawText(context.getString(R.string.pause), width / 2, height / 3, header);
                resume.render(canvas);
                quit.render(canvas);
            }
            break;
            case LOSE: {
                canvas.drawColor(Color.argb(60, 0, 0, 0));
                canvas.drawText(context.getString(R.string.u_died), width / 2, height / 3, header);
                retry.render(canvas);
                quit.render(canvas);
            }
            break;
            case WIN: {
                canvas.drawColor(Color.argb(60, 0, 0, 0));
                canvas.drawText(context.getString(R.string.win_popup_text), width / 2, height / 3, header);
                quit.render(canvas);
            }
        }
    }

    @Override
    public void update(float elapsedTime) {
        switch (state) {
            case ACTIVE: {
                level.update(elapsedTime);
                if (level.result==BasicLevel.Result.LOSE) {
                    playerData.deathsByLevels[level.aroma.ordinal()]++;
                    state = State.LOSE;
                }
                if (level.result ==BasicLevel.Result.WIN) {
                    state = State.WIN;
                    sfx.play(Sfx.GAME_WIN);
                }
            }
            case PAUSE: playerData.timeByLevels[level.aroma.ordinal()]+=elapsedTime; break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (state){
            case ACTIVE: control.onTouch(motionEvent); break;
            case PAUSE: {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP: {
                        if (resume.onTouch(view, motionEvent)) {
                            state = State.ACTIVE;
                            break;
                        }
                        if (quit.onTouch(view, motionEvent))
                            quit();
                    } break;
                    default: {
                        resume.onTouch(view, motionEvent);
                        quit.onTouch(view, motionEvent);
                    }
                }
            } break;
            case LOSE: {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_UP:
                        if (quit.onTouch(view, motionEvent))
                            quit();
                        if (retry.onTouch(view, motionEvent)) {
                            level.restart();
                            control.reset();
                            state = State.ACTIVE;
                        }
                    default:
                        quit.onTouch(view, motionEvent);
                        retry.onTouch(view, motionEvent);
                }
            } break;
            case WIN: {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        if (quit.onTouch(view, motionEvent))
                            quit();
                    default: quit.onTouch(view, motionEvent);
                }
            }
        }
        return true;
    }

    private void quit(){
        music.setMode(Music.Mode.MENU);
        getStateManager().setState(StateManager.State.LEVEL, false);
    }

    @Override
    public boolean onBackPressed() {
        switch (state){
            case ACTIVE: state=State.PAUSE; break;
            case PAUSE:  state=State.ACTIVE; break;
        }
        return true;
    }

}
