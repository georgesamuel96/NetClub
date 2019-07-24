package com.egcoders.technologysolution.netclub.data;

import android.app.Activity;
import android.util.Log;

import com.egcoders.technologysolution.netclub.Utils.Utils;
import com.egcoders.technologysolution.netclub.model.PostData;
import com.egcoders.technologysolution.netclub.model.PostResponse;
import com.egcoders.technologysolution.netclub.model.User;
import com.egcoders.technologysolution.netclub.model.UserData;
import com.egcoders.technologysolution.netclub.model.UserResponse;

import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserPresenter implements UserProfile.Presenter {

    private UserProfile.View view;
    private Activity activity;
    private UserSharedPreference preference;
    private String nextPage;

    public UserPresenter(Activity activity, UserProfile.View view){
        this.view = view;
        this.activity = activity;
        preference = new UserSharedPreference(activity.getApplicationContext());
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

        String token = preference.getUser().getData().getToken();
        int user_id = preference.getUser().getData().getId();

        ApiManager.getInstance().showPostsUser(token, user_id, new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {

                PostResponse postResponse = response.body();

                try{
                    if(postResponse.getSuccess()){

                        PostData postData = postResponse.getData();
                        nextPage = postData.getNext_page_url();
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

        if(nextPage == null)
            return;

        String token = preference.getUser().getData().getToken();
        int user_id = preference.getUser().getData().getId();
        nextPage = Utils.getUrl(nextPage);

        ApiManager.getInstance().showMorePostsUser(token, user_id, nextPage, new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {

                PostResponse postResponse = response.body();

                try {
                    if (postResponse.getSuccess()) {

                        PostData data = postResponse.getData();
                        view.showMorePosts(data);
                        nextPage = data.getNext_page_url();
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

    }

    @Override
    public void getMoreSavePosts() {

    }

}