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
import com.egcoders.technologysolution.netclub.model.profile.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

    private ArrayList<User> userList = new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;

    public UserAdapter(ArrayList<User> list){
        this.userList = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user, viewGroup, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        context = viewGroup.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        myViewHolder.setIsRecyclable(false);

        myViewHolder.userName.setText(userList.get(i).getUserName());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.profile);
        Glide.with(context).applyDefaultRequestOptions(requestOptions).load(userList.get(i).getUserImageUrl()).thumbnail(
                Glide.with(context).load(userList.get(i).getUserImageThumbUrl())
        ).into(myViewHolder.userImage);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView userName;
        CircleImageView userImage;

        public MyViewHolder(View view){
            super(view);

            userName = (TextView) view.findViewById(R.id.user_name);
            userImage = (CircleImageView) view.findViewById(R.id.user_image);
        }
    }
}
