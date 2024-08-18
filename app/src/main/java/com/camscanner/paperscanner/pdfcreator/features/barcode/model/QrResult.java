package com.camscanner.paperscanner.pdfcreator.features.barcode.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.zxing.client.result.ParsedResultType;

public class QrResult implements Parcelable {
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_RESULT = "result";
    public static final String COLUMN_TYPE = "type";
    public static final Parcelable.Creator<QrResult> CREATOR = new Parcelable.Creator<QrResult>() {
        public QrResult createFromParcel(Parcel in) {
            return new QrResult(in);
        }

        public QrResult[] newArray(int size) {
            return new QrResult[size];
        }
    };
    public static final String TABLE_NAME = "qrResults";
    private long date;
    private String name;
    private String result;
    private ParsedResultType type;

    public QrResult() {
    }

    public QrResult(ParsedResultType type2, String result2, String name2, long date2) {
        this.type = type2;
        this.result = result2;
        this.name = name2;
        this.date = date2;
    }

    protected QrResult(Parcel in) {
        this.type = ParsedResultAdapter.intToType(in.readInt());
        this.result = in.readString();
        this.name = in.readString();
        this.date = in.readLong();
    }

    public QrResult(Cursor c) {
        this.type = ParsedResultAdapter.intToType(c.getInt(c.getColumnIndex("type")));
        this.name = c.getString(c.getColumnIndex("name"));
        this.result = c.getString(c.getColumnIndex(COLUMN_RESULT));
        this.date = c.getLong(c.getColumnIndex("date"));
    }

    public ParsedResultType getType() {
        return this.type;
    }

    public String getResult() {
        return this.result;
    }

    public String getName() {
        return this.name;
    }

    public long getDate() {
        return this.date;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ParsedResultAdapter.typeToInt(this.type));
        dest.writeString(this.result);
        dest.writeString(this.name);
        dest.writeLong(this.date);
    }

    public static void createTable(SQLiteDatabase db) {
        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s(id INTEGER UNIQUE PRIMARY KEY AUTOINCREMENT NOT NULL,%s INTEGER NOT NULL,%s TEXT NOT NULL,%s TEXT NOT NULL,%s INTEGER NOT NULL)", new Object[]{TABLE_NAME, "type", COLUMN_RESULT, "name", "date"}));
    }

    public ContentValues prepareContentValue() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("type", Integer.valueOf(ParsedResultAdapter.typeToInt(this.type)));
        contentValues.put("name", this.name);
        contentValues.put(COLUMN_RESULT, this.result);
        contentValues.put("date", Long.valueOf(this.date));
        return contentValues;
    }
}
