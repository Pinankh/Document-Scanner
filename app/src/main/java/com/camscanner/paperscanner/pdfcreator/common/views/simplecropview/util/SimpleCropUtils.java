package com.camscanner.paperscanner.pdfcreator.common.views.simplecropview.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.opengl.GLES10;
import android.os.ParcelFileDescriptor;

import com.tom_roush.pdfbox.pdmodel.common.PDPageLabelRange;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SimpleCropUtils {
    private static final int SIZE_DEFAULT = 2048;
    private static final int SIZE_LIMIT = 4096;
    private static final String TAG = SimpleCropUtils.class.getSimpleName();
    public static int sInputImageHeight = 0;
    public static int sInputImageWidth = 0;

    public static int getExifRotation(File file) {
        if (file == null) {
            return 0;
        }
        try {
            return getRotateDegreeFromOrientation(new ExifInterface(file.getAbsolutePath()).getAttributeInt("Orientation", 0));
        } catch (IOException e) {
            return 0;
        }
    }

    public static int getExifRotation(Context context, Uri uri) {
        Cursor cursor = null;
        try {
            Cursor cursor2 = context.getContentResolver().query(uri, new String[]{"orientation"}, (String) null, (String[]) null, (String) null);
            if (cursor2 != null) {
                if (cursor2.moveToFirst()) {
                    int i = cursor2.getInt(0);
                    if (cursor2 != null) {
                        cursor2.close();
                    }
                    return i;
                }
            }
            if (cursor2 != null) {
                cursor2.close();
            }
            return 0;
        } catch (RuntimeException e) {
            if (cursor != null) {
                cursor.close();
            }
            return 0;
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }



    public static int getRotateDegreeFromOrientation(int orientation) {
        if (orientation == 3) {
            return 180;
        }
        if (orientation == 6) {
            return 90;
        }
        if (orientation != 8) {
            return 0;
        }
        return 270;
    }

    public static Matrix getMatrixFromExifOrientation(int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case 2:
                matrix.postScale(-1.0f, 1.0f);
                break;
            case 3:
                matrix.postRotate(180.0f);
                break;
            case 4:
                matrix.postScale(1.0f, -1.0f);
                break;
            case 5:
                matrix.postRotate(90.0f);
                matrix.postScale(1.0f, -1.0f);
                break;
            case 6:
                matrix.postRotate(90.0f);
                break;
            case 7:
                matrix.postRotate(-90.0f);
                matrix.postScale(1.0f, -1.0f);
                break;
            case 8:
                matrix.postRotate(-90.0f);
                break;
        }
        return matrix;
    }




    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        int columnIndex;
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, new String[]{"_data", "_display_name"}, selection, selectionArgs, (String) null);
            if (cursor != null && cursor.moveToFirst()) {
                if (uri.toString().startsWith("content://com.google.android.gallery3d")) {
                    columnIndex = cursor.getColumnIndex("_display_name");
                } else {
                    columnIndex = cursor.getColumnIndex("_data");
                }
                if (columnIndex != -1) {
                    return cursor.getString(columnIndex);
                }
            }
            if (cursor == null) {
                return null;
            }
            cursor.close();
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static boolean isGoogleDriveDocument(Uri uri) {
        return "com.google.android.apps.docs.storage".equals(uri.getAuthority());
    }

    private static File getGoogleDriveFile(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }
        String filePath = new File(context.getCacheDir(), "tmp").getAbsolutePath();
        try {
            ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, PDPageLabelRange.STYLE_ROMAN_LOWER);
            if (pfd == null) {
                return null;
            }
            FileInputStream input = new FileInputStream(pfd.getFileDescriptor());
            FileOutputStream output = new FileOutputStream(filePath);
            byte[] bytes = new byte[4096];
            while (true) {
                int read = input.read(bytes);
                int read2 = read;
                if (read != -1) {
                    output.write(bytes, 0, read2);
                } else {
                    File file = new File(filePath);
                    closeQuietly(input);
                    closeQuietly(output);
                    return file;
                }
            }
        } catch (IOException e) {
            return null;
        } finally {
            closeQuietly((Closeable) null);
            closeQuietly((Closeable) null);
        }
    }

    public static Bitmap decodeSampledBitmapFromUri(Context context, Uri sourceUri, int requestSize) {
        InputStream stream = null;
        Bitmap bitmap = null;
        try {
            InputStream stream2 = context.getContentResolver().openInputStream(sourceUri);
            if (stream2 != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = calculateInSampleSize(context, sourceUri, requestSize);
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeStream(stream2, (Rect) null, options);
            }
            if (stream2 != null) {
                try {
                    stream2.close();
                } catch (IOException e) {
                }
            }
        } catch (FileNotFoundException e2) {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Throwable th) {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e3) {
                }
            }
            throw th;
        }
        return bitmap;
    }

    public static int calculateInSampleSize(Context context, Uri sourceUri, int requestSize) {
        InputStream is = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            is = context.getContentResolver().openInputStream(sourceUri);
            BitmapFactory.decodeStream(is, (Rect) null, options);
        } catch (FileNotFoundException e) {
        } catch (Throwable th) {
            closeQuietly(is);
            throw th;
        }
        closeQuietly(is);
        int inSampleSize = 1;
        sInputImageWidth = options.outWidth;
        sInputImageHeight = options.outHeight;
        while (true) {
            if (options.outWidth / inSampleSize <= requestSize && options.outHeight / inSampleSize <= requestSize) {
                return inSampleSize;
            }
            inSampleSize *= 2;
        }
    }

    public static Bitmap getScaledBitmapForHeight(Bitmap bitmap, int outHeight) {
        return getScaledBitmap(bitmap, Math.round(((float) outHeight) * (((float) bitmap.getWidth()) / ((float) bitmap.getHeight()))), outHeight);
    }

    public static Bitmap getScaledBitmapForWidth(Bitmap bitmap, int outWidth) {
        return getScaledBitmap(bitmap, outWidth, Math.round(((float) outWidth) / (((float) bitmap.getWidth()) / ((float) bitmap.getHeight()))));
    }

    public static Bitmap getScaledBitmap(Bitmap bitmap, int outWidth, int outHeight) {
        int currentWidth = bitmap.getWidth();
        int currentHeight = bitmap.getHeight();
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.postScale(((float) outWidth) / ((float) currentWidth), ((float) outHeight) / ((float) currentHeight));
        return Bitmap.createBitmap(bitmap, 0, 0, currentWidth, currentHeight, scaleMatrix, true);
    }

    public static int getMaxSize() {
        int[] arr = new int[1];
        GLES10.glGetIntegerv(3379, arr, 0);
        if (arr[0] > 0) {
            return Math.min(arr[0], 4096);
        }
        return 2048;
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Throwable th) {
            }
        }
    }

}
