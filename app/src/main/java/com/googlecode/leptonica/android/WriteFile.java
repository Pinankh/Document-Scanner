package com.googlecode.leptonica.android;

import android.graphics.Bitmap;
import java.io.File;

public class WriteFile {
    public static final boolean DEFAULT_PROGRESSIVE = true;
    public static final int DEFAULT_QUALITY = 85;

    private static native boolean nativeWriteBitmap(long j, Bitmap bitmap);

    private static native int nativeWriteBytes8(long j, byte[] bArr);

    private static native boolean nativeWriteFiles(long j, String str, int i);

    private static native boolean nativeWriteImpliedFormat(long j, String str, int i, boolean z);

    private static native byte[] nativeWriteMem(long j, int i);

    static {
        System.loadLibrary("pngo");
        System.loadLibrary("lept");
    }

    public static byte[] writeBytes8(Pix pixs) {
        if (pixs != null) {
            int size = pixs.getWidth() * pixs.getHeight();
            if (pixs.getDepth() != 8) {
                Pix pix8 = Convert.convertTo8(pixs);
                pixs.recycle();
                pixs = pix8;
            }
            byte[] data = new byte[size];
            writeBytes8(pixs, data);
            return data;
        }
        throw new IllegalArgumentException("Source pix must be non-null");
    }

    public static int writeBytes8(Pix pixs, byte[] data) {
        if (pixs != null) {
            if (data.length >= pixs.getWidth() * pixs.getHeight()) {
                return nativeWriteBytes8(pixs.mNativePix, data);
            }
            throw new IllegalArgumentException("Data array must be large enough to hold image bytes");
        }
        throw new IllegalArgumentException("Source pix must be non-null");
    }

    public static boolean writeFiles(Pixa pixas, File path, String prefix, int format) {
        if (pixas == null) {
            throw new IllegalArgumentException("Source pixa must be non-null");
        } else if (path == null) {
            throw new IllegalArgumentException("Destination path non-null");
        } else if (prefix == null) {
            throw new IllegalArgumentException("Filename prefix must be non-null");
        } else {
            throw new RuntimeException("writeFiles() is not currently supported");
        }
    }

    public static byte[] writeMem(Pix pixs, int format) {
        if (pixs == null) {
            throw new IllegalArgumentException("Source pix must be non-null");
        }
        throw new RuntimeException("writeMem() is not currently supported");
    }

    public static boolean writeImpliedFormat(Pix pixs, File file) {
        return writeImpliedFormat(pixs, file, 85, true);
    }

    public static boolean writeImpliedFormat(Pix pixs, File file, int quality, boolean progressive) {
        if (pixs == null) {
            throw new IllegalArgumentException("Source pix must be non-null");
        } else if (file != null) {
            return nativeWriteImpliedFormat(pixs.mNativePix, file.getAbsolutePath(), quality, progressive);
        } else {
            throw new IllegalArgumentException("File must be non-null");
        }
    }

    public static Bitmap writeBitmap(Pix pixs) throws OutOfMemoryError {
        if (pixs != null) {
            int width = pixs.getWidth();
            int height = pixs.getHeight();
            int[] dimensions = pixs.getDimensions();
            if (dimensions == null) {
                return null;
            }
            Bitmap bitmap = Bitmap.createBitmap(dimensions[0], dimensions[1], Bitmap.Config.ARGB_8888);
            if (nativeWriteBitmap(pixs.mNativePix, bitmap)) {
                return bitmap;
            }
            bitmap.recycle();
            return null;
        }
        throw new IllegalArgumentException("Source pix must be non-null");
    }
}
