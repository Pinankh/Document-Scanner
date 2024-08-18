package com.jakewharton.rxbinding2.widget;

import androidx.annotation.CheckResult;

import androidx.annotation.NonNull;
import android.widget.AbsListView;



public abstract class AbsListViewScrollEvent {
    public abstract int firstVisibleItem();

    public abstract int scrollState();

    public abstract int totalItemCount();

    @NonNull
    public abstract AbsListView view();

    public abstract int visibleItemCount();

    @CheckResult
    @NonNull
    public static AbsListViewScrollEvent create(AbsListView listView, int scrollState, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        return new AutoValue_AbsListViewScrollEvent(listView, scrollState, firstVisibleItem, visibleItemCount, totalItemCount);
    }

    AbsListViewScrollEvent() {
    }
}
