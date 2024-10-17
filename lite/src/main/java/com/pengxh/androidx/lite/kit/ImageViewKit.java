package com.pengxh.androidx.lite.kit;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.widget.ImageView;

public class ImageViewKit {
    public static void switchBackground(ImageView imageView, Bitmap blurBitmap) {
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

        if (transitionDrawable == null) {
            Drawable[] drawables = new Drawable[2];
            drawables[0] = lastDrawable;
            drawables[1] = new BitmapDrawable(imageView.getResources(), blurBitmap);
            transitionDrawable = new TransitionDrawable(drawables);
            transitionDrawable.setId(0, 0);
            transitionDrawable.setId(1, 1);
            transitionDrawable.setCrossFadeEnabled(true);
            imageView.setImageDrawable(transitionDrawable);
        } else {
            transitionDrawable.setDrawableByLayerId(transitionDrawable.getId(0), lastDrawable);
            transitionDrawable.setDrawableByLayerId(
                    transitionDrawable.getId(1),
                    new BitmapDrawable(imageView.getResources(), blurBitmap)
            );
        }
        transitionDrawable.startTransition(1000);
    }
}
