package com.pengxh.androidx.lite.hub;

import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.TextSwitcher;

public class TextSwitcherHub {
    /**
     * 上下滚动消息动画扩展
     */
    public static void setAnimation(TextSwitcher textSwitcher) {
        TranslateAnimation translateIn = new TranslateAnimation(0f, 0f, 50f, 0f);
        AlphaAnimation alphaIn = new AlphaAnimation(0f, 1f);
        AnimationSet animatorSetIn = new AnimationSet(true);
        animatorSetIn.addAnimation(translateIn);
        animatorSetIn.addAnimation(alphaIn);
        animatorSetIn.setDuration(1000);
        textSwitcher.setInAnimation(animatorSetIn);

        TranslateAnimation translateOut = new TranslateAnimation(0f, 0f, 0f, -50f);
        AlphaAnimation alphaOut = new AlphaAnimation(1f, 0f);
        AnimationSet animatorSetOut = new AnimationSet(true);
        animatorSetOut.addAnimation(translateOut);
        animatorSetOut.addAnimation(alphaOut);
        animatorSetOut.setDuration(1000);
        textSwitcher.setOutAnimation(animatorSetOut);
    }
}
