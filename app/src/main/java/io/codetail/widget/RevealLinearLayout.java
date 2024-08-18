package io.codetail.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import io.codetail.animation.RevealViewGroup;
import io.codetail.animation.ViewRevealManager;

/* renamed from: io.codetail.widget.RevealLinearLayout */
public class RevealLinearLayout extends LinearLayout implements RevealViewGroup {
    private ViewRevealManager manager;

    public RevealLinearLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public RevealLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RevealLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        this.manager = new ViewRevealManager();
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        try {
            canvas.save();
            this.manager.transform(canvas, child);
            return super.drawChild(canvas, child, drawingTime);
        } finally {
            canvas.restore();
        }
    }

    public ViewRevealManager getViewRevealManager() {
        return this.manager;
    }
}
