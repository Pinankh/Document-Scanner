package com.camscanner.paperscanner.pdfcreator.ads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.camscanner.paperscanner.pdfcreator.common.Constant;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class InterstitialHelper extends  AppCompatActivity{

    private static InterstitialAd interstitialAd;
    public static boolean isLoaded = false;
    private static InterstialListner interstialListner;

    public static void loadAd(Context context) {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                context,
                Constant.Google_Intertitial,
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        isLoaded = true;
                        InterstitialHelper.interstitialAd = interstitialAd;
                        Log.i("InterstitalHelper", "onAdLoaded");
                        //Toast.makeText(context, "onAdLoaded()", Toast.LENGTH_SHORT).show();
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.

                                        isLoaded = false;
                                        InterstitialHelper.interstialListner.onAddClose();
                                        InterstitialHelper.interstitialAd = null;
                                        InterstitialHelper.interstialListner = null;
                                        Log.d("TAG", "The ad was dismissed.");
                                        loadAd(context);
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        InterstitialHelper.interstitialAd = null;
                                        InterstitialHelper.interstialListner = null;
                                        Log.d("TAG", "The ad failed to show.");
                                        loadAd(context);
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                        Log.d("TAG", "The ad was shown.");
                                    }
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("InterstitalHelper", loadAdError.getMessage());
                        interstitialAd = null;
                        InterstitialHelper.interstialListner = null;
                        /*String error =
                                String.format(
                                        "domain: %s, code: %d, message: %s",
                                        loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());
                        Toast.makeText(
                                context, "onAdFailedToLoad() with error: " + error, Toast.LENGTH_SHORT)
                                .show();*/
                    }
                });
    }

    public static void showInterstitial(Activity activity,InterstialListner interstialListner) {

        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (interstitialAd != null) {
            InterstitialHelper.interstialListner = interstialListner;
            interstitialAd.show(activity);
        } else {
            //Toast.makeText(activity, "Ad did not load", Toast.LENGTH_SHORT).show();

        }
    }

}
