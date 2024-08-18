package com.jakewharton.rxbinding2.widget;

import android.widget.SeekBar;

import androidx.annotation.NonNull;

final class AutoValue_SeekBarStartChangeEvent extends SeekBarStartChangeEvent {
    private final SeekBar view;

    AutoValue_SeekBarStartChangeEvent(SeekBar view2) {
        if (view2 != null) {
            this.view = view2;
            return;
        }
        throw new NullPointerException("Null view");
    }

    @NonNull
    public SeekBar view() {
        return this.view;
    }

    public String toString() {
        return "SeekBarStartChangeEvent{view=" + this.view + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof SeekBarStartChangeEvent) {
            return this.view.equals(((SeekBarStartChangeEvent) o).view());
        }
        return false;
    }

    public int hashCode() {
        return (1 * 1000003) ^ this.view.hashCode();
    }
}
