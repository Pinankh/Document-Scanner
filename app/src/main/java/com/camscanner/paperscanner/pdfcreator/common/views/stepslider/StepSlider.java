package com.camscanner.paperscanner.pdfcreator.common.views.stepslider;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.camscanner.paperscanner.pdfcreator.R;

public class StepSlider extends View {
    private static final int CROWN_HEIGHT = 24;
    private static final int DESELECTED_TEXT_SIZE = 12;
    private static final int MARGIN_TEXT = 20;
    private static final int SELECTED_TEXT_SIZE = 12;
    private static final int THUMB_RADIUS_BG = 14;
    private static final int THUMB_RADIUS_FG = 10;
    private static final int TRACK_HEIGHT_BG = 4;
    private static final int TRACK_HEIGHT_FG = 3;
    private final int COLOR_BG = Color.parseColor("#ffffff");
    private final int COLOR_FG = Color.parseColor("#ffffff");
    private final int COLOR_TEXT = Color.parseColor("#000000");
    private final String[] DEF_ARRAY = {"Small", "Medium", "Regular", "Max"};
    private Paint mBgPaint;
    private int mCrownRes = 0;
    private int mDeSelectedTextColor;
    private int mDeSelectedTextSize;
    private int mDeltaRadius;
    private int mLastPosition;
    private OnSliderPositionChangeListener mListener;
    private int mMarginText;
    private int mNumStep = 4;
    private int mPremSrcHeight;
    private Drawable mPremSrcThumb;
    private int mPremSrcWidth;
    private int mSelectedTextColor;
    private int mSelectedTextSize;
    private int mSelectedThumbRadius;
    private Drawable mStepThumb;
    private int mStepThumbRadius;
    private int mTempLastPosition;
    private Paint mTextPaint;
    private Paint mThumbPaint;
    private int mTrackBgHeight;
    private int mTrackFgHeight;
    private RectF mTrackRect;
    private String[] strArray = this.DEF_ARRAY;
    private int[] textWidths;
    private float xPosition;

    public StepSlider(Context context) {
        super(context);
        int i = this.COLOR_TEXT;
        this.mSelectedTextColor = i;
        this.mDeSelectedTextColor = i;
        this.mPremSrcHeight = 0;
        this.mPremSrcWidth = 0;
        this.mDeltaRadius = 0;
        init((AttributeSet) null, 0);
    }

    public StepSlider(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        int i = this.COLOR_TEXT;
        this.mSelectedTextColor = i;
        this.mDeSelectedTextColor = i;
        this.mPremSrcHeight = 0;
        this.mPremSrcWidth = 0;
        this.mDeltaRadius = 0;
        init(attrs, 0);
    }

    public StepSlider(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        int i = this.COLOR_TEXT;
        this.mSelectedTextColor = i;
        this.mDeSelectedTextColor = i;
        this.mPremSrcHeight = 0;
        this.mPremSrcWidth = 0;
        this.mDeltaRadius = 0;
        init(attrs, defStyleAttr);
    }

    public void setStepCount(int steps) {
        this.mNumStep = steps;
        invalidate();
    }

    public void setThumbColor(@ColorInt int color) {
        invalidate();
    }

    public void setTrackColor(@ColorInt int color) {
        this.mThumbPaint.setColor(color);
        invalidate();
    }

    public void setThumbBgColor(@ColorInt int color) {
        this.mBgPaint.setColor(color);
        invalidate();
    }

    public void setTrackBgColor(@ColorInt int color) {
        invalidate();
    }

    public void setThumbRadiusPx(int radiusPx) {
        this.mSelectedThumbRadius = radiusPx;
        invalidate();
    }

    public void setThumbBgRadiusPx(int radiusPx) {
        this.mStepThumbRadius = radiusPx;
        invalidate();
    }

    public void setTrackHeightPx(int heightPx) {
        this.mTrackFgHeight = heightPx;
        invalidate();
    }

    public void setTrackBgHeightPx(int heightPx) {
        this.mTrackBgHeight = heightPx;
        invalidate();
    }

    public void setPosition(int position) {
        onPositionChanged(position, true, false);
    }

    public int getPosition() {
        return this.mLastPosition;
    }

    public void setOnSliderPositionChangeListener(OnSliderPositionChangeListener listener) {
        this.mListener = listener;
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        setFocusable(true);
        int colorDefaultBg = resolveAttrColor("colorControlNormal", this.COLOR_BG);
        int colorDefaultFg = resolveAttrColor("colorControlActivated", this.COLOR_FG);
        this.mBgPaint = new Paint(1);
        this.mBgPaint.setStyle(Paint.Style.FILL);
        this.mBgPaint.setColor(colorDefaultBg);
        this.mStepThumb = AppCompatResources.getDrawable(getContext(), R.drawable.ic_step_slider_thumb);
        this.mThumbPaint = new Paint(1);
        this.mThumbPaint.setStyle(Paint.Style.FILL);
        this.mThumbPaint.setColor(colorDefaultFg);
        float shadow = getResources().getDimension(R.dimen.thump_stepslider_shadow);
        this.mThumbPaint.setShadowLayer(shadow, shadow, shadow, -11184811);
        this.mTrackRect = new RectF();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        this.mStepThumbRadius = (int) TypedValue.applyDimension(1, 14.0f, dm);
        this.mCrownRes = (int) TypedValue.applyDimension(1, 24.0f, dm);
        this.mSelectedThumbRadius = (int) TypedValue.applyDimension(1, 10.0f, dm);
        this.mTrackBgHeight = (int) TypedValue.applyDimension(1, 4.0f, dm);
        this.mTrackFgHeight = (int) TypedValue.applyDimension(1, 3.0f, dm);
        this.mSelectedTextSize = (int) TypedValue.applyDimension(2, 12.0f, dm);
        this.mDeSelectedTextSize = (int) TypedValue.applyDimension(2, 12.0f, dm);
        this.mMarginText = (int) TypedValue.applyDimension(1, 20.0f, dm);
        if (attrs != null) {
            TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.StepSlider, defStyleAttr, 0);
            try {
                this.mBgPaint.setColor(arr.getColor(9, this.mBgPaint.getColor()));
                this.mLastPosition = arr.getInt(1, 0);
                this.mTempLastPosition = this.mLastPosition;
                this.mThumbPaint.setColor(arr.getColor(15, this.mThumbPaint.getColor()));
                this.mSelectedThumbRadius = arr.getDimensionPixelSize(12, this.mSelectedThumbRadius);
                this.mStepThumbRadius = arr.getDimensionPixelSize(10, this.mStepThumbRadius);
                this.mCrownRes = arr.getResourceId(2, 0);
                this.mTrackFgHeight = arr.getDimensionPixelSize(16, this.mTrackFgHeight);
                this.mTrackBgHeight = arr.getDimensionPixelSize(14, this.mTrackBgHeight);
                this.mSelectedTextSize = arr.getDimensionPixelSize(8, this.mSelectedTextSize);
                this.mDeSelectedTextSize = arr.getDimensionPixelSize(7, this.mDeSelectedTextSize);
                this.mMarginText = arr.getDimensionPixelSize(0, this.mMarginText);
                int strResId = arr.getResourceId(4, 0);
                this.strArray = strResId != 0 ? getResources().getStringArray(strResId) : this.DEF_ARRAY;
                this.mNumStep = arr.getInteger(3, this.mNumStep);
                this.mDeSelectedTextColor = arr.getColor(5, this.COLOR_TEXT);
                this.mSelectedTextColor = arr.getColor(6, this.COLOR_TEXT);
            } finally {
                arr.recycle();
            }
        }
        this.mPremSrcThumb = AppCompatResources.getDrawable(getContext(), this.mCrownRes);
        Drawable drawable = this.mPremSrcThumb;
        if (drawable != null) {
            this.mPremSrcWidth = drawable.getIntrinsicWidth();
            this.mPremSrcHeight = this.mPremSrcThumb.getIntrinsicHeight();
        }
        this.mTextPaint = new Paint(1);
        this.mTextPaint.setColor(this.mDeSelectedTextColor);
        this.mTextPaint.setTextSize((float) this.mDeSelectedTextSize);
        this.mDeltaRadius = this.mSelectedThumbRadius - this.mStepThumbRadius;
        this.textWidths = new int[this.strArray.length];
        int i = 0;
        while (true) {
            String[] strArr = this.strArray;
            if (i < strArr.length) {
                this.textWidths[i] = getTextWidth(this.mTextPaint, strArr[i]).width();
                i++;
            } else {
                post(new Runnable() {
                    public final void run() {
                        StepSlider.this.lambda$init$0$StepSlider();
                    }
                });
                return;
            }
        }
    }

    public /* synthetic */ void lambda$init$0$StepSlider() {
        updateXByPositionInit(this.mLastPosition);
    }

    @ColorInt
    private int resolveAttrColor(String attrName, @ColorInt int defaultColor) {
        int attrRes = getResources().getIdentifier(attrName, "attr", getContext().getPackageName());
        if (attrRes <= 0) {
            return defaultColor;
        }
        TypedValue value = new TypedValue();
        getContext().getTheme().resolveAttribute(attrRes, value, true);
        return getResources().getColor(value.resourceId);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int contentHeight = getPaddingTop() + (Math.max(this.mStepThumbRadius, this.mSelectedThumbRadius) * 2) + Math.max(this.mSelectedTextSize, this.mDeSelectedTextSize) + this.mMarginText + getPaddingBottom() + this.mPremSrcHeight;
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode != 1073741824) {
            height = Math.max(contentHeight, getSuggestedMinimumHeight());
        }
        setMeasuredDimension(width, height);
    }


    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        float x = event.getX();
        int leftPadding = getPaddingLeft();
        int rightPadding = getPaddingRight();
        int i = this.mDeltaRadius;
        if (i > 0) {
            leftPadding += i;
            rightPadding += i;
        }
        int stepSize = (((getWidth() - leftPadding) - rightPadding) - (this.mStepThumbRadius * 2)) / (this.mNumStep - 1);
        int actionMasked = event.getActionMasked();
        if (actionMasked != 0) {
            if (actionMasked != 1) {
                if (actionMasked != 2) {
                }
            }
            updatePosition(x, stepSize, true);
            return true;
        } else if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        int i2 = this.mStepThumbRadius;
        int leftEnd = leftPadding + i2;
        int rightEnd = i2 + leftPadding + ((this.mNumStep - 1) * stepSize);
        this.xPosition = x;
        float f = this.xPosition;
        if (f < ((float) leftEnd)) {
            this.xPosition = (float) leftEnd;
        } else if (f > ((float) rightEnd)) {
            this.xPosition = (float) rightEnd;
        }
        updatePosition(x, stepSize, false);
        return true;
    }

    private void updatePosition(float x, int stepSize, boolean commit) {
        int left = 0;
        int i = 0;
        while (i < this.mNumStep) {
            int right = getPaddingLeft() + this.mStepThumbRadius + (stepSize * i);
            if (left == 0) {
                left = right;
            }
            if (x <= ((float) left)) {
                onPositionChanged(i, commit, true);
                return;
            } else if (x <= ((float) right)) {
                onPositionChanged(x - ((float) left) < ((float) right) - x ? i - 1 : i, commit, true);
                return;
            } else if (i == this.mNumStep - 1) {
                onPositionChanged(i, commit, true);
                return;
            } else {
                left = right;
                i++;
            }
        }
    }

    public void onPositionChanged(int pos, boolean commitChange, boolean fromUser) {
        if (commitChange) {
            this.mLastPosition = pos;
            updateXByPosition(this.mLastPosition);
        }
        this.mTempLastPosition = pos;
        invalidate();
        if (fromUser && this.mListener != null) {
            if (commitChange || this.mLastPosition != pos) {
                this.mLastPosition = pos;
                this.mListener.onPositionChanged(pos, commitChange);
            }
        }
    }

    public void updateXByPosition(int position) {
        int leftPadding = getPaddingLeft();
        int rightPadding = getPaddingRight();
        int i = this.mDeltaRadius;
        if (i > 0) {
            leftPadding += i;
            rightPadding += i;
        }
        int i2 = this.mStepThumbRadius;
        this.xPosition = (float) ((((((getWidth() - leftPadding) - rightPadding) - (i2 * 2)) / (this.mNumStep - 1)) * position) + leftPadding + i2);
    }

    public void updateXByPositionInit(int position) {
        updateXByPosition(position);
        invalidate();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 21) {
            int i = this.mLastPosition;
            if (i > 0) {
                onPositionChanged(i - 1, true, true);
            }
            return true;
        } else if (keyCode != 22) {
            return super.onKeyDown(keyCode, event);
        } else {
            int i2 = this.mLastPosition;
            if (i2 < this.mNumStep - 1) {
                onPositionChanged(i2 + 1, true, true);
            }
            return true;
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mNumStep > 0) {
            drawSlider(canvas);
            drawSelectedThumb(canvas);
        }
    }

    private void drawSelectedThumb(Canvas canvas) {
        float f = this.xPosition;
        int paddingTop = getPaddingTop();
        int i = this.mSelectedThumbRadius;
        canvas.drawCircle(f, (float) (paddingTop + i + this.mPremSrcHeight), (float) i, this.mThumbPaint);
    }

    private void drawSlider(Canvas canvas) {
        Canvas canvas2 = canvas;
        int lastPos = this.mNumStep - 1;
        int leftPadding = getPaddingLeft();
        int rightPadding = getPaddingRight();
        int i = this.mDeltaRadius;
        if (i > 0) {
            leftPadding += i;
            rightPadding += i;
        }
        int left = leftPadding;
        int top = getPaddingTop();
        int i2 = this.mStepThumbRadius;
        int stepSize = (((getWidth() - leftPadding) - rightPadding) - (i2 * 2)) / (this.mNumStep - 1);
        int topOffset = this.mSelectedThumbRadius + this.mPremSrcHeight;
        int trackLeft = i2 + left;
        int trackTop = top + topOffset;
        int i3 = this.mTrackBgHeight;
        int trackBottom = trackTop + i3;
        float trackRadius = ((float) i3) * 0.5f;
        this.mTrackRect.set((float) trackLeft, (float) trackTop, (float) ((lastPos * stepSize) + trackLeft), (float) trackBottom);
        canvas2.drawRoundRect(this.mTrackRect, trackRadius, trackRadius, this.mBgPaint);
        int topOffsetCenterTrack = this.mSelectedThumbRadius + this.mPremSrcHeight + this.mTrackBgHeight;
        int i4 = this.mStepThumbRadius;
        int topOfStep = (top + topOffsetCenterTrack) - i4;
        int bottomOfStep = top + topOffsetCenterTrack + i4;
        int topOfText = top + topOffsetCenterTrack + i4 + this.mMarginText;
        int i5 = 0;
        while (i5 <= lastPos) {
            int leftOffset = stepSize * i5;
            int topOffsetCenterTrack2 = topOffsetCenterTrack;
            int topOffset2 = topOffset;
            int trackTop2 = trackTop;
            this.mStepThumb.setBounds(new Rect(left + leftOffset, topOfStep, left + leftOffset + (this.mStepThumbRadius * 2), bottomOfStep));
            this.mStepThumb.draw(canvas2);
            String stepText = this.strArray[i5];
            if (i5 == this.mTempLastPosition) {
                this.mTextPaint.setColor(this.mSelectedTextColor);
            } else {
                this.mTextPaint.setColor(this.mDeSelectedTextColor);
            }
            canvas2.drawText(stepText, ((float) ((left + leftOffset) + this.mStepThumbRadius)) - (((float) this.textWidths[i5]) / 2.0f), (float) topOfText, this.mTextPaint);
            i5++;
            topOffsetCenterTrack = topOffsetCenterTrack2;
            topOffset = topOffset2;
            trackTop = trackTop2;
            topOfStep = topOfStep;
        }
        int i6 = (lastPos * stepSize) + this.mStepThumbRadius;
        int i7 = this.mPremSrcWidth;
        int leftOffset2 = i6 - (i7 / 2);
        this.mPremSrcThumb.setBounds(new Rect(left + leftOffset2, top, left + leftOffset2 + i7, this.mPremSrcHeight + top));
        this.mPremSrcThumb.draw(canvas2);
    }

    private Rect getTextWidth(Paint paint, String text) {
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return bounds;
    }
}
