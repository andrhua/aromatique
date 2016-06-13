package com.kekonyan.aromatique;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import com.kekonyan.aromatique.audio.Music;
import com.kekonyan.aromatique.audio.Sfx;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.core.GameView;
import com.kekonyan.aromatique.core.Language;
import com.kekonyan.aromatique.core.PlayerData;
import com.kekonyan.aromatique.game.object.Joe;
import com.kekonyan.aromatique.model.JoeModel;
import com.kekonyan.aromatique.util.Const;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;


public class GameActivity extends Activity {
    private GameView game;
    public static AssetManager assetManager;
    private static Context context;
    public static Resources resources;
    public static File saveJson;
    public static Sfx sfx;
    public static Music music;
    public static PlayerData playerData;
    public static Language language;
    public static Assets assets;
    public static Joe joe;
    public static Activity activity;
    private static final String SAVE="player.data";

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) game.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }


    @Override
    public void onPause() {
        music.release();
        try {
            playerData.write(new PrintStream(saveJson));
        } catch (IOException e) {
            e.printStackTrace();
        }
        playerData.savePreferences();
        game.gameThread.stopGameLoop();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        music = music.reset();
        sfx =new Sfx();
        language=new Language();
        game=new GameView(context);
        setContentView(game);
    }

    @Override
    public void onBackPressed() {
        game.gameThread.getStateManager().onBackPressed();
    }

    private void initialize(){
        activity=this;
        context=this;
        resources=getBaseContext().getResources();
        assetManager =getAssets();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        new Const(size.x,size.y);
        game=new GameView(context);
        setContentView(game);
        assets=new Assets();
        assets.execute();
        playerData=new PlayerData(getSharedPreferences(PlayerData.DATA, Context.MODE_PRIVATE));
        language=new Language();
        music =new Music();
        sfx =new Sfx();
        saveJson=new File(getFilesDir(), SAVE);
        playerData.joeModel =new JoeModel();
        try {
            if (saveJson.exists()) playerData.read(openFileInput(SAVE)); else saveJson.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Context getContext(){
        return context;
    }


}