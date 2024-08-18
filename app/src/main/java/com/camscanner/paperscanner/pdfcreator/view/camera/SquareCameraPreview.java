package com.camscanner.paperscanner.pdfcreator.view.camera;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import com.camscanner.paperscanner.pdfcreator.R;

public class SquareCameraPreview extends SurfaceView {
    private static final double ASPECT_RATIO = 0.75d;
    private static final int FOCUS_MAX_BOUND = 1000;
    private static final int FOCUS_MIN_BOUND = -1000;
    private static final int FOCUS_SQR_SIZE = 100;
    public static final String TAG = SquareCameraPreview.class.getSimpleName();
    private static final int ZOOM_DELTA = 1;
    private static final int ZOOM_IN = 1;
    private static final int ZOOM_OUT = 0;
    private boolean drawingViewSet = false;
    public Camera mCamera;
    private ArrayList<Camera.Area> mFocusAreas;
    private ScaleGestureDetector mScaleDetector;
    private boolean m_bFocus;
    private boolean m_bFocusReady;
    private boolean m_bZoomSupported;
    private float m_fltLastTouchX;
    private float m_fltLastTouchY;
    private int m_nMaxZoom;
    /* access modifiers changed from: private */
    public int m_nScaleFactor = 1;
    private Camera.Area m_rectFocusArea;
    /* access modifiers changed from: private */
    public DrawingView m_vwDrawing;
    Camera.AutoFocusCallback myAutoFocusCallback = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            if (success && SquareCameraPreview.this.m_vwDrawing != null) {
                SquareCameraPreview.this.m_vwDrawing.setHaveTouch(true, SquareCameraPreview.this.touchRect, true);
                SquareCameraPreview.this.m_vwDrawing.invalidate();
            }
        }
    };
    Rect touchRect;

    public SquareCameraPreview(Context context) {
        super(context);
        init(context);
    }

    public SquareCameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SquareCameraPreview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        this.m_rectFocusArea = new Camera.Area(new Rect(), 1000);
        this.mFocusAreas = new ArrayList<>();
        this.mFocusAreas.add(this.m_rectFocusArea);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        boolean isPortrait = true;
        if (getResources().getConfiguration().orientation != 1) {
            isPortrait = false;
        }
        if (isPortrait) {
            double d = (double) height;
            Double.isNaN(d);
            if (((double) width) > d * ASPECT_RATIO) {
                double d2 = (double) height;
                Double.isNaN(d2);
                width = (int) ((d2 * ASPECT_RATIO) + 0.5d);
            } else {
                double d3 = (double) width;
                Double.isNaN(d3);
                height = (int) ((d3 / ASPECT_RATIO) + 0.5d);
            }
        } else {
            double d4 = (double) width;
            Double.isNaN(d4);
            if (((double) height) > d4 * ASPECT_RATIO) {
                double d5 = (double) width;
                Double.isNaN(d5);
                height = (int) ((d5 * ASPECT_RATIO) + 0.5d);
            } else {
                double d6 = (double) height;
                Double.isNaN(d6);
                width = (int) ((d6 / ASPECT_RATIO) + 0.5d);
            }
        }
        setMeasuredDimension(width, height);
    }

    public int getViewWidth() {
        return getWidth();
    }

    public int getViewHeight() {
        return getHeight();
    }

    public void setCamera(Camera camera) {
        this.mCamera = camera;
        if (camera != null) {
            Camera.Parameters params = camera.getParameters();
            this.m_bZoomSupported = params.isZoomSupported();
            if (this.m_bZoomSupported) {
                this.m_nMaxZoom = params.getMaxZoom();
            }
            params.setFocusMode("auto");
            this.mCamera.setParameters(params);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        this.mScaleDetector.onTouchEvent(event);
        int action = event.getAction() & 255;
        if (action == 0) {
            this.m_bFocus = true;
            this.m_fltLastTouchX = event.getX();
            this.m_fltLastTouchY = event.getY();
        } else if (action != 1) {
            if (action == 5) {
                try {
                    this.mCamera.cancelAutoFocus();
                } catch (RuntimeException ex) {
                    ex.printStackTrace();
                }
                this.m_bFocus = false;
            }
        } else if (this.m_bFocus && this.m_bFocusReady) {
            Camera camera = this.mCamera;
            if (camera == null) {
                Toast.makeText(getContext(), R.string.alert_no_camera, 0).show();
            } else {
                try {
                    handleFocus(camera.getParameters());
                } catch (RuntimeException ex2) {
                    ex2.printStackTrace();
                }
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void handleZoom(Camera.Parameters params) {
        int zoom = params.getZoom();
        int i = this.m_nScaleFactor;
        if (i == 1) {
            if (zoom < this.m_nMaxZoom) {
                zoom++;
            }
        } else if (i == 0 && zoom > 0) {
            zoom--;
        }
        params.setZoom(zoom);
        this.mCamera.setParameters(params);
    }

    private void handleFocus(Camera.Parameters params) {
        float x = this.m_fltLastTouchX;
        float y = this.m_fltLastTouchY;
        this.touchRect = new Rect((int) (x - 70.0f), (int) (y - 70.0f), (int) (x + 70.0f), (int) (70.0f + y));
        Rect targetFocusRect = new Rect(((this.touchRect.left * 2000) / getWidth()) - 1000, ((this.touchRect.top * 2000) / getHeight()) - 1000, ((this.touchRect.right * 2000) / getWidth()) - 1000, ((this.touchRect.bottom * 2000) / getHeight()) - 1000);
        List<String> supportedFocusModes = params.getSupportedFocusModes();
        if (supportedFocusModes != null && supportedFocusModes.contains("auto")) {
            doTouchFocus(targetFocusRect);
        }
        DrawingView drawingView = this.m_vwDrawing;
        if (drawingView != null && this.drawingViewSet) {
            drawingView.setHaveTouch(true, this.touchRect, false);
            this.m_vwDrawing.invalidate();
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    SquareCameraPreview.this.m_vwDrawing.setHaveTouch(false, new Rect(0, 0, 0, 0), false);
                    SquareCameraPreview.this.m_vwDrawing.invalidate();
                }
            }, 3000);
        }
    }

    public void setDrawingView(DrawingView dView) {
        this.m_vwDrawing = dView;
        this.drawingViewSet = true;
    }

    public void setIsFocusReady(boolean isFocusReady) {
        this.m_bFocusReady = isFocusReady;
    }

    private boolean setFocusBound(float x, float y) {
        int left = (int) (x - 50.0f);
        int right = (int) (x + 50.0f);
        int top = (int) (y - 50.0f);
        int bottom = (int) (50.0f + y);
        if (-1000 > left || left > 1000 || -1000 > right || right > 1000 || -1000 > top || top > 1000 || -1000 > bottom || bottom > 1000) {
            return false;
        }
        this.m_rectFocusArea.rect.set(left, top, right, bottom);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private ScaleListener() {
        }

        public boolean onScale(ScaleGestureDetector detector) {
            try {
                int unused = SquareCameraPreview.this.m_nScaleFactor = (int) detector.getScaleFactor();
                SquareCameraPreview.this.handleZoom(SquareCameraPreview.this.mCamera.getParameters());
                return true;
            } catch (RuntimeException ex) {
                ex.printStackTrace();
                return true;
            }
        }
    }

    public void doTouchFocus(Rect tfocusRect) {
        try {
            List<Camera.Area> focusList = new ArrayList<>();
            focusList.add(new Camera.Area(tfocusRect, 1000));
            Camera.Parameters para = this.mCamera.getParameters();
            para.setFocusMode("auto");
            para.setFocusAreas(focusList);
            para.setMeteringAreas(focusList);
            this.mCamera.setParameters(para);
            this.mCamera.autoFocus(this.myAutoFocusCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
