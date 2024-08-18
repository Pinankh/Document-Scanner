package org.opencv.android;

import android.graphics.Bitmap;

import org.opencv.core.Mat;

public class Utils {
    private static native void nBitmapToMat2(Bitmap bitmap, long j, boolean z);

    private static native void nMatToBitmap2(long j, Bitmap bitmap, boolean z);









    public static void bitmapToMat(Bitmap bmp, Mat mat, boolean unPremultiplyAlpha) {
        if (bmp == null) {
            throw new IllegalArgumentException("bmp == null");
        } else if (mat != null) {
            nBitmapToMat2(bmp, mat.nativeObj, unPremultiplyAlpha);
        } else {
            throw new IllegalArgumentException("mat == null");
        }
    }


    public static void matToBitmap(Mat mat, Bitmap bmp, boolean premultiplyAlpha) {
        if (mat == null) {
            throw new IllegalArgumentException("mat == null");
        } else if (bmp != null) {
            nMatToBitmap2(mat.nativeObj, bmp, premultiplyAlpha);
        } else {
            throw new IllegalArgumentException("bmp == null");
        }
    }

    public static void matToBitmap(Mat mat, Bitmap bmp) {
        matToBitmap(mat, bmp, false);
    }
}
