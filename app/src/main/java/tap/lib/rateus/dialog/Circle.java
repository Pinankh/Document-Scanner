package tap.lib.rateus.dialog;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.camscanner.paperscanner.pdfcreator.R;
import timber.log.Timber;

public final class Circle extends View {
    private static final int ANGLE_POINT = 260;
    public static final int FULL = 360;
    private static final int START_ANGLE_POINT = 190;
    private float angle = 260.0f;
    private int layout_height;
    private int layout_width;
    private Paint paint;
    private RectF rect;
    private float strokeWidth;

    public Circle(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, new int[]{16842996, 16842997});
        this.layout_width = ta.getDimensionPixelSize(0, -1);
        this.layout_height = ta.getDimensionPixelSize(1, -1);
        ta.recycle();
        Timber.i("width %s height %s", Integer.valueOf(this.layout_width), Integer.valueOf(this.layout_height));
        this.strokeWidth = (float) ((int) getResources().getDimension(R.dimen.rateuslib_circle_width));
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(getRect(), 190.0f, this.angle, false, getPaint());
    }

    private Paint getPaint() {
        if (this.paint == null) {
            this.paint = new Paint();
            this.paint.setAntiAlias(true);
            this.paint.setStyle(Paint.Style.STROKE);
            this.paint.setStrokeWidth(this.strokeWidth);
            this.paint.setColor(getResources().getColor(R.color.rateuslib_icon_color));
        }
        return this.paint;
    }

    private RectF getRect() {
        if (this.rect == null) {
            float f = this.strokeWidth;
            this.rect = new RectF(f, f, ((float) this.layout_width) - f, ((float) this.layout_height) - f);
        }
        return this.rect;
    }

    public void setAngle(float angle2) {
        this.angle = angle2;
    }
}
