package com.camscanner.paperscanner.pdfcreator.view.activity.signature;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;


import java.util.ArrayList;
import java.util.List;

import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.Constant;
import com.camscanner.paperscanner.pdfcreator.common.cameraGalleryPicker.GalleryImage;
import com.camscanner.paperscanner.pdfcreator.common.cameraGalleryPicker.ImagesPickerManager;
import com.camscanner.paperscanner.pdfcreator.common.p018db.DBManager;
import com.camscanner.paperscanner.pdfcreator.common.utils.BitmapUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.GeoUtil;
import com.camscanner.paperscanner.pdfcreator.common.utils.ImageStorageUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.ImageViewUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.SharedPrefsUtils;
import com.camscanner.paperscanner.pdfcreator.features.analytics.AnalyticsConstants;
import com.camscanner.paperscanner.pdfcreator.model.Document;
import com.camscanner.paperscanner.pdfcreator.model.types.OptionMark;
import com.camscanner.paperscanner.pdfcreator.model.types.SignColor;
import com.camscanner.paperscanner.pdfcreator.model.types.SignMode;
import com.camscanner.paperscanner.pdfcreator.view.activity.BaseMainActivity;
import com.camscanner.paperscanner.pdfcreator.view.dialog.SignMyFreeDialog;
import com.camscanner.paperscanner.pdfcreator.view.dialog.SignMyTextDialog;
import com.camscanner.paperscanner.pdfcreator.view.element.SignatureImageViewHolder;
import com.camscanner.paperscanner.pdfcreator.view.element.SignatureTextViewHolder;
import com.camscanner.paperscanner.pdfcreator.view.element.SignatureViewHolder;

public class SignEditActivity extends BaseMainActivity implements SignMyTextDialog.SignTextCallback, SignMyFreeDialog.SignFreeCallback, SignatureViewHolder.SignatureListener {
    private static final String LOG_TAG = SignEditActivity.class.getSimpleName();
    private static final int SELECT_CHECK_BAR = 2;
    private static final int TOOLS_BAR = 1;
    float INIT_TEXT_SIZE;
    int MIN_DIFF_CENTERS;
    int RESIZE_INTERVAL;
    int SHIFT_CENTER;
    private Bitmap bmpOriginal;
    ConstraintLayout bottomBar;
    private Document doc;
    ImageView myimage;
    private RectF imageArea;
    ConstraintLayout imageContainer;
    int initMaxMark;
    int initSizeSign;
    private float lastX;
    private float lastY;
    private SignMode mode;
    LinearLayout optionBar;
    float selectedDegree;
    private SignatureViewHolder selectedSignature;
    private List<SignatureViewHolder> signatures = new ArrayList();

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!decodeDoc()) {
            finish();
            return;
        }
        setContentView((int) R.layout.activity_sign_edit);
        bottomBar = (ConstraintLayout)findViewById(R.id.bottombar);
        imageContainer = (ConstraintLayout)findViewById(R.id.root_image);
        myimage = (ImageView) findViewById(R.id.image);
        optionBar = (LinearLayout) findViewById(R.id.optionbar);
        ImageView  btn_back = (ImageView) findViewById(R.id.btn_back);
        ConstraintLayout  btn_checkbox = (ConstraintLayout) findViewById(R.id.btn_checkbox);
        ConstraintLayout  btn_date = (ConstraintLayout) findViewById(R.id.btn_date);
        ImageView  btn_done = (ImageView) findViewById(R.id.btn_done);
        ConstraintLayout  btn_freestyle = (ConstraintLayout) findViewById(R.id.btn_freestyle);
        ConstraintLayout  btn_image = (ConstraintLayout) findViewById(R.id.btn_image);
        ConstraintLayout  btn_signature = (ConstraintLayout) findViewById(R.id.btn_signature);
        ConstraintLayout  btn_text = (ConstraintLayout) findViewById(R.id.btn_text);
        RelativeLayout rl_option_cancel = (RelativeLayout) findViewById(R.id.rl_option_cancel);
        RelativeLayout  rl_option_checkbox = (RelativeLayout) findViewById(R.id.rl_option_checkbox);
        RelativeLayout  rl_option_radio = (RelativeLayout) findViewById(R.id.rl_option_radio);
        RelativeLayout  rl_select_cancel = (RelativeLayout) findViewById(R.id.rl_select_cancel);

        INIT_TEXT_SIZE = getApplicationContext().getResources().getDimension(R.dimen.init_size_font);
        MIN_DIFF_CENTERS = (int) getApplicationContext().getResources().getDimension(R.dimen.min_diff_centers);
        RESIZE_INTERVAL = (int) getApplicationContext().getResources().getDimension(R.dimen.resize_interval);
        SHIFT_CENTER = (int) getApplicationContext().getResources().getDimension(R.dimen.shift_centers);
        initMaxMark = (int) getApplicationContext().getResources().getDimension(R.dimen.init_size_mark);
        initSizeSign = (int) getApplicationContext().getResources().getDimension(R.dimen.init_size_sign);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btn_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleBottomBar(2);
            }
        });
        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddDate();
            }
        });
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSignature();
            }
        });
        btn_freestyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddFreeStyle();
            }
        });

        btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddImage();
            }
        });
        btn_signature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddSignFromPad(true);
            }
        });

        btn_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSignTextDialog();
            }
        });
        rl_option_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOptionMark(OptionMark.CROSS);
            }
        });
        rl_option_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOptionMark(OptionMark.CHECK);
            }
        });
        rl_option_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOptionMark(OptionMark.RADIO);
            }
        });
        rl_select_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleBottomBar(1);
            }
        });

        imageContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();
                if (action != 0) {
                    if (action != 1) {
                        if (action == 2) {
                            int i = C68711.MySignMode[mode.ordinal()];
                            if (i == 1) {
                                rotateSignatureView(event.getX(), event.getY());
                            } else if (i == 2) {
                                resizeSignatureView(event.getX(), event.getY());
                            } else if (i == 3) {
                                moveSignatureView(event.getX(), event.getY());
                            }
                        }
                    }
                    changeFullVisibilityOfSelectedSign(true);
                    mode = SignMode.NONE;
                    selectedDegree = 0.0f;
                } else if (selectedSignature != null) {
                    SignatureViewHolder signature =selectedSignature;
                    if (checkModeViewInArea(SignMode.RESIZE, signature.getResizeView(), signature.getView(), event) || checkModeViewInArea(SignMode.ROTATE, signature.getRotateView(), signature.getView(), event) || checkModeWithSignViewInArea(SignMode.MOVE, signature.getMoveView(), signature.getSignView(), signature.getView(), event)) {
                        changePartVisibilityOfSelectedSign();
                        if (mode.equals(SignMode.ROTATE)) {
                            selectedDegree = signature.getSignView().getRotation();
                        }
                    } else {
                        reselectSignArea(event.getX(), event.getY());
                    }
                } else {
                    reselectSignArea(event.getX(), event.getY());
                }
                return true;
            }
        });


        initData();
        initUI();
    }


    public void onStart() {
        super.onStart();
    }

    private boolean decodeDoc() {
        this.doc = (Document) getIntent().getSerializableExtra(Constant.EXTRA_DOCUMENT);
        try {
            this.bmpOriginal = BitmapFactory.decodeFile(this.doc.path);
            if (this.bmpOriginal != null) {
                return true;
            }
            return false;
        } catch (Exception | OutOfMemoryError ex) {
            return false;
        }
    }


    public boolean initData() {
        this.selectedSignature = null;
        this.mode = SignMode.NONE;
        this.lastX = 0.0f;
        this.lastY = 0.0f;
        this.selectedDegree = 0.0f;
        return true;
    }


    public void initUI() {
        toggleBottomBar(1);
        this.myimage.setImageBitmap(this.bmpOriginal);
    }



    private void onAddSignFromPad(boolean openPad) {
        Bitmap signature = getSignBmpFromPath(SharedPrefsUtils.getCurSignature(this));
        if (signature != null) {
            addSignatureAsImage(AnalyticsConstants.PARAM_VALUE_SIGN, signature, this.initSizeSign, true);
        } else if (openPad) {
            startActivityForResult(new Intent(this, SignPadActivity.class), 1014);
        }
    }

    private void onAddDate() {
        startActivityForResult(new Intent(this, SignDateActivity.class), 1018);
    }

    private void showSignTextDialog() {
        new SignMyTextDialog(this, LayoutInflater.from(this).inflate(R.layout.dlg_sign_text, (ViewGroup) null, false), this).show();
    }

    private void onAddFreeStyle() {
        new SignMyFreeDialog(this, LayoutInflater.from(this).inflate(R.layout.dlg_sign_freestyle, (ViewGroup) null, false), this).show();
    }


    public void onAddImage() {
        ImagesPickerManager.startPickerSolo(this);
    }

    private void toggleBottomBar(int mode2) {
        if (mode2 == 1) {
            this.bottomBar.setVisibility(0);
            this.optionBar.setVisibility(4);
        } else if (mode2 == 2) {
            this.bottomBar.setVisibility(4);
            this.optionBar.setVisibility(0);
        }
    }

    private void addOptionMark(OptionMark mark) {
        int drawableID;
        int i = C68711.MyOptionMark[mark.ordinal()];
        if (i == 2) {
            drawableID = R.drawable.icon_option_radio_black;
        } else if (i != 3) {
            drawableID = R.drawable.icon_option_checkbox_black;
        } else {
            drawableID = R.drawable.icon_option_cancel_black;
        }
        addSignatureAsImage(AnalyticsConstants.PARAM_VALUE_CHECKBOX, BitmapFactory.decodeResource(getResources(), drawableID), this.initMaxMark, true);
    }

    public void onBackPressed() {
        if (this.signatures.isEmpty()) {
            super.onBackPressed();
        } else {
            new AlertDialog.Builder(this).setTitle((int) R.string.dialog_title_sure).setMessage((int) R.string.dialog_message_changes).setPositiveButton((int) R.string.str_yes, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    SignEditActivity.this.lambda$onBackPressed$0$SignEditActivity(dialogInterface, i);
                }
            }).setNegativeButton((int) R.string.str_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).setCancelable(true).show();
        }
    }

    public /* synthetic */ void lambda$onBackPressed$0$SignEditActivity(DialogInterface dialog, int which) {
        dialog.dismiss();
        super.onBackPressed();
    }

    private void saveSignature() {
        Bitmap bmpSign;
        RectF imageArea2 = getImageArea();
        Bitmap bmpResult = this.bmpOriginal.copy(Bitmap.Config.ARGB_8888, true);
        float scale = Math.max(((float) this.bmpOriginal.getWidth()) / ((float) this.myimage.getWidth()), ((float) this.bmpOriginal.getHeight()) / ((float) this.myimage.getHeight()));
        StringBuilder signsForAnalytics = new StringBuilder();
        for (SignatureViewHolder signature : this.signatures) {
            View container = signature.getView();
            View sign = signature.getSignView();
            float width = ((float) sign.getWidth()) * scale;
            float x = ((container.getX() - imageArea2.left) + sign.getX()) * scale;
            float y = ((container.getY() - imageArea2.top) + sign.getY()) * scale;
            if (signature instanceof SignatureImageViewHolder) {
                bmpSign = ((BitmapDrawable) ((SignatureImageViewHolder) signature).signature().getDrawable()).getBitmap();
            } else if (signature instanceof SignatureTextViewHolder) {
                sign.buildDrawingCache();
                bmpSign = sign.getDrawingCache();
            } else {
                bmpSign = null;
            }
            if (bmpSign != null) {
                bmpResult = BitmapUtils.addSignature(bmpResult, bmpSign, x, y, sign.getRotation(), width);
                signsForAnalytics.append(signature.getAnalyticsName());
                signsForAnalytics.append(",");
            }
        }
        Bitmap imgThumb = BitmapUtils.createThumb(bmpResult);
        String pathEdited = ImageStorageUtils.saveImageToAppFolder(bmpResult);
        String pathThumb = ImageStorageUtils.saveImageToAppFolder(imgThumb);
        if (!TextUtils.isEmpty(pathEdited) && !TextUtils.isEmpty(pathThumb)) {
            ImageStorageUtils.deleteImage(this, this.doc.path);
            ImageStorageUtils.deleteImage(this, this.doc.thumb);
            Document document = this.doc;
            document.path = pathEdited;
            document.thumb = pathThumb;
            DBManager.getInstance().updateDocument(this.doc);
        }
        Intent intent = new Intent();
        intent.putExtra(Constant.EXTRA_DOCUMENT, this.doc);
        setResult(-1, intent);
        finish();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bmpSignature;
        GalleryImage galleryImage = ImagesPickerManager.handlePickerResult(requestCode, resultCode, data);
        if (galleryImage != null) {
            if (!TextUtils.isEmpty(galleryImage.getImagePath())) {
                goToSignCropActivity(galleryImage.getImagePath());
            }
        } else if (requestCode != 1014) {
            if (requestCode != 1016) {
               if (resultCode == -1) {
                    String text = data.getStringExtra(Constant.EXTRA_STR_DATE);
                    if (!TextUtils.isEmpty(text)) {
                        addSignatureAsText("date", text, SignColor.valueOf(data.getIntExtra("color", SignColor.BLACK.value())));
                    }
                }
            } else if (resultCode == -1 && (bmpSignature = getSignBmpFromPath(data.getStringExtra(Constant.EXTRA_IMG_PATH))) != null) {
                addSignatureAsImage(AnalyticsConstants.PARAM_VALUE_IMAGE, bmpSignature, this.initSizeSign, false);
            }
        } else if (resultCode == -1) {
            onAddSignFromPad(false);
        }
    }

    private Bitmap getSignBmpFromPath(String path) {
        try {
            if (TextUtils.isEmpty(path)) {
                return null;
            }
            return BitmapFactory.decodeFile(path);
        } catch (Exception | OutOfMemoryError ex) {
            return null;
        }
    }

    private void goToSignCropActivity(String path) {
        Intent intent = new Intent(this, SignCropActivity.class);
        intent.putExtra(Constant.EXTRA_IMG_PATH, path);
        startActivityForResult(intent, 1016);
    }

    public void onFreeStyleDoneClicked(Bitmap bmpSignature) {
        if (bmpSignature != null) {
            addSignatureAsImage(AnalyticsConstants.PARAM_VALUE_FREE, bmpSignature, this.initSizeSign, false);
        }
    }

    public void onTextDoneClicked(String text, SignColor color) {
        if (!TextUtils.isEmpty(text)) {
            addSignatureAsText("text", text, color);
        }
    }

    private void addSignatureAsImage(String name, Bitmap signature, int initSize, boolean replace) {
        addSignature(SignatureImageViewHolder.create(this, name, signature, initSize, this), replace);
    }

    private void addSignatureAsText(String name, String text, SignColor color) {
        RectF imageArea2 = getImageArea();
        addSignature(SignatureTextViewHolder.create(this, name, text, color, this.INIT_TEXT_SIZE, (int) (imageArea2.right - imageArea2.left), (int) (imageArea2.bottom - imageArea2.top), this), false);
    }

    private void addSignature(SignatureViewHolder signature, boolean replace) {
        placeSignatureInParent(signature, replace);
        this.signatures.add(signature);
    }

    private void placeSignatureInParent(final SignatureViewHolder signature, boolean replace) {
        final View view = signature.getView();
        this.imageContainer.addView(view);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this.imageContainer);
        ConstraintSet constraintSet2 = constraintSet;
        constraintSet2.connect(view.getId(), 4, 0, 4, 0);
        constraintSet2.connect(view.getId(), 3, 0, 3, 0);
        constraintSet2.connect(view.getId(), 1, 0, 1, 0);
        constraintSet2.connect(view.getId(), 2, 0, 2, 0);

        constraintSet.applyTo(this.imageContainer);
        if (replace) {
            view.setVisibility(View.INVISIBLE);
            this.imageContainer.post(new Runnable() {


                public final void run() {
                    SignEditActivity.this.lambda$placeSignatureInParent$2$SignEditActivity(signature, view);
                }
            });
            return;
        }
        selectSignature(signature);
    }

    public /* synthetic */ void lambda$placeSignatureInParent$2$SignEditActivity(SignatureViewHolder signature, View view) {
        replaceSignature(signature);
        view.setVisibility(0);
        selectSignature(signature);
    }

    private void replaceSignature(SignatureViewHolder signature) {
        boolean needReplace;
        View container = signature.getView();
        View sign = signature.getSignView();
        Point firstCenter = getCenter(signature);
        RectF imageArea2 = getImageArea();
        int width = sign.getWidth();
        int height = sign.getHeight();
        boolean replaced = false;
        do {
            List<SignatureViewHolder> others = new ArrayList<>(this.signatures);
            others.remove(signature);
            needReplace = false;
            boolean z = true;
            int i = others.size() - 1;
            while (true) {
                if (i < 0) {
                    break;
                }
                SignatureViewHolder other = others.get(i);
                if (compareCenters(firstCenter, getCenter(other))) {
                    needReplace = true;
                    others.remove(other);
                    break;
                }
                i--;
            }
            if (needReplace) {
                firstCenter = new Point(firstCenter.x + this.SHIFT_CENTER, firstCenter.y + this.SHIFT_CENTER);
                if (((float) (firstCenter.x + (width / 2))) >= imageArea2.right || ((float) (firstCenter.y + (height / 2))) >= imageArea2.bottom) {
                    z = false;
                }
                needReplace = z;
                replaced |= needReplace;
                continue;
            }
        } while (needReplace);
        if (replaced) {
            int newX = (firstCenter.x - (width / 2)) - ((int) sign.getX());
            int newY = (firstCenter.y - (height / 2)) - ((int) sign.getY());
            container.setX((float) newX);
            container.setY((float) newY);
        }
    }

    private boolean compareCenters(Point firstCenter, Point secondCenter) {
        return rectContains(new Point[]{new Point(secondCenter.x - this.MIN_DIFF_CENTERS, secondCenter.y - this.MIN_DIFF_CENTERS), new Point(secondCenter.x + this.MIN_DIFF_CENTERS, secondCenter.y - this.MIN_DIFF_CENTERS), new Point(secondCenter.x + this.MIN_DIFF_CENTERS, secondCenter.y + this.MIN_DIFF_CENTERS), new Point(secondCenter.x - this.MIN_DIFF_CENTERS, secondCenter.y + this.MIN_DIFF_CENTERS)}, firstCenter);
    }

    private Point getCenter(SignatureViewHolder signature) {
        return new Point((int) (signature.getView().getX() + signature.getSignView().getX() + ((float) (signature.getSignView().getWidth() / 2))), (int) (signature.getView().getY() + signature.getSignView().getY() + ((float) (signature.getSignView().getHeight() / 2))));
    }

    private void selectSignature(SignatureViewHolder signature) {
        deselectViews();
        this.selectedSignature = signature;
        changeFullVisibilityOfSelectedSign(true);
    }




    static /* synthetic */ class C68711 {
        static final /* synthetic */ int[] MyOptionMark = new int[OptionMark.values().length];
        static final /* synthetic */ int[] MySignMode = new int[SignMode.values().length];

        static {
            try {
                MySignMode[SignMode.ROTATE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                MySignMode[SignMode.RESIZE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                MySignMode[SignMode.MOVE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                MyOptionMark[OptionMark.CHECK.ordinal()] = 1;
            } catch (NoSuchFieldError e4) {
            }
            try {
                MyOptionMark[OptionMark.RADIO.ordinal()] = 2;
            } catch (NoSuchFieldError e5) {
            }
            try {
                MyOptionMark[OptionMark.CROSS.ordinal()] = 3;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    private void changeFullVisibilityOfSelectedSign(boolean visible) {
        SignatureViewHolder signatureViewHolder = this.selectedSignature;
        if (signatureViewHolder != null) {
            signatureViewHolder.setMenuVisibility(visible);
        }
    }

    private void changePartVisibilityOfSelectedSign() {
        if (this.selectedSignature != null && !this.mode.equals(SignMode.NONE)) {
            this.selectedSignature.setPartMenuVisibility(this.mode);
        }
    }

    private void reselectSignArea(float x, float y) {
        deselectViews();
        for (int i = this.signatures.size() - 1; i >= 0; i--) {
            SignatureViewHolder signature = this.signatures.get(i);
            if (checkViewArea(signature.getView(), signature.getSignView(), x, y)) {
                selectSignature(signature);
                this.lastX = x;
                this.lastY = y;
                return;
            }
        }
    }

    private boolean checkModeViewInArea(SignMode checkMode, View view, View container, MotionEvent event) {
        if (!checkViewArea(container, view, event.getX(), event.getY())) {
            return false;
        }
        this.mode = checkMode;
        this.lastX = event.getX();
        this.lastY = event.getY();
        return true;
    }

    private boolean checkModeWithSignViewInArea(SignMode checkMode, View modeView, View signView, View container, MotionEvent event) {
        if (!checkViewArea(container, modeView, event.getX(), event.getY()) && !checkViewArea(container, signView, event.getX(), event.getY())) {
            return false;
        }
        this.mode = checkMode;
        this.lastX = event.getX();
        this.lastY = event.getY();
        return true;
    }

    private boolean checkViewArea(View container, View view, float x, float y) {
        if (SignatureViewHolder.isSign(view)) {
            return rectContains(GeoUtil.getRotatedCoordsOfView(container.getX() + view.getX(), (float) view.getWidth(), container.getY() + view.getY(), (float) view.getHeight(), view.getRotation()), new Point((int) x, (int) y));
        }
        Rect rect = new Rect();
        view.getHitRect(rect);
        rect.offset((int) container.getX(), (int) container.getY());
        return rect.contains((int) x, (int) y);
    }

    private boolean rectContains(float[] rect, Point dot) {
        Point[] points = new Point[4];
        for (int i = 0; i < 4; i++) {
            points[i] = new Point((int) rect[i * 2], (int) rect[(i * 2) + 1]);
        }
        return rectContains(points, dot);
    }

    private boolean rectContains(Point[] points, Point dot) {
        int count = 0;
        for (int i = 0; i < 4; i++) {
            count += cross(i, points, dot) ? 1 : 0;
        }
        return count % 2 == 1;
    }

    private boolean cross(int first, Point[] points, Point dot) {
        int second = first == 3 ? 0 : first + 1;
        if (points[first].x != points[second].x && dot.y >= (((dot.x - points[first].x) * (points[second].y - points[first].y)) / (points[second].x - points[first].x)) + points[first].y && dot.x > Math.min(points[first].x, points[second].x) && dot.x <= Math.max(points[first].x, points[second].x)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void deselectViews() {
        if (this.selectedSignature != null) {
            this.selectedSignature = null;
            for (SignatureViewHolder signature : this.signatures) {
                signature.setMenuVisibility(false);
            }
        }
    }

    private RectF getImageArea() {
        if (this.imageArea == null) {
            this.imageArea = ImageViewUtils.getBitmapPositionInsideImageView(this.myimage);
        }
        return this.imageArea;
    }

    private void moveSignatureView(float x, float y) {
        SignatureViewHolder signature = this.selectedSignature;
        RectF imageArea2 = getImageArea();
        View container = signature.getView();
        View sign = signature.getSignView();
        float viewWidth = (float) sign.getWidth();
        float viewHeight = (float) sign.getHeight();
        float newContX = container.getX() + (x - this.lastX);
        float newContY = container.getY() + (y - this.lastY);
        if (newContX < imageArea2.left - sign.getX()) {
            newContX = imageArea2.left - sign.getX();
        }
        if (newContX > (imageArea2.right - sign.getX()) - viewWidth) {
            newContX = (imageArea2.right - sign.getX()) - viewWidth;
        }
        if (newContY < imageArea2.top - sign.getY()) {
            newContY = imageArea2.top - sign.getY();
        }
        if (newContY > (imageArea2.bottom - sign.getY()) - viewHeight) {
            newContY = (imageArea2.bottom - sign.getY()) - viewHeight;
        }
        container.setX(newContX);
        container.setY(newContY);
        this.lastX = x;
        this.lastY = y;
    }

    private void rotateSignatureView(float x, float y) {
        SignatureViewHolder signature = this.selectedSignature;
        float centerX = signature.getView().getX() + signature.getSignView().getX() + (((float) signature.getSignView().getWidth()) / 2.0f);
        float centerY = signature.getView().getY() + signature.getSignView().getY() + (((float) signature.getSignView().getHeight()) / 2.0f);
        double lastDiffY = (double) (this.lastY - centerY);
        float f = this.selectedDegree;
        signature.setSignRotation(roundDeg((f + ((float) (Math.atan2((double) (y - centerY), (double) (x - centerX)) * 57.29577951308232d))) - ((float) (Math.atan2(lastDiffY, (double) (this.lastX - centerX)) * 57.29577951308232d))));
    }

    private float roundDeg(float deg) {
        if (deg < -180.0f) {
            deg += 360.0f;
        }
        if (deg > 180.0f) {
            deg -= 360.0f;
        }
        if (Math.abs(deg) < 5.0f) {
            return 0.0f;
        }
        if (deg > -95.0f && deg < -85.0f) {
            return -90.0f;
        }
        if (deg > 85.0f && deg < 95.0f) {
            return 90.0f;
        }
        if ((deg <= -185.0f || deg >= -175.0f) && (deg >= 185.0f || deg <= 175.0f)) {
            return deg;
        }
        return 180.0f;
    }

    /* access modifiers changed from: package-private */
    public void resizeSignatureView(float x, float y) {
        int yc = Integer.compare((int) y, (int) this.lastY);
        float dist = (float) GeoUtil.distancePt2Pt(new PointF(this.lastX, this.lastY), new PointF(x, y));
        float signum = yc < 0 ? -1.0f : 1.0f;
        float increaseHypotenuse = Math.abs(dist / 2.0f);
        if (Math.abs(increaseHypotenuse) >= ((float) this.RESIZE_INTERVAL) && this.selectedSignature.resize(signum, increaseHypotenuse, getImageArea())) {
            this.lastX = x;
            this.lastY = y;
        }
    }

    public void onRemoveClicked(SignatureViewHolder signature) {
        this.imageContainer.removeView(signature.getView());
        this.signatures.remove(signature);
        this.selectedSignature = null;
    }
}
