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

public class Dragon extends DynamicGameObject implements IQueryable, IResetable{
    private boolean needToKill;
    private World world;

    public Dragon(int id, MapObject mapObject, World world, int zOrder) {
        super(id, mapObject, zOrder);
        this.world=world;
        polygonShape.setAsBox(width/2, height/2);
        bodyDef.type= BodyType.DYNAMIC;
        bodyDef.gravityScale=0;
        bodyDef.position.set(centerX, centerY);
        body=world.createBody(bodyDef);
        fixtureDef.shape=polygonShape;
        fixtureDef.filter.categoryBits= ContactsListener.CATEGORY_FLYING_ENEMY;
        fixtureDef.filter.maskBits=ContactsListener.MASK_FLYING_ENEMY;
        fixtureDef.isSensor=true;
        fixture=body.createFixture(fixtureDef);
        fixture.setUserData(new FixtureData(FixtureData.Type.DRAGON, id));
        type=FixtureData.Type.DRAGON;
        needToKill=false;
    }

    @Override
    public void render(Canvas canvas) {
        canvas.save();
        canvas.translate((body.getPosition().x-width)* Const.PPM, (body.getPosition().y-height)*Const.PPM);
        canvas.drawBitmap(Assets.Game.objects[Assets.Game.Object.DRAGON.ordinal()], 0, 0, null);
        canvas.restore();
    }

    @Override
    public void update(float elapsedTime) {
        if (needToKill) {
            body.setTransform(new Vec2(300, 7), 0);
            needToKill=false;
        }
    }

    @Override
    public void queryForMagic() {
        needToKill=true;
    }

    @Override
    public void reset() {
        body.setActive(false);
        world.destroyBody(body);
        body=world.createBody(bodyDef);
        fixture=body.createFixture(fixtureDef);
        fixture.setUserData(new FixtureData(FixtureData.Type.DRAGON, id));
    }
}
