package com.kekonyan.aromatique.state;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import com.kekonyan.aromatique.R;
import com.kekonyan.aromatique.UI.ActionPopup;
import com.kekonyan.aromatique.UI.Background;
import com.kekonyan.aromatique.UI.Button;
import com.kekonyan.aromatique.UI.ScrollText;
import com.kekonyan.aromatique.audio.Sfx;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.model.Clothes;
import com.kekonyan.aromatique.model.JoeModel;
import com.kekonyan.aromatique.util.Const;
import com.kekonyan.aromatique.util.UI;

import java.io.IOException;
import java.util.List;

public class WardrobeState extends BasicState implements IPageable, IRepositorable, IProccessable {
    private Paint inventoryPaint, hintPaint, emptyItem;
    private int selected, length, max, page;
    private Rect  items[];
    private Button wear, sell, back, takeoff, prev, next, inventory;
    private ActionPopup sellActionPopup;
    private ScrollText info;
    private JoeModel joeModel;
    private Clothes.Item selection;
    private Background background;
    private List<Clothes.Item> wardrobe;

    public WardrobeState(Context context, StateManager stateManager) {
        super(context, stateManager);
    }

    @Override
    public void preload() throws IOException {
        background=new Background(Assets.backgrounds[Assets.Background.HANGER.ordinal()]);
        wardrobe=playerData.getWardrobe();
        inventoryPaint = new Paint();
        hintPaint=Assets.hintPaint;
        int i=Const.BUTTON_BMP, a=Const.BUTTON_ARROW_BMP;
        back=new Button(Assets.buttons[Assets.Button.BACK.ordinal()], i/2, height-i, i/2, 0, false);
        sellActionPopup =new ActionPopup(context.getString(R.string.will_removed));
        inventory= new Button(Assets.buttons[Assets.Button.INVENTORY.ordinal()], width-3*i/2, height-i, i/2, 0, true);
        if (wardrobe.size() > 0) {
            initItemsRects();
            selected=0;
            selection = wardrobe.get(selected);
            info=new ScrollText(selection.printInfo(),13 * width / 20, height / 4,7*width/20, items[6].bottom-21*width/400-height/4);
            wear=new Button(context.getString(R.string.wear), 119*width/160, 5*height/8, hintPaint, Color.rgb(0,168,81), Color.rgb(20,188,101), true);
            takeoff=new Button(context.getString(R.string.takeoff), 143*width/160, 5*height/8, hintPaint, Color.BLACK, Color.rgb(195,195,195), false);
            sell=new Button(context.getString(R.string.sell), 131*width/160, 25*height/32, hintPaint, Color.rgb(244,67,54), Color.rgb(250, 87, 74), false);
            joeModel =playerData.joeModel;
            page=0;
            emptyItem = new Paint();
            emptyItem.setColor(Color.rgb(220, 220, 220));
        }
        if (wardrobe.size()>length){
            prev = new Button(Assets.buttons[Assets.Button.UP.ordinal()], a / 2, items[0].bottom - a, a/4, a/4, true);
            next = new Button(Assets.buttons[Assets.Button.DOWN.ordinal()], a / 2, items[10].top, a/4, a/4, true);
            max=(int) Math.ceil(wardrobe.size() / 15f);
        }
    }

    private void initItemsRects() {
        length=15;
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
        canvas.drawText(context.getString(R.string.wardrobe_title), width / 2, height/8,  Assets.headerPaint);
        back.render(canvas);
        inventory.render(canvas);
        drawRepository(canvas);
    }

    @Override
    public void drawRepository(Canvas canvas) {
        int f=Const.REPOSITORY_FRAME_CELL;
        if (wardrobe.isEmpty())
            canvas.drawText(context.getString(R.string.wardrobe_is_empty), width / 2, height / 2, hintPaint);
        else {
            if (wardrobe.size()>length) {
                prev.render(canvas);
                canvas.drawText(String.valueOf(page + 1), width / 20, items[5].top+Const.REPOSITORY_CELL /2+ Const.TEXT_HINT / 2, Assets.hintPaint);
                next.render(canvas);
            }
            for (int i =0; i < length; i++) {
                Clothes.Item item=null;
                synchronized (this) {
                    if (wardrobe.size()>i + (page * (length))) item = wardrobe.get(i + (page * (length)));
                }
                if (item != null) {
                    if (i == selected) canvas.drawRect(new Rect(items[i].left - f, items[i].top - f, items[i].right + f, items[i].bottom + f), Assets.headerPaint);
                    inventoryPaint.setColor(UI.doEvaluation(item.price));
                    canvas.drawRect(items[i], inventoryPaint);
                    item.renderPreview(canvas, items[i].left + Const.REPOSITORY_FRAME_CELL, items[i].top + Const.REPOSITORY_FRAME_CELL);
                    if ((item.compareTo(joeModel.clothes[0])==0||item.compareTo(joeModel.clothes[1])==0||item.compareTo(joeModel.clothes[2])==0||item.compareTo(joeModel.clothes[3])==0)&&(joeModel.clothes[0].purchaseDate==item.purchaseDate|| joeModel.clothes[1].purchaseDate==item.purchaseDate|| joeModel.clothes[2].purchaseDate==item.purchaseDate|| joeModel.clothes[3].purchaseDate==item.purchaseDate)) {
                        canvas.drawBitmap(Assets.other[Assets.Other.DRESSED.ordinal()], items[i].left, items[i].bottom - width / 50, null);
                    }
                } else {

                    canvas.drawRect(items[i], emptyItem);
                }
            }
            info.render(canvas);
            canvas.drawLine(103 * width / 160, height / 4 + height / 100, 103 * width / 160, height / 4 + 65 * width / 200, Assets.headerPaint);
            canvas.drawLine(103 * width / 160, height / 2 + width / 40, width, height / 2 + width / 40, Assets.headerPaint);
            wear.render(canvas);
            takeoff.render(canvas);
            sell.render(canvas);
            if (sellActionPopup.isActive()) sellActionPopup.render(canvas);
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
                if (sellActionPopup.isActive()) {
                    if (sellActionPopup.ok.onTouch(view,motionEvent)) {
                        sellActionPopup.setIdleState();
                        delete();
                        if (selection == null) turnPage(false);
                    }
                    if (sellActionPopup.no.onTouch(view,motionEvent)) {
                        sellActionPopup.setIdleState();
                    }
                    break;
                }
                if (info!=null) if (info.isActive()) {info.onTouch(view,motionEvent); break;}
                if (back.onTouch(view,motionEvent)) getStateManager().setState(StateManager.State.PRELOOK, false);
                if (inventory.onTouch(view,motionEvent)) getStateManager().setState(StateManager.State.INVENTORY, true);
                if (wardrobe.size() > 0) {
                    if (wear.onTouch(view,motionEvent)) {
                        use();
                        break;
                    }
                    if (sell.onTouch(view,motionEvent)) {
                        sellActionPopup.setActiveState();
                        sell.setIdleState();
                        break;
                    }
                    if (takeoff.onTouch(view,motionEvent)) {
                        joeModel.setItem(new Clothes.Item(selection.part,0,1));takeoff.setIdleState();break;}
                    if (wardrobe.size() > length) {
                        if (prev.onTouch(view,motionEvent)) turnPage(false);
                        if (next.onTouch(view,motionEvent)) turnPage(true);
                    }
                    process(x, y);
                }
            } break;
            default:{
                if (sellActionPopup.isActive()) {
                    sellActionPopup.onTouch(view, motionEvent);
                    break;
                }
                back.onTouch(view,motionEvent);
                inventory.onTouch(view,motionEvent);
                if (wardrobe.size() > 0) {
                    wear.onTouch(view,motionEvent);
                    sell.onTouch(view,motionEvent);
                    takeoff.onTouch(view,motionEvent);
                    info.onTouch(view,motionEvent);
                }
                if (wardrobe.size() > length) {
                    prev.onTouch(view,motionEvent);
                    next.onTouch(view,motionEvent);
                }
            }
        }
        return true;
    }

    @Override
    public void use() {
        joeModel.setItem(selection);
        joeModel.clothes[selection.part].purchaseDate=selection.purchaseDate;
    }

    @Override
    public void delete() {
        playerData.addMoney(selection.price/2);
        if (selection.purchaseDate== joeModel.clothes[selection.part].purchaseDate) joeModel.setItem(new Clothes.Item(selection.part,0,0));
        playerData.removeWardrobeItem(selected+page*length);
        selected = (selected == 0 ? 0 : --selected);
        if (selected+page*length!=wardrobe.size()) {
            selection = wardrobe.get(selected+page*length);
            joeModel.clothes[selection.part].purchaseDate=selection.purchaseDate;
        } else turnPage(false);

    }

    @Override
    public void process(int x, int y) {
        for (int i = 0; i< length; i++){
            if (items[i].contains(x,y)&&wardrobe.size()>i+page*length) {
                sfx.play(Sfx.CLICK_POSITIVE);
                selected=i;
                selection = wardrobe.get(selected+page*length);
                info.updateText(selection.printInfo());
                break;
            }
        }
    }

    @Override
    public boolean onBackPressed() {
        if (!sellActionPopup.onBackPressed()) getStateManager().setState(StateManager.State.PRELOOK, false);
        return true;
    }

    @Override
    public void turnPage(boolean increment) {
        synchronized (this) {
            page = increment ? (page + 1 > max - 1 ? 0 : page + 1) : (page - 1 < 0 ? max - 1 : page - 1);
            selected = 0;
            if (!wardrobe.isEmpty())selection = wardrobe.get(selected + page * length);
        }
    }
}