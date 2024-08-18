package com.camscanner.paperscanner.pdfcreator.view.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import com.camscanner.paperscanner.pdfcreator.R;

public class DrawingView extends View {
    private boolean m_bFocus = false;
    private boolean m_bHaveTouch = false;
    private Rect m_rectTouchArea;
    private Paint paint = new Paint();
    private int width;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeWidth(2.0f);
        this.m_bHaveTouch = false;
    }

    public void setHaveTouch(boolean val, Rect rect, boolean isfocus) {
        this.m_bHaveTouch = val;
        this.m_rectTouchArea = rect;
        this.m_bFocus = isfocus;
    }

    public boolean isHaveTouch() {
        return this.m_bHaveTouch;
    }

    public void onDraw(Canvas canvas) {
        if (!this.m_bHaveTouch) {
            return;
        }
        if (this.m_bFocus) {
            this.paint.setColor(getResources().getColor(R.color.colorNavBar));
            Canvas canvas2 = canvas;
            canvas2.drawLine((float) this.m_rectTouchArea.left, (float) this.m_rectTouchArea.top, (float) this.m_rectTouchArea.left, (float) this.m_rectTouchArea.bottom, this.paint);
            canvas2.drawLine((float) this.m_rectTouchArea.right, (float) this.m_rectTouchArea.top, (float) this.m_rectTouchArea.right, (float) this.m_rectTouchArea.bottom, this.paint);
            canvas2.drawLine((float) this.m_rectTouchArea.left, (float) this.m_rectTouchArea.top, (float) (this.m_rectTouchArea.left + 30), (float) this.m_rectTouchArea.top, this.paint);
            canvas2.drawLine((float) (this.m_rectTouchArea.left + 110), (float) this.m_rectTouchArea.top, (float) this.m_rectTouchArea.right, (float) this.m_rectTouchArea.top, this.paint);
            canvas2.drawLine((float) this.m_rectTouchArea.left, (float) this.m_rectTouchArea.bottom, (float) (this.m_rectTouchArea.left + 30), (float) this.m_rectTouchArea.bottom, this.paint);
            canvas2.drawLine((float) (this.m_rectTouchArea.left + 110), (float) this.m_rectTouchArea.bottom, (float) this.m_rectTouchArea.right, (float) this.m_rectTouchArea.bottom, this.paint);
            return;
        }
        this.paint.setColor(getResources().getColor(R.color.colorWhite));
        Canvas canvas3 = canvas;
        canvas3.drawLine((float) this.m_rectTouchArea.left, (float) this.m_rectTouchArea.top, (float) this.m_rectTouchArea.left, (float) this.m_rectTouchArea.bottom, this.paint);
        canvas3.drawLine((float) this.m_rectTouchArea.right, (float) this.m_rectTouchArea.top, (float) this.m_rectTouchArea.right, (float) this.m_rectTouchArea.bottom, this.paint);
        canvas3.drawLine((float) this.m_rectTouchArea.left, (float) this.m_rectTouchArea.top, (float) (this.m_rectTouchArea.left + 30), (float) this.m_rectTouchArea.top, this.paint);
        canvas3.drawLine((float) (this.m_rectTouchArea.left + 110), (float) this.m_rectTouchArea.top, (float) this.m_rectTouchArea.right, (float) this.m_rectTouchArea.top, this.paint);
        canvas3.drawLine((float) this.m_rectTouchArea.left, (float) this.m_rectTouchArea.bottom, (float) (this.m_rectTouchArea.left + 30), (float) this.m_rectTouchArea.bottom, this.paint);
        canvas3.drawLine((float) (this.m_rectTouchArea.left + 110), (float) this.m_rectTouchArea.bottom, (float) this.m_rectTouchArea.right, (float) this.m_rectTouchArea.bottom, this.paint);
    }
}
