package com.kekonyan.aromatique.game.scripts;

import com.kekonyan.aromatique.game.object.*;

public class BananaScript extends BasicScript {
    private Banana banana;
    private Platform.Transformable leftWall, rightWall;
    private Ghost ghost;
    private Panhandler panhandler;
    private Dialog dialog;
    private enum State {IDLE, ACTIVE, COLLECTED, FINISH}
    private State state;

    public BananaScript(Banana banana,
                        Platform.Transformable leftWall,
                        Platform.Transformable rightWall,
                        Ghost ghost,
                        Panhandler panhandler,
                        Dialog dialog
    ){
        this.banana=banana;
        this.leftWall=leftWall;
        this.rightWall=rightWall;
        this.ghost=ghost;
        this.panhandler=panhandler;
        this.dialog=dialog;
        setActiveState();
        state=State.IDLE;
    }

    @Override
    public void execute() {
        switch (state){
            case IDLE:
                leftWall.queryForMagic();
                state=State.ACTIVE;
                break;
            case ACTIVE:
                leftWall.reset();
                ghost.die();
                state=State.COLLECTED;
                break;
            case COLLECTED:
                panhandler.setState(Panhandler.State.SATISFIED);
                dialog.queryForMagic();
                rightWall.queryForMagic();
                setIdleState();
                break;
        }
    }

    @Override
    protected boolean checkCondition(float elapsedTime) {
        switch (state){
            case IDLE: if (ghost.behaviour==HauntingEnemy.Behaviour.HAUNTING) return true;  break;
            case ACTIVE: if (banana.state==9) return true; break;
            case COLLECTED: if (panhandler.isTouching) return true; break;
        }
        return false;
    }

    @Override
    public void reset() {
        state=State.IDLE;
        setActiveState();
    }

}
