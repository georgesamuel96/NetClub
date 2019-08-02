package com.egcoders.technologysolution.netclub.data.presenter;

import android.app.Activity;
import android.content.Intent;
import com.egcoders.technologysolution.netclub.ui.activities.ActivateAccountActivity;
import com.egcoders.technologysolution.netclub.Utils.Utils;
import com.egcoders.technologysolution.netclub.remote.MainApplication;
import com.egcoders.technologysolution.netclub.Utils.UserSharedPreference;
import com.egcoders.technologysolution.netclub.data.interfaces.Register;
import com.egcoders.technologysolution.netclub.model.profile.UserData;
import com.egcoders.technologysolution.netclub.model.profile.UserResponse;
import com.egcoders.technologysolution.netclub.remote.ApiManager;

import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterPresenter implements Register.Presenter {

    private Activity activity;
    private Register.View view;
    private UserSharedPreference preference;
    private Utils utils;


    public RegisterPresenter(Activity activity, Register.View view){
        this.activity = activity;
        this.view = view;
        preference = new UserSharedPreference(activity.getApplicationContext());
        utils = new Utils(activity);
    }

    @Override
    public void setUser(final UserData user, String imagePath) {

        utils.showProgressDialog("Register", "Loading");

        ApiManager.getInstance().createUser(imagePath, user, new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {

                UserResponse userResponse = response.body();
                if(userResponse.getSuccess()){

                    utils.hideProgressDialog();
                    login(user);
                }
                else {

                    utils.hideProgressDialog();
                    utils.showMessage("Register", userResponse.getMessage());


                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {

                utils.hideProgressDialog();

                String message;
                if(t instanceof SocketTimeoutException)
                    message = "Socket Time out. Please try again.";
                else
                   message = t.getMessage();

                utils.showMessage("Register", message);
            }
        });
    }

    private void login(UserData user){

        utils.showProgressDialog("Login to Account", "Loading");

        MainApplication.apiManager.loginUser(user.getEmail(), user.getPassword(), new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {

                UserResponse userResponse = response.body();

                if(userResponse.getSuccess()){

                    utils.hideProgressDialog();

                    userResponse.getData().setToken("Bearer" + response.headers().get("Authorization"));

                    String token = userResponse.getData().getToken();
                    int id = userResponse.getData().getId();

                    sendToActivateAccount(userResponse.getData().getEmail());
                }
                else {

                    utils.hideProgressDialog();

                    utils.showMessage("Login", userResponse.getMessage());
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {

                utils.hideProgressDialog();

                String message;
                if(t instanceof SocketTimeoutException)
                    message = "Please try again.";
                else
                    message = t.getMessage();

                utils.showMessage("Login", message);
            }
        });
    }

    private void sendToActivateAccount(String email){

        Intent intent = new Intent(activity, ActivateAccountActivity.class);
        intent.putExtra("email", email);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }
}
