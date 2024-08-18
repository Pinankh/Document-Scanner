package com.camscanner.paperscanner.pdfcreator.common.utils;

import android.os.Build;
import android.view.View;
import java.util.concurrent.atomic.AtomicInteger;

public class ViewUtil {
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    public static int generateId() {
        int result;
        int newValue;
        if (Build.VERSION.SDK_INT >= 17) {
            return View.generateViewId();
        }
        do {
            result = sNextGeneratedId.get();
            newValue = result + 1;
            if (newValue > 16777215) {
                newValue = 1;
            }
        } while (!sNextGeneratedId.compareAndSet(result, newValue));
        return result;
    }
}
