package com.kekonyan.aromatique.game.object;

import android.graphics.Canvas;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.game.FixtureData;
import com.kekonyan.aromatique.game.tiled.core.MapObject;
import com.kekonyan.aromatique.util.Const;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

public class GravitySwitcher extends Indicator implements IQueryable {
    private World world;
    private Vec2 gravity;

    @Override
    public void render(Canvas canvas) {
        canvas.save();
        canvas.translate(left* Const.PPM, top*Const.PPM);
        canvas.drawBitmap(Assets.Game.objects[Assets.Game.Object.GRAVITY_SWITCHER.ordinal()], 0, 0, null);
        canvas.restore();
    }

    public GravitySwitcher(int id, MapObject mapObject, World world) {
        super(id, mapObject, world);
        this.world=world;
        boolean isUp = Boolean.parseBoolean(mapObject.getProperties().getProperty("i", "false"));
        fixture.setUserData(new FixtureData(FixtureData.Type.GRAVITY_SWITCHER, id));
        type= FixtureData.Type.GRAVITY_SWITCHER;
        gravity=new Vec2(0, (isUp ?-1:1)*15);
    }

    @Override
    public void queryForMagic() {
        world.setGravity(gravity);
    }
}
