package com.kekonyan.aromatique.game.object;

import android.graphics.Canvas;
import com.kekonyan.aromatique.game.FixtureData;
import com.kekonyan.aromatique.game.contactslistener.ContactsListener;
import com.kekonyan.aromatique.game.tiled.core.MapObject;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

public class Indicator extends StaticGameObject {

    public Indicator(int id, MapObject mapObject, World world) {
        super(id, mapObject, 0);
        polygonShape.setAsBox(width/2, height/2);
        bodyDef.type= BodyType.STATIC;
        bodyDef.position.set(centerX, centerY);
        body=world.createBody(bodyDef);
        fixtureDef.shape=polygonShape;
        fixtureDef.isSensor=true;
        fixtureDef.filter.categoryBits= ContactsListener.CATEGORY_TRAP;
        fixtureDef.filter.maskBits=ContactsListener.MASK_SCENERY;
        fixture=body.createFixture(fixtureDef);
        fixture.setUserData(new FixtureData(FixtureData.Type.INDICATOR, id));
        type= FixtureData.Type.INDICATOR;
        setIdleState();
    }

    @Override
    public void render(Canvas canvas) {

    }

    public static class Death extends Indicator{

        public Death(int id, MapObject mapObject, World world) {
            super(id, mapObject, world);
            fixture.setUserData(new FixtureData(FixtureData.Type.DEATH_INDICATOR, id));
            type= FixtureData.Type.DEATH_INDICATOR;
        }
    }
}
