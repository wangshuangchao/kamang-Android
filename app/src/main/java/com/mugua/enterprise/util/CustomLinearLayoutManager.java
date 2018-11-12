package com.mugua.enterprise.util;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * Created by Lenovo on 2017/12/18.
 */

public class CustomLinearLayoutManager extends StaggeredGridLayoutManager {
    private boolean isScrollEnabled = true;

    public CustomLinearLayoutManager(int i, int vertical) {
        super(i, vertical);
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
