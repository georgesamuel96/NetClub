package com.egcoders.technologysolution.netclub;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MusicFragment extends Fragment implements CategoryPosts.View{

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private ArrayList<Post> postsList = new ArrayList<>();
    private CategoryPosts.Presenter presenter;
    private String category;
    private SwipeRefreshLayout refreshLayout;
    private Boolean isVisible = false;

    public MusicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_music, container, false);

        if(getUserVisibleHint())
            isVisible = true;

        if(isVisible) {
            System.out.println("visible");
            category = getArguments().getString("category");
            refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshList);
            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setHasFixedSize(true);
            adapter = new PostAdapter(postsList, 0);
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
                    if (reachedBottom && postsList.size() > 0) {
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

        return view;
    }

    @Override
    public void viewPosts(List<Post> list) {
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
    public void viewMorePosts(List<Post> list) {
        postsList.addAll(list);
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
