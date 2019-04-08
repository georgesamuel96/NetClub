package com.egcoders.technologysolution.netclub;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public void setCurrentUser(Map<String, Object> userMap){

        String userName = userMap.get("name").toString();
        String userEmail = userMap.get("email").toString();
        String userPhone = userMap.get("phone").toString();
        String userBirthday = userMap.get("birthday").toString();
        String userProfileUrl = userMap.get("profile_url").toString();
        String userProfileThumbUrl = userMap.get("profileThumb").toString();
        Boolean categorySelected = (Boolean) userMap.get("categorySelected");

        if(!userMap.containsKey("userStatue"))
            userMap.put("userStatue", "0");

        String userStatue = userMap.get("userStatue").toString();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(context.getResources().getString(R.string.name_currenetUser), userName);
        editor.putString(context.getResources().getString(R.string.email_currenetUser), userEmail);
        editor.putString(context.getResources().getString(R.string.phone_currenetUser), userPhone);
        editor.putString(context.getResources().getString(R.string.birthday_currenetUser), userBirthday);
        editor.putString(context.getResources().getString(R.string.profileUrl_currenetUser), userProfileUrl);
        editor.putString(context.getResources().getString(R.string.profileThumbUrl_currenetUser), userProfileThumbUrl);
        editor.putBoolean(context.getResources().getString(R.string.categorySelected_currenetUser), categorySelected);
        editor.putString(context.getResources().getString(R.string.userStatue_currenetUser), userStatue);
        editor.commit();
    }

    public Map<String, Object> getCurrentUser(){

        String status = "";
        Map<String, Object> currentUserMap = new HashMap<>();
        currentUserMap.put("name", preferences.getString(context.getResources().getString(R.string.name_currenetUser), status));
        currentUserMap.put("email", preferences.getString(context.getResources().getString(R.string.email_currenetUser), status));
        currentUserMap.put("phone", preferences.getString(context.getResources().getString(R.string.phone_currenetUser), status));
        currentUserMap.put("birthday", preferences.getString(context.getResources().getString(R.string.birthday_currenetUser), status));
        currentUserMap.put("profile_url", preferences.getString(context.getResources().getString(R.string.profileUrl_currenetUser), status));
        currentUserMap.put("profileThumb", preferences.getString(context.getResources().getString(R.string.profileThumbUrl_currenetUser), status));
        currentUserMap.put("categorySelected", preferences.getBoolean(context.getResources().getString(R.string.categorySelected_currenetUser), false));
        currentUserMap.put("userStatue", preferences.getString(context.getResources().getString(R.string.userStatue_currenetUser), status));

        return currentUserMap;
    }

    public void setUserCategory(String id){

        SharedPreferences.Editor editor = preferences.edit();
        String list = preferences.getString("userList", "");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(list);
        stringBuilder.append(id + ",");
        editor.putString("userList", stringBuilder.toString());
        editor.commit();
    }

    public String getUserCategory(){
       String list = "";
       list  = preferences.getString("userList", list);
       return list;
    }

    public void resetCategoryList(){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userList", "");
        editor.commit();
    }
}
