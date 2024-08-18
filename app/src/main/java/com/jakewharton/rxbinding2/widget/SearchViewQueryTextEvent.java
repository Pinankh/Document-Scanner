package com.jakewharton.rxbinding2.widget;

import androidx.annotation.CheckResult;

import androidx.annotation.NonNull;
import android.widget.SearchView;



public abstract class SearchViewQueryTextEvent {
    public abstract boolean isSubmitted();

    @NonNull
    public abstract CharSequence queryText();

    @NonNull
    public abstract SearchView view();

    @CheckResult
    @NonNull
    public static SearchViewQueryTextEvent create(@NonNull SearchView view, @NonNull CharSequence queryText, boolean submitted) {
        return new AutoValue_SearchViewQueryTextEvent(view, queryText, submitted);
    }

    SearchViewQueryTextEvent() {
    }
}
