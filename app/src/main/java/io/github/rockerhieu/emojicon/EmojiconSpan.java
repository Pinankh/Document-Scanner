package io.github.rockerhieu.emojicon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.DynamicDrawableSpan;
import java.lang.ref.WeakReference;

/* renamed from: io.github.rockerhieu.emojicon.EmojiconSpan */
class EmojiconSpan extends DynamicDrawableSpan {
    private final Context mContext;
    private Drawable mDrawable;
    private WeakReference<Drawable> mDrawableRef;
    private int mHeight;
    private final int mResourceId;
    private final int mSize;
    private final int mTextSize;
    private int mTop;
    private int mWidth;

    public EmojiconSpan(Context context, int resourceId, int size, int alignment, int textSize) {
        super(alignment);
        this.mContext = context;
        this.mResourceId = resourceId;
        this.mSize = size;
        this.mHeight = size;
        this.mWidth = size;
        this.mTextSize = textSize;
    }

    public Drawable getDrawable() {
        if (this.mDrawable == null) {
            try {
                this.mDrawable = this.mContext.getResources().getDrawable(this.mResourceId);
                this.mHeight = this.mSize;
                this.mWidth = (this.mHeight * this.mDrawable.getIntrinsicWidth()) / this.mDrawable.getIntrinsicHeight();
                this.mTop = (this.mTextSize - this.mHeight) / 2;
                this.mDrawable.setBounds(0, this.mTop, this.mWidth, this.mTop + this.mHeight);
            } catch (Exception e) {
            }
        }
        return this.mDrawable;
    }

    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        Drawable b = getCachedDrawable();
        canvas.save();
        int transY = bottom - b.getBounds().bottom;
        if (this.mVerticalAlignment == 1) {
            transY = ((((bottom - top) / 2) + top) - ((b.getBounds().bottom - b.getBounds().top) / 2)) - this.mTop;
        }
        canvas.translate(x, (float) transY);
        b.draw(canvas);
        canvas.restore();
    }

    private Drawable getCachedDrawable() {
        WeakReference<Drawable> weakReference = this.mDrawableRef;
        if (weakReference == null || weakReference.get() == null) {
            this.mDrawableRef = new WeakReference<>(getDrawable());
        }
        return (Drawable) this.mDrawableRef.get();
    }
}
