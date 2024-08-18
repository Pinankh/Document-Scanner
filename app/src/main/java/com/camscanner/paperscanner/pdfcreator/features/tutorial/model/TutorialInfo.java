package com.camscanner.paperscanner.pdfcreator.features.tutorial.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TutorialInfo implements Parcelable {
    public static final Parcelable.Creator<TutorialInfo> CREATOR = new Parcelable.Creator<TutorialInfo>() {
        public TutorialInfo createFromParcel(Parcel in) {
            return new TutorialInfo(in);
        }

        public TutorialInfo[] newArray(int size) {
            return new TutorialInfo[size];
        }
    };
    public final boolean correctTextPosition;
    public final int layoutId;
    public final int viewId;

    public TutorialInfo(int layoutId2, int viewId2) {
        this.layoutId = layoutId2;
        this.viewId = viewId2;
        this.correctTextPosition = false;
    }

    protected TutorialInfo(int layoutId2, int viewId2, boolean correctTextPosition2) {
        this.layoutId = layoutId2;
        this.viewId = viewId2;
        this.correctTextPosition = correctTextPosition2;
    }

    protected TutorialInfo(Parcel in) {
        this.layoutId = in.readInt();
        this.viewId = in.readInt();
        this.correctTextPosition = in.readByte() != 0;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.layoutId);
        dest.writeInt(this.viewId);
        dest.writeByte(this.correctTextPosition ? (byte) 1 : 0);
    }
}
