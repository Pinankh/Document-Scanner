package com.camscanner.paperscanner.pdfcreator.common.views.simplecropview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Color;
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
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Registry;
import com.tom_roush.fontbox.afm.AFMParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import org.opencv.core.Point;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.utils.BitmapUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.GeoUtil;
import com.camscanner.paperscanner.pdfcreator.common.views.simplecropview.animation.SimpleValueAnimator;
import com.camscanner.paperscanner.pdfcreator.common.views.simplecropview.animation.SimpleValueAnimatorListener;
import com.camscanner.paperscanner.pdfcreator.common.views.simplecropview.animation.ValueAnimatorV14;
import com.camscanner.paperscanner.pdfcreator.common.views.simplecropview.util.SimpleCropUtils;
import timber.log.Timber;

public class SimpleCropImageView extends AppCompatImageView {
    private static final int BLUE = -1878216961;
    private static final int DEBUG_TEXT_SIZE_IN_DP = 15;
    private static final int DEFAULT_ANIMATION_DURATION_MILLIS = 100;
    private static final float DEFAULT_INITIAL_FRAME_SCALE = 1.0f;
    private static final int FIRST_FINGER = 0;
    private static final int FRAME_STROKE_WEIGHT_IN_DP = 1;
    private static final int GUIDE_STROKE_WEIGHT_IN_DP = 1;
    private static final int HANDLE_SIZE_IN_DP = 34;
    private static final int MAG_ZOOM_RADIUS = 50;
    private static final int MAX_FINGERS = 2;
    private static final int MIN_FRAME_SIZE_IN_DP = 40;
    private static final int ORANGE = -1864070133;
    private static final int SECOND_FINGER = 1;
    private static final double SQRT_OF_2 = 1.4142135623730951d;
    private static final String TAG = SimpleCropImageView.class.getSimpleName();
    private static final int TRANSLUCENT_BLACK = -1157627904;
    private static final int TRANSLUCENT_WHITE = -1140850689;
    private static final int TRANSPARENT = 0;
    private static final int WHITE = -1;
    private final Interpolator DEFAULT_INTERPOLATOR;
    private SimpleCropImageViewCallback cropViewCallback;

    public float mAngle;
    private int mAnimationDurationMillis;
    private SimpleValueAnimator mAnimator;
    private int mBackgroundColor;
    private PointF mCenter;
    private Bitmap.CompressFormat mCompressFormat;
    private int mCompressQuality;
    private CropMode mCropMode;
    private PointF mCustomRatio;
    private PointF[] mEdgePoints;
    private ExecutorService mExecutor;
    private int mExifRotation;
    private int mFrameColor;

    public RectF mFrameRect;
    private float mFrameStrokeWeight;
    private int mGuideColor;
    private float mGuideStrokeWeight;
    private float mHandleHalfStroke;
    private int mHandleInvalidFillColor;
    private int mHandleInvalidStrokeColor;
    private int mHandleSize;
    private float mHandleStroke;
    private int mHandleValidFillColor;
    private int mHandleValidStrokeColor;
    private Handler mHandler;
    private RectF mImageRect;
    private float mImgHeight;
    private float mImgWidth;
    private float mInitialFrameScale;
    private int mInputImageHeight;
    private int mInputImageWidth;
    private Interpolator mInterpolator;

    public boolean mIsAnimating;
    private boolean mIsAnimationEnabled;
    private boolean mIsCentered;
    private boolean mIsCropEnabled;
    private AtomicBoolean mIsCropping;
    private boolean mIsDebug;
    private boolean mIsEnabled;
    private boolean mIsHandleShadowEnabled;

    public boolean mIsInitialized;
    private AtomicBoolean mIsLoading;

    public boolean mIsRotating;
    private AtomicBoolean mIsSaving;
    private double mLastDist;
    private float mLastX;
    private float mLastY;
    private Matrix mMatrix;
    private PointF[] mMidPoints;
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
    private Paint mPaintInnerTrans;
    private Paint mPaintTranslucent;
    private Uri mSaveUri;

    public float mScale;
    private Uri mSourceUri;
    private TouchArea mTouchArea;
    private int mTouchPadding;

    public int mViewHeight;

    public int mViewWidth;
    private boolean[] m_bSelHLines;
    private boolean[] m_bSelVLines;
    private Bitmap m_bmpRotate;
    private Bitmap m_bmpSource;
    private int m_nCurSelHLine;
    private int m_nCurSelVLine;
    private boolean multiTouch;
    private boolean wasMoved;

    public interface SimpleCropImageViewCallback {
        ImageView lensView();

        void onMove(float f, float f2, RectF rectF);

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

    public void setCallback(SimpleCropImageViewCallback callback) {
        this.cropViewCallback = callback;
    }

    public SimpleCropImageView(Context context) {
        this(context, (AttributeSet) null);
    }

    public SimpleCropImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleCropImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mViewWidth = 0;
        this.mViewHeight = 0;
        this.mScale = 1.0f;
        this.mAngle = 0.0f;
        this.mImgWidth = 0.0f;
        this.mImgHeight = 0.0f;
        this.mIsInitialized = false;
        this.mIsCentered = false;
        this.mMatrix = null;
        this.mCenter = new PointF();
        this.mIsRotating = false;
        this.mIsAnimating = false;
        this.mAnimator = null;
        this.DEFAULT_INTERPOLATOR = new DecelerateInterpolator();
        this.mInterpolator = this.DEFAULT_INTERPOLATOR;
        this.mHandler = new Handler(Looper.getMainLooper());
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
        this.mHandleStroke = (float) ((int) (2.0f * density));
        this.mHandleHalfStroke = (float) ((int) density);
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
        this.mPaintInnerTrans = new Paint();
        this.mMatrix = new Matrix();
        this.mScale = 1.0f;
        this.mBackgroundColor = 0;
        this.mFrameColor = -1;
        this.mOverlayColor = TRANSLUCENT_BLACK;
        this.mHandleValidFillColor = BLUE;
        this.mHandleInvalidFillColor = ORANGE;
        this.mHandleValidStrokeColor = -1;
        this.mHandleInvalidStrokeColor = ORANGE;
        this.mGuideColor = TRANSLUCENT_WHITE;
        this.wasMoved = false;
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


    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int viewWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int viewHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(viewWidth, viewHeight);
        this.mViewWidth = (viewWidth - getPaddingLeft()) - getPaddingRight();
        this.mViewHeight = (viewHeight - getPaddingTop()) - getPaddingBottom();
    }


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

    public boolean isInitialized() {
        return this.mIsInitialized;
    }


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
            this.mHandleInvalidFillColor = ta.getColor(9, ORANGE);
            this.mHandleValidStrokeColor = ta.getColor(14, -1);
            this.mHandleInvalidStrokeColor = ta.getColor(13, ORANGE);
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
        edgePath.moveTo(this.mEdgePoints[0].x, this.mEdgePoints[0].y);
        edgePath.lineTo(this.mEdgePoints[1].x, this.mEdgePoints[1].y);
        edgePath.lineTo(this.mEdgePoints[2].x, this.mEdgePoints[2].y);
        edgePath.lineTo(this.mEdgePoints[3].x, this.mEdgePoints[3].y);
        edgePath.close();
        canvas.save();
        canvas.clipPath(edgePath, Region.Op.DIFFERENCE);
        canvas.drawColor(this.mOverlayColor);
        canvas.restore();
    }

    private void drawFrame(Canvas canvas) {
        int frameColor;
        if (isValidPoints()) {
            frameColor = this.mHandleValidStrokeColor;
        } else {
            frameColor = this.mHandleInvalidStrokeColor;
        }
        this.mPaintFrame.setAntiAlias(true);
        this.mPaintFrame.setFilterBitmap(true);
        this.mPaintFrame.setStyle(Paint.Style.STROKE);
        this.mPaintFrame.setColor(frameColor);
        this.mPaintFrame.setStrokeWidth(this.mFrameStrokeWeight);
        Canvas canvas2 = canvas;
        canvas2.drawLine(this.mEdgePoints[0].x, this.mEdgePoints[0].y, this.mEdgePoints[1].x, this.mEdgePoints[1].y, this.mPaintFrame);
        canvas2.drawLine(this.mEdgePoints[1].x, this.mEdgePoints[1].y, this.mEdgePoints[2].x, this.mEdgePoints[2].y, this.mPaintFrame);
        canvas2.drawLine(this.mEdgePoints[2].x, this.mEdgePoints[2].y, this.mEdgePoints[3].x, this.mEdgePoints[3].y, this.mPaintFrame);
        canvas2.drawLine(this.mEdgePoints[3].x, this.mEdgePoints[3].y, this.mEdgePoints[0].x, this.mEdgePoints[0].y, this.mPaintFrame);
    }

    private void drawEdgeHandles(Canvas canvas) {
        int fillColor;
        int strokeColor;
        if (isValidPoints()) {
            strokeColor = this.mHandleValidStrokeColor;
            fillColor = this.mHandleValidFillColor;
        } else {
            strokeColor = this.mHandleInvalidStrokeColor;
            fillColor = this.mHandleInvalidFillColor;
        }
        this.mPaintFrame.setStyle(Paint.Style.STROKE);
        this.mPaintFrame.setStrokeWidth(this.mHandleStroke);
        this.mPaintFrame.setColor(strokeColor);
        canvas.drawCircle(this.mEdgePoints[0].x, this.mEdgePoints[0].y, (float) this.mHandleSize, this.mPaintFrame);
        canvas.drawCircle(this.mEdgePoints[1].x, this.mEdgePoints[1].y, (float) this.mHandleSize, this.mPaintFrame);
        canvas.drawCircle(this.mEdgePoints[2].x, this.mEdgePoints[2].y, (float) this.mHandleSize, this.mPaintFrame);
        canvas.drawCircle(this.mEdgePoints[3].x, this.mEdgePoints[3].y, (float) this.mHandleSize, this.mPaintFrame);
        this.mPaintFrame.setStyle(Paint.Style.FILL);
        this.mPaintFrame.setColor(fillColor);
        canvas.drawCircle(this.mEdgePoints[0].x, this.mEdgePoints[0].y, ((float) this.mHandleSize) - this.mHandleHalfStroke, this.mPaintFrame);
        canvas.drawCircle(this.mEdgePoints[1].x, this.mEdgePoints[1].y, ((float) this.mHandleSize) - this.mHandleHalfStroke, this.mPaintFrame);
        canvas.drawCircle(this.mEdgePoints[2].x, this.mEdgePoints[2].y, ((float) this.mHandleSize) - this.mHandleHalfStroke, this.mPaintFrame);
        canvas.drawCircle(this.mEdgePoints[3].x, this.mEdgePoints[3].y, ((float) this.mHandleSize) - this.mHandleHalfStroke, this.mPaintFrame);
    }

    private void drawMidHandles(Canvas canvas) {
        int fillColor;
        int strokeColor;
        if (isValidPoints()) {
            strokeColor = this.mHandleValidStrokeColor;
            fillColor = this.mHandleValidFillColor;
        } else {
            strokeColor = this.mHandleInvalidStrokeColor;
            fillColor = this.mHandleInvalidFillColor;
        }
        this.mPaintFrame.setStyle(Paint.Style.STROKE);
        this.mPaintFrame.setStrokeWidth(this.mHandleStroke);
        this.mPaintFrame.setColor(strokeColor);
        canvas.drawCircle(this.mMidPoints[0].x, this.mMidPoints[0].y, (float) this.mHandleSize, this.mPaintFrame);
        canvas.drawCircle(this.mMidPoints[1].x, this.mMidPoints[1].y, (float) this.mHandleSize, this.mPaintFrame);
        canvas.drawCircle(this.mMidPoints[2].x, this.mMidPoints[2].y, (float) this.mHandleSize, this.mPaintFrame);
        canvas.drawCircle(this.mMidPoints[3].x, this.mMidPoints[3].y, (float) this.mHandleSize, this.mPaintFrame);
        this.mPaintFrame.setStyle(Paint.Style.FILL);
        this.mPaintFrame.setColor(fillColor);
        canvas.drawCircle(this.mMidPoints[0].x, this.mMidPoints[0].y, ((float) this.mHandleSize) - this.mHandleHalfStroke, this.mPaintFrame);
        canvas.drawCircle(this.mMidPoints[1].x, this.mMidPoints[1].y, ((float) this.mHandleSize) - this.mHandleHalfStroke, this.mPaintFrame);
        canvas.drawCircle(this.mMidPoints[2].x, this.mMidPoints[2].y, ((float) this.mHandleSize) - this.mHandleHalfStroke, this.mPaintFrame);
        canvas.drawCircle(this.mMidPoints[3].x, this.mMidPoints[3].y, ((float) this.mHandleSize) - this.mHandleHalfStroke, this.mPaintFrame);
    }


    public float calculateAngle(PointF pt1, PointF pt2) {
        return (float) ((Math.atan((double) ((pt2.y - pt1.y) / (pt2.x - pt1.x))) / 3.141592653589793d) * 180.0d);
    }


    public void setMatrix() {
        this.mMatrix.reset();
        this.mMatrix.setTranslate(this.mCenter.x - (this.mImgWidth * 0.5f), this.mCenter.y - (this.mImgHeight * 0.5f));
        Matrix matrix = this.mMatrix;
        float f = this.mScale;
        matrix.postScale(f, f, this.mCenter.x, this.mCenter.y);
        this.mMatrix.postRotate(this.mAngle, this.mCenter.x, this.mCenter.y);
    }


    public void setupLayout(int viewW, int viewH) {
        if (viewW != 0 && viewH != 0) {
            if (!this.mIsInitialized) {
                setCenter(new PointF(((float) getPaddingLeft()) + (((float) viewW) * 0.5f), ((float) getPaddingTop()) + (((float) viewH) * 0.5f)));
            }
            setScale(calcScale(viewW, viewH, this.mAngle));
            setMatrix();
            this.mImageRect = calcImageRect(new RectF(0.0f, 0.0f, this.mImgWidth, this.mImgHeight), this.mMatrix);
            this.mFrameRect = calcFrameRect(this.mImageRect);
            if (!this.mIsInitialized) {
                calcEdgePoint();
            }
            this.mIsInitialized = true;
            this.mIsCentered = true;
            invalidate();
        }
    }

    public boolean isValidPoints() {
        if (this.mEdgePoints == null) {
            return false;
        }
        PointF ptCenter = new PointF();
        PointF[] pointFArr = this.mEdgePoints;
        int length = pointFArr.length;
        for (PointF pt : pointFArr) {
            ptCenter.x += pt.x / ((float) length);
            ptCenter.y += pt.y / ((float) length);
        }
        Map<Integer, PointF> orderedPoints = new HashMap<>();
        for (PointF pt2 : this.mEdgePoints) {
            int index = -1;
            if (pt2.x < ptCenter.x && pt2.y < ptCenter.y) {
                index = 0;
            } else if (pt2.x > ptCenter.x && pt2.y < ptCenter.y) {
                index = 1;
            } else if (pt2.x < ptCenter.x && pt2.y > ptCenter.y) {
                index = 2;
            } else if (pt2.x > ptCenter.x && pt2.y > ptCenter.y) {
                index = 3;
            }
            orderedPoints.put(Integer.valueOf(index), pt2);
        }
        if (orderedPoints.size() == 4) {
            return true;
        }
        return false;
    }

    public void setEdge(PointF[] ptEdge) {
        if (ptEdge == null) {
            ptEdge = new PointF[]{new PointF(0.001f, 0.001f), new PointF(0.999f, 0.001f), new PointF(0.999f, 0.999f), new PointF(0.001f, 0.999f)};
        }
        this.mEdgePoints = new PointF[4];
        this.mEdgePoints[0] = new PointF(ptEdge[0].x, ptEdge[0].y);
        this.mEdgePoints[1] = new PointF(ptEdge[1].x, ptEdge[1].y);
        this.mEdgePoints[2] = new PointF(ptEdge[2].x, ptEdge[2].y);
        this.mEdgePoints[3] = new PointF(ptEdge[3].x, ptEdge[3].y);
        this.mMidPoints = new PointF[4];
        this.mMidPoints[0] = new PointF((this.mEdgePoints[0].x + this.mEdgePoints[1].x) / 2.0f, (this.mEdgePoints[0].y + this.mEdgePoints[1].y) / 2.0f);
        this.mMidPoints[1] = new PointF((this.mEdgePoints[1].x + this.mEdgePoints[2].x) / 2.0f, (this.mEdgePoints[1].y + this.mEdgePoints[2].y) / 2.0f);
        this.mMidPoints[2] = new PointF((this.mEdgePoints[2].x + this.mEdgePoints[3].x) / 2.0f, (this.mEdgePoints[2].y + this.mEdgePoints[3].y) / 2.0f);
        this.mMidPoints[3] = new PointF((this.mEdgePoints[3].x + this.mEdgePoints[0].x) / 2.0f, (this.mEdgePoints[3].y + this.mEdgePoints[0].y) / 2.0f);
    }

    private PointF[] rotateEdge(RotateDegrees degrees) {
        PointF[] ptEdges = getEdge();
        PointF[] newEdges = new PointF[4];
        if (degrees == RotateDegrees.ROTATE_M90D) {
            newEdges[0] = new PointF(ptEdges[1].y, 1.0f - ptEdges[1].x);
            newEdges[1] = new PointF(ptEdges[2].y, 1.0f - ptEdges[2].x);
            newEdges[2] = new PointF(ptEdges[3].y, 1.0f - ptEdges[3].x);
            newEdges[3] = new PointF(ptEdges[0].y, 1.0f - ptEdges[0].x);
        } else if (degrees == RotateDegrees.ROTATE_90D) {
            newEdges[1] = new PointF(1.0f - ptEdges[0].y, ptEdges[0].x);
            newEdges[2] = new PointF(1.0f - ptEdges[1].y, ptEdges[1].x);
            newEdges[3] = new PointF(1.0f - ptEdges[2].y, ptEdges[2].x);
            newEdges[0] = new PointF(1.0f - ptEdges[3].y, ptEdges[3].x);
        }
        return newEdges;
    }

    public static PointF[] rotateEdge(PointF[] ptEdges, RotateDegrees degrees) {
        PointF[] newEdges = new PointF[4];
        if (ptEdges == null) {
            ptEdges = new PointF[]{new PointF(0.01f, 0.01f), new PointF(0.99f, 0.01f), new PointF(0.99f, 0.99f), new PointF(0.01f, 0.99f)};
        }
        if (degrees == RotateDegrees.ROTATE_M90D) {
            newEdges[0] = new PointF(ptEdges[1].y, 1.0f - ptEdges[1].x);
            newEdges[1] = new PointF(ptEdges[2].y, 1.0f - ptEdges[2].x);
            newEdges[2] = new PointF(ptEdges[3].y, 1.0f - ptEdges[3].x);
            newEdges[3] = new PointF(ptEdges[0].y, 1.0f - ptEdges[0].x);
        } else if (degrees == RotateDegrees.ROTATE_90D) {
            newEdges[1] = new PointF(1.0f - ptEdges[0].y, ptEdges[0].x);
            newEdges[2] = new PointF(1.0f - ptEdges[1].y, ptEdges[1].x);
            newEdges[3] = new PointF(1.0f - ptEdges[2].y, ptEdges[2].x);
            newEdges[0] = new PointF(1.0f - ptEdges[3].y, ptEdges[3].x);
        }
        return newEdges;
    }

    public PointF[] getOrgEdge() {
        PointF[] edgePoint = getEdge();
        float backAngle = -this.mAngle;
        if (backAngle == 0.0f) {
            return edgePoint;
        }
        if (backAngle > 0.0f) {
            for (int i = 0; ((float) i) < backAngle / 90.0f; i++) {
                edgePoint = rotateEdge(edgePoint, RotateDegrees.ROTATE_90D);
            }
        } else if (backAngle < 0.0f) {
            for (int i2 = 0; ((float) i2) < (-backAngle) / 90.0f; i2++) {
                edgePoint = rotateEdge(edgePoint, RotateDegrees.ROTATE_M90D);
            }
        }
        return edgePoint;
    }

    public PointF[] getRawEdge() {
        return this.mEdgePoints;
    }

    public int getHandleSize() {
        return this.mHandleSize;
    }

    public PointF[] getEdge() {
        PointF[] edgePoint = new PointF[4];
        if (this.mEdgePoints == null) {
            this.mEdgePoints = new PointF[4];
            this.mEdgePoints[0] = new PointF(0.001f, 0.001f);
            this.mEdgePoints[1] = new PointF(0.999f, 0.001f);
            this.mEdgePoints[2] = new PointF(0.999f, 0.999f);
            this.mEdgePoints[3] = new PointF(0.001f, 0.999f);
        }
        if (this.mImageRect == null) {
            this.mImageRect = calcImageRect(new RectF(0.0f, 0.0f, this.mImgWidth, this.mImgHeight), this.mMatrix);
        }
        for (int i = 0; i < 4; i++) {
            edgePoint[i] = new PointF(this.mEdgePoints[i].x, this.mEdgePoints[i].y);
            float f = this.mAngle;
            if (((f + 360.0f) / 90.0f) % 2.0f == 0.0f) {
                edgePoint[i].x -= this.mImageRect.left;
                edgePoint[i].y -= this.mImageRect.top;
                edgePoint[i].x /= this.mImgWidth * this.mScale;
                edgePoint[i].y /= this.mImgHeight * this.mScale;
                edgePoint[i].x = Math.min(Math.max(0.0f, edgePoint[i].x), 0.999f);
                edgePoint[i].y = Math.min(Math.max(0.0f, edgePoint[i].y), 0.999f);
            } else if (((f + 360.0f) / 90.0f) % 2.0f == 1.0f) {
                edgePoint[i].x -= this.mImageRect.left;
                edgePoint[i].y -= this.mImageRect.top;
                edgePoint[i].x /= this.mImgHeight * this.mScale;
                edgePoint[i].y /= this.mImgWidth * this.mScale;
                edgePoint[i].x = Math.min(Math.max(0.0f, edgePoint[i].x), 0.999f);
                edgePoint[i].y = Math.min(Math.max(0.0f, edgePoint[i].y), 0.999f);
            }
        }
        return edgePoint;
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
        if (this.mEdgePoints != null && this.mMidPoints != null) {
            float width = 0.0f;
            float height = 0.0f;
            float f = this.mAngle;
            if (((f + 360.0f) / 90.0f) % 2.0f == 1.0f) {
                width = this.mImgHeight;
                height = this.mImgWidth;
            } else if (((f + 360.0f) / 90.0f) % 2.0f == 0.0f) {
                width = this.mImgWidth;
                height = this.mImgHeight;
            }
            for (int i = 0; i < 4; i++) {
                this.mEdgePoints[i].x *= this.mScale * width;
                this.mEdgePoints[i].y *= this.mScale * height;
                this.mMidPoints[i].x *= this.mScale * width;
                this.mMidPoints[i].y *= this.mScale * height;
            }
            if (this.mImageRect == null) {
                this.mImageRect = calcImageRect(new RectF(0.0f, 0.0f, this.mImgWidth, this.mImgHeight), this.mMatrix);
            }
            for (int i2 = 0; i2 < 4; i2++) {
                this.mEdgePoints[i2].offset(this.mImageRect.left, this.mImageRect.top);
                this.mMidPoints[i2].offset(this.mImageRect.left, this.mImageRect.top);
            }
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
        int fingers;
        if (!this.mIsInitialized || !this.mIsCropEnabled || !this.mIsEnabled || this.mIsRotating || this.mIsAnimating || this.mIsLoading.get() || this.mIsCropping.get() || (fingers = event.getPointerCount()) > 2) {
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
                if (this.mTouchArea != TouchArea.OUT_OF_BOUNDS) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
            } else if (fingers > 1) {
                onMoveMultiTouch(event);
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

    private void onDown(MotionEvent e) {
        this.wasMoved = false;
        this.mLastX = e.getX();
        this.mLastY = e.getY();
        checkTouchArea(this.mLastX, this.mLastY);
        invalidate();
        setMagImage();
    }

    private void onPointerDown(MotionEvent e) {
        this.mLastDist = GeoUtil.distancePt2Pt(new PointF(e.getX(0), e.getY(0)), new PointF(e.getX(1), e.getY(1)));
        this.multiTouch = true;
        this.cropViewCallback.lensView().setVisibility(INVISIBLE);
    }

    private void onUp(MotionEvent e) {
        SimpleCropImageViewCallback simpleCropImageViewCallback = this.cropViewCallback;
        if (simpleCropImageViewCallback != null) {
            simpleCropImageViewCallback.onMoved(this.wasMoved);
        }
        this.mTouchArea = TouchArea.OUT_OF_BOUNDS;
        this.multiTouch = false;
        invalidate();
        this.cropViewCallback.lensView().setVisibility(4);
    }

    private void onPointerUp(MotionEvent event) {
    }


    private void onMoveMultiTouch(MotionEvent e) {
        PointF firstNew;
        int xSign;
        PointF secondNew = null;

        MotionEvent motionEvent = e;
        this.wasMoved = true;
        PointF firstNew2 = new PointF(motionEvent.getX(0), motionEvent.getY(0));
        PointF secondNew2 = new PointF(motionEvent.getX(1), motionEvent.getY(1));
        double dist = GeoUtil.distancePt2Pt(firstNew2, secondNew2);
        double diffDist = dist - this.mLastDist;
        double sign = Math.signum(diffDist);
        double increaseHypotenuse = Math.abs(diffDist / 2.0d);
        double diff = (sign * increaseHypotenuse) / SQRT_OF_2;
        int i = 0;
        while (i < 4) {
            float prevX = this.mEdgePoints[i].x;
            float prevY = this.mEdgePoints[i].y;
            int ySign = -1;
            if (i != 0) {
                firstNew = firstNew2;
                if (i != 3) {
                    xSign = 1;
                    if (i == 0) {
                        secondNew = secondNew2;
                        if (i != 1) {
                            ySign = 1;
                        }
                    } else {
                        secondNew = secondNew2;
                    }
                    PointF pointF = this.mEdgePoints[i];
                    double diffDist2 = diffDist;
                    double d = (double) pointF.x;
                    double sign2 = sign;
                    double sign3 = (double) xSign;
                    Double.isNaN(sign3);
                    Double.isNaN(d);
                    pointF.x = (float) (d + (sign3 * diff));
                    PointF pointF2 = this.mEdgePoints[i];
                    double d2 = (double) pointF2.y;
                    double increaseHypotenuse2 = increaseHypotenuse;
                    double d3 = (double) ySign;
                    Double.isNaN(d3);
                    Double.isNaN(d2);
                    pointF2.y = (float) (d2 + (d3 * diff));
                    if (isWidthTooSmall()) {
                        this.mEdgePoints[i].x = prevX;
                    }
                    if (!isHeightTooSmall()) {
                        this.mEdgePoints[i].y = prevY;
                    }
                    checkScaleBounds();
                    i++;
                    firstNew2 = firstNew;
                    secondNew2 = secondNew;
                    diffDist = diffDist2;
                    sign = sign2;
                    increaseHypotenuse = increaseHypotenuse2;
                }
            } else {
                firstNew = firstNew2;
            }
            xSign = -1;
            if (i == 0) {
            }
            PointF pointF3 = this.mEdgePoints[i];
            double diffDist22 = diffDist;
            double d4 = (double) pointF3.x;
            double sign22 = sign;
            double sign32 = (double) xSign;
            Double.isNaN(sign32);
            Double.isNaN(d4);
            pointF3.x = (float) (d4 + (sign32 * diff));
            PointF pointF22 = this.mEdgePoints[i];
            double d22 = (double) pointF22.y;
            double increaseHypotenuse22 = increaseHypotenuse;
            double d32 = (double) ySign;
            Double.isNaN(d32);
            Double.isNaN(d22);
            pointF22.y = (float) (d22 + (d32 * diff));
            if (isWidthTooSmall()) {
            }
            if (!isHeightTooSmall()) {
            }
            checkScaleBounds();
            i++;
            firstNew2 = firstNew;
            secondNew2 = secondNew;
            diffDist = diffDist22;
            sign = sign22;
            increaseHypotenuse = increaseHypotenuse22;
        }
        updateMidPoints();
        invalidate();
        this.mLastDist = dist;
    }

    private void onMoveSingleTouch(MotionEvent e) {
        this.wasMoved = true;
        float x = e.getX();
        float y = e.getY();
        float diffX = x - this.mLastX;
        float diffY = y - this.mLastY;
        switch (this.mTouchArea) {
            case CENTER:
                moveFrame(diffX, diffY);
                updateMidPoints();
                break;
            case LEFT_TOP:
                moveHandleLT(diffX, diffY);
                updateMidPoints();
                break;
            case RIGHT_TOP:
                moveHandleRT(diffX, diffY);
                updateMidPoints();
                break;
            case LEFT_BOTTOM:
                moveHandleLB(diffX, diffY);
                updateMidPoints();
                break;
            case RIGHT_BOTTOM:
                moveHandleRB(diffX, diffY);
                updateMidPoints();
                break;
            case TOP:
                moveHandleTop(0.0f, diffY);
                updateMidPoints();
                break;
            case RIGHT:
                moveHandleRight(diffX, 0.0f);
                updateMidPoints();
                break;
            case BOTTOM:
                moveHandleBottom(0.0f, diffY);
                updateMidPoints();
                break;
            case LEFT:
                moveHandleLeft(diffX, 0.0f);
                updateMidPoints();
                break;
        }
        setMagImage();
        invalidate();
        this.mLastX = x;
        this.mLastY = y;
    }

    private void updateLensPosition() {
        switch (this.mTouchArea) {
            case LEFT_TOP:
                this.cropViewCallback.onMove(this.mEdgePoints[0].x, this.mEdgePoints[0].y, this.mImageRect);
                return;
            case RIGHT_TOP:
                this.cropViewCallback.onMove(this.mEdgePoints[1].x, this.mEdgePoints[1].y, this.mImageRect);
                return;
            case LEFT_BOTTOM:
                this.cropViewCallback.onMove(this.mEdgePoints[3].x, this.mEdgePoints[3].y, this.mImageRect);
                return;
            case RIGHT_BOTTOM:
                this.cropViewCallback.onMove(this.mEdgePoints[2].x, this.mEdgePoints[2].y, this.mImageRect);
                return;
            case TOP:
                this.cropViewCallback.onMove(this.mMidPoints[0].x, this.mMidPoints[0].y, this.mImageRect);
                return;
            case RIGHT:
                this.cropViewCallback.onMove(this.mMidPoints[1].x, this.mMidPoints[1].y, this.mImageRect);
                return;
            case BOTTOM:
                this.cropViewCallback.onMove(this.mMidPoints[2].x, this.mMidPoints[2].y, this.mImageRect);
                return;
            case LEFT:
                this.cropViewCallback.onMove(this.mMidPoints[3].x, this.mMidPoints[3].y, this.mImageRect);
                return;
            default:
                return;
        }
    }

    private void onCancel() {
        SimpleCropImageViewCallback simpleCropImageViewCallback = this.cropViewCallback;
        if (simpleCropImageViewCallback != null) {
            simpleCropImageViewCallback.onMoved(this.wasMoved);
        }
        this.mTouchArea = TouchArea.OUT_OF_BOUNDS;
        this.multiTouch = false;
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
        float dx = x - this.mEdgePoints[0].x;
        float dy = y - this.mEdgePoints[0].y;
        if (m14908sq((float) (this.mHandleSize + this.mTouchPadding)) >= (dx * dx) + (dy * dy)) {
            return true;
        }
        return false;
    }

    private boolean isInsideCornerRightTop(float x, float y) {
        float dx = x - this.mEdgePoints[1].x;
        float dy = y - this.mEdgePoints[1].y;
        if (m14908sq((float) (this.mHandleSize + this.mTouchPadding)) >= (dx * dx) + (dy * dy)) {
            return true;
        }
        return false;
    }

    private boolean isInsideCornerLeftBottom(float x, float y) {
        float dx = x - this.mEdgePoints[3].x;
        float dy = y - this.mEdgePoints[3].y;
        return m14908sq((float) (this.mHandleSize + this.mTouchPadding)) >= (dx * dx) + (dy * dy);
    }

    private boolean isInsideCornerRightBottom(float x, float y) {
        float dx = x - this.mEdgePoints[2].x;
        float dy = y - this.mEdgePoints[2].y;
        return m14908sq((float) (this.mHandleSize + this.mTouchPadding)) >= (dx * dx) + (dy * dy);
    }

    private boolean isInsideTop(float x, float y) {
        float dx = x - this.mMidPoints[0].x;
        float dy = y - this.mMidPoints[0].y;
        if (m14908sq((float) (this.mHandleSize + (this.mTouchPadding * 2))) >= (dx * dx) + (dy * dy)) {
            return true;
        }
        return false;
    }

    private boolean isInsideRight(float x, float y) {
        float dx = x - this.mMidPoints[1].x;
        float dy = y - this.mMidPoints[1].y;
        if (m14908sq((float) (this.mHandleSize + (this.mTouchPadding * 2))) >= (dx * dx) + (dy * dy)) {
            return true;
        }
        return false;
    }

    private boolean isInsideBottom(float x, float y) {
        float dx = x - this.mMidPoints[2].x;
        float dy = y - this.mMidPoints[2].y;
        return m14908sq((float) (this.mHandleSize + (this.mTouchPadding * 2))) >= (dx * dx) + (dy * dy);
    }

    private boolean isInsideLeft(float x, float y) {
        float dx = x - this.mMidPoints[3].x;
        float dy = y - this.mMidPoints[3].y;
        return m14908sq((float) (this.mHandleSize + (this.mTouchPadding * 2))) >= (dx * dx) + (dy * dy);
    }

    private void moveFrame(float x, float y) {
        for (int i = 0; i < 4; i++) {
            this.mEdgePoints[i].x += x;
            this.mEdgePoints[i].y += y;
        }
        checkMoveBounds();
    }

    private void moveHandleLT(float diffX, float diffY) {
        if (this.mCropMode == CropMode.FREE) {
            this.mEdgePoints[0].x += diffX;
            this.mEdgePoints[0].y += diffY;
            if (isWidthTooSmall()) {
                this.mEdgePoints[0].x -= this.mMinFrameSize - getFrameW();
            }
            if (isHeightTooSmall()) {
                this.mEdgePoints[0].y -= this.mMinFrameSize - getFrameH();
            }
            checkScaleBounds();
        }
    }

    private void moveHandleRT(float diffX, float diffY) {
        if (this.mCropMode == CropMode.FREE) {
            this.mEdgePoints[1].x += diffX;
            this.mEdgePoints[1].y += diffY;
            if (isWidthTooSmall()) {
                this.mEdgePoints[1].x += this.mMinFrameSize - getFrameW();
            }
            if (isHeightTooSmall()) {
                this.mEdgePoints[1].y -= this.mMinFrameSize - getFrameH();
            }
            checkScaleBounds();
        }
    }

    private void moveHandleLB(float diffX, float diffY) {
        if (this.mCropMode == CropMode.FREE) {
            this.mEdgePoints[3].x += diffX;
            this.mEdgePoints[3].y += diffY;
            if (isWidthTooSmall()) {
                this.mEdgePoints[3].x -= this.mMinFrameSize - getFrameW();
            }
            if (isHeightTooSmall()) {
                this.mEdgePoints[3].y += this.mMinFrameSize - getFrameH();
            }
            checkScaleBounds();
        }
    }

    private void moveHandleRB(float diffX, float diffY) {
        if (this.mCropMode == CropMode.FREE) {
            this.mEdgePoints[2].x += diffX;
            this.mEdgePoints[2].y += diffY;
            if (isWidthTooSmall()) {
                this.mEdgePoints[2].x += this.mMinFrameSize - getFrameW();
            }
            if (isHeightTooSmall()) {
                this.mEdgePoints[2].y += this.mMinFrameSize - getFrameH();
            }
            checkScaleBounds();
        }
    }

    private void moveHandleTop(float diffX, float diffY) {
        PointF pt1 = new PointF(this.mEdgePoints[0].x + diffX, this.mEdgePoints[0].y + diffY);
        PointF pt2 = new PointF(this.mEdgePoints[1].x + diffX, this.mEdgePoints[1].y + diffY);
        PointF[] pointFArr = this.mEdgePoints;
        PointF ptNew1 = GeoUtil.intersection(pt1, pt2, pointFArr[0], pointFArr[3]);
        PointF[] pointFArr2 = this.mEdgePoints;
        PointF ptNew2 = GeoUtil.intersection(pt1, pt2, pointFArr2[1], pointFArr2[2]);
        moveHandleLT(ptNew1.x - this.mEdgePoints[0].x, ptNew1.y - this.mEdgePoints[0].y);
        moveHandleRT(ptNew2.x - this.mEdgePoints[1].x, ptNew2.y - this.mEdgePoints[1].y);
    }

    private void moveHandleRight(float diffX, float diffY) {
        PointF pt1 = new PointF(this.mEdgePoints[1].x + diffX, this.mEdgePoints[1].y + diffY);
        PointF pt2 = new PointF(this.mEdgePoints[2].x + diffX, this.mEdgePoints[2].y + diffY);
        PointF[] pointFArr = this.mEdgePoints;
        PointF ptNew1 = GeoUtil.intersection(pt1, pt2, pointFArr[0], pointFArr[1]);
        PointF[] pointFArr2 = this.mEdgePoints;
        PointF ptNew2 = GeoUtil.intersection(pt1, pt2, pointFArr2[3], pointFArr2[2]);
        moveHandleRT(ptNew1.x - this.mEdgePoints[1].x, ptNew1.y - this.mEdgePoints[1].y);
        moveHandleRB(ptNew2.x - this.mEdgePoints[2].x, ptNew2.y - this.mEdgePoints[2].y);
    }

    private void moveHandleBottom(float diffX, float diffY) {
        PointF pt1 = new PointF(this.mEdgePoints[3].x + diffX, this.mEdgePoints[3].y + diffY);
        PointF pt2 = new PointF(this.mEdgePoints[2].x + diffX, this.mEdgePoints[2].y + diffY);
        PointF[] pointFArr = this.mEdgePoints;
        PointF ptNew1 = GeoUtil.intersection(pt1, pt2, pointFArr[0], pointFArr[3]);
        PointF[] pointFArr2 = this.mEdgePoints;
        PointF ptNew2 = GeoUtil.intersection(pt1, pt2, pointFArr2[1], pointFArr2[2]);
        float diffX1 = ptNew1.x - this.mEdgePoints[3].x;
        float diffY1 = ptNew1.y - this.mEdgePoints[3].y;
        moveHandleRB(ptNew2.x - this.mEdgePoints[2].x, ptNew2.y - this.mEdgePoints[2].y);
        moveHandleLB(diffX1, diffY1);
    }

    private void moveHandleLeft(float diffX, float diffY) {
        PointF pt1 = new PointF(this.mEdgePoints[0].x + diffX, this.mEdgePoints[0].y + diffY);
        PointF pt2 = new PointF(this.mEdgePoints[3].x + diffX, this.mEdgePoints[3].y + diffY);
        PointF[] pointFArr = this.mEdgePoints;
        PointF ptNew1 = GeoUtil.intersection(pt1, pt2, pointFArr[0], pointFArr[1]);
        PointF[] pointFArr2 = this.mEdgePoints;
        PointF ptNew2 = GeoUtil.intersection(pt1, pt2, pointFArr2[3], pointFArr2[2]);
        moveHandleLT(ptNew1.x - this.mEdgePoints[0].x, ptNew1.y - this.mEdgePoints[0].y);
        moveHandleLB(ptNew2.x - this.mEdgePoints[3].x, ptNew2.y - this.mEdgePoints[3].y);
    }

    private void updateMidPoints() {
        this.mMidPoints[0].x = (this.mEdgePoints[0].x + this.mEdgePoints[1].x) / 2.0f;
        this.mMidPoints[0].y = (this.mEdgePoints[0].y + this.mEdgePoints[1].y) / 2.0f;
        this.mMidPoints[1].x = (this.mEdgePoints[1].x + this.mEdgePoints[2].x) / 2.0f;
        this.mMidPoints[1].y = (this.mEdgePoints[1].y + this.mEdgePoints[2].y) / 2.0f;
        this.mMidPoints[2].x = (this.mEdgePoints[2].x + this.mEdgePoints[3].x) / 2.0f;
        this.mMidPoints[2].y = (this.mEdgePoints[2].y + this.mEdgePoints[3].y) / 2.0f;
        this.mMidPoints[3].x = (this.mEdgePoints[3].x + this.mEdgePoints[0].x) / 2.0f;
        this.mMidPoints[3].y = (this.mEdgePoints[3].y + this.mEdgePoints[0].y) / 2.0f;
    }

    private void checkScaleBounds() {
        float diff = this.mEdgePoints[0].x - this.mImageRect.left;
        if (diff < 0.0f) {
            this.mEdgePoints[0].x -= diff;
        }
        float diff2 = this.mEdgePoints[3].x - this.mImageRect.left;
        if (diff2 < 0.0f) {
            this.mEdgePoints[3].x -= diff2;
        }
        float diff3 = this.mEdgePoints[1].x - this.mImageRect.right;
        if (diff3 > 0.0f) {
            this.mEdgePoints[1].x -= diff3;
        }
        float diff4 = this.mEdgePoints[2].x - this.mImageRect.right;
        if (diff4 > 0.0f) {
            this.mEdgePoints[2].x -= diff4;
        }
        float diff5 = this.mEdgePoints[0].y - this.mImageRect.top;
        if (diff5 < 0.0f) {
            this.mEdgePoints[0].y -= diff5;
        }
        float diff6 = this.mEdgePoints[1].y - this.mImageRect.top;
        if (diff6 < 0.0f) {
            this.mEdgePoints[1].y -= diff6;
        }
        float diff7 = this.mEdgePoints[2].y - this.mImageRect.bottom;
        if (diff7 > 0.0f) {
            this.mEdgePoints[2].y -= diff7;
        }
        float diff8 = this.mEdgePoints[3].y - this.mImageRect.bottom;
        if (diff8 > 0.0f) {
            this.mEdgePoints[3].y -= diff8;
        }
    }

    private void checkMoveBounds() {
        float left = Math.min(this.mEdgePoints[0].x, this.mEdgePoints[3].x);
        float right = Math.max(this.mEdgePoints[1].x, this.mEdgePoints[2].x);
        float top = Math.min(this.mEdgePoints[0].y, this.mEdgePoints[1].y);
        float bottom = Math.max(this.mEdgePoints[2].y, this.mEdgePoints[3].y);
        float diff = left - this.mImageRect.left;
        if (diff < 0.0f) {
            for (int i = 0; i < 4; i++) {
                this.mEdgePoints[i].x -= diff;
            }
        }
        float diff2 = right - this.mImageRect.right;
        if (diff2 > 0.0f) {
            for (int i2 = 0; i2 < 4; i2++) {
                this.mEdgePoints[i2].x -= diff2;
            }
        }
        float diff3 = top - this.mImageRect.top;
        if (diff3 < 0.0f) {
            for (int i3 = 0; i3 < 4; i3++) {
                this.mEdgePoints[i3].y -= diff3;
            }
        }
        float diff4 = bottom - this.mImageRect.bottom;
        if (diff4 > 0.0f) {
            for (int i4 = 0; i4 < 4; i4++) {
                this.mEdgePoints[i4].y -= diff4;
            }
        }
    }

    private boolean isInsideHorizontal(float x) {
        return this.mImageRect.left <= x && this.mImageRect.right >= x;
    }

    private boolean isInsideVertical(float y) {
        return this.mImageRect.top <= y && this.mImageRect.bottom >= y;
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
                        boolean unused = SimpleCropImageView.this.mIsAnimating = true;
                    }

                    public void onAnimationUpdated(float scale) {
                        RectF unused = SimpleCropImageView.this.mFrameRect = new RectF(rectF.left + (f * scale), rectF.top + (f2 * scale), rectF.right + (f3 * scale), rectF.bottom + (f4 * scale));
                        SimpleCropImageView.this.invalidate();
                    }

                    public void onAnimationFinished() {
                        RectF unused = SimpleCropImageView.this.mFrameRect = rectF2;
                        SimpleCropImageView.this.invalidate();
                        boolean unused2 = SimpleCropImageView.this.mIsAnimating = false;
                    }
                };
                SimpleValueAnimatorListener r9 = r0;

                animator.addAnimatorListener(r9);
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
    private float m14908sq(float value) {
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

    private Bitmap getCroppedBitmapFromUri() throws IOException {
        InputStream is = null;
        try {
            is = getContext().getContentResolver().openInputStream(this.mSourceUri);
            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(is, false);
            int originalImageWidth = decoder.getWidth();
            int originalImageHeight = decoder.getHeight();
            Rect cropRect = calcCropRect(originalImageWidth, originalImageHeight);
            if (this.mAngle != 0.0f) {
                Matrix matrix = new Matrix();
                matrix.setRotate(-this.mAngle);
                RectF rotated = new RectF();
                matrix.mapRect(rotated, new RectF(cropRect));
                rotated.offset(rotated.left < 0.0f ? (float) originalImageWidth : 0.0f, rotated.top < 0.0f ? (float) originalImageHeight : 0.0f);
                cropRect = new Rect((int) rotated.left, (int) rotated.top, (int) rotated.right, (int) rotated.bottom);
            }
            Bitmap cropped = decoder.decodeRegion(cropRect, new BitmapFactory.Options());
            if (this.mAngle != 0.0f) {
                Bitmap rotated2 = getRotatedBitmap(cropped);
                if (!(cropped == getBitmap() || cropped == rotated2)) {
                    cropped.recycle();
                }
                cropped = rotated2;
            }
            return cropped;
        } finally {
            SimpleCropUtils.closeQuietly(is);
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

    private Bitmap scaleBitmapIfNeeded(Bitmap cropped) {
        int i;
        int width = cropped.getWidth();
        int height = cropped.getHeight();
        int outWidth = 0;
        int outHeight = 0;
        float imageRatio = getRatioX(this.mFrameRect.width()) / getRatioY(this.mFrameRect.height());
        int i2 = this.mOutputWidth;
        if (i2 > 0) {
            outWidth = this.mOutputWidth;
            outHeight = Math.round(((float) i2) / imageRatio);
        } else {
            int i3 = this.mOutputHeight;
            if (i3 > 0) {
                outHeight = this.mOutputHeight;
                outWidth = Math.round(((float) i3) * imageRatio);
            } else {
                int i4 = this.mOutputMaxWidth;
                if (i4 > 0 && (i = this.mOutputMaxHeight) > 0 && (width > i4 || height > i)) {
                    int i5 = this.mOutputMaxWidth;
                    int i6 = this.mOutputMaxHeight;
                    if (((float) i5) / ((float) i6) >= imageRatio) {
                        outHeight = this.mOutputMaxHeight;
                        outWidth = Math.round(((float) i6) * imageRatio);
                    } else {
                        outWidth = this.mOutputMaxWidth;
                        outHeight = Math.round(((float) i5) / imageRatio);
                    }
                }
            }
        }
        if (outWidth <= 0 || outHeight <= 0) {
            return cropped;
        }
        Bitmap scaled = SimpleCropUtils.getScaledBitmap(cropped, outWidth, outHeight);
        if (!(cropped == getBitmap() || cropped == scaled)) {
            cropped.recycle();
        }
        return scaled;
    }

    public Bitmap getImageBitmap() {
        return getBitmap();
    }

    public void setImageBitmap(Bitmap bitmap) {
        super.setImageBitmap(bitmap);
        this.m_bmpSource = bitmap;
        this.m_bmpRotate = bitmap;
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




    public void rotateImage(RotateDegrees degrees, int durationMillis) {
        if (this.mIsRotating) {
            getAnimator().cancelAnimation();
        }
        this.m_bmpRotate = BitmapUtils.rotateImageAlpha(this.m_bmpRotate, (float) degrees.getValue());
        PointF[] newEdge = rotateEdge(degrees);
        float currentAngle = this.mAngle;
        float newAngle = this.mAngle + ((float) degrees.getValue());
        float angleDiff = newAngle - currentAngle;
        float currentScale = this.mScale;
        float newScale = calcScale(this.mViewWidth, this.mViewHeight, newAngle);
        if (this.mIsAnimationEnabled) {
            final float f = currentAngle;
            final float f2 = angleDiff;
            final float f3 = currentScale;
            final float f4 = newScale - currentScale;
            final PointF[] pointFArr = newEdge;
            final float f5 = newAngle;
            SimpleValueAnimator animator = getAnimator();
            final float f6 = newScale;
            SimpleValueAnimatorListener r0 = new SimpleValueAnimatorListener() {
                public void onAnimationStarted() {
                    boolean unused = SimpleCropImageView.this.mIsRotating = true;
                }

                public void onAnimationUpdated(float scale) {
                    float unused = SimpleCropImageView.this.mAngle = f + (f2 * scale);
                    float unused2 = SimpleCropImageView.this.mScale = f3 + (f4 * scale);
                    SimpleCropImageView.this.setMatrix();
                    SimpleCropImageView.this.invalidate();
                }

                public void onAnimationFinished() {
                    SimpleCropImageView.this.setEdge(pointFArr);
                    float unused = SimpleCropImageView.this.mAngle = f5 % 360.0f;
                    float unused2 = SimpleCropImageView.this.mScale = f6;
                    boolean unused3 = SimpleCropImageView.this.mIsInitialized = false;
                    SimpleCropImageView simpleCropImageView = SimpleCropImageView.this;
                    simpleCropImageView.setupLayout(simpleCropImageView.mViewWidth, SimpleCropImageView.this.mViewHeight);
                    boolean unused4 = SimpleCropImageView.this.mIsRotating = false;
                }
            };
            SimpleValueAnimatorListener r11 = r0;

            animator.addAnimatorListener(r11);
            animator.startAnimation((long) durationMillis);
            return;
        }
        setEdge(newEdge);
        this.mAngle = newAngle % 360.0f;
        this.mScale = newScale;
        setupLayout(this.mViewWidth, this.mViewHeight);
    }

    public void rotateImage(RotateDegrees degrees) {
        rotateImage(degrees, this.mAnimationDurationMillis);
    }

    public Bitmap getCroppedBitmap() {
        Bitmap source = getBitmap();
        if (source == null) {
            return null;
        }
        Bitmap rotated = getRotatedBitmap(source);
        Rect cropRect = calcCropRect(source.getWidth(), source.getHeight());
        Bitmap cropped = Bitmap.createBitmap(rotated, cropRect.left, cropRect.top, cropRect.width(), cropRect.height(), (Matrix) null, false);
        if (!(rotated == cropped || rotated == source)) {
            rotated.recycle();
        }
        if (this.mCropMode != CropMode.CIRCLE) {
            return cropped;
        }
        Bitmap circle = getCircularBitmap(cropped);
        if (cropped != getBitmap()) {
            cropped.recycle();
        }
        return circle;
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

    public void setMagImage() {
        int magMode;
        int curEdge;
        float yPos;
        float xPos;
        boolean bOutLeft;
        boolean bOutTop;
        boolean bOutRight;
        boolean bOutBottom;
        Bitmap bmpCrop = null;

        Bitmap bitmap = this.m_bmpRotate;
        if (bitmap != null && !bitmap.isRecycled()) {
            switch (this.mTouchArea) {
                case LEFT_TOP:
                    curEdge = 0;
                    magMode = 1;
                    break;
                case RIGHT_TOP:
                    curEdge = 1;
                    magMode = 1;
                    break;
                case LEFT_BOTTOM:
                    curEdge = 3;
                    magMode = 1;
                    break;
                case RIGHT_BOTTOM:
                    curEdge = 2;
                    magMode = 1;
                    break;
                case TOP:
                    curEdge = 0;
                    magMode = 2;
                    break;
                case RIGHT:
                    curEdge = 1;
                    magMode = 2;
                    break;
                case BOTTOM:
                    curEdge = 2;
                    magMode = 2;
                    break;
                case LEFT:
                    curEdge = 3;
                    magMode = 2;
                    break;
                default:
                    curEdge = -1;
                    magMode = -1;
                    break;
            }
            if (curEdge >= 0) {
                if (magMode == 1) {
                    xPos = (this.mEdgePoints[curEdge].x - this.mImageRect.left) / this.mScale;
                    yPos = (this.mEdgePoints[curEdge].y - this.mImageRect.top) / this.mScale;
                } else if (magMode == 2) {
                    xPos = (this.mMidPoints[curEdge].x - this.mImageRect.left) / this.mScale;
                    yPos = (this.mMidPoints[curEdge].y - this.mImageRect.top) / this.mScale;
                } else {
                    xPos = 0.0f;
                    yPos = 0.0f;
                }
                org.opencv.core.Rect rect = new org.opencv.core.Rect((int) (xPos - 50.0f), (int) (yPos - 50.0f), 101, 101);
                Point leftTopPoint = rect.mo64934tl();
                Point rightBottomPoint = rect.mo64926br();
                float imgWidth = (float) this.m_bmpRotate.getWidth();
                float imgHeight = (float) this.m_bmpRotate.getHeight();
                float imgWidth2 = imgWidth;
                if (leftTopPoint.f14653x < 0.0d) {
                    leftTopPoint.f14653x = 0.0d;
                    bOutLeft = true;
                } else {
                    bOutLeft = false;
                }
                if (leftTopPoint.f14654y < 0.0d) {
                    leftTopPoint.f14654y = 0.0d;
                    bOutTop = true;
                } else {
                    bOutTop = false;
                }
                float imgWidth3 = imgWidth2;
                if (rightBottomPoint.f14653x >= ((double) imgWidth3)) {
                    rightBottomPoint.f14653x = (double) (imgWidth3 - 1.0f);
                    bOutRight = true;
                } else {
                    bOutRight = false;
                }
                if (rightBottomPoint.f14654y >= ((double) imgHeight)) {
                    rightBottomPoint.f14654y = (double) (imgHeight - 1.0f);
                    bOutBottom = true;
                } else {
                    bOutBottom = false;
                }
                int left = (int) leftTopPoint.f14653x;
                int top = (int) leftTopPoint.f14654y;
                try {
                    try {
                        Bitmap bmpCrop2 = Bitmap.createBitmap(this.m_bmpRotate, left, top, (((int) rightBottomPoint.f14653x) - left) + 1, (((int) rightBottomPoint.f14654y) - top) + 1);
                        if (bOutLeft || bOutTop || bOutRight || bOutBottom) {
                            try {
                                bmpCrop = makeBlackOutImage(bmpCrop2, bOutLeft, bOutTop, bOutRight, bOutBottom);
                            } catch (Exception e) {
                                Exception ex = e;
                                Timber.e(ex);
                            }
                        } else {
                            bmpCrop = bmpCrop2;
                        }
                        float sideInCanvas = (float) this.cropViewCallback.lensView().getWidth();
                        try {
                            try {
                                Bitmap bmpWithRoundedCorner = getRoundedCornerBitmap(Bitmap.createScaledBitmap(bmpCrop, (int) sideInCanvas, (int) sideInCanvas, true), -1, sideInCanvas / 2.0f, 3.0f, new PointF(xPos, yPos), getPrevPoint(curEdge), getNextPoint(curEdge), this.mHandleSize, getContext());
                                setMagSide(xPos, yPos);
                                this.cropViewCallback.lensView().setImageBitmap(bmpWithRoundedCorner);
                            } catch (Exception e2) {
                                Exception  ex = e2;
                            }
                        } catch (Exception e3) {
                            Exception ex = e3;
                            Timber.e(ex);
                        }
                    } catch (Exception e4) {
                        Exception  ex = e4;
                        Timber.e(ex);
                    }
                } catch (Exception e5) {
                    Exception  ex = e5;
                    Timber.e(ex);
                }
            }
        }
    }

    public PointF getNextPoint(int curEdge) {
        PointF pt = this.mEdgePoints[(curEdge + 1) % 4];
        PointF point = new PointF();
        point.x = (pt.x - this.mImageRect.left) / this.mScale;
        point.y = (pt.y - this.mImageRect.top) / this.mScale;
        return point;
    }

    public PointF getPrevPoint(int curEdge) {
        PointF pt = new PointF();
        switch (this.mTouchArea) {
            case LEFT_TOP:
            case RIGHT_TOP:
            case LEFT_BOTTOM:
            case RIGHT_BOTTOM:
                pt = this.mEdgePoints[((curEdge + 4) - 1) % 4];
                break;
            case TOP:
            case RIGHT:
            case BOTTOM:
            case LEFT:
                pt = this.mEdgePoints[curEdge % 4];
                break;
        }
        PointF point = new PointF();
        point.x = (pt.x - this.mImageRect.left) / this.mScale;
        point.y = (pt.y - this.mImageRect.top) / this.mScale;
        return point;
    }

    public Bitmap makeBlackOutImage(Bitmap bitmap, boolean bOverLeft, boolean bOverTop, boolean bOverRight, boolean bOverBottom) {
        if (bitmap == null) {
            return null;
        }
        try {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Bitmap output = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            canvas.drawColor(-16777216);
            float xPos = 0.0f;
            float yPos = 0.0f;
            if (bOverLeft) {
                xPos = (float) (100 - width);
            }
            if (bOverRight) {
                xPos = 0.0f;
            }
            if (bOverTop) {
                yPos = (float) (100 - height);
            }
            if (bOverBottom) {
                yPos = 0.0f;
            }
            canvas.drawBitmap(bitmap, xPos, yPos, paint);
            return output;
        } catch (OutOfMemoryError ex) {
            ex.printStackTrace();
            return bitmap;
        }
    }

    public Bitmap getRoundedCornerBitmap(Bitmap bitmap, int color, float cornerDips, float borderDips, PointF ptCur, PointF ptPrev, PointF ptNext, int viewWidth, Context context) {
        PointF pointF = ptCur;
        PointF pointF2 = ptPrev;
        PointF pointF3 = ptNext;
        try {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Canvas canvas = new Canvas(output);
            try {
                float borderSizePx = TypedValue.applyDimension(1, borderDips, context.getResources().getDisplayMetrics());
                float cornerSizePx = TypedValue.applyDimension(1, cornerDips, context.getResources().getDisplayMetrics());
                Paint paint = new Paint();
                Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                RectF rectF = new RectF(rect);
                paint.setAntiAlias(true);
                paint.setColor(-1);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawARGB(0, 0, 0, 0);
                canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                canvas.drawBitmap(bitmap, rect, rect, paint);
                paint.setAntiAlias(true);
                paint.setColor(BLUE);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(borderSizePx);
                RectF rectF2 = rectF;
                canvas.drawCircle((float) (width / 2), (float) (height / 2), (float) (width / 2), paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(BLUE);
                float radius = ((float) (width / 2)) - (borderSizePx / 2.0f);
                float dx1 = pointF2.x - pointF.x;
                float dy1 = pointF2.y - pointF.y;
                float l1 = (float) Math.sqrt((double) ((dx1 * dx1) + (dy1 * dy1)));
                float dx12 = dx1 * (radius / l1);
                float dy12 = dy1 * (radius / l1);
                float dx2 = pointF3.x - pointF.x;
                float dy2 = pointF3.y - pointF.y;
                float l2 = (float) Math.sqrt((double) ((dx2 * dx2) + (dy2 * dy2)));
                float dx22 = dx2 * (radius / l2);
                float dy22 = dy2 * (radius / l2);
                float r1 = (float) viewWidth;
                float r2 = r1 - (borderSizePx / 2.0f);
                float f = l1;
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(borderSizePx);
                canvas.drawCircle((float) (width / 2), (float) (height / 2), r1, paint);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.parseColor("#ffe5f8"));
                paint.setAlpha(80);
                canvas.drawCircle((float) (width / 2), (float) (height / 2), r2, paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(BLUE);
                paint.setStrokeJoin(Paint.Join.ROUND);
                paint.setStrokeCap(Paint.Cap.ROUND);
                paint.setStrokeWidth(borderSizePx / 2.0f);
                float f2 = radius;
                float f3 = r2;
                float f4 = r1;
                Rect rect2 = rect;
                Paint paint2 = paint;
                float f5 = cornerSizePx;
                float f6 = l2;
                canvas.drawLine((float) (width / 2), (float) (height / 2), ((float) (width / 2)) + dx12, ((float) (height / 2)) + dy12, paint2);
                canvas.drawLine((float) (width / 2), (float) (height / 2), ((float) (width / 2)) + dx22, ((float) (height / 2)) + dy22, paint2);
                return output;
            } catch (OutOfMemoryError e) {
                return null;
            }
        } catch (OutOfMemoryError e2) {
            return null;
        }
    }

    public void setMagSide(float xPos, float yPos) {
        updateLensPosition();
        if (this.cropViewCallback.lensView().getVisibility() == 4) {
            this.cropViewCallback.lensView().setVisibility(0);
        }
    }

    public boolean isRightMag(float xPos, float yPos) {
        float imgWidth = 0.0f;
        float imgHeight = 0.0f;
        float f = this.mAngle;
        if (((f + 360.0f) / 90.0f) % 2.0f == 1.0f) {
            imgWidth = this.mImgHeight;
            imgHeight = this.mImgWidth;
        } else if (((f + 360.0f) / 90.0f) % 2.0f == 0.0f) {
            imgWidth = this.mImgWidth;
            imgHeight = this.mImgHeight;
        }
        return xPos < imgWidth / 2.0f && yPos < imgHeight / 5.0f;
    }

    private void setCenter(PointF mCenter2) {
        this.mCenter = mCenter2;
    }

    private float getFrameW() {
        return Math.min(this.mEdgePoints[1].x, this.mEdgePoints[2].x) - Math.max(this.mEdgePoints[0].x, this.mEdgePoints[3].x);
    }

    private float getFrameH() {
        return Math.min(this.mEdgePoints[2].y, this.mEdgePoints[3].y) - Math.max(this.mEdgePoints[0].y, this.mEdgePoints[1].y);
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
        private final int f14683ID;

        private CropMode(int id) {
            this.f14683ID = id;
        }

        public int getId() {
            return this.f14683ID;
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
