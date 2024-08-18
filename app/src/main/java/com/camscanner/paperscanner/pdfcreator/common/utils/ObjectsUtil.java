package com.camscanner.paperscanner.pdfcreator.common.utils;

public class ObjectsUtil {
    public static boolean equals(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }
}
