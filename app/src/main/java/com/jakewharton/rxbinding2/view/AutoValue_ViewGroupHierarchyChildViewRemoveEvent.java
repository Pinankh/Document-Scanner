package com.jakewharton.rxbinding2.view;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

final class AutoValue_ViewGroupHierarchyChildViewRemoveEvent extends ViewGroupHierarchyChildViewRemoveEvent {
    private final View child;
    private final ViewGroup view;

    AutoValue_ViewGroupHierarchyChildViewRemoveEvent(ViewGroup view2, View child2) {
        if (view2 != null) {
            this.view = view2;
            if (child2 != null) {
                this.child = child2;
                return;
            }
            throw new NullPointerException("Null child");
        }
        throw new NullPointerException("Null view");
    }

    @NonNull
    public ViewGroup view() {
        return this.view;
    }

    @NonNull
    public View child() {
        return this.child;
    }

    public String toString() {
        return "ViewGroupHierarchyChildViewRemoveEvent{view=" + this.view + ", child=" + this.child + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ViewGroupHierarchyChildViewRemoveEvent)) {
            return false;
        }
        ViewGroupHierarchyChildViewRemoveEvent that = (ViewGroupHierarchyChildViewRemoveEvent) o;
        if (!this.view.equals(that.view()) || !this.child.equals(that.child())) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return (((1 * 1000003) ^ this.view.hashCode()) * 1000003) ^ this.child.hashCode();
    }
}
