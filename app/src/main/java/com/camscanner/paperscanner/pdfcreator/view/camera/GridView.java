package com.camscanner.paperscanner.pdfcreator.view.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class GridView extends View {
    public static final int GRID_COUNT = 3;
    private Context mContext;
    private Paint mPaint = new Paint();
    private boolean m_bShowGrid;

    public GridView(Context context) {
        super(context);
        this.mPaint.setColor(Color.parseColor("#ffffff"));
        this.mPaint.setStrokeWidth(2.0f);
        this.m_bShowGrid = false;
        this.mContext = context;
    }

    public GridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mPaint.setColor(Color.parseColor("#ffffff"));
        this.mPaint.setStrokeWidth(2.0f);
        this.m_bShowGrid = false;
        this.mContext = context;
    }

    public GridView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mPaint.setColor(Color.parseColor("#ffffff"));
        this.mPaint.setStrokeWidth(2.0f);
        this.m_bShowGrid = false;
        this.mContext = context;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.m_bShowGrid) {
            int width = canvas.getWidth();
            int height = canvas.getHeight();
            for (int i = 1; i < 3; i++) {
                canvas.drawLine(0.0f, (float) ((height / 3) * i), (float) width, (float) ((height / 3) * i), this.mPaint);
            }
            for (int i2 = 1; i2 < 3; i2++) {
                canvas.drawLine((float) ((width / 3) * i2), 0.0f, (float) ((width / 3) * i2), (float) height, this.mPaint);
            }
        }
    }

    public void showGrid(boolean bShow) {
        this.m_bShowGrid = bShow;
        ((Activity) this.mContext).runOnUiThread(new Runnable() {
            public void run() {
                GridView.this.invalidate();
            }
        });
    }
}
