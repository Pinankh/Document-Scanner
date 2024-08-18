package com.camscanner.paperscanner.pdfcreator.common.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.AbsListView;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import timber.log.Timber;

public class FabListViewListener implements AbsListView.OnScrollListener {
    private static final String LOG_TAG = FabListViewListener.class.getSimpleName();
    private final View fab;
    private final int fabMargin;
    AnimatorListenerAdapter hideListener = new AnimatorListenerAdapter() {
        public void onAnimationStart(Animator animation) {
            super.onAnimationStart(animation);
            boolean unused = FabListViewListener.this.inHideAnimation = true;
        }

        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            boolean unused = FabListViewListener.this.inHideAnimation = false;
        }
    };
    /* access modifiers changed from: private */
    public boolean inHideAnimation;
    /* access modifiers changed from: private */
    public boolean inShowAnimation;
    private int lastFirstVisibleItem = 0;
    AnimatorListenerAdapter showListener = new AnimatorListenerAdapter() {
        public void onAnimationStart(Animator animation) {
            super.onAnimationStart(animation);
            boolean unused = FabListViewListener.this.inShowAnimation = true;
        }

        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            boolean unused = FabListViewListener.this.inShowAnimation = false;
        }
    };

    public static FabListViewListener create(View fab2, int marginId) {
        return new FabListViewListener(fab2, (int) fab2.getResources().getDimension(marginId));
    }

    private FabListViewListener(View fab2, int fabMargin2) {
        this.fab = fab2;
        this.fabMargin = fabMargin2;
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        Timber.tag(LOG_TAG).i("Prev %s current %s", Integer.valueOf(this.lastFirstVisibleItem), Integer.valueOf(firstVisibleItem));
        int i = this.lastFirstVisibleItem;
        if (i > firstVisibleItem) {
            showFab();
        } else if (i < firstVisibleItem) {
            hideFab();
        }
        this.lastFirstVisibleItem = firstVisibleItem;
    }

    private void showFab() {
        if (!this.inShowAnimation) {
            this.fab.animate().translationY(0.0f).setInterpolator(new AccelerateInterpolator()).setListener(this.showListener).start();
        }
    }

    private void hideFab() {
        if (!this.inHideAnimation) {
            this.fab.animate().translationY((float) (this.fab.getHeight() + this.fabMargin)).setInterpolator(new FastOutSlowInInterpolator()).setListener(this.hideListener).start();
        }
    }
}
