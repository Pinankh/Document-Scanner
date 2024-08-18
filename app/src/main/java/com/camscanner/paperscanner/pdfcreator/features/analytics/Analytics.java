package com.camscanner.paperscanner.pdfcreator.features.analytics;

import android.app.Application;

import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;

public class Analytics {
    private static volatile Analytics INSTANCE = null;
    private static final String LOG_TAG = "ANALYTICS_LOG";

    public Analytics(Application application) {

        YandexMetrica.activate(application, YandexMetricaConfig.newConfigBuilder("7749cc96-ab62-40f4-896e-8161b2989d98").build());
        YandexMetrica.enableActivityAutoTracking(application);
    }

    public static void init(Application application) {
        INSTANCE = new Analytics(application);
    }

//    public static Analytics get() {
//        if (INSTANCE != null) {
//            return INSTANCE;
//        }
//        throw new RuntimeException("need to call init() first");
//    }



























}
