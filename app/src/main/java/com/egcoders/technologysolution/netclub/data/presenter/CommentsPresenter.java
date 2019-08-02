package com.egcoders.technologysolution.netclub.data.presenter;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.egcoders.technologysolution.netclub.Utils.SharedPreferenceConfig;
import com.egcoders.technologysolution.netclub.data.interfaces.Comments;
import com.egcoders.technologysolution.netclub.model.post.Comment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsPresenter implements Comments.Presenter {

    private Activity activity;
    private Comments.View view;
    private FirebaseFirestore firestore;
    private List<Comment> commentsList = new ArrayList<>();
    private volatile int count;
    private Thread[] threads = new Thread[2];
    private SharedPreferenceConfig preferenceConfig;

    public CommentsPresenter(Activity activity, Comments.View view){
        this.activity = activity;
        this.view = view;
        firestore = FirebaseFirestore.getInstance();
        preferenceConfig = new SharedPreferenceConfig(activity.getApplicationContext());
    }

    @Override
    public void getComments(final String postId) {
        commentsList.clear();
        count = Integer.MAX_VALUE;

        threads[0] = new Thread(new Runnable() {
            @Override
            public void run() {
                firestore.collection("Posts").document(postId).collection("Comments")
                        .get().addOnCompleteListener(activity, new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            count = task.getResult().getDocumentChanges().size();
                            for(DocumentChange document : task.getResult().getDocumentChanges()){
                                Map<String, Object> commentMap = document.getDocument().getData();
                                String userId = commentMap.get("userId").toString();
                                final Comment comment = new Comment();
                                comment.setTimeStamp(commentMap.get("timeStamp").toString());
                                comment.setContent(commentMap.get("content").toString());
                                comment.setUserId(userId);
                                firestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        Map<String, Object> userMap = task.getResult().getData();
                                        comment.setUserName(userMap.get("name").toString());
                                        comment.setUserProfile(userMap.get("profile_url").toString());
                                        comment.setUserProfileThumb(userMap.get("profileThumb").toString());
                                        comment.setUserStatue(userMap.get("userStatue").toString());

                                        commentsList.add(comment);
                                        count--;
                                    }
                                });
                            }
                        }
                        else{

                        }
                    }
                });
            }
        });
        threads[0].start();

        threads[1] = new Thread(new Runnable() {
            @Override
            public void run() {
                while (count > 0);
                view.showComments(commentsList);
            }
        });
        threads[1].start();
    }

    @Override
    public void addComment(String comment, String postId) {
        Map<String, Object> commentMap = new HashMap<>();
        String random = Long.toString(System.currentTimeMillis());
        commentMap.put("timeStamp", random);
        commentMap.put("userId", preferenceConfig.getSharedPrefConfig());
        commentMap.put("content", comment);

        firestore.collection("Posts").document(postId).collection("Comments").document(random)
                .set(commentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                }
                else {

                }
            }
        });
    }
}
