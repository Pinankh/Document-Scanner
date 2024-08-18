package com.camscanner.paperscanner.pdfcreator.common.utils;

import android.content.Context;
import android.os.Handler;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

public class DeviceUtil {
    public static void closeKeyboard(Context context, EditText editText) {
        ((InputMethodManager) context.getSystemService("input_method")).hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static void closeKeyboard(Context context, MaterialSearchView searchView) {
        ((InputMethodManager) context.getSystemService("input_method")).hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }

    public static void showKeyboard(final Context context, final EditText editText) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                ((InputMethodManager) context.getSystemService("input_method")).showSoftInput(editText, 0);
            }
        }, 100);
    }
}
