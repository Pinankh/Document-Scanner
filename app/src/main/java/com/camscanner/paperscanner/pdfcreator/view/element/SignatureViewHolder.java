package com.camscanner.paperscanner.pdfcreator.view.element;

import android.content.Context;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;

import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.utils.GeoUtil;
import com.camscanner.paperscanner.pdfcreator.common.utils.ObjectsUtil;
import com.camscanner.paperscanner.pdfcreator.common.utils.ViewUtil;
import com.camscanner.paperscanner.pdfcreator.model.types.SignMode;

public abstract class SignatureViewHolder {
    public static final int PARAM_HEIGHT = 1;
    public static final int PARAM_WIDTH = 0;
    ImageView btnMove;
    ImageView btnRemove;
    ImageView btnResize;
    ImageView btnRotate;
    float btnSize;
    String degreeText;
    protected final int halfBtn = ((int) (this.btnSize / 2.0f));
    protected final SignatureListener listener;
    List<View> menu;
    protected final String name;
    ConstraintLayout root;
    View signContainer;
    View signContainerVert;
    View signFrame;
    TextView textRotate;

    public interface SignatureListener {
        void onRemoveClicked(SignatureViewHolder signatureViewHolder);
    }

    /* access modifiers changed from: protected */
    public abstract int[] initSize();

    /* access modifiers changed from: protected */
    public abstract int provideLayout();

    public abstract boolean resize(float f, float f2, RectF rectF);

    protected SignatureViewHolder(Context context, SignatureListener listener2, String name2) {
        this.listener = listener2;
        this.name = name2;
        this.root = inflateView(context);
        this.root.setId(ViewUtil.generateId());

        btnMove = (ImageView)root.findViewById(R.id.btn_move);
        btnRemove = (ImageView)root.findViewById(R.id.btn_remove);
        btnResize = (ImageView)root.findViewById(R.id.btn_resize);
        btnRotate = (ImageView)root.findViewById(R.id.btn_rotate);
        textRotate = (TextView) root.findViewById(R.id.text_rotate);
        signContainer = (View)root.findViewById(R.id.sign);
        signContainerVert = (View)root.findViewById(R.id.sign_rotated);
        signFrame = (View)root.findViewById(R.id.sign_frame);

        btnSize = context.getResources().getDimension(R.dimen.sign_ui_btn_size);
        degreeText = context.getResources().getString(R.string.degree);

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRemoveClicked();
            }
        });

    }

    public String getAnalyticsName() {
        return this.name;
    }

    /* access modifiers changed from: protected */
    public final void calculateInitSize() {
        int[] params = initSize();
        setSize(params[0], params[1]);
    }

    private void setSize(int realWidth, int realHeight) {
        this.root.setLayoutParams(this.root.getLayoutParams() != null ? (ConstraintLayout.LayoutParams) this.root.getLayoutParams() : new ConstraintLayout.LayoutParams(-2, -2));
        ConstraintLayout.LayoutParams paramsImage = (ConstraintLayout.LayoutParams) this.signContainer.getLayoutParams();
        paramsImage.width = realWidth;
        paramsImage.height = realHeight;
        this.signContainer.setLayoutParams(paramsImage);
        ConstraintLayout.LayoutParams paramsImageInverse = (ConstraintLayout.LayoutParams) this.signContainerVert.getLayoutParams();
        paramsImageInverse.width = realHeight;
        paramsImageInverse.height = realWidth;
        this.signContainerVert.setLayoutParams(paramsImageInverse);
    }

    public View getView() {
        return this.root;
    }

    public View getSignView() {
        return this.signContainer;
    }

    public int[] getSignParams() {
        ViewGroup.LayoutParams params = this.signContainer.getLayoutParams();
        return new int[]{params.width, params.height};
    }

    public void setSignParams(int[] params) {
        ViewGroup.LayoutParams layoutParams = this.signContainer.getLayoutParams();
        layoutParams.width = params[0];
        layoutParams.height = params[1];
        this.signContainer.setLayoutParams(layoutParams);
        ViewGroup.LayoutParams layoutParams2 = this.signContainerVert.getLayoutParams();
        layoutParams2.width = params[1];
        layoutParams2.height = params[0];
        this.signContainerVert.setLayoutParams(layoutParams2);
    }

    public View getResizeView() {
        return this.btnResize;
    }

    public View getMoveView() {
        return this.btnMove;
    }

    public View getRotateView() {
        return this.btnRotate;
    }

    public void setSignRotation(float finalDegree) {
        this.signContainer.setRotation(finalDegree);
        this.signFrame.setRotation(finalDegree);
        this.textRotate.setText(String.format(this.degreeText, new Object[]{String.valueOf((int) finalDegree)}));
        updateBtnsPositions(finalDegree);
    }

    /* access modifiers changed from: protected */
    public void updateBtnsPositions() {
        updateBtnsPositions(this.signContainer.getRotation());
    }

    private void updateBtnsPositions(float finalDegree) {
        float[] points = GeoUtil.getRotatedCoordsOfView((float) this.signContainer.getLeft(), (float) this.signContainer.getWidth(), (float) this.signContainer.getTop(), (float) this.signContainer.getHeight(), finalDegree);
        this.btnRotate.setX(points[2] - ((float) this.halfBtn));
        this.btnRotate.setY(points[3] - ((float) this.halfBtn));
        this.btnMove.setX(points[6] - ((float) this.halfBtn));
        this.btnMove.setY(points[7] - ((float) this.halfBtn));
        this.btnResize.setX(points[4] - ((float) this.halfBtn));
        this.btnResize.setY(points[5] - ((float) this.halfBtn));
    }

    public void setMenuVisibility(boolean visible) {
//        ButterKnife.apply(this.menu, $$Lambda$SignatureViewHolder$Wqg67caozXoQwJUedhfprX8n1Yg.INSTANCE, Integer.valueOf(visible ? 0 : 4));


//        this.menu.setVisibility(((Integer) obj).intValue());
        if (this.textRotate.getVisibility() == 0) {
            this.textRotate.setVisibility(4);
        }
    }

    public void setPartMenuVisibility(SignMode mode) {
        this.btnRemove.setVisibility(4);
        this.signFrame.setVisibility(0);
        int i = C69121.MySignMode[mode.ordinal()];
        if (i == 1) {
            this.btnMove.setVisibility(0);
            this.btnResize.setVisibility(4);
            this.btnRotate.setVisibility(4);
            this.textRotate.setVisibility(4);
        } else if (i == 2) {
            this.btnMove.setVisibility(4);
            this.btnResize.setVisibility(0);
            this.btnRotate.setVisibility(4);
            this.textRotate.setVisibility(4);
        } else if (i == 3) {
            this.btnMove.setVisibility(4);
            this.btnResize.setVisibility(4);
            this.btnRotate.setVisibility(0);
            this.textRotate.setText("");
            this.textRotate.setVisibility(0);
        }
    }

    static /* synthetic */ class C69121 {
        static final /* synthetic */ int[] MySignMode = new int[SignMode.values().length];

        static {
            try {
                MySignMode[SignMode.MOVE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                MySignMode[SignMode.RESIZE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                MySignMode[SignMode.ROTATE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }


    public void onRemoveClicked() {
        this.listener.onRemoveClicked(this);
    }

    private ConstraintLayout inflateView(Context context) {
        return (ConstraintLayout) LayoutInflater.from(context).inflate(provideLayout(), (ViewGroup) null, false);
    }

    public static boolean isSign(View view) {
        return view.getId() == R.id.sign;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SignatureViewHolder that = (SignatureViewHolder) o;
        if (!ObjectsUtil.equals(this.root, that.root) || !ObjectsUtil.equals(this.signContainer, that.signContainer) || !ObjectsUtil.equals(this.signContainerVert, that.signContainerVert) || !ObjectsUtil.equals(this.signFrame, that.signFrame) || !ObjectsUtil.equals(this.btnMove, that.btnMove) || !ObjectsUtil.equals(this.btnRemove, that.btnRemove) || !ObjectsUtil.equals(this.btnRotate, that.btnRotate) || !ObjectsUtil.equals(this.textRotate, that.textRotate) || !ObjectsUtil.equals(this.btnResize, that.btnResize) || !ObjectsUtil.equals(this.degreeText, that.degreeText) || !ObjectsUtil.equals(this.menu, that.menu) || !ObjectsUtil.equals(this.listener, that.listener)) {
            return false;
        }
        return true;
    }
}
