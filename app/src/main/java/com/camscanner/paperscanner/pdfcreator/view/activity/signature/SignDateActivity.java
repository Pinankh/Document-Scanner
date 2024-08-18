package com.camscanner.paperscanner.pdfcreator.view.activity.signature;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.OnDateChangedListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.Constant;
import com.camscanner.paperscanner.pdfcreator.common.utils.SharedPrefsUtils;
import com.camscanner.paperscanner.pdfcreator.model.types.SignColor;
import com.camscanner.paperscanner.pdfcreator.view.activity.BaseMainActivity;

public class SignDateActivity extends BaseMainActivity implements View.OnClickListener, OnDateChangedListener {
    private static final String strTimePattern1 = "MM/dd/yy";
    private static final String strTimePattern2 = "dd/MM/yy";
    private static final String strTimePattern3 = "MM/dd/yyyy";
    private static final String strTimePattern4 = "yyyy/MM/dd";
    private static final String strTimePattern5 = "dd-MMM-yyyy";
    private static final String strTimePattern6 = "dd MMMM yyyy";
    private static final String strTimePattern7 = "EEEE, dd MMMM yyyy";
    private static final String strTimePattern8 = "dd.MM.yy";
    private static final String strTimePattern9 = "dd.MM.yyyy";
    private RelativeLayout btnBlack;
    private RelativeLayout btnBlue;
    private RelativeLayout btnRed;
    private SignColor color;
    private int currentFormat;
    private Calendar mCalendar;
    private Calendar mCurrentDate;
    private ArrayList<String> m_dateFormatArray;
    private DatePicker m_dpPicker;
    ArrayAdapter<String> m_formatAdapter;
    private ArrayList<String> m_formatSampleArray;
    private ImageView m_ivCancel;
    private ImageView m_ivDone;
    private ListView m_lvDateFormat;
    private RelativeLayout m_rlDateFormat;
    private RelativeLayout m_rlPickDate;
    private TextView m_tvPreview;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_sign_date);
        initUI();
        initVariable();

        updateUI();
    }

    /* access modifiers changed from: package-private */
    public void initUI() {
        this.m_ivCancel = (ImageView) findViewById(R.id.iv_date_cancel);
        this.m_ivDone = (ImageView) findViewById(R.id.iv_date_done);
        this.m_tvPreview = (TextView) findViewById(R.id.tv_date_preview);
        this.btnBlue = (RelativeLayout) findViewById(R.id.rl_date_color_blue);
        this.btnRed = (RelativeLayout) findViewById(R.id.rl_date_color_red);
        this.btnBlack = (RelativeLayout) findViewById(R.id.rl_date_color_black);
        this.m_rlPickDate = (RelativeLayout) findViewById(R.id.rl_pick_date);
        this.m_rlDateFormat = (RelativeLayout) findViewById(R.id.rl_date_format);
        this.m_dpPicker = (DatePicker) findViewById(R.id.sdp_picker);
        this.m_lvDateFormat = (ListView) findViewById(R.id.lv_date_format);
        this.m_ivCancel.setOnClickListener(this);
        this.m_ivDone.setOnClickListener(this);
        this.btnBlue.setOnClickListener(this);
        this.btnRed.setOnClickListener(this);
        this.btnBlack.setOnClickListener(this);
        this.m_rlPickDate.setOnClickListener(this);
        this.m_rlDateFormat.setOnClickListener(this);
        this.m_dpPicker.setDateChagnedListner(this);
        this.m_lvDateFormat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                SignDateActivity.this.lambda$initUI$0$SignDateActivity(adapterView, view, i, j);
            }
        });
    }

    public /* synthetic */ void lambda$initUI$0$SignDateActivity(AdapterView parent, View view, int position, long id) {
        setDatePreview(position);
    }

    /* access modifiers changed from: package-private */
    public void initVariable() {
        this.mCurrentDate = Calendar.getInstance();
        this.mCurrentDate.setTimeInMillis(System.currentTimeMillis());
        this.mCalendar = Calendar.getInstance();
        this.mCalendar.setTimeInMillis(System.currentTimeMillis());
        this.m_dateFormatArray = new ArrayList<>();
        this.m_formatSampleArray = new ArrayList<>();
        this.m_dateFormatArray.add(strTimePattern1);
        this.m_dateFormatArray.add(strTimePattern2);
        this.m_dateFormatArray.add(strTimePattern3);
        this.m_dateFormatArray.add(strTimePattern4);
        this.m_dateFormatArray.add(strTimePattern5);
        this.m_dateFormatArray.add(strTimePattern6);
        this.m_dateFormatArray.add(strTimePattern7);
        this.m_dateFormatArray.add(strTimePattern8);
        this.m_dateFormatArray.add(strTimePattern9);
        Iterator<String> it = this.m_dateFormatArray.iterator();
        while (it.hasNext()) {
            this.m_formatSampleArray.add(new SimpleDateFormat(it.next()).format(this.mCalendar.getTime()));
        }
        this.m_formatAdapter = new ArrayAdapter<>(this, R.layout.item_date_format, this.m_formatSampleArray);
    }

    /* access modifiers changed from: package-private */
    public void updateUI() {
        updateColor(SignColor.valueOf(SharedPrefsUtils.getDateColor(this, SignColor.BLACK.value())));
        setTab(0);
        int savedFormat = SharedPrefsUtils.getDateFormat(this, 0);
        if (savedFormat > this.m_dateFormatArray.size()) {
            savedFormat = 0;
            SharedPrefsUtils.setDateFormat(this, 0);
        }
        this.m_lvDateFormat.setAdapter(this.m_formatAdapter);
        this.currentFormat = savedFormat;
        updateDatePreview();
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id != R.id.rl_pick_date) {
            switch (id) {
                case R.id.iv_date_cancel:
                    finish();
                    return;
                case R.id.iv_date_done:
                    Intent intent = new Intent();
                    intent.putExtra(Constant.EXTRA_STR_DATE, this.m_tvPreview.getText().toString());
                    intent.putExtra("color", this.color.value());
                    setResult(-1, intent);
                    finish();
                    return;
                default:
                    switch (id) {
                        case R.id.rl_date_color_black:
                            setColor(SignColor.BLACK);
                            return;
                        case R.id.rl_date_color_blue:
                            setColor(SignColor.BLUE);
                            return;
                        case R.id.rl_date_color_red:
                            setColor(SignColor.RED);
                            return;
                        case R.id.rl_date_format:
                            setTab(1);
                            return;
                        default:
                            return;
                    }
            }
        } else {
            setTab(0);
        }
    }

    /* access modifiers changed from: package-private */
    public void setColor(SignColor color2) {
        updateColor(color2);
        SharedPrefsUtils.setDateColor(this, color2.value());
    }

    static /* synthetic */ class C68701 {
        static final /* synthetic */ int[] MySignColor = new int[SignColor.values().length];

        static {
            try {
                MySignColor[SignColor.BLUE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                MySignColor[SignColor.RED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                MySignColor[SignColor.BLACK.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private void updateColor(SignColor color2) {
        this.color = color2;
        int i = C68701.MySignColor[color2.ordinal()];
        if (i == 1) {
            updateViewSelection(this.btnBlue, R.color.color_signature_blue, this.btnRed, this.btnBlack);
        } else if (i == 2) {
            updateViewSelection(this.btnRed, R.color.color_signature_red, this.btnBlack, this.btnBlue);
        } else if (i == 3) {
            updateViewSelection(this.btnBlack, R.color.color_signature_black, this.btnRed, this.btnBlue);
        }
    }

    private void updateViewSelection(View selected, int color2, View... nonselected) {
        selected.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBGGray));
        this.m_tvPreview.setTextColor(ContextCompat.getColor(this, color2));
        for (View view : nonselected) {
            view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorTransparent));
        }
    }

    /* access modifiers changed from: package-private */
    public void setTab(int mode) {
        if (mode == 0) {
            this.m_dpPicker.setVisibility(0);
            this.m_lvDateFormat.setVisibility(8);
            this.m_rlPickDate.setBackgroundColor(ContextCompat.getColor(this, R.color.color_signature_bg));
            this.m_rlDateFormat.setBackgroundColor(ContextCompat.getColor(this, R.color.colorTransparent));
        } else if (mode == 1) {
            this.m_dpPicker.setVisibility(8);
            this.m_lvDateFormat.setVisibility(0);
            this.m_rlPickDate.setBackgroundColor(ContextCompat.getColor(this, R.color.colorTransparent));
            this.m_rlDateFormat.setBackgroundColor(ContextCompat.getColor(this, R.color.color_signature_bg));
        }
    }

    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.mCalendar.set(year, monthOfYear, dayOfMonth);
        updateDatePreview();
    }

    private void setDatePreview(int position) {
        this.currentFormat = position;
        updateDatePreview();
        SharedPrefsUtils.setDateFormat(this, position);
    }

    private void updateDatePreview() {
        this.m_tvPreview.setText(new SimpleDateFormat(this.m_dateFormatArray.get(this.currentFormat)).format(this.mCalendar.getTime()));
    }
}
