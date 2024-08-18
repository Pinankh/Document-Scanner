package com.camscanner.paperscanner.pdfcreator.features.ocr.model;

import com.camscanner.paperscanner.pdfcreator.common.utils.ObjectsUtil;

public class OCRLanguage {
    public final String code;
    public final String language;
    public final String lowerLanguage;

    public OCRLanguage(String langFull, String langShort) {
        this.language = langFull;
        this.lowerLanguage = langFull.toLowerCase();
        this.code = langShort;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return ObjectsUtil.equals(this.code, ((OCRLanguage) o).code);
    }

    public String toString() {
        return "OCRLanguage{language='" + this.language + '\'' + ", code='" + this.code + '\'' + '}';
    }
}
