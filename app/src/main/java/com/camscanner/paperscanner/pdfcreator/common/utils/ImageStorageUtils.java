package com.camscanner.paperscanner.pdfcreator.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import com.camscanner.paperscanner.pdfcreator.model.types.Qualities;
import timber.log.Timber;

public class ImageStorageUtils {
    private static final String LOG_TAG = ImageStorageUtils.class.getSimpleName();

    public static String saveTakenPicture(Bitmap bitmap, Context context) {
        File dir = SharedPrefsUtils.getSaveToAlbum(context) ? getCameraFolder() : getImageFolder();
        return writeImageToDisk(bitmap, Bitmap.CompressFormat.JPEG, dir.toString() + File.separator + IONames.CAMERA_FILE + getTimeStamp() + Extensions.JPG, SharedPrefsUtils.getImgQuality(context));
    }

    public static String saveImageToAppFolder(Bitmap bitmap) {
        return writeImageToDisk(bitmap, Bitmap.CompressFormat.JPEG, getImageFolder().toString() + File.separator + IONames.CAMERA_FILE + getTimeStamp() + Extensions.JPG, Qualities.FULL.value());
    }

    public static String saveImageTutorial(Bitmap bitmap) {
        return writeImageToDisk(bitmap, Bitmap.CompressFormat.PNG, getTutorFolder().toString() + File.separator + IONames.TUTOR_FILE + getTimeStamp() + Extensions.PNG, Qualities.FULL.value());
    }

    public static String saveImageToShareFolder(Bitmap bitmap) {
        return writeImageToDisk(bitmap, Bitmap.CompressFormat.JPEG, getShareFolder().toString() + File.separator + IONames.CAMERA_FILE + getTimeStamp() + Extensions.JPG, Qualities.FULL.value());
    }

    public static String saveImageToGallery(Bitmap bitmap) {
        return writeImageToDisk(bitmap, Bitmap.CompressFormat.JPEG, getCameraFolder().toString() + File.separator + IONames.CAMERA_FILE + getTimeStamp() + Extensions.JPG, Qualities.FULL.value());
    }

    public static String saveSignatureImage(Bitmap bitmap) {
        return writeImageToDisk(bitmap, Bitmap.CompressFormat.PNG, getSignFolder().toString() + File.separator + IONames.SIGN_FILE + getTimeStamp() + Extensions.PNG, Qualities.SIGNATURE.value());
    }

    public static String saveImageOcr(Bitmap bitmap) {
        File dir = getOcrFolder();
        return writeImageToDisk(bitmap, Bitmap.CompressFormat.JPEG, dir.getPath() + File.separator + IONames.OCR_FILE + getTimeStamp() + Extensions.JPG, Qualities.OCR_COMPRESSING.value());
    }

    public static String saveImagePreOcr(Bitmap bitmap) {
        File dir = getOcrFolder();
        return writeImageToDisk(bitmap, Bitmap.CompressFormat.JPEG, dir.getPath() + File.separator + IONames.OCR_FILE + getTimeStamp() + Extensions.JPG, Qualities.FULL.value());
    }

    private static File getSignFolder() {
        return getAppSubFolder(IONames.SIGN_FOLDER);
    }

    private static File getOcrFolder() {
        return getAppSubFolder(IONames.OCR_FOLDER);
    }

    private static File getImageFolder() {
        return getAppSubFolder("IMG");
    }

    private static File getTutorFolder() {
        return getAppSubFolder(IONames.IMG_TUTOR);
    }

    private static File getShareFolder() {
        return getAppSubFolder(IONames.SHARE_FOLDER);
    }

    private static File getCameraFolder() {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory());
        sb.append("/"+Environment.DIRECTORY_DOWNLOADS+File.separator + IONames.CAMERA_FOLDER + File.separator);
        File dir = new File(sb.toString());
        //File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + File.separator + IONames.CAMERA_FOLDER + File.separator);

        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private static File getAppSubFolder(String subFolder) {
        File dir = new File(getAppFolder(), subFolder);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static File getAppFolder() {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory());
        sb.append("/"+Environment.DIRECTORY_DOWNLOADS+ File.separator + "DocScanner");
        File dir = new File(sb.toString());

        //File dir = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "DocScanner");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static void clearAllTempFolders() {
        clearShareFolder();
        clearFolder(getTutorFolder());
        clearFolder(getOcrFolder());
    }

    public static void clearShareFolder() {
        clearFolder(getShareFolder());
    }

    private static void clearFolder(final File folder) {
        new Thread(new Runnable() {


            public final void run() {
                ImageStorageUtils.lambda$clearFolder$0(folder);
            }
        }).start();
    }

    static /* synthetic */ void lambda$clearFolder$0(File folder) {
        try {
            deleteRecursive(folder);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private static void deleteRecursive(File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                deleteRecursive(child);
            }
        }
        file.delete();
    }

    private static String writeImageToDisk(Bitmap bitmap, Bitmap.CompressFormat format, String photoPath, int quality) {
        ByteArrayOutputStream out = null;
        FileOutputStream stream = null;
        try {
            File mediaFile = new File(photoPath);
            out = new ByteArrayOutputStream();
            bitmap.compress(format, quality, out);
            stream = new FileOutputStream(mediaFile);
            stream.write(out.toByteArray());
        } catch (IOException e) {
            IOException exception = e;
        } catch (NullPointerException e2) {
            NullPointerException  exception = e2;
        } catch (OutOfMemoryError exception) {
            Timber.tag(LOG_TAG).e(exception);
            System.gc();
            photoPath = "-1";
        } catch (Throwable th) {
            close(out);
            close(stream);
            throw th;
        }
        close(out);
        close(stream);
        return photoPath;
//        Timber.tag(LOG_TAG).e(exception);
//        Crashlytics.logException(exception);
//        photoPath = "-1";
//        close(out);
//        close(stream);
//        return photoPath;
    }

    private static synchronized String getTimeStamp() {
        String str;
        synchronized (ImageStorageUtils.class) {
            str = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_" + getRandIdx();
        }
        return str;
    }

    private static int getRandIdx() {
        return new Random().nextInt(80) + 65;
    }

    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }

    public static void deleteImages(Context context, String[] paths, boolean notifySystem) {
        for (String path : paths) {
            deleteImage(context, path);
        }
    }

    public static void deleteImage(Context context, String strPath) {
        if (!TextUtils.isEmpty(strPath) && new File(strPath).delete()) {
            MediaScannerUtils.notifySystemAboutFile(context, strPath);
        }
    }
}
