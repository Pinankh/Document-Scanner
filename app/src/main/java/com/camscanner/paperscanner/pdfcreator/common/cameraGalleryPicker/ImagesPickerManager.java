package com.camscanner.paperscanner.pdfcreator.common.cameraGalleryPicker;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;

import androidx.fragment.app.Fragment;

import com.camscanner.paperscanner.pdfcreator.R;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import java.util.ArrayList;
import java.util.List;

public class ImagesPickerManager {
    private static final int SELECT_REAL_IMAGE_REQUEST = 500;

    public static void startPickerSolo(Activity activity) {
        startPickerFrom(Matisse.from(activity), activity.getResources(), 1);
    }

    public static void startPicker(Activity activity) {
        startPickerFrom(Matisse.from(activity), activity.getResources(), 10);
    }

    public static void startPicker(Fragment fragment) {
        startPickerFrom(Matisse.from(fragment), fragment.getResources(), 10);
    }

    private static void startPickerFrom(Matisse matisse, Resources res, int maxCount) {
        matisse.choose(MimeType.of(MimeType.JPEG, MimeType.PNG)).countable(false).capture(false).theme(R.style.Matisse_TapScanner).showSingleMediaType(true).maxSelectable(maxCount).gridExpectedSize(res.getDimensionPixelSize(R.dimen.preview_image_size)).thumbnailScale(0.85f).imageEngine(new GlideEngine()).forResult(500);
    }

    public static GalleryImage handlePickerResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != 500 || resultCode != -1 || data == null) {
            return null;
        }
        List<Uri> obtainResult = Matisse.obtainResult(data);
        List<Uri> results = obtainResult;
        if (!(obtainResult == null || results.get(0) == null)) {
            List<String> obtainPathResult = Matisse.obtainPathResult(data);
            List<String> pathResults = obtainPathResult;
            if (!(obtainPathResult == null || pathResults.get(0) == null)) {
                return new GalleryImage(results.get(0), pathResults.get(0));
            }
        }
        return null;
    }

    public static ArrayList<GalleryImage> handlePickerMultiResults(int requestCode, int resultCode, Intent data) {
        if (requestCode != 500 || resultCode != -1 || data == null) {
            return null;
        }
        List<Uri> obtainResult = Matisse.obtainResult(data);
        List<Uri> uriResults = obtainResult;
        if (!(obtainResult == null || uriResults.get(0) == null)) {
            List<String> obtainPathResult = Matisse.obtainPathResult(data);
            List<String> pathResults = obtainPathResult;
            if (!(obtainPathResult == null || pathResults.get(0) == null)) {
                ArrayList<GalleryImage> galleryImages = new ArrayList<>();
                for (int i = 0; i < uriResults.size(); i++) {
                    galleryImages.add(new GalleryImage(uriResults.get(i), pathResults.get(i)));
                }
                return galleryImages;
            }
        }
        return null;
    }
}
