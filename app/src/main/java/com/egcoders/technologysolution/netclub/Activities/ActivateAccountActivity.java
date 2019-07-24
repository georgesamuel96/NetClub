package com.egcoders.technologysolution.netclub.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.egcoders.technologysolution.netclub.R;
import com.egcoders.technologysolution.netclub.Utils.Utils;
import com.egcoders.technologysolution.netclub.data.ApiManager;
import com.egcoders.technologysolution.netclub.data.Login;
import com.egcoders.technologysolution.netclub.data.UserSharedPreference;
import com.egcoders.technologysolution.netclub.model.UserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivateAccountActivity extends AppCompatActivity {

    private EditText codeText;
    private Button verifyBtn;
    private TextView message;
    private UserSharedPreference preference;
    private Utils utils;
    private android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_account);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Verify Code");

        codeText = (EditText) findViewById(R.id.code);
        verifyBtn = (Button) findViewById(R.id.verifyBtn);
        message = (TextView) findViewById(R.id.message);

        String email = getIntent().getStringExtra("email");
        message.setText("Code sent to your email\n" + email);

        preference = new UserSharedPreference(this);
        utils = new Utils(this);

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCode();
            }
        });
    }

    private void checkCode(){
        String code = codeText.getText().toString().trim();

        UserResponse user = preference.getUser();
        String code2 = user.getData().getVerifyCode();

        if(code.equals(code2)){
            user.getData().setActivate("1");
            preference.setUser(user);

            ApiManager.getInstance().activateUser(user.getData().getToken(), "1", new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {

                    UserResponse user = response.body();
                    if(user.getSuccess()){
                        sendToChooseCategory();
                    }
                    else{
                        utils.showMessage("Verify Code", user.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    utils.showMessage("Verify Code", t.getMessage());
                }
            });
        }
        else {
         utils.showMessage("Verify Code", "You entered wrong code");
        }
    }

    private void sendToChooseCategory(){

        Intent intent = new Intent(ActivateAccountActivity.this, CategoriesActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activate_account_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.logout){

            UserResponse user = preference.getUser();
            user.getData().setToken("");
            preference.setUser(user);

            sendToLogin();
        }

        return true;
    }

    private void sendToLogin(){
        Intent i = new Intent(ActivateAccountActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}
