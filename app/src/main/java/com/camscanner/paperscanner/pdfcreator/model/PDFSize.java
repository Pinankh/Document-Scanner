package com.camscanner.paperscanner.pdfcreator.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.io.Serializable;

public class PDFSize implements Serializable {
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PX_HEIGHT = "pxheight";
    private static final String COLUMN_PX_WIDTH = "pxwidth";
    public static final String TABLE_NAME = "PDFSize";

    /* renamed from: ID */
    public long f14689ID;
    public boolean bSelected;
    public String name;
    public int pxHeight;
    public int pxWidth;

    public PDFSize() {
        this.name = "";
    }

    public PDFSize(String name2) {
        this.name = name2;
    }

    public PDFSize(String name2, int width, int height) {
        this.name = name2;
        this.pxWidth = width;
        this.pxHeight = height;
        this.bSelected = false;
    }

    public PDFSize(Cursor c) {
        readFromCursor(c);
    }

    public static void createTable(SQLiteDatabase db) {
        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s(id INTEGER UNIQUE PRIMARY KEY AUTOINCREMENT NOT NULL,name VARCHAR(50),pxwidth INTEGER DEFAULT 0,pxheight INTEGER DEFAULT 0)", new Object[]{TABLE_NAME}));
    }

    public ContentValues prepareContentValue() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", this.name);
        contentValues.put(COLUMN_PX_WIDTH, Integer.valueOf(this.pxWidth));
        contentValues.put(COLUMN_PX_HEIGHT, Integer.valueOf(this.pxHeight));
        return contentValues;
    }

    public void readFromCursor(Cursor c) {
        this.f14689ID = (long) c.getInt(c.getColumnIndex("id"));
        this.name = c.getString(c.getColumnIndex("name"));
        this.pxWidth = c.getInt(c.getColumnIndex(COLUMN_PX_WIDTH));
        this.pxHeight = c.getInt(c.getColumnIndex(COLUMN_PX_HEIGHT));
        this.bSelected = false;
    }
}
