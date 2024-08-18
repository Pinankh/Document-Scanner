package io.codetail.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import io.codetail.animation.RevealViewGroup;
import io.codetail.animation.ViewRevealManager;

public class RevealFrameLayout extends FrameLayout implements RevealViewGroup {
    private ViewRevealManager manager;

    public RevealFrameLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public RevealFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RevealFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.manager = new ViewRevealManager();
    }

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
