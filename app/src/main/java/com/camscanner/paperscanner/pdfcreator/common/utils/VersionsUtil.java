package com.camscanner.paperscanner.pdfcreator.common.utils;

import android.os.Build;

public class VersionsUtil {
    public static boolean hasJellyBeanMR1() {
        return Build.VERSION.SDK_INT >= 17;
    }
}
