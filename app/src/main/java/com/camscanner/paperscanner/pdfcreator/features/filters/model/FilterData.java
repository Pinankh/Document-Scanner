package com.camscanner.paperscanner.pdfcreator.features.filters.model;

import android.graphics.Bitmap;
import com.camscanner.paperscanner.pdfcreator.common.utils.ObjectsUtil;

public class FilterData {
    public final Bitmap thumbImg;
    public final String txt;

    public FilterData(Bitmap thumbImg2, String text) {
        this.thumbImg = thumbImg2;
        this.txt = text;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FilterData that = (FilterData) o;
        if (!ObjectsUtil.equals(this.thumbImg, that.thumbImg) || !ObjectsUtil.equals(this.txt, that.txt)) {
            return false;
        }
        return true;
    }
}
