package com.camscanner.paperscanner.pdfcreator.common.utils;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class KeyboardUtils {
    private static final int COUNTER = 125;
    private static final int LATENCY = 16;
    private static final int TIME_FOR_FOCUS = 2000;

    interface OnFocusListener {
        void onFocus();
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) activity.getSystemService("input_method")).hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showKeyboard(Activity activity, View view) {
        if (view.requestFocus()) {
            ((InputMethodManager) activity.getSystemService("input_method")).showSoftInput(view, 2);
        }
    }

    public static void showKeyboardForce(final Activity activity, final View view) {
        waitFocusFor(view, new OnFocusListener() {


            public final void onFocus() {
                KeyboardUtils.showKeyboard(activity, view);
            }
        });
    }

    private static void waitFocusFor(final View view, final OnFocusListener listener) {
        final int[] counter = {0};
        final Handler waiter = new Handler();
        waiter.postDelayed(new Runnable() {
            public void run() {
                if (view.requestFocus()) {
                    Handler handler = waiter;
                    OnFocusListener onFocusListener = listener;
                    onFocusListener.getClass();
                    handler.postDelayed(new Runnable() {
                        public final void run() {
                            listener.onFocus();
                        }
                    }, 16);
                    return;
                }
                int[] iArr = counter;
                if (iArr[0] < 125) {
                    iArr[0] = iArr[0] + 1;
                    waiter.postDelayed(this, 16);
                }
            }
        }, 16);
    }
}
