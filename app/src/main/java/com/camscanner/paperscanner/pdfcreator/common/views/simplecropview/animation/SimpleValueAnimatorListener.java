package com.camscanner.paperscanner.pdfcreator.common.views.simplecropview.animation;

public interface SimpleValueAnimatorListener {
    void onAnimationFinished();

    void onAnimationStarted();

    void onAnimationUpdated(float f);
}
