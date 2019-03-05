package com.example.georgesamuel.netclub;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Nullable;


/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends Fragment {

    private UserAdapter adapter;
    private ArrayList<User> userList = new ArrayList<>();
    private RecyclerView recyclerView;
    private FirebaseFirestore firestore;
    private ProgressBar progressBar;
    private SaveUserInstance saveUserInstance;
    private DocumentSnapshot lastVisible;
    private int counter = 0;

    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        firestore = FirebaseFirestore.getInstance();
        saveUserInstance = new SaveUserInstance();

        if(!saveUserInstance.getIsFirstLoad()) {

            lastVisible = saveUserInstance.getDocumentSnapshot();
            userList = saveUserInstance.getList();
        }

        adapter = new UserAdapter(userList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // Get users when reached to then end of recycler view
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean reachedBottom = !recyclerView.canScrollVertically(1);
                if(reachedBottom){
                    loadMoreUsers();
                    System.out.println(true);
                }
            }
        });

        // Get users for first time
        if(saveUserInstance.getIsFirstLoad()) {

            saveUserInstance.setIsFirstLoad(false);
            progressBar.setVisibility(View.VISIBLE);

            progressBar.setVisibility(View.VISIBLE);
            Query query = firestore.collection("Users").limit(20);
            query.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    if (!queryDocumentSnapshots.isEmpty()) {

                        lastVisible = queryDocumentSnapshots.getDocuments()
                                .get(queryDocumentSnapshots.size() - 1);
                        saveUserInstance.setDocumentSnapshot(lastVisible);
                        userList.clear();

                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                Map<String, Object> userMap = doc.getDocument().getData();
                                User user = new User();
                                user.setUserName(userMap.get("name").toString());
                                user.setUserImageUrl(userMap.get("profile_url").toString());
                                user.setUserImageUrl(userMap.get("profileThumb").toString());
                                userList.add(user);
                                adapter.notifyDataSetChanged();
                            }
                        }
                        saveUserInstance.setList(userList);
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });

        }

        return view;
    }

    private void loadMoreUsers() {

        progressBar.setVisibility(View.VISIBLE);
        Query query = firestore.collection("Users")
                .startAfter(lastVisible).limit(20);
        query.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (!queryDocumentSnapshots.isEmpty()) {

                    lastVisible = queryDocumentSnapshots.getDocuments()
                            .get(queryDocumentSnapshots.size() - 1);
                    saveUserInstance.setDocumentSnapshot(lastVisible);

                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String blogPostId = doc.getDocument().getId();
                            Map<String, Object> userMap = doc.getDocument().getData();
                            User user = new User();
                            user.setUserName(userMap.get("name").toString());
                            user.setUserImageUrl(userMap.get("profile_url").toString());
                            user.setUserImageUrl(userMap.get("profileThumb_url").toString());
                            userList.add(user);
                            adapter.notifyDataSetChanged();
                        }
                    }
                    saveUserInstance.setList(userList);
                    progressBar.setVisibility(View.INVISIBLE);

                }
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

    }

}
