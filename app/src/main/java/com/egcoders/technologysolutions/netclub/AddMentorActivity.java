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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class AddMentorActivity extends AppCompatActivity {

    private CurrentMentor mentor;
    private CircleImageView mentorImage;
    private Uri mentorImageUri = null, downloadUri, dowbloadThumbUri;
    private EditText mentorName, mentorEmail, mentorPhone;
    private ProgressBar progressBar;
    private AlertDialog.Builder alertBuilder;
    private FirebaseFirestore firestore;
    private ArrayList<String> categories = new ArrayList<>();
    private String itemCategory;
    private ImageView continueBtn;
    private StorageReference storageReference;
    private Bitmap compressedImageFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mentor);

        mentor = new CurrentMentor();
        alertBuilder = new AlertDialog.Builder(this);
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        mentorName = (EditText) findViewById(R.id.mentor_name);
        mentorEmail = (EditText) findViewById(R.id.mentor_email);
        mentorPhone = (EditText) findViewById(R.id.mentor_phone);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        continueBtn = (ImageView) findViewById(R.id.next_btn);
        mentorImage = (CircleImageView) findViewById(R.id.mentor_image);

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name, email, phone;
                name = mentorName.getText().toString().trim();
                email = mentorEmail.getText().toString().trim();
                phone = mentorPhone.getText().toString().trim();

                if(!missingValue(name, email, phone)){

                    final String randomName = Long.toString(System.currentTimeMillis());
                    progressBar.setVisibility(View.VISIBLE);
                    final StorageReference mentorProfileReference = storageReference.child("mentor_images")
                            .child(randomName + ".jpg");
                    UploadTask mentorProfileUploadTask = mentorProfileReference.putFile(mentorImageUri);
                    Task<Uri> mentorProfileUriTask = mentorProfileUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                            return mentorProfileReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {

                                downloadUri = task.getResult();

                                File newImageFile = new File(mentorImageUri.getPath());
                                try {
                                    compressedImageFile = new Compressor(AddMentorActivity.this)
                                            .compressToBitmap(newImageFile);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] thumbData = baos.toByteArray();

                                final StorageReference mentorProfileThumbReference = storageReference.child("mentor_images/thumbs").
                                        child(randomName + "thumbs.jpg");
                                UploadTask mentorProfileThumbUploadTask = mentorProfileThumbReference
                                        .putBytes(thumbData);
                                Task<Uri> mentorProfileThumbUriTask = mentorProfileThumbUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                    @Override
                                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                        if (!task.isSuccessful()) {
                                            return null;
                                        } else {
                                            return mentorProfileThumbReference.getDownloadUrl();
                                        }
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {

                                            dowbloadThumbUri = task.getResult();

                                            Intent i = new Intent(AddMentorActivity.this, MentorCategoriesActivity.class);
                                            mentor.setName(name);
                                            mentor.setEmail(email);
                                            mentor.setPhone(phone);
                                            mentor.setImage_url(downloadUri.toString());
                                            mentor.setImageThumb_url(dowbloadThumbUri.toString());

                                            /*HashMap<String, String> hashMap = new HashMap<>();
                                            hashMap.put("name", name);
                                            hashMap.put("email", email);
                                            hashMap.put("phone", phone);
                                            hashMap.put("profile_url", downloadUri.toString());
                                            hashMap.put("profileThumb_url", dowbloadThumbUri.toString());*/

                                            //i.putExtra("mentor", mentor);
                                            startActivity(i);
                                        } else {

                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

        mentorImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(ContextCompat.checkSelfPermission(AddMentorActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(AddMentorActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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

    private Boolean missingValue(String name, String email, String phone){

        if(name.equals("") || email.equals("") || phone.equals("") || mentorImageUri == null){

            alertBuilder.setTitle("Missing Value");
            alertBuilder.setMessage("There is missing value");
            alertBuilder.setCancelable(false);
            alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mentorName.setText("");
                    mentorEmail.setText("");
                    mentorPhone.setText("");
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mentorImageUri = result.getUri();
                mentorImage.setImageURI(mentorImageUri);
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
                .start(AddMentorActivity.this);
    }

}
