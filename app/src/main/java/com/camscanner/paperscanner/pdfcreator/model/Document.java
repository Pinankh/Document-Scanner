package com.camscanner.paperscanner.pdfcreator.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PointF;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.Serializable;
import java.util.UUID;

public class Document implements Serializable {
    public static final String[] COLUMN = {"*"};
    private static final String COLUMN_CROP_POINTS = "cropPoints";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_IMG_PATH = "imgPath";
    private static final String COLUMN_IS_DIR = "isDir";
    private static final String COLUMN_IS_LOCKED = "isLocked";
    private static final String COLUMN_IS_MARKED = "isMarked";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PARENT = "parent";
    private static final String COLUMN_PATH = "path";
    private static final String COLUMN_SORT_ID = "sortID";
    private static final String COLUMN_TAG_LIST = "tagList";
    private static final String COLUMN_TEXT_PATH = "textPath";
    private static final String COLUMN_THUMB = "thumb";
    private static final String COLUMN_UID = "uid";
    public static final String CREATE_FOLDER_UID = "new_folder";
    private static final int INDEX_EDITED = 1;
    private static final int INDEX_ORIGINAL = 2;
    private static final int INDEX_THUMB = 0;
    public static final String TABLE_NAME = "Document";

    /* renamed from: ID */
    public long f14688ID;
    public String cropPoints;
    public long date;
    public String folderName;
    public String folderParent;
    public String imgPath;
    public boolean isDir;
    public boolean isFolderParent;
    public boolean isLocked;
    public boolean isMarked;
    public boolean m_bNew;
    public boolean m_bSelected;
    public boolean m_bShowButtons;
    public String name;
    public boolean notFirstInDoc;
    public String parent;
    public String parentName;
    public String path;
    public int sortID;
    private String tagList;
    public String textPath;
    public String thumb;
    public String uid;

    public Document(String parent2) {
        this.parent = parent2;
        this.uid = UUID.randomUUID().toString().replace("-", "");
        this.isDir = false;
        this.name = "";
        this.path = "";
        this.textPath = "";
        this.date = 0;
        this.thumb = "";
        this.cropPoints = "";
        this.imgPath = "";
        this.isLocked = false;
        this.isMarked = false;
        this.sortID = 0;
        this.tagList = "";
        this.m_bSelected = false;
        this.m_bShowButtons = true;
        this.m_bNew = true;
        this.notFirstInDoc = false;
        this.parentName = "";
        this.folderParent = "";
        this.isFolderParent = false;
        this.folderName = "";
    }

    public static Document createRoot() {
        Document document = new Document("");
        document.uid = "";
        return document;
    }

    public PointF[] getCropPoints() {
        return (PointF[]) new Gson().fromJson(this.cropPoints, new TypeToken<PointF[]>() {
        }.getType());
    }

    public void setCropPoints(PointF[] points) {
        this.cropPoints = new Gson().toJson((Object) points);
    }

    public Document(Cursor c) {
        readFromCursor(c);
    }

    public String[] getPaths() {
        return new String[]{this.thumb, this.path, this.imgPath};
    }

    public static void createTable(SQLiteDatabase db) {
        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s(id INTEGER UNIQUE PRIMARY KEY AUTOINCREMENT NOT NULL,parent VARCHAR(50),uid VARCHAR(50),isDir INTEGER DEFAULT 0,name VARCHAR(50),path VARCHAR(255),textPath VARCHAR(255),date BIGINT,thumb VARCHAR(255),cropPoints VARCHAR(255),imgPath VARCHAR(255),isLocked INTEGER DEFAULT 0,isMarked INTEGER DEFAULT 0,sortID INTEGER DEFAULT 0,tagList VARCHAR(1255) DEFAULT '')", new Object[]{"Document"}));
    }

    public ContentValues prepareContentValue() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("parent", this.parent);
        contentValues.put(COLUMN_UID, this.uid);
        contentValues.put(COLUMN_IS_DIR, Boolean.valueOf(this.isDir));
        contentValues.put("name", this.name);
        contentValues.put("path", this.path);
        contentValues.put(COLUMN_TEXT_PATH, this.textPath);
        contentValues.put("date", Long.valueOf(this.date));
        contentValues.put(COLUMN_THUMB, this.thumb);
        contentValues.put("cropPoints", this.cropPoints);
        contentValues.put(COLUMN_IMG_PATH, this.imgPath);
        contentValues.put(COLUMN_IS_LOCKED, Boolean.valueOf(this.isLocked));
        contentValues.put(COLUMN_IS_MARKED, Boolean.valueOf(this.isMarked));
        contentValues.put(COLUMN_SORT_ID, Integer.valueOf(this.sortID));
        contentValues.put(COLUMN_TAG_LIST, this.tagList);
        return contentValues;
    }

    public void readFromCursor(Cursor c) {

            this.f14688ID = (long) c.getInt(c.getColumnIndex("id"));
            this.parent = c.getString(c.getColumnIndex("parent"));
            this.uid = c.getString(c.getColumnIndex(COLUMN_UID));
            boolean z = false;
            this.isDir = c.getInt(c.getColumnIndex(COLUMN_IS_DIR)) == 1;
            this.name = c.getString(c.getColumnIndex("name"));
            this.path = c.getString(c.getColumnIndex("path"));
            this.textPath = c.getString(c.getColumnIndex(COLUMN_TEXT_PATH));
            this.date = c.getLong(c.getColumnIndex("date"));
            this.thumb = c.getString(c.getColumnIndex(COLUMN_THUMB));
            this.cropPoints = c.getString(c.getColumnIndex("cropPoints"));
            this.imgPath = c.getString(c.getColumnIndex(COLUMN_IMG_PATH));
            this.m_bNew = false;
            this.isLocked = c.getInt(c.getColumnIndex(COLUMN_IS_LOCKED)) == 1;
            if (c.getInt(c.getColumnIndex(COLUMN_IS_MARKED)) == 1) {
                z = true;
            }
            this.isMarked = z;
            this.sortID = c.getInt(c.getColumnIndex(COLUMN_SORT_ID));
            this.tagList = c.getString(c.getColumnIndex(COLUMN_TAG_LIST));
        }

}
