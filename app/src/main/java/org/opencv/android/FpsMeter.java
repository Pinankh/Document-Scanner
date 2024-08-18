package org.opencv.android;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import java.text.DecimalFormat;
import org.opencv.core.Core;

public class FpsMeter {
    private static final DecimalFormat FPS_FORMAT = new DecimalFormat("0.00");
    private static final int STEP = 20;
    private static final String TAG = "FpsMeter";
    private int mFramesCounter;
    private double mFrequency;
    int mHeight = 0;
    boolean mIsInitialized = false;
    Paint mPaint;
    private String mStrfps;
    int mWidth = 0;
    private long mprevFrameTime;

    public void init() {
        this.mFramesCounter = 0;
        this.mFrequency = Core.getTickFrequency();
        this.mprevFrameTime = Core.getTickCount();
        this.mStrfps = "";
        this.mPaint = new Paint();
        this.mPaint.setColor(-16776961);
        this.mPaint.setTextSize(20.0f);
    }

    public void measure() {
        if (!this.mIsInitialized) {
            init();
            this.mIsInitialized = true;
            return;
        }
        this.mFramesCounter++;
        if (this.mFramesCounter % 20 == 0) {
            long time = Core.getTickCount();
            double d = (double) (time - this.mprevFrameTime);
            Double.isNaN(d);
            double fps = (this.mFrequency * 20.0d) / d;
            this.mprevFrameTime = time;
            if (this.mWidth == 0 || this.mHeight == 0) {
                this.mStrfps = FPS_FORMAT.format(fps) + " FPS";
            } else {
                this.mStrfps = FPS_FORMAT.format(fps) + " FPS@" + Integer.valueOf(this.mWidth) + "x" + Integer.valueOf(this.mHeight);
            }
            Log.i(TAG, this.mStrfps);
        }
    }

    public void setResolution(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    public void draw(Canvas canvas, float offsetx, float offsety) {
        Log.d(TAG, this.mStrfps);
        canvas.drawText(this.mStrfps, offsetx, offsety, this.mPaint);
    }
}
