package com.camscanner.paperscanner.pdfcreator.features.barcode.model;

import com.google.gson.Gson;
import com.google.zxing.client.result.AddressBookParsedResult;
import com.google.zxing.client.result.CalendarParsedResult;
import com.google.zxing.client.result.EmailAddressParsedResult;
import com.google.zxing.client.result.GeoParsedResult;
import com.google.zxing.client.result.ISBNParsedResult;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;
import com.google.zxing.client.result.ProductParsedResult;
import com.google.zxing.client.result.SMSParsedResult;
import com.google.zxing.client.result.TelParsedResult;
import com.google.zxing.client.result.TextParsedResult;
import com.google.zxing.client.result.URIParsedResult;
import com.google.zxing.client.result.VINParsedResult;
import com.google.zxing.client.result.WifiParsedResult;
import org.joda.time.DateTime;

public class ParsedResultAdapter {
    private static final int UNKNOWN = -1;
    private static final int VALUE_ADDRESSBOOK = 0;
    private static final int VALUE_CALENDAR = 8;
    private static final int VALUE_EMAIL_ADDRESS = 1;
    private static final int VALUE_GEO = 5;
    private static final int VALUE_ISBN = 10;
    private static final int VALUE_PRODUCT = 2;
    private static final int VALUE_SMS = 7;
    private static final int VALUE_TEL = 6;
    private static final int VALUE_TEXT = 4;
    private static final int VALUE_URI = 3;
    private static final int VALUE_VIN = 11;
    private static final int VALUE_WIFI = 9;

    public static int typeToInt(ParsedResultType type) {
        switch (type) {
            case ADDRESSBOOK:
                return 0;
            case EMAIL_ADDRESS:
                return 1;
            case PRODUCT:
                return 2;
            case URI:
                return 3;
            case WIFI:
                return 9;
            case GEO:
                return 5;
            case TEL:
                return 6;
            case SMS:
                return 7;
            case CALENDAR:
                return 8;
            case ISBN:
                return 10;
            case TEXT:
                return 4;
            case VIN:
                return 11;
            default:
                return -1;
        }
    }

    public static ParsedResultType intToType(int value) {
        switch (value) {
            case 0:
                return ParsedResultType.ADDRESSBOOK;
            case 1:
                return ParsedResultType.EMAIL_ADDRESS;
            case 2:
                return ParsedResultType.PRODUCT;
            case 3:
                return ParsedResultType.URI;
            case 4:
            default:
                return ParsedResultType.TEXT;
            case 5:
                return ParsedResultType.GEO;
            case 6:
                return ParsedResultType.TEL;
            case 7:
                return ParsedResultType.SMS;
            case 8:
                return ParsedResultType.CALENDAR;
            case 9:
                return ParsedResultType.WIFI;
            case 10:
                return ParsedResultType.ISBN;
            case 11:
                return ParsedResultType.VIN;
        }
    }

    public static QrResult parsedToQrResult(ParsedResult parsedResult, String name, long date) {
        return new QrResult(parsedResult.getType(), new Gson().toJson((Object) parsedResult), name, date);
    }

    public static ParsedResult qrToParsedResult(QrResult qrResult) {
        Gson gson = new Gson();
        switch (qrResult.getType()) {
            case ADDRESSBOOK:
                return (ParsedResult) gson.fromJson(qrResult.getResult(), AddressBookParsedResult.class);
            case EMAIL_ADDRESS:
                return (ParsedResult) gson.fromJson(qrResult.getResult(), EmailAddressParsedResult.class);
            case PRODUCT:
                return (ParsedResult) gson.fromJson(qrResult.getResult(), ProductParsedResult.class);
            case URI:
                return (ParsedResult) gson.fromJson(qrResult.getResult(), URIParsedResult.class);
            case WIFI:
                return (ParsedResult) gson.fromJson(qrResult.getResult(), WifiParsedResult.class);
            case GEO:
                return (ParsedResult) gson.fromJson(qrResult.getResult(), GeoParsedResult.class);
            case TEL:
                return (ParsedResult) gson.fromJson(qrResult.getResult(), TelParsedResult.class);
            case SMS:
                return (ParsedResult) gson.fromJson(qrResult.getResult(), SMSParsedResult.class);
            case CALENDAR:
                return (ParsedResult) gson.fromJson(qrResult.getResult(), CalendarParsedResult.class);
            case ISBN:
                return (ParsedResult) gson.fromJson(qrResult.getResult(), ISBNParsedResult.class);
            case TEXT:
                return (ParsedResult) gson.fromJson(qrResult.getResult(), TextParsedResult.class);
            case VIN:
                return (ParsedResult) gson.fromJson(qrResult.getResult(), VINParsedResult.class);
            default:
                return null;
        }
    }

    public static String getDateDisplay(long date) {
        DateTime dateTime = new DateTime(date);
        return dateTime.getMonthOfYear() + "/" + dateTime.getDayOfMonth() + "/" + dateTime.getYear();
    }
}
