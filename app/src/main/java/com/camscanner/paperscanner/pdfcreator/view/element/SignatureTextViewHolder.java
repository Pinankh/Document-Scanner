package com.camscanner.paperscanner.pdfcreator.view.element;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.RectF;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;

import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.model.types.SignColor;

public class SignatureTextViewHolder extends SignatureViewHolder {

    int MAX_TEXT_SIZE;

    int MIN_TEXT_SIZE;

    float STEP_SIZE;
    private final SignColor color;
    private float currentTextSize;
    private final int initMaxHeight;
    private final int initMaxWidth;
    private final String sign;

    int textPadding;
    Context context;

    public static SignatureViewHolder create(Context context, String name, String sign2, SignColor color2, float initTextSize, int initMaxWidth2, int initMaxHeight2, SignatureViewHolder.SignatureListener listener) {
        return new SignatureTextViewHolder(context, sign2, color2, initTextSize, initMaxWidth2, initMaxHeight2, listener, name);
    }

    private SignatureTextViewHolder(Context context, String sign2, SignColor color2, float initTextSize, int initMaxWidth2, int initMaxHeight2, SignatureViewHolder.SignatureListener listener, String name) {
        super(context, listener, name);
        this.sign = sign2;
        this.color = color2;
        this.context = context;
        this.currentTextSize = initTextSize;
        this.initMaxWidth = initMaxWidth2;
        this.initMaxHeight = initMaxHeight2;
        calculateInitSize();
        initView();
    }

    private void initView() {

        Resources res = context.getResources();
       MAX_TEXT_SIZE = res.getDimensionPixelSize(R.dimen.sign_text_max);
       MIN_TEXT_SIZE = res.getDimensionPixelSize(R.dimen.sign_text_min);
       STEP_SIZE = res.getDimension(R.dimen.sign_text_step);
       textPadding = res.getDimensionPixelSize(R.dimen.sign_text_padding);
        signature().setText(this.sign);
        signature().setTextColor(ContextCompat.getColor(this.root.getContext(), getColor()));
        signature().setTextSize(this.currentTextSize);
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(signature(), this.MIN_TEXT_SIZE, this.MAX_TEXT_SIZE, (int) this.STEP_SIZE, 0);
    }

    /* access modifiers changed from: protected */
    @SuppressLint("WrongConstant")
    public int[] initSize() {
        TextView textView = new TextView(this.root.getContext());
        textView.setText(this.sign);
        textView.setTextSize(0, this.currentTextSize);
        int i = this.textPadding;
        textView.setPadding(i, i, i, i);
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
        textView.measure(widthMeasureSpec, heightMeasureSpec);
        int height = textView.getMeasuredHeight();
        int width = textView.getMeasuredWidth();
        while (true) {
            if (width > this.initMaxWidth || height > this.initMaxHeight) {
                int i2 = this.initMaxWidth;
                if (width <= i2 || height <= this.initMaxHeight) {
                    int i3 = this.initMaxWidth;
                    if (width > i3) {
                        widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(i3 - getPaddingFrame(), Integer.MIN_VALUE);
                    } else {
                        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(this.initMaxHeight - getPaddingFrame(), Integer.MIN_VALUE);
                    }
                } else {
                    widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(i2 - getPaddingFrame(), Integer.MIN_VALUE);
                    heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(this.initMaxHeight - getPaddingFrame(), Integer.MIN_VALUE);
                }
                textView.measure(widthMeasureSpec, heightMeasureSpec);
                height = textView.getMeasuredHeight();
                width = textView.getMeasuredWidth();
            } else {
                return new int[]{width, height};
            }
        }
    }

    private int getPaddingFrame() {
        return this.textPadding * 2;
    }

    public boolean resize(float signum, float diff, RectF imageArea) {
        float f = this.STEP_SIZE;
        if (diff < f) {
            return false;
        }
        changeSize(f * signum);
        updateBtnsPositions();
        return true;
    }

    static /* synthetic */ class C69111 {
        static final /* synthetic */ int[] MySignColor = new int[SignColor.values().length];

        static {
            try {
                MySignColor[SignColor.RED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                MySignColor[SignColor.BLUE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                MySignColor[SignColor.BLACK.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private int getColor() {
        int i = C69111.MySignColor[this.color.ordinal()];
        if (i == 1) {
            return R.color.color_signature_red;
        }
        if (i != 2) {
            return R.color.color_signature_black;
        }
        return R.color.color_signature_blue;
    }

    /* access modifiers changed from: protected */
    public int provideLayout() {
        return R.layout.item_signature_text;
    }

    private void changeSize(float diff) {
        float newSize = signature().getTextSize() + diff;
        if (newSize > ((float) this.MIN_TEXT_SIZE) && newSize < ((float) this.MAX_TEXT_SIZE)) {
            this.currentTextSize = newSize;
            calculateInitSize();
        }
    }

    public TextView signature() {
        return (TextView) this.signContainer;
    }
}
