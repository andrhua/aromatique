package com.kekonyan.aromatique.game.object;

import android.graphics.Canvas;
import android.graphics.RectF;
import com.kekonyan.aromatique.GameActivity;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.game.FixtureData;
import com.kekonyan.aromatique.game.tiled.core.MapObject;
import com.kekonyan.aromatique.util.Const;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

public class Turret extends DynamicGameObject {
    private RectF oval;
    private float time;
    private Bullet bullet;
    private Vec2 direction;
    private boolean canShoot;

    public Turret(int id, MapObject mapObject, World world, int zOrder) {
        super(id, mapObject, zOrder);
        bodyDef.type = BodyType.STATIC;
        bodyDef.position.set(centerX, centerY);
        body = world.createBody(bodyDef);
        polygonShape.setAsBox(.5f, .5f);
        fixtureDef.shape = polygonShape;
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(new FixtureData(FixtureData.Type.TURRET, id));
        type= FixtureData.Type.TURRET;
        oval=new RectF(0, 0, Const.PPM, Const.PPM);
        time=0;
        bullet=new Bullet(id, mapObject, world, zOrder);
        direction=new Vec2();
        canShoot=false;
    }

    @Override
    public void render(Canvas canvas) {
        bullet.render(canvas);
        canvas.save();
        canvas.translate((left)* Const.PPM, (top)*Const.PPM);
        canvas.drawArc(oval, 0, 180, true, Assets.moneyPaint);
        //canvas.drawRect(0, -.5f, Const.PPM, Const.PPM, Assets.aromaPaint);
        canvas.restore();
        /*canvas.save();
        float angle= MathUtils.fastAtan2(direction.top, direction.left)-MathUtils.fastAtan2(0, -1);
        Log.d("angle", angle+"");
        canvas.rotate(angle);
        canvas.translate((position.left-.25f)* Const.PPM, (position.top+.5f)*Const.PPM);
        canvas.drawRect(0, 0, Const.PPM/4, Const.PPM, Assets.aromaPaint);
        canvas.restore();*/
    }

    @Override
    public void update(float elapsedTime) {
        time+=elapsedTime;
        float gap = 3200;
        if (time>= gap) {
            time=0;
            if (bullet.body.getLinearVelocity().y!=0&&bullet.body.getLinearVelocity().x!=0) bullet.reset();
            direction=GameActivity.joe.getPosition().sub(body.getPosition());
            if (MathUtils.abs(body.getPosition().x-GameActivity.joe.getPosition().x)<=5&&
                    MathUtils.abs(body.getPosition().y-GameActivity.joe.getPosition().y)<=5) canShoot=true;
            if (canShoot) bullet.fire(direction);
        }
        bullet.update(elapsedTime);
    }

    public void resetBullet(){
        bullet.reset();
    }

    @Override
    public void reset() {

    }
}
