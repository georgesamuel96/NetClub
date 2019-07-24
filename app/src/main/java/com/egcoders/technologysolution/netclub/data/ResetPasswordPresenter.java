package com.egcoders.technologysolution.netclub.data;

import android.app.Activity;
import android.util.Log;

import com.egcoders.technologysolution.netclub.Utils.Utils;
import com.egcoders.technologysolution.netclub.model.ForgetPasswordResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordPresenter implements ResetPassword.Presenter {

    private Activity activity;
    private ResetPassword.View view;
    private Utils utils;
    private CodeResetPasswordPrefs prefs;

    public ResetPasswordPresenter(Activity activity, ResetPassword.View view){
        this.activity = activity;
        this.view = view;

        utils = new Utils(activity);
        prefs = new CodeResetPasswordPrefs(activity.getApplicationContext());
    }

    @Override
    public void sendCode(String email) {


        ApiManager.getInstance().forgetPassword(email, new Callback<ForgetPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgetPasswordResponse> call, Response<ForgetPasswordResponse> response) {

                ForgetPasswordResponse passwordResponse = response.body();

                if(passwordResponse.getSuccess()){
                    view.showMessage();
                    prefs.setCode(passwordResponse.getData().getVerifyCode());
                }
                else {

                    Log.v("Error", passwordResponse.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ForgetPasswordResponse> call, Throwable t) {

                Log.v("Error", t.getMessage());
            }
        });
    }

    @Override
    public void verifyCode(String code, String password) {

        utils.showProgressDialog("Change password", "Loading");

        ApiManager.getInstance().resetPasswordPassword(code, password, new Callback<ForgetPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgetPasswordResponse> call, Response<ForgetPasswordResponse> response) {

                ForgetPasswordResponse passwordResponse = response.body();

                utils.hideProgressDialog();
                if(passwordResponse.getSuccess()){
                    utils.showMessage("Change password", "Password changed successfully");
                    activity.finish();
                }
                else {
                    utils.showMessage("Change password", passwordResponse.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ForgetPasswordResponse> call, Throwable t) {
                utils.hideProgressDialog();
                utils.showMessage("Change password", t.getMessage());
            }
        });
    }
}
