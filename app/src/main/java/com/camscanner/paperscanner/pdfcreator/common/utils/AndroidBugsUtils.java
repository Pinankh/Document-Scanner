package com.camscanner.paperscanner.pdfcreator.common.utils;

import android.content.ClipData;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.Nullable;

public class AndroidBugsUtils {
    public static boolean isClipDataFromKeyboard(Context screen, @Nullable ClipData clipData) {
        Uri uri;
        Context context = screen.getApplicationContext();
        if (clipData == null || clipData.getItemCount() == 0 || (uri = clipData.getItemAt(0).getUri()) == null) {
            return false;
        }
        String authority = uri.getAuthority();
        if (TextUtils.isEmpty(authority)) {
            return false;
        }
        String defaultInputMethod = Settings.Secure.getString(context.getContentResolver(), "default_input_method");
        if (TextUtils.isEmpty(defaultInputMethod)) {
            return false;
        }
        try {
            ProviderInfo[] providers = context.getPackageManager().getPackageInfo(defaultInputMethod.split("/")[0], 8).providers;
            if (providers != null) {
                if (providers.length != 0) {
                    for (ProviderInfo provider : providers) {
                        if (TextUtils.equals(authority, provider.authority)) {
                            return true;
                        }
                    }
                    return false;
                }
            }
            return false;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return false;
    }
}
