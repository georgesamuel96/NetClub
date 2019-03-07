package com.egcoders.technologysolutions.netclub;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DatesMentorActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar toolbar;
    private FloatingActionButton fab;
    private ArrayList<String> dates = new ArrayList<>();
    private ListView listView;
    private CurrentMentor mentor;
    private AlertDialog.Builder alertBuilder;
    private FirebaseFirestore firestore;
    private ProgressBar progressBar;
    private String currentMentorId;;
    private SaveMentorInstance mentorInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dates_mentor);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        listView = (ListView) findViewById(R.id.list_view);

        mentorInstance = new SaveMentorInstance();
        firestore = FirebaseFirestore.getInstance();
        alertBuilder = new AlertDialog.Builder(this);
        mentor = new CurrentMentor();
        dates = mentor.getDates();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1, dates);

        listView.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DatesMentorActivity.this, AddDateActivity.class));
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.popup_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.submit){

            Map<String, Object> mentorMap = new HashMap<>();
            mentorMap.put("name", mentor.getName());
            mentorMap.put("content", mentor.getContent());
            mentorMap.put("profile_url", mentor.getImage_url());
            mentorMap.put("profileThumb_url", mentor.getImageThumb_url());
            mentorMap.put("description", mentor.getDescription());
            mentorMap.put("email", mentor.getEmail());
            mentorMap.put("phone", mentor.getPhone());

            currentMentorId = Long.toString(System.currentTimeMillis());
            progressBar.setVisibility(View.VISIBLE);
            firestore.collection("Mentors").document(currentMentorId).set(mentorMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                            }
                            else{

                            }
                        }
                    });

            progressBar.setVisibility(View.VISIBLE);
            for(Pair<String, String> category : mentor.getCategories()){
                Map<String, Object> categoryMap = new HashMap<>();
                categoryMap.put("name", category.first);
                System.out.println(currentMentorId);
                firestore.collection("Mentors").document(currentMentorId)
                        .collection("Categories").document(category.second).set(categoryMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                }
                                else{

                                }
                            }
                        });
            }
            progressBar.setVisibility(View.INVISIBLE);

            progressBar.setVisibility(View.VISIBLE);
            for(String date : mentor.getDates()){
                Map<String, Object> dateMap = new HashMap<>();
                dateMap.put("name", date);
                firestore.collection("Mentors").document(currentMentorId).collection("Dates")
                        .add(dateMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){

                        }
                        else{

                        }
                    }
                });
            }
            progressBar.setVisibility(View.INVISIBLE);

            Mentor _mentor = new Mentor();
            _mentor.setName(mentor.getName());
            _mentor.setEmail(mentor.getEmail());
            _mentor.setCategories(mentor.getCategories());
            _mentor.setImage_url(mentor.getImage_url());
            _mentor.setImageThumb_url(mentor.getImageThumb_url());
            _mentor.setDescription(mentor.getDescription());
            _mentor.setPhone(mentor.getPhone());
            _mentor.setDates(mentor.getDates());
            _mentor.setId(currentMentorId);

            mentor.getDates().clear();
            mentor.getCategories().clear();


            finish();
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        mentor.getDates().clear();
        mentor.getCategories().clear();
    }
}
