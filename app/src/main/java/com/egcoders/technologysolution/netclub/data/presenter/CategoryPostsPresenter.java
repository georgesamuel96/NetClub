package com.egcoders.technologysolution.netclub.data.presenter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.egcoders.technologysolution.netclub.Utils.SharedPreferenceConfig;
import com.egcoders.technologysolution.netclub.Utils.UserSharedPreference;
import com.egcoders.technologysolution.netclub.Utils.Utils;
import com.egcoders.technologysolution.netclub.data.interfaces.CategoryPosts;
import com.egcoders.technologysolution.netclub.model.category.Category;
import com.egcoders.technologysolution.netclub.model.category.CategorySelectedData;
import com.egcoders.technologysolution.netclub.model.post.CheckSavedResponse;
import com.egcoders.technologysolution.netclub.model.post.Post;
import com.egcoders.technologysolution.netclub.model.post.PostData;
import com.egcoders.technologysolution.netclub.model.post.PostResponse;
import com.egcoders.technologysolution.netclub.remote.ApiManager;
import com.egcoders.technologysolution.netclub.remote.ClientApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

public class CategoryPostsPresenter implements CategoryPosts.Presenter {

    private static final String TAG = CategoryPostsPresenter.class.getSimpleName();
    private CategoryPosts.View view;
    private Activity activity;
    private List<Post> postsList = new ArrayList<>();
    private List<String> categoryList = new ArrayList<>();
    private DocumentSnapshot lastVisible;
    private FirebaseFirestore firestore;
    private Thread[] threads = new Thread[4];
    private volatile int countPosts;
    private SharedPreferenceConfig preferenceConfig;
    private final String token;
    private UserSharedPreference preference;
    private CompositeDisposable disposable;
    private ConnectableObservable<List<Post>> observableListPost;
    private List<Post> postList = new ArrayList<>();
    private ClientApi clientApi;
    private final int userId;
    private boolean isLoadFirstTime;
    private String nextPage;

    public CategoryPostsPresenter(Activity activity, CategoryPosts.View view){
        this.view = view;
        this.activity = activity;
        firestore = FirebaseFirestore.getInstance();
        preferenceConfig = new SharedPreferenceConfig(activity);
        preference = new UserSharedPreference(activity.getApplicationContext());
        token = preference.getUser().getData().getToken();
        userId = preference.getUser().getData().getId();
        disposable = new CompositeDisposable();
        clientApi = ApiManager.getClient().create(ClientApi.class);
    }

    @Override
    public void loadPosts(final int category_id) {
        isLoadFirstTime = true;
        postList.clear();
        ApiManager.getInstance().showPostsCategory(token, category_id, new Callback<PostResponse>() {
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
                                    view.viewPosts(data.getData());
                                    isLoadFirstTime = false;
                                }
                                else{
                                    view.viewMorePosts(data.getData());
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

    private Observable<List<Post>> getPostObservable() {
        return Observable.fromArray(postList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void loadMorePosts(final int categoryId) {
        if(nextPage == null)
            return;
        nextPage = Utils.getUrl(nextPage);
        ApiManager.getInstance().showMorePostsCategory(token, categoryId, nextPage, new Callback<PostResponse>() {
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
    public void loadCategories() {
        ApiManager.getInstance().showCategories(token, new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                if(response.isSuccessful() && response.isSuccessful()){
                    Category categories = response.body();
                    for(CategorySelectedData name : categories.getData()){
                        String categoryName = name.getName();
                        categoryList.add(categoryName);
                    }
                    view.viewCategories(categoryList);
                }
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {

            }
        });
    }

    private void getPost(Query query) {

        query.addSnapshotListener(activity, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(e == null) {

                    if (!queryDocumentSnapshots.isEmpty()) {

                        lastVisible = queryDocumentSnapshots.getDocuments()
                                .get(queryDocumentSnapshots.size() - 1);

                        countPosts = queryDocumentSnapshots.getDocumentChanges().size();

                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                Map<String, Object> postMap = doc.getDocument().getData();
                                final Post post = new Post();
                                post.setPostId(doc.getDocument().getId());
                                post.setCategory(postMap.get("category").toString());
                                post.setContent(postMap.get("content").toString());
                                post.setPhotoUrl(postMap.get("photoUrl").toString());
                                post.setPhotoThumbUrl(postMap.get("photoThumbUrl").toString());
                                post.setTimeStamp(Long.parseLong(postMap.get("timeStamp").toString()));
                                post.setUserId(postMap.get("userId").toString());
                                firestore.collection("Users").document(post.getUserId()).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful()){
                                                    Map<String, Object> userMap = task.getResult().getData();
                                                    post.setUserName(userMap.get("name").toString());
                                                    post.setUserProfileUrl(userMap.get("profile_url").toString());
                                                    post.setUserProfileThumbUrl(userMap.get("profileThumb").toString());
                                                    if(userMap.containsKey("userStatue"))
                                                        post.setUserStatue(userMap.get("userStatue").toString());
                                                    else
                                                        post.setUserStatue("0");

                                                    postsList.add(post);
                                                    countPosts--;
                                                }
                                                else {

                                                }
                                            }
                                        });
                            }
                        }
                    }
                    else{
                        countPosts = 0;
                    }
                }
                else{
                    //countPosts = 0;
                }

            }
        });

    }
}
