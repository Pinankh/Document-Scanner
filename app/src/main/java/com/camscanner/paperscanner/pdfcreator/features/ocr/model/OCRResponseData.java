package com.camscanner.paperscanner.pdfcreator.features.ocr.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class OCRResponseData {
    @SerializedName("text")
    private final ArrayList<OCRResult> text;

    public OCRResponseData(ArrayList<OCRResult> text2) {
        this.text = text2;
    }

    public ArrayList<OCRResult> getText() {
        return this.text;
    }
}
