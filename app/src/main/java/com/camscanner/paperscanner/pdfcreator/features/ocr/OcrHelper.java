package com.camscanner.paperscanner.pdfcreator.features.ocr;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import com.camscanner.paperscanner.pdfcreator.common.Constant;
import com.camscanner.paperscanner.pdfcreator.common.Retrofit.WebService;
import com.camscanner.paperscanner.pdfcreator.common.p018db.DBManager;
import com.camscanner.paperscanner.pdfcreator.common.utils.BitmapUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.ImageStorageUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.OcrStorageUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.SharedPrefsUtils;
import com.camscanner.paperscanner.pdfcreator.features.ocr.model.OCRLanguage;
import com.camscanner.paperscanner.pdfcreator.features.ocr.model.OCRResponseData;
import com.camscanner.paperscanner.pdfcreator.features.ocr.model.OCRResult;
import com.camscanner.paperscanner.pdfcreator.features.ocr.presentation.OCRActivity;
import com.camscanner.paperscanner.pdfcreator.features.ocr.presentation.OCRResultActivity;
import com.camscanner.paperscanner.pdfcreator.model.Document;
import com.camscanner.paperscanner.pdfcreator.view.element.tagsedittext.TagsEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class OcrHelper {
    public static final String LOG_TAG = OcrHelper.class.getSimpleName();

    public interface OcrListener {
        void onFailure(Throwable th);

        void onPreCall();

        void onSuccess(OCRResult oCRResult);
    }

    public static void recognizeDocument(Activity context, Document document) {
        if (TextUtils.isEmpty(document.textPath) || !new File(document.textPath).exists()) {
            OCRActivity.start(context, document);
        } else {
            OCRResultActivity.start(context, document);
        }
    }

    public static void ocrProcess(Context context, Document document, boolean multiColumns, OcrListener listener) {
        final Context context2 = context;
        final Document document2 = document;
        listener.onPreCall();
        Bitmap original = BitmapUtils.decodeUriWithoutOptimizations(context2, document2.path);
        Bitmap ocrBitmap = BitmapUtils.resizeOcrImage(original);
        if (original != null && !original.equals(ocrBitmap)) {
            original.recycle();
        }
        File file = new File(ImageStorageUtils.saveImageOcr(ocrBitmap));
        RequestBody columns = null;
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create((MediaType) null, file));
        RequestBody app_version = RequestBody.create(MediaType.parse("text/plain"), "1.3");
        RequestBody platform = RequestBody.create(MediaType.parse("text/plain"), "android");
        RequestBody format = RequestBody.create(MediaType.parse("text/plain"), "1");
        if (multiColumns) {
            columns = RequestBody.create(MediaType.parse("text/plain"), "6");
        }
        final OcrListener ocrListener = listener;
        WebService.ocrApi().ocrProcess(fileToUpload, app_version, platform, format, columns, RequestBody.create(MediaType.parse("text/plain"), SharedPrefsUtils.getOCRLanguage(context))).enqueue(new Callback<OCRResponseData>() {
            public void onResponse(@NonNull Call<OCRResponseData> call, @NonNull Response<OCRResponseData> response) {
                if (response.isSuccessful()) {
                    Timber.tag(OcrHelper.LOG_TAG).i("onResponse %s", response.toString());
                    OCRResult ocrResult = OcrHelper.hasText(response) ? OcrHelper.getText(response) : null;
                    if (!OcrHelper.isEmptyResult(ocrResult)) {
                        OcrHelper.onSuccess(context2, ocrResult, document2, ocrListener);
                    } else {
                        OcrHelper.onError(new Throwable("text is empty"), ocrListener);
                    }
                } else {
                    OcrHelper.onError(new Throwable(response.errorBody().toString()), ocrListener);
                }
            }

            public void onFailure(Call<OCRResponseData> call, Throwable t) {
                Timber.tag(OcrHelper.LOG_TAG).w("Failed %s", t);
                OcrHelper.onError(t, ocrListener);
            }
        });
    }


    public static OCRResult getText(@NonNull Response<OCRResponseData> response) {
        return response.body().getText().get(0);
    }


    public static boolean hasText(@NonNull Response<OCRResponseData> response) {
        return (response.body() == null || response.body().getText() == null || response.body().getText().isEmpty()) ? false : true;
    }


    public static boolean isEmptyResult(OCRResult ocrResult) {
        if (ocrResult == null || ocrResult.getText() == null) {
            return true;
        }
        return ocrResult.getText().replace(TagsEditText.NEW_LINE, "").replace("\f", "").replace(" ", "").isEmpty();
    }


    public static void onSuccess(Context context, OCRResult ocrResult, Document document, OcrListener listener) {
        SharedPrefsUtils.setOcrCount(context, SharedPrefsUtils.getOcrCount(context) + 1);
        document.textPath = OcrStorageUtils.saveText(ocrResult.getText());
        DBManager.getInstance().updateDocument(document);
        listener.onSuccess(ocrResult);
    }


    public static void onError(Throwable error, OcrListener listener) {
        listener.onFailure(error);
    }

    public static List<OCRLanguage> getLanguages() {
        List<OCRLanguage> languageList = new ArrayList<>();
        languageList.add(new OCRLanguage(Constant.LANG_AFR_FULL, Constant.LANG_AFR_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_SQI_FULL, Constant.LANG_SQI_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_AMH_FULL, Constant.LANG_AMH_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_ARA_FULL, Constant.LANG_ARA_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_HYE_FULL, Constant.LANG_HYE_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_ASM_FULL, Constant.LANG_ASM_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_AZE_FULL, Constant.LANG_AZE_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_EUS_FULL, Constant.LANG_EUS_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_BEL_FULL, Constant.LANG_BEL_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_BEN_FULL, Constant.LANG_BEN_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_BOS_FULL, Constant.LANG_BOS_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_BRE_FULL, Constant.LANG_BRE_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_BUL_FULL, Constant.LANG_BUL_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_MYA_FULL, Constant.LANG_MYA_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_CAT_FULL, Constant.LANG_CAT_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_CEB_FULL, Constant.LANG_CEB_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_CHR_FULL, Constant.LANG_CHR_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_SIM_CHN_FULL, Constant.LANG_SIM_CHN_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_TRAD_CHN_FULL, Constant.LANG_TRAD_CHN_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_COS_FULL, Constant.LANG_COS_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_HRV_FULL, Constant.LANG_HRV_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_CES_FULL, Constant.LANG_CES_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_DAN_FULL, Constant.LANG_DAN_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_NLD_FULL, Constant.LANG_NLD_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_DZO_FULL, Constant.LANG_DZO_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_ENG_FULL, Constant.LANG_ENG_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_ENM_FULL, Constant.LANG_ENM_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_EPO_FULL, Constant.LANG_EPO_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_EST_FULL, Constant.LANG_EST_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_FAO_FULL, Constant.LANG_FAO_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_FIL_FULL, Constant.LANG_FIL_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_FIN_FULL, Constant.LANG_FIN_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_FRA_FULL, Constant.LANG_FRA_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_FRK_FULL, Constant.LANG_FRK_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_FRM_FULL, Constant.LANG_FRM_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_FRY_FULL, Constant.LANG_FRY_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_GLA_FULL, Constant.LANG_GLA_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_GLG_FULL, Constant.LANG_GLG_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_KAT_FULL, Constant.LANG_KAT_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_GRC_FULL, Constant.LANG_GRC_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_DEU_FULL, Constant.LANG_DEU_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_ELL_FULL, Constant.LANG_ELL_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_GUJ_FULL, Constant.LANG_GUJ_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_HAT_FULL, Constant.LANG_HAT_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_HEB_FULL, Constant.LANG_HEB_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_HIN_FULL, Constant.LANG_HIN_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_HUN_FULL, Constant.LANG_HUN_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_ISL_FULL, Constant.LANG_ISL_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_IKU_FULL, Constant.LANG_IKU_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_IND_FULL, Constant.LANG_IND_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_GLE_FULL, Constant.LANG_GLE_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_ITA_FULL, Constant.LANG_ITA_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_JAP_FULL, Constant.LANG_JAP_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_JAV_FULL, Constant.LANG_JAV_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_KAN_FULL, Constant.LANG_KAN_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_KAZ_FULL, Constant.LANG_KAZ_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_KHM_FULL, Constant.LANG_KHM_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_KIR_FULL, Constant.LANG_KIR_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_KOR_FULL, Constant.LANG_KOR_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_KUR_FULL, Constant.LANG_KUR_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_KMR_FULL, Constant.LANG_KMR_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_LAO_FULL, Constant.LANG_LAO_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_LAT_FULL, Constant.LANG_LAT_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_LAV_FULL, Constant.LANG_LAV_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_LIT_FULL, Constant.LANG_LIT_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_LTZ_FULL, Constant.LANG_LTZ_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_MAL_FULL, Constant.LANG_MAL_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_DIV_FULL, "div"));
        languageList.add(new OCRLanguage(Constant.LANG_MAR_FULL, Constant.LANG_MAR_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_MRI_FULL, Constant.LANG_MRI_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_MKD_FULL, Constant.LANG_MKD_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_MLT_FULL, Constant.LANG_MLT_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_MSA_FULL, Constant.LANG_MSA_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_MON_FULL, Constant.LANG_MON_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_NEP_FULL, Constant.LANG_NEP_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_NOR_FULL, Constant.LANG_NOR_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_OCI_FULL, Constant.LANG_OCI_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_ORI_FULL, Constant.LANG_ORI_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_PAN_FULL, Constant.LANG_PAN_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_FAS_FULL, Constant.LANG_FAS_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_POL_FULL, Constant.LANG_POL_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_POR_FULL, Constant.LANG_POR_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_PUS_FULL, Constant.LANG_PUS_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_QUE_FULL, Constant.LANG_QUE_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_RON_FULL, Constant.LANG_RON_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_RUS_FULL, Constant.LANG_RUS_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_SAN_FULL, Constant.LANG_SAN_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_SRP_FULL, Constant.LANG_SRP_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_SIN_FULL, Constant.LANG_SIN_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_SLK_FULL, Constant.LANG_SLK_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_SLV_FULL, Constant.LANG_SLV_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_SND_FULL, Constant.LANG_SND_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_SPA_FULL, Constant.LANG_SPA_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_SUN_FULL, Constant.LANG_SUN_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_SWA_FULL, Constant.LANG_SWA_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_SWE_FULL, Constant.LANG_SWE_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_SYR_FULL, Constant.LANG_SYR_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_TGL_FULL, Constant.LANG_TGL_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_TGK_FULL, Constant.LANG_TGK_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_TAM_FULL, Constant.LANG_TAM_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_TAT_FULL, Constant.LANG_TAT_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_TEL_FULL, "tel"));
        languageList.add(new OCRLanguage(Constant.LANG_THA_FULL, Constant.LANG_THA_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_TIR_FULL, Constant.LANG_TIR_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_BOD_FULL, Constant.LANG_BOD_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_TON_FULL, Constant.LANG_TON_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_TUR_FULL, Constant.LANG_TUR_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_UIG_FULL, Constant.LANG_UIG_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_UKR_FULL, Constant.LANG_UKR_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_URD_FULL, Constant.LANG_URD_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_UZB_FULL, Constant.LANG_UZB_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_VIE_FULL, Constant.LANG_VIE_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_CYM_FULL, Constant.LANG_CYM_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_YID_FULL, Constant.LANG_YID_SHORT));
        languageList.add(new OCRLanguage(Constant.LANG_YOR_FULL, Constant.LANG_YOR_SHORT));
        return languageList;
    }
}
