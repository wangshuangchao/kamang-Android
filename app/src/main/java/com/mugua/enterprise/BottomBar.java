package com.mugua.enterprise;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/26.
 */
public class BottomBar extends LinearLayout implements View.OnClickListener
{

    private static final int TAG_0 = 0;
    private static final int TAG_1 = 1;
    private static final int TAG_2 = 2;
    private static final int TAG_3 = 3;
    private Context mContext;
    public BottomBar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mContext = context;
        init();
    }

    public BottomBar(Context context)
    {
        super(context);
        mContext = context;
        init();
    }

    private List<View> itemList;
    /**
     * get the buttons from layout
     *
     * 得到布局文件中的按钮
     *
     * */
    private LayoutInflater inflater;
    private View layout;
    private RelativeLayout btnOne,btnTwo,btnThree,four;
    private void init()
    {
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(R.layout.bottom_bar, null);
        layout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0f));
        btnOne = (RelativeLayout)layout.findViewById(R.id.btn_item_one);
        btnTwo = (RelativeLayout)layout.findViewById(R.id.btn_item_two);
        btnThree = (RelativeLayout)layout.findViewById(R.id.btn_item_three);
        four = (RelativeLayout)layout.findViewById(R.id.btn_item_four);
        btnOne.setOnClickListener(this);
        btnTwo.setOnClickListener(this);
        btnThree.setOnClickListener(this);
        four.setOnClickListener(this);
        btnOne.setTag(TAG_0);
        btnTwo.setTag(TAG_1);
        btnThree.setTag(TAG_2);
        four.setTag(TAG_3);
        itemList = new ArrayList<View>();
        itemList.add(btnOne);
        itemList.add(btnTwo);
        itemList.add(btnThree);
        itemList.add(four);
        this.addView(layout);
    }

    private int tag;
    @Override
    public void onClick(View v)
    {
        tag = (Integer)v.getTag();
        switch (tag)
        {
            case TAG_0:
                setNormalState(lastButton);
                setSelectedState(tag);
                break;
            case TAG_1:
                setNormalState(lastButton);
                setSelectedState(tag);
                break;
            case TAG_2:
                setNormalState(lastButton);
                setSelectedState(tag);
                break;
            case TAG_3:
                setNormalState(lastButton);
                setSelectedState(tag);
                break;
        }
    }

    private int lastButton = -1;
    /**
     * set the default bar item of selected
     *
     * 设置默认选中的Item
     *
     * */
    public void setSelectedState(int index)
    {
        if(index != -1 && onItemChangedListener != null)
        {
            if(index > itemList.size())
            {
                throw new RuntimeException("the value of default bar item can not bigger than string array's length");
            }
            itemList.get(index).setSelected(true);
            onItemChangedListener.onItemChanged(index);
            lastButton = index;
        }
    }

    /**
     * set the normal state of the button by given index
     *
     * 恢复未选中状态
     *
     * */
    public void setNormalState(int index)
    {
        if(index != -1)
        {
            if(index > itemList.size())
            {
                throw new RuntimeException("the value of default bar item can not bigger than string array's length");
            }
            itemList.get(index).setSelected(false);
        }
    }

    public interface OnItemChangedListener
    {
        public void onItemChanged(int index);
    }

    private OnItemChangedListener onItemChangedListener;

    public void setOnItemChangedListener(OnItemChangedListener onItemChangedListener)
    {
        this.onItemChangedListener = onItemChangedListener;
    }
}