package com.camscanner.paperscanner.pdfcreator.features.filters.model;

import android.graphics.Bitmap;
import com.camscanner.paperscanner.pdfcreator.common.utils.ObjectsUtil;
import com.camscanner.paperscanner.pdfcreator.model.types.FilterFlag;

public class UpdateFilter {
    public final Bitmap bitmap;
    public final FilterFlag filter;

    public UpdateFilter(Bitmap bitmap2, FilterFlag filter2) {
        this.bitmap = bitmap2;
        this.filter = filter2;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UpdateFilter that = (UpdateFilter) o;
        if (!ObjectsUtil.equals(this.bitmap, that.bitmap) || this.filter != that.filter) {
            return false;
        }
        return true;
    }
}
