package com.example.groupcommunity.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.groupcommunity.ui.report;
import com.example.groupcommunity.ui.view_report;

public class fragmentAdapter extends FragmentStatePagerAdapter {

    private int TabCount;

    public fragmentAdapter(FragmentManager fragementManager, int countTabs){
        super(fragementManager);
        this.TabCount = countTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
        case 0:
            return new report();
        case 1:
            return new view_report();
        default:
            return null;
        }
    }

    @Override
    public int getCount() {
        return TabCount;
    }
}
