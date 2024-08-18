package com.camscanner.paperscanner.pdfcreator.common.utils;

import android.os.Handler;

public class ActivityUtils {

    public interface OnVisibleChecker {
        boolean isVisible();
    }

    public interface OnVisibleListener {
        void onVisible();
    }

    public static void waitVisibleFor(final OnVisibleChecker checker, final OnVisibleListener listener) {
        final Handler waiter = new Handler();
        waiter.postDelayed(new Runnable() {
            public void run() {
                if (checker.isVisible()) {
                    Handler handler = waiter;
                    OnVisibleListener onVisibleListener = listener;
                    onVisibleListener.getClass();
                    handler.postDelayed(new Runnable() {
                        public final void run() {
                            listener.onVisible();
                        }
                    }, 16);
                    return;
                }
                waiter.postDelayed(this, 32);
            }
        }, 32);
    }
}
