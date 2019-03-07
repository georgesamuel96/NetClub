package com.egcoders.technologysolutions.netclub;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private FirebaseFirestore firestore;
    private Button editBtn;
    private TextView email, birthday, phone, name;
    private CircleImageView profile;
    //private FirebaseUser currentUser;
    //private String userId;
    private ProgressBar progressBar;
    private SaveUserInstance userInstance;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        editBtn = (Button) view.findViewById(R.id.editBtn);
        email = (TextView) view.findViewById(R.id.user_email);
        birthday = (TextView) view.findViewById(R.id.user_birthday);
        phone = (TextView) view.findViewById(R.id.user_phone);
        name = (TextView) view.findViewById(R.id.user_name);
        profile = (CircleImageView) view.findViewById(R.id.user_profile);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        firestore = FirebaseFirestore.getInstance();
        //currentUser = FirebaseAuth.getInstance().getCurrentUser();
        //userId = currentUser.getUid();
        userInstance = new SaveUserInstance();

        progressBar.setVisibility(View.VISIBLE);
        email.setText(userInstance.getEmail());
        birthday.setText(userInstance.getBirthday());
        phone.setText(userInstance.getPhone());
        name.setText(userInstance.getName());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.profile);
        Glide.with(getContext()).applyDefaultRequestOptions(requestOptions).load(userInstance.getProfile_url()).thumbnail(
                Glide.with(getContext()).load(userInstance.getProfileThumb_url())
        ).into(profile);
        progressBar.setVisibility(View.INVISIBLE);

        /*progressBar.setVisibility(View.VISIBLE);
        firestore.collection("Users").document(userInstance.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Map<String, Object> userMap = task.getResult().getData();
                    email.setText(userInstance.getEmail());
                    birthday.setText(userMap.get("birthday").toString());
                    phone.setText(userMap.get("phone").toString());
                    name.setText(userMap.get("name").toString());
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.profile);
                    Glide.with(getContext()).applyDefaultRequestOptions(requestOptions).load(userMap.get("profile_url")).thumbnail(
                            Glide.with(getContext()).load(userMap.get("profileThumb"))
                    ).into(profile);
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else{
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });*/

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), EditUserProfileActivity.class);
                startActivity(i);
            }
        });

        return view;
    }

}
