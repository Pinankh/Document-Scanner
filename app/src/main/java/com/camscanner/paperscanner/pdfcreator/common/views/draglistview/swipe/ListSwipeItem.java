package com.camscanner.paperscanner.pdfcreator.common.views.draglistview.swipe;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.camscanner.paperscanner.pdfcreator.R;

public class ListSwipeItem extends RelativeLayout {
    private float mFlingSpeed;
    private View mLeftView;
    private int mLeftViewId;
    private View mRightView;
    private int mRightViewId;
    private float mStartSwipeTranslationX;
    private SwipeDirection mSwipeDirection = SwipeDirection.LEFT_AND_RIGHT;
    private SwipeInStyle mSwipeInStyle = SwipeInStyle.APPEAR;

    public ListSwipeHelper.OnSwipeListener mSwipeListener;
    private boolean mSwipeStarted;

    public SwipeState mSwipeState = SwipeState.IDLE;

    public float mSwipeTranslationX;
    private View mSwipeView;
    private int mSwipeViewId;

    public RecyclerView.ViewHolder mViewHolder;

    public enum SwipeDirection {
        LEFT,
        RIGHT,
        LEFT_AND_RIGHT,
        NONE
    }

    public enum SwipeInStyle {
        APPEAR,
        SLIDE
    }

    private enum SwipeState {
        IDLE,
        SWIPING,
        ANIMATING
    }

    public ListSwipeItem(Context context) {
        super(context);
    }

    public ListSwipeItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ListSwipeItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ListSwipeItem);
        this.mSwipeViewId = a.getResourceId(2, -1);
        this.mLeftViewId = a.getResourceId(0, -1);
        this.mRightViewId = a.getResourceId(1, -1);
        a.recycle();
    }


    public void onFinishInflate() {
        super.onFinishInflate();
        this.mSwipeView = findViewById(this.mSwipeViewId);
        this.mLeftView = findViewById(this.mLeftViewId);
        this.mRightView = findViewById(this.mRightViewId);
        View view = this.mLeftView;
        if (view != null) {
            view.setVisibility(4);
        }
        View view2 = this.mRightView;
        if (view2 != null) {
            view2.setVisibility(4);
        }
    }

    public void setTag(Object tag) {
        super.setTag(tag);
        RecyclerView.ViewHolder viewHolder = this.mViewHolder;
        if (viewHolder != null && viewHolder.isRecyclable()) {
            resetSwipe(false);
        }
    }

    public void setSwipeInStyle(SwipeInStyle style) {
        this.mSwipeInStyle = style;
    }

    public void setSupportedSwipeDirection(SwipeDirection swipeDirection) {
        this.mSwipeDirection = swipeDirection;
    }

    public SwipeDirection getSupportedSwipeDirection() {
        return this.mSwipeDirection;
    }


    public void setSwipeListener(ListSwipeHelper.OnSwipeListener listener) {
        this.mSwipeListener = listener;
    }


    public SwipeDirection getSwipedDirection() {
        if (this.mSwipeState != SwipeState.IDLE) {
            return SwipeDirection.NONE;
        }
        if (this.mSwipeView.getTranslationX() == ((float) (-getMeasuredWidth()))) {
            return SwipeDirection.LEFT;
        }
        if (this.mSwipeView.getTranslationX() == ((float) getMeasuredWidth())) {
            return SwipeDirection.RIGHT;
        }
        return SwipeDirection.NONE;
    }


    public boolean isAnimating() {
        return this.mSwipeState == SwipeState.ANIMATING;
    }

    public boolean isSwipeStarted() {
        return this.mSwipeStarted;
    }


    public void setFlingSpeed(float speed) {
        this.mFlingSpeed = speed;
    }


    public void swipeTranslationByX(float dx) {
        setSwipeTranslationX(this.mSwipeTranslationX + dx);
    }


    public void setSwipeTranslationX(float x) {
        if ((this.mSwipeDirection == SwipeDirection.LEFT && x > 0.0f) || ((this.mSwipeDirection == SwipeDirection.RIGHT && x < 0.0f) || this.mSwipeDirection == SwipeDirection.NONE)) {
            x = 0.0f;
        }
        this.mSwipeTranslationX = x;
        this.mSwipeTranslationX = Math.min(this.mSwipeTranslationX, (float) getMeasuredWidth());
        this.mSwipeTranslationX = Math.max(this.mSwipeTranslationX, (float) (-getMeasuredWidth()));
        this.mSwipeView.setTranslationX(this.mSwipeTranslationX);
        ListSwipeHelper.OnSwipeListener onSwipeListener = this.mSwipeListener;
        if (onSwipeListener != null) {
            onSwipeListener.onItemSwiping(this, this.mSwipeTranslationX);
        }
        float f = this.mSwipeTranslationX;
        if (f < 0.0f) {
            if (this.mSwipeInStyle == SwipeInStyle.SLIDE) {
                this.mRightView.setTranslationX(((float) getMeasuredWidth()) + this.mSwipeTranslationX);
            }
            this.mRightView.setVisibility(0);
            this.mLeftView.setVisibility(4);
        } else if (f > 0.0f) {
            if (this.mSwipeInStyle == SwipeInStyle.SLIDE) {
                this.mLeftView.setTranslationX(((float) (-getMeasuredWidth())) + this.mSwipeTranslationX);
            }
            this.mLeftView.setVisibility(0);
            this.mRightView.setVisibility(4);
        } else {
            this.mRightView.setVisibility(4);
            this.mLeftView.setVisibility(4);
        }
    }


    public void animateToSwipeTranslationX(float x, Animator.AnimatorListener... listeners) {
        if (x != this.mSwipeTranslationX) {
            this.mSwipeState = SwipeState.ANIMATING;
            ObjectAnimator animator = ObjectAnimator.ofFloat(this, "SwipeTranslationX", new float[]{this.mSwipeTranslationX, x});
            animator.setDuration(250);
            animator.setInterpolator(new DecelerateInterpolator());
            for (Animator.AnimatorListener listener : listeners) {
                if (listener != null) {
                    animator.addListener(listener);
                }
            }
            animator.start();
        }
    }


    public void resetSwipe(boolean animate) {
        if (!isAnimating() && this.mSwipeStarted) {
            if (this.mSwipeTranslationX == 0.0f) {
                this.mSwipeListener = null;
            } else if (animate) {
                animateToSwipeTranslationX(0.0f, new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        SwipeState unused = ListSwipeItem.this.mSwipeState = SwipeState.IDLE;
                        ListSwipeHelper.OnSwipeListener unused2 = ListSwipeItem.this.mSwipeListener = null;
                    }
                });
            } else {
                setSwipeTranslationX(0.0f);
                this.mSwipeState = SwipeState.IDLE;
                this.mSwipeListener = null;
            }
            RecyclerView.ViewHolder viewHolder = this.mViewHolder;
            if (viewHolder != null && !viewHolder.isRecyclable()) {
                this.mViewHolder.setIsRecyclable(true);
            }
            this.mViewHolder = null;
            this.mFlingSpeed = 0.0f;
            this.mStartSwipeTranslationX = 0.0f;
            this.mSwipeStarted = false;
        }
    }


    public void handleSwipeUp(Animator.AnimatorListener listener) {
        if (!isAnimating() && this.mSwipeStarted) {
            AnimatorListenerAdapter idleListener = new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    SwipeState unused = ListSwipeItem.this.mSwipeState = SwipeState.IDLE;
                    if (ListSwipeItem.this.mSwipeTranslationX == 0.0f) {
                        ListSwipeItem.this.resetSwipe(false);
                    }
                    if (ListSwipeItem.this.mViewHolder != null) {
                        ListSwipeItem.this.mViewHolder.setIsRecyclable(true);
                    }
                }
            };
            if (this.mFlingSpeed != 0.0f || Math.abs(this.mStartSwipeTranslationX - this.mSwipeTranslationX) >= ((float) (getMeasuredWidth() / 3))) {
                animateToSwipeTranslationX(getTranslateToXPosition(this.mStartSwipeTranslationX, this.mSwipeTranslationX, this.mFlingSpeed), idleListener, listener);
            } else {
                animateToSwipeTranslationX(this.mStartSwipeTranslationX, idleListener, listener);
            }
            this.mStartSwipeTranslationX = 0.0f;
            this.mFlingSpeed = 0.0f;
        }
    }

    private float getTranslateToXPosition(float startTranslationX, float currentTranslationX, float flingSpeed) {
        if (flingSpeed == 0.0f && Math.abs(startTranslationX - currentTranslationX) < ((float) (getMeasuredWidth() / 3))) {
            return startTranslationX;
        }
        if (currentTranslationX < 0.0f) {
            if (flingSpeed > 0.0f) {
                return 0.0f;
            }
            return (float) (-getMeasuredWidth());
        } else if (startTranslationX == 0.0f) {
            if (flingSpeed < 0.0f) {
                return 0.0f;
            }
            return (float) getMeasuredWidth();
        } else if (flingSpeed > 0.0f) {
            return (float) getMeasuredWidth();
        } else {
            return 0.0f;
        }
    }


    public void handleSwipeMoveStarted(ListSwipeHelper.OnSwipeListener listener) {
        this.mStartSwipeTranslationX = this.mSwipeTranslationX;
        this.mSwipeListener = listener;
    }


    public void handleSwipeMove(float dx, RecyclerView.ViewHolder viewHolder) {
        if (!isAnimating()) {
            this.mSwipeState = SwipeState.SWIPING;
            if (!this.mSwipeStarted) {
                this.mSwipeStarted = true;
                this.mViewHolder = viewHolder;
                this.mViewHolder.setIsRecyclable(false);
            }
            swipeTranslationByX(dx);
        }
    }
}
