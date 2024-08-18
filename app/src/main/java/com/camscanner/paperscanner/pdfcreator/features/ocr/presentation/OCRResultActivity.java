package com.camscanner.paperscanner.pdfcreator.features.ocr.presentation;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;


import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewAfterTextChangeEvent;
import com.ortiz.touchview.TouchImageView;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.Constant;
import com.camscanner.paperscanner.pdfcreator.common.p018db.DBManager;
import com.camscanner.paperscanner.pdfcreator.common.utils.KeyboardUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.OcrStorageUtils;
import com.camscanner.paperscanner.pdfcreator.model.Document;
import com.camscanner.paperscanner.pdfcreator.view.activity.BaseMainActivity;
import timber.log.Timber;

public class OCRResultActivity extends BaseMainActivity {
    private static final String LOG_TAG = OCRResultActivity.class.getSimpleName();
    View appbar;
    View btnSearch;
    int colorSearch;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private int currentPage = 0;
    private Document document;
    View footer;
    TouchImageView imagePreview;
    private String lowerText;
    private final Stack<Mode> modesStack = new Stack<>();
    int paddingSearch;
    private int pageHeight;
    private int pages;
    TextView pagesCounter;
    NestedScrollView scrollRoot;
    TextView searchCount;
    EditText searchView;
    private String text;
    LinedTextView textView;
    TextView title;

    enum Mode {
        NONE,
        SEARCH,
        COMPARE
    }

    public static void start(Activity context, Document document2) {
        Intent intent = new Intent(context, OCRResultActivity.class);
        intent.putExtra(Constant.EXTRA_DOCUMENT, document2);
        context.startActivityForResult(intent, Constant.REQUEST_OCR_RESULT);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_ocr_result);

        appbar = (View)findViewById(R.id.appbar);
        btnSearch = (View)findViewById(R.id.btn_search);
        footer = (View)findViewById(R.id.footer);
        imagePreview = (TouchImageView) findViewById(R.id.recognized_image);
        pagesCounter = (TextView) findViewById(R.id.pages_counter);
        searchCount = (TextView) findViewById(R.id.search_count);
        title = (TextView) findViewById(R.id.title);
        searchView = (EditText) findViewById(R.id.edit_search);
        scrollRoot = (NestedScrollView) findViewById(R.id.scroll_root);
        textView = (LinedTextView) findViewById(R.id.text);
        ConstraintLayout  btn_copy = (ConstraintLayout) findViewById(R.id.btn_copy);
        ConstraintLayout  btn_compare = (ConstraintLayout) findViewById(R.id.btn_compare);
        ConstraintLayout  btn_share = (ConstraintLayout) findViewById(R.id.btn_share);
        ConstraintLayout  btn_retake = (ConstraintLayout) findViewById(R.id.btn_retake);
        ImageView  btn_back = (ImageView) findViewById(R.id.btn_back);

        colorSearch = getResources().getColor(R.color.colorPrimary);
        paddingSearch = (int) getResources().getDimension(R.dimen.padding_search_word);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSearchClicked();
            }
        });

        btn_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCopyClicked();
            }
        });
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShareClicked();
            }
        });

        btn_compare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCompareClicked();
            }
        });

        btn_retake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRetakeClicked();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        initData();
        initUI();
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
//        Analytics.get().logOCRResultScreen();
    }

    /* access modifiers changed from: package-private */
    public void initData() {
        this.document = (Document) getIntent().getSerializableExtra(Constant.EXTRA_DOCUMENT);
        this.text = OcrStorageUtils.readText(this.document.textPath);
        this.text = TextUtils.isEmpty(this.text) ? "" : this.text.trim();
        this.lowerText = this.text.toLowerCase();
    }

    private void initUI() {
        this.textView.setText(this.text);
        this.textView.post(new Runnable() {
            public final void run() {
                OCRResultActivity.this.countPages();
            }
        });
        toggleMode(false, Mode.NONE);
        Glide.with((View) this.imagePreview).asBitmap().load(this.document.path).into((ImageView) this.imagePreview);
    }

    /* access modifiers changed from: protected */
    public void onResume() {

        super.onResume();
        this.compositeDisposable.add(RxTextView.afterTextChangeEvents(this.searchView).skipInitialValue().map(new Function<TextViewAfterTextChangeEvent, Object>() {
            @Override
            public Object apply(TextViewAfterTextChangeEvent textViewAfterTextChangeEvent) throws Exception {
                return (textViewAfterTextChangeEvent).view().getText().toString().trim().toLowerCase();
            }
        }).distinctUntilChanged().subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() {
            public final void accept(Object obj) {
                OCRResultActivity.this.updateSearch((String) obj);
            }
        }, new Consumer() {
            public final void accept(Object obj) {
                OCRResultActivity.this.handleError((Throwable) obj);
            }
        }));
    }

    /* access modifiers changed from: private */
    public void countPages() {
        this.pageHeight = this.footer.getTop() - this.appbar.getBottom();
        this.pages = getPageForPosition((float) this.textView.getHeight());
        updatePages();
        this.scrollRoot.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            public final void onScrollChange(NestedScrollView nestedScrollView, int i, int i2, int i3, int i4) {
                OCRResultActivity.this.lambda$countPages$1$OCRResultActivity(nestedScrollView, i, i2, i3, i4);
            }
        });
    }

    public /* synthetic */ void lambda$countPages$1$OCRResultActivity(NestedScrollView nestedScrollView, int i, int i1, int i2, int i3) {
        updatePages();
    }

    private void updatePages() {
        int page = getPageForPosition((float) this.scrollRoot.getScrollY());
        if (page == 0) {
            page = 1;
        } else if (this.scrollRoot.getScrollY() + this.pageHeight + this.paddingSearch >= this.textView.getHeight()) {
            page = this.pages;
        }
        if (page != this.currentPage) {
            this.currentPage = page;
            this.pagesCounter.setText(page + "/" + this.pages);
        }
    }

    private int getPageForPosition(float currentPosition) {
        return (int) Math.ceil((double) (currentPosition / ((float) this.pageHeight)));
    }

    /* access modifiers changed from: private */
    public void updateSearch(String query) {
        if (TextUtils.isEmpty(query)) {
            this.textView.setText(this.text);
            this.searchCount.setText("");
            return;
        }
        List<Integer> indexes = new ArrayList<>();
        int idx = -1;
        do {
            idx = this.lowerText.indexOf(query, idx + 1);
            if (idx != -1) {
                indexes.add(Integer.valueOf(idx));
                continue;
            }
        } while (idx != -1);
        if (indexes.isEmpty()) {
            this.textView.setText(this.text);
            this.searchCount.setText("");
            return;
        }
        SpannableString spanBuilder = new SpannableString(this.text);
        for (Integer intValue : indexes) {
            int index = intValue.intValue();
            spanBuilder.setSpan(new BackgroundColorSpan(this.colorSearch), index, query.length() + index, 33);
        }
        this.searchCount.setText(String.valueOf(indexes.size()));
        this.textView.setText(spanBuilder);
        int startOffsetOfClickedText = indexes.get(0).intValue();
        Layout textViewLayout = this.textView.getLayout();
        int currentLineStartOffset = textViewLayout.getLineForOffset(startOffsetOfClickedText);
        Rect lineRect = new Rect();
        textViewLayout.getLineBounds(currentLineStartOffset, lineRect);
        int scrollY = lineRect.top - this.paddingSearch;
        if (scrollY < 0) {
            scrollY = 0;
        }
        this.scrollRoot.smoothScrollTo(0, scrollY);
    }

    /* access modifiers changed from: private */
    public void handleError(Throwable throwable) {
        Timber.e(throwable);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        this.compositeDisposable.clear();
    }

    private void toggleMode(boolean backAction, Mode mode) {
        if (mode.equals(Mode.NONE)) {
            this.modesStack.clear();
        }
        this.modesStack.push(mode);
        int i = C67781.f14684x948b5b33[mode.ordinal()];
        if (i == 2) {
            this.searchView.setVisibility(0);
            this.searchCount.setVisibility(0);
            this.imagePreview.setVisibility(4);
            this.btnSearch.setVisibility(4);
            this.title.setVisibility(4);
            if (!backAction) {
                KeyboardUtils.showKeyboard(this, this.searchView);
            }
        } else if (i != 3) {
            KeyboardUtils.hideKeyboard(this);
            this.searchView.clearFocus();
            this.searchView.setVisibility(4);
            this.searchView.setText("");
            this.searchCount.setVisibility(4);
            this.btnSearch.setVisibility(0);
            this.imagePreview.setVisibility(4);
            this.title.setVisibility(0);
        } else {
            KeyboardUtils.hideKeyboard(this);
            this.searchView.setVisibility(4);
            this.searchCount.setVisibility(4);
            this.imagePreview.setVisibility(0);
            this.btnSearch.setVisibility(4);
            this.title.setVisibility(4);
        }
    }


    static /* synthetic */ class C67781 {


        static final /* synthetic */ int[] f14684x948b5b33 = new int[Mode.values().length];

        static {
            try {
                f14684x948b5b33[Mode.NONE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f14684x948b5b33[Mode.SEARCH.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f14684x948b5b33[Mode.COMPARE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }


    public void onSearchClicked() {
        toggleMode(false, Mode.SEARCH);
    }

    public void onCopyClicked() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService("clipboard");
        if (clipboard != null) {
            clipboard.setPrimaryClip(ClipData.newPlainText("text", this.text));
            Toast.makeText(getApplicationContext(), getString(R.string.copied), 0).show();
        }
    }

    public void onCompareClicked() {
        if (this.modesStack.isEmpty() || !this.modesStack.peek().equals(Mode.COMPARE)) {
            toggleMode(false, Mode.COMPARE);
            return;
        }
        this.modesStack.pop();
        toggleMode(true, this.modesStack.pop());
    }

    public void onShareClicked() {
        Intent sharingIntent = new Intent("android.intent.action.SEND");
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra("android.intent.extra.SUBJECT", getString(R.string.recognized_text));
        sharingIntent.putExtra("android.intent.extra.TEXT", this.text);
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.ocr_share)));
    }

    public void onRetakeClicked() {
        new AlertDialog.Builder(this).setTitle((CharSequence) getString(R.string.dialog_title_sure)).setMessage((CharSequence) getString(R.string.dialog_message_ocr_cache)).setCancelable(false).setNegativeButton((int) R.string.str_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setPositiveButton((int) R.string.ocr_retake, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                OCRResultActivity.this.lambda$onRetakeClicked$3$OCRResultActivity(dialogInterface, i);
            }
        }).show();
    }

    public /* synthetic */ void lambda$onRetakeClicked$3$OCRResultActivity(DialogInterface dialog, int id) {
        dialog.dismiss();
        this.document.textPath = "";
        DBManager.getInstance().updateDocument(this.document);
        closeScreen(true);
    }

    public void onBackPressed() {
        if (this.modesStack.isEmpty() || this.modesStack.peek().equals(Mode.NONE)) {
            closeScreen(false);
            return;
        }
        this.modesStack.pop();
        toggleMode(true, this.modesStack.pop());
    }

    private void closeScreen(boolean retake) {
        Intent intent = new Intent();
        intent.putExtra(Constant.EXTRA_DOCUMENT, this.document);
        intent.putExtra(Constant.RETAKE_OCR, retake);
        setResult(retake ? -1 : 0, intent);
        finish();
    }
}
