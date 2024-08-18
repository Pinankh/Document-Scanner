package com.camscanner.paperscanner.pdfcreator.common.views.simplecropview.animation;

public interface SimpleValueAnimator {
    void addAnimatorListener(SimpleValueAnimatorListener simpleValueAnimatorListener);

    void cancelAnimation();

    boolean isAnimationStarted();

    void startAnimation(long j);
}
