package com.kekonyan.aromatique.game.object;

import android.graphics.Canvas;
import com.kekonyan.aromatique.GameActivity;
import com.kekonyan.aromatique.audio.Sfx;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.game.FixtureData;
import com.kekonyan.aromatique.game.contactslistener.ContactsListener;
import com.kekonyan.aromatique.game.tiled.core.MapObject;
import com.kekonyan.aromatique.util.Const;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

public class Teleport extends DynamicGameObject implements IQueryable {
    private float angle;
    private float destinateX, destinateY;
    private boolean needToTeleport;
    public boolean rodnyan, scened;

    public Teleport(int id, MapObject mapObject, World world, int zOrder) {
        super(id, mapObject, zOrder);
        destinateX=Float.valueOf(mapObject.getProperties().getProperty("destinateX"));
        destinateY=Float.valueOf(mapObject.getProperties().getProperty("destinateY"));
        rodnyan=Boolean.valueOf(mapObject.getProperties().getProperty("rodnyan", "false"));
        polygonShape.setAsBox(1, 1);
        bodyDef.type= BodyType.STATIC;
        bodyDef.position.set(centerX, centerY);
        body=world.createBody(bodyDef);
        fixtureDef.shape=polygonShape;
        fixtureDef.isSensor=true;
        fixtureDef.filter.categoryBits= ContactsListener.CATEGORY_SCENERY;
        fixtureDef.filter.maskBits=ContactsListener.MASK_SCENERY;
        fixture=body.createFixture(fixtureDef);
        fixture.setUserData(new FixtureData(FixtureData.Type.TELEPORT, id));
        type= FixtureData.Type.TELEPORT;
        angle=0;
        needToTeleport=scened=false;
    }

    @Override
    public void render(Canvas canvas) {
        canvas.save();
        canvas.translate(centerX*Const.PPM, centerY*Const.PPM);
        canvas.rotate(angle);
        canvas.drawBitmap(Assets.Game.objects[Assets.Game.Object.TELEPORT.ordinal()], -Const.PPM, -Const.PPM, null);
        canvas.restore();
    }

    @Override
    public void update(float elapsedTime) {
        if (needToTeleport) {
            GameActivity.sfx.play(Sfx.TELEPORT);
            GameActivity.joe.body.setTransform(new Vec2(destinateX, destinateY), 0);
            needToTeleport=false;
        }
        angle+=elapsedTime*.03f;
        if (angle>=360) angle=0;
    }

    @Override
    public void queryForMagic() {
        if (destinateX!=-1&&destinateY!=-1) needToTeleport=true;
    }

    @Override
    public void reset() {
        scened=false;
    }
}
