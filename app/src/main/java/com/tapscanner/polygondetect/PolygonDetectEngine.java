package com.tapscanner.polygondetect;

public class PolygonDetectEngine {
    public static native void actCropPerspective(long j, long j2, float[] fArr, float[] fArr2, int i, int i2, int i3);

    public static native void adjustBrightContrast(long j, long j2, int i, int i2);

    public static native void autoBrightContrast(long j, long j2, float f);

    public static native void binarization(long j, long j2, int i);

    public static native void convertGray(long j, long j2);

    public static native long create();

    public static native void cropPerspective(long j, long j2, float[] fArr, int i, int i2);

    public static native void destroy(long j);

    public static native void getPerspectiveArray(long j, long j2, float[] fArr, float[] fArr2);

    public static native void lighten(long j, long j2);

    public static native void magicColor(long j, long j2);

    public static native boolean process(long j, long j2, byte[] bArr);

    public static native void removeShadow(long j, long j2);

    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("c++_shared");
        System.loadLibrary("polygon_detect");
    }
}
