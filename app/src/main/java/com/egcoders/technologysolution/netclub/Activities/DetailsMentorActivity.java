package com.egcoders.technologysolution.netclub.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.egcoders.technologysolution.netclub.R;
import com.egcoders.technologysolution.netclub.data.SaveMentorInstance;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailsMentorActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar toolbar;
    private FirebaseFirestore firestore;
    private TextView mentorDesc, mentorName;
    private CircleImageView mentorImage;
    private ProgressBar progressBar;
    private Button bookBtn;
    private SaveMentorInstance mentorInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_mentor);

        mentorInstance = new SaveMentorInstance();

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_menu);

        mentorName = (TextView) findViewById(R.id.mentor_name);
        mentorImage = (CircleImageView) findViewById(R.id.mentor_image);
        mentorDesc = (TextView) findViewById(R.id.mentor_description);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        bookBtn = (Button) findViewById(R.id.book_btn);

        firestore = FirebaseFirestore.getInstance();

        progressBar.setVisibility(View.VISIBLE);
        firestore.collection("Mentors").document(mentorInstance.getBookMentorId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Map<String, Object> mentorMap = task.getResult().getData();
                    mentorName.setText(mentorMap.get("name").toString());
                    mentorDesc.setText(mentorMap.get("description").toString());
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.profile);
                    Glide.with(getApplicationContext()).applyDefaultRequestOptions(requestOptions).load(mentorMap.get("profile_url"))
                            .thumbnail(Glide.with(getApplicationContext()).load(mentorMap.get("profileThumb_url"))).into(mentorImage);
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else{
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

        bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailsMentorActivity.this, MentorDatesActivity.class);
                startActivity(i);
            }
        });

    }
}
