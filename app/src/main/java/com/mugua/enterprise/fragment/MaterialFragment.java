package com.mugua.enterprise.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.mugua.enterprise.R;
import com.mugua.enterprise.activity.material.CommodityDetailsActivity;
import com.mugua.enterprise.activity.me.InventoryActivity;
import com.mugua.enterprise.bean.YlShangPinBean;
import com.mugua.enterprise.login.LoginActivity;
import com.mugua.enterprise.util.Constant;
import com.mugua.enterprise.util.CustomLinearLayoutManager;
import com.mugua.enterprise.util.CustomLinearLayoutManager1;
import com.mugua.enterprise.util.GlideRoundTransform;
import com.mugua.enterprise.util.MyItemClickListener;
import com.mugua.enterprise.util.Okhttp;
import com.mugua.enterprise.util.httppost.IRequestCallBack;
import com.mugua.enterprise.util.toast.CustomToast;
import com.zhy.autolayout.utils.AutoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import education.yunxun.com.mylibrary.PullToRefreshLayout;

/**
 * Created by Lenovo on 2017/11/7.
 */

public class MaterialFragment extends Fragment implements View.OnClickListener,MyItemClickListener,PullToRefreshLayout.OnRefreshListener {
    public static MaterialFragment instance = null;

    @BindView( R.id.refresh_view )
    public PullToRefreshLayout PullToRefreshLayout ;

    @BindView( R.id.yl_list )
    public ListView yl_list ;

    @BindView( R.id.rl_yoliao )
    public RelativeLayout rl_yoliao ;

    private MylistAdapter mylistAdapter;

    @BindView( R.id.my_recyclerview )
    public RecyclerView my_recyclerview ;

    private MyAdapter mAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        if (container == null)
            return null;
        View mView = inflater.inflate(R.layout.fragment_material,container,false);
        instance = this;
        //绑定fragment
        ButterKnife.bind( this , mView ) ;

        PullToRefreshLayout.setOnRefreshListener(this);

        mylistAdapter = new MylistAdapter(getActivity());
        yl_list.setAdapter(mylistAdapter);
        yl_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), CommodityDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("list", (Serializable) datas2);
                bundle.putInt("shu", i);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        mAdapter = new MyAdapter(getActivity());
        my_recyclerview.setHasFixedSize(true);
        CustomLinearLayoutManager1 linearLayoutManager1 = new CustomLinearLayoutManager1(getActivity(),2);
        linearLayoutManager1.setScrollEnabled(false);
        my_recyclerview.setLayoutManager(linearLayoutManager1);
        my_recyclerview.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);

        return mView;
    }

    @Override
    public void onStop() {
        super.onStop();
        Glide.clear(my_recyclerview);
        Glide.clear(yl_list);
        getActivity().onStateNotSaved();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        rl_yoliao.setOnClickListener(this);
        Data();
    }

    public List<YlShangPinBean> datas1 = new ArrayList<>();
    public List<YlShangPinBean> datas2 = new ArrayList<>();
    private void Data()
    {
        String www = "?startRow="+shu;
        try {
            Okhttp.doGet(getActivity(), Constant.URL_ylindex+www, new IRequestCallBack() {
                @Override
                public void onRequestBack(String text) {
                    JSONObject jsonobject = null;
                    try {
                        jsonobject = new JSONObject(text);
                        if(jsonobject.getString("code").equals("1000"))
                        {
                            JSONObject jsonObject = jsonobject.getJSONObject("data");
                            JSONArray jsonArray1 = jsonObject.getJSONArray("hot");
                            if(stype == true) {
                                if(jsonArray1.length() != 0)
                                {
                                    stype = false;
                                    shu++;
                                }
                            }else shu = 2;
                            for (int i = 0; i < jsonArray1.length();i++)
                            {
                                JSONObject temp = (JSONObject) jsonArray1.get(i);
                                Gson gson = new Gson();
                                YlShangPinBean ylShangPinBean = gson.fromJson(temp.toString(),YlShangPinBean.class);
                                datas1.add(ylShangPinBean);
                            }
                            //在adapter中添加数据
                            mAdapter.setdate(datas1);
                            mAdapter.notifyDataSetChanged();

                            JSONArray jsonArray2 = jsonObject.getJSONArray("commend");
                            for (int i = 0; i < jsonArray2.length();i++)
                            {
                                JSONObject temp = (JSONObject) jsonArray2.get(i);
                                Gson gson = new Gson();
                                YlShangPinBean ylShangPinBean = gson.fromJson(temp.toString(),YlShangPinBean.class);
                                datas2.add(ylShangPinBean);
                            }
                            //在adapter中添加数据
                            mylistAdapter.setdate(datas2);
                            mylistAdapter.notifyDataSetChanged();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.rl_yoliao:
                if(Constant.user != null){
                    Intent intent = new Intent(getActivity(), InventoryActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("open", false);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else startActivity(new Intent(getActivity(), LoginActivity.class));
//                CustomToast.showToast(getActivity(),"工程师正在开发中",1000);
                break;
        }
    }

    private int shu = 1;
    private boolean stype;
    @Override
    public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
        new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                stype = false;
                shu = 1;
                datas1.clear();
                datas2.clear();
                Data();
                // 千万别忘了告诉控件刷新完毕了哦！
                pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
            }
        }.sendEmptyMessageDelayed(0, 2000);
    }

    @Override
    public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
        // 加载操作
        new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                stype = true;
                datas2.clear();
                Data();
                // 千万别忘了告诉控件加载完毕了哦！
                pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            }
        }.sendEmptyMessageDelayed(0, 2000);
    }

    public class MylistAdapter extends BaseAdapter {

        private Context context;
        private List<YlShangPinBean> mList = new ArrayList<>();
        public MylistAdapter(Context context) {
            this.context = context;
        }

        public void setdate(List<YlShangPinBean> model) {
            mList.clear();
            mList.addAll(model);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mList.size();
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
                    .load(mList.get(position).getPhoto1())
                    .placeholder(R.drawable.zwt)
                    .error(R.drawable.zwt)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transform(new GlideRoundTransform(context, 10))
                    .into(holder.img_tutx);
            holder.tv_nmae.setText(mList.get(position).getName());
            holder.tv_jiage.setText(mList.get(position).getPrice());

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


    @Override
    public void onItemClick(View view, int postion) {
        Intent intent = new Intent(getActivity(), CommodityDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("list", (Serializable) datas1);
        bundle.putInt("shu", postion);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int postion) {
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>
    {
        private Context context;
        private List<YlShangPinBean> mList = new ArrayList<>();
        private MyItemClickListener mLongClickListener;
        private MyItemClickListener mItemClickListener;
        private MyAdapter(Context context)
        {
            this.context = context;
        }

        public void setdate(List<YlShangPinBean> model) {
            mList.clear();
            mList.addAll(model);
        }

        public void setOnItemClickListener(MyItemClickListener listener){
            this.mItemClickListener = listener;
        }

        public void setOnItemLongClickListener(MyItemClickListener listener){
            this.mLongClickListener = listener;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.yl_grid_item, parent, false)
                        ,mItemClickListener,mLongClickListener);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position)
        {
            Glide.with(context)
                    .load(mList.get(position).getPhoto1())
                    .placeholder(R.drawable.zwt)
                    .error(R.drawable.zwt)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transform(new GlideRoundTransform(context, 10))
                    .into(holder.img_tutx);
            holder.tv_nmae.setText(mList.get(position).getName());
            holder.tv_jiage.setText("￥"+mList.get(position).getPrice());
        }

        @Override
        public int getItemCount()
        {
            return mList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener
        {
            private ImageView img_tutx;
            private TextView tv_nmae;
            private TextView tv_jiage;
            private MyItemClickListener mListener;
            private MyItemClickListener mLongClickListener;
            public MyViewHolder(View view,MyItemClickListener listener,MyItemClickListener longClickListener)
            {
                super(view);
                img_tutx = (ImageView) view.findViewById(R.id.img_tutx);
                tv_nmae = (TextView) view.findViewById(R.id.tv_nmae);
                tv_jiage = (TextView) view.findViewById(R.id.tv_jiage);
                this.mListener = listener;
                this.mLongClickListener = longClickListener;
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            /**
             * 点击监听
             */
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onItemClick(v,getPosition());
                }
            }

            /**
             * 长按监听
             */
            @Override
            public boolean onLongClick(View v) {
                if(mLongClickListener != null){
                    mLongClickListener.onItemLongClick(v, getPosition());
                }
                return true;
            }
        }
    }
}
