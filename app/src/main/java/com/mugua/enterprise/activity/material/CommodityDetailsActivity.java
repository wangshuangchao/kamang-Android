package com.mugua.enterprise.activity.material;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.log.LocalUserInfo;
import com.mugua.enterprise.BeasActivity;
import com.mugua.enterprise.Mall.Order_Details;
import com.mugua.enterprise.MyApplication;
import com.mugua.enterprise.R;
import com.mugua.enterprise.activity.me.InventoryActivity;
import com.mugua.enterprise.bean.YlShangPinBean;
import com.mugua.enterprise.login.LoginActivity;
import com.mugua.enterprise.util.Constant;
import com.mugua.enterprise.util.CustomLinearLayoutManager;
import com.mugua.enterprise.util.GlideRoundTransform;
import com.mugua.enterprise.util.Okhttp;
import com.mugua.enterprise.util.httppost.IRequestCallBack;
import com.mugua.enterprise.util.toast.CustomToast;
import com.mugua.jiguang.chat.activity.ChatActivity;
import com.mugua.jiguang.chat.utils.HandleResponseCode;
import com.taro.headerrecycle.helper.RecycleViewScrollHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;

/**
 * Created by Lenovo on 2017/12/6.
 */

public class CommodityDetailsActivity extends BeasActivity implements View.OnClickListener,RecycleViewScrollHelper.OnScrollPositionChangedListener{
    public static CommodityDetailsActivity instance = null;
    @BindView( R.id.my_scrollview )
    public ScrollView my_scrollview ;

    @BindView( R.id.rl_back )
    public RelativeLayout rl_back ;

    @BindView( R.id.img_jian )
    public ImageView img_jian ;

    @BindView( R.id.img_jia )
    public ImageView img_jia ;

    @BindView( R.id.btn_gm )
    public TextView btn_gm ;

    @BindView( R.id.TvId )
    public TextView TvId ;

    @BindView( R.id.tv_name )
    public TextView tv_name ;

    @BindView( R.id.tv_jinbi )
    public TextView tv_jinbi ;

    @BindView( R.id.tv_jianjie )
    public TextView tv_jianjie ;

    @BindView( R.id.tv_kucun )
    public TextView tv_kucun ;

    @BindView( R.id.e_shu )
    public EditText e_shu ;

    @BindView( R.id.btn_zx )
    public LinearLayout btn_zx ;

    @BindView( R.id.my_viewpage )
    public ViewPager my_viewpage ;
    private TestAdapter mAdapter;

    @BindView( R.id.my_recyclerview )
    public RecyclerView my_recyclerview ;
    private MyAdapter myAdapter;

    private List<YlShangPinBean> groups;
    private int shu1,shu;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commoditydetails);
        instance = this;
        //绑定fragment
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        shu = bundle.getInt("shu");
        shu1 = 1;
        groups = (List<YlShangPinBean>) bundle.getSerializable("list");

        mAdapter = new TestAdapter(this);
        my_viewpage.setAdapter(mAdapter);

        myAdapter = new MyAdapter(this);
        my_recyclerview.setHasFixedSize(false);
        CustomLinearLayoutManager linearLayoutManager = new CustomLinearLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        linearLayoutManager.setScrollEnabled(false);
        my_recyclerview.setLayoutManager(linearLayoutManager);
        my_recyclerview.setAdapter(myAdapter);

        my_viewpage.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                TvId.setText((position+1)+"/"+list.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        my_scrollview.smoothScrollTo(0, 0);
        if(groups != null)
            data();

        xgshu();

        rl_back.setOnClickListener(this);
        btn_gm.setOnClickListener(this);
        btn_zx.setOnClickListener(this);
        img_jia.setOnClickListener(this);
        img_jian.setOnClickListener(this);
        e_shu.setOnClickListener(this);
    }

    private int data = 0;
    private void xgshu()
    {
        String www = "?userId="+ LocalUserInfo.getInstance(this).getUserInfo("id")+"&goodsId="+groups.get(shu).getId();
        try {
            Okhttp.doGet(this, Constant.URL_getLimit+www, new IRequestCallBack() {
                @Override
                public void onRequestBack(String text) {
                    JSONObject jsonobject = null;
                    try {
                        jsonobject = new JSONObject(text);
                        if (jsonobject.getString("code").equals("1000")) {
                            data = Integer.parseInt(jsonobject.getString("data"));
                        }
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Glide.clear(my_recyclerview);
        Glide.clear(my_viewpage);
    }

    private List<String> list = new ArrayList<String>();
    private void data()
    {
        if(!groups.get(shu).getPhoto1().equals("") && groups.get(shu).getPhoto1() != null
                && !groups.get(shu).getPhoto1().equals("null"))
            list.add(groups.get(shu).getPhoto1());
        if(!groups.get(shu).getPhoto2().equals("") && groups.get(shu).getPhoto2() != null
                && !groups.get(shu).getPhoto2().equals("null"))
            list.add(groups.get(shu).getPhoto2());
        if(!groups.get(shu).getPhoto3().equals("") && groups.get(shu).getPhoto3() != null
                && !groups.get(shu).getPhoto3().equals("null"))
            list.add(groups.get(shu).getPhoto3());
        if(!groups.get(shu).getPhoto4().equals("") && groups.get(shu).getPhoto4() != null
                && !groups.get(shu).getPhoto4().equals("null"))
            list.add(groups.get(shu).getPhoto4());
        if(!groups.get(shu).getPhoto5().equals("") && groups.get(shu).getPhoto5() != null
                && !groups.get(shu).getPhoto5().equals("null"))
            list.add(groups.get(shu).getPhoto5());
        if(!groups.get(shu).getPhoto6().equals("") && groups.get(shu).getPhoto6() != null
                && !groups.get(shu).getPhoto6().equals("null"))
            list.add(groups.get(shu).getPhoto6());
        if(!groups.get(shu).getPhoto7().equals("") && groups.get(shu).getPhoto7() != null
                && !groups.get(shu).getPhoto7().equals("null"))
            list.add(groups.get(shu).getPhoto7());

        TvId.setText(1+"/"+list.size());

        mAdapter.setdata(list);
        mAdapter.notifyDataSetChanged();

        myAdapter.setData(list);
        myAdapter.notifyDataSetChanged();

        tv_name.setText(groups.get(shu).getName());
        tv_jinbi.setText("￥"+groups.get(shu).getPrice());
        tv_jianjie.setText(groups.get(shu).getDetail());
        tv_kucun.setText("库存"+groups.get(shu).getNum()+"件");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_gm:
                if(e_shu.getText().toString().equals(""))
                {
                    CustomToast.showToast(getApplicationContext(),"购买数量不能为0");
                    return;
                }
                shu1 = Integer.parseInt(e_shu.getText().toString());
                if(shu1 == 0)
                {
                    CustomToast.showToast(getApplicationContext(),"购买数量不能为0");
                    return;
                }
                if(shu1 > Integer.parseInt(groups.get(shu).getNum()))
                {
                    CustomToast.showToast(getApplicationContext(),"超出库存");
                    return;
                }

                if(groups.get(shu).getIsLimitid().equals("1")){
                    if(shu1 > data)
                    {
                        CustomToast.showToast(getApplicationContext(),"超出限购数量",1000);
                        return;
                    }
                }
                if(Constant.user != null)
                {
                    if(!Constant.user.getIsCentification().equals("1"))
                    {
                        CustomToast.showToast(getApplicationContext(),"暂未认证");
                        return;
                    }
                    Intent intent = new Intent(CommodityDetailsActivity.this, Order_Details.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("list", (Serializable) groups);
                    bundle.putInt("data", data);
                    bundle.putInt("shu", shu);
                    bundle.putInt("shul", Integer.parseInt(e_shu.getText().toString()));
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else startActivity(new Intent(CommodityDetailsActivity.this, LoginActivity.class));
                break;
            case R.id.btn_zx:
                huoqu();
                break;
            case R.id.e_shu:
                e_shu.setFocusable(true);
                e_shu.setFocusableInTouchMode(true);
                e_shu.requestFocus();
                e_shu.setSelection(e_shu.length());
                break;
            case R.id.img_jia:
                String ejia = e_shu.getText().toString();
                if(!ejia.equals("")) {
                    shu1 = Integer.parseInt(ejia);
                    if(shu1 < Integer.parseInt(groups.get(shu).getNum()))
                    {
                        if(groups.get(shu).getIsLimitid().equals("0"))
                        {
                            shu1++;
                            e_shu.setText(shu1+"");
                        }else {
                            if(shu1 < data)
                            {
                                shu1++;
                                e_shu.setText(shu1+"");
                            }else CustomToast.showToast(getApplicationContext(),"超出限购数量",1000);
                        }
                    }else CustomToast.showToast(getApplicationContext(),"超出库存",1000);
                }
                break;
            case R.id.img_jian:
                String ejian = e_shu.getText().toString();
                if(!ejian.equals("")) {
                    shu1 = Integer.parseInt(ejian);
                    if (shu1 > 1)
                        shu1--;
                    e_shu.setText(shu1 + "");
                }
                break;
        }
    }

    private void huoqu()
    {
        String name = groups.get(shu).getPhone();
        if(name.equals(""))
            return;
        JMessageClient.getUserInfo(name, new GetUserInfoCallback() {
            @Override
            public void gotResult(int i, String s, UserInfo userInfo) {
                if (i == 0) {
                    Constant.jgusername = userInfo.getUserName();
                    Constant.jgnickname = userInfo.getNickname();
                    Constant.jgappKey = userInfo.getAppKey();
                    jinru();
                } else {
                    HandleResponseCode.onHandle(getApplicationContext(), i, false);
                }
            }
        });
    }

    private void jinru()
    {
        //点击会话条目
        Intent intent = new Intent();
        intent.putExtra(MyApplication.CONV_TITLE, Constant.jgnickname);
        intent.putExtra(MyApplication.TARGET_ID, Constant.jgusername);
        intent.putExtra(MyApplication.TARGET_APP_KEY, Constant.jgappKey);
        intent.setClass(CommodityDetailsActivity.this, ChatActivity.class);
        startActivity(intent);
    }

    public class TestAdapter extends PagerAdapter {

        private List<String> mPaths;
        private Context mContext;
        public TestAdapter(Context cx) {
            mContext = cx;
        }

        private void setdata(List<String> paths)
        {
            mPaths = paths;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mPaths != null ? mPaths.size() : 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            // TODO Auto-generated method stub
            return view == (View) obj;
        }

        @Override
        public Object instantiateItem (ViewGroup container, int position) {
            ImageView iv = new ImageView(mContext);
            try {
                //设置当前位置的图片资源
                Glide.with(mContext)
                        .load(mPaths.get(position))
                        .placeholder(R.drawable.zwt)
                        .error(R.drawable.zwt)
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transform(new GlideRoundTransform(mContext, 0))
                        .into(iv);
            } catch (OutOfMemoryError e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            ((ViewPager)container).addView(iv, 0);
            return iv;
        }

        @Override
        public void destroyItem (ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }
    }

    @Override
    public void onScrollToTop() {
        Log.i("scroll", "滑动到顶部");
    }

    @Override
    public void onScrollToBottom() {
        Log.i("scroll", "滑动到底部");
    }

    @Override
    public void onScrollToUnknown(boolean b, boolean b1) {
        Log.i("scroll", "滑动未达到底部或者顶部");
        if(my_recyclerview.getScrollState() != 0){
            //recycleView正在滑动
            Glide.with(this).pauseRequests();
            return;
        }else {
            Glide.with(this).resumeRequests();
            return;
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<MyRecycleViewHolder>
    {
        private Context context;
        private LayoutInflater mInflater;
        private List<String> datas = new ArrayList<String>();

        public MyAdapter(Context context) {
            this.context = context;
            mInflater = LayoutInflater.from(context);
        }

        public void setData(List<String> datas) {
            this.datas = datas;
        }

        @Override
        public MyRecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyRecycleViewHolder myRecycleViewHolder = null;
            myRecycleViewHolder = new MyRecycleViewHolder(mInflater.inflate(R.layout.one, parent,false));
            return myRecycleViewHolder;
        }

        @Override
        public void onBindViewHolder(MyRecycleViewHolder holder, int position) {
            if(!datas.get(position).equals("") && !datas.get(position).equals("0")) {
                holder.img_tu.setVisibility(View.VISIBLE);
                final MyRecycleViewHolder finalHolder = holder;
                Glide.with(context)
                        .load(datas.get(position))
                        .placeholder(R.drawable.zwt)
                        .error(R.drawable.zwt)
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                if (finalHolder.img_tu == null) {
                                    return false;
                                }
                                if (finalHolder.img_tu.getScaleType() != ImageView.ScaleType.FIT_XY) {
                                    finalHolder.img_tu.setScaleType(ImageView.ScaleType.FIT_XY);
                                }
                                ViewGroup.LayoutParams params = finalHolder.img_tu.getLayoutParams();
                                int vw = finalHolder.img_tu.getWidth() - finalHolder.img_tu.getPaddingLeft() - finalHolder.img_tu.getPaddingRight();
                                float scale = (float) vw / (float) resource.getIntrinsicWidth();
                                int vh = Math.round(resource.getIntrinsicHeight() * scale);
                                params.height = vh + finalHolder.img_tu.getPaddingTop() + finalHolder.img_tu.getPaddingBottom();
                                finalHolder.img_tu.setLayoutParams(params);
                                return false;
                            }
                        })
                        .transform(new GlideRoundTransform(context, 0))
                        .into(finalHolder.img_tu);
            }else holder.img_tu.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }
    }

    public class MyRecycleViewHolder extends RecyclerView.ViewHolder{
        private ImageView img_tu;
        public MyRecycleViewHolder(View itemView) {
            super(itemView);
            img_tu = (ImageView) itemView.findViewById(R.id.ivImage);
        }
    }
}
