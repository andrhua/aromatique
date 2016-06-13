package com.kekonyan.aromatique.core;

import android.graphics.Canvas;
import com.kekonyan.aromatique.state.StateManager;

public class GameThread extends Thread {
    public final GameView gameView;
    private StateManager stateManager;
    private boolean isRunning;
    private float surfaceHeight;
    private float surfaceWidth;

    GameThread(GameView gameView) {
        this.gameView = gameView;
        isRunning = true;
        stateManager = new StateManager(this);
    }

    @Override
    public void run() {
        long lastTime = System.currentTimeMillis(), newTime;
        while (isRunning) {
            float elapsedTime = ((newTime = System.currentTimeMillis()) - lastTime);
            lastTime = newTime;
            stateManager.update(elapsedTime);
            Canvas canvas = gameView.getHolder().lockCanvas();
            stateManager.render(canvas);
            gameView.getHolder().unlockCanvasAndPost(canvas);
        }
    }

    public void stopGameLoop() {
        isRunning = false;
        while (this.isAlive()) {
            try {
                this.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public float getSurfaceWidth() { return surfaceWidth; }

    public float getSurfaceHeight() {
        return surfaceHeight;
    }

    public void setSurfaceSize(int width, int height) {
        surfaceWidth=width;
        surfaceHeight=height;
    }

    public StateManager getStateManager() {
        return stateManager;
    }

}
