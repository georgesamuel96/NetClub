package com.egcoders.technologysolution.netclub;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class EditUserProfileActivity extends AppCompatActivity {

    private CircleImageView userImage;
    private TextInputLayout userName, userPhone;
    private TextInputLayout userBirthday, userEmail;
    private FirebaseFirestore firestore;
    private Button changeBtn;
    private Uri userImageURI, downloadUri, downloadThumbUri;
    private AlertDialog.Builder alertBuilder;
    private StorageReference storageReference;
    private Bitmap compressedImageFile;
    private SharedPreferenceConfig preferenceConfig;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        userImage = (CircleImageView) findViewById(R.id.userProfile);
        userName = (TextInputLayout) findViewById(R.id.name);
        userEmail = (TextInputLayout) findViewById(R.id.email);
        userPhone = (TextInputLayout) findViewById(R.id.phone);
        userBirthday = (TextInputLayout) findViewById(R.id.date);
        changeBtn = (Button) findViewById(R.id.change);

        userEmail.getEditText().setFocusable(false);
        userEmail.getEditText().setClickable(false);

        userBirthday.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                Calendar currentDate = Calendar.getInstance();
                int mYear = currentDate.get(Calendar.YEAR);
                int mMonth = currentDate.get(Calendar.MONTH);
                int mDay = currentDate.get(Calendar.DAY_OF_MONTH);
                userBirthday.getEditText().setText("");
                DatePickerDialog mDatePicker = new DatePickerDialog(EditUserProfileActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                                Calendar myCalendar = Calendar.getInstance();
                                myCalendar.set(Calendar.YEAR, selectedyear);
                                myCalendar.set(Calendar.MONTH, selectedmonth);
                                myCalendar.set(Calendar.DAY_OF_MONTH, selectedday);
                                String myFormat = "dd/MM/yyyy";
                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                                userBirthday.getEditText().setText(sdf.format(myCalendar.getTime()));

                            }
                        }, mYear, mMonth, mDay);
                mDatePicker.show();
            }
        });

        progressDialog = new ProgressDialog(this);
        preferenceConfig = new SharedPreferenceConfig(this);
        final Map<String, Object> currentUserMap = preferenceConfig.getCurrentUser();

        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        alertBuilder = new AlertDialog.Builder(this);

        userEmail.getEditText().setText(currentUserMap.get("email").toString());
        userBirthday.getEditText().setText(currentUserMap.get("birthday").toString());
        userPhone.getEditText().setText(currentUserMap.get("phone").toString());
        userName.getEditText().setText(currentUserMap.get("name").toString());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.profile);
        Glide.with(EditUserProfileActivity.this).applyDefaultRequestOptions(requestOptions).load(currentUserMap.get("profile_url").toString()).thumbnail(
                Glide.with(EditUserProfileActivity.this).load(currentUserMap.get("profileThumb").toString())
        ).into(userImage);

        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name, birthday, phone, randomName;
                name = userName.getEditText().getText().toString().trim();
                birthday = userBirthday.getEditText().getText().toString().trim();
                phone = userPhone.getEditText().getText().toString().trim();
                randomName = Long.toString(System.currentTimeMillis());

                if(!missingValue(name, birthday, phone)) {

                    progressDialog.setCancelable(false);
                    progressDialog.setTitle("Submit changing");
                    progressDialog.setMessage("Loading");
                    progressDialog.show();
                    final StorageReference userProfileReference = storageReference.child("profile_images")
                            .child(randomName + ".jpg");

                    if (userImageURI != null) {
                        UploadTask userProfileUploadTask = userProfileReference.putFile(userImageURI);
                        Task<Uri> userProfileUriTask = userProfileUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
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
                                if (task.isSuccessful()) {

                                    downloadUri = task.getResult();

                                    File newImageFile = new File(userImageURI.getPath());
                                    try {
                                        compressedImageFile = new Compressor(EditUserProfileActivity.this)
                                                .compressToBitmap(newImageFile);
                                    } catch (IOException e) {
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
                                            if (!task.isSuccessful()) {
                                                return null;
                                            } else {
                                                return userProfileThumbReference.getDownloadUrl();
                                            }
                                        }
                                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()) {

                                                downloadThumbUri = task.getResult();
                                                currentUserMap.put("name", name);
                                                currentUserMap.put("email", userEmail.getEditText().getText().toString());
                                                currentUserMap.put("birthday", birthday);
                                                currentUserMap.put("phone", phone);
                                                currentUserMap.put("profile_url", downloadUri.toString());
                                                currentUserMap.put("profileThumb", downloadThumbUri.toString());

                                                preferenceConfig.setCurrentUser(currentUserMap);

                                                firestore.collection("Users").document(preferenceConfig.getSharedPrefConfig()).update(currentUserMap)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                    intent.putExtra("TOP", true);
                                                                    startActivity(intent);
                                                                    progressDialog.dismiss();
                                                                }
                                                                else{
                                                                    progressDialog.dismiss();
                                                                }
                                                            }
                                                        });


                                            } else {
                                                progressDialog.dismiss();
                                            }
                                        }
                                    });

                                } else {
                                    progressDialog.dismiss();
                                }
                            }
                        });
                    }
                    else {
                        currentUserMap.put("name", name);
                        currentUserMap.put("email", userEmail.getEditText().getText().toString());
                        currentUserMap.put("birthday", birthday);
                        currentUserMap.put("phone", phone);

                        preferenceConfig.setCurrentUser(currentUserMap);

                        firestore.collection("Users").document(preferenceConfig.getSharedPrefConfig()).update(currentUserMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.putExtra("TOP", true);
                                            startActivity(intent);
                                            progressDialog.dismiss();
                                        } else {
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                    }
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

            if(name.equals("")){
                userName.setError("Enter your name");
            }
            else if(date.equals("")){
                userBirthday.setError("Enter your birthday");
            }
            else{
                userPhone.setError("Enter your phone");
            }

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
