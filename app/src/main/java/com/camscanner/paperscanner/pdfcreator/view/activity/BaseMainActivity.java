package com.camscanner.paperscanner.pdfcreator.view.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.camscanner.paperscanner.pdfcreator.common.utils.LanguageUtil.LocalManageUtil;

public class BaseMainActivity extends AppCompatActivity {
    protected ProgressDialog mProgressDialog;

    /* access modifiers changed from: protected */
    public void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalManageUtil.getInstance().updateLocal(newBase));
    }

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
//        ScanApplication.adsManager.attachActivity(this);
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
//        ScanApplication.adsManager.detachActivity(this);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        hideProgressDialog();
    }

    public void showProgressDialog(String message) {
        ProgressDialog progressDialog = this.mProgressDialog;
        if ((progressDialog == null || !progressDialog.isShowing()) && !isFinishing()) {
            this.mProgressDialog = new ProgressDialog(this);
            this.mProgressDialog.setCancelable(false);
            this.mProgressDialog.setMessage(message);
            this.mProgressDialog.show();
        }
    }

    public void hideProgressDialog() {
        ProgressDialog progressDialog = this.mProgressDialog;
        if (progressDialog != null && progressDialog.isShowing()) {
            if (!isFinishing()) {
                this.mProgressDialog.dismiss();
            }
            this.mProgressDialog = null;
        }
    }
}
