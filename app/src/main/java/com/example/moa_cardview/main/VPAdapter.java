package com.example.moa_cardview.main;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.moa_cardview.item_page.Food;
import com.example.moa_cardview.item_page.OTT;
import com.example.moa_cardview.item_page.Taxi;
import com.example.moa_cardview.item_page.Stuff;

import java.util.ArrayList;

public class VPAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> items;
    public VPAdapter(FragmentManager fm) {
        super(fm);
        items = new ArrayList<Fragment>();
        items.add(new Stuff());
//        items.add(new Food());
//        items.add(new OTT());
//        items.add(new Taxi());
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
