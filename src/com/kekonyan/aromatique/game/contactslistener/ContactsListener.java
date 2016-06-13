package com.kekonyan.aromatique.game.contactslistener;

import com.kekonyan.aromatique.GameActivity;
import com.kekonyan.aromatique.audio.Sfx;
import com.kekonyan.aromatique.core.PlayerData;
import com.kekonyan.aromatique.game.FixtureData;
import com.kekonyan.aromatique.game.level.BasicLevel;
import com.kekonyan.aromatique.game.object.*;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

public abstract class ContactsListener implements ContactListener {
    protected final BasicLevel level;
    protected final World world;
    protected final Sfx sfx;
    protected final PlayerData playerData;
    private Joe joe;
    final public static short CATEGORY_JOE = 0x0001, CATEGORY_ENEMY = 0x0002,
            CATEGORY_COIN=0x0004, CATEGORY_SCENERY=0x0008,
            CATEGORY_FLYING_ENEMY=0x0016, CATEGORY_TRAP=0x0032;
    final public static short MASK_JOE=CATEGORY_ENEMY|CATEGORY_COIN|CATEGORY_SCENERY|CATEGORY_FLYING_ENEMY|CATEGORY_TRAP,
            MASK_ENEMY=CATEGORY_JOE|CATEGORY_SCENERY,
            MASK_COIN=CATEGORY_JOE, MASK_SCENERY =CATEGORY_ENEMY|CATEGORY_JOE,
            MASK_FLYING_ENEMY=CATEGORY_JOE;

    public ContactsListener(World world, BasicLevel level){
        this.world=world;
        this.level=level;
        sfx =GameActivity.sfx;
        playerData=GameActivity.playerData;
        joe=GameActivity.joe;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture a=contact.getFixtureA();
        Fixture b=contact.getFixtureB();
        FixtureData aUserData=(FixtureData)a.getUserData();
        FixtureData bUserData=(FixtureData)b.getUserData();
        boolean condition1=aUserData.type== FixtureData.Type.LAND_SENSOR&&
                bUserData.type!= FixtureData.Type.COIN&&bUserData.type!= FixtureData.Type.PREQUARK&&
                bUserData.type!= FixtureData.Type.GRAVITY_ANOMALY&&bUserData.type!= FixtureData.Type.WATER&&
                bUserData.type!= FixtureData.Type.INDICATOR,
                condition2=bUserData.type==FixtureData.Type.LAND_SENSOR&&
                        aUserData.type!= FixtureData.Type.COIN&&aUserData.type!= FixtureData.Type.PREQUARK&&
                        aUserData.type!=FixtureData.Type.GRAVITY_ANOMALY&&aUserData.type!= FixtureData.Type.WATER&&
                        aUserData.type!= FixtureData.Type.INDICATOR;
        if (condition1||condition2) ++joe.footContacts;
        //a - player
        //b - object
        if (aUserData.type!= FixtureData.Type.JOE &&aUserData.type!= FixtureData.Type.BULLET){
            Fixture tmp=a;
            a=b;
            b=tmp;
            aUserData=(FixtureData)a.getUserData();
            bUserData=(FixtureData)b.getUserData();
        }
        switch (aUserData.type) {
            case JOE: {
                switch (bUserData.type) {
                    case WOOMBA_SENSOR:
                        sfx.play(Sfx.JUMP_OVER_ENEMY);
                        GameActivity.joe.jumpOverEnemy =true;
                        level.queryForRemove(b);
                        break;
                    case BULLET: playerData.deathsTurret++; level.result= BasicLevel.Result.LOSE; break;
                    case SPIKE: playerData.deathsSpike++; level.result = BasicLevel.Result.LOSE; break;
                    case COIN:
                        sfx.play(Sfx.PICK_UP_COIN);
                        playerData.totalGainedMoney++;
                        playerData.addMoney(10);
                        level.queryForRemove(b);
                    break;
                    case PREQUARK:
                        sfx.play(Sfx.PICK_UP_PREQUARK);
                        level.queryForRemove(b);
                        level.collectedPrequarks++;
                        break;
                    case DISPOSABLE_FLASHING_PLATFORM:
                        ((Platform.DisposableFlashing)level.getDynamic(bUserData.id)).queryForMagic();
                        break;
                    case DEATH_INDICATOR:
                        playerData.deathsFallIntoAbyss++;
                        level.result= BasicLevel.Result.LOSE;
                        break;
                    case INVISIBLE_BLADE:
                        ((Blade.Invisible)level.getStatic(bUserData.id)).queryForMagic();
                    case BLADE:
                        playerData.deathsBlade++;
                        level.result= BasicLevel.Result.LOSE;
                        break;
                    case TELEPORT:
                        Teleport t=(Teleport) level.getDynamic(bUserData.id);
                        t.queryForMagic();
                        if (t.rodnyan&&!t.scened) {
                            ++level.scene;
                            t.scened=true;
                        }
                        break;
                    case DISAPPEARING_PLATFORM: collideDisappearingWall(bUserData); break;
                    case DRAGON: sfx.play(Sfx.DRAGON);
                    case TIMING_SPIKE:
                    case WOOMBA:
                    case CLOUD:
                    case OCTOPUS:
                    case SHURIKEN:
                    case SMASH_PLATFORM:
                    case AMPHIBIAN:
                    case SPIDER:
                    case BIRD:
                    case GHOST:
                    case PACMAN_GHOST:
                    case BLASTOISE:
                    case MUK:level.result= BasicLevel.Result.LOSE; break;
                    case WATER: joe.inWater=true; break;
                    case INDICATOR: level.getStatic(bUserData.id).setActiveState(); break;
                    case COLLECTIBLE:level.queryForRemove(b); break;
                    case GRAVITY_ANOMALY: ((GravityAnomaly)level.getDynamic(bUserData.id)).needToAnomaly=true; break;
                    case PANHANDLER: ((Panhandler)level.getStatic(bUserData.id)).isTouching=true; break;
                    case BANANA: ((Banana)level.getDynamic(bUserData.id)).queryForMagic(); break;
                    case GRAVITY_SWITCHER: ((GravitySwitcher)level.getStatic(bUserData.id)).queryForMagic(); break;
                }
            }break;
            case BULLET: {
                switch (bUserData.type){
                    case PLATFORM: Turret turret=(Turret)level.getDynamic(aUserData.id); turret.resetBullet();
                }
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture a=contact.getFixtureA();
        Fixture b=contact.getFixtureB();
        FixtureData aUserData=(FixtureData)a.getUserData();
        FixtureData bUserData=(FixtureData)b.getUserData();
        boolean condition1=aUserData.type== FixtureData.Type.LAND_SENSOR&&
                bUserData.type!= FixtureData.Type.COIN&&bUserData.type!= FixtureData.Type.PREQUARK&&
                bUserData.type!= FixtureData.Type.GRAVITY_ANOMALY&&bUserData.type!= FixtureData.Type.WATER&&
                bUserData.type!= FixtureData.Type.INDICATOR,
                condition2=bUserData.type==FixtureData.Type.LAND_SENSOR&&
                        aUserData.type!= FixtureData.Type.COIN&&aUserData.type!= FixtureData.Type.PREQUARK&&
                        aUserData.type!=FixtureData.Type.GRAVITY_ANOMALY&&aUserData.type!= FixtureData.Type.WATER&&
                        aUserData.type!= FixtureData.Type.INDICATOR;
        if (condition1||condition2) --joe.footContacts;
        switch (aUserData.type){
            case JOE: {
                switch (bUserData.type){
                    case WATER: joe.inWater=false; break;
                    case GRAVITY_ANOMALY: ((GravityAnomaly)level.getDynamic(bUserData.id)).needToAnomaly=false; break;
                    case PANHANDLER: ((Panhandler)level.getStatic(bUserData.id)).isTouching=false; break;
                }
            }
        }
    }


    @Override
    public void preSolve(Contact contact, Manifold manifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {

    }

    protected abstract void collideDisappearingWall(FixtureData fd);


}
