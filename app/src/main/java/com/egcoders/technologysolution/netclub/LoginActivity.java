package com.egcoders.technologysolution.netclub;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmailText;
    private EditText loginPassText;
    private Button loginBtn;
    private FirebaseAuth mAuth;
    private TextView register;
    private AlertDialog.Builder alertBuilder;
    private FirebaseFirestore firestore;
    private SharedPreferenceConfig preferenceConfig;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmailText = (EditText) findViewById(R.id.email);
        loginPassText = (EditText) findViewById(R.id.password);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        register = (TextView) findViewById(R.id.register);

        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        alertBuilder = new AlertDialog.Builder(this);
        preferenceConfig = new SharedPreferenceConfig(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String loginEmail = loginEmailText.getText().toString().trim();
                String loginPass = loginPassText.getText().toString().trim();

                if(!missingValue(loginEmail, loginPass)){

                    progressDialog.setCancelable(false);
                    progressDialog.setTitle("Email Verification");
                    progressDialog.setMessage("Loading");
                    progressDialog.show();
                    mAuth.signInWithEmailAndPassword(loginEmail, loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                firestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.getResult().exists()) {

                                            if (task.isSuccessful()) {

                                                Map<String, Object> userMap = task.getResult().getData();

                                                String name, email, phone, birthday, profileUrl, profileThumbUrl;
                                                Boolean categorySelected;

                                                name = userMap.get("name").toString();
                                                email = userMap.get("email").toString();
                                                phone = userMap.get("phone").toString();
                                                birthday = userMap.get("birthday").toString();
                                                profileUrl = userMap.get("profile_url").toString();
                                                profileThumbUrl = userMap.get("profileThumb").toString();
                                                categorySelected = (Boolean) userMap.get("categorySelected");

                                                preferenceConfig.setSharedPrefConfig(userId);
                                                preferenceConfig.setCurrentUser(name, email, phone, birthday, profileUrl, profileThumbUrl, categorySelected);
                                                sendToMain();
                                            }
                                            else{

                                            }

                                        }
                                        else{

                                            preferenceConfig.setSharedPrefConfig("Empty");
                                            progressDialog.dismiss();
                                        }

                                    }
                                });

                            }
                            else{

                                progressDialog.dismiss();
                                String errorMessage = task.getException().getMessage();
                                alertBuilder.setTitle("Exception");
                                alertBuilder.setMessage(errorMessage);
                                alertBuilder.setCancelable(false);
                                alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                AlertDialog alertDialog = alertBuilder.create();
                                alertDialog.show();
                            }
                        }
                    });
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginActivity.this, RegisterFirstPageActivity.class));
            }
        });
    }

    private Boolean missingValue(String email, String password){

        if(email.equals("") || password.equals("")){

            if(email.equals("")) {
                loginEmailText.setError("Enter your email");
            }
            else{
                loginPassText.setError("Enter your password");
            }

            return true;
        }
        return false;
    }

    private void sendToMain() {

        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}
