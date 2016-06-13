package com.kekonyan.aromatique.game.object;

import android.graphics.Canvas;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.game.FixtureData;
import com.kekonyan.aromatique.game.tiled.core.MapObject;
import com.kekonyan.aromatique.util.Const;
import org.jbox2d.dynamics.World;

public class Blade extends Indicator.Death {

    public Blade(int id, MapObject mapObject, World world, int zOrder) {
        super(id, mapObject, world);
        this.zOrder=zOrder;
        fixture.setUserData(new FixtureData(FixtureData.Type.BLADE, id));
        type= FixtureData.Type.BLADE;
    }

    @Override
    public void render(Canvas canvas) {
        canvas.save();
        canvas.translate(left*Const.PPM, top*Const.PPM);
        canvas.drawRect(0, 0, width*Const.PPM, height*Const.PPM, Assets.Game.bladePaint);
        canvas.restore();
    }

    public static class Fake extends Blade implements IQueryable, IResetable{
        private float fakeX;

        public Fake(int id, MapObject mapObject, World world, int zOrder) {
            super(id, mapObject, world, zOrder);
            fakeX=Float.valueOf(mapObject.getProperties().getProperty("fakeX"));
            left=fakeX;
            type= FixtureData.Type.FAKE_BLADE;
        }

        @Override
        public void queryForMagic() {
            left=centerX-width/2;
        }

        @Override
        public void reset() {
            left=fakeX;
        }
    }

    public static class Invisible extends Blade implements IQueryable, IResetable {

        public Invisible(int id, MapObject mapObject, World world, int zOrder) {
            super(id, mapObject, world, zOrder);
            fixture.setUserData(new FixtureData(FixtureData.Type.INVISIBLE_BLADE, id));
            type= FixtureData.Type.INVISIBLE_BLADE;
            setIdleState();
        }

        @Override
        public void queryForMagic() {
            setActiveState();
        }

        @Override
        public void reset() {
            setIdleState();
        }
    }
}
