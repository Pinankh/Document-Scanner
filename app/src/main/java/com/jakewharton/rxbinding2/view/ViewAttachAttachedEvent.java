package com.jakewharton.rxbinding2.view;

import android.view.View;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;




public abstract class ViewAttachAttachedEvent extends ViewAttachEvent {
    @CheckResult
    @NonNull
    public static ViewAttachAttachedEvent create(@NonNull View view) {
        return new AutoValue_ViewAttachAttachedEvent(view);
    }

    ViewAttachAttachedEvent() {
    }
}
