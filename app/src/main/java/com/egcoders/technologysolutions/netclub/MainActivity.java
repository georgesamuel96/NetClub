package com.egcoders.technologysolutions.netclub;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

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
    private FirebaseUser currentUser;
    private FirebaseFirestore firestore;
    private User user;

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

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        user = new User();

        Boolean checkMentorFragment = getIntent().getBooleanExtra("TOP", false);
        if(currentUser != null) {

            homeFragment = new HomeFragment();
            categoriesFragment = new CategoriesFragment();
            usersFragment = new UsersFragment();
            mentorsFragment = new MentorsFragment();

            if(!checkMentorFragment)
                replaceFragment(homeFragment);
            else
                replaceFragment(mentorsFragment);

            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    if (menuItem.getItemId() == R.id.home) {
                        replaceFragment(homeFragment);
                        return true;
                    } else if (menuItem.getItemId() == R.id.categories) {
                        replaceFragment(categoriesFragment);
                        return true;
                    } else if (menuItem.getItemId() == R.id.users) {
                        replaceFragment(usersFragment);
                        return true;
                    } else if (menuItem.getItemId() == R.id.mentors) {
                        replaceFragment(mentorsFragment);
                        return true;
                    }

                    return false;
                }
            });
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

                    fragment = new ProfileFragment();
                }
                else if (itemId == R.id.about_us) {

                    fragment = new AboutUsFragment();
                }
                else if (itemId == R.id.share) {

                    //fragment = new AboutUsFragment();
                }
                else if (itemId == R.id.log_out) {

                    mAuth.signOut();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
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

    @Override
    protected void onStart() {
        super.onStart();

        if(currentUser != null){

            firestore.collection("Users").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){

                        Map<String, Object> userMap = task.getResult().getData();
                        Object categorySelected = userMap.get("categorySelected");

                        if(categorySelected.equals(true)){

                        }
                        else{
                            user.setUserName(userMap.get("name").toString());
                            user.setUserBirthday(userMap.get("birthday").toString());
                            user.setUserPhone(userMap.get("phone").toString());
                            user.setUserSelectCategories(false);
                            user.setUserImageUrl(userMap.get("profile_url").toString());
                            user.setUserImageThumbUrl(userMap.get("profileThumb").toString());

                            sendToCategories();
                        }
                    }
                    else{

                    }
                }
            });
        }
        else{
            sendToLogin();
        }
    }

    private void sendToCategories() {
        Intent i = new Intent(MainActivity.this, CategoriesActivity.class);
        i.putExtra("user", user);
        startActivity(i);
        finish();
    }

    private void sendToLogin() {

        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }
}
