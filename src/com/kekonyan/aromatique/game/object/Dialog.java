package com.kekonyan.aromatique.game.object;

import android.graphics.Canvas;
import android.text.DynamicLayout;
import android.text.Layout;
import com.kekonyan.aromatique.GameActivity;
import com.kekonyan.aromatique.R;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.game.FixtureData;
import com.kekonyan.aromatique.game.tiled.core.MapObject;
import com.kekonyan.aromatique.util.Const;

public class Dialog extends StaticGameObject implements IQueryable, IResetable{
    private DynamicLayout bounds;
    private String[] phrases;
    private String text;
    private int phrase;

    public Dialog(int id, MapObject mapObject, int zOrder) {
        super(id, mapObject, zOrder);
        phrases=GameActivity.resources.getStringArray(R.array.dialog)[Integer.valueOf(mapObject.getProperties().getProperty("dialog"))].split("#");
        text=phrases[phrase];
        bounds=new DynamicLayout(text, Assets.Game.textPaint, (int)(width* Const.PPM), Layout.Alignment.ALIGN_CENTER, 1, 1, false);
        height = (bounds.getLineCount()+1)*Const.TEXT_HINT;
        type= FixtureData.Type.DIALOG;
    }

    @Override
    public void render(Canvas canvas) {
        canvas.save();
        canvas.translate(left*Const.PPM, top*Const.PPM);
        canvas.drawRect(0, 0, width*Const.PPM, height, Assets.moneyPaint);
        bounds.draw(canvas);
        canvas.restore();
    }

    @Override
    public void queryForMagic() {
        text=phrases[++phrase];
        bounds=new DynamicLayout(text, Assets.Game.textPaint, (int)(width* Const.PPM), Layout.Alignment.ALIGN_CENTER, 1, 1, false);
    }

    @Override
    public void reset() {
        phrase=0;
        text=phrases[phrase];
        bounds=new DynamicLayout(text, Assets.Game.textPaint, (int)(width* Const.PPM), Layout.Alignment.ALIGN_CENTER, 1, 1, false);
    }
}
