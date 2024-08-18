package com.jakewharton.rxbinding2.widget;

import androidx.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;

final class AutoValue_AdapterViewItemSelectionEvent extends AdapterViewItemSelectionEvent {

    /* renamed from: id */
    private final long f10788id;
    private final int position;
    private final View selectedView;
    private final AdapterView<?> view;

    AutoValue_AdapterViewItemSelectionEvent(AdapterView<?> view2, View selectedView2, int position2, long id) {
        if (view2 != null) {
            this.view = view2;
            if (selectedView2 != null) {
                this.selectedView = selectedView2;
                this.position = position2;
                this.f10788id = id;
                return;
            }
            throw new NullPointerException("Null selectedView");
        }
        throw new NullPointerException("Null view");
    }

    @NonNull
    public AdapterView<?> view() {
        return this.view;
    }

    @NonNull
    public View selectedView() {
        return this.selectedView;
    }

    public int position() {
        return this.position;
    }

    /* renamed from: id */
    public long mo54503id() {
        return this.f10788id;
    }

    public String toString() {
        return "AdapterViewItemSelectionEvent{view=" + this.view + ", selectedView=" + this.selectedView + ", position=" + this.position + ", id=" + this.f10788id + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AdapterViewItemSelectionEvent)) {
            return false;
        }
        AdapterViewItemSelectionEvent that = (AdapterViewItemSelectionEvent) o;
        if (!this.view.equals(that.view()) || !this.selectedView.equals(that.selectedView()) || this.position != that.position() || this.f10788id != that.mo54503id()) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        long j = this.f10788id;
        return (((((((1 * 1000003) ^ this.view.hashCode()) * 1000003) ^ this.selectedView.hashCode()) * 1000003) ^ this.position) * 1000003) ^ ((int) (j ^ (j >>> 32)));
    }
}
