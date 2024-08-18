package com.camscanner.paperscanner.pdfcreator.view.crop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.camscanner.paperscanner.pdfcreator.ads.InterstialListner;
import com.camscanner.paperscanner.pdfcreator.ads.InterstitialHelper;
import com.google.gson.Gson;
import com.tapscanner.polygondetect.DetectionResult;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.ScanApplication;
import com.camscanner.paperscanner.pdfcreator.common.Constant;
import com.camscanner.paperscanner.pdfcreator.common.p018db.DBManager;
import com.camscanner.paperscanner.pdfcreator.common.utils.ActivityUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.ImageStorageUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.SharedPrefsUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.TagUtils;
import com.camscanner.paperscanner.pdfcreator.common.views.simplecropview.SimpleCropImageView;
import com.camscanner.paperscanner.pdfcreator.features.tutorial.TutorialManager;
import com.camscanner.paperscanner.pdfcreator.features.tutorial.model.TutorialInfo;
import com.camscanner.paperscanner.pdfcreator.features.tutorial.model.TutorialViewInfo;
import com.camscanner.paperscanner.pdfcreator.model.Document;
import com.camscanner.paperscanner.pdfcreator.view.activity.BaseMainActivity;
import com.camscanner.paperscanner.pdfcreator.view.activity.CropImageEffectActivity;
import timber.log.Timber;

public class CropImageActivity extends BaseMainActivity implements View.OnClickListener, SimpleCropImageView.SimpleCropImageViewCallback, TutorialManager.OnTutorialListener {
    private static final String LOG_TAG = CropImageActivity.class.getSimpleName();
    private static int m_nAngle;
    private int cropOpened = 0;
    private float distance;
    private float dotSize;
    private float halfSide;
    public ImageView ivMagLeft;
    private DetectionResult.FIX_RECT_MODE mFixRectMode;
    private boolean m_bFull;
    private boolean m_bMoved;
    private Bitmap m_bmpSource;
    private SimpleCropImageView m_civCrop;
    private Document m_curDoc;
    private PointF[] m_curPoints;
    private PointF[] m_defaultPoints;
    private DetectionResult m_detectionResult;
    private ImageView m_ivCrop;
//    private View m_rlBack;
    private View m_rlCrop;
    private View m_rlDone;
    private View m_rlLeft;
    private View m_rlRight;
    private View m_rlTopBack;
    private boolean moveSaved = false;
    public boolean saving;

    public static void replaceWithNewFile(FragmentActivity activity, DetectionResult.FIX_RECT_MODE fixRectMode, @Nullable String parent, int sortid, boolean notFirstInDoc) {
        Intent intent = getStartIntentForNewFile(activity, fixRectMode, parent, notFirstInDoc);
        intent.putExtra(Constant.EXTRA_REPLACE, true);
        intent.putExtra(Constant.EXTRA_SORTID, sortid);
        activity.startActivityForResult(intent, 1002);
    }

    public static void cropNewFile(FragmentActivity activity, DetectionResult.FIX_RECT_MODE fixRectMode, @Nullable String parent, boolean notFirstInDoc) {
        activity.startActivityForResult(getStartIntentForNewFile(activity, fixRectMode, parent, notFirstInDoc), 1002);
    }

    public static void recropOldFile(FragmentActivity activity, DetectionResult.FIX_RECT_MODE fixRectMode, Document document) {
        Intent intent = new Intent(activity, CropImageActivity.class);
        intent.putExtra(Constant.EXTRA_DOCUMENT, document);
        intent.putExtra(Constant.EXTRA_FIX_RECT_MODE, fixRectMode);
        activity.startActivityForResult(intent, 1002);
    }

    private static Intent getStartIntentForNewFile(FragmentActivity activity, DetectionResult.FIX_RECT_MODE fixRectMode, @Nullable String parent, boolean notFirstInDoc) {
        if (parent == null) {
            parent = "";
        }
        int num = DBManager.getInstance().getTodayDocuments(parent);
        String filename = TagUtils.getNameTemplate(SharedPrefsUtils.getNameTemplate(activity), activity);
        if (num > 0) {
            filename = filename + " (" + num + Constant.STR_BRACKET_CLOSE;
        }
        Document curDocument = new Document(parent);
        curDocument.m_bNew = true;
        curDocument.notFirstInDoc = notFirstInDoc;
        curDocument.name = filename;
        Intent intent = new Intent(activity, CropImageActivity.class);
        intent.putExtra(Constant.EXTRA_DOCUMENT, curDocument);
        intent.putExtra(Constant.EXTRA_FIX_RECT_MODE, fixRectMode);
        return intent;
    }

    public void onBackPressed() {
        deleteData();
        super.onBackPressed();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_crop);

        LinearLayout relative_ad = (LinearLayout) findViewById(R.id.relative_ad);
        ScanApplication app = (ScanApplication) getApplication();
        app.adaptiveBannerView(relative_ad);

        initUI();
        initVariable();
        showOverlay();
    }

    private void showOverlay() {
        int i = this.cropOpened;
        if (i == 1) {
            ActivityUtils.waitVisibleFor(new ActivityUtils.OnVisibleChecker() {
                public final boolean isVisible() {
                    return CropImageActivity.this.isBtnRecropReady();
                }
            }, new ActivityUtils.OnVisibleListener() {
                public final void onVisible() {
                    CropImageActivity.this.showRecropTutorial();
                }
            });
        } else if (i == 2) {
            ActivityUtils.waitVisibleFor(new ActivityUtils.OnVisibleChecker() {
                public final boolean isVisible() {
                    return CropImageActivity.this.isDotReady();
                }
            }, new ActivityUtils.OnVisibleListener() {
                public final void onVisible() {
                    CropImageActivity.this.showDotTutorial();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public boolean isBtnRecropReady() {
        return this.m_rlCrop.getWidth() != 0;
    }

    /* access modifiers changed from: private */
    public void showRecropTutorial() {
        TutorialManager.showTutorial(getSupportFragmentManager(), new TutorialInfo(R.layout.tutorial_crop_recrop, R.id.btn_crop));
    }

    /* access modifiers changed from: private */
    public boolean isDotReady() {
        return (this.m_civCrop.getWidth() == 0 || !this.m_civCrop.isInitialized() || this.m_civCrop.getRawEdge() == null) ? false : true;
    }

    /* access modifiers changed from: private */
    public void showDotTutorial() {
        PointF[] edge = this.m_civCrop.getRawEdge();
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        float x = (edge[3].x + this.m_civCrop.getX()) - (this.dotSize / 2.0f);
        float y = edge[3].y + this.m_civCrop.getY();
        float f = this.dotSize;
        TutorialManager.showTutorial(supportFragmentManager, new TutorialViewInfo((int) R.layout.tutorial_crop_dot, (int) R.id.btn_dot, x, y - (f / 2.0f), (int) f, (int) f, true));
    }

    public void onTutorialViewClicked(View v) {
        if (v.getId() == R.id.btn_crop) {
            onClick(this.m_rlCrop);
        }
    }

    public void onTutorialClosed(TutorialInfo tutorialInfo, boolean targetHit) {
        if (tutorialInfo.layoutId == R.layout.tutorial_crop_recrop) {
            SharedPrefsUtils.setCropOpened(this, 1);
//            Analytics.get().logTutorCrop();
        } else if (tutorialInfo.layoutId == R.layout.tutorial_crop_dot) {
            SharedPrefsUtils.setCropOpened(this, 2);
        }
    }

    private void initVariable() {
        System.gc();
        this.m_curDoc = (Document) getIntent().getSerializableExtra(Constant.EXTRA_DOCUMENT);
        this.mFixRectMode = (DetectionResult.FIX_RECT_MODE) getIntent().getSerializableExtra(Constant.EXTRA_FIX_RECT_MODE);
        this.m_bmpSource = ScanApplication.imageHolder().getOriginalPicture();
        this.cropOpened = SharedPrefsUtils.getCropOpened(this);
        this.dotSize = getResources().getDimension(R.dimen.crop_dot_size);
        if (this.m_bmpSource != null) {
            this.m_detectionResult = new DetectionResult();
            m_nAngle = 0;
            this.m_bFull = false;
            this.halfSide = getResources().getDimension(R.dimen.margin_mag_side) / 2.0f;
            this.distance = getResources().getDimension(R.dimen.margin_mag_distance);
            this.m_civCrop.setImageBitmap(this.m_bmpSource);
            if (this.m_curDoc.m_bNew) {
                getDetectInfo(this.m_bmpSource, this.m_detectionResult);
                this.m_curDoc.setCropPoints(this.m_detectionResult.m_ptfInfo);
            }
            this.m_curPoints = this.m_curDoc.getCropPoints();
            this.m_defaultPoints = this.m_curDoc.getCropPoints();
            this.m_civCrop.setEdge(this.m_curPoints);
        } else {
            Toast.makeText(this, getString(R.string.alert_sorry), Toast.LENGTH_SHORT).show();
        }
        System.gc();
    }

    private void initUI() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        this.m_civCrop = (SimpleCropImageView) findViewById(R.id.iv_crop);
//        this.m_rlBack = findViewById(R.id.btn_back);
        this.m_rlTopBack = findViewById(R.id.btn_back_top);
        this.m_rlLeft = findViewById(R.id.btn_rotate_left);
        this.m_rlRight = findViewById(R.id.btn_rotate_right);
        this.m_rlCrop = findViewById(R.id.btn_crop);
        this.m_rlDone = findViewById(R.id.btn_done);
        this.m_ivCrop = (ImageView) findViewById(R.id.img_crop);
        this.ivMagLeft = (ImageView) findViewById(R.id.iv_mag_left);
        if (this.m_bFull) {
            this.m_ivCrop.setImageResource(R.drawable.ic_recrop_decrease);
        } else {
            this.m_ivCrop.setImageResource(R.drawable.ic_recrop_increase);
        }
        this.m_civCrop.setHandleSizeInDp((int) getResources().getDimension(R.dimen.handal_size));
        this.m_civCrop.setCallback(this);
//        this.m_rlBack.setOnClickListener(this);
        this.m_rlTopBack.setOnClickListener(this);
        this.m_rlLeft.setOnClickListener(this);
        this.m_rlRight.setOnClickListener(this);
        this.m_rlCrop.setOnClickListener(this);
        this.m_rlDone.setOnClickListener(this);
    }

    public void getDetectInfo(Bitmap bmpSrc, DetectionResult result) {
        Mat matTemp = new Mat(bmpSrc.getHeight(), bmpSrc.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bmpSrc, matTemp, false);
        Mat matSrc = new Mat(bmpSrc.getHeight(), bmpSrc.getWidth(), CvType.CV_8UC1);
        Imgproc.cvtColor(matTemp, matSrc, 11);
        matTemp.release();
        if (!ScanApplication.getDetectionEngine().process(matSrc.getNativeObjAddr(), result)) {
            result.fixSmallRect(this.mFixRectMode);
        }
        matSrc.release();
    }

    private void deleteData() {
        ScanApplication.imageHolder().resetAll();
        if (this.m_curDoc.m_bNew) {
            ImageStorageUtils.deleteImage(this, ScanApplication.imageHolder().getOriginalPath());
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
            case R.id.btn_back_top:
                deleteData();
                finish();
                return;
            case R.id.btn_crop:
                resetCrop();
                return;
            case R.id.btn_done:
                if (this.m_civCrop.isValidPoints()) {
                    saveCrop();
                    return;
                } else {
                    Toast.makeText(this, getString(R.string.alert_sorry), Toast.LENGTH_SHORT).show();
                    return;
                }
            case R.id.btn_rotate_left:
                m_nAngle -= 90;
                if (m_nAngle == -360) {
                    m_nAngle = 0;
                }
                this.m_civCrop.rotateImage(SimpleCropImageView.RotateDegrees.ROTATE_M90D);
                this.m_defaultPoints = SimpleCropImageView.rotateEdge(this.m_defaultPoints, SimpleCropImageView.RotateDegrees.ROTATE_M90D);
                this.m_curPoints = SimpleCropImageView.rotateEdge(this.m_curPoints, SimpleCropImageView.RotateDegrees.ROTATE_M90D);
                return;
            case R.id.btn_rotate_right:
                m_nAngle += 90;
                if (m_nAngle == 360) {
                    m_nAngle = 0;
                }
                this.m_civCrop.rotateImage(SimpleCropImageView.RotateDegrees.ROTATE_90D);
                this.m_defaultPoints = SimpleCropImageView.rotateEdge(this.m_defaultPoints, SimpleCropImageView.RotateDegrees.ROTATE_90D);
                this.m_curPoints = SimpleCropImageView.rotateEdge(this.m_curPoints, SimpleCropImageView.RotateDegrees.ROTATE_90D);
                return;
            default:
                return;
        }
    }

    public void resetCrop() {
        this.m_bFull = !this.m_bFull;
        if (this.m_bFull) {
            this.m_ivCrop.setImageResource(R.drawable.ic_recrop_decrease);
        } else {
            this.m_ivCrop.setImageResource(R.drawable.ic_recrop_increase);
        }
        if (this.m_bFull) {
            this.m_civCrop.setEdge((PointF[]) null);
            this.m_civCrop.calcEdgePoint();
        } else {
            this.m_civCrop.setEdge(this.m_defaultPoints);
            this.m_civCrop.calcEdgePoint();
        }
        this.m_civCrop.invalidate();
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
//        Analytics.get().logCropScreen();
        this.saving = false;
    }

    private void saveCrop() {
        final float fltRectHeight;
        final float fltRectWidth;
        final int nVWHeight;
        final int nVWWidth;
        final int nBmpHeight;
        final int nBmpWidth;
        if (!this.saving) {
            boolean z = true;
            this.saving = true;
            try {
                if (!ScanApplication.imageHolder().hasOriginalPicture()) {
                    Toast.makeText(this, getString(R.string.alert_sorry), 1).show();
                    finish();
                    return;
                }
                if (m_nAngle != 0) {
                    ScanApplication.imageHolder().setCroppedPicture(rotate(ScanApplication.imageHolder().getOriginalPicture(), (float) m_nAngle), false, true);
                } else {
                    ScanApplication.imageHolder().setCroppedPicture(ScanApplication.imageHolder().getOriginalPicture(), false, false);
                }
//                Analytics analytics = Analytics.get();
                if (m_nAngle == 0) {
                    z = false;
                }
//                analytics.logCropDoc(z, this.m_bMoved);
                final PointF[] ptEdges = this.m_civCrop.getEdge();
                this.m_curDoc.setCropPoints(this.m_civCrop.getOrgEdge());
                this.m_detectionResult.saveDetectInfo(this.m_curDoc.getCropPoints(), new Rect());
                RectF resultRect = this.m_detectionResult.getResultRect();
                if (((this.m_civCrop.getAngle() + 360.0f) / 90.0f) % 2.0f == 0.0f) {
                    nBmpWidth = this.m_bmpSource.getWidth();
                    nBmpHeight = this.m_bmpSource.getHeight();
                    nVWWidth = this.m_civCrop.getWidth();
                    nVWHeight = this.m_civCrop.getHeight();
                    fltRectWidth = resultRect.width();
                    fltRectHeight = resultRect.height();
                } else {
                    nBmpWidth = this.m_bmpSource.getHeight();
                    nBmpHeight = this.m_bmpSource.getWidth();
                    nVWWidth = this.m_civCrop.getWidth();
                    nVWHeight = this.m_civCrop.getHeight();
                    fltRectWidth = resultRect.height();
                    fltRectHeight = resultRect.width();
                }

                if(InterstitialHelper.isLoaded)
                {
                    InterstitialHelper.showInterstitial(this, new InterstialListner() {
                        @Override
                        public void onAddClose() {
                            Intent intent = new Intent(CropImageActivity.this, CropImageEffectActivity.class);
                            intent.putExtra(Constant.EXTRA_DOCUMENT, m_curDoc);
                            intent.putExtra(Constant.EXTRA_SORTID, m_curDoc);
                            intent.putExtra(Constant.EXTRA_RECT_WIDTH, (int) (((float) nBmpWidth) * fltRectWidth));
                            intent.putExtra(Constant.EXTRA_RECT_HEIGHT, (int) (((float) nBmpHeight) * fltRectHeight));
                            intent.putExtra(Constant.EXTRA_VIEW_WIDTH, nVWWidth);
                            intent.putExtra(Constant.EXTRA_VIEW_HEIGHT, nVWHeight);
                            intent.putExtra(Constant.EXTRA_SORTID, getIntent().getIntExtra(Constant.EXTRA_SORTID, -1));
                            intent.putExtra(Constant.EXTRA_CROP_POINTS, new Gson().toJson((Object) ptEdges));
                            startActivityForResult(intent, 1009);
                        }
                    });
                }
                else {
                    Intent intent = new Intent(CropImageActivity.this, CropImageEffectActivity.class);
                    intent.putExtra(Constant.EXTRA_DOCUMENT, m_curDoc);
                    intent.putExtra(Constant.EXTRA_SORTID, m_curDoc);
                    intent.putExtra(Constant.EXTRA_RECT_WIDTH, (int) (((float) nBmpWidth) * fltRectWidth));
                    intent.putExtra(Constant.EXTRA_RECT_HEIGHT, (int) (((float) nBmpHeight) * fltRectHeight));
                    intent.putExtra(Constant.EXTRA_VIEW_WIDTH, nVWWidth);
                    intent.putExtra(Constant.EXTRA_VIEW_HEIGHT, nVWHeight);
                    intent.putExtra(Constant.EXTRA_SORTID, getIntent().getIntExtra(Constant.EXTRA_SORTID, -1));
                    intent.putExtra(Constant.EXTRA_CROP_POINTS, new Gson().toJson((Object) ptEdges));
                    startActivityForResult(intent, 1009);
                }


                // TODO: 04-02-2022 comment by peenalkumar
                //Google_Intertitial.Show_Intertitial_Ad(CropImageActivity.this);

                /*final Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {

                        if (Google_Intertitial.Load == true) {
                            if (Google_Intertitial.close == true) {

                                Google_Intertitial.close = false;
                                Google_Intertitial.Load = false;
                                timer.cancel();


                                Intent intent = new Intent(CropImageActivity.this, CropImageEffectActivity.class);
                                intent.putExtra(Constant.EXTRA_DOCUMENT, m_curDoc);
                                intent.putExtra(Constant.EXTRA_SORTID, m_curDoc);
                                intent.putExtra(Constant.EXTRA_RECT_WIDTH, (int) (((float) nBmpWidth) * fltRectWidth));
                                intent.putExtra(Constant.EXTRA_RECT_HEIGHT, (int) (((float) nBmpHeight) * fltRectHeight));
                                intent.putExtra(Constant.EXTRA_VIEW_WIDTH, nVWWidth);
                                intent.putExtra(Constant.EXTRA_VIEW_HEIGHT, nVWHeight);
                                intent.putExtra(Constant.EXTRA_SORTID, getIntent().getIntExtra(Constant.EXTRA_SORTID, -1));
                                intent.putExtra(Constant.EXTRA_CROP_POINTS, new Gson().toJson((Object) ptEdges));
                                startActivityForResult(intent, 1009);

                            } else {

                            }
                        } else {
                            Google_Intertitial.close = false;
                            Google_Intertitial.Load = false;
                            timer.cancel();

                            Intent intent = new Intent(CropImageActivity.this, CropImageEffectActivity.class);
                            intent.putExtra(Constant.EXTRA_DOCUMENT, m_curDoc);
                            intent.putExtra(Constant.EXTRA_SORTID, m_curDoc);
                            intent.putExtra(Constant.EXTRA_RECT_WIDTH, (int) (((float) nBmpWidth) * fltRectWidth));
                            intent.putExtra(Constant.EXTRA_RECT_HEIGHT, (int) (((float) nBmpHeight) * fltRectHeight));
                            intent.putExtra(Constant.EXTRA_VIEW_WIDTH, nVWWidth);
                            intent.putExtra(Constant.EXTRA_VIEW_HEIGHT, nVWHeight);
                            intent.putExtra(Constant.EXTRA_SORTID, getIntent().getIntExtra(Constant.EXTRA_SORTID, -1));
                            intent.putExtra(Constant.EXTRA_CROP_POINTS, new Gson().toJson((Object) ptEdges));
                            startActivityForResult(intent, 1009);


                        }


                        
                    }
                }, 0, 3);*/

            } catch (OutOfMemoryError ex) {
                Timber.e(ex);
                System.gc();
                Toast.makeText(this, getString(R.string.alert_sorry), Toast.LENGTH_LONG).show();
                this.saving = false;
            }
        }
    }

    private Bitmap rotate(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, false);
        } catch (Exception ex) {
            Timber.e(ex);
            return null;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1009 && resultCode == -1) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(Constant.EXTRA_MPARENT, data.getStringExtra(Constant.EXTRA_MPARENT));
            resultIntent.putExtra(Constant.EXTRA_MNAME, data.getStringExtra(Constant.EXTRA_MNAME));
            resultIntent.putExtra(Constant.EXTRA_REPLACE, getIntent().getBooleanExtra(Constant.EXTRA_REPLACE, false));
            setResult(-1, resultIntent);
            ScanApplication.imageHolder().resetAll();
            finish();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void onMoved(boolean bMoved) {
        if (bMoved) {
            this.m_bFull = false;
            this.m_bMoved = true;
            this.m_ivCrop.setImageResource(R.drawable.ic_recrop_increase);
            if (!this.moveSaved && this.cropOpened < 3) {
                SharedPrefsUtils.setCropOpened(this, 2);
                this.moveSaved = true;
            }
        }
    }

    public void onMove(float x, float y, RectF rect) {
        Timber.tag(LOG_TAG).d("before x %s y %s", Float.valueOf(x), Float.valueOf(y));
        float tempY = checkY(y, rect);
        float f = this.halfSide;
        float x2 = x - f;
        float f2 = this.distance;
        float y2 = (tempY - f) - f2;
        if (y2 < (-f)) {
            y2 = f2 + tempY + f;
        }
        Timber.tag(LOG_TAG).i("after x %s y %s", Float.valueOf(x2), Float.valueOf(y2));
        this.ivMagLeft.setX(x2);
        this.ivMagLeft.setY(y2);
    }

    private float checkY(float y, RectF rect) {
        if (y > rect.top && y < rect.bottom) {
            return y;
        }
        if (y <= rect.top) {
            return rect.top;
        }
        return rect.bottom;
    }

    public ImageView lensView() {
        return this.ivMagLeft;
    }
}
