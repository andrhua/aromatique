package com.kekonyan.aromatique.state;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import com.kekonyan.aromatique.GameActivity;
import com.kekonyan.aromatique.R;
import com.kekonyan.aromatique.UI.*;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.core.Language;
import com.kekonyan.aromatique.util.Const;

import java.io.IOException;
import java.io.PrintStream;

class SettingsState extends BasicState {
    private Button back, metamorphosis, sound, languages, confirmation, en, ru, info;
    private StaticLayout disclaimer;
    private ActionPopup metaActionPopup;
    private Slider menuMusicVolume, ingameMusicVolume;
    private Checkbox sfx;
    private Background background;
    private enum State {METAMORPHOSIS, SOUND, LANGUAGE}
    private State currentState;

    private void setState(State currentState){
        this.currentState=currentState;
        metamorphosis.setIdleState();
        sound.setIdleState();
        languages.setIdleState();
        metaActionPopup.setIdleState();
        switch (currentState){
            case METAMORPHOSIS: metamorphosis.setActiveState(); break;
            case SOUND: sound.setActiveState(); break;
            case LANGUAGE: languages.setActiveState(); break;
        }
    }

    private void setLanguage(Language.Locale language){
        this.language.setLanguage(language);
        en.setIdleState();
        ru.setIdleState();
        if (language== Language.Locale.EN) en.setActiveState();
        if (language== Language.Locale.RU) ru.setActiveState();
    }

    @Override
    public void preload() throws IOException {
        int i=Const.BUTTON_BMP;
        TextPaint tp = Assets.textPaint;
        info=new Button(Assets.buttons[Assets.Button.INFO.ordinal()], width-3*i/2, height-i, i/2, 0, true);
        back =new Button(Assets.buttons[Assets.Button.BACK.ordinal()], i/2, height-i, i/2, 0, false);
        languages=new Button(context.getString(R.string.language), width/2, 11*height/40, Assets.regularPaint, Color.TRANSPARENT, Color.rgb(195,195,195), true);
        sound=new Button(context.getString(R.string.sound), width/5, 11*height/40, Assets.regularPaint, Color.TRANSPARENT, Color.rgb(195,195,195), true);
        metamorphosis=new Button(context.getString(R.string.metamorphosis), 4*width/5, 11*height/40, Assets.regularPaint, Color.TRANSPARENT, Color.rgb(195,195,195), true);
        confirmation=new Button(context.getString(R.string.metamorphosis_confirmation), width/2, 2*height/3+height/16, Assets.hintPaint, Color.BLACK, Color.rgb(195,195,195), true);
        en=new Button(context.getString(R.string.english), 27*width/64, 37*height/64, Assets.hintPaint, Color.TRANSPARENT, Color.rgb(195,195,195), true);
        ru=new Button(context.getString(R.string.russian), 37*width/64, 37*height/64, Assets.hintPaint, Color.TRANSPARENT, Color.rgb(195,195,195), true);
        metaActionPopup =new ActionPopup(context.getString(R.string.cant_be_undone));
        background=new Background(Assets.backgrounds[Assets.Background.SETTINGS.ordinal()]);
        menuMusicVolume =new Slider(width/2, 11*height/24, width/3);
        menuMusicVolume.setPosition(music.getMenuVolume());
        ingameMusicVolume=new Slider(width/2, 15*height/24, width/3);
        ingameMusicVolume.setPosition(music.getIngameVolume());
        sfx=new Checkbox(GameActivity.sfx.getSFX(), context.getString(R.string.sfx), width/2, 15*height/20);
        disclaimer=new StaticLayout(context.getString(R.string.metamorphosis_description), tp, width, Layout.Alignment.ALIGN_CENTER, 1f, 0f, false);
        setLanguage(language.getLanguage());
        setState(State.SOUND);
    }

    SettingsState(Context context, StateManager stateManager) {
        super(context, stateManager);
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
        background.render(canvas);
        canvas.drawText(context.getString(R.string.settings_title), width/2, height/8, Assets.headerPaint);
        metamorphosis.render(canvas);
        sound.render(canvas);
        languages.render(canvas);
        back.render(canvas);
        info.render(canvas);
        switch (currentState){
            case METAMORPHOSIS: {
                canvas.save();
                canvas.translate(0, height/2);
                disclaimer.draw(canvas);
                canvas.restore();
                confirmation.render(canvas);
                if (metaActionPopup.isActive()) metaActionPopup.render(canvas);
            } break;
            case SOUND: {
                canvas.drawText(context.getString(R.string.menu_music), width/4, height/2, Assets.hintPaint);
                canvas.drawText(context.getString(R.string.ingame_music), width/4, 2*height/3, Assets.hintPaint);
                menuMusicVolume.render(canvas);
                ingameMusicVolume.render(canvas);
                sfx.render(canvas);
            }break;
            case LANGUAGE: {
                en.render(canvas);
                ru.render(canvas);
            }
        }
    }

    @Override
    public void update(float elapsedTime) {
        background.update(elapsedTime);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_UP: {
                if (metaActionPopup.isActive()) {
                    if (metaActionPopup.ok.onTouch(view, motionEvent)) {
                        playerData.clearStats();
                        playerData.clearClothes();
                        playerData.getWardrobe().clear();
                        playerData.getInventory().clear();
                        try {
                            playerData.write(new PrintStream(GameActivity.saveJson));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        getStateManager().setState(StateManager.State.MENU, false);
                    }
                    if (metaActionPopup.no.onTouch(view, motionEvent)) {
                        metaActionPopup.setIdleState();
                    }
                }
                if (back.onTouch(view, motionEvent)) {playerData.savePreferences();getStateManager().setState(StateManager.State.MENU, false); break;}
                if (info.onTouch(view, motionEvent)) {getStateManager().setState(StateManager.State.INFO, true); break;}
                if (metamorphosis.onTouch(view, motionEvent)) {setState(State.METAMORPHOSIS);break;}
                if (sound.onTouch(view, motionEvent)) {setState(State.SOUND);break;}
                if (languages.onTouch(view, motionEvent)) {setState(State.LANGUAGE);break;}
            }
            switch (currentState) {
                case METAMORPHOSIS: if (confirmation.onTouch(view, motionEvent)) metaActionPopup.setActiveState();
                    break;
                case SOUND: {
                    menuMusicVolume.onTouch(view, motionEvent);
                    ingameMusicVolume.onTouch(view, motionEvent);
                    sfx.onTouch(view, motionEvent);
                    GameActivity.sfx.setEnabled(sfx.isChecked());
                } break;
                case LANGUAGE: {
                    if (en.onTouch(view, motionEvent)) {setLanguage(Language.Locale.EN); getStateManager().setState(StateManager.State.MENU, false);}
                    if (ru.onTouch(view, motionEvent)) {setLanguage(Language.Locale.RU); getStateManager().setState(StateManager.State.MENU, false);}
                }break;
            } break;
            default:{
                if (metaActionPopup.isActive()) {
                    metaActionPopup.onTouch(view,motionEvent);
                }
                back.onTouch(view,motionEvent);
                info.onTouch(view,motionEvent);
                confirmation.onTouch(view,motionEvent);
                metamorphosis.onTouch(view,motionEvent);
                sound.onTouch(view,motionEvent);
                languages.onTouch(view,motionEvent);
            }
            switch (currentState){
                case METAMORPHOSIS:{
                    confirmation.onTouch(view,motionEvent);
                    if (metaActionPopup.isActive()){
                        metaActionPopup.ok.onTouch(view,motionEvent);
                        metaActionPopup.no.onTouch(view,motionEvent);
                    }
                } break;
                case SOUND:{
                    menuMusicVolume.onTouch(view, motionEvent);
                    ingameMusicVolume.onTouch(view, motionEvent);
                    music.setMenuVolume(menuMusicVolume.getPosition()/100f);
                    music.setIngameVolume(ingameMusicVolume.getPosition()/100f);
                } break;
                case LANGUAGE:{
                    en.onTouch(view,motionEvent);
                    ru.onTouch(view,motionEvent);
                }
            }
        }
        return true;
    }

    @Override
    public boolean onBackPressed() {
        if (!metaActionPopup.onBackPressed()) {
            getStateManager().setState(StateManager.State.MENU, false);
        }
        return true;
    }
    

}
