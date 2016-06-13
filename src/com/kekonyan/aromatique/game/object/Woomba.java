package com.kekonyan.aromatique.game.object;

import android.graphics.Canvas;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.game.FixtureData;
import com.kekonyan.aromatique.game.contactslistener.ContactsListener;
import com.kekonyan.aromatique.game.tiled.core.MapObject;
import com.kekonyan.aromatique.util.Const;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

public class Woomba extends DynamicGameObject {
    private float left, right;
    private boolean isGoingLeft;
    private Vec2 velocity;
    private World world;

    public Woomba(int id, MapObject mapObject, World world, int zOrder) {
        super(id, mapObject, zOrder);
        this.world=world;
        left=Float.valueOf(mapObject.getProperties().getProperty("left"));
        right=Float.valueOf(mapObject.getProperties().getProperty("right"));
        bodyDef.gravityScale=Integer.valueOf(mapObject.getProperties().getProperty("gravity","1"));
        polygonShape.setAsBox(.4f, .4f);
        bodyDef.type= BodyType.DYNAMIC;
        bodyDef.position.set(centerX, centerY);
        body=world.createBody(bodyDef);
        fixtureDef.shape=polygonShape;
        fixtureDef.filter.categoryBits= ContactsListener.CATEGORY_ENEMY;
        fixtureDef.filter.maskBits=ContactsListener.MASK_ENEMY;
        body.createFixture(fixtureDef).setUserData(new FixtureData(FixtureData.Type.WOOMBA, id));
        polygonShape.setAsBox(.2f, .2f, new Vec2(0, -.4f), 0);
        fixtureDef.isSensor=true;
        body.createFixture(fixtureDef).setUserData(new FixtureData(FixtureData.Type.WOOMBA_SENSOR, this.id));
        type=FixtureData.Type.WOOMBA;
        isGoingLeft=true;
        velocity=new Vec2(MathUtils.randomFloat(-9, -8), 0);
    }

    @Override
    public void render(Canvas canvas) {
        canvas.save();
        canvas.translate((body.getPosition().x-.5f)*Const.PPM, (body.getPosition().y-.5f)*Const.PPM);
        canvas.drawBitmap(Assets.Game.objects[Assets.Game.Object.WOOMBA.ordinal()], 0, 0, null);
        canvas.restore();
    }

    @Override
    public void update(float elapsedTime) {
        if (body.getPosition().x<=this.centerX-left) isGoingLeft=false;
        if (body.getPosition().x>=this.centerX+right) isGoingLeft=true;
        body.applyLinearImpulse(isGoingLeft?velocity:velocity.abs(), body.getPosition(), true);
        if (isGoingLeft){
            if (body.getLinearVelocity().x<velocity.x) body.setLinearVelocity(new Vec2(velocity.x, body.getLinearVelocity().y));
        } else if (body.getLinearVelocity().x>velocity.abs().x) body.setLinearVelocity(new Vec2(velocity.abs().x, body.getLinearVelocity().y));
    }

    @Override
    public void reset() {
        body.setActive(false);
        world.destroyBody(body);
        polygonShape.setAsBox(.4f, .4f);
        body=world.createBody(bodyDef);
        fixtureDef.isSensor=false;
        body.createFixture(fixtureDef).setUserData(new FixtureData(FixtureData.Type.WOOMBA, id));
        polygonShape.setAsBox(.2f, .2f, new Vec2(0, -.4f), 0);
        fixtureDef.isSensor=true;
        body.createFixture(fixtureDef).setUserData(new FixtureData(FixtureData.Type.WOOMBA_SENSOR, this.id));
        setActiveState();
    }
}
