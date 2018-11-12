package com.mugua.enterprise.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.log.LocalUserInfo;
import com.google.gson.Gson;
import com.mugua.enterprise.MainActivity;
import com.mugua.enterprise.R;
import com.mugua.enterprise.activity.FundsActivity;
import com.mugua.enterprise.activity.HuoDongActivity;
import com.mugua.enterprise.activity.ParticularsActivity;
import com.mugua.enterprise.activity.ShowcasingActivity;
import com.mugua.enterprise.activity.home.BrokenListActivity;
import com.mugua.enterprise.activity.home.SearchActivity;
import com.mugua.enterprise.activity.me.CardActivity;
import com.mugua.enterprise.activity.me.EnterpriseCertificationActivity;
import com.mugua.enterprise.activity.me.InventoryActivity;
import com.mugua.enterprise.bean.HomeBean;
import com.mugua.enterprise.bean.LoginBean;
import com.mugua.enterprise.login.LoginActivity;
import com.mugua.enterprise.model.DataBean;
import com.mugua.enterprise.util.CardDialog;
import com.mugua.enterprise.util.Constant;
import com.mugua.enterprise.util.GlideRoundTransform;
import com.mugua.enterprise.util.ImageAsyncTask;
import com.mugua.enterprise.util.LimitScrollerView;
import com.mugua.enterprise.util.Okhttp;
import com.mugua.enterprise.util.httppost.IRequestCallBack;
import com.mugua.enterprise.util.toast.CustomToast;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.utils.AutoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;
import static com.squareup.picasso.Picasso.Priority.HIGH;

/**
 * Created by Lenovo on 2017/10/30.
 */

public class HomeFragment extends Fragment implements View.OnClickListener{
    public static HomeFragment instance = null;
    public LimitScrollerView limitScroll;
    private MyLimitScrollAdapter myLimitScrollAdapteradapter;

    public RelativeLayout image_daohangzc ;
    public ImageView image_daohang ;

    private ListView hemo_list;
    private HomeListAdapter myadpter;
    private RelativeLayout hideView;
    private View header;
    private View headerb;
    private TextView tv_card;
    public ImageView fab;
    private boolean open;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null)
            return null;
        View mView = inflater.inflate(R.layout.fragment_home,container,false);
        instance = this;

        fab = (ImageView) mView.findViewById(R.id.fab);
        yincang(1000);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(open == true)
                {
                    open = false;
                    fab.setBackgroundResource(R.mipmap.hdan);
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("title", "免费送名片");
                    intent.putExtra("url", homeBean.activity.getUrl());
                    intent.setClass(getActivity(), HuoDongActivity.class);
                    startActivity(intent);
                }
                yincang(4000);
            }
        });

        hideView = (RelativeLayout)mView.findViewById(R.id.secondLayout);
        image_daohangzc = (RelativeLayout)mView.findViewById(R.id.image_daohangzc);
        image_daohang = (ImageView)mView.findViewById(R.id.image_daohang);
        if(LocalUserInfo.getInstance(getActivity()).getUserInfo("open4").equals("false") ||
                LocalUserInfo.getInstance(getActivity()).getUserInfo("open4").equals("")) {
            image_daohangzc.setVisibility(View.VISIBLE);
        }else {
            image_daohangzc.setVisibility(View.GONE);
        }

        header = LayoutInflater.from(getActivity()).inflate(R.layout.buy_layout_head, null);
        headerb = LayoutInflater.from(getActivity()).inflate(R.layout.buy_layout_headb, null);

        LinearLayout ll_broken = (LinearLayout) headerb.findViewById(R.id.ll_broken);//失信名单
        ll_broken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),BrokenListActivity.class));
            }
        });

        LinearLayout rl_funds = (LinearLayout) headerb.findViewById(R.id.rl_funds);//补充资金
        rl_funds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Constant.user != null) {
                    startActivity(new Intent(getActivity(), FundsActivity.class));
                }
                else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            }
        });

        LinearLayout ll_kucun = (LinearLayout) headerb.findViewById(R.id.ll_kucun);//我有库存
        ll_kucun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Constant.user != null){
                    Intent intent = new Intent(getActivity(), InventoryActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("open", false);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        tv_card = (TextView) headerb.findViewById(R.id.tv_card);//创建名片
        LinearLayout ll_card = (LinearLayout) headerb.findViewById(R.id.ll_card);//创建名片
        ll_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Constant.user != null)
                {
                    if(Constant.user.getIsCentification().equals("0") || Constant.user.getIsCentification().equals("2"))
                        startActivity(new Intent(getActivity(), EnterpriseCertificationActivity.class));
                    else if(Constant.user.getIsCentification().equals("1"))
                    {
                        if(Constant.user.getExts().equals("0"))//未创建名片
                        {
                            Intent intent = new Intent(getActivity(), CardActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("id", Constant.user.getId());
                            bundle.putString("kind", "0");
                            bundle.putString("title", "创建名片");
                            bundle.putString("btn", "创建名片");
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                        else {
                            Intent intent = new Intent(getActivity(), CardActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("id", Constant.user.getId());
                            bundle.putString("kind", "1");
                            bundle.putString("title", "修改名片");
                            bundle.putString("btn", "确认修改");
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }
                    else if(Constant.user.getIsCentification().equals("3"))
                    {
                        finish1();
                    }
                }
                else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            }
        });

        LinearLayout ll_12 = (LinearLayout) mView.findViewById(R.id.ll_1);//搜索
        ll_12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),SearchActivity.class));
            }
        });

        LinearLayout ll_11 = (LinearLayout) header.findViewById(R.id.ll_1);//搜索
        ll_11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),SearchActivity.class));
            }
        });
        View wei = LayoutInflater.from(getActivity()).inflate(R.layout.layout_wei, null);
        hemo_list = (ListView) mView.findViewById(R.id.hemo_list);
        hemo_list.addHeaderView(header);
        hemo_list.addHeaderView(headerb);
//        hemo_list.addFooterView(wei);

        hemo_list.setItemsCanFocus(false);
        hemo_list.setFocusable(false);
        final View view_t_1 = (View)mView.findViewById(R.id.view_t_1);
        hemo_list.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
                if (hideView.getHeight() > header.getBottom()+view_t_1.getHeight()) {
                    hideView.setVisibility(View.VISIBLE);
                } else {
                    hideView.setVisibility(View.INVISIBLE);
                }
            }
        });
        myadpter = new HomeListAdapter(getActivity());
        hemo_list.setAdapter(myadpter);
        hemo_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(position-2 >= 0)
                {
                    Intent intent = new Intent(getActivity(), ParticularsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("id", homeBean.sayOnes[position-2].getId());
                    bundle.putString("shu", "2");
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

        limitScroll = (LimitScrollerView)headerb.findViewById(R.id.limitScroll);
        //API:1、设置数据适配器
        myLimitScrollAdapteradapter = new MyLimitScrollAdapter();
        //API：4、设置条目点击事件
        limitScroll.setOnItemClickListener(new LimitScrollerView.OnItemClickListener() {
            @Override
            public void onItemClick(Object obj) {
                if(obj instanceof DataBean){
                    //强制转换
                    DataBean bean = (DataBean)obj;
                    if(bean.getText().equals("企业入驻"))
                    {
                        Intent intent = new Intent(getActivity(), ShowcasingActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("id", cardId[bean.getShu()]);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }else {
                        MainActivity.instance.showDetails(2);
                        MainActivity.instance.bottomBar.setNormalState(0);
                        MainActivity.instance.bottomBar.setSelectedState(2);
                    }
                }
            }
        });
        return mView;
    }

    private void yincang(int time) {
        new Handler(getActivity().getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                open = true;
                fab.setBackgroundResource(R.mipmap.hdanb);
            }
        }, time);
    }

    private void finish1()
    {
        //弹出提示框
        new CardDialog(getActivity(), R.style.dialog, R.layout.wait_card,0,"",false,new CardDialog.OnCloseListener() {
            @Override
            public void onClick(final Dialog dialog, View view) {
                view.findViewById(R.id.img_gb).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                view.findViewById(R.id.tv_bg).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        }).show();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        new Thread( new MyRunnable2()).start();

        image_daohang.setOnClickListener(this);
        image_daohangzc.setOnClickListener(this);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        getActivity().onStateNotSaved();
        HomeFragment.this.onDestroy();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if(LocalUserInfo.getInstance(getActivity()).getUserInfo("open").equals("true"))
            new Thread( new MyRunnable3()).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.image_daohang:
                LocalUserInfo.getInstance(getActivity()).setUserInfo("open4", "true");
                image_daohangzc.setVisibility(View.GONE);
                hdpd();
                break;
        }
    }

    private void huadong()
    {
        //弹出提示框
        new CardDialog(getActivity(), R.style.dialog, R.layout.home_huadong,4,"",false,new CardDialog.OnCloseListener() {
            @Override
            public void onClick(final Dialog dialog, View view) {
                view.findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        fab.setVisibility(View.VISIBLE);
                    }
                });
                view.findViewById(R.id.tv_cy).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.putExtra("title", "免费送名片");
                        intent.putExtra("url", homeBean.activity.getUrl());
                        intent.setClass(getActivity(), HuoDongActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                        fab.setVisibility(View.VISIBLE);
                    }
                });
            }
        }).show();
    }

    private class MyRunnable1 implements Runnable {
        private HomeBean homeBean;
        private MyRunnable1(HomeBean homeBean)
        {
            this.homeBean = homeBean;
        }
        @Override
        public void run() {
            initData(homeBean);
        }
    }

    private class MyRunnable2 implements Runnable {
        @Override
        public void run() {
            Data();
        }
    }

    private class MyRunnable3 implements Runnable {
        @Override
        public void run() {
            loginshuju();
        }
    }

    public void loginshuju()
    {
        String www = "?id="+LocalUserInfo.getInstance(getActivity()).getUserInfo("id");
        try {
            Okhttp.doGet(getActivity(), Constant.URL_getUserInfo+www, new IRequestCallBack() {
                @Override
                public void onRequestBack(String text) {
                    if (!Constant.home_text.equals(text)) {
                        Constant.home_text = text;

                        JSONObject jsonobject = null;
                        try {
                            jsonobject = new JSONObject(text);
                            if (jsonobject.getString("code").equals("1000"))
                            {
                                Gson gson = new Gson();
                                LoginBean loginBean = gson.fromJson(jsonobject.getString("data"),LoginBean.class);
                                Constant.user = loginBean;
                                if(Constant.user.getExts().equals("0"))
                                    tv_card.setText("创建名片");
                                else tv_card.setText("修改名片");
                            }
                            else CustomToast.showToast(getActivity(),jsonobject.getString("msg"),1000);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        if(Constant.user.getExts().equals("0"))
                            tv_card.setText("创建名片");
                        else tv_card.setText("修改名片");
                        return;
                    };
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void hdpd()
    {
        if(Constant.open5 == false)
        {
            if(Constant.user != null)
            {
                Constant.open5 = true;
                huadong();
            } else {
                Constant.open5 = true;
                huadong();
            }
        }else {
            fab.setVisibility(View.VISIBLE);
        }
    }

    public HomeBean homeBean;
    private String cardId[];
    private void Data()
    {
        try {
            Okhttp.doGet(getActivity(), Constant.URL_home, new IRequestCallBack() {
                @Override
                public void onRequestBack(String text) {
                    JSONObject jsonobject = null;
                    try {
                        jsonobject = new JSONObject(text);
                        if (jsonobject.getString("code").equals("1000"))
                        {
                            Gson gson = new Gson();
                            homeBean = gson.fromJson(jsonobject.getString("data"),HomeBean.class);

                            if(homeBean.activity.getExists().equals("1") && LocalUserInfo.getInstance(getActivity()).getUserInfo("open4").equals("true"))
                                hdpd();

                            myadpter.data(homeBean,true);

                            limitScroll.setDataAdapter(myLimitScrollAdapteradapter);
                            new Thread( new MyRunnable1(homeBean)).start();
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

    public void Data1()
    {
        try {
            Okhttp.doGet(getActivity(), Constant.URL_home, new IRequestCallBack() {
                @Override
                public void onRequestBack(String text) {
                    JSONObject jsonobject = null;
                    try {
                        jsonobject = new JSONObject(text);
                        if (jsonobject.getString("code").equals("1000"))
                        {
                            Gson gson = new Gson();
                            homeBean = gson.fromJson(jsonobject.getString("data"),HomeBean.class);
                            myadpter.data(homeBean,true);
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

    public class HomeListAdapter extends BaseAdapter {

        private Context context;
        private HomeBean homeBean;
        private boolean open;
        public HomeListAdapter(Context context) {
            this.context = context;
        }

        public void data(HomeBean homeBean,boolean open)
        {
            this.homeBean = homeBean;
            this.open = open;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            if(!open) return 0;
            else return homeBean.sayOnes.length;
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

            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.home_list_item, parent, false);
                holder.tv_homebg = (TextView)convertView.findViewById(R.id.tv_homebg);
                holder.img_tutx = (ImageView) convertView.findViewById(R.id.img_tutx);
                holder.tv_renshu = (TextView) convertView.findViewById(R.id.tv_renshu);
                holder.tv_tiale = (TextView) convertView.findViewById(R.id.tv_tiale);
                convertView.setTag(holder);
                AutoUtils.autoSize(convertView);
            } else
                holder = (ViewHolder) convertView.getTag();

            String tiale = homeBean.sayOnes[position].getTitleOne();
            holder.tv_tiale.setText(tiale);

            String renshu = homeBean.sayOnes[position].getBrowseNumber();
            holder.tv_renshu.setText(renshu);

            String img_bg = homeBean.sayOnes[position].getCoverPhotoOneUrl();
            Picasso.with(context)
                    .load(img_bg)
                    .placeholder(R.drawable.zwt)//默认显示图片
                    .error(R.drawable.zwt)//加载时出现错误显示的图片
                    .priority(HIGH)//底部的图片后加载,Picasso支持设置优先级,分为HIGH, MEDIUM, 和 LOW,所有的加载默认优先级为MEDIUM;
                    .memoryPolicy(NO_CACHE, NO_STORE)//查看大图放弃缓存，加速内存的回收
                    .tag("RecyclerView") //参数为 Object
                    .resize(220,180)
                    .onlyScaleDown() // 如果图片规格大于6000*2000,将只会被resize
                    .centerCrop()//设置图片圆角
                    .noFade()//Picasso的默认图片加载方式有一个淡入的效果,如果调用了noFade(),加载的图片将直接显示在ImageView上
                    .into(holder.img_tutx);
//            ImageLoader.getInstance().displayImage(img_bg, holder.img_tutx, TuActivity.tu(R.drawable.zwt));

            String type = homeBean.sayOnes[position].getNewType();
            holder.tv_homebg.setText(type);
            if(type.equals("典型案例"))
            {
                holder.tv_homebg.setBackgroundResource(R.drawable.img_normal_shape1);
                holder.tv_homebg.setTextColor(Color.parseColor("#0dbeb4"));
            }
            else if(type.equals("风险提示"))
            {
                holder.tv_homebg.setBackgroundResource(R.drawable.img_normal_shape4);
                holder.tv_homebg.setTextColor(Color.parseColor("#ff5050"));
            }
            else if(type.equals("重点领域"))
            {
                holder.tv_homebg.setBackgroundResource(R.drawable.img_normal_shape3);
                holder.tv_homebg.setTextColor(Color.parseColor("#f39800"));
            }
            else if(type.equals("信用大数据"))
            {
                holder.tv_homebg.setBackgroundResource(R.drawable.img_normal_shape2);
                holder.tv_homebg.setTextColor(Color.parseColor("#44a7e6"));
            }

            return convertView;
        }
    }

    public class ViewHolder{
        private TextView tv_homebg;
        private ImageView img_tutx;
        private TextView tv_renshu;
        private TextView tv_tiale;
    }

    List<DataBean> datas = new ArrayList<>();
    private void initData(final HomeBean homeBean){
        datas.clear();
        //TODO 模拟获取服务器数据操作，此处需要修改
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int www = homeBean.dynamics.length;
                if(www != 0)
                {
                    cardId = new String [www];
                    for (int i = 0;i < www;i++)
                    {
                        cardId[i] = homeBean.dynamics[i].getCardId();

                        if(homeBean.dynamics[i].getDynamicTitle().equals("企业入驻"))
                            datas.add(new DataBean(i,R.mipmap.home_xx, homeBean.dynamics[i].getDynamicTitle(), homeBean.dynamics[i].getDynamicContent()));
                        else datas.add(new DataBean(i,R.mipmap.home_xx1, homeBean.dynamics[i].getDynamicTitle(), homeBean.dynamics[i].getDynamicContent()));
                    }
                    myLimitScrollAdapteradapter.setDatas(datas);
                }
            }
        }).start();
    }

    //TODO 修改适配器绑定数据
    class MyLimitScrollAdapter implements LimitScrollerView.LimitScrollAdapter{

        private List<DataBean> datas;
        public void setDatas(List<DataBean> datas){
            this.datas = datas;
            //API:2、开始滚动
            limitScroll.startScroll();
        }
        @Override
        public int getCount() {
            return datas==null?0:datas.size();
        }

        @Override
        public View getView(int position) {

            View convertView = LayoutInflater.from(getActivity()).inflate(R.layout.limit_scroller_item, null,false);
            ImageView img_1 = (ImageView)convertView.findViewById(R.id.img_1);
            TextView tv_content = (TextView)convertView.findViewById(R.id.tv_content);
            TextView tv_name = (TextView)convertView.findViewById(R.id.tv_name);
            //绑定数据
            DataBean data = datas.get(position);
            convertView.setTag(data);
            img_1.setBackgroundResource(data.getIcon());
            tv_name.setText(data.getText());
            tv_content.setText(data.getContent());
            return convertView;
        }
    }
//    @Override
//    public void onStop() {
//        super.onStop();
//        //API:3、停止滚动
//        limitScroll.cancel();
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        limitScroll.startScroll();    //API:2、开始滚动
//    }
}