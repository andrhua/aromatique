package com.kekonyan.aromatique.game.object;

import android.graphics.Canvas;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.game.FixtureData;
import com.kekonyan.aromatique.game.tiled.core.MapObject;
import com.kekonyan.aromatique.util.Const;
import org.jbox2d.dynamics.World;

public class Collectible extends Prequark {
    private Assets.Game.Object object;
    private int bmpWidth, bmpHeight;

    public Collectible(int id, MapObject mapObject, World world, int zOrder) {
        super(id, mapObject, world, zOrder);
        object = Assets.Game.Object.valueOf(mapObject.getProperties().getProperty("type"));
        bmpWidth=Assets.Game.objects[object.ordinal()].getWidth();
        bmpHeight=Assets.Game.objects[object.ordinal()].getHeight();
        fixture.setUserData(new FixtureData(FixtureData.Type.COLLECTIBLE, id));
        type= FixtureData.Type.COLLECTIBLE;
    }

    @Override
    public void render(Canvas canvas) {
        canvas.save();
        canvas.translate(left * Const.PPM, top * Const.PPM);
        canvas.drawBitmap(Assets.Game.objects[object.ordinal()], 0, 0, null);
        canvas.restore();
    }

    @Override
    public void reset() {
        body.setActive(false);
        world.destroyBody(body);
        body=world.createBody(bodyDef);
        body.createFixture(fixtureDef).setUserData(new FixtureData(FixtureData.Type.COLLECTIBLE, id));
        setActiveState();
    }

}
