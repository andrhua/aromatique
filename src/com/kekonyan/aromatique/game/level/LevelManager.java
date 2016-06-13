package com.kekonyan.aromatique.game.level;

import android.os.AsyncTask;
import com.kekonyan.aromatique.GameActivity;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.game.object.*;
import com.kekonyan.aromatique.game.scripts.*;
import com.kekonyan.aromatique.game.tiled.core.Map;
import com.kekonyan.aromatique.game.tiled.core.MapLayer;
import com.kekonyan.aromatique.game.tiled.core.MapObject;
import com.kekonyan.aromatique.game.tiled.core.ObjectGroup;
import com.kekonyan.aromatique.game.tiled.io.TMXMapReader;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import java.util.ArrayList;
import java.util.Vector;

public class LevelManager extends AsyncTask <Void, Void, BasicLevel>{
    public enum Aroma{DOWN, TOP, STRANGE, CHARMED, BEAUTY, TRUTH}
    private Aroma aroma;
    private Assets.Game gameAssets;
    private int TRAFFIC_LIGHT_, DISAPPEARING_PLATFORM_, DRAGON_, CLOUD_,
            PRISON_TIMER_, MUK_, TRANSFORMABLE_, AMPHIBIAN_, PANHANDLER_, GRAVITY_ANOMALY_,
            KEY_, FISH_, DIALOG_, OCTOPUS_, BANANA_, GHOST_;
    private final static String
            JOE ="joe", PANHANDLER="panhandler",
            PLATFORM="platform", DISPOSABLE_FLASHING_PLATFORM="disposable_flashing_"+PLATFORM, DISAPPEARING_PLATFORM="disappearing_"+PLATFORM,
            TRANSFORMABLE_PLATFORM="transformable_"+PLATFORM, MOVING_PLATFORM="moving_"+PLATFORM, FAKE_PLATFORM="fake_"+PLATFORM,
            BEAUTEOUS_PLATFORM="beauteous_"+PLATFORM,
            SMASH_PLATFORM="smash_"+PLATFORM,
            INDICATOR="indicator", DEATH_INDICATOR="death_"+INDICATOR, GRAVITY_SWITCHER="gravity_switcher",
            SPIKE="spike", TIMING_SPIKE="timing_"+SPIKE,
            TURRET="turret", AMPHIBIAN="amphibian", MUK="muk", WOOMBA="woomba",DRAGON="dragon", CLOUD="cloud", SHURIKEN="shuriken",
            OCTOPUS="octopus", SPIDER="spider", BIRD="bird", GHOST="ghost", PACMAN_GHOST="pacman_"+GHOST, BLASTOISE="blastoise",
            BLADE="blade", FAKE_BLADE="fake_"+BLADE, INVISIBLE_BLADE="invisible_"+BLADE,
            COIN="coin", PREQUARK="prequark", COLLECTIBLE="collectible", BANANA="banana",
            WATER="water", TRAFFIC_LIGHT="traffic_light", GRAVITY_ANOMALY="gravity_anomaly", TELEPORT="teleport",  PRISON_TIMER="prison_timer",
            SPRITE="sprite", HINT="hint", BACKGROUND="background", OVERLAP="overlap", DIALOG="dialog",
            SEPARATOR="_";
    private Indicator cloudIndicator, amphibianIndicator, waterIndicator,
            octopusIndicator, spiderIndicator, birdIndicator, ghostIndicator,
            pacmanIndicator, backdoorIndicator;
    private Collectible fish, key;
    private Platform.Disappearing trafficDisappearing, octopusDisappearing;
    private Platform.Transformable leftPlatform, rightPlatform;



    public LevelManager(Aroma aroma){
        this.aroma = aroma;
        gameAssets=new Assets.Game();
        gameAssets.execute();
    }

    @Override
    protected void onPostExecute(BasicLevel level) {
        if (gameAssets.getStatus()==Status.FINISHED) super.onPostExecute(level);
    }

    @Override
    protected BasicLevel doInBackground(Void... voids) {
        try {
            GameActivity.sfx.loadGameSounds();
            ArrayList<DynamicGameObject> dynamics=new ArrayList<>();
            ArrayList<StaticGameObject>statics=new ArrayList<>();
            ArrayList<BasicScript>scripts=new ArrayList<>();
            Vec2 gravity=new Vec2(0, 15);
            World world = new World(gravity);
            world.setAutoClearForces(true);
            Map map=new TMXMapReader().readMap("level/"+aroma.ordinal()+".tmx");
            int mapHeight = map.getHeight();
            Vector<MapLayer> layers= map.getLayers();
            parseGameObjects(layers, statics, dynamics, world);
            initializeScripts(scripts, statics, dynamics);
            BasicLevel level=null;
            switch (aroma) {
                case DOWN:level = new DownLevel(world, aroma, statics, dynamics, scripts, mapHeight);break;
                case TOP:level = new TopLevel(world, aroma, statics, dynamics, scripts, mapHeight);break;
                case STRANGE:level = new StrangeLevel(world, aroma, statics, dynamics, scripts, mapHeight);break;
            }
            return level;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void parseGameObjects(Vector<MapLayer> layers, ArrayList<StaticGameObject> statics, ArrayList<DynamicGameObject> dynamics, World world) {
        int zOrder=0;
        for (MapLayer layer : layers) {
            ObjectGroup og = (ObjectGroup) layer;
            switch (og.getName()) {
                case JOE: {
                    MapObject mo = og.getObjects().get(0);
                    dynamics.add(new Joe(dynamics.size(), mo, world, zOrder));
                    GameActivity.joe = (Joe) dynamics.get(dynamics.size() - 1);
                }
                break;
                case PLATFORM:
                    for (MapObject mo : og.getObjects())
                        statics.add(new Platform(statics.size(), mo, world, zOrder));
                    break;
                case DISPOSABLE_FLASHING_PLATFORM:
                    for (MapObject mo : og.getObjects())
                        dynamics.add(new Platform.DisposableFlashing(dynamics.size(), mo, world, zOrder));
                    break;
                case SPIKE:
                    for (MapObject mo : og.getObjects())
                        statics.add(new Spike(statics.size(), mo, world, zOrder));
                    break;
                case TURRET:
                    for (MapObject mo : og.getObjects())
                        dynamics.add(new Turret(dynamics.size(), mo, world, zOrder));
                    break;
                case COIN:
                    for (MapObject mo : og.getObjects())
                        statics.add(new Coin(statics.size(), mo, world, zOrder));
                    break;
                case PREQUARK:
                    for (MapObject mo : og.getObjects())
                        statics.add(new Prequark(statics.size(), mo, world, zOrder));
                    break;
                case HINT:
                    for (MapObject mo : og.getObjects())
                        statics.add(new Hint(statics.size(), mo, zOrder));
                    break;
                case DEATH_INDICATOR:
                    for (MapObject mo : og.getObjects())
                        statics.add(new Indicator.Death(statics.size(), mo, world));
                    break;
                case WOOMBA:
                    for (MapObject mo : og.getObjects())
                        dynamics.add(new Woomba(dynamics.size(), mo, world, zOrder));
                    break;
                case MOVING_PLATFORM:
                    for (MapObject mo : og.getObjects())
                        dynamics.add(new Platform.Moving(dynamics.size(), mo, world, zOrder));
                    break;
                case BLADE:
                    for (MapObject mo : og.getObjects())
                        statics.add(new Blade(statics.size(), mo, world, zOrder));
                    break;
                case TELEPORT:
                    for (MapObject mo : og.getObjects())
                        dynamics.add(new Teleport(dynamics.size(), mo, world, zOrder));
                    break;
                case FAKE_PLATFORM:
                    for (MapObject mo : og.getObjects())
                        statics.add(new Platform.Fake(statics.size(), mo, zOrder));
                    break;
                case INVISIBLE_BLADE:
                    for (MapObject mo : og.getObjects())
                        statics.add(new Blade.Invisible(statics.size(), mo, world, zOrder));
                    break;
                case SHURIKEN:
                    for (MapObject mo : og.getObjects())
                        dynamics.add(new Platform.Shuriken(dynamics.size(), mo, world, zOrder));
                    break;
                case SPRITE:
                    for (MapObject mo : og.getObjects())
                        statics.add(new Sprite(statics.size(), mo, zOrder));
                    break;
                case WATER: {
                    MapObject mo = og.getObjects().get(0);
                    statics.add(new Water(statics.size(), mo, world, zOrder));
                    break;
                }
                case GRAVITY_ANOMALY:
                    GRAVITY_ANOMALY_ = dynamics.size();
                    for (MapObject mo : og.getObjects())
                        dynamics.add(new GravityAnomaly(dynamics.size(), mo, world));
                    break;
                case FAKE_BLADE:
                    for (MapObject mo : og.getObjects())
                        statics.add(new Blade.Fake(statics.size(), mo, world, zOrder));
                    break;
                case BACKGROUND:
                    for (MapObject mo : og.getObjects())
                        statics.add(new Background(statics.size(), mo, zOrder));
                    break;
                case OVERLAP:
                    for (MapObject mo : og.getObjects())
                        statics.add(new Background.Overlap(statics.size(), mo, zOrder));
                    break;
                case TIMING_SPIKE:
                    for (MapObject mo: og.getObjects())
                        dynamics.add(new Spike.Timing(dynamics.size(), mo, world, zOrder));
                    break;
                case SMASH_PLATFORM:
                    for (MapObject mo: og.getObjects())
                        dynamics.add(new Platform.Smash(dynamics.size(), mo, world, zOrder));
                    break;
                case SPIDER+SEPARATOR+INDICATOR: {
                    MapObject mo = og.getObjects().get(0);
                    spiderIndicator = new Indicator(statics.size(), mo, world);
                    statics.add(spiderIndicator);
                    break;
                }
                case SPIDER: {
                    MapObject mo = og.getObjects().get(0);
                    dynamics.add(new EarRapeSpider(dynamics.size(), mo, world, zOrder, spiderIndicator));
                    break;
                }
            }
                switch (aroma){
                    case DOWN:
                        switch (og.getName()){
                            case PANHANDLER:
                                PANHANDLER_ = statics.size();
                                for (MapObject mo : og.getObjects())
                                    statics.add(new Panhandler(statics.size(), mo, world, zOrder));
                                break;
                            case DIALOG:
                                DIALOG_ = statics.size();
                                for (MapObject mo : og.getObjects())
                                    statics.add(new Dialog(statics.size(), mo, zOrder));
                                break;
                            case TRANSFORMABLE_PLATFORM:
                                TRANSFORMABLE_ = statics.size();
                                for (MapObject mo : og.getObjects())
                                    statics.add(new Platform.Transformable(statics.size(), mo, world, zOrder));
                                break;
                            case DISAPPEARING_PLATFORM:
                                trafficDisappearing=new Platform.Disappearing(dynamics.size(), og.getObjects().get(0), world, zOrder);
                                dynamics.add(trafficDisappearing);
                                octopusDisappearing=new Platform.Disappearing(dynamics.size(), og.getObjects().get(1), world, zOrder);
                                dynamics.add(octopusDisappearing);
                                break;
                            case COLLECTIBLE:
                                fish=new Collectible(statics.size(), og.getObjects().get(0), world, zOrder);
                                statics.add(fish);
                                key=new Collectible(statics.size(), og.getObjects().get(1), world, zOrder);
                                statics.add(key);
                                break;
                            case AMPHIBIAN: {
                                MapObject mo = og.getObjects().get(0);
                                AMPHIBIAN_ = dynamics.size();
                                dynamics.add(new Amphibian(dynamics.size(), mo, world, zOrder, amphibianIndicator));
                                break;
                            }
                            case TRAFFIC_LIGHT: {
                                MapObject mo = og.getObjects().get(0);
                                TRAFFIC_LIGHT_ = dynamics.size();
                                dynamics.add(new TrafficLight(dynamics.size(), mo, zOrder));
                                break;
                            }
                            case CLOUD: {
                                MapObject mo = og.getObjects().get(0);
                                CLOUD_ = dynamics.size();
                                dynamics.add(new Cloud(dynamics.size(), mo, world, zOrder, cloudIndicator));
                                break;
                            }
                            case DRAGON: {
                                DRAGON_ = dynamics.size();
                                MapObject mo = og.getObjects().get(0);
                                dynamics.add(new Dragon(dynamics.size(), mo, world, zOrder));
                                break;
                            }
                            case CLOUD+SEPARATOR+INDICATOR: {
                                MapObject mo = og.getObjects().get(0);
                                cloudIndicator = new Indicator(statics.size(), mo, world);
                                statics.add(cloudIndicator);
                                break;
                            }
                            case AMPHIBIAN+SEPARATOR+INDICATOR: {
                                MapObject mo = og.getObjects().get(0);
                                amphibianIndicator = new Indicator(statics.size(), mo, world);
                                statics.add(amphibianIndicator);
                                break;
                            }
                            case WATER+SEPARATOR+INDICATOR:{
                                MapObject mo = og.getObjects().get(0);
                                waterIndicator = new Indicator(statics.size(), mo, world);
                                statics.add(waterIndicator);
                                break;
                            }
                            case BEAUTEOUS_PLATFORM:
                                for (MapObject mo: og.getObjects())
                                    statics.add(new Platform.Beauteous(statics.size(), mo, world, zOrder));
                                break;
                            case OCTOPUS+SEPARATOR+INDICATOR:{
                                MapObject mo = og.getObjects().get(0);
                                octopusIndicator = new Indicator(statics.size(), mo, world);
                                statics.add(octopusIndicator);
                                break;
                            }
                            case OCTOPUS:{
                                OCTOPUS_=dynamics.size();
                                MapObject mo = og.getObjects().get(0);
                                dynamics.add(new Octopus(dynamics.size(), mo, world, zOrder, octopusIndicator));
                                break;
                            }
                        } break;
                    case TOP: {
                        switch (og.getName()){
                            case PANHANDLER:
                                PANHANDLER_ = statics.size();
                                for (MapObject mo : og.getObjects())
                                    statics.add(new Panhandler(statics.size(), mo, world, zOrder));
                                break;
                            case DIALOG:
                                DIALOG_ = statics.size();
                                for (MapObject mo : og.getObjects())
                                    statics.add(new Dialog(statics.size(), mo, zOrder));
                                break;
                            case MUK: {
                                MUK_ = statics.size();
                                MapObject mo = og.getObjects().get(0);
                                statics.add(new Muk(statics.size(), mo, world, zOrder));
                                break;
                            }
                            case PRISON_TIMER: {
                                PRISON_TIMER_ = dynamics.size();
                                MapObject mo=og.getObjects().get(0);
                                dynamics.add(new PrisonTimer(dynamics.size(), mo, zOrder));
                                break;
                            }
                            case DISAPPEARING_PLATFORM:{
                                MapObject mo = og.getObjects().get(0);
                                DISAPPEARING_PLATFORM_=dynamics.size();
                                dynamics.add(new Platform.Disappearing(dynamics.size(), mo, world, zOrder));
                                break;
                            }
                            case TRANSFORMABLE_PLATFORM:{
                                leftPlatform=new Platform.Transformable(statics.size(), og.getObjects().get(0), world, zOrder);
                                statics.add(leftPlatform);
                                rightPlatform=new Platform.Transformable(statics.size(), og.getObjects().get(1), world, zOrder);
                                statics.add(rightPlatform);
                                break;
                            }
                            case BIRD+SEPARATOR+INDICATOR: {
                                MapObject mo = og.getObjects().get(0);
                                birdIndicator=new Indicator(statics.size(), mo, world);
                                statics.add(birdIndicator);
                                break;
                            }
                            case BIRD:{
                                MapObject mo = og.getObjects().get(0);
                                dynamics.add(new Bird(dynamics.size(), mo, world, zOrder, birdIndicator));
                                break;
                            }
                            case BANANA: {
                                BANANA_=dynamics.size();
                                MapObject mo = og.getObjects().get(0);
                                dynamics.add(new Banana(dynamics.size(), mo, world, zOrder));
                                break;
                            }
                            case GHOST+SEPARATOR+INDICATOR: {
                                MapObject mo = og.getObjects().get(0);
                                ghostIndicator=new Indicator(statics.size(), mo, world);
                                statics.add(ghostIndicator);
                                break;
                            }
                            case GHOST: {
                                GHOST_=dynamics.size();
                                MapObject mo = og.getObjects().get(0);
                                dynamics.add(new Ghost(dynamics.size(), mo, world, zOrder, ghostIndicator));
                                break;
                            }
                        }
                    }break;
                    case STRANGE:
                        switch (og.getName()){
                            case TRANSFORMABLE_PLATFORM:
                                TRANSFORMABLE_ = statics.size();
                                for (MapObject mo : og.getObjects())
                                    statics.add(new Platform.Transformable(statics.size(), mo, world, zOrder));
                                break;
                            case GRAVITY_SWITCHER:
                                for (MapObject mo : og.getObjects())
                                    statics.add(new GravitySwitcher(statics.size(), mo, world));
                                break;
                            case PACMAN_GHOST+SEPARATOR+INDICATOR: {
                                MapObject mo = og.getObjects().get(0);
                                pacmanIndicator = new Indicator(statics.size(), mo, world);
                                statics.add(pacmanIndicator);
                                break;
                            }
                            case PACMAN_GHOST: {
                                MapObject mo = og.getObjects().get(0);
                                dynamics.add(new PacmanGhost(dynamics.size(), mo, world, zOrder, pacmanIndicator));
                                break;
                            }
                            case BLASTOISE:
                                for (MapObject mo : og.getObjects())
                                    dynamics.add(new Blastoise(dynamics.size(), mo, world, zOrder));
                                break;
                            case "backdoor_"+INDICATOR:
                                MapObject mo = og.getObjects().get(0);
                                backdoorIndicator = new Indicator(statics.size(), mo, world);
                                statics.add(backdoorIndicator);
                                break;
                        }
                        break;
                    case CHARMED: break;
                    case BEAUTY: break;
                    case TRUTH: break;
            }
            zOrder++;
        }
        dynamics.trimToSize();
        statics.trimToSize();
    }

    private void initializeScripts(ArrayList<BasicScript> scripts, ArrayList<StaticGameObject> statics, ArrayList<DynamicGameObject> dynamics){
        switch (aroma){
            case DOWN:{
                scripts.add(new TrafficLightScript(
                        (TrafficLight)dynamics.get(TRAFFIC_LIGHT_),
                        trafficDisappearing,
                        (Dragon)dynamics.get(DRAGON_),
                        (Cloud)dynamics.get(CLOUD_)
                ));
                scripts.add(new WaterScript(
                        (Platform.Transformable) statics.get(TRANSFORMABLE_),
                        (Amphibian) dynamics.get(AMPHIBIAN_),
                        (Panhandler) statics.get(PANHANDLER_),
                        (GravityAnomaly) dynamics.get(GRAVITY_ANOMALY_),
                        fish,
                        waterIndicator,
                        (Dialog) statics.get(DIALOG_)

                ));
                scripts.add(new OctopusScript(
                        (Octopus)dynamics.get(OCTOPUS_),
                        key,
                        octopusDisappearing
                ));
            } break;
            case TOP:
                scripts.add(new PrisonScript(
                        (Platform.Disappearing)dynamics.get(DISAPPEARING_PLATFORM_),
                        (PrisonTimer)dynamics.get(PRISON_TIMER_),
                        (Muk)statics.get(MUK_)
                ));
                scripts.add(new BananaScript(
                        (Banana)dynamics.get(BANANA_),
                        leftPlatform,
                        rightPlatform,
                        (Ghost)dynamics.get(GHOST_),
                        (Panhandler)statics.get(PANHANDLER_),
                        (Dialog)statics.get(DIALOG_)
                ));
                break;
            case STRANGE:
                scripts.add(new BackdoorScript(
                        backdoorIndicator,
                        (Platform.Transformable)statics.get(TRANSFORMABLE_)
                ));
        }
    }


}
