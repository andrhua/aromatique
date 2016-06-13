package com.kekonyan.aromatique.game.object;

import android.graphics.Canvas;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.game.FixtureData;
import com.kekonyan.aromatique.game.contactslistener.ContactsListener;
import com.kekonyan.aromatique.game.tiled.core.MapObject;
import com.kekonyan.aromatique.util.Const;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

public class Coin extends StaticGameObject implements IResetable{
    private World world;

    public Coin(int id, MapObject mapObject, World world, int zOrder) {
        super(id, mapObject, zOrder);
        this.world=world;
        bodyDef.position.set(centerX, centerY);
        body=world.createBody(bodyDef);
        polygonShape.setAsBox(.30f, .30f);
        fixtureDef.shape=polygonShape;
        fixtureDef.isSensor=true;
        fixtureDef.filter.categoryBits= ContactsListener.CATEGORY_COIN;
        fixtureDef.filter.maskBits=ContactsListener.MASK_COIN;
        fixture=body.createFixture(fixtureDef);
        fixture.setUserData(new FixtureData(FixtureData.Type.COIN, id));
        type= FixtureData.Type.COIN;
    }

    @Override
    public void render(Canvas canvas) {
        Vec2 position = body.getPosition();
        canvas.save();
        canvas.translate((position.x - .3f) * Const.PPM, (position.y - .3f) * Const.PPM);
        canvas.drawBitmap(Assets.Game.objects[Assets.Game.Object.COIN.ordinal()], 0, 0, null);
        canvas.restore();
    }

    @Override
    public void reset() {
        body.setActive(false);
        world.destroyBody(body);
        body=world.createBody(bodyDef);
        body.createFixture(fixtureDef).setUserData(new FixtureData(FixtureData.Type.COIN, id));
        setActiveState();
    }
}
