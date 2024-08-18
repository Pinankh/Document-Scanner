package com.jakewharton.rxbinding2.view;

import android.view.View;

import androidx.annotation.NonNull;

public abstract class ViewAttachEvent {
    @NonNull
    public abstract View view();

    ViewAttachEvent() {
    }
}
