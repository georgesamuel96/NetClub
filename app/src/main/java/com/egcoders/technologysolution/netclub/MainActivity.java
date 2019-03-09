package com.egcoders.technologysolution.netclub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private android.support.v7.widget.Toolbar toolbar;
    private NavigationView navView;
    private FirebaseAuth mAuth;
    private BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment;
    private CategoriesFragment categoriesFragment;
    private UsersFragment usersFragment;
    private MentorsFragment mentorsFragment;
    private FirebaseFirestore firestore;
    private SaveUserInstance userInstance;
    private SharedPreferenceConfig preferences;
    private View headerView;
    private TextView headerEmail, headerName;
    private CircleImageView headerProfile;
    private String currentUserId;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_action_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navView = (NavigationView) findViewById(R.id.navigation);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_menu);

        configureNavigationDrawer();

        headerView = navView.getHeaderView(0);
        headerEmail = (TextView) headerView.findViewById(R.id.email_header);
        headerName = (TextView) headerView.findViewById(R.id.name_header);
        headerProfile = (CircleImageView) headerView.findViewById(R.id.profile_header);

        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userInstance = new SaveUserInstance();


        Boolean checkMentorFragment = getIntent().getBooleanExtra("TOP", false);
        preferences = new SharedPreferenceConfig(getApplicationContext());
        if(!preferences.getSharedPrefConfig().equals("Empty")) {

            currentUserId = preferences.getSharedPrefConfig();
            Map<String, Object> currentUserMap = preferences.getCurrentUser();

            headerEmail.setText(currentUserMap.get("email").toString());
            headerEmail.setVisibility(View.VISIBLE);
            headerName.setText(currentUserMap.get("name").toString());
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.profile);
            Glide.with(getApplicationContext()).applyDefaultRequestOptions(requestOptions)
                    .load(currentUserMap.get("profileUrl").toString()).thumbnail(Glide.with(getApplicationContext())
                    .load(currentUserMap.get("profileThumbUrl").toString())).into(headerProfile);

            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading");
            firestore.collection("Users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(!task.getResult().exists())
                    {
                        progressDialog.dismiss();
                        sendToLogin();
                    }
                    else
                    {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            replaceFragment(usersFragment);
                        }
                        else{

                        }
                    }
                }
            });

            homeFragment = new HomeFragment();
            categoriesFragment = new CategoriesFragment();
            usersFragment = new UsersFragment();
            mentorsFragment = new MentorsFragment();

            /*if(userInstance.getIsActivityFirstLoad()){

                userInstance.setIsActivityFirstLoad(false);

                replaceFragment(usersFragment);
            }*/



            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    /*if (menuItem.getItemId() == R.id.home) {
                        getSupportActionBar().setTitle("NetClub");
                        replaceFragment(homeFragment);
                        return true;
                    } else if (menuItem.getItemId() == R.id.categories) {
                        replaceFragment(categoriesFragment);
                        return true;
                    } else */if (menuItem.getItemId() == R.id.users) {
                        getSupportActionBar().setTitle("Users");
                        replaceFragment(usersFragment);
                        return true;
                    } else if (menuItem.getItemId() == R.id.mentors) {
                        getSupportActionBar().setTitle("Mentors");
                        replaceFragment(mentorsFragment);
                        return true;
                    }

                    return false;
                }
            });
        }
        else{
            sendToLogin();
        }

    }

    private void replaceFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }

    private void configureNavigationDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.navigation);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                Fragment fragment = null;
                int itemId = menuItem.getItemId();

                if (itemId == R.id.profile) {

                    Map<String, Object> currentUser = preferences.getCurrentUser();
                    String currentUserName = currentUser.get("name").toString();
                    getSupportActionBar().setTitle(currentUserName);
                    fragment = new ProfileFragment();
                }
                else if (itemId == R.id.about_us) {

                    getSupportActionBar().setTitle("About Us");
                    fragment = new AboutUsFragment();
                }
                else if (itemId == R.id.share) {

                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sharingIntent, "Share using"));
                }
                else if (itemId == R.id.log_out) {

                    preferences.setSharedPrefConfig("Empty");
                    mAuth.signOut();
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }

                if (fragment != null) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame, fragment);
                    transaction.commit();
                    drawerLayout.closeDrawers();
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch(itemId) {
            // Android home
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            // manage other entries if you have it ...
        }
        return true;
    }

    private void sendToCategories() {
        Intent i = new Intent(MainActivity.this, CategoriesActivity.class);
        //i.putExtra("user", user);
        startActivity(i);
        finish();
    }

    private void sendToLogin() {

        preferences.setSharedPrefConfig("Empty");
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        userInstance.setIsActivityFirstLoad(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }
}
