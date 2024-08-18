package com.tapscanner.polygondetect;

import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

public class DetectionResult {
    public static final int SizeOfDetectionResult = 32;
    public PointF[] m_ptfInfo = new PointF[4];
    public Rect m_rtResult;

    public enum FIX_RECT_MODE {
        FIX_RECT_CAMERA,
        FIX_RECT_GALLERY
    }

    public DetectionResult() {
        for (int i = 0; i < 4; i++) {
            this.m_ptfInfo[i] = new PointF();
        }
        this.m_rtResult = new Rect(0, 0, 0, 0);
    }

    public RectF getCoverRect() {
        RectF coverRect = new RectF();
        float left = 1.0f;
        float right = 0.0f;
        float top = 1.0f;
        float bottom = 0.0f;
        for (int i = 0; i < 4; i++) {
            if (this.m_ptfInfo[i].x < left) {
                left = this.m_ptfInfo[i].x;
            }
            if (this.m_ptfInfo[i].x > right) {
                right = this.m_ptfInfo[i].x;
            }
            if (this.m_ptfInfo[i].y < top) {
                top = this.m_ptfInfo[i].y;
            }
            if (this.m_ptfInfo[i].y > bottom) {
                bottom = this.m_ptfInfo[i].y;
            }
        }
        coverRect.set(left, top, right, bottom);
        return coverRect;
    }

    public RectF getResultRect() {
        RectF resultRect = new RectF();
        PointF[] pointFArr = this.m_ptfInfo;
        PointF[] pointFArr2 = this.m_ptfInfo;
        PointF[] pointFArr3 = this.m_ptfInfo;
        float heightLeft = (float) distancePt2Pt(pointFArr3[0], pointFArr3[3]);
        PointF[] pointFArr4 = this.m_ptfInfo;
        resultRect.set(0.0f, 0.0f, (((float) distancePt2Pt(pointFArr[0], pointFArr[1])) + ((float) distancePt2Pt(pointFArr2[2], pointFArr2[3]))) / 2.0f, (heightLeft + ((float) distancePt2Pt(pointFArr4[1], pointFArr4[2]))) / 2.0f);
        return resultRect;
    }

    private static double distancePt2Pt(PointF ptA, PointF ptB) {
        float dx = ptA.x - ptB.x;
        float dy = ptA.y - ptB.y;
        return Math.sqrt((double) ((dx * dx) + (dy * dy)));
    }

    public void saveDetectInfo(PointF[] ptInfo, Rect rect) {
        for (int i = 0; i < 4; i++) {
            this.m_ptfInfo[i].x = ptInfo[i].x;
            this.m_ptfInfo[i].y = ptInfo[i].y;
        }
        this.m_rtResult.top = rect.top;
        this.m_rtResult.left = rect.left;
        this.m_rtResult.right = rect.right;
        this.m_rtResult.bottom = rect.bottom;
    }

    public void fixSmallRect(FIX_RECT_MODE fixRectMode) {
        float top = 1.0f;
        float left = 1.0f;
        float bottom = 0.0f;
        float right = 0.0f;
        float minValue = fixRectMode == FIX_RECT_MODE.FIX_RECT_CAMERA ? 0.05f : 0.001f;
        float maxValue = fixRectMode == FIX_RECT_MODE.FIX_RECT_CAMERA ? 0.95f : 0.999f;
        for (int i = 0; i < 4; i++) {
            if (this.m_ptfInfo[i].x < left) {
                left = this.m_ptfInfo[i].x;
            }
            if (this.m_ptfInfo[i].y < top) {
                top = this.m_ptfInfo[i].y;
            }
            if (this.m_ptfInfo[i].x > right) {
                right = this.m_ptfInfo[i].x;
            }
            if (this.m_ptfInfo[i].y > bottom) {
                bottom = this.m_ptfInfo[i].y;
            }
        }
        float top2 = Math.max(0.0f, Math.min(top, 0.999f));
        float left2 = Math.max(0.0f, Math.min(left, 0.999f));
        float right2 = Math.max(0.0f, Math.min(right, 0.999f));
        float bottom2 = Math.max(0.0f, Math.min(bottom, 0.999f));
        if (right2 - left2 < 0.15f || bottom2 - top2 < 0.15f) {
            PointF[] pointFArr = this.m_ptfInfo;
            pointFArr[0].x = minValue;
            pointFArr[0].y = minValue;
            pointFArr[1].x = maxValue;
            pointFArr[1].y = minValue;
            pointFArr[2].x = maxValue;
            pointFArr[2].y = maxValue;
            pointFArr[3].x = minValue;
            pointFArr[3].y = maxValue;
        }
    }

    public static void ParseFromByte(byte[] pData, DetectionResult result) {
        result.m_ptfInfo[0].x = BaseConvertor.byteArray2Float(pData, 0);
        int nCursor = 0 + 4;
        result.m_ptfInfo[0].y = BaseConvertor.byteArray2Float(pData, nCursor);
        int nCursor2 = nCursor + 4;
        result.m_ptfInfo[1].x = BaseConvertor.byteArray2Float(pData, nCursor2);
        int nCursor3 = nCursor2 + 4;
        result.m_ptfInfo[1].y = BaseConvertor.byteArray2Float(pData, nCursor3);
        int nCursor4 = nCursor3 + 4;
        result.m_ptfInfo[2].x = BaseConvertor.byteArray2Float(pData, nCursor4);
        int nCursor5 = nCursor4 + 4;
        result.m_ptfInfo[2].y = BaseConvertor.byteArray2Float(pData, nCursor5);
        int nCursor6 = nCursor5 + 4;
        result.m_ptfInfo[3].x = BaseConvertor.byteArray2Float(pData, nCursor6);
        int nCursor7 = nCursor6 + 4;
        result.m_ptfInfo[3].y = BaseConvertor.byteArray2Float(pData, nCursor7);
        int nCursor8 = nCursor7 + 4;
    }

    public static void getInfoPtToByte(DetectionResult result, byte[] byDectectInfo) {
        System.arraycopy(BaseConvertor.float2ByteArray(result.m_ptfInfo[0].x), 0, byDectectInfo, 0, 4);
        int nCursor = 0 + 4;
        System.arraycopy(BaseConvertor.float2ByteArray(result.m_ptfInfo[0].y), 0, byDectectInfo, nCursor, 4);
        int nCursor2 = nCursor + 4;
        System.arraycopy(BaseConvertor.float2ByteArray(result.m_ptfInfo[1].x), 0, byDectectInfo, nCursor2, 4);
        int nCursor3 = nCursor2 + 4;
        System.arraycopy(BaseConvertor.float2ByteArray(result.m_ptfInfo[1].y), 0, byDectectInfo, nCursor3, 4);
        int nCursor4 = nCursor3 + 4;
        System.arraycopy(BaseConvertor.float2ByteArray(result.m_ptfInfo[2].x), 0, byDectectInfo, nCursor4, 4);
        int nCursor5 = nCursor4 + 4;
        System.arraycopy(BaseConvertor.float2ByteArray(result.m_ptfInfo[2].y), 0, byDectectInfo, nCursor5, 4);
        int nCursor6 = nCursor5 + 4;
        System.arraycopy(BaseConvertor.float2ByteArray(result.m_ptfInfo[3].x), 0, byDectectInfo, nCursor6, 4);
        int nCursor7 = nCursor6 + 4;
        System.arraycopy(BaseConvertor.float2ByteArray(result.m_ptfInfo[3].y), 0, byDectectInfo, nCursor7, 4);
        int nCursor8 = nCursor7 + 4;
    }

    public static void getInfoRectToByte(DetectionResult result, byte[] byDectectInfo) {
        System.arraycopy(BaseConvertor.float2ByteArray((float) result.m_rtResult.top), 0, byDectectInfo, 0, 4);
        int nCursor = 0 + 4;
        System.arraycopy(BaseConvertor.float2ByteArray((float) result.m_rtResult.left), 0, byDectectInfo, nCursor, 4);
        int nCursor2 = nCursor + 4;
        System.arraycopy(BaseConvertor.float2ByteArray((float) result.m_rtResult.bottom), 0, byDectectInfo, nCursor2, 4);
        int nCursor3 = nCursor2 + 4;
        System.arraycopy(BaseConvertor.float2ByteArray((float) result.m_rtResult.right), 0, byDectectInfo, nCursor3, 4);
        int nCursor4 = nCursor3 + 4;
    }
}
