package com.tapscanner.polygondetect;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

public class PDFBoxResourceLoader {
    private static AssetManager ASSET_MANAGER;
    private static Context CONTEXT;
    private static boolean hasWarned;

    public static void init(Context context) {
        if (CONTEXT == null) {
            CONTEXT = context.getApplicationContext();
            ASSET_MANAGER = CONTEXT.getAssets();
        }
    }

    public static boolean isReady() {
        if (ASSET_MANAGER == null && !hasWarned) {
            Log.w("PdfBoxAndroid", "Call PDFBoxResourceLoader.init() first to decrease resource load time");
            hasWarned = true;
        }
        if (ASSET_MANAGER != null) {
            return true;
        }
        return false;
    }

    public static InputStream getStream(String str) throws IOException {
        return ASSET_MANAGER.open(str);
    }
}
