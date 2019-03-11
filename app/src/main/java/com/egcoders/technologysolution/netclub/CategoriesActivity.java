package com.egcoders.technologysolution.netclub;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
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
import com.google.firebase.firestore.DocumentChange;
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
    private FirebaseFirestore firestore,firestoreUserCategory;
    private ProgressDialog progressDialog;
    private SharedPreferenceConfig preferenceConfig;
    private AlertDialog.Builder alertBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferenceConfig = new SharedPreferenceConfig(this);
        progressDialog = new ProgressDialog(this);
        alertBuilder = new AlertDialog.Builder(this);


        listView = (ListView) findViewById(R.id.list_view);

        firestore = FirebaseFirestore.getInstance();
        firestoreUserCategory = FirebaseFirestore.getInstance();

        adapter = new ChooseCategoryAdapter(getApplicationContext(), categoryList);
        listView.setAdapter(adapter);

        progressDialog.setCancelable(false);
        progressDialog.setTitle("Categories");
        progressDialog.setMessage("Loading");
        progressDialog.show();

        preferenceConfig.resetCategoryList();
        firestoreUserCategory.collection("Users").document(preferenceConfig.getSharedPrefConfig())
                .collection("selectedCategory").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentChange document : task.getResult().getDocumentChanges()){
                        preferenceConfig.setUserCategory(document.getDocument().getId());
                    }

                    firestore.collection("Categories").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(QueryDocumentSnapshot document : task.getResult()){
                                    Map categoryMap = document.getData();
                                    ChooseCategory category = new ChooseCategory();
                                    category.setcategoryChecked((preferenceConfig.getUserCategory().indexOf(document.getId()) != -1));
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
                else{
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
            if(itemChecked) {

                progressDialog.setCancelable(false);
                progressDialog.setTitle("Submit your choosing");
                progressDialog.setMessage("Loading");
                progressDialog.show();

                firestore.collection("Users").document(preferenceConfig.getSharedPrefConfig())
                        .collection("selectedCategory").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for (DocumentChange document : task.getResult().getDocumentChanges()) {

                            String categoryId = document.getDocument().getId();
                            firestoreUserCategory.collection("Users").document(preferenceConfig.getSharedPrefConfig())
                                    .collection("selectedCategory").document(categoryId).delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                            } else {

                                            }
                                        }
                                    });
                        }
                    }
                });

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        categoryList = adapter.getCheckedList();


                        firestore.collection("Users").document(preferenceConfig.getSharedPrefConfig())
                                .update("categorySelected", true).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    Map<String, Object> currentUserMap = preferenceConfig.getCurrentUser();
                                    preferenceConfig.setCurrentUser(currentUserMap.get("name").toString(),
                                            currentUserMap.get("email").toString(), currentUserMap.get("phone").toString(),
                                            currentUserMap.get("birthday").toString(), currentUserMap.get("profileUrl").toString(),
                                            currentUserMap.get("profileThumbUrl").toString(), true);
                                } else {
                                }
                            }
                        });

                        for (final ChooseCategory category : categoryList) {
                            if (category.getcategoryChecked()) {
                                Map<String, Object> categoryMap = new HashMap<>();
                                categoryMap.put("name", category.getCategoryName());
                                firestore.collection("Users")
                                        .document(preferenceConfig.getSharedPrefConfig())
                                        .collection("selectedCategory")
                                        .document(category.getCategoryId())
                                        .set(categoryMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressDialog.dismiss();
                                            sendToMain();
                                        } else {
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                            }
                        }
                    }

                }, 1000);

            }
            else{
                alertBuilder.setTitle("Categories");
                alertBuilder.setMessage("You must choose at least on category");
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

    private void sendToMain(){
        Intent i = new Intent(CategoriesActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        sendToMain();
    }
}
