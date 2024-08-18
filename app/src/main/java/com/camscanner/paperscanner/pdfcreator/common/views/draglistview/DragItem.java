package com.camscanner.paperscanner.pdfcreator.common.views.draglistview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

public class DragItem {
    protected static final int ANIMATION_DURATION = 250;
    private float mAnimationDx;
    private float mAnimationDy;
    private boolean mCanDragHorizontally = true;
    private View mDragView;
    private float mOffsetX;
    private float mOffsetY;
    private float mPosTouchDx;
    private float mPosTouchDy;
    private float mPosX;
    private float mPosY;
    private boolean mSnapToTouch = true;
    private float m_fltTopbarHeight = 0.0f;
    private float m_prevTouchX;
    private float m_prevTouchY;

    public DragItem(Context context) {
        this.mDragView = new View(context);
        this.mDragView.setAlpha(0.5f);
        hide();
    }

    public DragItem(Context context, int layoutId) {
        this.mDragView = View.inflate(context, layoutId, (ViewGroup) null);
        this.mDragView.setAlpha(0.5f);
        hide();
    }

    public void onBindDragView(View clickedView, View dragView) {
        Bitmap bitmap = Bitmap.createBitmap(clickedView.getWidth(), clickedView.getHeight(), Bitmap.Config.ARGB_8888);
        clickedView.draw(new Canvas(bitmap));
        if (Build.VERSION.SDK_INT >= 16) {
            dragView.setBackground(new BitmapDrawable(clickedView.getResources(), bitmap));
        } else {
            dragView.setBackgroundDrawable(new BitmapDrawable(clickedView.getResources(), bitmap));
        }
    }

    public void onMeasureDragView(View clickedView, View dragView) {
        dragView.setLayoutParams(new FrameLayout.LayoutParams(clickedView.getMeasuredWidth(), clickedView.getMeasuredHeight()));
        dragView.measure(View.MeasureSpec.makeMeasureSpec(clickedView.getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(clickedView.getMeasuredHeight(), 1073741824));
    }

    public void onStartDragAnimation(View dragView) {
    }

    public void onEndDragAnimation(View dragView) {
    }

    /* access modifiers changed from: package-private */
    public boolean canDragHorizontally() {
        return this.mCanDragHorizontally;
    }

    /* access modifiers changed from: package-private */
    public void setCanDragHorizontally(boolean canDragHorizontally) {
        this.mCanDragHorizontally = canDragHorizontally;
    }

    /* access modifiers changed from: package-private */
    public boolean isSnapToTouch() {
        return this.mSnapToTouch;
    }

    /* access modifiers changed from: package-private */
    public void setSnapToTouch(boolean snapToTouch) {
        this.mSnapToTouch = snapToTouch;
    }

    /* access modifiers changed from: package-private */
    public View getDragItemView() {
        return this.mDragView;
    }

    private void show() {
        this.mDragView.setVisibility(0);
    }

    /* access modifiers changed from: package-private */
    public void hide() {
        this.mDragView.setVisibility(8);
    }

    public void setTopbarHeight(float height) {
        this.m_fltTopbarHeight = height;
    }

    /* access modifiers changed from: package-private */
    public void startDrag(View startFromView, float touchX, float touchY) {
        show();
        onBindDragView(startFromView, this.mDragView);
        onMeasureDragView(startFromView, this.mDragView);
        onStartDragAnimation(this.mDragView);
        this.m_prevTouchX = touchX;
        this.m_prevTouchY = this.m_fltTopbarHeight + touchY;
        this.mDragView.setX(startFromView.getX());
        this.mDragView.setY(startFromView.getY() + this.m_fltTopbarHeight);
    }

    /* access modifiers changed from: package-private */
    public void endDrag(View endToView, AnimatorListenerAdapter listener) {
        onEndDragAnimation(this.mDragView);
        float endX = endToView.getX();
        float endY = endToView.getY() + this.m_fltTopbarHeight;
        ObjectAnimator animX = ObjectAnimator.ofFloat(this.mDragView, "x", new float[]{endX});
        ObjectAnimator animY = ObjectAnimator.ofFloat(this.mDragView, "y", new float[]{endY});
        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(new Animator[]{animX, animY});
        animSet.setInterpolator(new DecelerateInterpolator());
        animSet.setDuration(250);
        animSet.addListener(listener);
        animSet.start();
    }

    /* access modifiers changed from: package-private */
    public void setX(float x) {
        this.mPosX = x;
        updatePosition();
    }

    /* access modifiers changed from: package-private */
    public void setY(float y) {
        this.mPosY = y;
        updatePosition();
    }

    /* access modifiers changed from: package-private */
    public float getX() {
        return this.mDragView.getX();
    }

    /* access modifiers changed from: package-private */
    public float getY() {
        return this.mDragView.getY();
    }

    public float getTouchX() {
        return this.m_prevTouchX;
    }

    public float getTouchY() {
        return this.m_prevTouchY;
    }

    /* access modifiers changed from: package-private */
    public float getMeaSuredWidth() {
        return (float) this.mDragView.getMeasuredWidth();
    }

    /* access modifiers changed from: package-private */
    public float getMeasuredHeight() {
        return (float) this.mDragView.getMeasuredHeight();
    }

    /* access modifiers changed from: package-private */
    public void setPosition(float touchX, float touchY) {
        float curX = this.mDragView.getX();
        float curY = this.mDragView.getY();
        this.mDragView.setX(curX + (touchX - this.m_prevTouchX));
        this.mDragView.setY(curY + (touchY - this.m_prevTouchY));
        this.m_prevTouchX = touchX;
        this.m_prevTouchY = touchY;
    }

    private void updatePosition() {
        if (this.mCanDragHorizontally) {
            View view = this.mDragView;
            view.setX(((this.mPosX + this.mOffsetX) + this.mAnimationDx) - ((float) (view.getMeasuredWidth() / 2)));
        }
        View view2 = this.mDragView;
        view2.setY(((this.mPosY + this.mOffsetY) + this.mAnimationDy) - ((float) (view2.getMeasuredHeight() / 2)));
        this.mDragView.invalidate();
    }
}
