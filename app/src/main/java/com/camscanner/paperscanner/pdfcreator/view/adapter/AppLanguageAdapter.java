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
import com.camscanner.paperscanner.pdfcreator.model.AppLanguage;

public class AppLanguageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<AppLanguage> m_langList;

    public AppLanguageAdapter(Context context, ArrayList<AppLanguage> langList) {
        this.mContext = context;
        this.m_langList = langList;
    }

    public int getCount() {
        return this.m_langList.size();
    }

    public AppLanguage getItem(int position) {
        return this.m_langList.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        AppLanguage appLanguage = getItem(position);
        View convertView2 = LayoutInflater.from(this.mContext).inflate(R.layout.item_language, parent, false);
        ((TextView) convertView2.findViewById(R.id.tv_lang_title)).setText(appLanguage.strTitle);
        ImageView ivChecked = (ImageView) convertView2.findViewById(R.id.iv_lang_check);
        if (appLanguage.bSelected) {
            ivChecked.setVisibility(0);
        } else {
            ivChecked.setVisibility(8);
        }
        return convertView2;
    }
}
