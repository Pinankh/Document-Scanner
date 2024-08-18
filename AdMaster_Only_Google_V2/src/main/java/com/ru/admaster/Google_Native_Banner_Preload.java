package com.ru.admaster;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;

import java.util.List;
import java.util.Random;

public class Google_Native_Banner_Preload extends Activity {


    public static LinearLayout adViewBanner;
    private static LinearLayout nativeBannerapp;
    public static AdLoader.Builder builder;
    public static AdLoader adLoader;
    public static NativeAppInstallAd ads;
    public static NativeAppInstallAdView adView;


    public static void PreLoad_Native_Banner(final Activity ctx, final FrameLayout Frame, final LinearLayout App_ad) {
        if (ads != null) {
            try {
                Log.e("ankitasavaliyaBanner: ", "Display");
                Google_Native_Banner(ctx, Frame);
//                Frame.removeAllViews();
                builder = null;
                adLoader = null;
                adView = null;
				ads = null;
                Load_Native_Banner(ctx);

            } catch (Exception e) {

            }
        } else if (adLoader != null && !adLoader.isLoading()) {
            try {
                App_Native_Banner(ctx, App_ad);
                Load_Native_Banner(ctx);
            } catch (Exception e) {

            }
        }
    }

    public static void Load_Native_Banner(final Activity act) {
        builder = new AdLoader.Builder(act, Utils.Google_Native_Banner);

        builder.forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
            @Override
            public void onAppInstallAdLoaded(NativeAppInstallAd ad) {
						ads = ad;

            }
        });


        adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
				Log.e("ankitasavaliyaBanner: ", "fail");
            }
        }).build();


        adLoader.loadAd(new AdRequest.Builder().build());
    }


    public static void Google_Native_Banner(final Activity ctx, final FrameLayout frame) {

        adView = (NativeAppInstallAdView) ctx.getLayoutInflater()
                .inflate(R.layout.google_native_banner, null);
        populateAppInstallAdView(ads, adView);
        frame.removeAllViews();
        frame.addView(adView);

    }


    public static void populateAppInstallAdView(NativeAppInstallAd nativeAppInstallAd, NativeAppInstallAdView adView) {


        adView.setHeadlineView(adView.findViewById(R.id.appinstall_headline));
        adView.setImageView(adView.findViewById(R.id.appinstall_image));
        adView.setBodyView(adView.findViewById(R.id.appinstall_body));
        adView.setCallToActionView(adView.findViewById(R.id.appinstall_call_to_action));
        adView.setIconView(adView.findViewById(R.id.appinstall_app_icon));

        ((TextView) adView.getHeadlineView()).setText(nativeAppInstallAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAppInstallAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAppInstallAd.getCallToAction());
        ((ImageView) adView.getIconView()).setImageDrawable(nativeAppInstallAd.getIcon()
                .getDrawable());

        List<NativeAd.Image> images = nativeAppInstallAd.getImages();

        if (images.size() > 0) {
            ((ImageView) adView.getImageView()).setImageDrawable(images.get(0).getDrawable());
        }


        adView.setNativeAd(nativeAppInstallAd);
    }


    public static void App_Native_Banner(final Activity ctx, final LinearLayout app_small_ads) {

        if (Utils.Big_native != null) {
            if (Utils.Big_native.size() != 0) {
                LayoutInflater inflater = LayoutInflater.from(ctx);
                nativeBannerapp = (LinearLayout) inflater.inflate(R.layout.app_small_ad, app_small_ads, false);
                app_small_ads.addView(nativeBannerapp);

                ImageView app_icon = nativeBannerapp.findViewById(R.id.app_icon);
                TextView app_title = nativeBannerapp.findViewById(R.id.app_title);
                TextView app_des = nativeBannerapp.findViewById(R.id.app_des);
                Button install_now = nativeBannerapp.findViewById(R.id.install_now);

                Random randomGenerator = new Random();
                final int index = randomGenerator.nextInt(Utils.Big_native.size());

                Glide.with(ctx).load(Utils.Big_native.get(index).getNative_icon()).into(app_icon);
                app_title.setText("" + Utils.Big_native.get(index).getNative_title());
                app_des.setText("" + Utils.Big_native.get(index).getNative_desc());

                install_now.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri uri = Uri.parse("" + Utils.Big_native.get(index).getApp_link());
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        try {
                            ctx.startActivity(goToMarket);
                        } catch (ActivityNotFoundException e) {
                            ctx.startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("" + Utils.Big_native.get(index).getApp_link())));
                        }
                    }
                });
            }
        }

    }


}
