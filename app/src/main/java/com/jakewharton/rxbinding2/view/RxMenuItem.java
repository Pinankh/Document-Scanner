package com.jakewharton.rxbinding2.view;

import android.graphics.drawable.Drawable;
import androidx.annotation.CheckResult;

import androidx.annotation.NonNull;
import android.view.MenuItem;
import com.jakewharton.rxbinding2.internal.Functions;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public final class RxMenuItem {
    @CheckResult
    @NonNull
    public static Observable<Object> clicks(@NonNull MenuItem menuItem) {
        Preconditions.checkNotNull(menuItem, "menuItem == null");
        return new MenuItemClickOnSubscribe(menuItem, Functions.PREDICATE_ALWAYS_TRUE);
    }

    @CheckResult
    @NonNull
    public static Observable<Object> clicks(@NonNull MenuItem menuItem, @NonNull Predicate<? super MenuItem> handled) {
        Preconditions.checkNotNull(menuItem, "menuItem == null");
        Preconditions.checkNotNull(handled, "handled == null");
        return new MenuItemClickOnSubscribe(menuItem, handled);
    }

    @CheckResult
    @NonNull
    public static Observable<MenuItemActionViewEvent> actionViewEvents(@NonNull MenuItem menuItem) {
        Preconditions.checkNotNull(menuItem, "menuItem == null");
        return new MenuItemActionViewEventObservable(menuItem, Functions.PREDICATE_ALWAYS_TRUE);
    }

    @CheckResult
    @NonNull
    public static Observable<MenuItemActionViewEvent> actionViewEvents(@NonNull MenuItem menuItem, @NonNull Predicate<? super MenuItemActionViewEvent> handled) {
        Preconditions.checkNotNull(menuItem, "menuItem == null");
        Preconditions.checkNotNull(handled, "handled == null");
        return new MenuItemActionViewEventObservable(menuItem, handled);
    }

    @CheckResult
    @NonNull
    @Deprecated
    public static Consumer<? super Boolean> checked(@NonNull final MenuItem menuItem) {
        Preconditions.checkNotNull(menuItem, "menuItem == null");
        return new Consumer<Boolean>() {
            public void accept(Boolean value) {
                menuItem.setChecked(value.booleanValue());
            }
        };
    }

    @CheckResult
    @NonNull
    @Deprecated
    public static Consumer<? super Boolean> enabled(@NonNull final MenuItem menuItem) {
        Preconditions.checkNotNull(menuItem, "menuItem == null");
        return new Consumer<Boolean>() {
            public void accept(Boolean value) {
                menuItem.setEnabled(value.booleanValue());
            }
        };
    }

    @CheckResult
    @NonNull
    @Deprecated
    public static Consumer<? super Drawable> icon(@NonNull final MenuItem menuItem) {
        Preconditions.checkNotNull(menuItem, "menuItem == null");
        return new Consumer<Drawable>() {
            public void accept(Drawable value) {
                menuItem.setIcon(value);
            }
        };
    }

    @CheckResult
    @NonNull
    @Deprecated
    public static Consumer<? super Integer> iconRes(@NonNull final MenuItem menuItem) {
        Preconditions.checkNotNull(menuItem, "menuItem == null");
        return new Consumer<Integer>() {
            public void accept(Integer value) {
                menuItem.setIcon(value.intValue());
            }
        };
    }

    @CheckResult
    @NonNull
    @Deprecated
    public static Consumer<? super CharSequence> title(@NonNull final MenuItem menuItem) {
        Preconditions.checkNotNull(menuItem, "menuItem == null");
        return new Consumer<CharSequence>() {
            public void accept(CharSequence value) {
                menuItem.setTitle(value);
            }
        };
    }

    @CheckResult
    @NonNull
    @Deprecated
    public static Consumer<? super Integer> titleRes(@NonNull final MenuItem menuItem) {
        Preconditions.checkNotNull(menuItem, "menuItem == null");
        return new Consumer<Integer>() {
            public void accept(Integer value) {
                menuItem.setTitle(value.intValue());
            }
        };
    }

    @CheckResult
    @NonNull
    @Deprecated
    public static Consumer<? super Boolean> visible(@NonNull final MenuItem menuItem) {
        Preconditions.checkNotNull(menuItem, "menuItem == null");
        return new Consumer<Boolean>() {
            public void accept(Boolean value) {
                menuItem.setVisible(value.booleanValue());
            }
        };
    }

    private RxMenuItem() {
        throw new AssertionError("No instances.");
    }
}
