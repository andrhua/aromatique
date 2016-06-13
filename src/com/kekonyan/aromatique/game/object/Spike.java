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

public class Spike extends StaticGameObject {
    private Path path;
    private enum Direction {UP, DOWN, LEFT, RIGHT}
    private Direction direction;

    public Spike(int id, MapObject mapObject, World world, int zOrder) {
        super(id, mapObject, zOrder);
        direction=Direction.valueOf(mapObject.getProperties().getProperty("direction", "UP"));
        bodyDef.type= BodyType.STATIC;
        bodyDef.position.set(centerX, centerY);
        body=world.createBody(bodyDef);
        Vec2[] vertices = new Vec2[3];
        switch (direction){
            case UP:
                vertices[0]=new Vec2(0, -.5f);
                vertices[1]=new Vec2(.5f, .5f);
                vertices[2]=new Vec2(-.5f, .5f);
                break;
            case RIGHT:
                vertices[0]=new Vec2(-.5f, .5f);
                vertices[1]=new Vec2(-.5f, -.5f);
                vertices[2]=new Vec2(.5f, 0);
                break;
            case LEFT:
                vertices[0]=new Vec2(.5f, .5f);
                vertices[1]=new Vec2(.5f, -.5f);
                vertices[2]=new Vec2(-.5f, 0);
                break;
            case DOWN:
                vertices[0]=new Vec2(0, .5f);
                vertices[1]=new Vec2(.5f, -.5f);
                vertices[2]=new Vec2(-.5f, -.5f);
                break;
        }

        polygonShape.set(vertices, 3);
        fixtureDef.shape=polygonShape;
        fixtureDef.density=1;
        fixtureDef.filter.categoryBits= ContactsListener.CATEGORY_TRAP;
        fixtureDef.filter.maskBits= ContactsListener.MASK_SCENERY;
        fixture=body.createFixture(fixtureDef);
        fixture.setUserData(new FixtureData(FixtureData.Type.SPIKE, id));
        type= FixtureData.Type.SPIKE;
        path=new Path();
        path.reset();
        path.moveTo(vertices[2].x*Const.PPM, vertices[2].y*Const.PPM);
        path.lineTo(vertices[0].x*Const.PPM, vertices[0].y*Const.PPM);
        path.lineTo(vertices[1].x*Const.PPM, vertices[1].y*Const.PPM);
        path.lineTo(vertices[2].x*Const.PPM, vertices[2].y*Const.PPM);
        path.close();
    }

    @Override
    public void render(Canvas canvas) {
        canvas.save();
        canvas.translate((left+.5f)*Const.PPM, (top+.5f)*Const.PPM);
        canvas.drawPath(path, Assets.Game.spikePaint);
        canvas.drawPath(path, Assets.aromaPaint);
        canvas.restore();
    }

    public static class Timing extends DynamicGameObject{
        private Path path;
        private Direction direction;
        private int state;
        private float time, delay;
        private Vec2 destinate, initial;

        public Timing(int id, MapObject mapObject, World world, int zOrder) {
            super(id, mapObject, zOrder);
            direction=Direction.valueOf(mapObject.getProperties().getProperty("direction", "UP"));
            state=Integer.valueOf(mapObject.getProperties().getProperty("state", "1"));
            if (state==1) destinate=new Vec2(centerX, centerY); else initial=new Vec2(centerX, centerY);
            delay= MathUtils.randomFloat(1500, 1600);
            bodyDef.type= BodyType.KINEMATIC;
            bodyDef.position.set(centerX, centerY);
            body=world.createBody(bodyDef);
            Vec2[] vertices = new Vec2[3];
            switch (direction){
                case UP:
                    vertices[0]=new Vec2(0, -.5f);
                    vertices[1]=new Vec2(.5f, .5f);
                    vertices[2]=new Vec2(-.5f, .5f);
                    if (state==1) initial=new Vec2(centerX, centerY+1); else destinate=new Vec2(centerX, centerY-1);
                    break;
                case RIGHT:
                    vertices[0]=new Vec2(-.5f, .5f);
                    vertices[1]=new Vec2(-.5f, -.5f);
                    vertices[2]=new Vec2(.5f, 0);
                    break;
                case LEFT:
                    vertices[0]=new Vec2(.5f, .5f);
                    vertices[1]=new Vec2(.5f, -.5f);
                    vertices[2]=new Vec2(-.5f, 0);
                    break;
                case DOWN:
                    vertices[0]=new Vec2(0, .5f);
                    vertices[1]=new Vec2(.5f, -.5f);
                    vertices[2]=new Vec2(-.5f, -.5f);
                    if (state==1) initial=new Vec2(centerX, centerY-1); else destinate=new Vec2(centerX, centerY+1);
                    break;
            }
            polygonShape.set(vertices, 3);
            fixtureDef.shape=polygonShape;
            fixtureDef.density=1;
            fixtureDef.filter.categoryBits= ContactsListener.CATEGORY_TRAP;
            fixtureDef.filter.maskBits= ContactsListener.MASK_SCENERY;
            fixture=body.createFixture(fixtureDef);
            fixture.setUserData(new FixtureData(FixtureData.Type.TIMING_SPIKE, id));
            type= FixtureData.Type.TIMING_SPIKE;
            path=new Path();
            path.reset();
            path.moveTo(vertices[2].x*Const.PPM, vertices[2].y*Const.PPM);
            path.lineTo(vertices[0].x*Const.PPM, vertices[0].y*Const.PPM);
            path.lineTo(vertices[1].x*Const.PPM, vertices[1].y*Const.PPM);
            path.lineTo(vertices[2].x*Const.PPM, vertices[2].y*Const.PPM);
            path.close();
        }

        @Override
        public void render(Canvas canvas) {
            canvas.save();
            canvas.translate(body.getPosition().x*Const.PPM, body.getPosition().y*Const.PPM);
            canvas.drawPath(path, Assets.Game.spikePaint);
            canvas.drawPath(path, Assets.aromaPaint);
            canvas.restore();
        }

        @Override
        public void update(float elapsedTime) {
            time+=elapsedTime;
            if (time>=delay){
                body.setTransform(state==1?initial:destinate, 0);
                state=3-state;
                time=0;
            }
        }

        @Override
        public void reset() {
            time=0;
        }
    }
}
