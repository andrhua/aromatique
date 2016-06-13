package com.kekonyan.aromatique.model;

import android.graphics.Canvas;

interface IPreviewable {
    void render(Canvas canvas, int left, int top);
    void renderPreview(Canvas canvas, int left, int top);
}
