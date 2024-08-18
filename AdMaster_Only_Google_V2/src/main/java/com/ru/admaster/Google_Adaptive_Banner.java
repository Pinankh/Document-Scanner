package com.ru.admaster;

import android.app.Activity;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class Google_Adaptive_Banner extends Activity {

    public static com.google.android.gms.ads.AdView adview_google;

    public static void Load_Google_Adaptive_Banner(Context ctx, RelativeLayout rel) {

        adview_google = new com.google.android.gms.ads.AdView(ctx);
        adview_google.setAdUnitId(Utils.Google_Banner);
        rel.addView(adview_google);

        com.google.android.gms.ads.AdRequest adRequest =
                new com.google.android.gms.ads.AdRequest.Builder().build();

        com.google.android.gms.ads.AdSize adSize = getAdSize(ctx);
        adview_google.setAdSize(adSize);

        adview_google.loadAd(adRequest);

    }

    public static com.google.android.gms.ads.AdSize getAdSize(Context ctx) {


        float widthPixels = Utils.wi;
        float density = Utils.hg;

        int adWidth = (int) (widthPixels / density);

        return com.google.android.gms.ads.AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(ctx, adWidth);
    }
}
