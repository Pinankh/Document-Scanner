package com.camscanner.paperscanner.pdfcreator.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.camscanner.paperscanner.pdfcreator.R;

public class SignMyFreeDialog extends Dialog implements View.OnClickListener {
    private SignFreeCallback mCallback;
    private ImageView m_ivBlackOutline;
    private ImageView m_ivBlueOutline;
    private ImageView m_ivCancel;
    private ImageView m_ivDone;
    private ImageView m_ivRedOutline;
    private RelativeLayout m_rlBlack;
    private RelativeLayout m_rlBlue;
    private RelativeLayout m_rlRed;
    private SignaturePad m_signPad;
    private TextView m_tvClear;

    public interface SignFreeCallback {
        void onFreeStyleDoneClicked(Bitmap bitmap);
    }

    public SignMyFreeDialog(@NonNull Context context, View contentView, SignFreeCallback callback) {
        super(context);
        requestWindowFeature(1);
        this.mCallback = callback;
        this.m_ivCancel = (ImageView) contentView.findViewById(R.id.iv_free_cancel);
        this.m_ivDone = (ImageView) contentView.findViewById(R.id.iv_free_done);
        this.m_signPad = (SignaturePad) contentView.findViewById(R.id.sp_pad);
        this.m_tvClear = (TextView) contentView.findViewById(R.id.tv_sign_clear);
        this.m_rlBlack = (RelativeLayout) contentView.findViewById(R.id.rl_sign_color_black);
        this.m_rlBlue = (RelativeLayout) contentView.findViewById(R.id.rl_sign_color_blue);
        this.m_rlRed = (RelativeLayout) contentView.findViewById(R.id.rl_sign_color_red);
        this.m_ivBlackOutline = (ImageView) contentView.findViewById(R.id.iv_sign_outline_black);
        this.m_ivBlueOutline = (ImageView) contentView.findViewById(R.id.iv_sign_outline_blue);
        this.m_ivRedOutline = (ImageView) contentView.findViewById(R.id.iv_sign_outline_red);
        this.m_rlBlack.setOnClickListener(this);
        this.m_rlBlue.setOnClickListener(this);
        this.m_rlRed.setOnClickListener(this);
        this.m_ivCancel.setOnClickListener(this);
        this.m_ivDone.setOnClickListener(this);
        this.m_tvClear.setOnClickListener(this);
        setContentView(contentView);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.height = context.getResources().getDisplayMetrics().heightPixels / 2;
        contentView.setLayoutParams(layoutParams);
        setCanceledOnTouchOutside(false);
        setColor(0);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_free_cancel:
                dismiss();
                return;
            case R.id.iv_free_done:
                saveSign();
                dismiss();
                return;
            case R.id.rl_sign_color_black:
                setColor(0);
                return;
            case R.id.rl_sign_color_blue:
                setColor(1);
                return;
            case R.id.rl_sign_color_red:
                setColor(2);
                return;
            case R.id.tv_sign_clear:
                clearPad();
                return;
            default:
                return;
        }
    }

    /* access modifiers changed from: package-private */
    public void saveSign() {
        Bitmap bmpSignature = this.m_signPad.getTransparentSignatureBitmap(true);
        SignFreeCallback signFreeCallback = this.mCallback;
        if (signFreeCallback != null) {
            signFreeCallback.onFreeStyleDoneClicked(bmpSignature);
        }
    }

    /* access modifiers changed from: package-private */
    public void setColor(int type) {
        if (type == 0) {
            this.m_ivBlackOutline.setVisibility(0);
            this.m_ivBlueOutline.setVisibility(8);
            this.m_ivRedOutline.setVisibility(8);
        } else if (type == 1) {
            this.m_ivBlackOutline.setVisibility(8);
            this.m_ivBlueOutline.setVisibility(0);
            this.m_ivRedOutline.setVisibility(8);
        } else if (type == 2) {
            this.m_ivBlackOutline.setVisibility(8);
            this.m_ivBlueOutline.setVisibility(8);
            this.m_ivRedOutline.setVisibility(0);
        }
        setPadColor(type);
    }

    /* access modifiers changed from: package-private */
    public void clearPad() {
        this.m_signPad.clear();
    }

    /* access modifiers changed from: package-private */
    public void setPadColor(int type) {
        if (type == 0) {
            this.m_signPad.setPenColorRes(R.color.color_signature_black);
        } else if (type == 1) {
            this.m_signPad.setPenColorRes(R.color.color_signature_blue);
        } else if (type == 2) {
            this.m_signPad.setPenColorRes(R.color.color_signature_red);
        }
    }
}
