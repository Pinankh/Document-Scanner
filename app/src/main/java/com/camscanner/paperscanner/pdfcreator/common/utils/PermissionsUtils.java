package com.camscanner.paperscanner.pdfcreator.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import java.util.Iterator;
import java.util.List;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.common.Constant;

public class PermissionsUtils {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = PermissionsUtils.class.getSimpleName();
    public static String STORAGE_PERMISSIONS = "android.permission.WRITE_EXTERNAL_STORAGE";
    public static String TAKE_PICTURE_PERMISSIONS = "android.permission.CAMERA";
    public static String[] TAKE_SAVE_PICTURE_PERMISSIONS = {"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    public static final String ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION =
            "android.settings.MANAGE_ALL_FILES_ACCESS_PERMISSION";

    public interface PermissionListener {
        void onGranted();
    }

    public interface PermissionNonBlockListener {
        void onPermissionsChecked();
    }

    public static boolean checkPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != 0) {
                return false;
            }
        }
        return true;
    }

    public static void askPermissions(final Activity activity, final PermissionListener listener, String... permissions) {
        Dexter.withActivity(activity).withPermissions(permissions).withListener(new MultiplePermissionsListener() {
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                String access$000 = PermissionsUtils.LOG_TAG;
                Log.e(access$000, "onPermissionsChecked " + report.areAllPermissionsGranted());
                if (!report.areAllPermissionsGranted()) {
                    boolean hasPermanentlyDeniedAnyPermission = false;
                    Iterator<PermissionDeniedResponse> it = report.getDeniedPermissionResponses().iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        PermissionDeniedResponse deniedResponse = it.next();
                        if (deniedResponse.isPermanentlyDenied()) {
                            PermissionsUtils.showOpenPermissionSetting(activity, deniedResponse.getPermissionName());
                            hasPermanentlyDeniedAnyPermission = true;
                            break;
                        }
                    }
                    if (!hasPermanentlyDeniedAnyPermission) {
//                        Activity activity = activity;
                        Toast.makeText(activity, activity.getString(R.string.permissions_error), 0).show();
                        return;
                    }
                    return;
                }
                PermissionListener permissionListener = listener;
                if (permissionListener != null) {
                    permissionListener.onGranted();
                }
            }

            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).withErrorListener(new PermissionRequestErrorListener() {
            @Override
            public void onError(DexterError dexterError) {
                PermissionsUtils.lambda$askPermissions$0(dexterError);
            }
        }).onSameThread().check();
    }

    static /* synthetic */ void lambda$askPermissions$0(DexterError error) {
        Log.e(LOG_TAG, error.toString());
    }

    public static void askPermissionsWithoutBlock(Activity activity, final PermissionNonBlockListener listener, String... permissions) {
        Dexter.withActivity(activity).withPermissions(permissions).withListener(new MultiplePermissionsListener() {
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                PermissionNonBlockListener permissionNonBlockListener = listener;
                if (permissionNonBlockListener != null) {
                    permissionNonBlockListener.onPermissionsChecked();
                }
            }

            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).withErrorListener(new PermissionRequestErrorListener() {
            @Override
            public void onError(DexterError dexterError) {
                PermissionsUtils.lambda$askPermissionsWithoutBlock$1(dexterError);
            }
        }).onSameThread().check();
    }

    static /* synthetic */ void lambda$askPermissionsWithoutBlock$1(DexterError error) {
        Log.e(LOG_TAG, error.toString());
    }

    public static void showOpenPermissionSetting(final Activity context, String permission) {
        String permission2 = getReadablePermission(context, permission);
        AlertDialog.Builder title = new AlertDialog.Builder(context).setTitle((CharSequence) context.getString(R.string.permission_title));
        title.setMessage((CharSequence) context.getString(R.string.permission_force_denied) + " " + permission2 + " " + context.getString(R.string.permission_force_denied_to_do)).setPositiveButton((CharSequence) context.getString(R.string.permission_setting), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {


            public final void onClick(DialogInterface dialogInterface, int i) {
                context.startActivityForResult(new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.parse("package:" + context.getPackageName())), Constant.REQUEST_PERMISSION_SETTING);
            }
        }).setCancelable(false).create().show();
    }


    public static String getReadablePermission(Context context, String permission) {
        char c;
        int hashCode = permission.hashCode();
        if (hashCode != 463403621) {
            if (hashCode == 1365911975 && permission.equals("android.permission.WRITE_EXTERNAL_STORAGE")) {
                c = 1;
                if (c == 1) {
                    return context.getString(R.string.permission_storage);
                }
                if (c != 2) {
                    return permission.replace("android.permission.", "");
                }
                return context.getString(R.string.permission_camera);
            }
        } else if (permission.equals("android.permission.CAMERA")) {
            c = 2;
            if (c == 1) {
            }
        }
        c = 65535;
        if (c == 1) {
        }
        return null;
    }
}
