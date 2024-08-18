package com.camscanner.paperscanner.pdfcreator.common.utils;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.lang.reflect.Field;
import java.util.List;

public class ProcessUtils {
    private static final String LOG_TAG = ProcessUtils.class.getSimpleName();
    private static final String YANDEX_METRICA = ":metrica";

    public static boolean isMetricaProcess(Context context) {
        return isProcess(context, YANDEX_METRICA);
    }

    private static boolean isProcess(Context context, String name) {
        String processName = getAppProcessName(context);
        return !TextUtils.isEmpty(processName) && processName.toLowerCase().endsWith(name);
    }

    @Nullable
    public static String getAppProcessName(Context context) {
        String processName = null;
        if (Build.VERSION.SDK_INT >= 28) {
            processName = getAppProcessNameP();
        }
        if (TextUtils.isEmpty(processName)) {
            Context appContext = context.getApplicationContext();
            if (appContext instanceof Application) {
                processName = getProcessInvoke((Application) appContext);
            }
        }
        if (TextUtils.isEmpty(processName)) {
            return getAppProcessNameCompatibility(context);
        }
        return processName;
    }

    @RequiresApi(api = 28)
    private static String getAppProcessNameP() {
        return Application.getProcessName();
    }

    @Nullable
    private static String getProcessInvoke(Application application) {
        try {
            Field loadedApkField = application.getClass().getField("mLoadedApk");
            loadedApkField.setAccessible(true);
            Object loadedApk = loadedApkField.get(application);
            Field activityThreadField = loadedApk.getClass().getDeclaredField("mActivityThread");
            activityThreadField.setAccessible(true);
            Object activityThread = activityThreadField.get(loadedApk);
            return (String) activityThread.getClass().getDeclaredMethod("getProcessName", (Class[]) null).invoke(activityThread, (Object[]) null);
        } catch (Exception error) {
            Log.e(LOG_TAG, "getProcessInvoke", error);
            return null;
        }
    }

    @Nullable
    private static String getAppProcessNameCompatibility(Context context) {
        List<ActivityManager.RunningAppProcessInfo> infos;
        int pid = Process.myPid();
        ActivityManager manager = (ActivityManager) context.getSystemService("activity");
        if (!(manager == null || (infos = manager.getRunningAppProcesses()) == null)) {
            for (ActivityManager.RunningAppProcessInfo processInfo : infos) {
                if (processInfo.pid == pid) {
                    return processInfo.processName;
                }
            }
        }
        return null;
    }
}
