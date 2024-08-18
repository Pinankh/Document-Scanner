package com.camscanner.paperscanner.pdfcreator.common.p018db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import co.lujun.androidtagview.TagItem;
import com.camscanner.paperscanner.pdfcreator.features.barcode.model.QrResult;
import com.camscanner.paperscanner.pdfcreator.model.Document;
import com.camscanner.paperscanner.pdfcreator.model.PDFSize;
import timber.log.Timber;

public class SQLiteHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "camscanner.db";
    private static final int DATABASE_VERSION = 19;
    private static final String LOG_TAG = SQLiteHelper.class.getSimpleName();

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, 19);
    }

    public void onCreate(SQLiteDatabase db) {
        Document.createTable(db);
        PDFSize.createTable(db);
        TagItem.createTable(db);
        QrResult.createTable(db);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Timber.tag(LOG_TAG).i("onUpgrade %s to %s", Integer.valueOf(oldVersion), Integer.valueOf(newVersion));
        if (oldVersion < 15) {
            PDFSize.createTable(db);
            try {
                db.execSQL("ALTER TABLE 'Document' ADD COLUMN textPath VARCHAR DEFAULT '';");
                db.execSQL("ALTER TABLE 'Document' ADD COLUMN sortID INTEGER DEFAULT 0;");
            } catch (SQLiteException exception) {
                Timber.tag(LOG_TAG).e(exception);
            }
        }
        if (oldVersion < 16) {
            TagItem.createTable(db);
        }
        if (oldVersion < 17) {
            try {
                db.execSQL("ALTER TABLE 'Document' ADD COLUMN tagList VARCHAR(1255) DEFAULT '';");
            } catch (SQLiteException exception2) {
                Timber.tag(LOG_TAG).e(exception2);
            }
        }
        if (oldVersion < 19) {
            QrResult.createTable(db);
        }
    }
}
