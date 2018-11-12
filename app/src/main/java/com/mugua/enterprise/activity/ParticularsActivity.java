package com.mugua.enterprise.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.mugua.enterprise.BeasActivity;
import com.mugua.enterprise.R;
import com.mugua.enterprise.bean.HitokotoBean;
import com.mugua.enterprise.fragment.HitokotoFragment;
import com.mugua.enterprise.fragment.HomeFragment;
import com.mugua.enterprise.util.CardDialog;
import com.mugua.enterprise.util.Constant;
import com.mugua.enterprise.util.CustomLinearLayoutManager;
import com.mugua.enterprise.util.GlideRoundTransform;
import com.mugua.enterprise.util.Okhttp;
import com.mugua.enterprise.util.httppost.IRequestCallBack;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.utils.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lenovo on 2017/12/19.
 */

public class ParticularsActivity extends BeasActivity implements View.OnClickListener {
    public static ParticularsActivity instance = null;
    @BindView( R.id.rl_back )
    public RelativeLayout rl_back ;

    @BindView( R.id.tv_title )
    public TextView tv_title ;

    @BindView( R.id.tv_renshu )
    public TextView tv_renshu ;

    @BindView( R.id.tv_time )
    public TextView tv_time ;

    @BindView( R.id.my_scrollview )
    public ScrollView my_scrollview ;

    @BindView( R.id.rl_fx )
    public RelativeLayout rl_fx ;

    @BindView( R.id.my_recyclerview )
    public RecyclerView my_recyclerview ;
    private MyAdapter myAdapter;

    private String id,shu;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_particular);
        instance = this;
        //绑定fragment
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");
        shu = bundle.getString("shu");

        myAdapter = new MyAdapter(this);
        my_recyclerview.setHasFixedSize(false);
        CustomLinearLayoutManager linearLayoutManager = new CustomLinearLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        linearLayoutManager.setScrollEnabled(false);
        my_recyclerview.setLayoutManager(linearLayoutManager);
        my_recyclerview.setAdapter(myAdapter);

        new Thread( new MyRunnable()).start();
        rl_back.setOnClickListener(this);
        rl_fx.setOnClickListener(this);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Glide.clear(my_recyclerview);
    }

    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            Data();
        }
    }

    private List<String> datas1 = new ArrayList<>();
    private List<String> datas2 = new ArrayList<>();
    private void Data()
    {
        try {
            Okhttp.doGet(this, Constant.URL_getDetail+"?id="+id, new IRequestCallBack() {
                @Override
                public void onRequestBack(String text) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(text);
                        Gson gson = new Gson();
                        HitokotoBean hitokotoBean = gson.fromJson(jsonObject.toString(),HitokotoBean.class);
                        tv_title.setText(hitokotoBean.getTitleOne());
                        tv_renshu.setText(hitokotoBean.getBrowseNumber());
                        tv_time.setText(hitokotoBean.getExtOne());

                        datas1.add(hitokotoBean.getContPhotoOneUrl());
                        datas1.add(hitokotoBean.getContPhotoTwoUrl());
                        datas1.add(hitokotoBean.getContPhotoThrUrl());
                        datas1.add(hitokotoBean.getContPhotoForUrl());

                        datas2.add(hitokotoBean.getContTextOne());
                        datas2.add(hitokotoBean.getContTextTwo());
                        datas2.add(hitokotoBean.getContTextThr());
                        datas2.add(hitokotoBean.getContTextFor());
                        //在adapter中添加数据
                        myAdapter.setData(datas1,datas2);
                        myAdapter.notifyDataSetChanged();
                        my_scrollview.smoothScrollTo(0, 0);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(shu.equals("1")) {
                HitokotoFragment.instance.shua();
            }else if(shu.equals("2")){
                HomeFragment.instance.Data1();
            }
            finish();
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.rl_back:
                if(shu.equals("1")) {
                    HitokotoFragment.instance.shua();
                }else if(shu.equals("2")){
                    HomeFragment.instance.Data1();
                }
                    finish();
                break;
            case R.id.rl_fx:
                UMWeb web = new UMWeb(Constant.URL_newsDetails+id);//连接地址
                web.setTitle(tv_title.getText().toString());//标题
                web.setDescription(datas2.get(0));//描述
                if (TextUtils.isEmpty(datas1.get(0))) {
                    web.setThumb(new UMImage(this, R.mipmap.guanyu_tu));  //本地缩略图
                } else {
                    web.setThumb(new UMImage(this, datas1.get(0)));  //网络缩略图
                }
                new ShareAction(this)
                        .setDisplayList(SHARE_MEDIA.SINA,SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE,SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE)
                        .withMedia(web)
                        .setCallback(umShareListener)
                        .open();
                break;
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<MyRecycleViewHolder>
    {
        private Context context;
        private LayoutInflater mInflater;
        private List<String> datas1 = new ArrayList<>();
        private List<String> datas2 = new ArrayList<>();

        public MyAdapter(Context context) {
            this.context = context;
            mInflater = LayoutInflater.from(context);
        }

        public void setData(List<String> datas1,List<String> datas2) {
            this.datas1 = datas1;
            this.datas2 = datas2;
        }

        @Override
        public MyRecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyRecycleViewHolder myRecycleViewHolder = null;
            myRecycleViewHolder = new MyRecycleViewHolder(mInflater.inflate(R.layout.hitokoto_list_item4, parent,false));
            return myRecycleViewHolder;
        }

        @Override
        public void onBindViewHolder(MyRecycleViewHolder holder, int position) {
            if(!datas1.get(position).equals("") && !datas1.get(position).equals("0")) {
                holder.img_tu.setVisibility(View.VISIBLE);
                final MyRecycleViewHolder finalHolder = holder;
                Glide.with(context)
                        .load(datas1.get(position))
                        .placeholder(R.drawable.zwt)
                        .error(R.drawable.zwt)
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
                        .centerCrop()
                        .crossFade()
                        .transform(new GlideRoundTransform(context, 10))
                        .into(finalHolder.img_tu);
            }else holder.img_tu.setVisibility(View.GONE);

            if(!datas2.get(position).equals("") && !datas2.get(position).equals("0"))
            {
                holder.tv_name.setVisibility(View.VISIBLE);
                holder.tv_name.setText(datas2.get(position));
            }else holder.tv_name.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {
            return datas1.size();
        }
    }

    public class MyRecycleViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_name;
        private ImageView img_tu;
        public MyRecycleViewHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            img_tu = (ImageView) itemView.findViewById(R.id.img_tu);
        }
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat","platform"+platform);
            if(platform.name().equals("WEIXIN_FAVORITE")){
                Toast.makeText(ParticularsActivity.this," 收藏成功",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(ParticularsActivity.this, " 分享成功", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(ParticularsActivity.this," 分享失败", Toast.LENGTH_SHORT).show();
            if(t!=null){
                Log.d("throw","throw:"+t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(ParticularsActivity.this," 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(ParticularsActivity.this).onActivityResult(requestCode, resultCode, data);
    }
}
