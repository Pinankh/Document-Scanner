package com.googlecode.leptonica.android;

public class Binarize {
    public static final float OTSU_SCORE_FRACTION = 0.1f;
    public static final int OTSU_SIZE_X = 32;
    public static final int OTSU_SIZE_Y = 32;
    public static final int OTSU_SMOOTH_X = 2;
    public static final int OTSU_SMOOTH_Y = 2;
    public static final float TILED_FACTOR = 0.35f;
    public static final int TILED_NX_1 = 16;
    public static final int TILED_NX_2 = 32;
    public static final int TILED_NY_1 = 16;
    public static final int TILED_NY_2 = 32;
    public static final int TILED_WH = 64;

    private static native long nativeOtsuAdaptiveThreshold(long j, int i, int i2, int i3, int i4, float f);

    private static native long nativeSauvolaBinarizeTiled(long j, int i, float f, int i2, int i3);

    static {
        System.loadLibrary("pngo");
        System.loadLibrary("lept");
    }

    public static Pix otsuAdaptiveThreshold(Pix pixs) {
        return otsuAdaptiveThreshold(pixs, 32, 32, 2, 2, 0.1f);
    }

    public static Pix otsuAdaptiveThreshold(Pix pixs, int sizeX, int sizeY, int smoothX, int smoothY, float scoreFraction) {
        if (pixs == null) {
            throw new IllegalArgumentException("Source pix must be non-null");
        } else if (pixs.getDepth() == 8) {
            long nativePix = nativeOtsuAdaptiveThreshold(pixs.mNativePix, sizeX, sizeY, smoothX, smoothY, scoreFraction);
            if (nativePix != 0) {
                return new Pix(nativePix);
            }
            throw new RuntimeException("Failed to perform Otsu adaptive threshold on image");
        } else {
            throw new IllegalArgumentException("Source pix depth must be 8bpp");
        }
    }

    public static Pix binarayTiled(Pix pixs, int type) {
        if (pixs == null) {
            throw new IllegalArgumentException("Source pix must be non-null");
        } else if (pixs.getDepth() == 8) {
            int nx = 1;
            int ny = 1;
            if (type == 0) {
                nx = 16;
                ny = 16;
            } else if (type == 1) {
                nx = 32;
                ny = 32;
            }
            long nativePix = nativeSauvolaBinarizeTiled(pixs.mNativePix, 64, 0.35f, nx, ny);
            if (nativePix != 0) {
                return new Pix(nativePix);
            }
            throw new RuntimeException("Failed to perform Otsu adaptive threshold on image");
        } else {
            throw new IllegalArgumentException("Source pix depth must be 8bpp");
        }
    }

    public static Pix sauvolaBinarizeTiled(Pix pixs, int whsize, float factor, int nx, int ny) {
        if (pixs == null) {
            throw new IllegalArgumentException("Source pix must be non-null");
        } else if (pixs.getDepth() == 8) {
            long nativePix = nativeSauvolaBinarizeTiled(pixs.mNativePix, whsize, factor, nx, ny);
            if (nativePix != 0) {
                return new Pix(nativePix);
            }
            throw new RuntimeException("Failed to perform Otsu adaptive threshold on image");
        } else {
            throw new IllegalArgumentException("Source pix depth must be 8bpp");
        }
    }
}
