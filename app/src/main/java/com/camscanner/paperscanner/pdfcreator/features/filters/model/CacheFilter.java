package com.camscanner.paperscanner.pdfcreator.features.filters.model;

import android.graphics.Bitmap;
import com.camscanner.paperscanner.pdfcreator.common.utils.ObjectsUtil;

public class CacheFilter {
    public final Bitmap bitmap;
    public final boolean newest;

    public CacheFilter(Bitmap bitmap2, boolean newest2) {
        this.bitmap = bitmap2;
        this.newest = newest2;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CacheFilter that = (CacheFilter) o;
        if (this.newest != that.newest || !ObjectsUtil.equals(this.bitmap, that.bitmap)) {
            return false;
        }
        return true;
    }
}
