package co.lujun.androidtagview;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;

public class TagView extends View {
    private float bdDistance;
    private float fontH;
    private float fontW;

    public boolean isExecLongClick;

    public boolean isMoved;

    public boolean isUp;
    private boolean isViewClickable;
    private String mAbstractText;
    private int mBackgroundColor;
    private Bitmap mBitmapImage;
    private int mBorderColor;
    private float mBorderRadius;
    private float mBorderWidth;
    private float mCheckAreaPadding;
    private float mCheckAreaWidth;
    private int mCheckColor;
    private float mCheckLineWidth;
    private float mCrossAreaPadding;
    private float mCrossAreaWidth;
    private int mCrossColor;
    private float mCrossLineWidth;
    private boolean mEnableCheck;
    private boolean mEnableCross;
    private int mHorizontalPadding;
    private int mLastX;
    private int mLastY;
    private Runnable mLongClickHandle = new Runnable() {
        public void run() {
            if (!TagView.this.isMoved && !TagView.this.isUp && ((TagContainerLayout) TagView.this.getParent()).getTagViewState() == 0) {
                boolean unused = TagView.this.isExecLongClick = true;
                TagView.this.mOnTagClickListener.onTagLongClick(((Integer) TagView.this.getTag()).intValue(), TagView.this.mTagItem);
            }
        }
    };
    private int mLongPressTime = 500;
    private int mMoveSlop = 5;
    /* access modifiers changed from: private */
    public OnTagClickListener mOnTagClickListener;
    private String mOriginText;
    private Paint mPaint;
    private Path mPath;
    private RectF mRectF;
    private int mRippleAlpha;
    private int mRippleColor;
    private int mRippleDuration = 1000;
    private Paint mRipplePaint;
    /* access modifiers changed from: private */
    public float mRippleRadius;
    private ValueAnimator mRippleValueAnimator;
    private int mSlopThreshold = 4;
    TagItem mTagItem;
    private int mTagMaxLength;
    private boolean mTagSupportLettersRTL = false;
    private int mTextColor;
    private int mTextDirection = 3;
    private float mTextSize;
    private float mTouchX;
    private float mTouchY;
    private Typeface mTypeface;
    private int mVerticalPadding;
    private boolean unSupportedClipPath = false;

    /* renamed from: co.lujun.androidtagview.TagView$OnTagClickListener */
    public interface OnTagClickListener {
        void onTagClick(int i, TagItem tagItem);

        void onTagCrossClick(int i);

        void onTagLongClick(int i, TagItem tagItem);
    }

    public TagView(Context context, TagItem tagItem) {
        super(context);
        init(context, tagItem);
    }

    public TagView(Context context, TagItem tagItem, int defaultImageID) {
        super(context);
        init(context, tagItem);
        this.mBitmapImage = BitmapFactory.decodeResource(getResources(), defaultImageID);
    }

    private boolean isClickCrossArea(MotionEvent event) {
        if (this.mTextDirection == 4) {
            if (event.getX() <= this.mCrossAreaWidth) {
                return true;
            }
            return false;
        } else if (event.getX() >= ((float) getWidth()) - this.mCrossAreaWidth) {
            return true;
        } else {
            return false;
        }
    }

    public void setTagItem(TagItem tagItem) {
        this.mTagItem = tagItem;
        invalidate();
    }

    private void onDealText() {
        String str;
        if (!TextUtils.isEmpty(this.mOriginText)) {
            if (this.mOriginText.length() <= this.mTagMaxLength) {
                str = this.mOriginText;
            } else {
                str = this.mOriginText.substring(0, this.mTagMaxLength - 3) + "...";
            }
            this.mAbstractText = str;
        } else {
            this.mAbstractText = "";
        }
        this.mPaint.setTypeface(this.mTypeface);
        this.mPaint.setTextSize(this.mTextSize);
        Paint.FontMetrics fontMetrics = this.mPaint.getFontMetrics();
        this.fontH = fontMetrics.descent - fontMetrics.ascent;
        if (this.mTextDirection == 4) {
            this.fontW = 0.0f;
            char[] charArray = this.mAbstractText.toCharArray();
            int length = charArray.length;
            for (int i = 0; i < length; i++) {
                this.fontW += this.mPaint.measureText(String.valueOf(charArray[i]));
            }
            return;
        }
        this.fontW = this.mPaint.measureText(this.mAbstractText);
    }

    private void init(Context context, TagItem tagItem) {
        this.mPaint = new Paint(1);
        this.mRipplePaint = new Paint(1);
        this.mRipplePaint.setStyle(Paint.Style.FILL);
        this.mRectF = new RectF();
        this.mPath = new Path();
        this.mOriginText = tagItem.tagName == null ? "" : tagItem.tagName;
        this.mMoveSlop = (int) Utils.dp2px(context, (float) this.mMoveSlop);
        this.mSlopThreshold = (int) Utils.dp2px(context, (float) this.mSlopThreshold);
        this.mTagItem = tagItem;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = (this.mVerticalPadding * 2) + ((int) this.fontH);
        int i = 0;
        int i2 = (this.mHorizontalPadding * 2) + ((int) this.fontW) + ((isEnableCross() || isEnableCheck()) ? height : 0);
        if (isEnableImage()) {
            i = height;
        }
        int width = i2 + i;
        this.mCrossAreaWidth = Math.min(Math.max(this.mCrossAreaWidth, (float) height), (float) width);
        setMeasuredDimension(width, height);
    }

    private void drawImage(Canvas canvas) {
        if (isEnableImage()) {
            Bitmap scaledImageBitmap = Bitmap.createScaledBitmap(this.mBitmapImage, Math.round(((float) getHeight()) - this.mBorderWidth), Math.round(((float) getHeight()) - this.mBorderWidth), false);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(new BitmapShader(scaledImageBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
            float f = this.mBorderWidth;
            RectF rect = new RectF(f, f, ((float) getHeight()) - this.mBorderWidth, ((float) getHeight()) - this.mBorderWidth);
            canvas.drawRoundRect(rect, rect.height() / 2.0f, rect.height() / 2.0f, paint);
        }
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        RectF rectF = this.mRectF;
        float f = this.mBorderWidth;
        rectF.set(f, f, ((float) w) - f, ((float) h) - f);
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        if (this.isViewClickable) {
            int y = (int) event.getY();
            int x = (int) event.getX();
            int action = event.getAction();
            if (action == 0) {
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                this.mLastY = y;
                this.mLastX = x;
            } else if (action == 2 && (Math.abs(this.mLastY - y) > this.mSlopThreshold || Math.abs(this.mLastX - x) > this.mSlopThreshold)) {
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                this.isMoved = true;
                return false;
            }
        }
        return super.dispatchTouchEvent(event);
    }

    public boolean onTouchEvent(MotionEvent event) {
        OnTagClickListener onTagClickListener;
        int action = event.getAction();
        if (action == 0) {
            this.mRippleRadius = 0.0f;
            this.mTouchX = event.getX();
            this.mTouchY = event.getY();
            splashRipple();
        }
        if (isEnableCross() && isClickCrossArea(event) && (onTagClickListener = this.mOnTagClickListener) != null) {
            if (action == 1) {
                onTagClickListener.onTagCrossClick(((Integer) getTag()).intValue());
            }
            return true;
        } else if (!this.isViewClickable || this.mOnTagClickListener == null) {
            return super.onTouchEvent(event);
        } else {
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (action == 0) {
                this.mLastY = y;
                this.mLastX = x;
                this.isMoved = false;
                this.isUp = false;
                this.isExecLongClick = false;
                postDelayed(this.mLongClickHandle, (long) this.mLongPressTime);
            } else if (action == 1) {
                this.isUp = true;
                if (!this.isExecLongClick && !this.isMoved) {
                    this.mOnTagClickListener.onTagClick(((Integer) getTag()).intValue(), this.mTagItem);
                }
            } else if (action == 2 && !this.isMoved && (Math.abs(this.mLastX - x) > this.mMoveSlop || Math.abs(this.mLastY - y) > this.mMoveSlop)) {
                this.isMoved = true;
            }
            return true;
        }
    }

    private void drawCross(Canvas canvas) {
        int ltX;
        int lbX;
        if (isEnableCross()) {
            this.mCrossAreaPadding = this.mCrossAreaPadding > ((float) (getHeight() / 2)) ? (float) (getHeight() / 2) : this.mCrossAreaPadding;
            if (this.mTextDirection == 4) {
                ltX = (int) this.mCrossAreaPadding;
            } else {
                ltX = (int) (((float) (getWidth() - getHeight())) + this.mCrossAreaPadding);
            }
            int i = this.mTextDirection;
            int ltY = (int) this.mCrossAreaPadding;
            if (this.mTextDirection == 4) {
                lbX = (int) this.mCrossAreaPadding;
            } else {
                lbX = (int) (((float) (getWidth() - getHeight())) + this.mCrossAreaPadding);
            }
            int i2 = this.mTextDirection;
            int lbY = (int) (((float) getHeight()) - this.mCrossAreaPadding);
            int rtX = (int) (((float) (this.mTextDirection == 4 ? getHeight() : getWidth())) - this.mCrossAreaPadding);
            int i3 = this.mTextDirection;
            int rtY = (int) this.mCrossAreaPadding;
            int height = this.mTextDirection == 4 ? getHeight() : getWidth();
            int i4 = this.mTextDirection;
            this.mPaint.setStyle(Paint.Style.STROKE);
            this.mPaint.setColor(this.mCrossColor);
            this.mPaint.setStrokeWidth(this.mCrossLineWidth);
            canvas.drawLine((float) ltX, (float) ltY, (float) ((int) (((float) height) - this.mCrossAreaPadding)), (float) ((int) (((float) getHeight()) - this.mCrossAreaPadding)), this.mPaint);
            canvas.drawLine((float) lbX, (float) lbY, (float) rtX, (float) rtY, this.mPaint);
        }
    }

    private void drawCheck(Canvas canvas) {
        int ltX;
        int lbX;
        int rbX;
        if (isEnableCheck() && this.mTagItem.bSelected) {
            this.mCheckAreaPadding = this.mCheckAreaPadding > ((float) (getHeight() / 2)) ? (float) (getHeight() / 2) : this.mCheckAreaPadding;
            if (this.mTextDirection == 4) {
                ltX = (int) this.mCheckAreaPadding;
            } else {
                ltX = (int) (((float) (getWidth() - getHeight())) + this.mCheckAreaPadding);
            }
            int ltY = getHeight() / 2;
            if (this.mTextDirection == 4) {
                lbX = (int) (((float) (getHeight() / 2)) - this.mCheckAreaPadding);
            } else {
                lbX = (getWidth() - getHeight()) + (getHeight() / 2);
            }
            int i = this.mTextDirection;
            int lbY = (int) (((float) getHeight()) - this.mCheckAreaPadding);
            int rtX = (int) (((float) (this.mTextDirection == 4 ? getHeight() : getWidth())) - this.mCheckAreaPadding);
            int i2 = this.mTextDirection;
            int rtY = (int) this.mCheckAreaPadding;
            if (this.mTextDirection == 4) {
                rbX = (int) (((float) (getHeight() / 2)) - this.mCheckAreaPadding);
            } else {
                rbX = (getWidth() - getHeight()) + (getHeight() / 2);
            }
            int i3 = this.mTextDirection;
            this.mPaint.setStyle(Paint.Style.STROKE);
            this.mPaint.setColor(this.mCheckColor);
            this.mPaint.setStrokeWidth(this.mCheckLineWidth);
            canvas.drawLine((float) ltX, (float) ltY, (float) rbX, (float) ((int) (((float) getHeight()) - this.mCheckAreaPadding)), this.mPaint);
            canvas.drawLine((float) lbX, (float) lbY, (float) rtX, (float) rtY, this.mPaint);
        }
    }

    @TargetApi(11)
    private void splashRipple() {
        if (Build.VERSION.SDK_INT >= 11 && this.mTouchX > 0.0f && this.mTouchY > 0.0f) {
            this.mRipplePaint.setColor(this.mRippleColor);
            this.mRipplePaint.setAlpha(this.mRippleAlpha);
            final float maxDis = Math.max(Math.max(Math.max(this.mTouchX, this.mTouchY), Math.abs(((float) getMeasuredWidth()) - this.mTouchX)), Math.abs(((float) getMeasuredHeight()) - this.mTouchY));
            this.mRippleValueAnimator = ValueAnimator.ofFloat(new float[]{0.0f, maxDis}).setDuration((long) this.mRippleDuration);
            this.mRippleValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    float animValue = ((Float) animation.getAnimatedValue()).floatValue();
                    float unused = TagView.this.mRippleRadius = animValue >= maxDis ? 0.0f : animValue;
                    TagView.this.postInvalidate();
                }
            });
            this.mRippleValueAnimator.start();
        }
    }

    public String getText() {
        return this.mOriginText;
    }

    @TargetApi(11)
    private void drawRipple(Canvas canvas) {
        if (this.isViewClickable && Build.VERSION.SDK_INT >= 11 && canvas != null && !this.unSupportedClipPath) {
            if (Build.VERSION.SDK_INT < 18) {
                setLayerType(1, (Paint) null);
            }
            try {
                canvas.save();
                this.mPath.reset();
                canvas.clipPath(this.mPath);
                this.mPath.addRoundRect(this.mRectF, this.mBorderRadius, this.mBorderRadius, Path.Direction.CCW);
                canvas.drawCircle(this.mTouchX, this.mTouchY, this.mRippleRadius, this.mRipplePaint);
                canvas.restore();
            } catch (UnsupportedOperationException e) {
                this.unSupportedClipPath = true;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setColor(this.mBackgroundColor);
        RectF rectF = this.mRectF;
        float f = this.mBorderRadius;
        canvas.drawRoundRect(rectF, f, f, this.mPaint);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeWidth(this.mBorderWidth);
        this.mPaint.setColor(this.mBorderColor);
        RectF rectF2 = this.mRectF;
        float f2 = this.mBorderRadius;
        canvas.drawRoundRect(rectF2, f2, f2, this.mPaint);
        drawRipple(canvas);
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setColor(this.mTextColor);
        int i = 0;
        if (this.mTextDirection != 4) {
            String str = this.mAbstractText;
            float width = ((float) (((isEnableCross() || isEnableCheck()) ? getWidth() - getHeight() : getWidth()) / 2)) - (this.fontW / 2.0f);
            if (isEnableImage()) {
                i = getHeight() / 2;
            }
            canvas.drawText(str, width + ((float) i), (((float) (getHeight() / 2)) + (this.fontH / 2.0f)) - this.bdDistance, this.mPaint);
        } else if (this.mTagSupportLettersRTL) {
            float tmpX = ((float) (((isEnableCross() || isEnableCheck()) ? getWidth() + getHeight() : getWidth()) / 2)) + (this.fontW / 2.0f);
            char[] charArray = this.mAbstractText.toCharArray();
            int length = charArray.length;
            while (i < length) {
                String sc = String.valueOf(charArray[i]);
                tmpX -= this.mPaint.measureText(sc);
                canvas.drawText(sc, tmpX, (((float) (getHeight() / 2)) + (this.fontH / 2.0f)) - this.bdDistance, this.mPaint);
                i++;
            }
        } else {
            canvas.drawText(this.mAbstractText, (((isEnableCross() || isEnableCheck()) ? ((float) getWidth()) + this.fontW : (float) getWidth()) / 2.0f) - (this.fontW / 2.0f), (((float) (getHeight() / 2)) + (this.fontH / 2.0f)) - this.bdDistance, this.mPaint);
        }
        drawCross(canvas);
        drawCheck(canvas);
        drawImage(canvas);
    }

    public boolean getIsViewClickable() {
        return this.isViewClickable;
    }

    public void setTagMaxLength(int maxLength) {
        this.mTagMaxLength = maxLength;
        onDealText();
    }

    public void setOnTagClickListener(OnTagClickListener listener) {
        this.mOnTagClickListener = listener;
    }

    public void setTagBackgroundColor(int color) {
        this.mBackgroundColor = color;
    }

    public void setTagBorderColor(int color) {
        this.mBorderColor = color;
    }

    public void setTagTextColor(int color) {
        this.mTextColor = color;
    }

    public void setBorderWidth(float width) {
        this.mBorderWidth = width;
    }

    public void setBorderRadius(float radius) {
        this.mBorderRadius = radius;
    }

    public void setTextSize(float size) {
        this.mTextSize = size;
        onDealText();
    }

    public void setHorizontalPadding(int padding) {
        this.mHorizontalPadding = padding;
    }

    public void setVerticalPadding(int padding) {
        this.mVerticalPadding = padding;
    }

    public void setIsViewClickable(boolean clickable) {
        this.isViewClickable = clickable;
    }

    public void setImage(Bitmap newImage) {
        this.mBitmapImage = newImage;
        invalidate();
    }

    public int getTextDirection() {
        return this.mTextDirection;
    }

    public void setTextDirection(int textDirection) {
        this.mTextDirection = textDirection;
    }

    public void setTypeface(Typeface typeface) {
        this.mTypeface = typeface;
        onDealText();
    }

    public void setRippleAlpha(int mRippleAlpha2) {
        this.mRippleAlpha = mRippleAlpha2;
    }

    public void setRippleColor(int mRippleColor2) {
        this.mRippleColor = mRippleColor2;
    }

    public void setRippleDuration(int mRippleDuration2) {
        this.mRippleDuration = mRippleDuration2;
    }

    public void setBdDistance(float bdDistance2) {
        this.bdDistance = bdDistance2;
    }

    public boolean isEnableImage() {
        return (this.mBitmapImage == null || this.mTextDirection == 4) ? false : true;
    }

    public boolean isEnableCross() {
        return this.mEnableCross;
    }

    public boolean isEnableCheck() {
        return this.mEnableCheck;
    }

    public void setEnableCross(boolean mEnableCross2) {
        this.mEnableCross = mEnableCross2;
    }

    public float getCrossAreaWidth() {
        return this.mCrossAreaWidth;
    }

    public void setCrossAreaWidth(float mCrossAreaWidth2) {
        this.mCrossAreaWidth = mCrossAreaWidth2;
    }

    public float getCrossLineWidth() {
        return this.mCrossLineWidth;
    }

    public void setCrossLineWidth(float mCrossLineWidth2) {
        this.mCrossLineWidth = mCrossLineWidth2;
    }

    public float getCrossAreaPadding() {
        return this.mCrossAreaPadding;
    }

    public void setCrossAreaPadding(float mCrossAreaPadding2) {
        this.mCrossAreaPadding = mCrossAreaPadding2;
    }

    public int getCrossColor() {
        return this.mCrossColor;
    }

    public void setCrossColor(int mCrossColor2) {
        this.mCrossColor = mCrossColor2;
    }

    public void setEnableCheck(boolean mEnableCheck2) {
        this.mEnableCheck = mEnableCheck2;
    }

    public float getCheckAreaWidth() {
        return this.mCheckAreaWidth;
    }

    public void setCheckAreaWidth(float mCheckAreaWidth2) {
        this.mCheckAreaWidth = mCheckAreaWidth2;
    }

    public float getCheckLineWidth() {
        return this.mCheckLineWidth;
    }

    public void setCheckLineWidth(float mCheckLineWidth2) {
        this.mCheckLineWidth = mCheckLineWidth2;
    }

    public float getCheckAreaPadding() {
        return this.mCheckAreaPadding;
    }

    public void setCheckAreaPadding(float mCheckAreaPadding2) {
        this.mCheckAreaPadding = mCheckAreaPadding2;
    }

    public int getCheckColor() {
        return this.mCheckColor;
    }

    public void setCheckColor(int CheckColor) {
        this.mCheckColor = CheckColor;
    }

    public boolean isTagSupportLettersRTL() {
        return this.mTagSupportLettersRTL;
    }

    public void setTagSupportLettersRTL(boolean mTagSupportLettersRTL2) {
        this.mTagSupportLettersRTL = mTagSupportLettersRTL2;
    }
}
