package com.camscanner.paperscanner.pdfcreator.features.barcode;

import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ResultParser;
import com.tom_roush.pdfbox.pdmodel.interactive.action.PDActionURI;

import org.joda.time.DateTime;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import com.camscanner.paperscanner.pdfcreator.common.p018db.DBManager;
import com.camscanner.paperscanner.pdfcreator.features.barcode.model.ParsedResultAdapter;
import com.camscanner.paperscanner.pdfcreator.features.barcode.model.QrResult;

public final class QrResultHandler {
    private final DBManager dbManager;

    public QrResultHandler(DBManager dbManager2) {
        this.dbManager = dbManager2;
    }

    public Single<QrResult> handleResult(Result rawResult) {

        Single map = Single.just(rawResult).map(new Function<Result, Object>() {
            @Override
            public Object apply(Result result) throws Exception {
                return ResultParser.parseResult(result);
            }
        }).map(new Function() {
            public final Object apply(Object obj) {
                return QrResultHandler.this.createQrResult((ParsedResult) obj);
            }
        });
        final DBManager dBManager = this.dbManager;
        dBManager.getClass();
        return map.doOnSuccess(new Consumer() {
            public final void accept(Object obj) {
                dBManager.addQrResult((QrResult) obj);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /* access modifiers changed from: private */
    public QrResult createQrResult(ParsedResult result) {
        return ParsedResultAdapter.parsedToQrResult(result, generateName(result), DateTime.now().getMillis());
    }

    private String generateName(ParsedResult result) {
        switch (result.getType()) {
            case ADDRESSBOOK:
                return "AddressBook";
            case EMAIL_ADDRESS:
                return "Email";
            case PRODUCT:
                return "Product";
            case URI:
                return PDActionURI.SUB_TYPE;
            case WIFI:
                return "Wi-Fi";
            case GEO:
                return "Geo";
            case TEL:
                return "Phone Number";
            case SMS:
                return "SMS";
            case CALENDAR:
                return "Calendar";
            case ISBN:
                return "ISBN";
            case TEXT:
                return "Text";
            case VIN:
                return "VIN";
            default:
                return "New File";
        }
    }
}
