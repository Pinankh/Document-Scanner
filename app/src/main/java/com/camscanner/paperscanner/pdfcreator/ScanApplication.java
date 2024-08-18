package com.camscanner.paperscanner.pdfcreator;

import static com.camscanner.paperscanner.pdfcreator.view.camera.CameraOpenActivity.TAG;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.camscanner.paperscanner.pdfcreator.ads.InterstitialHelper;
import com.camscanner.paperscanner.pdfcreator.common.Constant;
import com.camscanner.paperscanner.pdfcreator.view.activity.main.MainActivity;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;


import com.tapscanner.polygondetect.PolygonDetectEngineInterface;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;

import com.camscanner.paperscanner.pdfcreator.common.p018db.DBManager;
import com.camscanner.paperscanner.pdfcreator.common.utils.ImageStorageUtils;
import com.camscanner.paperscanner.pdfcreator.common.utils.LanguageUtil.LocalManageUtil;
import com.camscanner.paperscanner.pdfcreator.common.utils.ProcessUtils;
import com.camscanner.paperscanner.pdfcreator.features.picture.ImageHolder;
import com.camscanner.paperscanner.pdfcreator.features.subscription.SubscriptionManager;
import com.camscanner.paperscanner.pdfcreator.features.subscription.UserRepository;
import com.camscanner.paperscanner.pdfcreator.view.activity.login.SplashActivity;

import java.util.Date;
import java.util.concurrent.Executor;

import timber.log.Timber;

public class ScanApplication extends Application implements Application.ActivityLifecycleCallbacks, LifecycleObserver {
    private static final String LOG_TAG = "papper Scanner";

    private static PolygonDetectEngineInterface gDetectionEngine;
    private static ImageHolder imageHolder;
    private static SubscriptionManager subManager;
    private static UserRepository userRepo;
    private static AdView adView;
    private BaseLoaderCallback mLoaderCallback;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    public static AppOpenAdManager appOpenAdManager;
    private Activity currentActivity;

    private void initDebug() {
    }

    public void onActivityDestroyed(Activity activity) {
    }

    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        LocalManageUtil.getInstance().onConfigurationChanged(getApplicationContext());
    }

    /* access modifiers changed from: protected */
    public void attachBaseContext(Context context) {
        super.attachBaseContext(LocalManageUtil.getInstance().updateLocal(context));
    }

    public void onCreate() {
        super.onCreate();
        this.registerActivityLifecycleCallbacks(this);
        FirebaseApp.initializeApp(this);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);



        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        if (!ProcessUtils.isMetricaProcess(this)) {

                LocalManageUtil.getInstance().setApplicationLanguage(this);
//                initFabric();
                initDebug();
                getDetectionEngine();
                initDB();
//                Analytics.init(this);

               // registerActivityLifecycleCallbacks(this);
//                initCloudSync();
//                initWakeupLib();
//                BillingManager.init(this);
                ImageStorageUtils.clearAllTempFolders();
            }
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        //appOpenAdManager = new AppOpenAdManager();
    }

    public static void intilizeAddOpen()
    {
        appOpenAdManager = new AppOpenAdManager();
    }




    /** LifecycleObserver method that shows the app open ad when the app moves to foreground. */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    protected void onMoveToForeground() {
        // Show the ad (if available) when the app moves to foreground.
        if(appOpenAdManager!=null)
            appOpenAdManager.showAdIfAvailable(currentActivity);
    }

    public void onActivityCreated(Activity activity, Bundle bundle) {
//        Crashlytics.setString("screen_created", activity.getComponentName().getClassName());
        Timber.tag(LOG_TAG).w("onActivityCreated %s", activity);
        if (activity instanceof SplashActivity) {
            this.mLoaderCallback = new BaseLoaderCallback(activity) {
                public void onManagerConnected(int status) {
                    if (status != 0) {
                        super.onManagerConnected(status);
                    }
                }
            };
            if (!OpenCVLoader.initDebug()) {
                OpenCVLoader.initAsync("4.0.1", activity, this.mLoaderCallback);
            } else {
                this.mLoaderCallback.onManagerConnected(0);
            }
        }
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        // An ad activity is started when an ad is showing, which could be AdActivity class from Google
        // SDK or another activity class implemented by a third party mediation partner. Updating the
        // currentActivity only when an ad is not showing will ensure it is not an ad activity, but the
        // one that shows the ad.
        if(appOpenAdManager != null) {
            if (!appOpenAdManager.isShowingAd) {
                currentActivity = activity;
            }
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {}

    @Override
    public void onActivityPaused(@NonNull Activity activity) {}

    @Override
    public void onActivityStopped(@NonNull Activity activity) {}


    /**
     * Shows an app open ad.
     *
     * @param activity the activity that shows the app open ad
     * @param onShowAdCompleteListener the listener to be notified when an app open ad is complete
     */
    public void showAdIfAvailable(
            @NonNull Activity activity,
            @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
        // We wrap the showAdIfAvailable to enforce that other classes only interact with MyApplication
        // class.
        appOpenAdManager.showAdIfAvailable(activity, onShowAdCompleteListener);
    }

    /**
     * Interface definition for a callback to be invoked when an app open ad is complete
     * (i.e. dismissed or fails to show).
     */
    public interface OnShowAdCompleteListener {
        void onShowAdComplete();
    }

    /** Inner class that loads and shows app open ads. */
    private static class AppOpenAdManager {

        private static final String LOG_TAG = "AppOpenAdManager";
        private final String AD_UNIT_ID = Constant.Google_App_Open;

        private AppOpenAd appOpenAd = null;
        private boolean isLoadingAd = false;
        private boolean isShowingAd = false;

        /** Keep track of the time an app open ad is loaded to ensure you don't show an expired ad. */
        private long loadTime = 0;

        /** Constructor. */
        public AppOpenAdManager() {}

        /**
         * Load an ad.
         *
         * @param context the context of the activity that loads the ad
         */
        private void loadAd(Context context) {
            // Do not load ad if there is an unused ad or one is already loading.
            if (isLoadingAd || isAdAvailable()) {
                return;
            }

            isLoadingAd = true;
            AdRequest request = new AdRequest.Builder().build();
            AppOpenAd.load(
                    context,
                    AD_UNIT_ID,
                    request,
                    AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                    new AppOpenAd.AppOpenAdLoadCallback() {
                        /**
                         * Called when an app open ad has loaded.
                         *
                         * @param ad the loaded app open ad.
                         */
                        @Override
                        public void onAdLoaded(AppOpenAd ad) {
                            appOpenAd = ad;
                            isLoadingAd = false;
                            loadTime = (new Date()).getTime();

                            Log.d(LOG_TAG, "onAdLoaded.");
                            //Toast.makeText(context, "onAdLoaded", Toast.LENGTH_SHORT).show();
                        }

                        /**
                         * Called when an app open ad has failed to load.
                         *
                         * @param loadAdError the error.
                         */
                        @Override
                        public void onAdFailedToLoad(LoadAdError loadAdError) {
                            isLoadingAd = false;
                            Log.d(LOG_TAG, "onAdFailedToLoad: " + loadAdError.getMessage());
                            //Toast.makeText(context, "onAdFailedToLoad", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        /** Check if ad was loaded more than n hours ago. */
        private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
            long dateDifference = (new Date()).getTime() - loadTime;
            long numMilliSecondsPerHour = 3600000;
            return (dateDifference < (numMilliSecondsPerHour * numHours));
        }

        /** Check if ad exists and can be shown. */
        private boolean isAdAvailable() {
            // Ad references in the app open beta will time out after four hours, but this time limit
            // may change in future beta versions. For details, see:
            // https://support.google.com/admob/answer/9341964?hl=en
            return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
        }

        /**
         * Show the ad if one isn't already showing.
         *
         * @param activity the activity that shows the app open ad
         */
        private void showAdIfAvailable(@NonNull final Activity activity) {
            showAdIfAvailable(
                    activity,
                    new OnShowAdCompleteListener() {
                        @Override
                        public void onShowAdComplete() {
                            // Empty because the user will go back to the activity that shows the ad.
                        }
                    });
        }

        /**
         * Show the ad if one isn't already showing.
         *
         * @param activity the activity that shows the app open ad
         * @param onShowAdCompleteListener the listener to be notified when an app open ad is complete
         */
        private void showAdIfAvailable(
                @NonNull final Activity activity,
                @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
            // If the app open ad is already showing, do not show the ad again.
            if (isShowingAd) {
                Log.d(LOG_TAG, "The app open ad is already showing.");
                return;
            }

            // If the app open ad is not available yet, invoke the callback then load the ad.
            if (!isAdAvailable()) {
                Log.d(LOG_TAG, "The app open ad is not ready yet.");
                onShowAdCompleteListener.onShowAdComplete();
                loadAd(activity);
                return;
            }

            Log.d(LOG_TAG, "Will show ad.");

            appOpenAd.setFullScreenContentCallback(
                    new FullScreenContentCallback() {
                        /** Called when full screen content is dismissed. */
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Set the reference to null so isAdAvailable() returns false.
                            appOpenAd = null;
                            isShowingAd = false;

                            Log.d(LOG_TAG, "onAdDismissedFullScreenContent.");
                            //Toast.makeText(activity, "onAdDismissedFullScreenContent", Toast.LENGTH_SHORT).show();

                            onShowAdCompleteListener.onShowAdComplete();
                            loadAd(activity);
                        }

                        /** Called when fullscreen content failed to show. */
                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            appOpenAd = null;
                            isShowingAd = false;

                            Log.d(LOG_TAG, "onAdFailedToShowFullScreenContent: " + adError.getMessage());
                            //Toast.makeText(activity, "onAdFailedToShowFullScreenContent", Toast.LENGTH_SHORT).show();

                            onShowAdCompleteListener.onShowAdComplete();
                            loadAd(activity);
                        }

                        /** Called when fullscreen content is shown. */
                        @Override
                        public void onAdShowedFullScreenContent() {
                            Log.d(LOG_TAG, "onAdShowedFullScreenContent.");
                            //Toast.makeText(activity, "onAdShowedFullScreenContent", Toast.LENGTH_SHORT).show();
                        }
                    });

            isShowingAd = true;
            appOpenAd.show(activity);
        }
    }



    public static void loadAdaptiveBanner(Context context) {

        adView = new AdView(context.getApplicationContext());
        adView.setAdUnitId( Constant.Google_Banner);
//        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        AdRequest adRequest = new AdRequest.Builder().build();


        AdSize adSize = getAdSize();
        adView.setAdSize(adSize);

        adView.loadAd(adRequest);
    }


    public void adaptiveBannerView(LinearLayout layAd) {

        if (adView.getParent() != null) {
            ViewGroup tempVg = (ViewGroup) adView.getParent();
            tempVg.removeView(adView);
        }
        layAd.addView(adView);
    }
    private static AdSize getAdSize() {
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);

        DisplayMetrics dm = adView.getContext().getResources().getDisplayMetrics();
        float widthPixels = dm.widthPixels;
        float density = dm.density;
        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(adView.getContext(), adWidth);
    }

    private void initDB() {
        DBManager.init(getApplicationContext());
    }

    public static PolygonDetectEngineInterface getDetectionEngine() {
        if (gDetectionEngine == null) {
            gDetectionEngine = new PolygonDetectEngineInterface();
            gDetectionEngine.CreateEngine();
        }
        return gDetectionEngine;
    }

    public static ImageHolder imageHolder() {
        if (imageHolder == null) {
            imageHolder = ImageHolder.get();
        }
        return imageHolder;
    }

    public static UserRepository userRepo() {
        if (userRepo == null) {
            userRepo = UserRepository.get();
        }
        return userRepo;
    }
}
