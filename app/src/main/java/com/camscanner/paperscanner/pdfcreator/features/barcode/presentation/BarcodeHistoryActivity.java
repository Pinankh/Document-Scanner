package com.camscanner.paperscanner.pdfcreator.features.barcode.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.p018db.DBManager;
import com.camscanner.paperscanner.pdfcreator.features.barcode.model.QrResult;
import com.camscanner.paperscanner.pdfcreator.view.activity.BaseMainActivity;
import timber.log.Timber;

public class BarcodeHistoryActivity extends BaseMainActivity implements QrHistoryAdapter.QrListListener {
    private static final String LOG_TAG = BarcodeHistoryActivity.class.getSimpleName();
    private QrHistoryAdapter adapter;
    private DBManager dbManager;
    RecyclerView historyList;

    public static void startHistoryActivity(FragmentActivity activity) {
        Intent intent = new Intent(activity, BarcodeHistoryActivity.class);
        intent.addFlags(131072);
        activity.startActivityForResult(intent, 1025);
    }

    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView((int) R.layout.activity_qr_history);
        historyList = (RecyclerView)findViewById(R.id.history);
        ImageView btn_back = (ImageView)findViewById(R.id.btn_back);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        this.dbManager = DBManager.getInstance();
        initList();
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
//        Analytics.get().logQrHistory();
    }

    private void initList() {
        this.adapter = new QrHistoryAdapter(new ArrayList(), this);
        this.historyList.setLayoutManager(new LinearLayoutManager(this));
        this.historyList.setAdapter(this.adapter);
        Disposable subscribe = this.dbManager.getQrHistory().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() {
            public final void accept(Object obj) {
                BarcodeHistoryActivity.this.handleList((List) obj);
            }
        }, new Consumer() {
            public final void accept(Object obj) {
                BarcodeHistoryActivity.this.handleError((Throwable) obj);
            }
        });
    }

    /* access modifiers changed from: private */
    public void handleList(List<QrResult> qrResults) {
        this.adapter.update(qrResults);
    }

    /* access modifiers changed from: private */
    public void handleError(Throwable throwable) {
        Timber.tag(LOG_TAG).e(throwable);
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    public void onQrClicked(QrResult result) {
        BarcodeResultActivity.startResultActivity(this, result);
    }
}
