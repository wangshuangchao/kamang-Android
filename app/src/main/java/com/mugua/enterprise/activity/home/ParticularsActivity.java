package com.mugua.enterprise.activity.home;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.mugua.enterprise.BeasActivity;
import com.mugua.enterprise.MyApplication;
import com.mugua.enterprise.R;
import com.mugua.enterprise.activity.ShowcasingActivity;
import com.mugua.enterprise.bean.ParticularsBean;
import com.mugua.enterprise.util.Canonical;
import com.mugua.enterprise.util.Constant;
import com.mugua.enterprise.util.CustomLinearLayoutManager;
import com.mugua.enterprise.util.GlideRoundTransform;
import com.mugua.enterprise.util.ImageAsyncTask;
import com.mugua.enterprise.util.Okhttp;
import com.mugua.enterprise.util.httppost.IRequestCallBack;
import com.mugua.enterprise.util.toast.CustomToast;
import com.mugua.jiguang.chat.activity.ChatActivity;
import com.mugua.jiguang.chat.utils.HandleResponseCode;
import com.squareup.picasso.Picasso;
import com.taro.headerrecycle.helper.RecycleViewScrollHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;

import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;
import static com.squareup.picasso.Picasso.Priority.HIGH;

/**
 * Created by Lenovo on 2017/12/18.
 */

public class ParticularsActivity extends BeasActivity implements View.OnClickListener ,RecycleViewScrollHelper.OnScrollPositionChangedListener{
    public static ParticularsActivity instance = null;

    @BindView( R.id.rl_back )
    public RelativeLayout rl_back ;

    @BindView( R.id.my_scrollview )
    public ScrollView my_scrollview ;

    @BindView( R.id.TvId )
    public TextView TvId ;

    @BindView( R.id.tv_name )
    public TextView tv_name ;

    @BindView( R.id.tv_jinbi )
    public TextView tv_jinbi ;

    @BindView( R.id.tv_jianjie )
    public TextView tv_jianjie ;

    @BindView( R.id.btn_zx )
    public LinearLayout btn_zx ;

    @BindView( R.id.my_viewpage )
    public ViewPager my_viewpage ;
    private TestAdapter mAdapter;

    @BindView( R.id.my_recyclerview )
    public RecyclerView my_recyclerview ;
    private MyAdapter myAdapter;
    private String id;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_particulars);
        instance = this;
        //绑定fragment
        ButterKnife.bind(this);

        mAdapter = new TestAdapter(this);
        my_viewpage.setAdapter(mAdapter);

        myAdapter = new MyAdapter(ParticularsActivity.this,list);
        my_recyclerview.setHasFixedSize(false);
        CustomLinearLayoutManager linearLayoutManager = new CustomLinearLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        linearLayoutManager.setScrollEnabled(false);
        my_recyclerview.setLayoutManager(linearLayoutManager);
        my_recyclerview.setAdapter(myAdapter);

        btn_zx.setOnClickListener(this);
        rl_back.setOnClickListener(this);
        my_scrollview.smoothScrollTo(0, 0);

        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");

        new Thread( new MyRunnable()).start();

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
    }

    private void huoqu()
    {
        String name = ShowcasingActivity.instance.username;
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
                    HandleResponseCode.onHandle(ParticularsActivity.this, i, false);
                }
            }
        });
    }

    private void jinru()
    {
        if(Constant.jgnickname.equals(""))
            Constant.jgnickname = Constant.jgusername;
        //点击会话条目
        Intent intent = new Intent();
        intent.putExtra(MyApplication.CONV_TITLE, Constant.jgnickname);
        intent.putExtra(MyApplication.TARGET_ID, Constant.jgusername);
        intent.putExtra(MyApplication.TARGET_APP_KEY, Constant.jgappKey);
        intent.setClass(ParticularsActivity.this, ChatActivity.class);
        startActivity(intent);
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

    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            data();
        }
    }

    List<String> list = new ArrayList<String>();
    private void data()
    {
        String www = "?id="+id;
        try {
            Okhttp.doGet(this, Constant.URL_getGoods+www, new IRequestCallBack() {
                @Override
                public void onRequestBack(String text) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(text);
                        if(jsonObject.getString("code").equals("1000"))
                        {
                            Gson gson = new Gson();
                            ParticularsBean showcasing1Bean = gson.fromJson(jsonObject.getString("data"),ParticularsBean.class);
                            CustomToast.showToast(getApplicationContext(),jsonObject.getString("msg"),1000);

                            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                            if(!jsonObject1.getString("photo1").equals("") && jsonObject1.getString("photo1") != null
                                    && !jsonObject1.getString("photo1").equals("null"))
                                list.add(jsonObject1.getString("photo1"));
                            if(!jsonObject1.getString("photo2").equals("") && jsonObject1.getString("photo2") != null
                                    && !jsonObject1.getString("photo2").equals("null"))
                                list.add(jsonObject1.getString("photo2"));
                            if(!jsonObject1.getString("photo3").equals("") && jsonObject1.getString("photo3") != null
                                    && !jsonObject1.getString("photo3").equals("null"))
                                list.add(jsonObject1.getString("photo3"));
                            if(!jsonObject1.getString("photo4").equals("") && jsonObject1.getString("photo4") != null
                                    && !jsonObject1.getString("photo4").equals("null"))
                                list.add(jsonObject1.getString("photo4"));
                            if(!jsonObject1.getString("photo5").equals("") && jsonObject1.getString("photo5") != null
                                    && !jsonObject1.getString("photo5").equals("null"))
                                list.add(jsonObject1.getString("photo5"));
                            if(!jsonObject1.getString("photo6").equals("") && jsonObject1.getString("photo6") != null
                                    && !jsonObject1.getString("photo6").equals("null"))
                                list.add(jsonObject1.getString("photo6"));
                            if(!jsonObject1.getString("photo7").equals("") && jsonObject1.getString("photo7") != null
                                    && !jsonObject1.getString("photo7").equals("null"))
                                list.add(jsonObject1.getString("photo7"));

                            TvId.setText(1+"/"+list.size());

                            mAdapter.setdata(list);
                            mAdapter.notifyDataSetChanged();

                            myAdapter.setData(list);
                            myAdapter.notifyDataSetChanged();

                            tv_name.setText(showcasing1Bean.getName());
                            tv_jinbi.setText("￥"+showcasing1Bean.getPrice());
                            tv_jianjie.setText(showcasing1Bean.getDetail());
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
    public void onDestroy()
    {
        super.onDestroy();
        Glide.clear(my_recyclerview);
        Glide.clear(my_viewpage);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_zx:
                huoqu();
                break;
        }
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

    public class MyAdapter extends RecyclerView.Adapter<MyRecycleViewHolder>
    {
        private Context context;
        private LayoutInflater mInflater;
        private List<String> datas;

        public MyAdapter(Context context,List<String> datas) {
            this.context = context;
            this.datas = datas;
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
                                params.width = vw;
                                params.height = vh;
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
