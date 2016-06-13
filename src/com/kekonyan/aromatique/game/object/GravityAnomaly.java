package com.kekonyan.aromatique.game.object;

import android.graphics.Canvas;
import com.kekonyan.aromatique.GameActivity;
import com.kekonyan.aromatique.game.FixtureData;
import com.kekonyan.aromatique.game.contactslistener.ContactsListener;
import com.kekonyan.aromatique.game.tiled.core.MapObject;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

public class GravityAnomaly extends DynamicGameObject {
    private enum Kind {NONE, GEYSER}
    private Kind kind = Kind.NONE;
    private Vec2 anomaly;
    public boolean needToAnomaly;
    private Joe joe;

    public GravityAnomaly(int id, MapObject mapObject, World world) {
        super(id, mapObject, 0);
        polygonShape.setAsBox(width/2, height/2);
        bodyDef.type= BodyType.STATIC;
        bodyDef.position.set(centerX, centerY);
        body=world.createBody(bodyDef);
        fixtureDef.shape=polygonShape;
        fixtureDef.filter.categoryBits= ContactsListener.CATEGORY_SCENERY;
        fixtureDef.filter.maskBits=ContactsListener.MASK_SCENERY;
        fixtureDef.isSensor=true;
        fixture=body.createFixture(fixtureDef);
        fixture.setUserData(new FixtureData(FixtureData.Type.GRAVITY_ANOMALY, id));
        type = FixtureData.Type.GRAVITY_ANOMALY;
        joe=GameActivity.joe;
        anomaly=new Vec2(Float.valueOf(mapObject.getProperties().getProperty("anomalyX")),
                Float.valueOf(mapObject.getProperties().getProperty("anomalyY")));
        setIdleState();
    }

    @Override
    public void render(Canvas canvas) {

    }

    @Override
    public void update(float elapsedTime) {
        if (needToAnomaly&&isActive()){
            joe.body.applyLinearImpulse(anomaly, joe.getPosition(), true);
        }
    }

    @Override
    public void reset() {
        setIdleState();
    }

}
