package com.camscanner.paperscanner.pdfcreator.common.Retrofit;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import com.camscanner.paperscanner.pdfcreator.features.ocr.model.OCRResponseData;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface OcrApi {
    @POST("/upload")
    @Multipart
    Call<OCRResponseData> ocrProcess(@Part MultipartBody.Part part, @Part("app_version") RequestBody requestBody, @Part("platform") RequestBody requestBody2, @Part("format") RequestBody requestBody3, @Part("psm") RequestBody requestBody4, @Part("lang") RequestBody requestBody5);
}
