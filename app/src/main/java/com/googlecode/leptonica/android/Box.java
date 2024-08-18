package com.googlecode.leptonica.android;

import android.graphics.Point;

public class Box {
    public static final int INDEX_H = 3;
    public static final int INDEX_W = 2;
    public static final int INDEX_X = 0;
    public static final int INDEX_Y = 1;
    private final long mNativeBox;
    private boolean mRecycled = false;

    private static native long nativeCreate(int i, int i2, int i3, int i4);

    private static native void nativeDestroy(long j);

    private static native boolean nativeGetGeometry(long j, int[] iArr);

    private static native int nativeGetHeight(long j);

    private static native int nativeGetWidth(long j);

    private static native int nativeGetX(long j);

    private static native int nativeGetY(long j);

    static {
        System.loadLibrary("pngo");
        System.loadLibrary("lept");
    }

    public Box(long nativeBox) {
        this.mNativeBox = nativeBox;
        this.mRecycled = false;
    }

    public Box(int x, int y, int w, int h) {
        long nativeBox = nativeCreate(x, y, w, h);
        if (nativeBox != 0) {
            this.mNativeBox = nativeBox;
            this.mRecycled = false;
            return;
        }
        throw new OutOfMemoryError();
    }

    public Point getCenter() {
        int[] g = new int[4];
        getGeometry(g);
        return new Point(g[0] + (g[2] / 2), g[1] + (g[3] / 2));
    }

    public int getX() {
        return nativeGetX(this.mNativeBox);
    }

    public int getY() {
        return nativeGetY(this.mNativeBox);
    }

    public int getWidth() {
        return nativeGetWidth(this.mNativeBox);
    }

    public int getHeight() {
        return nativeGetHeight(this.mNativeBox);
    }

    public int[] getGeometry() {
        int[] geometry = new int[4];
        if (getGeometry(geometry)) {
            return geometry;
        }
        return null;
    }

    public boolean getGeometry(int[] geometry) {
        if (geometry.length >= 4) {
            return nativeGetGeometry(this.mNativeBox, geometry);
        }
        throw new IllegalArgumentException("Geometry array must be at least 4 elements long");
    }

    public void recycle() {
        if (!this.mRecycled) {
            nativeDestroy(this.mNativeBox);
            this.mRecycled = true;
        }
    }

    public long getNativeBox() {
        return this.mNativeBox;
    }
}
