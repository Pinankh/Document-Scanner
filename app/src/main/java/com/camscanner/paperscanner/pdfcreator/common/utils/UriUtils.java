package com.camscanner.paperscanner.pdfcreator.common.utils;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import java.io.File;

import com.camscanner.paperscanner.pdfcreator.BuildConfig;
import com.camscanner.paperscanner.pdfcreator.R;

public class UriUtils {
    public static Uri getImageContentUri(Context context, File imageFile, boolean create) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{"_id"}, "_data=? ", new String[]{filePath}, (String) null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            cursor.close();
            return ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, (long) id);
        } else if (!create || !imageFile.exists()) {
            return Uri.fromFile(imageFile);
        } else {
            ContentValues values = new ContentValues();
            values.put("_data", filePath);
            return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
    }

    public static Uri getFileUri(Context context, String path) {
        return FileProvider.getUriForFile(context, /*context.getString(R.string.file_provider_authority)*/BuildConfig.APPLICATION_ID+".provider", new File(path));
    }
}
