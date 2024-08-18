package com.camscanner.paperscanner.pdfcreator.view.element;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import com.camscanner.paperscanner.pdfcreator.R;

public class BubbleDrawable extends Drawable {
    public static final int CENTER = 1;
    public static final int LEFT = 0;
    public static final int RIGHT = 2;
    private Context context;
    private int mBoxHeight;
    private Rect mBoxPadding = new Rect();
    private RectF mBoxRect;
    private int mBoxWidth;
    private int mColor;
    private float mCornerRad;
    private Paint mPaint;
    private Path mPointer;
    private int mPointerAlignment;
    private int mPointerHeight;
    private int mPointerWidth;

    public BubbleDrawable(Context context2) {
        this.context = context2;
        initBubble();
    }

    public void setPadding(int left, int top, int right, int bottom) {
        Rect rect = this.mBoxPadding;
        rect.left = left;
        rect.top = top;
        rect.right = right;
        rect.bottom = bottom;
    }

    public void setCornerRadius(float cornerRad) {
        this.mCornerRad = dipToPixels(this.context, cornerRad);
    }

    public void setPointerAlignment(int pointerAlignment) {
        if (pointerAlignment < 0 || pointerAlignment > 3) {
            Log.e("BubbleDrawable", "Invalid pointerAlignment argument");
        } else {
            this.mPointerAlignment = pointerAlignment;
        }
    }

    public void setPointerWidth(int pointerWidth) {
        this.mPointerWidth = pointerWidth;
    }

    public void setPointerHeight(int pointerHeight) {
        this.mPointerHeight = pointerHeight;
    }

    private void initBubble() {
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mColor = this.context.getResources().getColor(R.color.colorCamera11);
        this.mPaint.setColor(this.mColor);
        this.mCornerRad = 0.0f;
        setPointerWidth(40);
        setPointerHeight(20);
    }

    private void updatePointerPath() {
        this.mPointer = new Path();
        this.mPointer.setFillType(Path.FillType.EVEN_ODD);
        this.mPointer.moveTo((float) ((this.mBoxWidth / 2) - (this.mPointerWidth / 2)), (float) this.mBoxHeight);
        this.mPointer.lineTo((float) ((this.mBoxWidth / 2) + (this.mPointerWidth / 2)), (float) this.mBoxHeight);
        this.mPointer.lineTo((float) (this.mBoxWidth / 2), (float) (this.mBoxHeight + this.mPointerHeight));
        this.mPointer.lineTo((float) ((this.mBoxWidth / 2) - (this.mPointerWidth / 2)), (float) this.mBoxHeight);
        this.mPointer.close();
    }

    private float pointerHorizontalStart() {
        int i = this.mPointerAlignment;
        if (i == 0) {
            return this.mCornerRad;
        }
        if (i == 1) {
            return (float) ((this.mBoxWidth / 2) - (this.mPointerWidth / 2));
        }
        if (i != 2) {
            return 0.0f;
        }
        return (((float) this.mBoxWidth) - this.mCornerRad) - ((float) this.mPointerWidth);
    }

    public void draw(Canvas canvas) {
        this.mBoxRect = new RectF(0.0f, 0.0f, (float) this.mBoxWidth, (float) this.mBoxHeight);
        RectF rectF = this.mBoxRect;
        float f = this.mCornerRad;
        canvas.drawRoundRect(rectF, f, f, this.mPaint);
        updatePointerPath();
        canvas.drawPath(this.mPointer, this.mPaint);
    }

    public int getOpacity() {
        return -1;
    }

    public void setAlpha(int alpha) {
    }

    public void setColorFilter(ColorFilter cf) {
    }

    public boolean getPadding(Rect padding) {
        padding.set(this.mBoxPadding);
        padding.bottom += this.mPointerHeight;
        return true;
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect bounds) {
        this.mBoxWidth = bounds.width();
        this.mBoxHeight = getBounds().height() - this.mPointerHeight;
        super.onBoundsChange(bounds);
    }

    public static float dipToPixels(Context context2, float dipValue) {
        return TypedValue.applyDimension(1, dipValue, context2.getResources().getDisplayMetrics());
    }
}
