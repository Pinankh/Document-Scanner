package org.opencv.android;

import android.annotation.SuppressLint;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import com.camscanner.paperscanner.pdfcreator.common.Constant;
import com.camscanner.paperscanner.pdfcreator.features.analytics.AnalyticsConstants;

import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


@androidx.annotation.RequiresApi(api = android.os.Build.VERSION_CODES.LOLLIPOP)
public class Camera2Renderer extends CameraGLRendererBase {
    protected final String LOGTAG = "Camera2Renderer";
    public Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    public CameraDevice mCameraDevice;
    private String mCameraID;
    public Semaphore mCameraOpenCloseLock = new Semaphore(1);
    public CameraCaptureSession mCaptureSession;
    public CaptureRequest.Builder mPreviewRequestBuilder;
    private Size mPreviewSize = new Size(-1, -1);
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        public void onOpened(CameraDevice cameraDevice) {
            CameraDevice unused = Camera2Renderer.this.mCameraDevice = cameraDevice;
            Camera2Renderer.this.mCameraOpenCloseLock.release();
            Camera2Renderer.this.createCameraPreviewSession();
        }

        public void onDisconnected(CameraDevice cameraDevice) {
            cameraDevice.close();
            CameraDevice unused = Camera2Renderer.this.mCameraDevice = null;
            Camera2Renderer.this.mCameraOpenCloseLock.release();
        }

        public void onError(CameraDevice cameraDevice, int error) {
            cameraDevice.close();
            CameraDevice unused = Camera2Renderer.this.mCameraDevice = null;
            Camera2Renderer.this.mCameraOpenCloseLock.release();
        }
    };

    Camera2Renderer(CameraGLSurfaceView view) {
        super(view);
    }

    public void doStart() {
        Log.d("Camera2Renderer", "doStart");
        startBackgroundThread();
        super.doStart();
    }

    public void doStop() {
        Log.d("Camera2Renderer", "doStop");
        super.doStop();
        stopBackgroundThread();
    }

    public boolean cacPreviewSize(int width, int height) {
        int i = width;
        int i2 = height;
        Log.i("Camera2Renderer", "cacPreviewSize: " + i + "x" + i2);
        if (this.mCameraID == null) {
            Log.e("Camera2Renderer", "Camera isn't initialized!");
            return false;
        }
        CameraManager manager = (CameraManager) this.mView.getContext().getSystemService(AnalyticsConstants.PARAM_VALUE_PRE_SCAN_MODE_CAMERA);
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(this.mCameraID);
            float aspect = ((float) i) / ((float) i2);
            Size[] outputSizes = ((StreamConfigurationMap) characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)).getOutputSizes(SurfaceTexture.class);
            int length = outputSizes.length;
            int bestHeight = 0;
            int bestWidth = 0;
            int bestWidth2 = 0;
            while (bestWidth2 < length) {
                Size psize = outputSizes[bestWidth2];
                int w = psize.getWidth();
                int h = psize.getHeight();
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
                    Log.d("Camera2Renderer", sb.toString());
                    if (i >= w2 && i2 >= h2 && bestWidth <= w2 && bestHeight <= h2 && ((double) Math.abs(aspect - (((float) w2) / ((float) h2)))) < 0.2d) {
                        bestWidth = w2;
                        bestHeight = h2;
                    }
                    bestWidth2++;
                    i = width;
                    i2 = height;
                    manager = manager2;
                    characteristics = characteristics2;
                } catch (IllegalArgumentException e2) {
                    Log.e("Camera2Renderer", "cacPreviewSize - Illegal Argument Exception");
                    return false;
                } catch (SecurityException e3) {
                    Log.e("Camera2Renderer", "cacPreviewSize - Security Exception");
                    return false;
                }
            }
            CameraManager cameraManager = manager;
            Log.i("Camera2Renderer", "best size: " + bestWidth + "x" + bestHeight);
            if (bestWidth == 0 || bestHeight == 0) {
                return false;
            }
            if (this.mPreviewSize.getWidth() == bestWidth && this.mPreviewSize.getHeight() == bestHeight) {
                return false;
            }
            this.mPreviewSize = new Size(bestWidth, bestHeight);
            return true;
        } catch (CameraAccessException e4) {
            Log.e("Camera2Renderer", "cacPreviewSize - Camera Access Exception");
            return false;
        } catch (IllegalArgumentException e5) {
            Log.e("Camera2Renderer", "cacPreviewSize - Illegal Argument Exception");
            return false;
        } catch (SecurityException e6) {
            Log.e("Camera2Renderer", "cacPreviewSize - Security Exception");
            return false;
        }
    }


    public void openCamera(int id) {
        Log.i("Camera2Renderer", "openCamera");
        @SuppressLint("WrongConstant") CameraManager manager = (CameraManager) this.mView.getContext().getSystemService(AnalyticsConstants.PARAM_VALUE_PRE_SCAN_MODE_CAMERA);
        try {
            String[] camList = manager.getCameraIdList();
            if (camList.length == 0) {
                Log.e("Camera2Renderer", "Error: camera isn't detected.");
                return;
            }
            int i = 0;
            if (id != -1) {
                int length = camList.length;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    String cameraID = camList[i];
                    CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraID);
                    if (!((id == 99 && ((Integer) characteristics.get(CameraCharacteristics.LENS_FACING)).intValue() == 1) || (id == 98 && ((Integer) characteristics.get(CameraCharacteristics.LENS_FACING)).intValue() == 0))) {
                        i++;
                    }
                }
            } else {
                this.mCameraID = camList[0];
            }
            if (this.mCameraID == null) {
                return;
            }
            if (this.mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                Log.i("Camera2Renderer", "Opening camera: " + this.mCameraID);
                manager.openCamera(this.mCameraID, this.mStateCallback, this.mBackgroundHandler);
                return;
            }
            throw new RuntimeException("Time out waiting to lock camera opening.");
        } catch (CameraAccessException e) {
            Log.e("Camera2Renderer", "OpenCamera - Camera Access Exception");
        } catch (IllegalArgumentException e2) {
            Log.e("Camera2Renderer", "OpenCamera - Illegal Argument Exception");
        } catch (SecurityException e3) {
            Log.e("Camera2Renderer", "OpenCamera - Security Exception");
        } catch (InterruptedException e4) {
            Log.e("Camera2Renderer", "OpenCamera - Interrupted Exception");
        }
    }

    /* access modifiers changed from: protected */
    public void closeCamera() {
        Log.i("Camera2Renderer", "closeCamera");
        try {
            this.mCameraOpenCloseLock.acquire();
            if (this.mCaptureSession != null) {
                this.mCaptureSession.close();
                this.mCaptureSession = null;
            }
            if (this.mCameraDevice != null) {
                this.mCameraDevice.close();
                this.mCameraDevice = null;
            }
            this.mCameraOpenCloseLock.release();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } catch (Throwable th) {
            this.mCameraOpenCloseLock.release();
            throw th;
        }
    }


    public void createCameraPreviewSession() {
        int w = this.mPreviewSize.getWidth();
        int h = this.mPreviewSize.getHeight();
        Log.i("Camera2Renderer", "createCameraPreviewSession(" + w + "x" + h + Constant.STR_BRACKET_CLOSE);
        if (w >= 0 && h >= 0) {
            try {
                this.mCameraOpenCloseLock.acquire();
                if (this.mCameraDevice == null) {
                    this.mCameraOpenCloseLock.release();
                    Log.e("Camera2Renderer", "createCameraPreviewSession: camera isn't opened");
                } else if (this.mCaptureSession != null) {
                    this.mCameraOpenCloseLock.release();
                    Log.e("Camera2Renderer", "createCameraPreviewSession: mCaptureSession is already started");
                } else if (this.mSTexture == null) {
                    this.mCameraOpenCloseLock.release();
                    Log.e("Camera2Renderer", "createCameraPreviewSession: preview SurfaceTexture is null");
                } else {
                    this.mSTexture.setDefaultBufferSize(w, h);
                    Surface surface = new Surface(this.mSTexture);
                    this.mPreviewRequestBuilder = this.mCameraDevice.createCaptureRequest(1);
                    this.mPreviewRequestBuilder.addTarget(surface);
                    this.mCameraDevice.createCaptureSession(Arrays.asList(new Surface[]{surface}), new CameraCaptureSession.StateCallback() {
                        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                            CameraCaptureSession unused = Camera2Renderer.this.mCaptureSession = cameraCaptureSession;
                            try {
                                Camera2Renderer.this.mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, 4);
                                Camera2Renderer.this.mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, 2);
                                Camera2Renderer.this.mCaptureSession.setRepeatingRequest(Camera2Renderer.this.mPreviewRequestBuilder.build(), (CameraCaptureSession.CaptureCallback) null, Camera2Renderer.this.mBackgroundHandler);
                                Log.i("Camera2Renderer", "CameraPreviewSession has been started");
                            } catch (CameraAccessException e) {
                                Log.e("Camera2Renderer", "createCaptureSession failed");
                            }
                            Camera2Renderer.this.mCameraOpenCloseLock.release();
                        }

                        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                            Log.e("Camera2Renderer", "createCameraPreviewSession failed");
                            Camera2Renderer.this.mCameraOpenCloseLock.release();
                        }
                    }, this.mBackgroundHandler);
                }
            } catch (CameraAccessException e) {
                Log.e("Camera2Renderer", "createCameraPreviewSession");
            } catch (InterruptedException e2) {
                throw new RuntimeException("Interrupted while createCameraPreviewSession", e2);
            }
        }
    }

    private void startBackgroundThread() {
        Log.i("Camera2Renderer", "startBackgroundThread");
        stopBackgroundThread();
        this.mBackgroundThread = new HandlerThread("CameraBackground");
        this.mBackgroundThread.start();
        this.mBackgroundHandler = new Handler(this.mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        Log.i("Camera2Renderer", "stopBackgroundThread");
        HandlerThread handlerThread = this.mBackgroundThread;
        if (handlerThread != null) {
            handlerThread.quitSafely();
            try {
                this.mBackgroundThread.join();
                this.mBackgroundThread = null;
                this.mBackgroundHandler = null;
            } catch (InterruptedException e) {
                Log.e("Camera2Renderer", "stopBackgroundThread");
            }
        }
    }


    public void setCameraPreviewSize(int width, int height) {
        Log.i("Camera2Renderer", "setCameraPreviewSize(" + width + "x" + height + Constant.STR_BRACKET_CLOSE);
        if (this.mMaxCameraWidth > 0 && this.mMaxCameraWidth < width) {
            width = this.mMaxCameraWidth;
        }
        if (this.mMaxCameraHeight > 0 && this.mMaxCameraHeight < height) {
            height = this.mMaxCameraHeight;
        }
        try {
            this.mCameraOpenCloseLock.acquire();
            boolean needReconfig = cacPreviewSize(width, height);
            this.mCameraWidth = this.mPreviewSize.getWidth();
            this.mCameraHeight = this.mPreviewSize.getHeight();
            if (!needReconfig) {
                this.mCameraOpenCloseLock.release();
                return;
            }
            if (this.mCaptureSession != null) {
                Log.d("Camera2Renderer", "closing existing previewSession");
                this.mCaptureSession.close();
                this.mCaptureSession = null;
            }
            this.mCameraOpenCloseLock.release();
            createCameraPreviewSession();
        } catch (InterruptedException e) {
            this.mCameraOpenCloseLock.release();
            throw new RuntimeException("Interrupted while setCameraPreviewSize.", e);
        }
    }
}
