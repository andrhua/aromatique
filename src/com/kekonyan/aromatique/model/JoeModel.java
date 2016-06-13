package com.kekonyan.aromatique.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import com.kekonyan.aromatique.core.IRenderable;
import com.kekonyan.aromatique.util.Const;

public class JoeModel implements IRenderable{
    public Clothes.Item clothes[];

    public JoeModel(){
        clothes =new Clothes.Item[4];
        for (int i=0; i<4; i++) clothes[i]=new Clothes.Item(i, 0, 0);
    }

    @Override
    public void render(Canvas canvas) {
        canvas.save();
        canvas.translate((Const.WIDTH-Const.CLOTHES_PRELOOK)/2, (Const.HEIGHT-Const.CLOTHES_PRELOOK)/2);
        clothes[2].render(canvas, 0, 0);
        clothes[3].render(canvas, 0, 0);
        clothes[1].render(canvas, 0, 0);
        clothes[0].render(canvas, 0, 0);
        canvas.restore();
    }

    public Bitmap createIngameBitmap() {
        Bitmap []bitmaps=new Bitmap[4];
        int gs=(int)(Const.PPM*3.25f);
        for (int i=0; i<4; i++){
            bitmaps[i]=Bitmap.createScaledBitmap(clothes[i].getBitmap(),gs,gs,false);
        }
        int bodyWidth = bitmaps[1].getWidth(), bodyHeight = bitmaps[1].getHeight(),
                footsWidth = bitmaps[3].getWidth(), footsHeight = bitmaps[3].getHeight();
        float marginLeft =(float) (bodyWidth * 0.5 - footsWidth * 0.5);
        float marginTop =(float) (bodyHeight * 0.5 - footsHeight * 0.5);
        Bitmap character = Bitmap.createBitmap(bodyWidth, bodyHeight, bitmaps[1].getConfig());
        Canvas canvas = new Canvas(character);
        canvas.drawBitmap(bitmaps[2], new Matrix(), null);
        canvas.drawBitmap(bitmaps[3], marginLeft, marginTop, null);
        canvas.drawBitmap(bitmaps[1], marginLeft, marginTop, null);
        canvas.drawBitmap(bitmaps[0], marginLeft, marginTop, null);
        int vertexes[]=new int[4];
        vertexes[0]=vertexes[3]=Integer.MAX_VALUE;
        vertexes[1]=vertexes[2]=0;
        for (int x = 0; x< character.getWidth(); x++){
            for (int y = 0; y< character.getHeight(); y++){
                if (character.getPixel(x, y)!=Color.TRANSPARENT){
                    if (y<vertexes[0]) vertexes[0]=y;
                    if (y>vertexes[2]) vertexes[2]=y;
                    if (x<vertexes[3]) vertexes[3]=x;
                    if (x>vertexes[1]) vertexes[1]=x;
                }
            }
        }
        vertexes[2]=character.getHeight();
        return Bitmap.createBitmap(character, vertexes[3], vertexes[0], vertexes[1]-vertexes[3], vertexes[2]-vertexes[0]);
    }

    public void setItem(Clothes.Item item){
        clothes[item.part]=item;
    }

}
