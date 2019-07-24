package com.egcoders.technologysolution.netclub.data;

import android.app.Activity;
import android.util.Log;

import com.egcoders.technologysolution.netclub.Utils.Utils;
import com.egcoders.technologysolution.netclub.model.PostData;
import com.egcoders.technologysolution.netclub.model.PostResponse;

import java.io.IOException;
import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePresenter implements Home.Presenter {

    private Home.View view;
    private Activity activity;
    private UserSharedPreference preference;
    private Utils utils;
    private String nextPage;

    public HomePresenter(Activity activity, Home.View view){
        this.view = view;
        this.activity = activity;

        preference = new UserSharedPreference(activity.getApplicationContext());
        utils = new Utils(activity);
    }

    @Override
    public void loadPosts() {


        String token = preference.getUser().getData().getToken();

        ApiManager.getInstance().showPosts(token, new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {

                PostResponse postResponse = response.body();

                try {
                    if (postResponse.getSuccess()) {

                        PostData data = postResponse.getData();
                        nextPage = data.getNext_page_url();
                        view.showPosts(data);
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

                Log.v("Get posts Error", message);
            }
        });
    }

    @Override
    public void loadMorePosts() {

        if(nextPage == null)
            return;

        String token = preference.getUser().getData().getToken();

        nextPage = Utils.getUrl(nextPage);

        ApiManager.getInstance().showMorePosts(token, nextPage, new Callback<PostResponse>() {
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
}