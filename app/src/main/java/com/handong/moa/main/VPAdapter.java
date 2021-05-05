package com.handong.moa.main;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.handong.moa.item.Food;
import com.handong.moa.item.OTT;
import com.handong.moa.item.Stuff;
import com.handong.moa.item.Taxi;

import java.util.ArrayList;

public class VPAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> items;
    public VPAdapter(FragmentManager fm) {
        super(fm);
        items = new ArrayList<Fragment>();
        items.add(new Stuff());
        items.add(new Food());
        items.add(new OTT());
        items.add(new Taxi());
    }
    @Override
    public Fragment getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getCount() {
        return items.size();
    }
}

