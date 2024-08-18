package com.camscanner.paperscanner.pdfcreator.common.views.draglistview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.views.draglistview.swipe.ListSwipeHelper;

public class DragListView extends FrameLayout {
    private DragItem mDragItem;

    public DragListCallback mDragListCallback;

    public DragListListener mDragListListener;

    public DragItemRecyclerView mRecyclerView;
    private ListSwipeHelper mSwipeHelper;

    public float mTouchX;

    public float mTouchY;

    public ImageView m_ivRemove;
    private LinearLayout m_llContent;

    public ConstraintLayout m_llTopBar;

    public TextView m_tvRemove;
    public boolean mbRemove;

    public interface DragListCallback {
        boolean canDragItemAtPosition(int i);

        boolean canDropItemAtPosition(int i);
    }

    public interface DragListListener {
        void onItemDragEnded(int i, int i2, boolean z);

        void onItemDragStarted(int i);

        void onItemDragging(int i, float f, float f2);
    }

    public static abstract class DragListListenerAdapter implements DragListListener {
        public void onItemDragStarted(int position) {
        }

        public void onItemDragging(int itemPosition, float x, float y) {
        }

        public void onItemDragEnded(int fromPosition, int toPosition, boolean remove) {
        }
    }

    public static abstract class DragListCallbackAdapter implements DragListCallback {
        public boolean canDragItemAtPosition(int dragPosition) {
            return true;
        }

        public boolean canDropItemAtPosition(int dropPosition) {
            return true;
        }
    }

    public DragListView(Context context) {
        super(context);
        this.m_llContent = new LinearLayout(context);
        this.m_llContent.setOrientation(1);
        addView(this.m_llContent);
    }

    public DragListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.m_llContent = new LinearLayout(context);
        this.m_llContent.setOrientation(1);
        addView(this.m_llContent);
    }

    public DragListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.m_llContent = new LinearLayout(context);
        this.m_llContent.setOrientation(1);
        addView(this.m_llContent);
    }


    @RequiresApi(api = 11)
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mDragItem = new DragItem(getContext());
        this.mRecyclerView = createRecyclerView();
        this.mRecyclerView.setDragItem(this.mDragItem);
        this.m_llTopBar = (ConstraintLayout) inflate(getContext(), R.layout.draglist_topbar, (ViewGroup) null);
        this.m_ivRemove = (ImageView) this.m_llTopBar.findViewById(R.id.iv_remove);
        this.m_tvRemove = (TextView) this.m_llTopBar.findViewById(R.id.tv_remove);
        this.m_llContent.addView(this.m_llTopBar);
        this.m_llContent.addView(this.mRecyclerView);
        this.m_llContent.addView(this.mDragItem.getDragItemView());
        this.m_llTopBar.post(new Runnable() {
            public final void run() {
                DragListView.this.lambda$onFinishInflate$0$DragListView();
            }
        });
        this.mbRemove = false;
    }

    public /* synthetic */ void lambda$onFinishInflate$0$DragListView() {
        this.m_llTopBar.setVisibility(8);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) this.m_llTopBar.getLayoutParams();
        params.height = (int) getResources().getDimension(R.dimen.delete_area_height);
        this.m_llTopBar.setLayoutParams(params);
        this.mDragItem.setTopbarHeight((float) params.height);
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        return handleTouchEvent(event) || super.onInterceptTouchEvent(event);
    }

    public boolean onTouchEvent(MotionEvent event) {
        return handleTouchEvent(event) || super.onTouchEvent(event);
    }


    private boolean handleTouchEvent(MotionEvent event) {
        this.mTouchX = event.getX();
        this.mTouchY = event.getY();
        if (!isDragging()) {
            return false;
        }
        int action = event.getAction();
        if (action != 1) {
            if (action == 2) {
                this.mRecyclerView.onDragging(event.getX(), event.getY());
            }
            return true;
        }
        this.mRecyclerView.onDragEnded();
        return true;
    }

    @RequiresApi(api = 11)
    private DragItemRecyclerView createRecyclerView() {
        DragItemRecyclerView recyclerView = (DragItemRecyclerView) LayoutInflater.from(getContext()).inflate(R.layout.drag_item_recycler_view, this, false);
        recyclerView.setMotionEventSplittingEnabled(false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setVerticalScrollBarEnabled(false);
        recyclerView.setHorizontalScrollBarEnabled(false);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                DragListView.this.getAdapter().notifyDataSetChanged();
            }
        });
        recyclerView.setDragItemListener(new DragItemRecyclerView.DragItemListener() {
            private int mDragStartPosition;

            @SuppressLint({"RestrictedApi"})
            public void onDragStarted(int itemPosition, float x, float y) {
                DragListView.this.getParent().requestDisallowInterceptTouchEvent(true);
                this.mDragStartPosition = itemPosition;
                if (DragListView.this.mDragListListener != null) {
                    DragListView.this.mDragListListener.onItemDragStarted(itemPosition);
                }
                ActionBar actionBar = ((AppCompatActivity) DragListView.this.getContext()).getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setShowHideAnimationEnabled(false);
                    actionBar.hide();
                }
                DragListView.this.m_llTopBar.setVisibility(0);
            }

            public void onDragging(int itemPosition, float x, float y) {
                if (DragListView.this.mDragListListener != null) {
                    DragListView.this.mDragListListener.onItemDragging(itemPosition, x, y);
                }
                Rect rect = new Rect();
                DragListView.this.m_llTopBar.getHitRect(rect);
                if (x <= ((float) rect.left) || x >= ((float) rect.right) || y <= ((float) rect.top) || y >= ((float) rect.bottom)) {
                    DragListView.this.m_ivRemove.setImageResource(R.drawable.ic_drag_delete_off);
                    DragListView.this.m_tvRemove.setTextColor(DragListView.this.getResources().getColor(R.color.colorTextScanner));
                    DragListView.this.m_llTopBar.setBackgroundColor(DragListView.this.getResources().getColor(R.color.colorBackgroundFooter));
                    boolean unused = DragListView.this.mbRemove = false;
                    return;
                }
                DragListView.this.m_ivRemove.setImageResource(R.drawable.ic_drag_delete_on);
                DragListView.this.m_tvRemove.setTextColor(DragListView.this.getResources().getColor(R.color.white));
                DragListView.this.m_llTopBar.setBackgroundColor(DragListView.this.getResources().getColor(R.color.colorPrimary));
                boolean unused2 = DragListView.this.mbRemove = true;
            }

            @SuppressLint({"RestrictedApi"})
            public void onDragEnded(int newItemPosition) {
                if (DragListView.this.mDragListListener != null) {
                    DragListView.this.mDragListListener.onItemDragEnded(this.mDragStartPosition, newItemPosition, DragListView.this.mbRemove);
                }
                try {
                    DragListView.this.m_ivRemove.getDrawable().setColorFilter(new PorterDuffColorFilter(DragListView.this.getResources().getColor(17170443), PorterDuff.Mode.SRC_ATOP));
                    DragListView.this.m_tvRemove.setTextColor(DragListView.this.getResources().getColor(17170443));
                } catch (Exception e) {
                }
                ActionBar actionBar = ((AppCompatActivity) DragListView.this.getContext()).getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setShowHideAnimationEnabled(false);
                    actionBar.show();
                }
                DragListView.this.m_llTopBar.setVisibility(8);
                boolean unused = DragListView.this.mbRemove = false;
            }
        });
        recyclerView.setDragItemCallback(new DragItemRecyclerView.DragItemCallback() {
            public boolean canDragItemAtPosition(int dragPosition) {
                return DragListView.this.mDragListCallback == null || DragListView.this.mDragListCallback.canDragItemAtPosition(dragPosition);
            }

            public boolean canDropItemAtPosition(int dropPosition) {
                return DragListView.this.mDragListCallback == null || DragListView.this.mDragListCallback.canDropItemAtPosition(dropPosition);
            }
        });
        return recyclerView;
    }

    public void setSwipeListener(ListSwipeHelper.OnSwipeListener swipeListener) {
        ListSwipeHelper listSwipeHelper = this.mSwipeHelper;
        if (listSwipeHelper == null) {
            this.mSwipeHelper = new ListSwipeHelper(getContext().getApplicationContext(), swipeListener);
        } else {
            listSwipeHelper.setSwipeListener(swipeListener);
        }
        this.mSwipeHelper.detachFromRecyclerView();
        if (swipeListener != null) {
            this.mSwipeHelper.attachToRecyclerView(this.mRecyclerView);
        }
    }

    public void resetSwipedViews(View exceptionView) {
        ListSwipeHelper listSwipeHelper = this.mSwipeHelper;
        if (listSwipeHelper != null) {
            listSwipeHelper.resetSwipedViews(exceptionView);
        }
    }

    public RecyclerView getRecyclerView() {
        return this.mRecyclerView;
    }

    public DragItemAdapter getAdapter() {
        DragItemRecyclerView dragItemRecyclerView = this.mRecyclerView;
        if (dragItemRecyclerView != null) {
            return (DragItemAdapter) dragItemRecyclerView.getAdapter();
        }
        return null;
    }

    public void setAdapter(DragItemAdapter adapter, boolean hasFixedItemSize) {
        this.mRecyclerView.setHasFixedSize(hasFixedItemSize);
        this.mRecyclerView.setAdapter(adapter);
        adapter.setDragStartedListener(new DragItemAdapter.DragStartCallback() {
            public boolean startDrag(View itemView, long itemId) {
                return DragListView.this.mRecyclerView.startDrag(itemView, itemId, DragListView.this.mTouchX, DragListView.this.mTouchY);
            }

            public boolean isDragging() {
                return DragListView.this.mRecyclerView.isDragging();
            }
        });
    }

    public void setLayoutManager(RecyclerView.LayoutManager layout) {
        this.mRecyclerView.setLayoutManager(layout);
    }

    public void setDragListListener(DragListListener listener) {
        this.mDragListListener = listener;
    }

    public void setDragListCallback(DragListCallback callback) {
        this.mDragListCallback = callback;
    }

    public boolean isDragEnabled() {
        return this.mRecyclerView.isDragEnabled();
    }

    public void setDragEnabled(boolean enabled) {
        this.mRecyclerView.setDragEnabled(enabled);
    }

    public void setCustomDragItem(DragItem dragItem) {
        DragItem newDragItem;
        this.m_llContent.removeViewAt(2);
        if (dragItem != null) {
            newDragItem = dragItem;
        } else {
            newDragItem = new DragItem(getContext());
        }
        newDragItem.setCanDragHorizontally(this.mDragItem.canDragHorizontally());
        newDragItem.setSnapToTouch(this.mDragItem.isSnapToTouch());
        this.mDragItem = newDragItem;
        this.mRecyclerView.setDragItem(this.mDragItem);
        addView(this.mDragItem.getDragItemView());
    }

    public boolean isDragging() {
        return this.mRecyclerView.isDragging();
    }

    public void setCanDragHorizontally(boolean canDragHorizontally) {
        this.mDragItem.setCanDragHorizontally(canDragHorizontally);
    }

    public void setSnapDragItemToTouch(boolean snapToTouch) {
        this.mDragItem.setSnapToTouch(snapToTouch);
    }

    public void setCanNotDragAboveTopItem(boolean canNotDragAboveTop) {
        this.mRecyclerView.setCanNotDragAboveTopItem(canNotDragAboveTop);
    }

    public void setCanNotDragBelowBottomItem(boolean canNotDragBelowBottom) {
        this.mRecyclerView.setCanNotDragBelowBottomItem(canNotDragBelowBottom);
    }

    public void setScrollingEnabled(boolean scrollingEnabled) {
        this.mRecyclerView.setScrollingEnabled(scrollingEnabled);
    }

    public void setDisableReorderWhenDragging(boolean disableReorder) {
        this.mRecyclerView.setDisableReorderWhenDragging(disableReorder);
    }

    public void setDropTargetDrawables(Drawable backgroundDrawable, Drawable foregroundDrawable) {
        this.mRecyclerView.setDropTargetDrawables(backgroundDrawable, foregroundDrawable);
    }
}
