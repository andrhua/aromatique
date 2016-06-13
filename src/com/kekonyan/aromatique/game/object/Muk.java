package com.kekonyan.aromatique.game.object;

import android.graphics.Canvas;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.game.FixtureData;
import com.kekonyan.aromatique.game.contactslistener.ContactsListener;
import com.kekonyan.aromatique.game.tiled.core.MapObject;
import com.kekonyan.aromatique.util.Const;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

public class Muk extends StaticGameObject implements IQueryable, IResetable{
    private Vec2 velocity;
    private World world;

    public Muk(int id, MapObject mapObject, World world, int zOrder) {
        super(id, mapObject, zOrder);
        this.world=world;
        polygonShape.setAsBox(width/2, height/2);
        bodyDef.type=BodyType.DYNAMIC;
        bodyDef.gravityScale=0;
        bodyDef.position.set(centerX, centerY);
        body=world.createBody(bodyDef);
        fixtureDef.shape=polygonShape;
        fixtureDef.filter.categoryBits= ContactsListener.CATEGORY_ENEMY;
        fixtureDef.filter.maskBits=ContactsListener.MASK_ENEMY;
        fixtureDef.isSensor=true;
        fixture=body.createFixture(fixtureDef);
        fixture.setUserData(new FixtureData(FixtureData.Type.MUK, id));
        type= FixtureData.Type.MUK;
        velocity=new Vec2(0, 10);
    }

    @Override
    public void render(Canvas canvas) {
        canvas.save();
        canvas.translate((body.getPosition().x-width/2)*Const.PPM, (body.getPosition().y-height/2)*Const.PPM);
        canvas.drawBitmap(Assets.Game.objects[Assets.Game.Object.MUK.ordinal()], 0, 0, null);
        canvas.restore();
    }

    @Override
    public void queryForMagic() {
        body.setLinearVelocity(velocity);
    }

    @Override
    public void reset() {
        body.setActive(false);
        world.destroyBody(body);
        body=world.createBody(bodyDef);
        body.createFixture(fixtureDef).setUserData(new FixtureData(FixtureData.Type.MUK, id));
        body.setLinearVelocity(new Vec2());

    }
}
