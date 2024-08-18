package com.camscanner.paperscanner.pdfcreator.view.dialog;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.p018db.DBManager;
import com.camscanner.paperscanner.pdfcreator.model.Document;
import com.camscanner.paperscanner.pdfcreator.view.adapter.MoveFolderAdapter;

public class MoveToFragmentDialog extends AppCompatDialogFragment implements MoveFolderAdapter.AdapterMoveToListener {
    private String exclude = null;
    private ArrayList<Document> folders;
    RecyclerView foldersList;
    private MoveToListener listener;

    public interface MoveToListener {
        void onFolderSelected(Document document);
    }

    public static MoveToFragmentDialog newInstance() {
        return new MoveToFragmentDialog();
    }

    public void showDialog(FragmentActivity activity) {
        activity.getSupportFragmentManager().beginTransaction().add((Fragment) this, "move_to").commitAllowingStateLoss();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dlg_move_to_folder_new, container, false);

        foldersList = (RecyclerView)view.findViewById(R.id.lv_folder_list);
        TextView  btn_cancel = (TextView)view.findViewById(R.id.btn_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        prepareFolders();
        initUI(view);
        return view;
    }

    private void initUI(View view) {
        this.foldersList.setLayoutManager(new LinearLayoutManager(view.getContext()));
        this.foldersList.setAdapter(new MoveFolderAdapter(this.folders, this));
    }

    public void onResume() {
        super.onResume();
        initializeDialog();
    }

    private void initializeDialog() {
        Window window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = -1;
            params.height = -1;
            params.dimAmount = 0.7f;
            window.setAttributes(params);
            window.setBackgroundDrawable(new ColorDrawable(0));
        }
    }

    public MoveToFragmentDialog setListener(MoveToListener listener2) {
        this.listener = listener2;
        return this;
    }

    public MoveToFragmentDialog setExclude(String exclude2) {
        this.exclude = exclude2;
        return this;
    }

    private void prepareFolders() {
        this.folders = new ArrayList<>();
        Document createFolder = new Document("");
        createFolder.name = getContext().getString(R.string.create_new_folder);
        createFolder.uid = Document.CREATE_FOLDER_UID;
        this.folders.add(createFolder);
        if (!TextUtils.isEmpty(this.exclude)) {
            Document root = Document.createRoot();
            root.name = getContext().getString(R.string.folder_root);
            this.folders.add(root);
        }
        DBManager.getInstance().getDocumentsNameA2Z(this.folders, "", -1, true);
        if (!TextUtils.isEmpty(this.exclude)) {
            Iterator<Document> it = this.folders.iterator();
            while (it.hasNext()) {
                Document doc = it.next();
                if (this.exclude.equals(doc.uid)) {
                    this.folders.remove(doc);
                    return;
                }
            }
        }
    }

    public void onClick() {
        dismiss();
    }

    public void onFolderSelected(Document folder) {
        MoveToListener moveToListener = this.listener;
        if (moveToListener != null) {
            moveToListener.onFolderSelected(folder);
        }
        dismiss();
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AppCompatDialog(getContext(), getTheme()) {
            public void onBackPressed() {
                MoveToFragmentDialog.this.dismiss();
            }
        };
    }
}
