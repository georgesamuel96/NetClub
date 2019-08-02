package com.egcoders.technologysolution.netclub.data.pager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.egcoders.technologysolution.netclub.ui.fragments.CategoryPostsFragment;

import java.util.ArrayList;
import java.util.List;

public class CategoriesPager extends FragmentStatePagerAdapter {

    List<String> categoriesList = new ArrayList<>();
    private long baseId = 0;

    public CategoriesPager(FragmentManager fm, List<String> list){
        super(fm);
        this.categoriesList = list;
    }

    @Override
    public Fragment getItem(int i) {

        CategoryPostsFragment fragment = new CategoryPostsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("category", categoriesList.get(i));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return categoriesList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return categoriesList.get(position);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }



    public void notifyChangeInPosition(int n) {
        // shift the ID returned by getItemId outside the range of all previous fragments
        baseId += getCount() + n;
    }
}
