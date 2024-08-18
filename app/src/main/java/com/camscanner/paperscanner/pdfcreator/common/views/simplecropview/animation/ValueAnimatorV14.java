package com.camscanner.paperscanner.pdfcreator.common.views.simplecropview.animation;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.animation.Interpolator;

public class ValueAnimatorV14 implements SimpleValueAnimator, Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {
    private static final int DEFAULT_ANIMATION_DURATION = 150;
    private ValueAnimator animator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
    private SimpleValueAnimatorListener animatorListener = new SimpleValueAnimatorListener() {
        public void onAnimationStarted() {
        }

        public void onAnimationUpdated(float scale) {
        }

        public void onAnimationFinished() {
        }
    };

    public ValueAnimatorV14(Interpolator interpolator) {
        this.animator.addListener(this);
        this.animator.addUpdateListener(this);
        this.animator.setInterpolator(interpolator);
    }

    public void startAnimation(long duration) {
        if (duration >= 0) {
            this.animator.setDuration(duration);
        } else {
            this.animator.setDuration(150);
        }
        this.animator.start();
    }

    public void cancelAnimation() {
        this.animator.cancel();
    }

    public boolean isAnimationStarted() {
        return this.animator.isStarted();
    }

    public void addAnimatorListener(SimpleValueAnimatorListener animatorListener2) {
        if (animatorListener2 != null) {
            this.animatorListener = animatorListener2;
        }
    }

    public void onAnimationStart(Animator animation) {
        this.animatorListener.onAnimationStarted();
    }

    public void onAnimationEnd(Animator animation) {
        this.animatorListener.onAnimationFinished();
    }

    public void onAnimationCancel(Animator animation) {
        this.animatorListener.onAnimationFinished();
    }

    public void onAnimationRepeat(Animator animation) {
    }

    public void onAnimationUpdate(ValueAnimator animation) {
        this.animatorListener.onAnimationUpdated(animation.getAnimatedFraction());
    }
}
