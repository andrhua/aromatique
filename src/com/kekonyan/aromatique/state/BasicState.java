package com.kekonyan.aromatique.state;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import com.kekonyan.aromatique.GameActivity;
import com.kekonyan.aromatique.audio.Music;
import com.kekonyan.aromatique.audio.Sfx;
import com.kekonyan.aromatique.core.*;
import com.kekonyan.aromatique.util.Const;

import java.io.IOException;

public abstract class BasicState implements IBackable, IUpdatable, IRenderable, View.OnTouchListener {
    protected Context context;
    private StateManager stateManager;
    protected final PlayerData playerData= GameActivity.playerData;
    protected final Language language=GameActivity.language;
    protected final Music music =GameActivity.music;
    protected final Sfx sfx = GameActivity.sfx;
    protected final int width=Const.WIDTH, height=Const.HEIGHT;
    private Paint paint;


    public BasicState(Context context, StateManager stateManager) {
        paint=new Paint();
        paint.setColor(Color.WHITE);
        this.context = context;
        this.stateManager = stateManager;
        try {
            preload();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(Canvas canvas) {
        canvas.drawRect(0, 0, width, height, paint);
    }

    abstract void preload() throws IOException;

    public StateManager getStateManager() {
        return stateManager;
    }

    public float getWidth() { return getStateManager().getThread().getSurfaceWidth(); }

    public float getHeight() {
        return getStateManager().getThread().getSurfaceHeight();
    }
}
