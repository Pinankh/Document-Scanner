package com.camscanner.paperscanner.pdfcreator.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tapscanner.polygondetect.DetectionResult;
import java.lang.reflect.Array;
import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.ScanApplication;
import com.camscanner.paperscanner.pdfcreator.common.Constant;
import com.camscanner.paperscanner.pdfcreator.model.Document;
import com.camscanner.paperscanner.pdfcreator.view.activity.main.MainActivity;
import timber.log.Timber;

public class CropImageEffectActivity extends BaseMainActivity {
    private Rect m_Rect;
    private Bitmap m_bmpResult;
    private Bitmap m_bmpSource;
    private Document m_curDoc;
    private DetectionResult m_detectionResult;
    private ImageView m_ivCropEffect;
    private Mat m_matSource;
    private ProgressBar m_pbState;
    private PointF[] m_ptCrop;
    private PointF[][] m_ptDst = ((PointF[][]) Array.newInstance(PointF.class, new int[]{10, 4}));
    private int m_rectHeight;
    private int m_rectWidth;
    private float m_scale;
    private int m_viewHeight;
    private int m_viewWidth;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_crop_effect);
        if (savedInstanceState != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(268468224);
            startActivity(intent);
            finish();
            return;
        }
        initVariable();
        initUI();
    }

    /* access modifiers changed from: package-private */
    public void initVariable() {
        this.m_curDoc = (Document) getIntent().getSerializableExtra(Constant.EXTRA_DOCUMENT);
        this.m_rectWidth = getIntent().getIntExtra(Constant.EXTRA_RECT_WIDTH, 0);
        this.m_rectHeight = getIntent().getIntExtra(Constant.EXTRA_RECT_HEIGHT, 0);
        this.m_viewWidth = getIntent().getIntExtra(Constant.EXTRA_VIEW_WIDTH, 0);
        this.m_viewHeight = getIntent().getIntExtra(Constant.EXTRA_VIEW_HEIGHT, 0);
        String strCorpPoints = getIntent().getStringExtra(Constant.EXTRA_CROP_POINTS);
        this.m_bmpSource = ScanApplication.imageHolder().getCroppedPicture();
        if (this.m_bmpSource != null) {
            this.m_detectionResult = new DetectionResult();
            this.m_ptCrop = (PointF[]) new Gson().fromJson(strCorpPoints, new TypeToken<PointF[]>() {
            }.getType());
            this.m_Rect = new Rect(0, 0, this.m_rectWidth, this.m_rectHeight);
            this.m_detectionResult.saveDetectInfo(this.m_ptCrop, this.m_Rect);
            this.m_scale = Math.min(((float) this.m_viewWidth) / ((float) this.m_bmpSource.getWidth()), ((float) this.m_viewHeight) / ((float) this.m_bmpSource.getHeight()));
            for (int i = 0; i < 10; i++) {
                this.m_ptDst[i] = new PointF[4];
                for (int j = 0; j < 4; j++) {
                    this.m_ptDst[i][j] = new PointF();
                    PointF[][] pointFArr = this.m_ptDst;
                    pointFArr[i][j].x = 0.0f;
                    pointFArr[i][j].y = 0.0f;
                }
            }
            return;
        }
        Toast.makeText(this, getString(R.string.alert_fail_read_image), 0).show();
        finish();
    }

    /* access modifiers changed from: package-private */
    public void initUI() {
        this.m_ivCropEffect = (ImageView) findViewById(R.id.iv_cropEffect);
        this.m_pbState = (ProgressBar) findViewById(R.id.pb_cropState);
        try {
            if (this.m_bmpSource != null) {
                this.m_matSource = new Mat(this.m_bmpSource.getHeight(), this.m_bmpSource.getWidth(), CvType.CV_8UC3);
                Mat matSrc_tmp = new Mat(this.m_bmpSource.getHeight(), this.m_bmpSource.getWidth(), CvType.CV_8UC4);
                Utils.bitmapToMat(this.m_bmpSource, matSrc_tmp, false);
                Imgproc.cvtColor(matSrc_tmp, this.m_matSource, 3);
                matSrc_tmp.release();
                this.m_bmpSource.recycle();
                Mat matSrc = this.m_matSource.clone();
                Mat mat = new Mat(matSrc.rows(), matSrc.cols(), CvType.CV_8UC3);
                ScanApplication.getDetectionEngine().cropPerspective(matSrc.getNativeObjAddr(), this.m_detectionResult);
                Imgproc.cvtColor(matSrc, mat, 2);
                this.m_bmpResult = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(mat, this.m_bmpResult);
                matSrc.release();
                mat.release();
                startFrameThread();
                return;
            }
            Toast.makeText(this, getString(R.string.alert_sorry), 1).show();
            finish();
        } catch (OutOfMemoryError | CvException ex) {
            runOnUiThread(new Runnable() {


                public final void run() {
                    CropImageEffectActivity.this.lambda$initUI$0$CropEffectActivity(ex);
                }
            });
        }
    }

    public /* synthetic */ void lambda$initUI$0$CropEffectActivity(Throwable ex) {
        Timber.e(ex);
        System.gc();
        Toast.makeText(this, getString(R.string.alert_sorry), 1).show();
        finish();
    }

    /* access modifiers changed from: package-private */
    public void startFrameThread() {
        new Thread(new Runnable() {
            public final void run() {
                CropImageEffectActivity.this.lambda$startFrameThread$4$CropEffectActivity();
            }
        }).start();
    }

    public /* synthetic */ void lambda$startFrameThread$4$CropEffectActivity() {
        try {
            Mat matSrc = this.m_matSource;
            ScanApplication.getDetectionEngine().getPerspectiveArray(matSrc.getNativeObjAddr(), this.m_detectionResult, this.m_ptDst);
            Mat resizeMat = new Mat();
            Size sz = new Size((double) (((float) matSrc.cols()) * this.m_scale), (double) (((float) matSrc.rows()) * this.m_scale));
            if ((sz.width <= 0.0d || sz.height <= 0.0d) && sz.area() <= 0.0d) {
                matSrc.release();
                runOnUiThread(new Runnable() {
                    public final void run() {
                        CropImageEffectActivity.this.lambda$null$1$CropEffectActivity();
                    }
                });
                return;
            }
            Imgproc.resize(matSrc, resizeMat, sz);
            for (int i = 0; i < 10; i++) {
                Mat matWork = resizeMat.clone();
                ScanApplication.getDetectionEngine().actCropPerspective(matWork.getNativeObjAddr(), this.m_detectionResult, this.m_ptDst[i], i, this.m_viewWidth, this.m_viewHeight);
                Mat mat = new Mat(matWork.rows(), matWork.cols(), CvType.CV_8UC3);
                Imgproc.cvtColor(matWork, mat, 4);
                final Bitmap bmpFinal = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(mat, bmpFinal);
                matWork.release();
                mat.release();
                final int finalI = i;
                runOnUiThread(new Runnable() {


                    public final void run() {
                        CropImageEffectActivity.this.lambda$null$2$CropEffectActivity(bmpFinal, finalI);
                    }
                });
                if (i == 9) {
                    startOCRActivity(this.m_bmpResult);
                }
            }
            resizeMat.release();
            this.m_matSource.release();
        } catch (OutOfMemoryError ex) {
            runOnUiThread(new Runnable() {
                public final void run() {
                    CropImageEffectActivity.this.lambda$null$3$CropEffectActivity();
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$1$CropEffectActivity() {
        Toast.makeText(this, getString(R.string.alert_select_region), 1).show();
        finish();
    }

    public /* synthetic */ void lambda$null$2$CropEffectActivity(Bitmap bmpFinal, int finalI) {
        this.m_ivCropEffect.setImageBitmap(bmpFinal);
        this.m_pbState.setProgress(finalI + 1);
    }

    public /* synthetic */ void lambda$null$3$CropEffectActivity() {
        Toast.makeText(this, getString(R.string.alert_sorry), 1).show();
        finish();
    }

    /* access modifiers changed from: package-private */
    public void startOCRActivity(Bitmap bmpCrop) {
        ScanApplication.imageHolder().setCroppedPicture(bmpCrop, true, true);
        runOnUiThread(new Runnable() {
            public final void run() {
                CropImageEffectActivity.this.lambda$startOCRActivity$5$CropEffectActivity();
            }
        });
    }

    public /* synthetic */ void lambda$startOCRActivity$5$CropEffectActivity() {
        if (ScanApplication.imageHolder().hasCroppedAndOptimized()) {
            Intent intent = new Intent(this, EditFiltersActivity.class);
            intent.putExtra(Constant.EXTRA_DOCUMENT, this.m_curDoc);
            intent.putExtra(Constant.EXTRA_SORTID, getIntent().getIntExtra(Constant.EXTRA_SORTID, -1));
            startActivityForResult(intent, 1001);
            return;
        }
        Toast.makeText(this, getString(R.string.alert_sorry), 1).show();
        finish();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001) {
            if (resultCode == -1) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(Constant.EXTRA_MPARENT, data.getStringExtra(Constant.EXTRA_MPARENT));
                resultIntent.putExtra(Constant.EXTRA_MNAME, data.getStringExtra(Constant.EXTRA_MNAME));
                setResult(-1, resultIntent);
            } else if (resultCode == 0) {
                setResult(0, new Intent());
            }
            finish();
        }
    }
}
