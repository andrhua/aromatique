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

public class Blastoise extends DynamicGameObject {
    private float left, right;
    private boolean isGoingLeft;
    private Vec2 velocity;
    private World world;

    public Blastoise(int id, MapObject mapObject, World world, int zOrder) {
        super(id, mapObject, zOrder);
        this.world=world;
        left=Float.valueOf(mapObject.getProperties().getProperty("left"));
        right=Float.valueOf(mapObject.getProperties().getProperty("right"));
        polygonShape.setAsBox(1.5f, 1.5f);
        bodyDef.type= BodyType.DYNAMIC;
        bodyDef.position.set(centerX, centerY);
        body=world.createBody(bodyDef);
        fixtureDef.shape=polygonShape;
        fixtureDef.filter.categoryBits= ContactsListener.CATEGORY_ENEMY;
        fixtureDef.filter.maskBits=ContactsListener.MASK_ENEMY;
        body.createFixture(fixtureDef).setUserData(new FixtureData(FixtureData.Type.BLASTOISE, id));
        type=FixtureData.Type.WOOMBA;
        isGoingLeft=true;
        velocity=new Vec2(MathUtils.randomFloat(-3, -4), 0);
    }

    @Override
    public void render(Canvas canvas) {
        canvas.save();
        canvas.translate((body.getPosition().x-1.5f)* Const.PPM, (body.getPosition().y-1.5f)*Const.PPM);
        canvas.drawBitmap(Assets.Game.objects[Assets.Game.Object.BLASTOISE.ordinal()], 0, 0, null);
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
        body=world.createBody(bodyDef);
        body.createFixture(fixtureDef).setUserData(new FixtureData(FixtureData.Type.WOOMBA, id));
    }
}
