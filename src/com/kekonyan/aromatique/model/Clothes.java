package com.kekonyan.aromatique.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import com.kekonyan.aromatique.GameActivity;
import com.kekonyan.aromatique.R;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.util.Const;
import com.kekonyan.aromatique.util.UI;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Clothes implements Comparable<Clothes.Item>, IPreviewable{
    public String brand;
    public String model;
    public int price;
    public int index;
    public int part;
    private static JSONArray []jsonArray= new JSONArray[4];
    private static String[] jsonName ={"head", "body", "legs", "foots"};
    private String path_regular, path_preview;
    private boolean isRegularLoaded, isPreviewLoaded;
    private AsyncTask<Void,Void,Void> regularLoadingTask, previewLoadingTask;

    private Clothes(int part, int index){
        try {
            JSONObject jsonObject;
            jsonObject=jsonArray[part].getJSONObject(index);
            this.index =index;
            this.part=part;
            this.brand=jsonObject.optString("brand");
            this.model=jsonObject.optString("model");
            this.price=jsonObject.optInt("price");
            path_regular=Assets.CLOTHES_REGULAR.concat(String.valueOf(part)).concat("/").concat(String.valueOf(index));
            path_preview=Assets.CLOTHES_PREVIEW.concat(String.valueOf(part)).concat("/").concat(String.valueOf(index));
            regularLoadingTask=new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    Bitmap bmp=Assets.Util.decodeBitmapFromAssets(path_regular, Const.CLOTHES_PRELOOK, Const.CLOTHES_PRELOOK, false);
                    Assets.putBitmap(path_regular, bmp);
                    return null;
                }
            };
            previewLoadingTask= new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    Bitmap bmp=Assets.Util.decodeBitmapFromAssets(path_preview, Const.REPOSITORY_CELL, Const.REPOSITORY_CELL, false);
                    Assets.putBitmap(path_preview, bmp);
                    return null;
                }
            };
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    Bitmap getBitmap(){
        return Assets.getBitmap(path_regular);
    }

    @Override
    public void render(Canvas canvas, int left, int top) {
        if (Assets.getBitmap(path_regular)==null) isRegularLoaded=false;
        if (!isRegularLoaded){
            switch (regularLoadingTask.getStatus()){
                case PENDING: regularLoadingTask.execute(); break;
                case FINISHED: isRegularLoaded=true; break;
            }
        } else canvas.drawBitmap(Assets.getBitmap(path_regular), left, top, null);
    }

    @Override
    public void renderPreview(Canvas canvas, int left, int top) {
        if (Assets.getBitmap(path_preview)==null) isPreviewLoaded=false;
        if (!isPreviewLoaded){
            switch (previewLoadingTask.getStatus()){
                case PENDING: previewLoadingTask.execute(); break;
                case FINISHED: isPreviewLoaded=true; break;
            }
        } else canvas.drawBitmap(Assets.getBitmap(path_preview), left, top, null);
    }

    public Bitmap getPreviewLargedBitmap(){
        return Assets.Util.decodeBitmapFromAssets(path_preview, Const.WIDTH/4, Const.WIDTH/4, false);
    }


    public SpannableStringBuilder printInfo(){
        SpannableStringBuilder ssb=new SpannableStringBuilder();
        ForegroundColorSpan fcs=new ForegroundColorSpan(UI.doEvaluation(price));
        ssb.append(priceToQuality()).append("\n").append(brand).append("\n").append(model).append("\n").append(String.valueOf(price)).append("$");
        ssb.setSpan(fcs, 0, priceToQuality().length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return ssb;
    }

    private String priceToQuality(){
        String s;
        Context context=GameActivity.getContext();
        if (price< Const.PRICE_DEMOCRATIC) s=context.getString(R.string.democratic); else
        if (price<Const.PRICE_MASS) s=context.getString(R.string.mass); else
        if (price<Const.PRICE_FACTORY) s=context.getString(R.string.factory); else
        if (price<Const.PRICE_PRET_A_PORTER) s=context.getString(R.string.pret_a_porter); else
        if (price<Const.PRICE_PRET_A_PORTER_DE_LUXE) s=context.getString(R.string.pret_a_porter_de_luxe); else
        if (price<Const.PRICE_HAUTE_COUTURE) s=context.getString(R.string.haute_couture); else
            s=context.getString(R.string.unique);
        return s;
    }

    public static void loadClothesJSON() {
        try {
            InputStream is = GameActivity.assetManager.open("json/clothes.json");
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            line=sb.toString();
            for (int i=0; i<4; i++) jsonArray[i] = new JSONObject(line).getJSONArray(jsonName[i]);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int compareTo(Item clothes) {
        return this.part-clothes.part==0?this.index -clothes.index :this.part-clothes.part;
    }

    public static class Item extends Clothes {
        public long purchaseDate;

        public Item(int part, int index, long purchaseDate) {
            super(part, index);
            this.purchaseDate=purchaseDate;
        }
    }
}
