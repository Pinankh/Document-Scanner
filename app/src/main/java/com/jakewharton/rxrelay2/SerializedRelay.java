package com.jakewharton.rxrelay2;

import io.reactivex.Observer;

final class SerializedRelay<T> extends Relay<T> {
    private final Relay<T> actual;
    private boolean emitting;
    private AppendOnlyLinkedArrayList<T> queue;

    SerializedRelay(Relay<T> actual2) {
        this.actual = actual2;
    }

    /* access modifiers changed from: protected */
    public void subscribeActual(Observer<? super T> observer) {
        this.actual.subscribe(observer);
    }

    public void accept(T value) {
        synchronized (this) {
            if (this.emitting) {
                AppendOnlyLinkedArrayList<T> q = this.queue;
                if (q == null) {
                    q = new AppendOnlyLinkedArrayList<>(4);
                    this.queue = q;
                }
                q.add(value);
                return;
            }
            this.emitting = true;
            this.actual.accept(value);
            emitLoop();
        }
    }

    private void emitLoop() {
        AppendOnlyLinkedArrayList<T> q;
        while (true) {
            synchronized (this) {
                q = this.queue;
                if (q == null) {
                    this.emitting = false;
                    return;
                }
                this.queue = null;
            }
            q.accept(this.actual);
        }

    }

    public boolean hasObservers() {
        return this.actual.hasObservers();
    }
}
