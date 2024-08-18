package com.ru.admaster;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Get_All_ID extends AppCompatActivity {

    public static Dialog dialogf;
    public static NetworkChangeReceiver brd;
    public static boolean check;
    public static String pname;

    public static boolean isOnline(Context ctx) {
        NetworkInfo netInfo = ((ConnectivityManager) ctx.getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (netInfo == null || !netInfo.isConnected()) {
            return false;
        }
        return true;
    }

    public static void LoadAppData_Application(final Context ctx, final String str) {

        pname = str;
        if (isOnline(ctx)) {
            LoadDataFromUri(ctx, str);
        }
    }

    public static void LoadData_Activity(final Activity ctx, final String str) {

        pname = str;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ctx.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Utils.wi = displayMetrics.widthPixels;
        Utils.hg = displayMetrics.density;


        if (isOnline(ctx)) {
            LoadDataFromUri(ctx, str);
        } else {
            onlinechecking(ctx);
        }
    }


    public static void LoadDataFromUri(final Context ctx, final String str) {
        if (Utils.Big_native != null) {
            if (Utils.Big_native.size() == 0) {
                LoadDataForAd(ctx);
            }
        }

       /* RequestQueue MyRequestQueue = Volley.newRequestQueue(ctx);
        String url = "http://www.zblueinfotech.com/appdata/all_app_data.php";
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);

                    JSONArray optJSONArray = obj.getJSONArray("data");

                    JSONObject jsonObject = optJSONArray.getJSONObject(0);

                    if (Utils.App_Update.equals("")) {
                        Utils.App_Update = jsonObject.getString("update_app");
                    }
                    if (Utils.App_Update_image.equals("")) {
                        Utils.App_Update_image = jsonObject.getString("update_image");
                    }
                    if (Utils.App_Update_link.equals("")) {
                        Utils.App_Update_link = jsonObject.getString("update_link");
                    }



                    Log.d("aaaaaaaa", Utils.App_Update);
                    Log.d("aaaaaaaa", Utils.Google_Native_Banner);
                    Log.d("aaaaaaaa", Utils.Google_Intertitial);
                    Log.d("aaaaaaaa", Utils.Google_Banner);


                    try {
                        MobileAds.initialize(ctx, Utils.Google_App_Id);


                    } catch (Exception e) {

                    }
                } catch (JSONException e) {
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("package_name", str);
                return MyData;
            }
        };

        MyRequestQueue.add(MyStringRequest);*/

    }


    public static void LoadDataForAd(final Context ctx) {

        RequestQueue MyRequestQueue = Volley.newRequestQueue(ctx);
        String url = "http://www.zblueinfotech.com/appdata/all_native_data.php";
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);

                    JSONArray optJSONArray = obj.getJSONArray("data");

                    for (int i = 0; i < optJSONArray.length(); i++) {
                        JSONObject jsonObject = optJSONArray.getJSONObject(i);

                        AppAddDataList dataList = new AppAddDataList();

                        dataList.setPackage_name(jsonObject.getString("package_name"));
                        dataList.setApp_link(jsonObject.getString("app_link"));
                        dataList.setNative_banner(jsonObject.getString("native_banner"));
                        dataList.setNative_icon(jsonObject.getString("native_icon"));
                        dataList.setNative_title(jsonObject.getString("native_title"));
                        dataList.setNative_desc(jsonObject.getString("native_desc"));

                        if (ctx.getPackageName().equals(dataList.getPackage_name())) {
                        } else {
                            Utils.Big_native.add(dataList);
                            Log.e("Big_native: ", "" + Utils.Big_native.size());
                        }
                    }

                } catch (JSONException e) {
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("appkey", "Yarn@2K20upin");
                MyData.put("device", "0");
                return MyData;
            }
        };

        MyRequestQueue.add(MyStringRequest);

    }

    public static void onlinechecking(final Activity act) {

        dialogf = new Dialog(act, R.style.UserDialog);
        dialogf.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogf.setCancelable(false);
        dialogf.setContentView(R.layout.dialog_internet);
        if (!isOnline(act)) {

            brodCarst(act);

        }
        ImageView img_views = dialogf.findViewById(R.id.img_views);
        img_views.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOnline(act)) {
                    Toast.makeText(act, "Please Connect Internet Connection", Toast.LENGTH_SHORT).show();
                } else {
                    dialogf.dismiss();
                }
            }
        });
        if (!isOnline(act)) {
            dialogf.show();
        }
        dialogf.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    public static void brodCarst(final Context ctx) {
//        try {
//            unregisterReceiver(brd);
//        } catch (Exception e) {
//
//        }
        try {
            brd = new NetworkChangeReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            ctx.registerReceiver(brd, filter);
        } catch (Exception e) {

        }
    }


    public static class NetworkChangeReceiver extends BroadcastReceiver {

        boolean c = true;


        @Override
        public void onReceive(final Context context, final Intent intent) {


            if (c) {
                c = false;
                check = true;
            }

            final ConnectivityManager connMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            final NetworkInfo wifi = connMgr
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            final NetworkInfo mobile = connMgr
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (wifi.isAvailable() || mobile.isAvailable()) {
                if (isOnline(context)) {

                    check = false;
                    dialogf.dismiss();
                    LoadDataFromUri(context, pname);
                }
            } else {
            }
        }

    }

}
