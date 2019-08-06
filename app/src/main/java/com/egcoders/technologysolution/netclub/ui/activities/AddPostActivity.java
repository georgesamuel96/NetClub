package com.egcoders.technologysolution.netclub.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
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
import com.egcoders.technologysolution.netclub.Utils.GetImagePath;
import com.egcoders.technologysolution.netclub.Utils.Utils;
import com.egcoders.technologysolution.netclub.data.interfaces.AddPost;
import com.egcoders.technologysolution.netclub.data.presenter.PostPresenter;
import com.egcoders.technologysolution.netclub.data.adapter.SpinnerAdapter;
import com.egcoders.technologysolution.netclub.model.post.Post;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class AddPostActivity extends AppCompatActivity implements AddPost.View, View.OnClickListener {

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

        editPostId = getIntent().getIntExtra("postId", -1);
        init();

    }

    private void init() {
        spinner = findViewById(R.id.spinner);
        postImage = findViewById(R.id.postImage);
        contentTextInput = findViewById(R.id.content);
        postBtn = findViewById(R.id.addPost);
        deleteImage = findViewById(R.id.deleteImage);
        utils = new Utils(AddPostActivity.this);
        presenter = new PostPresenter(AddPostActivity.this, this);
        initToolbar();
        initSpinner();
        postImage.setOnClickListener(this);
        postBtn.setOnClickListener(this);
        deleteImage.setOnClickListener(this);
    }

    private void initSpinner() {
        adapter = new SpinnerAdapter(categoryListName, AddPostActivity.this);
        spinner.setAdapter(adapter);
        presenter.getCategories();
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
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(editPostId != -1){
            getSupportActionBar().setTitle(R.string.edit_post);
            presenter.getPost(editPostId);
        }
        else {
            getSupportActionBar().setTitle(R.string.add_post);
        }
    }

    private void setPost() {

        Post post = new Post();
        if(categoryId == -1){
            utils.showMessage(getString(R.string.choose_category), getString(R.string.msg_choose_category));
        }
        else{
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
            else
                utils.showMessage(getString(R.string.your_post), getString(R.string.msg_your_post));
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
        else
            postImage.setImageResource(R.drawable.placeholder);
        String content = post.getContent().substring(0, post.getContent().length() - 1);
        contentTextInput.getEditText().setText(content);
    }

    @Override
    public void showCategories(List<Pair<String, Integer>> list) {
        list.add(0, Pair.create(getString(R.string.choose_category), -1));
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
            if (Build.VERSION.SDK_INT < 11) {
                imagePath = GetImagePath.getRealPathFromURI_BelowAPI11(getApplicationContext(), path);
            } else if (Build.VERSION.SDK_INT < 19) {
                imagePath = GetImagePath.getRealPathFromURI_API11to18(getApplicationContext(), path);
            } else {
                imagePath = GetImagePath.getRealPathFromURI_API19(getApplicationContext(), data.getData());            }

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                postImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.postImage:
                chooseImage();
                break;
            case R.id.addPost:
                setPost();
                break;
            case R.id.deleteImage:
                postImage.setImageResource(R.drawable.placeholder);
                break;
        }
    }
}
