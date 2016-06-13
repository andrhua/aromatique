package com.kekonyan.aromatique.state;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.kekonyan.aromatique.GameActivity;
import com.kekonyan.aromatique.R;
import com.kekonyan.aromatique.UI.Background;
import com.kekonyan.aromatique.UI.explosionfield.ExplosionField;
import com.kekonyan.aromatique.audio.Sfx;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.model.Clothes;
import com.kekonyan.aromatique.model.Container;

import java.io.IOException;

class OpenState extends BasicState {
    private boolean isFromContainerState;
    private Container container;
    private enum State {WAIT, OPENING, DROP}
    private State state;
    private LinearLayout ll;
    private ImageView imageView;
    private ExplosionField explosionField;
    private Clothes.Item drop;
    private float time;
    private Bitmap dropBMP;
    private Background background;
    private Paint bmp, hint, regular, aroma;

    @Override
    void preload() throws IOException {
    }

    OpenState(Context context, StateManager stateManager, Container container, boolean isFromContainerState) {
        super(context, stateManager);
        bmp =new Paint();hint=new Paint(Assets.hintPaint); regular=new Paint(Assets.regularPaint); aroma=new Paint(Assets.aromaPaint);
        bmp.setAlpha(0); aroma.setAlpha(0);
        this.isFromContainerState=isFromContainerState;
        this.container=container;
        background=new Background(Assets.backgrounds[Assets.Background.CLOVER.ordinal()]);
        drop=Container.generateItem(container.content);
        dropBMP=drop.getPreviewLargedBitmap();
        explosionField=ExplosionField.attach2Window(GameActivity.activity);
        explosionField.layout(0, 0, width, height);
        imageView=new ImageView(context);
        imageView.setBackground(new BitmapDrawable(container.getBitmap()));
        ll=new LinearLayout(context);
        ll.setGravity(Gravity.CENTER);
        ll.addView(imageView);
        ll.measure(width, height);
        ll.layout(0,0,width,height);
        state=State.WAIT;
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
        background.render(canvas);
        ll.draw(canvas);
        switch (state){
            case WAIT:{
                canvas.drawText(container.name, width/2, height/4, Assets.regularPaint);
                canvas.drawText(context.getString(R.string.tap_to_open), width/2, 4*height/5, hint);
            } break;
            case DROP: {
                canvas.drawText(drop.brand.concat(" ").concat(drop.model), width/2, height/5, regular);
                canvas.drawText(context.getString(R.string.tap_to_continue), width/2, 14*height/15, hint);
                canvas.drawRect(3*width/8, height/2-width/8, 5*width/8, height/2+width/8, aroma);
                canvas.drawBitmap(dropBMP, width/2-width/8, height/2-width/8, bmp);
            } break;
        }

    }

    @Override
    public void update(float elapsedTime) {
        background.update(elapsedTime);
        if (state==State.OPENING) time+=elapsedTime;
        if (time>=2000) state=State.DROP;
        if (state==State.DROP) {
            time+=elapsedTime;
            int delta= bmp.getAlpha()+(int)(elapsedTime*.225f);
            if (delta<=255) {
                bmp.setAlpha(delta);
                hint.setAlpha(delta);
                regular.setAlpha(delta);
                aroma.setAlpha(delta);
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction()==MotionEvent.ACTION_UP)
            switch (state){
                case WAIT:
                    GameActivity.sfx.play(Sfx.CONTAINER_OPENING);
                    hint.setAlpha(0);
                    regular.setAlpha(0);
                    explosionField.explode(imageView);
                    playerData.removeInventoryItem(playerData.getInventory().size()-1);
                    playerData.addWardrobeItem(new Clothes.Item(drop.part, drop.index, System.currentTimeMillis()));
                    state=State.OPENING; break;
                case DROP:
                    if (time>=3000) {
                        GameActivity.sfx.play(Sfx.CLICK_NEGATIVE);
                        getStateManager().setState(isFromContainerState ? StateManager.State.CONTAINER : StateManager.State.INVENTORY, false);
                        break;
                    }
            }
        return true;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
