package com.ru.admaster;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
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
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Google_Native extends Activity {


    public static LinearLayout adView1;
    private static LinearLayout adapp;


    public static void Load_Google_Native(final Activity ctx, final FrameLayout nativeAdContainer, final LinearLayout App_ad) {

        AdLoader.Builder builder = new AdLoader.Builder( ctx, Utils.Google_Native );
        builder.forUnifiedNativeAd( new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                UnifiedNativeAdView adView = (UnifiedNativeAdView) ctx.getLayoutInflater().inflate( R.layout.google_native, null );
                populateUnifiedNativeAdView( unifiedNativeAd, adView );
                nativeAdContainer.removeAllViews();
                nativeAdContainer.addView( adView );
            }
        } );


        AdLoader adLoader = builder.withAdListener( new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                App_Native( ctx, App_ad );
            }
        } ).build();

        adLoader.loadAd( new AdRequest.Builder().build() );
    }

    public static void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        VideoController vc = nativeAd.getVideoController();

        vc.setVideoLifecycleCallbacks( new VideoController.VideoLifecycleCallbacks() {
            public void onVideoEnd() {
                super.onVideoEnd();
            }
        } );

        MediaView mediaView = adView.findViewById( R.id.ad_media );
        ImageView mainImageView = adView.findViewById( R.id.ad_image );

        if (vc.hasVideoContent()) {
            adView.setMediaView( mediaView );
            mainImageView.setVisibility( View.GONE );
        } else {
            adView.setImageView( mainImageView );
            mediaView.setVisibility( View.GONE );

// At least one image is guaranteed.
            List<com.google.android.gms.ads.formats.NativeAd.Image> images = nativeAd.getImages();
            try {
                mainImageView.setImageDrawable( images.get( 0 ).getDrawable() );
            } catch (Exception e) {

            }


        }

        adView.setHeadlineView( adView.findViewById( R.id.ad_headline ) );
        adView.setBodyView( adView.findViewById( R.id.ad_body ) );
        adView.setCallToActionView( adView.findViewById( R.id.ad_call_to_action ) );
        adView.setIconView( adView.findViewById( R.id.ad_app_icon ) );
        adView.setPriceView( adView.findViewById( R.id.ad_price ) );
        adView.setStoreView( adView.findViewById( R.id.ad_store ) );
// adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        ((TextView) adView.getHeadlineView()).setText( nativeAd.getHeadline() );
        ((TextView) adView.getBodyView()).setText( nativeAd.getBody() );
        ((Button) adView.getCallToActionView()).setText( nativeAd.getCallToAction() );

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility( View.GONE );
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable() );
            adView.getIconView().setVisibility( View.VISIBLE );
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility( View.INVISIBLE );
        } else {
            adView.getPriceView().setVisibility( View.VISIBLE );
            ((TextView) adView.getPriceView()).setText( nativeAd.getPrice() );
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility( View.INVISIBLE );
        } else {
            adView.getStoreView().setVisibility( View.VISIBLE );
            ((TextView) adView.getStoreView()).setText( nativeAd.getStore() );
        }


        adView.setNativeAd( nativeAd );
    }




    public static void App_Native(final Activity ctx, final LinearLayout app_big_ads) {

        if (Utils.Big_native != null) {
            if (Utils.Big_native.size() != 0) {
                LayoutInflater inflater = LayoutInflater.from( ctx );
                adapp = (LinearLayout) inflater.inflate( R.layout.app_big_ad, app_big_ads, false );
                app_big_ads.addView( adapp );

                ImageView image_banner = adapp.findViewById( R.id.image_banner );
                ImageView ad_app_icon = adapp.findViewById( R.id.ad_app_icon );
                TextView ad_title = adapp.findViewById( R.id.ad_title );
                TextView ad_des = adapp.findViewById( R.id.ad_des );
                Button install_now = adapp.findViewById( R.id.install_now );

                Random randomGenerator = new Random();
                final int index = randomGenerator.nextInt( Utils.Big_native.size() );

                Glide.with( ctx ).load( Utils.Big_native.get( index ).getNative_banner() ).into( image_banner );
                Glide.with( ctx ).load( Utils.Big_native.get( index ).getNative_icon() ).into( ad_app_icon );
                ad_title.setText( "" + Utils.Big_native.get( index ).getNative_title() );
                ad_des.setText( "" + Utils.Big_native.get( index ).getNative_desc() );

                install_now.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri uri = Uri.parse( "" + Utils.Big_native.get( index ).getApp_link() );
                        Intent goToMarket = new Intent( Intent.ACTION_VIEW, uri );
                        goToMarket.addFlags( Intent.FLAG_ACTIVITY_NO_HISTORY |
                                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK );
                        try {
                            ctx.startActivity( goToMarket );
                        } catch (ActivityNotFoundException e) {
                            ctx.startActivity( new Intent( Intent.ACTION_VIEW,
                                    Uri.parse( "" + Utils.Big_native.get( index ).getApp_link() ) ) );
                        }
                    }
                } );
            }
        }


    }





}
