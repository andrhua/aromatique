package com.kekonyan.aromatique.game.object;

import com.kekonyan.aromatique.core.IUpdatable;
import com.kekonyan.aromatique.game.tiled.core.MapObject;

abstract public class DynamicGameObject extends StaticGameObject implements IUpdatable, IResetable{
    public DynamicGameObject(int id, MapObject mapObject, int zOrder) {
        super(id, mapObject, zOrder);
    }
}
