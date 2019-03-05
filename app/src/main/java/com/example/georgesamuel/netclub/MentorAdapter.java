package com.example.georgesamuel.netclub;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MentorAdapter extends RecyclerView.Adapter<MentorAdapter.MyViewHolder> {

    private ArrayList<Mentor> mentorList = new ArrayList<>();
    private Context context;

    public MentorAdapter(ArrayList<Mentor> list){
        this.mentorList = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_mentor, viewGroup, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        context = viewGroup.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {

        myViewHolder.setIsRecyclable(false);

        myViewHolder.mentorName.setText(mentorList.get(i).getName());
        myViewHolder.mentorCategory.setText(mentorList.get(i).getCategory());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.placeholder);
        Glide.with(context).applyDefaultRequestOptions(requestOptions).load(mentorList.get(i).getImage_url()).thumbnail(
                Glide.with(context).load(mentorList.get(i).getImageThumb_url())
        ).into(myViewHolder.mentorImage);

        myViewHolder.bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mentor mentor = mentorList.get(i);
                Intent i = new Intent(context, MentorDetailsActivity.class);
                i.putExtra("mentor", mentor);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mentorList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView mentorName;
        TextView mentorCategory;
        Button bookBtn;
        CircleImageView mentorImage;

        public MyViewHolder(View view){
            super(view);

            mentorName = (TextView) view.findViewById(R.id.mentor_name);
            mentorImage = (CircleImageView) view.findViewById(R.id.mentor_image);
            bookBtn = (Button) view.findViewById(R.id.book_btn);
            mentorCategory = (TextView) view.findViewById(R.id.mentor_category);
        }
    }
}
