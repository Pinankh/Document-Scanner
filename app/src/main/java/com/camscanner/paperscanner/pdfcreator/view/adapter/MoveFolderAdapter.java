package com.camscanner.paperscanner.pdfcreator.view.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.model.Document;

public class MoveFolderAdapter extends RecyclerView.Adapter<MoveFolderAdapter.FolderViewHolder> {
    private List<Document> folders;
    private AdapterMoveToListener listener;

    public interface AdapterMoveToListener {
        void onFolderSelected(Document document);
    }

    static class FolderViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_folder;
        ViewGroup root;
        TextView tv_folderName;

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            this.root = (ViewGroup) itemView.findViewById(R.id.root);
            this.tv_folderName = (TextView) itemView.findViewById(R.id.tv_folder_name);
            this.iv_folder = (ImageView) itemView.findViewById(R.id.iv_folder);
        }
    }

    public MoveFolderAdapter(List<Document> folderList, AdapterMoveToListener listener2) {
        this.folders = new ArrayList(folderList);
        this.listener = listener2;
    }

    @NonNull
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FolderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_move_folder, parent, false));
    }

    public void onBindViewHolder(@NonNull FolderViewHolder viewHolder, int position) {
        final Document folder = this.folders.get(position);
        viewHolder.tv_folderName.setText(folder.name);
        viewHolder.iv_folder.setImageResource(position == 0 ? R.drawable.ic_create_folder_new : R.drawable.ic_folder_new);
        viewHolder.root.setOnClickListener(new View.OnClickListener() {


            public final void onClick(View view) {
                MoveFolderAdapter.this.lambda$onBindViewHolder$0$MoveFolderAdapter(folder, view);
            }
        });
    }

    public /* synthetic */ void lambda$onBindViewHolder$0$MoveFolderAdapter(Document folder, View v) {
        AdapterMoveToListener adapterMoveToListener = this.listener;
        if (adapterMoveToListener != null) {
            adapterMoveToListener.onFolderSelected(folder);
        }
    }

    public int getItemCount() {
        return this.folders.size();
    }
}
