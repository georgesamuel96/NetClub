package com.egcoders.technologysolution.netclub.data;

import android.app.Activity;
import android.util.Log;
import android.util.Pair;

import com.egcoders.technologysolution.netclub.Utils.Utils;
import com.egcoders.technologysolution.netclub.model.Category;
import com.egcoders.technologysolution.netclub.model.CreatePostResponse;
import com.egcoders.technologysolution.netclub.model.GetPostResponse;
import com.egcoders.technologysolution.netclub.model.Post;
import com.egcoders.technologysolution.netclub.model.PostResponse;
import com.egcoders.technologysolution.netclub.model.UpdatePostResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostPresenter implements AddPost.Presenter {

    private Activity activity;
    private AddPost.View view;
    private Utils utils;
    private UserSharedPreference preference;

    public PostPresenter(Activity activity, AddPost.View view){
        this.activity  = activity;
        this.view = view;
        utils = new Utils(activity);
        preference = new UserSharedPreference(activity.getApplicationContext());
    }

    @Override
    public void setPost(Post post) {

        utils.showProgressDialog("Create Post", "Loading");

        String token = preference.getUser().getData().getToken();

        ApiManager.getInstance().createPost(token, post.getPhotoUrl(), post, new Callback<CreatePostResponse>() {
            @Override
            public void onResponse(Call<CreatePostResponse> call, Response<CreatePostResponse> response) {

                utils.hideProgressDialog();

                CreatePostResponse postResponse = response.body();

                if(postResponse.getSuccess()){
                    activity.finish();
                }
                else {
                    utils.showMessage("Create post", postResponse.getMessage());
                }
            }

            @Override
            public void onFailure(Call<CreatePostResponse> call, Throwable t) {
                utils.hideProgressDialog();
                utils.showMessage("Create post", t.getMessage());
            }
        });
    }

    @Override
    public void getPost(int postId) {

        utils.showProgressDialog("Get Post", "Loading");

        String token = preference.getUser().getData().getToken();

        ApiManager.getInstance().showPost(token, postId, new Callback<GetPostResponse>() {
            @Override
            public void onResponse(Call<GetPostResponse> call, Response<GetPostResponse> response) {

                utils.hideProgressDialog();

                GetPostResponse postResponse = response.body();

                if(postResponse.getSuccess()){
                    view.showPost(postResponse.getData());
                }
                else {
                    Log.v("Get post", postResponse.getMessage());
                }
            }

            @Override
            public void onFailure(Call<GetPostResponse> call, Throwable t) {

                utils.hideProgressDialog();
                Log.v("Get post error", t.getMessage());
            }
        });
    }

    @Override
    public void getCategories() {

        utils.showProgressDialog("", "Loading");

        final List<Pair<String, Integer>> list = new ArrayList<>();

        String token = preference.getUser().getData().getToken();

        ApiManager.getInstance().showCategories(token, new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {

                Category category = response.body();
                utils.hideProgressDialog();

                if(category.getSuccess()){
                    for(int i=0; i < category.getData().size(); i++){
                        list.add(Pair.create(category.getData().get(i).getName(), category.getData().get(i).getId()));
                    }
                    view.showCategories(list);
                }
                else {
                    utils.showMessage("Categories", category.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {

                utils.hideProgressDialog();
                utils.showMessage("Categories", t.getMessage());
            }
        });
    }

    @Override
    public void updatePost(Post post) {

        utils.showProgressDialog("Update Post", "Loading");

        String token = preference.getUser().getData().getToken();

        ApiManager.getInstance().updatePost(token, post.getPhotoUrl(), post, new Callback<UpdatePostResponse>() {
            @Override
            public void onResponse(Call<UpdatePostResponse> call, Response<UpdatePostResponse> response) {

                utils.hideProgressDialog();

                UpdatePostResponse postResponse = response.body();

                if(postResponse.getSuccess()){
                    activity.finish();
                }
                else {
                    utils.showMessage("Update post", postResponse.getMessage());
                }
            }

            @Override
            public void onFailure(Call<UpdatePostResponse> call, Throwable t) {
                utils.hideProgressDialog();
                utils.showMessage("Update post", t.getMessage());
            }
        });
    }
}