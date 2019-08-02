package com.egcoders.technologysolution.netclub.ui.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.egcoders.technologysolution.netclub.R;
import com.egcoders.technologysolution.netclub.Utils.Utils;
import com.egcoders.technologysolution.netclub.data.interfaces.Login;
import com.egcoders.technologysolution.netclub.data.presenter.LoginPresenter;
import com.egcoders.technologysolution.netclub.Utils.SharedPreferenceConfig;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements Login.View{

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private SharedPreferenceConfig preferenceConfig;
    private Boolean finishActivity;
    private GoogleSignInClient mGoogleSignInClient;
    private Button signInGmailButton;
    private TextView registerTextView;
    private Button loginButton;
    private TextInputLayout emailTextInputLayout;
    private TextInputLayout passwordTextInputLayout;
    private Utils utils;
    private TextView forgetPassText;

    private Login.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        registerTextView = (TextView) findViewById(R.id.register);
        signInGmailButton = (Button) findViewById(R.id.sign_in_button);
        loginButton = (Button) findViewById(R.id.loginBtn);
        emailTextInputLayout = (TextInputLayout) findViewById(R.id.email);
        passwordTextInputLayout = (TextInputLayout) findViewById(R.id.password);
        forgetPassText = (TextView) findViewById(R.id.forgetPassword);

        finishActivity = false;
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        preferenceConfig = new SharedPreferenceConfig(this);

        presenter = new LoginPresenter(LoginActivity.this, this);

        utils = new Utils(LoginActivity.this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        /*signInGmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });*/

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toRegisterActivity();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailTextInputLayout.getEditText().getText().toString();
                String password = passwordTextInputLayout.getEditText().getText().toString();

                if(Utils.isEmailValid(email)) {
                    presenter.loginUser(email, password);
                }
                else {

                    if(!Utils.isEmailValid(email)){

                        emailTextInputLayout.getEditText().setError("Write valid email");
                    }
                }
            }
        });

        forgetPassText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 101);
    }


    private void sendToChooseCategories() {
        finishActivity = true;
        Intent i = new Intent(LoginActivity.this, CategoriesActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(finishActivity){
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 101) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                System.out.println("Google sign in failed " + e.getMessage());
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        System.out.println("firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            System.out.println("signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            System.out.println("signInWithCredential:failure " + task.getException());
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void updateUI(final FirebaseUser user) {

        utils.showProgressDialog("Check Account", "Loading");

        final String userId = user.getUid();
        final String userEmail = user.getEmail();

        firestore.collection("Users").whereEqualTo("email", userEmail).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    int isExist = task.getResult().getDocumentChanges().size();

                    if(isExist > 0){

                        preferenceConfig.setSharedPrefConfig(userId);
                        Map<String, Object> userMap = task.getResult().getDocumentChanges().get(0).getDocument().getData();

                        preferenceConfig.setSharedPrefConfig(userId);
                        preferenceConfig.setCurrentUser(userMap);

                        utils.hideProgressDialog();

                        sendToMain();
                    }
                    else {



                        preferenceConfig.setSharedPrefConfig(userId);
                        Map<String, Object> userMap = new HashMap<>();

                        userMap.put("name", user.getDisplayName());
                        userMap.put("email", user.getEmail());
                        userMap.put("phone", "");
                        userMap.put("birthday", "");
                        userMap.put("profile_url", user.getPhotoUrl().toString());
                        userMap.put("profileThumb", user.getPhotoUrl().toString());
                        userMap.put("categorySelected", false);
                        userMap.put("userStatue", "0");

                        preferenceConfig.setCurrentUser(userMap);

                        firestore.collection("Users").document(userId).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                    utils.hideProgressDialog();

                                    sendToChooseCategories();
                                }
                                else {

                                    utils.hideProgressDialog();
                                }
                            }
                        });
                    }
                }
                else{

                }
            }
        });
    }

    private void toRegisterActivity() {

        Intent intent = new Intent(LoginActivity.this, RegisterSecondPageActivity.class);
        startActivity(intent);

    }

    private void sendToMain() {
        finishActivity = true;
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
