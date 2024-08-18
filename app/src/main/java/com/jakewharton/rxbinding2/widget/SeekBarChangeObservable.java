package com.jakewharton.rxbinding2.widget;

import androidx.annotation.Nullable;
import android.widget.SeekBar;
import com.jakewharton.rxbinding2.InitialValueObservable;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

final class SeekBarChangeObservable extends InitialValueObservable<Integer> {
    @Nullable
    private final Boolean shouldBeFromUser;
    private final SeekBar view;

    SeekBarChangeObservable(SeekBar view2, @Nullable Boolean shouldBeFromUser2) {
        this.view = view2;
        this.shouldBeFromUser = shouldBeFromUser2;
    }


    public void subscribeListener(Observer<? super Integer> observer) {
        if (Preconditions.checkMainThread(observer)) {
            Listener listener = new Listener(this.view, this.shouldBeFromUser, observer);
            this.view.setOnSeekBarChangeListener(listener);
            observer.onSubscribe(listener);
        }
    }


    public Integer getInitialValue() {
        return Integer.valueOf(this.view.getProgress());
    }

    static final class Listener extends MainThreadDisposable implements SeekBar.OnSeekBarChangeListener {
        private final Observer<? super Integer> observer;
        private final Boolean shouldBeFromUser;
        private final SeekBar view;

        Listener(SeekBar view2, Boolean shouldBeFromUser2, Observer<? super Integer> observer2) {
            this.view = view2;
            this.shouldBeFromUser = shouldBeFromUser2;
            this.observer = observer2;
        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!isDisposed()) {
                Boolean bool = this.shouldBeFromUser;
                if (bool == null || bool.booleanValue() == fromUser) {
                    this.observer.onNext(Integer.valueOf(progress));
                }
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
        }


        public void onDispose() {
            this.view.setOnSeekBarChangeListener((SeekBar.OnSeekBarChangeListener) null);
        }
    }
}
