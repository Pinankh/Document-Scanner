package com.jakewharton.rxbinding2.view;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;



public abstract class ViewGroupHierarchyChildViewAddEvent extends ViewGroupHierarchyChangeEvent {
    @CheckResult
    @NonNull
    public static ViewGroupHierarchyChildViewAddEvent create(@NonNull ViewGroup viewGroup, @NonNull View child) {
        return new AutoValue_ViewGroupHierarchyChildViewAddEvent(viewGroup, child);
    }

    ViewGroupHierarchyChildViewAddEvent() {
    }
}
