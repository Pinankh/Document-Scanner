package com.camscanner.paperscanner.pdfcreator.common.views.draglistview.swipe;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.recyclerview.widget.RecyclerView;

public class ListSwipeHelper extends RecyclerView.OnScrollListener implements RecyclerView.OnItemTouchListener {
    private GestureDetector mGestureDetector;
    private GestureListener mGestureListener = new GestureListener();

    public RecyclerView mRecyclerView;

    public OnSwipeListener mSwipeListener;

    public ListSwipeItem mSwipeView;

    public int mTouchSlop;

    public interface OnSwipeListener {
        void onItemSwipeEnded(ListSwipeItem listSwipeItem, ListSwipeItem.SwipeDirection swipeDirection);

        void onItemSwipeStarted(ListSwipeItem listSwipeItem);

        void onItemSwiping(ListSwipeItem listSwipeItem, float f);
    }

    public static abstract class OnSwipeListenerAdapter implements OnSwipeListener {
        public void onItemSwipeStarted(ListSwipeItem item) {
        }

        public void onItemSwipeEnded(ListSwipeItem item, ListSwipeItem.SwipeDirection swipedDirection) {
        }

        public void onItemSwiping(ListSwipeItem item, float swipedDistanceX) {
        }
    }

    public ListSwipeHelper(Context applicationContext, OnSwipeListener listener) {
        this.mSwipeListener = listener;
        this.mGestureDetector = new GestureDetector(applicationContext, this.mGestureListener);
    }

    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent event) {
        handleTouch(rv, event);
        return this.mGestureListener.isSwipeStarted();
    }

    public void onTouchEvent(RecyclerView rv, MotionEvent event) {
        handleTouch(rv, event);
    }

    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        resetSwipedViews((View) null);
    }

    public void resetSwipedViews(View exceptionView) {
        int childCount = this.mRecyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = this.mRecyclerView.getChildAt(i);
            if ((view instanceof ListSwipeItem) && view != exceptionView) {
                ((ListSwipeItem) view).resetSwipe(true);
            }
        }
    }

    private void handleTouch(RecyclerView rv, MotionEvent event) {
        this.mGestureDetector.onTouchEvent(event);
        int action = event.getAction();
        if (action == 0) {
            View swipeView = rv.findChildViewUnder(event.getX(), event.getY());
            if ((swipeView instanceof ListSwipeItem) && ((ListSwipeItem) swipeView).getSupportedSwipeDirection() != ListSwipeItem.SwipeDirection.NONE) {
                this.mSwipeView = (ListSwipeItem) swipeView;
            }
        } else if (action == 1 || action == 3) {
            if (this.mSwipeView != null) {
                final ListSwipeItem endingSwipeView = this.mSwipeView;
                endingSwipeView.handleSwipeUp(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (endingSwipeView.isSwipeStarted()) {
                            ListSwipeHelper.this.resetSwipedViews(endingSwipeView);
                        }
                        if (ListSwipeHelper.this.mSwipeListener != null) {
                            OnSwipeListener access$100 = ListSwipeHelper.this.mSwipeListener;
                            ListSwipeItem listSwipeItem = endingSwipeView;
                            access$100.onItemSwipeEnded(listSwipeItem, listSwipeItem.getSwipedDirection());
                        }
                    }
                });
            } else {
                resetSwipedViews((View) null);
            }
            this.mSwipeView = null;
            this.mRecyclerView.requestDisallowInterceptTouchEvent(false);
        }
    }

    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    public void detachFromRecyclerView() {
        RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView != null) {
            recyclerView.removeOnItemTouchListener(this);
            this.mRecyclerView.removeOnScrollListener(this);
        }
        this.mRecyclerView = null;
    }

    public void attachToRecyclerView(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
        this.mRecyclerView.addOnItemTouchListener(this);
        this.mRecyclerView.addOnScrollListener(this);
        this.mTouchSlop = ViewConfiguration.get(this.mRecyclerView.getContext()).getScaledTouchSlop();
    }

    public void setSwipeListener(OnSwipeListener listener) {
        this.mSwipeListener = listener;
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private boolean mSwipeStarted;

        private GestureListener() {
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (e1 == null || e2 == null || ListSwipeHelper.this.mSwipeView == null || ListSwipeHelper.this.mRecyclerView.getScrollState() != 0) {
                return false;
            }
            float diffX = Math.abs(e2.getX() - e1.getX());
            float diffY = Math.abs(e2.getY() - e1.getY());
            if (!this.mSwipeStarted && diffX > ((float) (ListSwipeHelper.this.mTouchSlop * 2)) && 0.5f * diffX > diffY) {
                this.mSwipeStarted = true;
                ListSwipeHelper.this.mRecyclerView.requestDisallowInterceptTouchEvent(true);
                ListSwipeHelper.this.mSwipeView.handleSwipeMoveStarted(ListSwipeHelper.this.mSwipeListener);
                if (ListSwipeHelper.this.mSwipeListener != null) {
                    ListSwipeHelper.this.mSwipeListener.onItemSwipeStarted(ListSwipeHelper.this.mSwipeView);
                }
            }
            if (this.mSwipeStarted) {
                ListSwipeHelper.this.mSwipeView.handleSwipeMove(-distanceX, ListSwipeHelper.this.mRecyclerView.getChildViewHolder(ListSwipeHelper.this.mSwipeView));
            }
            return this.mSwipeStarted;
        }

        public boolean onDown(MotionEvent e) {
            this.mSwipeStarted = false;
            return true;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (!canStartSwipe(e1, e2)) {
                return false;
            }
            ListSwipeHelper.this.mSwipeView.setFlingSpeed(velocityX);
            return true;
        }


        public boolean isSwipeStarted() {
            return this.mSwipeStarted;
        }

        private boolean canStartSwipe(MotionEvent e1, MotionEvent e2) {
            return (e1 == null || e2 == null || ListSwipeHelper.this.mSwipeView == null || ListSwipeHelper.this.mRecyclerView.getScrollState() != 0 || ListSwipeHelper.this.mSwipeView.getSupportedSwipeDirection() == ListSwipeItem.SwipeDirection.NONE) ? false : true;
        }
    }
}
