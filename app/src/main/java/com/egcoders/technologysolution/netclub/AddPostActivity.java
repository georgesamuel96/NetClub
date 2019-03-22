package com.egcoders.technologysolution.netclub;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import id.zelory.compressor.Compressor;


public class AddPostActivity extends AppCompatActivity {

    private ArrayList<String> categoryList = new ArrayList<>();
    private Spinner spinner;
    private SpinnerAdapter adapter;
    private FirebaseFirestore firestore;
    private Uri postImageUri = null;
    private ImageView postImage;
    private TextInputLayout contentTextInput;
    private AlertDialog.Builder alertBuilder;
    private StorageReference storageReference;
    private Bitmap compressedImageFile;
    private Button postBtn;
    private String[] urls = new String[2];
    private String categoryName;
    private SharedPreferenceConfig preferenceConfig;
    private ProgressDialog progressDialog;
    private boolean isQueryingFinished = false;
    private Thread[] threads = new Thread[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        spinner = (Spinner) findViewById(R.id.spinner);
        postImage = (ImageView) findViewById(R.id.postImage);
        contentTextInput = (TextInputLayout) findViewById(R.id.content);
        postBtn = (Button) findViewById(R.id.post);

        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        adapter = new SpinnerAdapter(categoryList, this);
        spinner.setAdapter(adapter);
        alertBuilder = new AlertDialog.Builder(this);
        preferenceConfig = new SharedPreferenceConfig(this);
        progressDialog = new ProgressDialog(this);

        getCategories();

        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPost();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryName = categoryList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getCategories() {
        firestore.collection("Categories").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){;
                    for(DocumentChange document : task.getResult().getDocumentChanges()){
                        categoryList.add(document.getDocument().get("name").toString());
                        adapter.notifyDataSetChanged();
                    }
                    categoryName = categoryList.get(0);
                }
                else{
                }
            }
        });
    }

    private void setPost() {
        final String postContent = contentTextInput.getEditText().getText().toString().trim();
        if(!postContent.equals("") || postImageUri != null){
            //System.out.println("DONE:");
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Publishing post");
            progressDialog.setMessage("Loading");
            progressDialog.show();

            urls[0] = urls[1] = "";
            if(postImageUri != null)
                uploadImage();

            threads[3] = new Thread(new Runnable() {
                @Override
                public void run() {

                    while((urls[0].equals("")|| urls[1].equals("")) && postImageUri != null);

                    String random = Long.toString(System.currentTimeMillis());
                    Map<String, Object> postMap = new HashMap<>();
                    Map<String, Object> currentUser = preferenceConfig.getCurrentUser();

                    //postMap.put("userName", currentUser.get("name").toString());
                    //postMap.put("userProfile", currentUser.get("profile_url").toString());
                    //postMap.put("userProfileThumb", currentUser.get("profileThumb").toString());
                    postMap.put("userId", preferenceConfig.getSharedPrefConfig());
                    postMap.put("timeStamp", Long.parseLong(random));
                    postMap.put("photoUrl", urls[0]);
                    postMap.put("photoThumbUrl", urls[1]);
                    //postMap.put("likesNumber", "0");
                    postMap.put("category", categoryName);
                    postMap.put("content", postContent);
                    //postMap.put("userStatue", "0");


                    firestore.collection("Posts").document(random).set(postMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressDialog.dismiss();
                                finish();
                            }
                            else{
                                progressDialog.dismiss();
                            }
                        }
                    });

                }
            });
            threads[3].start();

        }
        else{
            alertBuilder.setTitle("Post");
            alertBuilder.setMessage("You must post content, image or both");
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

    private void uploadImage() {
        final String random = Long.toString(System.currentTimeMillis());
        final StorageReference postImageReference = storageReference.child("post_images").child(random + ".jpg");
        final UploadTask postImageUploadTask = postImageReference.putFile(postImageUri);

        File newImageFile = new File(postImageUri.getPath());
        try {
            compressedImageFile = new Compressor(AddPostActivity.this).compressToBitmap(newImageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] thumbData = baos.toByteArray();
        final StorageReference postImageThumbReference = storageReference.child("post_images/thumbs").
                child(random + ".jpg");
        final UploadTask postImageThumbUploadTask = postImageThumbReference.putBytes(thumbData);

        threads[0] = new Thread(new Runnable() {
            @Override
            public void run() {
                Task<Uri> postImageTask = postImageUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                        if(!task.isSuccessful()){
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
                        return postImageReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            urls[0] = task.getResult().toString();
                        }
                        else {

                        }
                    }
                });

            }
        });
        threads[0].start();

        threads[1] = new Thread(new Runnable() {
            @Override
            public void run() {


                    Task<Uri> postImageThumbTask = postImageThumbUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if(!task.isSuccessful()){
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
                            return postImageThumbReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                urls[1] = task.getResult().toString();
                            }
                            else {
                            }
                        }
                    });


            }
        });
        threads[1].start();

    }

    private void selectImage(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(AddPostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(AddPostActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
            else{
                imagePiker();
            }
        }
        else{
            imagePiker();
        }

    }

    private void imagePiker(){
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(AddPostActivity.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                postImageUri = result.getUri();
                postImage.setImageURI(postImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
