package io.codetail.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.os.Build;
import android.util.Property;
import android.view.View;
import java.util.HashMap;
import java.util.Map;

/* renamed from: io.codetail.animation.ViewRevealManager */
public class ViewRevealManager {
    public static final ClipRadiusProperty REVEAL = new ClipRadiusProperty();
    /* access modifiers changed from: private */
    public Map<View, RevealValues> targets = new HashMap();

    /* access modifiers changed from: protected */
    public ObjectAnimator createAnimator(RevealValues data) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(data, REVEAL, new float[]{data.startRadius, data.endRadius});
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animation) {
                ViewRevealManager.getValues(animation).clip(true);
            }

            public void onAnimationEnd(Animator animation) {
                RevealValues values = ViewRevealManager.getValues(animation);
                values.clip(false);
                ViewRevealManager.this.targets.remove(values.target());
            }
        });
        this.targets.put(data.target(), data);
        return animator;
    }

    /* access modifiers changed from: private */
    public static RevealValues getValues(Animator animator) {
        return (RevealValues) ((ObjectAnimator) animator).getTarget();
    }

    public final Map<View, RevealValues> getTargets() {
        return this.targets;
    }

    /* access modifiers changed from: protected */
    public boolean hasCustomerRevealAnimator() {
        return false;
    }

    public boolean isClipped(View child) {
        RevealValues data = this.targets.get(child);
        return data != null && data.isClipping();
    }

    public boolean transform(Canvas canvas, View child) {
        RevealValues revealData = this.targets.get(child);
        return revealData != null && revealData.applyTransformation(canvas, child);
    }

    /* renamed from: io.codetail.animation.ViewRevealManager$RevealValues */
    public static final class RevealValues {
        private static final Paint debugPaint = new Paint(1);
        final int centerX;
        final int centerY;
        boolean clipping;
        final float endRadius;

        /* renamed from: op */
        Region.Op f14461op = Region.Op.REPLACE;
        Path path = new Path();
        float radius;
        final float startRadius;
        View target;

        static {
            debugPaint.setColor(-16711936);
            debugPaint.setStyle(Paint.Style.FILL);
            debugPaint.setStrokeWidth(2.0f);
        }

        public RevealValues(View target2, int centerX2, int centerY2, float startRadius2, float endRadius2) {
            this.target = target2;
            this.centerX = centerX2;
            this.centerY = centerY2;
            this.startRadius = startRadius2;
            this.endRadius = endRadius2;
        }

        public void radius(float radius2) {
            this.radius = radius2;
        }

        public float radius() {
            return this.radius;
        }

        public View target() {
            return this.target;
        }

        public void clip(boolean clipping2) {
            this.clipping = clipping2;
        }

        public boolean isClipping() {
            return this.clipping;
        }

        /* renamed from: op */
        public Region.Op mo61044op() {
            return this.f14461op;
        }

        /* renamed from: op */
        public void mo61045op(Region.Op op) {
            this.f14461op = op;
        }

        /* access modifiers changed from: package-private */
        public boolean applyTransformation(Canvas canvas, View child) {
            if (child != this.target || !this.clipping) {
                return false;
            }
            this.path.reset();
            this.path.addCircle(child.getX() + ((float) this.centerX), child.getY() + ((float) this.centerY), this.radius, Path.Direction.CW);
            canvas.clipPath(this.path, this.f14461op);
            if (Build.VERSION.SDK_INT < 21) {
                return true;
            }
            child.invalidateOutline();
            return true;
        }
    }

    /* renamed from: io.codetail.animation.ViewRevealManager$ClipRadiusProperty */
    private static final class ClipRadiusProperty extends Property<RevealValues, Float> {
        ClipRadiusProperty() {
            super(Float.class, "supportCircularReveal");
        }

        public void set(RevealValues data, Float value) {
            data.radius(value.floatValue());
            data.target().invalidate();
        }

        public Float get(RevealValues v) {
            return Float.valueOf(v.radius());
        }
    }

    /* renamed from: io.codetail.animation.ViewRevealManager$ChangeViewLayerTypeAdapter */
    static class ChangeViewLayerTypeAdapter extends AnimatorListenerAdapter {
        private int featuredLayerType;
        private int originalLayerType;
        private RevealValues viewData;

        ChangeViewLayerTypeAdapter(RevealValues viewData2, int layerType) {
            this.viewData = viewData2;
            this.featuredLayerType = layerType;
            this.originalLayerType = viewData2.target.getLayerType();
        }

        public void onAnimationStart(Animator animation) {
            this.viewData.target().setLayerType(this.featuredLayerType, (Paint) null);
        }

        public void onAnimationCancel(Animator animation) {
            this.viewData.target().setLayerType(this.originalLayerType, (Paint) null);
        }

        public void onAnimationEnd(Animator animation) {
            this.viewData.target().setLayerType(this.originalLayerType, (Paint) null);
        }
    }
}
