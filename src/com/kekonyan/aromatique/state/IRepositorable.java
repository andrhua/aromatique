package com.kekonyan.aromatique.state;

import android.graphics.Canvas;

public interface IRepositorable {
    void drawRepository(Canvas canvas);
    void use();
    void delete();
}
