package com.jakewharton.rxbinding2.widget;

import androidx.annotation.CheckResult;

import androidx.annotation.NonNull;
import android.widget.RatingBar;
import com.jakewharton.rxbinding2.InitialValueObservable;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.functions.Consumer;

public final class RxRatingBar {
    @CheckResult
    @NonNull
    public static InitialValueObservable<Float> ratingChanges(@NonNull RatingBar view) {
        Preconditions.checkNotNull(view, "view == null");
        return new RatingBarRatingChangeObservable(view);
    }

    @CheckResult
    @NonNull
    public static InitialValueObservable<RatingBarChangeEvent> ratingChangeEvents(@NonNull RatingBar view) {
        Preconditions.checkNotNull(view, "view == null");
        return new RatingBarRatingChangeEventObservable(view);
    }

    @CheckResult
    @NonNull
    public static Consumer<? super Float> rating(@NonNull final RatingBar view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<Float>() {
            public void accept(Float value) {
                view.setRating(value.floatValue());
            }
        };
    }

    @CheckResult
    @NonNull
    public static Consumer<? super Boolean> isIndicator(@NonNull final RatingBar view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<Boolean>() {
            public void accept(Boolean value) {
                view.setIsIndicator(value.booleanValue());
            }
        };
    }

    private RxRatingBar() {
        throw new AssertionError("No instances.");
    }
}
