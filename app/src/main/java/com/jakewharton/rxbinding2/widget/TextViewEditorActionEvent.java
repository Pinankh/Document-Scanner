package com.jakewharton.rxbinding2.widget;

import androidx.annotation.CheckResult;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.KeyEvent;
import android.widget.TextView;



public abstract class TextViewEditorActionEvent {
    public abstract int actionId();

    @Nullable
    public abstract KeyEvent keyEvent();

    @NonNull
    public abstract TextView view();

    @CheckResult
    @NonNull
    public static TextViewEditorActionEvent create(@NonNull TextView view, int actionId, @Nullable KeyEvent keyEvent) {
        return new AutoValue_TextViewEditorActionEvent(view, actionId, keyEvent);
    }

    TextViewEditorActionEvent() {
    }
}
