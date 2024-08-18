package com.jakewharton.rxbinding2.view;

import android.view.MenuItem;

import androidx.annotation.NonNull;

final class AutoValue_MenuItemActionViewExpandEvent extends MenuItemActionViewExpandEvent {
    private final MenuItem menuItem;

    AutoValue_MenuItemActionViewExpandEvent(MenuItem menuItem2) {
        if (menuItem2 != null) {
            this.menuItem = menuItem2;
            return;
        }
        throw new NullPointerException("Null menuItem");
    }

    @NonNull
    public MenuItem menuItem() {
        return this.menuItem;
    }

    public String toString() {
        return "MenuItemActionViewExpandEvent{menuItem=" + this.menuItem + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof MenuItemActionViewExpandEvent) {
            return this.menuItem.equals(((MenuItemActionViewExpandEvent) o).menuItem());
        }
        return false;
    }

    public int hashCode() {
        return (1 * 1000003) ^ this.menuItem.hashCode();
    }
}
