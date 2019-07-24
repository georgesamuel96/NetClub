package com.egcoders.technologysolution.netclub.data;

import android.app.Activity;
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
import com.egcoders.technologysolution.netclub.Activities.DetailsMentorActivity;
import com.egcoders.technologysolution.netclub.R;
import com.egcoders.technologysolution.netclub.model.Mentor;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class MentorAdapter extends RecyclerView.Adapter<MentorAdapter.MyViewHolder> {

    private ArrayList<Mentor> mentorList = new ArrayList<>();
    private Context context;
    private FirebaseFirestore firestore;
    private SaveMentorInstance mentorInstance;

    public MentorAdapter(ArrayList<Mentor> list){
        this.mentorList = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_mentor, viewGroup, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        context = viewGroup.getContext();
        firestore = FirebaseFirestore.getInstance();
        mentorInstance = new SaveMentorInstance();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

        myViewHolder.setIsRecyclable(false);
        final String mentorId = mentorList.get(i).getId();

        myViewHolder.mentorName.setText(mentorList.get(i).getName());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.placeholder);
        Glide.with(context).applyDefaultRequestOptions(requestOptions).load(mentorList.get(i).getImage_url()).thumbnail(
                Glide.with(context).load(mentorList.get(i).getImageThumb_url())
        ).into(myViewHolder.mentorImage);

        myViewHolder.seeMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DetailsMentorActivity.class);
                //i.putExtra("mentorId", mentorId);
                mentorInstance.setBookMentorId(mentorId);
                context.startActivity(i);
            }
        });
        myViewHolder.mentorContent.setText(mentorList.get(i).getContent());

        // Get categories of mentor

        final StringBuilder stringBuilder = new StringBuilder();
        myViewHolder.mentorCategory.setText("");
        Query query = firestore.collection("Mentors").document(mentorId).collection("Categories");
        query.addSnapshotListener((Activity) context, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e == null){
                    if(!queryDocumentSnapshots.isEmpty()){
                        for (DocumentChange document : queryDocumentSnapshots.getDocumentChanges()){
                            if(document.getType() == DocumentChange.Type.ADDED){
                                Map<String, Object> categoryMap = document.getDocument().getData();
                                stringBuilder.append("#" + categoryMap.get("name").toString() + " ");
                            }
                        }
                        myViewHolder.mentorCategory.setText(stringBuilder.toString());
                    }
                }
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
        Button seeMoreBtn;
        CircleImageView mentorImage;
        TextView mentorContent;

        public MyViewHolder(View view){
            super(view);

            mentorName = (TextView) view.findViewById(R.id.mentor_name);
            mentorImage = (CircleImageView) view.findViewById(R.id.mentor_image);
            seeMoreBtn = (Button) view.findViewById(R.id.seeMore_btn);
            mentorCategory = (TextView) view.findViewById(R.id.mentor_category);
            mentorContent = (TextView) view.findViewById(R.id.mentor_content);
        }
    }
}
