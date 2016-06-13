package com.kekonyan.aromatique.core;

import android.graphics.Canvas;
import android.view.MotionEvent;
import com.kekonyan.aromatique.GameActivity;
import com.kekonyan.aromatique.UI.Button;
import com.kekonyan.aromatique.game.object.Joe;
import com.kekonyan.aromatique.util.Const;

public class Control implements IRenderable {
    private final int width=Const.WIDTH, height=Const.HEIGHT;
    private Joe joe;
    private Button left, right;

    public Control (){
        joe =GameActivity.joe;
        left=new Button(Assets.buttons[Assets.Button.GAME_LEFT.ordinal()], width/16, 3*height/4, 0, 0, true);
        right=new Button(Assets.buttons[Assets.Button.GAME_RIGHT.ordinal()], 3*width/16, 3*height/4, 0, 0, true);
        left.setSoundless();
        right.setSoundless();
    }

    public void reset(){
        left.setIdleState();
        right.setIdleState();
    }

    @Override
    public void render(Canvas canvas) {
        left.render(canvas);
        right.render(canvas);
    }

    public void onTouch(MotionEvent motionEvent) {
        joe =GameActivity.joe;
        int actionMask = motionEvent.getActionMasked();
        int actionIndex = motionEvent.getActionIndex();
        int x=(int)motionEvent.getX(actionIndex), y=(int)motionEvent.getY(actionIndex);
        switch (actionMask){
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN: {
                if (x>width/2) joe.needToJump =true;
                if (left.touch(x, y)) {left.onTouch(null, motionEvent); joe.setXState(Joe.xState.LEFT); break; }
                if (right.touch(x, y)) {right.onTouch(null, motionEvent); joe.setXState(Joe.xState.RIGHT); break; }
            } break;
            case MotionEvent.ACTION_MOVE: {
                if (left.isActive()&&!left.touch(x,y)) {
                    joe.setXState(Joe.xState.STAY); left.setIdleState(); } else
                if (left.touch(x,y)) {left.setActiveState(); joe.setXState(Joe.xState.LEFT);}
                if (right.isActive()&&!right.touch(x,y)){
                    joe.setXState(Joe.xState.STAY); right.setIdleState(); } else
                if (right.touch(x,y)){right.setActiveState(); joe.setXState(Joe.xState.RIGHT); }
            } break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:{
                if (left.isActive()&&left.touch(x, y)) {
                    joe.setXState(Joe.xState.STAY); left.setIdleState();}
                if (right.isActive()&&right.touch(x, y)) {
                    joe.setXState(Joe.xState.STAY); right.setIdleState();}

            } break;
        }
    }

}
