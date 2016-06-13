package com.kekonyan.aromatique.game.object;

import android.graphics.Canvas;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.game.FixtureData;
import com.kekonyan.aromatique.game.contactslistener.ContactsListener;
import com.kekonyan.aromatique.game.tiled.core.MapObject;
import com.kekonyan.aromatique.util.Const;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

public class Prequark extends StaticGameObject implements IResetable {
    protected World world;
    
    public Prequark(int id, MapObject mapObject, World world, int zOrder) {
        super(id, mapObject, zOrder);
        this.world=world;
        bodyDef.type= BodyType.STATIC;
        bodyDef.gravityScale=0;
        bodyDef.position.set(centerX, centerY);
        body=world.createBody(bodyDef);
        polygonShape.setAsBox(.45f, .45f);
        fixtureDef.shape=polygonShape;
        fixtureDef.isSensor=true;
        fixtureDef.filter.categoryBits=ContactsListener.CATEGORY_SCENERY;
        fixtureDef.filter.maskBits= ContactsListener.MASK_SCENERY;
        fixture=body.createFixture(fixtureDef);
        fixture.setUserData(new FixtureData(FixtureData.Type.PREQUARK, id));
        type= FixtureData.Type.PREQUARK;
    }


    @Override
    public void render(Canvas canvas) {
        canvas.save();
        canvas.translate(left*Const.PPM, top*Const.PPM);
        canvas.drawBitmap(Assets.Game.objects[Assets.Game.Object.PREQUARK.ordinal()], 0, 0, null);
        canvas.restore();
    }

    @Override
    public void reset() {
        body.setActive(false);
        world.destroyBody(body);
        body=world.createBody(bodyDef);
        body.createFixture(fixtureDef).setUserData(new FixtureData(FixtureData.Type.PREQUARK, id));
    }
}
