package com.ru.admaster;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class Google_Splash extends Activity {

    private static InterstitialAd mInterstitialAd;

    public static void LoadAd(final Context ctx, final Class<?> act) {
        mInterstitialAd = new com.google.android.gms.ads.InterstitialAd(ctx);
        mInterstitialAd.setAdUnitId(Utils.Google_Intertitial_Splash);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                ctx.startActivity(new Intent(ctx, act));
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                ctx.startActivity(new Intent(ctx, act));
            }
        });
    }
}
