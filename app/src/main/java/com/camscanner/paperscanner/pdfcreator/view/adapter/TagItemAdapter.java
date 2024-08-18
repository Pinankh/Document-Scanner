package com.camscanner.paperscanner.pdfcreator.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import co.lujun.androidtagview.TagItem;
import com.camscanner.paperscanner.pdfcreator.R;

public class TagItemAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<TagItem> tagItemList;

    public TagItemAdapter(Context context, ArrayList<TagItem> tagItemList2) {
        this.tagItemList = tagItemList2;
        this.mContext = context;
    }

    public int getCount() {
        return this.tagItemList.size();
    }

    public TagItem getItem(int position) {
        return this.tagItemList.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        TagItem tagItem = getItem(position);
        View convertView2 = LayoutInflater.from(this.mContext).inflate(R.layout.item_tag, parent, false);
        TextView tvTagCount = (TextView) convertView2.findViewById(R.id.tv_tag_count);
        ((TextView) convertView2.findViewById(R.id.tv_tag_mark)).setText(getTagMark(tagItem.tagName));
        ((TextView) convertView2.findViewById(R.id.tv_tag_name)).setText(tagItem.tagName);
        tvTagCount.setText(String.format("%d", new Object[]{Integer.valueOf(tagItem.nDocCount)}));
        if (tagItem.f52ID > 0) {
            tvTagCount.setVisibility(0);
        } else {
            tvTagCount.setVisibility(4);
        }
        return convertView2;
    }

    /* access modifiers changed from: package-private */
    public String getTagMark(String strTagName) {
        if (strTagName == null || strTagName.length() <= 0) {
            return "";
        }
        return String.format("%c", new Object[]{Character.valueOf(strTagName.charAt(0))});
    }
}
