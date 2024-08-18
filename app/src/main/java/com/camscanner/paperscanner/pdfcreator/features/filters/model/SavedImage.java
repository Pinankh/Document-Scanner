package com.camscanner.paperscanner.pdfcreator.features.filters.model;

import android.graphics.Bitmap;
import com.camscanner.paperscanner.pdfcreator.common.utils.ObjectsUtil;

public class SavedImage {
    public final Bitmap bitmap;
    public final String path;

    public SavedImage(Bitmap bitmap2, String path2) {
        this.bitmap = bitmap2;
        this.path = path2;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SavedImage that = (SavedImage) o;
        if (!ObjectsUtil.equals(this.bitmap, that.bitmap) || !ObjectsUtil.equals(this.path, that.path)) {
            return false;
        }
        return true;
    }
}
