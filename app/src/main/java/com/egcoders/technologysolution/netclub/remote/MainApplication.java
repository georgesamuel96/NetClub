package com.egcoders.technologysolution.netclub.remote;

import android.app.Application;

import com.egcoders.technologysolution.netclub.remote.ApiManager;

public class MainApplication extends Application {

    public static ApiManager apiManager;

    @Override
    public void onCreate() {
        super.onCreate();

        apiManager = ApiManager.getInstance();
    }
}
