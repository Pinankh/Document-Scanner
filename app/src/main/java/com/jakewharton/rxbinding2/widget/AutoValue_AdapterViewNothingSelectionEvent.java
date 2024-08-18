package com.jakewharton.rxbinding2.widget;

import androidx.annotation.NonNull;
import android.widget.AdapterView;

final class AutoValue_AdapterViewNothingSelectionEvent extends AdapterViewNothingSelectionEvent {
    private final AdapterView<?> view;

    AutoValue_AdapterViewNothingSelectionEvent(AdapterView<?> view2) {
        if (view2 != null) {
            this.view = view2;
            return;
        }
        throw new NullPointerException("Null view");
    }

    @NonNull
    public AdapterView<?> view() {
        return this.view;
    }

    public String toString() {
        return "AdapterViewNothingSelectionEvent{view=" + this.view + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof AdapterViewNothingSelectionEvent) {
            return this.view.equals(((AdapterViewNothingSelectionEvent) o).view());
        }
        return false;
    }

    public int hashCode() {
        return (1 * 1000003) ^ this.view.hashCode();
    }
}
