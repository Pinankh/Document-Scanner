package com.jakewharton.rxbinding2.internal;

import androidx.annotation.RestrictTo;

import java.util.concurrent.Callable;
import io.reactivex.functions.Predicate;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public final class Functions {
    private static final Always ALWAYS_TRUE = new Always(true);
    public static final Callable<Boolean> CALLABLE_ALWAYS_TRUE;
    public static final Predicate<Object> PREDICATE_ALWAYS_TRUE;

    static {
        Always always = ALWAYS_TRUE;
        CALLABLE_ALWAYS_TRUE = always;
        PREDICATE_ALWAYS_TRUE = always;
    }

    private static final class Always implements Callable<Boolean>, Predicate<Object> {
        private final Boolean value;

        Always(Boolean value2) {
            this.value = value2;
        }

        public Boolean call() {
            return this.value;
        }

        public boolean test(Object t) throws Exception {
            return this.value.booleanValue();
        }
    }

    private Functions() {
        throw new AssertionError("No instances.");
    }
}
