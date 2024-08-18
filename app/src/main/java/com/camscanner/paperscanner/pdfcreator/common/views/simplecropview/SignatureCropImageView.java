package com.camscanner.paperscanner.pdfcreator.common.views.simplecropview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Registry;
import com.tom_roush.fontbox.afm.AFMParser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.utils.GeoUtil;
import com.camscanner.paperscanner.pdfcreator.common.views.simplecropview.animation.SimpleValueAnimator;
import com.camscanner.paperscanner.pdfcreator.common.views.simplecropview.animation.SimpleValueAnimatorListener;
import com.camscanner.paperscanner.pdfcreator.common.views.simplecropview.animation.ValueAnimatorV14;
import timber.log.Timber;

public class SignatureCropImageView extends AppCompatImageView {
    private static final int BLUE = -1878216961;
    private static final int DEBUG_TEXT_SIZE_IN_DP = 15;
    private static final int DEFAULT_ANIMATION_DURATION_MILLIS = 100;
    private static final float DEFAULT_INITIAL_FRAME_SCALE = 1.0f;
    private static final int EDGE_MAG_RADIUS = 15;
    private static final int FIRST_FINGER = 0;
    private static final int FRAME_STROKE_WEIGHT_IN_DP = 1;
    private static final int GUIDE_STROKE_WEIGHT_IN_DP = 1;
    private static final int HANDLE_SIZE_IN_DP = 34;
    private static final double LIMIT_COS_VALUE = 0.984d;
    private static final int LIMIT_MAG_FILTER_WIDTH = 30;
    private static final int LIMIT_MAG_FILTER_WIDTH2 = 20;
    private static final int MAG_ZOOM_RADIUS = 50;
    private static final int MAX_FINGERS = 2;
    private static final int MIN_FRAME_SIZE_IN_DP = 40;
    private static final int ORANGE = -1864070133;
    private static final int SECOND_FINGER = 1;
    private static final double SQRT_OF_2 = 1.4142135623730951d;
    private static final String TAG = SignatureCropImageView.class.getSimpleName();
    private static final int TRANSLUCENT_BLACK = -1157627904;
    private static final int TRANSLUCENT_WHITE = -1140850689;
    private static final int TRANSPARENT = 0;
    private static final int WHITE = -1;
    private final Interpolator DEFAULT_INTERPOLATOR;
    private float mAngle;
    private int mAnimationDurationMillis;
    private SimpleValueAnimator mAnimator;
    private int mBackgroundColor;
    private PointF mCenter;
    private Bitmap.CompressFormat mCompressFormat;
    private int mCompressQuality;
    private CropMode mCropMode;
    private PointF mCustomRatio;
    private ExecutorService mExecutor;
    private int mExifRotation;
    private int mFrameColor;
    /* access modifiers changed from: private */
    public RectF mFrameRect;
    private float mFrameStrokeWeight;
    private int mGuideColor;
    private float mGuideStrokeWeight;
    private int mHandleSize;
    private int mHandleValidFillColor;
    private int mHandleValidStrokeColor;
    private RectF mImageRect;
    private float mImgHeight;
    private float mImgWidth;
    private float mInitialFrameScale;
    private int mInputImageHeight;
    private int mInputImageWidth;
    private Interpolator mInterpolator;
    /* access modifiers changed from: private */
    public boolean mIsAnimating;
    private boolean mIsAnimationEnabled;
    private boolean mIsCropEnabled;
    private AtomicBoolean mIsCropping;
    private boolean mIsDebug;
    private boolean mIsEnabled;
    private boolean mIsHandleShadowEnabled;
    private boolean mIsInitialized;
    private AtomicBoolean mIsLoading;
    private boolean mIsRotating;
    private AtomicBoolean mIsSaving;
    private double mLastDist;
    private float mLastX;
    private float mLastY;
    private Matrix mMatrix;
    private float mMinFrameSize;
    private int mOutputHeight;
    private int mOutputImageHeight;
    private int mOutputImageWidth;
    private int mOutputMaxHeight;
    private int mOutputMaxWidth;
    private int mOutputWidth;
    private int mOverlayColor;
    private Paint mPaintBitmap;
    private Paint mPaintDebug;
    private Paint mPaintFrame;
    private Paint mPaintTranslucent;
    private Uri mSaveUri;
    private float mScale;
    private Uri mSourceUri;
    private TouchArea mTouchArea;
    private int mTouchPadding;
    private int mViewHeight;
    private int mViewWidth;
    private Bitmap m_bmpSource;
    private boolean multiTouch;

    public interface SimpleCropImageViewCallback {
        void onMoved(boolean z);
    }

    private enum TouchArea {
        OUT_OF_BOUNDS,
        CENTER,
        LEFT_TOP,
        RIGHT_TOP,
        LEFT_BOTTOM,
        RIGHT_BOTTOM,
        LEFT,
        TOP,
        RIGHT,
        BOTTOM
    }

    public SignatureCropImageView(Context context) {
        this(context, (AttributeSet) null);
    }

    public SignatureCropImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SignatureCropImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mViewWidth = 0;
        this.mViewHeight = 0;
        this.mScale = 1.0f;
        this.mAngle = 0.0f;
        this.mImgWidth = 0.0f;
        this.mImgHeight = 0.0f;
        this.mIsInitialized = false;
        this.mMatrix = null;
        this.mCenter = new PointF();
        this.mIsRotating = false;
        this.mIsAnimating = false;
        this.mAnimator = null;
        this.DEFAULT_INTERPOLATOR = new DecelerateInterpolator();
        this.mInterpolator = this.DEFAULT_INTERPOLATOR;
        this.mSourceUri = null;
        this.mSaveUri = null;
        this.mExifRotation = 0;
        this.mOutputWidth = 0;
        this.mOutputHeight = 0;
        this.mIsDebug = false;
        this.mCompressFormat = Bitmap.CompressFormat.PNG;
        this.mCompressQuality = 100;
        this.mInputImageWidth = 0;
        this.mInputImageHeight = 0;
        this.mOutputImageWidth = 0;
        this.mOutputImageHeight = 0;
        this.mIsLoading = new AtomicBoolean(false);
        this.mIsCropping = new AtomicBoolean(false);
        this.mIsSaving = new AtomicBoolean(false);
        this.mTouchArea = TouchArea.OUT_OF_BOUNDS;
        this.mCropMode = CropMode.FREE;
        this.mTouchPadding = 0;
        this.mIsCropEnabled = true;
        this.mIsEnabled = true;
        this.mCustomRatio = new PointF(1.0f, 1.0f);
        this.mFrameStrokeWeight = 2.0f;
        this.mGuideStrokeWeight = 2.0f;
        this.mIsAnimationEnabled = true;
        this.mAnimationDurationMillis = 100;
        this.mIsHandleShadowEnabled = true;
        this.mExecutor = Executors.newSingleThreadExecutor();
        float density = getDensity();
        this.mHandleSize = (int) (34.0f * density);
        this.mMinFrameSize = (float) ((int) (40.0f * density));
        this.mFrameStrokeWeight = density * 1.0f;
        this.mGuideStrokeWeight = density * 1.0f;
        this.mPaintFrame = new Paint();
        this.mPaintTranslucent = new Paint();
        this.mPaintBitmap = new Paint();
        this.mPaintBitmap.setFilterBitmap(true);
        this.mPaintDebug = new Paint();
        this.mPaintDebug.setAntiAlias(true);
        this.mPaintDebug.setStyle(Paint.Style.STROKE);
        this.mPaintDebug.setColor(-1);
        this.mPaintDebug.setTextSize(15.0f * density);
        this.mMatrix = new Matrix();
        this.mScale = 1.0f;
        this.mBackgroundColor = 0;
        this.mFrameColor = -1;
        this.mOverlayColor = TRANSLUCENT_BLACK;
        this.mHandleValidFillColor = BLUE;
        this.mHandleValidStrokeColor = -1;
        this.mGuideColor = TRANSLUCENT_WHITE;
        handleStyleable(context, attrs, defStyle, density);
    }

    public Parcelable onSaveInstanceState() {
        SavedState ss = new SavedState(super.onSaveInstanceState());
        ss.mode = this.mCropMode;
        ss.backgroundColor = this.mBackgroundColor;
        ss.overlayColor = this.mOverlayColor;
        ss.frameColor = this.mFrameColor;
        ss.handleSize = this.mHandleSize;
        ss.touchPadding = this.mTouchPadding;
        ss.minFrameSize = this.mMinFrameSize;
        ss.customRatioX = this.mCustomRatio.x;
        ss.customRatioY = this.mCustomRatio.y;
        ss.frameStrokeWeight = this.mFrameStrokeWeight;
        ss.guideStrokeWeight = this.mGuideStrokeWeight;
        ss.isCropEnabled = this.mIsCropEnabled;
        ss.handleColor = this.mHandleValidFillColor;
        ss.guideColor = this.mGuideColor;
        ss.initialFrameScale = this.mInitialFrameScale;
        ss.angle = this.mAngle;
        ss.isAnimationEnabled = this.mIsAnimationEnabled;
        ss.animationDuration = this.mAnimationDurationMillis;
        ss.exifRotation = this.mExifRotation;
        ss.sourceUri = this.mSourceUri;
        ss.saveUri = this.mSaveUri;
        ss.compressFormat = this.mCompressFormat;
        ss.compressQuality = this.mCompressQuality;
        ss.isDebug = this.mIsDebug;
        ss.outputMaxWidth = this.mOutputMaxWidth;
        ss.outputMaxHeight = this.mOutputMaxHeight;
        ss.outputWidth = this.mOutputWidth;
        ss.outputHeight = this.mOutputHeight;
        ss.isHandleShadowEnabled = this.mIsHandleShadowEnabled;
        ss.inputImageWidth = this.mInputImageWidth;
        ss.inputImageHeight = this.mInputImageHeight;
        ss.outputImageWidth = this.mOutputImageWidth;
        ss.outputImageHeight = this.mOutputImageHeight;
        return ss;
    }

    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        this.mCropMode = ss.mode;
        this.mBackgroundColor = ss.backgroundColor;
        this.mOverlayColor = ss.overlayColor;
        this.mFrameColor = ss.frameColor;
        this.mHandleSize = ss.handleSize;
        this.mTouchPadding = ss.touchPadding;
        this.mMinFrameSize = ss.minFrameSize;
        this.mCustomRatio = new PointF(ss.customRatioX, ss.customRatioY);
        this.mFrameStrokeWeight = ss.frameStrokeWeight;
        this.mGuideStrokeWeight = ss.guideStrokeWeight;
        this.mIsCropEnabled = ss.isCropEnabled;
        this.mHandleValidFillColor = ss.handleColor;
        this.mGuideColor = ss.guideColor;
        this.mInitialFrameScale = ss.initialFrameScale;
        this.mAngle = ss.angle;
        this.mIsAnimationEnabled = ss.isAnimationEnabled;
        this.mAnimationDurationMillis = ss.animationDuration;
        this.mExifRotation = ss.exifRotation;
        this.mSourceUri = ss.sourceUri;
        this.mSaveUri = ss.saveUri;
        this.mCompressFormat = ss.compressFormat;
        this.mCompressQuality = ss.compressQuality;
        this.mIsDebug = ss.isDebug;
        this.mOutputMaxWidth = ss.outputMaxWidth;
        this.mOutputMaxHeight = ss.outputMaxHeight;
        this.mOutputWidth = ss.outputWidth;
        this.mOutputHeight = ss.outputHeight;
        this.mIsHandleShadowEnabled = ss.isHandleShadowEnabled;
        this.mInputImageWidth = ss.inputImageWidth;
        this.mInputImageHeight = ss.inputImageHeight;
        this.mOutputImageWidth = ss.outputImageWidth;
        this.mOutputImageHeight = ss.outputImageHeight;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int viewWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int viewHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(viewWidth, viewHeight);
        this.mViewWidth = (viewWidth - getPaddingLeft()) - getPaddingRight();
        this.mViewHeight = (viewHeight - getPaddingTop()) - getPaddingBottom();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getDrawable() != null) {
            setupLayout(this.mViewWidth, this.mViewHeight);
        }
    }

    public void onDraw(Canvas canvas) {
        canvas.drawColor(this.mBackgroundColor);
        if (this.mIsInitialized) {
            setMatrix();
            Bitmap bm = getBitmap();
            if (bm != null && !bm.isRecycled()) {
                canvas.drawBitmap(bm, this.mMatrix, this.mPaintBitmap);
                drawCropFrame(canvas);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        this.mExecutor.shutdown();
        super.onDetachedFromWindow();
    }

    private void handleStyleable(Context context, AttributeSet attrs, int defStyle, float mDensity) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.scv_CropImageView, defStyle, 0);
        this.mCropMode = CropMode.FREE;
        try {
            Drawable drawable = ta.getDrawable(15);
            if (drawable != null) {
                setImageDrawable(drawable);
            }
            this.mBackgroundColor = ta.getColor(2, 0);
            this.mOverlayColor = ta.getColor(18, TRANSLUCENT_BLACK);
            this.mFrameColor = ta.getColor(5, -1);
            this.mHandleValidFillColor = ta.getColor(10, BLUE);
            this.mHandleValidStrokeColor = ta.getColor(14, -1);
            this.mGuideColor = ta.getColor(7, TRANSLUCENT_WHITE);
            this.mHandleSize = ta.getDimensionPixelSize(12, 34);
            this.mTouchPadding = ta.getDimensionPixelSize(19, 0);
            this.mFrameStrokeWeight = (float) ta.getDimensionPixelSize(6, (int) (mDensity * 1.0f));
            this.mGuideStrokeWeight = (float) ta.getDimensionPixelSize(8, (int) (mDensity * 1.0f));
            this.mIsCropEnabled = ta.getBoolean(3, true);
            this.mInitialFrameScale = constrain(ta.getFloat(16, 1.0f), 0.01f, 1.0f, 1.0f);
            this.mIsAnimationEnabled = ta.getBoolean(1, true);
            this.mAnimationDurationMillis = ta.getInt(0, 100);
            this.mIsHandleShadowEnabled = ta.getBoolean(11, true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ta.recycle();
        }
    }

    private void drawDebugInfo(Canvas canvas) {
        StringBuilder builder;
        int y;
        Paint.FontMetrics fontMetrics = this.mPaintDebug.getFontMetrics();
        this.mPaintDebug.measureText(AFMParser.CHARMETRICS_W);
        int textHeight = (int) (fontMetrics.descent - fontMetrics.ascent);
        int x = (int) (this.mImageRect.left + (((float) this.mHandleSize) * 0.5f * getDensity()));
        int y2 = (int) (this.mImageRect.top + ((float) textHeight) + (((float) this.mHandleSize) * 0.5f * getDensity()));
        StringBuilder builder2 = new StringBuilder();
        builder2.append("LOADED FROM: ");
        builder2.append(this.mSourceUri != null ? "Uri" : Registry.BUCKET_BITMAP);
        canvas.drawText(builder2.toString(), (float) x, (float) y2, this.mPaintDebug);
        StringBuilder builder3 = new StringBuilder();
        if (this.mSourceUri == null) {
            builder3.append("INPUT_IMAGE_SIZE: ");
            builder3.append((int) this.mImgWidth);
            builder3.append("x");
            builder3.append((int) this.mImgHeight);
            y = y2 + textHeight;
            canvas.drawText(builder3.toString(), (float) x, (float) y, this.mPaintDebug);
            builder = new StringBuilder();
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("INPUT_IMAGE_SIZE: ");
            sb.append(this.mInputImageWidth);
            sb.append("x");
            y = y2 + textHeight;
            canvas.drawText(sb.append(this.mInputImageHeight).toString(), (float) x, (float) y, this.mPaintDebug);
            builder = new StringBuilder();
        }
        builder.append("LOADED_IMAGE_SIZE: ");
        builder.append(getBitmap().getWidth());
        builder.append("x");
        builder.append(getBitmap().getHeight());
        int y3 = y + textHeight;
        canvas.drawText(builder.toString(), (float) x, (float) y3, this.mPaintDebug);
        StringBuilder builder4 = new StringBuilder();
        if (this.mOutputImageWidth > 0 && this.mOutputImageHeight > 0) {
            builder4.append("OUTPUT_IMAGE_SIZE: ");
            builder4.append(this.mOutputImageWidth);
            builder4.append("x");
            builder4.append(this.mOutputImageHeight);
            int y4 = y3 + textHeight;
            canvas.drawText(builder4.toString(), (float) x, (float) y4, this.mPaintDebug);
            StringBuilder sb2 = new StringBuilder();
            sb2.append("EXIF ROTATION: ");
            int y5 = y4 + textHeight;
            canvas.drawText(sb2.append(this.mExifRotation).toString(), (float) x, (float) y5, this.mPaintDebug);
            StringBuilder sb3 = new StringBuilder();
            sb3.append("CURRENT_ROTATION: ");
            y3 = y5 + textHeight;
            canvas.drawText(sb3.append((int) this.mAngle).toString(), (float) x, (float) y3, this.mPaintDebug);
        }
        int y6 = y3 + textHeight;
        canvas.drawText("FRAME_RECT: " + this.mFrameRect.toString(), (float) x, (float) y6, this.mPaintDebug);
        canvas.drawText("ACTUAL_CROP_RECT: " + getActualCropRect().toString(), (float) x, (float) (y6 + textHeight), this.mPaintDebug);
    }

    private void drawCropFrame(Canvas canvas) {
        if (this.mIsCropEnabled && !this.mIsRotating) {
            drawOverlay(canvas);
            drawFrame(canvas);
            drawEdgeHandles(canvas);
            drawMidHandles(canvas);
        }
    }

    private void drawOverlay(Canvas canvas) {
        this.mPaintTranslucent.setAntiAlias(true);
        this.mPaintTranslucent.setFilterBitmap(true);
        this.mPaintTranslucent.setColor(this.mOverlayColor);
        this.mPaintTranslucent.setStyle(Paint.Style.FILL);
        Path edgePath = new Path();
        edgePath.moveTo(this.mFrameRect.left, this.mFrameRect.top);
        edgePath.lineTo(this.mFrameRect.right, this.mFrameRect.top);
        edgePath.lineTo(this.mFrameRect.right, this.mFrameRect.bottom);
        edgePath.lineTo(this.mFrameRect.left, this.mFrameRect.bottom);
        edgePath.close();
        canvas.save();
        canvas.clipPath(edgePath, Region.Op.DIFFERENCE);
        canvas.drawColor(this.mOverlayColor);
        canvas.restore();
    }

    private void drawFrame(Canvas canvas) {
        this.mPaintFrame.setAntiAlias(true);
        this.mPaintFrame.setFilterBitmap(true);
        this.mPaintFrame.setStyle(Paint.Style.STROKE);
        this.mPaintFrame.setColor(this.mFrameColor);
        this.mPaintFrame.setStrokeWidth(this.mFrameStrokeWeight);
        canvas.drawRect(this.mFrameRect, this.mPaintFrame);
    }

    private void drawEdgeHandles(Canvas canvas) {
        int strokeColor = this.mHandleValidStrokeColor;
        int fillColor = this.mHandleValidFillColor;
        float density = getDensity();
        this.mPaintFrame.setStyle(Paint.Style.STROKE);
        this.mPaintFrame.setStrokeWidth(2.0f * density);
        this.mPaintFrame.setColor(strokeColor);
        canvas.drawCircle(this.mFrameRect.left, this.mFrameRect.top, (float) this.mHandleSize, this.mPaintFrame);
        canvas.drawCircle(this.mFrameRect.right, this.mFrameRect.top, (float) this.mHandleSize, this.mPaintFrame);
        canvas.drawCircle(this.mFrameRect.left, this.mFrameRect.bottom, (float) this.mHandleSize, this.mPaintFrame);
        canvas.drawCircle(this.mFrameRect.right, this.mFrameRect.bottom, (float) this.mHandleSize, this.mPaintFrame);
        this.mPaintFrame.setStyle(Paint.Style.FILL);
        this.mPaintFrame.setColor(fillColor);
        canvas.drawCircle(this.mFrameRect.left, this.mFrameRect.top, ((float) this.mHandleSize) - density, this.mPaintFrame);
        canvas.drawCircle(this.mFrameRect.right, this.mFrameRect.top, ((float) this.mHandleSize) - density, this.mPaintFrame);
        canvas.drawCircle(this.mFrameRect.left, this.mFrameRect.bottom, ((float) this.mHandleSize) - density, this.mPaintFrame);
        canvas.drawCircle(this.mFrameRect.right, this.mFrameRect.bottom, ((float) this.mHandleSize) - density, this.mPaintFrame);
    }

    private void drawMidHandles(Canvas canvas) {
        int strokeColor = this.mHandleValidStrokeColor;
        int fillColor = this.mHandleValidFillColor;
        float density = getDensity();
        this.mPaintFrame.setStyle(Paint.Style.STROKE);
        this.mPaintFrame.setStrokeWidth(density * 2.0f);
        this.mPaintFrame.setColor(strokeColor);
        canvas.drawCircle((this.mFrameRect.left + this.mFrameRect.right) / 2.0f, this.mFrameRect.top, (float) this.mHandleSize, this.mPaintFrame);
        canvas.drawCircle((this.mFrameRect.left + this.mFrameRect.right) / 2.0f, this.mFrameRect.bottom, (float) this.mHandleSize, this.mPaintFrame);
        canvas.drawCircle(this.mFrameRect.left, (this.mFrameRect.top + this.mFrameRect.bottom) / 2.0f, (float) this.mHandleSize, this.mPaintFrame);
        canvas.drawCircle(this.mFrameRect.right, (this.mFrameRect.top + this.mFrameRect.bottom) / 2.0f, (float) this.mHandleSize, this.mPaintFrame);
        this.mPaintFrame.setStyle(Paint.Style.FILL);
        this.mPaintFrame.setColor(fillColor);
        canvas.drawCircle((this.mFrameRect.left + this.mFrameRect.right) / 2.0f, this.mFrameRect.top, (float) this.mHandleSize, this.mPaintFrame);
        canvas.drawCircle((this.mFrameRect.left + this.mFrameRect.right) / 2.0f, this.mFrameRect.bottom, (float) this.mHandleSize, this.mPaintFrame);
        canvas.drawCircle(this.mFrameRect.left, (this.mFrameRect.top + this.mFrameRect.bottom) / 2.0f, (float) this.mHandleSize, this.mPaintFrame);
        canvas.drawCircle(this.mFrameRect.right, (this.mFrameRect.top + this.mFrameRect.bottom) / 2.0f, (float) this.mHandleSize, this.mPaintFrame);
    }

    private void setMatrix() {
        this.mMatrix.reset();
        this.mMatrix.setTranslate(this.mCenter.x - (this.mImgWidth * 0.5f), this.mCenter.y - (this.mImgHeight * 0.5f));
        Matrix matrix = this.mMatrix;
        float f = this.mScale;
        matrix.postScale(f, f, this.mCenter.x, this.mCenter.y);
        this.mMatrix.postRotate(this.mAngle, this.mCenter.x, this.mCenter.y);
    }

    private void setupLayout(int viewW, int viewH) {
        if (viewW != 0 && viewH != 0) {
            if (!this.mIsInitialized) {
                setCenter(new PointF(((float) getPaddingLeft()) + (((float) viewW) * 0.5f), ((float) getPaddingTop()) + (((float) viewH) * 0.5f)));
            }
            setScale(calcScale(viewW, viewH, this.mAngle));
            setMatrix();
            this.mImageRect = calcImageRect(new RectF(0.0f, 0.0f, this.mImgWidth, this.mImgHeight), this.mMatrix);
            if (!this.mIsInitialized) {
                calcEdgePoint();
            }
            this.mIsInitialized = true;
            invalidate();
        }
    }

    public void setEdgeRect(RectF rect) {
        if (rect == null) {
            rect = new RectF(0.001f, 0.001f, 0.999f, 0.999f);
        }
        this.mFrameRect = new RectF(rect);
    }

    public RectF getEdgeRect() {
        if (this.mFrameRect == null) {
            this.mFrameRect = new RectF(-0.001f, 0.001f, 0.999f, 0.999f);
        }
        if (this.mImageRect == null) {
            this.mImageRect = calcImageRect(new RectF(0.0f, 0.0f, this.mImgWidth, this.mImgHeight), this.mMatrix);
        }
        return new RectF(Math.min(Math.max(0.0f, (this.mFrameRect.left - this.mImageRect.left) / (this.mImgWidth * this.mScale)), 0.999f), Math.min(Math.max(0.0f, (this.mFrameRect.top - this.mImageRect.top) / (this.mImgHeight * this.mScale)), 0.999f), Math.min(Math.max(0.0f, (this.mFrameRect.right - this.mImageRect.left) / (this.mImgWidth * this.mScale)), 0.999f), Math.min(Math.max(0.0f, (this.mFrameRect.bottom - this.mImageRect.top) / (this.mImgHeight * this.mScale)), 0.999f));
    }

    private float calcScale(int viewW, int viewH, float angle) {
        Bitmap bitmap = this.m_bmpSource;
        if (bitmap == null) {
            return 1.0f;
        }
        this.mImgWidth = (float) bitmap.getWidth();
        this.mImgHeight = (float) this.m_bmpSource.getHeight();
        if (this.mImgWidth <= 0.0f) {
            this.mImgWidth = (float) viewW;
        }
        if (this.mImgHeight <= 0.0f) {
            this.mImgHeight = (float) viewH;
        }
        float viewRatio = ((float) viewW) / ((float) viewH);
        float imgRatio = getRotatedWidth(angle) / getRotatedHeight(angle);
        if (imgRatio >= viewRatio) {
            return ((float) viewW) / getRotatedWidth(angle);
        }
        if (imgRatio < viewRatio) {
            return ((float) viewH) / getRotatedHeight(angle);
        }
        return 1.0f;
    }

    public void calcEdgePoint() {
        RectF rectF = this.mFrameRect;
        if (rectF != null) {
            float width = this.mImgWidth;
            float height = this.mImgHeight;
            rectF.left *= this.mScale * width;
            this.mFrameRect.top *= this.mScale * height;
            this.mFrameRect.right *= this.mScale * width;
            this.mFrameRect.bottom *= this.mScale * height;
            if (this.mImageRect == null) {
                this.mImageRect = calcImageRect(new RectF(0.0f, 0.0f, this.mImgWidth, this.mImgHeight), this.mMatrix);
            }
            this.mFrameRect.offset(this.mImageRect.left, this.mImageRect.top);
        }
    }

    private RectF calcImageRect(RectF rect, Matrix matrix) {
        RectF applied = new RectF();
        matrix.mapRect(applied, rect);
        return applied;
    }

    private RectF calcFrameRect(RectF imageRect) {
        RectF rectF = imageRect;
        float frameW = getRatioX(imageRect.width());
        float frameH = getRatioY(imageRect.height());
        float imgRatio = imageRect.width() / imageRect.height();
        float frameRatio = frameW / frameH;
        float l = rectF.left;
        float t = rectF.top;
        float r = rectF.right;
        float b = rectF.bottom;
        if (frameRatio >= imgRatio) {
            l = rectF.left;
            r = rectF.right;
            float hy = (rectF.top + rectF.bottom) * 0.5f;
            float hh = (imageRect.width() / frameRatio) * 0.5f;
            t = hy - hh;
            b = hy + hh;
        } else if (frameRatio < imgRatio) {
            t = rectF.top;
            b = rectF.bottom;
            float hx = (rectF.left + rectF.right) * 0.5f;
            float hw = imageRect.height() * frameRatio * 0.5f;
            l = hx - hw;
            r = hx + hw;
        }
        float w = r - l;
        float h = b - t;
        float cx = (w / 2.0f) + l;
        float cy = (h / 2.0f) + t;
        float f = this.mInitialFrameScale;
        float sw = w * f;
        float sh = f * h;
        return new RectF(cx - (sw / 2.0f), cy - (sh / 2.0f), cx + (sw / 2.0f), (sh / 2.0f) + cy);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!this.mIsInitialized || !this.mIsCropEnabled || !this.mIsEnabled || this.mIsRotating || this.mIsAnimating || this.mIsLoading.get() || this.mIsCropping.get()) {
            return false;
        }
        int fingers = event.getPointerCount();
        Timber.tag(TAG).d("onTouchEvent %s %s", this.mTouchArea, event);
        if (fingers > 2) {
            return false;
        }
        int actionMasked = event.getActionMasked();
        if (actionMasked == 0) {
            onDown(event);
            return true;
        } else if (actionMasked == 1) {
            getParent().requestDisallowInterceptTouchEvent(false);
            onUp(event);
            return true;
        } else if (actionMasked == 2) {
            if (!this.multiTouch) {
                onMoveSingleTouch(event);
            } else if (fingers > 1) {
                onMoveMultiTouch(event);
            }
            if (this.mTouchArea != TouchArea.OUT_OF_BOUNDS) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            return true;
        } else if (actionMasked == 3) {
            getParent().requestDisallowInterceptTouchEvent(false);
            onCancel();
            return true;
        } else if (actionMasked == 5) {
            onPointerDown(event);
            return true;
        } else if (actionMasked != 6) {
            return false;
        } else {
            onPointerUp(event);
            return true;
        }
    }

    private void onPointerDown(MotionEvent e) {
        this.mLastDist = GeoUtil.distancePt2Pt(new PointF(e.getX(0), e.getY(0)), new PointF(e.getX(1), e.getY(1)));
        this.multiTouch = true;
    }

    private void onPointerUp(MotionEvent event) {
    }

    private void onMoveMultiTouch(MotionEvent e) {
        MotionEvent motionEvent = e;
        double dist = GeoUtil.distancePt2Pt(new PointF(motionEvent.getX(0), motionEvent.getY(0)), new PointF(motionEvent.getX(1), motionEvent.getY(1)));
        double diffDist = dist - this.mLastDist;
        double diff = (Math.signum(diffDist) * Math.abs(diffDist / 2.0d)) / SQRT_OF_2;
        float prevLeft = this.mFrameRect.left;
        float prevRight = this.mFrameRect.right;
        float prevTop = this.mFrameRect.top;
        float prevBottom = this.mFrameRect.bottom;
        RectF rectF = this.mFrameRect;
        double d = (double) rectF.left;
        double increaseHypotenuse = (double) -1;
        Double.isNaN(increaseHypotenuse);
        Double.isNaN(d);
        rectF.left = (float) (d + (increaseHypotenuse * diff));
        RectF rectF2 = this.mFrameRect;
        double d2 = (double) rectF2.top;
        double d3 = (double) -1;
        Double.isNaN(d3);
        Double.isNaN(d2);
        rectF2.top = (float) (d2 + (d3 * diff));
        RectF rectF3 = this.mFrameRect;
        double d4 = (double) rectF3.right;
        double d5 = (double) 1;
        Double.isNaN(d5);
        Double.isNaN(d4);
        rectF3.right = (float) (d4 + (d5 * diff));
        RectF rectF4 = this.mFrameRect;
        double d6 = (double) rectF4.bottom;
        double d7 = (double) 1;
        Double.isNaN(d7);
        Double.isNaN(d6);
        rectF4.bottom = (float) (d6 + (d7 * diff));
        if (isWidthTooSmall()) {
            RectF rectF5 = this.mFrameRect;
            rectF5.left = prevLeft;
            rectF5.right = prevRight;
        }
        if (isHeightTooSmall()) {
            RectF rectF6 = this.mFrameRect;
            rectF6.top = prevTop;
            rectF6.bottom = prevBottom;
        }
        checkScaleBounds();
        invalidate();
        this.mLastDist = dist;
    }

    private void onDown(MotionEvent e) {
        this.mLastX = e.getX();
        this.mLastY = e.getY();
        checkTouchArea(e.getX(), e.getY());
        invalidate();
    }

    private void onMoveSingleTouch(MotionEvent e) {
        float diffX = e.getX() - this.mLastX;
        float diffY = e.getY() - this.mLastY;
        switch (this.mTouchArea) {
            case CENTER:
                moveFrame(diffX, diffY);
                break;
            case LEFT_TOP:
                moveHandleLT(diffX, diffY);
                break;
            case RIGHT_TOP:
                moveHandleRT(diffX, diffY);
                break;
            case LEFT_BOTTOM:
                moveHandleLB(diffX, diffY);
                break;
            case RIGHT_BOTTOM:
                moveHandleRB(diffX, diffY);
                break;
            case TOP:
                moveHandleTop(0.0f, diffY);
                break;
            case RIGHT:
                moveHandleRight(diffX, 0.0f);
                break;
            case BOTTOM:
                moveHandleBottom(0.0f, diffY);
                break;
            case LEFT:
                moveHandleLeft(diffX, 0.0f);
                break;
        }
        invalidate();
        this.mLastX = e.getX();
        this.mLastY = e.getY();
    }

    private void onUp(MotionEvent e) {
        this.mTouchArea = TouchArea.OUT_OF_BOUNDS;
        this.multiTouch = false;
        invalidate();
    }

    private void onCancel() {
        this.multiTouch = false;
        this.mTouchArea = TouchArea.OUT_OF_BOUNDS;
        invalidate();
    }

    private void checkTouchArea(float x, float y) {
        if (isInsideCornerLeftTop(x, y)) {
            this.mTouchArea = TouchArea.LEFT_TOP;
        } else if (isInsideCornerRightTop(x, y)) {
            this.mTouchArea = TouchArea.RIGHT_TOP;
        } else if (isInsideCornerLeftBottom(x, y)) {
            this.mTouchArea = TouchArea.LEFT_BOTTOM;
        } else if (isInsideCornerRightBottom(x, y)) {
            this.mTouchArea = TouchArea.RIGHT_BOTTOM;
        } else if (isInsideTop(x, y)) {
            this.mTouchArea = TouchArea.TOP;
        } else if (isInsideRight(x, y)) {
            this.mTouchArea = TouchArea.RIGHT;
        } else if (isInsideBottom(x, y)) {
            this.mTouchArea = TouchArea.BOTTOM;
        } else if (isInsideLeft(x, y)) {
            this.mTouchArea = TouchArea.LEFT;
        } else if (isInsideFrame(x, y)) {
            this.mTouchArea = TouchArea.CENTER;
        } else {
            this.mTouchArea = TouchArea.OUT_OF_BOUNDS;
        }
    }

    private boolean isInsideFrame(float x, float y) {
        if (this.mFrameRect.left > x || this.mFrameRect.right < x || this.mFrameRect.top > y || this.mFrameRect.bottom < y) {
            return false;
        }
        this.mTouchArea = TouchArea.CENTER;
        return true;
    }

    private boolean isInsideCornerLeftTop(float x, float y) {
        float dx = x - this.mFrameRect.left;
        float dy = y - this.mFrameRect.top;
        return m14907sq((float) (this.mHandleSize + this.mTouchPadding)) >= (dx * dx) + (dy * dy);
    }

    private boolean isInsideCornerRightTop(float x, float y) {
        float dx = x - this.mFrameRect.right;
        float dy = y - this.mFrameRect.top;
        return m14907sq((float) (this.mHandleSize + this.mTouchPadding)) >= (dx * dx) + (dy * dy);
    }

    private boolean isInsideCornerLeftBottom(float x, float y) {
        float dx = x - this.mFrameRect.left;
        float dy = y - this.mFrameRect.bottom;
        return m14907sq((float) (this.mHandleSize + this.mTouchPadding)) >= (dx * dx) + (dy * dy);
    }

    private boolean isInsideCornerRightBottom(float x, float y) {
        float dx = x - this.mFrameRect.right;
        float dy = y - this.mFrameRect.bottom;
        return m14907sq((float) (this.mHandleSize + this.mTouchPadding)) >= (dx * dx) + (dy * dy);
    }

    private boolean isInsideTop(float x, float y) {
        float dx = x - ((this.mFrameRect.left + this.mFrameRect.right) / 2.0f);
        float dy = y - this.mFrameRect.top;
        return m14907sq((float) (this.mHandleSize + (this.mTouchPadding * 2))) >= (dx * dx) + (dy * dy);
    }

    private boolean isInsideRight(float x, float y) {
        float dx = x - this.mFrameRect.right;
        float dy = y - ((this.mFrameRect.top + this.mFrameRect.bottom) / 2.0f);
        return m14907sq((float) (this.mHandleSize + (this.mTouchPadding * 2))) >= (dx * dx) + (dy * dy);
    }

    private boolean isInsideBottom(float x, float y) {
        float dx = x - ((this.mFrameRect.left + this.mFrameRect.right) / 2.0f);
        float dy = y - this.mFrameRect.bottom;
        return m14907sq((float) (this.mHandleSize + (this.mTouchPadding * 2))) >= (dx * dx) + (dy * dy);
    }

    private boolean isInsideLeft(float x, float y) {
        float dx = x - this.mFrameRect.left;
        float dy = y - ((this.mFrameRect.top + this.mFrameRect.bottom) / 2.0f);
        return m14907sq((float) (this.mHandleSize + (this.mTouchPadding * 2))) >= (dx * dx) + (dy * dy);
    }

    private void moveFrame(float x, float y) {
        this.mFrameRect.offset(x, y);
        checkMoveBounds();
    }

    private void moveHandleLT(float diffX, float diffY) {
        if (this.mCropMode == CropMode.FREE) {
            this.mFrameRect.left += diffX;
            this.mFrameRect.top += diffY;
            if (isWidthTooSmall()) {
                this.mFrameRect.left -= this.mMinFrameSize - getFrameW();
            }
            if (isHeightTooSmall()) {
                this.mFrameRect.top -= this.mMinFrameSize - getFrameH();
            }
            checkScaleBounds();
        }
    }

    private void moveHandleRT(float diffX, float diffY) {
        if (this.mCropMode == CropMode.FREE) {
            this.mFrameRect.right += diffX;
            this.mFrameRect.top += diffY;
            if (isWidthTooSmall()) {
                this.mFrameRect.right += this.mMinFrameSize - getFrameW();
            }
            if (isHeightTooSmall()) {
                this.mFrameRect.top -= this.mMinFrameSize - getFrameH();
            }
            checkScaleBounds();
        }
    }

    private void moveHandleLB(float diffX, float diffY) {
        if (this.mCropMode == CropMode.FREE) {
            this.mFrameRect.left += diffX;
            this.mFrameRect.bottom += diffY;
            if (isWidthTooSmall()) {
                this.mFrameRect.left -= this.mMinFrameSize - getFrameW();
            }
            if (isHeightTooSmall()) {
                this.mFrameRect.bottom += this.mMinFrameSize - getFrameH();
            }
            checkScaleBounds();
        }
    }

    private void moveHandleRB(float diffX, float diffY) {
        if (this.mCropMode == CropMode.FREE) {
            this.mFrameRect.right += diffX;
            this.mFrameRect.bottom += diffY;
            if (isWidthTooSmall()) {
                this.mFrameRect.right += this.mMinFrameSize - getFrameW();
            }
            if (isHeightTooSmall()) {
                this.mFrameRect.bottom += this.mMinFrameSize - getFrameH();
            }
            checkScaleBounds();
        }
    }

    private void moveHandleTop(float diffX, float diffY) {
        if (this.mCropMode == CropMode.FREE) {
            this.mFrameRect.top += diffY;
            if (isHeightTooSmall()) {
                this.mFrameRect.top -= this.mMinFrameSize - getFrameH();
            }
            checkScaleBounds();
        }
    }

    private void moveHandleRight(float diffX, float diffY) {
        if (this.mCropMode == CropMode.FREE) {
            this.mFrameRect.right += diffX;
            if (isWidthTooSmall()) {
                this.mFrameRect.right += this.mMinFrameSize - getFrameW();
            }
            checkScaleBounds();
        }
    }

    private void moveHandleBottom(float diffX, float diffY) {
        if (this.mCropMode == CropMode.FREE) {
            this.mFrameRect.bottom += diffY;
            if (isHeightTooSmall()) {
                this.mFrameRect.bottom += this.mMinFrameSize - getFrameH();
            }
            checkScaleBounds();
        }
    }

    private void moveHandleLeft(float diffX, float diffY) {
        if (this.mCropMode == CropMode.FREE) {
            this.mFrameRect.left += diffX;
            if (isWidthTooSmall()) {
                this.mFrameRect.left -= this.mMinFrameSize - getFrameW();
            }
            checkScaleBounds();
        }
    }

    private void checkScaleBounds() {
        float lDiff = this.mFrameRect.left - this.mImageRect.left;
        float rDiff = this.mFrameRect.right - this.mImageRect.right;
        float tDiff = this.mFrameRect.top - this.mImageRect.top;
        float bDiff = this.mFrameRect.bottom - this.mImageRect.bottom;
        if (lDiff < 0.0f) {
            this.mFrameRect.left -= lDiff;
        }
        if (rDiff > 0.0f) {
            this.mFrameRect.right -= rDiff;
        }
        if (tDiff < 0.0f) {
            this.mFrameRect.top -= tDiff;
        }
        if (bDiff > 0.0f) {
            this.mFrameRect.bottom -= bDiff;
        }
    }

    private void checkMoveBounds() {
        float diff = this.mFrameRect.left - this.mImageRect.left;
        if (diff < 0.0f) {
            this.mFrameRect.left -= diff;
            this.mFrameRect.right -= diff;
        }
        float diff2 = this.mFrameRect.right - this.mImageRect.right;
        if (diff2 > 0.0f) {
            this.mFrameRect.left -= diff2;
            this.mFrameRect.right -= diff2;
        }
        float diff3 = this.mFrameRect.top - this.mImageRect.top;
        if (diff3 < 0.0f) {
            this.mFrameRect.top -= diff3;
            this.mFrameRect.bottom -= diff3;
        }
        float diff4 = this.mFrameRect.bottom - this.mImageRect.bottom;
        if (diff4 > 0.0f) {
            this.mFrameRect.top -= diff4;
            this.mFrameRect.bottom -= diff4;
        }
    }

    private boolean isWidthTooSmall() {
        return getFrameW() < this.mMinFrameSize;
    }

    private boolean isHeightTooSmall() {
        return getFrameH() < this.mMinFrameSize;
    }

    private void recalculateFrameRect(int durationMillis) {
        if (this.mImageRect != null) {
            if (this.mIsAnimating) {
                getAnimator().cancelAnimation();
            }
            RectF currentRect = new RectF(this.mFrameRect);
            RectF newRect = calcFrameRect(this.mImageRect);
            float diffL = newRect.left - currentRect.left;
            float diffT = newRect.top - currentRect.top;
            float diffR = newRect.right - currentRect.right;
            float diffB = newRect.bottom - currentRect.bottom;
            if (this.mIsAnimationEnabled) {
                SimpleValueAnimator animator = getAnimator();
                final RectF rectF = currentRect;
                final float f = diffL;
                final float f2 = diffT;
                final float f3 = diffR;
                final float f4 = diffB;
                final RectF rectF2 = newRect;
                SimpleValueAnimatorListener r0 = new SimpleValueAnimatorListener() {
                    public void onAnimationStarted() {
                        boolean unused = SignatureCropImageView.this.mIsAnimating = true;
                    }

                    public void onAnimationUpdated(float scale) {
                        RectF unused = SignatureCropImageView.this.mFrameRect = new RectF(rectF.left + (f * scale), rectF.top + (f2 * scale), rectF.right + (f3 * scale), rectF.bottom + (f4 * scale));
                        SignatureCropImageView.this.invalidate();
                    }

                    public void onAnimationFinished() {
                        RectF unused = SignatureCropImageView.this.mFrameRect = rectF2;
                        SignatureCropImageView.this.invalidate();
                        boolean unused2 = SignatureCropImageView.this.mIsAnimating = false;
                    }
                };
                animator.addAnimatorListener(r0);
                animator.startAnimation((long) durationMillis);
                return;
            }
            this.mFrameRect = calcFrameRect(this.mImageRect);
            invalidate();
        }
    }

    private float getRatioX(float w) {
        switch (this.mCropMode) {
            case FIT_IMAGE:
                return this.mImageRect.width();
            case FREE:
                return w;
            case RATIO_4_3:
                return 4.0f;
            case RATIO_3_4:
                return 3.0f;
            case RATIO_16_9:
                return 16.0f;
            case RATIO_9_16:
                return 9.0f;
            case SQUARE:
            case CIRCLE:
            case CIRCLE_SQUARE:
                return 1.0f;
            case CUSTOM:
                return this.mCustomRatio.x;
            default:
                return w;
        }
    }

    private float getRatioY(float h) {
        switch (this.mCropMode) {
            case FIT_IMAGE:
                return this.mImageRect.height();
            case FREE:
                return h;
            case RATIO_4_3:
                return 3.0f;
            case RATIO_3_4:
                return 4.0f;
            case RATIO_16_9:
                return 9.0f;
            case RATIO_9_16:
                return 16.0f;
            case SQUARE:
            case CIRCLE:
            case CIRCLE_SQUARE:
                return 1.0f;
            case CUSTOM:
                return this.mCustomRatio.y;
            default:
                return h;
        }
    }

    private float getRatioX() {
        switch (this.mCropMode) {
            case FIT_IMAGE:
                return this.mImageRect.width();
            case FREE:
            default:
                return 1.0f;
            case RATIO_4_3:
                return 4.0f;
            case RATIO_3_4:
                return 3.0f;
            case RATIO_16_9:
                return 16.0f;
            case RATIO_9_16:
                return 9.0f;
            case SQUARE:
            case CIRCLE:
            case CIRCLE_SQUARE:
                return 1.0f;
            case CUSTOM:
                return this.mCustomRatio.x;
        }
    }

    private float getRatioY() {
        switch (this.mCropMode) {
            case FIT_IMAGE:
                return this.mImageRect.height();
            case FREE:
            default:
                return 1.0f;
            case RATIO_4_3:
                return 3.0f;
            case RATIO_3_4:
                return 4.0f;
            case RATIO_16_9:
                return 9.0f;
            case RATIO_9_16:
                return 16.0f;
            case SQUARE:
            case CIRCLE:
            case CIRCLE_SQUARE:
                return 1.0f;
            case CUSTOM:
                return this.mCustomRatio.y;
        }
    }

    private float getDensity() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) getContext().getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.density;
    }

    /* renamed from: sq */
    private float m14907sq(float value) {
        return value * value;
    }

    private float constrain(float val, float min, float max, float defaultVal) {
        if (val < min || val > max) {
            return defaultVal;
        }
        return val;
    }

    private Bitmap getBitmap() {
        Drawable d = getDrawable();
        if (d == null || !(d instanceof BitmapDrawable)) {
            return null;
        }
        return ((BitmapDrawable) d).getBitmap();
    }

    private float getRotatedWidth(float angle) {
        return getRotatedWidth(angle, this.mImgWidth, this.mImgHeight);
    }

    private float getRotatedWidth(float angle, float width, float height) {
        return angle % 180.0f == 0.0f ? width : height;
    }

    private float getRotatedHeight(float angle) {
        return getRotatedHeight(angle, this.mImgWidth, this.mImgHeight);
    }

    private float getRotatedHeight(float angle, float width, float height) {
        return angle % 180.0f == 0.0f ? height : width;
    }

    private Bitmap getRotatedBitmap(Bitmap bitmap) {
        Matrix rotateMatrix = new Matrix();
        rotateMatrix.setRotate(this.mAngle, (float) (bitmap.getWidth() / 2), (float) (bitmap.getHeight() / 2));
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), rotateMatrix, true);
    }

    private SimpleValueAnimator getAnimator() {
        setupAnimatorIfNeeded();
        return this.mAnimator;
    }

    private void setupAnimatorIfNeeded() {
        if (this.mAnimator == null) {
            this.mAnimator = new ValueAnimatorV14(this.mInterpolator);
        }
    }

    private Rect calcCropRect(int originalImageWidth, int originalImageHeight) {
        int i = originalImageWidth;
        int i2 = originalImageHeight;
        float scaleToOriginal = getRotatedWidth(this.mAngle, (float) i, (float) i2) / this.mImageRect.width();
        float offsetX = this.mImageRect.left * scaleToOriginal;
        float offsetY = this.mImageRect.top * scaleToOriginal;
        int left = Math.round((this.mFrameRect.left * scaleToOriginal) - offsetX);
        int top = Math.round((this.mFrameRect.top * scaleToOriginal) - offsetY);
        int right = Math.round((this.mFrameRect.right * scaleToOriginal) - offsetX);
        int bottom = Math.round((this.mFrameRect.bottom * scaleToOriginal) - offsetY);
        return new Rect(Math.max(left, 0), Math.max(top, 0), Math.min(right, Math.round(getRotatedWidth(this.mAngle, (float) i, (float) i2))), Math.min(bottom, Math.round(getRotatedHeight(this.mAngle, (float) i, (float) i2))));
    }

    public void setImageBitmap(Bitmap bitmap) {
        super.setImageBitmap(bitmap);
        this.m_bmpSource = bitmap;
    }

    public void setImageResource(int resId) {
        this.mIsInitialized = false;
        resetImageInfo();
        super.setImageResource(resId);
        updateLayout();
    }

    public void setImageDrawable(Drawable drawable) {
        this.mIsInitialized = false;
        resetImageInfo();
        setImageDrawableInternal(drawable);
    }

    private void setImageDrawableInternal(Drawable drawable) {
        super.setImageDrawable(drawable);
        updateLayout();
    }

    public void setImageURI(Uri uri) {
        this.mIsInitialized = false;
        super.setImageURI(uri);
        updateLayout();
    }

    private void updateLayout() {
        if (getDrawable() != null) {
            setupLayout(this.mViewWidth, this.mViewHeight);
        }
    }

    private void resetImageInfo() {
        if (!this.mIsLoading.get()) {
            this.mSourceUri = null;
            this.mSaveUri = null;
            this.mInputImageWidth = 0;
            this.mInputImageHeight = 0;
            this.mOutputImageWidth = 0;
            this.mOutputImageHeight = 0;
            this.mAngle = (float) this.mExifRotation;
        }
    }

    public Bitmap getCircularBitmap(Bitmap square) {
        if (square == null) {
            return null;
        }
        Bitmap output = Bitmap.createBitmap(square.getWidth(), square.getHeight(), Bitmap.Config.ARGB_8888);
        Rect rect = new Rect(0, 0, square.getWidth(), square.getHeight());
        Canvas canvas = new Canvas(output);
        int halfWidth = square.getWidth() / 2;
        int halfHeight = square.getHeight() / 2;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        canvas.drawCircle((float) halfWidth, (float) halfHeight, (float) Math.min(halfWidth, halfHeight), paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(square, rect, rect, paint);
        return output;
    }

    public RectF getActualCropRect() {
        float offsetX = this.mImageRect.left / this.mScale;
        float offsetY = this.mImageRect.top / this.mScale;
        return new RectF(Math.max(0.0f, (this.mFrameRect.left / this.mScale) - offsetX), Math.max(0.0f, (this.mFrameRect.top / this.mScale) - offsetY), Math.min(this.mImageRect.right / this.mScale, (this.mFrameRect.right / this.mScale) - offsetX), Math.min(this.mImageRect.bottom / this.mScale, (this.mFrameRect.bottom / this.mScale) - offsetY));
    }

    private RectF applyInitialFrameRect(RectF initialFrameRect) {
        RectF frameRect = new RectF();
        frameRect.set(initialFrameRect.left * this.mScale, initialFrameRect.top * this.mScale, initialFrameRect.right * this.mScale, initialFrameRect.bottom * this.mScale);
        frameRect.offset(this.mImageRect.left, this.mImageRect.top);
        frameRect.set(Math.max(this.mImageRect.left, frameRect.left), Math.max(this.mImageRect.top, frameRect.top), Math.min(this.mImageRect.right, frameRect.right), Math.min(this.mImageRect.bottom, frameRect.bottom));
        return frameRect;
    }

    public void setCropMode(CropMode mode, int durationMillis) {
        if (mode == CropMode.CUSTOM) {
            setCustomRatio(1, 1);
            return;
        }
        this.mCropMode = mode;
        recalculateFrameRect(durationMillis);
    }

    public void setCropMode(CropMode mode) {
        setCropMode(mode, this.mAnimationDurationMillis);
    }

    public void setCustomRatio(int ratioX, int ratioY, int durationMillis) {
        if (ratioX != 0 && ratioY != 0) {
            this.mCropMode = CropMode.CUSTOM;
            this.mCustomRatio = new PointF((float) ratioX, (float) ratioY);
            recalculateFrameRect(durationMillis);
        }
    }

    public void setCustomRatio(int ratioX, int ratioY) {
        setCustomRatio(ratioX, ratioY, this.mAnimationDurationMillis);
    }

    public void setOverlayColor(int overlayColor) {
        this.mOverlayColor = overlayColor;
        invalidate();
    }

    public void setFrameColor(int frameColor) {
        this.mFrameColor = frameColor;
        invalidate();
    }

    public void setHandleColor(int handleColor) {
        this.mHandleValidFillColor = handleColor;
        invalidate();
    }

    public void setGuideColor(int guideColor) {
        this.mGuideColor = guideColor;
        invalidate();
    }

    public void setBackgroundColor(int bgColor) {
        this.mBackgroundColor = bgColor;
        invalidate();
    }

    public void setMinFrameSizeInDp(int minDp) {
        this.mMinFrameSize = ((float) minDp) * getDensity();
    }

    public void setMinFrameSizeInPx(int minPx) {
        this.mMinFrameSize = (float) minPx;
    }

    public void setHandleSizeInDp(int handleDp) {
        this.mHandleSize = (int) (((float) handleDp) * getDensity());
    }

    public void setTouchPaddingInDp(int paddingDp) {
        this.mTouchPadding = (int) (((float) paddingDp) * getDensity());
    }

    public void setFrameStrokeWeightInDp(int weightDp) {
        this.mFrameStrokeWeight = ((float) weightDp) * getDensity();
        invalidate();
    }

    public void setGuideStrokeWeightInDp(int weightDp) {
        this.mGuideStrokeWeight = ((float) weightDp) * getDensity();
        invalidate();
    }

    public void setCropEnabled(boolean enabled) {
        this.mIsCropEnabled = enabled;
        invalidate();
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.mIsEnabled = enabled;
    }

    public void setInitialFrameScale(float initialScale) {
        this.mInitialFrameScale = constrain(initialScale, 0.01f, 1.0f, 1.0f);
    }

    public void setAnimationEnabled(boolean enabled) {
        this.mIsAnimationEnabled = enabled;
    }

    public void setAnimationDuration(int durationMillis) {
        this.mAnimationDurationMillis = durationMillis;
    }

    public void setInterpolator(Interpolator interpolator) {
        this.mInterpolator = interpolator;
        this.mAnimator = null;
        setupAnimatorIfNeeded();
    }

    public void setDebug(boolean debug) {
        this.mIsDebug = debug;
        invalidate();
    }

    public void setOutputWidth(int outputWidth) {
        this.mOutputWidth = outputWidth;
        this.mOutputHeight = 0;
    }

    public void setOutputHeight(int outputHeight) {
        this.mOutputHeight = outputHeight;
        this.mOutputWidth = 0;
    }

    public void setOutputMaxSize(int maxWidth, int maxHeight) {
        this.mOutputMaxWidth = maxWidth;
        this.mOutputMaxHeight = maxHeight;
    }

    public void setCompressFormat(Bitmap.CompressFormat format) {
        this.mCompressFormat = format;
    }

    public void setCompressQuality(int quality) {
        this.mCompressQuality = quality;
    }

    public void setHandleShadowEnabled(boolean handleShadowEnabled) {
        this.mIsHandleShadowEnabled = handleShadowEnabled;
    }

    public boolean isCropping() {
        return this.mIsCropping.get();
    }

    public Uri getSourceUri() {
        return this.mSourceUri;
    }

    public Uri getSaveUri() {
        return this.mSaveUri;
    }

    public boolean isSaving() {
        return this.mIsSaving.get();
    }

    private void setScale(float mScale2) {
        this.mScale = mScale2;
    }

    public float getScale() {
        return this.mScale;
    }

    private void setCenter(PointF mCenter2) {
        this.mCenter = mCenter2;
    }

    private float getFrameW() {
        return this.mFrameRect.right - this.mFrameRect.left;
    }

    private float getFrameH() {
        return this.mFrameRect.bottom - this.mFrameRect.top;
    }

    public enum CropMode {
        FIT_IMAGE(0),
        RATIO_4_3(1),
        RATIO_3_4(2),
        SQUARE(3),
        RATIO_16_9(4),
        RATIO_9_16(5),
        FREE(6),
        CUSTOM(7),
        CIRCLE(8),
        CIRCLE_SQUARE(9);
        

        /* renamed from: ID */
        private final int f14680ID;

        private CropMode(int id) {
            this.f14680ID = id;
        }

        public int getId() {
            return this.f14680ID;
        }
    }

    public enum RotateDegrees {
        ROTATE_90D(90),
        ROTATE_180D(180),
        ROTATE_270D(270),
        ROTATE_M90D(-90),
        ROTATE_M180D(-180),
        ROTATE_M270D(-270);
        
        private final int VALUE;

        private RotateDegrees(int value) {
            this.VALUE = value;
        }

        public int getValue() {
            return this.VALUE;
        }
    }

    public float getAngle() {
        return this.mAngle;
    }

    public static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
            public SavedState createFromParcel(Parcel inParcel) {
                return new SavedState(inParcel);
            }

            public SavedState[] newArray(int inSize) {
                return new SavedState[inSize];
            }
        };
        float angle;
        int animationDuration;
        int backgroundColor;
        Bitmap.CompressFormat compressFormat;
        int compressQuality;
        float customRatioX;
        float customRatioY;
        int exifRotation;
        int frameColor;
        float frameStrokeWeight;
        int guideColor;
        float guideStrokeWeight;
        int handleColor;
        int handleSize;
        float initialFrameScale;
        int inputImageHeight;
        int inputImageWidth;
        boolean isAnimationEnabled;
        boolean isCropEnabled;
        boolean isDebug;
        boolean isHandleShadowEnabled;
        float minFrameSize;
        CropMode mode;
        int outputHeight;
        int outputImageHeight;
        int outputImageWidth;
        int outputMaxHeight;
        int outputMaxWidth;
        int outputWidth;
        int overlayColor;
        Uri saveUri;
        boolean showGuide;
        boolean showHandle;
        Uri sourceUri;
        int touchPadding;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.mode = (CropMode) in.readSerializable();
            this.backgroundColor = in.readInt();
            this.overlayColor = in.readInt();
            this.frameColor = in.readInt();
            boolean z = true;
            this.showGuide = in.readInt() != 0;
            this.showHandle = in.readInt() != 0;
            this.handleSize = in.readInt();
            this.touchPadding = in.readInt();
            this.minFrameSize = in.readFloat();
            this.customRatioX = in.readFloat();
            this.customRatioY = in.readFloat();
            this.frameStrokeWeight = in.readFloat();
            this.guideStrokeWeight = in.readFloat();
            this.isCropEnabled = in.readInt() != 0;
            this.handleColor = in.readInt();
            this.guideColor = in.readInt();
            this.initialFrameScale = in.readFloat();
            this.angle = in.readFloat();
            this.isAnimationEnabled = in.readInt() != 0;
            this.animationDuration = in.readInt();
            this.exifRotation = in.readInt();
            this.sourceUri = (Uri) in.readParcelable(Uri.class.getClassLoader());
            this.saveUri = (Uri) in.readParcelable(Uri.class.getClassLoader());
            this.compressFormat = (Bitmap.CompressFormat) in.readSerializable();
            this.compressQuality = in.readInt();
            this.isDebug = in.readInt() != 0;
            this.outputMaxWidth = in.readInt();
            this.outputMaxHeight = in.readInt();
            this.outputWidth = in.readInt();
            this.outputHeight = in.readInt();
            this.isHandleShadowEnabled = in.readInt() == 0 ? false : z;
            this.inputImageWidth = in.readInt();
            this.inputImageHeight = in.readInt();
            this.outputImageWidth = in.readInt();
            this.outputImageHeight = in.readInt();
        }

        public void writeToParcel(Parcel out, int flag) {
            super.writeToParcel(out, flag);
            out.writeSerializable(this.mode);
            out.writeInt(this.backgroundColor);
            out.writeInt(this.overlayColor);
            out.writeInt(this.frameColor);
            out.writeInt(this.showGuide ? 1 : 0);
            out.writeInt(this.showHandle ? 1 : 0);
            out.writeInt(this.handleSize);
            out.writeInt(this.touchPadding);
            out.writeFloat(this.minFrameSize);
            out.writeFloat(this.customRatioX);
            out.writeFloat(this.customRatioY);
            out.writeFloat(this.frameStrokeWeight);
            out.writeFloat(this.guideStrokeWeight);
            out.writeInt(this.isCropEnabled ? 1 : 0);
            out.writeInt(this.handleColor);
            out.writeInt(this.guideColor);
            out.writeFloat(this.initialFrameScale);
            out.writeFloat(this.angle);
            out.writeInt(this.isAnimationEnabled ? 1 : 0);
            out.writeInt(this.animationDuration);
            out.writeInt(this.exifRotation);
            out.writeParcelable(this.sourceUri, flag);
            out.writeParcelable(this.saveUri, flag);
            out.writeSerializable(this.compressFormat);
            out.writeInt(this.compressQuality);
            out.writeInt(this.isDebug ? 1 : 0);
            out.writeInt(this.outputMaxWidth);
            out.writeInt(this.outputMaxHeight);
            out.writeInt(this.outputWidth);
            out.writeInt(this.outputHeight);
            out.writeInt(this.isHandleShadowEnabled ? 1 : 0);
            out.writeInt(this.inputImageWidth);
            out.writeInt(this.inputImageHeight);
            out.writeInt(this.outputImageWidth);
            out.writeInt(this.outputImageHeight);
        }
    }
}
