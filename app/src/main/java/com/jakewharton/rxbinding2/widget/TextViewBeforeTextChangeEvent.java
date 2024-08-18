package com.jakewharton.rxbinding2.widget;

import androidx.annotation.CheckResult;

import androidx.annotation.NonNull;
import android.widget.TextView;



public abstract class TextViewBeforeTextChangeEvent {
    public abstract int after();

    public abstract int count();

    public abstract int start();

    @NonNull
    public abstract CharSequence text();

    @NonNull
    public abstract TextView view();

    @CheckResult
    @NonNull
    public static TextViewBeforeTextChangeEvent create(@NonNull TextView view, @NonNull CharSequence text, int start, int count, int after) {
        return new AutoValue_TextViewBeforeTextChangeEvent(view, text, start, count, after);
    }

    TextViewBeforeTextChangeEvent() {
    }
}
