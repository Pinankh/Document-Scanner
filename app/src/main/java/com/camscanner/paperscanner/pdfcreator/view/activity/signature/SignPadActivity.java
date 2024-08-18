package com.camscanner.paperscanner.pdfcreator.view.activity.signature;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.cameraGalleryPicker.GalleryImage;
import com.camscanner.paperscanner.pdfcreator.common.cameraGalleryPicker.ImagesPickerManager;
import com.camscanner.paperscanner.pdfcreator.common.utils.ImageStorageUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.PermissionsUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.SharedPrefsUtils;
import com.camscanner.paperscanner.pdfcreator.view.activity.BaseMainActivity;

public class SignPadActivity extends BaseMainActivity implements View.OnClickListener {
    public static SignPadActivity mInstanceActivity = null;
    private Bundle mInstanceBundle = null;
    private boolean m_bImageAvailable;
    private Bitmap m_bmpSignature;
    private ImageView m_ivBlackOutline;
    private ImageView m_ivBlueOutline;
    private ImageView m_ivRedOutline;
    private ImageView m_ivSignature;
    private RelativeLayout m_rlBlack;
    private RelativeLayout m_rlBlue;
    private RelativeLayout m_rlRed;
    private SignaturePad m_signPad;
    private TextView m_tvAdopt;
    private TextView m_tvCancel;
    private TextView m_tvClear;
    private TextView m_tvPickFromGallery;

    public SignPadActivity pushData(Bundle instanceState) {
        Bundle bundle = this.mInstanceBundle;
        if (bundle == null) {
            this.mInstanceBundle = instanceState;
        } else {
            bundle.putAll(instanceState);
        }
        return this;
    }

    public Bundle popData() {
        Bundle out = this.mInstanceBundle;
        this.mInstanceBundle = null;
        return out;
    }

    public static final SignPadActivity getInstance() {
        if (mInstanceActivity == null) {
            mInstanceActivity = new SignPadActivity();
        }
        return mInstanceActivity;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(getInstance().popData());
        setContentView((int) R.layout.activity_signature);
        initVariable();
        initUI();
        updateUI();
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getInstance().pushData((Bundle) outState.clone());
        outState.clear();
    }

    /* access modifiers changed from: package-private */
    public void initVariable() {
        try {
            this.m_bmpSignature = BitmapFactory.decodeFile(SharedPrefsUtils.getCurSignature(this));
        } catch (Exception ex) {
            ex.printStackTrace();
            this.m_bmpSignature = null;
        }
    }

    /* access modifiers changed from: package-private */
    public void initUI() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        this.m_signPad = (SignaturePad) findViewById(R.id.sp_pad);
        this.m_ivSignature = (ImageView) findViewById(R.id.iv_signature);
        this.m_tvCancel = (TextView) findViewById(R.id.tv_sign_cancel);
        this.m_tvAdopt = (TextView) findViewById(R.id.tv_sign_adopt);
        this.m_tvClear = (TextView) findViewById(R.id.tv_sign_clear);
        this.m_rlBlack = (RelativeLayout) findViewById(R.id.rl_sign_color_black);
        this.m_rlBlue = (RelativeLayout) findViewById(R.id.rl_sign_color_blue);
        this.m_rlRed = (RelativeLayout) findViewById(R.id.rl_sign_color_red);
        this.m_ivBlackOutline = (ImageView) findViewById(R.id.iv_sign_outline_black);
        this.m_ivBlueOutline = (ImageView) findViewById(R.id.iv_sign_outline_blue);
        this.m_ivRedOutline = (ImageView) findViewById(R.id.iv_sign_outline_red);
        this.m_tvPickFromGallery = (TextView) findViewById(R.id.tv_sign_pick);
        this.m_tvCancel.setOnClickListener(this);
        this.m_tvAdopt.setOnClickListener(this);
        this.m_tvClear.setOnClickListener(this);
        this.m_rlBlack.setOnClickListener(this);
        this.m_rlBlue.setOnClickListener(this);
        this.m_rlRed.setOnClickListener(this);
        this.m_tvPickFromGallery.setOnClickListener(this);
    }

    /* access modifiers changed from: package-private */
    public void updateUI() {
        this.m_bImageAvailable = false;
        Bitmap bitmap = this.m_bmpSignature;
        if (bitmap != null) {
            this.m_ivSignature.setImageBitmap(bitmap);
            this.m_signPad.setVisibility(8);
            this.m_bImageAvailable = true;
        }
        setColor(0);
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
        if (this.m_bImageAvailable) {
            this.m_ivSignature.setImageBitmap((Bitmap) null);
            this.m_bmpSignature = null;
            this.m_signPad.setVisibility(0);
            this.m_bImageAvailable = false;
            return;
        }
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

    /* access modifiers changed from: package-private */
    public void onCancel() {
        setResult(0);
        finish();
    }

    /* access modifiers changed from: package-private */
    public void onAdopt() {
        if (!this.m_bImageAvailable) {
            if (this.m_signPad.getPoints().size() == 0) {
                SharedPrefsUtils.setCurSignature(this, "");
            } else {
                SharedPrefsUtils.setCurSignature(this, ImageStorageUtils.saveSignatureImage(this.m_signPad.getTransparentSignatureBitmap(true)));
            }
        }
        setResult(-1);
        finish();
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id != R.id.tv_sign_pick) {
            switch (id) {
                case R.id.rl_sign_color_black:
                    setColor(0);
                    return;
                case R.id.rl_sign_color_blue:
                    setColor(1);
                    return;
                case R.id.rl_sign_color_red:
                    setColor(2);
                    return;
                default:
                    switch (id) {
                        case R.id.tv_sign_adopt:
                            PermissionsUtils.askPermissions(this, new PermissionsUtils.PermissionListener() {
                                public final void onGranted() {
                                    SignPadActivity.this.onAdopt();
                                }
                            }, PermissionsUtils.STORAGE_PERMISSIONS);
                            return;
                        case R.id.tv_sign_cancel:
                            onCancel();
                            return;
                        case R.id.tv_sign_clear:
                            clearPad();
                            return;
                        default:
                            return;
                    }
            }
        } else {
            PermissionsUtils.askPermissions(this, new PermissionsUtils.PermissionListener() {
                public final void onGranted() {
                    SignPadActivity.this.lambda$onClick$0$SignPadActivity();
                }
            }, PermissionsUtils.STORAGE_PERMISSIONS);
        }
    }

    public /* synthetic */ void lambda$onClick$0$SignPadActivity() {
        ImagesPickerManager.startPickerSolo(this);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GalleryImage galleryImage = ImagesPickerManager.handlePickerResult(requestCode, resultCode, data);
        if (galleryImage != null) {
            String realPath = galleryImage.getImagePath();
            if (realPath == null || realPath.isEmpty()) {
                setResult(0);
                finish();
                return;
            }
            SharedPrefsUtils.setCurSignature(this, realPath);
            goToSignCropActivity();
        }
        if (requestCode == 1016) {
            if (resultCode == -1) {
                setResult(-1);
            }
            finish();
        }
    }

    /* access modifiers changed from: package-private */
    public void goToSignCropActivity() {
        startActivityForResult(new Intent(this, SignCropActivity.class), 1016);
    }
}
