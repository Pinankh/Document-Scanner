package com.camscanner.paperscanner.pdfcreator.common.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.camscanner.paperscanner.pdfcreator.R;
import timber.log.Timber;

public class FileNameDialogUtils {

    public interface OnRenameListener {
        void nameChanged(String str);
    }

    public static void showFileNameDialog(final Context context, String startText, String hintText, String title, final OnRenameListener listener) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dlg_create_folder, (ViewGroup) null);
        final AutoCompleteTextView editTextView = (AutoCompleteTextView) dialogView.findViewById(R.id.txt_create_folder);
        final TextView errorView = (TextView) dialogView.findViewById(R.id.txt_error);
        editTextView.setHint(hintText);
        final AlertDialog dialog = new AlertDialog.Builder(context).setTitle((CharSequence) title).setView(dialogView).setCancelable(false).setPositiveButton((int) R.string.str_ok, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {


            public final void onClick(DialogInterface dialogInterface, int i) {
                FileNameDialogUtils.lambda$showFileNameDialog$0(context, editTextView,listener, dialogInterface, i);
            }
        }).setNegativeButton((CharSequence) context.getString(R.string.str_cancel), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {


            public final void onClick(DialogInterface dialogInterface, int i) {
                FileNameDialogUtils.lambda$showFileNameDialog$1(context, editTextView, dialogInterface, i);
            }
        }).create();
        String startClearedText = StringHelper.clearFilename(startText, false).trim();
        editTextView.setText(startClearedText);
        editTextView.setSelection(startClearedText.length());
        editTextView.addTextChangedListener(new TextWatcher() {
            boolean afterFix = false;
            int fixSelection = 0;

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Timber.tag("RenameDialog").i("beforeTextChanged %s %s %s %s", s, Integer.valueOf(start), Integer.valueOf(count), Integer.valueOf(after));
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Timber.tag("RenameDialog").i("onTextChanged %s %s %s %s", s, Integer.valueOf(start), Integer.valueOf(before), Integer.valueOf(count));
            }

            public void afterTextChanged(Editable s) {
                Timber.tag("RenameDialog").i("afterTextChanged %s %s %s", s, Boolean.valueOf(this.afterFix), Integer.valueOf(this.fixSelection));
                if (this.afterFix) {
                    this.afterFix = false;
                    editTextView.setSelection(this.fixSelection);
                    return;
                }
                String approvedText = StringHelper.clearFilename(s.toString(), false);
                int approvedLength = approvedText.length();
                int diff = s.length() - approvedLength;
                Button posButton = dialog.getButton(-1);
                if (posButton != null) {
                    posButton.setEnabled(approvedText.trim().length() != 0);
                }
                if (diff != 0) {
                    errorView.setVisibility(0);
                    this.fixSelection = FileNameDialogUtils.getSelection(editTextView.getSelectionStart(), diff, approvedLength);
                    this.afterFix = true;
                    s.replace(0, s.length(), approvedText);
                    return;
                }
                errorView.setVisibility(8);
            }
        });
        dialog.show();
        DeviceUtil.showKeyboard(context, editTextView);
    }

    static /* synthetic */ void lambda$showFileNameDialog$0(Context context, AutoCompleteTextView editTextView, OnRenameListener listener, DialogInterface dialog, int id) {
        DeviceUtil.closeKeyboard(context, (EditText) editTextView);
        listener.nameChanged(editTextView.getText().toString().trim());
        dialog.dismiss();
    }

    static /* synthetic */ void lambda$showFileNameDialog$1(Context context, AutoCompleteTextView editTextView, DialogInterface dialog, int id) {
        DeviceUtil.closeKeyboard(context, (EditText) editTextView);
        dialog.dismiss();
    }

    /* access modifiers changed from: private */
    public static int getSelection(int oldSelection, int diff, int newLength) {
        int fixSelection = oldSelection - diff;
        if (fixSelection < 0) {
            fixSelection = 0;
        }
        if (fixSelection > newLength) {
            return newLength;
        }
        return fixSelection;
    }
}
