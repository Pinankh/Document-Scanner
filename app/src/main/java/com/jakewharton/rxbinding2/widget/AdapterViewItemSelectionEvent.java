package com.jakewharton.rxbinding2.widget;

import androidx.annotation.CheckResult;

import androidx.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;



public abstract class AdapterViewItemSelectionEvent extends AdapterViewSelectionEvent {
    /* renamed from: id */
    public abstract long mo54503id();

    public abstract int position();

    @NonNull
    public abstract View selectedView();

    @CheckResult
    @NonNull
    public static AdapterViewSelectionEvent create(@NonNull AdapterView<?> view, @NonNull View selectedView, int position, long id) {
        return new AutoValue_AdapterViewItemSelectionEvent(view, selectedView, position, id);
    }

    AdapterViewItemSelectionEvent() {
    }
}
