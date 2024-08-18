package com.ru.admaster;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.widget.Toast;

public class Privacy_Policy extends Activity {

    public static boolean isOnline(Context ctx) {
        NetworkInfo netInfo = ((ConnectivityManager) ctx.getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (netInfo == null || !netInfo.isConnected()) {
            return false;
        }
        return true;
    }

    public static void Privacy(Activity act){
        if (isOnline(act)) {
            try{
                Uri uri = Uri.parse(Utils.Privacy);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                act.startActivity(intent);
            }catch (Exception e){

            }

        } else {
            Toast.makeText(act, "Please Check Your Internet Connection And Try Again.", Toast.LENGTH_SHORT).show();
        }
    }
}
