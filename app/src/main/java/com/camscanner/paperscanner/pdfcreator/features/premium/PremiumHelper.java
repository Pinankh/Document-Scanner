package com.camscanner.paperscanner.pdfcreator.features.premium;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;

import com.camscanner.paperscanner.pdfcreator.R;

public class PremiumHelper {

    public interface StartActivityController {
        void startActivity(Intent intent, int i);
    }

    public static void showPremiumAfterAlertDialog(final Context context, int titleId, int messageId, final StartActivityController controller) {
        new AlertDialog.Builder(context).setTitle((CharSequence) context.getString(titleId)).setMessage((CharSequence) context.getString(messageId)).setCancelable(false).setPositiveButton((int) R.string.str_ok, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {


            public final void onClick(DialogInterface dialogInterface, int i) {
                PremiumHelper.lambda$showPremiumAfterAlertDialog$0(controller, context, dialogInterface, i);
            }
        }).show();
    }

    static /* synthetic */ void lambda$showPremiumAfterAlertDialog$0(StartActivityController controller, Context context, DialogInterface dialog, int id) {
        dialog.dismiss();
    }
}
