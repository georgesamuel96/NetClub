package com.egcoders.technologysolution.netclub.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckNetwork {

    public static boolean hasNetwork(Context context){
        boolean connected;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        connected = (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
        return connected;
    }
}
