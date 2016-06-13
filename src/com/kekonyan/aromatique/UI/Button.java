package com.kekonyan.aromatique.UI;

import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;
import com.kekonyan.aromatique.GameActivity;
import com.kekonyan.aromatique.audio.Sfx;
import com.kekonyan.aromatique.core.IRenderable;
import com.kekonyan.aromatique.util.Const;

public class Button extends BasicActivatable implements IRenderable, ITouchable, View.OnTouchListener {
    private boolean isPositive, soundless;
    private Rect rect;
    private String text;
    private int x, y, regular, pressed;
    private Paint textPaint, rectPaint;
    private Bitmap bitmap;
    private static int width= Const.WIDTH;

    public void setSoundless(){
        soundless=true;
    }

    @Override
    public void setActiveState() {
        super.setActiveState();
        if (rectPaint==null) rectPaint=new Paint();
        rectPaint.setColor(pressed);
        rectPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void setIdleState() {
        super.setIdleState();
        if (rectPaint==null) rectPaint=new Paint();
        rectPaint.setColor(regular);
        rectPaint.setStyle(Paint.Style.STROKE);
    }

    public Button (String text, int x, int y, Paint paint, int colorRegular, int colorPressed, boolean isPositive){
        rect=new Rect(x-(int)paint.measureText(text)/2-width/32, y-(int)(7*paint.getTextSize()/6), x+(int)paint.measureText(text)/2+width/32, y+(int)paint.getTextSize()/2);
        this.text=text;
        this.x=x;
        this.y=y;
        textPaint=new Paint(paint);
        this.regular=colorRegular;
        this.pressed=colorPressed;
        this.isPositive=isPositive;
        setIdleState();
    }

    public Button (Bitmap bitmap, int x, int y, int sideIndent, int frontIndent, boolean isPositive){
        rect=new Rect(x-sideIndent, y-frontIndent, x+bitmap.getWidth()+sideIndent, y+bitmap.getHeight()+frontIndent);
        this.bitmap=bitmap;
        this.x=x;
        this.y=y;
        this.regular=Color.TRANSPARENT;
        this.pressed=Color.argb(100,220,220,220);
        this.isPositive=isPositive;
        setIdleState();
    }

    @Override
    public void render(Canvas canvas) {
        canvas.drawRect(rect, rectPaint);
        if (text!=null) canvas.drawText(text, x, y, textPaint); else canvas.drawBitmap(bitmap, x, y, null);
    }

    @Override
    public boolean touch(int x, int y) {
        return rect.contains(x,y);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int x=(int)motionEvent.getX(), y=(int)motionEvent.getY();
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:{
                if (touch(x,y)) setActiveState();
                return touch(x,y);
            }
            case MotionEvent.ACTION_MOVE:{
                if (!touch(x,y)) setIdleState(); else setActiveState();
                return !touch(x,y);
            }
            case MotionEvent.ACTION_UP:{
                if (touch(x,y)) {
                    setIdleState();
                    if (!soundless)
                        GameActivity.sfx.play(isPositive? Sfx.CLICK_POSITIVE: Sfx.CLICK_NEGATIVE);
                }
                return touch(x,y);
            }
        }
        return false;
    }
}

