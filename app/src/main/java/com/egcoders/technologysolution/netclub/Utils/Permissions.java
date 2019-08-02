package com.egcoders.technologysolution.netclub.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

public class Permissions {

    public static final int REQUEST_CODE = 1001;
    public static void permissionStorage(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if(activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_GRANTED){
                if(activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_GRANTED){
                    return ;
                }
                else{
                    ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE);
                }
            }
            else{
                ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE);
            }
        }
    }
}
