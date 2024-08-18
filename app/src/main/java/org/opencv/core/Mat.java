package org.opencv.core;


import com.camscanner.paperscanner.pdfcreator.common.Constant;

import java.nio.ByteBuffer;

public class Mat {
    public final long nativeObj;

    private static native void locateROI_0(long j, double[] dArr, double[] dArr2);

    private static native String nDump(long j);

    private static native double[] nGet(long j, int i, int i2);

    private static native int nGetB(long j, int i, int i2, int i3, byte[] bArr);

    private static native int nGetD(long j, int i, int i2, int i3, double[] dArr);

    private static native int nGetF(long j, int i, int i2, int i3, float[] fArr);

    private static native int nGetI(long j, int i, int i2, int i3, int[] iArr);

    private static native int nGetS(long j, int i, int i2, int i3, short[] sArr);

    private static native int nPutB(long j, int i, int i2, int i3, byte[] bArr);

    private static native int nPutBwOffset(long j, int i, int i2, int i3, int i4, byte[] bArr);

    private static native int nPutD(long j, int i, int i2, int i3, double[] dArr);

    private static native int nPutF(long j, int i, int i2, int i3, float[] fArr);

    private static native int nPutI(long j, int i, int i2, int i3, int[] iArr);

    private static native int nPutS(long j, int i, int i2, int i3, short[] sArr);

    private static native long n_Mat();

    private static native long n_Mat(double d, double d2, int i);

    private static native long n_Mat(double d, double d2, int i, double d3, double d4, double d5, double d6);

    private static native long n_Mat(int i, int i2, int i3);

    private static native long n_Mat(int i, int i2, int i3, double d, double d2, double d3, double d4);

    private static native long n_Mat(int i, int i2, int i3, ByteBuffer byteBuffer);

    private static native long n_Mat(long j, int i, int i2);

    private static native long n_Mat(long j, int i, int i2, int i3, int i4);

    private static native long n_adjustROI(long j, int i, int i2, int i3, int i4);

    private static native void n_assignTo(long j, long j2);

    private static native void n_assignTo(long j, long j2, int i);

    private static native int n_channels(long j);

    private static native int n_checkVector(long j, int i);

    private static native int n_checkVector(long j, int i, int i2);

    private static native int n_checkVector(long j, int i, int i2, boolean z);

    private static native long n_clone(long j);

    private static native long n_col(long j, int i);

    private static native long n_colRange(long j, int i, int i2);

    private static native int n_cols(long j);

    private static native void n_convertTo(long j, long j2, int i);

    private static native void n_convertTo(long j, long j2, int i, double d);

    private static native void n_convertTo(long j, long j2, int i, double d, double d2);

    private static native void n_copyTo(long j, long j2);

    private static native void n_copyTo(long j, long j2, long j3);

    private static native void n_create(long j, double d, double d2, int i);

    private static native void n_create(long j, int i, int i2, int i3);

    private static native long n_cross(long j, long j2);

    private static native long n_dataAddr(long j);

    private static native void n_delete(long j);

    private static native int n_depth(long j);

    private static native long n_diag(long j);

    private static native long n_diag(long j, int i);

    private static native int n_dims(long j);

    private static native double n_dot(long j, long j2);

    private static native long n_elemSize(long j);

    private static native long n_elemSize1(long j);

    private static native boolean n_empty(long j);

    private static native long n_eye(double d, double d2, int i);

    private static native long n_eye(int i, int i2, int i3);

    private static native long n_inv(long j);

    private static native long n_inv(long j, int i);

    private static native boolean n_isContinuous(long j);

    private static native boolean n_isSubmatrix(long j);

    private static native long n_mul(long j, long j2);

    private static native long n_mul(long j, long j2, double d);

    private static native long n_ones(double d, double d2, int i);

    private static native long n_ones(int i, int i2, int i3);

    private static native void n_push_back(long j, long j2);

    private static native void n_release(long j);

    private static native long n_reshape(long j, int i);

    private static native long n_reshape(long j, int i, int i2);

    private static native long n_reshape_1(long j, int i, int i2, int[] iArr);

    private static native long n_row(long j, int i);

    private static native long n_rowRange(long j, int i, int i2);

    private static native int n_rows(long j);

    private static native long n_setTo(long j, double d, double d2, double d3, double d4);

    private static native long n_setTo(long j, double d, double d2, double d3, double d4, long j2);

    private static native long n_setTo(long j, long j2);

    private static native long n_setTo(long j, long j2, long j3);

    private static native double[] n_size(long j);

    private static native int n_size_i(long j, int i);

    private static native long n_step1(long j);

    private static native long n_step1(long j, int i);

    private static native long n_submat(long j, int i, int i2, int i3, int i4);

    private static native long n_submat_rr(long j, int i, int i2, int i3, int i4);

    private static native long n_t(long j);

    private static native long n_total(long j);

    private static native int n_type(long j);

    private static native long n_zeros(double d, double d2, int i);

    private static native long n_zeros(int i, int i2, int i3);

    public Mat(long addr) {
        if (addr != 0) {
            this.nativeObj = addr;
            return;
        }
        throw new UnsupportedOperationException("Native object address is NULL");
    }

    public Mat() {
        this.nativeObj = n_Mat();
    }

    public Mat(int rows, int cols, int type) {
        this.nativeObj = n_Mat(rows, cols, type);
    }

    public Mat(int rows, int cols, int type, ByteBuffer data) {
        this.nativeObj = n_Mat(rows, cols, type, data);
    }

    public Mat(Size size, int type) {
        this.nativeObj = n_Mat(size.width, size.height, type);
    }

    public Mat(int rows, int cols, int type, Scalar s) {
        Scalar scalar = s;
        this.nativeObj = n_Mat(rows, cols, type, scalar.val[0], scalar.val[1], scalar.val[2], scalar.val[3]);
    }

    public Mat(Size size, int type, Scalar s) {
        Size size2 = size;
        Scalar scalar = s;
        this.nativeObj = n_Mat(size2.width, size2.height, type, scalar.val[0], scalar.val[1], scalar.val[2], scalar.val[3]);
    }

    public Mat(Mat m, Range rowRange, Range colRange) {
        this.nativeObj = n_Mat(m.nativeObj, rowRange.start, rowRange.end, colRange.start, colRange.end);
    }

    public Mat(Mat m, Range rowRange) {
        this.nativeObj = n_Mat(m.nativeObj, rowRange.start, rowRange.end);
    }

    public Mat(Mat m, Rect roi) {
        this.nativeObj = n_Mat(m.nativeObj, roi.f14659y, roi.f14659y + roi.height, roi.f14658x, roi.f14658x + roi.width);
    }

    public Mat adjustROI(int dtop, int dbottom, int dleft, int dright) {
        return new Mat(n_adjustROI(this.nativeObj, dtop, dbottom, dleft, dright));
    }

    public void assignTo(Mat m, int type) {
        n_assignTo(this.nativeObj, m.nativeObj, type);
    }

    public void assignTo(Mat m) {
        n_assignTo(this.nativeObj, m.nativeObj);
    }

    public int channels() {
        return n_channels(this.nativeObj);
    }

    public int checkVector(int elemChannels, int depth, boolean requireContinuous) {
        return n_checkVector(this.nativeObj, elemChannels, depth, requireContinuous);
    }

    public int checkVector(int elemChannels, int depth) {
        return n_checkVector(this.nativeObj, elemChannels, depth);
    }

    public int checkVector(int elemChannels) {
        return n_checkVector(this.nativeObj, elemChannels);
    }

    public Mat clone() {
        return new Mat(n_clone(this.nativeObj));
    }

    public Mat col(int x) {
        return new Mat(n_col(this.nativeObj, x));
    }

    public Mat colRange(int startcol, int endcol) {
        return new Mat(n_colRange(this.nativeObj, startcol, endcol));
    }

    public Mat colRange(Range r) {
        return new Mat(n_colRange(this.nativeObj, r.start, r.end));
    }

    public int dims() {
        return n_dims(this.nativeObj);
    }

    public int cols() {
        return n_cols(this.nativeObj);
    }

    public void convertTo(Mat m, int rtype, double alpha, double beta) {
        n_convertTo(this.nativeObj, m.nativeObj, rtype, alpha, beta);
    }

    public void convertTo(Mat m, int rtype, double alpha) {
        n_convertTo(this.nativeObj, m.nativeObj, rtype, alpha);
    }

    public void convertTo(Mat m, int rtype) {
        n_convertTo(this.nativeObj, m.nativeObj, rtype);
    }

    public void copyTo(Mat m) {
        n_copyTo(this.nativeObj, m.nativeObj);
    }

    public void copyTo(Mat m, Mat mask) {
        n_copyTo(this.nativeObj, m.nativeObj, mask.nativeObj);
    }

    public void create(int rows, int cols, int type) {
        n_create(this.nativeObj, rows, cols, type);
    }

    public void create(Size size, int type) {
        n_create(this.nativeObj, size.width, size.height, type);
    }

    public Mat cross(Mat m) {
        return new Mat(n_cross(this.nativeObj, m.nativeObj));
    }

    public long dataAddr() {
        return n_dataAddr(this.nativeObj);
    }

    public int depth() {
        return n_depth(this.nativeObj);
    }

    public Mat diag(int d) {
        return new Mat(n_diag(this.nativeObj, d));
    }

    public Mat diag() {
        return new Mat(n_diag(this.nativeObj, 0));
    }

    public static Mat diag(Mat d) {
        return new Mat(n_diag(d.nativeObj));
    }

    public double dot(Mat m) {
        return n_dot(this.nativeObj, m.nativeObj);
    }

    public long elemSize() {
        return n_elemSize(this.nativeObj);
    }

    public long elemSize1() {
        return n_elemSize1(this.nativeObj);
    }

    public boolean empty() {
        return n_empty(this.nativeObj);
    }

    public static Mat eye(int rows, int cols, int type) {
        return new Mat(n_eye(rows, cols, type));
    }

    public static Mat eye(Size size, int type) {
        return new Mat(n_eye(size.width, size.height, type));
    }

    public Mat inv(int method) {
        return new Mat(n_inv(this.nativeObj, method));
    }

    public Mat inv() {
        return new Mat(n_inv(this.nativeObj));
    }

    public boolean isContinuous() {
        return n_isContinuous(this.nativeObj);
    }

    public boolean isSubmatrix() {
        return n_isSubmatrix(this.nativeObj);
    }

    public void locateROI(Size wholeSize, Point ofs) {
        double[] wholeSize_out = new double[2];
        double[] ofs_out = new double[2];
        locateROI_0(this.nativeObj, wholeSize_out, ofs_out);
        if (wholeSize != null) {
            wholeSize.width = wholeSize_out[0];
            wholeSize.height = wholeSize_out[1];
        }
        if (ofs != null) {
            ofs.f14653x = ofs_out[0];
            ofs.f14654y = ofs_out[1];
        }
    }

    public Mat mul(Mat m, double scale) {
        return new Mat(n_mul(this.nativeObj, m.nativeObj, scale));
    }

    public Mat mul(Mat m) {
        return new Mat(n_mul(this.nativeObj, m.nativeObj));
    }

    public static Mat ones(int rows, int cols, int type) {
        return new Mat(n_ones(rows, cols, type));
    }

    public static Mat ones(Size size, int type) {
        return new Mat(n_ones(size.width, size.height, type));
    }

    public void push_back(Mat m) {
        n_push_back(this.nativeObj, m.nativeObj);
    }

    public void release() {
        n_release(this.nativeObj);
    }

    public Mat reshape(int cn, int rows) {
        return new Mat(n_reshape(this.nativeObj, cn, rows));
    }

    public Mat reshape(int cn) {
        return new Mat(n_reshape(this.nativeObj, cn));
    }

    public Mat reshape(int cn, int[] newshape) {
        return new Mat(n_reshape_1(this.nativeObj, cn, newshape.length, newshape));
    }

    public Mat row(int y) {
        return new Mat(n_row(this.nativeObj, y));
    }

    public Mat rowRange(int startrow, int endrow) {
        return new Mat(n_rowRange(this.nativeObj, startrow, endrow));
    }

    public Mat rowRange(Range r) {
        return new Mat(n_rowRange(this.nativeObj, r.start, r.end));
    }

    public int rows() {
        return n_rows(this.nativeObj);
    }

    public Mat setTo(Scalar s) {
        return new Mat(n_setTo(this.nativeObj, s.val[0], s.val[1], s.val[2], s.val[3]));
    }

    public Mat setTo(Scalar value, Mat mask) {
        Scalar scalar = value;
        return new Mat(n_setTo(this.nativeObj, scalar.val[0], scalar.val[1], scalar.val[2], scalar.val[3], mask.nativeObj));
    }

    public Mat setTo(Mat value, Mat mask) {
        return new Mat(n_setTo(this.nativeObj, value.nativeObj, mask.nativeObj));
    }

    public Mat setTo(Mat value) {
        return new Mat(n_setTo(this.nativeObj, value.nativeObj));
    }

    public Size size() {
        return new Size(n_size(this.nativeObj));
    }

    public int size(int i) {
        return n_size_i(this.nativeObj, i);
    }

    public long step1(int i) {
        return n_step1(this.nativeObj, i);
    }

    public long step1() {
        return n_step1(this.nativeObj);
    }

    public Mat submat(int rowStart, int rowEnd, int colStart, int colEnd) {
        return new Mat(n_submat_rr(this.nativeObj, rowStart, rowEnd, colStart, colEnd));
    }

    public Mat submat(Range rowRange, Range colRange) {
        return new Mat(n_submat_rr(this.nativeObj, rowRange.start, rowRange.end, colRange.start, colRange.end));
    }

    public Mat submat(Rect roi) {
        return new Mat(n_submat(this.nativeObj, roi.f14658x, roi.f14659y, roi.width, roi.height));
    }

    /* renamed from: t */
    public Mat mo64816t() {
        return new Mat(n_t(this.nativeObj));
    }

    public long total() {
        return n_total(this.nativeObj);
    }

    public int type() {
        return n_type(this.nativeObj);
    }

    public static Mat zeros(int rows, int cols, int type) {
        return new Mat(n_zeros(rows, cols, type));
    }

    public static Mat zeros(Size size, int type) {
        return new Mat(n_zeros(size.width, size.height, type));
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        n_delete(this.nativeObj);
        super.finalize();
    }

    public String toString() {
        return "Mat [ " + rows() + "*" + cols() + "*" + CvType.typeToString(type()) + ", isCont=" + isContinuous() + ", isSubmat=" + isSubmatrix() + ", nativeObj=0x" + Long.toHexString(this.nativeObj) + ", dataAddr=0x" + Long.toHexString(dataAddr()) + " ]";
    }

    public String dump() {
        return nDump(this.nativeObj);
    }

    public int put(int row, int col, double... data) {
        int t = type();
        if (data == null || data.length % CvType.channels(t) != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Provided data element number (");
            sb.append(data == null ? 0 : data.length);
            sb.append(") should be multiple of the Mat channels count (");
            sb.append(CvType.channels(t));
            sb.append(Constant.STR_BRACKET_CLOSE);
            throw new UnsupportedOperationException(sb.toString());
        }
        return nPutD(this.nativeObj, row, col, data.length, data);
    }

    public int put(int row, int col, float[] data) {
        int t = type();
        if (data == null || data.length % CvType.channels(t) != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Provided data element number (");
            sb.append(data == null ? 0 : data.length);
            sb.append(") should be multiple of the Mat channels count (");
            sb.append(CvType.channels(t));
            sb.append(Constant.STR_BRACKET_CLOSE);
            throw new UnsupportedOperationException(sb.toString());
        } else if (CvType.depth(t) == 5) {
            return nPutF(this.nativeObj, row, col, data.length, data);
        } else {
            throw new UnsupportedOperationException("Mat data type is not compatible: " + t);
        }
    }

    public int put(int row, int col, int[] data) {
        int t = type();
        if (data == null || data.length % CvType.channels(t) != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Provided data element number (");
            sb.append(data == null ? 0 : data.length);
            sb.append(") should be multiple of the Mat channels count (");
            sb.append(CvType.channels(t));
            sb.append(Constant.STR_BRACKET_CLOSE);
            throw new UnsupportedOperationException(sb.toString());
        } else if (CvType.depth(t) == 4) {
            return nPutI(this.nativeObj, row, col, data.length, data);
        } else {
            throw new UnsupportedOperationException("Mat data type is not compatible: " + t);
        }
    }

    public int put(int row, int col, short[] data) {
        int t = type();
        if (data == null || data.length % CvType.channels(t) != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Provided data element number (");
            sb.append(data == null ? 0 : data.length);
            sb.append(") should be multiple of the Mat channels count (");
            sb.append(CvType.channels(t));
            sb.append(Constant.STR_BRACKET_CLOSE);
            throw new UnsupportedOperationException(sb.toString());
        } else if (CvType.depth(t) == 2 || CvType.depth(t) == 3) {
            return nPutS(this.nativeObj, row, col, data.length, data);
        } else {
            throw new UnsupportedOperationException("Mat data type is not compatible: " + t);
        }
    }

    public int put(int row, int col, byte[] data) {
        int t = type();
        if (data == null || data.length % CvType.channels(t) != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Provided data element number (");
            sb.append(data == null ? 0 : data.length);
            sb.append(") should be multiple of the Mat channels count (");
            sb.append(CvType.channels(t));
            sb.append(Constant.STR_BRACKET_CLOSE);
            throw new UnsupportedOperationException(sb.toString());
        } else if (CvType.depth(t) == 0 || CvType.depth(t) == 1) {
            return nPutB(this.nativeObj, row, col, data.length, data);
        } else {
            throw new UnsupportedOperationException("Mat data type is not compatible: " + t);
        }
    }

    public int put(int row, int col, byte[] data, int offset, int length) {
        int t = type();
        if (data == null || length % CvType.channels(t) != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Provided data element number (");
            sb.append(data == null ? 0 : data.length);
            sb.append(") should be multiple of the Mat channels count (");
            sb.append(CvType.channels(t));
            sb.append(Constant.STR_BRACKET_CLOSE);
            throw new UnsupportedOperationException(sb.toString());
        } else if (CvType.depth(t) == 0 || CvType.depth(t) == 1) {
            return nPutBwOffset(this.nativeObj, row, col, length, offset, data);
        } else {
            throw new UnsupportedOperationException("Mat data type is not compatible: " + t);
        }
    }

    public int get(int row, int col, byte[] data) {
        int t = type();
        if (data == null || data.length % CvType.channels(t) != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Provided data element number (");
            sb.append(data == null ? 0 : data.length);
            sb.append(") should be multiple of the Mat channels count (");
            sb.append(CvType.channels(t));
            sb.append(Constant.STR_BRACKET_CLOSE);
            throw new UnsupportedOperationException(sb.toString());
        } else if (CvType.depth(t) == 0 || CvType.depth(t) == 1) {
            return nGetB(this.nativeObj, row, col, data.length, data);
        } else {
            throw new UnsupportedOperationException("Mat data type is not compatible: " + t);
        }
    }

    public int get(int row, int col, short[] data) {
        int t = type();
        if (data == null || data.length % CvType.channels(t) != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Provided data element number (");
            sb.append(data == null ? 0 : data.length);
            sb.append(") should be multiple of the Mat channels count (");
            sb.append(CvType.channels(t));
            sb.append(Constant.STR_BRACKET_CLOSE);
            throw new UnsupportedOperationException(sb.toString());
        } else if (CvType.depth(t) == 2 || CvType.depth(t) == 3) {
            return nGetS(this.nativeObj, row, col, data.length, data);
        } else {
            throw new UnsupportedOperationException("Mat data type is not compatible: " + t);
        }
    }

    public int get(int row, int col, int[] data) {
        int t = type();
        if (data == null || data.length % CvType.channels(t) != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Provided data element number (");
            sb.append(data == null ? 0 : data.length);
            sb.append(") should be multiple of the Mat channels count (");
            sb.append(CvType.channels(t));
            sb.append(Constant.STR_BRACKET_CLOSE);
            throw new UnsupportedOperationException(sb.toString());
        } else if (CvType.depth(t) == 4) {
            return nGetI(this.nativeObj, row, col, data.length, data);
        } else {
            throw new UnsupportedOperationException("Mat data type is not compatible: " + t);
        }
    }

    public int get(int row, int col, float[] data) {
        int t = type();
        if (data == null || data.length % CvType.channels(t) != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Provided data element number (");
            sb.append(data == null ? 0 : data.length);
            sb.append(") should be multiple of the Mat channels count (");
            sb.append(CvType.channels(t));
            sb.append(Constant.STR_BRACKET_CLOSE);
            throw new UnsupportedOperationException(sb.toString());
        } else if (CvType.depth(t) == 5) {
            return nGetF(this.nativeObj, row, col, data.length, data);
        } else {
            throw new UnsupportedOperationException("Mat data type is not compatible: " + t);
        }
    }

    public int get(int row, int col, double[] data) {
        int t = type();
        if (data == null || data.length % CvType.channels(t) != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Provided data element number (");
            sb.append(data == null ? 0 : data.length);
            sb.append(") should be multiple of the Mat channels count (");
            sb.append(CvType.channels(t));
            sb.append(Constant.STR_BRACKET_CLOSE);
            throw new UnsupportedOperationException(sb.toString());
        } else if (CvType.depth(t) == 6) {
            return nGetD(this.nativeObj, row, col, data.length, data);
        } else {
            throw new UnsupportedOperationException("Mat data type is not compatible: " + t);
        }
    }

    public double[] get(int row, int col) {
        return nGet(this.nativeObj, row, col);
    }

    public int height() {
        return rows();
    }

    public int width() {
        return cols();
    }

    public long getNativeObjAddr() {
        return this.nativeObj;
    }
}
