package com.kekonyan.aromatique.state;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import com.kekonyan.aromatique.GameActivity;
import com.kekonyan.aromatique.R;
import com.kekonyan.aromatique.UI.ActionPopup;
import com.kekonyan.aromatique.UI.Background;
import com.kekonyan.aromatique.UI.Button;
import com.kekonyan.aromatique.UI.ScrollText;
import com.kekonyan.aromatique.audio.Sfx;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.model.Container;
import com.kekonyan.aromatique.util.Const;

import java.io.IOException;
import java.util.List;

class InventoryState extends BasicState implements IPageable, IRepositorable, IProccessable {
    private List<Container.Item> inventory;
    private Container.Item selection;
    private Rect items[];
    private Button back, container, open, sell, prev, next;
    private ActionPopup sellActionPopup;
    private Background background;
    private int length,selected,max;
    private Integer page;
    private Paint itemPaint;
    private ScrollText scrollText;

    InventoryState(Context context, StateManager stateManager) {
        super(context, stateManager);
    }

    @Override
    public void preload() throws IOException {
        background=new Background(Assets.backgrounds[Assets.Background.CLOVER.ordinal()]);
        inventory = playerData.getInventory();
        int i=Const.BUTTON_BMP, a=Const.BUTTON_ARROW_BMP;
        back=new Button(Assets.buttons[Assets.Button.BACK.ordinal()], i/2, height-i, i/2, 0, false);
        container=new Button(Assets.buttons[Assets.Button.CONTAINER.ordinal()], width-3*i/2, height-i, i/2, 0, true);
        sellActionPopup =new ActionPopup(context.getString(R.string.will_removed));
        itemPaint = new Paint(); itemPaint.setColor(Color.rgb(220,220,220));
        if (inventory.size()>0) {
            open=new Button(context.getString(R.string.open), 118*width/160, 4*height/5, Assets.hintPaint, Color.rgb(0,168,81), Color.rgb(20,188,101), true);
            sell=new Button(context.getString(R.string.sell), 144*width/160, 4*height/5, Assets.hintPaint, Color.rgb(244,67,54), Color.rgb(250, 87, 74), false);
            initItemsRects();
            selected=0;
            selection = inventory.get(selected);
            scrollText =new ScrollText(selection.printInfo().append(selection.printContent()),
                    13 * width / 20, height / 4,width-105 * width / 160, height/2-height/40);
            page=0;
        }
        if (inventory.size()>length){
            prev = new Button(Assets.buttons[Assets.Button.UP.ordinal()], a / 2, items[0].bottom - a, a/4, a/4, true);
            next = new Button(Assets.buttons[Assets.Button.DOWN.ordinal()],  a / 2, items[10].top, a/4, a/4, true);
            max=(int) Math.ceil(inventory.size() / 15f);
        }
    }

    private void initItemsRects() {
        length = 15;
        items = new Rect[length];
        int s=21 * width / 200;
        int h = height / 4 - s;
        int h1 = height / 4;
        int w,w1;
        for (int i = 0; i < length; i++) {
            if (i  % 5 == 0) {
                h += s + width / 200;
                h1 += s + width / 200;
                w = width / 10;
                w1 = w + s;
                items[i] = new Rect(w, h, w1, h1);
            } else
                items[i] = new Rect(items[i - 1].right + width / 200, h, items[i - 1].right + s + width / 200, h1);
        }
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
        background.render(canvas);
        canvas.drawText(context.getString(R.string.inventory_title), width / 2, height/8,  Assets.headerPaint);
        back.render(canvas);
        container.render(canvas);
        drawRepository(canvas);
    }

    @Override
    public void drawRepository(Canvas canvas) {
        synchronized (this) {
            int f = Const.REPOSITORY_FRAME_CELL;
            if (inventory.isEmpty())
                canvas.drawText(context.getString(R.string.inventory_is_empty), width / 2, height / 2, Assets.hintPaint); else {
                if (inventory.size() > length) {
                    prev.render(canvas);
                    canvas.drawText(String.valueOf(page + 1), width / 20, items[5].top + Const.REPOSITORY_CELL / 2 + Const.TEXT_HINT / 2, Assets.hintPaint);
                    next.render(canvas);
                }
                for (int i = 0; i < length; i++) {
                    Container.Item item = null;
                    if (inventory.size() > i + (page * (length))) item = inventory.get(i + (page * (length)));
                    canvas.drawRect(items[i], itemPaint);
                    if (item != null) {
                        Assets.headerPaint.setStyle(Paint.Style.STROKE);
                        if (i == selected)
                            canvas.drawRect(new Rect(items[i].left - f, items[i].top - f, items[i].right + f, items[i].bottom + f), Assets.headerPaint);
                        Assets.headerPaint.setStyle(Paint.Style.FILL);
                        inventory.get(i + page * length).renderPreview(canvas, items[i].left + Const.REPOSITORY_FRAME_CELL, items[i].top + Const.REPOSITORY_FRAME_CELL);
                    }

                }
                scrollText.render(canvas);
                canvas.drawLine(103 * width / 160, height / 4 + height / 100, 103 * width / 160, height / 4 + 65 * width / 200, Assets.headerPaint);
                canvas.drawLine(103 * width / 160, 4 * height / 5 - height / 15, width, 4 * height / 5 - height / 15, Assets.headerPaint);
                open.render(canvas);
                sell.render(canvas);
                if (sellActionPopup.isActive()) sellActionPopup.render(canvas);
            }
        }
    }

    @Override
    public void update(float elapsedTime) {
        background.update(elapsedTime);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int x=(int)motionEvent.getX(), y=(int)motionEvent.getY();
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_UP: {
                if (scrollText!=null) if (scrollText.isActive()){scrollText.onTouch(view,motionEvent);break;}
                if (!sellActionPopup.isActive()) {
                    if (back.onTouch(view, motionEvent)) getStateManager().setState(StateManager.State.WARDROBE, false);
                    if (container.onTouch(view, motionEvent)) getStateManager().setState(StateManager.State.CONTAINER, true);
                    if (inventory.size() > 0) {
                        if (open.onTouch(view, motionEvent)) {use();break;}
                        if (sell.onTouch(view, motionEvent)) {
                            sellActionPopup.setActiveState();
                            break;
                        }
                        if (inventory.size() > length) {
                            if (prev.onTouch(view, motionEvent)) {turnPage(false);break;}
                            if (next.onTouch(view, motionEvent)) {turnPage(true);break;}
                        }
                        process(x, y);
                    }
                } else {
                    if (sellActionPopup.ok.onTouch(view, motionEvent)) {
                        sellActionPopup.setIdleState();
                        delete();
                        if (selection == null) turnPage(false);
                        break;
                    }
                    if (sellActionPopup.no.onTouch(view, motionEvent)) {
                        sellActionPopup.setIdleState();
                    }
                }
            } break;
            default:{
                if (!sellActionPopup.isActive()) {
                    back.onTouch(view,motionEvent);
                    container.onTouch(view,motionEvent);
                    if (inventory.size() > 0) {
                        scrollText.onTouch(view, motionEvent);
                        open.onTouch(view,motionEvent);
                        sell.onTouch(view,motionEvent);
                    }
                    if (inventory.size() > length) {
                        prev.onTouch(view,motionEvent);
                        next.onTouch(view,motionEvent);
                    }
                } else {
                    sellActionPopup.onTouch(view,motionEvent);
                }
            }
        }
        return true;
    }

    @Override
    public void process(int x, int y) {
        for (int i = 0; i< length; i++){
            if (items[i].contains(x,y)&&inventory.size()>i+page*length) {
                sfx.play(Sfx.CLICK_POSITIVE);
                selected=i;
                selection = inventory.get(selected+page*length);
                scrollText.updateText(selection.printInfo().append(selection.printContent()));
                break;
            }
        }
    }

    @Override
    public void use() {
        synchronized (this){
            playerData.addInventoryItem(new Container.Item(selection.index, System.currentTimeMillis()));
            getStateManager().setOpenState(selection);
            delete();
        }
    }

    @Override
    public void delete() {
        synchronized (this) {
            GameActivity.playerData.addMoney(selection.price / 2);
            max = (int) Math.ceil(inventory.size() / 15f);
            playerData.removeInventoryItem(selected + page * length);
            selected = (selected == 0 ? 0 : --selected);
            if (selected + page * length != inventory.size()) selection = inventory.get(selected + page * length); else turnPage(false);
        }
    }

    @Override
    public boolean onBackPressed() {
        if (!sellActionPopup.isActive()) getStateManager().setState(StateManager.State.WARDROBE, false);
        return !sellActionPopup.isActive();
    }

    @Override
    public void turnPage(boolean increment) {
        synchronized (this){
            page = increment ? (page + 1 > max - 1 ? 0 : page + 1) : (page - 1 < 0 ? max - 1 : page - 1);
            selected = 0;
            if (!inventory.isEmpty())selection = inventory.get(selected + page * length);
        }
    }
}