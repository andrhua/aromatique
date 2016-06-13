package com.kekonyan.aromatique.model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import com.kekonyan.aromatique.GameActivity;
import com.kekonyan.aromatique.R;
import com.kekonyan.aromatique.UI.BasicActivatable;
import com.kekonyan.aromatique.UI.ITouchable;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.core.IRenderable;
import com.kekonyan.aromatique.util.Const;

public class GameLevelPreview extends BasicActivatable implements IRenderable, ITouchable {
    private int width, height;
    private String name;
    private Paint levelPaint, lockedPaint, regularPaint;
    private Rect preview;
    private int number;

    public GameLevelPreview(int number){
        this.number=number;
        if (GameActivity.playerData.aromas-1>=number) setActiveState(); else setIdleState();
        width= Const.WIDTH; height=Const.HEIGHT;
        name= GameActivity.resources.getStringArray(R.array.levelName)[number];
        int color = GameActivity.resources.getIntArray(R.array.aromaColors)[number];
        levelPaint=new Paint();
        levelPaint.setColor(color);
        lockedPaint=new Paint();
        lockedPaint.setColor(Color.argb(140,0,0,0));
        regularPaint=new Paint(Assets.regularPaint);
        regularPaint.setColor(number>2?Color.WHITE:Color.BLACK);
        int i=number/3, j=number-i*3;
        preview=new Rect((j+1)*width/4-width/10, (i+1)*height/3-height/10, (j+1)*width/4+width/10, (i+1)*height/3+height/10);
    }

    @Override
    public void render(Canvas canvas) {
        canvas.drawRect(preview, levelPaint);
        canvas.drawBitmap(Assets.previewBG[number], preview.left+1, preview.top+1, null);
        canvas.drawText(name, preview.left+width/10, preview.top+height/8, regularPaint);
        if (!isActive()) {
            canvas.drawRect(preview, lockedPaint);
            canvas.drawBitmap(Assets.other[Assets.Other.LOCKED.ordinal()], preview.right-width/10-height/16, preview.bottom-height/10-height/16, null);
        }
    }

    @Override
    public boolean touch(int x, int y) {
        return preview.contains(x,y);
    }
}
