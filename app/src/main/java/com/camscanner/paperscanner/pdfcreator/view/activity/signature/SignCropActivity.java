package com.camscanner.paperscanner.pdfcreator.view.activity.signature;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.Constant;
import com.camscanner.paperscanner.pdfcreator.common.utils.BitmapUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.ImageStorageUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.SharedPrefsUtils;
import com.camscanner.paperscanner.pdfcreator.common.views.simplecropview.SignatureCropImageView;
import com.camscanner.paperscanner.pdfcreator.view.activity.BaseMainActivity;

public class SignCropActivity extends BaseMainActivity {
    private static final String KEY_EDGE_RECT = "edge_rect";
    private static final String KEY_IMG_PATH = "image_path";
    private static final String KEY_UPDATE_DISK = "update_disk";
    private Bitmap bmpSignature;
    private String imagePath;
    SignatureCropImageView signCropImage;
    private RectF startEdgeRect;
    private boolean updateDiskSignature;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_sign_crop);

        signCropImage = (SignatureCropImageView)findViewById(R.id.sgcv_image);
        TextView tv_sign_crop_cancel = (TextView)findViewById(R.id.tv_sign_crop_cancel);
        TextView tv_sign_crop_done = (TextView)findViewById(R.id.tv_sign_crop_done);
        tv_sign_crop_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        tv_sign_crop_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropSignature();
            }
        });

        if (savedInstanceState != null) {
            restoreVariable(savedInstanceState);
        } else {
            initVariable();
        }
        loadImage();
        updateUI();
    }

    private void restoreVariable(Bundle savedInstanceState) {
        this.imagePath = savedInstanceState.getString(KEY_IMG_PATH);
        this.updateDiskSignature = savedInstanceState.getBoolean(KEY_UPDATE_DISK);
        this.startEdgeRect = (RectF) savedInstanceState.getParcelable(KEY_EDGE_RECT);
    }

    private void loadImage() {
        this.bmpSignature = BitmapUtils.decodeFile(this, this.imagePath);
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_IMG_PATH, this.imagePath);
        outState.putBoolean(KEY_UPDATE_DISK, this.updateDiskSignature);
        outState.putParcelable(KEY_EDGE_RECT, this.signCropImage.getEdgeRect());
    }

    /* access modifiers changed from: package-private */
    public void initVariable() {
        this.imagePath = getIntent().getStringExtra(Constant.EXTRA_IMG_PATH);
        this.updateDiskSignature = TextUtils.isEmpty(this.imagePath);
        this.startEdgeRect = new RectF(0.1f, 0.1f, 0.9f, 0.9f);
        if (this.updateDiskSignature) {
            this.imagePath = SharedPrefsUtils.getCurSignature(this);
        }
    }

    /* access modifiers changed from: package-private */
    public void updateUI() {
        this.signCropImage.setImageBitmap(this.bmpSignature);
        this.signCropImage.setEdgeRect(this.startEdgeRect);
        this.signCropImage.invalidate();
    }



    /* access modifiers changed from: package-private */
    public void cropSignature() {
        RectF rectCrop = this.signCropImage.getEdgeRect();
        int orgWidth = this.bmpSignature.getWidth();
        int orgHeight = this.bmpSignature.getHeight();
        int cropLeft = (int) (rectCrop.left * ((float) orgWidth));
        int cropTop = (int) (rectCrop.top * ((float) orgHeight));
        String signaturePath = ImageStorageUtils.saveSignatureImage(Bitmap.createBitmap(this.bmpSignature, cropLeft, cropTop, ((int) (rectCrop.right * ((float) orgWidth))) - cropLeft, ((int) (rectCrop.bottom * ((float) orgHeight))) - cropTop));
        Intent intent = new Intent();
        if (this.updateDiskSignature) {
            SharedPrefsUtils.setCurSignature(this, signaturePath);
        } else {
            intent.putExtra(Constant.EXTRA_IMG_PATH, signaturePath);
        }
        setResult(-1, intent);
        finish();
    }
}
