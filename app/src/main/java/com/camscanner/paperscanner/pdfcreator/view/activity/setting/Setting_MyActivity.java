package com.camscanner.paperscanner.pdfcreator.view.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;

import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.view.activity.BaseMainActivity;

public class Setting_MyActivity extends BaseMainActivity implements View.OnClickListener {
    private static final int OPEN_PRIVACY_SETTINGS = 984;
//    private View settingPrivacy;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_setting);
        initUI();
    }

    /* access modifiers changed from: package-private */
    public void initUI() {
        findViewById(R.id.rl_setting_scan).setOnClickListener(this);
        findViewById(R.id.rl_setting_display).setOnClickListener(this);

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                Setting_MyActivity.this.lambda$initUI$0$SettingActivity(view);
            }
        });
    }

    public /* synthetic */ void lambda$initUI$0$SettingActivity(View v) {
        onBackPressed();
    }



    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 16908332) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_setting_display:
                goToDisplaySetting();
                return;


            case R.id.rl_setting_scan:
                goToScanSetting();
                return;

            default:
                return;
        }
    }


    public void goToScanSetting() {
        startActivity(new Intent(this, Setting_MyScanActivity.class));
    }


    public void goToDisplaySetting() {
        startActivity(new Intent(this, Setting_MyDisplayActivity.class));
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == OPEN_PRIVACY_SETTINGS) {

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
