package com.camscanner.paperscanner.pdfcreator.features.export;

public enum ExportType {
    SAVE(0),
    SHARE(1);
    
    private final int value;

    private ExportType(int value2) {
        this.value = value2;
    }

    public int value() {
        return this.value;
    }

    public static ExportType get(int value2) {
        if (value2 >= 0 && value2 <= 1) {
            return values()[value2];
        }
        throw new RuntimeException("Invalid value");
    }
}
