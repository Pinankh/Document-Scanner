package com.camscanner.paperscanner.pdfcreator.common.views.draglistview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

public class DragItemRecyclerView extends RecyclerView implements AutoScroller.AutoScrollListener {

    public DragItemAdapter mAdapter;
    private AutoScroller mAutoScroller;
    private boolean mCanNotDragAboveTop;
    private boolean mCanNotDragBelowBottom;
    private boolean mClipToPadding;
    private boolean mDisableReorderWhenDragging;
    private DragItemCallback mDragCallback;
    private boolean mDragEnabled = true;
    private DragItem mDragItem;
    private long mDragItemId = -1;
    private int mDragItemPosition;
    private DragState mDragState = DragState.DRAG_ENDED;

    public Drawable mDropTargetBackgroundDrawable;

    public Drawable mDropTargetForegroundDrawable;

    public boolean mHoldChangePosition;
    private DragItemListener mListener;
    private boolean mScrollingEnabled = true;
    private float mStartY;
    private int mTouchSlop;

    public interface DragItemCallback {
        boolean canDragItemAtPosition(int i);

        boolean canDropItemAtPosition(int i);
    }

    public interface DragItemListener {
        void onDragEnded(int i);

        void onDragStarted(int i, float f, float f2);

        void onDragging(int i, float f, float f2);
    }

    private enum DragState {
        DRAG_STARTED,
        DRAGGING,
        DRAG_ENDED
    }

    public DragItemRecyclerView(Context context) {
        super(context);
        init();
    }

    public DragItemRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init();
    }

    public DragItemRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        this.mAutoScroller = new AutoScroller(getContext(), this);
        this.mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        addItemDecoration(new RecyclerView.ItemDecoration() {
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
                drawDecoration(c, parent, DragItemRecyclerView.this.mDropTargetBackgroundDrawable);
            }

            public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDrawOver(c, parent, state);
                drawDecoration(c, parent, DragItemRecyclerView.this.mDropTargetForegroundDrawable);
            }

            private void drawDecoration(Canvas c, RecyclerView parent, Drawable drawable) {
                if (DragItemRecyclerView.this.mAdapter != null && DragItemRecyclerView.this.mAdapter.getDropTargetId() != -1 && drawable != null) {
                    for (int i = 0; i < parent.getChildCount(); i++) {
                        View item = parent.getChildAt(i);
                        int pos = DragItemRecyclerView.this.getChildAdapterPosition(item);
                        if (pos != -1 && DragItemRecyclerView.this.mAdapter.getItemId(pos) == DragItemRecyclerView.this.mAdapter.getDropTargetId()) {
                            drawable.setBounds(item.getLeft(), item.getTop(), item.getRight(), item.getBottom());
                            drawable.draw(c);
                        }
                    }
                }
            }
        });
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!this.mScrollingEnabled) {
            return false;
        }
        int action = event.getAction();
        if (action == 0) {
            this.mStartY = event.getY();
        } else if (action == 2) {
            double d = (double) this.mTouchSlop;
            Double.isNaN(d);
            if (((double) Math.abs(event.getY() - this.mStartY)) > d * 0.5d) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
        }
        return super.onInterceptTouchEvent(event);
    }


    public void setDragEnabled(boolean enabled) {
        this.mDragEnabled = enabled;
    }


    public boolean isDragEnabled() {
        return this.mDragEnabled;
    }


    public void setCanNotDragAboveTopItem(boolean canNotDragAboveTop) {
        this.mCanNotDragAboveTop = canNotDragAboveTop;
    }


    public void setCanNotDragBelowBottomItem(boolean canNotDragBelowBottom) {
        this.mCanNotDragBelowBottom = canNotDragBelowBottom;
    }


    public void setScrollingEnabled(boolean scrollingEnabled) {
        this.mScrollingEnabled = scrollingEnabled;
    }


    public void setDisableReorderWhenDragging(boolean disableReorder) {
        this.mDisableReorderWhenDragging = disableReorder;
    }

    public void setDropTargetDrawables(Drawable backgroundDrawable, Drawable foregroundDrawable) {
        this.mDropTargetBackgroundDrawable = backgroundDrawable;
        this.mDropTargetForegroundDrawable = foregroundDrawable;
    }


    public void setDragItemListener(DragItemListener listener) {
        this.mListener = listener;
    }


    public void setDragItemCallback(DragItemCallback callback) {
        this.mDragCallback = callback;
    }


    public void setDragItem(DragItem dragItem) {
        this.mDragItem = dragItem;
    }


    public boolean isDragging() {
        return this.mDragState != DragState.DRAG_ENDED;
    }


    public long getDragItemId() {
        return this.mDragItemId;
    }

    public void setClipToPadding(boolean clipToPadding) {
        super.setClipToPadding(clipToPadding);
        this.mClipToPadding = clipToPadding;
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        if (!isInEditMode()) {
            if (!(adapter instanceof DragItemAdapter)) {
                throw new RuntimeException("Adapter must extend DragItemAdapter");
            } else if (!adapter.hasStableIds()) {
                throw new RuntimeException("Adapter must have stable ids");
            }
        }
        super.setAdapter(adapter);
        this.mAdapter = (DragItemAdapter) adapter;
    }

    public void setLayoutManager(RecyclerView.LayoutManager layout) {
        super.setLayoutManager(layout);
        if (!(layout instanceof LinearLayoutManager)) {
            throw new RuntimeException("Layout must be an instance of LinearLayoutManager");
        }
    }

    public void onAutoScrollPositionBy(int dx, int dy) {
        if (isDragging()) {
            scrollBy(dx, dy);
            updateDragPositionAndScroll();
            return;
        }
        this.mAutoScroller.stopAutoScroll();
    }

    public void onAutoScrollColumnBy(int columns) {
    }

    public View findChildView(float x, float y) {
        int count = getChildCount();
        if (y <= 0.0f && count > 0) {
            return getChildAt(0);
        }
        for (int i = count - 1; i >= 0; i--) {
            View child = getChildAt(i);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
            if (x >= ((float) (child.getLeft() - params.leftMargin)) && x <= ((float) (child.getRight() + params.rightMargin)) && y >= ((float) (child.getTop() - params.topMargin)) && y <= ((float) (child.getBottom() + params.bottomMargin))) {
                return child;
            }
        }
        return null;
    }

    private boolean shouldChangeItemPosition(int newPos) {
        int i;
        if (this.mHoldChangePosition || (i = this.mDragItemPosition) == -1 || i == newPos) {
            return false;
        }
        if ((this.mCanNotDragAboveTop && newPos == 0) || (this.mCanNotDragBelowBottom && newPos == this.mAdapter.getItemCount() - 1)) {
            return false;
        }
        DragItemCallback dragItemCallback = this.mDragCallback;
        if (dragItemCallback == null || dragItemCallback.canDropItemAtPosition(newPos)) {
            return true;
        }
        return false;
    }

    private void updateDragPositionAndScroll() {
        View view = findChildView(this.mDragItem.getX(), this.mDragItem.getY());
        int newPos = getChildLayoutPosition(view);
        if (newPos != -1 && view != null) {
            if ((getLayoutManager() instanceof LinearLayoutManager) && !(getLayoutManager() instanceof GridLayoutManager)) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                int viewHeight = view.getMeasuredHeight() + params.topMargin + params.bottomMargin;
                int viewCenterY = (view.getTop() - params.topMargin) + (viewHeight / 2);
                boolean movedPassedCenterY = !(this.mDragItemPosition < getChildLayoutPosition(view)) ? this.mDragItem.getY() < ((float) viewCenterY) : this.mDragItem.getY() > ((float) viewCenterY);
                if (viewHeight > this.mDragItem.getDragItemView().getMeasuredHeight() && !movedPassedCenterY) {
                    newPos = this.mDragItemPosition;
                }
            }
            LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
            if (shouldChangeItemPosition(newPos)) {
                if (this.mDisableReorderWhenDragging) {
                    DragItemAdapter dragItemAdapter = this.mAdapter;
                    dragItemAdapter.setDropTargetId(dragItemAdapter.getItemId(newPos));
                    this.mAdapter.notifyDataSetChanged();
                } else {
                    int pos = layoutManager.findFirstVisibleItemPosition();
                    View posView = layoutManager.findViewByPosition(pos);
                    this.mAdapter.changeItemPosition(this.mDragItemPosition, newPos);
                    this.mDragItemPosition = newPos;
                    if (layoutManager.getOrientation() == 1) {
                        layoutManager.scrollToPositionWithOffset(pos, posView.getTop() - ((ViewGroup.MarginLayoutParams) posView.getLayoutParams()).topMargin);
                    } else {
                        layoutManager.scrollToPositionWithOffset(pos, posView.getLeft() - ((ViewGroup.MarginLayoutParams) posView.getLayoutParams()).leftMargin);
                    }
                }
            }
            boolean lastItemReached = false;
            boolean firstItemReached = false;
            int top = this.mClipToPadding ? getPaddingTop() : 0;
            int bottom = this.mClipToPadding ? getHeight() - getPaddingBottom() : getHeight();
            int left = this.mClipToPadding ? getPaddingLeft() : 0;
            int right = this.mClipToPadding ? getWidth() - getPaddingRight() : getWidth();
            RecyclerView.ViewHolder lastChild = findViewHolderForLayoutPosition(this.mAdapter.getItemCount() - 1);
            RecyclerView.ViewHolder firstChild = findViewHolderForLayoutPosition(0);
            if (layoutManager.getOrientation() == 1) {
                if (lastChild != null && lastChild.itemView.getBottom() <= bottom) {
                    lastItemReached = true;
                }
                if (firstChild != null && firstChild.itemView.getTop() >= top) {
                    firstItemReached = true;
                }
            } else {
                if (lastChild != null && lastChild.itemView.getRight() <= right) {
                    lastItemReached = true;
                }
                if (firstChild != null && firstChild.itemView.getLeft() >= left) {
                    firstItemReached = true;
                }
            }
            if (layoutManager.getOrientation() == 1) {
                if (this.mDragItem.getY() > ((float) (getHeight() - view.getHeight())) && !lastItemReached) {
                    this.mAutoScroller.startAutoScroll(AutoScroller.ScrollDirection.UP);
                } else if (this.mDragItem.getY() >= ((float) view.getHeight()) || firstItemReached) {
                    this.mAutoScroller.stopAutoScroll();
                } else {
                    this.mAutoScroller.startAutoScroll(AutoScroller.ScrollDirection.DOWN);
                }
            } else if (this.mDragItem.getX() > ((float) (getWidth() - view.getWidth())) && !lastItemReached) {
                this.mAutoScroller.startAutoScroll(AutoScroller.ScrollDirection.LEFT);
            } else if (this.mDragItem.getX() >= ((float) view.getWidth()) || firstItemReached) {
                this.mAutoScroller.stopAutoScroll();
            } else {
                this.mAutoScroller.startAutoScroll(AutoScroller.ScrollDirection.RIGHT);
            }
        }
    }


    public boolean startDrag(View itemView, long itemId, float x, float y) {
        int dragItemPosition = this.mAdapter.getPositionForItemId(itemId);
        if (!this.mDragEnabled || ((this.mCanNotDragAboveTop && dragItemPosition == 0) || (this.mCanNotDragBelowBottom && dragItemPosition == this.mAdapter.getItemCount() - 1))) {
            return false;
        }
        DragItemCallback dragItemCallback = this.mDragCallback;
        if (dragItemCallback != null && !dragItemCallback.canDragItemAtPosition(dragItemPosition)) {
            return false;
        }
        getParent().requestDisallowInterceptTouchEvent(false);
        this.mDragState = DragState.DRAG_STARTED;
        this.mDragItemId = itemId;
        this.mDragItem.startDrag(itemView, x, y);
        this.mDragItemPosition = dragItemPosition;
        updateDragPositionAndScroll();
        this.mAdapter.setDragItemId(this.mDragItemId);
        this.mAdapter.notifyDataSetChanged();
        DragItemListener dragItemListener = this.mListener;
        if (dragItemListener != null) {
            dragItemListener.onDragStarted(this.mDragItemPosition, this.mDragItem.getX(), this.mDragItem.getY());
        }
        invalidate();
        return true;
    }


    public void onDragging(float x, float y) {
        if (this.mDragState != DragState.DRAG_ENDED) {
            this.mDragState = DragState.DRAGGING;
            this.mDragItemPosition = this.mAdapter.getPositionForItemId(this.mDragItemId);
            this.mDragItem.setPosition(x, y);
            if (!this.mAutoScroller.isAutoScrolling()) {
                updateDragPositionAndScroll();
            }
            DragItemListener dragItemListener = this.mListener;
            if (dragItemListener != null) {
                dragItemListener.onDragging(this.mDragItemPosition, x, y);
            }
            invalidate();
        }
    }


    public void onDragEnded() {
        if (this.mDragState != DragState.DRAG_ENDED) {
            this.mAutoScroller.stopAutoScroll();
            setEnabled(false);
            if (this.mDisableReorderWhenDragging) {
                DragItemAdapter dragItemAdapter = this.mAdapter;
                int newPos = dragItemAdapter.getPositionForItemId(dragItemAdapter.getDropTargetId());
                if (newPos != -1) {
                    this.mAdapter.swapItems(this.mDragItemPosition, newPos);
                    this.mDragItemPosition = newPos;
                }
                this.mAdapter.setDropTargetId(-1);
            }
            post(new Runnable() {
                public final void run() {
                    DragItemRecyclerView.this.lambda$onDragEnded$0$DragItemRecyclerView();
                }
            });
        }
    }

    public void lambda$onDragEnded$0$DragItemRecyclerView() {
        final RecyclerView.ViewHolder holder = findViewHolderForAdapterPosition(this.mDragItemPosition);
        if (holder != null) {
            getItemAnimator().endAnimation(holder);
            this.mDragItem.endDrag(holder.itemView, new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    holder.itemView.setAlpha(1.0f);
                    DragItemRecyclerView.this.onDragItemAnimationEnd();
                }
            });
            return;
        }
        onDragItemAnimationEnd();
    }


    public void onDragItemAnimationEnd() {
        this.mAdapter.setDragItemId(-1);
        this.mAdapter.setDropTargetId(-1);
        this.mAdapter.notifyDataSetChanged();
        this.mDragState = DragState.DRAG_ENDED;
        DragItemListener dragItemListener = this.mListener;
        if (dragItemListener != null) {
            dragItemListener.onDragEnded(this.mDragItemPosition);
        }
        this.mDragItemId = -1;
        this.mDragItem.hide();
        setEnabled(true);
        invalidate();
    }


    public int getDragPositionForY(float y) {
        int pos;
        View child = findChildView(0.0f, y);
        if (child != null || getChildCount() <= 0) {
            pos = getChildLayoutPosition(child);
        } else {
            pos = getChildLayoutPosition(getChildAt(getChildCount() - 1)) + 1;
        }
        if (pos == -1) {
            return 0;
        }
        return pos;
    }


    public void addDragItemAndStart(float y, Object item, long itemId) {
        int pos = getDragPositionForY(y);
        this.mDragState = DragState.DRAG_STARTED;
        this.mDragItemId = itemId;
        this.mAdapter.setDragItemId(this.mDragItemId);
        this.mAdapter.addItem(pos, item);
        this.mDragItemPosition = pos;
        this.mHoldChangePosition = true;
        postDelayed(new Runnable() {
            public void run() {
                boolean unused = DragItemRecyclerView.this.mHoldChangePosition = false;
            }
        }, getItemAnimator().getMoveDuration());
        invalidate();
    }


    public Object removeDragItemAndEnd() {
        if (this.mDragItemPosition == -1) {
            return null;
        }
        this.mAutoScroller.stopAutoScroll();
        Object item = this.mAdapter.removeItem(this.mDragItemPosition);
        this.mAdapter.setDragItemId(-1);
        this.mDragState = DragState.DRAG_ENDED;
        this.mDragItemId = -1;
        invalidate();
        return item;
    }
}
