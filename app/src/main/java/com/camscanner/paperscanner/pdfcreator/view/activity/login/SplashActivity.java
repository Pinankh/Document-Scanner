package com.camscanner.paperscanner.pdfcreator.view.activity.login;

import static com.camscanner.paperscanner.pdfcreator.view.camera.CameraOpenActivity.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.camscanner.paperscanner.pdfcreator.ScanApplication;
import com.camscanner.paperscanner.pdfcreator.ads.InterstitialHelper;
import com.camscanner.paperscanner.pdfcreator.ads.NativeAdvanceHelper;
import com.camscanner.paperscanner.pdfcreator.common.Constant;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;


import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.view.activity.BaseMainActivity;
import com.camscanner.paperscanner.pdfcreator.view.activity.main.MainActivity;


import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends BaseMainActivity {

    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    public static void reStart(Context context) {
        Intent intent = new Intent(context, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setContentView( R.layout.activity_splash );

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        // [START fetch_config_with_callback]
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            boolean updated = task.getResult();
                            Log.d(TAG, "Config params updated: " + updated);
                            String id_value = mFirebaseRemoteConfig.getValue("ad_mode_id").asString();
                            try {
                                JSONObject jsonObject = new JSONObject(id_value);
                                Constant.Google_App_Id = jsonObject.getString("Google_App_Id");
                                Constant.Google_Intertitial_Splash = jsonObject.getString("Google_Intertitial_Splash");
                                Constant.Google_Native = jsonObject.getString("Google_Native");
                                Constant.Google_Native_Banner = jsonObject.getString("Google_Native_Banner");
                                Constant.Google_Intertitial = jsonObject.getString("Google_Intertitial");
                                Constant.Google_Banner = jsonObject.getString("Google_Banner");
                                Constant.Google_App_Open = jsonObject.getString("Google_App_Open");

                                Constant.website = jsonObject.getString("website");
                                Constant.facebook_id = jsonObject.getString("facebook_id");
                                Constant.instagram_id = jsonObject.getString("instagram_id");
                                Constant.github_id = jsonObject.getString("github_id");

                                InterstitialHelper.loadAd(getApplicationContext());
                                ScanApplication.intilizeAddOpen();
                                ScanApplication.loadAdaptiveBanner(getApplicationContext());

                                ((ScanApplication) getApplication())
                                        .showAdIfAvailable(
                                                SplashActivity.this,
                                                new ScanApplication.OnShowAdCompleteListener() {
                                                    @Override
                                                    public void onShowAdComplete() {
                                                        //startMainActivity();
                                                    }
                                                });

                               //NativeAdvanceHelper.loadNativeAds(getApplicationContext());
                                SplashActivity.this.startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                SplashActivity.this.finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                SplashActivity.this.startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                SplashActivity.this.finish();
                            }
                        } else {
                            SplashActivity.this.startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            SplashActivity.this.finish();
                        }

                    }
                });
        // [END fetch_config_with_callback]

    }

}
