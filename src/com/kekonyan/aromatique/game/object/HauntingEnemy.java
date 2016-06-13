package com.kekonyan.aromatique.game.object;

import com.kekonyan.aromatique.game.tiled.core.MapObject;
import org.jbox2d.dynamics.World;

public abstract class HauntingEnemy extends DynamicGameObject implements IQueryable, IResetable{

    public enum Behaviour {IDLE, HAUNTING}
    public Behaviour behaviour;
    protected World world;
    protected Indicator indicator;
    int scaleX;

    HauntingEnemy(int id, MapObject mapObject, World world, int zOrder, Indicator indicator) {
        super(id, mapObject, zOrder);
        this.world=world;
        this.indicator=indicator;
        behaviour=Behaviour.IDLE;
        scaleX=1;
    }

    @Override
    public void queryForMagic() {
    }

    @Override
    public void reset() {
        body.setActive(false);
        world.destroyBody(body);
        body=world.createBody(bodyDef);
        behaviour=Behaviour.IDLE;
        fixture=body.createFixture(fixtureDef);
        setActiveState();
    }

    @Override
    public void update(float elapsedTime) {
        behaviour=indicator.isActive()?Behaviour.HAUNTING:Behaviour.IDLE;
    }
}
