package com.kekonyan.aromatique.game.scripts;

import com.kekonyan.aromatique.UI.BasicActivatable;
import com.kekonyan.aromatique.core.IUpdatable;
import com.kekonyan.aromatique.game.object.IResetable;

public abstract class BasicScript extends BasicActivatable implements IUpdatable, IResetable{

    public abstract void execute();
    protected abstract boolean checkCondition(float elapsedTime);
    public void start(){
        setActiveState();
    }

    @Override
    public void update(float elapsedTime) {
        if (isActive())
            if (checkCondition(elapsedTime)) execute();
    }
}
