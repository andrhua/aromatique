package com.kekonyan.aromatique.state;

import android.content.Context;
import android.graphics.Canvas;
import android.text.SpannableStringBuilder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import com.kekonyan.aromatique.R;
import com.kekonyan.aromatique.UI.ScrollText;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.util.Const;

import java.io.IOException;

class IntroductionState extends BasicState {
    private enum State {WELCOME, ITS_JOE, LIKES_TO_LOOK_STYLISH, HELP_HIM}
    private State state;
    private ScrollText scrollText;

    private void setState(State state){
        if (state==State.HELP_HIM) scrollText.updateText(new SpannableStringBuilder(context.getString(R.string.help_him)));
        this.state=state;
    }

    @Override
    void preload() throws IOException {
        scrollText=new ScrollText(new SpannableStringBuilder(context.getString(R.string.likes_to_look_stylish)), 9*width/20, 0, width/2, height);
        scrollText.setGravity(Gravity.CENTER);
        scrollText.setTextSize(Const.TEXT_REGULAR);
        setState(State.WELCOME);
    }

    IntroductionState(Context context, StateManager stateManager) {
        super(context, stateManager);
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
        switch (state){
            case WELCOME: {
                canvas.drawText(context.getString(R.string.welcome), width/2, height/2, Assets.headerPaint);
            } break;
            case ITS_JOE: {
                canvas.drawBitmap(Assets.other[Assets.Other.JOE.ordinal()], 0, height/2-width/4, null);
                canvas.drawText(context.getString(R.string.his_name_is), 13*width/20, height/2, Assets.headerPaint);
            } break;
            case LIKES_TO_LOOK_STYLISH:
            case HELP_HIM:
                canvas.drawBitmap(Assets.other[Assets.Other.JOE.ordinal()], 0, height/2-width/4, null);
                scrollText.render(canvas); break;
        }
    }

    @Override
    public void update(float elapsedTime) {
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction()==MotionEvent.ACTION_UP)
        switch (state){
            case WELCOME: setState(State.ITS_JOE); break;
            case ITS_JOE: setState(State.LIKES_TO_LOOK_STYLISH); break;
            case LIKES_TO_LOOK_STYLISH: setState(State.HELP_HIM); break;
            case HELP_HIM:
                playerData.isIntroductionShown=true;
                getStateManager().setState(StateManager.State.MENU, true); break;
        }
        return true;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

}
