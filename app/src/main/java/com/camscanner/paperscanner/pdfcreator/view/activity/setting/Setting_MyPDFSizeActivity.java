package com.camscanner.paperscanner.pdfcreator.view.activity.setting;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.Constant;
import com.camscanner.paperscanner.pdfcreator.common.p018db.DBManager;
import com.camscanner.paperscanner.pdfcreator.model.PDFSize;
import com.camscanner.paperscanner.pdfcreator.view.activity.BaseMainActivity;
import com.camscanner.paperscanner.pdfcreator.view.adapter.PDFSizeAdapter;

public class Setting_MyPDFSizeActivity extends BaseMainActivity implements PDFSizeAdapter.PDFSizeCallback {
    private PDFSizeAdapter m_PDFSizeAdapter;
    private ArrayList<PDFSize> m_PDFSizeList;
    private ListView m_lvPDFSize;
    private Toolbar toolbar;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_setting_pdfsize);
        initUI();
        initVariable();
    }

    /* access modifiers changed from: package-private */
    public void initUI() {
         toolbar = (Toolbar) findViewById(R.id.toolbar);
         toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle((int) R.string.setting_display_pdf);

            toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        }
        this.m_lvPDFSize = (ListView) findViewById(R.id.lv_pdfsize);
        this.m_lvPDFSize.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                Setting_MyPDFSizeActivity.this.lambda$initUI$0$SettingPDFSizeActivity(adapterView, view, i, j);
            }
        });
    }

    public /* synthetic */ void lambda$initUI$0$SettingPDFSizeActivity(AdapterView parent, View view, int position, long id) {
        Intent intent = new Intent(this, Setting_MyPDFSizeItemActivity.class);
        intent.putExtra(Constant.EXTRA_PDF_SIZE_ITEM, this.m_PDFSizeList.get(position));
        intent.putExtra(Constant.EXTRA_PDF_SIZE_MODE, 2);
        startActivityForResult(intent, 1011);
    }

    /* access modifiers changed from: package-private */
    public void initVariable() {
        this.m_PDFSizeList = new ArrayList<>();
        DBManager.getInstance().getPDFSize(this.m_PDFSizeList);
        this.m_PDFSizeAdapter = new PDFSizeAdapter(this, this.m_PDFSizeList, this);
        this.m_lvPDFSize.setAdapter(this.m_PDFSizeAdapter);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1011 && resultCode == -1) {
            ReadPDFSize();
        }
    }

    /* access modifiers changed from: package-private */
    public void ReadPDFSize() {
        this.m_PDFSizeList.clear();
        DBManager.getInstance().getPDFSize(this.m_PDFSizeList);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pdf_size, menu);
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
        } else if (itemId == R.id.action_pdf_size_add) {
            Intent intent = new Intent(this, Setting_MyPDFSizeItemActivity.class);
            intent.putExtra(Constant.EXTRA_PDF_SIZE_MODE, 1);
            startActivityForResult(intent, 1011);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onDeletePDFSize(int position) {
        if (this.m_PDFSizeList.size() == 1) {
            Toast.makeText(this, getString(R.string.alert_pdf_size_delete), Toast.LENGTH_SHORT).show();
            return;
        }
        PDFSize pdfSize = this.m_PDFSizeList.get(position);
        this.m_PDFSizeList.remove(pdfSize);
        DBManager.getInstance().deletePDFSize(pdfSize);
        runOnUiThread(new Runnable() {
            public final void run() {
                Setting_MyPDFSizeActivity.this.lambda$onDeletePDFSize$1$SettingPDFSizeActivity();
            }
        });
    }

    public /* synthetic */ void lambda$onDeletePDFSize$1$SettingPDFSizeActivity() {
        this.m_PDFSizeAdapter.notifyDataSetChanged();
    }
}
