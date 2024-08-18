package com.camscanner.paperscanner.pdfcreator.view.activity.signature;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.Constant;
import com.camscanner.paperscanner.pdfcreator.common.p018db.DBManager;
import com.camscanner.paperscanner.pdfcreator.common.utils.BitmapUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.ImageStorageUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.SharedPrefsUtils;
import com.camscanner.paperscanner.pdfcreator.model.Document;
import com.camscanner.paperscanner.pdfcreator.view.activity.BaseMainActivity;

public class SignAddActivity extends BaseMainActivity implements View.OnTouchListener {
    private Bitmap m_bmpOrg;
    private Bitmap m_bmpSignature;
    private Document m_curDoc;
    private ImageView m_ivImage;
    private ImageView m_ivSignature;
    private RectF m_rectPad;
    private RelativeLayout m_rlPad;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_add_sign);
        if (!initVariable()) {
            finish();
            return;
        }
        initUI();
        updateUI();
    }

    /* access modifiers changed from: package-private */
    public boolean initVariable() {
        this.m_curDoc = (Document) getIntent().getSerializableExtra(Constant.EXTRA_DOCUMENT);
        try {
            this.m_bmpOrg = BitmapFactory.decodeFile(this.m_curDoc.path);
            try {
                this.m_bmpSignature = BitmapFactory.decodeFile(SharedPrefsUtils.getCurSignature(this));
                this.m_rectPad = null;
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        } catch (Exception ex2) {
            ex2.printStackTrace();
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    public void initUI() {
        this.m_ivImage = (ImageView) findViewById(R.id.iv_image);
        this.m_ivSignature = (ImageView) findViewById(R.id.iv_signature);
        this.m_rlPad = (RelativeLayout) findViewById(R.id.rl_pad);
        this.m_rlPad.setOnTouchListener(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle((int) R.string.str_sign_add_title);
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: package-private */
    public void updateUI() {
        this.m_ivImage.setImageBitmap(this.m_bmpOrg);
        this.m_ivSignature.setImageBitmap(this.m_bmpSignature);
        this.m_ivSignature.setVisibility(8);
    }

    /* access modifiers changed from: package-private */
    public void setSignaturePos(float posX, float posY) {
        float posX2;
        float posY2;
        if (this.m_rectPad == null) {
            float vwPosX = this.m_ivImage.getX();
            float vwPosY = this.m_ivImage.getY();
            int vwWidth = this.m_ivImage.getWidth();
            int vwHeight = this.m_ivImage.getHeight();
            int bmpWidth = this.m_bmpOrg.getWidth();
            int bmpHeight = this.m_bmpOrg.getHeight();
            float scale = Math.min(((float) vwWidth) / ((float) bmpWidth), ((float) vwHeight) / ((float) bmpHeight));
            int vwNewWidth = (int) (((float) bmpWidth) * scale);
            int vwNewHeight = (int) (((float) bmpHeight) * scale);
            this.m_rectPad = new RectF(0.0f, 0.0f, (float) vwNewWidth, (float) vwNewHeight);
            this.m_rectPad.offset(vwPosX + ((float) ((vwWidth - vwNewWidth) / 2)), vwPosY + ((float) ((vwHeight - vwNewHeight) / 2)));
            ViewGroup.LayoutParams layoutParams = this.m_ivImage.getLayoutParams();
            layoutParams.width = vwNewWidth;
            layoutParams.height = vwNewHeight;
            this.m_ivImage.setLayoutParams(layoutParams);
        }
        int signatureWidth = this.m_ivSignature.getWidth();
        int signatureHeight = this.m_ivSignature.getHeight();
        if (posX < this.m_rectPad.left + ((float) (signatureWidth / 2))) {
            posX2 = this.m_rectPad.left + ((float) (signatureWidth / 2));
        } else {
            posX2 = posX;
        }
        if (posX2 > this.m_rectPad.right - ((float) (signatureWidth / 2))) {
            posX2 = this.m_rectPad.right - ((float) (signatureWidth / 2));
        }
        if (posY < this.m_rectPad.top + ((float) (signatureHeight / 2))) {
            posY2 = this.m_rectPad.top + ((float) (signatureHeight / 2));
        } else {
            posY2 = posY;
        }
        if (posY2 > this.m_rectPad.bottom - ((float) (signatureHeight / 2))) {
            posY2 = this.m_rectPad.bottom - ((float) (signatureHeight / 2));
        }
        this.m_ivSignature.setX(posX2 - ((float) (signatureWidth / 2)));
        this.m_ivSignature.setY(posY2 - ((float) (signatureHeight / 2)));
        this.m_ivSignature.setVisibility(0);
    }

    /* access modifiers changed from: package-private */
    public void saveSignature() {
        new Rect();
        this.m_ivSignature.getLocationInWindow(new int[2]);
        int bmpWidth = this.m_bmpOrg.getWidth();
        int bmpHeight = this.m_bmpOrg.getHeight();
        float scaleX = ((float) bmpWidth) / ((float) this.m_ivImage.getWidth());
        float scaleY = ((float) bmpHeight) / ((float) this.m_ivImage.getHeight());
        String strPath = ImageStorageUtils.saveImageToAppFolder(BitmapUtils.addSignature(this.m_bmpOrg, this.m_bmpSignature, (this.m_ivSignature.getX() - this.m_ivImage.getX()) * scaleX, (this.m_ivSignature.getY() - this.m_ivImage.getY()) * scaleY, 0.0f, ((float) this.m_ivSignature.getWidth()) * scaleX));
        if (strPath != null) {
            this.m_curDoc.path = strPath;
            DBManager.getInstance().updateDocument(this.m_curDoc);
        }
        Intent intent = new Intent();
        intent.putExtra(Constant.EXTRA_DOCUMENT, this.m_curDoc);
        setResult(-1, intent);
        finish();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sign_add, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                finish();
                break;
            case R.id.action_sign_add_cancel:
                finish();
                break;
            case R.id.action_sign_add_done:
                saveSignature();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onTouch(View v, MotionEvent event) {
        float xPos = event.getX();
        float yPos = event.getY();
        if (event.getAction() != 0) {
            return true;
        }
        setSignaturePos(xPos, yPos);
        return true;
    }
}
