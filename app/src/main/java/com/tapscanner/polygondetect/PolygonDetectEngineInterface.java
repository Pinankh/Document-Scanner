package com.tapscanner.polygondetect;

import android.graphics.PointF;
import android.util.Log;

public class PolygonDetectEngineInterface {
    public static PolygonDetectEngine m_instance = new PolygonDetectEngine();
    long m_hEngineHandle = 0;

    public void CreateEngine() {
        PolygonDetectEngine polygonDetectEngine = m_instance;
        this.m_hEngineHandle = PolygonDetectEngine.create();
    }

    public void DestroyEngine() {
        PolygonDetectEngine polygonDetectEngine = m_instance;
        PolygonDetectEngine.destroy(this.m_hEngineHandle);
    }

    public boolean process(long matimage, DetectionResult detectionInfo) {
        byte[] result = new byte[32];
        double t1 = (double) System.currentTimeMillis();
        PolygonDetectEngine polygonDetectEngine = m_instance;
        boolean bResult = PolygonDetectEngine.process(this.m_hEngineHandle, matimage, result);
        double t2 = (double) System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        sb.append("Engine-> ");
        Double.isNaN(t2);
        Double.isNaN(t1);
        sb.append(t2 - t1);
        Log.e("Crop__Time", sb.toString());
        DetectionResult.ParseFromByte(result, detectionInfo);
        return bResult;
    }

    public void cropPerspective(long matimage, DetectionResult detectionInfo) {
        float[] pt = {detectionInfo.m_ptfInfo[0].x, detectionInfo.m_ptfInfo[0].y, detectionInfo.m_ptfInfo[1].x, detectionInfo.m_ptfInfo[1].y, detectionInfo.m_ptfInfo[2].x, detectionInfo.m_ptfInfo[2].y, detectionInfo.m_ptfInfo[3].x, detectionInfo.m_ptfInfo[3].y};
        PolygonDetectEngine polygonDetectEngine = m_instance;
        PolygonDetectEngine.cropPerspective(this.m_hEngineHandle, matimage, pt, detectionInfo.m_rtResult.width(), detectionInfo.m_rtResult.height());
    }

    public void actCropPerspective(long matimage, DetectionResult detectionInfo, PointF[] dis0, int index, int width, int height) {
        DetectionResult detectionResult = detectionInfo;
        float[] src = {detectionResult.m_ptfInfo[0].x, detectionResult.m_ptfInfo[0].y, detectionResult.m_ptfInfo[1].x, detectionResult.m_ptfInfo[1].y, detectionResult.m_ptfInfo[2].x, detectionResult.m_ptfInfo[2].y, detectionResult.m_ptfInfo[3].x, detectionResult.m_ptfInfo[3].y};
        float[] dst = {dis0[0].x, dis0[0].y, dis0[1].x, dis0[1].y, dis0[2].x, dis0[2].y, dis0[3].x, dis0[3].y};
        PolygonDetectEngine polygonDetectEngine = m_instance;
        PolygonDetectEngine.actCropPerspective(this.m_hEngineHandle, matimage, src, dst, index, width, height);
    }

    public void getPerspectiveArray(long matimage, DetectionResult detectionInfo, PointF[][] dst0) {
        DetectionResult detectionResult = detectionInfo;
        float[] src = {detectionResult.m_ptfInfo[0].x, detectionResult.m_ptfInfo[0].y, detectionResult.m_ptfInfo[1].x, detectionResult.m_ptfInfo[1].y, detectionResult.m_ptfInfo[2].x, detectionResult.m_ptfInfo[2].y, detectionResult.m_ptfInfo[3].x, detectionResult.m_ptfInfo[3].y};
        float[] dst = new float[80];
        for (int i = 0; i < 10; i++) {
            dst[(i * 8) + 0] = dst0[i][0].x;
            dst[(i * 8) + 1] = dst0[i][0].y;
            dst[(i * 8) + 2] = dst0[i][1].x;
            dst[(i * 8) + 3] = dst0[i][1].y;
            dst[(i * 8) + 4] = dst0[i][2].x;
            dst[(i * 8) + 5] = dst0[i][2].y;
            dst[(i * 8) + 6] = dst0[i][3].x;
            dst[(i * 8) + 7] = dst0[i][3].y;
        }
        PolygonDetectEngine polygonDetectEngine = m_instance;
        float[] dst2 = dst;
        PolygonDetectEngine.getPerspectiveArray(this.m_hEngineHandle, matimage, src, dst);
        for (int i2 = 0; i2 < 10; i2++) {
            dst0[i2][0].x = dst2[(i2 * 8) + 0];
            dst0[i2][0].y = dst2[(i2 * 8) + 1];
            dst0[i2][1].x = dst2[(i2 * 8) + 2];
            dst0[i2][1].y = dst2[(i2 * 8) + 3];
            dst0[i2][2].x = dst2[(i2 * 8) + 4];
            dst0[i2][2].y = dst2[(i2 * 8) + 5];
            dst0[i2][3].x = dst2[(i2 * 8) + 6];
            dst0[i2][3].y = dst2[(i2 * 8) + 7];
        }
    }

    public void autoBrightContrast(long matImage, float clipHistPercent) {
        PolygonDetectEngine polygonDetectEngine = m_instance;
        PolygonDetectEngine.autoBrightContrast(this.m_hEngineHandle, matImage, clipHistPercent);
    }

    public void adjustBrightContrast(long matImage, int nBright, int nContrast) {
        PolygonDetectEngine polygonDetectEngine = m_instance;
        PolygonDetectEngine.adjustBrightContrast(this.m_hEngineHandle, matImage, nBright, nContrast);
    }

    public void binarization(long matImage, int mode) {
        PolygonDetectEngine polygonDetectEngine = m_instance;
        PolygonDetectEngine.binarization(this.m_hEngineHandle, matImage, mode);
    }

    public void convertGray(long matImage) {
        PolygonDetectEngine polygonDetectEngine = m_instance;
        PolygonDetectEngine.convertGray(this.m_hEngineHandle, matImage);
    }

    public void lighten(long matImage) {
        PolygonDetectEngine polygonDetectEngine = m_instance;
        PolygonDetectEngine.lighten(this.m_hEngineHandle, matImage);
    }

    public void magicColor(long matImage) {
        PolygonDetectEngine polygonDetectEngine = m_instance;
        PolygonDetectEngine.magicColor(this.m_hEngineHandle, matImage);
    }

    public void removeShadow(long matIamge) {
        PolygonDetectEngine polygonDetectEngine = m_instance;
        PolygonDetectEngine.removeShadow(this.m_hEngineHandle, matIamge);
    }
}
