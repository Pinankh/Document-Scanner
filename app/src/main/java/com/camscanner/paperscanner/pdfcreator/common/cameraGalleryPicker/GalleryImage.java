package com.camscanner.paperscanner.pdfcreator.common.cameraGalleryPicker;

import android.net.Uri;

public class GalleryImage {
    private Uri bitmapUri;
    private String imagePath;

    public GalleryImage(Uri bitmapUri2, String imagePath2) {
        this.bitmapUri = bitmapUri2;
        this.imagePath = imagePath2;
    }

    public Uri getBitmapUri() {
        return this.bitmapUri;
    }

    public void setBitmapUri(Uri bitmapUri2) {
        this.bitmapUri = bitmapUri2;
    }

    public String getImagePath() {
        return this.imagePath;
    }

    public void setImagePath(String imagePath2) {
        this.imagePath = imagePath2;
    }
}
