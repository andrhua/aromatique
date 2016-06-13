package com.kekonyan.aromatique.game.scripts;

import com.kekonyan.aromatique.game.object.Collectible;
import com.kekonyan.aromatique.game.object.HauntingEnemy;
import com.kekonyan.aromatique.game.object.Octopus;
import com.kekonyan.aromatique.game.object.Platform;

public class OctopusScript extends BasicScript {
    private Octopus octopus;
    private Collectible key;
    private Platform.Disappearing door;

    public OctopusScript(Octopus octopus, Collectible key, Platform.Disappearing door){
        this.octopus=octopus;
        this.key=key;
        this.door=door;
        setActiveState();
    }
    @Override
    public void execute() {
        door.queryForMagic();
        octopus.behaviour= HauntingEnemy.Behaviour.HAUNTING;
        setIdleState();
    }

    @Override
    protected boolean checkCondition(float elapsedTime) {
        return !key.isActive();
    }

    @Override
    public void reset() {
        setActiveState();
        octopus.behaviour= HauntingEnemy.Behaviour.IDLE;
    }
}
