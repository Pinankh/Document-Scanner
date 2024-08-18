package com.camscanner.paperscanner.pdfcreator.features.export;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.camscanner.paperscanner.pdfcreator.ads.NativeAdvanceHelper;
import com.google.android.material.tabs.TabLayout;
import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.utils.SharedPrefsUtils;
import com.camscanner.paperscanner.pdfcreator.common.views.stepslider.OnSliderPositionChangeListener;
import com.camscanner.paperscanner.pdfcreator.common.views.stepslider.StepSlider;
import com.camscanner.paperscanner.pdfcreator.features.premium.PremiumHelper;
import com.camscanner.paperscanner.pdfcreator.model.types.Resolution;
import com.camscanner.paperscanner.pdfcreator.view.fragment.BackButtonListener;
import timber.log.Timber;

public class ExportDialogFragment extends AppCompatDialogFragment implements BackButtonListener, TabLayout.BaseOnTabSelectedListener, OnSliderPositionChangeListener {
    private static final int APPEARING_TIME = 250;
    private static final int CLOSING_TIME = 300;
    private static final String TYPE = "export_type";
    View bottomAfter;
    View bottomBefore;
    ImageView btnClose;
    TextView btnExport;
    private int chosenPosition;
    private int chosenTab;
    int colorSelected;
    int colorUnselected;
    ConstraintLayout constraintLayout;
    private Context context;
    FrameLayout dialogRoot;
    private OnDismissDialogListener dismissListener;
    private OnExportDialogListener exportListener;
    String saveTitle;
    String shareTitle;
    private boolean showPremium;
    StepSlider slider;
    private TextView tabImage;
    private TextView tabPdf;
    TabLayout tabs;
    private ExportType type;

    public interface OnDismissDialogListener {
        void onDismissed();
    }

    public interface OnExportDialogListener {
        void onShareClicked(ExportFormat exportFormat);
    }

    private interface OnVisibleListener {
        void onVisible();
    }

    public static ExportDialogFragment newInstance(ExportType type2) {
        ExportDialogFragment fragment = new ExportDialogFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE, type2.value());
        fragment.setArguments(args);
        return fragment;
    }

    public void onAttach(Context context2) {
        super.onAttach(context2);
        this.context = context2;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fmt_export_dialog, container, false);

        bottomAfter = (View)view.findViewById(R.id.bottom_after);
        bottomBefore = (View)view.findViewById(R.id.bottom_before);
        btnClose = (ImageView) view.findViewById(R.id.btn_close);
        btnExport = (TextView) view.findViewById(R.id.btn_export);
        constraintLayout = (ConstraintLayout) view.findViewById(R.id.root);
        dialogRoot = (FrameLayout) view.findViewById(R.id.dialog_root);
        slider = (StepSlider) view.findViewById(R.id.slider_quality);
        tabs = (TabLayout) view.findViewById(R.id.tabs);

        colorSelected = getActivity().getResources().getColor(R.color.colorPrimary);
        colorUnselected = getActivity().getResources().getColor(R.color.colorTextScanner);
        saveTitle = getActivity().getResources().getString(R.string.export_save);
        shareTitle = getActivity().getResources().getString(R.string.export_share);

        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShareClicked();
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        initData();
        initTabs(view);
        initDialog();
        return view;
    }

    private void initData() {
        Bundle args = getArguments();
        this.type = args != null ? ExportType.get(args.getInt(TYPE, ExportType.SAVE.value())) : ExportType.SAVE;
    }

    private void initTabs(View view) {
        this.tabPdf = (TextView) LayoutInflater.from(view.getContext()).inflate(R.layout.tab_share, (ViewGroup) null, false);
        this.tabImage = (TextView) LayoutInflater.from(view.getContext()).inflate(R.layout.tab_share, (ViewGroup) null, false);
        this.tabPdf.setText(getString(R.string.pdf));
        this.tabImage.setText(getString(R.string.image));
        this.tabs.addOnTabSelectedListener(this);
        TabLayout tabLayout = this.tabs;
        tabLayout.addTab(tabLayout.newTab().setCustomView((View) this.tabPdf));
        TabLayout tabLayout2 = this.tabs;
        tabLayout2.addTab(tabLayout2.newTab().setCustomView((View) this.tabImage));
        this.slider.setPosition(SharedPrefsUtils.getOutputSize(this.context).pos());
        this.slider.setOnSliderPositionChangeListener(this);
    }

    private void initDialog() {
        this.btnExport.setText(this.type.equals(ExportType.SAVE) ? this.saveTitle : this.shareTitle);
    }

    public void onResume() {
        super.onResume();
        initializeDialog();
        waitVisibleFor(new OnVisibleListener() {
            public final void onVisible() {
                ExportDialogFragment.this.startOpenAnimation();
            }
        });
    }

    public void onTabSelected(TabLayout.Tab tab) {
        TextView selected = tab.getPosition() == 0 ? this.tabPdf : this.tabImage;
        selected.setTextColor(this.colorSelected);
        selected.setTypeface(selected.getTypeface(), 1);
    }

    public void onTabUnselected(TabLayout.Tab tab) {
        TextView selected = tab.getPosition() == 0 ? this.tabPdf : this.tabImage;
        selected.setTextColor(this.colorUnselected);
        selected.setTypeface((Typeface) null, 0);
    }

    public void onTabReselected(TabLayout.Tab tab) {
    }

    public ExportDialogFragment setExportListener(OnExportDialogListener listener) {
        this.exportListener = listener;
        return this;
    }

    public ExportDialogFragment setDismissListener(OnDismissDialogListener listener) {
        this.dismissListener = listener;
        return this;
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

    private void waitVisibleFor(final OnVisibleListener listener) {
        final Handler waiter = new Handler();
        waiter.postDelayed(new Runnable() {
            public void run() {
                if (ExportDialogFragment.this.isVisible()) {
                    Handler handler = waiter;
                    OnVisibleListener onVisibleListener = listener;
                    onVisibleListener.getClass();
                    handler.postDelayed(new Runnable() {
                        public final void run() {
                            listener.onVisible();
                        }
                    }, 16);
                    return;
                }
                waiter.postDelayed(this, 16);
            }
        }, 16);
    }


    public void startOpenAnimation() {
        TransitionSet set = new TransitionSet();
        ChangeBounds move = new ChangeBounds();
        Fade fade = new Fade(1);
        set.setInterpolator((TimeInterpolator) new FastOutSlowInInterpolator());
        set.addTarget((View) this.dialogRoot);
        set.addTarget((View) this.btnClose);
        set.setDuration(250);
        set.addTransition(move);
        set.addTransition(fade);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this.constraintLayout);
        constraintSet.clear(R.id.dialog_root, 4);
        constraintSet.connect(R.id.dialog_root, 4, R.id.bottom_after, 4, 0);
        TransitionManager.beginDelayedTransition(this.constraintLayout, set);
        constraintSet.applyTo(this.constraintLayout);
        this.dialogRoot.setVisibility(View.VISIBLE);
    }

    private void startCloseAnimation(Transition.TransitionListener listener) {
        TransitionSet set = new TransitionSet();
        ChangeBounds move = new ChangeBounds();
        Fade fade = new Fade(2);
        set.setInterpolator((TimeInterpolator) new FastOutSlowInInterpolator());
        set.addTarget((View) this.dialogRoot);
        set.addTarget((View) this.btnClose);
        set.setDuration(300);
        set.addTransition(move);
        set.addTransition(fade);
        set.addListener(listener);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this.constraintLayout);
        constraintSet.clear(R.id.dialog_root, 4);
        constraintSet.connect(R.id.dialog_root, 4, R.id.bottom_end, 4, 0);
        TransitionManager.beginDelayedTransition(this.constraintLayout, set);
        constraintSet.applyTo(this.constraintLayout);
        this.dialogRoot.setVisibility(4);
        this.btnClose.setVisibility(View.INVISIBLE);
    }

    public void onDestroyView() {
        super.onDestroyView();
    }

    public void onPositionChanged(int currentPosition, boolean commitPosition) {
        if (commitPosition) {
            checkPremiumAndShowDialog(Resolution.get(currentPosition));
        }
    }

    private boolean checkPremiumAndShowDialog(Resolution resolution) {
        if (this.showPremium || !resolution.equals(Resolution.FULL)) {
            return true;
        }
        showPremiumDialogAndActivity();
        return false;
    }

    private void showPremiumDialogAndActivity() {
        this.showPremium = true;
        PremiumHelper.showPremiumAfterAlertDialog(this.context, R.string.best_quality, R.string.best_quality_output_alert, new PremiumHelper.StartActivityController() {
            public final void startActivity(Intent intent, int i) {
                ExportDialogFragment.this.lambda$showPremiumDialogAndActivity$0$ExportDialogFragment(intent, i);
            }
        });
    }

    public  void lambda$showPremiumDialogAndActivity$0$ExportDialogFragment(Intent intent, int requestCode) {
        try {
            startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1012) {
            this.showPremium = false;

                this.slider.setPosition(Resolution.HIGH.pos());

        }
    }

    public void onShareClicked() {
        this.chosenPosition = this.slider.getPosition();
        this.chosenTab = this.tabs.getSelectedTabPosition();
        startCloseAnimation(new Transition.TransitionListener() {
            public void onTransitionStart(@NonNull Transition transition) {
            }

            public void onTransitionEnd(@NonNull Transition transition) {
                ExportDialogFragment.this.shareDocs();
            }

            public void onTransitionCancel(@NonNull Transition transition) {
                ExportDialogFragment.this.shareDocs();
            }

            public void onTransitionPause(@NonNull Transition transition) {
            }

            public void onTransitionResume(@NonNull Transition transition) {
            }
        });
    }


    public void shareDocs() {
        Resolution resolution = Resolution.get(this.chosenPosition);
        if (checkPremiumAndShowDialog(resolution)) {
            SharedPrefsUtils.setOutputSize(this.context, resolution);
        }
        ExportFormat exportFormat = this.chosenTab == 0 ? ExportFormat.PDF : ExportFormat.IMAGES;
        OnExportDialogListener onExportDialogListener = this.exportListener;
        if (onExportDialogListener != null) {
            onExportDialogListener.onShareClicked(exportFormat);
        }
        dismissAllowingStateLoss();
    }

    public void showDialog(FragmentManager fragmentManager) {
        fragmentManager.beginTransaction().add((Fragment) this, "share").commitAllowingStateLoss();
    }


    public void onBackPressed() {
        dismiss();
        OnDismissDialogListener onDismissDialogListener = this.dismissListener;
        if (onDismissDialogListener != null) {
            onDismissDialogListener.onDismissed();
        }
    }
}
