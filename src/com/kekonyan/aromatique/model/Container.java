package com.kekonyan.aromatique.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
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
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import static com.kekonyan.aromatique.core.Assets.CONTAINERS;

public class Container implements IPreviewable {
    public String name;
    public int price;
    public int index;
    public List<Clothes.Item> content;
    private static JSONArray jsonArray=new JSONArray();
    private Bitmap preview;
    private AsyncTask<Void,Void,Void> loadingTask;
    private boolean isRegularLoaded;
    private String path;

    public Container(int index){
        try {
            content= new ArrayList<>();
            JSONObject jsonObject;
            jsonObject=jsonArray.getJSONObject(index);
            this.index=index;
            this.name=jsonObject.optString("name");
            this.price=jsonObject.optInt("price");
            Log.d("container", name + " " + price);
            parseContent(jsonObject.optString("content"));
            path=CONTAINERS.concat(String.valueOf(index));
            loadingTask=new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    Bitmap bmp=Assets.Util.decodeBitmapFromAssets(path, Const.INSHOP_CONTAINER, Const.INSHOP_CONTAINER, false);
                    preview=Bitmap.createScaledBitmap(bmp, Const.REPOSITORY_CELL, Const.REPOSITORY_CELL, true);
                    Assets.putBitmap(path, bmp);
                    return null;
                }
            };
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(Canvas canvas, int left, int top){
        if (Assets.getBitmap(path)==null) isRegularLoaded =false;
        if (!isRegularLoaded){
            switch (loadingTask.getStatus()){
                case PENDING: loadingTask.execute(); break;
                case FINISHED: isRegularLoaded =true; break;
            }
        } else canvas.drawBitmap(Assets.getBitmap(path), left, top, null);
    }

    @Override
    public void renderPreview(Canvas canvas, int left, int top){
        if (Assets.getBitmap(path)==null) isRegularLoaded=false;
        if (!isRegularLoaded){
            switch (loadingTask.getStatus()){
                case PENDING: loadingTask.execute(); break;
                case FINISHED: isRegularLoaded =true; break;
            }
        } else canvas.drawBitmap(preview, left, top, null);
    }

    private void parseContent(String s){
        String[] intermediate=s.split(",");
        for (String i:intermediate)
            this.content.add(new Clothes.Item(Character.getNumericValue(i.charAt(0)),Integer.parseInt(i.substring(1)),0));
    }

    public SpannableStringBuilder printContent(){
        SpannableStringBuilder ssb=new SpannableStringBuilder();
        int previous=0;
        for (Clothes.Item c : content) {
            ForegroundColorSpan fcs = new ForegroundColorSpan(UI.doEvaluation(c.price));
            ssb.append(c.brand).append(" ").append(c.model).append("\n");
            ssb.setSpan(fcs, previous, previous + c.brand.length() + c.model.length() + 2, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            previous += c.brand.length() + c.model.length() + 2;
        }
        return ssb;
    }

    public SpannableStringBuilder printInfo(){
        SpannableStringBuilder ssb=new SpannableStringBuilder();
        Context context=GameActivity.getContext();
        return  ssb.append(context.getString(R.string.container)).append(" ").append(name).append("\n").append(String.valueOf(price)).append("$\n").append(context.getString(R.string.includes));
    }

    public static void loadContainerJSON() {
        try {
            InputStream is = GameActivity.assetManager.open("json/containers.json");
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            line = sb.toString();
            jsonArray = new JSONObject(line).getJSONArray("containers");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public static Clothes.Item generateItem(List<Clothes.Item> list){
        SecureRandom secureRandom=new SecureRandom();
        Clothes.Item clothes=list.get(secureRandom.nextInt(list.size()));
        return new Clothes.Item(clothes.part, clothes.index, System.currentTimeMillis());
    }

    public Bitmap getBitmap() {
        return Assets.Util.decodeBitmapFromAssets(path, Const.WIDTH/5, Const.WIDTH/5, false);
    }


    public static class Item extends Container {
        public long purchaseDate;

        public Item(int index, long purchaseDate) {
            super(index);
            this.purchaseDate=purchaseDate;
        }

    }
}
