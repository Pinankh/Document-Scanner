package com.jakewharton.rxbinding2.widget;

import androidx.annotation.CheckResult;

import androidx.annotation.NonNull;
import android.widget.TextView;
import com.jakewharton.rxbinding2.InitialValueObservable;
import com.jakewharton.rxbinding2.internal.Functions;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public final class RxTextView {
    @CheckResult
    @NonNull
    public static Observable<Integer> editorActions(@NonNull TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return editorActions(view, Functions.PREDICATE_ALWAYS_TRUE);
    }

    @CheckResult
    @NonNull
    public static Observable<Integer> editorActions(@NonNull TextView view, @NonNull Predicate<? super Integer> handled) {
        Preconditions.checkNotNull(view, "view == null");
        Preconditions.checkNotNull(handled, "handled == null");
        return new TextViewEditorActionObservable(view, handled);
    }

    @CheckResult
    @NonNull
    public static Observable<TextViewEditorActionEvent> editorActionEvents(@NonNull TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return editorActionEvents(view, Functions.PREDICATE_ALWAYS_TRUE);
    }

    @CheckResult
    @NonNull
    public static Observable<TextViewEditorActionEvent> editorActionEvents(@NonNull TextView view, @NonNull Predicate<? super TextViewEditorActionEvent> handled) {
        Preconditions.checkNotNull(view, "view == null");
        Preconditions.checkNotNull(handled, "handled == null");
        return new TextViewEditorActionEventObservable(view, handled);
    }

    @CheckResult
    @NonNull
    public static InitialValueObservable<CharSequence> textChanges(@NonNull TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new TextViewTextObservable(view);
    }

    @CheckResult
    @NonNull
    public static InitialValueObservable<TextViewTextChangeEvent> textChangeEvents(@NonNull TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new TextViewTextChangeEventObservable(view);
    }

    @CheckResult
    @NonNull
    public static InitialValueObservable<TextViewBeforeTextChangeEvent> beforeTextChangeEvents(@NonNull TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new TextViewBeforeTextChangeEventObservable(view);
    }

    @CheckResult
    @NonNull
    public static InitialValueObservable<TextViewAfterTextChangeEvent> afterTextChangeEvents(@NonNull TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new TextViewAfterTextChangeEventObservable(view);
    }

    @CheckResult
    @NonNull
    public static Consumer<? super CharSequence> text(@NonNull final TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<CharSequence>() {
            public void accept(CharSequence text) {
                view.setText(text);
            }
        };
    }

    @CheckResult
    @NonNull
    public static Consumer<? super Integer> textRes(@NonNull final TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<Integer>() {
            public void accept(Integer textRes) {
                view.setText(textRes.intValue());
            }
        };
    }

    @CheckResult
    @NonNull
    public static Consumer<? super CharSequence> error(@NonNull final TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<CharSequence>() {
            public void accept(CharSequence text) {
                view.setError(text);
            }
        };
    }

    @CheckResult
    @NonNull
    public static Consumer<? super Integer> errorRes(@NonNull final TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<Integer>() {
            public void accept(Integer textRes) {
                TextView textView = view;
                textView.setError(textView.getContext().getResources().getText(textRes.intValue()));
            }
        };
    }

    @CheckResult
    @NonNull
    public static Consumer<? super CharSequence> hint(@NonNull final TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<CharSequence>() {
            public void accept(CharSequence hint) {
                view.setHint(hint);
            }
        };
    }

    @CheckResult
    @NonNull
    public static Consumer<? super Integer> hintRes(@NonNull final TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<Integer>() {
            public void accept(Integer hintRes) {
                view.setHint(hintRes.intValue());
            }
        };
    }

    @CheckResult
    @NonNull
    public static Consumer<? super Integer> color(@NonNull final TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Consumer<Integer>() {
            public void accept(Integer color) throws Exception {
                view.setTextColor(color.intValue());
            }
        };
    }

    private RxTextView() {
        throw new AssertionError("No instances.");
    }
}
