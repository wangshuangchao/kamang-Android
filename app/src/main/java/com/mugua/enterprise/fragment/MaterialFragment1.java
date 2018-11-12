package com.mugua.enterprise.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mugua.enterprise.R;
import com.mugua.enterprise.activity.material.CommodityDetailsActivity;
import com.mugua.enterprise.util.toast.CustomToast;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.utils.AutoUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;
import static com.squareup.picasso.Picasso.Priority.HIGH;

/**
 * Created by Lenovo on 2017/11/7.
 */

public class MaterialFragment1 extends Fragment implements View.OnClickListener{
    public static MaterialFragment1 instance = null;

    @BindView( R.id.yl_list )
    public ListView yl_list ;

    @BindView( R.id.rl_yoliao )
    public RelativeLayout rl_yoliao ;

    @BindView( R.id.yl_grid )
    public GridView yl_grid ;

    private MylistAdapter mylistAdapter;
    private MygridAdapter mygridAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        if (container == null)
            return null;
        View mView = inflater.inflate(R.layout.fragment_material1,container,false);
        instance = this;
        //绑定fragment
        ButterKnife.bind( this , mView ) ;

        mylistAdapter = new MylistAdapter(getActivity());
        yl_list.setAdapter(mylistAdapter);
        mygridAdapter = new MygridAdapter(getActivity());
        yl_grid.setAdapter(mygridAdapter);
        yl_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CustomToast.showToast(getActivity(),"正在开发中...");
            }
        });

        yl_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CustomToast.showToast(getActivity(),"正在开发中...");
            }
        });
        return mView;
    }

    @Override
    public void onStop()
    {
        super.onStop();
        getActivity().onStateNotSaved();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        rl_yoliao.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.rl_yoliao:
                CustomToast.showToast(getActivity(),"正在开发中...");
                break;
        }
    }

    public class MylistAdapter extends BaseAdapter {

        private Context context;
        private int tu[] = {
                R.mipmap.tu110, R.mipmap.tu111
        };
        private String tuname[] = {
                "糖果手机5000万像素64G自拍视频美颜Sugar C9", "卡贝指纹锁家用防盗门密码锁大门智能锁磁卡手机APP远程电子门锁"
        };
        private String tujiage[] = {
                "￥899", "￥1650"
        };
        public MylistAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return tu.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder1 holder = null;
            if (convertView == null) {
                holder = new ViewHolder1();
                convertView = LayoutInflater.from(context).inflate(R.layout.yl_list_item, parent, false);
                holder.view_x = (View) convertView.findViewById(R.id.view_x);
                holder.img_tutx = (ImageView) convertView.findViewById(R.id.img_tutx);
                holder.tv_nmae = (TextView) convertView.findViewById(R.id.tv_nmae);
                holder.tv_jiage = (TextView) convertView.findViewById(R.id.tv_jiage);
                convertView.setTag(holder);
                AutoUtils.autoSize(convertView);
            } else
                holder = (ViewHolder1) convertView.getTag();

            Glide.with(context)
                    .load(tu[position])
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.img_tutx);
            holder.tv_nmae.setText(tuname[position]);
            holder.tv_jiage.setText(tujiage[position]);

            if(position == 1)
                holder.view_x.setVisibility(View.GONE);
            else
                holder.view_x.setVisibility(View.VISIBLE);
            return convertView;
        }
    }

    public class ViewHolder1{
        private View view_x;
        private ImageView img_tutx;
        private TextView tv_nmae;
        private TextView tv_jiage;
    }

    public class MygridAdapter extends BaseAdapter {

        private Context context;
        private int tu[] = {
                R.mipmap.tu112, R.mipmap.tu113,
                R.mipmap.tu114, R.mipmap.tu115,
                R.mipmap.tu116, R.mipmap.tu117
        };
        private String tuname[] = {
                "徽六茶叶绿茶新茶六安瓜片春茶茶叶高山绿茶礼盒", "赣南脐橙新鲜现摘发货榨汁水果信丰脐橙",
                "趁早2018效率手册 厚本 多色 日程本 文具笔记本 日记本手帐", "得力直液式走珠笔黑色子弹头中性笔签字笔学生用水笔水性笔碳素笔",
                "临安小香薯 天目山小红薯5斤新鲜紫薯地瓜 板栗山芋农家蔬菜番薯", "Seko/新功 N68带遥控全自动上水电热水壶玻璃烧水壶煮茶器电水壶"
        };
        private String tujiage[] = {
                "￥150", "￥4",
                "￥35", "￥12",
                "￥500", "￥190"
        };
        public MygridAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return tu.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder2 holder = null;
            if (convertView == null) {
                holder = new ViewHolder2();
                convertView = LayoutInflater.from(context).inflate(R.layout.yl_grid_item, parent, false);
                holder.img_tutx = (ImageView) convertView.findViewById(R.id.img_tutx);
                holder.tv_nmae = (TextView) convertView.findViewById(R.id.tv_nmae);
                holder.tv_jiage = (TextView) convertView.findViewById(R.id.tv_jiage);
                convertView.setTag(holder);
                AutoUtils.autoSize(convertView);
            } else
                holder = (ViewHolder2) convertView.getTag();

            Glide.with(context)
                    .load(tu[position])
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.img_tutx);
            holder.tv_nmae.setText(tuname[position]);
            holder.tv_jiage.setText(tujiage[position]);

            return convertView;
        }
    }

    public class ViewHolder2{
        private ImageView img_tutx;
        private TextView tv_nmae;
        private TextView tv_jiage;
    }
}
