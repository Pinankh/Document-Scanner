package com.camscanner.paperscanner.pdfcreator.view.activity.setting;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.utils.SharedPrefsUtils;
import com.camscanner.paperscanner.pdfcreator.view.activity.BaseMainActivity;

public class Setting_MyBatchScanActivity extends BaseMainActivity implements View.OnClickListener {
    private ImageView m_ivColorModeAuto;
    private ImageView m_ivColorModeBW1;
    private ImageView m_ivColorModeBW2;
    private ImageView m_ivColorModeGray;
    private ImageView m_ivColorModeLighten;
    private ImageView m_ivColorModeOriginal;
    private ImageView m_ivColorModePolish;
    private ImageView m_ivCropModeAlways;
    private ImageView m_ivCropModeNot;
    private ImageView m_ivCropModeSmart;
    private RelativeLayout m_rlColorModeAuto;
    private RelativeLayout m_rlColorModeBW1;
    private RelativeLayout m_rlColorModeBW2;
    private RelativeLayout m_rlColorModeGray;
    private RelativeLayout m_rlColorModeLighten;
    private RelativeLayout m_rlColorModeOriginal;
    private RelativeLayout m_rlColorModePolish;
    private RelativeLayout m_rlCropModeAlways;
    private RelativeLayout m_rlCropModeNot;
    private RelativeLayout m_rlCropModeSmart;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_setting_batch_scan);
        initUI();
        initVariable();
    }

    /* access modifiers changed from: package-private */
    public void initUI() {

        this.m_rlColorModeAuto = (RelativeLayout) findViewById(R.id.rl_color_mode_auto);
        this.m_rlColorModeOriginal = (RelativeLayout) findViewById(R.id.rl_color_mode_original);
        this.m_rlColorModeLighten = (RelativeLayout) findViewById(R.id.rl_color_mode_lighten);
        this.m_rlColorModePolish = (RelativeLayout) findViewById(R.id.rl_color_mode_polish);
        this.m_rlColorModeGray = (RelativeLayout) findViewById(R.id.rl_color_mode_gray);
        this.m_rlColorModeBW1 = (RelativeLayout) findViewById(R.id.rl_color_mode_bw1);
        this.m_rlColorModeBW2 = (RelativeLayout) findViewById(R.id.rl_color_mode_bw2);
        this.m_ivColorModeAuto = (ImageView) findViewById(R.id.iv_color_mode_auto);
        this.m_ivColorModeOriginal = (ImageView) findViewById(R.id.iv_color_mode_original);
        this.m_ivColorModeLighten = (ImageView) findViewById(R.id.iv_color_mode_lighten);
        this.m_ivColorModePolish = (ImageView) findViewById(R.id.iv_color_mode_polish);
        this.m_ivColorModeGray = (ImageView) findViewById(R.id.iv_color_mode_gray);
        this.m_ivColorModeBW1 = (ImageView) findViewById(R.id.iv_color_mode_bw1);
        this.m_ivColorModeBW2 = (ImageView) findViewById(R.id.iv_color_mode_bw2);
        this.m_rlCropModeNot = (RelativeLayout) findViewById(R.id.rl_crop_mode_not);
        this.m_rlCropModeSmart = (RelativeLayout) findViewById(R.id.rl_crop_mode_smart);
        this.m_rlCropModeAlways = (RelativeLayout) findViewById(R.id.rl_crop_mode_always);
        this.m_ivCropModeNot = (ImageView) findViewById(R.id.iv_crop_mode_not);
        this.m_ivCropModeSmart = (ImageView) findViewById(R.id.iv_crop_mode_smart);
        this.m_ivCropModeAlways = (ImageView) findViewById(R.id.iv_crop_mode_always);
        this.m_rlColorModeAuto.setOnClickListener(this);
        this.m_rlColorModeOriginal.setOnClickListener(this);
        this.m_rlColorModeLighten.setOnClickListener(this);
        this.m_rlColorModePolish.setOnClickListener(this);
        this.m_rlColorModeGray.setOnClickListener(this);
        this.m_rlColorModeBW1.setOnClickListener(this);
        this.m_rlColorModeBW2.setOnClickListener(this);
        this.m_rlCropModeNot.setOnClickListener(this);
        this.m_rlCropModeSmart.setOnClickListener(this);
        this.m_rlCropModeAlways.setOnClickListener(this);

        ImageView btn_back = (ImageView)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void initVariable() {
        int colorMode = SharedPrefsUtils.getBatchColorMode(this);
        int cropMode = SharedPrefsUtils.getBatchCropMode(this);
        setColorMode(colorMode);
        setCropMode(cropMode);
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
            case R.id.rl_color_mode_auto:
                setColorMode(0);
                return;
            case R.id.rl_color_mode_bw1:
                setColorMode(5);
                return;
            case R.id.rl_color_mode_bw2:
                setColorMode(6);
                return;
            case R.id.rl_color_mode_gray:
                setColorMode(4);
                return;
            case R.id.rl_color_mode_lighten:
                setColorMode(2);
                return;
            case R.id.rl_color_mode_original:
                setColorMode(1);
                return;
            case R.id.rl_color_mode_polish:
                setColorMode(3);
                return;

            default:
                return;
            case R.id.rl_crop_mode_always:
                setCropMode(2);
                return;
            case R.id.rl_crop_mode_not:
                setCropMode(0);
                return;
            case R.id.rl_crop_mode_smart:
                setCropMode(1);
                return;
        }
    }

    /* access modifiers changed from: package-private */
    public void setColorMode(int mode) {
        this.m_ivColorModeAuto.setVisibility(4);
        this.m_ivColorModeOriginal.setVisibility(4);
        this.m_ivColorModeLighten.setVisibility(4);
        this.m_ivColorModePolish.setVisibility(4);
        this.m_ivColorModeGray.setVisibility(4);
        this.m_ivColorModeBW1.setVisibility(4);
        this.m_ivColorModeBW2.setVisibility(4);
        switch (mode) {
            case 0:
                this.m_ivColorModeAuto.setVisibility(0);
                break;
            case 1:
                this.m_ivColorModeOriginal.setVisibility(0);
                break;
            case 2:
                this.m_ivColorModeLighten.setVisibility(0);
                break;
            case 3:
                this.m_ivColorModePolish.setVisibility(0);
                break;
            case 4:
                this.m_ivColorModeGray.setVisibility(0);
                break;
            case 5:
                this.m_ivColorModeBW1.setVisibility(0);
                break;
            case 6:
                this.m_ivColorModeBW2.setVisibility(0);
                break;
        }
        SharedPrefsUtils.setBatchColorMode(this, mode);
    }

    /* access modifiers changed from: package-private */
    public void setCropMode(int mode) {
        this.m_ivCropModeNot.setVisibility(4);
        this.m_ivCropModeSmart.setVisibility(4);
        this.m_ivCropModeAlways.setVisibility(4);
        if (mode == 0) {
            this.m_ivCropModeNot.setVisibility(0);
        } else if (mode == 1) {
            this.m_ivCropModeSmart.setVisibility(0);
        } else if (mode == 2) {
            this.m_ivCropModeAlways.setVisibility(0);
        }
        SharedPrefsUtils.setBatchCropMode(this, mode);
    }
}
