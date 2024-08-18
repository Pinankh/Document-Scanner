package com.camscanner.paperscanner.pdfcreator.common;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class Animations {
    public static final int DEFAULT_DURATION = 350;
    public static final int LONG_LENGTH = 500;
    public static final int SHORT_LENGTH = 200;
    public static final int SHOW_FIRST_CAPTURE_MODE = 1200;

    public static void fadeOut(final View v, int duration) {
        v.setVisibility(0);
        alpha(v, duration, 1.0f, 0.0f, new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                v.setClickable(false);
                v.setVisibility(8);
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    public static void translate(View v, int duration, float fromX, float toX, float fromY, float toY, Animation.AnimationListener listener) {
        v.setClickable(false);
        Animation translateAnimation = new TranslateAnimation(fromX, toX, fromY, toY);
        translateAnimation.setDuration((long) duration);
        translateAnimation.setAnimationListener(listener);
        v.startAnimation(translateAnimation);
    }

    public static void alpha(View v, int duration, float fromAlpha, float toAlpha, Animation.AnimationListener listener) {
        v.setClickable(false);
        AlphaAnimation fadeAnimation = new AlphaAnimation(fromAlpha, toAlpha);
        fadeAnimation.setDuration((long) duration);
        fadeAnimation.setAnimationListener(listener);
        v.startAnimation(fadeAnimation);
    }
}
