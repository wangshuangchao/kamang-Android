package com.mugua.jiguang.chat.controller;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

import com.mugua.enterprise.activity.me.XiaoXiActivity;

import com.mugua.jiguang.chat.activity.fragment.ConversationListFragment;
import com.mugua.jiguang.chat.adapter.ViewPagerAdapter;
import com.mugua.jiguang.chat.view.MainView;

/**
 * Created by ${chenyn} on 2017/2/20.
 */

public class MainController implements ViewPager.OnPageChangeListener {
    private MainView mMainView;
    private XiaoXiActivity mContext;
    private ConversationListFragment mConvListFragment;


    public MainController(MainView mMainView, XiaoXiActivity context) {
        this.mMainView = mMainView;
        this.mContext = context;
        setViewPager();
    }

    private void setViewPager() {
        final List<Fragment> fragments = new ArrayList<>();
        // init Fragment
        mConvListFragment = new ConversationListFragment();

        fragments.add(mConvListFragment);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(mContext.getSupportFragmentManger(),
                fragments);
        mMainView.setViewPagerAdapter(viewPagerAdapter);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void sortConvList() {
        mConvListFragment.sortConvList();
    }

}