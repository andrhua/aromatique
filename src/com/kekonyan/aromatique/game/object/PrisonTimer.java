package com.kekonyan.aromatique.game.object;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.game.tiled.core.MapObject;
import com.kekonyan.aromatique.util.Const;

import java.util.concurrent.TimeUnit;

public class PrisonTimer extends DynamicGameObject implements IQueryable{
    private long time;
    private Paint paint;
    private boolean needToCountdown;

    public PrisonTimer(int id, MapObject mapObject, int zOrder) {
        super(id, mapObject, zOrder);
        paint=new Paint();
        paint.setColor(Color.WHITE);
        time= TimeUnit.MINUTES.toMillis(2);
    }

    @Override
    public void render(Canvas canvas) {
        canvas.save();
        canvas.translate(left* Const.PPM, top*Const.PPM);
        canvas.drawRect(0, 0, width*Const.PPM, height*Const.PPM, paint);
        canvas.drawText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(time),
                TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time))
        ), width/2*Const.PPM, 3*height/5*Const.PPM, Assets.hintPaint);
        canvas.restore();
    }

    @Override
    public void update(float elapsedTime) {
        if (needToCountdown) time-=elapsedTime;
        if (time<=0){
            needToCountdown=false;
            time=0;
        }
    }

    @Override
    public void reset() {
        needToCountdown=false;
        time= TimeUnit.MINUTES.toMillis(2);
    }

    @Override
    public void queryForMagic() {
        needToCountdown=true;
    }
}
