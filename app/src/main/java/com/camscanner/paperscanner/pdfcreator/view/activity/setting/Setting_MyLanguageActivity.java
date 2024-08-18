package com.camscanner.paperscanner.pdfcreator.view.activity.setting;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.utils.LanguageUtil.LocalManageUtil;
import com.camscanner.paperscanner.pdfcreator.common.utils.LanguageUtil.SPUtil;
import com.camscanner.paperscanner.pdfcreator.model.AppLanguage;
import com.camscanner.paperscanner.pdfcreator.view.activity.BaseMainActivity;
import com.camscanner.paperscanner.pdfcreator.view.activity.login.SplashActivity;
import com.camscanner.paperscanner.pdfcreator.view.adapter.AppLanguageAdapter;

public class Setting_MyLanguageActivity extends BaseMainActivity implements View.OnClickListener {
    private AppLanguageAdapter m_langAdapter;
    private ArrayList<AppLanguage> m_langList;
    private ListView m_lvLanguage;
    private int m_nLanguageMode;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_setting_language);
        initVariable();
        initUI();
        updateUI();
    }

    /* access modifiers changed from: package-private */
    public void initVariable() {
        getLanguageMode();
        this.m_langList = new ArrayList<>();
        this.m_langList.add(new AppLanguage(getString(R.string.setting_language_ar)));
        this.m_langList.add(new AppLanguage(getString(R.string.setting_language_nl)));
        this.m_langList.add(new AppLanguage(getString(R.string.setting_language_en)));
        this.m_langList.add(new AppLanguage(getString(R.string.setting_language_fr)));
        this.m_langList.add(new AppLanguage(getString(R.string.setting_language_de)));
        this.m_langList.add(new AppLanguage(getString(R.string.setting_language_iw)));
        this.m_langList.add(new AppLanguage(getString(R.string.setting_language_in)));
        this.m_langList.add(new AppLanguage(getString(R.string.setting_language_it)));
        this.m_langList.add(new AppLanguage(getString(R.string.setting_language_fa)));
        this.m_langList.add(new AppLanguage(getString(R.string.setting_language_pt)));
        this.m_langList.add(new AppLanguage(getString(R.string.setting_language_ro)));
        this.m_langList.add(new AppLanguage(getString(R.string.setting_language_ru)));
        this.m_langList.add(new AppLanguage(getString(R.string.setting_language_es)));
        this.m_langList.add(new AppLanguage(getString(R.string.setting_language_tr)));
        this.m_langList.add(new AppLanguage(getString(R.string.setting_language_vi)));
    }

    /* access modifiers changed from: package-private */
    public void initUI() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle((int) R.string.setting_language);
        } catch (Exception e) {
        }
        this.m_lvLanguage = (ListView) findViewById(R.id.lv_language);
        this.m_lvLanguage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Setting_MyLanguageActivity.this.setLanguage(position + 1);
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void updateUI() {
        this.m_langAdapter = new AppLanguageAdapter(this, this.m_langList);
        this.m_lvLanguage.setAdapter(this.m_langAdapter);
        setLanguage(this.m_nLanguageMode);
    }

    /* access modifiers changed from: package-private */
    public void getLanguageMode() {
        this.m_nLanguageMode = SPUtil.getInstance(this).getSelectLanguage();
    }

    /* access modifiers changed from: package-private */
    public void setLanguage(int nLanguageMode) {
        this.m_nLanguageMode = nLanguageMode;
        for (int i = 0; i < this.m_langList.size(); i++) {
            this.m_langList.get(i).bSelected = false;
        }
        this.m_langList.get(this.m_nLanguageMode - 1).bSelected = true;
        this.m_langAdapter.notifyDataSetChanged();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_language, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == 16908332) {
            finish();
        } else if (itemId == R.id.action_language_apply) {
            applyLanguage();
        }
        return super.onOptionsItemSelected(item);
    }

    /* access modifiers changed from: package-private */
    public void applyLanguage() {
        LocalManageUtil.getInstance().saveSelectLanguage(this, this.m_nLanguageMode);
        SplashActivity.reStart(this);
    }

    public void onClick(View v) {
    }
}
