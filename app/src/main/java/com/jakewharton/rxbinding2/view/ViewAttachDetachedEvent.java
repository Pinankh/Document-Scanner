package com.jakewharton.rxbinding2.view;

import android.view.View;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;




public abstract class ViewAttachDetachedEvent extends ViewAttachEvent {
    @CheckResult
    @NonNull
    public static ViewAttachDetachedEvent create(@NonNull View view) {
        return new AutoValue_ViewAttachDetachedEvent(view);
    }

    ViewAttachDetachedEvent() {
    }
}
