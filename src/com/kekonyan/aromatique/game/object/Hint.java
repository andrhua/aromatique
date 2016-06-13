package com.kekonyan.aromatique.game.object;

import android.graphics.Canvas;
import android.text.Layout;
import android.text.StaticLayout;
import com.kekonyan.aromatique.GameActivity;
import com.kekonyan.aromatique.R;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.game.tiled.core.MapObject;
import com.kekonyan.aromatique.util.Const;

public class Hint extends StaticGameObject {
    private StaticLayout bounds;

    public Hint(int id, MapObject mapObject, int zOrder) {
        super(id, mapObject, zOrder);
        String text = GameActivity.resources.getStringArray(R.array.knowledges)[Integer.valueOf(mapObject.getProperties().getProperty("knowledge"))];
        bounds=new StaticLayout(text, Assets.Game.textPaint, (int)(width*Const.PPM), Layout.Alignment.ALIGN_CENTER, 1, 1, false);
        height = (bounds.getLineCount()+1)*Const.TEXT_HINT;
    }

    @Override
    public void render(Canvas canvas) {
        canvas.save();
        canvas.translate(left*Const.PPM, top*Const.PPM);
        canvas.drawRect(0, 0, width*Const.PPM, height, Assets.moneyPaint);
        bounds.draw(canvas);
        canvas.restore();
    }
}
