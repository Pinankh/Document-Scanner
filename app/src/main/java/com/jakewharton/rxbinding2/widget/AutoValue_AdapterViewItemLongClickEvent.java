package com.jakewharton.rxbinding2.widget;

import androidx.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;

final class AutoValue_AdapterViewItemLongClickEvent extends AdapterViewItemLongClickEvent {
    private final View clickedView;

    /* renamed from: id */
    private final long f10787id;
    private final int position;
    private final AdapterView<?> view;

    AutoValue_AdapterViewItemLongClickEvent(AdapterView<?> view2, View clickedView2, int position2, long id) {
        if (view2 != null) {
            this.view = view2;
            if (clickedView2 != null) {
                this.clickedView = clickedView2;
                this.position = position2;
                this.f10787id = id;
                return;
            }
            throw new NullPointerException("Null clickedView");
        }
        throw new NullPointerException("Null view");
    }

    @NonNull
    public AdapterView<?> view() {
        return this.view;
    }

    @NonNull
    public View clickedView() {
        return this.clickedView;
    }

    public int position() {
        return this.position;
    }

    /* renamed from: id */
    public long mo54498id() {
        return this.f10787id;
    }

    public String toString() {
        return "AdapterViewItemLongClickEvent{view=" + this.view + ", clickedView=" + this.clickedView + ", position=" + this.position + ", id=" + this.f10787id + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AdapterViewItemLongClickEvent)) {
            return false;
        }
        AdapterViewItemLongClickEvent that = (AdapterViewItemLongClickEvent) o;
        if (!this.view.equals(that.view()) || !this.clickedView.equals(that.clickedView()) || this.position != that.position() || this.f10787id != that.mo54498id()) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        long j = this.f10787id;
        return (((((((1 * 1000003) ^ this.view.hashCode()) * 1000003) ^ this.clickedView.hashCode()) * 1000003) ^ this.position) * 1000003) ^ ((int) (j ^ (j >>> 32)));
    }
}
