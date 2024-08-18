package org.opencv.utils;

import java.util.List;
import org.opencv.core.CvType;

import org.opencv.core.Mat;


public class Converters {

    public static Mat vector_Mat_to_Mat(List<Mat> mats) {
        int count = mats != null ? mats.size() : 0;
        if (count <= 0) {
            return new Mat();
        }
        Mat res = new Mat(count, 1, CvType.CV_32SC2);
        int[] buff = new int[(count * 2)];
        for (int i = 0; i < count; i++) {
            long addr = mats.get(i).nativeObj;
            buff[i * 2] = (int) (addr >> 32);
            buff[(i * 2) + 1] = (int) (-1 & addr);
        }
        res.put(0, 0, buff);
        return res;
    }

    public static void Mat_to_vector_Mat(Mat m, List<Mat> mats) {
        if (mats != null) {
            int count = m.rows();
            if (CvType.CV_32SC2 == m.type() && m.cols() == 1) {
                mats.clear();
                int[] buff = new int[(count * 2)];
                m.get(0, 0, buff);
                for (int i = 0; i < count; i++) {
                    mats.add(new Mat((((long) buff[i * 2]) << 32) | (((long) buff[(i * 2) + 1]) & 4294967295L)));
                }
                return;
            }
            throw new IllegalArgumentException("CvType.CV_32SC2 != m.type() ||  m.cols()!=1\n" + m);
        }
        throw new IllegalArgumentException("mats == null");
    }



















}
