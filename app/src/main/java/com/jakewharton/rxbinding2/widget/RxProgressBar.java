package com.jakewharton.rxbinding2.widget;

import androidx.annotation.CheckResult;

import androidx.annotation.NonNull;
import android.widget.ProgressBar;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.functions.Consumer;

public final class RxProgressBar {
    @CheckResult
    @NonNull
    public static Consumer<? super Integer> incrementProgressBy(@NonNull final ProgressBar view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<Integer>() {
            public void accept(Integer value) {
                view.incrementProgressBy(value.intValue());
            }
        };
    }

    @CheckResult
    @NonNull
    public static Consumer<? super Integer> incrementSecondaryProgressBy(@NonNull final ProgressBar view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<Integer>() {
            public void accept(Integer value) {
                view.incrementSecondaryProgressBy(value.intValue());
            }
        };
    }

    @CheckResult
    @NonNull
    public static Consumer<? super Boolean> indeterminate(@NonNull final ProgressBar view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<Boolean>() {
            public void accept(Boolean value) {
                view.setIndeterminate(value.booleanValue());
            }
        };
    }

    @CheckResult
    @NonNull
    public static Consumer<? super Integer> max(@NonNull final ProgressBar view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<Integer>() {
            public void accept(Integer value) {
                view.setMax(value.intValue());
            }
        };
    }

    @CheckResult
    @NonNull
    public static Consumer<? super Integer> progress(@NonNull final ProgressBar view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<Integer>() {
            public void accept(Integer value) {
                view.setProgress(value.intValue());
            }
        };
    }

    @CheckResult
    @NonNull
    public static Consumer<? super Integer> secondaryProgress(@NonNull final ProgressBar view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<Integer>() {
            public void accept(Integer value) {
                view.setSecondaryProgress(value.intValue());
            }
        };
    }

    private RxProgressBar() {
        throw new AssertionError("No instances.");
    }
}
