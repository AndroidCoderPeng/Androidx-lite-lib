package com.pengxh.androidx.lite.kit;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;

public class ViewKit {
    public static void expand(View view, Long duration) {
        // 先测量真实高度
        view.measure(
                View.MeasureSpec.makeMeasureSpec(((View) view.getParent()).getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        int targetHeight = view.getMeasuredHeight();
        view.getLayoutParams().height = 0;
        view.setVisibility(View.VISIBLE);
        view.setAlpha(0f);

        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, targetHeight);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                view.getLayoutParams().height = (int) valueAnimator.getAnimatedValue();
                view.requestLayout();
                view.setAlpha(valueAnimator.getAnimatedFraction());
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                view.setAlpha(1f);
            }
        });
        valueAnimator.start();
    }

    public static void collapse(View view, Long duration) {
        int initialHeight = view.getMeasuredHeight();

        ValueAnimator valueAnimator = ValueAnimator.ofInt(initialHeight, 0);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                view.getLayoutParams().height = (int) valueAnimator.getAnimatedValue();
                view.requestLayout();
                view.setAlpha(1f - valueAnimator.getAnimatedFraction());
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                view.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                view.setAlpha(1f);
            }
        });
        valueAnimator.start();
    }
}
