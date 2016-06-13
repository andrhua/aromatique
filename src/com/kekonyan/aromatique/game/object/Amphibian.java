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

public class Amphibian extends HauntingEnemy{

    @Override
    public void queryForMagic() {
        super.queryForMagic();
    }

    public Amphibian(int id, MapObject mapObject, World world, int zOrder, Indicator indicator) {
        super(id, mapObject, world, zOrder, indicator);
        this.world=world;
        polygonShape.setAsBox(width/2, height/6);
        bodyDef.type= BodyType.DYNAMIC;
        bodyDef.position.set(centerX, centerY);
        body=world.createBody(bodyDef);
        fixtureDef.shape=polygonShape;
        fixtureDef.filter.categoryBits= ContactsListener.CATEGORY_ENEMY;
        fixtureDef.filter.maskBits=ContactsListener.MASK_ENEMY;
        body.createFixture(fixtureDef).setUserData(new FixtureData(FixtureData.Type.AMPHIBIAN, id));
    }

    @Override
    public void render(Canvas canvas) {
        canvas.save();
        canvas.translate(body.getPosition().x*Const.PPM-Const.PPM*1.5f, body.getPosition().y*Const.PPM- Const.PPM*1.5f);
        canvas.drawBitmap(Assets.Game.objects[Assets.Game.Object.AMPHIBIAN.ordinal()], 0, 0, null);
        canvas.restore();
    }

    @Override
    public void update(float elapsedTime) {
        super.update(elapsedTime);
        switch (behaviour){
            case HAUNTING:
                Vec2 direction=GameActivity.joe.getPosition().sub(body.getPosition());
                scaleX=direction.x<0?-1:1;
                Vec2 velocity=new Vec2(direction.x/direction.length(), direction.y/direction.length()).mul(3.5f);
                body.setLinearVelocity(velocity);
        }
    }

    @Override
    public void reset() {
        super.reset();
        fixture.setUserData(new FixtureData(FixtureData.Type.AMPHIBIAN, id));
        setActiveState();
    }

    public void die(){
        body.setActive(false);
        world.destroyBody(body);
        this.setIdleState();
    }
}
