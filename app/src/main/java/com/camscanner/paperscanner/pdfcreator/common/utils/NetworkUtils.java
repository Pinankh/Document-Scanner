package com.camscanner.paperscanner.pdfcreator.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.Nullable;

public class NetworkUtils {
    public static final int UNKNOWN = 0;
    public static final int WIFI = 1;
    public static final int _2G = 2;
    public static final int _3G = 3;
    public static final int _4G = 4;
    private final Context context;

    public NetworkUtils(Context context2) {
        this.context = context2;
    }

    public boolean isNetworkAvailable() {
        NetworkInfo info = getNetworkInfo();
        return info != null && info.isConnected();
    }

    public int getNetworkType() {
        NetworkInfo info = getNetworkInfo();
        if (info == null || !info.isConnected()) {
            return 0;
        }
        int type = info.getType();
        int subType = info.getSubtype();
        if (type == 1) {
            return 1;
        }
        if (type != 0) {
            return 0;
        }
        switch (subType) {
            case 1:
            case 2:
            case 4:
            case 7:
            case 11:
                return 2;
            case 3:
            case 5:
            case 6:
            case 8:
            case 9:
            case 10:
            case 12:
            case 14:
            case 15:
                return 3;
            case 13:
                return 4;
            default:
                return 0;
        }
    }

    @Nullable
    private NetworkInfo getNetworkInfo() {
        ConnectivityManager cm = (ConnectivityManager) this.context.getSystemService("connectivity");
        if (cm != null) {
            return cm.getActiveNetworkInfo();
        }
        return null;
    }
}
