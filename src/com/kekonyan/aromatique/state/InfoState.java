package com.kekonyan.aromatique.state;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import com.kekonyan.aromatique.R;
import com.kekonyan.aromatique.UI.Button;
import com.kekonyan.aromatique.UI.ScrollText;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.util.Const;

import java.io.IOException;

class InfoState extends BasicState {
    private Button authors, intro, back;
    private ScrollText captions;
    private enum State {INFO, AUTHORS}
    private State state;

    @Override
    void preload() throws IOException {
        int i= Const.BUTTON_BMP;
        back=new Button(Assets.buttons[Assets.Button.BACK.ordinal()], i/2, height-i, i/2, 0, false);
        authors=new Button(context.getString(R.string.authors), width/2, 2*height/5, Assets.hintPaint, Color.BLACK, Color.rgb(195,195,195), true);
        intro=new Button(context.getString(R.string.intro), width/2, 3*height/5, Assets.hintPaint, Color.BLACK, Color.rgb(195,195,195), true);
        captions=new ScrollText(
                new SpannableStringBuilder(context.getString(R.string.authors_)),
                0,
                0,
                width,
                height);
        captions.setTextSize(Const.TEXT_REGULAR);
        captions.setGravity(Gravity.CENTER);
        state=State.INFO;
    }

    InfoState(Context context, StateManager stateManager) {
        super(context, stateManager);
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
        canvas.drawText(context.getString(R.string.about_title), width/2, height/8, Assets.headerPaint);
        back.render(canvas);
        switch (state){
            case INFO:
                authors.render(canvas);
                intro.render(canvas);
                break;
            case AUTHORS:
                captions.render(canvas);
                break;
        }

    }

    @Override
    public void update(float elapsedTime) {
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (state) {
            case INFO: {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP: {
                        if (authors.onTouch(view, motionEvent)) state = State.AUTHORS;
                        if (intro.onTouch(view, motionEvent)) getStateManager().setState(StateManager.State.INTRODUCTION, true);
                        if (back.onTouch(view, motionEvent)) getStateManager().setState(StateManager.State.SETTINGS, false);
                    }break;
                    default: {
                        authors.onTouch(view, motionEvent);
                        intro.onTouch(view, motionEvent);
                        back.onTouch(view, motionEvent);
                    }break;
                }
            } break;
            case AUTHORS: {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP: if (back.onTouch(view, motionEvent)) state=State.INFO;
                    default: back.onTouch(view, motionEvent);
                }
            }
        }
        return true;
    }


    @Override
    public boolean onBackPressed() {
        getStateManager().setState(StateManager.State.SETTINGS, false);
        return true;
    }
}
