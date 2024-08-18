package io.codetail.animation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.view.View;
import io.codetail.animation.ViewRevealManager;

/* renamed from: io.codetail.animation.ViewAnimationUtils */
public final class ViewAnimationUtils {
    private static final boolean DEBUG = false;
    private static final boolean LOLLIPOP_PLUS = (Build.VERSION.SDK_INT >= 21);

    public static Animator createCircularReveal(View view, int centerX, int centerY, float startRadius, float endRadius) {
        return createCircularReveal(view, centerX, centerY, startRadius, endRadius, 1);
    }

    public static Animator createCircularReveal(View view, int centerX, int centerY, float startRadius, float endRadius, int layerType) {
        if (view.getParent() instanceof RevealViewGroup) {
            ViewRevealManager rm = ((RevealViewGroup) view.getParent()).getViewRevealManager();
            if (!rm.hasCustomerRevealAnimator() && LOLLIPOP_PLUS) {
                return android.view.ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius);
            }
            ViewRevealManager.RevealValues revealValues = new ViewRevealManager.RevealValues(view, centerX, centerY, startRadius, endRadius);
            ObjectAnimator animator = rm.createAnimator(revealValues);
            if (layerType != view.getLayerType()) {
                animator.addListener(new ViewRevealManager.ChangeViewLayerTypeAdapter(revealValues, layerType));
            }
            return animator;
        }
        throw new IllegalArgumentException("Parent must be instance of RevealViewGroup");
    }
}
