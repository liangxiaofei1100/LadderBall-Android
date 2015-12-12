package com.zhaoyan.ladderball.ui.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.ui.adapter.MainPagerAdaper;
import com.zhaoyan.ladderball.ui.fragments.PracticeFragment;
import com.zhaoyan.ladderball.ui.fragments.TaskFragment;
import com.zhaoyan.ladderball.ui.fragments.UserCenterFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.main_tab_layout)
    TabLayout mTabLayout;
    @Bind(R.id.main_viewpager)
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        String[] titleArray = getResources().getStringArray(R.array.tab_titles);

        List<Fragment> fragments = new ArrayList<>();
        TaskFragment taskFragment = TaskFragment.newInstance();
        PracticeFragment practiceFragment = PracticeFragment.newInstance();
        UserCenterFragment userCenterFragment = UserCenterFragment.newInstance();
        fragments.add(taskFragment);
        fragments.add(practiceFragment);
        fragments.add(userCenterFragment);

        List<String> titles = new ArrayList<>();
        titles.add(titleArray[0]);
        titles.add(titleArray[1]);
        titles.add(titleArray[2]);
        MainPagerAdaper adaper = new MainPagerAdaper(getSupportFragmentManager(), titles, fragments);

        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(adaper);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(adaper);

    }

    public int getSelectTabPosition() {
        if (mTabLayout != null) {
            return mTabLayout.getSelectedTabPosition();
        }
        return 0;
    }

}
