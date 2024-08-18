package com.jakewharton.rxbinding2.widget;

import androidx.annotation.CheckResult;

import androidx.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;



public abstract class AdapterViewItemLongClickEvent {
    @NonNull
    public abstract View clickedView();

    /* renamed from: id */
    public abstract long mo54498id();

    public abstract int position();

    @NonNull
    public abstract AdapterView<?> view();

    @CheckResult
    @NonNull
    public static AdapterViewItemLongClickEvent create(@NonNull AdapterView<?> view, @NonNull View clickedView, int position, long id) {
        return new AutoValue_AdapterViewItemLongClickEvent(view, clickedView, position, id);
    }

    AdapterViewItemLongClickEvent() {
    }
}
