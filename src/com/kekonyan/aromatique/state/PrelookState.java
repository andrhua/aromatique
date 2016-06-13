package com.kekonyan.aromatique.state;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import com.kekonyan.aromatique.R;
import com.kekonyan.aromatique.UI.Background;
import com.kekonyan.aromatique.UI.Button;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.model.JoeModel;
import com.kekonyan.aromatique.util.Const;
import com.kekonyan.aromatique.util.UI;

import java.io.IOException;

class PrelookState extends BasicState {
    private JoeModel joeModel;
    private Button back, next, dressup;
    private Background background;
    private Paint hintPaint;


    PrelookState(Context context, StateManager stateManager) {
        super(context, stateManager);
    }

    @Override
    public void preload() throws IOException {

        hintPaint=new Paint(Assets.hintPaint);
        int i=Const.BUTTON_BMP;
        joeModel =playerData.joeModel;
        back=new Button(Assets.buttons[Assets.Button.BACK.ordinal()], i/2, height-i, i/2, 0, false);
        next=new Button(Assets.buttons[Assets.Button.NEXT.ordinal()], width-3*i/2, height-i, i/2, 0, true);
        dressup=new Button(context.getString(R.string.dress_up), width/2, 7*height/8+Const.TEXT_HINT /2, hintPaint, Color.BLACK, Color.rgb(195,195,195), true);
        background=new Background(Assets.backgrounds[Assets.Background.HEART.ordinal()]);
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
        background.render(canvas);
        canvas.drawText(context.getString(R.string.prelook_title), width/2, height/8, Assets.headerPaint);
        joeModel.render(canvas);
        hintPaint.setTextAlign(Paint.Align.LEFT);
        hintPaint.setColor(UI.doEvaluation(joeModel.clothes[0].price));
        canvas.drawText(joeModel.clothes[0].price+context.getString(R.string.dollars), 2*width/3, height/3+height/64,hintPaint);
        hintPaint.setColor(UI.doEvaluation(joeModel.clothes[1].price));
        canvas.drawText(joeModel.clothes[1].price+context.getString(R.string.dollars), 2*width/3, height/2-height/32, hintPaint);
        hintPaint.setColor(UI.doEvaluation(joeModel.clothes[2].price));
        canvas.drawText(joeModel.clothes[2].price+context.getString(R.string.dollars), 2*width/3, 2*height/3-height/32, hintPaint);
        hintPaint.setColor(UI.doEvaluation(joeModel.clothes[3].price));
        canvas.drawText(joeModel.clothes[3].price+context.getString(R.string.dollars), 2*width/3, height-15*height/60, hintPaint);
        hintPaint.setTextAlign(Paint.Align.CENTER);
        hintPaint.setColor(Color.WHITE);
        dressup.render(canvas);
        back.render(canvas);
        next.render(canvas);
    }

    @Override
    public void update(float elapsedTime) {
        background.update(elapsedTime);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int x=(int)motionEvent.getX(), y=(int)motionEvent.getY();
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_UP:{
                if (back.onTouch(view, motionEvent)) getStateManager().setState(StateManager.State.MENU, false);
                if (next.onTouch(view, motionEvent)) getStateManager().setState(StateManager.State.LEVEL, true);
                if (dressup.onTouch(view, motionEvent)) getStateManager().setState(StateManager.State.WARDROBE, true);
            } break;
            default: {
                back.onTouch(view,motionEvent);
                next.onTouch(view,motionEvent);
                dressup.onTouch(view,motionEvent);
            }
        }
        return true;
    }

    @Override
    public boolean onBackPressed() {
        getStateManager().setState(StateManager.State.MENU, false);
        return true;
    }
}
