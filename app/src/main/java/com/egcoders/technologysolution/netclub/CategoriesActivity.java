package com.egcoders.technologysolution.netclub;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class CategoriesActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar toolbar;
    private ListView listView;
    private ChooseCategoryAdapter adapter;
    private ArrayList<ChooseCategory> categoryList = new ArrayList<>();
    private FirebaseFirestore firestore;
    private ProgressBar progressBar;
    private User user;
    private AlertDialog.Builder alertBuilder;
    //private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    //private String currentUserId;
    private SaveUserInstance userInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        alertBuilder = new AlertDialog.Builder(this);

        listView = (ListView) findViewById(R.id.list_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        //firebaseUser = mAuth.getCurrentUser();

        user = new User();
        userInstance = new SaveUserInstance();

        //currentUserId = firebaseUser.getUid();
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", userInstance.getName());
        userMap.put("birthday", userInstance.getBirthday());
        userMap.put("phone", userInstance.getPhone());
        userMap.put("profile_url", userInstance.getProfile_url());
        userMap.put("profileThumb", userInstance.getProfileThumb_url());
        userMap.put("categorySelected", userInstance.getCategorySelected());
        userMap.put("email", userInstance.getEmail());

        user.setUserName(userInstance.getName());
        user.setUserBirthday(userInstance.getBirthday());
        user.setUserPhone(userInstance.getPhone());
        user.setUserImageUrl(userInstance.getProfile_url());
        user.setUserImageThumbUrl(userInstance.getProfileThumb_url());
        user.setUserSelectCategories(userInstance.getCategorySelected());
        user.setUserEmail(userInstance.getEmail());
        if(userInstance.getIsFirstLoad())
            userInstance.getList().add(user);
        else
            userInstance.getList().add(0, user);

        progressBar.setVisibility(View.VISIBLE);
        firestore.collection("Users").document(userInstance.getId()).set(userMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                        else{
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });

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
                        Map<String, Object> categoryMap = new HashMap<>();
                        categoryMap.put("name", category.getCategoryName());
                        firestore.collection("Users")
                                .document(userInstance.getId())
                                .collection("selectedCategory")
                                .document(category.getCategoryId())
                                .set(categoryMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    progressBar.setVisibility(View.INVISIBLE);
                                    startActivity(new Intent(CategoriesActivity.this, MainActivity.class));
                                    finish();
                                }
                                else{
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    }
                }

                progressBar.setVisibility(View.VISIBLE);
                firestore.collection("Users").document(userInstance.getId()).update("categorySelected",
                        true).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    userInstance.setCategorySelected(true);
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                                else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
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
}
