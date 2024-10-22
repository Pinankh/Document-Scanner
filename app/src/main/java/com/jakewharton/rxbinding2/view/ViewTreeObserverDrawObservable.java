package com.jakewharton.rxbinding2.view;

import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.RequiresApi;

import com.jakewharton.rxbinding2.internal.Notification;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

@RequiresApi(16)
final class ViewTreeObserverDrawObservable extends Observable<Object> {
    private final View view;

    ViewTreeObserverDrawObservable(View view2) {
        this.view = view2;
    }

    /* access modifiers changed from: protected */
    public void subscribeActual(Observer<? super Object> observer) {
        if (Preconditions.checkMainThread(observer)) {
            Listener listener = new Listener(this.view, observer);
            observer.onSubscribe(listener);
            this.view.getViewTreeObserver().addOnDrawListener(listener);
        }
    }

    static final class Listener extends MainThreadDisposable implements ViewTreeObserver.OnDrawListener {
        private final Observer<? super Object> observer;
        private final View view;

        Listener(View view2, Observer<? super Object> observer2) {
            this.view = view2;
            this.observer = observer2;
        }

        public void onDraw() {
            if (!isDisposed()) {
                this.observer.onNext(Notification.INSTANCE);
            }
        }

        /* access modifiers changed from: protected */
        public void onDispose() {
            this.view.getViewTreeObserver().removeOnDrawListener(this);
        }
    }
}
