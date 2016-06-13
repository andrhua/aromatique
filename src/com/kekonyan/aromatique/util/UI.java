package com.kekonyan.aromatique.util;

import android.content.Context;
import android.graphics.*;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.UpdateAppearance;
import android.util.Property;
import com.kekonyan.aromatique.GameActivity;
import com.kekonyan.aromatique.R;

import java.util.Arrays;

public class UI {
    public static int doEvaluation(long cost){
        if (cost<Const.PRICE_DEMOCRATIC) return Color.BLACK; else
        if (cost<Const.PRICE_MASS) return Color.rgb(160,202,250); else
        if (cost<Const.PRICE_FACTORY) return Color.rgb(0,0,255); else
        if (cost<Const.PRICE_PRET_A_PORTER) return Color.rgb(175,44,197); else
        if (cost<Const.PRICE_PRET_A_PORTER_DE_LUXE) return Color.rgb(235,75,75); else
        if (cost<Const.PRICE_HAUTE_COUTURE) return Color.rgb(173,255,47); else
            return Color.rgb(0,255,0);
    }

    public static final Property<AnimatedColorSpan, Float> ANIMATED_COLOR_SPAN_FLOAT_PROPERTY
            = new Property<AnimatedColorSpan, Float>(Float.class, "ANIMATED_COLOR_SPAN_FLOAT_PROPERTY") {
        @Override
        public void set(AnimatedColorSpan span, Float value) {
            span.setTranslateXPercentage(value);
        }
        @Override
        public Float get(AnimatedColorSpan span) {
            return span.getTranslateXPercentage();
        }
    };

    public static class AnimatedColorSpan extends CharacterStyle implements UpdateAppearance {
        private final int[] colors;
        private Shader shader = null;
        private Matrix matrix = new Matrix();
        private float translateXPercentage = 0;

        public AnimatedColorSpan(Context context) {
            int [] colors_;
            if (GameActivity.playerData.aromas==1){
                colors_=new int[2];
                colors_[0]= Color.BLACK;
                colors_[1]= Color.WHITE;
            } else
                colors_= Arrays.copyOfRange(context.getResources().getIntArray(R.array.aromaColors), 0, GameActivity.playerData.aromas);
            colors = colors_;
        }

        void setTranslateXPercentage(float percentage) {
            translateXPercentage = percentage;
        }

        float getTranslateXPercentage() {
            return translateXPercentage;
        }

        @Override
        public void updateDrawState(TextPaint paint) {
            paint.setStyle(Paint.Style.FILL);
            float width = paint.getTextSize() * colors.length;
            if (shader == null) {
                shader = new LinearGradient(0, 0, 0, width, colors, null,
                        Shader.TileMode.MIRROR);
            }
            matrix.reset();
            matrix.setRotate(90);
            matrix.postTranslate(width * translateXPercentage, 0);
            shader.setLocalMatrix(matrix);
            paint.setShader(shader);
        }
    }

}
