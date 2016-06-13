package com.kekonyan.aromatique.UI;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import com.kekonyan.aromatique.GameActivity;
import com.kekonyan.aromatique.audio.Sfx;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.core.IRenderable;
import com.kekonyan.aromatique.util.Const;

public class Checkbox implements IRenderable, View.OnTouchListener{
    private boolean isChecked;
    private String text;
    private Rect checkboxBounds;
    private Paint paint;

    public Checkbox(boolean isChecked, String text, int centerX, int centerY){
        this.isChecked=isChecked;
        this.text=text;
        checkboxBounds=new Rect(centerX+Const.WIDTH/20, centerY+Const.WIDTH/50, centerX+9*Const.WIDTH/100, centerY+3*Const.WIDTH/50);
        paint=new Paint(Assets.hintPaint);
        paint.setTextAlign(Paint.Align.RIGHT);
    }

    public void setChecked(boolean isChecked){
        this.isChecked=isChecked;
    }

    public boolean isChecked(){
        return isChecked;
    }

    @Override
    public void render(Canvas canvas) {
        canvas.drawText(text, checkboxBounds.left-Const.WIDTH/10, checkboxBounds.centerY()+Const.HEIGHT/40, paint);
        canvas.drawRect(checkboxBounds, Assets.aromaPaint);
        if (isChecked) canvas.drawBitmap(Assets.other[Assets.Other.DRESSED.ordinal()],
                checkboxBounds.centerX()-Const.WIDTH/100,
                checkboxBounds.centerY()-Const.WIDTH/100,
                null);
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction()==MotionEvent.ACTION_UP && checkboxBounds.contains((int)motionEvent.getX(), (int)motionEvent.getY())) {
            GameActivity.sfx.play(Sfx.CLICK_POSITIVE);
            isChecked = !isChecked;
        }
        return false;
    }
}
