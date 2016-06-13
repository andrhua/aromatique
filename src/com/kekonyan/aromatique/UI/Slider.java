package com.kekonyan.aromatique.UI;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.core.IRenderable;

public class Slider extends BasicActivatable implements View.OnTouchListener, IRenderable {
    private int left, radius;
    private float position;
    private Rect circle, slider;

    public Slider (int left, int top, int sliderWidth){
        this.left=left;
        radius=sliderWidth/15;
        slider=new Rect(left, top, left+sliderWidth, top+2*radius);
        circle=new Rect(slider.left, slider.top, slider.left+2*radius, slider.bottom);
        position=0;
        setIdleState();
    }

    public void setPosition(float position){
        if (position>1) position=1;
        if (position<0) position=0;
        this.position=position;
        circle.offsetTo(left+(int)(position*(slider.width()-2*radius)),slider.top);
    }

    public int getPosition(){
        return (int)Math.floor(position*100);
    }

    @Override
    public void render(Canvas canvas) {
        Assets.headerPaint.setColor(Color.rgb(25,25,25));
        canvas.drawLine(slider.left+radius, slider.top+radius, slider.right-radius, slider.top+radius, Assets.headerPaint);
        Assets.headerPaint.setColor(Color.BLACK);
        canvas.drawCircle(circle.left+radius, circle.top+radius, radius, Assets.headerPaint);
        canvas.drawText(String.valueOf((int)(position*100)), left+slider.width()/2, slider.top, Assets.hintPaint);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int x = (int) motionEvent.getX(), y = (int) motionEvent.getY();
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:{
                if (circle.contains(x,y)) {setActiveState(); break;}
                if (slider.contains(x,y)) {
                    setActiveState();
                    setPosition((float) (x-(left+radius))/(slider.width()-2*radius));
                    break;
                }
            } break;
            case MotionEvent.ACTION_MOVE:{
                if (isActive()) setPosition((float) (x-(left+radius))/(slider.width()-2*radius));
            } break;
            case MotionEvent.ACTION_UP:{
                setIdleState();
            }
        }
        return true;
    }

}
