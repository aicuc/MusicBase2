package com.musicbase.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.List;

public class MyViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> list;
    public MyViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public MyViewPagerAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.list = list;
    }

    @Override
    public Fragment getItem(int i) {
        return list.get(i);
    }

    @Override
    public int getCount() {
        return list == null?0:list.size();
    }

    //每次切换后调用
    @Override
    public void finishUpdate(ViewGroup container) {
        super.finishUpdate(container);
    }
}
