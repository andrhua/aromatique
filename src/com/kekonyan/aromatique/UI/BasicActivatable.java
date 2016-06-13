package com.kekonyan.aromatique.UI;

abstract public class BasicActivatable {
    protected enum State {IDLE, ACTIVE}
    private State state;

    public void setActiveState(){
        state=State.ACTIVE;
    }

    public void setIdleState(){
        state=State.IDLE;
    }

    public boolean isActive(){
        return state==State.ACTIVE;
    }

}
