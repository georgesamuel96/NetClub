package com.egcoders.technologysolution.netclub.ui.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.egcoders.technologysolution.netclub.Utils.RunLayoutAnimation;
import com.egcoders.technologysolution.netclub.data.interfaces.Message;
import com.egcoders.technologysolution.netclub.model.post.Post;
import com.egcoders.technologysolution.netclub.ui.adapter.PostAdapter;
import com.egcoders.technologysolution.netclub.R;
import com.egcoders.technologysolution.netclub.data.presenter.UserPresenter;
import com.egcoders.technologysolution.netclub.data.interfaces.UserProfile;
import com.egcoders.technologysolution.netclub.model.post.PostData;
import com.egcoders.technologysolution.netclub.model.post.SavePostData;
import com.egcoders.technologysolution.netclub.model.post.SavePostDetail;
import com.egcoders.technologysolution.netclub.model.profile.UserData;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SavesUserFragment extends Fragment implements UserProfile.View, Message {

    private UserProfile.Presenter userPresenter;
    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private List<Post> postsUserList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout refreshLayout;
    private TextView tvNoMorePosts;
    private LottieAnimationView loadingAnimation;

    public SavesUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_saves_user, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        refreshLayout = view.findViewById(R.id.refreshList);
        tvNoMorePosts = view.findViewById(R.id.tv_no_posts);
        loadingAnimation = view.findViewById(R.id.loadingAnimation);
        initRecyclerView();
        userPresenter = new UserPresenter(getActivity(), this, this);
        userPresenter.getUserSavePosts();
        // Get posts when reached to then end of recycler view
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean reachedBottom = !recyclerView.canScrollVertically(1);
                if(reachedBottom && postsUserList.size() > 0){
                    userPresenter.getMorePosts();
                }
            }
        });

        // Refresh Data
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                postsUserList.clear();
                adapter.notifyDataSetChanged();
                userPresenter.getUserPosts();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void initRecyclerView() {
        adapter = new PostAdapter(getActivity(), postsUserList, 0);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void showUserData(UserData user) {

    }

    @Override
    public void showUserPosts(PostData postData) {

    }

    @Override
    public void showMorePosts(PostData postData) {

    }

    @Override
    public void showUserSavePosts(SavePostData postData) {
        for(SavePostDetail postDetail : postData.getData()){
            UserData userData = postDetail.getUser();
            userData.setPhoto_max(getProfileUrl(userData.getPhoto_max()));
            Post post = postDetail.getPost();
            post.setUserData(userData);
            post.setSaved(true);
            postsUserList.add(post);
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingAnimation.setVisibility(View.GONE);
                RunLayoutAnimation.run(recyclerView, getContext());
                if(postsUserList.size() == 0)
                    tvNoMorePosts.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void showMoreSavePosts(SavePostData postData) {
        for(SavePostDetail postDetail : postData.getData()){
            UserData userData = postDetail.getUser();
            Post post = postDetail.getPost();
            post.setSaved(true);
            post.setUserData(userData);
            postsUserList.add(post);
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RunLayoutAnimation.run(recyclerView, getContext());
            }
        });
    }

    private String getProfileUrl(String url){
        int index = url.indexOf("net-club");
        url = "http://www.egcoders.net/" + url.substring(index, url.length());
        return url;
    }

    @Override
    public void successMessage(UserData data) {

    }

    @Override
    public void failMessage() {

    }
}
