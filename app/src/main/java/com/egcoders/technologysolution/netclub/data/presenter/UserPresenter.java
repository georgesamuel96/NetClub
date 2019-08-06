package com.egcoders.technologysolution.netclub.data.presenter;

import android.app.Activity;
import android.util.Log;

import com.egcoders.technologysolution.netclub.Utils.Utils;
import com.egcoders.technologysolution.netclub.data.interfaces.UserProfile;
import com.egcoders.technologysolution.netclub.Utils.UserSharedPreference;
import com.egcoders.technologysolution.netclub.model.post.PostData;
import com.egcoders.technologysolution.netclub.model.post.PostResponse;
import com.egcoders.technologysolution.netclub.model.profile.UserData;
import com.egcoders.technologysolution.netclub.remote.ApiManager;

import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserPresenter implements UserProfile.Presenter {

    private UserProfile.View view;
    private Activity activity;
    private UserSharedPreference preference;
    private String nextPageUserPosts, nextPageUserSaves;
    private final String token;
    private final int user_id;

    public UserPresenter(Activity activity, UserProfile.View view){
        this.view = view;
        this.activity = activity;
        preference = new UserSharedPreference(activity.getApplicationContext());
        token = preference.getUser().getData().getToken();
        user_id = preference.getUser().getData().getId();
    }

    @Override
    public void showUserData() {

        UserData user = preference.getUser().getData();
        view.showUserData(user);
    }

    @Override
    public void setUserDataWithPhoto(UserData user, String imagePath) {

    }

    @Override
    public void setUserDataNoPhoto(UserData user) {

    }

    @Override
    public void getUserPosts() {
        ApiManager.getInstance().showPostsUser(token, user_id, new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                PostResponse postResponse = response.body();
                try{
                    if(postResponse.getSuccess()){
                        PostData postData = postResponse.getData();
                        nextPageUserPosts = postData.getNext_page_url();
                        view.showUserPosts(postData);
                    }
                    else{
                        Log.v("Get post", postResponse.getMessage());
                    }
                }
                catch (Exception e){
                    Log.v("Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                String message;
                if(t instanceof SocketTimeoutException)
                    message = "Please try again.";
                else
                    message = t.getMessage();
                Log.v("Get posts Error", message);
            }
        });
    }

    @Override
    public void getMorePosts() {
        if(nextPageUserPosts == null)
            return;
        nextPageUserPosts = Utils.getUrl(nextPageUserPosts);
        ApiManager.getInstance().showMorePostsUser(token, user_id, nextPageUserPosts, new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                PostResponse postResponse = response.body();
                try {
                    if (postResponse.getSuccess()) {

                        PostData data = postResponse.getData();
                        view.showMorePosts(data);
                        nextPageUserPosts = data.getNext_page_url();
                    } else {
                        Log.v("Get post", postResponse.getMessage());
                    }
                }
                catch (Exception e){
                    Log.v("Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                String message;
                if(t instanceof SocketTimeoutException)
                    message = "Please try again.";
                else
                    message = t.getMessage();
                Log.v("Get more posts Error", message);
            }
        });
    }

    @Override
    public void getUserSavePosts() {
        ApiManager.getInstance().getSavedPosts(token, new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                PostResponse postResponse = response.body();
                try{
                    if(postResponse.getSuccess()){
                        PostData postData = postResponse.getData();
                        nextPageUserSaves = postData.getNext_page_url();
                        view.showUserSavePosts(postData);
                    }
                    else{
                        Log.v("Get post", postResponse.getMessage());
                    }
                }
                catch (Exception e){
                    Log.v("Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                String message;
                if(t instanceof SocketTimeoutException)
                    message = "Please try again.";
                else
                    message = t.getMessage();
                Log.v("Get posts Error", message);
            }
        });
    }

    @Override
    public void getMoreSavePosts() {
        if(nextPageUserSaves == null)
            return;
        nextPageUserSaves = Utils.getUrl(nextPageUserSaves);
        ApiManager.getInstance().showMoreSavedPosts(token, nextPageUserSaves, new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                PostResponse postResponse = response.body();
                try {
                    if (postResponse.getSuccess()) {
                        PostData data = postResponse.getData();
                        view.showMoreSavePosts(data);
                        nextPageUserSaves = data.getNext_page_url();
                    } else {
                        Log.v("Get post", postResponse.getMessage());
                    }
                }
                catch (Exception e){
                    Log.v("Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                String message;
                if(t instanceof SocketTimeoutException)
                    message = "Please try again.";
                else
                    message = t.getMessage();
                Log.v("Get more posts Error", message);
            }
        });
    }

}
