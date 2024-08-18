package org.opencv.core;

import java.util.List;
import org.opencv.utils.Converters;

public class Core {

    public static final String VERSION = getVersion();


    public static class MinMaxLocResult {
        public Point maxLoc = new Point();
        public double maxVal = 0.0d;
        public Point minLoc = new Point();
        public double minVal = 0.0d;
    }






    private static native void add_0(long j, long j2, long j3, long j4, int i);

    private static native void add_1(long j, long j2, long j3, long j4);

    private static native void add_2(long j, long j2, long j3);

    private static native void add_3(long j, double d, double d2, double d3, double d4, long j2, long j3, int i);

    private static native void add_4(long j, double d, double d2, double d3, double d4, long j2, long j3);

    private static native void add_5(long j, double d, double d2, double d3, double d4, long j2);



    private static native String getBuildInformation_0();



    private static native int getThreadNum_0();

    private static native long getTickCount_0();

    private static native double getTickFrequency_0();


    private static native void log_0(long j, long j2);


    private static native void max_0(long j, long j2, long j3);

    private static native void max_1(long j, double d, double d2, double d3, double d4, long j2);


    private static native void min_0(long j, long j2, long j3);

    private static native void min_1(long j, double d, double d2, double d3, double d4, long j2);





    private static String getVersion() {
        return "4.0.1";
    }





    public static String getBuildInformation() {
        return getBuildInformation_0();
    }






    public static double getTickFrequency() {
        return getTickFrequency_0();
    }









    @Deprecated
    public static int getThreadNum() {
        return getThreadNum_0();
    }



    public static long getTickCount() {
        return getTickCount_0();
    }




    public static void add(Mat src1, Mat src2, Mat dst, Mat mask, int dtype) {
        add_0(src1.nativeObj, src2.nativeObj, dst.nativeObj, mask.nativeObj, dtype);
    }

    public static void add(Mat src1, Mat src2, Mat dst, Mat mask) {
        add_1(src1.nativeObj, src2.nativeObj, dst.nativeObj, mask.nativeObj);
    }

    public static void add(Mat src1, Mat src2, Mat dst) {
        add_2(src1.nativeObj, src2.nativeObj, dst.nativeObj);
    }

    public static void add(Mat src1, Scalar src2, Mat dst, Mat mask, int dtype) {
        Scalar scalar = src2;
        add_3(src1.nativeObj, scalar.val[0], scalar.val[1], scalar.val[2], scalar.val[3], dst.nativeObj, mask.nativeObj, dtype);
    }

    public static void add(Mat src1, Scalar src2, Mat dst, Mat mask) {
        Scalar scalar = src2;
        add_4(src1.nativeObj, scalar.val[0], scalar.val[1], scalar.val[2], scalar.val[3], dst.nativeObj, mask.nativeObj);
    }

    public static void add(Mat src1, Scalar src2, Mat dst) {
        add_5(src1.nativeObj, src2.val[0], src2.val[1], src2.val[2], src2.val[3], dst.nativeObj);
    }



    public static void log(Mat src, Mat dst) {
        log_0(src.nativeObj, dst.nativeObj);
    }

    public static void max(Mat src1, Mat src2, Mat dst) {
        max_0(src1.nativeObj, src2.nativeObj, dst.nativeObj);
    }

    public static void max(Mat src1, Scalar src2, Mat dst) {
        max_1(src1.nativeObj, src2.val[0], src2.val[1], src2.val[2], src2.val[3], dst.nativeObj);
    }



    public static void min(Mat src1, Mat src2, Mat dst) {
        min_0(src1.nativeObj, src2.nativeObj, dst.nativeObj);
    }

    public static void min(Mat src1, Scalar src2, Mat dst) {
        min_1(src1.nativeObj, src2.val[0], src2.val[1], src2.val[2], src2.val[3], dst.nativeObj);
    }


}
