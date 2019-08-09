package com.egcoders.technologysolution.netclub.ui.activities;

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
import com.egcoders.technologysolution.netclub.R;
import com.egcoders.technologysolution.netclub.Utils.SharedPreferenceConfig;
import com.egcoders.technologysolution.netclub.data.presenter.UserPresenter;
import com.egcoders.technologysolution.netclub.data.interfaces.UserProfile;
import com.egcoders.technologysolution.netclub.data.pager.ViewPagerAdapter;
import com.egcoders.technologysolution.netclub.model.post.PostData;
import com.egcoders.technologysolution.netclub.model.profile.UserData;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity implements UserProfile.View {

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;
    private UserProfile.Presenter userPresenter;
    private CircleImageView userImage;
    private TextView userName;
    private Button editBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        viewPager = findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout = findViewById(R.id.tabLayout);
        userImage = findViewById(R.id.userImage);
        userName = findViewById(R.id.userName);
        editBtn = findViewById(R.id.editBtn);

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
        customTab1.setText(getString(R.string.posts));
        customTab1.setBackgroundResource(R.color.transparent);
        tabLayout.getTabAt(0).setCustomView(customTab1);
        customTab2.setText(getString(R.string.saved));
        customTab2.setBackgroundResource(R.color.transparent);
        tabLayout.getTabAt(1).setCustomView(customTab2);


    }

    @Override
    public void showUserData(UserData user) {

        userName.setText(user.getName());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.profile);
        Glide.with(UserProfileActivity.this).applyDefaultRequestOptions(requestOptions).load(user.getPhoto_max())
                .into(userImage);
    }

    @Override
    public void showUserPosts(PostData post) {

    }

    @Override
    public void showMorePosts(PostData post) {

    }

    @Override
    public void showUserSavePosts(PostData post) {

    }

    @Override
    public void showMoreSavePosts(PostData post) {

    }
}