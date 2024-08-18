package com.camscanner.paperscanner.pdfcreator.common.utils.LanguageUtil;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Locale;
import com.camscanner.paperscanner.pdfcreator.common.Constant;

public class SPUtil {
    private static volatile SPUtil instance;
    private final String SP_NAME = "language_setting";
    private final String TAG_LANGUAGE = "language_select";
    private final String TAG_SYSTEM_LANGUAGE = "system_language";
    private final SharedPreferences mSharedPreferences;
    private Locale systemCurrentLocal = Locale.ENGLISH;

    public SPUtil(Context context) {
        this.mSharedPreferences = context.getSharedPreferences("language_setting", 0);
    }

    public void saveLanguage(int select) {
        SharedPreferences.Editor edit = this.mSharedPreferences.edit();
        edit.putInt("language_select", select);
        edit.commit();
    }

    public int getSelectLanguage() {
        return this.mSharedPreferences.getInt("language_select", 0);
    }

    public Locale getSystemCurrentLocal() {
        return this.systemCurrentLocal;
    }

    public void setSystemCurrentLocal(Context context, Locale local) {
        int index = -1;
        for (int i = 0; i < Constant.APP_LANGUAGE_LIST.length; i++) {
            if (local.getLanguage().equals(Constant.APP_LANGUAGE_LIST[i])) {
                index = i + 1;
            }
        }
        if (index == -1) {
            index = 3;
            local = Locale.ENGLISH;
        }
        getInstance(context).saveLanguage(index);
        this.systemCurrentLocal = local;
    }

    public static SPUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (SPUtil.class) {
                if (instance == null) {
                    instance = new SPUtil(context);
                }
            }
        }
        return instance;
    }
}
