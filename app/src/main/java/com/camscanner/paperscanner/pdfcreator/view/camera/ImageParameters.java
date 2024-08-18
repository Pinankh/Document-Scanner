package com.camscanner.paperscanner.pdfcreator.view.camera;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageParameters implements Parcelable {
    public static final Parcelable.Creator<ImageParameters> CREATOR = new Parcelable.Creator<ImageParameters>() {
        public ImageParameters createFromParcel(Parcel source) {
            return new ImageParameters(source);
        }

        public ImageParameters[] newArray(int size) {
            return new ImageParameters[size];
        }
    };
    public int mCoverHeight;
    public int mCoverWidth;
    public int mDisplayOrientation;
    public boolean mIsPortrait;
    public int mLayoutOrientation;
    public int mPreviewHeight;
    public int mPreviewWidth;

    public ImageParameters(Parcel in) {
        this.mIsPortrait = in.readByte() != 1 ? false : true;
        this.mDisplayOrientation = in.readInt();
        this.mLayoutOrientation = in.readInt();
        this.mCoverHeight = in.readInt();
        this.mCoverWidth = in.readInt();
        this.mPreviewHeight = in.readInt();
        this.mPreviewWidth = in.readInt();
    }

    public ImageParameters() {
    }

    public int calculateCoverWidthHeight() {
        return 0;
    }

    public int getAnimationParameter() {
        return this.mIsPortrait ? this.mCoverHeight : this.mCoverWidth;
    }

    public boolean isPortrait() {
        return this.mIsPortrait;
    }

    public ImageParameters createCopy() {
        ImageParameters imageParameters = new ImageParameters();
        imageParameters.mIsPortrait = this.mIsPortrait;
        imageParameters.mDisplayOrientation = this.mDisplayOrientation;
        imageParameters.mLayoutOrientation = this.mLayoutOrientation;
        imageParameters.mCoverHeight = this.mCoverHeight;
        imageParameters.mCoverWidth = this.mCoverWidth;
        imageParameters.mPreviewHeight = this.mPreviewHeight;
        imageParameters.mPreviewWidth = this.mPreviewWidth;
        return imageParameters;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.mIsPortrait ? (byte) 1 : 0);
        dest.writeInt(this.mDisplayOrientation);
        dest.writeInt(this.mLayoutOrientation);
        dest.writeInt(this.mCoverHeight);
        dest.writeInt(this.mCoverWidth);
        dest.writeInt(this.mPreviewHeight);
        dest.writeInt(this.mPreviewWidth);
    }
}
