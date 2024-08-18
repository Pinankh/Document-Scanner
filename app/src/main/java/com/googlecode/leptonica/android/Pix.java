package com.googlecode.leptonica.android;

import android.graphics.Rect;

public class Pix {
    public static final int INDEX_D = 2;
    public static final int INDEX_H = 1;
    public static final int INDEX_W = 0;
    final long mNativePix;
    private boolean mRecycled;

    private static native long nativeClone(long j);

    private static native long nativeCopy(long j);

    private static native long nativeCreateFromData(byte[] bArr, int i, int i2, int i3);

    private static native long nativeCreatePix(int i, int i2, int i3);

    private static native void nativeDestroy(long j);

    private static native boolean nativeGetData(long j, byte[] bArr);

    private static native int nativeGetDataSize(long j);

    private static native int nativeGetDepth(long j);

    private static native boolean nativeGetDimensions(long j, int[] iArr);

    private static native int nativeGetHeight(long j);

    private static native int nativeGetPixel(long j, int i, int i2);

    private static native int nativeGetRefCount(long j);

    private static native int nativeGetWidth(long j);

    private static native boolean nativeInvert(long j);

    private static native void nativeSetPixel(long j, int i, int i2, int i3);

    static {
        System.loadLibrary("pngo");
        System.loadLibrary("lept");
    }

    public Pix(long nativePix) {
        this.mNativePix = nativePix;
        this.mRecycled = false;
    }

    public Pix(int width, int height, int depth) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Pix width and height must be > 0");
        } else if (depth == 1 || depth == 2 || depth == 4 || depth == 8 || depth == 16 || depth == 24 || depth == 32) {
            this.mNativePix = nativeCreatePix(width, height, depth);
            this.mRecycled = false;
        } else {
            throw new IllegalArgumentException("Depth must be one of 1, 2, 4, 8, 16, or 32");
        }
    }

    public long getNativePix() {
        return this.mNativePix;
    }

    public byte[] getData() {
        byte[] buffer = new byte[nativeGetDataSize(this.mNativePix)];
        if (nativeGetData(this.mNativePix, buffer)) {
            return buffer;
        }
        throw new RuntimeException("native getData failed");
    }

    public int[] getDimensions() {
        int[] dimensions = new int[4];
        if (getDimensions(dimensions)) {
            return dimensions;
        }
        return null;
    }

    public boolean getDimensions(int[] dimensions) {
        return nativeGetDimensions(this.mNativePix, dimensions);
    }

    public Pix clone() {
        long nativePix = nativeClone(this.mNativePix);
        if (nativePix != 0) {
            return new Pix(nativePix);
        }
        throw new OutOfMemoryError();
    }

    public Pix copy() {
        long nativePix = nativeCopy(this.mNativePix);
        if (nativePix != 0) {
            return new Pix(nativePix);
        }
        throw new OutOfMemoryError();
    }

    public boolean invert() {
        return nativeInvert(this.mNativePix);
    }

    public void recycle() {
        if (!this.mRecycled) {
            nativeDestroy(this.mNativePix);
            this.mRecycled = true;
        }
    }

    public static Pix createFromPix(byte[] pixData, int width, int height, int depth) {
        long nativePix = nativeCreateFromData(pixData, width, height, depth);
        if (nativePix != 0) {
            return new Pix(nativePix);
        }
        throw new OutOfMemoryError();
    }

    public Rect getRect() {
        return new Rect(0, 0, getWidth(), getHeight());
    }

    public int getWidth() {
        return nativeGetWidth(this.mNativePix);
    }

    public int getHeight() {
        return nativeGetHeight(this.mNativePix);
    }

    public int getDepth() {
        return nativeGetDepth(this.mNativePix);
    }

    public int getRefCount() {
        return nativeGetRefCount(this.mNativePix);
    }

    public int getPixel(int x, int y) {
        if (x < 0 || x >= getWidth()) {
            throw new IllegalArgumentException("Supplied x coordinate exceeds image bounds");
        } else if (y >= 0 && y < getHeight()) {
            return nativeGetPixel(this.mNativePix, x, y);
        } else {
            throw new IllegalArgumentException("Supplied x coordinate exceeds image bounds");
        }
    }

    public void setPixel(int x, int y, int color) {
        if (x < 0 || x >= getWidth()) {
            throw new IllegalArgumentException("Supplied x coordinate exceeds image bounds");
        } else if (y < 0 || y >= getHeight()) {
            throw new IllegalArgumentException("Supplied x coordinate exceeds image bounds");
        } else {
            nativeSetPixel(this.mNativePix, x, y, color);
        }
    }
}
