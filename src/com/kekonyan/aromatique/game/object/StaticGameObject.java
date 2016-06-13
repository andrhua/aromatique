package com.kekonyan.aromatique.game.object;

import com.kekonyan.aromatique.UI.BasicActivatable;
import com.kekonyan.aromatique.core.IRenderable;
import com.kekonyan.aromatique.game.FixtureData;
import com.kekonyan.aromatique.game.tiled.core.MapObject;
import com.kekonyan.aromatique.game.tiled.core.Rectangle;
import com.kekonyan.aromatique.util.Const;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

abstract public class StaticGameObject extends BasicActivatable implements IRenderable {
    public Body body;
    PolygonShape polygonShape;
    BodyDef bodyDef;
    FixtureDef fixtureDef;
    Fixture fixture;
    public MapObject mapObject;
    public int id, zOrder;
    public float left, top, centerX, centerY, width, height, angle;
    public FixtureData.Type type;


    public StaticGameObject(int id, MapObject mapObject, int zOrder) {
        this.zOrder = zOrder;
        this.id=id;
        this.mapObject=mapObject;
        left=mapObject.getX()/Const.TPX;
        top=mapObject.getY()/Const.TPX;
        Rectangle rectangle=mapObject.getBounds();
        width=rectangle.width/Const.TPX;
        height=rectangle.height/Const.TPX;
        centerX=left+(rectangle.width/2)/Const.TPX;
        centerY=top+(rectangle.height/2)/Const.TPX;
        angle=mapObject.getAngle();
        bodyDef=new BodyDef();
        polygonShape=new PolygonShape();
        fixtureDef=new FixtureDef();
        setActiveState();
        type=FixtureData.Type.NONE;
    }

    public static class Comparator implements java.util.Comparator<StaticGameObject>{

        @Override
        public int compare(StaticGameObject o1, StaticGameObject o2) {
            return o1.zOrder -o2.zOrder;
        }
    }

}
