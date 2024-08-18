package com.camscanner.paperscanner.pdfcreator.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.utils.KeyboardUtils;
import com.camscanner.paperscanner.pdfcreator.model.types.SignColor;

public class SignMyTextDialog extends Dialog implements View.OnClickListener {
    RelativeLayout btnBlack;
    RelativeLayout btnBlue;
    RelativeLayout btnRed;
    private SignColor color;
    EditText editText;
    private SignTextCallback mCallback;
    private Activity mContext;

    public interface SignTextCallback {
        void onTextDoneClicked(String str, SignColor signColor);
    }

    public SignMyTextDialog(@NonNull Activity context, View contentView, SignTextCallback callback) {
        super(context);
        requestWindowFeature(1);
        this.mContext = context;
        this.mCallback = callback;
        setContentView(contentView);

        btnBlack = (RelativeLayout)contentView.findViewById(R.id.rl_text_color_black);
        btnBlue = (RelativeLayout)contentView.findViewById(R.id.rl_text_color_blue);
        btnRed = (RelativeLayout)contentView.findViewById(R.id.rl_text_color_red);
        editText = (EditText) contentView.findViewById(R.id.et_sign_text);
        ImageView iv_text_cancel = (ImageView) contentView.findViewById(R.id.iv_text_cancel);
        ImageView iv_text_done = (ImageView) contentView.findViewById(R.id.iv_text_done);

        iv_text_cancel.setOnClickListener(this);
        iv_text_done.setOnClickListener(this);
        btnBlack.setOnClickListener(this);
        btnRed.setOnClickListener(this);

        setColor(SignColor.BLACK);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.height = context.getResources().getDisplayMetrics().heightPixels / 2;
        contentView.setLayoutParams(layoutParams);
        setCanceledOnTouchOutside(false);
        if (getWindow() != null) {
            getWindow().setSoftInputMode(4);
        }
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_text_cancel:
                dismiss();
                return;
            case R.id.iv_text_done:
                SignTextCallback signTextCallback = this.mCallback;
                if (signTextCallback != null) {
                    signTextCallback.onTextDoneClicked(this.editText.getText().toString(), this.color);
                }
                dismiss();
                return;
            case R.id.rl_text_color_black:
                setColor(SignColor.BLACK);
                return;
            case R.id.rl_text_color_blue:
                setColor(SignColor.BLUE);
                return;
            case R.id.rl_text_color_red:
                setColor(SignColor.RED);
                return;
            default:
                return;
        }
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        KeyboardUtils.showKeyboard(this.mContext, this.editText);
    }

    /* access modifiers changed from: package-private */
    public void setColor(SignColor color2) {
        this.color = color2;
        int i = C69041.MySignColor[color2.ordinal()];
        if (i == 1) {
            updateViewSelection(this.btnBlue, R.color.color_signature_blue, this.btnRed, this.btnBlack);
        } else if (i == 2) {
            updateViewSelection(this.btnRed, R.color.color_signature_red, this.btnBlue, this.btnBlack);
        } else if (i == 3) {
            updateViewSelection(this.btnBlack, R.color.color_signature_black, this.btnBlue, this.btnRed);
        }
    }

    static /* synthetic */ class C69041 {
        static final /* synthetic */ int[] MySignColor = new int[SignColor.values().length];

        static {
            try {
                MySignColor[SignColor.BLUE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                MySignColor[SignColor.RED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                MySignColor[SignColor.BLACK.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private void updateViewSelection(View selected, int color2, View... nonselected) {
        selected.setBackgroundColor(ContextCompat.getColor(this.mContext, R.color.colorBGGray));
        this.editText.setTextColor(ContextCompat.getColor(this.mContext, color2));
        for (View view : nonselected) {
            view.setBackgroundColor(ContextCompat.getColor(this.mContext, R.color.colorTransparent));
        }
    }
}
