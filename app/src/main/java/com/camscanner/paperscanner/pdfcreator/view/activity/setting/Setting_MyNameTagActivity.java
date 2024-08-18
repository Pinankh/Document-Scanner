package com.camscanner.paperscanner.pdfcreator.view.activity.setting;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import io.github.rockerhieu.emojicon.EmojiconEditText;
import io.github.rockerhieu.emojicon.emoji.Emojicon;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.Constant;
import com.camscanner.paperscanner.pdfcreator.common.utils.SharedPrefsUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.TagUtils;
import com.camscanner.paperscanner.pdfcreator.view.activity.BaseMainActivity;

public class Setting_MyNameTagActivity extends BaseMainActivity implements View.OnClickListener {

    public EmojiconEditText m_emetNameTemplate;

    public TextView m_tvSample;
    private TextView m_tvTagDay;
    private TextView m_tvTagHour;
    private TextView m_tvTagMinute;
    private TextView m_tvTagMonth;
    private TextView m_tvTagSecond;
    private TextView m_tvTagTag;
    private TextView m_tvTagYear;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_setting_name_tag);
        initUI();
        initVairable();
    }

    public void onBackPressed() {
        if (this.m_emetNameTemplate.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.alert_name_empty, 0).show();
            return;
        }
        SharedPrefsUtils.setNameTemplate(this, this.m_emetNameTemplate.getText().toString());
        super.onBackPressed();
    }


    public void onDestroy() {
        super.onDestroy();
    }


    public void initUI() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle((int) R.string.setting_name_new_doc);
        } catch (Exception e) {
        }
        this.m_tvSample = (TextView) findViewById(R.id.txt_file_name);
        this.m_tvTagYear = (TextView) findViewById(R.id.tv_tag_year);
        this.m_tvTagMonth = (TextView) findViewById(R.id.tv_tag_month);
        this.m_tvTagDay = (TextView) findViewById(R.id.tv_tag_day);
        this.m_tvTagHour = (TextView) findViewById(R.id.tv_tag_hour);
        this.m_tvTagMinute = (TextView) findViewById(R.id.tv_tag_minute);
        this.m_tvTagSecond = (TextView) findViewById(R.id.tv_tag_second);
        this.m_tvTagTag = (TextView) findViewById(R.id.tv_tag_tag);
        this.m_emetNameTemplate = (EmojiconEditText) findViewById(R.id.emet_name_template);
        this.m_emetNameTemplate.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Setting_MyNameTagActivity.this.checkTagStatus();
                Setting_MyNameTagActivity.this.m_tvSample.setText(TagUtils.getNameTemplate(Setting_MyNameTagActivity.this.m_emetNameTemplate.getText().toString(), Setting_MyNameTagActivity.this));
            }

            public void afterTextChanged(Editable s) {
            }
        });
        findViewById(R.id.ivClear).setOnClickListener(this);
        this.m_tvTagYear.setOnClickListener(this);
        this.m_tvTagMonth.setOnClickListener(this);
        this.m_tvTagDay.setOnClickListener(this);
        this.m_tvTagHour.setOnClickListener(this);
        this.m_tvTagMinute.setOnClickListener(this);
        this.m_tvTagSecond.setOnClickListener(this);
        this.m_tvTagTag.setOnClickListener(this);
    }


    public void initVairable() {
        this.m_emetNameTemplate.setText(SharedPrefsUtils.getNameTemplate(this));
        EmojiconEditText emojiconEditText = this.m_emetNameTemplate;
        emojiconEditText.setSelection(emojiconEditText.getText().length());
        checkTagStatus();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 16908332) {
            if (this.m_emetNameTemplate.getText().toString().isEmpty()) {
                Toast.makeText(this, R.string.alert_name_empty, 0).show();
            } else {
                SharedPrefsUtils.setNameTemplate(this, this.m_emetNameTemplate.getText().toString());
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivClear:
                this.m_emetNameTemplate.setText("");
                return;
            case R.id.tv_tag_day:
                this.m_tvTagDay.setVisibility(8);
                inputTag(new Emojicon(Constant.TAG_DAY));
                return;
            case R.id.tv_tag_hour:
                this.m_tvTagHour.setVisibility(8);
                inputTag(new Emojicon(Constant.TAG_HOUR));
                return;
            case R.id.tv_tag_minute:
                this.m_tvTagMinute.setVisibility(8);
                inputTag(new Emojicon(Constant.TAG_MINUTE));
                return;
            case R.id.tv_tag_month:
                this.m_tvTagMonth.setVisibility(8);
                inputTag(new Emojicon(Constant.TAG_MONTH));
                return;
            case R.id.tv_tag_second:
                this.m_tvTagSecond.setVisibility(8);
                inputTag(new Emojicon(Constant.TAG_SECOND));
                return;
            case R.id.tv_tag_tag:
                this.m_tvTagTag.setVisibility(8);
                inputTag(new Emojicon(Constant.TAG_TAG));
                return;
            case R.id.tv_tag_year:
                this.m_tvTagYear.setVisibility(8);
                inputTag(new Emojicon(Constant.TAG_YEAR));
                return;
            default:
                return;
        }
    }

    public void inputTag(Emojicon emojicon) {
        EmojiconEditText emojiconEditText = this.m_emetNameTemplate;
        if (emojiconEditText != null && emojicon != null) {
            int start = emojiconEditText.getSelectionStart();
            int end = this.m_emetNameTemplate.getSelectionEnd();
            if (start < 0) {
                this.m_emetNameTemplate.append(emojicon.getEmoji());
            } else {
                this.m_emetNameTemplate.getText().replace(Math.min(start, end), Math.max(start, end), emojicon.getEmoji(), 0, emojicon.getEmoji().length());
            }
        }
    }

    public void checkTagStatus() {
        String str = this.m_emetNameTemplate.getText().toString();
        if (!str.contains(Constant.TAG_YEAR)) {
            this.m_tvTagYear.setVisibility(0);
        } else {
            this.m_tvTagYear.setVisibility(8);
        }
        if (!str.contains(Constant.TAG_MONTH)) {
            this.m_tvTagMonth.setVisibility(0);
        } else {
            this.m_tvTagMonth.setVisibility(8);
        }
        if (!str.contains(Constant.TAG_DAY)) {
            this.m_tvTagDay.setVisibility(0);
        } else {
            this.m_tvTagDay.setVisibility(8);
        }
        if (!str.contains(Constant.TAG_HOUR)) {
            this.m_tvTagHour.setVisibility(0);
        } else {
            this.m_tvTagHour.setVisibility(8);
        }
        if (!str.contains(Constant.TAG_MINUTE)) {
            this.m_tvTagMinute.setVisibility(0);
        } else {
            this.m_tvTagMinute.setVisibility(8);
        }
        if (!str.contains(Constant.TAG_SECOND)) {
            this.m_tvTagSecond.setVisibility(0);
        } else {
            this.m_tvTagSecond.setVisibility(8);
        }
        if (!str.contains(Constant.TAG_TAG)) {
            this.m_tvTagTag.setVisibility(0);
        } else {
            this.m_tvTagTag.setVisibility(8);
        }
    }
}
