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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.egcoders.technologysolution.netclub.model.post.Post;
import com.egcoders.technologysolution.netclub.data.adapter.PostAdapter;
import com.egcoders.technologysolution.netclub.R;
import com.egcoders.technologysolution.netclub.data.presenter.UserPresenter;
import com.egcoders.technologysolution.netclub.data.interfaces.UserProfile;
import com.egcoders.technologysolution.netclub.model.post.PostData;
import com.egcoders.technologysolution.netclub.model.profile.UserData;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostsUserFragment extends Fragment implements UserProfile.View {

    private UserProfile.Presenter userPresenter;
    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private List<Post> postsUserList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout refreshLayout;
    private ProgressBar progressBar;
    private TextView tvNoMorePosts;

    public PostsUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_posts_user, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        refreshLayout = view.findViewById(R.id.refreshList);
        tvNoMorePosts = view.findViewById(R.id.tv_no_posts);
        progressBar = view.findViewById(R.id.progressBar);
        initRecyclerView();
        userPresenter = new UserPresenter(getActivity(), this);
        userPresenter.getUserPosts();
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

        postsUserList.addAll(postData.getData());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                if(postsUserList.size() == 0)
                    tvNoMorePosts.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void showMorePosts(PostData postData) {

        postsUserList.addAll(postData.getData());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void showUserSavePosts(PostData postData) {

    }

    @Override
    public void showMoreSavePosts(PostData postData) {

    }
}
