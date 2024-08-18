package com.camscanner.paperscanner.pdfcreator.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.views.draglistview.DragItemAdapter;
import com.camscanner.paperscanner.pdfcreator.model.Document;
import com.camscanner.paperscanner.pdfcreator.view.interfaces.DragItemCallback;
import com.google.android.material.imageview.ShapeableImageView;

public class ItemAdapter extends DragItemAdapter<Document, ItemAdapter.ViewHolder> {
    private DragItemCallback mDragItemCallback;

    public boolean mDragOnLongPress;

    public int mGrabHandleId;
    private int mLayoutId;
    private boolean m_bShowLast = true;
    ArrayList<Document>  m_data = new ArrayList<>();
    private Context con;

    public ItemAdapter(Context context,ArrayList<Document> list, int layoutId, int grabHandleId, boolean dragOnLongPress, DragItemCallback callback) {
        this.con = context;
        this.mLayoutId = layoutId;
        this.m_data = list;
        this.mGrabHandleId = grabHandleId;
        this.mDragOnLongPress = dragOnLongPress;
        setItemList(list);
        this.mDragItemCallback = callback;
    }

    public void showLastItem(boolean bShow) {
        this.m_bShowLast = bShow;
        notifyDataSetChanged();
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(this.mLayoutId, parent, false);
        view.post(new Runnable() {
            public void run() {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.width = view.getWidth();
                layoutParams.height = (int) (((float) layoutParams.width) * 1.4151261f);
                view.setLayoutParams(layoutParams);
            }
        });
        return new ViewHolder(view, this.mDragItemCallback);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.setPosition(position);
        if (position == getItemCount() - 1) {
            holder.m_tvPage.setText("");
            holder.rlRealItem.setVisibility(4);
            holder.rlLastItem.setVisibility(0);
            if (this.m_bShowLast) {
                holder.m_vwContent.setVisibility(0);
            } else {
                holder.m_vwContent.setVisibility(View.GONE);
            }
        } else {
            holder.m_tvPage.setText(String.format("%d", new Object[]{Integer.valueOf(position + 1)}));
            holder.rlRealItem.setVisibility(0);
            holder.rlLastItem.setVisibility(4);

            InputStream is = null;
            try {
                Uri uri = Uri.fromFile(new File(((Document) getItemList().get(position)).thumb));
                is = con.getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                holder.m_ivThumb.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

           // Bitmap bitmap = BitmapFactory.decodeStream(is);
           // holder.m_ivThumb.setImageBitmap(BitmapFactory.decodeFile(((Document) getItemList().get(position)).thumb));


        }
        holder.itemView.setTag(this.mItemList.get(position));

    }


    public long getUniqueItemId(int position) {
        return ((Document) this.mItemList.get(position)).f14688ID;
    }

    class ViewHolder extends DragItemAdapter.ViewHolder {
        DragItemCallback dragItemCallback;
        boolean m_bResized = false;
        ShapeableImageView m_ivThumb;

        int m_nPosition;
        TextView m_tvPage;
        View m_vwContent;
        RelativeLayout rlLastItem;
        RelativeLayout rlRealItem;

        ViewHolder(View itemView, DragItemCallback callback) {
            super(itemView, ItemAdapter.this.mGrabHandleId, ItemAdapter.this.mDragOnLongPress);
            this.m_vwContent = itemView;
            this.m_ivThumb = (ShapeableImageView) itemView.findViewById(R.id.iv_file);

            this.m_tvPage = (TextView) itemView.findViewById(R.id.txt_file_num);
            this.rlRealItem = (RelativeLayout) itemView.findViewById(R.id.rl_grid_real_item);
            this.rlLastItem = (RelativeLayout) itemView.findViewById(R.id.rl_grid_last_item);
            this.dragItemCallback = callback;
        }

        public void setPosition(int position) {
            this.m_nPosition = position;
        }

        public void onItemClicked(View view) {
            DragItemCallback dragItemCallback2 = this.dragItemCallback;
            if (dragItemCallback2 != null) {
                dragItemCallback2.onItemClicked(this.m_nPosition);
            }
        }

        public boolean onItemLongClicked(View view) {
            return false;
        }
    }
}
