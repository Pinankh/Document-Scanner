package com.jakewharton.rxbinding2.view;

import android.view.MenuItem;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;



public abstract class MenuItemActionViewExpandEvent extends MenuItemActionViewEvent {
    @CheckResult
    @NonNull
    public static MenuItemActionViewExpandEvent create(@NonNull MenuItem menuItem) {
        return new AutoValue_MenuItemActionViewExpandEvent(menuItem);
    }

    MenuItemActionViewExpandEvent() {
    }
}
