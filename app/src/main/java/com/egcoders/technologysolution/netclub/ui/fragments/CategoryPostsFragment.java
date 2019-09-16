package com.egcoders.technologysolution.netclub.ui.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.egcoders.technologysolution.netclub.Utils.RunLayoutAnimation;
import com.egcoders.technologysolution.netclub.data.interfaces.CategoryPosts;
import com.egcoders.technologysolution.netclub.data.presenter.CategoryPostsPresenter;
import com.egcoders.technologysolution.netclub.model.post.Post;
import com.egcoders.technologysolution.netclub.ui.adapter.PostAdapter;
import com.egcoders.technologysolution.netclub.R;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryPostsFragment extends ViewstupFragment implements CategoryPosts.View {

    private static final String TAG = HomeFragment.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<Post> postsList = new ArrayList<>();
    private PostAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private CategoryPosts.Presenter presenter;
    private TextView textPosts;
    private LottieAnimationView loadingAnimation;
    private int categoryId;

    @Override
    protected int getViewStubLayoutResource() {
        return R.layout.fragment_category_posts;
    }

    @Override
    protected void onCreateViewAfterViewStubInflated(View view, Bundle savedInstanceState) {

        categoryId = getArguments().getInt("category");
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshList);
        textPosts = (TextView) view.findViewById(R.id.textPosts);
        loadingAnimation= view.findViewById(R.id.loadingAnimation);

        adapter = new PostAdapter(getActivity(), postsList, 0);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        presenter = new CategoryPostsPresenter(getActivity(), this);
        presenter.loadPosts(categoryId);

        // Get posts when reached to then end of recycler view
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean reachedBottom = !recyclerView.canScrollVertically(1);
                if(reachedBottom && postsList.size() > 0){
                    presenter.loadMorePosts(categoryId);
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
                presenter.loadPosts(categoryId);
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void viewPosts(List<Post> list) {
        postsList.clear();
        postsList.addAll(list);
        if(getActivity() == null)
            return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingAnimation.setVisibility(View.GONE);
                RunLayoutAnimation.run(recyclerView, getContext());

                if(postsList.size() == 0){
                    textPosts.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void viewMorePosts(List<Post> list) {
        postsList.addAll(list);
        if(getActivity() == null)
            return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RunLayoutAnimation.run(recyclerView, getContext());
            }
        });
    }

    @Override
    public void viewCategories(List<String> list) {

    }
}
