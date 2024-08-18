package com.camscanner.paperscanner.pdfcreator.common.Retrofit;

import com.tapscanner.polygondetect.GsonConverterFactory;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class WebService {
    private static String BASE_URL = "https://ocr.y0.com";
    private static final int TIME_LIMIT = 20;
    private static volatile OcrApi ocrApi;
    private static volatile Retrofit retrofit;

    public static OcrApi ocrApi() {
        if (ocrApi == null) {
            synchronized (WebService.class) {
                if (ocrApi == null) {
                    ocrApi = (OcrApi) getRetrofitInstance().create(OcrApi.class);
                }
            }
        }
        return ocrApi;
    }

    private static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            synchronized (WebService.class) {
                if (retrofit == null) {
                    retrofit = new Retrofit.Builder().baseUrl(BASE_URL).client(buildClient()).addConverterFactory(GsonConverterFactory.create()).build();
                }
            }
        }
        return retrofit;
    }

    private static OkHttpClient buildClient() {
        return new OkHttpClient.Builder().retryOnConnectionFailure(true).connectTimeout(20, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS).writeTimeout(20, TimeUnit.SECONDS).build();
    }
}
