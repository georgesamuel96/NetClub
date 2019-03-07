package com.example.georgesamuel.netclub;

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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class EditUserProfileActivity extends AppCompatActivity {

    private String currentUserId;
    private CircleImageView userImage;
    private EditText userName, userEmail, userPhone;
    private EditText userBirthday;
    private FirebaseFirestore firestore;
    private Button changeBtn;
    private Uri userImageURI, downloadUri, downloadThumbUri;
    private AlertDialog.Builder alertBuilder;
    private StorageReference storageReference;
    private Bitmap compressedImageFile;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        userImage = (CircleImageView) findViewById(R.id.user_profile);
        userName = (EditText) findViewById(R.id.user_name);
        userEmail = (EditText) findViewById(R.id.user_email);
        userPhone = (EditText) findViewById(R.id.user_phone);
        userBirthday = (EditText) findViewById(R.id.user_birthday);
        changeBtn = (Button) findViewById(R.id.change_btn);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        userEmail.setClickable(false);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        alertBuilder = new AlertDialog.Builder(this);

        progressBar.setVisibility(View.VISIBLE);
        firestore.collection("Users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Map<String, Object> userMap = task.getResult().getData();
                    userEmail.setText(mAuth.getCurrentUser().getEmail());
                    userBirthday.setText(userMap.get("birthday").toString());
                    userPhone.setText(userMap.get("phone").toString());
                    userName.setText(userMap.get("name").toString());
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.profile);
                    Glide.with(EditUserProfileActivity.this).applyDefaultRequestOptions(requestOptions)
                            .load(userMap.get("profile_url")).thumbnail(Glide.with(EditUserProfileActivity.this)
                                    .load(userMap.get("profileThumb"))).into(userImage);

                    progressBar.setVisibility(View.INVISIBLE);
                }
                else{
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name, birthday, phone, randomName;
                name = userName.getText().toString().trim();
                birthday = userBirthday.getText().toString().trim();
                phone = userPhone.getText().toString().trim();
                randomName = Long.toString(System.currentTimeMillis());

                if(!missingValue(name, birthday, phone)){

                    progressBar.setVisibility(View.VISIBLE);
                    final StorageReference userProfileReference = storageReference.child("profile_images")
                            .child(randomName + ".jpg");
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
                                    compressedImageFile = new Compressor(EditUserProfileActivity.this)
                                            .compressToBitmap(newImageFile);
                                }
                                catch (IOException e){
                                    e.printStackTrace();
                                }
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] thumbData = baos.toByteArray();

                                final StorageReference userProfileThumbReference = storageReference.child("profile_images/thumbs").
                                        child(randomName + ".jpg");
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
                                            Map<String, Object> userMap = new HashMap<>();
                                            userMap.put("name", name);
                                            userMap.put("birthday", birthday);
                                            userMap.put("phone", phone);
                                            userMap.put("profile_url", downloadUri.toString());
                                            userMap.put("profileThumb", downloadThumbUri.toString());

                                            firestore.collection("Users").document(currentUserId).update(userMap)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                intent.putExtra("TOP", true);
                                                                startActivity(intent);
                                                                progressBar.setVisibility(View.INVISIBLE);
                                                            }
                                                            else{
                                                                progressBar.setVisibility(View.INVISIBLE);
                                                            }
                                                        }
                                                    });
                                        }
                                        else{
                                            progressBar.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                });

                            }
                            else{
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            }
        });

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(ContextCompat.checkSelfPermission(EditUserProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(EditUserProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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

    private Boolean missingValue(String name, String date, String phone){

        if(name.equals("") || date.equals("") || phone.equals("")){

            alertBuilder.setTitle("Missing Value");
            alertBuilder.setMessage("There is missing value");
            alertBuilder.setCancelable(false);
            alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                userImageURI = result.getUri();
                userImage.setImageURI(userImageURI);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void imagePiker(){
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(EditUserProfileActivity.this);
    }
}
