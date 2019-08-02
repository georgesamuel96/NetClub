package com.egcoders.technologysolution.netclub.data.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.egcoders.technologysolution.netclub.R;
import com.egcoders.technologysolution.netclub.model.post.Comment;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {

    private List<Comment> commentList = new ArrayList<>();
    private Context context;

    public CommentAdapter(List<Comment> list){
        this.commentList = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_comment, viewGroup, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        context = viewGroup.getContext();
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        myViewHolder.setIsRecyclable(false);

        // Set user name
        myViewHolder.userName.setVisibility(View.VISIBLE);
        myViewHolder.userName.setText(commentList.get(i).getUserName());

        // Check user statue
        if(!commentList.get(i).getUserStatue().equals("0"))
            myViewHolder.userStatue.setVisibility(View.VISIBLE);

        // Set content of comment
        myViewHolder.content.setText(commentList.get(i).getContent());

        // Set user profile
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.profile);
        Glide.with(context).applyDefaultRequestOptions(requestOptions).load(commentList.get(i).getUserProfile()).thumbnail(
                Glide.with(context).load(commentList.get(i).getUserProfileThumb())).into(myViewHolder.userProfile);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView userName;
        TextView userStatue;
        TextView content;
        CircleImageView userProfile;

        public MyViewHolder(View view){
            super(view);

            userName = (TextView) view.findViewById(R.id.userName);
            userStatue = (TextView) view.findViewById(R.id.userStatue);
            content = (TextView) view.findViewById(R.id.content);
            userProfile = (CircleImageView) view.findViewById(R.id.userProfile);
        }
    }
}
