package com.camscanner.paperscanner.pdfcreator.view.activity.setting;

import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.suke.widget.SwitchButton;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.utils.SharedPrefsUtils;
import com.camscanner.paperscanner.pdfcreator.features.ocr.presentation.OCRActivity;
import com.camscanner.paperscanner.pdfcreator.model.Document;
import com.camscanner.paperscanner.pdfcreator.view.activity.BaseMainActivity;

public class Setting_MyScanActivity extends BaseMainActivity implements View.OnClickListener {
    private SwitchButton m_swtSaveAlbum;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_setting_scan);
        initUI();
        initVariable();
    }


    public void initUI() {

        ImageView btn_back = (ImageView)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        this.m_swtSaveAlbum = (SwitchButton) findViewById(R.id.swt_setting_save_album);
        this.m_swtSaveAlbum.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            public final void onCheckedChanged(SwitchButton switchButton, boolean z) {
                Setting_MyScanActivity.this.lambda$initUI$0$SettingScanActivity(switchButton, z);
            }
        });
        findViewById(R.id.rl_setting_ocr_lang).setOnClickListener(this);
        findViewById(R.id.rl_setting_single_scan).setOnClickListener(this);
        findViewById(R.id.rl_setting_batch_scan).setOnClickListener(this);
    }

    public /* synthetic */ void lambda$initUI$0$SettingScanActivity(SwitchButton view, boolean isChecked) {
        SharedPrefsUtils.setSaveToAlbum(this, isChecked);
    }

    /* access modifiers changed from: package-private */
    public void initVariable() {
        this.m_swtSaveAlbum.setChecked(SharedPrefsUtils.getSaveToAlbum(this));
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
            case R.id.rl_setting_batch_scan:
                goToBatchScanSetting();
                return;
            case R.id.rl_setting_ocr_lang:
                goToOCRLangActivity();
                return;

            case R.id.rl_setting_single_scan:
                goToSingleScanSetting();
                return;
            default:
                return;
        }
    }


    /* access modifiers changed from: package-private */
    public void goToOCRLangActivity() {
        OCRActivity.start(this, (Document) null);
    }

    /* access modifiers changed from: package-private */
    public void goToSingleScanSetting() {
        startActivity(new Intent(this, Setting_MySingleScanActivity.class));
    }

    /* access modifiers changed from: package-private */
    public void goToBatchScanSetting() {
        startActivity(new Intent(this, Setting_MyBatchScanActivity.class));
    }
}
