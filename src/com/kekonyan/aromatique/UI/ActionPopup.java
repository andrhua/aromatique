package com.kekonyan.aromatique.UI;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import com.kekonyan.aromatique.GameActivity;
import com.kekonyan.aromatique.R;
import com.kekonyan.aromatique.core.Assets;

public class ActionPopup extends NotifyPopup {
    public Button no;

    public ActionPopup(String text){
        super(text);
        ok=new Button(GameActivity.getContext().getString(R.string.ok), 8*width/20, rect.bottom-height/16, Assets.hintPaint, Color.rgb(0,168,81), Color.rgb(20,188,101), true);
        no=new Button(GameActivity.getContext().getString(R.string.no), 12*width/20, rect.bottom-height/16, Assets.hintPaint, Color.rgb(244,67,54), Color.rgb(250, 87, 74), false);
        setIdleState();
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
        if (isActive()) no.render(canvas);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        super.onTouch(view, motionEvent);
        if (isActive()) no.onTouch(view,motionEvent);
        return true;
    }
}
