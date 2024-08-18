package com.camscanner.paperscanner.pdfcreator.view.adapter;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import java.util.ArrayList;
import java.util.List;

import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.p018db.DBManager;
import com.camscanner.paperscanner.pdfcreator.common.p018db.DocumentRepository;
import com.camscanner.paperscanner.pdfcreator.common.utils.ExportDialogUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.StringHelper;
import com.camscanner.paperscanner.pdfcreator.model.Document;
import com.google.android.material.imageview.ShapeableImageView;


public class DocumentAdapter extends BaseAdapter {
    FragmentActivity activity;
    public DocumentAdapterCallback callback = null;
    List<Document> m_arrData;

    public interface DocumentAdapterCallback {
        void onRemoveClick();

        void onRenameClick(int i);
    }

    public DocumentAdapter(FragmentActivity activity2, List<Document> object) {
        this.activity = activity2;
        this.m_arrData = object;
    }

    public int getCount() {
        return this.m_arrData.size();
    }

    public Document getItem(int i) {
        return this.m_arrData.get(i);
    }

    public long getItemId(int i) {
        return 0;
    }

    public View getView(final int position, View view, ViewGroup viewGroup) {

        View view2;
        final Document document = getItem(position);
        if (document.isDir) {
            view2 = LayoutInflater.from(this.activity).inflate(R.layout.row_dir, viewGroup, false);
        } else {
            view2 = LayoutInflater.from(this.activity).inflate(R.layout.row_file, viewGroup, false);
        }
        if (document.isDir) {
            ((ImageView) view2.findViewById(R.id.img_folder_rename)).setOnClickListener(new View.OnClickListener() {
               

                public final void onClick(View view) {
                    DocumentAdapter.this.lambda$getView$0$DocumentAdapter(position, view);
                }
            });
            ((TextView) view2.findViewById(R.id.txt_folder_name)).setText(document.name);
        } else {
            TextView tvFileName = (TextView) view2.findViewById(R.id.txt_file_name);
            TextView tvDocDate = (TextView) view2.findViewById(R.id.txt_doc_date);
            ShapeableImageView ivThumb = (ShapeableImageView) view2.findViewById(R.id.img_document);
            ImageView ivDelete = (ImageView) view2.findViewById(R.id.img_delete);
            ImageView ivShare = (ImageView) view2.findViewById(R.id.img_share);
            final ImageView ivMark = (ImageView) view2.findViewById(R.id.img_bookmark);
            ImageView ivDocRename = (ImageView) view2.findViewById(R.id.img_doc_rename);
            if (document.isMarked) {
                ivMark.setImageResource(R.drawable.ic_favourite_on);
            } else {
                ivMark.setImageResource(R.drawable.ic_favourite_off);
            }
            ivDelete.setOnClickListener(new View.OnClickListener() {


                public final void onClick(View view) {
                    DocumentAdapter.this.lambda$getView$1$DocumentAdapter(position, view);
                }
            });
            ivShare.setOnClickListener(new View.OnClickListener() {


                public final void onClick(View view) {
                    DocumentAdapter.this.lambda$getView$2$DocumentAdapter(position, document, view);
                }
            });
            ivMark.setOnClickListener(new View.OnClickListener() {

                public final void onClick(View view) {
                    DocumentAdapter.this.lambda$getView$3$DocumentAdapter(position, ivMark, view);
                }
            });
            ivDocRename.setOnClickListener(new View.OnClickListener() {


                public final void onClick(View view) {
                    DocumentAdapter.this.lambda$getView$4$DocumentAdapter(position, view);
                }
            });
            ((RequestBuilder) ((RequestBuilder) Glide.with((View) ivThumb).load(document.thumb).centerCrop()).placeholder((int) R.drawable.bg_doc_sample)).into(ivThumb);
            tvFileName.setText(document.name);
            tvDocDate.setText(StringHelper.getDateTimeFromMills(document.date));
        }
        ImageView ivSelected = (ImageView) view2.findViewById(R.id.chk_selected);
        if (document.m_bSelected) {
            ivSelected.setImageResource(R.drawable.icon_item_check_on);
        } else {
            //view2.setBackgroundColor(this.activity.getResources().getColor(R.color.colorTransparent));
            ivSelected.setImageResource(R.drawable.icon_item_check_off);
        }
        if (document.m_bShowButtons) {
            view2.findViewById(R.id.ll_detail_buttons).setVisibility(0);
            ivSelected.setVisibility(8);
        } else {
            view2.findViewById(R.id.ll_detail_buttons).setVisibility(4);
            ivSelected.setVisibility(0);
        }
        return view2;
    }

    public /* synthetic */ void lambda$getView$0$DocumentAdapter(int position, View view1) {
        DocumentAdapterCallback documentAdapterCallback = this.callback;
        if (documentAdapterCallback != null) {
            documentAdapterCallback.onRenameClick(position);
        }
    }

    public /* synthetic */ void lambda$getView$1$DocumentAdapter(int position, View v) {
        if (this.m_arrData.get(position).isLocked) {
            Toast.makeText(this.activity, "Locked file!", 0).show();
        } else {
            showConfirmDelete(position);
        }
    }

    public /* synthetic */ void lambda$getView$2$DocumentAdapter(int position, Document document, View v) {
        if (this.m_arrData.get(position).isLocked) {
            Toast.makeText(this.activity, "Locked file!", 0).show();
            return;
        }
        List<Document> documents = new ArrayList<>();
        DBManager.getInstance().getDocumentsBySortID(documents, document.uid);
        ExportDialogUtils.showShareDialog(this.activity, documents, document.name);
    }

    public /* synthetic */ void lambda$getView$3$DocumentAdapter(int position, ImageView ivMark, View v) {
        Document doc = this.m_arrData.get(position);
        doc.isMarked = !doc.isMarked;
        if (doc.isMarked) {
            ivMark.setImageResource(R.drawable.ic_favourite_on);
        } else {
            ivMark.setImageResource(R.drawable.ic_favourite_off);
        }
        DBManager.getInstance().updateDocument(doc);
        notifyDataSetChanged();
    }

    public /* synthetic */ void lambda$getView$4$DocumentAdapter(int position, View v) {
        DocumentAdapterCallback documentAdapterCallback = this.callback;
        if (documentAdapterCallback != null) {
            documentAdapterCallback.onRenameClick(position);
        }
    }

    /* access modifiers changed from: package-private */
    public void showConfirmDelete(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        builder.setTitle((CharSequence) this.activity.getString(R.string.str_delete));
        builder.setMessage((int) R.string.msg_delete);
        builder.setPositiveButton((int) R.string.str_yes, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {


            public final void onClick(DialogInterface dialogInterface, int i) {
                DocumentAdapter.this.lambda$showConfirmDelete$5$DocumentAdapter(position, dialogInterface, i);
            }
        });
        builder.setNegativeButton((int) R.string.str_cancel, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                DocumentAdapter.this.lambda$showConfirmDelete$6$DocumentAdapter(dialogInterface, i);
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    public /* synthetic */ void lambda$showConfirmDelete$5$DocumentAdapter(int position, DialogInterface dialog, int which) {
        if (position >= this.m_arrData.size()) {
            FragmentActivity fragmentActivity = this.activity;
            Toast.makeText(fragmentActivity, fragmentActivity.getString(R.string.str_delete), 1).show();
            return;
        }
//        System.out.println("size====="+m_arrData.size());
//
//        if(position == 0){
//            DocumentRepository.removeDocumentWithChilds(activity, m_arrData.get(0));
//
//        }
//        else{
//            DocumentRepository.removeDocumentWithChilds(activity, m_arrData.get(position));
//
//        }
        DocumentRepository.removeDocumentWithChilds(activity, m_arrData.get(position));

        this.m_arrData.remove(position);




        notifyDataSetChanged();
        DocumentAdapterCallback documentAdapterCallback = this.callback;
        if (documentAdapterCallback != null) {
            documentAdapterCallback.onRemoveClick();
        }
        if (!this.activity.isFinishing() && dialog != null) {
            dialog.dismiss();
        }
    }

    public /* synthetic */ void lambda$showConfirmDelete$6$DocumentAdapter(DialogInterface dialog, int which) {
        if (!this.activity.isFinishing() && dialog != null) {
            dialog.dismiss();
        }
    }
}
