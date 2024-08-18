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

public class PDFSizeAdapter extends BaseAdapter {
    private Context mContext;
    private PDFSizeCallback m_pdfSizeCallback;
    private ArrayList<PDFSize> m_pdfSizeList;

    public interface PDFSizeCallback {
        void onDeletePDFSize(int i);
    }

    public PDFSizeAdapter(Context context, ArrayList<PDFSize> list, PDFSizeCallback pdfSizeCallback) {
        this.mContext = context;
        this.m_pdfSizeList = list;
        this.m_pdfSizeCallback = pdfSizeCallback;
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

    public View getView(final int position, View convertView, ViewGroup parent) {
        View convertView2 = LayoutInflater.from(this.mContext).inflate(R.layout.row_pdf_size_item, parent, false);
        ((TextView) convertView2.findViewById(R.id.tv_pdf_size_name)).setText(this.m_pdfSizeList.get(position).name);
        ((ImageView) convertView2.findViewById(R.id.iv_pdf_size_remove)).setOnClickListener(new View.OnClickListener() {


            public final void onClick(View view) {
                PDFSizeAdapter.this.lambda$getView$0$PDFSizeAdapter(position, view);
            }
        });
        return convertView2;
    }

    public /* synthetic */ void lambda$getView$0$PDFSizeAdapter(int position, View v) {
        PDFSizeCallback pDFSizeCallback = this.m_pdfSizeCallback;
        if (pDFSizeCallback != null) {
            pDFSizeCallback.onDeletePDFSize(position);
        }
    }
}
