package com.jakewharton.rxbinding2.widget;

import androidx.annotation.CheckResult;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.widget.TextView;



public abstract class TextViewAfterTextChangeEvent {
    @Nullable
    public abstract Editable editable();

    @NonNull
    public abstract TextView view();

    @CheckResult
    @NonNull
    public static TextViewAfterTextChangeEvent create(@NonNull TextView view, @Nullable Editable editable) {
        return new AutoValue_TextViewAfterTextChangeEvent(view, editable);
    }

    TextViewAfterTextChangeEvent() {
    }
}
