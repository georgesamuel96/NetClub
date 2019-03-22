package com.egcoders.technologysolution.netclub;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

public class WelcomeActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private SharedPreferenceConfig preferenceConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
                preferenceConfig = new SharedPreferenceConfig(WelcomeActivity.this);
                if(!preferenceConfig.getSharedPrefConfig().equals("Empty")){
                    sendToMain();
                }
                else {
                    sendToLogin();
                }
                
            }
        }, 1000);

    }

    private void sendToMain() {
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
    }

    private void sendToLogin() {
        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
