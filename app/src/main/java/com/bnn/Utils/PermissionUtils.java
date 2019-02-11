package com.bnn.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.bnn.Activity.SplashActivity;


/**
 * Created by acilryandi on 10/25/17.
 */

public class PermissionUtils {

    private static final String TAG = PermissionUtils.class.getSimpleName();

    private Activity activity;
    private Context context;
    private RequestPermission requestPermission;

    public interface RequestPermission {
        void onPermissionsGiven(boolean permitted);
    }

    public PermissionUtils(Context context) {
        this.activity = (Activity) context;
        this.context = context;
    }

    public static PermissionUtils getInstance(Context context){
        PermissionUtils permissionUtils = new PermissionUtils(context);
        return permissionUtils;
    }

    public void checkAndRequestPermission(RequestPermission requestPermission) {
        this.requestPermission = requestPermission;

        if (activity instanceof SplashActivity) {
            boolean isPermitted = isPermitted(this.context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA});
            if (!isPermitted) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
                    Configuration.showAlertNoTitle(context, "Please allow permissions to use the application features.")
                            .setPositiveButton("Request", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
                                }
                            }).create().show();
                    this.requestPermission.onPermissionsGiven(isPermitted);
                } else {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
                    this.requestPermission.onPermissionsGiven(isPermitted);
                }
            }

            this.requestPermission.onPermissionsGiven(isPermitted);
        }
    }

    private boolean isPermitted (Context context, String[] permissions) {

        if (permissions.length < 1) {
            return false;
        }

        for (String permission:permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    public boolean isGranteds(int[] grants) {
        if (grants.length < 1) {
            return false;
        }

        for (int grant: grants) {
            if (grant != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }
}
