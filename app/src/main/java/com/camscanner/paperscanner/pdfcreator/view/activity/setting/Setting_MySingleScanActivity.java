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

public class Setting_MySingleScanActivity extends BaseMainActivity implements View.OnClickListener {
    private ImageView m_ivColorModeAuto;
    private ImageView m_ivColorModeBW1;
    private ImageView m_ivColorModeBW2;
    private ImageView m_ivColorModeGray;
    private ImageView m_ivColorModeLighten;
    private ImageView m_ivColorModeOriginal;
    private ImageView m_ivColorModePolish;
    private RelativeLayout m_rlColorModeAuto;
    private RelativeLayout m_rlColorModeBW1;
    private RelativeLayout m_rlColorModeBW2;
    private RelativeLayout m_rlColorModeGray;
    private RelativeLayout m_rlColorModeLighten;
    private RelativeLayout m_rlColorModeOriginal;
    private RelativeLayout m_rlColorModePolish;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_setting_single_scan);
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
        this.m_rlColorModeAuto.setOnClickListener(this);
        this.m_rlColorModeOriginal.setOnClickListener(this);
        this.m_rlColorModeLighten.setOnClickListener(this);
        this.m_rlColorModePolish.setOnClickListener(this);
        this.m_rlColorModeGray.setOnClickListener(this);
        this.m_rlColorModeBW1.setOnClickListener(this);
        this.m_rlColorModeBW2.setOnClickListener(this);

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
        setColorMode(SharedPrefsUtils.getSingleColorMode(this));
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
        SharedPrefsUtils.setSingleColorMode(this, mode);
    }
}
