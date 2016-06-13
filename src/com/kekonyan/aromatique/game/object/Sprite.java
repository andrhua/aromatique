package com.kekonyan.aromatique.game.object;

import android.graphics.Canvas;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.game.tiled.core.MapObject;
import com.kekonyan.aromatique.util.Const;

public class Sprite extends StaticGameObject{
    private Assets.Game.Object object;

    public Sprite(int id, MapObject mapObject, int zOrder) {
        super(id, mapObject, zOrder);
        object= Assets.Game.Object.valueOf(mapObject.getProperties().getProperty("type"));
    }

    @Override
    public void render(Canvas canvas) {
        canvas.save();
        canvas.translate(left* Const.PPM, top*Const.PPM);
        canvas.drawBitmap(Assets.Game.objects[object.ordinal()], 0, 0, null);
        canvas.restore();
    }
}
