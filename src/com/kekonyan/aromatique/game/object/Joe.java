package com.kekonyan.aromatique.game.object;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import com.kekonyan.aromatique.GameActivity;
import com.kekonyan.aromatique.audio.Sfx;
import com.kekonyan.aromatique.game.FixtureData;
import com.kekonyan.aromatique.game.contactslistener.ContactsListener;
import com.kekonyan.aromatique.game.tiled.core.MapObject;
import com.kekonyan.aromatique.util.Const;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

import static com.kekonyan.aromatique.game.object.Joe.xState.*;


public class Joe extends DynamicGameObject {
    private Vec2 jumpImpulse, xVelocity;
    public boolean needToJump, jumpOverEnemy, inWater;
    private Bitmap bitmap;
    private float deltaHeight, deltaWidth;
    public enum xState {LEFT, RIGHT, STAY}
    private xState xState;
    private int scaleX, scaleY;
    public int footContacts, bodyContacts;

    public void setXState(xState xState){
        switch (xState){
            case LEFT: xVelocity.set(-7, 0); scaleX=-1; scaleY=1;break;
            case RIGHT: xVelocity.set(7, 0); scaleX=1; scaleY=1; break;
            case STAY: xVelocity.set(0, xVelocity.y);
                if (this.xState!=null) {
                    if (this.xState == LEFT) body.m_linearVelocity.x = -3f;
                    if (this.xState == RIGHT) body.m_linearVelocity.x = 3f;
                }
        }
        this.xState=xState;
    }

    public Joe(int id, MapObject mapObject, World world, int zOrder) {
        super(id, mapObject, zOrder);
        bodyDef.position.set(centerX, centerY);
        bodyDef.type=BodyType.DYNAMIC;
        body=world.createBody(bodyDef);
        body.setFixedRotation(true);
        polygonShape.setAsBox(.4f, 1.4f);
        fixtureDef.density=1;
        fixtureDef.filter.categoryBits= ContactsListener.CATEGORY_JOE;
        fixtureDef.filter.maskBits=ContactsListener.MASK_JOE;
        fixtureDef.shape=polygonShape;
        body.createFixture(fixtureDef).setUserData(new FixtureData(FixtureData.Type.JOE, this.id));
        polygonShape.setAsBox(.38f, .2f, new Vec2(0, 1.4f), 0);
        fixtureDef.isSensor=true;
        body.createFixture(fixtureDef).setUserData(new FixtureData(FixtureData.Type.LAND_SENSOR, id));
        needToJump= jumpOverEnemy =false;
        jumpImpulse=new Vec2(0, -37f);
        xVelocity=new Vec2(-7, 0);
        setXState(STAY);
        bitmap=GameActivity.playerData.joeModel.createIngameBitmap();
        deltaHeight=bitmap.getHeight()-2.8f*Const.PPM;
        deltaWidth=bitmap.getWidth()-.8f*Const.PPM;
        scaleX=scaleY=1;
        type= FixtureData.Type.JOE;
        inWater=false;
        footContacts=bodyContacts=0;
    }

    @Override
    public void render(Canvas canvas) {
        Vec2 position = body.getPosition();
        canvas.save();
        canvas.translate((position.x+(-scaleX*.4f))*Const.PPM+(-scaleX*deltaWidth), (position.y-1.4f)*Const.PPM-deltaHeight);
        canvas.scale(scaleX, scaleY);
        canvas.drawBitmap(bitmap, 0, 0,  null);
        canvas.restore();
    }

    @Override
    public void update(float elapsedTime) {
        body.setLinearDamping(inWater?2:1);
        if (jumpOverEnemy) {
            GameActivity.sfx.play(Sfx.JUMP_OVER_ENEMY);
            jumpImpulse.set(0, -body.getLinearVelocity().y-72);
            body.applyLinearImpulse(jumpImpulse, body.getPosition(), true);
            jumpOverEnemy=false;
        }
        if (needToJump&&!jumpOverEnemy) {
            if (footContacts ==1) {
                GameActivity.sfx.play(Sfx.JUMP);
                body.applyLinearImpulse(jumpImpulse, body.getPosition(), true);
            }
            needToJump =false;
        } else jumpOverEnemy =false;
        body.applyLinearImpulse(xVelocity, body.getPosition(), true);
        switch (xState){
            case LEFT: if (body.getLinearVelocity().x< xVelocity.x) body.setLinearVelocity(new Vec2(xVelocity.x, body.getLinearVelocity().y)); break;
            case RIGHT: if (body.getLinearVelocity().x>xVelocity.abs().x) body.setLinearVelocity(new Vec2(xVelocity.abs().x, body.getLinearVelocity().y));
        }

    }

    @Override
    public void reset() {
        body.setTransform(new Vec2(this.left, this.top), 0);
        xVelocity.setZero();
        body.setLinearVelocity(new Vec2(0, 0));
        setXState(STAY);
    }

    public Vec2 getPosition(){
        return body.getPosition();
    }


}
