package com.kekonyan.aromatique.game.level;

import android.graphics.Canvas;
import com.kekonyan.aromatique.GameActivity;
import com.kekonyan.aromatique.R;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.core.IRenderable;
import com.kekonyan.aromatique.core.IUpdatable;
import com.kekonyan.aromatique.game.FixtureData;
import com.kekonyan.aromatique.game.object.*;
import com.kekonyan.aromatique.game.scripts.BasicScript;
import com.kekonyan.aromatique.util.Const;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;

import java.util.ArrayList;
import java.util.Collections;

public abstract class BasicLevel implements IRenderable, IUpdatable {
    public LevelManager.Aroma aroma;
    private ArrayList<StaticGameObject> statics;
    protected ArrayList<DynamicGameObject> dynamics;
    protected ArrayList<StaticGameObject> objects;
    public short collectedPrequarks;
    protected World world;
    private int  heightInCells;
    public enum Result {WIN, LOSE, NEITHER}
    public Result result;
    private ArrayList<Query> queueForRemove;
    private ArrayList<BasicScript> scripts;
    public int scene;

    public BasicLevel(World world,
                      LevelManager.Aroma aroma,
                      ArrayList<StaticGameObject> statics,
                      ArrayList<DynamicGameObject> dynamics,
                      ArrayList<BasicScript> scripts,
                      int heightInCells){
        this.world=world;
        this.aroma=aroma;
        this.statics=statics;
        this.dynamics=dynamics;
        this.scripts=scripts;
        this.heightInCells =heightInCells;
        collectedPrequarks=0;
        result=Result.NEITHER;
        queueForRemove =new ArrayList<>();
        objects=new ArrayList<>(statics.size()+dynamics.size());
        objects.addAll(statics);
        objects.addAll(dynamics);
        Collections.sort(objects, new StaticGameObject.Comparator());
        this.scripts=scripts;
        Assets.Game.wallPaint.setColor(GameActivity.resources.getIntArray(R.array.aromaColors)[aroma.ordinal()]);
        Assets.Game.bgPaint.setColor(GameActivity.resources.getIntArray(R.array.bgColors)[aroma.ordinal()]);
        scene=1;
    }

    @Override
    public void update(float elapsedTime) {
        world.step(1/60f, 8, 3);
        removeObjects();
    }

    @Override
    public void render(Canvas canvas) {
        canvas.drawBitmap(Assets.Game.gameBGs[aroma.ordinal()], 0, 0, null);
        canvas.save();
        Vec2 position=GameActivity.joe.getPosition();
        canvas.translate(-(position.x)* Const.PPM, -(position.y)*Const.PPM);
        canvas.translate(Const.WIDTH/2, Const.HEIGHT/2);
    }

    void executeScripts(float elapsedTime){
        for (BasicScript bs:scripts) bs.update(elapsedTime);
    }

    public void queryForRemove(Fixture f){
        queueForRemove.add(new Query(f));
    }

    private void removeObjects(){
        if (!queueForRemove.isEmpty()){
            for (Query q : queueForRemove) {
                if (q.isDynamic) {
                    q.fixture.getBody().setActive(false);
                    world.destroyBody(dynamics.get(q.id).body);
                    dynamics.get(q.id).setIdleState();
                } else {
                    q.fixture.getBody().setActive(false);
                    world.destroyBody(statics.get(q.id).body);
                    statics.get(q.id).setIdleState();
                }
            }
            queueForRemove.clear();
        }
    }

    void checkForOutOfBounds(){
        if (GameActivity.joe.getPosition().y>heightInCells) {
            result = Result.LOSE;
            GameActivity.playerData.deathsFallIntoAbyss++;
        }
    }

    void checkForWin(){
        short COLLECTED_PREQUARKS_FOR_COMPLETION = 3;
        if (collectedPrequarks==COLLECTED_PREQUARKS_FOR_COMPLETION){
            result=Result.WIN;
            GameActivity.playerData.aromas=Math.min(aroma.ordinal()+2, 3);
        }
    }

    public void restart(){
        scene=1;
        world.clearForces();
        collectedPrequarks=0;
        result=Result.NEITHER;
        GameActivity.joe.reset();
        for (StaticGameObject object : statics) {
            switch (object.type) {
                case INDICATOR:object.setIdleState();break;
                case COLLECTIBLE: ((Collectible)object).reset(); break;
                case PREQUARK:((Prequark) object).reset();break;
                case DIALOG: ((Dialog)object).reset(); break;
                case TRANSFORMABLE_PLATFORM:((Platform.Transformable) object).reset();break;
                case PANHANDLER:((Panhandler)object).reset(); break;
                case FAKE_BLADE:((Blade.Fake)object).reset(); break;
                case MUK: ((Muk)object).reset(); break;
                case COIN: ((Coin)object).reset(); break;
            }
        }
        for (DynamicGameObject object : dynamics) object.reset();
        for (BasicScript script: scripts) script.reset();
    }

    public BasicScript getScript(int id){return scripts.get(id);}

    public StaticGameObject getStatic(int id){
        return statics.get(id);
    }

    public DynamicGameObject getDynamic(int id){ return dynamics.get(id); }

    private class Query {
        private Fixture fixture;
        private int id;
        private boolean isDynamic;

        Query(Fixture fixture) {
            this.fixture=fixture;
            FixtureData fd=(FixtureData) fixture.getUserData();
            id=fd.id;
            isDynamic=fd.isDynamic;
        }
    }
}
