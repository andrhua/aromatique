package com.kekonyan.aromatique.game.object;

import android.graphics.Canvas;
import com.kekonyan.aromatique.GameActivity;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.game.FixtureData;
import com.kekonyan.aromatique.game.contactslistener.ContactsListener;
import com.kekonyan.aromatique.game.tiled.core.MapObject;
import com.kekonyan.aromatique.util.Const;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

public class Bird extends HauntingEnemy {
    private World world;

    public Bird(int id, MapObject mapObject, World world, int zOrder, Indicator indicator) {
        super(id, mapObject, world, zOrder, indicator);
        polygonShape.setAsBox(width/2, height/2);
        bodyDef.type= BodyType.DYNAMIC;
        bodyDef.gravityScale=0;
        bodyDef.position.set(centerX, centerY);
        body=world.createBody(bodyDef);
        fixtureDef.shape=polygonShape;
        fixtureDef.filter.categoryBits= ContactsListener.CATEGORY_FLYING_ENEMY;
        fixtureDef.filter.maskBits=ContactsListener.MASK_FLYING_ENEMY;
        fixtureDef.isSensor=true;
        fixture=body.createFixture(fixtureDef);
        fixture.setUserData(new FixtureData(FixtureData.Type.BIRD, id));
        type= FixtureData.Type.BIRD;
    }

    @Override
    public void render(Canvas canvas) {
        canvas.save();
        canvas.translate((body.getPosition().x-1)* Const.PPM, (body.getPosition().y-1)*Const.PPM);
        canvas.drawBitmap(Assets.Game.objects[Assets.Game.Object.BIRD.ordinal()], 0, 0, null);
        canvas.restore();
    }

    @Override
    public void update(float elapsedTime) {
        super.update(elapsedTime);
        switch (behaviour){
            case HAUNTING:
                Vec2 direction= GameActivity.joe.getPosition().sub(body.getPosition());
                Vec2 velocity=new Vec2(direction.x/direction.length(), direction.y/direction.length()).mul(4);
                body.setLinearVelocity(velocity);
        }
    }

    @Override
    public void reset() {
        super.reset();
        body.createFixture(fixtureDef).setUserData(new FixtureData(FixtureData.Type.BIRD, id));
    }
}
