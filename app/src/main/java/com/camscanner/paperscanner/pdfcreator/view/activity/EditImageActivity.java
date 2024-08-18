package com.camscanner.paperscanner.pdfcreator.view.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ortiz.touchview.TouchImageView;
import com.tapscanner.polygondetect.DetectionResult;
import java.util.Collections;
import java.util.List;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.ScanApplication;
import com.camscanner.paperscanner.pdfcreator.common.Constant;
import com.camscanner.paperscanner.pdfcreator.common.p018db.DocumentRepository;
import com.camscanner.paperscanner.pdfcreator.common.utils.ActivityUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.BitmapUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.ExportDialogUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.ImageStorageUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.PermissionsUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.PolygonUtil;
import com.camscanner.paperscanner.pdfcreator.common.utils.SharedPrefsUtils;
import com.camscanner.paperscanner.pdfcreator.features.ocr.OcrHelper;
import com.camscanner.paperscanner.pdfcreator.features.tutorial.TutorialManager;
import com.camscanner.paperscanner.pdfcreator.features.tutorial.model.TutorialInfo;
import com.camscanner.paperscanner.pdfcreator.model.Document;
import com.camscanner.paperscanner.pdfcreator.view.activity.main.MainActivity;
import com.camscanner.paperscanner.pdfcreator.view.activity.signature.SignAddActivity;
import com.camscanner.paperscanner.pdfcreator.view.activity.signature.SignEditActivity;
import com.camscanner.paperscanner.pdfcreator.view.camera.CameraOpenActivity;
import com.camscanner.paperscanner.pdfcreator.view.crop.CropImageActivity;

public class EditImageActivity extends BaseMainActivity implements TutorialManager.OnTutorialListener {
    View btnCrop;
    View btnSign;
    private int editOpened = 0;
    private int existCnt;
    private Bitmap m_bmpSource;
     Document m_curDoc;
    TouchImageView pictureImageView;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_edit);

        LinearLayout relative_ad = (LinearLayout) findViewById(R.id.relative_ad);
        ScanApplication app = (ScanApplication) getApplication();
        app.adaptiveBannerView(relative_ad);

        btnCrop = (View)findViewById(R.id.btn_crop);
        btnSign = (View)findViewById(R.id.btn_sign);
        pictureImageView = (TouchImageView)findViewById(R.id.iv_croped);
        ImageView  btn_back = (ImageView)findViewById(R.id.btn_back);
        ConstraintLayout  btn_edit = (ConstraintLayout)findViewById(R.id.btn_edit);
        ImageView  btn_share = (ImageView)findViewById(R.id.btn_share);
        ConstraintLayout  btn_delete = (ConstraintLayout)findViewById(R.id.btn_delete);
        ConstraintLayout  btn_ocr = (ConstraintLayout)findViewById(R.id.btn_ocr);
        ConstraintLayout  btn_retake = (ConstraintLayout)findViewById(R.id.btn_retake);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToEditFiltersActivity();
            }
        });

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareImage();

            }
        });
        btnCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCropActivity();

            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog();
            }
        });
        btn_ocr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recognizeDocument();
            }
        });
        btn_retake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToRetake();
            }
        });

        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSignEditActivity();

            }
        });

        if (savedInstanceState != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(268468224);
            startActivity(intent);
            finish();
            return;
        }
        initVariable();
        initUI();
        showOverlay();
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
//        Analytics.get().logEditScreen();
    }

    private void showOverlay() {
        if (this.editOpened == 1) {
            ActivityUtils.waitVisibleFor(new ActivityUtils.OnVisibleChecker() {
                public final boolean isVisible() {
                    return EditImageActivity.this.isFooterReady();
                }
            }, new ActivityUtils.OnVisibleListener() {
                public final void onVisible() {
                    EditImageActivity.this.showFooterTutorial();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public boolean isFooterReady() {
        return this.btnSign.getWidth() != 0;
    }

    /* access modifiers changed from: private */
    public void showFooterTutorial() {
        TutorialManager.showTutorial(getSupportFragmentManager(), new TutorialInfo(R.layout.tutorial_edit_sign, R.id.btn_sign), new TutorialInfo(R.layout.tutorial_edit_crop, R.id.btn_crop));
    }

    public void onTutorialViewClicked(View v) {
        if (v.getId() == R.id.btn_sign) {
            onFooterClicked(this.btnSign);
        } else if (v.getId() == R.id.btn_crop) {
            onFooterClicked(this.btnCrop);
        }
    }

    public void onTutorialClosed(TutorialInfo tutorialInfo, boolean targetHit) {
        if (tutorialInfo.layoutId == R.layout.tutorial_edit_crop) {
            SharedPrefsUtils.setEditOpened(this, 1);
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
    }

    /* access modifiers changed from: package-private */
    public void initVariable() {
        this.m_curDoc = (Document) getIntent().getSerializableExtra(Constant.EXTRA_DOCUMENT);
        this.existCnt = getIntent().getIntExtra(Constant.EXTRA_EXIST_COUNT, 0);
        this.editOpened = SharedPrefsUtils.getEditOpened(this);
        try {
            this.m_bmpSource = BitmapFactory.decodeFile(this.m_curDoc.path);
        } catch (OutOfMemoryError ex) {
            Toast.makeText(this, getString(R.string.alert_sorry), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /* access modifiers changed from: package-private */
    public void initUI() {
        this.pictureImageView.setImageBitmap(this.m_bmpSource);


    }

    /* access modifiers changed from: package-private */
    public void goToEditFiltersActivity() {
        if (setCropBitmapData()) {
            Intent intent = new Intent(this, EditFiltersActivity.class);
            intent.putExtra(Constant.EXTRA_DOCUMENT, this.m_curDoc);
            intent.putExtra(Constant.NEED_AUTO_FILTER, false);
            startActivityForResult(intent, 1001);
        }
    }

    /* access modifiers changed from: package-private */
    public void shareImage() {
        ExportDialogUtils.showShareDialog((FragmentActivity) this, (List<Document>) Collections.singletonList(this.m_curDoc), this.m_curDoc.name);
    }

    /* access modifiers changed from: package-private */
    public void goToCropActivity() {
        if (setBitmapData(this.m_curDoc.imgPath)) {
            CropImageActivity.recropOldFile(this, DetectionResult.FIX_RECT_MODE.FIX_RECT_GALLERY, this.m_curDoc);
        } else {
            Toast.makeText(this, getString(R.string.alert_fail_open), 1).show();
        }
    }

    private void recognizeDocument() {
        OcrHelper.recognizeDocument(this, this.m_curDoc);
    }

    /* access modifiers changed from: package-private */
    public boolean setCropBitmapData() {
        try {
            BitmapFactory.Options op = new BitmapFactory.Options();
            op.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bmpCrop = BitmapFactory.decodeFile(this.m_curDoc.path, op);
            if (bmpCrop == null) {
                goToCropActivity();
                return false;
            }
            ScanApplication.imageHolder().setCroppedPicture(bmpCrop, true, true);
            return true;
        } catch (OutOfMemoryError ex) {
            finish();
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean setBitmapData(String path) {
        try {
            Bitmap bmp = BitmapUtils.decodeUriWithoutOptimizations((Context) this, path);
            if (bmp == null) {
                Toast.makeText(this, getString(R.string.alert_fail_decode), 1).show();
                return false;
            }
            ScanApplication.imageHolder().setOriginalPicture(bmp, (String) null);
            return true;
        } catch (OutOfMemoryError ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public void onFabClicked(View view) {
        int id = view.getId();
        if (id == R.id.btn_back) {
            onBackPressed();
        } else if (id == R.id.btn_edit) {
            goToEditFiltersActivity();
        } else if (id == R.id.btn_share) {
            shareImage();
        }
    }

    public void onFooterClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_crop:
                goToCropActivity();
                return;
            case R.id.btn_delete:
                showDeleteDialog();
                return;
            case R.id.btn_ocr:
                recognizeDocument();
                return;
            case R.id.btn_retake:
                goToRetake();
                return;
            case R.id.btn_sign:
                goToSignEditActivity();
                return;
            default:
                return;
        }
    }

    private void goToRetake() {
        PermissionsUtils.askPermissions(this, new PermissionsUtils.PermissionListener() {
            public final void onGranted() {
                EditImageActivity.this.startCamera();
            }
        }, PermissionsUtils.TAKE_SAVE_PICTURE_PERMISSIONS);
    }

    public void startCamera() {
        startActivityForResult(new Intent(this, CameraOpenActivity.class), 1003);
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle((int) R.string.str_delete);
        builder.setMessage((int) R.string.msg_delete);
        builder.setPositiveButton((int) R.string.str_yes, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                EditImageActivity.this.lambda$showDeleteDialog$0$EditActivity(dialogInterface, i);
            }
        });
        builder.setNegativeButton((int) R.string.str_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    public /* synthetic */ void lambda$showDeleteDialog$0$EditActivity(DialogInterface dialog, int which) {
        DocumentRepository.removeDocument(this, this.m_curDoc);
        dialog.dismiss();
        commitResult();
        finish();
    }

    private void commitResult() {
        Intent resultIntent = new Intent();
        setIntentExtra(resultIntent);
        setResult(-1, resultIntent);
    }

    private void setIntentExtra(Intent resultIntent) {
        resultIntent.putExtra(Constant.EXTRA_DOCUMENT, this.m_curDoc);
        resultIntent.putExtra("position", getIntent().getIntExtra("position", -1));
    }

    /* access modifiers changed from: package-private */
    public void goToSignAddActivity() {
        Intent intent = new Intent(this, SignAddActivity.class);
        intent.putExtra(Constant.EXTRA_DOCUMENT, this.m_curDoc);
        startActivityForResult(intent, 1015);
    }

    /* access modifiers changed from: package-private */
    public void goToSignEditActivity() {
        Intent intent = new Intent(this, SignEditActivity.class);
        intent.putExtra(Constant.EXTRA_DOCUMENT, this.m_curDoc);
        startActivityForResult(intent, 1017);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1010) {
            ImageStorageUtils.clearShareFolder();
        } else if (requestCode != 1014) {
            if (requestCode != 1017) {
                if (requestCode == 1021) {
                    this.m_curDoc = (data == null || !data.hasExtra(Constant.EXTRA_DOCUMENT)) ? this.m_curDoc : (Document) data.getSerializableExtra(Constant.EXTRA_DOCUMENT);
                } else if (requestCode != 1022) {
                    switch (requestCode) {
                        case 1001:
                            if (resultCode == -1) {
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra(Constant.EXTRA_MPARENT, data.getStringExtra(Constant.EXTRA_MPARENT));
                                resultIntent.putExtra(Constant.EXTRA_MNAME, data.getStringExtra(Constant.EXTRA_MNAME));
                                setIntentExtra(resultIntent);
                                setResult(-1, resultIntent);
                                finish();
                                return;
                            }
                            return;
                        case 1002:
                            if (resultCode == -1) {
                                Intent resultIntent2 = new Intent();
                                resultIntent2.putExtra(Constant.EXTRA_MPARENT, data.getStringExtra(Constant.EXTRA_MPARENT));
                                resultIntent2.putExtra(Constant.EXTRA_MNAME, data.getStringExtra(Constant.EXTRA_MNAME));
                                if (data.getBooleanExtra(Constant.EXTRA_REPLACE, false)) {
                                    DocumentRepository.removeDocument(this, this.m_curDoc);
                                }
                                setIntentExtra(resultIntent2);
                                setResult(-1, resultIntent2);
                                finish();
                                return;
                            }
                            return;
                        case 1003:
                            if (resultCode == 5) {
                                Toast.makeText(this, R.string.error_can_not_take_image, 1).show();
                                return;
                            } else if (resultCode != -1) {
                                return;
                            } else {
                                if (data.getFlags() == 1) {
                                    DocumentRepository.removeDocument(this, this.m_curDoc);
                                    PolygonUtil.insertBatch2Grid(this, (List) new Gson().fromJson(data.getStringExtra(Constant.EXTRA_DOC_ARRAY), new TypeToken<List<Document>>() {
                                    }.getType()), this.m_curDoc.parent, this.existCnt);
                                    commitResult();
                                    finish();
                                    return;
                                }
                                CropImageActivity.replaceWithNewFile(this, DetectionResult.FIX_RECT_MODE.FIX_RECT_CAMERA, this.m_curDoc.parent, this.m_curDoc.sortID, true);
                                return;
                            }
                        default:
                            super.onActivityResult(requestCode, resultCode, data);
                            return;
                    }
                } else {
                    this.m_curDoc = (data == null || !data.hasExtra(Constant.EXTRA_DOCUMENT)) ? this.m_curDoc : (Document) data.getSerializableExtra(Constant.EXTRA_DOCUMENT);
                    if (data != null && data.getBooleanExtra(Constant.RETAKE_OCR, false)) {
                        recognizeDocument();
                    }
                }
            } else if (resultCode == -1) {
//                Analytics.get().logDocumentSigned();
                this.m_curDoc = (Document) data.getSerializableExtra(Constant.EXTRA_DOCUMENT);
                try {
                    this.m_bmpSource = BitmapFactory.decodeFile(this.m_curDoc.path);
                    this.pictureImageView.setImageBitmap(this.m_bmpSource);
                } catch (NullPointerException | OutOfMemoryError ex) {
                    Toast.makeText(this, getString(R.string.alert_sorry), 1).show();
                    finish();
                }
            }
        } else if (resultCode == -1) {
            goToSignAddActivity();
        }
    }

    public void onBackPressed() {
        commitResult();
        finish();
    }
}
