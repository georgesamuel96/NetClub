package com.egcoders.technologysolution.netclub.data.presenter;

import android.app.Activity;
import android.os.Parcelable;
import android.util.Log;

import com.egcoders.technologysolution.netclub.R;
import com.egcoders.technologysolution.netclub.Utils.Utils;
import com.egcoders.technologysolution.netclub.data.interfaces.Message;
import com.egcoders.technologysolution.netclub.data.interfaces.UserProfile;
import com.egcoders.technologysolution.netclub.Utils.UserSharedPreference;
import com.egcoders.technologysolution.netclub.model.post.CheckSavedResponse;
import com.egcoders.technologysolution.netclub.model.post.Post;
import com.egcoders.technologysolution.netclub.model.post.PostData;
import com.egcoders.technologysolution.netclub.model.post.PostResponse;
import com.egcoders.technologysolution.netclub.model.post.SavePostResponse;
import com.egcoders.technologysolution.netclub.model.post.UpdatePostResponse;
import com.egcoders.technologysolution.netclub.model.profile.UserData;
import com.egcoders.technologysolution.netclub.model.profile.UserResponse;
import com.egcoders.technologysolution.netclub.remote.ApiManager;
import com.egcoders.technologysolution.netclub.remote.ClientApi;
import com.google.android.gms.common.api.Api;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserPresenter implements UserProfile.Presenter {
    private static final String TAG = UserPresenter.class.getSimpleName();
    private UserProfile.View view;
    private Activity activity;
    private UserSharedPreference preference;
    private String nextPageUserPosts, nextPageUserSaves;
    private final String token;
    private final int user_id;
    private List<Post> postList = new ArrayList<>();
    private CompositeDisposable disposable;
    private ConnectableObservable<List<Post>> observableListPost;
    private ClientApi clientApi;
    private boolean isLoadFirstTime;
    private FirebaseFirestore firestore;
    private Utils utils;
    private Message message;

    public UserPresenter(Activity activity, UserProfile.View view, Message message){
        this.view = view;
        this.activity = activity;
        this.message = message;
        preference = new UserSharedPreference(activity.getApplicationContext());
        token = preference.getUser().getData().getToken();
        user_id = preference.getUser().getData().getId();
        clientApi = ApiManager.getClient().create(ClientApi.class);
        isLoadFirstTime = true;
        disposable = new CompositeDisposable();
        firestore = FirebaseFirestore.getInstance();
        utils = new Utils(activity);
    }

    @Override
    public void showUserData() {

        UserData user = preference.getUser().getData();
        view.showUserData(user);
    }

    @Override
    public void setUserData(UserData user, String imagePath) {
        ApiManager.getInstance().updateUserProfile(token, imagePath, user, new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {

                if(response.isSuccessful() && response.body() != null){
                    UserResponse userResponse = response.body();
                    if(userResponse.getSuccess()){
                        userResponse.getData().setSelectedCategory(true);
                        userResponse.getData().setToken(token);
                        preference.setUser(userResponse);
                        message.successMessage(userResponse.getData());
                    }
                    else {
                        message.failMessage();
                    }
                }
                else{
                    utils.showMessage(activity.getString(R.string.update_user_profile), activity.getString(R.string.something_wrong));
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                String message;
                if(t instanceof SocketTimeoutException)
                    message = "Please try again.";
                else
                    message = t.getMessage();
                utils.showMessage(activity.getString(R.string.update_user_profile), message);
            }
        });
    }

    @Override
    public void getUserPosts() {
        isLoadFirstTime = true;
        postList.clear();
        ApiManager.getInstance().showPostsUser(token, user_id, new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                PostResponse postResponse = response.body();
                try{
                    if(postResponse.getSuccess()){
                        PostData data = postResponse.getData();
                        nextPageUserPosts = data.getNext_page_url();
                        postList.addAll(data.getData());
                        observeData();
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

    private void observeData() {
        observableListPost = getPostObservable().replay();
        disposable.add(
                observableListPost.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<List<Post>>() {
                            @Override
                            public void onNext(List<Post> posts) {
                                getSavedPosts();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d(TAG, "onError: get user posts" + e.getMessage());
                            }

                            @Override
                            public void onComplete() {

                            }
                        })
        );
        observableListPost.connect();
    }

    private Observable<List<Post>> getPostObservable() {
        return Observable.fromArray(postList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void getSavedPosts() {
        disposable.add(
                observableListPost.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .concatMap(new Function<List<Post>, ObservableSource<Post>>() {
                            @Override
                            public ObservableSource<Post> apply(List<Post> posts) throws Exception {
                                return Observable.fromIterable(posts);
                            }
                        })
                        .concatMap(new Function<Post, ObservableSource<Post>>() {
                            @Override
                            public ObservableSource<Post> apply(Post post) throws Exception {
                                return getSavedPostsObservavle(post);
                            }
                        })
                        .subscribeWith(new DisposableObserver<Post>() {
                            @Override
                            public void onNext(Post post) {
                                Log.d(TAG, "onNext: post id: " + post.getId());
                                int position = postList.indexOf(post);
                                if (position == -1) {
                                    return;
                                }
                                postList.set(position, post);
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {
                                PostData data = new PostData();
                                data.setData(postList);
                                if(isLoadFirstTime) {
                                    view.showUserPosts(data);
                                    isLoadFirstTime = false;
                                }
                                else{
                                    view.showMorePosts(data);
                                }
                            }
                        })
        );
    }

    private Observable<Post> getSavedPostsObservavle(final Post post) {
        return clientApi
                .checkSavedPost(token, post.getId(), user_id)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<CheckSavedResponse, Post>() {
                    @Override
                    public Post apply(CheckSavedResponse checkSavedResponse) throws Exception {
                        Log.d(TAG, "apply: " + checkSavedResponse.getSuccess());
                        post.setSaved(checkSavedResponse.getSuccess());
                        firestore.collection("Posts").document(post.getId() + "")
                                .collection("Likes")
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                        if(e == null) {
                                            int count;
                                            if (!queryDocumentSnapshots.isEmpty()) {
                                                count = queryDocumentSnapshots.size();
                                            } else {
                                                count = 0;
                                            }
                                            post.setLikes(count);
                                        }
                                        else{
                                            System.out.println("error " + e.getMessage());
                                        }
                                    }
                                });
                        firestore.collection("Posts").document(post.getId() + "")
                                .collection("Comments")
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                        if(e == null) {
                                            int count;
                                            if (!queryDocumentSnapshots.isEmpty()) {
                                                count = queryDocumentSnapshots.size();
                                            } else {
                                                count = 0;
                                            }
                                            post.setComments(count);
                                        }
                                        else{
                                            System.out.println("error " + e.getMessage());
                                        }
                                    }
                                });
                        return post;
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
                        nextPageUserPosts = data.getNext_page_url();
                        postList.clear();
                        postList.addAll(data.getData());
                        observeData();
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
        ApiManager.getInstance().getSavedPosts(token, new Callback<SavePostResponse>() {
            @Override
            public void onResponse(Call<SavePostResponse> call, Response<SavePostResponse> response) {
                SavePostResponse postResponse = response.body();
                try{
                    if(postResponse.getSuccess()){
                        nextPageUserSaves = (String)postResponse.getData().getNextPageUrl();
                        view.showUserSavePosts(postResponse.getData());
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
            public void onFailure(Call<SavePostResponse> call, Throwable t) {
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
        nextPageUserPosts = Utils.getUrl(nextPageUserPosts);
        ApiManager.getInstance().showMoreSavedPosts(token, nextPageUserPosts, new Callback<SavePostResponse>() {
            @Override
            public void onResponse(Call<SavePostResponse> call, Response<SavePostResponse> response) {
                SavePostResponse postResponse = response.body();
                try {
                    if (postResponse.getSuccess()) {
                        nextPageUserSaves = (String)postResponse.getData().getNextPageUrl();
                        view.showMoreSavePosts(postResponse.getData());
                    } else {
                        Log.v("Get post", postResponse.getMessage());
                    }
                }
                catch (Exception e){
                    Log.v("Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<SavePostResponse> call, Throwable t) {
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
    public void clearDisposal() {
        disposable.clear();
    }

}
