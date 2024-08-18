package com.ru.admaster;

import android.app.Activity;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;

public class Google_Banner extends Activity {

    public static com.google.android.gms.ads.AdView adview_google;

    public static void Load_Google_Banner(Context ctx, RelativeLayout rel) {
        adview_google = new com.google.android.gms.ads.AdView(ctx);
        adview_google.setAdSize(com.google.android.gms.ads.AdSize.SMART_BANNER);
        adview_google.setAdUnitId(Utils.Google_Banner);
        rel.addView(adview_google);
        adview_google.loadAd(new AdRequest.Builder().build());
    }
}
