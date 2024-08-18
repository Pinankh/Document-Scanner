package com.camscanner.paperscanner.pdfcreator.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import co.lujun.androidtagview.TagItem;
import com.camscanner.paperscanner.pdfcreator.R;

public class TagEditItemAdapter extends BaseAdapter {
    private TagEditItemCallback mCallback;
    private Context mContext;
    private ArrayList<TagItem> tagItemList;

    public interface TagEditItemCallback {
        void onDeleteTagItem(int i);

        void onEditTagItem(int i);
    }

    public TagEditItemAdapter(Context context, ArrayList<TagItem> tagItemList2, TagEditItemCallback callback) {
        this.tagItemList = tagItemList2;
        this.mContext = context;
        this.mCallback = callback;
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

    public View getView(final int position, View convertView, ViewGroup parent) {
        TagItem tagItem = getItem(position);
        View convertView2 = LayoutInflater.from(this.mContext).inflate(R.layout.item_edit_tag, parent, false);
        ImageView ivTagEdit = (ImageView) convertView2.findViewById(R.id.iv_tag_edit);
        ImageView ivTagDelete = (ImageView) convertView2.findViewById(R.id.iv_tag_delete);
        ((TextView) convertView2.findViewById(R.id.tv_tag_mark)).setText(getTagMark(tagItem.tagName));
        ((TextView) convertView2.findViewById(R.id.tv_tag_name)).setText(tagItem.tagName);
        if (tagItem.bEditable) {
            ivTagDelete.setVisibility(0);
            ivTagEdit.setVisibility(0);
        } else {
            ivTagDelete.setVisibility(8);
            ivTagEdit.setVisibility(8);
        }
        ivTagDelete.setOnClickListener(new View.OnClickListener() {


            public final void onClick(View view) {
                TagEditItemAdapter.this.lambda$getView$0$TagEditItemAdapter(position, view);
            }
        });
        ivTagEdit.setOnClickListener(new View.OnClickListener() {


            public final void onClick(View view) {
                TagEditItemAdapter.this.lambda$getView$1$TagEditItemAdapter(position, view);
            }
        });
        return convertView2;
    }

    public /* synthetic */ void lambda$getView$0$TagEditItemAdapter(int position, View v) {
        TagEditItemCallback tagEditItemCallback = this.mCallback;
        if (tagEditItemCallback != null) {
            tagEditItemCallback.onDeleteTagItem(position);
        }
    }

    public /* synthetic */ void lambda$getView$1$TagEditItemAdapter(int position, View v) {
        TagEditItemCallback tagEditItemCallback = this.mCallback;
        if (tagEditItemCallback != null) {
            tagEditItemCallback.onEditTagItem(position);
        }
    }

    /* access modifiers changed from: package-private */
    public String getTagMark(String strTagName) {
        if (strTagName == null || strTagName.length() <= 0) {
            return "";
        }
        return String.format("%c", new Object[]{Character.valueOf(strTagName.charAt(0))});
    }
}
