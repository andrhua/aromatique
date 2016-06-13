package com.kekonyan.aromatique.game.contactslistener;

import com.kekonyan.aromatique.game.FixtureData;
import com.kekonyan.aromatique.game.level.StrangeLevel;
import org.jbox2d.dynamics.World;

public class StrangeContactsListener extends ContactsListener {

    public StrangeContactsListener(World world, StrangeLevel level) {
        super(world, level);
    }

    @Override
    protected void collideDisappearingWall(FixtureData fd) {

    }
}
