package com.camscanner.paperscanner.pdfcreator.view.adapter;

import android.content.res.Resources;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import java.util.List;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.features.filters.model.FilterData;
import com.google.android.material.imageview.ShapeableImageView;

public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {
    private final List<FilterData> horizontalList;
    private final int itemSide;
    private final OnFilterClickListener listener;
    private final int paddingColor;
    private final int paddingSize;
    private final int parentWidth;
    private int selectedPosition;

    public interface OnFilterClickListener {
        void onFilterItemClicked(int i);
    }

    public HorizontalAdapter(List<FilterData> horizontalList2, Resources res, int parentWith, int initialSelected, OnFilterClickListener listener2) {
        this.horizontalList = horizontalList2;
        this.listener = listener2;
        this.parentWidth = parentWith;
        this.selectedPosition = initialSelected;
        this.itemSide = (int) res.getDimension(R.dimen.filter_toolbar_height);
        this.paddingSize = (int) res.getDimension(R.dimen.filter_item_padding);
        this.paddingColor = res.getColor(R.color.colorPrimary);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView filterName;
        ShapeableImageView filterPreview;
        ViewGroup root;

        public MyViewHolder(View view) {
            super(view);
            this.root = (ViewGroup) view.findViewById(R.id.root);
            this.filterPreview = (ShapeableImageView) view.findViewById(R.id.iv_filter);
            this.filterName = (TextView) view.findViewById(R.id.txt_filter_name);
        }
    }

    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_menu, parent, false));
    }

    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.filterName.setText(this.horizontalList.get(position).txt);
        if (this.selectedPosition == position) {
            //holder.filterPreview.setBackgroundColor(this.paddingColor);
            ShapeableImageView imageView = holder.filterPreview;
            imageView.setStrokeColorResource(R.color.colorPrimary);
            imageView.setStrokeWidth(2.0f);
            /*int i = this.paddingSize;
            imageView.setPadding(i, i, i, i);*/
        } else {
           /* holder.filterPreview.setBackgroundResource(0);
            holder.filterPreview.setPadding(0, 0, 0, 0);*/
            holder.filterPreview.setStrokeWidth(0f);
        }
        ((RequestBuilder) Glide.with((View) holder.filterPreview).load(this.horizontalList.get(position).thumbImg).centerCrop()).into(holder.filterPreview);
        holder.root.setOnClickListener(new View.OnClickListener() {


            public final void onClick(View view) {
                HorizontalAdapter.this.lambda$onBindViewHolder$0$HorizontalAdapter(position, view);
            }
        });
    }

    public /* synthetic */ void lambda$onBindViewHolder$0$HorizontalAdapter(int position, View v) {
        this.selectedPosition = position;
        OnFilterClickListener onFilterClickListener = this.listener;
        if (onFilterClickListener != null) {
            onFilterClickListener.onFilterItemClicked(position);
        }
        notifyDataSetChanged();
    }

    public int getItemCount() {
        return this.horizontalList.size();
    }

    public int getItemWidth() {
        return this.itemSide;
    }

    public int getParentWidth() {
        return this.parentWidth;
    }
}
