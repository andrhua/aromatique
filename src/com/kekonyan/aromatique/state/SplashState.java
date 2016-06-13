package com.kekonyan.aromatique.state;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.View;
import com.kekonyan.aromatique.GameActivity;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.core.Control;

import java.io.IOException;

class SplashState extends BasicState {
    private Paint bgPaint, logoPaint;
    private enum State {FADE_IN, ACTIVE, FADE_OUT}
    private State state;
    private float time, endOfLoad;
    private boolean loaded;

    public void setState(State state) {
        this.state = state;
        switch (state){
            case FADE_IN: logoPaint.setAlpha(0); break;
            case ACTIVE: logoPaint.setAlpha(255); break;
            case FADE_OUT:
                if (!loaded) {
                    loaded = true;
                    playerData.control = new Control();
                    endOfLoad = time;
                }
                break;
        }
    }

    SplashState(Context context, StateManager stateManager) {
        super(context, stateManager);
    }

    @Override
    public void preload() throws IOException {
        bgPaint =new Paint(Assets.hintPaint);
        bgPaint.setColor(Color.BLACK);
        logoPaint=new Paint(Assets.hintPaint);
        logoPaint.setColor(Color.WHITE);
        logoPaint.setTypeface(Typeface.createFromAsset(GameActivity.assetManager, "font/logo.otf"));
        time=0;
        setState(State.FADE_IN);
    }

    @Override
    public void render(Canvas canvas) {
        canvas.drawRect(0, 0, width, height, bgPaint);
        canvas.drawText("KEKONYAN GAMES", width/2, height/2+height/40, logoPaint);
    }

    @Override
    public void update(float elapsedTime) {
        int delta;
        time+=elapsedTime;
        if (time>=endOfLoad+1500 && state==State.FADE_OUT) {
            music.start();
            if (playerData.isIntroductionShown) getStateManager().setMenuState();else
                getStateManager().setState(StateManager.State.INTRODUCTION, true);
        } else
        if (time>=2000&&GameActivity.assets.getStatus()== AsyncTask.Status.FINISHED) setState(State.FADE_OUT); else
        if (time>=1500) setState(State.ACTIVE);
        switch (state){
            case FADE_IN:
                delta= logoPaint.getAlpha()+(int)(elapsedTime*.300f);
                if (delta<=255) logoPaint.setAlpha(delta);
                break;
            case FADE_OUT:
                delta=logoPaint.getAlpha()-(int)(elapsedTime*.300f);
                if (delta>=0) logoPaint.setAlpha(delta);
                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return true;
    }

    @Override
    public boolean onBackPressed(){
        return false;
    }
}
