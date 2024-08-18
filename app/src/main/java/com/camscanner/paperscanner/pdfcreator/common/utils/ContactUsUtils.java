package com.camscanner.paperscanner.pdfcreator.common.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.multidex.BuildConfig;

import com.camscanner.paperscanner.pdfcreator.common.Constant;
import com.camscanner.paperscanner.pdfcreator.view.element.tagsedittext.TagsEditText;

public class ContactUsUtils {
    private static final String[] EMAIL_RECIPIENTS = {"app-support@smedia.co.il"};
    public static final int NO_RATED = -2;

    public static void sendMail(Activity activity, String feedback, int starsRated) {
        Intent sendIntent = new Intent("android.intent.action.SENDTO");
        sendIntent.setData(Uri.parse("mailto:"));
        sendIntent.putExtra("android.intent.extra.EMAIL", EMAIL_RECIPIENTS);
        sendIntent.putExtra("android.intent.extra.SUBJECT", getApplicationName(activity) + " Feedback");
        sendIntent.putExtra("android.intent.extra.TEXT", getAdjustedFeedback(feedback, starsRated));
        try {
            activity.startActivity(Intent.createChooser(sendIntent, "Send mail..."));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "There are no email clients installed.", 0).show();
        }
    }

    private static String getAdjustedFeedback(String feedback, int starsRated) {
        String adjustedFeedback;
        if (feedback == null) {
            adjustedFeedback = "";
        } else {
            try {
                adjustedFeedback = feedback + "\n\n";
            } catch (Exception e) {
                return feedback;
            }
        }
        if (starsRated != -2) {
            adjustedFeedback = adjustedFeedback + "Rated: " + starsRated + TagsEditText.NEW_LINE;
        }
        return adjustedFeedback + getAndroidVersion() + TagsEditText.NEW_LINE + getDeviceName() + "\nApplication version: " + BuildConfig.VERSION_NAME + "\nAB version: " + 8;
    }

    private static String getAndroidVersion() {
        try {
            String release = Build.VERSION.RELEASE;
            int sdkVersion = Build.VERSION.SDK_INT;
            return "Android SDK: " + sdkVersion + " (" + release + Constant.STR_BRACKET_CLOSE;
        } catch (Exception e) {
            return "";
        }
    }

    private static String getDeviceName() {
        String finalName;
        try {
            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
            if (model.startsWith(manufacturer)) {
                finalName = capitalize(model);
            } else {
                finalName = capitalize(manufacturer) + " " + model;
            }
            return "Device: " + finalName;
        } catch (Exception e) {
            return "";
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        }
        return Character.toUpperCase(first) + s.substring(1);
    }

    private static String getApplicationName(Context context) {
        try {
            ApplicationInfo applicationInfo = context.getApplicationInfo();
            int stringId = applicationInfo.labelRes;
            return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
        } catch (Exception e) {
            return "";
        }
    }
}
