package com.jakewharton.rxbinding2.view;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;




public abstract class ViewGroupHierarchyChildViewRemoveEvent extends ViewGroupHierarchyChangeEvent {
    @CheckResult
    @NonNull
    public static ViewGroupHierarchyChildViewRemoveEvent create(@NonNull ViewGroup viewGroup, @NonNull View child) {
        return new AutoValue_ViewGroupHierarchyChildViewRemoveEvent(viewGroup, child);
    }

    ViewGroupHierarchyChildViewRemoveEvent() {
    }
}
