package com.camscanner.paperscanner.pdfcreator.features.filters.model;

import com.camscanner.paperscanner.pdfcreator.model.types.EditFilter;

public class TuneData {
    public final EditFilter filter;
    public final int value;

    public TuneData(EditFilter filter2, int value2) {
        this.filter = filter2;
        this.value = value2;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TuneData tuneData = (TuneData) o;
        if (this.value == tuneData.value && this.filter == tuneData.filter) {
            return true;
        }
        return false;
    }

    public String toString() {
        return "TuneData{filter=" + this.filter + ", value=" + this.value + '}';
    }
}
