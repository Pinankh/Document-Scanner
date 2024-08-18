package com.jakewharton.rxbinding2.view;

import android.view.View;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;




public abstract class ViewScrollChangeEvent {
    public abstract int oldScrollX();

    public abstract int oldScrollY();

    public abstract int scrollX();

    public abstract int scrollY();

    @NonNull
    public abstract View view();

    @CheckResult
    @NonNull
    public static ViewScrollChangeEvent create(@NonNull View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        return new AutoValue_ViewScrollChangeEvent(view, scrollX, scrollY, oldScrollX, oldScrollY);
    }

    ViewScrollChangeEvent() {
    }
}
