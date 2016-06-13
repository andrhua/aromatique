package com.kekonyan.aromatique.game.object;

import android.graphics.Canvas;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.game.FixtureData;
import com.kekonyan.aromatique.game.contactslistener.ContactsListener;
import com.kekonyan.aromatique.game.tiled.core.MapObject;
import com.kekonyan.aromatique.util.Const;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

public class Panhandler extends StaticGameObject implements IResetable {

    public enum State{SATISFIED, NOT_SATISFIED}
    public boolean isTouching;
    private State state;

    public void setState(State state){
        this.state=state;
    }

    public Panhandler(int id, MapObject mapObject, World world, int zOrder) {
        super(id, mapObject, zOrder);
        polygonShape.setAsBox(width/2, height/2);
        bodyDef.type= BodyType.STATIC;
        bodyDef.position.set(centerX, centerY);
        body=world.createBody(bodyDef);
        fixtureDef.shape=polygonShape;
        fixtureDef.filter.categoryBits= ContactsListener.CATEGORY_SCENERY;
        fixtureDef.filter.maskBits=ContactsListener.MASK_SCENERY;
        fixtureDef.isSensor=true;
        fixture=body.createFixture(fixtureDef);
        fixture.setUserData(new FixtureData(FixtureData.Type.PANHANDLER, id));
        type= FixtureData.Type.PANHANDLER;
        state=State.NOT_SATISFIED;
    }

    @Override
    public void render(Canvas canvas) {
        canvas.save();
        canvas.translate(left* Const.PPM, top*Const.PPM);
        canvas.drawBitmap(Assets.Game.objects[Assets.Game.Object.PANHANDLER.ordinal()], 0, 0, null);
        canvas.restore();
    }

    public State getState() {
        return state;
    }

    @Override
    public void reset() {
        state=State.NOT_SATISFIED;
        isTouching=false;
    }
}
