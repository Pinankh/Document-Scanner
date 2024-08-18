package com.camscanner.paperscanner.pdfcreator.features.picture;

import android.graphics.Bitmap;
import com.camscanner.paperscanner.pdfcreator.common.utils.BitmapUtils;
import timber.log.Timber;

public class ImageHolder {
    private static volatile ImageHolder imageHolder;
    private volatile Bitmap croppedPicture;
    private volatile Bitmap optimizedPicture;
    private String originalPath;
    private volatile Bitmap originalPicture;

    public static ImageHolder get() {
        if (imageHolder == null) {
            synchronized (ImageHolder.class) {
                if (imageHolder == null) {
                    imageHolder = new ImageHolder();
                }
            }
        }
        return imageHolder;
    }

    public synchronized Bitmap getOriginalPicture() {
        return this.originalPicture;
    }

    public synchronized Bitmap getOptimizedPicture() {
        return this.optimizedPicture;
    }

    public synchronized Bitmap getCroppedPicture() {
        return this.croppedPicture;
    }

    public synchronized String getOriginalPath() {
        return this.originalPath;
    }

    public synchronized boolean hasOriginalPicture() {
        return this.originalPicture != null;
    }

    public synchronized boolean hasCroppedAndOptimized() {
        return (this.croppedPicture == null || this.optimizedPicture == null) ? false : true;
    }


    public synchronized void setOriginalPicture(Bitmap picture, String path) {
        if (picture != null) {
            BitmapUtils.recycleBitmap(this.originalPicture);
            this.originalPicture = copyBitmap(picture);
            if (path != null) {
                this.originalPath = path;
            }
            if (!picture.equals(this.originalPicture)) {
                BitmapUtils.recycleBitmap(picture);
            }
        }
    }


    public synchronized void setCroppedPicture(Bitmap picture, boolean optimize, boolean recycle) {
        if (picture != null) {
            BitmapUtils.recycleBitmap(this.croppedPicture);
            BitmapUtils.recycleBitmap(this.optimizedPicture);
            this.croppedPicture = copyBitmap(picture);
            if (optimize) {
                this.optimizedPicture = BitmapUtils.resizeInAppImage(picture);
            } else {
                this.optimizedPicture = null;
            }
            if (recycle && !picture.equals(this.croppedPicture) && !picture.equals(this.optimizedPicture)) {
                BitmapUtils.recycleBitmap(picture);
            }
        }
    }

    public synchronized void resetAll() {
        resetPaths();
        resetPictures();
    }

    private synchronized void resetPictures() {
        BitmapUtils.recycleBitmap(this.originalPicture);
        BitmapUtils.recycleBitmap(this.croppedPicture);
        BitmapUtils.recycleBitmap(this.optimizedPicture);
        this.originalPicture = null;
        this.croppedPicture = null;
        this.optimizedPicture = null;
        System.gc();
    }

    private synchronized void resetPaths() {
        this.originalPath = null;
    }

    private Bitmap copyBitmap(Bitmap bmp) {
        try {
            return bmp.copy(Bitmap.Config.ARGB_8888, false);
        } catch (OutOfMemoryError ex) {
            Timber.e(ex);
            return null;
        }
    }
}
