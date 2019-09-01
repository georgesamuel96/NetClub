package com.egcoders.technologysolution.netclub.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.value.LottieFrameInfo;
import com.airbnb.lottie.value.SimpleLottieValueCallback;
import com.egcoders.technologysolution.netclub.R;
import com.egcoders.technologysolution.netclub.Utils.CheckNetwork;
import com.egcoders.technologysolution.netclub.Utils.UserSharedPreference;
import com.egcoders.technologysolution.netclub.model.profile.UserData;

public class WelcomeActivity extends AppCompatActivity {

    private UserSharedPreference preference;
    private ConstraintLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        container = findViewById(R.id.container);

        preference = new UserSharedPreference(WelcomeActivity.this);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!CheckNetwork.hasNetwork(WelcomeActivity.this)){
                    Snackbar.make(container, R.string.network_connection, Snackbar.LENGTH_LONG).show();
                    return;
                }
                UserData user = preference.getUser().getData();
                if(!user.getToken().equals("")){
                    if(preference.getUser().getData().getActivate().equals("1")){
                        if(preference.getUser().getData().getSelectedCategory()){
                            sendToMain();
                        }
                        else{
                            sendToChooseCategories();
                        }
                    }
                    else{
                        sendToActivateAccount();
                    }
                }
                else {
                    sendToLogin();
                }

            }
        }, 1500);
    }

    private void sendToMain() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToLogin() {
        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
        finish();
    }

    private void sendToActivateAccount(){
        Intent intent = new Intent(WelcomeActivity.this, ActivateAccountActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToChooseCategories(){
        Intent intent = new Intent(WelcomeActivity.this, CategoriesActivity.class);
        startActivity(intent);
        finish();
    }
}
