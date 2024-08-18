package com.jakewharton.rxbinding2.view;

import android.view.MenuItem;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;



public abstract class MenuItemActionViewCollapseEvent extends MenuItemActionViewEvent {
    @CheckResult
    @NonNull
    public static MenuItemActionViewCollapseEvent create(@NonNull MenuItem menuItem) {
        return new AutoValue_MenuItemActionViewCollapseEvent(menuItem);
    }

    MenuItemActionViewCollapseEvent() {
    }
}
