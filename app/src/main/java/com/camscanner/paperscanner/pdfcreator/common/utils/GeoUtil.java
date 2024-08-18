package com.camscanner.paperscanner.pdfcreator.common.utils;

import android.graphics.Matrix;
import android.graphics.PointF;

public class GeoUtil {
    public static final int LB_X = 6;
    public static final int LB_Y = 7;
    public static final int LT_X = 0;
    public static final int LT_Y = 1;
    public static final int RB_X = 4;
    public static final int RB_Y = 5;
    public static final int RT_X = 2;
    public static final int RT_Y = 3;

    public static double distancePt2Line(float x1, float y1, float x2, float y2, float x3, float y3) {
        float px = x2 - x1;
        float py = y2 - y1;
        float u = (((x3 - x1) * px) + ((y3 - y1) * py)) / ((px * px) + (py * py));
        if (u > 1.0f) {
            u = 1.0f;
        } else if (u < 0.0f) {
            u = 0.0f;
        }
        float dx = ((u * px) + x1) - x3;
        float dy = ((u * py) + y1) - y3;
        return Math.sqrt((double) ((dx * dx) + (dy * dy)));
    }

    public static double distancePt2Line(PointF ptA, PointF ptB, PointF ptDot) {
        return distancePt2Line(ptA.x, ptA.y, ptB.x, ptB.y, ptDot.x, ptDot.y);
    }

    public static double distancePt2Pt(PointF ptA, PointF ptB) {
        float dx = ptA.x - ptB.x;
        float dy = ptA.y - ptB.y;
        return Math.sqrt((double) ((dx * dx) + (dy * dy)));
    }

    public static double getCosVal(PointF ptLineA1, PointF ptLineA2, PointF ptLineB1, PointF ptLineB2) {
        PointF pointF = ptLineA1;
        PointF pointF2 = ptLineA2;
        PointF pointF3 = ptLineB1;
        PointF pointF4 = ptLineB2;
        double dxA = (double) (pointF.x - pointF2.x);
        double dyA = (double) (pointF.y - pointF2.y);
        double dxB = (double) (pointF3.x - pointF4.x);
        double dyB = (double) (pointF3.y - pointF4.y);
        Double.isNaN(dxA);
        Double.isNaN(dxB);
        Double.isNaN(dyA);
        Double.isNaN(dyB);
        double v1 = (dxA * dxB) + (dyA * dyB);
        Double.isNaN(dxA);
        Double.isNaN(dxA);
        Double.isNaN(dyA);
        Double.isNaN(dyA);
        double sqrt = Math.sqrt((dxA * dxA) + (dyA * dyA));
        Double.isNaN(dxB);
        Double.isNaN(dxB);
        Double.isNaN(dyB);
        Double.isNaN(dyB);
        return Math.abs(v1 / (sqrt * Math.sqrt((dxB * dxB) + (dyB * dyB))));
    }

    public static float[] getRotatedCoordsOfView(float left, float width, float top, float height, float degrees) {
        float right = left + width;
        float bottom = top + height;
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees, (left + right) * 0.5f, (top + bottom) * 0.5f);
        float[] points = {left, top, right, top, right, bottom, left, bottom};
        matrix.mapPoints(points);
        return points;
    }

    public static PointF intersection(PointF o1, PointF p1, PointF o2, PointF p2) {
        PointF x = new PointF();
        x.x = o2.x - o1.x;
        x.y = o2.y - o1.y;
        PointF d1 = new PointF();
        d1.x = p1.x - o1.x;
        d1.y = p1.y - o1.y;
        PointF d2 = new PointF();
        d2.x = p2.x - o2.x;
        d2.y = p2.y - o2.y;
        double t1 = (double) (((x.x * d2.y) - (x.y * d2.x)) / ((d1.x * d2.y) - (d1.y * d2.x)));
        double d = (double) o1.x;
        double d3 = (double) d1.x;
        Double.isNaN(d3);
        Double.isNaN(t1);
        Double.isNaN(d);
        double d4 = (double) o1.y;
        double d5 = (double) d1.y;
        Double.isNaN(d5);
        Double.isNaN(t1);
        Double.isNaN(d4);
        return new PointF((float) (d + (d3 * t1)), (float) (d4 + (d5 * t1)));
    }
}
