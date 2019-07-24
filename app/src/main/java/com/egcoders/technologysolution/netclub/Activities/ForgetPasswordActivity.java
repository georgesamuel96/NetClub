package com.egcoders.technologysolution.netclub.Activities;

import android.support.design.widget.TextInputLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.egcoders.technologysolution.netclub.R;
import com.egcoders.technologysolution.netclub.Utils.Utils;
import com.egcoders.technologysolution.netclub.data.CodeResetPasswordPrefs;
import com.egcoders.technologysolution.netclub.data.ResetPassword;
import com.egcoders.technologysolution.netclub.data.ResetPasswordPresenter;

public class ForgetPasswordActivity extends AppCompatActivity implements ResetPassword.View {

    private TextInputLayout emailText;
    private TextInputLayout codeText;
    private TextInputLayout passwordText;
    private TextInputLayout confirmPassText;
    private Button sendCode;
    private Button verifyCode;
    private TextView messageText;
    private android.support.v7.widget.Toolbar toolbar;
    private ResetPassword.Presenter presenter;
    private Utils utils;
    private CodeResetPasswordPrefs prefs;
    private ProgressBar progressBar;
    private Boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reset password");

        // Check if activity opened to reset password || forget password
        String check = getIntent().getStringExtra("email");
        if(check != null){
            isLogin = true;
        }

        emailText = (TextInputLayout) findViewById(R.id.email);
        codeText = (TextInputLayout) findViewById(R.id.code);
        passwordText = (TextInputLayout) findViewById(R.id.password);
        confirmPassText = (TextInputLayout) findViewById(R.id.confirmPassword);
        sendCode = (Button) findViewById(R.id.sendCode);
        verifyCode = (Button) findViewById(R.id.verifyBtn);
        messageText = (TextView) findViewById(R.id.message);
        progressBar = (ProgressBar) findViewById(R.id.loadProgress);

        if(isLogin){
            emailText.getEditText().setFocusable(false);
            emailText.getEditText().setClickable(false);
        }

        presenter = new ResetPasswordPresenter(ForgetPasswordActivity.this, this);
        utils = new Utils(ForgetPasswordActivity.this);
        prefs = new CodeResetPasswordPrefs(ForgetPasswordActivity.this);

        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailText.getEditText().getText().toString().trim();

                if(email.equals(""))
                    emailText.setError("Enter your email");
                else{
                    if(Utils.isEmailValid(email)){
                        progressBar.setVisibility(View.VISIBLE);
                        presenter.sendCode(email);
                    }
                    else {
                        emailText.setError("Enter valid email");
                    }
                }
            }
        });

        verifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code = codeText.getEditText().getText().toString().trim();
                String password = passwordText.getEditText().getText().toString();
                String confirmPass = confirmPassText.getEditText().getText().toString();

                if(code.equals(""))
                    codeText.setError("Enter code");
                else if(password.equals(""))
                    passwordText.setError("Enter password");
                else if(confirmPass.equals(""))
                    confirmPassText.setError("Enter confirm password");
                else if(!password.equals(confirmPass)){
                    utils.showMessage("Passwords", "Password not same as confirm password");

                    passwordText.getEditText().setText("");
                    confirmPassText.getEditText().setText("");
                }
                else if(!code.equals(prefs.getCode())){
                    utils.showMessage("Verify code", "You entered wrong code");
                }
                else {
                    presenter.verifyCode(code, password);
                }
            }
        });
    }

    @Override
    public void showMessage() {
        progressBar.setVisibility(View.GONE);
        messageText.setVisibility(View.VISIBLE);
        codeText.setVisibility(View.VISIBLE);
        passwordText.setVisibility(View.VISIBLE);
        confirmPassText.setVisibility(View.VISIBLE);
        verifyCode.setVisibility(View.VISIBLE);
    }
}
