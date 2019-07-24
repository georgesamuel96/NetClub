package com.egcoders.technologysolution.netclub.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.egcoders.technologysolution.netclub.R;
import com.egcoders.technologysolution.netclub.model.UserData;
import com.egcoders.technologysolution.netclub.model.UserResponse;

public class UserSharedPreference {

    private Context context;
    private SharedPreferences preferences;

    public UserSharedPreference(Context context){
        this.context = context;
        preferences = context.getSharedPreferences(context.getResources().getString(R.string.preference), Context.MODE_PRIVATE);
    }

    public void setUser(UserResponse user){

        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(context.getResources().getString(R.string.name_currenetUser), user.getData().getName());
        editor.putString(context.getResources().getString(R.string.email_currenetUser), user.getData().getEmail());
        editor.putString(context.getResources().getString(R.string.birthday_currenetUser), user.getData().getBirth_date());
        editor.putString(context.getResources().getString(R.string.phone_currenetUser), user.getData().getPhone());
        editor.putString(context.getResources().getString(R.string.profileUrl_currenetUser), user.getData().getPhoto_max());
        editor.putString(context.getResources().getString(R.string.verifyCode_currenetUser), user.getData().getVerifyCode());
        editor.putString(context.getResources().getString(R.string.updated_at_currenetUser), user.getData().getUpdated_at());
        editor.putString(context.getResources().getString(R.string.created_at_currenetUser), user.getData().getCreated_at());
        editor.putInt(context.getResources().getString(R.string.id_currenetUser), user.getData().getId());
        editor.putBoolean(context.getResources().getString(R.string.categorySelected_currenetUser), user.getData().getSelectedCategory());
        editor.putString(context.getResources().getString(R.string.userStatue_currenetUser), user.getData().getUserStatus());
        editor.putString(context.getResources().getString(R.string.token_currenetUser), user.getData().getToken());
        editor.putString(context.getResources().getString(R.string.activate_currenetUser), user.getData().getActivate());

        editor.commit();
    }

    public UserResponse getUser(){
        UserResponse user = new UserResponse();

        UserData userData = new UserData();

        userData.setName(preferences.getString(context.getResources().getString(R.string.name_currenetUser), ""));
        userData.setEmail(preferences.getString(context.getResources().getString(R.string.email_currenetUser), ""));
        userData.setBirth_date(preferences.getString(context.getResources().getString(R.string.birthday_currenetUser), ""));
        userData.setPhone(preferences.getString(context.getResources().getString(R.string.phone_currenetUser), ""));
        userData.setPhoto_max(preferences.getString(context.getResources().getString(R.string.profileUrl_currenetUser), ""));
        userData.setVerifyCode(preferences.getString(context.getResources().getString(R.string.verifyCode_currenetUser), ""));
        userData.setUpdated_at(preferences.getString(context.getResources().getString(R.string.updated_at_currenetUser), ""));
        userData.setCreated_at(preferences.getString(context.getResources().getString(R.string.created_at_currenetUser), ""));
        userData.setId(preferences.getInt(context.getResources().getString(R.string.id_currenetUser), -1));
        userData.setSelectedCategory(preferences.getBoolean(context.getResources().getString(R.string.categorySelected_currenetUser), false));
        userData.setUserStatus(preferences.getString(context.getResources().getString(R.string.userStatue_currenetUser), ""));
        userData.setToken(preferences.getString(context.getResources().getString(R.string.token_currenetUser), ""));
        userData.setActivate(preferences.getString(context.getResources().getString(R.string.activate_currenetUser), "0"));

        user.setData(userData);

        return user;
    }

}
