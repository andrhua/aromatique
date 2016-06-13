package com.kekonyan.aromatique.game;

public class FixtureData {
    public enum Type {
        JOE, PLATFORM, PREQUARK, COIN, TURRET, LAND_SENSOR, SPIKE, BULLET,
        DEATH_INDICATOR, WOOMBA, CLOUD, BLADE, FLAG, TELEPORT, DISAPPEARING_PLATFORM, DRAGON,
        NONE, TRAFFIC_LIGHT, MUK, WATER, SHURIKEN, GRAVITY_ANOMALY, WOOMBA_SENSOR, INVISIBLE_BLADE,
        COLLECTIBLE, PANHANDLER, OCTOPUS, SMASH_PLATFORM, AMPHIBIAN, INDICATOR, TRANSFORMABLE_PLATFORM, FISH, DIALOG, FAKE_BLADE, WAITING_PLATFORM, SPIDER, TIMING_SPIKE, BIRD, BANANA, GHOST, GRAVITY_SWITCHER, PACMAN_GHOST, BLASTOISE, DISPOSABLE_FLASHING_PLATFORM
    }
    public Type type;
    public int id;
    public boolean isDynamic;

    public FixtureData (Type type, int id){
        this.type=type;
        this.id=id;
        switch (type){
            case COIN:
            case BLADE:
            case PLATFORM:
            case PREQUARK:
            case SPIKE:
            case DEATH_INDICATOR:
            case COLLECTIBLE: isDynamic=false; break;
            default: isDynamic=true;
        }
    }
}
