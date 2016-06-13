package com.kekonyan.aromatique.game.object;

import android.graphics.Canvas;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.game.FixtureData;
import com.kekonyan.aromatique.game.contactslistener.ContactsListener;
import com.kekonyan.aromatique.game.tiled.core.MapObject;
import com.kekonyan.aromatique.util.Const;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

public class Banana extends DynamicGameObject implements IQueryable{
    public int state;
    private Vec2[] states;
    private boolean needToTransform;

    public Banana(int id, MapObject mapObject, World world, int zOrder) {
        super(id, mapObject, zOrder);
        state=0;
        states=new Vec2[10];
        states[0]=new Vec2(524, 56);
        states[1]=new Vec2(520, 43);
        states[2]=new Vec2(511, 56);
        states[3]=new Vec2(515, 43);
        states[4]=new Vec2(517.5f, 48);
        states[5]=states[2];
        states[6]=states[3];
        states[7]=states[0];
        states[8]=new Vec2(518, 43);
        states[9]=new Vec2(518, 0);
        bodyDef.position.set(states[state]);
        body=world.createBody(bodyDef);
        polygonShape.setAsBox(.30f, .30f);
        fixtureDef.shape=polygonShape;
        fixtureDef.isSensor=true;
        fixtureDef.filter.categoryBits= ContactsListener.CATEGORY_SCENERY;
        fixtureDef.filter.maskBits=ContactsListener.MASK_SCENERY;
        body.createFixture(fixtureDef).setUserData(new FixtureData(FixtureData.Type.BANANA, id));
        type= FixtureData.Type.BANANA;

    }

    @Override
    public void render(Canvas canvas) {
        canvas.save();
        canvas.translate((body.getPosition().x-.3f)* Const.PPM, (body.getPosition().y-.3f)*Const.PPM);
        canvas.drawBitmap(Assets.Game.objects[Assets.Game.Object.BANANA.ordinal()], 0, 0, null);
        canvas.restore();
    }

    @Override
    public void reset() {
        state=0;
        needToTransform=false;
        body.setTransform(states[0], 0);
    }

    @Override
    public void queryForMagic() {
        needToTransform=true;
    }

    @Override
    public void update(float elapsedTime) {
        if (needToTransform) {
            body.setTransform(states[++state], 0);
            needToTransform=false;
        }
    }
}
