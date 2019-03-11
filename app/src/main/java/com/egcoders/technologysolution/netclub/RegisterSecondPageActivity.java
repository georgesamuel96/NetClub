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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class RegisterSecondPageActivity extends AppCompatActivity {

    private EditText userNameText;
    private EditText phoneText;
    private TextView birthdayText;
    private Button register;
    private CircleImageView userImage;
    private AlertDialog.Builder alertBuilder;
    private Uri userImageURI = null, downloadUri, downloadThumbUri;
    private String email, password;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private Bitmap compressedImageFile;
    private String currentUserId;
    private SharedPreferenceConfig preference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_second_page);

        alertBuilder = new AlertDialog.Builder(this);
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");

        userNameText = (EditText) findViewById(R.id.user_name);
        birthdayText = (TextView) findViewById(R.id.date);
        phoneText = (EditText) findViewById(R.id.phone);
        register = (Button) findViewById(R.id.register);
        userImage = (CircleImageView) findViewById(R.id.user_image);

        progressDialog = new ProgressDialog(this);
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        preference = new SharedPreferenceConfig(getApplicationContext());

        birthdayText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentDate = Calendar.getInstance();
                int mYear = currentDate.get(Calendar.YEAR);
                int mMonth = currentDate.get(Calendar.MONTH);
                int mDay = currentDate.get(Calendar.DAY_OF_MONTH);
                birthdayText.setText("");
                DatePickerDialog mDatePicker = new DatePickerDialog(RegisterSecondPageActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        Calendar myCalendar = Calendar.getInstance();
                        myCalendar.set(Calendar.YEAR, selectedyear);
                        myCalendar.set(Calendar.MONTH, selectedmonth);
                        myCalendar.set(Calendar.DAY_OF_MONTH, selectedday);
                        String myFormat = "dd/MM/yyyy";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                        birthdayText.setText(sdf.format(myCalendar.getTime()));

                    }
                }, mYear, mMonth, mDay);
                mDatePicker.show();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String userName = userNameText.getText().toString().trim();
                final String birthday = birthdayText.getText().toString().trim();
                final String phone = phoneText.getText().toString().trim();

                if(!missingValue(userName, birthday, phone)){

                    progressDialog.setCancelable(false);
                    progressDialog.setTitle("Create Account");
                    progressDialog.setMessage("Loading");
                    progressDialog.show();
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                currentUserId = task.getResult().getUser().getUid();
                                preference.setSharedPrefConfig(currentUserId);
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

                                                        preference.setCurrentUser(userName, email, phone, birthday,
                                                                downloadUri.toString(), downloadThumbUri.toString(), false);

                                                        Map<String, Object> userMap = preference.getCurrentUser();
                                                        userMap.put("name", userName);
                                                        userMap.put("birthday", birthday);
                                                        userMap.put("phone", phone);
                                                        userMap.put("profile_url", downloadUri.toString());
                                                        userMap.put("profileThumb", downloadThumbUri.toString());
                                                        userMap.put("categorySelected", false);
                                                        userMap.put("email", email);

                                                        firestore.collection("Users").document(preference.getSharedPrefConfig()).set(userMap)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if(task.isSuccessful()){

                                                                            progressDialog.dismiss();
                                                                            Intent i = new Intent(RegisterSecondPageActivity.this, CategoriesActivity.class);
                                                                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                            startActivity(i);
                                                                            finish();
                                                                        }
                                                                        else{
                                                                            progressDialog.dismiss();
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
                                        dialog.cancel();
                                    }
                                });
                                AlertDialog alertDialog = alertBuilder.create();
                                alertDialog.show();
                                progressDialog.dismiss();
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

            if(userName.equals("")){
                userNameText.setError("Enter your name");
            }
            else if(date.equals("")){
                birthdayText.setError("Enter your birthday");
            }
            else if(phone.equals("")){
                phoneText.setError("Enter Your phone");
            }
            else if(userImageURI == null){
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
        i.putExtra("backPressed", true);
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
                System.out.println(userImageURI);
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
