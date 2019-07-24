package com.egcoders.technologysolution.netclub.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.egcoders.technologysolution.netclub.R;
import com.egcoders.technologysolution.netclub.data.SharedPreferenceConfig;
import com.egcoders.technologysolution.netclub.data.UserSharedPreference;
import com.egcoders.technologysolution.netclub.model.UserData;
import com.egcoders.technologysolution.netclub.model.UserResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class WelcomeActivity extends AppCompatActivity {

    private UserSharedPreference preference;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        preference = new UserSharedPreference(WelcomeActivity.this);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);

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
        }, 500);



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
