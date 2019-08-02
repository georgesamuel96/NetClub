package com.egcoders.technologysolution.netclub.ui.fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.egcoders.technologysolution.netclub.R;
import com.egcoders.technologysolution.netclub.data.pager.ViewPagerAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout = (TabLayout)view.findViewById(R.id.tabLayout);

        tabLayout.setupWithViewPager(viewPager);
        setupTabLayout();
        viewPager.setCurrentItem(0);

        return view;
    }

    private void setupTabLayout() {

        TextView customTab1 = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        TextView customTab2 = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        customTab1.setText("Posts");
        customTab1.setBackgroundResource(R.color.transparent);
        tabLayout.getTabAt(0).setCustomView(customTab1);
        customTab2.setText("Saves");
        customTab2.setBackgroundResource(R.color.transparent);
        tabLayout.getTabAt(1).setCustomView(customTab2);


    }

}
