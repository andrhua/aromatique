package com.kekonyan.aromatique.game.scripts;

import com.kekonyan.aromatique.GameActivity;
import com.kekonyan.aromatique.game.object.Cloud;
import com.kekonyan.aromatique.game.object.Dragon;
import com.kekonyan.aromatique.game.object.Platform;
import com.kekonyan.aromatique.game.object.TrafficLight;

public class TrafficLightScript extends BasicScript {
    private TrafficLight trafficLight;
    private Platform.Disappearing wallDisappearing;
    private Dragon dragon;
    private Cloud cloud;
    private boolean isPositiveResult;

    public TrafficLightScript(TrafficLight trafficLight, Platform.Disappearing wallDisappearing, Dragon dragon, Cloud cloud){
        this.trafficLight=trafficLight;
        this.wallDisappearing=wallDisappearing;
        this.dragon=dragon;
        this.cloud=cloud;
        setIdleState();
    }

    @Override
    public void execute() {
        if (isPositiveResult){
            wallDisappearing.queryForMagic();
            cloud.queryForMagic();
        } else dragon.queryForMagic();
    }

    @Override
    protected boolean checkCondition(float elapsedTime) {
        if (trafficLight.getState()== TrafficLight.State.GREEN && GameActivity.joe.footContacts==0){
            isPositiveResult=true;
            return true;
        } else {
            if (trafficLight.getState()!= TrafficLight.State.GREEN && GameActivity.joe.footContacts==0) {
                isPositiveResult = false;
                return true;
            }
        }
        return false;
    }

    @Override
    public void start() {
        super.start();
        trafficLight.queryForMagic();
    }

    @Override
    public void reset() {
        setIdleState();
    }
}
