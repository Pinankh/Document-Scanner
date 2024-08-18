package com.camscanner.paperscanner.pdfcreator.common.views.draglistview;

import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import com.camscanner.paperscanner.pdfcreator.common.views.draglistview.DragItemAdapter.ViewHolder;

public abstract class DragItemAdapter<T, VH extends ViewHolder> extends RecyclerView.Adapter<VH> {
    private long mDragItemId = -1;
    private DragStartCallback mDragStartCallback;
    private long mDropTargetId = -1;
    protected List<T> mItemList;

    interface DragStartCallback {
        boolean isDragging();

        boolean startDrag(View view, long j);
    }

    public abstract long getUniqueItemId(int i);

    public DragItemAdapter() {
        setHasStableIds(true);
    }

    public void setItemList(List<T> itemList) {
        this.mItemList = itemList;
        notifyDataSetChanged();
    }

    public List<T> getItemList() {
        return this.mItemList;
    }

    public int getPositionForItem(T item) {
        int count = getItemCount();
        for (int i = 0; i < count; i++) {
            if (this.mItemList.get(i) == item) {
                return i;
            }
        }
        return -1;
    }

    public Object removeItem(int pos) {
        List<T> list = this.mItemList;
        if (list == null || list.size() <= pos || pos < 0) {
            return null;
        }
        Object item = this.mItemList.remove(pos);
        notifyItemRemoved(pos);
        return item;
    }

    public void addItem(int pos, T item) {
        List<T> list = this.mItemList;
        if (list != null && list.size() >= pos) {
            this.mItemList.add(pos, item);
            notifyItemInserted(pos);
        }
    }

    public void changeItemPosition(int fromPos, int toPos) {
        List<T> list = this.mItemList;
        if (list != null && list.size() > fromPos && this.mItemList.size() > toPos) {
            this.mItemList.add(toPos, this.mItemList.remove(fromPos));
            notifyItemMoved(fromPos, toPos);
        }
    }

    public void swapItems(int pos1, int pos2) {
        List<T> list = this.mItemList;
        if (list != null && list.size() > pos1 && this.mItemList.size() > pos2) {
            Collections.swap(this.mItemList, pos1, pos2);
            notifyDataSetChanged();
        }
    }

    public int getPositionForItemId(long id) {
        int count = getItemCount();
        for (int i = 0; i < count; i++) {
            if (id == getItemId(i)) {
                return i;
            }
        }
        return -1;
    }

    public final long getItemId(int position) {
        return getUniqueItemId(position);
    }

    public int getItemCount() {
        List<T> list = this.mItemList;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public void onBindViewHolder(VH holder, int position) {
        long itemId = getItemId(position);
        holder.mItemId = itemId;
        holder.itemView.setVisibility(this.mDragItemId == itemId ? 4 : 0);
        holder.setDragStartCallback(this.mDragStartCallback);
    }

    public void onViewRecycled(VH holder) {
        super.onViewRecycled(holder);
        holder.setDragStartCallback((DragStartCallback) null);
    }

    /* access modifiers changed from: package-private */
    public void setDragStartedListener(DragStartCallback dragStartedListener) {
        this.mDragStartCallback = dragStartedListener;
    }

    /* access modifiers changed from: package-private */
    public void setDragItemId(long dragItemId) {
        this.mDragItemId = dragItemId;
    }

    /* access modifiers changed from: package-private */
    public void setDropTargetId(long dropTargetId) {
        this.mDropTargetId = dropTargetId;
    }

    public long getDropTargetId() {
        return this.mDropTargetId;
    }

    public static abstract class ViewHolder extends RecyclerView.ViewHolder {
        /* access modifiers changed from: private */
        public DragStartCallback mDragStartCallback;
        public View mGrabView;
        public long mItemId;

        public ViewHolder(final View itemView, int handleResId, boolean dragOnLongPress) {
            super(itemView);
            this.mGrabView = itemView.findViewById(handleResId);
            if (dragOnLongPress) {

            } else {
                this.mGrabView.setOnTouchListener(new View.OnTouchListener() {
                    public boolean onTouch(View view, MotionEvent event) {
                        if (ViewHolder.this.mDragStartCallback == null) {
                            return false;
                        }
                        if (event.getAction() == 0 && ViewHolder.this.mDragStartCallback.startDrag(itemView, ViewHolder.this.mItemId)) {
                            return true;
                        }
                        if (ViewHolder.this.mDragStartCallback.isDragging() || itemView != ViewHolder.this.mGrabView) {
                            return false;
                        }
                        return ViewHolder.this.onItemTouch(view, event);
                    }
                });
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {

                    ViewHolder.this.onItemClicked(view);
                }
            });
            if (itemView != this.mGrabView) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    public boolean onLongClick(View view) {
                        return ViewHolder.this.onItemLongClicked(view);
                    }
                });
                itemView.setOnTouchListener(new View.OnTouchListener() {
                    public boolean onTouch(View view, MotionEvent event) {
                        return ViewHolder.this.onItemTouch(view, event);
                    }
                });
            }
        }

        public void setDragStartCallback(DragStartCallback dragStartedListener) {
            this.mDragStartCallback = dragStartedListener;
        }

        public void onItemClicked(View view) {
        }

        public boolean onItemLongClicked(View view) {
            return false;
        }

        public boolean onItemTouch(View view, MotionEvent event) {
            return false;
        }
    }
}
