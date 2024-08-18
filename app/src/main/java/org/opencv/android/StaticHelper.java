package org.opencv.android;

import android.util.Log;
import java.util.StringTokenizer;
import org.opencv.core.Core;

class StaticHelper {
    private static final String TAG = "OpenCV/StaticHelper";

    private static native String getLibraryList();

    StaticHelper() {
    }

    public static boolean initOpenCV(boolean InitCuda) {
        String libs = "";
        if (InitCuda) {
            loadLibrary("cudart");
            loadLibrary("nppc");
            loadLibrary("nppi");
            loadLibrary("npps");
            loadLibrary("cufft");
            loadLibrary("cublas");
        }
        Log.d(TAG, "Trying to get library list");
        try {
            System.loadLibrary("opencv_info");
            libs = getLibraryList();
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "OpenCV error: Cannot load info library for OpenCV");
        }
        Log.d(TAG, "Library list: \"" + libs + "\"");
        Log.d(TAG, "First attempt to load libs");
        if (initOpenCVLibs(libs)) {
            Log.d(TAG, "First attempt to load libs is OK");
            for (String str : Core.getBuildInformation().split(System.getProperty("line.separator"))) {
                Log.i(TAG, str);
            }
            return true;
        }
        Log.d(TAG, "First attempt to load libs fails");
        return false;
    }

    private static boolean loadLibrary(String Name) {
        Log.d(TAG, "Trying to load library " + Name);
        try {
            System.loadLibrary(Name);
            Log.d(TAG, "Library " + Name + " loaded");
            return true;
        } catch (UnsatisfiedLinkError e) {
            Log.d(TAG, "Cannot load library \"" + Name + "\"");
            e.printStackTrace();
            return false;
        }
    }

    private static boolean initOpenCVLibs(String Libs) {
        Log.d(TAG, "Trying to init OpenCV libs");
        boolean result = true;
        if (Libs == null || Libs.length() == 0) {
            return loadLibrary("opencv_java4");
        }
        Log.d(TAG, "Trying to load libs by dependency list");
        StringTokenizer splitter = new StringTokenizer(Libs, ";");
        while (splitter.hasMoreTokens()) {
            result &= loadLibrary(splitter.nextToken());
        }
        return result;
    }
}
