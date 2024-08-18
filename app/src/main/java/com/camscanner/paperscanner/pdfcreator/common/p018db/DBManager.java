package com.camscanner.paperscanner.pdfcreator.common.p018db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.opencv.videoio.Videoio;
import co.lujun.androidtagview.TagItem;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import com.camscanner.paperscanner.pdfcreator.common.Constant;
import com.camscanner.paperscanner.pdfcreator.features.barcode.model.QrResult;
import com.camscanner.paperscanner.pdfcreator.model.Document;
import com.camscanner.paperscanner.pdfcreator.model.PDFSize;
import tap.lib.rateus.dialog.Circle;

public class DBManager {
    private static DBManager instance;


    private SQLiteHelper helper;

    private SQLiteDatabase f2485db = null;

    public static void init(Context ctx) {
        instance = new DBManager(ctx);
        addPDFSize();
    }

    public static void addPDFSize() {
        if (instance.getPDFSizeCount() == 0) {
            instance.addPDFSize(new PDFSize(Constant.PDF_SIZE_A5, Videoio.CAP_PROP_XI_TIMEOUT, Videoio.CAP_PROP_XI_FFS_FILE_ID));
            instance.addPDFSize(new PDFSize(Constant.PDF_SIZE_B5, Videoio.CAP_PROP_XI_ACQ_FRAME_BURST_COUNT, 709));
            instance.addPDFSize(new PDFSize(Constant.PDF_SIZE_B4, 709, 1001));
            instance.addPDFSize(new PDFSize(Constant.PDF_SIZE_US46, 288, Videoio.CAP_PROP_XI_DECIMATION_VERTICAL));
            instance.addPDFSize(new PDFSize(Constant.PDF_SIZE_EXECUTIVE, Videoio.CAP_PROP_XI_DEVICE_SN, 756));
            instance.addPDFSize(new PDFSize(Constant.PDF_SIZE_COMM10, 297, 684));
            instance.addPDFSize(new PDFSize(Constant.PDF_SIZE_LETTER, 612, 792));
            instance.addPDFSize(new PDFSize(Constant.PDF_SIZE_A3, 842, 1190));
            instance.addPDFSize(new PDFSize(Constant.PDF_SIZE_A4, Videoio.CAP_PROP_XI_REGION_MODE, 842));
            instance.addPDFSize(new PDFSize(Constant.PDF_SIZE_US48, 288, 576));
            instance.addPDFSize(new PDFSize(Constant.PDF_SIZE_LEGAL, 612, 1008));
            instance.addPDFSize(new PDFSize(Constant.PDF_SIZE_US57, Circle.FULL, 504));
        }
    }

    public static DBManager getInstance() {
        return instance;
    }

    private DBManager(Context ctx) {
        this.helper = new SQLiteHelper(ctx);
        f2485db = this.helper.getWritableDatabase();

    }

    private long insertData(String tableName, ContentValues contentValues) {
        long result;
        try {
            this.f2485db.beginTransaction();
            result = this.f2485db.insert(tableName, (String) null, contentValues);
            this.f2485db.setTransactionSuccessful();
        } catch (Exception e) {
            result = -1;
        } catch (Throwable th) {
            this.f2485db.endTransaction();
            throw th;
        }
        this.f2485db.endTransaction();
        return result;
    }

    private boolean deleteData(String tableName, long id) {
        boolean ret = false;
        try {
            this.f2485db.beginTransaction();
            this.f2485db.delete(tableName, "id=?", new String[]{String.valueOf(id)});
            this.f2485db.setTransactionSuccessful();
            ret = true;
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable th) {
            this.f2485db.endTransaction();
            throw th;
        }
        this.f2485db.endTransaction();
        return ret;
    }

    private boolean deleteDataByUid(String tableName, String uid) {
        boolean ret = false;
        try {
            this.f2485db.beginTransaction();
            this.f2485db.delete(tableName, "uid=?", new String[]{String.valueOf(uid)});
            this.f2485db.setTransactionSuccessful();
            ret = true;
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable th) {
            this.f2485db.endTransaction();
            throw th;
        }
        this.f2485db.endTransaction();
        return ret;
    }

    private boolean updateData(String tableName, ContentValues contentValues, long id) {
        boolean ret = false;
        try {
            this.f2485db.beginTransaction();
            this.f2485db.update(tableName, contentValues, "id=?", new String[]{String.valueOf(id)});
            this.f2485db.setTransactionSuccessful();
            ret = true;
        } catch (Exception e) {
        } catch (Throwable th) {
            this.f2485db.endTransaction();
            throw th;
        }
        this.f2485db.endTransaction();
        return ret;
    }

    private boolean updateDataByUid(String tableName, ContentValues contentValues, String uid) {
        boolean ret = false;
        try {
            this.f2485db.beginTransaction();
            this.f2485db.update(tableName, contentValues, "uid=?", new String[]{uid});
            this.f2485db.setTransactionSuccessful();
            ret = true;
        } catch (Exception e) {
        } catch (Throwable th) {
            this.f2485db.endTransaction();
            throw th;
        }
        this.f2485db.endTransaction();
        return ret;
    }

    public boolean addTagItem(TagItem tagItem) {
        long tagID = insertData(TagItem.TABLE_NAME, tagItem.prepareContentValue());
        tagItem.f52ID = tagID;
        return tagID > 0;
    }

    public void getTagItems(List<TagItem> data) {
        Cursor c = null;
        try {
            c = this.f2485db.rawQuery("SELECT * FROM TagItem", (String[]) null);
            while (c != null && c.moveToNext()) {
                data.add(new TagItem(c));
            }
            if (c == null) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (c == null) {
                return;
            }
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
            throw th;
        }
        c.close();
    }

    public int getTagDocCount(TagItem tagItem) {
        int ret = 0;
        try {
            Cursor c = this.f2485db.rawQuery("SELECT count(*) FROM Document WHERE tagList LIKE " + ("'%," + tagItem.f52ID + ",%'"), (String[]) null);
            if (c != null && c.moveToFirst()) {
                ret = c.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tagItem.nDocCount = ret;
        return ret;
    }

    public boolean updateTagItem(TagItem tagItem) {
        return updateData(TagItem.TABLE_NAME, tagItem.prepareContentValue(), tagItem.f52ID);
    }

    public boolean deleteTagItem(TagItem tagItem) {
        return deleteData(TagItem.TABLE_NAME, tagItem.f52ID);
    }

    public boolean addPDFSize(PDFSize pdfSize) {
        return insertData(PDFSize.TABLE_NAME, pdfSize.prepareContentValue()) > 0;
    }

    public void getPDFSize(List<PDFSize> data) {
        Cursor c = null;
        try {
            c = this.f2485db.rawQuery("SELECT * FROM PDFSize", (String[]) null);
            while (c != null && c.moveToNext()) {
                data.add(new PDFSize(c));
            }
            if (c == null) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (c == null) {
                return;
            }
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
            throw th;
        }
        c.close();
    }

    public int getPDFSizeCount() {
        try {
            Cursor c = this.f2485db.rawQuery("SELECT count(*) FROM PDFSize", (String[]) null);
            if (c == null || !c.moveToFirst()) {
                return 0;
            }
            return c.getInt(0);
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean updatePDFSize(PDFSize pdfSize) {
        return updateData(PDFSize.TABLE_NAME, pdfSize.prepareContentValue(), pdfSize.f14689ID);
    }

    public boolean deletePDFSize(PDFSize pdfSize) {
        return deleteData(PDFSize.TABLE_NAME, pdfSize.f14689ID);
    }

    public boolean addDocument(Document contact) {
        return insertData("Document", contact.prepareContentValue()) > 0;
    }

    public boolean addGroup(Document contact) {
        return insertData("Document", contact.prepareContentValue()) > 0;
    }


    public Document getDocumentWithUID(String uid) {
        Cursor c = null;
        Document document = null;
        try {
            c = this.f2485db.query("Document", Document.COLUMN, "uid=?", new String[]{uid}, (String) null, (String) null, (String) null);
            if (c != null && c.moveToNext()) {
                document = new Document(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
            throw th;
        }
        return null;
    }

    public int getDocumentsCount() {
        try {
            Cursor c = this.f2485db.rawQuery("SELECT count(*) FROM Document", (String[]) null);
            if (c == null || !c.moveToFirst()) {
                return 0;
            }
            return c.getInt(0);
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean deleteDocument(Document contact) {
        return deleteData("Document", contact.f14688ID);
    }

    public boolean updateDocument(Document contact) {
        return updateData("Document", contact.prepareContentValue(), contact.f14688ID);
    }

    public boolean updateDocumenttByUid(Document contact) {
        return updateDataByUid("Document", contact.prepareContentValue(), contact.uid);
    }


    public void searchDocuments(List<Document> data, String content, boolean isMarked) {
        String dir;
        if (isMarked) {
            dir = "1";
        } else {
            dir = "0";
        }
        Cursor c = null;
        String content2 = "%" + content + "%";
        if (isMarked) {
            try {
                c = this.f2485db.query("Document", Document.COLUMN, "isMarked=? AND name!=? AND isDir=? AND name LIKE ?", new String[]{dir, "", "0", content2}, (String) null, (String) null, (String) null);
                while (c != null && c.moveToNext()) {
                    try {
                        data.add(new Document(c));
                    } catch (Exception e) {
                    } catch (Throwable th) {
                        th = th;
                        if (c != null) {
                        }
                        throw th;
                    }
                }
                if (c == null) {
                    return;
                }
            } catch (Exception e2) {
                if (c == null) {
                    return;
                }
                c.close();
            } catch (Throwable th2) {
                if (c != null) {
                    c.close();
                }
            }
        } else {
            c = this.f2485db.query("Document", Document.COLUMN, "name!=? AND isDir=? AND name LIKE ?", new String[]{"", "0", content2}, (String) null, (String) null, (String) null);


            if (c != null && c.moveToFirst()){
                do {
                    data.add(new Document(c));

                } while (c.moveToNext());
            }
        }
        c.close();
    }

    public void searchFolderDocuments(List<Document> data, String parent, String content) {
        Cursor c = null;
        try {
            c = this.f2485db.query("Document", Document.COLUMN, "parent=? AND name!=? AND name LIKE ?", new String[]{parent, "", "%" + content + "%"}, (String) null, (String) null, (String) null);
            while (c != null && c.moveToNext()) {
                data.add(new Document(c));
            }
            if (c == null) {
                return;
            }
        } catch (Exception e) {
            if (c == null) {
                return;
            }
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
            throw th;
        }
        c.close();
    }

    public void searchDocument(List<Document> data, String uid) {
        Cursor c = null;
        try {
            c = this.f2485db.query("Document", Document.COLUMN, "uid=?", new String[]{uid}, (String) null, (String) null, (String) null);
            while (c != null && c.moveToNext()) {
                data.add(new Document(c));
            }
            if (c == null) {
                return;
            }
        } catch (Exception e) {
            if (c == null) {
                return;
            }
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
            throw th;
        }
        c.close();
    }

    public void getDocumentsCreatedUp(List<Document> data, String parent, long tagID, boolean isFolderOrFile) {
        String dir;
        String[] args;
        String strSelection;
        long j = tagID;
        Cursor c = null;
        if (isFolderOrFile) {
            dir = "1";
        } else {
            dir = "0";
        }
        if (j > 0) {
            strSelection = "parent=? AND isDir=?" + " AND tagList LIKE ? ";
            args = new String[]{parent, dir, "" + "%," + j + ",%"};
        } else {
            strSelection = "parent=? AND isDir=?";
            args = new String[]{parent, dir};
        }
        try {
            c = this.f2485db.query("Document", Document.COLUMN, strSelection, args, (String) null, (String) null, "date ASC");
            while (c != null && c.moveToNext()) {
                try {
                    data.add(new Document(c));
                } catch (Exception e) {
                } catch (Throwable th) {
                    th = th;
                    if (c != null) {
                    }
                    throw th;
                }
            }
            if (c == null) {
                return;
            }
        } catch (Exception e2) {
            if (c == null) {
                return;
            }
            c.close();
        } catch (Throwable th2) {
            if (c != null) {
                c.close();
            }
        }
        c.close();
    }


    public void getDocumentsCreatedDown(List<Document> data, String parent, long tagID, boolean isFolderOrFile) {
        String dir;
        String[] args;
        String strSelection;
        long j = tagID;
        Cursor c = null;
        if (isFolderOrFile) {
            dir = "1";
        } else {
            dir = "0";
        }
        if (j > 0) {
            strSelection = "parent=? AND isDir=?" + " AND tagList LIKE ? ";
            args = new String[]{parent, dir, "" + "%," + j + ",%"};
        } else {
            strSelection = "parent=? AND isDir=?";
            args = new String[]{parent, dir};
        }
        try {
            c = this.f2485db.query("Document", Document.COLUMN, strSelection, args, (String) null, (String) null, "date DESC");
            while (c != null && c.moveToNext()) {
                try {
                    data.add(new Document(c));
                } catch (Exception e) {
                } catch (Throwable th) {
                    th = th;
                    if (c != null) {
                    }
                    throw th;
                }
            }
            if (c == null) {
                return;
            }
        } catch (Exception e2) {
            if (c == null) {
                return;
            }
            c.close();
        } catch (Throwable th2) {

            if (c != null) {
                c.close();
            }

        }
        c.close();
    }


    public void getDocumentsNameA2Z(List<Document> data, String parent, long tagID, boolean isFolderOrFile) {
        String dir;
        String[] args;
        String strSelection;
        long j = tagID;
        Cursor c = null;
        if (isFolderOrFile) {
            dir = "1";
        } else {
            dir = "0";
        }
        if (j > 0) {
            strSelection = "parent=? AND isDir=?" + " AND tagList LIKE ? ";
            args = new String[]{parent, dir, "" + "%," + j + ",%"};
        } else {
            strSelection = "parent=? AND isDir=?";
            args = new String[]{parent, dir};
        }
        try {
            c = this.f2485db.query("Document", Document.COLUMN, strSelection, args, (String) null, (String) null, "name ASC");
            while (c != null && c.moveToNext()) {
                try {
                    data.add(new Document(c));
                } catch (Exception e) {
                } catch (Throwable th) {
                    th = th;
                    if (c != null) {
                    }
                    throw th;
                }
            }
            if (c == null) {
                return;
            }
        } catch (Exception e2) {
            if (c == null) {
                return;
            }
            c.close();
        } catch (Throwable th2) {

            if (c != null) {
                c.close();
            }

        }
        c.close();
    }


    public void getDocumentsNameZ2A(List<Document> data, String parent, long tagID, boolean isFolderOrFile) {
        String dir;
        String[] args;
        String strSelection;
        long j = tagID;
        Cursor c = null;
        if (isFolderOrFile) {
            dir = "1";
        } else {
            dir = "0";
        }
        if (j > 0) {
            strSelection = "parent=? AND isDir=?" + " AND tagList LIKE ? ";
            args = new String[]{parent, dir, "" + "%," + j + ",%"};
        } else {
            strSelection = "parent=? AND isDir=?";
            args = new String[]{parent, dir};
        }
        try {
            c = this.f2485db.query("Document", Document.COLUMN, strSelection, args, (String) null, (String) null, "name DESC");
            while (c != null && c.moveToNext()) {
                try {
                    data.add(new Document(c));
                } catch (Exception e) {
                } catch (Throwable th) {
                    th = th;
                    if (c != null) {
                    }
                    throw th;
                }
            }
            if (c == null) {
                return;
            }
        } catch (Exception e2) {
            if (c == null) {
                return;
            }
            c.close();
        } catch (Throwable th2) {

            if (c != null) {
                c.close();
            }

        }
        c.close();
    }

    public void getDocuments(List<Document> data, String parent) {
        Cursor c = null;
        try {
            c = this.f2485db.query("Document", Document.COLUMN, "parent=?", new String[]{parent}, (String) null, (String) null, (String) null);
            while (c != null && c.moveToNext()) {
                data.add(new Document(c));
            }
            if (c == null) {
                return;
            }
        } catch (Exception e) {
            if (c == null) {
                return;
            }
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
            throw th;
        }
        c.close();
    }


    public int getLastSortID(String parent) {
        int sortID = 0;
        Cursor c = null;
        try {
            c = this.f2485db.query("Document", Document.COLUMN, "parent=?", new String[]{parent}, (String) null, (String) null, "sortID DESC", "1");
            while (c != null && c.moveToNext()) {
                sortID = new Document(c).sortID;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
            throw th;
        }
        return 0;

    }

    public void getDocumentsBySortID(List<Document> data, String parent) {
        Cursor c = null;
        try {
            c = this.f2485db.query("Document", Document.COLUMN, "parent=?", new String[]{parent}, (String) null, (String) null, "sortID ASC");
            while (c != null && c.moveToNext()) {
                data.add(new Document(c));
            }
            if (c == null) {
                return;
            }
        } catch (Exception e) {
            if (c == null) {
                return;
            }
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
            throw th;
        }
        c.close();
    }

    public void getDocuments(List<Document> data, String parent, boolean isFolderOrFile) {
        String dir;
        if (isFolderOrFile) {
            dir = "1";
        } else {
            dir = "0";
        }
        Cursor c = null;
        try {
            c = this.f2485db.query("Document", Document.COLUMN, "parent=? AND isDir=?", new String[]{parent, dir}, (String) null, (String) null, "date DESC");
            while (c != null && c.moveToNext()) {
                data.add(new Document(c));
            }
            if (c == null) {
                return;
            }
        } catch (Exception e) {
            if (c == null) {
                return;
            }
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
            throw th;
        }
        c.close();
    }


    public int getFolderCount() {
        int cntFolder = 0;
        Cursor cursor = null;
        try {
            cursor = this.f2485db.query("Document", Document.COLUMN, "parent=? AND isDir=?", new String[]{"", "1"}, (String) null, (String) null, (String) null);
            while (cursor != null && cursor.moveToNext()) {
                cntFolder++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
        return 0;
    }

    public void getMarkedDocuments(List<Document> data) {
        Cursor c = null;
        try {
            c = this.f2485db.query("Document", Document.COLUMN, "isMarked=?", new String[]{"1"}, (String) null, (String) null, (String) null);
            while (c != null && c.moveToNext()) {
                data.add(new Document(c));
            }
            if (c == null) {
                return;
            }
        } catch (Exception e) {
            if (c == null) {
                return;
            }
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
            throw th;
        }
        c.close();
    }


    public int getTodayDocuments(String parent) {
        List<Document> data = new ArrayList<>();
        long now = new Date().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(now);
        calendar.set(11, 0);
        calendar.set(12, 0);
        long start = calendar.getTimeInMillis();
        calendar.set(11, 23);
        calendar.set(12, 59);
        long end = calendar.getTimeInMillis();
        Cursor c = null;
        try {
            c = this.f2485db.query("Document", Document.COLUMN, "parent=? AND isDir=? AND date>? AND date <?", new String[]{parent, "0", String.valueOf(start), String.valueOf(end)}, (String) null, (String) null, (String) null);
            while (c != null && c.moveToNext()) {
                data.add(new Document(c));
            }
        } catch (Exception e) {
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
            throw th;
        }
        return 0;
    }

    public boolean addQrResult(QrResult result) {
        return insertData(QrResult.TABLE_NAME, result.prepareContentValue()) > 0;
    }

    public Single<List<QrResult>> getQrHistory() {
        return Single.create(new SingleOnSubscribe() {
            public final void subscribe(SingleEmitter singleEmitter) {
                try {
                    DBManager.this.lambda$getQrHistory$0$DBManager(singleEmitter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public /* synthetic */ void lambda$getQrHistory$0$DBManager(SingleEmitter emitter) throws Exception {
        List<QrResult> data = new ArrayList<>();
        Cursor c = null;
        try {
            c = this.f2485db.query(QrResult.TABLE_NAME, (String[]) null, (String) null, (String[]) null, (String) null, (String) null, "date DESC");
            while (c != null && c.moveToNext()) {
                data.add(new QrResult(c));
            }
        } catch (Exception e) {
            emitter.onError(e);
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
            throw th;
        }
    }
}
