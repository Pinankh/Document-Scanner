package com.camscanner.paperscanner.pdfcreator.features.subscription;

import android.app.Activity;
import android.content.Intent;
import io.reactivex.Observable;
import io.reactivex.Single;

public interface SubscriptionManager {
    boolean handleOnActivityResult(int i, int i2, Intent intent);

    Observable<InitializationResponse> observeInitialization();

    Single<SubResponse> requestSubscribe(SubType subType, Activity activity, String str);

    Single<Boolean> restore(SubType subType);
}
