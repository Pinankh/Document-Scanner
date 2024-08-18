package com.camscanner.paperscanner.pdfcreator.features.ocr.model;

import java.util.List;

public class SearchResult {
    public final String query;
    public final List<OCRLanguage> results;

    public SearchResult(List<OCRLanguage> results2, String query2) {
        this.results = results2;
        this.query = query2;
    }
}
