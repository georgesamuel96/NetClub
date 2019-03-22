package com.egcoders.technologysolution.netclub;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity implements UserProfile.View{

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;
    private SharedPreferenceConfig preferenceConfig;
    private UserProfile.Presenter userPresenter;
    private CircleImageView userImage;
    private TextView userName;
    private Button editBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        userImage = (CircleImageView) findViewById(R.id.userImage);
        userName = (TextView) findViewById(R.id.userName);
        editBtn = (Button) findViewById(R.id.editBtn);

        preferenceConfig = new SharedPreferenceConfig(this);

        tabLayout.setupWithViewPager(viewPager);
        setupTabLayout();
        viewPager.setCurrentItem(0);

        userPresenter = new UserPresenter(this, this);
        userPresenter.showUserData();

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserProfileActivity.this, EditUserProfileActivity.class);
                startActivity(i);
            }
        });
    }

    private void setupTabLayout() {

        TextView customTab1 = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        TextView customTab2 = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        customTab1.setText("Posts");
        customTab1.setBackgroundResource(R.color.transparent);
        tabLayout.getTabAt(0).setCustomView(customTab1);
        customTab2.setText("Saves");
        customTab2.setBackgroundResource(R.color.transparent);
        tabLayout.getTabAt(1).setCustomView(customTab2);


    }

    @Override
    public void showUserData(Map<String, Object> userMap) {
        userName.setText(userMap.get("name").toString());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.profile);
        Glide.with(UserProfileActivity.this).applyDefaultRequestOptions(requestOptions).load(userMap.get("profile_url"))
                .thumbnail(Glide.with(UserProfileActivity.this).load(userMap.get("profileThumb"))).into(userImage);
    }

    @Override
    public void showUserPosts(ArrayList<Post> postsList) {

    }

    @Override
    public void showMorePosts(ArrayList<Post> postsList) {

    }

    @Override
    public void showUserSavePosts(ArrayList<Post> postsList) {

    }

    @Override
    public void showMoreSavePosts(ArrayList<Post> postsList) {

    }
}
