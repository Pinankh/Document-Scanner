package com.camscanner.paperscanner.pdfcreator.common.utils.LanguageUtil;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;
import android.util.Log;
import com.zhihu.matisse.MatisseLanguageUtils;
import java.util.Locale;
import com.camscanner.paperscanner.pdfcreator.common.Constant;

public class LocalManageUtil implements MatisseLanguageUtils {
    private static final String TAG = "LocalManageUtil";
    private static volatile LocalManageUtil instance;

    public static LocalManageUtil getInstance() {
        if (instance == null) {
            synchronized (SPUtil.class) {
                if (instance == null) {
                    instance = new LocalManageUtil();
                }
            }
        }
        return instance;
    }

    public void setApplicationLanguage(Context context) {
        Resources resources = context.getApplicationContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        Locale locale = getLanguageLocale(context);
        config.locale = locale;
        if (Build.VERSION.SDK_INT >= 24) {
            LocaleList localeList = new LocaleList(new Locale[]{locale});
            LocaleList.setDefault(localeList);
            config.setLocales(localeList);
            context.getApplicationContext().createConfigurationContext(config);
            Locale.setDefault(locale);
        }
        resources.updateConfiguration(config, dm);
    }

    public Context updateLocal(Context context) {
        Locale locale = getLanguageLocale(context);
        Locale.setDefault(locale);
        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        if (Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale);
            return context.createConfigurationContext(config);
        }
        config.locale = locale;
        res.updateConfiguration(config, res.getDisplayMetrics());
        return context;
    }

    public void onConfigurationChanged(Context context) {
        saveSystemCurrentLanguage(context);
        updateLocal(context);
        setApplicationLanguage(context);
    }

    private void saveSystemCurrentLanguage(Context context) {
        Locale locale;
        if (Build.VERSION.SDK_INT >= 24) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }
        Log.d(TAG, locale.getLanguage());
        SPUtil.getInstance(context).setSystemCurrentLocal(context, locale);
    }

    private Locale getSystemLocale(Context context) {
        return SPUtil.getInstance(context).getSystemCurrentLocal();
    }

    public Locale getLanguageLocale(Context context) {
        int lang = SPUtil.getInstance(context).getSelectLanguage();
        if (lang == 0) {
            saveSystemCurrentLanguage(context);
            return getSystemLocale(context);
        } else if (lang < 1 || lang > Constant.APP_LANGUAGE_LIST.length) {
            return getSystemLocale(context);
        } else {
            return new Locale(Constant.APP_LANGUAGE_LIST[lang - 1]);
        }
    }

    public void saveSelectLanguage(Context context, int select) {
        SPUtil.getInstance(context).saveLanguage(select);
        setApplicationLanguage(context);
    }
}
