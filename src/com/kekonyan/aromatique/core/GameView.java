package com.kekonyan.aromatique.core;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import com.kekonyan.aromatique.state.StateManager;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener, IBackable{
    public GameThread gameThread;

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        getHolder().setFormat(PixelFormat.RGB_565);
        gameThread=new GameThread(this);
        setOnTouchListener(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        gameThread.setSurfaceSize(getWidth(), getHeight());
        gameThread.getStateManager().setState(StateManager.State.SPLASH, false);
        gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        gameThread.stopGameLoop();
    }

    @Override
    public boolean onBackPressed(){return gameThread.getStateManager().onBackPressed();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return gameThread.getStateManager().onTouch(view, motionEvent);
    }

}
