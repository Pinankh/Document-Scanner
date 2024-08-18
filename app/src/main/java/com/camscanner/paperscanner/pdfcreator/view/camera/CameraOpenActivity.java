package com.camscanner.paperscanner.pdfcreator.view.camera;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.gson.Gson;
import java.util.List;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.ScanApplication;
import com.camscanner.paperscanner.pdfcreator.common.Constant;
import com.camscanner.paperscanner.pdfcreator.model.Document;
import com.camscanner.paperscanner.pdfcreator.model.types.CameraScanMode;
import com.camscanner.paperscanner.pdfcreator.view.activity.BaseMainActivity;
import com.camscanner.paperscanner.pdfcreator.view.fragment.BackButtonListener;
import timber.log.Timber;

public class CameraOpenActivity extends BaseMainActivity {
    public static final String TAG = CameraOpenActivity.class.getSimpleName();
    static boolean m_bActive = false;


    public void onStart() {
        super.onStart();
        m_bActive = true;
    }


    public void onStop() {
        super.onStop();
        m_bActive = false;
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_camera);

        LinearLayout relative_ad = (LinearLayout) findViewById(R.id.relative_ad);
        ScanApplication app = (ScanApplication) getApplication();
        app.adaptiveBannerView(relative_ad);

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                CameraOpenActivity.this.lambda$onCreate$0$CameraActivity(view);
            }
        });
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, CameraFragment.newInstance(), CameraFragment.TAG).commit();
        }
    }

    public /* synthetic */ void lambda$onCreate$0$CameraActivity(View v) {
        onBackPressed();
    }

    public void errorActivity(Throwable ex) {
        Timber.e(ex);
        hideProgressDialog();
        runOnUiThread(new Runnable() {
            public final void run() {
                CameraOpenActivity.this.lambda$errorActivity$1$CameraActivity();
            }
        });
    }

    public /* synthetic */ void lambda$errorActivity$1$CameraActivity() {
        Toast.makeText(this, getString(R.string.alert_sorry), 1).show();
        finish();
    }

    public void returnPhotoUri(CameraScanMode mode, List<Document> arr) {
        Intent data = new Intent();
        data.setFlags(mode.value());
        data.putExtra(Constant.EXTRA_DOC_ARRAY, new Gson().toJson((Object) arr));
        if (getParent() == null) {
            setResult(-1, data);
        } else {
            getParent().setResult(-1, data);
        }
        finish();
    }

    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment instanceof BackButtonListener) {
            ((BackButtonListener) fragment).onBackPressed();
        } else {
            super.onBackPressed();
        }
    }
}
