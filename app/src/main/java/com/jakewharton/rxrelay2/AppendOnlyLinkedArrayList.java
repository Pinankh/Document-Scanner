package com.jakewharton.rxrelay2;

import io.reactivex.functions.Predicate;

class AppendOnlyLinkedArrayList<T> {
    private final int capacity;
    private Object[] head = new Object[0];

    private int offset;
    private Object[] tail = head;

    public interface NonThrowingPredicate<T> extends Predicate<T> {
        @Override // io.reactivex.functions.Predicate
        boolean test(T t);
    }

    AppendOnlyLinkedArrayList(int i) {
        this.capacity = i;
        this.head = new Object[(i + 1)];
    }

    void add(T t) {
        int i = this.capacity;
        int i2 = this.offset;
        if (i2 == i) {
            Object[] objArr = new Object[(i + 1)];
            this.tail[i] = objArr;
            this.tail = objArr;
            i2 = 0;
        }
        this.tail[i2] = t;
        this.offset = i2 + 1;
    }

    void forEachWhile(NonThrowingPredicate<? super T> nonThrowingPredicate) {
        int i = this.capacity;
        for (Object[] objArr = this.head; objArr != null; objArr = (Object[]) objArr[i]) {
            for (int i2 = 0; i2 < i; i2++) {
                Object obj = objArr[i2];
//                if (obj == null || nonThrowingPredicate.test(obj)) {
//                    break;
//                }
            }
        }
    }

    boolean accept(Relay<? super T> relay) {
        Object[] objArr = this.head;
        int i = this.capacity;
        while (true) {
            if (objArr == null) {
                return false;
            }
            for (int i2 = 0; i2 < i; i2++) {
                Object obj = objArr[i2];
                if (obj == null) {
                    break;
                }
                relay.accept((T)obj);
            }
            objArr = (Object[]) objArr[i];
        }
    }
}
