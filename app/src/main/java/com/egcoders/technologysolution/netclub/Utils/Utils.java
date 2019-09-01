package com.egcoders.technologysolution.netclub.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import static com.egcoders.technologysolution.netclub.Utils.Permissions.REQUEST_CODE;

public class Utils {

    private ProgressDialog progressDialog;
    private AlertDialog.Builder alertBuilder;
    private Activity activity;

    public Utils(Activity activity) {
        this.activity = activity;
        progressDialog = new ProgressDialog(activity);
        alertBuilder = new AlertDialog.Builder(activity);
    }

    public void showProgressDialog(String title, String content) {
        progressDialog.setCancelable(false);
        progressDialog.setTitle(title);
        progressDialog.setMessage(content);
        progressDialog.show();
    }

    public void hideProgressDialog() {
        progressDialog.dismiss();
    }

    public void showMessage(String title, String message) {

        alertBuilder.setTitle(title);
        alertBuilder.setMessage(message);
        alertBuilder.setCancelable(false);
        alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static String getUrl(String nextPage) {
        if (nextPage.length() > 23) {
            String url = nextPage.substring(23);
            return url;
        } else {
            return "";
        }
    }

    public void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                if (activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                } else {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
                }
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }
    }
}
