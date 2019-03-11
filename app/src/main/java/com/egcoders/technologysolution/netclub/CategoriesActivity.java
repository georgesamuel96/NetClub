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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CategoriesActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar toolbar;
    private ListView listView;
    private ChooseCategoryAdapter adapter;
    private ArrayList<ChooseCategory> categoryList = new ArrayList<>();
    private FirebaseFirestore firestore;
    private ProgressDialog progressDialog;
    private SharedPreferenceConfig preferenceConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferenceConfig = new SharedPreferenceConfig(this);
        progressDialog = new ProgressDialog(this);


        listView = (ListView) findViewById(R.id.list_view);

        firestore = FirebaseFirestore.getInstance();

        adapter = new ChooseCategoryAdapter(getApplicationContext(), categoryList);
        listView.setAdapter(adapter);

        progressDialog.setCancelable(false);
        progressDialog.setTitle("Categories");
        progressDialog.setMessage("Loading");
        progressDialog.show();
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
                    progressDialog.dismiss();
                }
                else{
                    progressDialog.dismiss();
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
                progressDialog.setCancelable(false);
                progressDialog.setTitle("Submit your choosing");
                progressDialog.setMessage("Loading");
                progressDialog.show();

                firestore.collection("Users").document(preferenceConfig.getSharedPrefConfig())
                        .update("categorySelected", true).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            Map<String, Object> currentUserMap = preferenceConfig.getCurrentUser();
                            preferenceConfig.setCurrentUser(currentUserMap.get("name").toString(),
                                    currentUserMap.get("email").toString(), currentUserMap.get("phone").toString(),
                                    currentUserMap.get("birthday").toString(), currentUserMap.get("profileUrl").toString(),
                                    currentUserMap.get("profileThumbUrl").toString(), true);
                        }
                        else {
                        }
                    }
                });

                for(ChooseCategory category : categoryList){
                    if(category.getcategoryChecked()){
                        Map<String, Object> categoryMap = new HashMap<>();
                        categoryMap.put("name", category.getCategoryName());
                        firestore.collection("Users")
                                .document(preferenceConfig.getSharedPrefConfig())
                                .collection("selectedCategory")
                                .document(category.getCategoryId())
                                .set(categoryMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    progressDialog.dismiss();
                                    startActivity(new Intent(CategoriesActivity.this, MainActivity.class));
                                    finish();
                                }
                                else{
                                    progressDialog.dismiss();
                                }
                            }
                        });
                    }
                }

            }
        }

        return true;
    }
}
