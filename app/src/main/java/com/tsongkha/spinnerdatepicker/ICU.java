package com.tsongkha.spinnerdatepicker;

public class ICU {
    public static char[] getDateFormatOrder(String pattern) {
        char[] result = new char[3];
        int resultIndex = 0;
        boolean sawDay = false;
        boolean sawMonth = false;
        boolean sawYear = false;
        int i = 0;
        while (i < pattern.length()) {
            char ch = pattern.charAt(i);
            if (ch == 'd' || ch == 'L' || ch == 'M' || ch == 'y') {
                if (ch == 'd' && !sawDay) {
                    result[resultIndex] = 'd';
                    sawDay = true;
                    resultIndex++;
                } else if ((ch == 'L' || ch == 'M') && !sawMonth) {
                    result[resultIndex] = 'M';
                    sawMonth = true;
                    resultIndex++;
                } else if (ch == 'y' && !sawYear) {
                    result[resultIndex] = 'y';
                    sawYear = true;
                    resultIndex++;
                }
            } else if (ch == 'G') {
                continue;
            } else if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
                throw new IllegalArgumentException("Bad pattern character '" + ch + "' in " + pattern);
            } else if (ch != '\'') {
                continue;
            } else if (i >= pattern.length() - 1 || pattern.charAt(i + 1) != '\'') {
                int i2 = pattern.indexOf(39, i + 1);
                if (i2 != -1) {
                    i = i2 + 1;
                } else {
                    throw new IllegalArgumentException("Bad quoting in " + pattern);
                }
            } else {
                i++;
            }
            i++;
        }
        return result;
    }
}
