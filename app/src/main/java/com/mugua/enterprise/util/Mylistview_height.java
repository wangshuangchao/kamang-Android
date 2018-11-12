package com.mugua.enterprise.util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by Administrator on 2017/5/5.
 */

public class Mylistview_height {
    public static void setHeight(ListAdapter adapter, ListView my_listview,int listViewHeight){
        int adaptCount = adapter.getCount();
        for(int i=0;i<adaptCount;i++){
            View temp = adapter.getView(i,null,my_listview);
            temp.measure(0,0);
            listViewHeight += temp.getMeasuredHeight();
        }
        ViewGroup.LayoutParams layoutParams = my_listview.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.FILL_PARENT;
        layoutParams.height = listViewHeight;
        my_listview.setLayoutParams(layoutParams);
    }
}
