package com.kekonyan.aromatique.UI;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.core.IRenderable;
import com.kekonyan.aromatique.core.IUpdatable;
import com.kekonyan.aromatique.util.Const;

import java.security.SecureRandom;

public class Background implements IRenderable, IUpdatable, View.OnTouchListener{
    private BackgroundImage[][]bgimages;
    private int length, direction;
    private static final int DIRECTION_LEFT=0, DIRECTION_RIGHT=1;

    public Background(Bitmap backgroundImage){
        int width = Const.WIDTH;
        int height = Const.HEIGHT;
        length=4;
        bgimages=new BackgroundImage[5][length+1];
        direction=new SecureRandom().nextInt(2);
        for (int i=0; i<5; i++){
            for (int j=0; j<length+1; j++) {
                bgimages[i][j]=(new BackgroundImage(backgroundImage, j * width /length, i * height / 5+Const.BACKGROUND_BMP));
            }
        }
    }

    @Override
    public void render(Canvas canvas) {
        for (int i=0; i<5; i++){
            for (int j=0; j<length+1; j++) {
                canvas.save();
                canvas.translate(bgimages[i][j].x,(bgimages[i][j].y));
                canvas.rotate(bgimages[i][j].angle);
                canvas.drawBitmap(bgimages[i][j].image,-Assets.backgrounds[0].getWidth()/2,-Assets.backgrounds[0].getHeight()/2,null);
                canvas.restore();
            }
        }
    }

    @Override
    public void update(float elapsedTime) {
        for (int i=0; i<5; i++){
            for (int j=0; j<length+1; j++) {
                bgimages[i][j].angle += elapsedTime / 10 * bgimages[i][j].speed;
                switch (direction) {
                    case DIRECTION_LEFT: {
                        bgimages[i][j].x -= elapsedTime / 10 * 2;
                        if (bgimages[i][j].x < -Const.BACKGROUND_BMP) {
                            bgimages[i][j].x = bgimages[i][j == 0 ? length : j - 1].x + Const.WIDTH / length;
                        }
                    }
                    break;
                    case DIRECTION_RIGHT: {
                        bgimages[i][j].x += elapsedTime / 10 * 2;
                        if (bgimages[i][j].x > Const.WIDTH +Const.BACKGROUND_BMP) {
                            bgimages[i][j].x = bgimages[i][j == length ? 0 : j + 1].x - Const.WIDTH / length;
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    private static class BackgroundImage {
        int x,y,speed;
        float angle;

        Bitmap image;
        BackgroundImage(Bitmap image, int x, int y){
            this.image=image;
            this.x=x;
            this.y=y;
            speed=(int)(Math.random()*10)-5;
            if (speed==0) speed=1;
            angle=0;
        }
    }
}
