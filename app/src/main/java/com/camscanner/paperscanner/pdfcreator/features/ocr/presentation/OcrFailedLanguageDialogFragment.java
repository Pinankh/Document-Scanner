package com.camscanner.paperscanner.pdfcreator.features.ocr.presentation;

import android.animation.TimeInterpolator;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;
import com.camscanner.paperscanner.pdfcreator.R;

public class OcrFailedLanguageDialogFragment extends AppCompatDialogFragment {
    private boolean animate = true;
    CardView dialogRoot;
    private OnFailedOcrListener listener;
    ConstraintLayout root;

    public interface OnFailedOcrListener {
        void onCancel();

        void onChangeLanguage();
    }

    private interface OnVisibleListener {
        void onVisible();
    }

    public static OcrFailedLanguageDialogFragment newInstance() {
        return new OcrFailedLanguageDialogFragment();
    }

    public void showDialog(FragmentActivity activity) {
        activity.getSupportFragmentManager().beginTransaction().add((Fragment) this, "failed_language").commitAllowingStateLoss();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dlg_ocr_failed_language, container, false);

        dialogRoot = (CardView)view.findViewById(R.id.dialog_root);
        root = (ConstraintLayout) view.findViewById(R.id.root);
        TextView btn_ok = (TextView) view.findViewById(R.id.btn_ok);
        TextView btn_cancel = (TextView) view.findViewById(R.id.btn_cancel);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChangeLanguageClick();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCancelClick();
            }
        });

        return view;
    }

    public void onResume() {
        super.onResume();
        initializeDialog();
        waitVisibleFor(new OnVisibleListener() {
            public final void onVisible() {
                OcrFailedLanguageDialogFragment.this.startOpenAnimation();
            }
        });
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
                if (OcrFailedLanguageDialogFragment.this.isVisible()) {
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

    public OcrFailedLanguageDialogFragment setListener(OnFailedOcrListener listener2) {
        this.listener = listener2;
        return this;
    }

    /* access modifiers changed from: private */
    public void startOpenAnimation() {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this.root);
        constraintSet.clear(R.id.dialog_root, 3);
        constraintSet.connect(R.id.dialog_root, 3, 0, 3, 0);
        if (this.animate) {
            TransitionSet set = new TransitionSet();
            ChangeBounds move = new ChangeBounds();
            Fade fade = new Fade(1);
            set.setInterpolator((TimeInterpolator) new FastOutSlowInInterpolator());
            set.addTarget((View) this.dialogRoot);
            set.setDuration(250);
            set.addTransition(move);
            set.addTransition(fade);
            TransitionManager.beginDelayedTransition(this.root, set);
        }
        constraintSet.applyTo(this.root);
        this.dialogRoot.setVisibility(0);
        this.animate = false;
    }

    public void onChangeLanguageClick() {
        dismissAllowingStateLoss();
        OnFailedOcrListener onFailedOcrListener = this.listener;
        if (onFailedOcrListener != null) {
            onFailedOcrListener.onChangeLanguage();
        }
    }

    public void onCancelClick() {
        dismissAllowingStateLoss();
        OnFailedOcrListener onFailedOcrListener = this.listener;
        if (onFailedOcrListener != null) {
            onFailedOcrListener.onCancel();
        }
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AppCompatDialog(getContext(), getTheme()) {
            public void onBackPressed() {
                OcrFailedLanguageDialogFragment.this.onChangeLanguageClick();
            }
        };
    }
}
