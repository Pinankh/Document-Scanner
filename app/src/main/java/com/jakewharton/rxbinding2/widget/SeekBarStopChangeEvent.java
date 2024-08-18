package com.jakewharton.rxbinding2.widget;

import androidx.annotation.CheckResult;

import androidx.annotation.NonNull;
import android.widget.SeekBar;



public abstract class SeekBarStopChangeEvent extends SeekBarChangeEvent {
    @CheckResult
    @NonNull
    public static SeekBarStopChangeEvent create(@NonNull SeekBar view) {
        return new AutoValue_SeekBarStopChangeEvent(view);
    }

    SeekBarStopChangeEvent() {
    }
}
