package com.kekonyan.aromatique.game.object;

import android.graphics.Canvas;
import android.graphics.Path;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.game.FixtureData;
import com.kekonyan.aromatique.game.contactslistener.ContactsListener;
import com.kekonyan.aromatique.game.tiled.core.MapObject;
import com.kekonyan.aromatique.util.Const;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

public class Platform extends StaticGameObject {
    private boolean isTriangle;
    private Path path;

    public Platform(int id, MapObject mapObject, World world, int zOrder){
        super(id, mapObject, zOrder);
        isTriangle=Boolean.parseBoolean(mapObject.getProperties().getProperty("isTriangle", "false"));
        String direction=mapObject.getProperties().getProperty("direction", "right");
        if (isTriangle) {
            Vec2[] vertices = new Vec2[3];
            int coeff;
            coeff=direction.equals("right")?1:-1;
            vertices[0]=new Vec2(coeff*width/2, -3*height/2);
            vertices[1]=new Vec2(coeff*width/2, -height/2);
            vertices[2]=new Vec2(-coeff*width/2, -height/2);
            polygonShape.set(vertices, 3);
            path=new Path();
            path.reset();
            path.moveTo(vertices[2].x*Const.PPM, vertices[2].y*Const.PPM);
            path.lineTo(vertices[0].x*Const.PPM, vertices[0].y*Const.PPM);
            path.lineTo(vertices[1].x*Const.PPM, vertices[1].y*Const.PPM);
            path.lineTo(vertices[2].x*Const.PPM, vertices[2].y*Const.PPM);
            path.close();
            fixtureDef.friction=1;
        } else polygonShape.setAsBox(width/2, height/2);
        bodyDef.type=BodyType.STATIC;
        bodyDef.position.set(centerX, centerY);
        body=world.createBody(bodyDef);
        fixtureDef.shape=polygonShape;
        fixtureDef.filter.categoryBits= ContactsListener.CATEGORY_SCENERY;
        fixtureDef.filter.maskBits=ContactsListener.MASK_SCENERY;
        fixtureDef.friction=0;
        fixtureDef.density=1;
        fixture=body.createFixture(fixtureDef);
        fixture.setUserData(new FixtureData(FixtureData.Type.PLATFORM, id));
        type= FixtureData.Type.PLATFORM;
    }

    @Override
    public void render(Canvas canvas) {
        canvas.save();
        if (isTriangle) {
            canvas.translate(centerX*Const.PPM, centerY*Const.PPM);
            canvas.drawPath(path, Assets.Game.wallPaint);
            canvas.drawPath(path, Assets.aromaPaint);
        } else {
            canvas.translate(left * Const.PPM, top * Const.PPM);
            canvas.drawRect(0, 0, Const.PPM * width, Const.PPM * height, Assets.Game.wallPaint);
            canvas.drawRect(0, 0, Const.PPM* width, Const.PPM*height, Assets.aromaPaint);
        }
        canvas.restore();
    }

    public static class Moving extends DynamicGameObject {
        private enum Direction {VERTICAL, HORIZONTAL}
        private Direction direction;
        private float up, down, left, right;
        private Vec2 velocity;
        private boolean isGoingDown, isGoingRight;

        public Moving(int id, MapObject mapObject, World world, int zOrder) {
            super(id, mapObject, zOrder);
            direction=Direction.valueOf(mapObject.getProperties().getProperty("direction"));
            if (direction==Direction.VERTICAL) {
                up= Float.valueOf(mapObject.getProperties().getProperty("up"));
                down= Float.valueOf(mapObject.getProperties().getProperty("down"));
                velocity=new Vec2(0, Float.valueOf(mapObject.getProperties().getProperty("velocity")));
                isGoingDown=true;
            } else {
                right= Float.valueOf(mapObject.getProperties().getProperty("right"));
                left= Float.valueOf(mapObject.getProperties().getProperty("left"));
                velocity=new Vec2(Float.valueOf(mapObject.getProperties().getProperty("velocity")), 0);
                isGoingRight=true;
            }
            velocity.set(velocity.x*MathUtils.cos(angle*MathUtils.DEG2RAD)-velocity.y*MathUtils.sin(angle*MathUtils.DEG2RAD),
                    velocity.x*MathUtils.sin(angle*MathUtils.DEG2RAD)+velocity.y*MathUtils.cos(angle*MathUtils.DEG2RAD));
            polygonShape.setAsBox(width/2, height/2);
            bodyDef.type=BodyType.KINEMATIC;
            bodyDef.angle=angle*MathUtils.DEG2RAD;
            bodyDef.fixedRotation=true;
            bodyDef.position.set(centerX, centerY);
            body=world.createBody(bodyDef);
            fixtureDef.shape=polygonShape;
            fixtureDef.friction=0;
            fixtureDef.filter.categoryBits= ContactsListener.CATEGORY_SCENERY;
            fixtureDef.filter.maskBits=ContactsListener.MASK_SCENERY;
            fixture=body.createFixture(fixtureDef);
            fixture.setUserData(new FixtureData(FixtureData.Type.PLATFORM, id));
            type= FixtureData.Type.PLATFORM;

        }

        @Override
        public void render(Canvas canvas) {
            canvas.save();
            canvas.translate((body.getPosition().x-width/2) * Const.PPM, (body.getPosition().y-height/2)* Const.PPM);
            canvas.rotate(angle, width/2*Const.PPM, height/2*Const.PPM);
            canvas.drawRect(0, 0, Const.PPM*width, Const.PPM*height, Assets.Game.wallPaint);
            canvas.drawRect(0, 0, Const.PPM*width, Const.PPM*height, Assets.aromaPaint);
            canvas.restore();
        }

        @Override
        public void update(float elapsedTime) {
            if (direction==Direction.VERTICAL) {
                if (body.getPosition().y >= this.centerY + down) isGoingDown = false; else
                if (body.getPosition().y <= this.centerY - up) isGoingDown = true;
            } else  {
                if (body.getPosition().x >= this.centerX + right) isGoingRight = false; else
                if (body.getPosition().x <= this.centerX - left) isGoingRight = true;
            }
            if (isGoingDown||isGoingRight) body.setLinearVelocity(velocity.negate()); else
                body.setLinearVelocity(velocity);
        }

        @Override
        public void reset() {
            body.setTransform(new Vec2(centerX, centerY), 0);
        }

    }

    public static class DisposableFlashing extends DynamicGameObject implements IQueryable{
        private float time=0, timeToDestroy;
        private boolean needToStartDestroy;
        public enum State {APPEAR, DISAPPEAR}
        public State state;
        private World world;

        public DisposableFlashing(int id, MapObject mapObject, World world, int zOrder) {
            super(id, mapObject, zOrder);
            this.world=world;
            bodyDef.type= BodyType.STATIC;
            bodyDef.position.set(centerX, centerY);
            body=world.createBody(bodyDef);
            polygonShape.setAsBox(width/2, height/2);
            fixtureDef.shape=polygonShape;
            fixtureDef.filter.maskBits=ContactsListener.MASK_SCENERY;
            fixtureDef.filter.categoryBits=ContactsListener.CATEGORY_SCENERY;
            fixtureDef.density=1;
            fixtureDef.friction=0;
            fixture=body.createFixture(fixtureDef);
            fixture.setUserData(new FixtureData(FixtureData.Type.DISPOSABLE_FLASHING_PLATFORM, id));
            type= FixtureData.Type.DISPOSABLE_FLASHING_PLATFORM;
            state= State.APPEAR;
            needToStartDestroy=false;
        }

        @Override
        public void render(Canvas canvas) {
            canvas.save();
            canvas.translate(left* Const.PPM, top*Const.PPM);
            if (state== State.APPEAR) canvas.drawRect(0, 0, width*Const.PPM, height*Const.PPM, Assets.Game.DFWPaint);
            canvas.restore();
        }

        @Override
        public void update(float elapsedTime) {
            if (needToStartDestroy) {
                timeToDestroy += elapsedTime;
                if (timeToDestroy>2600) setIdleState();
            }

            float appearingTime = 1000;
            float disappearingTime = 2000;
            time+=elapsedTime;
            switch (state){
                case APPEAR: if (time> appearingTime) {
                    time=0;
                    state= State.DISAPPEAR;
                } break;
                case DISAPPEAR: if (time> disappearingTime) {
                    time=0;
                    state= State.APPEAR;
                }
            }
        }

        @Override
        public void setIdleState() {
            super.setIdleState();
            body.setActive(false);
            world.destroyBody(body);
        }

        @Override
        public void queryForMagic() {
            needToStartDestroy=true;
        }

        @Override
        public void reset() {
            body.setActive(false);
            world.destroyBody(body);
            body=world.createBody(bodyDef);
            fixture=body.createFixture(fixtureDef);
            fixture.setUserData(new FixtureData(FixtureData.Type.DISPOSABLE_FLASHING_PLATFORM, id));
            setActiveState();
            needToStartDestroy=false;
            time=0;
            timeToDestroy=0;
        }

    }

    public static class Disappearing extends DynamicGameObject implements IQueryable {
        private boolean needToDestroy;
        private World world;

        public Disappearing(int id, MapObject mapObject, World world, int zOrder) {
            super(id, mapObject, zOrder);
            this.world=world;
            polygonShape.setAsBox(width/2, height/2);
            bodyDef.type=BodyType.STATIC;
            bodyDef.position.set(centerX, centerY);
            body=world.createBody(bodyDef);
            fixtureDef.shape=polygonShape;
            fixtureDef.filter.categoryBits= ContactsListener.CATEGORY_SCENERY;
            fixtureDef.filter.maskBits=ContactsListener.MASK_SCENERY;
            fixtureDef.density=1;
            fixtureDef.friction=0;
            fixture=body.createFixture(fixtureDef);
            fixture.setUserData(new FixtureData(FixtureData.Type.DISAPPEARING_PLATFORM, id));
            type= FixtureData.Type.DISAPPEARING_PLATFORM;
            needToDestroy =false;
        }

        @Override
        public void update(float elapsedTime) {
            if (needToDestroy) {
                setIdleState();
                world.destroyBody(body);
                needToDestroy=false;
            }
        }

        @Override
        public void render(Canvas canvas) {
            canvas.save();
            canvas.translate(left * Const.PPM, top * Const.PPM);
            canvas.drawRect(0, 0, Const.PPM * width, Const.PPM * height, Assets.Game.wallPaint);
            canvas.drawRect(0, 0, Const.PPM* width, Const.PPM*height, Assets.aromaPaint);
            canvas.restore();
        }

        @Override
        public void queryForMagic() {
            needToDestroy =true;
        }

        @Override
        public void reset() {
            body.setActive(false);
            world.destroyBody(body);
            body=world.createBody(bodyDef);
            fixture=body.createFixture(fixtureDef);
            fixture.setUserData(new FixtureData(FixtureData.Type.DISAPPEARING_PLATFORM, id));
            setActiveState();
        }
    }

    public static class Fake extends StaticGameObject{

        public Fake(int id, MapObject mapObject, int zOrder) {
            super(id, mapObject, zOrder );
        }

        @Override
        public void render(Canvas canvas) {
            canvas.save();
            canvas.translate(left * Const.PPM, top * Const.PPM);
            canvas.drawRect(0, 0, Const.PPM * width, Const.PPM * height, Assets.Game.wallPaint);
            canvas.drawRect(0, 0, Const.PPM* width, Const.PPM*height, Assets.aromaPaint);
            canvas.restore();
        }
    }

    public static class Shuriken extends Moving {

        public Shuriken(int id, MapObject mapObject, World world, int zOrder) {
            super(id, mapObject, world, zOrder);
            fixture.setUserData(new FixtureData(FixtureData.Type.SHURIKEN, id));
            type = FixtureData.Type.SHURIKEN;
        }

        @Override
        public void render(Canvas canvas) {
            canvas.save();
            canvas.translate((body.getPosition().x-width/2) * Const.PPM, (body.getPosition().y-height/2)* Const.PPM);
            canvas.drawBitmap(Assets.Game.objects[Assets.Game.Object.SHURIKEN.ordinal()], 0, 0, null);
            canvas.restore();
        }
    }

    public static class Transformable extends Platform implements IQueryable, IResetable{
        private float transformY;

        public Transformable(int id, MapObject mapObject, World world, int zOrder) {
            super(id, mapObject, world, zOrder);
            transformY=Float.valueOf(mapObject.getProperties().getProperty("transformY", "0"));
            fixture.setFriction(0);
            fixture.setUserData(new FixtureData(FixtureData.Type.TRANSFORMABLE_PLATFORM, id));
            type= FixtureData.Type.TRANSFORMABLE_PLATFORM;
        }

        @Override
        public void render(Canvas canvas) {
            canvas.save();
            canvas.translate((body.getPosition().x-width/2)*Const.PPM, (body.getPosition().y-height/2)*Const.PPM);
            canvas.drawRect(0, 0, Const.PPM * width, Const.PPM * height, Assets.Game.wallPaint);
            canvas.drawRect(0, 0, Const.PPM* width, Const.PPM*height, Assets.aromaPaint);
            canvas.restore();

        }

        @Override
        public void queryForMagic() {
            body.setTransform(new Vec2(centerX, centerY+transformY), 0);
        }

        @Override
        public void reset() {
            body.setTransform(new Vec2(centerX, centerY), 0);
        }
    }

    public static class Smash extends DynamicGameObject {
        private float time, delay;
        private Vec2 velocity, initial;
        private boolean discharged;

        public Smash(int id, MapObject mapObject, World world, int zOrder) {
            super(id, mapObject, zOrder);
            delay=MathUtils.randomFloat(600, 1900);
            polygonShape.setAsBox(width/2, height/2);
            bodyDef.type=BodyType.DYNAMIC;
            bodyDef.position.set(centerX, centerY);
            body=world.createBody(bodyDef);
            body.setGravityScale(0);
            fixtureDef.shape=polygonShape;
            fixtureDef.filter.categoryBits= ContactsListener.CATEGORY_ENEMY;
            fixtureDef.filter.maskBits=ContactsListener.MASK_ENEMY;
            fixture=body.createFixture(fixtureDef);
            fixture.setUserData(new FixtureData(FixtureData.Type.SMASH_PLATFORM, id));
            type= FixtureData.Type.SMASH_PLATFORM;
            velocity=new Vec2(0, 50);
            initial=new Vec2(centerX, centerY);
        }

        @Override
        public void render(Canvas canvas) {
            canvas.save();
            canvas.translate((body.getPosition().x-width/2) * Const.PPM, (body.getPosition().y-height/2) * Const.PPM);
            canvas.drawRect(0, 0, Const.PPM * width, Const.PPM * height, Assets.Game.wallPaint);
            canvas.drawRect(0, 0, Const.PPM* width, Const.PPM*height, Assets.aromaPaint);
            canvas.restore();
        }

        @Override
        public void update(float elapsedTime) {
            time+=elapsedTime;
            if (time>=delay){
                if (!discharged) {
                    body.applyLinearImpulse(velocity, body.getPosition(), true);
                    discharged=true;
                } else
                if (time>=delay+1500) reset();
            }
        }

        @Override
        public void reset() {
            time=0;
            discharged=false;
            body.setLinearVelocity(new Vec2());
            body.setTransform(initial, 0);
        }
    }
    public static class Beauteous extends Platform{
        private Assets.Game.Object object;

        public Beauteous(int id, MapObject mapObject, World world, int zOrder) {
            super(id, mapObject, world, zOrder);
            object= Assets.Game.Object.valueOf(mapObject.getProperties().getProperty("type"));
        }

        @Override
        public void render(Canvas canvas) {
            canvas.save();
            canvas.translate(left*Const.PPM, top*Const.PPM);
            canvas.drawBitmap(Assets.Game.objects[object.ordinal()], 0, 0, null);
            canvas.restore();
        }
    }


}
