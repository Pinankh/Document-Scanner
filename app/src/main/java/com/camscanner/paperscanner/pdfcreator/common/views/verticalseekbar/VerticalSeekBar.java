package com.camscanner.paperscanner.pdfcreator.common.views.verticalseekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.camscanner.paperscanner.pdfcreator.R;

public class VerticalSeekBar extends View {
    private Drawable backgroundDark;
    private Drawable backgroundLight;
    private int colorDoneDistance;
    private int colorRemainingDistanceDark;
    private int colorRemainingDistanceLight;
    private boolean darkMode = false;
    private OnSeekBarChangeListener listener;
    private Paint paintDoneDistance;
    private Paint paintRemainingDistance;
    private Paint paintThumb;
    private int progress = 50;
    private float propMarginBottomPercent = 0.08f;
    private float propRadiusPercent = 0.65f;
    private Drawable propertyIcon;
    private int thumbColor;
    private int thumbRadius = 0;
    private float thumbRadiusPercent;
    private float trackMarginBottomPercent = 0.24f;
    private float trackMarginTopPercent = 0.14f;
    private int widthTrack;
    private float yPosition;

    public interface OnSeekBarChangeListener {
        void onProgressChanged(int i, boolean z);

        void onTrackEnded(int i);

        void onTrackStarted(int i);
    }

    public VerticalSeekBar(Context context) {
        super(context);
        init((AttributeSet) null, 0);
    }

    public VerticalSeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public VerticalSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener listener2) {
        this.listener = listener2;
    }

    public void setProgress(int progress2) {
        this.progress = progress2;
        OnSeekBarChangeListener onSeekBarChangeListener = this.listener;
        if (onSeekBarChangeListener != null) {
            onSeekBarChangeListener.onProgressChanged(progress2, false);
        }
        updateYByProgress();
        invalidate();
    }

    public int getProgress() {
        return this.progress;
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        this.colorDoneDistance = Color.parseColor("#ffffff");
        this.colorRemainingDistanceLight = Color.parseColor("#ffffff");
        this.colorRemainingDistanceDark = Color.parseColor("#ffffff");
        this.thumbColor = Color.parseColor("#ffffff");
        int shadow = (int) TypedValue.applyDimension(1, 3.0f, dm);
        this.widthTrack = (int) TypedValue.applyDimension(1, 4.0f, dm);
        int propertyIconResId = R.drawable.ic_prop_brightness;
        int propertyBackgroundLightResId = R.drawable.ic_edit_bg_light_left;
        int propertyBackgroundDarkResId = R.drawable.ic_edit_bg_light_left;
        if (attrs != null) {
            TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.VerticalSeekBar, defStyleAttr, 0);
            try {
                propertyIconResId = arr.getResourceId(11, R.drawable.ic_prop_brightness);
                propertyBackgroundLightResId = arr.getResourceId(1, R.drawable.ic_edit_bg_light_left);
                propertyBackgroundDarkResId = arr.getResourceId(0, R.drawable.ic_edit_bg_light_left);
                this.darkMode = arr.getBoolean(5, false);
                this.colorDoneDistance = arr.getColor(2, this.colorDoneDistance);
                this.colorRemainingDistanceLight = arr.getColor(4, this.colorRemainingDistanceLight);
                this.colorRemainingDistanceDark = arr.getColor(3, this.colorRemainingDistanceDark);
                this.thumbColor = arr.getColor(12, this.thumbColor);
                this.thumbRadiusPercent = arr.getFloat(13, this.thumbRadiusPercent);
                this.trackMarginTopPercent = arr.getFloat(8, this.trackMarginTopPercent);
                this.trackMarginBottomPercent = arr.getFloat(6, this.trackMarginBottomPercent);
                this.propMarginBottomPercent = arr.getFloat(7, this.propMarginBottomPercent);
                this.propRadiusPercent = arr.getFloat(10, this.propRadiusPercent);
                shadow = arr.getDimensionPixelSize(14, shadow);
                this.widthTrack = arr.getDimensionPixelSize(15, this.widthTrack);
                this.progress = arr.getInt(9, this.progress);
            } finally {
                arr.recycle();
            }
        }
        this.propertyIcon = AppCompatResources.getDrawable(getContext(), propertyIconResId);
        this.backgroundLight = AppCompatResources.getDrawable(getContext(), propertyBackgroundLightResId);
        this.backgroundDark = AppCompatResources.getDrawable(getContext(), propertyBackgroundDarkResId);
        setBackground(this.darkMode ? this.backgroundDark : this.backgroundLight);
        this.paintDoneDistance = new Paint(1);
        this.paintDoneDistance.setColor(this.colorDoneDistance);
        this.paintDoneDistance.setStyle(Paint.Style.FILL);
        this.paintRemainingDistance = new Paint(1);
        this.paintRemainingDistance.setColor(this.darkMode ? this.colorRemainingDistanceDark : this.colorRemainingDistanceLight);
        this.paintDoneDistance.setStyle(Paint.Style.FILL);
        this.paintThumb = new Paint(1);
        this.paintThumb.setColor(this.thumbColor);
        this.paintThumb.setStyle(Paint.Style.FILL);
        this.paintThumb.setShadowLayer((float) shadow, (float) shadow, (float) shadow, -11184811);
    }

    public void setDarkMode(boolean darkMode2) {
        this.darkMode = darkMode2;
        setBackground(darkMode2 ? this.backgroundDark : this.backgroundLight);
        this.paintRemainingDistance.setColor(darkMode2 ? this.colorRemainingDistanceDark : this.colorRemainingDistanceLight);
        invalidate();
    }

    public boolean isDarkMode() {
        return this.darkMode;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    private int getTrackTopEdge() {
        return getPaddingTop() + getTrackMarginTop();
    }

    private int getTrackBottomEdge() {
        return getPaddingBottom() + getTrackMarginBottom();
    }


    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        float y = event.getY();
        int topEdge = getTrackTopEdge();
        int bottomEdge = getHeight() - getTrackBottomEdge();
        int actionMasked = event.getActionMasked();
        if (actionMasked != 0) {
            if (actionMasked != 1) {
                if (actionMasked == 2) {
                    onMove(y, topEdge, bottomEdge);
                }
            }
            onUp(y, topEdge, bottomEdge);
        } else if (!isInsideTrack(y, (float) topEdge, (float) bottomEdge)) {
            return false;
        } else {
            onDown(y, topEdge, bottomEdge);
        }
        return true;
    }

    private void onDown(float y, int topEdge, int bottomEdge) {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        updateProgress(y, topEdge, bottomEdge);
        OnSeekBarChangeListener onSeekBarChangeListener = this.listener;
        if (onSeekBarChangeListener != null) {
            onSeekBarChangeListener.onTrackStarted(this.progress);
        }
    }

    private void onMove(float y, int topEdge, int bottomEdge) {
        updateProgress(y, topEdge, bottomEdge);
        OnSeekBarChangeListener onSeekBarChangeListener = this.listener;
        if (onSeekBarChangeListener != null) {
            onSeekBarChangeListener.onProgressChanged(this.progress, true);
        }
    }

    private void onUp(float y, int topEdge, int bottomEdge) {
        updateProgress(y, topEdge, bottomEdge);
        OnSeekBarChangeListener onSeekBarChangeListener = this.listener;
        if (onSeekBarChangeListener != null) {
            onSeekBarChangeListener.onTrackEnded(this.progress);
        }
    }

    private boolean isInsideTrack(float y, float top, float bottom) {
        return y > top && y < bottom;
    }

    private void updateProgress(float y, int topEdge, int bottomEdge) {
        if (y < ((float) topEdge)) {
            y = (float) topEdge;
        } else if (y > ((float) bottomEdge)) {
            y = (float) bottomEdge;
        }
        int trackHeight = bottomEdge - topEdge;
        this.progress = Math.round((((((float) trackHeight) - y) + ((float) topEdge)) / ((float) trackHeight)) * 100.0f);
        this.yPosition = y;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        super.onDraw(canvas);
        int center = ((getWidth() - getPaddingLeft()) - getPaddingRight()) / 2;
        float trackRadius = ((float) this.widthTrack) * 0.5f;
        float trackTop = (float) getTrackTopEdge();
        drawProperty(canvas2, center);
        drawTrack(canvas, ((float) center) - trackRadius, trackTop, ((float) center) + trackRadius, (float) (getHeight() - getTrackBottomEdge()), this.yPosition, trackRadius);
        drawThumb(canvas2, (float) center, this.yPosition);
    }

    private void updateYByProgress() {
        float trackTop = (float) getTrackTopEdge();
        this.yPosition = (((((float) (getHeight() - getTrackBottomEdge())) - trackTop) * ((float) (100 - this.progress))) / 100.0f) + trackTop;
    }

    private void drawProperty(Canvas canvas, int center) {
        int bottom = (getHeight() - getPaddingBottom()) - getPropMarginBottom();
        int propertyRadius = getPropRadius();
        this.propertyIcon.setBounds(new Rect(center - propertyRadius, bottom - (propertyRadius * 2), center + propertyRadius, bottom));
        this.propertyIcon.draw(canvas);
    }

    private void drawTrack(Canvas canvas, float trackLeft, float trackTop, float trackRight, float trackBottom, float trackProgress, float trackRadius) {
        float f = trackLeft;
        float f2 = trackRight;
        float f3 = trackRadius;
        float f4 = trackRadius;
        canvas.drawPath(RoundedRect(f, trackTop, f2, trackProgress, f3, f4, true, true, false, false), this.paintRemainingDistance);
        canvas.drawPath(RoundedRect(f, trackProgress, f2, trackBottom, f3, f4, false, false, true, true), this.paintDoneDistance);
    }

    private void drawThumb(Canvas canvas, float x, float y) {
        canvas.drawCircle(x, y, (float) getThumbRadius(), this.paintThumb);
    }

    private int getTrackMarginTop() {
        return (int) (((float) getHeight()) * this.trackMarginTopPercent);
    }

    private int getTrackMarginBottom() {
        return (int) (((float) getHeight()) * this.trackMarginBottomPercent);
    }

    private int getPropMarginBottom() {
        return (int) (((float) getHeight()) * this.propMarginBottomPercent);
    }

    private int getPropRadius() {
        return (int) (((float) getWidth()) * this.propRadiusPercent);
    }

    private int getThumbRadius() {
        if (this.thumbRadius == 0) {
            this.thumbRadius = (int) (((float) getWidth()) * this.thumbRadiusPercent);
        }
        return this.thumbRadius;
    }

    private Path RoundedRect(float left, float top, float right, float bottom, float rx, float ry, boolean tl, boolean tr, boolean br, boolean bl) {
        float f = right;
        Path path = new Path();
        float rx2 = rx < 0.0f ? 0.0f : rx;
        float ry2 = ry < 0.0f ? 0.0f : ry;
        float width = f - left;
        float height = bottom - top;
        if (rx2 > width / 2.0f) {
            rx2 = width / 2.0f;
        }
        if (ry2 > height / 2.0f) {
            ry2 = height / 2.0f;
        }
        float widthMinusCorners = width - (rx2 * 2.0f);
        float heightMinusCorners = height - (2.0f * ry2);
        path.moveTo(right, top + ry2);
        if (tr) {
            path.rQuadTo(0.0f, -ry2, -rx2, -ry2);
        } else {
            path.rLineTo(0.0f, -ry2);
            path.rLineTo(-rx2, 0.0f);
        }
        path.rLineTo(-widthMinusCorners, 0.0f);
        if (tl) {
            path.rQuadTo(-rx2, 0.0f, -rx2, ry2);
        } else {
            path.rLineTo(-rx2, 0.0f);
            path.rLineTo(0.0f, ry2);
        }
        path.rLineTo(0.0f, heightMinusCorners);
        if (bl) {
            path.rQuadTo(0.0f, ry2, rx2, ry2);
        } else {
            path.rLineTo(0.0f, ry2);
            path.rLineTo(rx2, 0.0f);
        }
        path.rLineTo(widthMinusCorners, 0.0f);
        if (br) {
            path.rQuadTo(rx2, 0.0f, rx2, -ry2);
        } else {
            path.rLineTo(rx2, 0.0f);
            path.rLineTo(0.0f, -ry2);
        }
        path.rLineTo(0.0f, -heightMinusCorners);
        path.close();
        return path;
    }
}
