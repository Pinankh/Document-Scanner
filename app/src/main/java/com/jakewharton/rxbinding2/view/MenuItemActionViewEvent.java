package com.jakewharton.rxbinding2.view;

import android.view.MenuItem;

import androidx.annotation.NonNull;

public abstract class MenuItemActionViewEvent {
    @NonNull
    public abstract MenuItem menuItem();

    MenuItemActionViewEvent() {
    }
}
