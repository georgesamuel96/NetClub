package com.egcoders.technologysolution.netclub;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceConfig {

    private Context context;
    private SharedPreferences preferences;

    public SharedPreferenceConfig(Context context){
        this.context = context;
        preferences = context.getSharedPreferences(context.getResources().getString(R.string.preference), Context.MODE_PRIVATE);
    }

    public String getSharedPrefConfig(){
        String status = "Empty";
        status = preferences.getString(context.getResources().getString(R.string.status_preference), status);
        return status;
    }

    public void setSharedPrefConfig(String userId){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(context.getResources().getString(R.string.status_preference), userId);
        editor.commit();
    }
}
