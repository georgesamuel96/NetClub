package com.egcoders.technologysolution.netclub;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Nullable;

public class UserPresenter implements UserProfile.Presenter {

    private User user;
    private UserProfile.View view;
    private SharedPreferenceConfig preferenceConfig;
    private Activity activity;
    private FirebaseFirestore firestore;
    private DocumentSnapshot lastVisiblePosts, lastVisibleSaves;
    private ArrayList<Post> userPostsList, userSavesList;
    private volatile int countUserPosts, countUserSaves;
    private Thread[] threads = new Thread[8];

    public UserPresenter(Activity activity, UserProfile.View view){
        user = new User();
        this.view = view;
        this.activity = activity;
        preferenceConfig = new SharedPreferenceConfig(activity);
        firestore = FirebaseFirestore.getInstance();
        userPostsList = new ArrayList<>();
        userSavesList = new ArrayList<>();
    }

    @Override
    public void showUserData() {
        view.showUserData(preferenceConfig.getCurrentUser());
    }

    @Override
    public void setUserData(Map<String, Object> userMap) {
        preferenceConfig.setCurrentUser(userMap);
    }

    @Override
    public void getUserPosts() {
        userPostsList.clear();
        countUserPosts = Integer.MAX_VALUE;


        threads[2] = new Thread(new Runnable() {
            @Override
            public void run() {
                Query query = firestore.collection("Posts").whereEqualTo("userId", preferenceConfig.getSharedPrefConfig())
                        .orderBy("timeStamp", Query.Direction.DESCENDING).limit(5);
                getUserPosts(query);
            }
        });
        threads[2].start();

        threads[3] = new Thread(new Runnable() {
            @Override
            public void run() {

                while (countUserPosts > 0);
                view.showUserPosts(userPostsList);
            }
        });
        threads[3].start();
    }

    @Override
    public void getMorePosts() {
        userPostsList.clear();
        countUserPosts = Integer.MAX_VALUE;

        threads[0] = new Thread(new Runnable() {
            @Override
            public void run() {
                Query query = firestore.collection("Posts")
                        .whereEqualTo("userId", preferenceConfig.getSharedPrefConfig()).orderBy("timeStamp", Query.Direction.DESCENDING).startAfter(lastVisiblePosts).limit(5);
                getUserPosts(query);
            }
        });
        threads[0].start();

        threads[1] = new Thread(new Runnable() {
            @Override
            public void run() {
                while (countUserPosts > 0);
                view.showMorePosts(userPostsList);
            }
        });
        threads[1].start();
    }

    @Override
    public void getUserSavePosts() {

        userSavesList.clear();
        countUserSaves = Integer.MAX_VALUE;

        threads[4] = new Thread(new Runnable() {
            @Override
            public void run() {
                Query query = firestore.collection("Users").document(preferenceConfig.getSharedPrefConfig())
                        .collection("Saves").orderBy("timestamp", Query.Direction.DESCENDING).limit(5);
                getUserSaves(query);
            }
        });
        threads[4].start();

        threads[5] = new Thread(new Runnable() {
            @Override
            public void run() {
                while(countUserSaves > 0);
                view.showUserSavePosts(userSavesList);
            }
        });
        threads[5].start();
    }

    @Override
    public void getMoreSavePosts() {
        userSavesList.clear();
        countUserSaves = Integer.MAX_VALUE;

        threads[6] = new Thread(new Runnable() {
            @Override
            public void run() {
                Query query = firestore.collection("Users").document(preferenceConfig.getSharedPrefConfig())
                        .collection("Saves").orderBy("timestamp", Query.Direction.DESCENDING)
                        .startAfter(lastVisibleSaves).limit(5);
                getUserSaves(query);
            }
        });
        threads[6].start();

        threads[7] = new Thread(new Runnable() {
            @Override
            public void run() {
                while(countUserSaves > 0);
                view.showMoreSavePosts(userSavesList);
            }
        });
        threads[7].start();
    }

    private void getUserPosts(Query query){

        query.addSnapshotListener(activity, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(e == null) {

                    if (!queryDocumentSnapshots.isEmpty()) {

                        lastVisiblePosts = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);

                        countUserPosts = queryDocumentSnapshots.getDocumentChanges().size();

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

                                                    userPostsList.add(post);
                                                    countUserPosts--;
                                                }
                                                else {

                                                }
                                            }
                                        });
                            }
                        }
                    }
                    else{
                        countUserPosts = 0;
                    }
                }
                else{
                    System.out.println(e.getMessage());
                    countUserPosts = 0;
                }

            }
        });

    }

    private void getUserSaves(Query query){
        query.addSnapshotListener(activity, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(e == null) {

                    if (!queryDocumentSnapshots.isEmpty()) {

                        lastVisibleSaves = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);

                        countUserSaves = queryDocumentSnapshots.getDocumentChanges().size();

                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                final Post post = new Post();
                                post.setPostId(doc.getDocument().getId());
                                firestore.collection("Posts").document(post.getPostId()).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful()){
                                                    Map<String, Object> postMap = task.getResult().getData();
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

                                                                        userSavesList.add(post);
                                                                        countUserSaves--;
                                                                    }
                                                                    else {

                                                                    }
                                                                }
                                                            });
                                                }
                                                else {

                                                }
                                            }
                                        });

                            }
                        }
                    }
                    else{
                        countUserSaves = 0;
                    }
                }
                else{
                    countUserSaves = 0;
                }

            }
        });
    }

}
