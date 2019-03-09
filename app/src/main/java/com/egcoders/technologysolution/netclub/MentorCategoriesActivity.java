package com.egcoders.technologysolution.netclub;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MentorCategoriesActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar toolbar;
    private ListView listView;
    private ChooseCategoryAdapter adapter;
    private ArrayList<ChooseCategory> categoryList = new ArrayList<>();
    private FirebaseFirestore firestore;
    private ProgressBar progressBar;
    private CurrentMentor currentMentor;
    private AlertDialog.Builder alertBuilder;
    private FirebaseAuth mAuth;
    private String currentMentorId;
    private SaveMentorInstance mentorInstance;
    private HashMap<String, String> hashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_categories);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        alertBuilder = new AlertDialog.Builder(this);

        listView = (ListView) findViewById(R.id.list_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        currentMentor = new CurrentMentor();
        mentorInstance = new SaveMentorInstance();

        adapter = new ChooseCategoryAdapter(getApplicationContext(), categoryList);
        listView.setAdapter(adapter);

        progressBar.setVisibility(View.VISIBLE);
        firestore.collection("Categories").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        Map categoryMap = document.getData();
                        ChooseCategory category = new ChooseCategory();
                        category.setcategoryChecked(false);
                        category.setCategoryId(document.getId());
                        category.setCategoryName(categoryMap.get("name").toString());
                        categoryList.add(category);
                        adapter.notifyDataSetChanged();
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else{
                    progressBar.setVisibility(View.INVISIBLE);
                }
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
            Boolean itemChecked = (adapter.itemsChecked());
            categoryList = adapter.getCheckedList();
            if(itemChecked){

                progressBar.setVisibility(View.VISIBLE);
                for(ChooseCategory category : categoryList){
                    if(category.getcategoryChecked()){
                        currentMentor.getCategories().add(Pair.create(category.getCategoryName(), category.getCategoryId()));
                    }
                }
                progressBar.setVisibility(View.INVISIBLE);
                Intent i = new Intent(MentorCategoriesActivity.this, DatesMentorActivity.class);
                startActivity(i);
                finish();
            }
            else{
                alertBuilder.setTitle("Category");
                alertBuilder.setMessage("Choose at least one category");
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

}
