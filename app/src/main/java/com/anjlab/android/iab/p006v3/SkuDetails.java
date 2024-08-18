package com.anjlab.android.iab.p006v3;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;

/* renamed from: com.anjlab.android.iab.v3.SkuDetails */
public class SkuDetails implements Parcelable {
    public static final Parcelable.Creator<SkuDetails> CREATOR = new Parcelable.Creator<SkuDetails>() {
        public SkuDetails createFromParcel(Parcel source) {
            return new SkuDetails(source);
        }

        public SkuDetails[] newArray(int size) {
            return new SkuDetails[size];
        }
    };
    public final String currency;
    public final String description;
    public final boolean haveIntroductoryPeriod;
    public final boolean haveTrialPeriod;
    public final int introductoryPriceCycles;
    public final long introductoryPriceLong;
    public final String introductoryPricePeriod;
    public final String introductoryPriceText;
    public final double introductoryPriceValue;
    public final boolean isSubscription;
    public final long priceLong;
    public final String priceText;
    public final Double priceValue;
    public final String productId;
    public final String subscriptionFreeTrialPeriod;
    public final String subscriptionPeriod;
    public final String title;

    public SkuDetails(JSONObject source) throws JSONException {
        String responseType = source.optString("type");
        responseType = responseType == null ? Constants.PRODUCT_TYPE_MANAGED : responseType;
        this.productId = source.optString(Constants.RESPONSE_PRODUCT_ID);
        this.title = source.optString("title");
        this.description = source.optString(Constants.RESPONSE_DESCRIPTION);
        this.isSubscription = responseType.equalsIgnoreCase(Constants.PRODUCT_TYPE_SUBSCRIPTION);
        this.currency = source.optString(Constants.RESPONSE_PRICE_CURRENCY);
        this.priceLong = source.optLong(Constants.RESPONSE_PRICE_MICROS);
        double d = (double) this.priceLong;
        Double.isNaN(d);
        this.priceValue = Double.valueOf(d / 1000000.0d);
        this.priceText = source.optString("price");
        this.subscriptionPeriod = source.optString(Constants.RESPONSE_SUBSCRIPTION_PERIOD);
        this.subscriptionFreeTrialPeriod = source.optString(Constants.RESPONSE_FREE_TRIAL_PERIOD);
        this.haveTrialPeriod = !TextUtils.isEmpty(this.subscriptionFreeTrialPeriod);
        this.introductoryPriceLong = source.optLong(Constants.RESPONSE_INTRODUCTORY_PRICE_MICROS);
        double d2 = (double) this.introductoryPriceLong;
        Double.isNaN(d2);
        this.introductoryPriceValue = d2 / 1000000.0d;
        this.introductoryPriceText = source.optString(Constants.RESPONSE_INTRODUCTORY_PRICE);
        this.introductoryPricePeriod = source.optString(Constants.RESPONSE_INTRODUCTORY_PRICE_PERIOD);
        this.haveIntroductoryPeriod = !TextUtils.isEmpty(this.introductoryPricePeriod);
        this.introductoryPriceCycles = source.optInt(Constants.RESPONSE_INTRODUCTORY_PRICE_CYCLES);
    }

    public String toString() {
        return String.format(Locale.US, "%s: %s(%s) %f in %s (%s)", new Object[]{this.productId, this.title, this.description, this.priceValue, this.currency, this.priceText});
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SkuDetails that = (SkuDetails) o;
        if (this.isSubscription != that.isSubscription) {
            return false;
        }
        String str = this.productId;
        if (str != null) {
            if (!str.equals(that.productId)) {
                return false;
            }
            return true;
        } else if (that.productId == null) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        String str = this.productId;
        return ((str != null ? str.hashCode() : 0) * 31) + (this.isSubscription ? 1 : 0);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.productId);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeByte(this.isSubscription ? (byte) 1 : 0);
        dest.writeString(this.currency);
        dest.writeDouble(this.priceValue.doubleValue());
        dest.writeLong(this.priceLong);
        dest.writeString(this.priceText);
        dest.writeString(this.subscriptionPeriod);
        dest.writeString(this.subscriptionFreeTrialPeriod);
        dest.writeByte(this.haveTrialPeriod ? (byte) 1 : 0);
        dest.writeDouble(this.introductoryPriceValue);
        dest.writeLong(this.introductoryPriceLong);
        dest.writeString(this.introductoryPriceText);
        dest.writeString(this.introductoryPricePeriod);
        dest.writeByte(this.haveIntroductoryPeriod ? (byte) 1 : 0);
        dest.writeInt(this.introductoryPriceCycles);
    }

    protected SkuDetails(Parcel in) {
        this.productId = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        boolean z = true;
        this.isSubscription = in.readByte() != 0;
        this.currency = in.readString();
        this.priceValue = Double.valueOf(in.readDouble());
        this.priceLong = in.readLong();
        this.priceText = in.readString();
        this.subscriptionPeriod = in.readString();
        this.subscriptionFreeTrialPeriod = in.readString();
        this.haveTrialPeriod = in.readByte() != 0;
        this.introductoryPriceValue = in.readDouble();
        this.introductoryPriceLong = in.readLong();
        this.introductoryPriceText = in.readString();
        this.introductoryPricePeriod = in.readString();
        this.haveIntroductoryPeriod = in.readByte() == 0 ? false : z;
        this.introductoryPriceCycles = in.readInt();
    }
}
