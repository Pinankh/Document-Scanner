package com.camscanner.paperscanner.pdfcreator.common.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.camscanner.paperscanner.pdfcreator.common.Constant;
import com.camscanner.paperscanner.pdfcreator.features.appsee.AppseeConsentDialogFragment;

public class PrivacyUtil {
    private static final String LOG_TAG = PrivacyUtil.class.getSimpleName();

    public static boolean isPrivacySettingsVisible(Context context) {
        return isAdConsentVisible(context) || isCollectConsentVisible();
    }

    public static boolean isAdConsentVisible(Context context) {
        return false;
    }

    public static boolean isCollectConsentVisible() {
        return false;
    }

    public static void goToPrivacyPolicy(Fragment fragment) {
        fragment.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(Constant.PRIVACY_POLICY_URL)));
    }

    public static void goToPrivacyPolicy(Context context) {
        context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(Constant.PRIVACY_POLICY_URL)));
    }

    public static void showAdvertisementConsentDialog(FragmentActivity activity) {
    }

    public static void showCollectingConsentDialog(final FragmentActivity activity, boolean force, boolean premium) {
        if (!isCollectConsentVisible()) {
            return;
        }
        if (force || !SharedPrefsUtils.isAppseeAsked(activity)) {
            AppseeConsentDialogFragment.newInstance().setAgreeButtonListener(new AppseeConsentDialogFragment.OnButtonClickListener() {
                public final void onClick() {
                    SharedPrefsUtils.setAppseeEnabled(activity, true);
                }
            }).setNotAgreeButtonListener(new AppseeConsentDialogFragment.OnButtonClickListener() {
                public final void onClick() {
                    SharedPrefsUtils.setAppseeEnabled(activity, false);
                }
            }).setPayButtonListener(premium ? null : new AppseeConsentDialogFragment.OnButtonClickListener() {
                public final void onClick() {
                }
            }).showDialog(activity);
        }
    }
}
