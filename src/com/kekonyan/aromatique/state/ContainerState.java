package com.kekonyan.aromatique.state;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.SpannableStringBuilder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import com.kekonyan.aromatique.GameActivity;
import com.kekonyan.aromatique.R;
import com.kekonyan.aromatique.UI.*;
import com.kekonyan.aromatique.audio.Sfx;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.model.Container;
import com.kekonyan.aromatique.util.Const;

import java.io.IOException;

class ContainerState extends BasicState implements IPageable {
    private int length;
    private int selected;
    private Integer page;
    private Paint caseInfoPaint;
    private Rect caseRect[];
    private Container[] container;
    private Container selection;
    private Button prevPage, nextPage, back, acquire;
    private ScrollText containerContent;
    private Background background;
    private NotifyPopup notEnoughMoney;
    private ActionPopup openNow;
    private enum State {CATALOG, DESCRIPTION}
    private State state;

    private void setState(State state){
        this.state =state;
    }

    ContainerState(Context context, StateManager stateManager) {
        super(context, stateManager);
        state =State.CATALOG;
    }

    @Override
    public void preload() throws IOException {
        openNow=new ActionPopup(context.getString(R.string.open_right_now));
        notEnoughMoney=new NotifyPopup(context.getString(R.string.not_enough_money));
        background=new Background(Assets.backgrounds[Assets.Background.CONTAINER.ordinal()]);
        container =new Container[10];
        for (int i = 0; i< container.length; i++) container[i]=new Container(i);
        caseInfoPaint=Assets.hintPaint;
        length=5;
        int i=Const.BUTTON_BMP, a=Const.BUTTON_ARROW_BMP;
        back=new Button(Assets.buttons[Assets.Button.BACK.ordinal()], i/2, height-i, i/2, 0, false);
        prevPage=new Button(Assets.buttons[Assets.Button.LEFT.ordinal()], 5*width/14, 3*height/4-Const.TEXT_REGULAR, a/4, a/4, true);
        nextPage=new Button(Assets.buttons[Assets.Button.RIGHT.ordinal()], 9*width/14-i, 3*height/4-Const.TEXT_REGULAR, a/4, a/4, true);
        acquire=new Button(context.getString(R.string.acquire), 27*width/32, 33*height/64, Assets.regularPaint, Color.rgb(0,168,81), Color.rgb(20,188,101), true);
        containerContent=new ScrollText(new SpannableStringBuilder(), width/4, 5*height/16, width/2, 19*height/32);
        containerContent.setGravity(Gravity.CENTER);
        initCaseRects();
        page=0;

    }

    private void initCaseRects(){
        caseRect=new Rect[length];
        int h=height/2-Const.INSHOP_CONTAINER /2, h1=height/2+Const.INSHOP_CONTAINER /2;
        caseRect[0]=new Rect(width/16, h, 3*width/16, h1);
        for (int i=1; i<length; i++){
            caseRect[i]=new Rect(caseRect[i-1].right+width/16, h, caseRect[i-1].right+3*width/16, h1);
        }
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
        background.render(canvas);
        canvas.drawText(context.getString(R.string.case_state_title), width/2, height/8, Assets.headerPaint);
        back.render(canvas);
        canvas.drawText(playerData.money+context.getString(R.string.dollars), width/2, 47*height/48, Assets.hintPaint);
        switch (state){
            case CATALOG: {
                drawCases(canvas);
                canvas.drawText(context.getString(R.string.page) + (page+1) + context.getString(R.string.of_2), width / 2, 3 * height / 4, Assets.regularPaint);
                prevPage.render(canvas);
                nextPage.render(canvas);
            } break;
            case DESCRIPTION: {
                canvas.drawLine(width/3, 3*height/10, 2*width/3, 3*height/10, Assets.hintPaint);
                canvas.drawLine(width/3, 44*height/48, 2*width/3, 44*height/48, Assets.hintPaint);
                acquire.render(canvas);
                selection.render(canvas, width/16, caseRect[0].top);
                canvas.drawText(selection.name, width/16+Const.INSHOP_CONTAINER /2, height/3, caseInfoPaint);
                canvas.drawText(selection.price+context.getString(R.string.dollars), width/16+ Const.INSHOP_CONTAINER /2, 2*height/3+Const.TEXT_HINT, caseInfoPaint);
                canvas.drawText(context.getString(R.string.from_this), width/2, height/4, caseInfoPaint);
                containerContent.render(canvas);
                notEnoughMoney.render(canvas);
                openNow.render(canvas);
            } break;
        }

    }

    private void drawCases(Canvas canvas){
        synchronized (this) {
            for (int i = page * length; i < (page+1) * length; i++) {
                canvas.drawText(container[i].name, caseRect[i - page * length].left + Const.INSHOP_CONTAINER / 2, height / 3, Assets.hintPaint);
                container[i].render(canvas, caseRect[i - page * length].left, caseRect[i - page * length].top);
            }
        }
    }

    @Override
    public void update(float elapsedTime) {
        background.update(elapsedTime);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int x = (int) motionEvent.getX(), y = (int) motionEvent.getY();
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_UP: {
                if (back.onTouch(view, motionEvent)) switch (state) {
                    case CATALOG:
                        getStateManager().setState(StateManager.State.INVENTORY, false);
                        break;
                    case DESCRIPTION:
                        if(!openNow.isActive()&&!notEnoughMoney.isActive())setState(State.CATALOG);
                        break;
                }
                switch (state) {
                    case CATALOG: {
                        if (prevPage.onTouch(view, motionEvent)) {
                            turnPage(false);
                        }
                        if (nextPage.onTouch(view, motionEvent)) {
                            turnPage(true);
                        }
                        processingCaseChoice(x, y);
                    }break;
                    case DESCRIPTION: {
                        if (notEnoughMoney.isActive()) {
                            if (notEnoughMoney.ok.onTouch(view, motionEvent)) {
                                notEnoughMoney.setIdleState();
                            }
                        } else
                        if (openNow.isActive()){
                            if (openNow.ok.onTouch(view, motionEvent)) {
                                getStateManager().setOpenState(selection);
                                break;
                            }
                            if (openNow.no.onTouch(view, motionEvent)) {
                                openNow.setIdleState();
                                break;
                            }
                        } else {
                            containerContent.onTouch(view, motionEvent);
                            if (acquire.onTouch(view, motionEvent)) {
                                if (playerData.withdrawMoney(selection.price)) {
                                    playerData.addInventoryItem(new Container.Item(selection.index, System.currentTimeMillis()));
                                    openNow.setActiveState();
                                } else notEnoughMoney.setActiveState();
                            }
                        }
                    } break;
                }
            } break;
            default: {
                switch (state) {
                    case CATALOG: {
                        back.onTouch(view, motionEvent);
                        prevPage.onTouch(view, motionEvent);
                        nextPage.onTouch(view, motionEvent);
                    }
                    break;
                    case DESCRIPTION: {
                        if (notEnoughMoney.isActive()) {
                            notEnoughMoney.onTouch(view, motionEvent);
                            break;
                        } else
                        if (openNow.isActive()){
                            openNow.onTouch(view, motionEvent);
                            break;
                        }
                        back.onTouch(view, motionEvent);
                        acquire.onTouch(view, motionEvent);
                        if (containerContent.touch(x, y)) containerContent.onTouch(view, motionEvent);
                    } break;
                }
            }
        }
        return true;
    }

    private void processingCaseChoice(int x, int y){
        for (int i=0; i<length; i++){
            if (caseRect[i].contains(x,y)){
                GameActivity.sfx.play(Sfx.CLICK_POSITIVE);
                selected =i;
                int index = selected + page * length;
                selection = container[index];
                containerContent.updateText(selection.printContent());
                setState(State.DESCRIPTION);
                break;
            }
        }
    }

    @Override
    public boolean onBackPressed() {
        switch (state){
            case CATALOG:getStateManager().setState(StateManager.State.INVENTORY, false); break;
            case DESCRIPTION: {
                if (!notEnoughMoney.onBackPressed()&&!openNow.onBackPressed()) setState(State.CATALOG);
                notEnoughMoney.setIdleState();
                openNow.setIdleState();
            } break;
        }
        return true;
    }

    @Override
    public void turnPage(boolean increment) {
        synchronized (this){
            page = increment ? (page + 1 > 1 ? 0 : page + 1) : (page - 1 < 0 ? 1 : page - 1);
        }
    }

}
