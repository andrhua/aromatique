package com.kekonyan.aromatique.game.scripts;

import com.kekonyan.aromatique.game.object.Muk;
import com.kekonyan.aromatique.game.object.Platform;
import com.kekonyan.aromatique.game.object.PrisonTimer;

import java.util.concurrent.TimeUnit;

public class PrisonScript extends BasicScript {
    private Muk muk;
    private Platform.Disappearing wall;
    private PrisonTimer prisonTimer;
    private float time;

    public PrisonScript(Platform.Disappearing wall, PrisonTimer prisonTimer, Muk muk){
        this.wall=wall;
        this.prisonTimer=prisonTimer;
        this.muk=muk;
        setIdleState();
    }
    @Override
    public void execute() {
        if (System.currentTimeMillis()%2==0) wall.queryForMagic(); else muk.queryForMagic();
        reset();
    }

    @Override
    public void start() {
        super.start();
        prisonTimer.queryForMagic();
    }

    @Override
    protected boolean checkCondition(float elapsedTime) {
        time+=elapsedTime;
        return time>=TimeUnit.MINUTES.toMillis(2);
    }

    @Override
    public void reset() {
        time=0;
        setIdleState();
    }
}
