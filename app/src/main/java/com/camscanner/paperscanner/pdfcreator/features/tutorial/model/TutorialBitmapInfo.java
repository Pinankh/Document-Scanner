package com.camscanner.paperscanner.pdfcreator.features.tutorial.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TutorialBitmapInfo extends TutorialViewInfo {
    public static final Parcelable.Creator<TutorialBitmapInfo> CREATOR = new Parcelable.Creator<TutorialBitmapInfo>() {
        public TutorialBitmapInfo createFromParcel(Parcel in) {
            return new TutorialBitmapInfo(in);
        }

        public TutorialBitmapInfo[] newArray(int size) {
            return new TutorialBitmapInfo[size];
        }
    };
    public final String pathToBitmap;

    public TutorialBitmapInfo(int layoutId, int viewId, String pathToBitmap2, float x, float y, int width, int height) {
        this(layoutId, viewId, -1, pathToBitmap2, x, y, width, height);
    }

    public TutorialBitmapInfo(int layoutId, int viewId, int outsideViewId, String pathToBitmap2, float x, float y, int width, int height) {
        super(layoutId, viewId, outsideViewId, x, y, width, height);
        this.pathToBitmap = pathToBitmap2;
    }

    protected TutorialBitmapInfo(Parcel in) {
        super(in);
        this.pathToBitmap = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.pathToBitmap);
    }
}
