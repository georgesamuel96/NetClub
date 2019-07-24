package com.egcoders.technologysolution.netclub.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.egcoders.technologysolution.netclub.R;
import com.egcoders.technologysolution.netclub.Utils.Utils;
import com.egcoders.technologysolution.netclub.data.AddPost;
import com.egcoders.technologysolution.netclub.data.PostPresenter;
import com.egcoders.technologysolution.netclub.data.SharedPreferenceConfig;
import com.egcoders.technologysolution.netclub.data.SpinnerAdapter;
import com.egcoders.technologysolution.netclub.data.UserSharedPreference;
import com.egcoders.technologysolution.netclub.model.Post;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;


public class AddPostActivity extends AppCompatActivity implements AddPost.View {

    private List<Pair<String, Integer>> categoryList = new ArrayList<>();
    private List<String> categoryListName = new ArrayList<>();
    private Spinner spinner;
    private SpinnerAdapter adapter;
    private ImageView postImage, deleteImage;
    private TextInputLayout contentTextInput;
    private Button postBtn;
    private int editPostId;
    private android.support.v7.widget.Toolbar toolbar;
    private AddPost.Presenter presenter;
    private String categoryName;
    private int categoryId;
    private Utils utils;
    private static final int IMG_REQUEST = 777;
    private Bitmap bitmap = null;
    private String imagePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        spinner = (Spinner) findViewById(R.id.spinner);
        postImage = (ImageView) findViewById(R.id.postImage);
        contentTextInput = (TextInputLayout) findViewById(R.id.content);
        postBtn = (Button) findViewById(R.id.addPost);
        deleteImage = (ImageView) findViewById(R.id.deleteImage);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        presenter = new PostPresenter(AddPostActivity.this, this);
        adapter = new SpinnerAdapter(categoryListName, AddPostActivity.this);
        spinner.setAdapter(adapter);
        utils = new Utils(AddPostActivity.this);

        editPostId = getIntent().getIntExtra("postId", -1);
        if(editPostId != -1){
            presenter.getPost(editPostId);
        }
        else {

            getSupportActionBar().setTitle("Add Post");
        }

        presenter.getCategories();

        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
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
                categoryName = categoryList.get(position).first;
                categoryId = categoryList.get(position).second;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                categoryName = "";
            }
        });

        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postImage.setImageResource(R.drawable.placeholder);
            }
        });
    }

    private void setPost() {

        Post post = new Post();

        if(categoryId == -1){

            utils.showMessage("Choose Category", "Choose category for your post");
        }
        else {

            String postContent = contentTextInput.getEditText().getText().toString().trim() + "*";

            if (!postContent.equals("") || imagePath != null) {

                post.setCategory(categoryName);
                post.setCategoryId(categoryId);
                post.setContent(postContent);
                post.setPhotoUrl(imagePath);

                if(editPostId == -1)
                    presenter.setPost(post);
                else{
                    post.setId(editPostId);
                    presenter.updatePost(post);
                }
            }
            else {
                utils.showMessage("Your Post", "You must post content, image or both");
            }
        }

    }

    @Override
    public void showPost(Post post) {

        if(post.getPhotoUrl() != null){

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.placeholder);
            Glide.with(AddPostActivity.this).applyDefaultRequestOptions(requestOptions).load(post.getPhotoUrl())
                    .into(postImage);
        }
        else {
            postImage.setImageResource(R.drawable.placeholder);
        }

        String content = post.getContent().substring(0, post.getContent().length() - 1);
        contentTextInput.getEditText().setText(content);
    }

    @Override
    public void showCategories(List<Pair<String, Integer>> list) {

        list.add(0, Pair.create("Choose category", -1));
        this.categoryList = list;
        categoryName = list.get(0).first;
        categoryId = -1;
        for(int i = 0; i < list.size(); i++){
            categoryListName.add(list.get(i).first);
            adapter.notifyDataSetChanged();
        }
    }

    private void chooseImage(){
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(i.ACTION_GET_CONTENT);
        startActivityForResult(i, IMG_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMG_REQUEST && resultCode == RESULT_OK  && data != null)
        {
            Uri path = data.getData();
            imagePath = getRealPathFromURI_API19(data.getData());

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                postImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("NewApi")
    public String getRealPathFromURI_API19(Uri uri){

        String filePath = "";

        String wholeID = DocumentsContract.getDocumentId(uri);
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column,
                sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

}
