package com.camscanner.paperscanner.pdfcreator.view.dialog;

import android.content.Context;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.Constant;
import com.camscanner.paperscanner.pdfcreator.view.adapter.SortAdapter;

public class SortDialog {
    SortDialogCallback callback;
    Context context;
    ListView listView;
    AlertDialog m_alertDialog;
    Constant.SORT_TYPE sort_type;

    public interface SortDialogCallback {
        void onCreatedDateDown();

        void onCreatedDateUp();

        void onNameA2Z();

        void onNameZ2A();
    }

    public SortDialog(Context context2, View contentView, final SortDialogCallback callback2, Constant.SORT_TYPE sort_type2) {
        this.callback = callback2;
        this.context = context2;
        this.sort_type = sort_type2;
        int curSel = 0;
        if (sort_type2 == Constant.SORT_TYPE.CREATE_UP) {
            curSel = 0;
        } else if (sort_type2 == Constant.SORT_TYPE.CREATE_DOWN) {
            curSel = 1;
        } else if (sort_type2 == Constant.SORT_TYPE.NAMEA2Z) {
            curSel = 2;
        } else if (sort_type2 == Constant.SORT_TYPE.NAMEZ2A) {
            curSel = 3;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context2);
        builder.setView(contentView);
        builder.setTitle((CharSequence) context2.getString(R.string.str_sort_by));
        this.m_alertDialog = builder.create();
        this.listView = (ListView) contentView.findViewById(R.id.lvSort);
        this.listView.setAdapter(new SortAdapter(context2, curSel));
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 0) {
                    callback2.onCreatedDateUp();
                } else if (position == 1) {
                    callback2.onCreatedDateDown();
                } else if (position == 2) {
                    callback2.onNameA2Z();
                } else if (position == 3) {
                    callback2.onNameZ2A();
                }
                SortDialog.this.dismiss();
            }
        });
    }

    public void show() {
        AlertDialog alertDialog = this.m_alertDialog;
        if (alertDialog != null) {
            alertDialog.show();
        }
    }

    public void dismiss() {
        AlertDialog alertDialog = this.m_alertDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }
}
