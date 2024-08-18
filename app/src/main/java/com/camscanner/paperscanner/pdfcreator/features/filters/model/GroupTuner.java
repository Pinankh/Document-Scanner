package com.camscanner.paperscanner.pdfcreator.features.filters.model;

import java.util.ArrayList;
import java.util.List;
import com.camscanner.paperscanner.pdfcreator.common.utils.GPUImageFilterTools;
import com.camscanner.paperscanner.pdfcreator.common.utils.ObjectsUtil;

public class GroupTuner extends Tuner {
    private List<TuneData> dataList = new ArrayList();

    public GroupTuner(List<TuneData> dataList2) {
        this.dataList.addAll(dataList2);
    }

    public void tune(List<GPUImageFilterTools.FilterAdjuster> adjusters) {
        for (TuneData data : this.dataList) {
            tuneByData(adjusters, data);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass() || !super.equals(o)) {
            return false;
        }
        return ObjectsUtil.equals(this.dataList, ((GroupTuner) o).dataList);
    }

    public String toString() {
        return "GroupTuner{dataList=" + this.dataList + '}';
    }
}
