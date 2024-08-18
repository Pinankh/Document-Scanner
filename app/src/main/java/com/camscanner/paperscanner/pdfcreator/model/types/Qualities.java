package com.camscanner.paperscanner.pdfcreator.model.types;

public enum Qualities {
    FULL(100),
    DEFAULT_FOR_USER(90),
    OCR_COMPRESSING(90),
    SIGNATURE(100),
    PDF(80);
    
    private int quality;

    private Qualities(int quality2) {
        this.quality = quality2;
    }

    public int value() {
        return this.quality;
    }
}
