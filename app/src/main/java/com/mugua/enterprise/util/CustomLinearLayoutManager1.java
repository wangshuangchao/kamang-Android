package com.mugua.enterprise.util;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * Created by Lenovo on 2017/12/18.
 */

public class CustomLinearLayoutManager1 extends GridLayoutManager {
    private boolean isScrollEnabled = true;

    public CustomLinearLayoutManager1(Context context, int i) {
        super(context, i);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollVertically();
    }
}
