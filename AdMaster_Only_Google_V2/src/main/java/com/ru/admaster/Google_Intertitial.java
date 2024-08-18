package com.ru.admaster;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.adsInterstitialAd;
import com.google.android.gms.ads.LoadAdError;

public class Google_Intertitial extends Activity {

    private static InterstitialAd mInterstitialAd;
    public static boolean close = false;
    public static boolean Load = false;

    public static void Show_Intertitial_Ad(final Activity ctx) {
        Load = false;
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            Load = true;
            close = false;



            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();

                }

                @Override
                public void onAdFailedToLoad(LoadAdError adError) {
                    super.onAdFailedToLoad(adError);
                    close = true;


                }

                @Override
                public void onAdOpened() {

                }

                @Override
                public void onAdClicked() {

                }

                @Override
                public void onAdLeftApplication() {

                }

                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    close = true;
                    Load_Google_Intertitial(ctx);

                }
            });


        } else {
            Load_Google_Intertitial(ctx);
        }
    }

    public static void Load_Google_Intertitial(Context ctx) {
        mInterstitialAd = new com.google.android.gms.ads.InterstitialAd(ctx);
        mInterstitialAd.setAdUnitId(Utils.Google_Intertitial);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Utils.AdLoad = true;
            }
        });
    }
}
