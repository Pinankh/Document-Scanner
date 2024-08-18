package com.camscanner.paperscanner.pdfcreator.features.ocr.presentation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewAfterTextChangeEvent;
import com.transitionseverywhere.TransitionManager;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.Constant;
import com.camscanner.paperscanner.pdfcreator.common.utils.KeyboardUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.LanguageUtil.LocalManageUtil;
import com.camscanner.paperscanner.pdfcreator.common.utils.NetworkUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.SharedPrefsUtils;
import com.camscanner.paperscanner.pdfcreator.features.ocr.OcrHelper;
import com.camscanner.paperscanner.pdfcreator.features.ocr.model.OCRLanguage;
import com.camscanner.paperscanner.pdfcreator.features.ocr.model.OCRResult;
import com.camscanner.paperscanner.pdfcreator.features.ocr.model.SearchResult;
import com.camscanner.paperscanner.pdfcreator.model.Document;
import com.camscanner.paperscanner.pdfcreator.view.activity.BaseMainActivity;
import timber.log.Timber;

public class OCRActivity extends BaseMainActivity implements LanguageAdapter.OnLanguageListener {
    private static final String LOG_TAG = OCRActivity.class.getSimpleName();
    private LanguageAdapter adapter;
    Drawable backgroundListClosed;
    Drawable backgroundListOpened;
    ImageView btnMany;
    ImageView btnOne;
    TextView btnProcess;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Columns currentColumns;
    private Document document;
    private boolean focusedNow;
    Drawable icMany;
    Drawable icManySelected;
    Drawable icOne;
    Drawable icOneSelected;
    private List<OCRLanguage> languageList;
    RecyclerView list;
    private NetworkUtils networkUtils;
    ConstraintLayout root;
    EditText searchLanguage;

    enum Columns {
        ONE,
        MANY
    }

    public static void start(Activity context, Document document2) {
        Intent intent = new Intent(context, OCRActivity.class);
        intent.putExtra(Constant.EXTRA_DOCUMENT, document2);
        context.startActivityForResult(intent, 1021);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_ocr);

        backgroundListClosed = getResources().getDrawable(R.drawable.background_ocr_language_closed);
        backgroundListOpened = getResources().getDrawable(R.drawable.background_ocr_language_opened);
        icMany = getResources().getDrawable(R.drawable.ic_ocr_many);
        icManySelected = getResources().getDrawable(R.drawable.ic_ocr_many_selected);
        icOne = getResources().getDrawable(R.drawable.ic_ocr_one);
        icOneSelected = getResources().getDrawable(R.drawable.ic_ocr_one_selected);

        btnMany = (ImageView)findViewById(R.id.btn_many_columns);
        btnOne = (ImageView)findViewById(R.id.btn_one_column);
      ImageView  btn_open = (ImageView)findViewById(R.id.btn_open);
        btnProcess = (TextView)findViewById(R.id.btn_process);
        ImageView  btn_back = (ImageView)findViewById(R.id.btn_back);
        list = (RecyclerView) findViewById(R.id.list);
        root = (ConstraintLayout) findViewById(R.id.root);
        searchLanguage = (EditText) findViewById(R.id.language);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        btnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOneColumnClicked();
            }
        });
        btnMany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onManyColumnsClicked();
            }
        });

        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onProcessClicked();
            }
        });

        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOpenClicked();
            }
        });

        if (!initData()) {
            closeScreen(false);
        } else {
            initUI();
        }
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
//        Analytics.get().logPreOCRScreen();
    }

    private boolean initData() {
        this.document = (Document) getIntent().getSerializableExtra(Constant.EXTRA_DOCUMENT);
        Document document2 = this.document;
        if (document2 != null && !new File(document2.path).exists()) {
            return false;
        }
        this.languageList = OcrHelper.getLanguages();
        Collections.sort(this.languageList, new Comparator<OCRLanguage>() {
            @Override
            public int compare(OCRLanguage ocrLanguage, OCRLanguage t1) {
                return ((OCRLanguage) ocrLanguage).language.compareToIgnoreCase(((OCRLanguage) t1).language);
            }
        });
        this.networkUtils = new NetworkUtils(getApplicationContext());
        selectColumns(Columns.ONE);
        return true;
    }

    private void initDefaultLanguage() {
        String savedCode = SharedPrefsUtils.getOCRLanguage(this);
        if (savedCode.equalsIgnoreCase(SharedPrefsUtils.OCR_SYSTEM_LANGUAGE)) {
            try {
                savedCode = LocalManageUtil.getInstance().getLanguageLocale(this).getISO3Language();
            } catch (Exception e) {
                handleError(e);
            }
        }
        OCRLanguage initLanguage = null;
        if (!TextUtils.isEmpty(savedCode) && !savedCode.equalsIgnoreCase(SharedPrefsUtils.OCR_SYSTEM_LANGUAGE)) {
            initLanguage = findLanguage(savedCode);
        }
        if (initLanguage == null) {
            initLanguage = findLanguage(Constant.LANG_ENG_SHORT);
        }
        if (initLanguage != null) {
            select(initLanguage);
            updateSearchView();
        }
    }

    private OCRLanguage findLanguage(String savedCode) {
        for (OCRLanguage language : this.languageList) {
            if (language.code.equalsIgnoreCase(savedCode)) {
                return language;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void initUI() {
        if (this.document == null) {
            this.btnProcess.setText(R.string.save_ocr_language);
        }
        this.adapter = new LanguageAdapter(this, this.languageList);
        initDefaultLanguage();
        this.list.setLayoutManager(new LinearLayoutManager(this));
        this.list.setAdapter(this.adapter);
    }

    /* access modifiers changed from: protected */
    public void onResume() {

        super.onResume();
        this.compositeDisposable.add(Observable.combineLatest(Observable.just(this.languageList), RxTextView.afterTextChangeEvents(this.searchLanguage).skipInitialValue().map(new Function<TextViewAfterTextChangeEvent, Object>() {
            @Override
            public Object apply(TextViewAfterTextChangeEvent textViewAfterTextChangeEvent) throws Exception {
                return (textViewAfterTextChangeEvent).view().getText().toString().trim().toLowerCase();
            }
        }).distinctUntilChanged(), new BiFunction() {
            public final Object apply(Object obj, Object obj2) {
                return OCRActivity.this.sortLanguages((List) obj, (String) obj2);
            }
        }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() {
            public final void accept(Object obj) {
                OCRActivity.this.updateSearch((SearchResult) obj);
            }
        }, new Consumer() {
            public final void accept(Object obj) {
                OCRActivity.this.handleError((Throwable) obj);
            }
        }));
        this.compositeDisposable.add(RxView.focusChanges(this.searchLanguage).distinctUntilChanged().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() {
            public final void accept(Object obj) {
                OCRActivity.this.updatedFocus((Boolean) obj);
            }
        }, new Consumer() {
            public final void accept(Object obj) {
                OCRActivity.this.handleError((Throwable) obj);
            }
        }));
        if (!SharedPrefsUtils.isOCRSelectDialogShown(this)) {
            OcrSelectLanguageDialogFragment.newInstance().showDialog(this);
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        this.compositeDisposable.clear();
    }

    /* access modifiers changed from: private */
    public SearchResult sortLanguages(List<OCRLanguage> list2, String query) {
        if (TextUtils.isEmpty(query)) {
            return new SearchResult(list2, query);
        }
        List<OCRLanguage> newList = new ArrayList<>();
        for (OCRLanguage language : list2) {
            if (language.lowerLanguage.startsWith(query)) {
                newList.add(language);
            }
        }
        return new SearchResult(newList, query);
    }

    /* access modifiers changed from: private */
    public void updateSearch(SearchResult result) {
        this.adapter.setQuery(result.query);
        this.adapter.update(result.results);
    }

    /* access modifiers changed from: private */
    public void updatedFocus(Boolean focused) {
        this.focusedNow = focused.booleanValue();
        if (focused.booleanValue()) {
            this.searchLanguage.setText("");
            TransitionManager.endTransitions(this.root);
            TransitionManager.beginDelayedTransition(this.root);
            this.list.setVisibility(0);
            this.searchLanguage.setBackground(this.backgroundListOpened);
            return;
        }
        this.list.setVisibility(4);
        this.searchLanguage.setBackground(this.backgroundListClosed);
        updateSearchView();
    }

    private void updateSearchView() {
        if (this.adapter.getSelected() != null) {
            this.searchLanguage.setText(this.adapter.getSelected().language);
            this.searchLanguage.setSelection(this.adapter.getSelected().language.length());
            return;
        }
        this.searchLanguage.setText("");
    }

    public void onLanguageClicked(OCRLanguage language) {
        select(language);
        closeInput(false);
    }

    private OCRLanguage findLanguage(String lang, List<OCRLanguage> sortedList) {
        if (TextUtils.isEmpty(lang)) {
            return null;
        }
        for (OCRLanguage language : sortedList) {
            if (language.language.equalsIgnoreCase(lang)) {
                return language;
            }
        }
        return null;
    }

    /* access modifiers changed from: private */
    public void handleError(Throwable throwable) {
        Timber.e(throwable);
    }

    public void onOneColumnClicked() {
        selectColumns(Columns.ONE);
    }


    public void onManyColumnsClicked() {
        selectColumns(Columns.MANY);
    }


    public void onProcessClicked() {
        closeInput(true);
        SharedPrefsUtils.setOCRLanguage(this, this.adapter.getSelected().code);
        String lang = SharedPrefsUtils.getOCRLanguage(this);
        if (TextUtils.isEmpty(lang) || lang.equals(SharedPrefsUtils.OCR_SYSTEM_LANGUAGE)) {
            openLanguageList();
        } else if (this.document == null) {
            closeScreen(true);
        } /*else if (  SharedPrefsUtils.getOcrCount(this) >= 5) {
//            Analytics.get().logPremiumFeature(AnalyticsConstants.PARAM_VALUE_PREMIUM_FEATURE_5_OCR);
            PremiumHelper.showPremiumAfterAlertDialog(this, R.string.alert_premium_ocr_title, R.string.alert_premium_ocr_message, new PremiumHelper.StartActivityController() {
                public final void startActivity(Intent intent, int i) {
                    OCRActivity.this.startActivityForResult(intent, i);
                }
            });
        }*/
        else if (this.networkUtils.isNetworkAvailable()) {
            startProcessing();
        } else {
            Toast.makeText(this, getString(R.string.network_try_later), 0).show();
        }
    }

    /* access modifiers changed from: private */
    public void goToOCRResult(OCRResult ocrResult) {
        OCRResultActivity.start(this, this.document);
    }

    public void onOpenClicked() {
        if (this.focusedNow) {
            closeInput(true);
        } else {
            openLanguageList();
        }
    }

    /* access modifiers changed from: private */
    public void openLanguageList() {
        KeyboardUtils.showKeyboard(this, this.searchLanguage);
    }

    private void selectColumns(Columns columns) {
        if (!columns.equals(this.currentColumns)) {
            this.currentColumns = columns;
            if (this.currentColumns.equals(Columns.ONE)) {
                this.btnOne.setImageDrawable(this.icOneSelected);
                this.btnMany.setImageDrawable(this.icMany);
                return;
            }
            this.btnOne.setImageDrawable(this.icOne);
            this.btnMany.setImageDrawable(this.icManySelected);
        }
    }

    private void startProcessing() {
        OcrHelper.ocrProcess(this, this.document, this.currentColumns.equals(Columns.MANY), new OcrHelper.OcrListener() {
            public void onPreCall() {
                OCRActivity oCRActivity = OCRActivity.this;
                oCRActivity.showProgressDialog(oCRActivity.getResources().getText(R.string.ocr_process).toString());
            }

            public void onSuccess(final OCRResult result) {
                OCRActivity.this.runOnUiThread(new Runnable() {
                    public final void run() {
                        lambda$onSuccess$0$OCRActivity$1(result);
                    }
                });
            }

            public /* synthetic */ void lambda$onSuccess$0$OCRActivity$1(OCRResult result) {
                OCRActivity.this.hideProgressDialog();
                OCRActivity.this.goToOCRResult(result);
            }

            public void onFailure(final Throwable error) {
                OCRActivity.this.runOnUiThread(new Runnable() {


                    public final void run() {
                       lambda$onFailure$1$OCRActivity$1(error);
                    }
                });
            }

            public /* synthetic */ void lambda$onFailure$1$OCRActivity$1(Throwable error) {
                OCRActivity.this.hideProgressDialog();
                OCRActivity.this.showFailedDialog();
            }
        });
    }

    /* access modifiers changed from: private */
    public void showFailedDialog() {
        OcrFailedLanguageDialogFragment.newInstance().setListener(new OcrFailedLanguageDialogFragment.OnFailedOcrListener() {
            public void onChangeLanguage() {
                OCRActivity.this.openLanguageList();
            }

            public void onCancel() {
                OCRActivity.this.closeScreen(false);
            }
        }).showDialog(this);
    }


    public void onBackPressed() {
        if (this.focusedNow) {
            closeInput(true);
        } else {
            closeScreen(false);
        }
    }

    private void closeInput(boolean reselect) {
        OCRLanguage language;
        if (reselect && (language = findLanguage(this.searchLanguage.getText().toString(), this.adapter.getSortedList())) != null) {
            select(language);
        }
        KeyboardUtils.hideKeyboard(this);
        this.searchLanguage.clearFocus();
    }

    private void select(OCRLanguage language) {
        this.adapter.setSelected(language);
        this.btnProcess.setEnabled(true);
        this.btnProcess.setBackgroundResource(R.drawable.background_button_primary);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1022) {
            if (data == null || !data.getBooleanExtra(Constant.RETAKE_OCR, false)) {
                closeScreen(false);
            } else {
                this.document = data.hasExtra(Constant.EXTRA_DOCUMENT) ? (Document) data.getSerializableExtra(Constant.EXTRA_DOCUMENT) : this.document;
            }
        } else if (requestCode != 1012) {
            super.onActivityResult(requestCode, resultCode, data);
        }
//        else if (ScanApplication.userRepo().isUserPremium(this)) {
//            onProcessClicked();
//        }
    }

    /* access modifiers changed from: private */
    public void closeScreen(boolean success) {
        Intent intent = new Intent();
        intent.putExtra(Constant.EXTRA_DOCUMENT, this.document);
        setResult(success ? -1 : 0, intent);
        finish();
    }
}
