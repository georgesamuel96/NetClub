package com.egcoders.technologysolution.netclub;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Pair;

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
        //Fragment fragment;
        Bundle bundle = new Bundle();
        bundle.putString("category", categoriesList.get(i));
        fragment.setArguments(bundle);
        return fragment;
        /*for(String name : categoriesList){
            if(name.equals("Tech / Programming")){
                fragment = new TechProgrammingFragment();
                fragment.setArguments(bundle);
                System.out.println("ASD1");
                return fragment;
            }
            else if(name.equals("Social skills / Motivation")){
                fragment = new SocialskillsMotivationFragment();
                fragment.setArguments(bundle);
                System.out.println("ASD2");
                return fragment;
            }
            else if(name.equals("Music")){
                fragment = new MusicFragment();
                fragment.setArguments(bundle);
                return fragment;
            }
        }
        return null;*/
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
