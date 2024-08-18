package com.camscanner.paperscanner.pdfcreator.view.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.tom_roush.pdfbox.pdmodel.documentinterchange.taggedpdf.PDPrintFieldAttributeObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.ScanApplication;
import com.camscanner.paperscanner.pdfcreator.common.Animations;
import com.camscanner.paperscanner.pdfcreator.common.utils.BitmapUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.ImageStorageUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.PolygonUtil;
import com.camscanner.paperscanner.pdfcreator.common.utils.SharedPrefsUtils;
import com.camscanner.paperscanner.pdfcreator.model.Document;
import com.camscanner.paperscanner.pdfcreator.model.types.CameraScanMode;
import com.camscanner.paperscanner.pdfcreator.view.fragment.BackButtonListener;
import tap.lib.rateus.dialog.Circle;

public class CameraFragment extends Fragment implements SurfaceHolder.Callback, BackButtonListener {
    private static final int PICTURE_SIZE_MAX_WIDTH = 1280;
    private static final int PREVIEW_SIZE_MAX_WIDTH = 640;
    public static final String TAG = CameraFragment.class.getSimpleName();
    /* access modifiers changed from: private */
    public static CameraScanMode mCaptureMode;
    private TextView alertMode;
    private int batchNum = 0;
    private View btnBatch;
    private View btnBatchDone;
    private View btnFlash;
    private View btnGrid;
    private View btnSingle;
    /* access modifiers changed from: private */
    public View btnTakePhoto;
    private GridView gridView;
    private ImageView imageBatch;
    private ImageView imageFlash;
    private ImageView imageGrid;
    private ImageView imagePreview;
    private ImageView imageSingle;
    private Camera mCamera = null;
    /* access modifiers changed from: private */
    public Context mContext;
    private String mFlashMode;
    /* access modifiers changed from: private */
    public ImageParameters mImageParameters;
    private CameraOrientationListener mOrientationListener;
    /* access modifiers changed from: private */
    public SquareCameraPreview mPreviewView;
    private SurfaceHolder mSurfaceHolder;
    private List<Document> m_arrData = new ArrayList();
    private boolean m_bGrid;
    private boolean m_bSafeToTakePhoto = false;
    private boolean m_bSurfaceDestroyed;
    private int m_nCameraID;
    /* access modifiers changed from: private */
    public DrawingView m_vwDrawing;
    Camera.PictureCallback postView = new Camera.PictureCallback() {
        public void onPictureTaken(final byte[] data, Camera camera) {
            final int rotation = CameraFragment.this.getPhotoRotation();
            CameraFragment.this.showProgressDialog();
            new Thread(new Runnable() {


                public final void run() {
                    lambda$onPictureTaken$2$CameraFragment$2(data, rotation);
                }
            }).start();
        }

        public /* synthetic */ void lambda$onPictureTaken$2$CameraFragment$2(byte[] data, int rotation) {
            try {
                ((Activity) CameraFragment.this.mContext).runOnUiThread(new Runnable() {
                    public final void run() {
                        lambda$null$0$CameraFragment$2();
                    }
                });
                Bitmap mBmpCapture = BitmapUtils.resizeAndRotateScan(CameraFragment.this.mContext, data, rotation);
                if (CameraFragment.mCaptureMode.equals(CameraScanMode.SINGLE)) {
                    ScanApplication.imageHolder().setOriginalPicture(mBmpCapture, ImageStorageUtils.saveTakenPicture(mBmpCapture, CameraFragment.this.mContext));
//                    Analytics.get().logScanDocument(AnalyticsConstants.PARAM_VALUE_SCAN_MODE_SINGLE);
                    CameraFragment.this.hideProgressDialog1();
                    ((CameraOpenActivity) CameraFragment.this.mContext).returnPhotoUri(CameraScanMode.SINGLE, (List<Document>) null);
                    return;
                }
                CameraFragment.this.onPictureFromBatchSaved(ImageStorageUtils.saveTakenPicture(mBmpCapture, CameraFragment.this.mContext));
                mBmpCapture.recycle();
                ((Activity) CameraFragment.this.mContext).runOnUiThread(new Runnable() {
                    public final void run() {
                        lambda$null$1$CameraFragment$2();
                    }
                });
            } catch (OutOfMemoryError ex) {
                System.gc();
                ((CameraOpenActivity) CameraFragment.this.mContext).errorActivity(ex);
            } catch (IllegalArgumentException | NullPointerException ex2) {
                ((CameraOpenActivity) CameraFragment.this.mContext).errorActivity(ex2);
            }
        }

        public /* synthetic */ void lambda$null$0$CameraFragment$2() {
            CameraFragment.this.m_vwDrawing.setHaveTouch(false, new Rect(0, 0, 0, 0), false);
            CameraFragment.this.m_vwDrawing.invalidate();
        }

        public /* synthetic */ void lambda$null$1$CameraFragment$2() {
            CameraFragment.this.btnTakePhoto.setClickable(true);
        }
    };
    private TextView textPreview;

    public static Fragment newInstance() {
        return new CameraFragment();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        this.mOrientationListener = new CameraOrientationListener(context);
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.m_nCameraID = getBackCameraID();
        this.mFlashMode = SharedPrefsUtils.getCameraFlashMode(this.mContext);
        this.mImageParameters = new ImageParameters();
        mCaptureMode = CameraScanMode.SINGLE;
        this.m_bSurfaceDestroyed = true;
        this.batchNum = 0;
        this.m_bGrid = false;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mOrientationListener.enable();
        this.mPreviewView = (SquareCameraPreview) view.findViewById(R.id.camera_preview_view);
        this.imagePreview = (ImageView) view.findViewById(R.id.batch_mode_preview_image);
        this.textPreview = (TextView) view.findViewById(R.id.batch_mode_preview_num);
        this.btnBatchDone = view.findViewById(R.id.btn_finish);
        this.imageGrid = (ImageView) view.findViewById(R.id.img_grid);
        this.imageFlash = (ImageView) view.findViewById(R.id.img_flash);
        this.imageBatch = (ImageView) view.findViewById(R.id.img_batch);
        this.imageSingle = (ImageView) view.findViewById(R.id.img_single);
        this.btnGrid = view.findViewById(R.id.btn_grid);
        this.btnFlash = view.findViewById(R.id.btn_flash);
        this.btnBatch = view.findViewById(R.id.btn_batch);
        this.btnSingle = view.findViewById(R.id.btn_single);
        this.btnTakePhoto = view.findViewById(R.id.btn_take_photo);
        this.m_vwDrawing = (DrawingView) view.findViewById(R.id.drawing_surface);
        this.mPreviewView.setDrawingView(this.m_vwDrawing);
        this.gridView = (GridView) view.findViewById(R.id.vw_grid);
        this.alertMode = (TextView) view.findViewById(R.id.tv_alert_mode);
        updateCaptureMode();
        this.mPreviewView.getHolder().addCallback(this);
        final View topCoverView = view.findViewById(R.id.cover_top_view);
        final View btnCoverView = view.findViewById(R.id.cover_bottom_view);
        ImageParameters imageParameters = this.mImageParameters;
        boolean z = true;
        if (getResources().getConfiguration().orientation != 1) {
            z = false;
        }
        imageParameters.mIsPortrait = z;
        if (savedInstanceState == null) {
            this.mPreviewView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    CameraFragment.this.mImageParameters.mPreviewWidth = CameraFragment.this.mPreviewView.getWidth();
                    CameraFragment.this.mImageParameters.mPreviewHeight = CameraFragment.this.mPreviewView.getHeight();
                    ImageParameters access$000 = CameraFragment.this.mImageParameters;
                    ImageParameters access$0002 = CameraFragment.this.mImageParameters;
                    int calculateCoverWidthHeight = CameraFragment.this.mImageParameters.calculateCoverWidthHeight();
                    access$0002.mCoverHeight = calculateCoverWidthHeight;
                    access$000.mCoverWidth = calculateCoverWidthHeight;
                    CameraFragment.this.resizeTopAndBtmCover(topCoverView, btnCoverView);
                    new Handler().postDelayed(new Runnable() {
                        public final void run() {
                            lambda$onGlobalLayout$0$CameraFragment$1();
                        }
                    }, 400);
                    CameraFragment.this.mPreviewView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                public /* synthetic */ void lambda$onGlobalLayout$0$CameraFragment$1() {
                    if (CameraFragment.this.isVisible()) {
                        CameraFragment.this.showCaptureAlert(CameraFragment.mCaptureMode, 1200);
                    }
                }
            });
        } else if (this.mImageParameters.isPortrait()) {
            topCoverView.getLayoutParams().height = this.mImageParameters.mCoverHeight;
            btnCoverView.getLayoutParams().height = this.mImageParameters.mCoverHeight;
        } else {
            topCoverView.getLayoutParams().width = this.mImageParameters.mCoverWidth;
            btnCoverView.getLayoutParams().width = this.mImageParameters.mCoverWidth;
        }
        this.btnFlash.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                CameraFragment.this.lambda$onViewCreated$0$CameraFragment(view);
            }
        });
        this.btnGrid.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                CameraFragment.this.lambda$onViewCreated$1$CameraFragment(view);
            }
        });
        this.btnBatchDone.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                CameraFragment.this.lambda$onViewCreated$2$CameraFragment(view);
            }
        });
        updateFlashIcon();
        this.btnBatch.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                CameraFragment.this.lambda$onViewCreated$3$CameraFragment(view);
            }
        });
        this.btnSingle.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                CameraFragment.this.lambda$onViewCreated$4$CameraFragment(view);
            }
        });
        this.btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                CameraFragment.this.lambda$onViewCreated$5$CameraFragment(view);
            }
        });
    }

    public /* synthetic */ void lambda$onViewCreated$0$CameraFragment(View v) {
        changeFlashMode();
        updateFlashIcon();
        if (this.mCamera != null) {
            setupCamera();
        } else {
            Toast.makeText(this.mContext, "Can't start camera preview", 0).show();
        }
    }

    public /* synthetic */ void lambda$onViewCreated$1$CameraFragment(View v) {
        swipGridMode();
    }

    public /* synthetic */ void lambda$onViewCreated$2$CameraFragment(View v) {
        if (this.mCamera != null) {
            stopCameraPreview();
            this.mCamera.release();
            this.mCamera = null;
        }
        ((CameraOpenActivity) this.mContext).returnPhotoUri(CameraScanMode.BATCH, this.m_arrData);
    }

    public /* synthetic */ void lambda$onViewCreated$3$CameraFragment(View btn) {
        setCaptureMode(CameraScanMode.BATCH);
    }

    public  void lambda$onViewCreated$4$CameraFragment(View btn) {
        setCaptureMode(CameraScanMode.SINGLE);
    }

    public  void lambda$onViewCreated$5$CameraFragment(View btn) {
        takePicture();
    }


    private void changeFlashMode() {
        char c;
        String str = this.mFlashMode;
        int hashCode = str.hashCode();
        if (hashCode != 3551) {
            if (hashCode != 109934) {
                if (str.equals("off")) {
                    c = 3;
                    if (c != 0) {
                        this.mFlashMode = PDPrintFieldAttributeObject.CHECKED_STATE_OFF;
                        return;
                    } else if (c != 1) {
                        this.mFlashMode = PDPrintFieldAttributeObject.CHECKED_STATE_ON;
                        return;
                    } else {
                        this.mFlashMode = "auto";
                        return;
                    }
                }
            } else if (str.equals(PDPrintFieldAttributeObject.CHECKED_STATE_OFF)) {
                c = 1;

            }
        } else if (str.equals(PDPrintFieldAttributeObject.CHECKED_STATE_ON)) {
            c = 0;

        }
        c = 65535;

    }


    private void updateFlashIcon() {
        char c;
        String str = this.mFlashMode;
        int hashCode = str.hashCode();
        if (hashCode != 3551) {
            if (hashCode != 109934) {
                if (str.equals("off")) {
                    c = 3;
                    if (c != 0) {
                        this.imageFlash.setImageResource(R.drawable.ic_flash_on);
                        return;
                    } else if (c != 1) {
                        this.imageFlash.setImageResource(R.drawable.ic_flash_auto);
                        return;
                    } else {
                        this.imageFlash.setImageResource(R.drawable.ic_flash_off);
                        return;
                    }
                }
            } else if (str.equals(PDPrintFieldAttributeObject.CHECKED_STATE_OFF)) {
                c = 1;

            }
        } else if (str.equals(PDPrintFieldAttributeObject.CHECKED_STATE_ON)) {
            c = 0;

        }
        c = 65535;

    }

    private void setCaptureMode(CameraScanMode newMode) {
        if (!mCaptureMode.equals(newMode)) {
            showCaptureAlert(newMode, 500);
            mCaptureMode = newMode;
            updateCaptureMode();
        }
    }

    private void updateCaptureMode() {
        if (mCaptureMode.equals(CameraScanMode.SINGLE)) {
            this.imageBatch.setImageResource(R.drawable.ic_batch_off);
            this.imageSingle.setImageResource(R.drawable.ic_single_on);
            return;
        }
        this.imageBatch.setImageResource(R.drawable.ic_batch_on);
        this.imageSingle.setImageResource(R.drawable.ic_single_off);
        int i = this.batchNum;
        if (i > 0) {
            this.textPreview.setText(String.valueOf(i));
            updateVisibility(0, this.imagePreview, this.textPreview, this.btnBatchDone);
            updateVisibility(4, this.btnFlash, this.btnSingle);
        } else {
            updateVisibility(4, this.imagePreview, this.textPreview, this.btnBatchDone);
            updateVisibility(0, this.btnFlash, this.btnSingle);
        }
        if (this.m_arrData.size() > 0) {
            List<Document> list = this.m_arrData;
            this.imagePreview.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeFile(list.get(list.size() - 1).thumb), 100, 100, false));
        }
    }

    /* access modifiers changed from: private */
    public void showCaptureAlert(CameraScanMode newMode, int duration) {
        this.alertMode.setText(getString(newMode.equals(CameraScanMode.SINGLE) ? R.string.str_single_mode : R.string.str_batch_mode));
        Animations.fadeOut(this.alertMode, duration);
    }

    /* access modifiers changed from: private */
    public void resizeTopAndBtmCover(View topCover, View bottomCover) {
        ResizeAnimation resizeTopAnimation = new ResizeAnimation(topCover, this.mImageParameters);
        resizeTopAnimation.setDuration(800);
        resizeTopAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        topCover.startAnimation(resizeTopAnimation);
        ResizeAnimation resizeBtmAnimation = new ResizeAnimation(bottomCover, this.mImageParameters);
        resizeBtmAnimation.setDuration(800);
        resizeBtmAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        bottomCover.startAnimation(resizeBtmAnimation);
    }

    private void getCamera(int cameraID) {
        try {
            this.mCamera = Camera.open(cameraID);
            this.mPreviewView.setCamera(this.mCamera);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void restartPreview() {
        if (this.mCamera != null) {
            stopCameraPreview();
            this.mCamera.release();
            this.mCamera = null;
        }
        getCamera(this.m_nCameraID);
        if (!this.m_bSurfaceDestroyed) {
            startCameraPreview();
        }
    }

    private void startCameraPreview() {
        if (this.mCamera != null) {
            determineDisplayOrientation();
            setupCamera();
            try {
                this.mCamera.setPreviewDisplay(this.mSurfaceHolder);
                this.mCamera.startPreview();
                setSafeToTakePhoto(true);
                setCameraFocusReady(true);
            } catch (RuntimeException ex) {
                ex.printStackTrace();
                Toast.makeText(this.mContext, getString(R.string.alert_sorry), 1).show();
                ((CameraOpenActivity) this.mContext).finish();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this.mContext, getString(R.string.alert_sorry), 1).show();
                ((CameraOpenActivity) this.mContext).finish();
            }
        } else {
            Toast.makeText(this.mContext, getString(R.string.alert_sorry), Toast.LENGTH_SHORT).show();
        }
    }

    private void stopCameraPreview() {
        setSafeToTakePhoto(false);
        setCameraFocusReady(false);
        this.mCamera.stopPreview();
        this.mPreviewView.setCamera((Camera) null);
    }

    private void setSafeToTakePhoto(boolean isSafeToTakePhoto) {
        this.m_bSafeToTakePhoto = isSafeToTakePhoto;
    }

    private void setCameraFocusReady(boolean isFocusReady) {
        SquareCameraPreview squareCameraPreview = this.mPreviewView;
        if (squareCameraPreview != null) {
            squareCameraPreview.setIsFocusReady(isFocusReady);
        }
    }

    private void determineDisplayOrientation() {
        int displayOrientation;
        try {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(this.m_nCameraID, cameraInfo);
            int rotation = ((CameraOpenActivity) this.mContext).getWindowManager().getDefaultDisplay().getRotation();
            int degrees = 0;
            if (rotation == 0) {
                degrees = 0;
            } else if (rotation == 1) {
                degrees = 90;
            } else if (rotation == 2) {
                degrees = 180;
            } else if (rotation == 3) {
                degrees = 270;
            }
            if (cameraInfo.facing == 1) {
                displayOrientation = (360 - ((cameraInfo.orientation + degrees) % Circle.FULL)) % Circle.FULL;
            } else {
                displayOrientation = ((cameraInfo.orientation - degrees) + Circle.FULL) % Circle.FULL;
            }
            this.mImageParameters.mDisplayOrientation = displayOrientation;
            this.mImageParameters.mLayoutOrientation = degrees;
            this.mCamera.setDisplayOrientation(this.mImageParameters.mDisplayOrientation);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }
    }

    private void setupCamera() {
        try {
            Camera.Parameters parameters = this.mCamera.getParameters();
            Camera.Size bestPreviewSize = determineBestPreviewSize(parameters);
            Camera.Size bestPictureSize = determineBestPictureSize(parameters);
            parameters.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
            parameters.setPictureSize(bestPictureSize.width, bestPictureSize.height);
            if (parameters.getSupportedFocusModes().contains("continuous-picture")) {
                parameters.setFocusMode("continuous-picture");
            }
            View changeCameraFlashModeBtn = getView().findViewById(R.id.btn_flash);
            List<String> flashModes = parameters.getSupportedFlashModes();
            if (flashModes == null || !flashModes.contains(this.mFlashMode)) {
                changeCameraFlashModeBtn.setVisibility(4);
            } else {
                parameters.setFlashMode(this.mFlashMode);
                changeCameraFlashModeBtn.setVisibility(0);
            }
            this.mCamera.setParameters(parameters);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }
    }

    private Camera.Size determineBestPreviewSize(Camera.Parameters parameters) {
        return determineBestSize(parameters.getSupportedPreviewSizes(), PREVIEW_SIZE_MAX_WIDTH);
    }

    private Camera.Size determineBestPictureSize(Camera.Parameters parameters) {
        return determineBestSize(parameters.getSupportedPictureSizes(), PICTURE_SIZE_MAX_WIDTH);
    }

    private Camera.Size determineBestSize(List<Camera.Size> sizes, int widthThreshold) {
        Camera.Size bestSize = null;
        int numOfSizes = sizes.size();
        int i = 0;
        while (true) {
            boolean isBetterSize = true;
            if (i >= numOfSizes) {
                break;
            }
            Camera.Size size = sizes.get(i);
            boolean isDesireRatio = size.width / 4 == size.height / 3;
            if (bestSize != null && size.width <= bestSize.width) {
                isBetterSize = false;
            }
            if (isDesireRatio && isBetterSize) {
                bestSize = size;
            }
            i++;
        }
        if (bestSize == null) {
            return sizes.get(sizes.size() - 1);
        }
        return bestSize;
    }

    private int getBackCameraID() {
        return 0;
    }

    private void takePicture() {
        this.btnTakePhoto.setClickable(false);
        Camera camera = this.mCamera;
        if (camera == null) {
            Toast.makeText(this.mContext, getString(R.string.alert_sorry), 1).show();
            return;
        }
        boolean bAutoFocus = false;
        try {
            bAutoFocus = camera.getParameters().getSupportedFocusModes().contains("auto");
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }
        if (bAutoFocus && !this.m_vwDrawing.isHaveTouch()) {
            float x = (float) (this.mPreviewView.getViewWidth() / 2);
            float y = (float) (this.mPreviewView.getViewHeight() / 2);
            final Rect touchRect = new Rect((int) (x - 70.0f), (int) (y - 70.0f), (int) (x + 70.0f), (int) (70.0f + y));
            this.m_vwDrawing.setHaveTouch(true, touchRect, false);
            this.m_vwDrawing.invalidate();
            try {
                this.mPreviewView.mCamera.autoFocus(new Camera.AutoFocusCallback() {


                    public final void onAutoFocus(boolean z, Camera camera) {
                        CameraFragment.this.lambda$takePicture$6$CameraFragment(touchRect, null, null, z, camera);
                    }
                });
            } catch (RuntimeException ex2) {
                ex2.printStackTrace();
            }
        } else if (this.m_bSafeToTakePhoto) {
            setSafeToTakePhoto(false);
            this.mOrientationListener.rememberOrientation();
            try {
                this.mCamera.takePicture((Camera.ShutterCallback) null, (Camera.PictureCallback) null, this.postView);
            } catch (RuntimeException ex3) {
                Toast.makeText(this.mContext, getString(R.string.alert_take_picture_failed), 0).show();
            }
        }
    }

    public /* synthetic */ void lambda$takePicture$6$CameraFragment(Rect touchRect, Camera.ShutterCallback shutterCallback, Camera.PictureCallback raw, boolean success, Camera camera) {
        if (success) {
            this.m_vwDrawing.setHaveTouch(true, touchRect, true);
            this.m_vwDrawing.invalidate();
        }
        if (this.m_bSafeToTakePhoto) {
            setSafeToTakePhoto(false);
            this.mOrientationListener.rememberOrientation();
            try {
                this.mCamera.takePicture(shutterCallback, raw, this.postView);
            } catch (RuntimeException ex) {
                Toast.makeText(this.mContext, getString(R.string.alert_take_picture_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onResume() {
        super.onResume();
        if (this.mCamera == null) {
            restartPreview();
        }
    }

    public void onStop() {
        this.mOrientationListener.disable();
        if (this.mCamera != null) {
            stopCameraPreview();
            this.mCamera.release();
            this.mCamera = null;
        }
        SharedPrefsUtils.setCameraFlashMode(this.mContext, this.mFlashMode);
        super.onStop();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        this.mSurfaceHolder = holder;
        if (this.mCamera == null) {
            getCamera(this.m_nCameraID);
        }
        if (this.m_bSurfaceDestroyed) {
            startCameraPreview();
        }
        this.m_bSurfaceDestroyed = false;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        this.m_bSurfaceDestroyed = true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
            if (requestCode != 1) {
                super.onActivityResult(requestCode, resultCode, data);
            } else {
                Uri data2 = data.getData();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void onPictureFromBatchSaved(final String photoPath) {
        ((CameraOpenActivity) this.mContext).runOnUiThread(new Runnable() {


            public final void run() {
                CameraFragment.this.lambda$onPictureFromBatchSaved$7$CameraFragment(photoPath);
            }
        });
    }

    public /* synthetic */ void lambda$onPictureFromBatchSaved$7$CameraFragment(String photoPath) {
//        Analytics.get().logScanDocument(AnalyticsConstants.PARAM_VALUE_SCAN_MODE_BATCH);
        PolygonUtil.saveImageWithAutoCrop(this.mContext, photoPath, this.m_arrData);
        hideProgressDialog1();
        restartPreview();
        this.batchNum++;
        updateCaptureMode();
    }

    private void updateVisibility(int visibility, View... views) {
        for (View view : views) {
            view.setVisibility(visibility);
        }
    }

    /* access modifiers changed from: private */
    public int getPhotoRotation() {
        int orientation = this.mOrientationListener.getRememberedNormalOrientation();
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(this.m_nCameraID, info);
        if (info.facing == 1) {
            return ((info.orientation - orientation) + Circle.FULL) % Circle.FULL;
        }
        return (info.orientation + orientation) % Circle.FULL;
    }

    public void onBackPressed() {
        for (Document doc : this.m_arrData) {
            ImageStorageUtils.deleteImages(getActivity(), doc.getPaths(), false);
        }
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private static class CameraOrientationListener extends OrientationEventListener {
        private int mCurrentNormalizedOrientation;
        private int mRememberedNormalOrientation;

        public CameraOrientationListener(Context context) {
            super(context, 3);
        }

        public void onOrientationChanged(int orientation) {
            if (orientation != -1) {
                this.mCurrentNormalizedOrientation = normalize(orientation);
            }
        }

        private int normalize(int degrees) {
            if (degrees > 315 || degrees <= 45) {
                return 0;
            }
            if (degrees > 45 && degrees <= 135) {
                return 90;
            }
            if (degrees > 135 && degrees <= 225) {
                return 180;
            }
            if (degrees > 225 && degrees <= 315) {
                return 270;
            }
            throw new RuntimeException("The physics as we know them are no more. Watch out for anomalies.");
        }

        public void rememberOrientation() {
            this.mRememberedNormalOrientation = this.mCurrentNormalizedOrientation;
        }

        public int getRememberedNormalOrientation() {
            rememberOrientation();
            return this.mRememberedNormalOrientation;
        }
    }

    /* access modifiers changed from: package-private */
    public void showProgressDialog() {
        final CameraOpenActivity activity = (CameraOpenActivity) this.mContext;
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {


                public final void run() {
                    CameraFragment.this.lambda$showProgressDialog$8$CameraFragment(activity);
                }
            });
        }
    }

    public /* synthetic */ void lambda$showProgressDialog$8$CameraFragment(CameraOpenActivity activity) {
        if (CameraOpenActivity.m_bActive) {
            activity.showProgressDialog(getString(R.string.loading_and_process_image));
        }
    }



    public void hideProgressDialog1() {
        final CameraOpenActivity activity = (CameraOpenActivity) this.mContext;
        if (activity != null) {
            activity.getClass();
            activity.runOnUiThread(new Runnable() {
                public final void run() {
                    activity.hideProgressDialog();
                }
            });
        }
    }



    /* access modifiers changed from: package-private */
    public void swipGridMode() {
        this.m_bGrid = !this.m_bGrid;
        if (this.m_bGrid) {
            this.imageGrid.setImageResource(R.drawable.ic_grid_on);
        } else {
            this.imageGrid.setImageResource(R.drawable.ic_grid_off);
        }
        this.gridView.showGrid(this.m_bGrid);
    }
}
