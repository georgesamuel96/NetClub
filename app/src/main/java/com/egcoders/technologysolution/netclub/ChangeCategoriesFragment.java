package com.egcoders.technologysolution.netclub;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChangeCategoriesFragment extends Fragment {

    private ListView listView;
    private FloatingActionButton fab;
    private FirebaseFirestore firestore, firestoreUserCategory;
    private ProgressDialog progressDialog;
    private ChooseCategoryAdapter adapter;
    private ArrayList<ChooseCategory> categoryList = new ArrayList<>();
    private SharedPreferenceConfig preferenceConfig;

    public ChangeCategoriesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_categories, container, false);

        listView = (ListView) view.findViewById(R.id.list_view);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        progressDialog = new ProgressDialog(getContext());
        firestore = FirebaseFirestore.getInstance();
        firestoreUserCategory = FirebaseFirestore.getInstance();
        preferenceConfig = new SharedPreferenceConfig(getContext());
        adapter = new ChooseCategoryAdapter(getContext(), categoryList);
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


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.setCancelable(false);
                progressDialog.setTitle("Submit your choosing");
                progressDialog.setMessage("Loading");
                progressDialog.show();

                firestore.collection("Users").document(preferenceConfig.getSharedPrefConfig())
                        .collection("selectedCategory").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for(DocumentChange document : task.getResult().getDocumentChanges()){

                            String categoryId = document.getDocument().getId();
                            firestoreUserCategory.collection("Users").document(preferenceConfig.getSharedPrefConfig())
                                    .collection("selectedCategory").document(categoryId).delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){

                                            }
                                            else {

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

                        System.out.println("true");
                        Boolean itemChecked = (adapter.itemsChecked());
                        categoryList = adapter.getCheckedList();
                        if(itemChecked) {

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

                                            } else {
                                                progressDialog.dismiss();
                                            }
                                        }
                                    });
                                }
                            }
                        }

                    }
                }, 1000);

            }
        });


        return  view;
    }

}
