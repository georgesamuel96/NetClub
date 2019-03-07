package com.egcoders.technologysolutions.netclub;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmailText;
    private EditText loginPassText;
    private Button loginBtn;
    private FirebaseAuth mAuth;
    private TextView register;
    private AlertDialog.Builder alertBuilder;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmailText = (EditText) findViewById(R.id.email);
        loginPassText = (EditText) findViewById(R.id.password);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        register = (TextView) findViewById(R.id.register);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        alertBuilder = new AlertDialog.Builder(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String loginEmail = loginEmailText.getText().toString().trim();
                String loginPass = loginPassText.getText().toString().trim();

                if(!missingValue(loginEmail, loginPass)){
                    mAuth.signInWithEmailAndPassword(loginEmail, loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                sendToMain();
                            }
                            else{
                                String errorMessage = task.getException().getMessage();

                                alertBuilder.setTitle("Exception");
                                alertBuilder.setMessage(errorMessage);
                                alertBuilder.setCancelable(false);
                                alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        loginEmailText.setText("");
                                        loginPassText.setText("");
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
                finish();
            }
        });
    }

    private Boolean missingValue(String email, String password){

        if(email.equals("") || password.equals("")){

            alertBuilder.setTitle("Missing Value");
            alertBuilder.setMessage("There is missing value email, password or both");
            alertBuilder.setCancelable(false);
            alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    loginEmailText.setText("");
                    loginPassText.setText("");
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = alertBuilder.create();
            alertDialog.show();

            return true;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    private void sendToMain() {

        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}
