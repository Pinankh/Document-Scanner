package com.jakewharton.rxbinding2.widget;

import androidx.annotation.CheckResult;

import androidx.annotation.NonNull;
import android.widget.CompoundButton;
import com.jakewharton.rxbinding2.InitialValueObservable;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.functions.Consumer;

public final class RxCompoundButton {
    @CheckResult
    @NonNull
    public static InitialValueObservable<Boolean> checkedChanges(@NonNull CompoundButton view) {
        Preconditions.checkNotNull(view, "view == null");
        return new CompoundButtonCheckedChangeObservable(view);
    }

    @CheckResult
    @NonNull
    public static Consumer<? super Boolean> checked(@NonNull final CompoundButton view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<Boolean>() {
            public void accept(Boolean value) throws Exception {
                view.setChecked(value.booleanValue());
            }
        };
    }

    @CheckResult
    @NonNull
    public static Consumer<? super Object> toggle(@NonNull final CompoundButton view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<Object>() {
            public void accept(Object value) {
                view.toggle();
            }
        };
    }

    private RxCompoundButton() {
        throw new AssertionError("No instances.");
    }
}
