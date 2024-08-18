package com.camscanner.paperscanner.pdfcreator.common.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import java.io.File;

public class MediaScannerUtils {
    public static void notifySystemAboutFile(Context context, String filePath) {
        notifySystemAboutFile(context, new File(filePath));
    }

    public static void notifySystemAboutFile(Context context, File file) {
        notifySystemAboutFile(context, Uri.fromFile(file));
    }

    public static void notifySystemAboutFile(Context context, Uri uri) {
        Intent mediaScannerIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        mediaScannerIntent.setData(uri);
        context.sendBroadcast(mediaScannerIntent);
    }
}
