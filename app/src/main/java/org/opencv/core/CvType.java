package org.opencv.core;


import com.camscanner.paperscanner.pdfcreator.common.Constant;

public final class CvType {
    public static final int CV_16S = 3;
    public static final int CV_16SC1 = CV_16SC(1);
    public static final int CV_16SC2 = CV_16SC(2);
    public static final int CV_16SC3 = CV_16SC(3);
    public static final int CV_16SC4 = CV_16SC(4);
    public static final int CV_16U = 2;
    public static final int CV_16UC1 = CV_16UC(1);
    public static final int CV_16UC2 = CV_16UC(2);
    public static final int CV_16UC3 = CV_16UC(3);
    public static final int CV_16UC4 = CV_16UC(4);
    public static final int CV_32F = 5;
    public static final int CV_32FC1 = CV_32FC(1);
    public static final int CV_32FC2 = CV_32FC(2);
    public static final int CV_32FC3 = CV_32FC(3);
    public static final int CV_32FC4 = CV_32FC(4);
    public static final int CV_32S = 4;
    public static final int CV_32SC1 = CV_32SC(1);
    public static final int CV_32SC2 = CV_32SC(2);
    public static final int CV_32SC3 = CV_32SC(3);
    public static final int CV_32SC4 = CV_32SC(4);
    public static final int CV_64F = 6;
    public static final int CV_64FC1 = CV_64FC(1);
    public static final int CV_64FC2 = CV_64FC(2);
    public static final int CV_64FC3 = CV_64FC(3);
    public static final int CV_64FC4 = CV_64FC(4);
    public static final int CV_8S = 1;
    public static final int CV_8SC1 = CV_8SC(1);
    public static final int CV_8SC2 = CV_8SC(2);
    public static final int CV_8SC3 = CV_8SC(3);
    public static final int CV_8SC4 = CV_8SC(4);
    public static final int CV_8U = 0;
    public static final int CV_8UC1 = CV_8UC(1);
    public static final int CV_8UC2 = CV_8UC(2);
    public static final int CV_8UC3 = CV_8UC(3);
    public static final int CV_8UC4 = CV_8UC(4);
    private static final int CV_CN_MAX = 512;
    private static final int CV_CN_SHIFT = 3;
    private static final int CV_DEPTH_MAX = 8;
    public static final int CV_USRTYPE1 = 7;

    public static final int makeType(int depth, int channels) {
        if (channels <= 0 || channels >= 512) {
            throw new UnsupportedOperationException("Channels count should be 1..511");
        } else if (depth >= 0 && depth < 8) {
            return (depth & 7) + ((channels - 1) << 3);
        } else {
            throw new UnsupportedOperationException("Data type depth should be 0..7");
        }
    }

    public static final int CV_8UC(int ch) {
        return makeType(0, ch);
    }

    public static final int CV_8SC(int ch) {
        return makeType(1, ch);
    }

    public static final int CV_16UC(int ch) {
        return makeType(2, ch);
    }

    public static final int CV_16SC(int ch) {
        return makeType(3, ch);
    }

    public static final int CV_32SC(int ch) {
        return makeType(4, ch);
    }

    public static final int CV_32FC(int ch) {
        return makeType(5, ch);
    }

    public static final int CV_64FC(int ch) {
        return makeType(6, ch);
    }

    public static final int channels(int type) {
        return (type >> 3) + 1;
    }

    public static final int depth(int type) {
        return type & 7;
    }

    public static final boolean isInteger(int type) {
        return depth(type) < 5;
    }

    public static final int ELEM_SIZE(int type) {
        switch (depth(type)) {
            case 0:
            case 1:
                return channels(type);
            case 2:
            case 3:
                return channels(type) * 2;
            case 4:
            case 5:
                return channels(type) * 4;
            case 6:
                return channels(type) * 8;
            default:
                throw new UnsupportedOperationException("Unsupported CvType value: " + type);
        }
    }

    public static final String typeToString(int type) {
        String s;
        switch (depth(type)) {
            case 0:
                s = "CV_8U";
                break;
            case 1:
                s = "CV_8S";
                break;
            case 2:
                s = "CV_16U";
                break;
            case 3:
                s = "CV_16S";
                break;
            case 4:
                s = "CV_32S";
                break;
            case 5:
                s = "CV_32F";
                break;
            case 6:
                s = "CV_64F";
                break;
            case 7:
                s = "CV_USRTYPE1";
                break;
            default:
                throw new UnsupportedOperationException("Unsupported CvType value: " + type);
        }
        int ch = channels(type);
        if (ch <= 4) {
            return s + "C" + ch;
        }
        return s + "C(" + ch + Constant.STR_BRACKET_CLOSE;
    }
}
