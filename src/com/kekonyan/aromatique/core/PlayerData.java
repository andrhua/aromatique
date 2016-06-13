package com.kekonyan.aromatique.core;

import android.content.SharedPreferences;
import android.util.JsonReader;
import android.util.JsonWriter;
import com.kekonyan.aromatique.GameActivity;
import com.kekonyan.aromatique.model.Clothes;
import com.kekonyan.aromatique.model.Container;
import com.kekonyan.aromatique.model.JoeModel;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class PlayerData {
    public static final String DATA = "data";
    private static final String MONEY = "money", AROMAS="aromas",
            HEAD_ID="head_id", BODY_ID="body_id", LEGS_ID="legs_id", FOOTS_ID="foots_id",
            HEAD="head", BODY="body", LEGS="legs", FOOTS="foots",
            INTRODUCTION="introduction",
            TIME_IN_GAME="time_in_game", TIME_IN_MENU="time_in_menu", TIME_BY_LEVELS="time_by_levels",
            MONEY_TOTAL_GAINED="money_total_gained", MONEY_TOTAL_SPENT="money_total_spent",
            DEATHS_OUT_OF_BOUNDS="deaths_out_of_bounds", DEATHS_SPIKE="deaths_spike",
            DEATHS_TURRET="deaths_turret", DEATHS_BLADE="deaths_blade", DEATHS_BY_LEVELS="deaths_by_levels",
            WARDROBE_COST="wardrobe_cost", INVENTORY_COST="inventory_cost",
            STATS="stats", PLAYER_DATA="player_data", INVENTORY="inventory", WARDROBE="wardrobe",
            PART="part", INDEX="index", PURCHASE_DATE="purchase_date";
    public static final String CONTROL="control", MUSIC_MENU ="music_menu", MUSIC_INGAME="music_ingame", LANGUAGE = "language", SFX="sfx";
    public SharedPreferences data;
    public boolean isIntroductionShown;
    public BigInteger money=BigInteger.ZERO;
    public int aromas;
    public JoeModel joeModel;
    public Control control;
    private List<Container.Item> inventory;
    private List<Clothes.Item> wardrobe;
    public long timeInGame, timeInMenu,
            totalGainedMoney, totalSpentMoney,
            deathsFallIntoAbyss, deathsSpike, deathsTurret, deathsBlade,
            wardrobeCost, inventoryCost;
    public long[] deathsByLevels, timeByLevels;

    public PlayerData(SharedPreferences data){
        this.data=data;
        wardrobe=new ArrayList<>();
        inventory=new ArrayList<>();
        Clothes.loadClothesJSON();
        Container.loadContainerJSON();
        timeInGame=timeInMenu=totalGainedMoney=totalSpentMoney=deathsFallIntoAbyss=deathsSpike=deathsTurret=deathsBlade=0;
        aromas=1;
        money=BigInteger.valueOf(100000);
        isIntroductionShown=false;
        deathsByLevels=new long[6];
        timeByLevels=new long[6];
    }

    public void read(InputStream inputStream){
        clearClothes();
        wardrobe.clear();
        inventory.clear();
        try {
            JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));
            jsonReader.beginObject();
            jsonReader.nextName();
            jsonReader.beginArray();
            while (jsonReader.hasNext()){
                wardrobe.add(readClothes(jsonReader));
            }
            jsonReader.endArray();
            jsonReader.nextName();
            jsonReader.beginArray();
            while (jsonReader.hasNext()){
                inventory.add(readContainer(jsonReader));
            }
            jsonReader.endArray();
            readPlayerData(jsonReader);
            readStats(jsonReader);
            jsonReader.endObject();
            jsonReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readStats(JsonReader jsonReader)throws IOException {
        jsonReader.nextName();
        jsonReader.beginObject();
        while (jsonReader.hasNext()){
            String name = jsonReader.nextName();
            switch (name) {
                case TIME_IN_GAME: timeInGame=jsonReader.nextLong(); break;
                case TIME_IN_MENU: timeInMenu=jsonReader.nextLong(); break;
                case MONEY_TOTAL_GAINED: totalGainedMoney=jsonReader.nextLong(); break;
                case MONEY_TOTAL_SPENT: totalSpentMoney=jsonReader.nextLong(); break;
                case DEATHS_OUT_OF_BOUNDS: deathsFallIntoAbyss =jsonReader.nextLong(); break;
                case DEATHS_SPIKE: deathsSpike=jsonReader.nextLong(); break;
                case DEATHS_TURRET: deathsTurret=jsonReader.nextLong(); break;
                case DEATHS_BLADE: deathsBlade=jsonReader.nextLong(); break;
                case DEATHS_BY_LEVELS: {
                    jsonReader.beginArray();
                    for (int i=0; i<6; i++)deathsByLevels[i]=jsonReader.nextLong();
                    jsonReader.endArray();
                }break;
                case TIME_BY_LEVELS:{
                    jsonReader.beginArray();
                    for (int i=0; i<6; i++)timeByLevels[i]=jsonReader.nextLong();
                    jsonReader.endArray();
                } break;
                case WARDROBE_COST: wardrobeCost=jsonReader.nextLong(); break;
                case INVENTORY_COST: inventoryCost=jsonReader.nextLong(); break;
            }
        }
        jsonReader.endObject();
    }

    private void readPlayerData(JsonReader jsonReader) throws IOException{
        int id[]=new int[4];
        long purchaseDates[]=new long[4];
        jsonReader.nextName();
        jsonReader.beginObject();
        while (jsonReader.hasNext()){
            String name = jsonReader.nextName();
            switch (name) {
                case INTRODUCTION: isIntroductionShown =jsonReader.nextBoolean(); break;
                case MONEY: money=BigInteger.valueOf(jsonReader.nextLong()); break;
                case AROMAS: aromas=jsonReader.nextInt(); break;
                case HEAD: id[0]=jsonReader.nextInt(); break;
                case BODY: id[1]=jsonReader.nextInt(); break;
                case LEGS: id[2]=jsonReader.nextInt(); break;
                case FOOTS: id[3]=jsonReader.nextInt(); break;
                case HEAD_ID: purchaseDates[0]=jsonReader.nextLong(); break;
                case BODY_ID: purchaseDates[1]=jsonReader.nextLong(); break;
                case LEGS_ID: purchaseDates[2]=jsonReader.nextLong(); break;
                case FOOTS_ID: purchaseDates[3]=jsonReader.nextLong(); break;
            }
        }
        jsonReader.endObject();
        for (int i=0; i<4; i++) joeModel.clothes[i]=new Clothes.Item(i, id[i], purchaseDates[i]);
    }

    private Clothes.Item readClothes(JsonReader jsonReader) throws IOException {
        int index=-1;
        int part=-1;
        long purchaseDate=0;
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            switch (name) {
                case PART:part = jsonReader.nextInt();break;
                case INDEX:index = jsonReader.nextInt();break;
                case PURCHASE_DATE:purchaseDate = jsonReader.nextLong(); break;
            }
        }
        jsonReader.endObject();
        return new Clothes.Item(part, index, purchaseDate);
    }

    private Container.Item readContainer(JsonReader jsonReader) throws IOException {
        int index=-1;
        long purchaseDate=0;
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            switch (name) {
                case INDEX:index = jsonReader.nextInt();break;
                case PURCHASE_DATE:purchaseDate = jsonReader.nextLong();
            }
        }
        jsonReader.endObject();
        return new Container.Item(index, purchaseDate);
    }

    public void write(PrintStream printStream) throws IOException {
        JsonWriter jsonWriter = new JsonWriter(new OutputStreamWriter(printStream));
        jsonWriter.setIndent("  ");
        jsonWriter.beginObject();
        jsonWriter.name(WARDROBE);
        jsonWriter.beginArray();
        for (Clothes.Item item:wardrobe) {
            jsonWriter.beginObject();
            jsonWriter.name(PART).value(item.part);
            jsonWriter.name(INDEX).value(item.index);
            jsonWriter.name(PURCHASE_DATE).value(item.purchaseDate);
            jsonWriter.endObject();
        }
        jsonWriter.endArray();
        jsonWriter.name(INVENTORY);
        jsonWriter.beginArray();
        for (Container.Item item:inventory){
            jsonWriter.beginObject();
            jsonWriter.name(INDEX).value(item.index);
            jsonWriter.name(PURCHASE_DATE).value(item.purchaseDate);
            jsonWriter.endObject();
        }
        jsonWriter.endArray();
        writePlayerData(jsonWriter);
        writeStats(jsonWriter);
        jsonWriter.endObject();
        jsonWriter.close();
    }

    private void writeStats(JsonWriter jsonWriter)throws IOException {
        jsonWriter.name(STATS);
        jsonWriter.beginObject();
        jsonWriter.name(TIME_IN_GAME).value(timeInGame);
        jsonWriter.name(TIME_IN_MENU).value(timeInMenu);
        jsonWriter.name(MONEY_TOTAL_GAINED).value(totalGainedMoney);
        jsonWriter.name(MONEY_TOTAL_SPENT).value(totalSpentMoney);
        jsonWriter.name(DEATHS_OUT_OF_BOUNDS).value(deathsFallIntoAbyss);
        jsonWriter.name(DEATHS_SPIKE).value(deathsSpike);
        jsonWriter.name(DEATHS_TURRET).value(deathsTurret);
        jsonWriter.name(DEATHS_BLADE).value(deathsBlade);
        jsonWriter.name(DEATHS_BY_LEVELS).beginArray();
        for (int i=0; i<6; i++) jsonWriter.value(deathsByLevels[i]);
        jsonWriter.endArray();
        jsonWriter.name(TIME_BY_LEVELS).beginArray();
        for (int i=0; i<6; i++) jsonWriter.value(timeByLevels[i]);
        jsonWriter.endArray();
        jsonWriter.name(WARDROBE_COST).value(wardrobeCost);
        jsonWriter.name(INVENTORY_COST).value(inventoryCost);
        jsonWriter.endObject();
    }

    private void writePlayerData(JsonWriter jsonWriter) throws IOException{
        jsonWriter.name(PLAYER_DATA);
        jsonWriter.beginObject();
        jsonWriter.name(INTRODUCTION).value(isIntroductionShown);
        jsonWriter.name(MONEY).value(money.longValue());
        jsonWriter.name(AROMAS).value(aromas);
        jsonWriter.name(HEAD).value(joeModel.clothes[0].index);
        jsonWriter.name(BODY).value(joeModel.clothes[1].index);
        jsonWriter.name(LEGS).value(joeModel.clothes[2].index);
        jsonWriter.name(FOOTS).value(joeModel.clothes[3].index);
        jsonWriter.name(HEAD_ID).value(joeModel.clothes[0].purchaseDate);
        jsonWriter.name(BODY_ID).value(joeModel.clothes[1].purchaseDate);
        jsonWriter.name(LEGS_ID).value(joeModel.clothes[2].purchaseDate);
        jsonWriter.name(FOOTS_ID).value(joeModel.clothes[3].purchaseDate);
        jsonWriter.endObject();
    }


    public boolean withdrawMoney(long howMuch){
        if (money.compareTo(BigInteger.valueOf(howMuch))<0) return false;
        else {
            totalSpentMoney+=howMuch;
            money=money.subtract(BigInteger.valueOf(howMuch));
            return true;
        }
    }

    public void addMoney(long howMuch){
        money=money.add(BigInteger.valueOf(howMuch));
    }

    public void savePreferences(){
        SharedPreferences.Editor editor = data.edit();
        editor.putFloat(MUSIC_MENU, GameActivity.music.getMenuVolume());
        editor.putFloat(MUSIC_INGAME, GameActivity.music.getIngameVolume());
        editor.putString(LANGUAGE, GameActivity.language.getLanguage().toString());
        editor.putBoolean(SFX, GameActivity.sfx.getSFX());
        editor.apply();
    }

    public void clearClothes() {
        for (int i=0; i<4; i++) {
            joeModel.clothes[i]=new Clothes.Item(i, 0, 0);
        }
    }

    public void clearStats(){
        deathsFallIntoAbyss=deathsSpike=deathsTurret=deathsBlade
                =timeInGame=timeInMenu=totalGainedMoney
                =totalSpentMoney=inventoryCost=wardrobeCost
                =0;
        aromas=1;
        money=BigInteger.valueOf(100000);
        for (int i=0; i<6; i++) {
            deathsByLevels[i] = 0;
            timeByLevels[i] = 0;
        }

    }

    public void addInventoryItem(Container.Item item){
        inventory.add(item);
        inventoryCost+=item.price;
    }

    public void addWardrobeItem(Clothes.Item item){
        wardrobe.add(item);
        wardrobeCost+=item.price;
    }

    public List<Clothes.Item> getWardrobe(){
        return wardrobe;
    }

    public List<Container.Item> getInventory(){
        return inventory;
    }

    public void removeInventoryItem(int index) {
        inventoryCost -= inventory.get(index).price;
        inventory.remove(index);
    }

    public void removeWardrobeItem(int index) {
        wardrobeCost-=wardrobe.get(index).price;
        wardrobe.remove(index);
    }
}
