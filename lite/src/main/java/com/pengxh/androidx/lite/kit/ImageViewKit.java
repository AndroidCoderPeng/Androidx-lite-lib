package com.pengxh.androidx.lite.kit;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.widget.ImageView;

public class ImageViewKit {
    public static void switchBackground(ImageView imageView, Bitmap blurBitmap) {
        if (imageView == null || blurBitmap == null) {
            throw new IllegalArgumentException("ImageView or blurBitmap cannot be null");
        }

        Resources resources = imageView.getResources();
        if (resources == null) {
            throw new IllegalStateException("ImageView resources cannot be null");
        }

        TransitionDrawable transitionDrawable = null;
        Drawable lastDrawable;

        Drawable imageViewDrawable = imageView.getDrawable();
        if (imageViewDrawable instanceof TransitionDrawable) {
            transitionDrawable = (TransitionDrawable) imageViewDrawable;
            lastDrawable = transitionDrawable.findDrawableByLayerId(transitionDrawable.getId(1));
        } else if (imageViewDrawable instanceof BitmapDrawable) {
            lastDrawable = imageViewDrawable;
        } else {
            lastDrawable = new ColorDrawable(Color.TRANSPARENT);
        }

        final int TRANSITION_DURATION = 1000; // 常量化过渡时间

        if (transitionDrawable == null) {
            Drawable[] drawables = new Drawable[2];
            drawables[0] = lastDrawable;
            drawables[1] = new BitmapDrawable(resources, blurBitmap);
            transitionDrawable = new TransitionDrawable(drawables);
            transitionDrawable.setId(0, 0);
            transitionDrawable.setId(1, 1);
            transitionDrawable.setCrossFadeEnabled(true);
            imageView.setImageDrawable(transitionDrawable);
        } else {
            setDrawablesById(transitionDrawable, 0, lastDrawable);
            setDrawablesById(transitionDrawable, 1, new BitmapDrawable(resources, blurBitmap));
        }

        transitionDrawable.startTransition(TRANSITION_DURATION);
    }

    private static void setDrawablesById(TransitionDrawable transitionDrawable, int id, Drawable drawable) {
        transitionDrawable.setDrawableByLayerId(transitionDrawable.getId(id), drawable);
    }
}
