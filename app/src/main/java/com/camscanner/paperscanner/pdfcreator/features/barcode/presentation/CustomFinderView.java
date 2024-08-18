package com.camscanner.paperscanner.pdfcreator.features.barcode.presentation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import me.dm7.barcodescanner.core.DisplayUtils;
import me.dm7.barcodescanner.core.IViewFinder;
import com.camscanner.paperscanner.pdfcreator.R;

public class CustomFinderView extends View implements IViewFinder {
    private static final long ANIMATION_DELAY = 80;
    private static final float DEFAULT_SQUARE_DIMENSION_RATIO = 0.7f;
    private static final float LANDSCAPE_HEIGHT_RATIO = 0.625f;
    private static final float LANDSCAPE_WIDTH_HEIGHT_RATIO = 1.4f;
    private static final int MIN_DIMENSION_DIFF = 50;
    private static final int POINT_SIZE = 10;
    private static final float PORTRAIT_WIDTH_HEIGHT_RATIO = 0.75f;
    private static final float PORTRAIT_WIDTH_RATIO = 0.7f;
    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private static final String TAG = "ViewFinderView";
    protected float mBorderLineLength;
    protected Paint mBorderPaint;
    private float mBordersAlpha;
    private final int mDefaultBorderColor = getResources().getColor(R.color.colorPrimary);
    private final float mDefaultBorderLineLength = getResources().getDimension(R.dimen.qr_border_length);
    private final float mDefaultBorderStrokeWidth = getResources().getDimension(R.dimen.qr_border_width);
    private final int mDefaultFrameColor = getResources().getColor(R.color.white);
    private final float mDefaultFrameStrokeWidth = getResources().getDimension(R.dimen.qr_frame_width);
    private final int mDefaultLaserColor = getResources().getColor(R.color.colorRed);
    private final int mDefaultMaskColor = getResources().getColor(R.color.viewfinder_mask);
    protected Paint mFinderMaskPaint;
    protected Paint mFramePaint;
    private Rect mFramingRect;
    private boolean mIsLaserEnabled = true;
    protected Paint mLaserPaint;
    protected boolean mSquareViewFinder = true;
    private int mViewFinderOffset = 0;
    private int scannerAlpha;

    public CustomFinderView(Context context) {
        super(context);
        init();
    }

    public CustomFinderView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    private void init() {
        this.mLaserPaint = new Paint();
        this.mLaserPaint.setColor(this.mDefaultLaserColor);
        this.mLaserPaint.setStyle(Paint.Style.FILL);
        this.mFinderMaskPaint = new Paint();
        this.mFinderMaskPaint.setColor(this.mDefaultMaskColor);
        this.mBorderPaint = new Paint();
        this.mBorderPaint.setColor(this.mDefaultBorderColor);
        this.mBorderPaint.setStyle(Paint.Style.STROKE);
        this.mBorderPaint.setStrokeWidth(this.mDefaultBorderStrokeWidth);
        this.mBorderPaint.setAntiAlias(true);
        this.mFramePaint = new Paint();
        this.mFramePaint.setColor(this.mDefaultFrameColor);
        this.mFramePaint.setStyle(Paint.Style.STROKE);
        this.mFramePaint.setStrokeWidth(this.mDefaultFrameStrokeWidth);
        this.mFramePaint.setAntiAlias(true);
        this.mBorderLineLength = this.mDefaultBorderLineLength;
    }

    public void setLaserColor(int laserColor) {
        this.mLaserPaint.setColor(laserColor);
    }

    public void setMaskColor(int maskColor) {
        this.mFinderMaskPaint.setColor(maskColor);
    }

    public void setBorderColor(int borderColor) {
        this.mBorderPaint.setColor(borderColor);
    }

    public void setBorderStrokeWidth(int borderStrokeWidth) {
        this.mBorderPaint.setStrokeWidth((float) borderStrokeWidth);
    }

    public void setBorderLineLength(int borderLineLength) {
        this.mBorderLineLength = (float) borderLineLength;
    }

    public void setLaserEnabled(boolean isLaserEnabled) {
        this.mIsLaserEnabled = isLaserEnabled;
    }

    public void setBorderCornerRounded(boolean isBorderCornersRounded) {
        if (isBorderCornersRounded) {
            this.mBorderPaint.setStrokeJoin(Paint.Join.ROUND);
        } else {
            this.mBorderPaint.setStrokeJoin(Paint.Join.BEVEL);
        }
    }

    public void setBorderAlpha(float alpha) {
        this.mBordersAlpha = alpha;
        this.mBorderPaint.setAlpha((int) (255.0f * alpha));
    }

    public void setBorderCornerRadius(int borderCornersRadius) {
        this.mBorderPaint.setPathEffect(new CornerPathEffect((float) borderCornersRadius));
    }

    public void setViewFinderOffset(int offset) {
        this.mViewFinderOffset = offset;
    }

    public void setSquareViewFinder(boolean set) {
        this.mSquareViewFinder = set;
    }

    public void setupViewFinder() {
        updateFramingRect();
        invalidate();
    }

    public Rect getFramingRect() {
        return this.mFramingRect;
    }

    public void onDraw(Canvas canvas) {
        if (getFramingRect() != null) {
            drawViewFinderMask(canvas);
            drawViewFinderFrame(canvas);
            drawViewFinderBorder(canvas);
            if (this.mIsLaserEnabled) {
                drawLaser(canvas);
            }
        }
    }

    public void drawViewFinderMask(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        Rect framingRect = getFramingRect();
        canvas.drawRect(0.0f, 0.0f, (float) width, (float) framingRect.top, this.mFinderMaskPaint);
        Canvas canvas2 = canvas;
        canvas2.drawRect(0.0f, (float) framingRect.top, (float) framingRect.left, (float) (framingRect.bottom + 1), this.mFinderMaskPaint);
        canvas2.drawRect((float) (framingRect.right + 1), (float) framingRect.top, (float) width, (float) (framingRect.bottom + 1), this.mFinderMaskPaint);
        canvas2.drawRect(0.0f, (float) (framingRect.bottom + 1), (float) width, (float) height, this.mFinderMaskPaint);
    }

    private void drawViewFinderFrame(Canvas canvas) {
        canvas.drawRect(getFramingRect(), this.mFramePaint);
    }

    public void drawViewFinderBorder(Canvas canvas) {
        Rect framingRect = getFramingRect();
        float offset = (this.mDefaultBorderStrokeWidth / 2.0f) - this.mDefaultFrameStrokeWidth;
        Path path = new Path();
        path.moveTo(((float) framingRect.left) - offset, ((float) framingRect.top) + this.mBorderLineLength);
        path.lineTo(((float) framingRect.left) - offset, ((float) framingRect.top) - offset);
        path.lineTo(((float) framingRect.left) + this.mBorderLineLength, ((float) framingRect.top) - offset);
        canvas.drawPath(path, this.mBorderPaint);
        path.moveTo(((float) framingRect.right) + offset, ((float) framingRect.top) + this.mBorderLineLength);
        path.lineTo(((float) framingRect.right) + offset, ((float) framingRect.top) - offset);
        path.lineTo(((float) framingRect.right) - this.mBorderLineLength, ((float) framingRect.top) - offset);
        canvas.drawPath(path, this.mBorderPaint);
        path.moveTo(((float) framingRect.right) + offset, ((float) framingRect.bottom) - this.mBorderLineLength);
        path.lineTo(((float) framingRect.right) + offset, ((float) framingRect.bottom) + offset);
        path.lineTo(((float) framingRect.right) - this.mBorderLineLength, ((float) framingRect.bottom) + offset);
        canvas.drawPath(path, this.mBorderPaint);
        path.moveTo(((float) framingRect.left) - offset, ((float) framingRect.bottom) - this.mBorderLineLength);
        path.lineTo(((float) framingRect.left) - offset, ((float) framingRect.bottom) + offset);
        path.lineTo(((float) framingRect.left) + this.mBorderLineLength, ((float) framingRect.bottom) + offset);
        canvas.drawPath(path, this.mBorderPaint);
    }

    public void drawLaser(Canvas canvas) {
        Rect framingRect = getFramingRect();
        this.mLaserPaint.setAlpha(SCANNER_ALPHA[this.scannerAlpha]);
        this.scannerAlpha = (this.scannerAlpha + 1) % SCANNER_ALPHA.length;
        int middle = (framingRect.height() / 2) + framingRect.top;
        canvas.drawRect((float) (framingRect.left + 2), (float) (middle - 1), (float) (framingRect.right - 1), (float) (middle + 2), this.mLaserPaint);
        postInvalidateDelayed(ANIMATION_DELAY, framingRect.left - 10, framingRect.top - 10, framingRect.right + 10, framingRect.bottom + 10);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        updateFramingRect();
    }

    public synchronized void updateFramingRect() {
        int width;
        int height;
        Point viewResolution = new Point(getWidth(), getHeight());
        int orientation = DisplayUtils.getScreenOrientation(getContext());
        if (this.mSquareViewFinder) {
            if (orientation != 1) {
                height = (int) (((float) getHeight()) * 0.7f);
                width = height;
            } else {
                width = (int) (((float) getWidth()) * 0.7f);
                height = width;
            }
        } else if (orientation != 1) {
            height = (int) (((float) getHeight()) * LANDSCAPE_HEIGHT_RATIO);
            width = (int) (((float) height) * LANDSCAPE_WIDTH_HEIGHT_RATIO);
        } else {
            width = (int) (((float) getWidth()) * 0.7f);
            height = (int) (((float) width) * 0.75f);
        }
        if (width > getWidth()) {
            width = getWidth() - 50;
        }
        if (height > getHeight()) {
            height = getHeight() - 50;
        }
        int leftOffset = (viewResolution.x - width) / 2;
        int topOffset = (viewResolution.y - height) / 2;
        this.mFramingRect = new Rect(this.mViewFinderOffset + leftOffset, this.mViewFinderOffset + topOffset, (leftOffset + width) - this.mViewFinderOffset, (topOffset + height) - this.mViewFinderOffset);
    }
}
