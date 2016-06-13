package com.kekonyan.aromatique.game.object;

import android.graphics.Canvas;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.game.FixtureData;
import com.kekonyan.aromatique.game.tiled.core.MapObject;
import com.kekonyan.aromatique.util.Const;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

public class Bullet extends DynamicGameObject {
    private boolean needToReset;
    private World world;

    Bullet(int id, MapObject mapObject, World world, int zOrder) {
        super(id, mapObject, zOrder);
        this.world=world;
        bodyDef.type = BodyType.DYNAMIC;
        bodyDef.position.set(centerX, centerY);
        bodyDef.bullet=true;
        bodyDef.gravityScale=0;
        body = world.createBody(bodyDef);
        polygonShape.setAsBox(.2f, .2f);
        fixtureDef.shape = polygonShape;
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(new FixtureData(FixtureData.Type.BULLET, id));
        type= FixtureData.Type.BULLET;
        needToReset=false;
    }

    @Override
    public void render(Canvas canvas) {
        Vec2 position=body.getPosition();
        canvas.save();
        canvas.translate((position.x)* Const.PPM, (position.y+.5f)*Const.PPM);
        canvas.drawCircle(0,0, Const.PPM/5, Assets.Game.spikePaint);
        canvas.restore();
    }

    void fire(Vec2 direction){
        direction.normalize();
        body.setLinearVelocity(direction.mul(7));
    }


    @Override
    public void reset(){
        needToReset=true;
    }

    @Override
    public void update(float elapsedTime) {
        if (needToReset) {
            body.setTransform(new Vec2(left +.5f, top +.5f), 0);
            body.setLinearVelocity(new Vec2(0,0));
            needToReset=false;
        }
    }
}
