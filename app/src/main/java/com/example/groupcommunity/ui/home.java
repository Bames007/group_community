package com.example.groupcommunity.ui;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.groupcommunity.R;
import com.example.groupcommunity.adapter.fragmentAdapter;
import com.google.android.material.tabs.TabLayout;

public class home extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    com.example.groupcommunity.adapter.fragmentAdapter fragmentAdapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.viewPager);

        tabLayout.addTab(tabLayout.newTab().setText("Report"));
        tabLayout.addTab(tabLayout.newTab().setText("View Posts"));

        tabLayout.getTabAt(0).setIcon(R.drawable.volume);

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        fragmentAdapter = new fragmentAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(fragmentAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if (tab.getPosition() == 0) {
                    tabLayout.getTabAt(0).setIcon(R.drawable.volume);
                    tabLayout.getTabAt(1).setIcon(null);
                }
                if (tab.getPosition() == 1) {
                    tabLayout.getTabAt(0).setIcon(null);
                    tabLayout.getTabAt(1).setIcon(R.drawable.community);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }
}