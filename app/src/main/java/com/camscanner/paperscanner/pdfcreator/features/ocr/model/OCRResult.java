package com.camscanner.paperscanner.pdfcreator.features.ocr.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class OCRResult implements Serializable {
    @SerializedName("file")
    private String file;
    @SerializedName("text")
    private String text;

    public OCRResult(String file2, String text2) {
        this.file = file2;
        this.text = text2;
    }

    public String getFile() {
        return this.file;
    }

    public void setFile(String file2) {
        this.file = file2;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text2) {
        this.text = text2;
    }
}
