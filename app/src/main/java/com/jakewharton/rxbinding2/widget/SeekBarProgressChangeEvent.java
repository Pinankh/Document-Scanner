package com.jakewharton.rxbinding2.widget;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import android.widget.SeekBar;



public abstract class SeekBarProgressChangeEvent extends SeekBarChangeEvent {
    public abstract boolean fromUser();

    public abstract int progress();

    @CheckResult
    @NonNull
    public static SeekBarProgressChangeEvent create(@NonNull SeekBar view, int progress, boolean fromUser) {
        return new AutoValue_SeekBarProgressChangeEvent(view, progress, fromUser);
    }

    SeekBarProgressChangeEvent() {
    }
}
