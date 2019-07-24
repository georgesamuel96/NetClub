package com.egcoders.technologysolution.netclub.Activities;

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
import com.egcoders.technologysolution.netclub.R;
import com.egcoders.technologysolution.netclub.data.SaveUserInstance;
import com.egcoders.technologysolution.netclub.data.SharedPreferenceConfig;
import com.egcoders.technologysolution.netclub.data.UserSharedPreference;
import com.egcoders.technologysolution.netclub.model.UserResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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
    private SharedPreferenceConfig preferenceConfig;
    private View headerView;
    private TextView headerEmail, headerName;
    private CircleImageView headerProfile;
    private String currentUserId;
    private Boolean finishActivity = false;
    private UserSharedPreference preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navView = (NavigationView) findViewById(R.id.navigation);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_menu);

        configureNavigationDrawer();

        preference = new UserSharedPreference(MainActivity.this);

        /*headerView = navView.getHeaderView(0);
        headerEmail = (TextView) headerView.findViewById(R.id.email_header);
        headerName = (TextView) headerView.findViewById(R.id.name_header);
        headerProfile = (CircleImageView) headerView.findViewById(R.id.profile_header);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userInstance = new SaveUserInstance();

        preferenceConfig = new SharedPreferenceConfig(getApplicationContext());

        currentUserId = preferenceConfig.getSharedPrefConfig();
        final Map<String, Object> currentUserMap = preferenceConfig.getCurrentUser();

        headerEmail.setText(currentUserMap.get("email").toString());
        headerEmail.setVisibility(View.VISIBLE);
        headerName.setText(currentUserMap.get("name").toString());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.profile);
        Glide.with(getApplicationContext()).applyDefaultRequestOptions(requestOptions)
                .load(currentUserMap.get("profile_url").toString()).thumbnail(Glide.with(getApplicationContext())
                .load(currentUserMap.get("profileThumb").toString())).into(headerProfile);

        Boolean categorySelected = (Boolean) currentUserMap.get("categorySelected");
        if (!categorySelected) {
            sendToCategories();
        } else {

            firestore.collection("Users").document(currentUserId).collection("selectedCategory")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        int numberCategories = task.getResult().getDocumentChanges().size();
                        if(numberCategories > 0){

                        }
                        else{
                            currentUserMap.put("selectedCategory", false);
                            preferenceConfig.setCurrentUser(currentUserMap);
                            sendToCategories();
                        }
                    }
                    else{

                    }
                }
            });*/

            homeFragment = new HomeFragment();
            categoriesFragment = new CategoriesFragment();
            usersFragment = new UsersFragment();
            mentorsFragment = new MentorsFragment();

            replaceFragment(homeFragment);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    if (menuItem.getItemId() == R.id.home) {
                        getSupportActionBar().setTitle("Net Club");
                        replaceFragment(homeFragment);
                        return true;
                    } else if (menuItem.getItemId() == R.id.categories) {
                        getSupportActionBar().setTitle("Categories");
                        replaceFragment(categoriesFragment);
                        return true;
                    } else if (menuItem.getItemId() == R.id.users) {
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

                    Intent i = new Intent(MainActivity.this, UserProfileActivity.class);
                    startActivity(i);
                }
                else if(itemId == R.id.change_category){

                    sendToCategories();
                }
                else if (itemId == R.id.about_us) {

                    getSupportActionBar().setTitle("About Us");
                    fragment = new AboutUsFragment();
                }
                else if (itemId == R.id.share) {

                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = "Meet mentors and instructors online";
                    String link = "https://play.google.com/store/apps/details?id=com.egcoders.technologysolution.netclub";
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareBody);
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, link);
                    startActivity(Intent.createChooser(sharingIntent, "Share using"));
                }
                else if (itemId == R.id.log_out) {

                    UserResponse user = preference.getUser();
                    user.getData().setToken("");
                    preference.setUser(user);

                    sendToLogin();
                }

                if (fragment != null) {

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame, fragment);
                    transaction.addToBackStack(null);
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
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return true;
    }

    private void sendToCategories() {
        finishActivity = true;
        Intent i = new Intent(MainActivity.this, CategoriesActivity.class);
        startActivity(i);

    }

    private void sendToLogin() {

        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {


        if(this.drawerLayout.isDrawerOpen(GravityCompat.START)){
            this.drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();

            //userInstance.setIsActivityFirstLoad(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        /*FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }*/
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(finishActivity){
            finish();
        }
    }
}
