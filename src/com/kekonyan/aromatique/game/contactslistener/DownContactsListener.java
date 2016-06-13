package com.kekonyan.aromatique.game.contactslistener;

import com.kekonyan.aromatique.game.FixtureData;
import com.kekonyan.aromatique.game.level.DownLevel;
import org.jbox2d.dynamics.World;

public class DownContactsListener extends ContactsListener {
    public DownContactsListener(World world, DownLevel level) {
        super(world, level);
    }

    @Override
    protected void collideDisappearingWall(FixtureData fd) {
        level.getScript(DownLevel.Script.TRAFFIC_LIGHT.ordinal()).start();
    }
}
