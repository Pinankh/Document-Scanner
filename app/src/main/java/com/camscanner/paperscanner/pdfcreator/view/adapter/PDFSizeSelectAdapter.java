package com.camscanner.paperscanner.pdfcreator.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.model.PDFSize;

public class PDFSizeSelectAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<PDFSize> m_pdfSizeList;

    public PDFSizeSelectAdapter(Context context, ArrayList<PDFSize> list) {
        this.mContext = context;
        this.m_pdfSizeList = list;
    }

    public int getCount() {
        return this.m_pdfSizeList.size();
    }

    public Object getItem(int position) {
        return this.m_pdfSizeList.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View convertView2 = LayoutInflater.from(this.mContext).inflate(R.layout.row_pdf_size_item_select, parent, false);
        ((TextView) convertView2.findViewById(R.id.tv_pdf_size_name)).setText(this.m_pdfSizeList.get(position).name);
        ImageView ivChecked = (ImageView) convertView2.findViewById(R.id.iv_pdf_size_check);
        if (this.m_pdfSizeList.get(position).bSelected) {
            ivChecked.setVisibility(0);
        } else {
            ivChecked.setVisibility(8);
        }
        return convertView2;
    }
}
