package com.jakewharton.rxbinding2.widget;

import androidx.annotation.CheckResult;

import androidx.annotation.NonNull;
import android.widget.TextSwitcher;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.functions.Consumer;

public final class RxTextSwitcher {
    @CheckResult
    @NonNull
    public static Consumer<? super CharSequence> text(@NonNull final TextSwitcher view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<CharSequence>() {
            public void accept(CharSequence text) {
                view.setText(text);
            }
        };
    }

    @CheckResult
    @NonNull
    public static Consumer<? super CharSequence> currentText(@NonNull final TextSwitcher view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<CharSequence>() {
            public void accept(CharSequence textRes) {
                view.setCurrentText(textRes);
            }
        };
    }

    private RxTextSwitcher() {
        throw new AssertionError("No instances.");
    }
}
