package com.camscanner.paperscanner.pdfcreator.view.activity.setting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.utils.DeviceUtil;
import com.camscanner.paperscanner.pdfcreator.common.utils.SharedPrefsUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.StringHelper;
import com.camscanner.paperscanner.pdfcreator.common.utils.TagUtils;
import com.camscanner.paperscanner.pdfcreator.common.views.stepslider.OnSliderPositionChangeListener;
import com.camscanner.paperscanner.pdfcreator.common.views.stepslider.StepSlider;
import com.camscanner.paperscanner.pdfcreator.features.premium.PremiumHelper;
import com.camscanner.paperscanner.pdfcreator.model.types.Resolution;
import com.camscanner.paperscanner.pdfcreator.view.activity.BaseMainActivity;

public class Setting_MyDisplayActivity extends BaseMainActivity implements View.OnClickListener {
    private boolean m_bPremiumShow;
    private StepSlider m_sldImgSize;
    private TextView m_tvNameTemplate;
    private TextView m_tvTagText;
    private Resolution resolution;
    private TextView valueHoriz;
    private TextView valueVert;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_setting_display);

        ImageView btn_back = (ImageView)findViewById(R.id.btn_back);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        initVariable();
        initUI();
        updateUI();
    }

    /* access modifiers changed from: package-private */
    public void initVariable() {
        this.resolution = SharedPrefsUtils.getOutputSize(this);
        this.m_bPremiumShow = false;
    }

    /* access modifiers changed from: package-private */
    public void initUI() {

        findViewById(R.id.rl_display_name_tag).setOnClickListener(this);
        findViewById(R.id.rl_display_name_tag_set).setOnClickListener(this);
        findViewById(R.id.rl_display_pdf_size).setOnClickListener(this);
        this.m_tvNameTemplate = (TextView) findViewById(R.id.tv_name_template);
        this.m_tvTagText = (TextView) findViewById(R.id.tv_tag_text);
        this.m_sldImgSize = (StepSlider) findViewById(R.id.sld_img_size);
        this.valueVert = (TextView) findViewById(R.id.text_value_vert);
        this.valueHoriz = (TextView) findViewById(R.id.text_value_horiz);
        this.m_sldImgSize.setOnSliderPositionChangeListener(new OnSliderPositionChangeListener() {
            public final void onPositionChanged(int i, boolean z) {
                Setting_MyDisplayActivity.this.lambda$initUI$0$SettingDisplayActivity(i, z);
            }
        });
    }

    public /* synthetic */ void lambda$initUI$0$SettingDisplayActivity(int position, boolean bPosChanged) {
        int value;
        boolean bSaveQuality = true;
        if (position == 0) {
            value = 30;
        } else if (position == 1) {
            value = 50;
        } else if (position != 3) {
            value = 70;
        } else {
            value = 100;
        }
        String newValue = value + "%";
        this.valueVert.setText(newValue);
        this.valueHoriz.setText(newValue);
        if (!bPosChanged) {
            return;
        }
        if (bSaveQuality) {
            SharedPrefsUtils.setOutputSize(this, Resolution.get(position));
        } else if (!this.m_bPremiumShow) {
            showPremiumDialogAndActivity();
        }
    }

    private void showPremiumDialogAndActivity() {
        this.m_bPremiumShow = true;
        PremiumHelper.showPremiumAfterAlertDialog(this, R.string.best_quality, R.string.best_quality_output_alert, new PremiumHelper.StartActivityController() {
            public final void startActivity(Intent intent, int i) {
                Setting_MyDisplayActivity.this.startActivityForResult(intent, i);
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void updateUI() {
        this.m_tvNameTemplate.setText(TagUtils.getNameTemplate(SharedPrefsUtils.getNameTemplate(this), this));
        this.m_tvTagText.setText(SharedPrefsUtils.getTagText(this));
        this.m_sldImgSize.setPosition(this.resolution.pos());
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        this.m_tvNameTemplate.setText(TagUtils.getNameTemplate(SharedPrefsUtils.getNameTemplate(this), this));
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
            case R.id.rl_display_name_tag:
                goToNameTag();
                return;
            case R.id.rl_display_name_tag_set:
                goToTagText();
                return;
            case R.id.rl_display_pdf_size:
                goToPDFSize();
                return;
            default:
                return;
        }
    }

    /* access modifiers changed from: package-private */
    public void goToTagText() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dlg_tag_set, (ViewGroup) null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle((CharSequence) getString(R.string.setting_tag_name_dlg_title));
        builder.setView(dialogView);
        builder.setCancelable(false);
        final EditText edtTagText = (EditText) dialogView.findViewById(R.id.tv_tag_text_set);
        edtTagText.setText(SharedPrefsUtils.getTagText(this));
        builder.setPositiveButton((int) R.string.str_ok, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {


            public final void onClick(DialogInterface dialogInterface, int i) {
                Setting_MyDisplayActivity.this.lambda$goToTagText$1$SettingDisplayActivity(edtTagText, dialogInterface, i);
            }
        });
        builder.setNegativeButton((CharSequence) getString(R.string.str_cancel), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {


            public final void onClick(DialogInterface dialogInterface, int i) {
                Setting_MyDisplayActivity.this.lambda$goToTagText$2$SettingDisplayActivity(edtTagText, dialogInterface, i);
            }
        });
        builder.show();
        DeviceUtil.showKeyboard(this, edtTagText);
    }

    public /* synthetic */ void lambda$goToTagText$1$SettingDisplayActivity(EditText edtTagText, DialogInterface dialog, int id) {
        DeviceUtil.closeKeyboard((Context) this, edtTagText);
        String tagText = edtTagText.getText().toString();
        if (StringHelper.isEmpty(tagText)) {
            Toast.makeText(this, getString(R.string.alert_tag_text_empty), 0).show();
            return;
        }
        SharedPrefsUtils.setTagText(this, tagText);
        this.m_tvTagText.setText(tagText);
    }

    public /* synthetic */ void lambda$goToTagText$2$SettingDisplayActivity(EditText edtTagText, DialogInterface dialog, int id) {
        DeviceUtil.closeKeyboard((Context) this, edtTagText);
        dialog.cancel();
    }


    public void goToNameTag() {
        startActivity(new Intent(this, Setting_MyNameTagActivity.class));
    }


    public void goToPDFSize() {
        startActivity(new Intent(this, Setting_MyPDFSizeActivity.class));
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1012) {
            this.m_bPremiumShow = false;

                this.m_sldImgSize.setPosition(Resolution.HIGH.pos());

        }
    }
}
