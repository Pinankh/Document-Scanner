package com.jakewharton.rxrelay2;

import com.jakewharton.rxrelay2.AppendOnlyLinkedArrayList;
import java.lang.reflect.Array;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public final class BehaviorRelay<T> extends Relay<T> {
    private static final BehaviorDisposable[] EMPTY = new BehaviorDisposable[0];
    private static final Object[] EMPTY_ARRAY = new Object[0];
    long index;
    final Lock readLock;
    private AtomicReference<BehaviorDisposable[]> subscribers;
    final AtomicReference<T> value;
    private final Lock writeLock;

    public static <T> BehaviorRelay<T> create() {
        return new BehaviorRelay<>();
    }

    public static <T> BehaviorRelay<T> createDefault(T t) {
        return new BehaviorRelay<>(t);
    }

    private BehaviorRelay() {
        ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
        this.readLock = reentrantReadWriteLock.readLock();
        this.writeLock = reentrantReadWriteLock.writeLock();
        this.subscribers = new AtomicReference<>(EMPTY);
        this.value = new AtomicReference<>();
    }

    private BehaviorRelay(T t) {
        this();
        if (t != null) {
            this.value.lazySet(t);
            return;
        }
        throw new NullPointerException("defaultValue == null");
    }

    @Override // io.reactivex.Observable
    protected void subscribeActual(Observer<? super T> observer) {
        BehaviorDisposable<T> behaviorDisposable = new BehaviorDisposable<>(observer, this);
        observer.onSubscribe(behaviorDisposable);
        add(behaviorDisposable);
        if (behaviorDisposable.cancelled) {
            remove(behaviorDisposable);
        } else {
            behaviorDisposable.emitFirst();
        }
    }

    @Override // com.jakewharton.rxrelay2.Relay, io.reactivex.functions.Consumer
    public void accept(T t) {
        if (t != null) {
            setCurrent(t);
            for (BehaviorDisposable<T> behaviorDisposable : this.subscribers.get()) {
                behaviorDisposable.emitNext(t, this.index);
            }
            return;
        }
        throw new NullPointerException("value == null");
    }

    @Override // com.jakewharton.rxrelay2.Relay
    public boolean hasObservers() {
        return this.subscribers.get().length != 0;
    }

    int subscriberCount() {
        return this.subscribers.get().length;
    }

    public T getValue() {
        return this.value.get();
    }

    public Object[] getValues() {
        T[] values = getValues((T[]) EMPTY_ARRAY);
        return values == EMPTY_ARRAY ? new Object[0] : values;
    }

    public T[] getValues(T[] tArr) {
        T t = this.value.get();
        if (t == null) {
            if (tArr.length != 0) {
                tArr[0] = null;
            }
            return tArr;
        } else if (tArr.length != 0) {
            tArr[0] = t;
            if (tArr.length == 1) {
                return tArr;
            }
            tArr[1] = null;
            return tArr;
        } else {
            T[] tArr2 = (T[]) Array.newInstance(tArr.getClass().getComponentType(), 1);
            tArr2[0] = t;
            return tArr2;
        }
    }

    public boolean hasValue() {
        return this.value.get() != null;
    }

    private void add(BehaviorDisposable<T> behaviorDisposable) {
        BehaviorDisposable<T>[] behaviorDisposableArr;
        BehaviorDisposable<T>[] behaviorDisposableArr2;
        do {
            behaviorDisposableArr = this.subscribers.get();
            int length = behaviorDisposableArr.length;
            behaviorDisposableArr2 = new BehaviorDisposable[(length + 1)];
            System.arraycopy(behaviorDisposableArr, 0, behaviorDisposableArr2, 0, length);
            behaviorDisposableArr2[length] = behaviorDisposable;
        } while (!this.subscribers.compareAndSet(behaviorDisposableArr, behaviorDisposableArr2));
    }

    void remove(BehaviorDisposable<T> behaviorDisposable) {
        BehaviorDisposable<T>[] behaviorDisposableArr;
        BehaviorDisposable<T>[] behaviorDisposableArr2;
        do {
            behaviorDisposableArr = this.subscribers.get();
            if (behaviorDisposableArr != EMPTY) {
                int length = behaviorDisposableArr.length;
                int i = -1;
                int i2 = 0;
                while (true) {
                    if (i2 >= length) {
                        break;
                    } else if (behaviorDisposableArr[i2] == behaviorDisposable) {
                        i = i2;
                        break;
                    } else {
                        i2++;
                    }
                }
                if (i >= 0) {
                    if (length == 1) {
                        behaviorDisposableArr2 = EMPTY;
                    } else {
                        BehaviorDisposable<T>[] behaviorDisposableArr3 = new BehaviorDisposable[(length - 1)];
                        System.arraycopy(behaviorDisposableArr, 0, behaviorDisposableArr3, 0, i);
                        System.arraycopy(behaviorDisposableArr, i + 1, behaviorDisposableArr3, i, (length - i) - 1);
                        behaviorDisposableArr2 = behaviorDisposableArr3;
                    }
                } else {
                    return;
                }
            } else {
                return;
            }
        } while (!this.subscribers.compareAndSet(behaviorDisposableArr, behaviorDisposableArr2));
    }

    private void setCurrent(T t) {
        this.writeLock.lock();
        try {
            this.index++;
            this.value.lazySet(t);
        } finally {
            this.writeLock.unlock();
        }
    }

    static final class BehaviorDisposable<T> implements Disposable, AppendOnlyLinkedArrayList.NonThrowingPredicate<T> {
        final Observer<? super T> actual;
        volatile boolean cancelled;
        boolean emitting;
        boolean fastPath;
        long index;
        boolean next;
        AppendOnlyLinkedArrayList<T> queue;
        final BehaviorRelay<T> state;

        BehaviorDisposable(Observer<? super T> observer, BehaviorRelay<T> behaviorRelay) {
            this.actual = observer;
            this.state = behaviorRelay;
        }

        @Override // io.reactivex.disposables.Disposable
        public void dispose() {
            if (!this.cancelled) {
                this.cancelled = true;
                this.state.remove(this);
            }
        }

        @Override // io.reactivex.disposables.Disposable
        public boolean isDisposed() {
            return this.cancelled;
        }


        void emitFirst() {
            if (!this.cancelled) {
                synchronized (this) {
                    if (!this.cancelled) {
                        if (!this.next) {
                            BehaviorRelay<T> behaviorRelay = this.state;
                            Lock lock = behaviorRelay.readLock;
                            lock.lock();
                            this.index = behaviorRelay.index;
                            T t = behaviorRelay.value.get();
                            lock.unlock();
                            this.emitting = t != null;
                            this.next = true;
                        }
                    }
                }
            }
        }


        void emitNext(T t, long j) {
            if (!this.cancelled) {
                if (!this.fastPath) {
                    synchronized (this) {
                        if (!this.cancelled) {
                            if (this.index != j) {
                                if (this.emitting) {
                                    AppendOnlyLinkedArrayList<T> appendOnlyLinkedArrayList = this.queue;
                                    if (appendOnlyLinkedArrayList == null) {
                                        appendOnlyLinkedArrayList = new AppendOnlyLinkedArrayList<>(4);
                                        this.queue = appendOnlyLinkedArrayList;
                                    }
                                    appendOnlyLinkedArrayList.add(t);
                                    return;
                                }
                                this.next = true;
                            } else {
                                return;
                            }
                        } else {
                            return;
                        }
                    }
                }
                test(t);
            }
        }

        @Override // com.jakewharton.rxrelay2.AppendOnlyLinkedArrayList.NonThrowingPredicate, io.reactivex.functions.Predicate
        public boolean test(T t) {
            if (this.cancelled) {
                return false;
            }
            this.actual.onNext(t);
            return false;
        }


        void emitLoop() {
            while (!this.cancelled) {
                synchronized (this) {
                    AppendOnlyLinkedArrayList<T> appendOnlyLinkedArrayList = this.queue;
                    if (appendOnlyLinkedArrayList == null) {
                        this.emitting = false;
                        return;
                    }
                    this.queue = null;
                }
            }
        }
    }
}
