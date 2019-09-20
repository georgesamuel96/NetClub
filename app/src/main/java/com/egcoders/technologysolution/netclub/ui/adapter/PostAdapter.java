package com.egcoders.technologysolution.netclub.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.egcoders.technologysolution.netclub.Utils.CheckNetwork;
import com.egcoders.technologysolution.netclub.model.post.SavePostResponse;
import com.egcoders.technologysolution.netclub.remote.ClientApi;
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
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    private static final String TAG = PostAdapter.class.getSimpleName();
    private List<Post> postsList;
    private Context context;
    private FirebaseFirestore firestore;
    private int lines = 0;
    private String likesCount = "";
    private int statue;
    private List<Pair<Long, String>> time = new ArrayList<>();
    private UserSharedPreference preference;
    private Utils utils;
    private Activity activity;
    private String token;
    private List<Boolean> isChecked;
    private ClientApi clientApi;
    private CompositeDisposable disposable;

    public PostAdapter(Activity activity, List<Post> list, int statue){
        this.postsList = list;
        this.statue = statue;
        this.activity = activity;
        clientApi = ApiManager.getClient().create(ClientApi.class);
        disposable = new CompositeDisposable();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_post, viewGroup, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        context = viewGroup.getContext();

        firestore = FirebaseFirestore.getInstance();
        preference = new UserSharedPreference(context);
        utils = new Utils(activity);

        time.add(Pair.create(59L, "S"));
        time.add(Pair.create(59L, "M"));
        time.add(Pair.create(23L, "H"));
        time.add(Pair.create(29L, "D"));
        time.add(Pair.create(11L, "M"));

        token = preference.getUser().getData().getToken();


        isChecked = Arrays.asList(new Boolean[postsList.size()]);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int position) {

        myViewHolder.setIsRecyclable(false);
        if(!CheckNetwork.hasNetwork(context)){
            Toast.makeText(context, context.getString(R.string.network_connection), Toast.LENGTH_LONG).show();
            return;
        }
        // Set user name
        myViewHolder.userName.setText(postsList.get(position).getUserData().getName());

        // Set user profile
        Glide.with(context).load(postsList.get(position).getUserData().getPhoto_max())
                .into(myViewHolder.userImage);

        // Check if this post belong to a mentor
       if(postsList.get(position).getUserData().getUserStatus().equals("user"))
            myViewHolder.statue.setText("");
        else {
            myViewHolder.statue.setText(context.getString(R.string.mentor));
        }

       // Set time when this post was published
        String str_date = postsList.get(position).getCreated_at();
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

      // Set popup menu if this post was published wish current user
        if(postsList.get(position).getUserData().getId() != preference.getUser().getData().getId())
            myViewHolder.deleteBtn.setClickable(false);
       else{
            myViewHolder.deleteBtn.setClickable(true);
            myViewHolder.deleteBtn.setImageResource(R.drawable.dots);
            final PopupMenu popupMenu = new PopupMenu(context, myViewHolder.deleteBtn);
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu_post, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    // Delete Post
                    if(item.getItemId() == R.id.delete){

                        utils.showProgressDialog("Delete post", "Loading");

                        ApiManager.getInstance().deletePost(token, postsList.get(position).getId(), new Callback<DeletePostResponse>() {
                            @Override
                            public void onResponse(Call<DeletePostResponse> call, Response<DeletePostResponse> response) {

                                utils.hideProgressDialog();

                                DeletePostResponse postResponse = response.body();

                                try{
                                    if(postResponse.getSuccess()){
                                        postsList.remove(position);
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
                        intent.putExtra("postId", postsList.get(position).getId());
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
        if(postsList.get(position).getPhotoUrl() != null && postsList.get(position).getPhotoUrl().contains("uploads/posts")){
            Picasso.with(context)
                    .load(postsList.get(position).getPhotoUrl())
                    .into(myViewHolder.postImage);
        }

        // Set category of this post
        myViewHolder.category.setText(postsList.get(position).getCategory());

        // Set content
        String content = postsList.get(position).getContent();
        content = content.substring(0, content.length() - 1);
        content = checkURLs(content);
        myViewHolder.content.setText(Html.fromHtml(content));
        myViewHolder.content.setMovementMethod(LinkMovementMethod.getInstance());
        myViewHolder.content.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                // Check if the content has more 2 lines
                myViewHolder.content.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                lines = myViewHolder.content.getLineCount();
                if(lines > 2){
                    myViewHolder.seeMore.setText(context.getString(R.string.see_more));
                }
            }
        });

        myViewHolder.seeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myViewHolder.content.setMaxLines(myViewHolder.content.getLineCount() + 1);
               myViewHolder.seeMore.setText("");
            }
        });

       // Check if user like this post or not
       Observable<Boolean> likeObservable = getLikeObservable(position);
       DisposableObserver<Boolean> likeObserver = getLikeObserver(myViewHolder);
        disposable.add(
                likeObservable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(likeObserver)
        );

        // Get likes count
        myViewHolder.likeNumber.setText(postsList.get(position).getLikes() + " Likes");

        // User like or dislike post
        myViewHolder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firestore.collection("Posts").
                        document(postsList.get(position).getId() + "").collection("Likes")
                        .document(preference.getUser().getData().getId() + "")
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()) {

                            firestore.collection("Posts").document(postsList.get(position).getId() + "")
                                    .collection("Likes").document(preference.getUser().getData().getId() + "").
                                    delete();
                            myViewHolder.likeBtn.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_like_gray));
                            postsList.get(position).setLikes(postsList.get(position).getLikes() - 1);
                            myViewHolder.likeNumber.setText(postsList.get(position).getLikes() + " Likes");
                        } else {
                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", Long.toString(System.currentTimeMillis()));

                            firestore.collection("Posts").document(postsList.get(position).getId() + "")
                                    .collection("Likes").document(preference.getUser().getData().getId() + "")
                                    .set(likesMap);
                            myViewHolder.likeBtn.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_like_accent));
                            postsList.get(position).setLikes(postsList.get(position).getLikes() + 1);
                            myViewHolder.likeNumber.setText(postsList.get(position).getLikes() + " Likes");
                        }
                    }
                });

            }
        });

        // Get Comments count
        myViewHolder.commentNumber.setText(postsList.get(position).getComments() + " Comments");

        // Open comments of this post
        myViewHolder.commentNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddComentActivity.class);
                intent.putExtra("postId", postsList.get(position).getId());
                context.startActivity(intent);
            }
        });

      //Check if user save this post or not
        if(postsList.get(position).isSaved()){
            myViewHolder.saveBtn.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_save_accent));
        }
        else{
            myViewHolder.saveBtn.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_save_gray));
        }

        //Save or not save post
        myViewHolder.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int userId = preference.getUser().getData().getId();
                if(postsList.get(position).isSaved()){
                    myViewHolder.saveBtn.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_save_gray));
                    postsList.get(position).setSaved(false);
                    getUnSavePostObservable(postsList.get(position).getId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new SingleObserver<SavePostResponse>() {
                                @Override
                                public void onSubscribe(Disposable d) {
                                    Log.d(TAG, "onSubscribe");
                                }

                                @Override
                                public void onSuccess(SavePostResponse savePostResponse) {
                                    Log.d(TAG, "onSuccess: " + savePostResponse.getMessage());
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.d(TAG, "onError: " + e.getMessage());
                                }
                            });
                }
                else{
                    myViewHolder.saveBtn.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_save_accent));
                    postsList.get(position).setSaved(true);
                    getSavePostObservable(postsList.get(position).getId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new SingleObserver<SavePostResponse>() {
                                @Override
                                public void onSubscribe(Disposable d) {
                                    Log.d(TAG, "onSubscribe");
                                }

                                @Override
                                public void onSuccess(SavePostResponse savePostResponse) {
                                    Log.d(TAG, "onSuccess: " + savePostResponse.getMessage());
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.d(TAG, "onError: " + e.getMessage());
                                }
                            });
                }
            }
        });
    }

    private DisposableObserver<Boolean> getLikeObserver(final MyViewHolder myViewHolder) {
        return new DisposableObserver<Boolean>(){

            @Override
            public void onNext(Boolean liked) {
                if(liked)
                    myViewHolder.likeBtn.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_like_accent));
                else
                    myViewHolder.likeBtn.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_like_gray));
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    private Observable<Boolean> getLikeObservable(final int position) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> emitter) throws Exception {
                firestore.collection("Posts").document(postsList.get(position).getId() + "")
                        .collection("Likes")
                        .document(preference.getUser().getData().getId() + "")
                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                if(e == null) {
                                    if (documentSnapshot.exists()) {
                                        if(!emitter.isDisposed())
                                            emitter.onNext(true);
                                    } else {
                                        if(!emitter.isDisposed())
                                            emitter.onNext(false);
                                    }
                                }
                            }
                        });
            }
        });
    }

    private Single<SavePostResponse> getSavePostObservable(int post_id) {
        return clientApi.savePost(token, post_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Single<SavePostResponse> getUnSavePostObservable(int post_id) {
        return clientApi.unSavePost(token, post_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
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

        public MyViewHolder(View view){
            super(view);

            userImage = view.findViewById(R.id.userProfile);
            userName = view.findViewById(R.id.userName);
            date = view.findViewById(R.id.postDate);
            statue = view.findViewById(R.id.statue);
            postImage = view.findViewById(R.id.postImage);
            category = view.findViewById(R.id.category);
            content = view.findViewById(R.id.content);
            seeMore = view.findViewById(R.id.seeMore);
            likeNumber = view.findViewById(R.id.likeNumber);
            likeBtn = view.findViewById(R.id.likeBtn);
            saveBtn = view.findViewById(R.id.saveBtn);
            deleteBtn = view.findViewById(R.id.deleteBtn);
            commentBtn = view.findViewById(R.id.commentBtn);
            commentNumber = view.findViewById(R.id.commentNumber);
        }
    }

    public void clearDisposal(){
        disposable.clear();
    }

    private String checkURLs(String content){
        String finalContent = "";
        String url = "";
        for(int i=0; i < content.length(); i++){
            if(content.charAt(i) == ' '){
                if(Patterns.WEB_URL.matcher("https://" + url).matches()){
                    finalContent = finalContent + String.format("<a href=\"%s\">" + url + "</a> ", "https://" + url) + ' ';
                }
                else {
                    finalContent = finalContent + url + ' ';
                }
                url = "";
            }
            else {
                url = url + content.charAt(i);
            }
        }
        if(url.length() > 0){
            if(Patterns.WEB_URL.matcher("https://" + url).matches()){
                finalContent = finalContent + String.format("<a href=\"%s\">" + url + "</a> ", "https://" + url);
            }
            else {
                finalContent = finalContent + url;
            }
        }

        return finalContent;
    }
}
