package com.camscanner.paperscanner.pdfcreator.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.barteksc.pdfviewpager.PDFViewPager;

import java.util.ArrayList;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.Constant;
import com.camscanner.paperscanner.pdfcreator.common.utils.UriUtils;

public class PDFViewImageActivity extends BaseMainActivity {
    private PDFViewPager m_pdfViewPager;
    private String m_strPdfName;
    private String m_strPdfPath;
    private Toolbar m_toolbar;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_pdfview);
        initVariable();
        if (this.m_strPdfPath == null) {
            Toast.makeText(this, R.string.alert_invalid_pdf, 0).show();
            finish();
            return;
        }
        initUI();
        updateUI();
    }

    /* access modifiers changed from: package-private */
    public void initUI() {
        this.m_toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.m_toolbar);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_long);
        } catch (Exception e) {
        }
        Spannable text = new SpannableString(this.m_strPdfName);
        text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        setTitle(text);
    }

    /* access modifiers changed from: package-private */
    public void initVariable() {
        this.m_strPdfPath = getIntent().getStringExtra(Constant.EXTRA_FILE_NAME);
        String str = this.m_strPdfPath;
        if (str != null) {
            this.m_strPdfName = str.substring(str.lastIndexOf(47) + 1, this.m_strPdfPath.length() - 4);
        }
    }

    /* access modifiers changed from: package-private */
    public void addPDFView(String fileName) {
        try {
            this.m_pdfViewPager = new PDFViewPager((Context) this, fileName);
            ((LinearLayout) findViewById(R.id.ll_containter)).addView(this.m_pdfViewPager);
        } catch (IllegalArgumentException e) {
            finish();
        }
    }

    /* access modifiers changed from: package-private */
    public void updateUI() {
        addPDFView(this.m_strPdfPath);
    }

    public void sharePDF() {
        Intent shareIntent = new Intent("android.intent.action.SEND_MULTIPLE");
        ArrayList<Uri> allFiles = new ArrayList<>();
        shareIntent.setType("application/pdf");
        allFiles.add(UriUtils.getFileUri(this, this.m_strPdfPath));
        shareIntent.addFlags(1);
        shareIntent.putParcelableArrayListExtra("android.intent.extra.STREAM", allFiles);
        shareIntent.putExtra("android.intent.extra.SUBJECT", "");
        shareIntent.putExtra("android.intent.extra.TEXT", "");
        startActivityForResult(Intent.createChooser(shareIntent, getString(R.string.share_chooser_title)), 1010);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pdf, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == 16908332) {
            finish();
        } else if (itemId == R.id.action_share) {
            sharePDF();
        }
        return super.onOptionsItemSelected(item);
    }
}
