package com.googlecode.leptonica.android;

public class Scale {

    public enum ScaleType {
        FILL,
        FIT,
        FIT_SHRINK
    }

    private static native long nativeScale(long j, float f, float f2);

    private static native int nativeScaleGeneral(long j, float f, float f2, float f3, int i);

    static {
        System.loadLibrary("pngo");
        System.loadLibrary("lept");
    }

    public static Pix scaleToSize(Pix pixs, int width, int height, ScaleType type) {
        if (pixs != null) {
            float scaleX = ((float) width) / ((float) pixs.getWidth());
            float scaleY = ((float) height) / ((float) pixs.getHeight());
            int i = C42871.$SwitchMap$com$googlecode$leptonica$android$Scale$ScaleType[type.ordinal()];
            if (i != 1) {
                if (i == 2) {
                    scaleX = Math.min(scaleX, scaleY);
                    scaleY = scaleX;
                } else if (i == 3) {
                    scaleX = Math.min(1.0f, Math.min(scaleX, scaleY));
                    scaleY = scaleX;
                }
            }
            return scale(pixs, scaleX, scaleY);
        }
        throw new IllegalArgumentException("Source pix must be non-null");
    }

    /* renamed from: com.googlecode.leptonica.android.Scale$1 */
    static /* synthetic */ class C42871 {
        static final /* synthetic */ int[] $SwitchMap$com$googlecode$leptonica$android$Scale$ScaleType = new int[ScaleType.values().length];

        static {
            try {
                $SwitchMap$com$googlecode$leptonica$android$Scale$ScaleType[ScaleType.FILL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$googlecode$leptonica$android$Scale$ScaleType[ScaleType.FIT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$googlecode$leptonica$android$Scale$ScaleType[ScaleType.FIT_SHRINK.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public static Pix scale(Pix pixs, float scale) {
        return scale(pixs, scale, scale);
    }

    public static Pix scaleWithoutFiltering(Pix pixs, float scale) {
        return new Pix((long) nativeScaleGeneral(pixs.mNativePix, scale, scale, 0.0f, 0));
    }

    public static Pix scale(Pix pixs, float scaleX, float scaleY) {
        if (pixs == null) {
            throw new IllegalArgumentException("Source pix must be non-null");
        } else if (scaleX <= 0.0f) {
            throw new IllegalArgumentException("X scaling factor must be positive");
        } else if (scaleY > 0.0f) {
            return new Pix(nativeScale(pixs.mNativePix, scaleX, scaleY));
        } else {
            throw new IllegalArgumentException("Y scaling factor must be positive");
        }
    }
}
