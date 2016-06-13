package com.kekonyan.aromatique.audio;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import com.kekonyan.aromatique.GameActivity;
import com.kekonyan.aromatique.R;
import com.kekonyan.aromatique.core.PlayerData;

public class Sfx {
    private SoundPool soundPool;
    private boolean enabled;
    public static int GAME_WIN, PICK_UP_COIN, PICK_UP_PREQUARK, JUMP, JUMP_OVER_ENEMY, TELEPORT, DRAGON, JOHN_CENA,
    CLICK_POSITIVE, CLICK_NEGATIVE, CONTAINER_OPENING;
    private Context context;

    public Sfx(){
        context=GameActivity.getContext();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attributes;
            attributes = new AudioAttributes.Builder().
                    setFlags(AudioAttributes.USAGE_UNKNOWN).
                    setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).
                    build();
            soundPool =new SoundPool.Builder().
                    setAudioAttributes(attributes).
                    setMaxStreams(5).
                    build();
        } else {
            soundPool=new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }
        loadUISounds();
        enabled =GameActivity.playerData.data.getBoolean(PlayerData.SFX, true);
    }

    private void loadUISounds(){
        CLICK_POSITIVE=soundPool.load(context, R.raw.click_positive, 5);
        CLICK_NEGATIVE=soundPool.load(context, R.raw.click_negative, 5);
        CONTAINER_OPENING=soundPool.load(context, R.raw.container_opening, 5);
    }

    public void loadGameSounds(){
        GAME_WIN=soundPool.load(context, R.raw.game_win, 5);
        JUMP=soundPool.load(context, R.raw.jump, 5);
        JUMP_OVER_ENEMY=soundPool.load(context, R.raw.jump_over_enemy, 5);
        PICK_UP_COIN=soundPool.load(context, R.raw.pick_up_coin, 5);
        PICK_UP_PREQUARK=soundPool.load(context, R.raw.pick_up_prequark, 5);
        TELEPORT=soundPool.load(context, R.raw.teleport, 5);
        DRAGON=soundPool.load(context, R.raw.dragon, 5);
        JOHN_CENA=soundPool.load(context, R.raw.john_cena, 6);
    }

    public void play(final int soundID){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                soundPool.play(soundID, enabled ?1:0, enabled ?1:0, 5, 0, 1);
                return null;
            }
        }.execute();
    }

    public void setEnabled(boolean enabled){
        this.enabled =enabled;
        GameActivity.playerData.savePreferences();
    }

    public boolean getSFX() {
        return enabled;
    }
}
