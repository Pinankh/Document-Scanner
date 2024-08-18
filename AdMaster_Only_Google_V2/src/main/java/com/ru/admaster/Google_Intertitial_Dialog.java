package com.ru.admaster;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.AnimationDrawable;
import android.view.Window;
import android.widget.ImageView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class Google_Intertitial_Dialog extends Activity {

    public static InterstitialAd mInterstitialAd;
    public static boolean close = false;
    public static boolean Load = false;

    public static void AdLoad_Dialog(Activity act) {
        final Dialog AdLoadDialog = new Dialog(act, R.style.UserDialog);
        AdLoadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        AdLoadDialog.setCancelable(false);
        AdLoadDialog.setContentView(R.layout.ad_load_dialog);

        AdLoadDialog.show();
        mInterstitialAd = new com.google.android.gms.ads.InterstitialAd(act);
        mInterstitialAd.setAdUnitId(Utils.Google_Intertitial);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                    close = false;
                    Load = true;
                    AdLoadDialog.dismiss();
                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                close = true;
                Load = true;
                AdLoadDialog.dismiss();
            }

            @Override
            public void onAdOpened() {
            }

            @Override
            public void onAdLeftApplication() {
            }

            @Override
            public void onAdClosed() {
                close = true;
            }
        });
    }

}
