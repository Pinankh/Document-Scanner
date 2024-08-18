package com.camscanner.paperscanner.pdfcreator.features.ocr.presentation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.camscanner.paperscanner.pdfcreator.R;

public class LinedTextView extends AppCompatTextView {
    private final int COLOR_BACK = Color.parseColor("#ffffff");
    private final int COLOR_UNDERLINE = Color.parseColor("#eeeeee");
    private Paint backPaint = new Paint();
    private float dividerHeight;
    private float doubleDividerHeight;
    private Paint underlinePaint = new Paint();

    public LinedTextView(Context context) {
        super(context);
        initPaint();
    }

    public LinedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public LinedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initPaint();
    }

    private void initPaint() {
        this.underlinePaint.setStyle(Paint.Style.STROKE);
        this.underlinePaint.setColor(this.COLOR_UNDERLINE);
        this.backPaint.setColor(this.COLOR_BACK);
        this.dividerHeight = getResources().getDimension(R.dimen.ocr_text_divider_height);
        this.doubleDividerHeight = this.dividerHeight * 2.0f;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawUnderline(canvas);
    }

    private void drawUnderline(Canvas canvas) {
        int left = getLeft();
        int right = getRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int height = getHeight();
        int lineHeight = getLineHeight();
        float line8Height = ((float) getLineHeight()) / 8.0f;
        float line4Height = 2.0f * line8Height;
        float line38Height = 3.0f * line8Height;
        int count = ((height - paddingTop) - paddingBottom) / lineHeight;
        int i = 0;
        while (i < count) {
            float nextLineTop = (float) (((i + 1) * lineHeight) + paddingTop);
            float underLineTop = nextLineTop - line4Height;
            Canvas canvas2 = canvas;
            canvas2.drawRect((float) (left + paddingLeft), nextLineTop - line38Height, (float) (right - paddingRight), nextLineTop + this.doubleDividerHeight, this.backPaint);
            canvas.drawRect((float) (left + paddingLeft), underLineTop, (float) (right - paddingRight), underLineTop + this.dividerHeight, this.underlinePaint);
            i++;
            paddingTop = paddingTop;
            paddingBottom = paddingBottom;
        }
    }
}
