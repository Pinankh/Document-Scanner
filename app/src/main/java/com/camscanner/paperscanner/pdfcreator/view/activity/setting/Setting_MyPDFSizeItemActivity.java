package com.camscanner.paperscanner.pdfcreator.view.activity.setting;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.Constant;
import com.camscanner.paperscanner.pdfcreator.common.p018db.DBManager;
import com.camscanner.paperscanner.pdfcreator.model.PDFSize;
import com.camscanner.paperscanner.pdfcreator.view.activity.BaseMainActivity;

public class Setting_MyPDFSizeItemActivity extends BaseMainActivity {
    private EditText m_etPDFSizeHeight;
    private EditText m_etPDFSizeName;
    private EditText m_etPDFSizeWidth;
    private int m_nSaveMode;
    private PDFSize m_pdfSize;
    private Toolbar toolbar;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_setting_pdfsize_item);
        initVariable();
        initUI();
        updateUI();
    }

    /* access modifiers changed from: package-private */
    public void initVariable() {
        this.m_pdfSize = (PDFSize) getIntent().getSerializableExtra(Constant.EXTRA_PDF_SIZE_ITEM);
        this.m_nSaveMode = getIntent().getIntExtra(Constant.EXTRA_PDF_SIZE_MODE, 1);
    }

    /* access modifiers changed from: package-private */
    public void initUI() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle((int) R.string.setting_pdf_size);
            toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        }
        this.m_etPDFSizeName = (EditText) findViewById(R.id.et_pdf_size_item_name);
        this.m_etPDFSizeWidth = (EditText) findViewById(R.id.et_pdf_size_item_width);
        this.m_etPDFSizeHeight = (EditText) findViewById(R.id.et_pdf_size_item_height);
    }

    /* access modifiers changed from: package-private */
    public void updateUI() {
        if (this.m_nSaveMode == 2) {
            this.m_etPDFSizeName.setText(this.m_pdfSize.name);
            this.m_etPDFSizeWidth.setText(String.valueOf(this.m_pdfSize.pxWidth));
            this.m_etPDFSizeHeight.setText(String.valueOf(this.m_pdfSize.pxHeight));
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pdf_size_item, menu);
        for(int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString spanString = new SpannableString(menu.getItem(i).getTitle().toString());
            spanString.setSpan(new ForegroundColorSpan(Color.WHITE), 0,     spanString.length(), 0); //fix the color to white
            item.setTitle(spanString);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == 16908332) {
            finish();
        } else if (itemId == R.id.action_pdf_size_save) {
            savePDFSize();
        }
        return super.onOptionsItemSelected(item);
    }

    /* access modifiers changed from: package-private */
    public void savePDFSize() {
        if (this.m_etPDFSizeName.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.alert_name_empty, 0).show();
        } else if (this.m_etPDFSizeWidth.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.alert_width_empty, 0).show();
        } else if (this.m_etPDFSizeHeight.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.alert_height_empty, 0).show();
        } else {
            int pdfWidth = Integer.parseInt(this.m_etPDFSizeWidth.getText().toString());
            int pdfHeight = Integer.parseInt(this.m_etPDFSizeHeight.getText().toString());
            if (pdfWidth < 3 || pdfWidth > 2048) {
                Toast.makeText(this, R.string.alert_width_range, 0).show();
            } else if (pdfHeight < 3 || pdfHeight > 2048) {
                Toast.makeText(this, R.string.alert_height_range, 0).show();
            } else {
                if (this.m_pdfSize == null) {
                    this.m_pdfSize = new PDFSize();
                }
                this.m_pdfSize.name = this.m_etPDFSizeName.getText().toString();
                this.m_pdfSize.pxWidth = Integer.parseInt(this.m_etPDFSizeWidth.getText().toString());
                this.m_pdfSize.pxHeight = Integer.parseInt(this.m_etPDFSizeHeight.getText().toString());
                int i = this.m_nSaveMode;
                if (i == 1) {
                    DBManager.getInstance().addPDFSize(this.m_pdfSize);
                } else if (i == 2) {
                    DBManager.getInstance().updatePDFSize(this.m_pdfSize);
                }
                setResult(-1);
                finish();
            }
        }
    }
}
