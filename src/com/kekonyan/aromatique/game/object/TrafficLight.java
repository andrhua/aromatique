package com.kekonyan.aromatique.game.object;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.game.FixtureData;
import com.kekonyan.aromatique.game.tiled.core.MapObject;
import com.kekonyan.aromatique.util.Const;

public class TrafficLight extends DynamicGameObject implements IQueryable, IResetable{
    public enum State {IDLE, RED, YELLOW, FAKE_GREEN, GREEN}
    private State state;
    private Paint paint;
    private float time;

    public State getState() {
        return state;
    }

    public TrafficLight(int id, MapObject mapObject, int zOrder) {
        super(id, mapObject, zOrder);
        state=State.IDLE;
        paint=new Paint();
        paint.setColor(Color.rgb(220,220,220));
        time=0;
        type= FixtureData.Type.TRAFFIC_LIGHT;
    }


    @Override
    public void render(Canvas canvas) {
        canvas.save();
        canvas.translate(left* Const.PPM, top*Const.PPM);
        canvas.drawRect(0, 0, width*Const.PPM, height*Const.PPM, Assets.hintPaint);
        canvas.drawCircle(width/2*Const.PPM, height/6*Const.PPM, height/8*Const.PPM, paint);
        canvas.drawCircle(width/2*Const.PPM, height/2*Const.PPM, height/8*Const.PPM, paint);
        canvas.drawCircle(width/2*Const.PPM, 5*height/6*Const.PPM, height/8*Const.PPM, paint);
        switch (state){
            case RED:
                paint.setColor(Color.RED);
                canvas.drawCircle(width/2*Const.PPM, height/6*Const.PPM, height/8*Const.PPM, paint);
                paint.setColor(Color.rgb(220,220,240));
                break;
            case YELLOW:
                paint.setColor(Color.YELLOW);
                canvas.drawCircle(width/2*Const.PPM, height/2*Const.PPM, height/8*Const.PPM, paint);
                paint.setColor(Color.rgb(220,220,220));
                break;
            case GREEN:
                paint.setColor(Color.GREEN);
                canvas.drawCircle(width/2*Const.PPM, 5*height/6*Const.PPM, height/8*Const.PPM, paint);
                paint.setColor(Color.rgb(220,220,220));
                break;
        }
        canvas.restore();
    }

    @Override
    public void update(float elapsedTime) {
        if (state!=State.IDLE) time+=elapsedTime;
        if (time>=4100) state=State.RED; else
        if (time>=3400) state=State.GREEN; else
        if (time>=2000) state=State.FAKE_GREEN; else
        if (time>=1000) state=State.YELLOW;
    }

    @Override
    public void queryForMagic() {
        state=State.RED;
    }

    @Override
    public void reset() {
        state=State.IDLE;
        time=0;
    }
}
