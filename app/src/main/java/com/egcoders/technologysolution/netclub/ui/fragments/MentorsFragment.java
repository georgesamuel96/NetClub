package com.egcoders.technologysolution.netclub.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.egcoders.technologysolution.netclub.model.Mentor;
import com.egcoders.technologysolution.netclub.ui.adapter.MentorAdapter;
import com.egcoders.technologysolution.netclub.R;
import com.egcoders.technologysolution.netclub.data.instance.SaveMentorInstance;
import com.egcoders.technologysolution.netclub.Utils.SharedPreferenceConfig;
import com.egcoders.technologysolution.netclub.ui.activities.AddMentorActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class MentorsFragment extends Fragment {

    /*private FloatingActionButton fab;
    private FirebaseFirestore firestore;
    //private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SaveMentorInstance saveMentorInstance;
    private DocumentSnapshot lastVisible;
    private ArrayList<Mentor> mentorList = new ArrayList<>();
    private MentorAdapter adapter;
    private SharedPreferenceConfig preferenceConfig;
    private TextView textComingSoon;*/
    private static final String TAG = MentorsFragment.class.getSimpleName();

    public MentorsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mentors, container, false);
        Log.d(TAG, "onCreateView");
        /*firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        saveMentorInstance = new SaveMentorInstance();

        fab = (FloatingActionButton) view.findViewById(R.id.add_mentor);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        textComingSoon = (TextView) view.findViewById(R.id.textComingSoon);
        preferenceConfig = new SharedPreferenceConfig(getContext());
        
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMentor();
            }
        });

        Map<String, Object> currentUserMap = preferenceConfig.getCurrentUser();
        if(currentUserMap.get("userStatue").equals("2")){
            fab.show();
        }

        if(!saveMentorInstance.getIsFirstLoad()) {

            lastVisible = saveMentorInstance.getDocumentSnapshot();
            mentorList = saveMentorInstance.getList();

        }

        adapter = new MentorAdapter(mentorList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // Get mentors when reached to then end of recycler view
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean reachedBottom = !recyclerView.canScrollVertically(1);
                if(reachedBottom){
                    loadMoreMentors();
                }
            }
        });

        // Get Mentors for first time
        if(saveMentorInstance.getIsFirstLoad()) {

            saveMentorInstance.setIsFirstLoad(false);

            progressBar.setVisibility(View.VISIBLE);
            Query query = firestore.collection("Mentors").limit(10);
            query.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    if(e == null) {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            lastVisible = queryDocumentSnapshots.getDocuments()
                                    .get(queryDocumentSnapshots.size() - 1);
                            saveMentorInstance.setDocumentSnapshot(lastVisible);

                            if(queryDocumentSnapshots.getDocumentChanges().size() == 0){
                                textComingSoon.setVisibility(View.VISIBLE);
                            }

                            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {

                                    Map<String, Object> mentorMap = doc.getDocument().getData();
                                    Mentor mentor = new Mentor();
                                    mentor.setName(mentorMap.get("name").toString());
                                    mentor.setImage_url(mentorMap.get("profile_url").toString());
                                    mentor.setImageThumb_url(mentorMap.get("profileThumb_url").toString());
                                    mentor.setId(doc.getDocument().getId());
                                    mentor.setContent(mentorMap.get("content").toString());

                                    mentorList.add(mentor);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                            saveMentorInstance.setList(mentorList);
                        }
                    }

                }
            });
            progressBar.setVisibility(View.INVISIBLE);

        }*/

        return view;
    }

    /*private void loadMoreMentors() {

        progressBar.setVisibility(View.VISIBLE);
        Query query = firestore.collection("Mentors")
                .startAfter(lastVisible).limit(10);
        query.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(e == null) {
                    if (!queryDocumentSnapshots.isEmpty()) {

                        lastVisible = queryDocumentSnapshots.getDocuments()
                                .get(queryDocumentSnapshots.size() - 1);
                        saveMentorInstance.setDocumentSnapshot(lastVisible);

                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                Map<String, Object> mentorMap = doc.getDocument().getData();
                                Mentor mentor = new Mentor();
                                mentor.setName(mentorMap.get("name").toString());
                                mentor.setImage_url(mentorMap.get("profile_url").toString());
                                mentor.setImageThumb_url(mentorMap.get("profileThumb_url").toString());
                                mentor.setId(doc.getDocument().getId());
                                mentor.setContent(mentorMap.get("content").toString());

                                mentorList.add(mentor);
                                adapter.notifyDataSetChanged();
                            }
                        }
                        saveMentorInstance.setList(mentorList);
                        progressBar.setVisibility(View.INVISIBLE);

                    }
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void addMentor() {
        getActivity().startActivity(new Intent(getContext(), AddMentorActivity.class));
    }*/

}
