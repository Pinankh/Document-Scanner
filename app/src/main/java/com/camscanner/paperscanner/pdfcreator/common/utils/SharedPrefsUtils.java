package com.camscanner.paperscanner.pdfcreator.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.camscanner.paperscanner.pdfcreator.model.types.Qualities;
import com.camscanner.paperscanner.pdfcreator.model.types.Resolution;

public class SharedPrefsUtils {
    private static final String ADS_CONSENT = "ads_consent";
    private static final String APPSEE_ASKED = "appsee_asked";
    private static final String APPSEE_ENABLED = "appsee_enabled";
    private static final String BATCH_COLOR_MODE = "BATCH_COLOR_MODE";
    private static final String BATCH_CROP_MODE = "CROP_MODE";
    private static final String CLOUD_SHOULD_SYNC = "CLOUD_SHOULD_SYNC";
    private static final String CLOUD_SYNCED = "CLOUD_SYNCED";
    private static final String CLOUD_TYPE = "CLOUD_TYPE";
    private static final String CLOUD_WIFI_ONLY = "CLOUD_WIFI_ONLY";
    private static final String CROP_OPENED = "tutor_crop_opened";
    private static final String CROP_TUTORIAL = "crop_tutorial";
    private static final String CUR_SIGNATURE = "CUR_SIGNATURE";
    private static final String DATE_COLOR = "date_color";
    private static final String DATE_FORMAT = "date_format";
    private static final String EDIT_OPENED = "tutor_edit_opened";
    private static final String FILTERS_OPENED = "tutor_filters_opened";
    private static final String FIRST_OPEN_APP = "FIRST_OPEN_APP_2264";
    private static final String FIRST_TIME_IN_GRID = "FIRST_TIME_IN_GRID";
    private static final String FLASH_MODE = "camera_flash_mode";
    private static final String GRID_OPENED = "tutor_grid_opened";
    private static final String IMG_OUTPUT_SIZE = "IMG_OUTPUT_SIZE";
    private static final String IMG_SCAN_SIZE = "IMG_SCAN_SIZE";
    private static final String IMG_SETTING_QUALITY = "IMG_SETTING_QUALITY";
    private static final String IS_PREMIUM = "IS_PREMIUM";
    private static final String LIMITED_PROMO_FIRST = "limited_promo_first";
    private static final String LIMITED_SCREEN_DATE = "limited_date_2264";
    private static final String MAIN_OPENED = "tutor_main_opened";
    private static final String NAME_TEMPLATE = "NAME_TEMPLATE";
    private static final String OCR_COUNT = "ocr_limit_count";
    private static final String OCR_LANGUAGE = "new_ocr_lang";
    private static final String OCR_SELECT_LANG_SHOWN = "select_lang_shown";
    public static final String OCR_SYSTEM_LANGUAGE = "ocr_system_lang";
    private static final String PDF_DIRECTTION = "PDF_DIRECTION";
    private static final String PDF_PAGE_SELECTED = "PDF_PAGE_SELECTED";
    private static final String PDF_PASSWORD = "PDF_PASSWORD";
    private static final String REMINDER_ORDER = "reminder_order";
    private static final String REPORT_AFTER_TRIAL = "report_after_trial";
    private static final String SAVE_TO_ALBUM = "SAVE_ALBUM";
    private static final String SCAN_DOC_CNT = "SCAN_DOC_CNT";
    private static final String SINGLE_COLOR_MODE = "SINGLE_COLOR_MODE";
    private static final String TAG_TEXT = "TAG_TEXT";
    private static final String strDefNameTemplate = "\ue530\ue531\ue532";
    private static final String strDefPage = "A4";
    private static final String strDefTagName = "New Doc";

    public static long getLastConsentShown(Context context) {
        return getPref(context).getLong(ADS_CONSENT, -1);
    }

    public static void setLastConsentShown(Context context, long value) {
        getPref(context).edit().putLong(ADS_CONSENT, value).apply();
    }

    public static long getDateLimitedScreenOpened(Context context) {
        return getPref(context).getLong(LIMITED_SCREEN_DATE, -1);
    }

    public static void setDateLimitedScreenOpened(Context context, long value) {
        getPref(context).edit().putLong(LIMITED_SCREEN_DATE, value).apply();
    }

    public static String getCameraFlashMode(Context context) {
        return getPref(context).getString(FLASH_MODE, "auto");
    }

    public static void setCameraFlashMode(Context context, String value) {
        getPref(context).edit().putString(FLASH_MODE, value).apply();
    }

    private static SharedPreferences getPref(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean isFirstOpenApp(Context context) {
        return getPref(context).getBoolean(FIRST_OPEN_APP, true);
    }

    public static void setFirstOpenApp(Context context, boolean bFirstOpenApp) {
        getPref(context).edit().putBoolean(FIRST_OPEN_APP, bFirstOpenApp).apply();
    }

    public static final boolean isFirstTimeInGrid(Context context) {
        return getPref(context).getBoolean(FIRST_TIME_IN_GRID, true);
    }

    public static void setFirstTimeInGrid(Context context, boolean bFirstTimeInGrid) {
        getPref(context).edit().putBoolean(FIRST_TIME_IN_GRID, bFirstTimeInGrid).apply();
    }

    public static void setIsPremium(Context context, boolean isPremium) {
        getPref(context).edit().putBoolean(IS_PREMIUM, isPremium).apply();
    }

    public static boolean isUserPremium(Context context) {
        return getPref(context).getBoolean(IS_PREMIUM, false);
    }

    public static void setNameTemplate(Context context, String strTemplate) {
        getPref(context).edit().putString(NAME_TEMPLATE, strTemplate).apply();
    }

    public static String getNameTemplate(Context context) {
        return getPref(context).getString(NAME_TEMPLATE, strDefNameTemplate);
    }

    public static void setTagText(Context context, String strTagText) {
        getPref(context).edit().putString(TAG_TEXT, strTagText).apply();
    }

    public static String getTagText(Context context) {
        return getPref(context).getString(TAG_TEXT, strDefTagName);
    }

    public static void setSingleColorMode(Context context, int colorMode) {
        getPref(context).edit().putInt(SINGLE_COLOR_MODE, colorMode).apply();
    }

    public static int getSingleColorMode(Context context) {
        return getPref(context).getInt(SINGLE_COLOR_MODE, 0);
    }

    public static void setBatchColorMode(Context context, int colorMode) {
        getPref(context).edit().putInt(BATCH_COLOR_MODE, colorMode).apply();
    }

    public static int getBatchColorMode(Context context) {
        return getPref(context).getInt(BATCH_COLOR_MODE, 0);
    }

    public static void setBatchCropMode(Context context, int cropMode) {
        getPref(context).edit().putInt(BATCH_CROP_MODE, cropMode).apply();
    }

    public static int getBatchCropMode(Context context) {
        return getPref(context).getInt(BATCH_CROP_MODE, 1);
    }

    public static void setSaveToAlbum(Context context, boolean bSaveToAlbum) {
        getPref(context).edit().putBoolean(SAVE_TO_ALBUM, bSaveToAlbum).apply();
    }

    public static boolean getSaveToAlbum(Context context) {
        return getPref(context).getBoolean(SAVE_TO_ALBUM, false);
    }

    public static void setPDFPassword(Context context, String strPassword) {
        getPref(context).edit().putString(PDF_PASSWORD, strPassword).apply();
    }

    public static String getPDFPassword(Context context) {
        return getPref(context).getString(PDF_PASSWORD, "");
    }

    public static void setPDFDirection(Context context, int mode) {
        getPref(context).edit().putInt(PDF_DIRECTTION, mode).apply();
    }

    public static int getPDFDirecttion(Context context) {
        return getPref(context).getInt(PDF_DIRECTTION, 1);
    }

    public static void setPDFPageSelected(Context context, String strPage) {
        getPref(context).edit().putString(PDF_PAGE_SELECTED, strPage).apply();
    }

    public static String getPDFPageSelected(Context context) {
        return getPref(context).getString(PDF_PAGE_SELECTED, "A4");
    }

    public static void setScanSize(Context context, Resolution resolution) {
        getPref(context).edit().putInt(IMG_SCAN_SIZE, resolution.pos()).apply();
    }

    public static Resolution getScanSize(Context context) {
        return Resolution.get(getPref(context).getInt(IMG_SCAN_SIZE, Resolution.HIGH.pos()));
    }

    public static void setOutputSize(Context context, Resolution resolution) {
        getPref(context).edit().putInt(IMG_OUTPUT_SIZE, resolution.pos()).apply();
    }

    public static Resolution getOutputSize(Context context) {
        return Resolution.get(getPref(context).getInt(IMG_OUTPUT_SIZE, Resolution.HIGH.pos()));
    }

    public static void setImgQuality(Context context, int nQuality) {
        getPref(context).edit().putInt(IMG_SETTING_QUALITY, nQuality).apply();
    }

    public static int getImgQuality(Context context) {
        return getPref(context).getInt(IMG_SETTING_QUALITY, Qualities.DEFAULT_FOR_USER.value());
    }

    public static void setCloudType(Context context, int nCloudeType) {
        getPref(context).edit().putInt(CLOUD_TYPE, nCloudeType).apply();
    }

    public static int getCloudType(Context context) {
        return getPref(context).getInt(CLOUD_TYPE, 0);
    }

    public static void setCloudSynced(Context context, boolean bCloudSynced) {
        getPref(context).edit().putBoolean(CLOUD_SYNCED, bCloudSynced).apply();
    }

    public static boolean getCloudSynced(Context context) {
        return getPref(context).getBoolean(CLOUD_SYNCED, false);
    }

    public static void setCloudWifiOnly(Context context, boolean bWifiOnly) {
        getPref(context).edit().putBoolean(CLOUD_WIFI_ONLY, bWifiOnly).apply();
    }

    public static boolean getCloudWifiOnly(Context context) {
        return getPref(context).getBoolean(CLOUD_WIFI_ONLY, false);
    }

    public static void setOCRLanguage(Context context, String ocrLang) {
        getPref(context).edit().putString(OCR_LANGUAGE, ocrLang).apply();
    }

    public static String getOCRLanguage(Context context) {
        return getPref(context).getString(OCR_LANGUAGE, OCR_SYSTEM_LANGUAGE);
    }

    public static void setCurSignature(Context context, String path) {
        getPref(context).edit().putString(CUR_SIGNATURE, path).apply();
    }

    public static String getCurSignature(Context context) {
        return getPref(context).getString(CUR_SIGNATURE, "");
    }

    public static void setScanDocCnt(Context context, int cnt) {
        getPref(context).edit().putInt(SCAN_DOC_CNT, cnt).apply();
    }

    public static int getScanDocCnt(Context context) {
        return getPref(context).getInt(SCAN_DOC_CNT, 0);
    }

    public static void setCloudShouldSync(Context context, boolean bShould) {
        getPref(context).edit().putBoolean(CLOUD_SHOULD_SYNC, bShould).apply();
    }

    public static boolean getCloudShouldSync(Context context) {
        return getPref(context).getBoolean(CLOUD_SHOULD_SYNC, false);
    }

    public static void setNeedReportAfterTrial(Context context, boolean need) {
        getPref(context).edit().putBoolean(REPORT_AFTER_TRIAL, need).apply();
    }

    public static boolean isNeedReportAfterTrial(Context context) {
        return getPref(context).getBoolean(REPORT_AFTER_TRIAL, true);
    }

    public static void setAppseeEnabled(Context context, boolean enabled) {
        getPref(context).edit().putBoolean(APPSEE_ENABLED, enabled).putBoolean(APPSEE_ASKED, true).apply();
    }

    public static boolean isAppseeEnabled(Context context) {
        return getPref(context).getBoolean(APPSEE_ENABLED, false);
    }

    public static boolean isAppseeAsked(Context context) {
        return getPref(context).getBoolean(APPSEE_ASKED, false);
    }

    public static boolean isPromoShownFirst(Context context) {
        return getPref(context).getBoolean(LIMITED_PROMO_FIRST, true);
    }

    public static void setPromoShownFirst(Context context, boolean shown) {
        getPref(context).edit().putBoolean(LIMITED_PROMO_FIRST, shown).apply();
    }

    public static void setDateColor(Context context, int value) {
        getPref(context).edit().putInt(DATE_COLOR, value).apply();
    }

    public static int getDateColor(Context context, int defvalue) {
        return getPref(context).getInt(DATE_COLOR, defvalue);
    }

    public static void setDateFormat(Context context, int value) {
        getPref(context).edit().putInt(DATE_FORMAT, value).apply();
    }

    public static int getDateFormat(Context context, int defvalue) {
        return getPref(context).getInt(DATE_FORMAT, defvalue);
    }

    public static boolean isOCRSelectDialogShown(Context context) {
        return getPref(context).getBoolean(OCR_SELECT_LANG_SHOWN, false);
    }

    public static void setOCRSelectDialogShown(Context context, boolean shown) {
        getPref(context).edit().putBoolean(OCR_SELECT_LANG_SHOWN, shown).apply();
    }

    public static void setOcrCount(Context context, int value) {
        getPref(context).edit().putInt(OCR_COUNT, value).apply();
    }

    public static int getOcrCount(Context context) {
        return getPref(context).getInt(OCR_COUNT, 0);
    }

    public static void setReminderOrder(Context context, int value) {
        getPref(context).edit().putInt(REMINDER_ORDER, value).apply();
    }

    public static int getReminderOrder(Context context) {
        return getPref(context).getInt(REMINDER_ORDER, 0);
    }

    public static int getMainOpened(Context context) {
        return getTutorOpened(context, MAIN_OPENED);
    }

    public static void setMainOpened(Context context, int count) {
        setTutorOpened(context, MAIN_OPENED, count);
    }

    public static int getCropOpened(Context context) {
        return getTutorOpened(context, CROP_OPENED);
    }

    public static void setCropOpened(Context context, int count) {
        setTutorOpened(context, CROP_OPENED, count);
    }

    public static int getGridOpened(Context context) {
        return getTutorOpened(context, GRID_OPENED);
    }

    public static void setGridOpened(Context context, int count) {
        setTutorOpened(context, GRID_OPENED, count);
    }

    public static int getFiltersOpened(Context context) {
        return getTutorOpened(context, FILTERS_OPENED);
    }

    public static void setFiltersOpened(Context context, int count) {
        setTutorOpened(context, FILTERS_OPENED, count);
    }

    public static int getEditOpened(Context context) {
        return getTutorOpened(context, EDIT_OPENED);
    }

    public static void setEditOpened(Context context, int count) {
        setTutorOpened(context, EDIT_OPENED, count);
    }

    private static int getTutorOpened(Context context, String tutorial) {
        return getPref(context).getInt(tutorial, 0) + 1;
    }

    private static void setTutorOpened(Context context, String tutorial, int count) {
        getPref(context).edit().putInt(tutorial, count).apply();
    }
}
