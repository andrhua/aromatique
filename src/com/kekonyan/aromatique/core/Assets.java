package com.kekonyan.aromatique.core;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.*;
import android.os.AsyncTask;
import android.text.TextPaint;
import android.util.LruCache;
import com.kekonyan.aromatique.GameActivity;
import com.kekonyan.aromatique.util.Colour;
import com.kekonyan.aromatique.util.Const;

import java.io.IOException;


public class Assets extends AsyncTask<Void,Void,Void> {
    public static final String CLOTHES_REGULAR="clothes/", CLOTHES_PREVIEW="clothes/preview/", CONTAINERS="containers/";
    public static Bitmap[] buttons, backgrounds, previewBG, other;
    public static Typeface font;
    public static Paint headerPaint, regularPaint, hintPaint, aromaPaint, transparentPaint, moneyPaint;
    public static TextPaint textPaint;
    private static BitmapCache bitmapCache;
    public enum Button{ADD, BACK, CONTAINER, STATS, INVENTORY, NEXT, SETTINGS, INFO, RIGHT, DOWN, LEFT, UP, GAME_LEFT, GAME_RIGHT}
    public enum Background{HEXAGON, SETTINGS, HEART, HANGER, CLOVER, CONTAINER, FLOWER}
    public enum Other{DRESSED, LOCKED, JOE}

    public static Bitmap getBitmap(String path){
        return bitmapCache.get(path);
    }

    public static void putBitmap(String path, Bitmap bmp){
        bitmapCache.put(path, bmp);
    }

    @Override
    protected void onPreExecute() {
        try {
            ActivityManager am=(ActivityManager) GameActivity.activity.getSystemService(Context.ACTIVITY_SERVICE);
            bitmapCache=new BitmapCache(am.getMemoryClass()*1024*1024/8);
            previewBG=new Bitmap[GameActivity.assetManager.list("bitmap/backgrounds/game/preview").length];
            backgrounds=new Bitmap[GameActivity.assetManager.list("bitmap/backgrounds").length-1];
            buttons =new Bitmap[GameActivity.assetManager.list("bitmap/buttons").length];
            other=new Bitmap[GameActivity.assetManager.list("bitmap/other").length];
            font = Typeface.createFromAsset(GameActivity.assetManager, "font/main.otf");
            new Colour();
            headerPaint = new Paint();
            headerPaint.setTypeface(font);
            headerPaint.setAntiAlias(true);
            headerPaint.setColor(Color.BLACK);
            headerPaint.setTextAlign(Paint.Align.CENTER);
            regularPaint = new Paint(headerPaint);
            regularPaint.setTextSize(Const.TEXT_REGULAR);
            hintPaint = new Paint(regularPaint);
            hintPaint.setTextSize(Const.TEXT_HINT);
            aromaPaint = new Paint(headerPaint);
            aromaPaint.setStyle(Paint.Style.STROKE);
            textPaint = new TextPaint();
            textPaint.setColor(Color.BLACK);
            textPaint.setTextSize(Const.TEXT_HINT);
            textPaint.setTypeface(Assets.font);
            textPaint.setAntiAlias(true);
            headerPaint.setTextSize(Const.TEXT_HEADER);
            transparentPaint=new Paint();
            transparentPaint.setColor(Color.argb(128,0,0,0));
            moneyPaint =new Paint(regularPaint);
            moneyPaint.setColor(Color.WHITE);
        } catch (IOException e) {
                e.printStackTrace();
            }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        int i = Const.BUTTON_BMP, a = Const.BUTTON_ARROW_BMP, g = Const.INGAME_ARROW, b = Const.BACKGROUND_BMP;
        other[0] = Util.decodeBitmapFromAssets("other/0", Const.WIDTH / 50, Const.WIDTH / 50, false);
        other[1] = Util.decodeBitmapFromAssets("other/1", Const.HEIGHT / 8, Const.HEIGHT / 8, false);
        other[2]=Util.decodeBitmapFromAssets("other/2", Const.WIDTH/2, Const.WIDTH/2, false);
        int j;
        for (j=0; j<buttons.length; j++) {
            if (j<7) buttons[j] = Util.decodeBitmapFromAssets("buttons/" + j, i, i, false); else
            if (j<12) buttons[j]= Util.decodeBitmapFromAssets("buttons/" + j, a, a, false); else
            buttons[j]= Util.decodeBitmapFromAssets("buttons/"+j, g, g, true);
        }
        for (j = 0; j < backgrounds.length; j++)
            backgrounds[j] = Util.decodeBitmapFromAssets("backgrounds/" + j, b, b, true);
        for (j=0; j<previewBG.length; j++)
            previewBG[j]=Util.decodeBitmapFromAssets("backgrounds/game/preview/"+j, Const.WIDTH/5-2, Const.HEIGHT/5-2, false);
        return null;
    }

    public static class Game extends AsyncTask<Void, Void, Void>{
        public static Bitmap[] objects, gameBGs;

        public enum Object {
            PREQUARK, COIN, WOOMBA, CLOUD, TELEPORT, DRAGON, MUK,
            SHURIKEN, PANHANDLER, OCTOPUS, KEY, TANGELO, MACHAMP,
            LAIR, AMPHIBIAN, FISH, SPIDER, BIRD, BANANA, GHOST,
            GRAVITY_SWITCHER, PACMAN_GHOST, BLASTOISE
        }
        public static Paint DFWPaint, spikePaint, wallPaint, bladePaint, bloodPaint, bgPaint;
        public static TextPaint textPaint;

        @Override
        protected void onPreExecute() {
            DFWPaint=new Paint();
            DFWPaint.setColor(Color.RED);
            spikePaint=new Paint();
            spikePaint.setColor(Color.rgb(192, 192, 192));
            wallPaint=new Paint();
            bladePaint=new Paint();
            bladePaint.setColor(Color.GRAY);
            bloodPaint=new Paint();
            bloodPaint.setColor(Colour.BLOOD);
            bgPaint=new Paint();
            textPaint=new TextPaint(Assets.textPaint);
            textPaint.setTextSize(9*Const.TEXT_HINT/10);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                objects =new Bitmap[GameActivity.assetManager.list("bitmap/game_objects").length];
                gameBGs=new Bitmap[GameActivity.assetManager.list("bitmap/backgrounds/game").length-1];
                int j;
                for (j=0; j< objects.length; j++) {
                    int size=0;
                    switch (Object.values()[j]){
                        case WOOMBA:case SHURIKEN:case KEY:case FISH:case BANANA:size = Const.PPM; break;
                        case PREQUARK:case COIN:size=(int)(Const.PPM*.6f); break;
                        case CLOUD: size=5*Const.PPM; break;
                        case GHOST: size=4*Const.PPM; break;
                        case GRAVITY_SWITCHER:case BIRD:case OCTOPUS:case TELEPORT: size=2*Const.PPM; break;
                        case DRAGON: size=26*Const.PPM; break;
                        case MUK: size=7*Const.PPM; break;
                        case PANHANDLER: size=3*Const.PPM/2; break;
                        case MACHAMP:case TANGELO: size=6*Const.PPM; break;
                        case BLASTOISE: case AMPHIBIAN: size=3*Const.PPM; break;
                        case PACMAN_GHOST:case LAIR: size=6*Const.PPM; break;
                        case SPIDER: size=14*Const.PPM; break;
                    }
                    objects[j] =Util.decodeBitmapFromAssets("game_objects/" + j, size, size, false);
                }
                for (j=0; j<gameBGs.length; j++)
                    gameBGs[j]=Util.decodeBitmapFromAssets("backgrounds/game/"+j, Const.WIDTH, Const.HEIGHT, true);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static class Util{
        public static Bitmap decodeBitmapFromAssets(String path, int width, int height, boolean isARGB) {
            try {
                BitmapFactory.Options options=new BitmapFactory.Options();
                options.inPreferredConfig=Bitmap.Config.RGB_565;
                options.inJustDecodeBounds=true;
                BitmapFactory.decodeStream(GameActivity.assetManager.open("bitmap/".concat(path).concat(".png")),null, options);
                options.inSampleSize=calculateInSampleSize(options, width, height);
                options.inJustDecodeBounds=false;
                Bitmap intermediate=BitmapFactory.decodeStream(GameActivity.assetManager.open("bitmap/".concat(path).concat(".png")), null, options);
                return Bitmap.createScaledBitmap(intermediate, width, height, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;
            if (height > reqHeight || width > reqWidth) {
                final int halfHeight = height / 2;
                final int halfWidth = width / 2;
                while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) inSampleSize *= 2;
            }
            return inSampleSize;
        }
    }

    private static class BitmapCache extends LruCache<String, Bitmap> {

        BitmapCache(int maxSize) {
            super(maxSize);
        }

        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount();
        }

    }
}
