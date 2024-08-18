package com.camscanner.paperscanner.pdfcreator.view.element;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.view.View;
import android.widget.ImageView;
import com.camscanner.paperscanner.pdfcreator.R;
import timber.log.Timber;

public class SignatureImageViewHolder extends SignatureViewHolder {

    int MIN_SIGN_SIZE;
    private final Bitmap bmpSign;
    private final int initMaxSize;
    private boolean isWidthBigger;
    private float ratio;

    public static SignatureViewHolder create(Context context, String name, Bitmap sign, int initialMaxSize, SignatureViewHolder.SignatureListener listener) {
        return new SignatureImageViewHolder(context, sign, initialMaxSize, listener, name);
    }

    private SignatureImageViewHolder(Context context, Bitmap sign, int initMaxSize2, SignatureViewHolder.SignatureListener listener, String name) {
        super(context, listener, name);
        this.bmpSign = sign;
        this.initMaxSize = initMaxSize2;
        Resources res = context.getResources();
        MIN_SIGN_SIZE = res.getDimensionPixelSize(R.dimen.min_sign_size);
        calculateInitSize();
        initView();



    }

    private void initView() {
        signature().setImageBitmap(this.bmpSign);
    }

    /* access modifiers changed from: protected */
    public int[] initSize() {
        int bmpWidth = this.bmpSign.getWidth();
        int bmpHeight = this.bmpSign.getHeight();
        this.isWidthBigger = bmpWidth > bmpHeight;
        this.ratio = ((float) bmpWidth) / ((float) bmpHeight);
        int i = this.initMaxSize;
        float scale = Math.min(((float) i) / ((float) bmpWidth), ((float) i) / ((float) bmpHeight));
        return new int[]{(int) (((float) bmpWidth) * scale), (int) (((float) bmpHeight) * scale)};
    }

    public boolean resize(float signum, float diff, RectF imageArea) {
        float yDiff;
        float xDiff;
        RectF rectF = imageArea;
        float diff2 = diff * signum;
        Timber.tag("sss").i("resize %s", Float.valueOf(diff2));
        if (this.isWidthBigger) {
            xDiff = diff2;
            yDiff = xDiff / this.ratio;
        } else {
            yDiff = diff2;
            xDiff = this.ratio * yDiff;
        }
        int[] signParams = getSignParams();
        float signWidth = ((float) signParams[0]) + xDiff;
        float signHeight = signWidth / this.ratio;
        View cont = getView();
        View sign = getSignView();
        float newX = (cont.getX() + sign.getX()) - (xDiff / 2.0f);
        float newY = (cont.getY() + sign.getY()) - (yDiff / 2.0f);
        int i = this.MIN_SIGN_SIZE;
        if (signWidth <= ((float) i) || signHeight <= ((float) i) || newX <= rectF.left || newX + signWidth >= rectF.right || newY <= rectF.top || newY + signHeight >= rectF.bottom) {
            return false;
        }
        signParams[0] = (int) signWidth;
        signParams[1] = (int) signHeight;
        setSignParams(signParams);
        updateBtnsPositions();
        return true;
    }

    /* access modifiers changed from: protected */
    public int provideLayout() {
        return R.layout.item_signature_image;
    }

    public ImageView signature() {
        return (ImageView) this.signContainer;
    }
}
