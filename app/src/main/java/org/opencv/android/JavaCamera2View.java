package org.opencv.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import com.camscanner.paperscanner.pdfcreator.common.Constant;
import com.camscanner.paperscanner.pdfcreator.features.analytics.AnalyticsConstants;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;


@androidx.annotation.RequiresApi(api = android.os.Build.VERSION_CODES.LOLLIPOP)
public class JavaCamera2View extends CameraBridgeViewBase {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final String LOGTAG = "JavaCamera2View";

    public Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;

    public CameraDevice mCameraDevice;
    private String mCameraID;

    public CameraCaptureSession mCaptureSession;
    private ImageReader mImageReader;

    public int mPreviewFormat = 35;

    public CaptureRequest.Builder mPreviewRequestBuilder;
    private Size mPreviewSize = new Size(-1, -1);
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        public void onOpened(CameraDevice cameraDevice) {
            CameraDevice unused = JavaCamera2View.this.mCameraDevice = cameraDevice;
            JavaCamera2View.this.createCameraPreviewSession();
        }

        public void onDisconnected(CameraDevice cameraDevice) {
            cameraDevice.close();
            CameraDevice unused = JavaCamera2View.this.mCameraDevice = null;
        }

        public void onError(CameraDevice cameraDevice, int error) {
            cameraDevice.close();
            CameraDevice unused = JavaCamera2View.this.mCameraDevice = null;
        }
    };

    public JavaCamera2View(Context context, int cameraId) {
        super(context, cameraId);
    }

    public JavaCamera2View(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void startBackgroundThread() {
        Log.i(LOGTAG, "startBackgroundThread");
        stopBackgroundThread();
        this.mBackgroundThread = new HandlerThread("OpenCVCameraBackground");
        this.mBackgroundThread.start();
        this.mBackgroundHandler = new Handler(this.mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        Log.i(LOGTAG, "stopBackgroundThread");
        HandlerThread handlerThread = this.mBackgroundThread;
        if (handlerThread != null) {
            handlerThread.quitSafely();
            try {
                this.mBackgroundThread.join();
                this.mBackgroundThread = null;
                this.mBackgroundHandler = null;
            } catch (InterruptedException e) {
                Log.e(LOGTAG, "stopBackgroundThread", e);
            }
        }
    }


    public boolean initializeCamera() {
        Log.i(LOGTAG, "initializeCamera");
        @SuppressLint("WrongConstant") CameraManager manager = (CameraManager) getContext().getSystemService(AnalyticsConstants.PARAM_VALUE_PRE_SCAN_MODE_CAMERA);
        try {
            String[] camList = manager.getCameraIdList();
            if (camList.length == 0) {
                Log.e(LOGTAG, "Error: camera isn't detected.");
                return false;
            }
            if (this.mCameraIndex != -1) {
                int length = camList.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    String cameraID = camList[i];
                    CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraID);
                    if (!((this.mCameraIndex == 99 && ((Integer) characteristics.get(CameraCharacteristics.LENS_FACING)).intValue() == 1) || (this.mCameraIndex == 98 && ((Integer) characteristics.get(CameraCharacteristics.LENS_FACING)).intValue() == 0))) {
                        i++;
                    }
                }
            } else {
                this.mCameraID = camList[0];
            }
            if (this.mCameraID != null) {
                Log.i(LOGTAG, "Opening camera: " + this.mCameraID);
                manager.openCamera(this.mCameraID, this.mStateCallback, this.mBackgroundHandler);
            }
            return true;
        } catch (CameraAccessException e) {
            Log.e(LOGTAG, "OpenCamera - Camera Access Exception", e);
            return false;
        } catch (IllegalArgumentException e2) {
            Log.e(LOGTAG, "OpenCamera - Illegal Argument Exception", e2);
            return false;
        } catch (SecurityException e3) {
            Log.e(LOGTAG, "OpenCamera - Security Exception", e3);
            return false;
        }
    }

    @SuppressLint("WrongConstant")
    public void createCameraPreviewSession() {
        final int w = this.mPreviewSize.getWidth();
        final int h = this.mPreviewSize.getHeight();
        Log.i(LOGTAG, "createCameraPreviewSession(" + w + "x" + h + Constant.STR_BRACKET_CLOSE);
        if (w >= 0 && h >= 0) {
            try {
                if (this.mCameraDevice == null) {
                    Log.e(LOGTAG, "createCameraPreviewSession: camera isn't opened");
                } else if (this.mCaptureSession != null) {
                    Log.e(LOGTAG, "createCameraPreviewSession: mCaptureSession is already started");
                } else {
                    this.mImageReader = ImageReader.newInstance(w, h, this.mPreviewFormat, 2);
                    this.mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                        static final /* synthetic */ boolean $assertionsDisabled = false;

                        public void onImageAvailable(ImageReader reader) {
                            Image image = reader.acquireLatestImage();
                            if (image != null) {
                                Image.Plane[] planes = image.getPlanes();
                                ByteBuffer y_plane = planes[0].getBuffer();
                                ByteBuffer uv_plane = planes[1].getBuffer();
                                JavaCamera2Frame tempFrame = new JavaCamera2Frame(new Mat(h, w, CvType.CV_8UC1, y_plane), new Mat(h / 2, w / 2, CvType.CV_8UC2, uv_plane), w, h);
                                JavaCamera2View.this.deliverAndDrawFrame(tempFrame);
                                tempFrame.release();
                                image.close();
                            }
                        }
                    }, this.mBackgroundHandler);
                    Surface surface = this.mImageReader.getSurface();
                    this.mPreviewRequestBuilder = this.mCameraDevice.createCaptureRequest(1);
                    this.mPreviewRequestBuilder.addTarget(surface);
                    this.mCameraDevice.createCaptureSession(Arrays.asList(new Surface[]{surface}), new CameraCaptureSession.StateCallback() {
                        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                            Log.i(JavaCamera2View.LOGTAG, "createCaptureSession::onConfigured");
                            if (JavaCamera2View.this.mCameraDevice != null) {
                                CameraCaptureSession unused = JavaCamera2View.this.mCaptureSession = cameraCaptureSession;
                                try {
                                    JavaCamera2View.this.mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, 4);
                                    JavaCamera2View.this.mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, 2);
                                    JavaCamera2View.this.mCaptureSession.setRepeatingRequest(JavaCamera2View.this.mPreviewRequestBuilder.build(), (CameraCaptureSession.CaptureCallback) null, JavaCamera2View.this.mBackgroundHandler);
                                    Log.i(JavaCamera2View.LOGTAG, "CameraPreviewSession has been started");
                                } catch (Exception e) {
                                    Log.e(JavaCamera2View.LOGTAG, "createCaptureSession failed", e);
                                }
                            }
                        }

                        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                            Log.e(JavaCamera2View.LOGTAG, "createCameraPreviewSession failed");
                        }
                    }, (Handler) null);
                }
            } catch (CameraAccessException e) {
                Log.e(LOGTAG, "createCameraPreviewSession", e);
            }
        }
    }

    public void disconnectCamera() {
        Log.i(LOGTAG, "closeCamera");
        try {
            CameraDevice c = this.mCameraDevice;
            this.mCameraDevice = null;
            if (this.mCaptureSession != null) {
                this.mCaptureSession.close();
                this.mCaptureSession = null;
            }
            if (c != null) {
                c.close();
            }
            if (this.mImageReader != null) {
                this.mImageReader.close();
                this.mImageReader = null;
            }
        } finally {
            stopBackgroundThread();
        }
    }


    public boolean calcPreviewSize(int width, int height) {
        int i = width;
        int i2 = height;
        Log.i(LOGTAG, "calcPreviewSize: " + i + "x" + i2);
        if (this.mCameraID == null) {
            Log.e(LOGTAG, "Camera isn't initialized!");
            return false;
        }
        @SuppressLint("WrongConstant") CameraManager manager = (CameraManager) getContext().getSystemService(AnalyticsConstants.PARAM_VALUE_PRE_SCAN_MODE_CAMERA);
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(this.mCameraID);
            float aspect = ((float) i) / ((float) i2);
            Size[] sizes = ((StreamConfigurationMap) characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)).getOutputSizes(ImageReader.class);
            int bestWidth = sizes[0].getWidth();
            int bestHeight = sizes[0].getHeight();
            int length = sizes.length;
            int bestHeight2 = bestHeight;
            int bestWidth2 = bestWidth;
            int bestWidth3 = 0;
            while (bestWidth3 < length) {
                Size sz = sizes[bestWidth3];
                int w = sz.getWidth();
                int h = sz.getHeight();
                StringBuilder sb = new StringBuilder();
                CameraCharacteristics characteristics2 = characteristics;
                sb.append("trying size: ");
                int w2 = w;
                sb.append(w2);
                sb.append("x");
                CameraManager manager2 = manager;
                int h2 = h;
                try {
                    sb.append(h2);
                    Log.d(LOGTAG, sb.toString());
                    if (i >= w2 && i2 >= h2 && bestWidth2 <= w2 && bestHeight2 <= h2 && ((double) Math.abs(aspect - (((float) w2) / ((float) h2)))) < 0.2d) {
                        bestWidth2 = w2;
                        bestHeight2 = h2;
                    }
                    bestWidth3++;
                    i = width;
                    i2 = height;
                    manager = manager2;
                    characteristics = characteristics2;
                } catch (IllegalArgumentException e2) {
                    IllegalArgumentException e = e2;
                    Log.e(LOGTAG, "calcPreviewSize - Illegal Argument Exception", e);
                    return false;
                } catch (SecurityException e3) {
                    SecurityException e = e3;
                    Log.e(LOGTAG, "calcPreviewSize - Security Exception", e);
                    return false;
                }
            }
            CameraManager cameraManager = manager;
            Log.i(LOGTAG, "best size: " + bestWidth2 + "x" + bestHeight2);
            if (this.mPreviewSize.getWidth() == bestWidth2 && this.mPreviewSize.getHeight() == bestHeight2) {
                return false;
            }
            this.mPreviewSize = new Size(bestWidth2, bestHeight2);
            return true;
        } catch (CameraAccessException e4) {
            CameraAccessException  e = e4;
            Log.e(LOGTAG, "calcPreviewSize - Camera Access Exception", e);
            return false;
        } catch (IllegalArgumentException e5) {
            IllegalArgumentException e = e5;
            Log.e(LOGTAG, "calcPreviewSize - Illegal Argument Exception", e);
            return false;
        } catch (SecurityException e6) {
            SecurityException  e = e6;
            Log.e(LOGTAG, "calcPreviewSize - Security Exception", e);
            return false;
        }
    }


    public boolean connectCamera(int width, int height) {
        Log.i(LOGTAG, "setCameraPreviewSize(" + width + "x" + height + Constant.STR_BRACKET_CLOSE);
        startBackgroundThread();
        initializeCamera();
        try {
            boolean needReconfig = calcPreviewSize(width, height);
            this.mFrameWidth = this.mPreviewSize.getWidth();
            this.mFrameHeight = this.mPreviewSize.getHeight();
            if (getLayoutParams().width == -1 && getLayoutParams().height == -1) {
                this.mScale = Math.min(((float) height) / ((float) this.mFrameHeight), ((float) width) / ((float) this.mFrameWidth));
            } else {
                this.mScale = 0.0f;
            }
            AllocateCache();
            if (!needReconfig) {
                return true;
            }
            if (this.mCaptureSession != null) {
                Log.d(LOGTAG, "closing existing previewSession");
                this.mCaptureSession.close();
                this.mCaptureSession = null;
            }
            createCameraPreviewSession();
            return true;
        } catch (RuntimeException e) {
            throw new RuntimeException("Interrupted while setCameraPreviewSize.", e);
        }
    }

    private class JavaCamera2Frame implements CameraBridgeViewBase.CvCameraViewFrame {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private int mHeight;
        private Mat mRgba;
        private Mat mUVFrameData;
        private int mWidth;
        private Mat mYuvFrameData;

        public Mat gray() {
            return this.mYuvFrameData.submat(0, this.mHeight, 0, this.mWidth);
        }

        public Mat rgba() {
            if (JavaCamera2View.this.mPreviewFormat == 17) {
                Imgproc.cvtColor(this.mYuvFrameData, this.mRgba, 96, 4);
            } else if (JavaCamera2View.this.mPreviewFormat == 842094169) {
                Imgproc.cvtColor(this.mYuvFrameData, this.mRgba, 100, 4);
            } else if (JavaCamera2View.this.mPreviewFormat == 35) {
                Imgproc.cvtColorTwoPlane(this.mYuvFrameData, this.mUVFrameData, this.mRgba, 96);
            } else {
                throw new IllegalArgumentException("Preview Format can be NV21 or YV12");
            }
            return this.mRgba;
        }

        public JavaCamera2Frame(Mat Yuv420sp, int width, int height) {
            this.mWidth = width;
            this.mHeight = height;
            this.mYuvFrameData = Yuv420sp;
            this.mUVFrameData = null;
            this.mRgba = new Mat();
        }

        public JavaCamera2Frame(Mat Y, Mat UV, int width, int height) {
            this.mWidth = width;
            this.mHeight = height;
            this.mYuvFrameData = Y;
            this.mUVFrameData = UV;
            this.mRgba = new Mat();
        }

        public void release() {
            this.mRgba.release();
        }
    }
}
