package com.camscanner.paperscanner.pdfcreator.features.appsee;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.utils.PrivacyUtil;

public class AppseeConsentDialogFragment extends AppCompatDialogFragment {
    public static final int AGREE = 1;
    public static final int BUY_PREMIUM = 3;
    public static final int NOT_AGREE = 2;
    private OnButtonClickListener agreeListener;
    private ImageView appIconView;
    private String appTitle;
    private TextView appTitleView;
    private List<Integer> btnsOrder;
    private TextView firstBtn;
    private OnButtonClickListener notAgreeListener;
    private OnButtonClickListener payButtonListener;
    private TextView secondBtn;
    private TextView secondLine;
    private TextView thirdBtn;

    public interface OnButtonClickListener {
        void onClick();
    }

    public static AppseeConsentDialogFragment newInstance() {
        return new AppseeConsentDialogFragment();
    }

    public void showDialog(FragmentActivity activity) {
        activity.getSupportFragmentManager().beginTransaction().add((Fragment) this, "appsee_consent").commitAllowingStateLoss();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fmt_apsee_consent_dialog, container, false);
        findViews(view);
        initializeAppInfo(view.getContext());
        initializeTexts(view.getContext());
        initializeButtons();
        return view;
    }

    private void initializeButtons() {
        if (this.btnsOrder == null) {
            this.btnsOrder = getDefaultBtnsOrder();
        }
        Integer[] order = new Integer[3];
        this.btnsOrder.toArray(order);
        this.firstBtn.setText(getButtonText(order[0].intValue()));
        final OnButtonClickListener firstListener = getButtonListener(order[0].intValue());
        if (firstListener != null) {
            this.firstBtn.setOnClickListener(new View.OnClickListener() {


                public final void onClick(View view) {
                    AppseeConsentDialogFragment.this.lambda$initializeButtons$0$AppseeConsentDialogFragment(firstListener, view);
                }
            });
        } else {
            this.firstBtn.setVisibility(8);
        }
        this.secondBtn.setText(getButtonText(order[1].intValue()));
        final OnButtonClickListener secondListener = getButtonListener(order[1].intValue());
        if (secondListener != null) {
            this.secondBtn.setOnClickListener(new View.OnClickListener() {


                public final void onClick(View view) {
                    AppseeConsentDialogFragment.this.lambda$initializeButtons$1$AppseeConsentDialogFragment(secondListener, view);
                }
            });
        } else {
            this.secondBtn.setVisibility(8);
        }
        this.thirdBtn.setText(getButtonText(order[2].intValue()));
        final OnButtonClickListener thirdListener = getButtonListener(order[2].intValue());
        if (thirdListener != null) {
            this.thirdBtn.setOnClickListener(new View.OnClickListener() {


                public final void onClick(View view) {
                    AppseeConsentDialogFragment.this.lambda$initializeButtons$2$AppseeConsentDialogFragment(thirdListener, view);
                }
            });
        } else {
            this.thirdBtn.setVisibility(8);
        }
    }

    public /* synthetic */ void lambda$initializeButtons$0$AppseeConsentDialogFragment(OnButtonClickListener firstListener, View v) {
        firstListener.onClick();
        dismiss();
    }

    public /* synthetic */ void lambda$initializeButtons$1$AppseeConsentDialogFragment(OnButtonClickListener secondListener, View v) {
        secondListener.onClick();
        dismiss();
    }

    public /* synthetic */ void lambda$initializeButtons$2$AppseeConsentDialogFragment(OnButtonClickListener thirdListener, View v) {
        thirdListener.onClick();
        dismiss();
    }

    private int getButtonText(int type) {
        if (type == 1) {
            return R.string.appsee_ok_btn;
        }
        if (type == 2) {
            return R.string.appsee_no_btn;
        }
        if (type != 3) {
            return -1;
        }
        return R.string.appsee_pay_btn;
    }

    private OnButtonClickListener getButtonListener(int type) {
        if (type == 1) {
            return this.agreeListener;
        }
        if (type == 2) {
            return this.notAgreeListener;
        }
        if (type != 3) {
            return null;
        }
        return this.payButtonListener;
    }

    private void findViews(View view) {
        this.appTitleView = (TextView) view.findViewById(R.id.title);
        this.appIconView = (ImageView) view.findViewById(R.id.logo);
        this.secondLine = (TextView) view.findViewById(R.id.text_second);
        this.firstBtn = (TextView) view.findViewById(R.id.first_button);
        this.secondBtn = (TextView) view.findViewById(R.id.second_button);
        this.thirdBtn = (TextView) view.findViewById(R.id.third_button);
    }

    private void initializeTexts(Context context) {
        String secondStart = context.getString(R.string.appsee_second_line_start);
        String secondText = secondStart + " " + context.getString(R.string.appsee_second_line_end);
        this.secondLine.setText(initializeProvidersLink(secondText, secondStart.length() + 1, secondText.length(), context));
        this.secondLine.setMovementMethod(LinkMovementMethod.getInstance());
        this.secondLine.setHighlightColor(0);
    }

    private void initializeAppInfo(Context context) {
        Context appContext = context.getApplicationContext();
        this.appTitle = DialogUtils.getApplicationName(appContext);
        this.appTitleView.setText(this.appTitle);
        this.appIconView.setImageBitmap(DialogUtils.getApplicationIcon(appContext));
    }

    private Spannable initializeProvidersLink(String text, int start, int end, Context context) {
        SpannableString content = new SpannableString(text);
        content.setSpan(new ClickableSpan() {
            public void onClick(View widget) {
                PrivacyUtil.goToPrivacyPolicy((Fragment) AppseeConsentDialogFragment.this);
            }
        }, start, end, 0);
        content.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorPrimary)), start, end, 0);
        return content;
    }

    public void onResume() {
        super.onResume();
        initializeDialog();
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

    public AppseeConsentDialogFragment setAgreeButtonListener(OnButtonClickListener personalizedListener) {
        this.agreeListener = personalizedListener;
        return this;
    }

    public AppseeConsentDialogFragment setNotAgreeButtonListener(OnButtonClickListener nonPersonalizedListener) {
        this.notAgreeListener = nonPersonalizedListener;
        return this;
    }

    public AppseeConsentDialogFragment setPayButtonListener(OnButtonClickListener payButtonListener2) {
        this.payButtonListener = payButtonListener2;
        return this;
    }

    public AppseeConsentDialogFragment setOrder(int first, int second, int third) {
        this.btnsOrder = new ArrayList();
        this.btnsOrder.add(Integer.valueOf(first));
        this.btnsOrder.add(Integer.valueOf(second));
        this.btnsOrder.add(Integer.valueOf(third));
        if (this.btnsOrder.size() == 3) {
            return this;
        }
        throw new RuntimeException("Invalid order's size");
    }

    private List<Integer> getDefaultBtnsOrder() {
        List<Integer> set = new ArrayList<>();
        set.add(1);
        set.add(2);
        set.add(3);
        return set;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AppCompatDialog(getContext(), getTheme()) {
            public void onBackPressed() {
                AppseeConsentDialogFragment.this.dismiss();
            }
        };
    }
}
