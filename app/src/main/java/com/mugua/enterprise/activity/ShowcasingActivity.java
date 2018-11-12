package com.mugua.enterprise.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.google.gson.Gson;
import com.mugua.enterprise.GSYBaseActivityDetail;
import com.mugua.enterprise.MyApplication;
import com.mugua.enterprise.R;
import com.mugua.enterprise.activity.home.ParticularsActivity;
import com.mugua.enterprise.activity.me.TipoffActivity;
import com.mugua.enterprise.bean.ShowcasingBean;
import com.mugua.enterprise.util.Canonical;
import com.mugua.enterprise.util.CardDialog;
import com.mugua.enterprise.util.Constant;
import com.mugua.enterprise.util.DialogActivity;
import com.mugua.enterprise.util.GlideRoundTransform;
import com.mugua.enterprise.util.MyItemClickListener;
import com.mugua.enterprise.util.Okhttp;
import com.mugua.enterprise.util.TuActivity;
import com.mugua.enterprise.util.httppost.IRequestCallBack;
import com.mugua.enterprise.util.toast.CustomToast;
import com.mugua.jiguang.chat.activity.ChatActivity;
import com.mugua.jiguang.chat.utils.HandleResponseCode;
import com.mugua.videoplayer.utils.JumpUtils;
import com.mugua.videoplayer.video.SampleControlVideo;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;
import com.squareup.picasso.Picasso;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.utils.Log;

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
import static com.squareup.picasso.Picasso.Priority.LOW;

/**
 * Created by Lenovo on 2017/11/22.
 */

public class ShowcasingActivity extends GSYBaseActivityDetail implements View.OnClickListener,MyItemClickListener {
    public static ShowcasingActivity instance = null;
    @BindView( R.id.rl_back )
    public RelativeLayout rl_back ;

    @BindView( R.id.btn_sc )
    public TextView btn_sc ;

    @BindView( R.id.tv_shtydm )
    public TextView tv_shtydm ;

    @BindView( R.id.tv_frdb )
    public TextView tv_frdb ;

    @BindView( R.id.tv_zczb )
    public TextView tv_zczb ;

    @BindView( R.id.tv_cltime )
    public TextView tv_cltime ;

    @BindView( R.id.tv_zcdz )
    public TextView tv_zcdz ;

    @BindView( R.id.tv_frlxfs )
    public TextView tv_frlxfs ;

    @BindView( R.id.tv_gsname )
    public TextView tv_gsname ;

    @BindView( R.id.tv_conmon )
    public TextView tv_conmon ;

    @BindView(R.id.detail_player)
    SampleControlVideo detailPlayer;

    @BindView( R.id.my_scrollview )
    public ScrollView my_scrollview ;

    @BindView( R.id.ll_1 )
    public RelativeLayout ll_1 ;

    @BindView( R.id.rl_jubao )
    public RelativeLayout rl_jubao ;

    @BindView( R.id.rl_zwsp )
    public RelativeLayout rl_zwsp ;

    @BindView( R.id.img_rz )
    public ImageView img_rz ;

    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private String url = "";
    private String url1 = "";
    public String id = "";
    private DialogActivity dialogActivity;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showcasing);
        instance = this;
        //绑定fragment
        ButterKnife.bind(this);

        dialogActivity = new DialogActivity(this);
        dialogActivity.showProgressDialog("加载中，请稍后！");
        mRecyclerView = (RecyclerView)findViewById(R.id.showcasing_recyclerview);
        mAdapter = new MyAdapter(this);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);

        rl_back.setOnClickListener(this);
        btn_sc.setOnClickListener(this);
        ll_1.setOnClickListener(this);
        rl_jubao.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");

        new Thread( new MyRunnable()).start();

        my_scrollview.smoothScrollTo(0, 0);
    }

    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            data();
        }
    }

    List<String> datas=new ArrayList<>();
    private String id1 = "";
    private String productUrlOne = "";
    private String productUrlTwo = "";
    public String username = "";
    private void data()
    {
        String www = "?id="+id;
        try {
            Okhttp.doGet(this, Constant.URL_getCardInfo+www, new IRequestCallBack() {
                @Override
                public void onRequestBack(String text) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(text);
                        if(jsonObject.getString("code").equals("1000"))
                        {
                            Gson gson = new Gson();
                            ShowcasingBean showcasing1Bean = gson.fromJson(jsonObject.getString("data"),ShowcasingBean.class);
                            CustomToast.showToast(getApplicationContext(),jsonObject.getString("msg"),1000);

                            url = showcasing1Bean.getCompanyVidio();
                            url1 = showcasing1Bean.getMicroblog();
                            if(url.equals("") || url.equals("null")) {
                                rl_zwsp.setVisibility(View.VISIBLE);
                                detailPlayer.setVisibility(View.GONE);
                            }
                            else {
                                detailPlayer.setVisibility(View.VISIBLE);
                                rl_zwsp.setVisibility(View.GONE);
                            }

                            initVideoBuilderMode();
                            detailPlayer.setLockClickListener(new LockClickListener() {
                                @Override
                                public void onClick(View view, boolean lock) {
                                    if (orientationUtils != null) {
                                        //配合下方的onConfigurationChanged
                                        orientationUtils.setEnable(!lock);
                                    }
                                }
                            });

                            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                            if(!jsonObject1.getString("goodsPhoto1").equals("") && jsonObject1.getString("goodsPhoto1") != null
                                    && !jsonObject1.getString("goodsPhoto1").equals("null")) {
                                datas.add(jsonObject1.getString("goodsPhoto1"));
                            }
                            if(!jsonObject1.getString("goodsPhoto2").equals("") && jsonObject1.getString("goodsPhoto2") != null
                                    && !jsonObject1.getString("goodsPhoto2").equals("null")) {
                                datas.add(jsonObject1.getString("goodsPhoto2"));
                            }

                            if(jsonObject1.getString("isCentification").equals("1")) {
                                rl_jubao.setVisibility(View.GONE);
                                img_rz.setVisibility(View.VISIBLE);
                                img_rz.setBackgroundResource(R.mipmap.yrz);
                            }
                            else
                            {
                                rl_jubao.setVisibility(View.VISIBLE);
                                img_rz.setVisibility(View.GONE);
                                img_rz.setBackgroundResource(R.mipmap.wrz);
                            }

                            if(datas.size() == 0)
                                mRecyclerView.setVisibility(View.GONE);
                            else mRecyclerView.setVisibility(View.VISIBLE);
                            mAdapter.setdata(datas);
                            mAdapter.notifyDataSetChanged();

                            id1 = showcasing1Bean.getId();
                            username = showcasing1Bean.getUserPhone();
                            productUrlOne = showcasing1Bean.getProductUrlOne();
                            productUrlTwo = showcasing1Bean.getProductUrlTwo();
                            tv_shtydm.setText(showcasing1Bean.getOrganizationCode());
                            tv_frdb.setText(showcasing1Bean.getLegalPersonName());
                            tv_zczb.setText(showcasing1Bean.getRegisteredAssets());
                            tv_cltime.setText(showcasing1Bean.getCreateTimes());
                            tv_zcdz.setText(showcasing1Bean.getCompanyAddr());
                            tv_frlxfs.setText(showcasing1Bean.getLegalPersonPhone());
                            tv_gsname.setText(showcasing1Bean.getCompany());
                            tv_conmon.setText(showcasing1Bean.getIntroduceCompany());
                        }
                        dialogActivity.dissmissProgressDialog();
                    } catch (JSONException e) {
                        dialogActivity.dissmissProgressDialog();
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            dialogActivity.dissmissProgressDialog();
            e.printStackTrace();
        }
    }

    @Override
    public GSYBaseVideoPlayer getGSYVideoPlayer() {
        return detailPlayer;
    }

    @Override
    public GSYVideoOptionBuilder getGSYVideoOptionBuilder() {
        //内置封面可参考SampleCoverVideo
        ImageView imageView = new ImageView(this);
        loadCover(imageView);
        return new GSYVideoOptionBuilder()
                .setThumbImageView(imageView)
                .setUrl(url)
                .setCacheWithPlay(true)
                .setVideoTitle(" ")
                .setIsTouchWiget(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setShowFullAnimation(false)
                .setNeedLockFull(true)
                .setSeekRatio(1);
    }

    private void loadCover(ImageView imageView) {
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        ImageLoader.getInstance().displayImage(url1, imageView, TuActivity.tu1(R.drawable.zwt));
    }

    @Override
    public void clickForFullScreen() {

    }

    @Override
    protected void onStop() {
        super.onStop();
        Glide.clear(mRecyclerView);
    }

    private void huoqu()
    {
        String name = username;
        if(name.equals("")) {
            dialogActivity.dissmissProgressDialog();
            return;
        }
        JMessageClient.getUserInfo(name, new GetUserInfoCallback() {
            @Override
            public void gotResult(int i, String s, UserInfo userInfo) {
                if (i == 0) {
                    Constant.jgusername = userInfo.getUserName();
                    Constant.jgnickname = userInfo.getNickname();
                    Constant.jgappKey = userInfo.getAppKey();
                    jinru();
                } else {
                    dialogActivity.dissmissProgressDialog();
                    HandleResponseCode.onHandle(ShowcasingActivity.this, i, false);
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
        intent.setClass(ShowcasingActivity.this, ChatActivity.class);
        startActivity(intent);
        dialogActivity.dissmissProgressDialog();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.rl_jubao:
                startActivity(new Intent(ShowcasingActivity.this,TipoffActivity.class));
                break;
            case R.id.ll_1:
                dialogActivity.showProgressDialog("进入中，请稍后！");
                huoqu();
                break;
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_sc:
                //弹出提示框
                new CardDialog(this, R.style.dialog, R.layout.showcasing_card,2,id1,false,new CardDialog.OnCloseListener() {
                    @Override
                    public void onClick(final Dialog dialog, View view) {
                        view.findViewById(R.id.img_preservation).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                boolean isSdCardExist = Environment.getExternalStorageState().equals(
                                        Environment.MEDIA_MOUNTED);// 判断sdcard是否存在
                                if (isSdCardExist) {
                                    if(CardDialog.instance.bitmap1 != null) {
                                        try {
                                            Canonical.saveBitmapToFile1(CardDialog.instance.bitmap1,ShowcasingActivity.this);
                                            CustomToast.showToast(getApplicationContext(),"保存成功",1000);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    CustomToast.showToast(getApplicationContext(),"sd卡不存在！",1000);
                                }
                            }
                        });

                        view.findViewById(R.id.img_share).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(CardDialog.instance.bitmap1 != null)
                                {
                                    dialog.dismiss();
                                    new ShareAction(ShowcasingActivity.this)
                                            .setDisplayList(SHARE_MEDIA.SINA,SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE,SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE)
                                            .withMedia(new UMImage(ShowcasingActivity.this, CardDialog.instance.bitmap1))
                                            .setCallback(umShareListener)
                                            .open();
                                }
                            }
                        });

                        view.findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                    }
                }).show();
                break;
        }
    }

    @Override
    public void onItemClick(View view, int postion) {
        switch (postion)
        {
            case 0:
                Intent intent1 = new Intent(ShowcasingActivity.this, ParticularsActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putString("id", productUrlOne);
                intent1.putExtras(bundle1);
                startActivity(intent1);
                break;
            case 1:
                Intent intent2 = new Intent(ShowcasingActivity.this, ParticularsActivity.class);
                Bundle bundle2 = new Bundle();
                bundle2.putString("id", productUrlTwo);
                intent2.putExtras(bundle2);
                startActivity(intent2);
                break;
        }
    }

    @Override
    public void onItemLongClick(View view, int postion) {
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>
    {
        private MyItemClickListener mLongClickListener;
        private MyItemClickListener mItemClickListener;
        private List<String> mList = new ArrayList<>();
        private Context context;
        private MyAdapter(Context context)
        {
            this.context = context;
        }

        private void setdata(List<String> mList)
        {
            this.mList = mList;
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

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(getApplicationContext()).inflate(R.layout.showcasing_recycler_item, parent, false)
                    ,mItemClickListener,mLongClickListener);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position)
        {
            Glide.with(context)
                    .load(mList.get(position))
                    .placeholder(R.drawable.zwt)
                    .error(R.drawable.zwt)
                    .dontAnimate()
                    .override(200,200)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transform(new GlideRoundTransform(context, 20))
                    .into(holder.showcasing_icon);
//            Picasso.with(ShowcasingActivity.this)
//                    .load(mList.get(position))
////                    .placeholder(R.drawable.zwt)//默认显示图片
//                    .error(R.drawable.zwt)//加载时出现错误显示的图片
//                    .priority(HIGH)//底部的图片后加载,Picasso支持设置优先级,分为HIGH, MEDIUM, 和 LOW,所有的加载默认优先级为MEDIUM;
//                    .memoryPolicy(NO_CACHE, NO_STORE)//查看大图放弃缓存，加速内存的回收
//                    .tag("RecyclerView") //参数为 Object
//                    .resize(200,200)
//                    .onlyScaleDown() // 如果图片规格大于6000*2000,将只会被resize
//                    .centerCrop()//设置图片圆角
//                    .noFade()//Picasso的默认图片加载方式有一个淡入的效果,如果调用了noFade(),加载的图片将直接显示在ImageView上
//                    .into(holder.showcasing_icon);
////            ImageLoader.getInstance().displayImage(mList.get(position), holder.showcasing_icon, TuActivity.tu(R.drawable.zwt));
        }

        @Override
        public int getItemCount()
        {
            return mList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener
        {
            private ImageView showcasing_icon;
            private MyItemClickListener mListener;
            private MyItemClickListener mLongClickListener;
            public MyViewHolder(View view,MyItemClickListener listener,MyItemClickListener longClickListener)
            {
                super(view);
                showcasing_icon = (ImageView)view.findViewById(R.id.showcasing_icon);
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

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat","platform"+platform);
            if(platform.name().equals("WEIXIN_FAVORITE")){
                Toast.makeText(ShowcasingActivity.this," 收藏成功",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(ShowcasingActivity.this," 分享成功", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(ShowcasingActivity.this," 分享失败", Toast.LENGTH_SHORT).show();
            if(t!=null){
                Log.d("throw","throw:"+t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(ShowcasingActivity.this," 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(ShowcasingActivity.this).onActivityResult(requestCode, resultCode, data);
    }
}
