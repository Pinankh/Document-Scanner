package com.camscanner.paperscanner.pdfcreator.features.ocr.presentation;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.features.ocr.model.OCRLanguage;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder> {
    private List<OCRLanguage> items;
    private final OnLanguageListener listener;
    private String query;
    private OCRLanguage selected;

    public interface OnLanguageListener {
        void onLanguageClicked(OCRLanguage oCRLanguage);
    }



    static class LanguageViewHolder extends RecyclerView.ViewHolder {
        View root;
//        @BindDrawable(R.drawable.background_button_ocr_selected)
        Drawable selected;
        TextView title;
//        @BindDrawable(R.drawable.background_button_ocr)
        Drawable unselected;

        public LanguageViewHolder(View itemView) {
            super(itemView);

            root = (View)itemView.findViewById(R.id.root);
            title = (TextView)itemView.findViewById(R.id.text_language);


        }
    }

    public LanguageAdapter(OnLanguageListener listener2, List<OCRLanguage> items2) {
        this.listener = listener2;
        this.items = new ArrayList(items2);
    }

    public LanguageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LanguageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ocr_language, parent, false));
    }

    public void onBindViewHolder(@NonNull LanguageViewHolder holder, int position) {
        final OCRLanguage language = this.items.get(position);
        setTitle(holder, language);
        holder.root.setBackground(language.equals(this.selected) ? holder.selected : holder.unselected);
        holder.root.setOnClickListener(new View.OnClickListener() {

            public final void onClick(View view) {
                LanguageAdapter.this.lambda$onBindViewHolder$0$LanguageAdapter(language, view);
            }
        });
    }

    public /* synthetic */ void lambda$onBindViewHolder$0$LanguageAdapter(OCRLanguage language, View v) {
        this.listener.onLanguageClicked(language);
    }

    private void setTitle(@NonNull LanguageViewHolder holder, OCRLanguage language) {
        if (TextUtils.isEmpty(this.query)) {
            holder.title.setText(language.language);
            return;
        }
        SpannableStringBuilder highlightedText = new SpannableStringBuilder(language.language);
        highlightedText.setSpan(new StyleSpan(1), 0, this.query.length(), 33);
        holder.title.setText(highlightedText);
    }

    public void update(List<OCRLanguage> newList) {
        this.items = newList;
        notifyDataSetChanged();
    }

    public void setQuery(String query2) {
        this.query = query2;
    }

    public void setSelected(OCRLanguage language) {
        this.selected = language;
    }

    public OCRLanguage getSelected() {
        return this.selected;
    }

    public List<OCRLanguage> getSortedList() {
        return this.items;
    }

    public int getItemCount() {
        return this.items.size();
    }
}
