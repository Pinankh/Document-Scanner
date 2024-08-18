package com.camscanner.paperscanner.pdfcreator.features.barcode.presentation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;


import com.google.zxing.Result;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.dm7.barcodescanner.core.CameraUtils;
import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.p018db.DBManager;
import com.camscanner.paperscanner.pdfcreator.common.utils.ViewUtil;
import com.camscanner.paperscanner.pdfcreator.features.barcode.QrResultHandler;
import com.camscanner.paperscanner.pdfcreator.features.barcode.model.QrResult;
import com.camscanner.paperscanner.pdfcreator.view.activity.BaseMainActivity;
import timber.log.Timber;

public class BarcodeScannerActivity extends BaseMainActivity implements ZXingScannerView.ResultHandler {
    private static final String LOG_TAG = BarcodeScannerActivity.class.getSimpleName();
    private int currentCamera;
    private Disposable handling;
    private QrResultHandler resultHandler;
    ConstraintLayout root;
    private ZXingScannerView scannerView;

    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView((int) R.layout.activity_qr_scan);

        root = (ConstraintLayout)findViewById(R.id.root);
        ImageView btn_back = (ImageView)findViewById(R.id.btn_back);
        ImageView btn_list = (ImageView)findViewById(R.id.btn_list);
        ImageView btn_flash = (ImageView)findViewById(R.id.btn_flash);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btn_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onListPressed();
            }
        });

        btn_flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFlashPressed();
            }
        });


        this.resultHandler = new QrResultHandler(DBManager.getInstance());
        initScannerView();
    }

    private void initScannerView() {
        this.scannerView = new ZXingScannerView(this) {
            /* access modifiers changed from: protected */
            public IViewFinder createViewFinderView(Context context) {
                return new CustomFinderView(context);
            }
        };
        this.scannerView.setId(ViewUtil.generateId());
        this.scannerView.setAspectTolerance(0.2f);
        this.root.addView(this.scannerView);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this.root);
        constraintSet.connect(this.scannerView.getId(), 3, R.id.appbar, 4, 0);
        constraintSet.connect(this.scannerView.getId(), 1, 0, 1, 0);
        constraintSet.connect(this.scannerView.getId(), 2, 0, 2, 0);
        constraintSet.connect(this.scannerView.getId(), 4, 0, 4, 0);
        constraintSet.applyTo(this.root);
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
//        Analytics.get().logQrScreen();
    }

    public void onResume() {
        super.onResume();
        this.currentCamera = CameraUtils.getDefaultCameraId();
        this.scannerView.setResultHandler(this);
        this.scannerView.startCamera(this.currentCamera);
    }

    public void onPause() {
        super.onPause();
        this.scannerView.stopCamera();
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    public void onListPressed() {
        BarcodeHistoryActivity.startHistoryActivity(this);
    }

    public void onFlashPressed() {
        ZXingScannerView zXingScannerView = this.scannerView;
        zXingScannerView.setFlash(!zXingScannerView.getFlash());
    }

    public void handleResult(Result rawResult) {
        Timber.tag(LOG_TAG).i("result %s", rawResult);
        Disposable disposable = this.handling;
        if (disposable == null || disposable.isDisposed()) {
            showProgressDialog(getString(R.string.loading_and_process_image));
            this.handling = this.resultHandler.handleResult(rawResult).subscribe(new Consumer() {
                public final void accept(Object obj) {
                    BarcodeScannerActivity.this.handleSuccess((QrResult) obj);
                }
            }, new Consumer() {
                public final void accept(Object obj) {
                    BarcodeScannerActivity.this.handleError((Throwable) obj);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void handleSuccess(QrResult qrResult) {
        hideProgressDialog();
        BarcodeResultActivity.startResultActivity(this, qrResult);
//        Analytics.get().logQrScanned();
    }

    /* access modifiers changed from: private */
    public void handleError(Throwable throwable) {
        hideProgressDialog();
        Timber.e(throwable);
        this.scannerView.resumeCameraPreview(this);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0) {
            finish();
        }
    }
}
