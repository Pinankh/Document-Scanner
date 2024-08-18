package com.camscanner.paperscanner.pdfcreator.common.utils;

import android.text.TextUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class StringHelper {
    private static final char[] ILLEGAL_CHARACTERS = {'/', 10, 13, 9, 0, 12, '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};
    private static final String strAM1 = "a.m.";
    private static final String strAM2 = "AM";
    private static final String strAlt = "@";
    private static final String strLocalEN = "en-us";
    private static final String strPM1 = "p.m.";
    private static final String strPM2 = "PM";
    private static final String strSpace = " ";
    private static final String strTimePattern1 = "MM.dd.yyyy";
    private static final String strTimePattern2 = "MM/dd/yyyy";

    public static boolean isEmpty(String str) {
        return TextUtils.isEmpty(str);
    }

    public static String createId() {
        return UUID.randomUUID().toString();
    }

    public static boolean isValidFilename(String filename) {
        for (char symbol : filename.toCharArray()) {
            if (isIllegalCharacter(symbol)) {
                return false;
            }
        }
        return true;
    }

    public static String clearFilename(String filename, boolean generateNew) {
        StringBuilder newName = new StringBuilder();
        for (char symbol : filename.toCharArray()) {
            if (!isIllegalCharacter(symbol)) {
                newName.append(symbol);
            }
        }
        if (generateNew && newName.length() == 0) {
            Random r = new Random();
            for (int i = 0; i < 5; i++) {
                newName.append((char) (r.nextInt(26) + 97));
            }
        }
        return newName.toString();
    }

    private static boolean isIllegalCharacter(char symbol) {
        for (char illegalSymbol : ILLEGAL_CHARACTERS) {
            if (illegalSymbol == symbol) {
                return true;
            }
        }
        return false;
    }

    public static String convertToTitleCase(String input) {
        String cap;
        String[] strArray = input.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String s : strArray) {
            if (s.toLowerCase().equals(strAM1)) {
                cap = strAM2;
            } else if (s.toLowerCase().equals(strPM1)) {
                cap = strPM2;
            } else {
                cap = s.substring(0, 1).toUpperCase() + s.substring(1);
            }
            builder.append(cap + " ");
        }
        return builder.toString();
    }

    public static String getDateFromMills(long time) {
        Locale locale = new Locale(strLocalEN);
        try {
            return convertToTitleCase(new SimpleDateFormat(strTimePattern1, locale).format(new Date(time)));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getDateTimeFromMills(long time) {
        Locale locale = new Locale(strLocalEN);
        try {
            return convertToTitleCase(new SimpleDateFormat(strTimePattern2, locale).format(new Date(time)));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
