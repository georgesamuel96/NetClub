package com.egcoders.technologysolution.netclub;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MentorDatesActivity extends AppCompatActivity {

    private SaveMentorInstance mentorInstance;
    private String mentorName;
    private FirebaseFirestore firestore;
    private android.support.v7.widget.Toolbar toolbar;
    private static ArrayList<ChooseCategory> datesList = new ArrayList<>();
    private ChooseCategoryAdapter adapter;
    private ListView listView;
    private AlertDialog.Builder alertBuilder;
    private ArrayList<String> datesByUser = new ArrayList<>();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_details);

        mentorInstance = new SaveMentorInstance();

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.list_view);
        adapter = new ChooseCategoryAdapter(getApplicationContext(), datesList);
        adapter.clearAdapter();

        progressDialog = new ProgressDialog(this);
        firestore = FirebaseFirestore.getInstance();

        listView.setAdapter(adapter);
        alertBuilder = new AlertDialog.Builder(this);

        firestore.collection("Mentors").document(mentorInstance.getBookMentorId()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Map<String, Object> mentorMap = task.getResult().getData();
                    mentorName = mentorMap.get("name").toString();
                    getSupportActionBar().setTitle(mentorName);
                }
                else{

                }
            }
        });

        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading Dates");
        progressDialog.setMessage("Loading");
        progressDialog.show();
        firestore.collection("Mentors").document(mentorInstance.getBookMentorId()).collection("Dates").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(DocumentChange document : task.getResult().getDocumentChanges()){
                                Map<String, Object> dateMap = document.getDocument().getData();
                                ChooseCategory category = new ChooseCategory();
                                category.setcategoryChecked(false);
                                category.setCategoryName(dateMap.get("name").toString());
                                category.setCategoryId(document.getDocument().getId());
                                datesList.add(category);
                                adapter.notifyDataSetChanged();
                            }
                        }
                        else{
                        }
                    }
                });
        progressDialog.dismiss();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.popup_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.submit) {

            Boolean itemChecked = (adapter.itemsChecked());
            datesList = adapter.getCheckedList();
            if (itemChecked) {
                progressDialog.setCancelable(false);
                progressDialog.setTitle("Submit your choosing");
                progressDialog.setMessage("Loading");
                progressDialog.show();
                final String registerId = Long.toString(System.currentTimeMillis());
                for (ChooseCategory category : datesList) {
                    if (category.getcategoryChecked()) {

                        datesByUser.add(category.getCategoryName());

                        final Map<String, Object> registerMap = new HashMap<>();
                        registerMap.put("date", category.getCategoryName());
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        String currentUserId = currentUser.getUid();

                        // Get current user
                        firestore.collection("Users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    Map<String, Object> userMap = task.getResult().getData();
                                    registerMap.put("userName", userMap.get("name").toString());
                                    registerMap.put("userPhone", userMap.get("phone").toString());

                                    // Get currentMentor
                                    firestore.collection("Mentors").document(mentorInstance.getBookMentorId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()){
                                                Map<String, Object> mentorMap = task.getResult().getData();
                                                registerMap.put("mentorName", mentorMap.get("name").toString());
                                                registerMap.put("mentorPhone", mentorMap.get("phone").toString());

                                                registerMap.put("paymentMethod", "");


                                                firestore.collection("Registration").document(registerId)
                                                        .set(registerMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            /*Intent i = new Intent(MentorDatesActivity.this, PaymentMethodActivity.class);
                                                            i.putExtra("registrationId", registerId);
                                                            startActivity(i);
                                                            finish();*/
                                                        }
                                                        else{

                                                        }
                                                    }
                                                });
                                            }
                                            else{

                                            }
                                        }
                                    });


                                }
                                else {

                                }
                            }
                        });


                    }
                }
                Intent i = new Intent(MentorDatesActivity.this, PaymentMethodActivity.class);
                i.putExtra("registrationId", registerId);
                i.putStringArrayListExtra("dates", datesByUser);
                startActivity(i);
                finish();
                progressDialog.dismiss();
            }
            else{
                alertBuilder.setTitle("Dates");
                alertBuilder.setMessage("Use at Least on date to book mentor");
                alertBuilder.setCancelable(false);
                alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = alertBuilder.create();
                alertDialog.show();
            }

        }

        return true;
    }

}
