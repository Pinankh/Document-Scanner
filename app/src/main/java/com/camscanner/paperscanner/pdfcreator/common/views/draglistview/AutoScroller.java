package com.camscanner.paperscanner.pdfcreator.common.views.draglistview;

import android.content.Context;
import android.os.Handler;

class AutoScroller {
    private static final int AUTO_SCROLL_UPDATE_DELAY = 12;
    private static final int COLUMN_SCROLL_UPDATE_DELAY = 1000;
    private static final int SCROLL_SPEED_DP = 8;
    private AutoScrollMode mAutoScrollMode = AutoScrollMode.POSITION;
    private Handler mHandler = new Handler();
    private boolean mIsAutoScrolling;
    private long mLastScrollTime;
    private AutoScrollListener mListener;
    private int mScrollSpeed;

    interface AutoScrollListener {
        void onAutoScrollColumnBy(int i);

        void onAutoScrollPositionBy(int i, int i2);
    }

    enum AutoScrollMode {
        POSITION,
        COLUMN
    }

    enum ScrollDirection {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    AutoScroller(Context context, AutoScrollListener listener) {
        this.mListener = listener;
        this.mScrollSpeed = (int) (context.getResources().getDisplayMetrics().density * 8.0f);
    }

    /* access modifiers changed from: package-private */
    public void setAutoScrollMode(AutoScrollMode autoScrollMode) {
        this.mAutoScrollMode = autoScrollMode;
    }

    /* access modifiers changed from: package-private */
    public boolean isAutoScrolling() {
        return this.mIsAutoScrolling;
    }

    /* access modifiers changed from: package-private */
    public void stopAutoScroll() {
        this.mIsAutoScrolling = false;
    }


    static /* synthetic */ class C67203 {

        static final /* synthetic */ int[] f14676x27abfe8e = new int[ScrollDirection.values().length];

        static {
            try {
                f14676x27abfe8e[ScrollDirection.UP.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f14676x27abfe8e[ScrollDirection.DOWN.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f14676x27abfe8e[ScrollDirection.LEFT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f14676x27abfe8e[ScrollDirection.RIGHT.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void startAutoScroll(ScrollDirection direction) {
        int i = C67203.f14676x27abfe8e[direction.ordinal()];
        if (i == 1) {
            startAutoScrollPositionBy(0, this.mScrollSpeed);
        } else if (i == 2) {
            startAutoScrollPositionBy(0, -this.mScrollSpeed);
        } else if (i != 3) {
            if (i == 4) {
                if (this.mAutoScrollMode == AutoScrollMode.POSITION) {
                    startAutoScrollPositionBy(-this.mScrollSpeed, 0);
                } else {
                    startAutoScrollColumnBy(-1);
                }
            }
        } else if (this.mAutoScrollMode == AutoScrollMode.POSITION) {
            startAutoScrollPositionBy(this.mScrollSpeed, 0);
        } else {
            startAutoScrollColumnBy(1);
        }
    }

    private void startAutoScrollPositionBy(int dx, int dy) {
        if (!this.mIsAutoScrolling) {
            this.mIsAutoScrolling = true;
            autoScrollPositionBy(dx, dy);
        }
    }

    /* access modifiers changed from: private */
    public void autoScrollPositionBy(final int dx, final int dy) {
        if (this.mIsAutoScrolling) {
            this.mListener.onAutoScrollPositionBy(dx, dy);
            this.mHandler.postDelayed(new Runnable() {
                public void run() {
                    AutoScroller.this.autoScrollPositionBy(dx, dy);
                }
            }, 12);
        }
    }

    private void startAutoScrollColumnBy(int columns) {
        if (!this.mIsAutoScrolling) {
            this.mIsAutoScrolling = true;
            autoScrollColumnBy(columns);
        }
    }

    /* access modifiers changed from: private */
    public void autoScrollColumnBy(final int columns) {
        if (this.mIsAutoScrolling) {
            if (System.currentTimeMillis() - this.mLastScrollTime > 1000) {
                this.mListener.onAutoScrollColumnBy(columns);
                this.mLastScrollTime = System.currentTimeMillis();
            } else {
                this.mListener.onAutoScrollColumnBy(0);
            }
            this.mHandler.postDelayed(new Runnable() {
                public void run() {
                    AutoScroller.this.autoScrollColumnBy(columns);
                }
            }, 12);
        }
    }
}
