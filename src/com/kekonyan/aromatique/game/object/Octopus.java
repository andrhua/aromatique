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

public class Octopus extends HauntingEnemy {
    private World world;

    public Octopus(int id, MapObject mapObject, World world, int zOrder, Indicator indicator) {
        super(id, mapObject, world, zOrder, indicator);
        this.world=world;
        polygonShape.setAsBox(1, 1);
        bodyDef.type= BodyType.DYNAMIC;
        bodyDef.position.set(centerX, centerY);
        body=world.createBody(bodyDef);
        fixtureDef.shape=polygonShape;
        fixtureDef.filter.categoryBits= ContactsListener.CATEGORY_ENEMY;
        fixtureDef.filter.maskBits=ContactsListener.MASK_ENEMY;
        fixtureDef.density=1;
        fixture=body.createFixture(fixtureDef);
        fixture.setUserData(new FixtureData(FixtureData.Type.OCTOPUS, id));
        type= FixtureData.Type.OCTOPUS;
    }

    @Override
    public void render(Canvas canvas) {
        canvas.save();
        canvas.translate((body.getPosition().x-1)* Const.PPM, (body.getPosition().y-1)*Const.PPM);
        canvas.drawBitmap(Assets.Game.objects[Assets.Game.Object.OCTOPUS.ordinal()], 0, 0, null);
        canvas.restore();
    }

    @Override
    public void update(float elapsedTime) {
        if (behaviour==Behaviour.HAUNTING) {
            Vec2 direction = GameActivity.joe.getPosition().sub(body.getPosition());
            Vec2 velocity = new Vec2(direction.x / direction.length(), (direction.y-1) / direction.length()).mul(5.5f);
            body.setLinearVelocity(velocity);
        }
    }

    @Override
    public void reset() {
        body.setActive(false);
        world.destroyBody(body);
        body=world.createBody(bodyDef);
        fixture=body.createFixture(fixtureDef);
        fixture.setUserData(new FixtureData(FixtureData.Type.OCTOPUS, id));
    }

}
