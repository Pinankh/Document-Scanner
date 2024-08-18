package com.camscanner.paperscanner.pdfcreator.common.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import com.camscanner.paperscanner.pdfcreator.view.element.tagsedittext.TagsEditText;

public class OcrStorageUtils {
    private static final String LOG_TAG = OcrStorageUtils.class.getSimpleName();

    public static String saveText(String text) {
        return writeTextToDisk(text, getTextFolder().toString() + File.separator + IONames.TEXT_FILE + getTimeStamp() + Extensions.TEXT);
    }

    private static String writeTextToDisk(String text, String textPath) {
        FileOutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        try {
            outputStream = new FileOutputStream(textPath);
            outputStreamWriter = new OutputStreamWriter(outputStream);
            outputStreamWriter.write(text);
            outputStreamWriter.close();
        } catch (IOException e) {
        } catch (Throwable th) {
            close(outputStream);
            close(outputStreamWriter);
            throw th;
        }
        close(outputStream);
        close(outputStreamWriter);
        return textPath;
    }

    public static String readText(String path) {
        String text = "";
        FileInputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        try {
            inputStream = new FileInputStream(path);
            inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            while (true) {
                String readLine = bufferedReader.readLine();
                String receiveString = readLine;
                if (readLine == null) {
                    break;
                }
                stringBuilder.append(receiveString);
                stringBuilder.append(TagsEditText.NEW_LINE);
            }
            inputStream.close();
            text = stringBuilder.toString();
        } catch (IOException e) {
        } catch (Throwable th) {
            close((Closeable) null);
            close((Closeable) null);
            throw th;
        }
        close(inputStream);
        close(inputStreamReader);
        return text;
    }

    private static File getTextFolder() {
        File dir = new File(getAppFolder(), IONames.TEXT_FOLDER);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static File getAppFolder() {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory());
        sb.append("/"+Environment.DIRECTORY_DOWNLOADS+ File.separator + "DocScanner");
        File dir = new File(sb.toString());
        //File dir = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "DocScanner");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private static synchronized String getTimeStamp() {
        String str;
        synchronized (OcrStorageUtils.class) {
            str = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_" + getRandIdx();
        }
        return str;
    }

    private static int getRandIdx() {
        return new Random().nextInt(80) + 65;
    }

    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }

    public static void deleteText(Context context, String strPath) {
        if (!TextUtils.isEmpty(strPath) && new File(strPath).delete()) {
            MediaScannerUtils.notifySystemAboutFile(context, strPath);
        }
    }
}
