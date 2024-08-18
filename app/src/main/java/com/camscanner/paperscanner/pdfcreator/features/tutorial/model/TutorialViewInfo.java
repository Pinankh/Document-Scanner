package com.camscanner.paperscanner.pdfcreator.features.tutorial.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TutorialViewInfo extends TutorialInfo {
    public static final Parcelable.Creator<TutorialViewInfo> CREATOR = new Parcelable.Creator<TutorialViewInfo>() {
        public TutorialViewInfo createFromParcel(Parcel in) {
            return new TutorialViewInfo(in);
        }

        public TutorialViewInfo[] newArray(int size) {
            return new TutorialViewInfo[size];
        }
    };
    public static final int NO_VIEW = -1;
    public final int height;
    public final int outsideViewId;
    public final int width;

    /* renamed from: x */
    public final float f14686x;

    /* renamed from: y */
    public final float f14687y;

    public TutorialViewInfo(int layoutId, int viewId, float x, float y, int width2, int height2) {
        this(layoutId, viewId, -1, x, y, width2, height2);
    }

    public TutorialViewInfo(int layoutId, int viewId, int outsideViewId2, float x, float y, int width2, int height2) {
        super(layoutId, viewId);
        this.outsideViewId = outsideViewId2;
        this.f14686x = x;
        this.f14687y = y;
        this.width = width2;
        this.height = height2;
    }

    public TutorialViewInfo(int layoutId, int viewId, float x, float y, int width2, int height2, boolean correctTextPosition) {
        this(layoutId, viewId, -1, x, y, width2, height2, correctTextPosition);
    }

    private TutorialViewInfo(int layoutId, int viewId, int outsideViewId2, float x, float y, int width2, int height2, boolean correctTextPosition) {
        super(layoutId, viewId, correctTextPosition);
        this.outsideViewId = outsideViewId2;
        this.f14686x = x;
        this.f14687y = y;
        this.width = width2;
        this.height = height2;
    }

    protected TutorialViewInfo(Parcel in) {
        super(in);
        this.outsideViewId = in.readInt();
        this.f14686x = in.readFloat();
        this.f14687y = in.readFloat();
        this.width = in.readInt();
        this.height = in.readInt();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.outsideViewId);
        dest.writeFloat(this.f14686x);
        dest.writeFloat(this.f14687y);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
    }

    public String toString() {
        return "TutorialViewInfo{x=" + this.f14686x + ", y=" + this.f14687y + ", width=" + this.width + ", height=" + this.height + '}';
    }

    public boolean hasOutsideView() {
        return this.outsideViewId != -1;
    }
}
