package com.egcoders.technologysolution.netclub.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.egcoders.technologysolution.netclub.data.interfaces.Home;
import com.egcoders.technologysolution.netclub.data.presenter.HomePresenter;
import com.egcoders.technologysolution.netclub.model.post.Post;
import com.egcoders.technologysolution.netclub.ui.adapter.PostAdapter;
import com.egcoders.technologysolution.netclub.R;
import com.egcoders.technologysolution.netclub.model.post.PostData;
import com.egcoders.technologysolution.netclub.ui.activities.AddPostActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements Home.View {

    private static final String TAG = HomeFragment.class.getSimpleName();
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private List<Post> postsList = new ArrayList<>();
    private PostAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private Home.Presenter homePresenter;
    private TextView textPosts;
    private LottieAnimationView loadingAnimation;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshList);
        textPosts = (TextView) view.findViewById(R.id.textPosts);
        loadingAnimation= view.findViewById(R.id.loadingAnimation);

        homePresenter = new HomePresenter(getActivity(), this);

        adapter = new PostAdapter(getActivity(), postsList, 0);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        homePresenter = new HomePresenter(getActivity(), this);

        homePresenter.loadPosts();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddPostActivity.class));
            }
        });

        // Get posts when reached to then end of recycler view
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean reachedBottom = !recyclerView.canScrollVertically(1);
                if(reachedBottom && postsList.size() > 0){
                    homePresenter.loadMorePosts();
                }
            }
        });

        // Refresh Data
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                postsList.clear();
                adapter.notifyDataSetChanged();
                loadingAnimation.setVisibility(View.VISIBLE);
                homePresenter.loadPosts();
                refreshLayout.setRefreshing(false);
            }
        });
        return view;
    }

    @Override
    public void showPosts(PostData data) {
        postsList.clear();
        postsList.addAll(data.getData());
        if(getActivity() == null)
            return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingAnimation.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();

                if(postsList.size() == 0){
                    textPosts.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void showMorePosts(PostData data) {
        postsList.addAll(data.getData());
        if(getActivity() == null)
            return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        homePresenter.clearDisposal();
        adapter.clearDisposal();
    }
}
