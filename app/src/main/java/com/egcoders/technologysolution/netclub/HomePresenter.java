package com.egcoders.technologysolution.netclub;

import android.app.Activity;
import android.net.wifi.hotspot2.pps.HomeSp;
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

public class HomePresenter implements Home.Presenter {

    private Home.View view;
    private Activity activity;
    private ArrayList<Post> postsList = new ArrayList<>();
    private DocumentSnapshot lastVisible;
    private FirebaseFirestore firestore;
    private Thread[] threads = new Thread[4];
    private volatile int countPosts;

    public HomePresenter(Activity activity, Home.View view){
        this.view = view;
        this.activity = activity;
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public void loadPosts() {

        postsList.clear();
        countPosts = Integer.MAX_VALUE;

        threads[0] = new Thread(new Runnable() {
            @Override
            public void run() {
                Query query = firestore.collection("Posts").orderBy("timeStamp", Query.Direction.DESCENDING)
                        .limit(5);
                getPost(query);
            }
        });
        threads[0].start();

        threads[1] = new Thread(new Runnable() {
            @Override
            public void run() {

                while (countPosts > 0);
                view.showPosts(postsList);
            }
        });
        threads[1].start();
    }

    @Override
    public void loadMorePosts() {
        postsList.clear();
        countPosts = Integer.MAX_VALUE;

        if(lastVisible == null)
            return;

        threads[2] = new Thread(new Runnable() {
            @Override
            public void run() {
                Query query = firestore.collection("Posts").orderBy("timeStamp", Query.Direction.DESCENDING)
                        .startAfter(lastVisible).limit(5);
                getPost(query);

            }
        });
        threads[2].start();

        threads[3] = new Thread(new Runnable() {
            @Override
            public void run() {
                while (countPosts > 0);
                view.showMorePosts(postsList);
            }
        });
        threads[3].start();
    }

    private void getPost(Query query){

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
                    countPosts = 0;
                }

            }
        });

    }
}
