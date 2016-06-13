package com.kekonyan.aromatique.game.scripts;

import com.kekonyan.aromatique.game.object.*;

public class WaterScript extends BasicScript {
    private Collectible fish;
    private Platform.Transformable platform;
    private Amphibian amphibian;
    private Panhandler panhandler;
    private GravityAnomaly anomaly;
    private Indicator indicator;
    private Dialog dialog;

    private enum State {IDLE, CLOSED_EXIT, COLLECTED_FISH, SATISFIED_PANHANDLER}
    private State state;

    public WaterScript (Platform.Transformable platform,
                        Amphibian amphibian,
                        Panhandler panhandler,
                        GravityAnomaly anomaly,
                        Collectible fish,
                        Indicator indicator,
                        Dialog dialog){
        this.platform=platform;
        this.amphibian=amphibian;
        this.panhandler=panhandler;
        this.anomaly=anomaly;
        this.fish=fish;
        this.indicator=indicator;
        this.dialog=dialog;
        state=State.IDLE;
        setActiveState();
    }

    @Override
    public void execute() {
        switch (state){
            case IDLE:
                platform.queryForMagic();
                state=State.CLOSED_EXIT;
                break;
            case CLOSED_EXIT:
                platform.reset();
                state=State.COLLECTED_FISH;
                break;
            case COLLECTED_FISH:
                dialog.queryForMagic();
                anomaly.setActiveState();
                amphibian.die();
                state=State.SATISFIED_PANHANDLER;
                setIdleState();
                break;
        }
    }

    @Override
    protected boolean checkCondition(float elapsedTime) {
        switch (state){
            case IDLE: if (indicator.isActive()) return true; break;
            case CLOSED_EXIT: if (!fish.isActive()) return true; break;
            case COLLECTED_FISH:
                if (panhandler.isTouching) panhandler.setState(Panhandler.State.SATISFIED);
                if (panhandler.getState()== Panhandler.State.SATISFIED) return true; break;
        }
        return false;
    }

    @Override
    public void reset() {
        setActiveState();
        state=State.IDLE;
    }
}
