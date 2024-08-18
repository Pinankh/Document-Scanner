package com.camscanner.paperscanner.pdfcreator.common.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.Nullable;

import com.googlecode.leptonica.android.Binarize;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.leptonica.android.WriteFile;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import com.camscanner.paperscanner.pdfcreator.model.types.Resolution;
import timber.log.Timber;

public class BitmapUtils {
    private static final String LOG_TAG = BitmapUtils.class.getSimpleName();

    public static Bitmap decodeFile(Context context, String path) {
        return decodeUri(context, UriUtils.getImageContentUri(context, new File(path), false));
    }

    public static Bitmap decodeUri(Context context, Uri selectedImage) {
        System.gc();
        Bitmap edited = null;
        try {
            InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(imageStream, (Rect) null, options);
            if (imageStream != null) {
                imageStream.close();
            }
            options.inSampleSize = calculateInSampleSize(options, 4096, 4096);
            options.inScaled = false;
            options.inMutable = true;
            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inJustDecodeBounds = false;
            InputStream imageStream2 = context.getContentResolver().openInputStream(selectedImage);
            Bitmap original = BitmapFactory.decodeStream(imageStream2, (Rect) null, options);
            if (imageStream2 != null) {
                imageStream2.close();
            }
            Matrix matrix = new Matrix();
            int rotation = getRotation(context, selectedImage);
            if (rotation > 0) {
                matrix.postRotate((float) rotation);
            }
            edited = resizeBitmap(original, matrix, Resolution.HIGH, 2500);
            if (original != null && !original.equals(edited)) {
                original.recycle();
            }
        } catch (Exception | OutOfMemoryError e) {
            Timber.tag(LOG_TAG).e(e);
        }
        System.gc();
        return edited;
    }

    public static Bitmap decodeUriWithoutOptimizations(Context context, String imagePath) {
        if (imagePath == null) {
            return null;
        }
        return decodeUriWithoutOptimizations(context, UriUtils.getImageContentUri(context, new File(imagePath), false));
    }

    public static Bitmap decodeUriWithoutOptimizations(Context context, Uri selectedImage) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            options.inMutable = true;
            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inJustDecodeBounds = false;
            InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
            Bitmap original = BitmapFactory.decodeStream(imageStream, (Rect) null, options);
            if (imageStream != null) {
                imageStream.close();
            }
            return original;
        } catch (Exception | OutOfMemoryError e) {
            Timber.tag(LOG_TAG).e(e);
            return null;
        }
    }

    private static int getRotation(Context context, Uri selectedImage) throws IOException {
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23) {
            ei = new ExifInterface(context.getContentResolver().openInputStream(selectedImage));
        } else {
            ei = new ExifInterface(selectedImage.getPath());
        }
        int orientation = ei.getAttributeInt("Orientation", 1);
        if (orientation == 0) {
            int angle = getUriOrientation(context, selectedImage);
            if (angle > 0) {
                return angle;
            }
            return 0;
        } else if (orientation == 3) {
            return 180;
        } else {
            if (orientation == 6) {
                return 90;
            }
            if (orientation != 8) {
                return 0;
            }
            return 270;
        }
    }

    public static Bitmap rotateImageAlpha(Bitmap bitmap, float angle) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        rotatedBitmap.setHasAlpha(true);
        return rotatedBitmap;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;
            while (halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private static int getUriOrientation(Context context, Uri selectedImage) {
        int rotation = 0;
        Cursor mediaCursor = context.getContentResolver().query(selectedImage, new String[]{"orientation"}, (String) null, (String[]) null, (String) null);
        if (!(mediaCursor == null || mediaCursor.getCount() == 0)) {
            if (mediaCursor.getCount() != 0 && mediaCursor.moveToNext()) {
                rotation = mediaCursor.getInt(mediaCursor.getColumnIndex("orientation"));
            }
            mediaCursor.close();
        }
        return rotation;
    }

    public static Bitmap resizeInAppImage(Bitmap picture) {
        return resizeBitmap(picture, new Matrix(), Resolution.HIGH, 1024);
    }

    public static Bitmap resizeOcrImage(Bitmap original) {
        return resizeBitmap(original, new Matrix(), Resolution.HIGH, 2000);
    }

    public static Bitmap resizeOutputImage(Context context, String path) {
        Bitmap original = decodeUriWithoutOptimizations(context, path);
        Bitmap edited = resizeBitmap(original, new Matrix(), SharedPrefsUtils.getOutputSize(context), 2000);
        if (original != null && !original.equals(edited)) {
            original.recycle();
        }
        return edited;
    }

    public static Bitmap resizeAndRotateScan(Context context, byte[] data, int rotation) {
        Bitmap original = BitmapFactory.decodeByteArray(data, 0, data.length);
        Matrix matrix = new Matrix();
        matrix.postRotate((float) rotation);
        Bitmap edited = resizeBitmap(original, matrix, SharedPrefsUtils.getScanSize(context), 2500);
        if (original != null && !original.equals(edited)) {
            original.recycle();
        }
        return edited;
    }

    private static Bitmap resizeBitmap(@Nullable Bitmap original, Matrix matrix, Resolution resolution, int maxSize) {
        if (original == null) {
            return null;
        }
        int origWidth = original.getWidth();
        int origHeight = original.getHeight();
        int i = C67071.MyResolution[resolution.ordinal()];
        if (i != 1) {
            if (i == 3 || i == 4) {
                matrix.postScale(resolution.size(), resolution.size());
            } else if (origWidth > origHeight) {
                if (origWidth > maxSize) {
                    int newWidth = maxSize;
                    matrix.postScale(((float) newWidth) / ((float) origWidth), ((float) ((int) (((float) origHeight) * ((((float) newWidth) * 1.0f) / ((float) origWidth))))) / ((float) origHeight));
                }
            } else if (origHeight > maxSize) {
                int newHeight = maxSize;
                matrix.postScale(((float) ((int) (((float) origWidth) * ((((float) newHeight) * 1.0f) / ((float) origHeight))))) / ((float) origWidth), ((float) newHeight) / ((float) origHeight));
            }
        }
        return Bitmap.createBitmap(original, 0, 0, origWidth, origHeight, matrix, false);
    }

    static /* synthetic */ class C67071 {
        static final /* synthetic */ int[] MyResolution = new int[Resolution.values().length];

        static {
            try {
                MyResolution[Resolution.FULL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                MyResolution[Resolution.HIGH.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                MyResolution[Resolution.MEDIUM.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                MyResolution[Resolution.LOW.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public static Bitmap addSignature(Bitmap bmpSrc, Bitmap bmpSignature, float x, float y, float degree, float scaledWidth) {
        Bitmap bitmap = bmpSrc;
        Bitmap bitmap2 = bmpSignature;
        if (bitmap == null) {
            return null;
        }
        if (bitmap2 == null) {
            return bitmap;
        }
        Bitmap bmp = Bitmap.createBitmap(bmpSrc.getWidth(), bmpSrc.getHeight(), Bitmap.Config.ARGB_8888);
        Paint paint = new Paint(7);
        Canvas canvas = new Canvas(bmp);
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
        float scaleX = scaledWidth / ((float) bmpSignature.getWidth());
        Matrix matrix = new Matrix();
        matrix.setScale(scaleX, scaleX);
        matrix.postRotate(degree, (((float) bmpSignature.getWidth()) * scaleX) / 2.0f, (((float) bmpSignature.getHeight()) * scaleX) / 2.0f);
        matrix.postTranslate(x, y);
        Timber.tag("SignEditActivity").w("addSignature %s %s %s", Float.valueOf(scaleX), Float.valueOf(x), Float.valueOf(y));
        canvas.drawBitmap(bitmap2, matrix, paint);
        return bmp;
    }

    public static InputStream bitmap2InputStream(Bitmap bm, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }

    public static Bitmap getBinaryFromMat(Mat matOrg) {
        try {
            Mat matGray = new Mat(matOrg.rows(), matOrg.cols(), CvType.CV_8UC1);
            Imgproc.cvtColor(matOrg, matGray, 11);
            Utils.matToBitmap(matGray, Bitmap.createBitmap(matGray.cols(), matGray.rows(), Bitmap.Config.ARGB_8888));
            byte[] byteGray = new byte[(matGray.cols() * matGray.rows())];
            matGray.get(0, 0, byteGray);
            return WriteFile.writeBitmap(Binarize.sauvolaBinarizeTiled(ReadFile.readBytes8(byteGray, matGray.cols(), matGray.rows()), 9, 0.15f, 8, 8));
        } catch (Exception e) {
            return null;
        }
    }

    public static Bitmap rotate(Bitmap source, float angle, boolean recycleSource) {
        if (isRecycled(source)) {
            return null;
        }
        if (angle == 0.0f) {
            return source;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap rotated = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, false);
        if (recycleSource && !source.equals(rotated)) {
            recycleBitmap(source);
        }
        return rotated;
    }

    public static void recycleBitmap(Bitmap bmp) {
        if (bmp != null && !bmp.isRecycled()) {
            bmp.recycle();
        }
    }

    public static boolean isRecycled(Bitmap bitmap) {
        return bitmap == null || bitmap.isRecycled();
    }

    public static Bitmap createThumb(Bitmap bmpSrc) {
        float iw = (float) bmpSrc.getWidth();
        float ih = (float) bmpSrc.getHeight();
        float scale = Math.min(400.0f / iw, 400.0f / ih);
        return Bitmap.createScaledBitmap(bmpSrc, (int) (scale * iw), (int) (scale * ih), false);
    }

    public static Bitmap createScaledBitmap(Bitmap bmp, int biggestSize) {
        int height;
        int width;
        if (bmp.getWidth() > bmp.getHeight()) {
            width = biggestSize;
            height = (int) (((float) biggestSize) * ((((float) bmp.getHeight()) * 1.0f) / ((float) bmp.getWidth())));
        } else {
            height = biggestSize;
            width = (int) (((float) biggestSize) * ((((float) bmp.getWidth()) * 1.0f) / ((float) bmp.getHeight())));
        }
        return Bitmap.createScaledBitmap(bmp, width, height, false);
    }
}
