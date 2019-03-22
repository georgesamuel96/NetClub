package com.egcoders.technologysolution.netclub;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {


    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        PostsUserFragment postsUserFragment = new PostsUserFragment();
        SavesUserFragment savesUserFragment = new SavesUserFragment();

        if(position == 0)
        {
            return postsUserFragment;
        }
        else
        {
            return savesUserFragment;
        }

    }

    @Override
    public int getCount() {
        return 2;
    }
}

