package com.egcoders.technologysolution.netclub.ui.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.egcoders.technologysolution.netclub.data.interfaces.CategoryPosts;
import com.egcoders.technologysolution.netclub.data.presenter.CategoryPostsPresenter;
import com.egcoders.technologysolution.netclub.model.post.Post;
import com.egcoders.technologysolution.netclub.data.adapter.PostAdapter;
import com.egcoders.technologysolution.netclub.R;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryPostsFragment extends ViewstupFragment implements CategoryPosts.View {

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private ArrayList<Post> postsList = new ArrayList<>();
    private CategoryPosts.Presenter presenter;
    private String category;
    private SwipeRefreshLayout refreshLayout;
    private RelativeLayout container;

    @Override
    protected int getViewStubLayoutResource() {
        return R.layout.fragment_category_posts;
    }

    @Override
    protected void onCreateViewAfterViewStubInflated(View view, Bundle savedInstanceState) {

        container = (RelativeLayout) view.findViewById(R.id.container);

        category = getArguments().getString("category");
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshList);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        adapter = new PostAdapter(getActivity(), postsList, 0);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        presenter = new CategoryPostsPresenter(getActivity(), this);
        presenter.loadPosts(category);

        // Get posts when reached to then end of recycler view
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean reachedBottom = !recyclerView.canScrollVertically(1);
                if(reachedBottom && postsList.size() > 0){
                    presenter.loadMorePosts(category);
                }
            }
        });

        // Refresh Data
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                postsList.clear();
                adapter.notifyDataSetChanged();
                presenter.loadPosts(category);
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void viewPosts(List<Post> list) {
        postsList.addAll(list);
        System.out.println("POSt SIZE: " + postsList.size());
        if(getActivity() == null)
            return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(postsList.size() == 0){
                    container.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
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
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void viewCategories(List<String> list) {

    }
}
