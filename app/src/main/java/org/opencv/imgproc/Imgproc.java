package org.opencv.imgproc;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

public class Imgproc {


    private static native void cvtColorTwoPlane_0(long j, long j2, long j3, int i);

    private static native void cvtColor_0(long j, long j2, int i, int i2);

    private static native void cvtColor_1(long j, long j2, int i);


    private static native void linearPolar_0(long j, long j2, double d, double d2, double d3, int i);

    private static native void logPolar_0(long j, long j2, double d, double d2, double d3, int i);


    private static native void rectangle_0(long j, double d, double d2, double d3, double d4, double d5, double d6, double d7, double d8, int i, int i2, int i3);

    private static native void rectangle_1(long j, double d, double d2, double d3, double d4, double d5, double d6, double d7, double d8, int i, int i2);

    private static native void rectangle_2(long j, double d, double d2, double d3, double d4, double d5, double d6, double d7, double d8, int i);

    private static native void rectangle_3(long j, double d, double d2, double d3, double d4, double d5, double d6, double d7, double d8);

    private static native void rectangle_4(long j, int i, int i2, int i3, int i4, double d, double d2, double d3, double d4, int i5, int i6, int i7);

    private static native void rectangle_5(long j, int i, int i2, int i3, int i4, double d, double d2, double d3, double d4, int i5, int i6);

    private static native void rectangle_6(long j, int i, int i2, int i3, int i4, double d, double d2, double d3, double d4, int i5);

    private static native void rectangle_7(long j, int i, int i2, int i3, int i4, double d, double d2, double d3, double d4);


    private static native void resize_3(long j, long j2, double d, double d2);



    public static void cvtColor(Mat src, Mat dst, int code, int dstCn) {
        cvtColor_0(src.nativeObj, dst.nativeObj, code, dstCn);
    }

    public static void cvtColor(Mat src, Mat dst, int code) {
        cvtColor_1(src.nativeObj, dst.nativeObj, code);
    }

    public static void cvtColorTwoPlane(Mat src1, Mat src2, Mat dst, int code) {
        cvtColorTwoPlane_0(src1.nativeObj, src2.nativeObj, dst.nativeObj, code);
    }


    @Deprecated
    public static void linearPolar(Mat src, Mat dst, Point center, double maxRadius, int flags) {
        Point point = center;
        linearPolar_0(src.nativeObj, dst.nativeObj, point.f14653x, point.f14654y, maxRadius, flags);
    }

    @Deprecated
    public static void logPolar(Mat src, Mat dst, Point center, double M, int flags) {
        Point point = center;
        logPolar_0(src.nativeObj, dst.nativeObj, point.f14653x, point.f14654y, M, flags);
    }


    public static void rectangle(Mat img, Point pt1, Point pt2, Scalar color, int thickness, int lineType, int shift) {
        Point point = pt1;
        Point point2 = pt2;
        Scalar scalar = color;
        rectangle_0(img.nativeObj, point.f14653x, point.f14654y, point2.f14653x, point2.f14654y, scalar.val[0], scalar.val[1], scalar.val[2], scalar.val[3], thickness, lineType, shift);
    }

    public static void rectangle(Mat img, Point pt1, Point pt2, Scalar color, int thickness, int lineType) {
        Point point = pt1;
        Point point2 = pt2;
        Scalar scalar = color;
        rectangle_1(img.nativeObj, point.f14653x, point.f14654y, point2.f14653x, point2.f14654y, scalar.val[0], scalar.val[1], scalar.val[2], scalar.val[3], thickness, lineType);
    }

    public static void rectangle(Mat img, Point pt1, Point pt2, Scalar color, int thickness) {
        Point point = pt1;
        Point point2 = pt2;
        Scalar scalar = color;
        rectangle_2(img.nativeObj, point.f14653x, point.f14654y, point2.f14653x, point2.f14654y, scalar.val[0], scalar.val[1], scalar.val[2], scalar.val[3], thickness);
    }

    public static void rectangle(Mat img, Point pt1, Point pt2, Scalar color) {
        Point point = pt1;
        Point point2 = pt2;
        Scalar scalar = color;
        rectangle_3(img.nativeObj, point.f14653x, point.f14654y, point2.f14653x, point2.f14654y, scalar.val[0], scalar.val[1], scalar.val[2], scalar.val[3]);
    }

    public static void rectangle(Mat img, Rect rec, Scalar color, int thickness, int lineType, int shift) {
        Rect rect = rec;
        Scalar scalar = color;
        rectangle_4(img.nativeObj, rect.f14658x, rect.f14659y, rect.width, rect.height, scalar.val[0], scalar.val[1], scalar.val[2], scalar.val[3], thickness, lineType, shift);
    }

    public static void rectangle(Mat img, Rect rec, Scalar color, int thickness, int lineType) {
        Rect rect = rec;
        Scalar scalar = color;
        rectangle_5(img.nativeObj, rect.f14658x, rect.f14659y, rect.width, rect.height, scalar.val[0], scalar.val[1], scalar.val[2], scalar.val[3], thickness, lineType);
    }

    public static void rectangle(Mat img, Rect rec, Scalar color, int thickness) {
        Rect rect = rec;
        Scalar scalar = color;
        rectangle_6(img.nativeObj, rect.f14658x, rect.f14659y, rect.width, rect.height, scalar.val[0], scalar.val[1], scalar.val[2], scalar.val[3], thickness);
    }

    public static void rectangle(Mat img, Rect rec, Scalar color) {
        Rect rect = rec;
        Scalar scalar = color;
        rectangle_7(img.nativeObj, rect.f14658x, rect.f14659y, rect.width, rect.height, scalar.val[0], scalar.val[1], scalar.val[2], scalar.val[3]);
    }

    public static void resize(Mat src, Mat dst, Size dsize) {
        resize_3(src.nativeObj, dst.nativeObj, dsize.width, dsize.height);
    }

}
