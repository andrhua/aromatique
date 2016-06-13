package com.kekonyan.aromatique.game.level;

import android.graphics.Canvas;
import com.kekonyan.aromatique.game.FixtureData;
import com.kekonyan.aromatique.game.contactslistener.DownContactsListener;
import com.kekonyan.aromatique.game.object.DynamicGameObject;
import com.kekonyan.aromatique.game.object.StaticGameObject;
import com.kekonyan.aromatique.game.scripts.BasicScript;
import org.jbox2d.dynamics.World;

import java.util.ArrayList;

public class DownLevel extends BasicLevel {
    public enum Script{TRAFFIC_LIGHT}

    DownLevel(World world,
              LevelManager.Aroma aroma,
              ArrayList<StaticGameObject> statics,
              ArrayList<DynamicGameObject> dynamics,
              ArrayList<BasicScript> scripts,
              int heightInCells) {
        super(world, aroma, statics, dynamics, scripts, heightInCells);
        DownContactsListener dcl=new DownContactsListener(world, this);
        world.setContactListener(dcl);
        scene=1;
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
        for (StaticGameObject object:objects)
            //if (scene==1&&object.top<70||scene==2&&object.top>70||object.type== FixtureData.Type.JOE)
                if (object.isActive()) object.render(canvas);
        canvas.restore();
    }

    @Override
    public void update(float elapsedTime) {
        super.update(elapsedTime);
        for (DynamicGameObject object:dynamics)
            if (scene==1&&object.top<70||scene==2&&object.top>70||object.type== FixtureData.Type.JOE)
                if (object.isActive()) object.update(elapsedTime);
        executeScripts(elapsedTime);
        checkForWin();
        checkForOutOfBounds();
    }

}


