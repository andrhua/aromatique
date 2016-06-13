package com.kekonyan.aromatique.game.object;

import android.graphics.Canvas;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.game.tiled.core.MapObject;
import com.kekonyan.aromatique.util.Const;

public class Background extends StaticGameObject {
    public Background(int id, MapObject mapObject, int zOrder) {
        super(id, mapObject, zOrder);
    }

    @Override
    public void render(Canvas canvas) {
        canvas.save();
        canvas.translate(left* Const.PPM, top*Const.PPM);
        canvas.drawRect(0, 0, width*Const.PPM, height*Const.PPM, Assets.Game.bgPaint);
        canvas.restore();
    }

    public static class Overlap extends Background{

        public Overlap(int id, MapObject mapObject, int zOrder) {
            super(id, mapObject, zOrder);
        }
    }
}
