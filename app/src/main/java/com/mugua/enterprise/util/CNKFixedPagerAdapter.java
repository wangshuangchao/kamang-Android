package com.mugua.enterprise.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mugua.enterprise.R;

import java.util.List;

public class CNKFixedPagerAdapter extends FragmentStatePagerAdapter {
    private String[] titles;
    private LayoutInflater mInflater;

    public void setTitles(String[] titles) {
        this.titles = titles;
    }
    private List<Fragment> fragments;
    public CNKFixedPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }
    @Override
    public int getCount() {
        return this.fragments.size();
    }

    Fragment fragment=null;
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        try {
            fragment=(Fragment)super.instantiateItem(container,position);
        }catch (Exception e){

        }
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        //释放资源,
//        container.removeView((View) object);
    }
//    此方法用来显示tab上的名字
    @Override
    public CharSequence getPageTitle(int position) {

        return titles[position % titles.length];
    }
    public List<Fragment> getFragments() {
        return fragments;
    }
    public void setFragments(List<Fragment> fragments) {
        this.fragments = fragments;
    }

    /**
     * 添加getTabView的方法，来进行自定义Tab的布局View
     * @param position
     * @return
     */
    public View getTabView(int position){
        View view=mInflater.inflate(R.layout.tab_item_layout,null);
        TextView tv= (TextView) view.findViewById(R.id.textView);
        tv.setText(titles[position]);
        ImageView img = (ImageView) view.findViewById(R.id.imageView);
        img.setImageResource(R.mipmap.ic_launcher);
        return view;
    }
}
