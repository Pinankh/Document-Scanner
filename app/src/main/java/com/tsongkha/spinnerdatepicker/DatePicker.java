package com.tsongkha.spinnerdatepicker;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import org.opencv.videoio.Videoio;

import com.camscanner.paperscanner.pdfcreator.R;

public class DatePicker extends FrameLayout {
    private static final String DATE_FORMAT = "MM/dd/yyyy";
    private static final boolean DEFAULT_ENABLED_STATE = true;
    private Calendar defaultDate = new GregorianCalendar(1980, 0, 1);
    private Context mContext;
    /* access modifiers changed from: private */
    public Calendar mCurrentDate;
    private final DateFormat mDateFormat = new SimpleDateFormat(DATE_FORMAT);
    /* access modifiers changed from: private */
    public NumberPicker mDaySpinner;
    private EditText mDaySpinnerInput;
    private boolean mIsDayShown = true;
    private boolean mIsEnabled = true;
    private Calendar mMaxDate;
    private Calendar mMinDate;
    /* access modifiers changed from: private */
    public NumberPicker mMonthSpinner;
    private EditText mMonthSpinnerInput;
    private int mNumberOfMonths;
    private OnDateChangedListener mOnDateChangedListener;
    private LinearLayout mPickerContainer;
    private String[] mShortMonths;
    /* access modifiers changed from: private */
    public Calendar mTempDate;
    /* access modifiers changed from: private */
    public NumberPicker mYearSpinner;
    private EditText mYearSpinnerInput;
    private Calendar maxDate = new GregorianCalendar(2100, 0, 1);
    private Calendar minDate = new GregorianCalendar(Videoio.CAP_FFMPEG, 0, 1);

    public DatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCurrentLocale(Locale.getDefault());
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        inflater.inflate(R.layout.date_picker_container, this, true);
        this.mPickerContainer = (LinearLayout) findViewById(R.id.parent);
        NumberPicker.OnValueChangeListener onChangeListener = new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                DatePicker.this.updateInputState();
                DatePicker.this.mTempDate.setTimeInMillis(DatePicker.this.mCurrentDate.getTimeInMillis());
                if (picker == DatePicker.this.mDaySpinner) {
                    int maxDayOfMonth = DatePicker.this.mTempDate.getActualMaximum(5);
                    if (oldVal == maxDayOfMonth && newVal == 1) {
                        DatePicker.this.mTempDate.add(5, 1);
                    } else if (oldVal == 1 && newVal == maxDayOfMonth) {
                        DatePicker.this.mTempDate.add(5, -1);
                    } else {
                        DatePicker.this.mTempDate.add(5, newVal - oldVal);
                    }
                } else if (picker == DatePicker.this.mMonthSpinner) {
                    if (oldVal == 11 && newVal == 0) {
                        DatePicker.this.mTempDate.add(2, 1);
                    } else if (oldVal == 0 && newVal == 11) {
                        DatePicker.this.mTempDate.add(2, -1);
                    } else {
                        DatePicker.this.mTempDate.add(2, newVal - oldVal);
                    }
                } else if (picker == DatePicker.this.mYearSpinner) {
                    DatePicker.this.mTempDate.set(1, newVal);
                } else {
                    throw new IllegalArgumentException();
                }
                DatePicker datePicker = DatePicker.this;
                datePicker.setDate(datePicker.mTempDate.get(1), DatePicker.this.mTempDate.get(2), DatePicker.this.mTempDate.get(5));
                DatePicker.this.updateSpinners();
                DatePicker.this.notifyDateChanged();
            }
        };
        this.mDaySpinner = (NumberPicker) inflater.inflate(R.layout.number_picker_day_month, this.mPickerContainer, false);
        this.mDaySpinner.setId(R.id.day);
        this.mDaySpinner.setFormatter(new TwoDigitFormatter());
        this.mDaySpinner.setOnLongPressUpdateInterval(100);
        this.mDaySpinner.setOnValueChangedListener(onChangeListener);
        this.mDaySpinnerInput = NumberPickers.findEditText(this.mDaySpinner);
        this.mMonthSpinner = (NumberPicker) inflater.inflate(R.layout.number_picker_day_month, this.mPickerContainer, false);
        this.mMonthSpinner.setId(R.id.month);
        this.mMonthSpinner.setMinValue(0);
        this.mMonthSpinner.setMaxValue(this.mNumberOfMonths - 1);
        this.mMonthSpinner.setDisplayedValues(this.mShortMonths);
        this.mMonthSpinner.setOnLongPressUpdateInterval(200);
        this.mMonthSpinner.setOnValueChangedListener(onChangeListener);
        this.mMonthSpinnerInput = NumberPickers.findEditText(this.mMonthSpinner);
        this.mYearSpinner = (NumberPicker) inflater.inflate(R.layout.number_picker_year, this.mPickerContainer, false);
        this.mYearSpinner.setId(R.id.year);
        this.mYearSpinner.setOnLongPressUpdateInterval(100);
        this.mYearSpinner.setOnValueChangedListener(onChangeListener);
        this.mYearSpinnerInput = NumberPickers.findEditText(this.mYearSpinner);
        this.mCurrentDate.setTimeInMillis(System.currentTimeMillis());
        reorderSpinners();
        if (getImportantForAccessibility() == 0) {
            setImportantForAccessibility(1);
        }
        setMinDate(this.minDate.getTimeInMillis());
        setMaxDate(this.maxDate.getTimeInMillis());
        init(this.mCurrentDate.get(1), this.mCurrentDate.get(2), this.mCurrentDate.get(5), true, (OnDateChangedListener) null);
    }

    public DatePicker(ViewGroup root, int numberPickerStyle) {
        super(root.getContext());
        this.mContext = root.getContext();
        setCurrentLocale(Locale.getDefault());
        LayoutInflater inflater = (LayoutInflater) new ContextThemeWrapper(this.mContext, numberPickerStyle).getSystemService("layout_inflater");
        inflater.inflate(R.layout.date_picker_container, this, true);
        this.mPickerContainer = (LinearLayout) findViewById(R.id.parent);
        NumberPicker.OnValueChangeListener onChangeListener = new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                DatePicker.this.updateInputState();
                DatePicker.this.mTempDate.setTimeInMillis(DatePicker.this.mCurrentDate.getTimeInMillis());
                if (picker == DatePicker.this.mDaySpinner) {
                    int maxDayOfMonth = DatePicker.this.mTempDate.getActualMaximum(5);
                    if (oldVal == maxDayOfMonth && newVal == 1) {
                        DatePicker.this.mTempDate.add(5, 1);
                    } else if (oldVal == 1 && newVal == maxDayOfMonth) {
                        DatePicker.this.mTempDate.add(5, -1);
                    } else {
                        DatePicker.this.mTempDate.add(5, newVal - oldVal);
                    }
                } else if (picker == DatePicker.this.mMonthSpinner) {
                    if (oldVal == 11 && newVal == 0) {
                        DatePicker.this.mTempDate.add(2, 1);
                    } else if (oldVal == 0 && newVal == 11) {
                        DatePicker.this.mTempDate.add(2, -1);
                    } else {
                        DatePicker.this.mTempDate.add(2, newVal - oldVal);
                    }
                } else if (picker == DatePicker.this.mYearSpinner) {
                    DatePicker.this.mTempDate.set(1, newVal);
                } else {
                    throw new IllegalArgumentException();
                }
                DatePicker datePicker = DatePicker.this;
                datePicker.setDate(datePicker.mTempDate.get(1), DatePicker.this.mTempDate.get(2), DatePicker.this.mTempDate.get(5));
                DatePicker.this.updateSpinners();
                DatePicker.this.notifyDateChanged();
            }
        };
        this.mDaySpinner = (NumberPicker) inflater.inflate(R.layout.number_picker_day_month, this.mPickerContainer, false);
        this.mDaySpinner.setId(R.id.day);
        this.mDaySpinner.setFormatter(new TwoDigitFormatter());
        this.mDaySpinner.setOnLongPressUpdateInterval(100);
        this.mDaySpinner.setOnValueChangedListener(onChangeListener);
        this.mDaySpinnerInput = NumberPickers.findEditText(this.mDaySpinner);
        this.mMonthSpinner = (NumberPicker) inflater.inflate(R.layout.number_picker_day_month, this.mPickerContainer, false);
        this.mMonthSpinner.setId(R.id.month);
        this.mMonthSpinner.setMinValue(0);
        this.mMonthSpinner.setMaxValue(this.mNumberOfMonths - 1);
        this.mMonthSpinner.setDisplayedValues(this.mShortMonths);
        this.mMonthSpinner.setOnLongPressUpdateInterval(200);
        this.mMonthSpinner.setOnValueChangedListener(onChangeListener);
        this.mMonthSpinnerInput = NumberPickers.findEditText(this.mMonthSpinner);
        this.mYearSpinner = (NumberPicker) inflater.inflate(R.layout.number_picker_year, this.mPickerContainer, false);
        this.mYearSpinner.setId(R.id.year);
        this.mYearSpinner.setOnLongPressUpdateInterval(100);
        this.mYearSpinner.setOnValueChangedListener(onChangeListener);
        this.mYearSpinnerInput = NumberPickers.findEditText(this.mYearSpinner);
        this.mCurrentDate.setTimeInMillis(System.currentTimeMillis());
        reorderSpinners();
        if (getImportantForAccessibility() == 0) {
            setImportantForAccessibility(1);
        }
        root.addView(this);
    }

    /* access modifiers changed from: package-private */
    public void init(int year, int monthOfYear, int dayOfMonth, boolean isDayShown, OnDateChangedListener onDateChangedListener) {
        this.mIsDayShown = isDayShown;
        setDate(year, monthOfYear, dayOfMonth);
        updateSpinners();
        this.mOnDateChangedListener = onDateChangedListener;
        notifyDateChanged();
    }

    public void setDateChagnedListner(OnDateChangedListener onDateChangedListener) {
        this.mOnDateChangedListener = onDateChangedListener;
    }

    /* access modifiers changed from: package-private */
    public void updateDate(int year, int month, int dayOfMonth) {
        if (isNewDate(year, month, dayOfMonth)) {
            setDate(year, month, dayOfMonth);
            updateSpinners();
            notifyDateChanged();
        }
    }

    /* access modifiers changed from: package-private */
    public int getYear() {
        return this.mCurrentDate.get(1);
    }

    /* access modifiers changed from: package-private */
    public int getMonth() {
        return this.mCurrentDate.get(2);
    }

    /* access modifiers changed from: package-private */
    public int getDayOfMonth() {
        return this.mCurrentDate.get(5);
    }

    /* access modifiers changed from: package-private */
    public void setMinDate(long minDate2) {
        this.mTempDate.setTimeInMillis(minDate2);
        if (this.mTempDate.get(1) != this.mMinDate.get(1) || this.mTempDate.get(6) != this.mMinDate.get(6)) {
            this.mMinDate.setTimeInMillis(minDate2);
            if (this.mCurrentDate.before(this.mMinDate)) {
                this.mCurrentDate.setTimeInMillis(this.mMinDate.getTimeInMillis());
            }
            updateSpinners();
        }
    }

    /* access modifiers changed from: package-private */
    public void setMaxDate(long maxDate2) {
        this.mTempDate.setTimeInMillis(maxDate2);
        if (this.mTempDate.get(1) != this.mMaxDate.get(1) || this.mTempDate.get(6) != this.mMaxDate.get(6)) {
            this.mMaxDate.setTimeInMillis(maxDate2);
            if (this.mCurrentDate.after(this.mMaxDate)) {
                this.mCurrentDate.setTimeInMillis(this.mMaxDate.getTimeInMillis());
            }
            updateSpinners();
        }
    }

    public void setEnabled(boolean enabled) {
        this.mDaySpinner.setEnabled(enabled);
        this.mMonthSpinner.setEnabled(enabled);
        this.mYearSpinner.setEnabled(enabled);
        this.mIsEnabled = enabled;
    }

    public boolean isEnabled() {
        return this.mIsEnabled;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        setCurrentLocale(newConfig.locale);
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        onPopulateAccessibilityEvent(event);
        return true;
    }

    /* access modifiers changed from: protected */
    public void setCurrentLocale(Locale locale) {
        this.mTempDate = getCalendarForLocale(this.mTempDate, locale);
        this.mMinDate = getCalendarForLocale(this.mMinDate, locale);
        this.mMaxDate = getCalendarForLocale(this.mMaxDate, locale);
        this.mCurrentDate = getCalendarForLocale(this.mCurrentDate, locale);
        this.mNumberOfMonths = this.mTempDate.getActualMaximum(2) + 1;
        this.mShortMonths = new DateFormatSymbols().getShortMonths();
        if (usingNumericMonths()) {
            this.mShortMonths = new String[this.mNumberOfMonths];
            for (int i = 0; i < this.mNumberOfMonths; i++) {
                this.mShortMonths[i] = String.format("%d", new Object[]{Integer.valueOf(i + 1)});
            }
        }
    }

    private boolean usingNumericMonths() {
        return Character.isDigit(this.mShortMonths[0].charAt(0));
    }

    private Calendar getCalendarForLocale(Calendar oldCalendar, Locale locale) {
        if (oldCalendar == null) {
            return Calendar.getInstance(locale);
        }
        long currentTimeMillis = oldCalendar.getTimeInMillis();
        Calendar newCalendar = Calendar.getInstance(locale);
        newCalendar.setTimeInMillis(currentTimeMillis);
        return newCalendar;
    }

    private void reorderSpinners() {
        String pattern;
        this.mPickerContainer.removeAllViews();
        if (Build.VERSION.SDK_INT < 18) {
            pattern = getOrderJellyBeanMr2();
        } else {
            pattern = android.text.format.DateFormat.getBestDateTimePattern(Locale.getDefault(), "yyyyMMMdd");
        }
        char[] order = ICU.getDateFormatOrder(pattern);
        int spinnerCount = order.length;
        for (int i = 0; i < spinnerCount; i++) {
            char c = order[i];
            if (c == 'M') {
                this.mPickerContainer.addView(this.mMonthSpinner);
                setImeOptions(this.mMonthSpinner, spinnerCount, i);
            } else if (c == 'd') {
                this.mPickerContainer.addView(this.mDaySpinner);
                setImeOptions(this.mDaySpinner, spinnerCount, i);
            } else if (c == 'y') {
                this.mPickerContainer.addView(this.mYearSpinner);
                setImeOptions(this.mYearSpinner, spinnerCount, i);
            } else {
                throw new IllegalArgumentException(Arrays.toString(order));
            }
        }
    }

    private String getOrderJellyBeanMr2() {
        DateFormat format;
        if (this.mShortMonths[0].startsWith("1")) {
            format = android.text.format.DateFormat.getDateFormat(getContext());
        } else {
            format = android.text.format.DateFormat.getMediumDateFormat(getContext());
        }
        if (format instanceof SimpleDateFormat) {
            return ((SimpleDateFormat) format).toPattern();
        }
        return new String(android.text.format.DateFormat.getDateFormatOrder(getContext()));
    }

    private boolean isNewDate(int year, int month, int dayOfMonth) {
        if (this.mCurrentDate.get(1) == year && this.mCurrentDate.get(2) == month && this.mCurrentDate.get(5) == dayOfMonth) {
            return false;
        }
        return true;
    }

    public void setDate(int year, int month, int dayOfMonth) {
        this.mCurrentDate.set(year, month, dayOfMonth);
        if (this.mCurrentDate.before(this.mMinDate)) {
            this.mCurrentDate.setTimeInMillis(this.mMinDate.getTimeInMillis());
        } else if (this.mCurrentDate.after(this.mMaxDate)) {
            this.mCurrentDate.setTimeInMillis(this.mMaxDate.getTimeInMillis());
        }
    }

    /* access modifiers changed from: private */
    public void updateSpinners() {
        this.mDaySpinner.setVisibility(this.mIsDayShown ? 0 : 8);
        if (this.mCurrentDate.equals(this.mMinDate)) {
            this.mDaySpinner.setMinValue(this.mCurrentDate.get(5));
            this.mDaySpinner.setMaxValue(this.mCurrentDate.getActualMaximum(5));
            this.mDaySpinner.setWrapSelectorWheel(false);
            this.mMonthSpinner.setDisplayedValues((String[]) null);
            this.mMonthSpinner.setMinValue(this.mCurrentDate.get(2));
            this.mMonthSpinner.setMaxValue(this.mCurrentDate.getActualMaximum(2));
            this.mMonthSpinner.setWrapSelectorWheel(false);
        } else if (this.mCurrentDate.equals(this.mMaxDate)) {
            this.mDaySpinner.setMinValue(this.mCurrentDate.getActualMinimum(5));
            this.mDaySpinner.setMaxValue(this.mCurrentDate.get(5));
            this.mDaySpinner.setWrapSelectorWheel(false);
            this.mMonthSpinner.setDisplayedValues((String[]) null);
            this.mMonthSpinner.setMinValue(this.mCurrentDate.getActualMinimum(2));
            this.mMonthSpinner.setMaxValue(this.mCurrentDate.get(2));
            this.mMonthSpinner.setWrapSelectorWheel(false);
        } else {
            this.mDaySpinner.setMinValue(1);
            this.mDaySpinner.setMaxValue(this.mCurrentDate.getActualMaximum(5));
            this.mDaySpinner.setWrapSelectorWheel(true);
            this.mMonthSpinner.setDisplayedValues((String[]) null);
            this.mMonthSpinner.setMinValue(0);
            this.mMonthSpinner.setMaxValue(11);
            this.mMonthSpinner.setWrapSelectorWheel(true);
        }
        this.mMonthSpinner.setDisplayedValues((String[]) Arrays.copyOfRange(this.mShortMonths, this.mMonthSpinner.getMinValue(), this.mMonthSpinner.getMaxValue() + 1));
        this.mYearSpinner.setMinValue(this.mMinDate.get(1));
        this.mYearSpinner.setMaxValue(this.mMaxDate.get(1));
        this.mYearSpinner.setWrapSelectorWheel(false);
        this.mYearSpinner.setValue(this.mCurrentDate.get(1));
        this.mMonthSpinner.setValue(this.mCurrentDate.get(2));
        this.mDaySpinner.setValue(this.mCurrentDate.get(5));
        if (usingNumericMonths()) {
            this.mMonthSpinnerInput.setRawInputType(2);
        }
    }

    /* access modifiers changed from: private */
    public void notifyDateChanged() {
        sendAccessibilityEvent(4);
        OnDateChangedListener onDateChangedListener = this.mOnDateChangedListener;
        if (onDateChangedListener != null) {
            onDateChangedListener.onDateChanged(this, getYear(), getMonth(), getDayOfMonth());
        }
    }

    private void setImeOptions(NumberPicker spinner, int spinnerCount, int spinnerIndex) {
        int imeOptions;
        if (spinnerIndex < spinnerCount - 1) {
            imeOptions = 5;
        } else {
            imeOptions = 6;
        }
        NumberPickers.findEditText(spinner).setImeOptions(imeOptions);
    }

    /* access modifiers changed from: private */
    public void updateInputState() {
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService("input_method");
        if (inputMethodManager == null) {
            return;
        }
        if (inputMethodManager.isActive(this.mYearSpinnerInput)) {
            this.mYearSpinnerInput.clearFocus();
            inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
        } else if (inputMethodManager.isActive(this.mMonthSpinnerInput)) {
            this.mMonthSpinnerInput.clearFocus();
            inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
        } else if (inputMethodManager.isActive(this.mDaySpinnerInput)) {
            this.mDaySpinnerInput.clearFocus();
            inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
        }
    }

    /* access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        return new SavedState(super.onSaveInstanceState(), this.mCurrentDate, this.mMinDate, this.mMaxDate, this.mIsDayShown);
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        this.mCurrentDate = Calendar.getInstance();
        this.mCurrentDate.setTimeInMillis(ss.currentDate);
        this.mMinDate = Calendar.getInstance();
        this.mMinDate.setTimeInMillis(ss.minDate);
        this.mMaxDate = Calendar.getInstance();
        this.mMaxDate.setTimeInMillis(ss.maxDate);
        updateSpinners();
    }

    private static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        final long currentDate;
        final boolean isDaySpinnerShown;
        final long maxDate;
        final long minDate;

        SavedState(Parcelable superState, Calendar currentDate2, Calendar minDate2, Calendar maxDate2, boolean isDaySpinnerShown2) {
            super(superState);
            this.currentDate = currentDate2.getTimeInMillis();
            this.minDate = minDate2.getTimeInMillis();
            this.maxDate = maxDate2.getTimeInMillis();
            this.isDaySpinnerShown = isDaySpinnerShown2;
        }

        private SavedState(Parcel in) {
            super(in);
            this.currentDate = in.readLong();
            this.minDate = in.readLong();
            this.maxDate = in.readLong();
            this.isDaySpinnerShown = in.readByte() != 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeLong(this.currentDate);
            dest.writeLong(this.minDate);
            dest.writeLong(this.maxDate);
            dest.writeByte(this.isDaySpinnerShown ? (byte) 1 : 0);
        }
    }
}
