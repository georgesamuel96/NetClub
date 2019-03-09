package com.egcoders.technologysolution.netclub;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

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

    public void setCurrentUser(String userName, String userEmail, String userPhone, String userBirthday, String userProfileUrl,
                               String userProfileThumbUrl, Boolean categorySelected){

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(context.getResources().getString(R.string.name_currenetUser), userName);
        editor.putString(context.getResources().getString(R.string.email_currenetUser), userEmail);
        editor.putString(context.getResources().getString(R.string.phone_currenetUser), userPhone);
        editor.putString(context.getResources().getString(R.string.birthday_currenetUser), userBirthday);
        editor.putString(context.getResources().getString(R.string.profileUrl_currenetUser), userProfileUrl);
        editor.putString(context.getResources().getString(R.string.profileThumbUrl_currenetUser), userProfileThumbUrl);
        editor.putBoolean(context.getResources().getString(R.string.categorySelected_currenetUser), categorySelected);
        editor.commit();
    }

    public Map<String, Object> getCurrentUser(){

        String status = "";
        Map<String, Object> currentUserMap = new HashMap<>();
        currentUserMap.put("name", preferences.getString(context.getResources().getString(R.string.name_currenetUser), status));
        currentUserMap.put("email", preferences.getString(context.getResources().getString(R.string.email_currenetUser), status));
        currentUserMap.put("phone", preferences.getString(context.getResources().getString(R.string.phone_currenetUser), status));
        currentUserMap.put("birthday", preferences.getString(context.getResources().getString(R.string.birthday_currenetUser), status));
        currentUserMap.put("profileUrl", preferences.getString(context.getResources().getString(R.string.profileUrl_currenetUser), status));
        currentUserMap.put("profileThumbUrl", preferences.getString(context.getResources().getString(R.string.profileThumbUrl_currenetUser), status));
        currentUserMap.put("categorySelected", preferences.getBoolean(context.getResources().getString(R.string.categorySelected_currenetUser), false));

        return currentUserMap;
    }
}
