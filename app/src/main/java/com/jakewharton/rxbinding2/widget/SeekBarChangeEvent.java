package com.jakewharton.rxbinding2.widget;

import androidx.annotation.NonNull;
import android.widget.SeekBar;

public abstract class SeekBarChangeEvent {
    @NonNull
    public abstract SeekBar view();

    SeekBarChangeEvent() {
    }
}
