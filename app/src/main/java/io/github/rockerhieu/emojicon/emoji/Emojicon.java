package io.github.rockerhieu.emojicon.emoji;

import android.os.Parcel;
import android.os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* renamed from: io.github.rockerhieu.emojicon.emoji.Emojicon */
public class Emojicon implements Parcelable {
    public static final Parcelable.Creator<Emojicon> CREATOR = new Parcelable.Creator<Emojicon>() {
        public Emojicon createFromParcel(Parcel in) {
            return new Emojicon(in);
        }

        public Emojicon[] newArray(int size) {
            return new Emojicon[size];
        }
    };
    public static final int TYPE_NATURE = 2;
    public static final int TYPE_OBJECTS = 3;
    public static final int TYPE_PEOPLE = 1;
    public static final int TYPE_PLACES = 4;
    public static final int TYPE_SYMBOLS = 5;
    public static final int TYPE_UNDEFINED = 0;
    private String emoji;
    private int icon;
    private char value;

    /* renamed from: io.github.rockerhieu.emojicon.emoji.Emojicon$Alignment */
    public @interface Alignment {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: io.github.rockerhieu.emojicon.emoji.Emojicon$Type */
    public @interface Type {
    }

    public static Emojicon[] getEmojicons(int type) {
        if (type == 1) {
            return People.DATA;
        }
        throw new IllegalArgumentException("Invalid emojicon type: " + type);
    }

    public Emojicon(int icon2, char value2, String emoji2) {
        this.icon = icon2;
        this.value = value2;
        this.emoji = emoji2;
    }

    public Emojicon(Parcel in) {
        this.icon = in.readInt();
        this.value = (char) in.readInt();
        this.emoji = in.readString();
    }

    private Emojicon() {
    }

    public Emojicon(String emoji2) {
        this.emoji = emoji2;
    }

    public static Emojicon fromResource(int icon2, int value2) {
        Emojicon emoji2 = new Emojicon();
        emoji2.icon = icon2;
        emoji2.value = (char) value2;
        return emoji2;
    }

    public static Emojicon fromCodePoint(int codePoint) {
        Emojicon emoji2 = new Emojicon();
        emoji2.emoji = newString(codePoint);
        return emoji2;
    }

    public static Emojicon fromChar(char ch) {
        Emojicon emoji2 = new Emojicon();
        emoji2.emoji = Character.toString(ch);
        return emoji2;
    }

    public static Emojicon fromChars(String chars) {
        Emojicon emoji2 = new Emojicon();
        emoji2.emoji = chars;
        return emoji2;
    }

    public static final String newString(int codePoint) {
        if (Character.charCount(codePoint) == 1) {
            return String.valueOf(codePoint);
        }
        return new String(Character.toChars(codePoint));
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.icon);
        dest.writeInt(this.value);
        dest.writeString(this.emoji);
    }

    public char getValue() {
        return this.value;
    }

    public int getIcon() {
        return this.icon;
    }

    public String getEmoji() {
        return this.emoji;
    }

    public boolean equals(Object o) {
        return (o instanceof Emojicon) && this.emoji.equals(((Emojicon) o).emoji);
    }

    public int hashCode() {
        return this.emoji.hashCode();
    }
}
