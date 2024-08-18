package com.ortiz.touchview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.OverScroller;
import android.widget.Scroller;

import androidx.appcompat.widget.AppCompatImageView;

import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.Constant;


public class TouchImageView extends AppCompatImageView {
    public static final float AUTOMATIC_MIN_ZOOM = -1.0f;
    private static final String DEBUG = "DEBUG";
    private static final float SUPER_MAX_MULTIPLIER = 1.25f;
    private static final float SUPER_MIN_MULTIPLIER = 0.75f;
    /* access modifiers changed from: private */
    public Context context;
    private ZoomVariables delayedZoomVariables;
    /* access modifiers changed from: private */
    public GestureDetector.OnDoubleTapListener doubleTapListener;
    /* access modifiers changed from: private */
    public Fling fling;
    private ImagePositionListener imagePositionListener;
    private boolean imageRenderedAtLeastOnce;
    /* access modifiers changed from: private */

    /* renamed from: m */
    public float[] f11451m;
    /* access modifiers changed from: private */
    public GestureDetector mGestureDetector;
    /* access modifiers changed from: private */
    public ScaleGestureDetector mScaleDetector;
    private ImageView.ScaleType mScaleType;
    private float matchViewHeight;
    private float matchViewWidth;
    /* access modifiers changed from: private */
    public Matrix matrix;
    /* access modifiers changed from: private */
    public float maxScale;
    private boolean maxScaleIsSetByMultiplier;
    private float maxScaleMultiplier;
    /* access modifiers changed from: private */
    public float minScale;
    /* access modifiers changed from: private */
    public float normalizedScale;
    private boolean onDrawReady;
    private int orientation;
    private FixedPixel orientationChangeFixedPixel;
    private boolean orientationJustChanged;
    private float prevMatchViewHeight;
    private float prevMatchViewWidth;
    private Matrix prevMatrix;
    private int prevViewHeight;
    private int prevViewWidth;
    /* access modifiers changed from: private */
    public State state;
    private float superMaxScale;
    private float superMinScale;
    /* access modifiers changed from: private */
    public OnTouchImageViewListener touchImageViewListener;
    private float userSpecifiedMinScale;
    /* access modifiers changed from: private */
    public View.OnTouchListener userTouchListener;
    /* access modifiers changed from: private */
    public int viewHeight;
    private FixedPixel viewSizeChangeFixedPixel;
    /* access modifiers changed from: private */
    public int viewWidth;
    private boolean zoomEnabled;

    public enum FixedPixel {
        CENTER,
        TOP_LEFT,
        BOTTOM_RIGHT
    }

    public interface ImagePositionListener {
        void onPositionChanged(RectF rectF);
    }

    public interface OnTouchImageViewListener {
        void onMove();
    }

    private enum State {
        NONE,
        DRAG,
        ZOOM,
        FLING,
        ANIMATE_ZOOM
    }

    public TouchImageView(Context context2) {
        this(context2, (AttributeSet) null);
    }

    public TouchImageView(Context context2, AttributeSet attrs) {
        this(context2, attrs, 0);
    }

    public TouchImageView(Context context2, AttributeSet attrs, int defStyle) {
        super(context2, attrs, defStyle);
        this.orientationChangeFixedPixel = FixedPixel.CENTER;
        this.viewSizeChangeFixedPixel = FixedPixel.CENTER;
        this.orientationJustChanged = false;
        this.maxScaleIsSetByMultiplier = false;
        this.doubleTapListener = null;
        this.userTouchListener = null;
        this.touchImageViewListener = null;
        this.imagePositionListener = null;
        configureImageView(context2, attrs, defStyle);
    }

    private void configureImageView(Context context2, AttributeSet attrs, int defStyleAttr) {
        this.context = context2;
        super.setClickable(true);
        this.orientation = getResources().getConfiguration().orientation;
        this.mScaleDetector = new ScaleGestureDetector(context2, new ScaleListener(this, (C52761) null));
        this.mGestureDetector = new GestureDetector(context2, new GestureListener(this, (C52761) null));
        this.matrix = new Matrix();
        this.prevMatrix = new Matrix();
        this.f11451m = new float[9];
        this.normalizedScale = 1.0f;
        if (this.mScaleType == null) {
            this.mScaleType = ImageView.ScaleType.FIT_CENTER;
        }
        this.minScale = 1.0f;
        this.maxScale = 3.0f;
        this.superMinScale = this.minScale * 0.75f;
        this.superMaxScale = this.maxScale * SUPER_MAX_MULTIPLIER;
        setImageMatrix(this.matrix);
        setScaleType(ImageView.ScaleType.MATRIX);
        setState(State.NONE);
        this.onDrawReady = false;
        super.setOnTouchListener(new PrivateOnTouchListener(this, (C52761) null));
        TypedArray attributes = context2.getTheme().obtainStyledAttributes(attrs, R.styleable.TouchImageView, defStyleAttr, 0);
        if (attributes != null) {
            try {
                if (!isInEditMode()) {
                    setZoomEnabled(attributes.getBoolean(R.styleable.TouchImageView_zoom_enabled, true));
                }
            } catch (Throwable th) {
                if (attributes != null) {
                    attributes.recycle();
                }
                throw th;
            }
        }
        if (attributes != null) {
            attributes.recycle();
        }
    }

    public void setOnTouchListener(View.OnTouchListener l) {
        this.userTouchListener = l;
    }

    public void setOnTouchImageViewListener(OnTouchImageViewListener l) {
        this.touchImageViewListener = l;
    }

    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener l) {
        this.doubleTapListener = l;
    }

    public boolean isZoomEnabled() {
        return this.zoomEnabled;
    }

    public void setZoomEnabled(boolean zoomEnabled2) {
        this.zoomEnabled = zoomEnabled2;
    }

    public void setImageResource(int resId) {
        this.imageRenderedAtLeastOnce = false;
        super.setImageResource(resId);
        savePreviousImageValues();
        fitImageToView();
    }

    public void setImageBitmap(Bitmap bm) {
        this.imageRenderedAtLeastOnce = false;
        super.setImageBitmap(bm);
        savePreviousImageValues();
        fitImageToView();
    }

    public void setImageDrawable(Drawable drawable) {
        this.imageRenderedAtLeastOnce = false;
        super.setImageDrawable(drawable);
        savePreviousImageValues();
        fitImageToView();
    }

    public void setImageURI(Uri uri) {
        this.imageRenderedAtLeastOnce = false;
        super.setImageURI(uri);
        savePreviousImageValues();
        fitImageToView();
    }

    public void setScaleType(ImageView.ScaleType type) {
        if (type == ImageView.ScaleType.MATRIX) {
            super.setScaleType(ImageView.ScaleType.MATRIX);
            return;
        }
        this.mScaleType = type;
        if (this.onDrawReady) {
            setZoom(this);
        }
    }

    public ImageView.ScaleType getScaleType() {
        return this.mScaleType;
    }

    public FixedPixel getOrientationChangeFixedPixel() {
        return this.orientationChangeFixedPixel;
    }

    public void setOrientationChangeFixedPixel(FixedPixel fixedPixel) {
        this.orientationChangeFixedPixel = fixedPixel;
    }

    public FixedPixel getViewSizeChangeFixedPixel() {
        return this.viewSizeChangeFixedPixel;
    }

    public void setViewSizeChangeFixedPixel(FixedPixel viewSizeChangeFixedPixel2) {
        this.viewSizeChangeFixedPixel = viewSizeChangeFixedPixel2;
    }

    public boolean isZoomed() {
        return this.normalizedScale != 1.0f;
    }

    public RectF getZoomedRect() {
        if (this.mScaleType != ImageView.ScaleType.FIT_XY) {
            PointF topLeft = transformCoordTouchToBitmap(0.0f, 0.0f, true);
            PointF bottomRight = transformCoordTouchToBitmap((float) this.viewWidth, (float) this.viewHeight, true);
            float w = (float) getDrawable().getIntrinsicWidth();
            float h = (float) getDrawable().getIntrinsicHeight();
            return new RectF(topLeft.x / w, topLeft.y / h, bottomRight.x / w, bottomRight.y / h);
        }
        throw new UnsupportedOperationException("getZoomedRect() not supported with FIT_XY");
    }

    public void savePreviousImageValues() {
        Matrix matrix2 = this.matrix;
        if (matrix2 != null && this.viewHeight != 0 && this.viewWidth != 0) {
            matrix2.getValues(this.f11451m);
            this.prevMatrix.setValues(this.f11451m);
            this.prevMatchViewHeight = this.matchViewHeight;
            this.prevMatchViewWidth = this.matchViewWidth;
            this.prevViewHeight = this.viewHeight;
            this.prevViewWidth = this.viewWidth;
        }
    }

    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("orientation", this.orientation);
        bundle.putFloat("saveScale", this.normalizedScale);
        bundle.putFloat("matchViewHeight", this.matchViewHeight);
        bundle.putFloat("matchViewWidth", this.matchViewWidth);
        bundle.putInt(Constant.EXTRA_VIEW_WIDTH, this.viewWidth);
        bundle.putInt(Constant.EXTRA_VIEW_HEIGHT, this.viewHeight);
        this.matrix.getValues(this.f11451m);
        bundle.putFloatArray("matrix", this.f11451m);
        bundle.putBoolean("imageRendered", this.imageRenderedAtLeastOnce);
        bundle.putSerializable("viewSizeChangeFixedPixel", this.viewSizeChangeFixedPixel);
        bundle.putSerializable("orientationChangeFixedPixel", this.orientationChangeFixedPixel);
        return bundle;
    }

    public void onRestoreInstanceState(Parcelable state2) {
        if (state2 instanceof Bundle) {
            Bundle bundle = (Bundle) state2;
            this.normalizedScale = bundle.getFloat("saveScale");
            this.f11451m = bundle.getFloatArray("matrix");
            this.prevMatrix.setValues(this.f11451m);
            this.prevMatchViewHeight = bundle.getFloat("matchViewHeight");
            this.prevMatchViewWidth = bundle.getFloat("matchViewWidth");
            this.prevViewHeight = bundle.getInt(Constant.EXTRA_VIEW_HEIGHT);
            this.prevViewWidth = bundle.getInt(Constant.EXTRA_VIEW_WIDTH);
            this.imageRenderedAtLeastOnce = bundle.getBoolean("imageRendered");
            this.viewSizeChangeFixedPixel = (FixedPixel) bundle.getSerializable("viewSizeChangeFixedPixel");
            this.orientationChangeFixedPixel = (FixedPixel) bundle.getSerializable("orientationChangeFixedPixel");
            if (this.orientation != bundle.getInt("orientation")) {
                this.orientationJustChanged = true;
            }
            super.onRestoreInstanceState(bundle.getParcelable("instanceState"));
            return;
        }
        super.onRestoreInstanceState(state2);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        this.onDrawReady = true;
        this.imageRenderedAtLeastOnce = true;
        ZoomVariables zoomVariables = this.delayedZoomVariables;
        if (zoomVariables != null) {
            setZoom(zoomVariables.scale, this.delayedZoomVariables.focusX, this.delayedZoomVariables.focusY, this.delayedZoomVariables.scaleType);
            this.delayedZoomVariables = null;
        }
        super.onDraw(canvas);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int newOrientation = getResources().getConfiguration().orientation;
        if (newOrientation != this.orientation) {
            this.orientationJustChanged = true;
            this.orientation = newOrientation;
        }
        savePreviousImageValues();
    }

    private void notifyImagePositionChanges() {
        RectF rectF;
        if (this.imagePositionListener != null && (rectF = getBitmapPositionInsideImageView()) != null) {
            this.imagePositionListener.onPositionChanged(rectF);
        }
    }

    public void setImagePositionListener(ImagePositionListener imagePositionListener2) {
        this.imagePositionListener = imagePositionListener2;
    }

    public RectF getBitmapPositionInsideImageView() {
        if (getDrawable() == null) {
            return null;
        }
        float[] f = new float[9];
        getImageMatrix().getValues(f);
        float scaleX = f[0];
        float scaleY = f[4];
        Drawable d = getDrawable();
        int origW = d.getIntrinsicWidth();
        int origH = d.getIntrinsicHeight();
        int actW = Math.round(((float) origW) * scaleX);
        int actH = Math.round(((float) origH) * scaleY);
        int imgViewW = getWidth();
        int imgViewH = getHeight();
        int top = (imgViewH - actH) >> 1;
        int left = (imgViewW - actW) >> 1;
        if (left < 0) {
            left = 0;
        }
        if (top < 0) {
            top = 0;
        }
        int right = left + actW;
        int bottom = top + actH;
        if (right > imgViewW) {
            right = imgViewW;
        }
        if (bottom > imgViewH) {
            bottom = imgViewH;
        }
        return new RectF((float) left, (float) top, (float) right, (float) bottom);
    }

    public float getMaxZoom() {
        return this.maxScale;
    }

    public void setMaxZoom(float max) {
        this.maxScale = max;
        this.superMaxScale = this.maxScale * SUPER_MAX_MULTIPLIER;
        this.maxScaleIsSetByMultiplier = false;
    }

    public void setMaxZoomRatio(float max) {
        this.maxScaleMultiplier = max;
        this.maxScale = this.minScale * this.maxScaleMultiplier;
        this.superMaxScale = this.maxScale * SUPER_MAX_MULTIPLIER;
        this.maxScaleIsSetByMultiplier = true;
    }

    public float getMinZoom() {
        return this.minScale;
    }

    public float getCurrentZoom() {
        return this.normalizedScale;
    }

    public void setMinZoom(float min) {
        this.userSpecifiedMinScale = min;
        if (min != -1.0f) {
            this.minScale = this.userSpecifiedMinScale;
        } else if (this.mScaleType == ImageView.ScaleType.CENTER || this.mScaleType == ImageView.ScaleType.CENTER_CROP) {
            Drawable drawable = getDrawable();
            int drawableWidth = drawable.getIntrinsicWidth();
            int drawableHeight = drawable.getIntrinsicHeight();
            if (drawable != null && drawableWidth > 0 && drawableHeight > 0) {
                float widthRatio = ((float) this.viewWidth) / ((float) drawableWidth);
                float heightRatio = ((float) this.viewHeight) / ((float) drawableHeight);
                if (this.mScaleType == ImageView.ScaleType.CENTER) {
                    this.minScale = Math.min(widthRatio, heightRatio);
                } else {
                    this.minScale = Math.min(widthRatio, heightRatio) / Math.max(widthRatio, heightRatio);
                }
            }
        } else {
            this.minScale = 1.0f;
        }
        if (this.maxScaleIsSetByMultiplier) {
            setMaxZoomRatio(this.maxScaleMultiplier);
        }
        this.superMinScale = this.minScale * 0.75f;
    }

    public void resetZoom() {
        this.normalizedScale = 1.0f;
        fitImageToView();
    }

    public void setZoom(float scale) {
        setZoom(scale, 0.5f, 0.5f);
    }

    public void setZoom(float scale, float focusX, float focusY) {
        setZoom(scale, focusX, focusY, this.mScaleType);
    }

    public void setZoom(float scale, float focusX, float focusY, ImageView.ScaleType scaleType) {
        if (!this.onDrawReady) {
            this.delayedZoomVariables = new ZoomVariables(scale, focusX, focusY, scaleType);
            return;
        }
        if (this.userSpecifiedMinScale == -1.0f) {
            setMinZoom(-1.0f);
            float f = this.normalizedScale;
            float f2 = this.minScale;
            if (f < f2) {
                this.normalizedScale = f2;
            }
        }
        if (scaleType != this.mScaleType) {
            setScaleType(scaleType);
        }
        resetZoom();
        scaleImage((double) scale, (float) (this.viewWidth / 2), (float) (this.viewHeight / 2), true);
        this.matrix.getValues(this.f11451m);
        this.f11451m[2] = -((getImageWidth() * focusX) - (((float) this.viewWidth) * 0.5f));
        this.f11451m[5] = -((getImageHeight() * focusY) - (((float) this.viewHeight) * 0.5f));
        this.matrix.setValues(this.f11451m);
        fixTrans();
        setImageMatrix(this.matrix);
    }

    public void setZoom(TouchImageView img) {
        PointF center = img.getScrollPosition();
        setZoom(img.getCurrentZoom(), center.x, center.y, img.getScaleType());
    }

    public PointF getScrollPosition() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return null;
        }
        int drawableWidth = drawable.getIntrinsicWidth();
        int drawableHeight = drawable.getIntrinsicHeight();
        PointF point = transformCoordTouchToBitmap((float) (this.viewWidth / 2), (float) (this.viewHeight / 2), true);
        point.x /= (float) drawableWidth;
        point.y /= (float) drawableHeight;
        return point;
    }

    public void setScrollPosition(float focusX, float focusY) {
        setZoom(this.normalizedScale, focusX, focusY);
    }

    /* access modifiers changed from: private */
    public void fixTrans() {
        this.matrix.getValues(this.f11451m);
        float[] fArr = this.f11451m;
        float transX = fArr[2];
        float transY = fArr[5];
        float fixTransX = getFixTrans(transX, (float) this.viewWidth, getImageWidth());
        float fixTransY = getFixTrans(transY, (float) this.viewHeight, getImageHeight());
        if (fixTransX != 0.0f || fixTransY != 0.0f) {
            this.matrix.postTranslate(fixTransX, fixTransY);
        }
    }

    /* access modifiers changed from: private */
    public void fixScaleTrans() {
        fixTrans();
        this.matrix.getValues(this.f11451m);
        float imageWidth = getImageWidth();
        int i = this.viewWidth;
        if (imageWidth < ((float) i)) {
            this.f11451m[2] = (((float) i) - getImageWidth()) / 2.0f;
        }
        float imageHeight = getImageHeight();
        int i2 = this.viewHeight;
        if (imageHeight < ((float) i2)) {
            this.f11451m[5] = (((float) i2) - getImageHeight()) / 2.0f;
        }
        this.matrix.setValues(this.f11451m);
    }

    private float getFixTrans(float trans, float viewSize, float contentSize) {
        float maxTrans;
        float minTrans;
        if (contentSize <= viewSize) {
            minTrans = 0.0f;
            maxTrans = viewSize - contentSize;
        } else {
            minTrans = viewSize - contentSize;
            maxTrans = 0.0f;
        }
        if (trans < minTrans) {
            return (-trans) + minTrans;
        }
        if (trans > maxTrans) {
            return (-trans) + maxTrans;
        }
        return 0.0f;
    }

    /* access modifiers changed from: private */
    public float getFixDragTrans(float delta, float viewSize, float contentSize) {
        if (contentSize <= viewSize) {
            return 0.0f;
        }
        return delta;
    }

    /* access modifiers changed from: private */
    public float getImageWidth() {
        return this.matchViewWidth * this.normalizedScale;
    }

    /* access modifiers changed from: private */
    public float getImageHeight() {
        return this.matchViewHeight * this.normalizedScale;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable drawable = getDrawable();
        if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0) {
            setMeasuredDimension(0, 0);
            return;
        }
        int drawableWidth = drawable.getIntrinsicWidth();
        int drawableHeight = drawable.getIntrinsicHeight();
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int totalViewWidth = setViewSize(widthMode, widthSize, drawableWidth);
        int totalViewHeight = setViewSize(heightMode, heightSize, drawableHeight);
        if (!this.orientationJustChanged) {
            savePreviousImageValues();
        }
        setMeasuredDimension((totalViewWidth - getPaddingLeft()) - getPaddingRight(), (totalViewHeight - getPaddingTop()) - getPaddingBottom());
    }


    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.viewWidth = w;
        this.viewHeight = h;
        fitImageToView();
    }

    private void fitImageToView() {
        float scaleX;
        float scaleY;
        TouchImageView touchImageView;
        FixedPixel fixedPixel = this.orientationJustChanged ? this.orientationChangeFixedPixel : this.viewSizeChangeFixedPixel;
        this.orientationJustChanged = false;
        Drawable drawable = getDrawable();
        if (drawable != null && drawable.getIntrinsicWidth() != 0) {
            if (drawable.getIntrinsicHeight() != 0) {
                if (this.matrix == null) {
                    return;
                }
                if (this.prevMatrix != null) {
                    if (this.userSpecifiedMinScale == -1.0f) {
                        setMinZoom(-1.0f);
                        float f = this.normalizedScale;
                        float f2 = this.minScale;
                        if (f < f2) {
                            this.normalizedScale = f2;
                        }
                    }
                    int drawableWidth = drawable.getIntrinsicWidth();
                    int drawableHeight = drawable.getIntrinsicHeight();
                    float scaleX2 = ((float) this.viewWidth) / ((float) drawableWidth);
                    float scaleY2 = ((float) this.viewHeight) / ((float) drawableHeight);
                    switch (C52761.$SwitchMap$android$widget$ImageView$ScaleType[this.mScaleType.ordinal()]) {
                        case 1:
                            scaleX = 1.0f;
                            scaleY = 1.0f;
                            break;
                        case 2:
                            float scaleX3 = Math.max(scaleX2, scaleY2);
                            scaleX = scaleX3;
                            scaleY = scaleX3;
                            break;
                        case 3:
                            float min = Math.min(1.0f, Math.min(scaleX2, scaleY2));
                            scaleY2 = min;
                            scaleX2 = min;
                        case 4:
                        case 5:
                        case 6:
                            float scaleX4 = Math.min(scaleX2, scaleY2);
                            scaleX = scaleX4;
                            scaleY = scaleX4;
                            break;
                        case 7:
                        default:
                            scaleX = scaleX2;
                            scaleY = scaleY2;
                            break;
                    }
                    int i = this.viewWidth;
                    float redundantXSpace = ((float) i) - (((float) drawableWidth) * scaleX);
                    int i2 = this.viewHeight;
                    float redundantYSpace = ((float) i2) - (((float) drawableHeight) * scaleY);
                    this.matchViewWidth = ((float) i) - redundantXSpace;
                    this.matchViewHeight = ((float) i2) - redundantYSpace;
                    if (isZoomed() || this.imageRenderedAtLeastOnce) {
                        if (this.prevMatchViewWidth == 0.0f || this.prevMatchViewHeight == 0.0f) {
                            savePreviousImageValues();
                        }
                        this.prevMatrix.getValues(this.f11451m);
                        float[] fArr = this.f11451m;
                        float f3 = this.matchViewWidth / ((float) drawableWidth);
                        float f4 = this.normalizedScale;
                        fArr[0] = f3 * f4;
                        fArr[4] = (this.matchViewHeight / ((float) drawableHeight)) * f4;
                        float transX = fArr[2];
                        float transY = fArr[5];
                        this.f11451m[2] = newTranslationAfterChange(transX, this.prevMatchViewWidth * f4, getImageWidth(), this.prevViewWidth, this.viewWidth, drawableWidth, fixedPixel);
                        this.f11451m[5] = newTranslationAfterChange(transY, this.prevMatchViewHeight * this.normalizedScale, getImageHeight(), this.prevViewHeight, this.viewHeight, drawableHeight, fixedPixel);
                        touchImageView = this;
                        touchImageView.matrix.setValues(touchImageView.f11451m);
                    } else {
                        this.matrix.setScale(scaleX, scaleY);
                        int i3 = C52761.$SwitchMap$android$widget$ImageView$ScaleType[this.mScaleType.ordinal()];
                        if (i3 == 5) {
                            this.matrix.postTranslate(0.0f, 0.0f);
                        } else if (i3 != 6) {
                            this.matrix.postTranslate(redundantXSpace / 2.0f, redundantYSpace / 2.0f);
                        } else {
                            this.matrix.postTranslate(redundantXSpace, redundantYSpace);
                        }
                        this.normalizedScale = 1.0f;
                        touchImageView = this;
                    }
                    fixTrans();
                    touchImageView.setImageMatrix(touchImageView.matrix);
                }
            }
        }
    }


    static /* synthetic */ class C52761 {
        static final /* synthetic */ int[] $SwitchMap$android$widget$ImageView$ScaleType = new int[ImageView.ScaleType.values().length];

        static {
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.CENTER.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.CENTER_CROP.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.CENTER_INSIDE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.FIT_CENTER.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.FIT_START.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.FIT_END.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.FIT_XY.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    private int setViewSize(int mode, int size, int drawableWidth) {
        if (mode == Integer.MIN_VALUE) {
            return Math.min(drawableWidth, size);
        }
        if (mode == 0) {
            return drawableWidth;
        }
        if (mode != 1073741824) {
            return size;
        }
        return size;
    }

    private float newTranslationAfterChange(float trans, float prevImageSize, float imageSize, int prevViewSize, int viewSize, int drawableSize, FixedPixel sizeChangeFixedPixel) {
        if (imageSize < ((float) viewSize)) {
            return (((float) viewSize) - (((float) drawableSize) * this.f11451m[0])) * 0.5f;
        }
        if (trans > 0.0f) {
            return -((imageSize - ((float) viewSize)) * 0.5f);
        }
        float fixedPixelPositionInView = 0.5f;
        if (sizeChangeFixedPixel == FixedPixel.BOTTOM_RIGHT) {
            fixedPixelPositionInView = 1.0f;
        } else if (sizeChangeFixedPixel == FixedPixel.TOP_LEFT) {
            fixedPixelPositionInView = 0.0f;
        }
        return -(((((-trans) + (((float) prevViewSize) * fixedPixelPositionInView)) / prevImageSize) * imageSize) - (((float) viewSize) * fixedPixelPositionInView));
    }


    public void setState(State state2) {
        this.state = state2;
    }

    public boolean canScrollHorizontallyFroyo(int direction) {
        return canScrollHorizontally(direction);
    }

    public boolean canScrollHorizontally(int direction) {
        this.matrix.getValues(this.f11451m);
        float x = this.f11451m[2];
        if (getImageWidth() < ((float) this.viewWidth)) {
            return false;
        }
        if (x >= -1.0f && direction < 0) {
            return false;
        }
        if (Math.abs(x) + ((float) this.viewWidth) + 1.0f < getImageWidth() || direction <= 0) {
            return true;
        }
        return false;
    }

    public boolean canScrollVertically(int direction) {
        this.matrix.getValues(this.f11451m);
        float y = this.f11451m[5];
        if (getImageHeight() < ((float) this.viewHeight)) {
            return false;
        }
        if (y >= -1.0f && direction < 0) {
            return false;
        }
        if (Math.abs(y) + ((float) this.viewHeight) + 1.0f < getImageHeight() || direction <= 0) {
            return true;
        }
        return false;
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private GestureListener() {
        }

        /* synthetic */ GestureListener(TouchImageView x0, C52761 x1) {
            this();
        }

        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (TouchImageView.this.doubleTapListener != null) {
                return TouchImageView.this.doubleTapListener.onSingleTapConfirmed(e);
            }
            return TouchImageView.this.performClick();
        }

        public void onLongPress(MotionEvent e) {
            TouchImageView.this.performLongClick();
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (TouchImageView.this.fling != null) {
                TouchImageView.this.fling.cancelFling();
            }
            TouchImageView touchImageView = TouchImageView.this;
            Fling unused = touchImageView.fling = new Fling(touchImageView, (int) velocityX, (int) velocityY);
            TouchImageView touchImageView2 = TouchImageView.this;
            touchImageView2.compatPostOnAnimation(touchImageView2.fling);
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        public boolean onDoubleTap(MotionEvent e) {
            boolean consumed = false;
            if (!TouchImageView.this.isZoomEnabled()) {
                return false;
            }
            if (TouchImageView.this.doubleTapListener != null) {
                consumed = TouchImageView.this.doubleTapListener.onDoubleTap(e);
            }
            if (TouchImageView.this.state != State.NONE) {
                return consumed;
            }
            TouchImageView.this.compatPostOnAnimation(new DoubleTapZoom(TouchImageView.this.normalizedScale == TouchImageView.this.minScale ? TouchImageView.this.maxScale : TouchImageView.this.minScale, e.getX(), e.getY(), false));
            return true;
        }

        public boolean onDoubleTapEvent(MotionEvent e) {
            if (TouchImageView.this.doubleTapListener != null) {
                return TouchImageView.this.doubleTapListener.onDoubleTapEvent(e);
            }
            return false;
        }
    }

    private class PrivateOnTouchListener implements View.OnTouchListener {
        private PointF last;

        private PrivateOnTouchListener() {
            this.last = new PointF();
        }

        /* synthetic */ PrivateOnTouchListener(TouchImageView x0, C52761 x1) {
            this();
        }


        public boolean onTouch(View v, MotionEvent event) {
            if (TouchImageView.this.getDrawable() == null) {
                TouchImageView.this.setState(State.NONE);
                return false;
            }
            TouchImageView.this.mScaleDetector.onTouchEvent(event);
            TouchImageView.this.mGestureDetector.onTouchEvent(event);
            PointF curr = new PointF(event.getX(), event.getY());
            if (TouchImageView.this.state == State.NONE || TouchImageView.this.state == State.DRAG || TouchImageView.this.state == State.FLING) {
                int action = event.getAction();
                if (action != 0) {
                    if (action != 1) {
                        if (action == 2) {
                            if (TouchImageView.this.state == State.DRAG) {
                                float deltaX = curr.x - this.last.x;
                                float deltaY = curr.y - this.last.y;
                                TouchImageView touchImageView = TouchImageView.this;
                                float fixTransX = touchImageView.getFixDragTrans(deltaX, (float) touchImageView.viewWidth, TouchImageView.this.getImageWidth());
                                TouchImageView touchImageView2 = TouchImageView.this;
                                TouchImageView.this.matrix.postTranslate(fixTransX, touchImageView2.getFixDragTrans(deltaY, (float) touchImageView2.viewHeight, TouchImageView.this.getImageHeight()));
                                TouchImageView.this.fixTrans();
                                this.last.set(curr.x, curr.y);
                            }
                        }
                    }
                    TouchImageView.this.setState(State.NONE);
                } else {
                    this.last.set(curr);
                    if (TouchImageView.this.fling != null) {
                        TouchImageView.this.fling.cancelFling();
                    }
                    TouchImageView.this.setState(State.DRAG);
                }
            }
            TouchImageView touchImageView3 = TouchImageView.this;
            touchImageView3.setImageMatrix(touchImageView3.matrix);
            if (TouchImageView.this.userTouchListener != null) {
                TouchImageView.this.userTouchListener.onTouch(v, event);
            }
            if (TouchImageView.this.touchImageViewListener != null) {
                TouchImageView.this.touchImageViewListener.onMove();
            }
            return true;
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private ScaleListener() {
        }

        /* synthetic */ ScaleListener(TouchImageView x0, C52761 x1) {
            this();
        }

        public boolean onScaleBegin(ScaleGestureDetector detector) {
            TouchImageView.this.setState(State.ZOOM);
            return true;
        }

        public boolean onScale(ScaleGestureDetector detector) {
            TouchImageView.this.scaleImage((double) detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY(), true);
            if (TouchImageView.this.touchImageViewListener == null) {
                return true;
            }
            TouchImageView.this.touchImageViewListener.onMove();
            return true;
        }

        public void onScaleEnd(ScaleGestureDetector detector) {
            float targetZoom;
            super.onScaleEnd(detector);
            TouchImageView.this.setState(State.NONE);
            boolean animateToZoomBoundary = false;
            float targetZoom2 = TouchImageView.this.normalizedScale;
            if (TouchImageView.this.normalizedScale > TouchImageView.this.maxScale) {
                animateToZoomBoundary = true;
                targetZoom = TouchImageView.this.maxScale;
            } else if (TouchImageView.this.normalizedScale < TouchImageView.this.minScale) {
                animateToZoomBoundary = true;
                targetZoom = TouchImageView.this.minScale;
            } else {
                targetZoom = targetZoom2;
            }
            if (animateToZoomBoundary) {
                TouchImageView touchImageView = TouchImageView.this;
                TouchImageView.this.compatPostOnAnimation(new DoubleTapZoom(targetZoom, (float) (touchImageView.viewWidth / 2), (float) (TouchImageView.this.viewHeight / 2), true));
            }
        }
    }

    /* access modifiers changed from: private */
    public void scaleImage(double deltaScale, float focusX, float focusY, boolean stretchImageToSuper) {
        float upperScale;
        float lowerScale;
        if (stretchImageToSuper) {
            lowerScale = this.superMinScale;
            upperScale = this.superMaxScale;
        } else {
            lowerScale = this.minScale;
            upperScale = this.maxScale;
        }
        float origScale = this.normalizedScale;
        double d = (double) this.normalizedScale;
        Double.isNaN(d);
        this.normalizedScale = (float) (d * deltaScale);
        float f = this.normalizedScale;
        if (f > upperScale) {
            this.normalizedScale = upperScale;
            deltaScale = (double) (upperScale / origScale);
        } else if (f < lowerScale) {
            this.normalizedScale = lowerScale;
            deltaScale = (double) (lowerScale / origScale);
        }
        this.matrix.postScale((float) deltaScale, (float) deltaScale, focusX, focusY);
        fixScaleTrans();
    }

    private class DoubleTapZoom implements Runnable {
        private static final float ZOOM_TIME = 500.0f;
        private float bitmapX;
        private float bitmapY;
        private PointF endTouch;
        private AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
        private long startTime;
        private PointF startTouch;
        private float startZoom;
        private boolean stretchImageToSuper;
        private float targetZoom;

        DoubleTapZoom(float targetZoom2, float focusX, float focusY, boolean stretchImageToSuper2) {
            TouchImageView.this.setState(State.ANIMATE_ZOOM);
            this.startTime = System.currentTimeMillis();
            this.startZoom = TouchImageView.this.normalizedScale;
            this.targetZoom = targetZoom2;
            this.stretchImageToSuper = stretchImageToSuper2;
            PointF bitmapPoint = TouchImageView.this.transformCoordTouchToBitmap(focusX, focusY, false);
            this.bitmapX = bitmapPoint.x;
            this.bitmapY = bitmapPoint.y;
            this.startTouch = TouchImageView.this.transformCoordBitmapToTouch(this.bitmapX, this.bitmapY);
            this.endTouch = new PointF((float) (TouchImageView.this.viewWidth / 2), (float) (TouchImageView.this.viewHeight / 2));
        }

        public void run() {
            if (TouchImageView.this.getDrawable() == null) {
                TouchImageView.this.setState(State.NONE);
                return;
            }
            float t = interpolate();
            TouchImageView.this.scaleImage(calculateDeltaScale(t), this.bitmapX, this.bitmapY, this.stretchImageToSuper);
            translateImageToCenterTouchPosition(t);
            TouchImageView.this.fixScaleTrans();
            TouchImageView touchImageView = TouchImageView.this;
            touchImageView.setImageMatrix(touchImageView.matrix);
            if (TouchImageView.this.touchImageViewListener != null) {
                TouchImageView.this.touchImageViewListener.onMove();
            }
            if (t < 1.0f) {
                TouchImageView.this.compatPostOnAnimation(this);
            } else {
                TouchImageView.this.setState(State.NONE);
            }
        }

        private void translateImageToCenterTouchPosition(float t) {
            float targetX = this.startTouch.x + ((this.endTouch.x - this.startTouch.x) * t);
            float targetY = this.startTouch.y + ((this.endTouch.y - this.startTouch.y) * t);
            PointF curr = TouchImageView.this.transformCoordBitmapToTouch(this.bitmapX, this.bitmapY);
            TouchImageView.this.matrix.postTranslate(targetX - curr.x, targetY - curr.y);
        }

        private float interpolate() {
            return this.interpolator.getInterpolation(Math.min(1.0f, ((float) (System.currentTimeMillis() - this.startTime)) / ZOOM_TIME));
        }

        private double calculateDeltaScale(float t) {
            float f = this.startZoom;
            double zoom = (double) (f + ((this.targetZoom - f) * t));
            double access$700 = (double) TouchImageView.this.normalizedScale;
            Double.isNaN(zoom);
            Double.isNaN(access$700);
            return zoom / access$700;
        }
    }

    /* access modifiers changed from: private */
    public PointF transformCoordTouchToBitmap(float x, float y, boolean clipToBitmap) {
        this.matrix.getValues(this.f11451m);
        float origW = (float) getDrawable().getIntrinsicWidth();
        float origH = (float) getDrawable().getIntrinsicHeight();
        float[] fArr = this.f11451m;
        float transX = fArr[2];
        float transY = fArr[5];
        float finalX = ((x - transX) * origW) / getImageWidth();
        float finalY = ((y - transY) * origH) / getImageHeight();
        if (clipToBitmap) {
            finalX = Math.min(Math.max(finalX, 0.0f), origW);
            finalY = Math.min(Math.max(finalY, 0.0f), origH);
        }
        return new PointF(finalX, finalY);
    }

    /* access modifiers changed from: private */
    public PointF transformCoordBitmapToTouch(float bx, float by) {
        this.matrix.getValues(this.f11451m);
        return new PointF(this.f11451m[2] + (getImageWidth() * (bx / ((float) getDrawable().getIntrinsicWidth()))), this.f11451m[5] + (getImageHeight() * (by / ((float) getDrawable().getIntrinsicHeight()))));
    }

    private class Fling implements Runnable {
        int currX;
        int currY;
        CompatScroller scroller;
        final /* synthetic */ TouchImageView this$0;

        Fling(TouchImageView touchImageView, int velocityX, int velocityY) {
            int maxX;
            int minX;
            int maxY;
            int maxY2;
            TouchImageView touchImageView2 = touchImageView;
            this.this$0 = touchImageView2;
            touchImageView2.setState(State.FLING);
            this.scroller = new CompatScroller(touchImageView.context);
            touchImageView.matrix.getValues(touchImageView.f11451m);
            int startX = (int) touchImageView.f11451m[2];
            int startY = (int) touchImageView.f11451m[5];
            if (touchImageView.getImageWidth() > ((float) touchImageView.viewWidth)) {
                minX = touchImageView.viewWidth - ((int) touchImageView.getImageWidth());
                maxX = 0;
            } else {
                maxX = startX;
                minX = startX;
            }
            if (touchImageView.getImageHeight() > ((float) touchImageView.viewHeight)) {
                maxY = 0;
                maxY2 = touchImageView.viewHeight - ((int) touchImageView.getImageHeight());
            } else {
                maxY = startY;
                maxY2 = startY;
            }
            this.scroller.fling(startX, startY, velocityX, velocityY, minX, maxX, maxY2, maxY);
            this.currX = startX;
            this.currY = startY;
        }

        public void cancelFling() {
            if (this.scroller != null) {
                this.this$0.setState(State.NONE);
                this.scroller.forceFinished(true);
            }
        }

        public void run() {
            if (this.this$0.touchImageViewListener != null) {
                this.this$0.touchImageViewListener.onMove();
            }
            if (this.scroller.isFinished()) {
                this.scroller = null;
            } else if (this.scroller.computeScrollOffset()) {
                int newX = this.scroller.getCurrX();
                int newY = this.scroller.getCurrY();
                this.currX = newX;
                this.currY = newY;
                this.this$0.matrix.postTranslate((float) (newX - this.currX), (float) (newY - this.currY));
                this.this$0.fixTrans();
                TouchImageView touchImageView = this.this$0;
                touchImageView.setImageMatrix(touchImageView.matrix);
                this.this$0.compatPostOnAnimation(this);
            }
        }
    }

    @TargetApi(9)
    private class CompatScroller {
        OverScroller overScroller;
        Scroller scroller;

        CompatScroller(Context context) {
            this.overScroller = new OverScroller(context);
        }

        /* access modifiers changed from: package-private */
        public void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX, int minY, int maxY) {
            this.overScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
        }

        /* access modifiers changed from: package-private */
        public void forceFinished(boolean finished) {
            this.overScroller.forceFinished(finished);
        }

        public boolean isFinished() {
            return this.overScroller.isFinished();
        }

        /* access modifiers changed from: package-private */
        public boolean computeScrollOffset() {
            this.overScroller.computeScrollOffset();
            return this.overScroller.computeScrollOffset();
        }

        /* access modifiers changed from: package-private */
        public int getCurrX() {
            return this.overScroller.getCurrX();
        }

        /* access modifiers changed from: package-private */
        public int getCurrY() {
            return this.overScroller.getCurrY();
        }
    }

    /* access modifiers changed from: private */
    @TargetApi(16)
    public void compatPostOnAnimation(Runnable runnable) {
        if (Build.VERSION.SDK_INT >= 16) {
            postOnAnimation(runnable);
        } else {
            postDelayed(runnable, 16);
        }
    }

    private class ZoomVariables {
        float focusX;
        float focusY;
        float scale;
        ImageView.ScaleType scaleType;

        ZoomVariables(float scale2, float focusX2, float focusY2, ImageView.ScaleType scaleType2) {
            this.scale = scale2;
            this.focusX = focusX2;
            this.focusY = focusY2;
            this.scaleType = scaleType2;
        }
    }

    private void printMatrixInfo() {
        float[] n = new float[9];
        this.matrix.getValues(n);
        Log.d(DEBUG, "Scale: " + n[0] + " TransX: " + n[2] + " TransY: " + n[5]);
    }
}
