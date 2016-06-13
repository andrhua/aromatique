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

public class EarRapeSpider extends DynamicGameObject {
    private Indicator indicator;
    private boolean isScreamed, needToKill;
    private enum State {WAITING, ONE, TWO}
    private State state;
    private Vec2 velocity, initial;

    public EarRapeSpider(int id, MapObject mapObject, World world, int zOrder, Indicator indicator) {
        super(id, mapObject, zOrder);
        this.indicator=indicator;
        velocity=new Vec2(0, 15);
        initial=new Vec2(centerX, centerY);
        state=State.WAITING;
        polygonShape.setAsBox(width/2, height/2);
        bodyDef.type= BodyType.DYNAMIC;
        bodyDef.gravityScale=0;
        bodyDef.position.set(centerX, centerY);
        body=world.createBody(bodyDef);
        fixtureDef.shape=polygonShape;
        fixtureDef.filter.categoryBits= ContactsListener.CATEGORY_FLYING_ENEMY;
        fixtureDef.filter.maskBits=ContactsListener.MASK_FLYING_ENEMY;
        fixtureDef.isSensor=true;
        body.createFixture(fixtureDef).setUserData(new FixtureData(FixtureData.Type.SPIDER, id));
        type= FixtureData.Type.SPIDER;
    }

    @Override
    public void render(Canvas canvas) {
        if (isScreamed) {
            canvas.save();
            canvas.translate((body.getPosition().x - width / 2) * Const.PPM, (body.getPosition().y - height / 2) * Const.PPM);
            canvas.drawBitmap(Assets.Game.objects[Assets.Game.Object.SPIDER.ordinal()], 0, 0, null);
            canvas.restore();
        }
    }

    @Override
    public void update(float elapsedTime) {
        if (indicator.isActive()&&!isScreamed) {
            GameActivity.sfx.play(Sfx.JOHN_CENA);
            isScreamed = true;
            needToKill = true;
        }
        if (needToKill)
            switch (state){
                case WAITING:
                    state=State.ONE;
                    body.setLinearVelocity(velocity);
                    break;
                case ONE:
                    if (body.getPosition().y>=centerY+38){
                        state=State.TWO;
                        body.setLinearVelocity(velocity.negate());
                    }
                    break;
                case TWO:
                    if (body.getPosition().y<=centerY){
                        needToKill=false;
                        body.setLinearVelocity(new Vec2());
                        body.setTransform(initial, 0);
                    }
            }
    }

    @Override
    public void reset() {
        state=State.WAITING;
        needToKill=false;
        isScreamed=false;
        body.setLinearVelocity(new Vec2());
        body.setTransform(initial, 0);
    }
}
