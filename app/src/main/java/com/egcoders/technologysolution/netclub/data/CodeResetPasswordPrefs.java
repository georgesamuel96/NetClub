package com.egcoders.technologysolution.netclub.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.egcoders.technologysolution.netclub.R;

public class CodeResetPasswordPrefs {

    private Context context;
    private SharedPreferences preferences;

    public CodeResetPasswordPrefs(Context context){
        this.context = context;
        preferences = context.getSharedPreferences(context.getResources().getString(R.string.preference), Context.MODE_PRIVATE);
    }

    public void setCode(String code){

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("forgetPasswordCode", code);
        editor.commit();
    }

    public String getCode(){
        String code = "";
        code = preferences.getString("forgetPasswordCode", "");
        return code;
    }

}
