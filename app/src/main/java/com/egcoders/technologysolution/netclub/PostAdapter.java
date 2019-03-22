package com.egcoders.technologysolution.netclub;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    private ArrayList<Post> postsList = new ArrayList<>();
    private Context context;
    private SharedPreferenceConfig preferenceConfig;
    private FirebaseFirestore firestore;
    private int lines = 0;
    private String likesCount = "";
    private int statue;
    private ArrayList<Pair<Long, String>> time = new ArrayList<>();

    public PostAdapter(ArrayList<Post> list, int statue){
        this.postsList = list;
        this.statue = statue;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_post, viewGroup, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        context = viewGroup.getContext();
        preferenceConfig = new SharedPreferenceConfig(context);
        firestore = FirebaseFirestore.getInstance();
        time.add(Pair.create(59L, "S"));
        time.add(Pair.create(59L, "M"));
        time.add(Pair.create(23L, "H"));
        time.add(Pair.create(29L, "D"));
        time.add(Pair.create(11L, "M"));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

        myViewHolder.setIsRecyclable(false);
        myViewHolder.userName.setText(postsList.get(i).getUserName());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.profile);
        Glide.with(context).applyDefaultRequestOptions(requestOptions).load(postsList.get(i).getUserProfileUrl()).thumbnail(
                Glide.with(context).load(postsList.get(i).getUserProfileUrl())
        ).into(myViewHolder.userImage);
        if(postsList.get(i).getUserStatue().equals("0"))
            myViewHolder.statue.setVisibility(View.GONE);

        Long timeStamp = postsList.get(i).getTimeStamp();
        Long currentTimeStamp = System.currentTimeMillis();
        timeStamp = currentTimeStamp - timeStamp;
        timeStamp /= 1000;
        for(int j=0;j<6;j++){
            if(j == 5){
                myViewHolder.date.setText(timeStamp + " Y");
                break;
            }

            System.out.println("Cur:" + timeStamp);
            if(timeStamp <= time.get(j).first){
                myViewHolder.date.setText(timeStamp + " " + time.get(j).second);
                break;
            }
            timeStamp /= time.get(j).first;
        }


        if(!postsList.get(i).getUserId().equals(preferenceConfig.getSharedPrefConfig()))
            myViewHolder.deleteBtn.setVisibility(View.GONE);
        else{

            // Delete Post
            final PopupMenu popupMenu = new PopupMenu(context, myViewHolder.deleteBtn);
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu_post, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if(item.getItemId() == R.id.delete){

                        firestore.collection("Posts").document(postsList.get(i).getPostId()).collection("Likes")
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                        if(!queryDocumentSnapshots.isEmpty()){
                                            for(DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                                                firestore.collection("Posts").document(postsList.get(i).getPostId())
                                                        .collection("Likes").document(doc.getDocument().getId())
                                                        .delete();
                                            }
                                        }
                                    }
                                });
                        firestore.collection("Posts").document(postsList.get(i).getPostId()).collection("Saves")
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                        if(!queryDocumentSnapshots.isEmpty()){
                                            for(DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                                                firestore.collection("Posts").document(postsList.get(i).getPostId())
                                                        .collection("Saves").document(doc.getDocument().getId())
                                                        .delete();
                                            }
                                        }
                                    }
                                });
                        firestore.collection("Users").document(preferenceConfig.getSharedPrefConfig())
                                .collection("Saves").document(postsList.get(i).getPostId()).delete();

                        firestore.collection("Posts").document(postsList.get(i).getPostId()).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        postsList.remove(i);
                                        notifyDataSetChanged();
                                    }
                                });
                    }
                    return true;
                }
            });

            myViewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupMenu.show();
                }
            });
        }

        if(postsList.get(i).getPhotoUrl().equals(""))
            myViewHolder.postImage.setVisibility(View.GONE);
        else{
            requestOptions.placeholder(R.drawable.placeholder);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(postsList.get(i).getPhotoUrl()).thumbnail(
                    Glide.with(context).load(postsList.get(i).getPhotoThumbUrl())
            ).into(myViewHolder.postImage);
        }

        myViewHolder.category.setText(postsList.get(i).getCategory());
        if(postsList.get(i).getContent().equals("")){
            myViewHolder.content.setVisibility(View.GONE);
            myViewHolder.seeMore.setVisibility(View.GONE);
        }
        else{
            myViewHolder.content.setText(postsList.get(i).getContent());
            myViewHolder.content.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    myViewHolder.content.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    lines = myViewHolder.content.getLineCount();
                    if(lines <= 2){
                        myViewHolder.seeMore.setVisibility(View.INVISIBLE);
                    }
                }
            });

            myViewHolder.seeMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myViewHolder.content.setMaxLines(myViewHolder.content.getLineCount() + 1);
                    myViewHolder.seeMore.setVisibility(View.INVISIBLE);
                }
            });

        }

        // Check if user like this post or not
        firestore.collection("Posts").document(postsList.get(i).getPostId()).collection("Likes")
                .document(preferenceConfig.getSharedPrefConfig()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e == null) {
                    if (documentSnapshot.exists()) {
                        myViewHolder.likeBtn.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_like_accent));
                    } else {
                        myViewHolder.likeBtn.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_like_gray));
                    }
                }
            }
        });

        // Get likes count
        firestore.collection("Posts").document(postsList.get(i).getPostId()).collection("Likes")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            int count = queryDocumentSnapshots.size();
                            myViewHolder.likeNumber.setText(count + " Likes");
                            likesCount = Integer.toString(count);
                        }
                        else {
                            myViewHolder.likeNumber.setText("0 Likes");
                        }
                    }
                });

        // User like or dislike post
        myViewHolder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firestore.collection("Posts").document(postsList.get(i).getPostId()).collection("Likes")
                        .document(preferenceConfig.getSharedPrefConfig()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()) {

                            firestore.collection("Posts").document(postsList.get(i).getPostId())
                                    .collection("Likes").document(preferenceConfig.getSharedPrefConfig()).delete();
                            myViewHolder.likeBtn.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_like_gray));
                        } else {
                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", Long.toString(System.currentTimeMillis()));

                            firestore.collection("Posts").document(postsList.get(i).getPostId())
                                    .collection("Likes").document(preferenceConfig.getSharedPrefConfig()).set(likesMap);
                            myViewHolder.likeBtn.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_like_accent));
                        }
                    }
                });

            }
        });

        // Check if user save this post or not
        firestore.collection("Posts").document(postsList.get(i).getPostId()).collection("Saves")
                .document(preferenceConfig.getSharedPrefConfig()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    myViewHolder.saveBtn.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_save_accent));
                }
                else{
                    myViewHolder.saveBtn.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_save_gray));
                }
            }
        });

        // Save or not save post
        myViewHolder.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firestore.collection("Posts").document(postsList.get(i).getPostId()).collection("Saves")
                        .document(preferenceConfig.getSharedPrefConfig()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            myViewHolder.saveBtn.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_save_gray));

                            firestore.collection("Users").document(preferenceConfig.getSharedPrefConfig()).collection("Saves")
                                    .document(postsList.get(i).getPostId()).delete();

                            firestore.collection("Posts").document(postsList.get(i).getPostId())
                                    .collection("Saves").document(preferenceConfig.getSharedPrefConfig()).delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        if(statue == 1){
                                            postsList.remove(i);
                                            notifyDataSetChanged();
                                        }
                                    }
                                    else{
                                    }
                                }
                            });

                            firestore.collection("Users").document(preferenceConfig.getSharedPrefConfig())
                                    .collection("Saves").document(postsList.get(i).getPostId()).delete();
                        }
                        else{
                            myViewHolder.saveBtn.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_save_accent));

                            String random = Long.toString(System.currentTimeMillis());
                            Map<String, Object> savesMap = new HashMap<>();
                            savesMap.put("timestamp", random);

                            firestore.collection("Posts").document(postsList.get(i).getPostId())
                                    .collection("Saves").document(preferenceConfig.getSharedPrefConfig()).set(savesMap);

                            firestore.collection("Users").document(preferenceConfig.getSharedPrefConfig())
                                    .collection("Saves").document(postsList.get(i).getPostId()).set(savesMap);
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView userImage;
        private TextView userName;
        private TextView date;
        private TextView statue;
        private ImageView postImage;
        private TextView category;
        private TextView content;
        private TextView seeMore;
        private TextView likeNumber;
        private ImageView likeBtn;
        private ImageView saveBtn;
        private ImageView deleteBtn;

        public MyViewHolder(View view){
            super(view);

            userImage = (CircleImageView) view.findViewById(R.id.userProfile);
            userName = (TextView) view.findViewById(R.id.userName);
            date = (TextView) view.findViewById(R.id.postDate);
            statue = (TextView) view.findViewById(R.id.statue);
            postImage = (ImageView) view.findViewById(R.id.postImage);
            category = (TextView) view.findViewById(R.id.category);
            content = (TextView) view.findViewById(R.id.content);
            seeMore = (TextView) view.findViewById(R.id.seeMore);
            likeNumber = (TextView) view.findViewById(R.id.likeNumber);
            likeBtn = (ImageView) view.findViewById(R.id.likeBtn);
            saveBtn = (ImageView) view.findViewById(R.id.saveBtn);
            deleteBtn = (ImageView) view.findViewById(R.id.deleteBtn);
        }
    }
}
