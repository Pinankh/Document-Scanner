package com.camscanner.paperscanner.pdfcreator.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import co.lujun.androidtagview.TagItem;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.Constant;

public class TagSpinnerAdapter extends BaseAdapter {
    private final Context context;
    private List<TagItem> mItems = new ArrayList();

    public TagSpinnerAdapter(Context context2) {
        this.context = context2;
    }

    public void clear() {
        this.mItems.clear();
    }

    public void setmItems(List<TagItem> tagItems) {
        this.mItems = tagItems;
    }

    public int getCount() {
        return this.mItems.size();
    }

    public TagItem getItem(int position) {
        return this.mItems.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getDropDownView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals(Constant.STR_DROPDOWN)) {
            view = LayoutInflater.from(this.context).inflate(R.layout.toolbar_spinner_item_dropdown, parent, false);
            view.setTag(Constant.STR_DROPDOWN);
        }
        ((TextView) view.findViewById(16908308)).setText(this.mItems.get(position).tagName);
        return view;
    }

    public View getView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals(Constant.STR_NON_DROPDOWN)) {
            view = LayoutInflater.from(this.context).inflate(R.layout.toolbar_spinner_item_actionbar, parent, false);
            view.setTag(Constant.STR_NON_DROPDOWN);
        }
        ((TextView) view.findViewById(16908308)).setText(this.mItems.get(position).tagName);
        return view;
    }
}
