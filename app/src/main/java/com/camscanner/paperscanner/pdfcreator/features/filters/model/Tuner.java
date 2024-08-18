package com.camscanner.paperscanner.pdfcreator.features.filters.model;

import java.util.List;
import com.camscanner.paperscanner.pdfcreator.common.utils.GPUImageFilterTools;
import com.camscanner.paperscanner.pdfcreator.common.utils.ObjectsUtil;
import com.camscanner.paperscanner.pdfcreator.model.types.EditFilter;

public class Tuner {
    private TuneData data;

    public Tuner() {
    }

    public Tuner(TuneData data2) {
        this.data = data2;
    }

    public void tune(List<GPUImageFilterTools.FilterAdjuster> adjusters) {
        tuneByData(adjusters, this.data);
    }

    static /* synthetic */ class C67671 {
        static final /* synthetic */ int[] MyEditFilter = new int[EditFilter.values().length];

        static {
            try {
                MyEditFilter[EditFilter.CONTRAST.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                MyEditFilter[EditFilter.BRIGHTNESS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    /* access modifiers changed from: protected */
    public void tuneByData(List<GPUImageFilterTools.FilterAdjuster> adjusters, TuneData data2) {
        int i = C67671.MyEditFilter[data2.filter.ordinal()];
        if (i == 1) {
            adjusters.get(0).adjust(data2.value);
        } else if (i == 2) {
            adjusters.get(1).adjust(data2.value);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return ObjectsUtil.equals(this.data, ((Tuner) o).data);
    }

    public String toString() {
        return "Tuner{data=" + this.data + '}';
    }
}
