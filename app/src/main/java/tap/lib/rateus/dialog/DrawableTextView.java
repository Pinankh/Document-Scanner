package tap.lib.rateus.dialog;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatTextView;

import com.camscanner.paperscanner.pdfcreator.R;

public class DrawableTextView extends AppCompatTextView {
    public DrawableTextView(Context context) {
        super(context);
    }

    public DrawableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public DrawableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    /* access modifiers changed from: package-private */
    public void initAttrs(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray attributeArray = context.obtainStyledAttributes(attrs, R.styleable.DrawableTextView);
            Drawable drawableLeft = null;
            Drawable drawableRight = null;
            Drawable drawableBottom = null;
            Drawable drawableTop = null;
            if (Build.VERSION.SDK_INT >= 21) {
                drawableLeft = attributeArray.getDrawable(R.styleable.DrawableTextView_drawableLeftCompat);
                drawableRight = attributeArray.getDrawable(R.styleable.DrawableTextView_drawableRightCompat);
                drawableBottom = attributeArray.getDrawable(R.styleable.DrawableTextView_drawableBottomCompat);
                drawableTop = attributeArray.getDrawable(R.styleable.DrawableTextView_drawableTopCompat);
            } else {
                int drawableLeftId = attributeArray.getResourceId(R.styleable.DrawableTextView_drawableLeftCompat, -1);
                int drawableRightId = attributeArray.getResourceId(R.styleable.DrawableTextView_drawableRightCompat, -1);
                int drawableBottomId = attributeArray.getResourceId(R.styleable.DrawableTextView_drawableBottomCompat, -1);
                int drawableTopId = attributeArray.getResourceId(R.styleable.DrawableTextView_drawableTopCompat, -1);
                if (drawableLeftId != -1) {
                    drawableLeft = AppCompatResources.getDrawable(context, drawableLeftId);
                }
                if (drawableRightId != -1) {
                    drawableRight = AppCompatResources.getDrawable(context, drawableRightId);
                }
                if (drawableBottomId != -1) {
                    drawableBottom = AppCompatResources.getDrawable(context, drawableBottomId);
                }
                if (drawableTopId != -1) {
                    drawableTop = AppCompatResources.getDrawable(context, drawableTopId);
                }
            }
            setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawableRight, drawableBottom);
            attributeArray.recycle();
        }
    }
}
