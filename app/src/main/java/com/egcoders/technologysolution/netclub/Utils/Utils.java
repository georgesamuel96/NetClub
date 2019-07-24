package com.egcoders.technologysolution.netclub.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Utils {

    private ProgressDialog progressDialog;
    private AlertDialog.Builder alertBuilder;
    private Activity activity;

    public Utils(Activity activity){
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

    public void hideProgressDialog(){
        progressDialog.dismiss();
    }

    public void showMessage(String title, String message){

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

        String url = nextPage.substring(23);

        return url;
    }
}
