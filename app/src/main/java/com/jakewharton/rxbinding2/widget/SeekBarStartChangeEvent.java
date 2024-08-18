package com.jakewharton.rxbinding2.widget;

import androidx.annotation.CheckResult;

import androidx.annotation.NonNull;
import android.widget.SeekBar;



public abstract class SeekBarStartChangeEvent extends SeekBarChangeEvent {
    @CheckResult
    @NonNull
    public static SeekBarStartChangeEvent create(@NonNull SeekBar view) {
        return new AutoValue_SeekBarStartChangeEvent(view);
    }

    SeekBarStartChangeEvent() {
    }
}
