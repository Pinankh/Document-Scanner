package org.opencv.android;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

import com.camscanner.paperscanner.pdfcreator.common.Constant;

import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class JavaCameraView extends CameraBridgeViewBase implements Camera.PreviewCallback {
    private static final int MAGIC_TEXTURE_ID = 10;
    private static final String TAG = "JavaCameraView";
    private byte[] mBuffer;
    protected Camera mCamera;
    protected JavaCameraFrame[] mCameraFrame;

    public boolean mCameraFrameReady = false;

    public int mChainIdx = 0;

    public Mat[] mFrameChain;

    public int mPreviewFormat = 17;

    public boolean mStopThread;
    private SurfaceTexture mSurfaceTexture;
    private Thread mThread;

    public static class JavaCameraSizeAccessor implements CameraBridgeViewBase.ListItemAccessor {
        public int getWidth(Object obj) {
            return ((Camera.Size) obj).width;
        }

        public int getHeight(Object obj) {
            return ((Camera.Size) obj).height;
        }
    }

    public JavaCameraView(Context context, int cameraId) {
        super(context, cameraId);
    }

    public JavaCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public boolean initializeCamera(int width, int height) {
        int localCameraIndex;
        int i = width;
        int i2 = height;
        Log.d(TAG, "Initialize java camera");
        boolean result = true;
        synchronized (this) {
            this.mCamera = null;
            if (this.mCameraIndex == -1) {
                Log.d(TAG, "Trying to open camera with old open()");
                try {
                    this.mCamera = Camera.open();
                } catch (Exception e) {
                    Log.e(TAG, "Camera is not available (in use or does not exist): " + e.getLocalizedMessage());
                }
                if (this.mCamera == null && Build.VERSION.SDK_INT >= 9) {
                    int camIdx = 0;
                    boolean connected = false;
                    while (true) {
                        if (camIdx >= Camera.getNumberOfCameras()) {
                            break;
                        }
                        Log.d(TAG, "Trying to open camera with new open(" + Integer.valueOf(camIdx) + Constant.STR_BRACKET_CLOSE);
                        try {
                            this.mCamera = Camera.open(camIdx);
                            connected = true;
                        } catch (RuntimeException e2) {
                            Log.e(TAG, "Camera #" + camIdx + "failed to open: " + e2.getLocalizedMessage());
                        }
                        if (connected) {
                            break;
                        }
                        camIdx++;
                    }
                }
            } else if (Build.VERSION.SDK_INT >= 9) {
                int localCameraIndex2 = this.mCameraIndex;
                if (this.mCameraIndex == 99) {
                    Log.i(TAG, "Trying to open back camera");
                    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                    int camIdx2 = 0;
                    while (true) {
                        if (camIdx2 >= Camera.getNumberOfCameras()) {
                            break;
                        }
                        Camera.getCameraInfo(camIdx2, cameraInfo);
                        if (cameraInfo.facing == 0) {
                            localCameraIndex2 = camIdx2;
                            break;
                        }
                        camIdx2++;
                    }
                    localCameraIndex = localCameraIndex2;
                } else {
                    if (this.mCameraIndex == 98) {
                        Log.i(TAG, "Trying to open front camera");
                        Camera.CameraInfo cameraInfo2 = new Camera.CameraInfo();
                        int camIdx3 = 0;
                        while (true) {
                            if (camIdx3 >= Camera.getNumberOfCameras()) {
                                break;
                            }
                            Camera.getCameraInfo(camIdx3, cameraInfo2);
                            if (cameraInfo2.facing == 1) {
                                localCameraIndex = camIdx3;
                                break;
                            }
                            camIdx3++;
                        }
                    }
                    localCameraIndex = localCameraIndex2;
                }
                if (localCameraIndex == 99) {
                    Log.e(TAG, "Back camera not found!");
                } else if (localCameraIndex == 98) {
                    Log.e(TAG, "Front camera not found!");
                } else {
                    Log.d(TAG, "Trying to open camera with new open(" + Integer.valueOf(localCameraIndex) + Constant.STR_BRACKET_CLOSE);
                    try {
                        this.mCamera = Camera.open(localCameraIndex);
                    } catch (RuntimeException e3) {
                        Log.e(TAG, "Camera #" + localCameraIndex + "failed to open: " + e3.getLocalizedMessage());
                    }
                }
            }
            if (this.mCamera == null) {
                return false;
            }
            try {
                Camera.Parameters params = this.mCamera.getParameters();
                Log.d(TAG, "getSupportedPreviewSizes()");
                List<Camera.Size> sizes = params.getSupportedPreviewSizes();
                if (sizes != null) {
                    Size frameSize = calculateCameraFrameSize(sizes, new JavaCameraSizeAccessor(), i, i2);
                    if (!Build.FINGERPRINT.startsWith("generic") && !Build.FINGERPRINT.startsWith("unknown") && !Build.MODEL.contains("google_sdk") && !Build.MODEL.contains("Emulator") && !Build.MODEL.contains("Android SDK built for x86") && !Build.MANUFACTURER.contains("Genymotion") && (!Build.BRAND.startsWith("generic") || !Build.DEVICE.startsWith("generic"))) {
                        if (!"google_sdk".equals(Build.PRODUCT)) {
                            params.setPreviewFormat(17);
                            this.mPreviewFormat = params.getPreviewFormat();
                            Log.d(TAG, "Set preview size to " + Integer.valueOf((int) frameSize.width) + "x" + Integer.valueOf((int) frameSize.height));
                            params.setPreviewSize((int) frameSize.width, (int) frameSize.height);
                            if (Build.VERSION.SDK_INT >= 14 && !Build.MODEL.equals("GT-I9100")) {
                                params.setRecordingHint(true);
                            }
                            List<String> FocusModes = params.getSupportedFocusModes();
                            if (FocusModes != null && FocusModes.contains("continuous-video")) {
                                params.setFocusMode("continuous-video");
                            }
                            this.mCamera.setParameters(params);
                            Camera.Parameters params2 = this.mCamera.getParameters();
                            this.mFrameWidth = params2.getPreviewSize().width;
                            this.mFrameHeight = params2.getPreviewSize().height;
                            if (getLayoutParams().width == -1 || getLayoutParams().height != -1) {
                                this.mScale = 0.0f;
                            } else {
                                this.mScale = Math.min(((float) i2) / ((float) this.mFrameHeight), ((float) i) / ((float) this.mFrameWidth));
                            }
                            if (this.mFpsMeter != null) {
                                this.mFpsMeter.setResolution(this.mFrameWidth, this.mFrameHeight);
                            }
                            this.mBuffer = new byte[((ImageFormat.getBitsPerPixel(params2.getPreviewFormat()) * (this.mFrameWidth * this.mFrameHeight)) / 8)];
                            this.mCamera.addCallbackBuffer(this.mBuffer);
                            this.mCamera.setPreviewCallbackWithBuffer(this);
                            this.mFrameChain = new Mat[2];
                            this.mFrameChain[0] = new Mat(this.mFrameHeight + (this.mFrameHeight / 2), this.mFrameWidth, CvType.CV_8UC1);
                            this.mFrameChain[1] = new Mat(this.mFrameHeight + (this.mFrameHeight / 2), this.mFrameWidth, CvType.CV_8UC1);
                            AllocateCache();
                            this.mCameraFrame = new JavaCameraFrame[2];
                            this.mCameraFrame[0] = new JavaCameraFrame(this.mFrameChain[0], this.mFrameWidth, this.mFrameHeight);
                            this.mCameraFrame[1] = new JavaCameraFrame(this.mFrameChain[1], this.mFrameWidth, this.mFrameHeight);
                            if (Build.VERSION.SDK_INT < 11) {
                                this.mSurfaceTexture = new SurfaceTexture(10);
                                this.mCamera.setPreviewTexture(this.mSurfaceTexture);
                            } else {
                                this.mCamera.setPreviewDisplay((SurfaceHolder) null);
                            }
                            Log.d(TAG, "startPreview");
                            this.mCamera.startPreview();
                        }
                    }
                    params.setPreviewFormat(842094169);
                    this.mPreviewFormat = params.getPreviewFormat();
                    Log.d(TAG, "Set preview size to " + Integer.valueOf((int) frameSize.width) + "x" + Integer.valueOf((int) frameSize.height));
                    params.setPreviewSize((int) frameSize.width, (int) frameSize.height);
                    params.setRecordingHint(true);
                    List<String> FocusModes2 = params.getSupportedFocusModes();
                    params.setFocusMode("continuous-video");
                    this.mCamera.setParameters(params);
                    Camera.Parameters params22 = this.mCamera.getParameters();
                    this.mFrameWidth = params22.getPreviewSize().width;
                    this.mFrameHeight = params22.getPreviewSize().height;
                    if (getLayoutParams().width == -1) {
                    }
                    this.mScale = 0.0f;
                    if (this.mFpsMeter != null) {
                    }
                    this.mBuffer = new byte[((ImageFormat.getBitsPerPixel(params22.getPreviewFormat()) * (this.mFrameWidth * this.mFrameHeight)) / 8)];
                    this.mCamera.addCallbackBuffer(this.mBuffer);
                    this.mCamera.setPreviewCallbackWithBuffer(this);
                    this.mFrameChain = new Mat[2];
                    this.mFrameChain[0] = new Mat(this.mFrameHeight + (this.mFrameHeight / 2), this.mFrameWidth, CvType.CV_8UC1);
                    this.mFrameChain[1] = new Mat(this.mFrameHeight + (this.mFrameHeight / 2), this.mFrameWidth, CvType.CV_8UC1);
                    AllocateCache();
                    this.mCameraFrame = new JavaCameraFrame[2];
                    this.mCameraFrame[0] = new JavaCameraFrame(this.mFrameChain[0], this.mFrameWidth, this.mFrameHeight);
                    this.mCameraFrame[1] = new JavaCameraFrame(this.mFrameChain[1], this.mFrameWidth, this.mFrameHeight);
                    if (Build.VERSION.SDK_INT < 11) {
                    }
                    Log.d(TAG, "startPreview");
                    this.mCamera.startPreview();
                } else {
                    result = false;
                }
            } catch (Exception e4) {
                result = false;
                e4.printStackTrace();
            }
        }
        return result;
    }


    public void releaseCamera() {
        synchronized (this) {
            if (this.mCamera != null) {
                this.mCamera.stopPreview();
                this.mCamera.setPreviewCallback((Camera.PreviewCallback) null);
                this.mCamera.release();
            }
            this.mCamera = null;
            if (this.mFrameChain != null) {
                this.mFrameChain[0].release();
                this.mFrameChain[1].release();
            }
            if (this.mCameraFrame != null) {
                this.mCameraFrame[0].release();
                this.mCameraFrame[1].release();
            }
        }
    }


    public boolean connectCamera(int width, int height) {
        Log.d(TAG, "Connecting to camera");
        if (!initializeCamera(width, height)) {
            return false;
        }
        this.mCameraFrameReady = false;
        Log.d(TAG, "Starting processing thread");
        this.mStopThread = false;
        this.mThread = new Thread(new CameraWorker());
        this.mThread.start();
        return true;
    }


    public void disconnectCamera() {
        Log.d(TAG, "Disconnecting from camera");
        try {
            this.mStopThread = true;
            Log.d(TAG, "Notify thread");
            synchronized (this) {
                notify();
            }
            Log.d(TAG, "Waiting for thread");
            if (this.mThread != null) {
                this.mThread.join();
            }
        } catch (InterruptedException e) {
            try {
                e.printStackTrace();
            } catch (Throwable th) {
                this.mThread = null;
                throw th;
            }
        }
        this.mThread = null;
        releaseCamera();
        this.mCameraFrameReady = false;
    }

    public void onPreviewFrame(byte[] frame, Camera arg1) {
        synchronized (this) {
            this.mFrameChain[this.mChainIdx].put(0, 0, frame);
            this.mCameraFrameReady = true;
            notify();
        }
        Camera camera = this.mCamera;
        if (camera != null) {
            camera.addCallbackBuffer(this.mBuffer);
        }
    }

    private class JavaCameraFrame implements CameraBridgeViewBase.CvCameraViewFrame {
        private int mHeight;
        private Mat mRgba = new Mat();
        private int mWidth;
        private Mat mYuvFrameData;

        public Mat gray() {
            return this.mYuvFrameData.submat(0, this.mHeight, 0, this.mWidth);
        }

        public Mat rgba() {
            if (JavaCameraView.this.mPreviewFormat == 17) {
                Imgproc.cvtColor(this.mYuvFrameData, this.mRgba, 96, 4);
            } else if (JavaCameraView.this.mPreviewFormat == 842094169) {
                Imgproc.cvtColor(this.mYuvFrameData, this.mRgba, 100, 4);
            } else {
                throw new IllegalArgumentException("Preview Format can be NV21 or YV12");
            }
            return this.mRgba;
        }

        public JavaCameraFrame(Mat Yuv420sp, int width, int height) {
            this.mWidth = width;
            this.mHeight = height;
            this.mYuvFrameData = Yuv420sp;
        }

        public void release() {
            this.mRgba.release();
        }
    }

    private class CameraWorker implements Runnable {
        private CameraWorker() {
        }

        public void run() {
            do {
                boolean hasFrame = false;
                synchronized (JavaCameraView.this) {
                    while (!JavaCameraView.this.mCameraFrameReady && !JavaCameraView.this.mStopThread) {
                        try {
                            JavaCameraView.this.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (JavaCameraView.this.mCameraFrameReady) {
                        int unused = JavaCameraView.this.mChainIdx = 1 - JavaCameraView.this.mChainIdx;
                        boolean unused2 = JavaCameraView.this.mCameraFrameReady = false;
                        hasFrame = true;
                    }
                }
                if (!JavaCameraView.this.mStopThread && hasFrame && !JavaCameraView.this.mFrameChain[1 - JavaCameraView.this.mChainIdx].empty()) {
                    JavaCameraView javaCameraView = JavaCameraView.this;
                    javaCameraView.deliverAndDrawFrame(javaCameraView.mCameraFrame[1 - JavaCameraView.this.mChainIdx]);
                }
            } while (!JavaCameraView.this.mStopThread);
            Log.d(JavaCameraView.TAG, "Finish processing thread");
        }
    }
}
