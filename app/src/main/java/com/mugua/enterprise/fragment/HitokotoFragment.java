package com.mugua.enterprise.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mugua.enterprise.R;
import com.mugua.enterprise.activity.ParticularsActivity;
import com.mugua.enterprise.bean.HitokotoBean;
import com.mugua.enterprise.util.Constant;
import com.mugua.enterprise.util.CustomLinearLayoutManager;
import com.mugua.enterprise.util.MyItemClickListener;
import com.mugua.enterprise.util.Okhttp;
import com.mugua.enterprise.util.TuActivity;
import com.mugua.enterprise.util.httppost.IRequestCallBack;
import com.mugua.enterprise.util.toast.CustomToast;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhy.autolayout.utils.AutoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import education.yunxun.com.mylibrary.PullToRefreshLayout;

/**
 * Created by Lenovo on 2017/10/31.
 */

public class HitokotoFragment extends Fragment implements MyItemClickListener ,PullToRefreshLayout.OnRefreshListener{
    public static HitokotoFragment instance = null;

    @BindView( R.id.hitokoto_recyclerview )
    public RecyclerView hitokoto_recyclerview ;

    @BindView( R.id.refresh_view )
    public PullToRefreshLayout PullToRefreshLayout ;

    private MyAdapter myAdapter;
    private View mView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null)
            return null;
        mView = inflater.inflate(R.layout.fragment_hitokoto,container,false);
        instance = this;
        //绑定fragment
        ButterKnife.bind( this , mView ) ;

        PullToRefreshLayout.setOnRefreshListener(this);

        myAdapter = new MyAdapter(getActivity());
        hitokoto_recyclerview.setHasFixedSize(false);
        CustomLinearLayoutManager linearLayoutManager = new CustomLinearLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        linearLayoutManager.setScrollEnabled(false);
        hitokoto_recyclerview.setLayoutManager(linearLayoutManager);
        hitokoto_recyclerview.setAdapter(myAdapter);

        myAdapter.setOnItemClickListener(this);
        myAdapter.setOnItemLongClickListener(this);
        return mView;
    }

    @Override
    public void onStop()
    {
        super.onStop();
        getActivity().onStateNotSaved();
        HitokotoFragment.this.onDestroy();
    }

    @Override
    public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
        new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                stype = false;
                datas.clear();
                shu = 1;
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
                Data();
                // 千万别忘了告诉控件加载完毕了哦！
                pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            }
        }.sendEmptyMessageDelayed(0, 2000);
    }

    private boolean stype;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        new Thread( new MyRunnable()).start();
    }

    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            Data();
        }
    }

    public void shua()
    {
        datas.clear();
        shu = 1;
        Data();
    }

    private int shu = 1;
    public List<HitokotoBean> datas = new ArrayList<>();
    public void Data()
    {
        try {
            Okhttp.doGet(getActivity(), Constant.URL_getList+"?page="+shu, new IRequestCallBack() {
                @Override
                public void onRequestBack(String text) {
                    JSONArray jsonArray = null;
                    try {
                        if(stype == true && !text.equals("[]")) {
                            stype = false;
                            shu++;
                        }else shu = 2;
                        jsonArray = new JSONArray(text);
                        for (int i = 0; i < jsonArray.length();i++)
                        {
                            JSONObject temp = (JSONObject) jsonArray.get(i);
                            Gson gson = new Gson();
                            HitokotoBean hitokotoBean = gson.fromJson(temp.toString(),HitokotoBean.class);
                            datas.add(hitokotoBean);
                        }
                        //在adapter中添加数据
                        myAdapter.setData(datas);
                        myAdapter.notifyDataSetChanged();
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
    public void onItemClick(View view, int postion) {
        Intent intent = new Intent(getActivity(), ParticularsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("id", datas.get(postion).getId());
        bundle.putString("shu", "1");
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int postion) {
    }

    public class MyAdapter extends RecyclerView.Adapter<MyRecycleViewHolder>
    {
        private MyItemClickListener mLongClickListener;
        private MyItemClickListener mItemClickListener;
        private Context context;
        private LayoutInflater mInflater;
        private List<HitokotoBean> mList = new ArrayList<>();

        public MyAdapter(Context context) {
            this.context = context;
            mInflater = LayoutInflater.from(context);
        }

        /**
         * 设置Item点击监听
         * @param listener
         */
        public void setOnItemClickListener(MyItemClickListener listener){
            this.mItemClickListener = listener;
        }

        public void setOnItemLongClickListener(MyItemClickListener listener){
            this.mLongClickListener = listener;
        }

        public void setData(List<HitokotoBean> model) {
            mList.clear();
            mList.addAll(model);
        }

        @Override
        public MyRecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyRecycleViewHolder myRecycleViewHolder = null;
            switch (viewType) {
                case 1:
                    myRecycleViewHolder = new MyRecycleViewHolderOne(mInflater.inflate(R.layout.hitokoto_list_item1, parent,false),mItemClickListener,mLongClickListener);
                    break;
                case 2:
                    myRecycleViewHolder = new MyRecycleViewHolderTwo(mInflater.inflate(R.layout.hitokoto_list_item2, parent,false),mItemClickListener,mLongClickListener);
                    break;
                case 3:
                    myRecycleViewHolder = new MyRecycleViewHolderThree(mInflater.inflate(R.layout.hitokoto_list_item3, parent,false),mItemClickListener,mLongClickListener);
                    break;
            }
            return myRecycleViewHolder;
        }

        @Override
        public void onBindViewHolder(MyRecycleViewHolder holder, int position) {
            holder.bindHolder(mList.get(position));
        }

        @Override
        public int getItemViewType(int position) {
            return mList.get(position).photoType;
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    public  abstract class MyRecycleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

        public ImageView img_tutx;
        public ImageView img_tutx1;
        public ImageView img_tutx2;
        public ImageView img_tutx3;
        public TextView tv_name;
        public TextView newType;
        public TextView tv_renshu;
        private MyItemClickListener mListener;
        private MyItemClickListener mLongClickListener;
        public MyRecycleViewHolder(View itemView,MyItemClickListener listener,MyItemClickListener longClickListener) {
            super(itemView);
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
        public abstract void bindHolder(HitokotoBean data);
    }

    public class MyRecycleViewHolderOne extends MyRecycleViewHolder {

        public MyRecycleViewHolderOne(View itemView, MyItemClickListener listener, MyItemClickListener longClickListener) {
            super(itemView, listener, longClickListener);
            img_tutx = (ImageView)itemView.findViewById(R.id.img_tutx);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            newType = (TextView)itemView.findViewById(R.id.newType);
            tv_renshu = (TextView) itemView.findViewById(R.id.tv_renshu);
        }

        @Override
        public void bindHolder(HitokotoBean data) {
            String img_bg = data.getCoverPhotoOneUrl();
            ImageLoader.getInstance().displayImage(img_bg, img_tutx, TuActivity.tu(R.drawable.zwt));
            String type = data.getNewType();
            newType.setText(type);
            if(type.equals("典型案例"))
            {
                newType.setBackgroundResource(R.drawable.img_normal_shape1);
                newType.setTextColor(Color.parseColor("#0dbeb4"));
            }
            else if(type.equals("风险提示"))
            {
                newType.setBackgroundResource(R.drawable.img_normal_shape4);
                newType.setTextColor(Color.parseColor("#ff5050"));
            }
            else if(type.equals("重点领域"))
            {
                newType.setBackgroundResource(R.drawable.img_normal_shape3);
                newType.setTextColor(Color.parseColor("#f39800"));
            }
            else if(type.equals("信用大数据"))
            {
                newType.setBackgroundResource(R.drawable.img_normal_shape2);
                newType.setTextColor(Color.parseColor("#44a7e6"));
            }
            tv_name.setText(data.getTitleOne());
            tv_renshu.setText(data.getBrowseNumber());
        }
    }

    public class MyRecycleViewHolderTwo extends MyRecycleViewHolder {

        public MyRecycleViewHolderTwo(View itemView, MyItemClickListener listener, MyItemClickListener longClickListener) {
            super(itemView, listener, longClickListener);
            img_tutx1 = (ImageView)itemView.findViewById(R.id.img_tutx1);
            img_tutx2 = (ImageView)itemView.findViewById(R.id.img_tutx2);
            img_tutx3 = (ImageView)itemView.findViewById(R.id.img_tutx3);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            newType = (TextView)itemView.findViewById(R.id.newType);
            tv_renshu = (TextView) itemView.findViewById(R.id.tv_renshu);
        }

        @Override
        public void bindHolder(HitokotoBean data) {
            String img_bg1 = data.getCoverPhotoOneUrl();
            ImageLoader.getInstance().displayImage(img_bg1, img_tutx1, TuActivity.tu(R.drawable.zwt));
            String img_bg2 = data.getCoverPhotoTwoUrl();
            ImageLoader.getInstance().displayImage(img_bg2, img_tutx2, TuActivity.tu(R.drawable.zwt));
            String img_bg3 = data.getCoverPhotoThrUrl();
            ImageLoader.getInstance().displayImage(img_bg3, img_tutx3, TuActivity.tu(R.drawable.zwt));

            String type = data.getNewType();
            newType.setText(type);
            if(type.equals("典型案例"))
            {
                newType.setBackgroundResource(R.drawable.img_normal_shape1);
                newType.setTextColor(Color.parseColor("#0dbeb4"));
            }
            else if(type.equals("风险提示"))
            {
                newType.setBackgroundResource(R.drawable.img_normal_shape4);
                newType.setTextColor(Color.parseColor("#ff5050"));
            }
            else if(type.equals("重点领域"))
            {
                newType.setBackgroundResource(R.drawable.img_normal_shape3);
                newType.setTextColor(Color.parseColor("#f39800"));
            }
            else if(type.equals("信用大数据"))
            {
                newType.setBackgroundResource(R.drawable.img_normal_shape2);
                newType.setTextColor(Color.parseColor("#44a7e6"));
            }
            tv_name.setText(data.getTitleOne());
            tv_renshu.setText(data.getBrowseNumber());
        }
    }

    public class MyRecycleViewHolderThree extends MyRecycleViewHolder {

        public MyRecycleViewHolderThree(View itemView, MyItemClickListener listener, MyItemClickListener longClickListener) {
            super(itemView, listener, longClickListener);
            img_tutx = (ImageView)itemView.findViewById(R.id.img_tutx);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            newType = (TextView)itemView.findViewById(R.id.newType);
            tv_renshu = (TextView) itemView.findViewById(R.id.tv_renshu);
        }

        @Override
        public void bindHolder(HitokotoBean data) {
            String img_bg = data.getCoverPhotoOneUrl();
            ImageLoader.getInstance().displayImage(img_bg, img_tutx, TuActivity.tu(R.drawable.zwt));
            String type = data.getNewType();
            newType.setText(type);
            if(type.equals("典型案例"))
            {
                newType.setBackgroundResource(R.drawable.img_normal_shape1);
                newType.setTextColor(Color.parseColor("#0dbeb4"));
            }
            else if(type.equals("风险提示"))
            {
                newType.setBackgroundResource(R.drawable.img_normal_shape4);
                newType.setTextColor(Color.parseColor("#ff5050"));
            }
            else if(type.equals("重点领域"))
            {
                newType.setBackgroundResource(R.drawable.img_normal_shape3);
                newType.setTextColor(Color.parseColor("#f39800"));
            }
            else if(type.equals("信用大数据"))
            {
                newType.setBackgroundResource(R.drawable.img_normal_shape2);
                newType.setTextColor(Color.parseColor("#44a7e6"));
            }
            tv_name.setText(data.getTitleOne());
            tv_renshu.setText(data.getBrowseNumber());
        }
    }
}
