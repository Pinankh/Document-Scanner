package com.camscanner.paperscanner.pdfcreator.view.camera;

import androidx.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ResizeAnimation extends Animation {
    final int mFinalLength;
    final boolean mIsPortrait;
    final int mStartLength;
    final View mView;

    public ResizeAnimation(@NonNull View view, ImageParameters imageParameters) {
        this.mIsPortrait = imageParameters.isPortrait();
        this.mView = view;
        this.mStartLength = this.mIsPortrait ? this.mView.getHeight() : this.mView.getWidth();
        this.mFinalLength = imageParameters.getAnimationParameter();
    }

    /* access modifiers changed from: protected */
    public void applyTransformation(float interpolatedTime, Transformation t) {
        int i = this.mStartLength;
        int newLength = (int) (((float) i) + (((float) (this.mFinalLength - i)) * interpolatedTime));
        if (this.mIsPortrait) {
            this.mView.getLayoutParams().height = newLength;
        } else {
            this.mView.getLayoutParams().width = newLength;
        }
        if (newLength <= 0) {
            this.mView.setVisibility(4);
        } else {
            this.mView.requestLayout();
        }
    }

    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    public boolean willChangeBounds() {
        return true;
    }
}
