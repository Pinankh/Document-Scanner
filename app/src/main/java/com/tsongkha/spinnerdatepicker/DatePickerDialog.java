package com.tsongkha.spinnerdatepicker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.text.DateFormat;
import java.util.Calendar;

import com.camscanner.paperscanner.pdfcreator.R;

public class DatePickerDialog extends AlertDialog implements DialogInterface.OnClickListener, OnDateChangedListener {
    private static final String DAY = "day";
    private static final String MONTH = "month";
    private static final String TITLE_SHOWN = "title_enabled";
    private static final String YEAR = "year";
    private final OnDateSetListener mCallBack;
    private final DatePicker mDatePicker;
    private boolean mIsDayShown = true;
    private boolean mIsTitleShown = true;
    private final DateFormat mTitleDateFormat;

    public interface OnDateSetListener {
        void onDateSet(DatePicker datePicker, int i, int i2, int i3);
    }

    DatePickerDialog(Context context, int theme, int spinnerTheme, OnDateSetListener callBack, Calendar defaultDate, Calendar minDate, Calendar maxDate, boolean isDayShown, boolean isTitleShown) {
        super(context, theme);
        Context context2 = context;
        Calendar calendar = defaultDate;
        this.mCallBack = callBack;
        this.mTitleDateFormat = DateFormat.getDateInstance(1);
        this.mIsDayShown = isDayShown;
        this.mIsTitleShown = isTitleShown;
        updateTitle(calendar);
        setButton(-1, context2.getText(17039370), this);
        setButton(-2, context2.getText(17039360), (OnClickListener) null);
        View view = ((LayoutInflater) context2.getSystemService("layout_inflater")).inflate(R.layout.date_picker_dialog_container, (ViewGroup) null);
        setView(view);
        this.mDatePicker = new DatePicker((ViewGroup) view, spinnerTheme);
        this.mDatePicker.setMinDate(minDate.getTimeInMillis());
        this.mDatePicker.setMaxDate(maxDate.getTimeInMillis());
        this.mDatePicker.init(calendar.get(1), calendar.get(2), calendar.get(5), isDayShown, this);
    }

    public void onClick(DialogInterface dialog, int which) {
        if (this.mCallBack != null) {
            this.mDatePicker.clearFocus();
            OnDateSetListener onDateSetListener = this.mCallBack;
            DatePicker datePicker = this.mDatePicker;
            onDateSetListener.onDateSet(datePicker, datePicker.getYear(), this.mDatePicker.getMonth(), this.mDatePicker.getDayOfMonth());
        }
    }

    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar updatedDate = Calendar.getInstance();
        updatedDate.set(1, year);
        updatedDate.set(2, monthOfYear);
        updatedDate.set(5, dayOfMonth);
        updateTitle(updatedDate);
    }

    private void updateTitle(Calendar updatedDate) {
        if (this.mIsTitleShown) {
            setTitle(this.mTitleDateFormat.format(updatedDate.getTime()));
        } else {
            setTitle(" ");
        }
    }

    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(YEAR, this.mDatePicker.getYear());
        state.putInt(MONTH, this.mDatePicker.getMonth());
        state.putInt(DAY, this.mDatePicker.getDayOfMonth());
        state.putBoolean(TITLE_SHOWN, this.mIsTitleShown);
        return state;
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int year = savedInstanceState.getInt(YEAR);
        int month = savedInstanceState.getInt(MONTH);
        int day = savedInstanceState.getInt(DAY);
        this.mIsTitleShown = savedInstanceState.getBoolean(TITLE_SHOWN);
        Calendar c = Calendar.getInstance();
        c.set(1, year);
        c.set(2, month);
        c.set(5, day);
        updateTitle(c);
        this.mDatePicker.init(year, month, day, this.mIsDayShown, this);
    }
}
