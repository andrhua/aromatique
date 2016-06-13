package com.kekonyan.aromatique.game.object;

import android.graphics.Canvas;
import android.graphics.Paint;
import com.kekonyan.aromatique.game.FixtureData;
import com.kekonyan.aromatique.game.contactslistener.ContactsListener;
import com.kekonyan.aromatique.game.tiled.core.MapObject;
import com.kekonyan.aromatique.util.Colour;
import com.kekonyan.aromatique.util.Const;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

public class Water extends StaticGameObject {
    private Paint paint;

    public Water(int id, MapObject mapObject, World world, int zOrder) {
        super(id, mapObject, zOrder);
        bodyDef.position.set(centerX, centerY);
        bodyDef.type = BodyType.STATIC;
        body = world.createBody(bodyDef);
        body.setFixedRotation(true);
        polygonShape.setAsBox(width / 2, height / 2);
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = ContactsListener.CATEGORY_SCENERY;
        fixtureDef.filter.maskBits = ContactsListener.MASK_SCENERY;
        fixtureDef.shape = polygonShape;
        body.createFixture(fixtureDef).setUserData(new FixtureData(FixtureData.Type.WATER, id));
        paint = new Paint();
        paint.setColor(Colour.TWIITER);
        paint.setAlpha(128);
        type = FixtureData.Type.WATER;
    }

    @Override
    public void render(Canvas canvas) {
        canvas.save();
        canvas.translate(left * Const.PPM, top * Const.PPM);
        canvas.drawRect(0, 0, width * Const.PPM, height * Const.PPM, paint);
        canvas.restore();
    }
}
