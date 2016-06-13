package com.kekonyan.aromatique.core;

import android.content.res.Configuration;
import com.kekonyan.aromatique.GameActivity;

public class Language {
    private Locale language;
    public enum Locale{EN, RU}

    public Locale getLanguage(){
        return language;
    }

    public Language(){
        language = Locale.valueOf(GameActivity.playerData.data.getString(PlayerData.LANGUAGE, Locale.EN.toString()));
        setLanguage(language);
    }

    public void setLanguage(Locale language) {
        this.language=language;
        java.util.Locale locale = new java.util.Locale(language.toString().toLowerCase());
        java.util.Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        GameActivity.resources.updateConfiguration(config, null);
    }


}
