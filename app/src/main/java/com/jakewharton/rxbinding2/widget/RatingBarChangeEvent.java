package com.jakewharton.rxbinding2.widget;

import androidx.annotation.CheckResult;

import androidx.annotation.NonNull;
import android.widget.RatingBar;



public abstract class RatingBarChangeEvent {
    public abstract boolean fromUser();

    public abstract float rating();

    @NonNull
    public abstract RatingBar view();

    @CheckResult
    @NonNull
    public static RatingBarChangeEvent create(@NonNull RatingBar view, float rating, boolean fromUser) {
        return new AutoValue_RatingBarChangeEvent(view, rating, fromUser);
    }

    RatingBarChangeEvent() {
    }
}
