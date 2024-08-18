package com.camscanner.paperscanner.pdfcreator.common.utils;

import android.content.Context;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import co.lujun.androidtagview.TagItem;
import com.camscanner.paperscanner.pdfcreator.common.Constant;
import com.camscanner.paperscanner.pdfcreator.common.p018db.DBManager;

public class TagUtils {
    private static final String strSpace = " ";
    private static final String strTimePattern = "yyyy_MM_dd_HH_mm_ss";
    private static final String strUnderline = "_";

    public static String getNameTemplate(String template, Context context) {
        String strResult = String.format("%s", new Object[]{template});
        Date curDate = new Date();
        String[] strArr = new String[6];
        String[] timeStamps = new SimpleDateFormat(strTimePattern).format(new Date()).split("_");
        Calendar.getInstance().setTime(curDate);
        String strYear = timeStamps[0];
        String strMonth = timeStamps[1];
        String strDay = timeStamps[2];
        String strHour = timeStamps[3];
        String strMinute = timeStamps[4];
        String strSecond = timeStamps[5];
        return strResult.replace(Constant.TAG_YEAR, strYear).replace(Constant.TAG_MONTH, strMonth).replace(Constant.TAG_DAY, strDay).replace(Constant.TAG_HOUR, strHour).replace(Constant.TAG_MINUTE, strMinute).replace(Constant.TAG_SECOND, strSecond).replace(Constant.TAG_TAG, SharedPrefsUtils.getTagText(context) + " ");
    }

    public static void getTagDocCount(TagItem tagItem) {
        DBManager.getInstance().getTagDocCount(tagItem);
    }

    public static boolean isTagDuplicated(String strTagName) {
        ArrayList<TagItem> tagList = new ArrayList<>();
        DBManager.getInstance().getTagItems(tagList);
        Iterator<TagItem> it = tagList.iterator();
        while (it.hasNext()) {
            if (strTagName.equals(it.next().tagName)) {
                return true;
            }
        }
        return false;
    }
}
