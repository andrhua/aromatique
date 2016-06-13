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

public class PacmanGhost extends DynamicGameObject{
    private Vec2 impulse;
    private boolean needToImpulse;
    private World world;
    private Indicator indicator;

    public PacmanGhost(int id, MapObject mapObject, World world, int zOrder, Indicator indicator) {
        super(id, mapObject, zOrder);
        this.world=world;
        this.indicator=indicator;
        polygonShape.setAsBox(width/2, height/2);
        bodyDef.type= BodyType.DYNAMIC;
        bodyDef.position.set(centerX, centerY);
        body=world.createBody(bodyDef);
        fixtureDef.shape=polygonShape;
        fixtureDef.filter.categoryBits= ContactsListener.CATEGORY_ENEMY;
        fixtureDef.filter.maskBits=ContactsListener.MASK_ENEMY;
        fixtureDef.density=1;
        fixture=body.createFixture(fixtureDef);
        fixture.setUserData(new FixtureData(FixtureData.Type.PACMAN_GHOST, id));
        type= FixtureData.Type.PACMAN_GHOST;
        impulse=new Vec2(0, -40);
    }

    @Override
    public void render(Canvas canvas) {
        canvas.save();
        canvas.translate(((body.getPosition().x-width/2)* Const.PPM), (body.getPosition().y-height/2)*Const.PPM);
        canvas.drawBitmap(Assets.Game.objects[Assets.Game.Object.PACMAN_GHOST.ordinal()], 0, 0, null);
        canvas.restore();
    }

    @Override
    public void update(float elapsedTime) {
        if (indicator.isActive()) needToImpulse=true;
        if (needToImpulse){
            body.applyLinearImpulse(impulse, body.getPosition(), true);
            needToImpulse=false;
        }
    }

    @Override
    public void reset() {
        body.setActive(false);
        world.destroyBody(body);
        body=world.createBody(bodyDef);
        body.createFixture(fixtureDef).setUserData(new FixtureData(FixtureData.Type.PACMAN_GHOST, id));
    }

}
