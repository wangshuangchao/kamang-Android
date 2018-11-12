package com.mugua.jiguang.chat.view;

import android.content.Context;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.mugua.enterprise.R;

/**
 * Created by ${chenyn} on 2017/2/20.
 */

public class MainView extends RelativeLayout {

    private ScrollControlViewPager mViewContainer;

    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void initModule() {
        mViewContainer = (ScrollControlViewPager) findViewById(R.id.viewpager);
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        mViewContainer.addOnPageChangeListener(onPageChangeListener);
    }

    public void setViewPagerAdapter(FragmentPagerAdapter adapter) {
        mViewContainer.setAdapter(adapter);
    }

    public void setCurrentItem(int index, boolean scroll) {
        mViewContainer.setCurrentItem(index, scroll);
    }
}
