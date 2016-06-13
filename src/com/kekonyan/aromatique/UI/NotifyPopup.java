package com.kekonyan.aromatique.UI;

import android.graphics.*;
import android.text.Layout;
import android.text.StaticLayout;
import android.view.MotionEvent;
import android.view.View;
import com.kekonyan.aromatique.GameActivity;
import com.kekonyan.aromatique.R;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.core.IBackable;
import com.kekonyan.aromatique.core.IRenderable;
import com.kekonyan.aromatique.util.Const;

public class NotifyPopup extends BasicActivatable implements IRenderable, View.OnTouchListener, IBackable {
    public Button ok;
    protected int width, height;
    Rect rect;
    private Paint rectPaint;
    protected String text;
    private StaticLayout textField;
    private Bitmap background;
    private Rect src, out;

    public NotifyPopup(String text){
        width= Const.WIDTH; height= Const.HEIGHT;
        this.text=text;
        textField=new StaticLayout(text, Assets.textPaint, 9*width/20, Layout.Alignment.ALIGN_CENTER, 1, 0, false);
        rect=new Rect(width/4, 11*height/32-Math.round((textField.getLineCount()/2f)*Assets.textPaint.getTextSize()), 3*width/4, 21*height/32+Math.round((textField.getLineCount()/2f)*Assets.textPaint.getTextSize()));
        rectPaint=new Paint(); rectPaint.setColor(Color.rgb(205,205,205));
        ok=new Button(GameActivity.getContext().getString(R.string.ok), width/2, rect.bottom-height/16, Assets.hintPaint, Color.rgb(0,168,81), Color.rgb(20,188,101), true);
        setIdleState();
    }

    @Override
    public void render(Canvas canvas) {
        if (isActive()) {
            canvas.drawColor(Color.argb(128,0,0,0));
            canvas.drawRect(rect, rectPaint);
            if (background!=null) {
                rectPaint.setAlpha(80);
                canvas.drawBitmap(background, src, out, rectPaint);
                rectPaint.setAlpha(255);
            }
            canvas.save();
            canvas.translate(rect.left + (rect.right - rect.left - textField.getWidth()) / 2, rect.top + height / 8);
            textField.draw(canvas);
            canvas.restore();
            ok.render(canvas);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (isActive()) ok.onTouch(view,motionEvent);
        return true;
    }

    @Override
    public boolean onBackPressed() {
        if (isActive()) {
            setIdleState();
            return true;
        }
        return false;
    }

}
