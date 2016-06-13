package com.kekonyan.aromatique.UI;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.kekonyan.aromatique.GameActivity;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.core.IRenderable;
import com.kekonyan.aromatique.util.Const;

public class ScrollText extends BasicActivatable implements IRenderable, View.OnTouchListener, ITouchable {
    private int left, top, startY, maxLinesInScreen, maxScrollY;
    private LinearLayout linearLayout;
    private TextView textView;
    private Rect rect;

    public ScrollText(SpannableStringBuilder text, int left, int top, int width, int height) {
        setIdleState();
        this.left = left;
        this.top = top;
        maxLinesInScreen=height/Const.TEXT_HINT;
        rect=new Rect(left, top, left+width, top+height);
        linearLayout=new LinearLayout(GameActivity.getContext());
        textView=new TextView(GameActivity.getContext());
        linearLayout.addView(textView);
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, Const.TEXT_HINT);
        textView.setTypeface(Assets.font);
        textView.setTextColor(Color.BLACK);
        textView.setWidth(width);
        textView.setHeight(height);
        textView.setMovementMethod(new ScrollingMovementMethod());
        linearLayout.measure(width, height);
        linearLayout.layout(0, 0, width, height);
        updateMaxScrollY();
    }

    public void updateText(SpannableStringBuilder text){
        textView.setScrollY(0);
        textView.setText(text);
        updateMaxScrollY();
    }

    public void setGravity(int gravity){
        textView.setGravity(gravity);
    }

    public void setTextSize(float textSize){
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }

    private void updateMaxScrollY(){
        maxScrollY=(textView.getLineCount()-maxLinesInScreen)*textView.getLineHeight();
        if (maxScrollY<0) maxScrollY=0;
    }

    @Override
    public void render(Canvas canvas) {
        canvas.save();
        canvas.translate(left, top);
        linearLayout.draw(canvas);
        canvas.restore();
    }

    public void scroll(int value){
        textView.scrollBy(0, value);
        if (textView.getScrollY() < 0) textView.setScrollY(0);
        if (textView.getScrollY() > maxScrollY) textView.setScrollY(maxScrollY);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN: {
                if(touch((int)motionEvent.getX(),(int)motionEvent.getY())) {
                    setActiveState();
                    startY = (int) motionEvent.getY();
                }
            } break;
            case MotionEvent.ACTION_MOVE: {
                if (isActive()) {
                    int y = (int) motionEvent.getY();
                    scroll(startY-y);
                    startY = y;
                }
            } break;
            case MotionEvent.ACTION_UP:{
                setIdleState();
            }break;
        }
        return true;
    }

    @Override
    public boolean touch(int x, int y) {
        return rect.contains(x,y);
    }
}
