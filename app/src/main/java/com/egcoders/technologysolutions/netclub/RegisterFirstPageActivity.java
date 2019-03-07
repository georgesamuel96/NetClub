package com.egcoders.technologysolutions.netclub;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterFirstPageActivity extends AppCompatActivity {

    private EditText emailText;
    private EditText passwordText;
    private EditText confirmPasswordText;
    private ImageView nextImage;
    private AlertDialog.Builder alertBuilder;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_first_page);

        alertBuilder = new AlertDialog.Builder(this);

        emailText = (EditText) findViewById(R.id.email);
        passwordText = (EditText) findViewById(R.id.password);
        confirmPasswordText = (EditText) findViewById(R.id.confirmPass);
        nextImage = (ImageView) findViewById(R.id.next);

        mAuth = FirebaseAuth.getInstance();

        nextImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = emailText.getText().toString().trim();
                final String password = passwordText.getText().toString().trim();
                String confirmPass = confirmPasswordText.getText().toString().trim();

                if(!missingValue(email, password, confirmPass)){

                    if(password.equals(confirmPass)) {

                        mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){

                                    mAuth.getCurrentUser().updateEmail(email);
                                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Intent i = new Intent(RegisterFirstPageActivity.this, RegisterSecondPageActivity.class);
                                                i.putExtra("email", email);
                                                i.putExtra("password", password);
                                                startActivity(i);
                                                finish();                                            }
                                            else{
                                                alertBuilder.setTitle("Email Error");
                                                alertBuilder.setMessage("You should inter valid email");
                                                alertBuilder.setCancelable(false);
                                                alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        passwordText.setText("");
                                                        confirmPasswordText.setText("");
                                                        dialog.cancel();
                                                    }
                                                });
                                                AlertDialog alertDialog = alertBuilder.create();
                                                alertDialog.show();
                                            }
                                        }
                                    });

                                }
                                else {

                                }
                            }
                        });

                    }
                    else{
                        alertBuilder.setTitle("Authentication Error");
                        alertBuilder.setMessage("Password must be as same as confirm password");
                        alertBuilder.setCancelable(false);
                        alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                passwordText.setText("");
                                confirmPasswordText.setText("");
                                dialog.cancel();
                            }
                        });
                        AlertDialog alertDialog = alertBuilder.create();
                        alertDialog.show();
                    }
                }
            }
        });
    }

    private Boolean missingValue(String email, String password, String confirmPass){

        if(email.equals("") || password.equals("") || confirmPass.equals("")){

            alertBuilder.setTitle("Missing Value");
            alertBuilder.setMessage("There is missing value");
            alertBuilder.setCancelable(false);
            alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    emailText.setText("");
                    passwordText.setText("");
                    confirmPasswordText.setText("");
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = alertBuilder.create();
            alertDialog.show();

            return true;
        }
        return false;
    }

    private void sendToLogin() {

        startActivity(new Intent(RegisterFirstPageActivity.this, LoginActivity.class));
        finish();
    }
}
