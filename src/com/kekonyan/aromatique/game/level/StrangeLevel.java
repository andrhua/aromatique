package com.kekonyan.aromatique.game.level;

import android.graphics.Canvas;
import com.kekonyan.aromatique.game.FixtureData;
import com.kekonyan.aromatique.game.contactslistener.StrangeContactsListener;
import com.kekonyan.aromatique.game.object.DynamicGameObject;
import com.kekonyan.aromatique.game.object.StaticGameObject;
import com.kekonyan.aromatique.game.scripts.BasicScript;
import org.jbox2d.dynamics.World;

import java.util.ArrayList;

public class StrangeLevel extends BasicLevel {

    StrangeLevel(World world,
                 LevelManager.Aroma aroma,
                 ArrayList<StaticGameObject> statics,
                 ArrayList<DynamicGameObject> dynamics,
                 ArrayList<BasicScript> scripts,
                 int heightInCells) {
        super(world, aroma, statics, dynamics, scripts, heightInCells);
        StrangeContactsListener scl=new StrangeContactsListener(world, this);
        world.setContactListener(scl);
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
        for (StaticGameObject object:objects)
            if (scene==1&&object.left<130||(scene==2&&object.left>130&&object.left<285)||
                    (scene==3&&object.left>285&&object.left<564)||(scene==4&&object.left>564&&object.left<819)||
                    (scene==5&&object.left>819)||object.type== FixtureData.Type.JOE)
                if (object.isActive()) object.render(canvas);
        canvas.restore();
    }

    @Override
    public void update(float elapsedTime) {
        super.update(elapsedTime);
        for (DynamicGameObject object:dynamics)
            if (scene==1&&object.left<130||(scene==2&&object.left>130&&object.left<285)||
                    (scene==3&&object.left>285&&object.left<564)||(scene==4&&object.left>564&&object.left<819)||
                    (scene==5&&object.left>819)||object.type== FixtureData.Type.JOE)
                if (object.isActive()) object.update(elapsedTime);
        executeScripts(elapsedTime);
        checkForWin();
        checkForOutOfBounds();
    }
}
