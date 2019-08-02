package com.egcoders.technologysolution.netclub.data.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.egcoders.technologysolution.netclub.ui.activities.AddComentActivity;
import com.egcoders.technologysolution.netclub.ui.activities.AddPostActivity;
import com.egcoders.technologysolution.netclub.R;
import com.egcoders.technologysolution.netclub.Utils.Utils;
import com.egcoders.technologysolution.netclub.Utils.UserSharedPreference;
import com.egcoders.technologysolution.netclub.model.post.DeletePostResponse;
import com.egcoders.technologysolution.netclub.model.post.Post;
import com.egcoders.technologysolution.netclub.remote.ApiManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    private List<Post> postsList = new ArrayList<>();
    private Context context;
    //private SharedPreferenceConfig preferenceConfig;
    private FirebaseFirestore firestore;
    private int lines = 0;
    private String likesCount = "";
    private int statue;
    private List<Pair<Long, String>> time = new ArrayList<>();
    private UserSharedPreference preference;
    private Utils utils;
    private Activity activity;

    public PostAdapter(Activity activity, List<Post> list, int statue){
        this.postsList = list;
        this.statue = statue;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_post, viewGroup, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        context = viewGroup.getContext();

        /*preferenceConfig = new SharedPreferenceConfig(context);*/
        firestore = FirebaseFirestore.getInstance();
        preference = new UserSharedPreference(context);
        utils = new Utils(activity);

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

        // Set user name
        myViewHolder.userName.setText(postsList.get(i).getUserData().getName());

        // Set user profile
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.profile);
        Glide.with(context).applyDefaultRequestOptions(requestOptions).load(postsList.get(i).getUserData().getPhoto_max())
                .into(myViewHolder.userImage);

        // Check if this post belong to a mentor
        if(postsList.get(i).getUserData().getUserStatus().equals("user"))
            myViewHolder.statue.setVisibility(View.GONE);
        else {
            myViewHolder.statue.setVisibility(View.VISIBLE);
        }

       // Set time when this post was published
        String str_date = postsList.get(i).getCreated_at();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = null;
        try {
            date = (Date)formatter.parse(str_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Long timeStamp = date.getTime();
        Long currentTimeStamp = System.currentTimeMillis();
        timeStamp = currentTimeStamp - timeStamp;
        timeStamp /= 1000;
        for(int j=0;j<6;j++){
            if(j == 5){
                myViewHolder.date.setText(timeStamp + " Y");
                break;
            }

            if(timeStamp <= time.get(j).first){
                myViewHolder.date.setText(timeStamp + " " + time.get(j).second);
                break;
            }
            timeStamp /= time.get(j).first;
        }
        myViewHolder.date.setVisibility(View.VISIBLE);

      // Set popup menu if this post was published wish current user
        if(postsList.get(i).getUserData().getId() != preference.getUser().getData().getId())
            myViewHolder.deleteBtn.setVisibility(View.INVISIBLE);
       else{

            myViewHolder.deleteBtn.setVisibility(View.VISIBLE);
            final PopupMenu popupMenu = new PopupMenu(context, myViewHolder.deleteBtn);
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu_post, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    // Delete AddPost
                    if(item.getItemId() == R.id.delete){

                        utils.showProgressDialog("Delete post", "Loading");

                        String token = preference.getUser().getData().getToken();

                        ApiManager.getInstance().deletePost(token, postsList.get(i).getId(), new Callback<DeletePostResponse>() {
                            @Override
                            public void onResponse(Call<DeletePostResponse> call, Response<DeletePostResponse> response) {

                                utils.hideProgressDialog();

                                DeletePostResponse postResponse = response.body();

                                try{
                                    if(postResponse.getSuccess()){
                                        postsList.remove(i);
                                        notifyDataSetChanged();
                                    }
                                    else{
                                        utils.showMessage("Delete post", postResponse.getMessage());
                                    }
                                }
                                catch (Exception e){
                                    Log.v("Delete post", e.getMessage());
                                }
                            }

                            @Override
                            public void onFailure(Call<DeletePostResponse> call, Throwable t) {
                                Log.v("Delete post", t.getMessage());
                            }
                        });
                    }
                    else if(item.getItemId() == R.id.edit){

                        // Edit post
                        Intent intent = new Intent(context, AddPostActivity.class);
                        intent.putExtra("postId", postsList.get(i).getId());
                        context.startActivity(intent);
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

       // Check if this post has image
        if(postsList.get(i).getPhotoUrl() != null){

            Glide.with(context).load(postsList.get(i).getPhotoUrl())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@android.support.annotation.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                            myViewHolder.imageLoad.setVisibility(View.INVISIBLE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                            myViewHolder.imageLoad.setVisibility(View.INVISIBLE);
                            return false;
                        }
                    })
                    .into(myViewHolder.postImage);

            myViewHolder.postImage.setVisibility(View.VISIBLE);
        }
        else {
            myViewHolder.imageLoad.setVisibility(View.GONE);
            myViewHolder.postImage.setVisibility(View.GONE);
        }

       // Set category of this post
        myViewHolder.category.setText(postsList.get(i).getCategory());

        // Check if this post has text content
        if(!postsList.get(i).getContent().equals("")){

            // Set content
            String content = postsList.get(i).getContent();
            content = content.substring(0, content.length() - 1);
            myViewHolder.content.setText(content);
            myViewHolder.content.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    // Check if the content has more 2 lines
                    myViewHolder.content.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    lines = myViewHolder.content.getLineCount();
                    if(lines > 2){
                        myViewHolder.seeMore.setVisibility(View.VISIBLE);
                    }
                    else {
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
        else {

            myViewHolder.content.setVisibility(View.INVISIBLE);
        }

       // Check if user like this post or not
        firestore.collection("Posts").document(postsList.get(i).getId() + "")
                .collection("Likes")
                .document(preference.getUser().getData().getId() + "")
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
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
        myViewHolder.likeBtn.setVisibility(View.VISIBLE);

        // Get likes count
        firestore.collection("Posts").document(postsList.get(i).getId() + "")
                .collection("Likes")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e == null) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                int count = queryDocumentSnapshots.size();
                                myViewHolder.likeNumber.setText(count + " Likes");
                                likesCount = Integer.toString(count);
                            } else {
                                myViewHolder.likeNumber.setText("0 Likes");
                            }
                        }
                        else{
                            System.out.println("error " + e.getMessage());
                        }
                    }
                });

        // User like or dislike post
        myViewHolder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firestore.collection("Posts").
                        document(postsList.get(i).getId() + "").collection("Likes")
                        .document(preference.getUser().getData().getId() + "")
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()) {

                            firestore.collection("Posts").document(postsList.get(i).getId() + "")
                                    .collection("Likes").document(preference.getUser().getData().getId() + "").
                                    delete();
                            myViewHolder.likeBtn.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_like_gray));
                        } else {
                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", Long.toString(System.currentTimeMillis()));

                            firestore.collection("Posts").document(postsList.get(i).getId() + "")
                                    .collection("Likes").document(preference.getUser().getData().getId() + "")
                                    .set(likesMap);
                            myViewHolder.likeBtn.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_like_accent));
                        }
                    }
                });

            }
        });

        // Get Comments count
        firestore.collection("Posts").document(postsList.get(i).getId() + "")
                .collection("Comments")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e == null) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                int count = queryDocumentSnapshots.size();
                                myViewHolder.commentNumber.setText(count + " Comments");
                                likesCount = Integer.toString(count);
                            } else {
                                myViewHolder.commentNumber.setText("0 Comments");
                            }
                        }
                        else{
                            System.out.println("error " + e.getMessage());
                        }
                    }
                });

        // Open comments of this post
        myViewHolder.commentNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddComentActivity.class);
                intent.putExtra("postId", postsList.get(i).getId());
                context.startActivity(intent);
            }
        });

      /*// Check if user save this post or not
        myViewHolder.saveBtn.setVisibility(View.VISIBLE);
        firestore.collection("Posts").document(postsList.get(i).getPostId()).collection("Saves")
                .document(preferenceConfig.getSharedPrefConfig()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e == null) {
                    if (documentSnapshot.exists()) {
                        myViewHolder.saveBtn.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_save_accent));
                    } else {
                        myViewHolder.saveBtn.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_save_gray));
                    }
                }
            }
        });*/

        // Save or not save post
     /*   myViewHolder.saveBtn.setOnClickListener(new View.OnClickListener() {
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
        });*/
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
        private ImageView commentBtn;
        private TextView commentNumber;
        private ProgressBar imageLoad;

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
            commentBtn = (ImageView) view.findViewById(R.id.commentBtn);
            commentNumber = (TextView) view.findViewById(R.id.commentNumber);
            imageLoad = (ProgressBar) view.findViewById(R.id.progressBar);
        }
    }
}
