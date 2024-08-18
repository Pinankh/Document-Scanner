package com.camscanner.paperscanner.pdfcreator.view.activity.setting;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.p018db.DBManager;
import com.camscanner.paperscanner.pdfcreator.common.utils.DeviceUtil;
import com.camscanner.paperscanner.pdfcreator.common.utils.SharedPrefsUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.StringHelper;
import com.camscanner.paperscanner.pdfcreator.model.PDFSize;
import com.camscanner.paperscanner.pdfcreator.view.activity.BaseMainActivity;
import com.camscanner.paperscanner.pdfcreator.view.adapter.PDFSizeSelectAdapter;

public class Setting_MyDocPropertyActivity extends BaseMainActivity implements View.OnClickListener {

    public ArrayList<PDFSize> m_PDFSizeList;

    public PDFSizeSelectAdapter m_PDFSizeSelectAdapter;
    private ImageView m_ivPDFDirectionLandscape;
    private ImageView m_ivPDFDirectionPortrait;

    public ListView m_lvPDFSize;
    private RelativeLayout m_rlPDFDriectionLandscape;
    private RelativeLayout m_rlPDFDriectionPortrait;
    private RelativeLayout m_rlPDFPassword;

    public TextView m_tvPDFPassword;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_setting_doc_property);
        initVariable();
        initUI();
        updateUI();
    }


    public void initVariable() {
        this.m_PDFSizeList = new ArrayList<>();
        DBManager.getInstance().getPDFSize(this.m_PDFSizeList);
    }


    public void initUI() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle((int) R.string.setting_pdf_property);
        }
        this.m_rlPDFPassword = (RelativeLayout) findViewById(R.id.rl_setting_pdf_password);
        this.m_rlPDFDriectionPortrait = (RelativeLayout) findViewById(R.id.rl_setting_pdf_direction_portrait);
        this.m_rlPDFDriectionLandscape = (RelativeLayout) findViewById(R.id.rl_setting_pdf_direction_landscape);
        this.m_tvPDFPassword = (TextView) findViewById(R.id.tv_pdf_password);
        this.m_ivPDFDirectionPortrait = (ImageView) findViewById(R.id.iv_pdf_direction_portrait);
        this.m_ivPDFDirectionLandscape = (ImageView) findViewById(R.id.iv_pdf_direction_ladnscape);
        this.m_lvPDFSize = (ListView) findViewById(R.id.lv_pdfsize);
        this.m_rlPDFPassword.setOnClickListener(this);
        this.m_rlPDFDriectionPortrait.setOnClickListener(this);
        this.m_rlPDFDriectionLandscape.setOnClickListener(this);
        this.m_lvPDFSize.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                for (int i = 0; i < Setting_MyDocPropertyActivity.this.m_PDFSizeList.size(); i++) {
                    ((PDFSize) Setting_MyDocPropertyActivity.this.m_PDFSizeList.get(i)).bSelected = false;
                }
                Setting_MyDocPropertyActivity settingDocPropertyActivity = Setting_MyDocPropertyActivity.this;
                SharedPrefsUtils.setPDFPageSelected(settingDocPropertyActivity, ((PDFSize) settingDocPropertyActivity.m_PDFSizeList.get(position)).name);
                ((PDFSize) Setting_MyDocPropertyActivity.this.m_PDFSizeList.get(position)).bSelected = true;
                Setting_MyDocPropertyActivity.this.m_PDFSizeSelectAdapter.notifyDataSetChanged();
            }
        });
    }


    public void updateUI() {
        if (SharedPrefsUtils.getPDFPassword(this).isEmpty()) {
            this.m_tvPDFPassword.setText(R.string.setting_pdf_set_password);
        } else {
            this.m_tvPDFPassword.setText(R.string.setting_pdf_delete_password);
        }
        int nPDFDirection = SharedPrefsUtils.getPDFDirecttion(this);
        if (nPDFDirection == 1) {
            this.m_ivPDFDirectionPortrait.setVisibility(0);
            this.m_ivPDFDirectionLandscape.setVisibility(8);
        } else if (nPDFDirection == 2) {
            this.m_ivPDFDirectionPortrait.setVisibility(8);
            this.m_ivPDFDirectionLandscape.setVisibility(0);
        }
        String strPDFPageSeleted = SharedPrefsUtils.getPDFPageSelected(this);
        boolean bPDFSizeExist = false;
        int curPos = 0;
        for (int i = 0; i < this.m_PDFSizeList.size(); i++) {
            PDFSize pdfSize = this.m_PDFSizeList.get(i);
            if (pdfSize.name.equals(strPDFPageSeleted)) {
                curPos = i;
                pdfSize.bSelected = true;
                bPDFSizeExist = true;
            } else {
                pdfSize.bSelected = false;
            }
        }
        if (!bPDFSizeExist) {
            PDFSize pdfSize2 = this.m_PDFSizeList.get(0);
            pdfSize2.bSelected = true;
            SharedPrefsUtils.setPDFPageSelected(this, pdfSize2.name);
        }
        this.m_PDFSizeSelectAdapter = new PDFSizeSelectAdapter(this, this.m_PDFSizeList);
        this.m_lvPDFSize.setAdapter(this.m_PDFSizeSelectAdapter);
        if (curPos > 0) {
            final int finalCurPos = curPos;
            this.m_lvPDFSize.post(new Runnable() {
                public void run() {
                    Setting_MyDocPropertyActivity.this.m_lvPDFSize.smoothScrollToPosition(finalCurPos);
                }
            });
        }
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
            case R.id.rl_setting_pdf_direction_landscape:
                changePDFDirection(2);
                return;
            case R.id.rl_setting_pdf_direction_portrait:
                changePDFDirection(1);
                return;
            case R.id.rl_setting_pdf_password:
                showPDFPasswordDlg();
                return;
            default:
                return;
        }
    }


    public void showPDFPasswordDlg() {
        if (SharedPrefsUtils.getPDFPassword(this).isEmpty()) {
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dlg_pdf_password, (ViewGroup) null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle((CharSequence) getString(R.string.setting_pdf_password_title));
            builder.setView(dialogView);
            builder.setCancelable(false);
            final EditText edtPDFPassword = (EditText) dialogView.findViewById(R.id.tv_pdf_password);
            builder.setPositiveButton((int) R.string.str_ok, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    DeviceUtil.closeKeyboard((Context) Setting_MyDocPropertyActivity.this, edtPDFPassword);
                    String tagText = edtPDFPassword.getText().toString();
                    if (StringHelper.isEmpty(tagText)) {
                        Setting_MyDocPropertyActivity settingDocPropertyActivity = Setting_MyDocPropertyActivity.this;
                        Toast.makeText(settingDocPropertyActivity, settingDocPropertyActivity.getString(R.string.alert_pdf_password_emptry), 0).show();
                        return;
                    }
                    SharedPrefsUtils.setPDFPassword(Setting_MyDocPropertyActivity.this, tagText);
                    Setting_MyDocPropertyActivity.this.m_tvPDFPassword.setText(R.string.setting_pdf_delete_password);
                }
            });
            builder.setNegativeButton((CharSequence) getString(R.string.str_cancel), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    DeviceUtil.closeKeyboard((Context) Setting_MyDocPropertyActivity.this, edtPDFPassword);
                    dialog.cancel();
                }
            });
            builder.show();
            DeviceUtil.showKeyboard(this, edtPDFPassword);
            return;
        }
        SharedPrefsUtils.setPDFPassword(this, "");
        this.m_tvPDFPassword.setText(R.string.setting_pdf_set_password);
    }


    public void changePDFDirection(int mode) {
        SharedPrefsUtils.setPDFDirection(this, mode);
        if (mode == 1) {
            this.m_ivPDFDirectionPortrait.setVisibility(0);
            this.m_ivPDFDirectionLandscape.setVisibility(8);
        } else if (mode == 2) {
            this.m_ivPDFDirectionPortrait.setVisibility(8);
            this.m_ivPDFDirectionLandscape.setVisibility(0);
        }
    }
}
