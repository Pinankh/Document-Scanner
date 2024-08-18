package com.jakewharton.rxbinding2.widget;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.widget.TextView;

final class AutoValue_TextViewAfterTextChangeEvent extends TextViewAfterTextChangeEvent {
    private final Editable editable;
    private final TextView view;

    AutoValue_TextViewAfterTextChangeEvent(TextView view2, @Nullable Editable editable2) {
        if (view2 != null) {
            this.view = view2;
            this.editable = editable2;
            return;
        }
        throw new NullPointerException("Null view");
    }

    @NonNull
    public TextView view() {
        return this.view;
    }

    @Nullable
    public Editable editable() {
        return this.editable;
    }

    public String toString() {
        return "TextViewAfterTextChangeEvent{view=" + this.view + ", editable=" + this.editable + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TextViewAfterTextChangeEvent)) {
            return false;
        }
        TextViewAfterTextChangeEvent that = (TextViewAfterTextChangeEvent) o;
        if (this.view.equals(that.view())) {
            Editable editable2 = this.editable;
            if (editable2 == null) {
                if (that.editable() == null) {
                    return true;
                }
            } else if (editable2.equals(that.editable())) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        int h = ((1 * 1000003) ^ this.view.hashCode()) * 1000003;
        Editable editable2 = this.editable;
        return h ^ (editable2 == null ? 0 : editable2.hashCode());
    }
}
