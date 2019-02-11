package com.bnn.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.bnn.R;
import com.bnn.Utils.PermissionUtils;


public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    private PermissionUtils permissionUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionUtils = PermissionUtils.getInstance(this);
            permissionUtils.checkAndRequestPermission(new PermissionUtils.RequestPermission() {
                @Override
                public void onPermissionsGiven(boolean permitted) {
                    if (permitted) {
                        showLoading();
                    } else {

                    }

                }
            });
        } else {
            showLoading();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                for( int i = 0; i < permissions.length; i++ ) {
                    if( grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.d( "Permissions", "Permission Granted: " + permissions[i] );
                    } else if( grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        Log.d( "Permissions", "Permission Denied: " + permissions[i] );
                    }
                }
                if (permissionUtils.isGranteds(grantResults)) {
                    showLoading();
                } else {
                    Toast.makeText(SplashActivity.this, "Please allow some permissions, you can't use this app because you've just denied permissions for this app.", Toast.LENGTH_LONG).show();
                }
            }
            break;
            default:
            {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    private void showLoading(){
        Log.d(TAG, "Loading splash");

        new Thread(new Runnable() {

            public void run() {
                for (int x = 0;x < 1;x++){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Intent x = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(x);
                finish();
            }
        }).start();
    }
}
