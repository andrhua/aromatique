package com.kekonyan.aromatique.audio;

import android.media.MediaPlayer;
import com.kekonyan.aromatique.GameActivity;
import com.kekonyan.aromatique.R;
import com.kekonyan.aromatique.core.PlayerData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Music implements MediaPlayer.OnCompletionListener{
    private MediaPlayer mediaPlayer;
    private List<Integer> menuIDs;
    private int lastTrackID;
    private float menuVolume, ingameVolume;
    public enum Mode{MENU, GAME}
    private Mode mode;

    public Music reset(){
        if (mediaPlayer!=null) mediaPlayer.release();
        return new Music();
    }

    public void setMode(Mode mode){
        this.mode=mode;
        switch (mode){
            case MENU: mediaPlayer.setVolume(menuVolume, menuVolume); break;
            case GAME: mediaPlayer.setVolume(ingameVolume, ingameVolume); break;
        }
    }

   public void setMenuVolume(float menuVolume){
        this.menuVolume = menuVolume;
        if (mediaPlayer!=null) mediaPlayer.setVolume(menuVolume, menuVolume);
    }

    public float getMenuVolume(){return menuVolume;}

    public float getIngameVolume() {
        return ingameVolume;
    }

    public void setIngameVolume(float ingameVolume) {
        this.ingameVolume = ingameVolume;
    }

    public void start(){
        mediaPlayer.start();
    }

    public Music(){
        mode=Mode.MENU;
        menuVolume =GameActivity.playerData.data.getFloat(PlayerData.MUSIC_MENU, .5f);
        ingameVolume=GameActivity.playerData.data.getFloat(PlayerData.MUSIC_INGAME, .35f);
        Integer[] tracks = {R.raw.codeine, R.raw.dude, R.raw.get_up,
                R.raw.liquor, R.raw.stop_fucking_lying, R.raw.this_feeling,
                R.raw.thats_not_me, R.raw.san_francisco, R.raw.syrup, R.raw.new_day};
        menuIDs =new ArrayList<>(Arrays.asList(tracks));
        Collections.shuffle(menuIDs);
        mediaPlayer=MediaPlayer.create(GameActivity.getContext(), menuIDs.get(lastTrackID));
        mediaPlayer.setVolume(menuVolume, menuVolume);
        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mediaPlayer.release();
        if (lastTrackID== menuIDs.size()-1) lastTrackID=-1;
        mediaPlayer=MediaPlayer.create(GameActivity.getContext(), menuIDs.get(++lastTrackID));
        this.mediaPlayer=mediaPlayer;
        setMode(mode);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.start();
    }

    public void pause(){
        mediaPlayer.pause();
    }

    public void release(){
        mediaPlayer.release();
    }

    public void resume(){
        mediaPlayer.start();
    }
}
