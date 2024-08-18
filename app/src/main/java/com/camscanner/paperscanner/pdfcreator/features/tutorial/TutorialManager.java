package com.camscanner.paperscanner.pdfcreator.features.tutorial;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import io.codetail.animation.ViewAnimationUtils;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.Animations;
import com.camscanner.paperscanner.pdfcreator.common.utils.BitmapUtils;
import com.camscanner.paperscanner.pdfcreator.features.tutorial.model.TutorialBitmapInfo;
import com.camscanner.paperscanner.pdfcreator.features.tutorial.model.TutorialInfo;
import com.camscanner.paperscanner.pdfcreator.features.tutorial.model.TutorialViewInfo;
import timber.log.Timber;

public class TutorialManager extends AppCompatDialogFragment implements View.OnClickListener {
    private static final String CURRENT = "current";
    public static final String LOG_TAG = TutorialManager.class.getSimpleName();
    private static final String TUTORIALS = "tutorials";
    /* access modifiers changed from: private */
    public List<Animator> animators = new ArrayList();
    private Context context;
    private int currentTutorial = 0;
    private OnTutorialListener listener;
    int minSideTextMargin;
    int minTopTextMargin;
    FrameLayout root;
    private boolean targetHit = false;
    int textsMargin;
    private List<TutorialInfo> tutorials = new ArrayList();

    private interface OnAnimationListener {
        void onEnd();
    }

    public interface OnTutorialListener {
        void onTutorialClosed(TutorialInfo tutorialInfo, boolean z);

        void onTutorialViewClicked(View view);
    }

    private interface OnVisibleListener {
        void onVisible();
    }

    public static void showTutorial(FragmentManager manager, TutorialInfo... tutorials2) {
        showTutorial(manager, new ArrayList<>(Arrays.asList(tutorials2)));
    }

    public static void showTutorial(FragmentManager manager, ArrayList<TutorialInfo> list) {
        newInstance(list).showDialog(manager);
    }

    public static TutorialManager newInstance(ArrayList<TutorialInfo> list) {
        TutorialManager fragment = new TutorialManager();
        Bundle args = new Bundle();
        args.putParcelableArrayList(TUTORIALS, list);
        fragment.setArguments(args);
        return fragment;
    }

    public void showDialog(FragmentManager fm) {
        if (fm != null) {
            fm.beginTransaction().add((Fragment) this,"tutorial").commitAllowingStateLoss();
        }
    }

    public void onAttach(Context context2) {
        super.onAttach(context2);
        this.context = context2;
        if (context2 instanceof OnTutorialListener) {
            this.listener = (OnTutorialListener) context2;
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dlg_tutorial, container, false);

        root = (FrameLayout)view.findViewById(R.id.tutorials_root);


        minSideTextMargin = (int) getActivity().getResources().getDimension(R.dimen.min_side_tutorial_margin);
        minTopTextMargin = (int) getActivity().getResources().getDimension(R.dimen.min_top_tutorial_margin);
        textsMargin = (int) getActivity().getResources().getDimension(R.dimen.texts_tutorial_margin);

        initData();
        return view;
    }

    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT, this.currentTutorial);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            this.currentTutorial = savedInstanceState.getInt(CURRENT, 0);
        }
    }

    private void initData() {
        this.tutorials = getArguments() != null ? getArguments().getParcelableArrayList(TUTORIALS) : new ArrayList();
    }

    public void onResume() {
        super.onResume();
        initializeDialog();
        waitVisibleFor(new OnVisibleListener() {
            public final void onVisible() {
                TutorialManager.this.startTutorial();
            }
        });
    }

    /* access modifiers changed from: private */
    public void startTutorial() {
        this.root.removeAllViews();
        startOpenAnimation(this.currentTutorial);
    }

    /* access modifiers changed from: private */
    public void nextTutorial() {
        this.currentTutorial++;
        startOpenAnimation(this.currentTutorial);
    }

    private void initializeDialog() {
        Window window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = -1;
            params.height = -1;
            params.dimAmount = 0.7f;
            window.setAttributes(params);
            window.setBackgroundDrawable(new ColorDrawable(0));
        }
    }

    private void waitVisibleFor(final OnVisibleListener listener2) {
        final Handler waiter = new Handler();
        waiter.postDelayed(new Runnable() {
            public void run() {
                if (TutorialManager.this.isVisible()) {
                    Handler handler = waiter;
                    OnVisibleListener onVisibleListener = listener2;
                    onVisibleListener.getClass();
                    handler.postDelayed(new Runnable() {
                        public final void run() {
                            listener2.onVisible();
                        }
                    }, 16);
                    return;
                }
                waiter.postDelayed(this, 16);
            }
        }, 16);
    }

    private void startOpenAnimation(int i) {
        TutorialInfo tutorial = this.tutorials.get(i);
        if (tutorial instanceof TutorialBitmapInfo) {
            showTutorialBitmap((TutorialBitmapInfo) tutorial);
        } else if (tutorial instanceof TutorialViewInfo) {
            showTutorialView((TutorialViewInfo) tutorial);
        } else {
            showTutorialLayout(tutorial);
        }
    }

    private void showTutorialView(TutorialViewInfo tutorial) {
        showTutorialView(tutorial, (Bitmap) null);
    }

    private void showTutorialBitmap(TutorialBitmapInfo tutorial) {
        showTutorialView(tutorial, BitmapUtils.decodeUriWithoutOptimizations(this.context, tutorial.pathToBitmap));
    }

    private void showTutorialView(final TutorialViewInfo tutorial, Bitmap bitmap) {
        TutorialViewInfo tutorialViewInfo = tutorial;
        Bitmap bitmap2 = bitmap;
        final View tutorView = LayoutInflater.from(this.context).inflate(tutorialViewInfo.layoutId, (ViewGroup) null, false);
        final View frame = tutorView.findViewById(R.id.frame);
        final ConstraintLayout revealView = (ConstraintLayout) tutorView.findViewById(R.id.reveal_view);
        final View mainView = tutorView.findViewById(tutorialViewInfo.viewId);
        final View title = tutorView.findViewById(R.id.title);
        final View message = tutorView.findViewById(R.id.message);
        final View outsideView = tutorial.hasOutsideView() ? tutorView.findViewById(tutorialViewInfo.outsideViewId) : mainView;
        tutorView.setVisibility(4);
        if (tutorial.hasOutsideView()) {
            applyNewParams(outsideView, tutorialViewInfo.width, tutorialViewInfo.height);
            outsideView.setVisibility(0);
        }
        applyNewParams(mainView, tutorialViewInfo.width, tutorialViewInfo.height);
        if (bitmap2 != null) {
            ((ImageView) outsideView).setImageBitmap(bitmap2);
        }
        this.root.addView(tutorView);
        tutorView.setOnClickListener(this);
        outsideView.setOnClickListener(this);
        Runnable r0 = new Runnable() {


            public final void run() {
                TutorialManager.this.lambda$showTutorialView$0$TutorialManager(frame,tutorial, revealView, outsideView, title, message, tutorView, mainView);
            }
        };
        tutorView.post(r0);
    }

    public /* synthetic */ void lambda$showTutorialView$0$TutorialManager(View frame, TutorialViewInfo tutorial, ConstraintLayout revealView, View outsideView, View title, View message, View tutorView, View mainView) {
        frame.setX(tutorial.f14686x + (((float) (tutorial.width - revealView.getWidth())) / 2.0f));
        frame.setY(tutorial.f14687y + (((float) (tutorial.height - revealView.getHeight())) / 2.0f));
        if (tutorial.hasOutsideView()) {
            outsideView.setX(tutorial.f14686x);
            outsideView.setY(tutorial.f14687y);
        }
        if (tutorial.correctTextPosition) {
            correctTextPlace(tutorial, frame, revealView, title, message);
        }
        startAnimation(tutorView, mainView, revealView, 600, true);
    }

    private void correctTextPlace(TutorialViewInfo tutorial, View frame, ConstraintLayout revealView, View title, View message) {
        boolean changeY;
        float newX;
        TutorialViewInfo tutorialViewInfo = tutorial;
        ConstraintLayout constraintLayout = revealView;
        View view = title;
        View view2 = message;
        if (title.getWidth() != 0 && message.getWidth() != 0) {
            float titleY = title.getY() + frame.getY();
            float textX = message.getX() + frame.getX();
            float messageY = message.getY() + frame.getY();
            int maxWidth = Math.max(title.getWidth(), message.getWidth());
            boolean z = false;
            boolean textOnTop = messageY < tutorialViewInfo.f14687y;
            if (textOnTop) {
                changeY = titleY < this.root.getY() + ((float) this.minSideTextMargin);
            } else {
                changeY = ((float) message.getHeight()) + messageY > ((float) (this.root.getBottom() - this.minSideTextMargin));
            }
            boolean changeX = textX < this.root.getX() + ((float) this.minSideTextMargin);
            boolean textOnLeft = false;
            if (changeX) {
                textOnLeft = true;
            } else {
                if (((float) maxWidth) + textX > ((float) this.root.getRight())) {
                    z = true;
                }
                changeX = z;
            }
            if (changeY || changeX) {
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);
                if (changeY) {
                    if (textOnTop) {
                        constraintSet.clear(R.id.message, 4);
                        constraintSet.clear(R.id.title, 4);
                        ConstraintSet constraintSet2 = constraintSet;
                        constraintSet2.connect(R.id.title, 3, tutorialViewInfo.viewId, 4, this.minTopTextMargin);
                        constraintSet2.connect(R.id.message, 3, R.id.title, 4, this.textsMargin);
                    } else {
                        constraintSet.clear(R.id.title, 3);
                        constraintSet.clear(R.id.message, 3);
                        ConstraintSet constraintSet3 = constraintSet;
                        constraintSet3.connect(R.id.message, 4, tutorialViewInfo.viewId, 3, this.minTopTextMargin);
                        constraintSet3.connect(R.id.title, 4, R.id.message, 3, this.textsMargin);
                    }
                }
                constraintSet.applyTo(constraintLayout);
                if (!changeX) {
                    return;
                }
                if (textOnLeft) {
                    float outsidePosition = (float) this.minSideTextMargin;
                    if (frame.getX() < outsidePosition) {
                        newX = outsidePosition - frame.getX();
                    } else {
                        newX = frame.getX() + outsidePosition;
                    }
                    view2.setX(newX);
                    view.setX(newX);
                    return;
                }
                float newX2 = (((float) (this.root.getRight() - this.minSideTextMargin)) - frame.getX()) - ((float) maxWidth);
                view2.setX(newX2);
                view.setX(newX2);
            }
        }
    }

    private void showTutorialLayout(TutorialInfo tutorial) {
        final View tutorView = LayoutInflater.from(this.context).inflate(tutorial.layoutId, (ViewGroup) null, false);
        final View mainView = tutorView.findViewById(tutorial.viewId);
        final View revealView = tutorView.findViewById(R.id.reveal_view);
        tutorView.setVisibility(4);
        this.root.addView(tutorView);
        tutorView.setOnClickListener(this);
        mainView.setOnClickListener(this);
        tutorView.post(new Runnable() {


            public final void run() {
                TutorialManager.this.lambda$showTutorialLayout$1$TutorialManager(tutorView, mainView, revealView);
            }
        });
    }

    public /* synthetic */ void lambda$showTutorialLayout$1$TutorialManager(View tutorView, View mainView, View revealView) {
        startAnimation(tutorView, mainView, revealView, 600, true);
    }

    private void hideTutorialLayout(TutorialInfo tutorial, final OnAnimationListener listener2) {
        final View tutorView = this.root.findViewById(R.id.root);
        View startView = tutorView.findViewById(tutorial.viewId);
        startAnimation(tutorView, startView, tutorView.findViewById(R.id.reveal_view), Animations.DEFAULT_DURATION, false, new OnAnimationListener() {


            public final void onEnd() {
                TutorialManager.this.lambda$hideTutorialLayout$2$TutorialManager(tutorView, listener2);
            }
        });
    }

    public /* synthetic */ void lambda$hideTutorialLayout$2$TutorialManager(View tutorView, OnAnimationListener listener2) {
        this.root.removeView(tutorView);
        if (listener2 != null) {
            listener2.onEnd();
        }
    }

    private void applyNewParams(View view, int width, int height) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }

    private void startAnimation(View tutorView, View mainView, View revealView, int duration, boolean show) {
        startAnimation(tutorView, mainView, revealView, duration, show, (OnAnimationListener) null);
    }

    private void startAnimation(View tutorView, View mainView, View revealView, int duration, boolean show, final OnAnimationListener listener2) {
        int cx = (int) (tutorView.getX() + ((float) ((mainView.getLeft() + mainView.getRight()) / 2)));
        int cy = (int) (tutorView.getY() + ((float) ((mainView.getTop() + mainView.getBottom()) / 2)));
        float bigRadius = getMaxRadiusOf(tutorView);
        float finalRadius = 0.0f;
        float startRadius = show ? this.currentTutorial == 0 ? getMinRadiusOf(mainView) : 0.0f : bigRadius;
        if (show) {
            finalRadius = bigRadius;
        }
        tutorView.setVisibility(View.VISIBLE);
        try {
            Animator animator = ViewAnimationUtils.createCircularReveal(revealView, cx, cy, startRadius, finalRadius);
            animator.setInterpolator(show ? new AccelerateInterpolator() : new AccelerateDecelerateInterpolator());
            animator.setDuration((long) duration);
            animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    close(animation);
                }

                public void onAnimationEnd(Animator animation, boolean isReverse) {
                    close(animation);
                }

                public void onAnimationCancel(Animator animation) {
                    close(animation);
                }

                private void close(Animator animation) {
                    OnAnimationListener onAnimationListener = listener2;
                    if (onAnimationListener != null) {
                        onAnimationListener.onEnd();
                    }
                    animation.removeAllListeners();
                    TutorialManager.this.animators.remove(animation);
                }
            });
            this.animators.add(animator);
            animator.start();
        } catch (IllegalStateException ignore) {
            Timber.tag(LOG_TAG).e(ignore);
        }
    }

    private float getMaxRadiusOf(View tutorView) {
        return ((float) Math.max(tutorView.getHeight(), tutorView.getWidth())) / 2.0f;
    }

    private float getMinRadiusOf(View mainView) {
        return ((float) Math.min(mainView.getHeight(), mainView.getWidth())) / 2.0f;
    }

    public void onClick(View v) {
        if (!isAnimatorWorking()) {
            this.targetHit = (this.listener == null || !isLastTutorial() || v == null || v.getId() == R.id.root) ? false : true;
            onCancelClick();
            if (this.targetHit) {
                this.listener.onTutorialViewClicked(v);
            }
        }
    }

    private boolean isAnimatorWorking() {
        if (this.animators.isEmpty()) {
            return false;
        }
        Iterator it = new ArrayList(this.animators).iterator();
        while (it.hasNext()) {
            if (((Animator) it.next()).isRunning()) {
                return true;
            }
        }
        return false;
    }

    private boolean isLastTutorial() {
        return this.currentTutorial >= this.tutorials.size() - 1;
    }

    public void onCancelClick() {
        if (isLastTutorial()) {
            dismissAllowingStateLoss();
            OnTutorialListener onTutorialListener = this.listener;
            if (onTutorialListener != null) {
                List<TutorialInfo> list = this.tutorials;
                onTutorialListener.onTutorialClosed(list.get(list.size() - 1), this.targetHit);
                return;
            }
            return;
        }
        hideTutorialLayout(this.tutorials.get(this.currentTutorial), new OnAnimationListener() {
            public final void onEnd() {
                TutorialManager.this.nextTutorial();
            }
        });
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AppCompatDialog(getContext(), getTheme()) {
            public void onBackPressed() {
                TutorialManager.this.onClick((View) null);
            }
        };
    }
}
