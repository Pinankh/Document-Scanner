package com.camscanner.paperscanner.pdfcreator.common.utils;

import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class ImageViewUtils {
    public static RectF getBitmapPositionInsideImageView(ImageView image) {
        if (image.getDrawable() == null) {
            return null;
        }
        float[] f = new float[9];
        image.getImageMatrix().getValues(f);
        float scaleX = f[0];
        float scaleY = f[4];
        Drawable d = image.getDrawable();
        int origW = d.getIntrinsicWidth();
        int origH = d.getIntrinsicHeight();
        int actW = Math.round(((float) origW) * scaleX);
        int actH = Math.round(((float) origH) * scaleY);
        int imgViewW = image.getWidth();
        int imgViewH = image.getHeight();
        int top = (imgViewH - actH) >> 1;
        int left = (imgViewW - actW) >> 1;
        if (left < 0) {
            left = 0;
        }
        if (top < 0) {
            top = 0;
        }
        int right = left + actW;
        int bottom = top + actH;
        if (right > imgViewW) {
            right = imgViewW;
        }
        if (bottom > imgViewH) {
            bottom = imgViewH;
        }
        return new RectF((float) left, (float) top, (float) right, (float) bottom);
    }
}
