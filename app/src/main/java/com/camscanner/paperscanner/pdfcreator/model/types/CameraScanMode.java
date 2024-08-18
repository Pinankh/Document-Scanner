package com.camscanner.paperscanner.pdfcreator.model.types;

public enum CameraScanMode {
    SINGLE(0),
    BATCH(1);
    
    private int mode;

    private CameraScanMode(int mode2) {
        this.mode = mode2;
    }

    public int value() {
        return this.mode;
    }
}
