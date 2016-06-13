package com.kekonyan.aromatique.util;

public class Const {
    public static final float TPX = 32;
    public static int BUTTON_BMP, BUTTON_ARROW_BMP, INGAME_ARROW, INSHOP_CONTAINER,
            TEXT_HEADER, TEXT_REGULAR, TEXT_HINT,
            REPOSITORY_CELL, REPOSITORY_FRAME_CELL,
            BACKGROUND_BMP,
            WIDTH, HEIGHT,
            CLOTHES_PRELOOK, CLOTHES_INGAME,
            MIN_DISTANCE,
            GAME_CELL_HEIGHT, GAME_CELL_WIDTH,
            PPM;
    public static final int
            PRICE_DEMOCRATIC=100, PRICE_MASS=PRICE_DEMOCRATIC *5, PRICE_FACTORY = PRICE_MASS *2, PRICE_PRET_A_PORTER=PRICE_FACTORY *5,
            PRICE_PRET_A_PORTER_DE_LUXE=PRICE_PRET_A_PORTER *20, PRICE_HAUTE_COUTURE=100* PRICE_PRET_A_PORTER_DE_LUXE;


    public Const(int w, int h){
        BUTTON_BMP =h/10;
        INSHOP_CONTAINER =w/8;
        TEXT_HEADER =h/12;
        TEXT_REGULAR =h/15;
        TEXT_HINT =h/20;
        BUTTON_ARROW_BMP =h/10;
        INGAME_ARROW =h/6;
        CLOTHES_PRELOOK =h/2;
        CLOTHES_INGAME =w/8;
        REPOSITORY_CELL =w/10;
        REPOSITORY_FRAME_CELL = REPOSITORY_CELL /40;
        WIDTH =w;
        HEIGHT =h;
        MIN_DISTANCE=20;
        BACKGROUND_BMP =w/20;
        GAME_CELL_WIDTH=(int)(Math.floor(w/22f));
        GAME_CELL_HEIGHT=(int)(Math.floor(h/12f));
        PPM=(int)(Math.floor(w/25));
    }
}
