package com.egcoders.technologysolution.netclub.Activities;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.egcoders.technologysolution.netclub.data.CategoriesPager;
import com.egcoders.technologysolution.netclub.data.CategoryPosts;
import com.egcoders.technologysolution.netclub.data.CategoryPostsPresenter;
import com.egcoders.technologysolution.netclub.model.Post;
import com.egcoders.technologysolution.netclub.R;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CategoriesFragment extends Fragment implements CategoryPosts.View {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private CategoriesPager pager;
    private CategoryPosts.Presenter presenter;
    private List<String> categoriesList = new ArrayList<>();

    public CategoriesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.pager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs_container);

        pager = new CategoriesPager(getFragmentManager(), categoriesList);
        viewPager.setAdapter(pager);
        tabLayout.setupWithViewPager(viewPager);

        presenter = new CategoryPostsPresenter(getActivity(), this);
        presenter.loadCategories();

        return view;
    }

    @Override
    public void viewPosts(List<Post> list) {

    }

    @Override
    public void viewMorePosts(List<Post> list) {

    }

    @Override
    public void viewCategories(List<String> list) {
        categoriesList.clear();
        /*Fragment fragment;
        for(String name : list){
            if(name.equals("Tech / Programming")){
                fragment = new TechProgrammingFragment();
                categoriesList.add(Pair.create(fragment, name));
            }
            else if(name.equals("Social skills / Motivation")){
                fragment = new SocialskillsMotivationFragment();
                categoriesList.add(Pair.create(fragment, name));
            }
            else if(name.equals("Music")){
                fragment = new MusicFragment();
                categoriesList.add(Pair.create(fragment, name));
            }
        }*/
        categoriesList.addAll(list);
        //pager.notifyChangeInPosition(list.size());
        pager.notifyDataSetChanged();
    }

}
