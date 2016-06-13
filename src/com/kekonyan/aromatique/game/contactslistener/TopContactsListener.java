package com.kekonyan.aromatique.game.contactslistener;

import com.kekonyan.aromatique.game.FixtureData;
import com.kekonyan.aromatique.game.level.TopLevel;
import org.jbox2d.dynamics.World;

public class TopContactsListener extends ContactsListener {

    public TopContactsListener(World world, TopLevel level) {
        super(world, level);
    }

    @Override
    protected void collideDisappearingWall(FixtureData fd) {
        level.getScript(TopLevel.Script.PRISON.ordinal()).start();
    }
}