package com.egcoders.technologysolutions.netclub;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class RegisterSecondPageActivity extends AppCompatActivity {

    private EditText userNameText;
    private EditText birthdayText;
    private EditText phoneText;
    private Button register;
    private CircleImageView userImage;
    private AlertDialog.Builder alertBuilder;
    private Uri userImageURI = null, downloadUri, downloadThumbUri;
    private String email, password;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private Bitmap compressedImageFile;
    private String currentUserId;
    private SaveUserInstance saveUserInstance;
    private SharedPreferenceConfig preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_second_page);

        alertBuilder = new AlertDialog.Builder(this);
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");

        userNameText = (EditText) findViewById(R.id.user_name);
        birthdayText = (EditText) findViewById(R.id.date);
        phoneText = (EditText) findViewById(R.id.phone);
        register = (Button) findViewById(R.id.register);
        userImage = (CircleImageView) findViewById(R.id.user_image);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        saveUserInstance = new SaveUserInstance();
        preference = new SharedPreferenceConfig(getApplicationContext());

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String userName = userNameText.getText().toString().trim();
                final String birthday = birthdayText.getText().toString().trim();
                final String phone = phoneText.getText().toString().trim();

                if(!missingValue(userName, birthday, phone)){

                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                currentUserId = task.getResult().getUser().getUid();
                                saveUserInstance.setId(currentUserId);
                                final StorageReference userProfileReference = storageReference.child("profile_images")
                                        .child(currentUserId + ".jpg");
                                UploadTask userProfileUploadTask = userProfileReference.putFile(userImageURI);
                                Task<Uri> userProfileUriTask = userProfileUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                    @Override
                                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                        if(!task.isSuccessful())
                                        {
                                            alertBuilder.setTitle("Upload Image");
                                            alertBuilder.setMessage(task.getException().getMessage());
                                            alertBuilder.setCancelable(false);
                                            alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });
                                            AlertDialog alertDialog = alertBuilder.create();
                                            alertDialog.show();
                                            return null;
                                        }
                                        return userProfileReference.getDownloadUrl();
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if(task.isSuccessful()){

                                            downloadUri = task.getResult();

                                            File newImageFile = new File(userImageURI.getPath());
                                            try {
                                                compressedImageFile = new Compressor(RegisterSecondPageActivity.this)
                                                        .compressToBitmap(newImageFile);
                                            }
                                            catch (IOException e){
                                                e.printStackTrace();
                                            }
                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                            byte[] thumbData = baos.toByteArray();

                                            final StorageReference userProfileThumbReference = storageReference.child("profile_images/thumbs").
                                                    child(currentUserId + ".jpg");
                                            UploadTask userProfileThumbUploadTask = userProfileThumbReference
                                                    .putBytes(thumbData);
                                            Task<Uri> userProfileThumbUriTask = userProfileThumbUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                                @Override
                                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                    if(!task.isSuccessful()) {
                                                        return null;
                                                    }
                                                    else {
                                                        return userProfileThumbReference.getDownloadUrl();
                                                    }
                                                }
                                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Uri> task) {
                                                    if(task.isSuccessful()){

                                                        downloadThumbUri = task.getResult();

                                                        Intent i = new Intent(RegisterSecondPageActivity.this, CategoriesActivity.class);


                                                        preference.setSharedPrefConfig(saveUserInstance.getId());
                                                        saveUserInstance.setName(userName);
                                                        saveUserInstance.setEmail(email);
                                                        saveUserInstance.setBirthday(birthday);
                                                        saveUserInstance.setPhone(phone);
                                                        saveUserInstance.setCategorySelected(false);
                                                        saveUserInstance.setProfile_url(downloadThumbUri.toString());
                                                        saveUserInstance.setProfileThumb_url(downloadThumbUri.toString());
                                                        saveUserInstance.setId(currentUserId);

                                                        startActivity(i);
                                                        finish();
                                                    }
                                                    else{

                                                    }
                                                }
                                            });

                                        }
                                        else{

                                        }
                                    }
                                });

                            }
                            else{
                                alertBuilder.setTitle("Error");
                                alertBuilder.setMessage(task.getException().getMessage());
                                alertBuilder.setCancelable(false);
                                alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        userNameText.setText("");
                                        birthdayText.setText("");
                                        phoneText.setText("");
                                        dialog.cancel();
                                    }
                                });
                                AlertDialog alertDialog = alertBuilder.create();
                                alertDialog.show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
                else {

                }
            }
        });

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(ContextCompat.checkSelfPermission(RegisterSecondPageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(RegisterSecondPageActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }
                    else{
                        imagePiker();
                    }
                }
                else{
                    imagePiker();
                }

            }
        });

    }

    private Boolean missingValue(String userName, String date, String phone){

        if(userName.equals("") || date.equals("") || phone.equals("") || userImageURI == null){

            /*alertBuilder.setTitle("Missing Value");
            alertBuilder.setMessage("There is missing value");
            alertBuilder.setCancelable(false);
            alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    userNameText.setText("");
                    birthdayText.setText("");
                    phoneText.setText("");
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = alertBuilder.create();
            alertDialog.show();*/
            if(userName.equals("")){
                userNameText.setError("Enter your name");
            }
            else if(date.equals("")){
                birthdayText.setError("Enter your birthday");
            }
            else if(phone.equals("")){
                phoneText.setError("Enter Your phone");
            }
            else{
                alertBuilder.setTitle("Your photo");
                alertBuilder.setMessage("Choose your photo");
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

            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        sendToFirstPage();
    }

    private void sendToFirstPage() {
        Intent i = new Intent(RegisterSecondPageActivity.this, RegisterFirstPageActivity.class);
        i.putExtra("email", email);
        startActivity(i);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                userImageURI = result.getUri();
                userImage.setImageURI(userImageURI);
                //isChanged = true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void imagePiker(){
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(RegisterSecondPageActivity.this);
    }

}
