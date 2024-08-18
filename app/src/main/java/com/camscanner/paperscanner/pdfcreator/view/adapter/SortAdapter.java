package com.camscanner.paperscanner.pdfcreator.view.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.camscanner.paperscanner.pdfcreator.R;

public class SortAdapter extends BaseAdapter {
    Context context;
    int curSel;
    TypedArray m_sortIcons;
    TypedArray m_sortLabels;

    public SortAdapter(Context context2, int curSel2) {
        this.context = context2;
        this.m_sortIcons = context2.getResources().obtainTypedArray(R.array.sortIcons);
        this.m_sortLabels = context2.getResources().obtainTypedArray(R.array.sortLabels);
        this.curSel = curSel2;
    }

    public int getCount() {
        return this.m_sortLabels.length();
    }

    public Object getItem(int position) {
        return this.context.getString(this.m_sortIcons.getResourceId(position, 0));
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.sort_content, parent, false);
        }
        TextView tvSort = (TextView) convertView.findViewById(R.id.tvSort);
        tvSort.setText(this.context.getString(this.m_sortLabels.getResourceId(position, 0)));
        ((ImageView) convertView.findViewById(R.id.ivSort)).setImageResource(this.m_sortIcons.getResourceId(position, 0));
        if (position == this.curSel) {
            tvSort.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            tvSort.setTypeface(Typeface.DEFAULT);
        }
        return convertView;
    }
}
