package org.opencv.android;

import android.content.Context;

public class OpenCVLoader {


    public static boolean initDebug() {
        return StaticHelper.initOpenCV(false);
    }


    public static boolean initAsync(String Version, Context AppContext, LoaderCallbackInterface Callback) {
        return AsyncServiceHelper.initOpenCV(Version, AppContext, Callback);
    }
}
