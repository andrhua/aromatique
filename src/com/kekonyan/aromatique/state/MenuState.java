package com.kekonyan.aromatique.state;

import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.format.DateUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.kekonyan.aromatique.GameActivity;
import com.kekonyan.aromatique.R;
import com.kekonyan.aromatique.UI.Background;
import com.kekonyan.aromatique.UI.Button;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.util.Const;
import com.kekonyan.aromatique.util.UI;

import java.io.IOException;

import static com.kekonyan.aromatique.util.UI.ANIMATED_COLOR_SPAN_FLOAT_PROPERTY;

class MenuState extends BasicState {
    private Button settings, stats, start;
    private Background background;
    private TextView tv;
    private LinearLayout ll;

    MenuState(Context context, StateManager stateManager) {
        super(context, stateManager);
    }

    @Override
    public void preload() throws IOException {
        int i=Const.BUTTON_BMP;
        tv=new TextView(context);
        tv.setTypeface(Assets.font);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, height/4);
        tv.setText(context.getString(R.string.aromatique));
        ll=new LinearLayout(context);
        ll.setGravity(Gravity.CENTER);
        ll.addView(tv);
        ll.measure(width, height);
        ll.layout(0, 0, width, height);
        settings=new Button(Assets.buttons[Assets.Button.SETTINGS.ordinal()], i/2, height-i, i/2, 0, true);
        stats =new Button(Assets.buttons[Assets.Button.STATS.ordinal()], width-3*i/2, height-i, i/2, 0, true);
        start=new Button(context.getString(R.string.start), width/2, 5*height/6-height/32, Assets.hintPaint, Color.BLACK, Color.rgb(195,195,195), true);
        background=new Background(Assets.backgrounds[Assets.Background.HEXAGON.ordinal()]);
        UI.AnimatedColorSpan span = new UI.AnimatedColorSpan(context);
        final SpannableString spannableString = new SpannableString(tv.getText());
        spannableString.setSpan(span, 0, 10, 0);
        final ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(
                span, ANIMATED_COLOR_SPAN_FLOAT_PROPERTY, 0, 100);
        objectAnimator.setEvaluator(new FloatEvaluator());
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                tv.setText(spannableString);
            }
        });
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setDuration(DateUtils.MINUTE_IN_MILLIS * 3);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        GameActivity.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                objectAnimator.start();
            }
        });
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
        background.render(canvas);
        ll.draw(canvas);
        settings.render(canvas);
        stats.render(canvas);
        start.render(canvas);
    }

    @Override
    public void update(float elapsedTime) {
        background.update(elapsedTime);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_UP: {
                if (settings.onTouch(view, motionEvent)) getStateManager().setState(StateManager.State.SETTINGS, true); else
                if (stats.onTouch(view, motionEvent)) getStateManager().setState(StateManager.State.STATS, true); else
                if (start.onTouch(view, motionEvent)) getStateManager().setState(StateManager.State.PRELOOK, true);
            } break;
            default: {
                stats.onTouch(view,motionEvent);
                settings.onTouch(view,motionEvent);
                start.onTouch(view,motionEvent);
            }
        }
        return true;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }


}
