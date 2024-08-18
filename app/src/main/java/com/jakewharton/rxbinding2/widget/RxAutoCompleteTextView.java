package com.jakewharton.rxbinding2.widget;

import androidx.annotation.CheckResult;

import androidx.annotation.NonNull;
import android.widget.AutoCompleteTextView;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public final class RxAutoCompleteTextView {
    @CheckResult
    @NonNull
    public static Observable<AdapterViewItemClickEvent> itemClickEvents(@NonNull AutoCompleteTextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new AutoCompleteTextViewItemClickEventObservable(view);
    }

    @CheckResult
    @NonNull
    public static Consumer<? super CharSequence> completionHint(@NonNull final AutoCompleteTextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<CharSequence>() {
            public void accept(CharSequence completionHint) {
                view.setCompletionHint(completionHint);
            }
        };
    }

    @CheckResult
    @NonNull
    public static Consumer<? super Integer> threshold(@NonNull final AutoCompleteTextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<Integer>() {
            public void accept(Integer threshold) {
                view.setThreshold(threshold.intValue());
            }
        };
    }

    private RxAutoCompleteTextView() {
        throw new AssertionError("No instances.");
    }
}
