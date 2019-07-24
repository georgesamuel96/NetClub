package com.egcoders.technologysolution.netclub.data;

import android.app.Activity;
import android.content.Intent;

import com.egcoders.technologysolution.netclub.Activities.ActivateAccountActivity;
import com.egcoders.technologysolution.netclub.Activities.CategoriesActivity;
import com.egcoders.technologysolution.netclub.Activities.MainActivity;
import com.egcoders.technologysolution.netclub.Utils.Utils;
import com.egcoders.technologysolution.netclub.model.CategorySelected;
import com.egcoders.technologysolution.netclub.model.ForgetPasswordResponse;
import com.egcoders.technologysolution.netclub.model.UserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginPresenter implements Login.Presenter {

    private Login.View view;
    private Utils utils;
    private volatile Boolean isSelected = null;
    private Activity activity;
    private UserSharedPreference preference;

    public LoginPresenter(Activity activity, Login.View view){
        this.view = view;
        this.activity = activity;
        preference = new UserSharedPreference(activity.getApplicationContext());
        utils = new Utils(activity);
    }

    @Override
    public void loginUser(String email, String password) {

        utils.showProgressDialog("Login to your account", "Loading");
        MainApplication.apiManager.loginUser(email, password, new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {

                final UserResponse userResponse = response.body();

                if(userResponse.getSuccess()){

                    userResponse.getData().setToken("Bearer" + response.headers().get("Authorization"));

                    String token = userResponse.getData().getToken();
                    int id = userResponse.getData().getId();

                    if(userActivated(userResponse)){
                        userResponse.getData().setActivate("1");
                        userResponse.getData().setSelectedCategory(false);

                        preference.setUser(userResponse);

                        MainApplication.apiManager.categorySelected(token, id, new Callback<CategorySelected>() {
                            @Override
                            public void onResponse(Call<CategorySelected> call, Response<CategorySelected> response) {

                                utils.hideProgressDialog();

                                CategorySelected selected = response.body();
                                if(selected.getSuccess()) {
                                    userResponse.getData().setSelectedCategory(true);

                                    preference.setUser(userResponse);
                                    sendToMain();
                                }
                                else {
                                    userResponse.getData().setSelectedCategory(false);

                                    preference.setUser(userResponse);
                                    sendToChooseCategory();
                                }
                            }
                            @Override
                            public void onFailure(Call<CategorySelected> call, Throwable t) {

                                utils.hideProgressDialog();
                                utils.showMessage("Your Categories", t.getMessage());
                            }
                        });
                    }
                    else {
                        utils.hideProgressDialog();

                        userResponse.getData().setActivate("0");
                        userResponse.getData().setSelectedCategory(false);

                        preference.setUser(userResponse);
                        sendToActivateAccount(userResponse.getData().getEmail());
                    }
                }
                else {
                    utils.hideProgressDialog();
                    utils.showMessage("Error", userResponse.getMessage());
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                utils.hideProgressDialog();
                utils.showMessage("Login Error", t.getMessage());
            }
        });
    }

    private Boolean userActivated(UserResponse user){
        if(user.getData().getActivate() != null && user.getData().getActivate().equals("1"))
            return true;
        return false;
    }

    private void sendToMain(){

        utils.hideProgressDialog();
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }

    private void sendToChooseCategory(){

        utils.hideProgressDialog();
        Intent intent = new Intent(activity, CategoriesActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    private void sendToActivateAccount(String email){

        utils.hideProgressDialog();
        Intent intent = new Intent(activity, ActivateAccountActivity.class);
        intent.putExtra("email", email);
        activity.startActivity(intent);
        activity.finish();
    }
}
