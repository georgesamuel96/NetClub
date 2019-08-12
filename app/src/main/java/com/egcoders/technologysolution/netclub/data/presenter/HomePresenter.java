package com.egcoders.technologysolution.netclub.data.presenter;

import android.app.Activity;
import android.util.Log;

import com.egcoders.technologysolution.netclub.Utils.Utils;
import com.egcoders.technologysolution.netclub.Utils.UserSharedPreference;
import com.egcoders.technologysolution.netclub.data.interfaces.Home;
import com.egcoders.technologysolution.netclub.model.post.CheckSavedResponse;
import com.egcoders.technologysolution.netclub.model.post.Post;
import com.egcoders.technologysolution.netclub.model.post.PostData;
import com.egcoders.technologysolution.netclub.model.post.PostResponse;
import com.egcoders.technologysolution.netclub.remote.ApiManager;
import com.egcoders.technologysolution.netclub.remote.ClientApi;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePresenter implements Home.Presenter {

    private static final String TAG = HomePresenter.class.getSimpleName();
    private Home.View view;
    private Activity activity;
    private UserSharedPreference preference;
    private Utils utils;
    private String nextPage;
    private final String token;
    private CompositeDisposable disposable;
    private ConnectableObservable<List<Post>> observableListPost;
    private List<Post> postList = new ArrayList<>();
    private ClientApi clientApi;
    private final int userId;
    private boolean isLoadFirstTime;

    public HomePresenter(Activity activity, Home.View view){
        this.view = view;
        this.activity = activity;
        preference = new UserSharedPreference(activity.getApplicationContext());
        utils = new Utils(activity);
        token = preference.getUser().getData().getToken();
        userId = preference.getUser().getData().getId();
        disposable = new CompositeDisposable();
        clientApi = ApiManager.getClient().create(ClientApi.class);
    }

    @Override
    public void loadPosts() {
        isLoadFirstTime = true;
        postList.clear();
        ApiManager.getInstance().showPosts(token, new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                PostResponse postResponse = response.body();
                try {
                    if (postResponse.getSuccess()) {
                        PostData data = postResponse.getData();
                        nextPage = data.getNext_page_url();
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

                    }

                    @Override
                    public void onComplete() {

                    }
                })
        );
        observableListPost.connect();
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
                            view.showPosts(data);
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
                .checkSavedPost(token, post.getId(), userId)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<CheckSavedResponse, Post>() {
                    @Override
                    public Post apply(CheckSavedResponse checkSavedResponse) throws Exception {
                        Log.d(TAG, "apply: " + checkSavedResponse.getSuccess());
                        post.setSaved(checkSavedResponse.getSuccess());
                        return post;
                    }
                });
    }

    private Observable<List<Post>> getPostObservable() {
        return Observable.fromArray(postList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void loadMorePosts() {
        if(nextPage == null)
            return;
        nextPage = Utils.getUrl(nextPage);
        ApiManager.getInstance().showMorePosts(token, nextPage, new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                PostResponse postResponse = response.body();
                try {
                    if (postResponse.getSuccess()) {
                        PostData data = postResponse.getData();
                        nextPage = data.getNext_page_url();
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
    public void clearDisposal() {
        disposable.clear();
    }
}