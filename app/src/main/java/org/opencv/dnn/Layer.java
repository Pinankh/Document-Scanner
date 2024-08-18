package org.opencv.dnn;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Algorithm;
import org.opencv.core.Mat;
import org.opencv.utils.Converters;

public class Layer extends Algorithm {
    private static native void delete(long j);



    private static native void run_0(long j, long j2, long j3, long j4);


    protected Layer(long addr) {
        super(addr);
    }




    @Deprecated
    public void run(List<Mat> inputs, List<Mat> outputs, List<Mat> internals) {
        Mat inputs_mat = Converters.vector_Mat_to_Mat(inputs);
        Mat outputs_mat = new Mat();
        Mat internals_mat = Converters.vector_Mat_to_Mat(internals);
        run_0(this.nativeObj, inputs_mat.nativeObj, outputs_mat.nativeObj, internals_mat.nativeObj);
        Converters.Mat_to_vector_Mat(outputs_mat, outputs);
        outputs_mat.release();
        Converters.Mat_to_vector_Mat(internals_mat, internals);
        internals_mat.release();
    }



    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        delete(this.nativeObj);
    }
}
