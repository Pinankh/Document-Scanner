package org.opencv.android;

import android.content.Context;
import android.content.res.TypedArray;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

import com.camscanner.paperscanner.pdfcreator.R;

public class CameraGLSurfaceView extends GLSurfaceView {
    private static final String LOGTAG = "CameraGLSurfaceView";
    private CameraGLRendererBase mRenderer;
    private CameraTextureListener mTexListener;

    public interface CameraTextureListener {
        boolean onCameraTexture(int i, int i2, int i3, int i4);

        void onCameraViewStarted(int i, int i2);

        void onCameraViewStopped();
    }

    public CameraGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray styledAttrs = getContext().obtainStyledAttributes(attrs, R.styleable.CameraBridgeViewBase);
        int cameraIndex = styledAttrs.getInt(R.styleable.CameraBridgeViewBase_camera_id, -1);
        styledAttrs.recycle();
        if (Build.VERSION.SDK_INT >= 21) {
            this.mRenderer = new Camera2Renderer(this);
        } else {
            this.mRenderer = new CameraRenderer(this);
        }
        setCameraIndex(cameraIndex);
        setEGLContextClientVersion(2);
        setRenderer(this.mRenderer);
        setRenderMode(0);
    }

    public void setCameraTextureListener(CameraTextureListener texListener) {
        this.mTexListener = texListener;
    }

    public CameraTextureListener getCameraTextureListener() {
        return this.mTexListener;
    }

    public void setCameraIndex(int cameraIndex) {
        this.mRenderer.setCameraIndex(cameraIndex);
    }

    public void setMaxCameraPreviewSize(int maxWidth, int maxHeight) {
        this.mRenderer.setMaxCameraPreviewSize(maxWidth, maxHeight);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        this.mRenderer.mHaveSurface = false;
        super.surfaceDestroyed(holder);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        super.surfaceChanged(holder, format, w, h);
    }

    public void onResume() {
        Log.i(LOGTAG, "onResume");
        super.onResume();
        this.mRenderer.onResume();
    }

    public void onPause() {
        Log.i(LOGTAG, "onPause");
        this.mRenderer.onPause();
        super.onPause();
    }

    public void enableView() {
        this.mRenderer.enableView();
    }

    public void disableView() {
        this.mRenderer.disableView();
    }
}
