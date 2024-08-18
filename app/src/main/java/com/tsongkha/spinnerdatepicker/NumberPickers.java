package com.tsongkha.spinnerdatepicker;

import android.widget.EditText;
import android.widget.NumberPicker;

public class NumberPickers {
    public static EditText findEditText(NumberPicker np) {
        for (int i = 0; i < np.getChildCount(); i++) {
            if (np.getChildAt(i) instanceof EditText) {
                return (EditText) np.getChildAt(i);
            }
        }
        return null;
    }
}
