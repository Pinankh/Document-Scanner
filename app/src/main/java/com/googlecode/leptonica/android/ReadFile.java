package com.googlecode.leptonica.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;

public class ReadFile {
    private static final String LOG_TAG = ReadFile.class.getSimpleName();

    private static native long nativeReadBitmap(Bitmap bitmap);

    private static native long nativeReadBytes8(byte[] bArr, int i, int i2);

    private static native long nativeReadFile(String str);

    private static native long nativeReadFiles(String str, String str2);

    private static native long nativeReadMem(byte[] bArr, int i);

    private static native boolean nativeReplaceBytes8(long j, byte[] bArr, int i, int i2);

    static {
        System.loadLibrary("pngo");
        System.loadLibrary("lept");
    }

    public static Pix readMem(byte[] encodedData, Context ctx) {
        if (encodedData == null) {
            return null;
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bmp = BitmapFactory.decodeByteArray(encodedData, 0, encodedData.length, opts);
        Pix pix = readBitmap(bmp);
        bmp.recycle();
        return pix;
    }

    public static Pix readBytes8(byte[] pixelData, int width, int height) {
        if (pixelData == null) {
            throw new IllegalArgumentException("Byte array must be non-null");
        } else if (width <= 0) {
            throw new IllegalArgumentException("Image width must be greater than 0");
        } else if (height <= 0) {
            throw new IllegalArgumentException("Image height must be greater than 0");
        } else if (pixelData.length >= width * height) {
            long nativePix = nativeReadBytes8(pixelData, width, height);
            if (nativePix != 0) {
                return new Pix(nativePix);
            }
            throw new RuntimeException("Failed to read pix from memory");
        } else {
            throw new IllegalArgumentException("Array length does not match dimensions");
        }
    }

    public static boolean replaceBytes8(Pix pixs, byte[] pixelData, int width, int height) {
        if (pixs == null) {
            throw new IllegalArgumentException("Source pix must be non-null");
        } else if (pixelData == null) {
            throw new IllegalArgumentException("Byte array must be non-null");
        } else if (width <= 0) {
            throw new IllegalArgumentException("Image width must be greater than 0");
        } else if (height <= 0) {
            throw new IllegalArgumentException("Image height must be greater than 0");
        } else if (pixelData.length < width * height) {
            throw new IllegalArgumentException("Array length does not match dimensions");
        } else if (pixs.getWidth() != width) {
            throw new IllegalArgumentException("Source pix width does not match image width");
        } else if (pixs.getHeight() == height) {
            return nativeReplaceBytes8(pixs.mNativePix, pixelData, width, height);
        } else {
            throw new IllegalArgumentException("Source pix width does not match image width");
        }
    }

    public static Pix readFile(File file) {
        return readFile((Context) null, file);
    }

    public static Pix readFile(Context context, File file) {
        if (file == null) {
            Log.w(LOG_TAG, "File must be non-null");
            return null;
        } else if (!file.exists()) {
            Log.w(LOG_TAG, "File does not exist");
            return null;
        } else if (file.canRead()) {
            return loadWithPicasso(context, file);
        } else {
            Log.w(LOG_TAG, "Cannot read file");
            return null;
        }
    }

    public static Pix loadWithPicasso(Context context, File file) {
        Bitmap bmp = BitmapFactory.decodeFile(file.getPath());
        if (bmp == null) {
            return null;
        }
        Pix pix = readBitmap(bmp);
        bmp.recycle();
        return pix;
    }


    public static Pix readBitmap(Bitmap bmp) {
        if (bmp == null) {
            Log.w(LOG_TAG, "Bitmap must be non-null");
            return null;
        } else if (bmp.getConfig() != Bitmap.Config.ARGB_8888) {
            Log.w(LOG_TAG, "Bitmap config must be ARGB_8888");
            return null;
        } else {
            long nativePix = nativeReadBitmap(bmp);
            if (nativePix != 0) {
                return new Pix(nativePix);
            }
            Log.w(LOG_TAG, "Failed to read pix from bitmap");
            return null;
        }
    }
}
