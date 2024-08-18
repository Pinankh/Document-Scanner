package co.lujun.androidtagview;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.io.Serializable;

/* renamed from: co.lujun.androidtagview.TagItem */
public class TagItem implements Serializable {
    public static final String TABLE_NAME = "TagItem";

    /* renamed from: ID */
    public long f52ID;
    public boolean bEditable;
    public boolean bSelected;
    public int nDocCount;
    public String tagName;

    public TagItem() {
        this.tagName = "";
        this.bSelected = false;
        this.bEditable = false;
        this.nDocCount = 0;
    }

    public TagItem(String strName, boolean bMode) {
        this.tagName = strName;
        this.bSelected = false;
        this.bEditable = bMode;
        this.nDocCount = 0;
    }

    public TagItem(Cursor c) {
        readFromCursor(c);
    }

    public static void createTable(SQLiteDatabase db) {
        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s(id INTEGER UNIQUE PRIMARY KEY AUTOINCREMENT NOT NULL,tag_name VARCHAR(50))", new Object[]{TABLE_NAME}));
    }

    public ContentValues prepareContentValue() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("tag_name", this.tagName);
        return contentValues;
    }

    public void readFromCursor(Cursor c) {
        this.f52ID = (long) c.getInt(c.getColumnIndex("id"));
        this.tagName = c.getString(c.getColumnIndex("tag_name"));
        this.bSelected = false;
        this.bEditable = true;
        this.nDocCount = 0;
    }
}
