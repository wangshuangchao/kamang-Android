package com.mugua.enterprise.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.log.LocalUserInfo;
import com.google.gson.Gson;
import com.mugua.enterprise.Mall.Order_Details;
import com.mugua.enterprise.R;
import com.mugua.enterprise.activity.FundsActivity;
import com.mugua.enterprise.activity.me.AllOrderActivity;
import com.mugua.enterprise.activity.me.CardActivity;
import com.mugua.enterprise.activity.me.EditDataActivity;
import com.mugua.enterprise.activity.me.EnterpriseCertificationActivity;
import com.mugua.enterprise.activity.me.InventoryActivity;
import com.mugua.enterprise.activity.me.MoreActivity;
import com.mugua.enterprise.activity.me.SettingActivity;
import com.mugua.enterprise.activity.me.XiaoXiActivity;
import com.mugua.enterprise.bean.LoginBean;
import com.mugua.enterprise.login.LoginActivity;
import com.mugua.enterprise.util.Canonical;
import com.mugua.enterprise.util.CardDialog;
import com.mugua.enterprise.util.Constant;
import com.mugua.enterprise.util.GlideRoundTransform;
import com.mugua.enterprise.util.ImageAsyncTask;
import com.mugua.enterprise.util.Okhttp;
import com.mugua.enterprise.util.httppost.BaseModel;
import com.mugua.enterprise.util.httppost.IRequestCallBack;
import com.mugua.enterprise.util.toast.CustomToast;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.utils.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lenovo on 2017/9/9.
 */

public class MeFragment extends Fragment implements View.OnClickListener{
    public static MeFragment instance = null;

    @BindView( R.id.ll_share )
    public LinearLayout ll_share ;

    @BindView( R.id.rl_xiaoxi )
    public RelativeLayout rl_xiaoxi ;

    @BindView( R.id.rl_setting )
    public RelativeLayout rl_setting ;

    @BindView( R.id.ll_tipoff )
    public LinearLayout ll_tipoff;

    @BindView( R.id.ll_card )
    public LinearLayout ll_card;

    @BindView( R.id.btn_login )
    public Button btn_login;

    @BindView( R.id.img_tx )
    public ImageView img_tx ;

    @BindView( R.id.rl_funds )
    public RelativeLayout rl_funds ;

    @BindView( R.id.rl_kucun )
    public RelativeLayout rl_kucun ;

    @BindView( R.id.rl_order )
    public RelativeLayout rl_order ;

    @BindView( R.id.rl_gdm )
    public RelativeLayout rl_gdm ;

    @BindView( R.id.tv_name )
    public TextView tv_name ;

    @BindView( R.id.tv_card )
    public TextView tv_card ;

    private View mView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null)
            return null;
        mView = inflater.inflate(R.layout.fragment_me,container,false);
        instance = this;
        //绑定fragment
        ButterKnife.bind( this , mView ) ;
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(LocalUserInfo.getInstance(getActivity()).getUserInfo("open").equals("true")) {
            btn_login.setVisibility(View.GONE);
            new Thread( new MyRunnable()).start();
        }
        else itin();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        Glide.clear(img_tx);
        getActivity().onStateNotSaved();
        MeFragment.this.onDestroy();
    }

    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            loginshuju();
        }
    }

    public void itin()
    {
        Bitmap bitmap = BitmapFactory. decodeResource (getResources(),R.mipmap.mr_tx1);
        img_tx.setImageBitmap(bitmap);
        btn_login.setVisibility(View.VISIBLE);
        tv_name.setText("");
        tv_card.setText("创建名片");
    }

    public void data()
    {
        btn_login.setVisibility(View.GONE);
//        ImageAsyncTask imageAsyncTask = new ImageAsyncTask(Constant.user.getPhotoUrl());
//        imageAsyncTask.data(new ImageAsyncTask.OnCloseListener()
//        {
//            @Override
//            public void onClick(Bitmap bitmap) {
//                img_tx.setImageBitmap(bitmap);
//            }
//        });
        Glide.with(this)
                .load(Constant.user.getPhotoUrl())
                .placeholder(img_tx.getBackground())
                .error(R.mipmap.mr_tx1)
                .override(150,150)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new GlideRoundTransform(getActivity(), 0))
                .into(img_tx);

        tv_name.setText(Constant.user.getUserName());
        if(Constant.user.getExts().equals("0"))
            tv_card.setText("创建名片");
        else tv_card.setText("修改名片");
        if(Constant.user.getIsCentification().equals("0"))
            setinit();
    }

    public Dialog dialog1 = null;
    private void setinit()
    {
        //弹出提示框
        new CardDialog(getActivity(), R.style.dialog, R.layout.me_card,0,"",true,new CardDialog.OnCloseListener() {
            @Override
            public void onClick(final Dialog dialog, View view) {
                view.findViewById(R.id.btn_zb).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog1 = dialog;
                        renzheng();
                    }
                });
                view.findViewById(R.id.btn_qrz).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        renzheng();
                        startActivity(new Intent(getActivity(), EnterpriseCertificationActivity.class));
                    }
                });
                view.findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog1 = dialog;
                        renzheng();
                    }
                });
            }
        }).show();
    }

    public void renzheng()
    {
        String www = "id="+Constant.user.getId()+"&isCentification=2";
        BaseModel baseModel = new BaseModel(Constant.URL_turnIdentification);
        baseModel.send(www, new IRequestCallBack() {
            @Override
            public void onRequestBack(String text) {
                JSONObject jsonobject = null;
                try {
                    jsonobject = new JSONObject(text);
                    if (jsonobject.getString("code").equals("1000"))
                    {
                        dialog1.dismiss();
                    }
                    else CustomToast.showToast(getActivity(),jsonobject.getString("msg"),1000);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        rl_setting.setOnClickListener(this);
        ll_share.setOnClickListener(this);
        rl_xiaoxi.setOnClickListener(this);
        ll_tipoff.setOnClickListener(this);
        ll_card.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        rl_funds.setOnClickListener(this);
        img_tx.setOnClickListener(this);
        rl_order.setOnClickListener(this);
        rl_kucun.setOnClickListener(this);
        rl_gdm.setOnClickListener(this);
    }

    public void loginshuju()
    {
        String www = "?id="+Constant.user.getId();
        try {
            Okhttp.doGet(getActivity(), Constant.URL_getUserInfo+www, new IRequestCallBack() {
                @Override
                public void onRequestBack(String text) {
                    if (!Constant.me_text.equals(text)) {
                        Constant.me_text = text;

                        JSONObject jsonobject = null;
                        try {
                            jsonobject = new JSONObject(text);
                            if (jsonobject.getString("code").equals("1000"))
                            {
                                Gson gson = new Gson();
                                LoginBean loginBean = gson.fromJson(jsonobject.getString("data"),LoginBean.class);
                                Constant.user = loginBean;
                                data();
                            }
                            else CustomToast.showToast(getActivity(),jsonobject.getString("msg"),1000);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else data();
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
            case R.id.ll_share:
                if(Constant.user != null)
                {
                    if(Constant.user.getExts().equals("0") && Constant.user.getIsCentification().equals("1"))//未创建名片
                    {
                        Intent intent = new Intent(getActivity(), CardActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("id", Constant.user.getId());
                        bundle.putString("kind", "0");
                        bundle.putString("title", "创建名片");
                        bundle.putString("btn", "创建名片");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }else {
                        //弹出提示框
                        new CardDialog(getActivity(), R.style.dialog, R.layout.showcasing_card,2,Constant.user.getId(),false,new CardDialog.OnCloseListener() {
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
                                                    Canonical.saveBitmapToFile1(CardDialog.instance.bitmap1,getActivity());
                                                    CustomToast.showToast(getActivity(),"保存成功",1000);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        } else {
                                            CustomToast.showToast(getActivity(),"sd卡不存在！",1000);
                                        }
                                    }
                                });

                                view.findViewById(R.id.img_share).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(CardDialog.instance.bitmap1 != null)
                                        {
                                            dialog.dismiss();
                                            new ShareAction(getActivity())
                                                    .setDisplayList(SHARE_MEDIA.SINA,SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE,SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE)
                                                    .withMedia(new UMImage(getActivity(), CardDialog.instance.bitmap1))
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
                    }
                }
                else startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case R.id.rl_gdm:
                if(Constant.user != null)
                    startActivity(new Intent(getActivity(), MoreActivity.class));
                else startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case R.id.rl_xiaoxi:
                if(Constant.user != null)
                    startActivity(new Intent(getActivity(), XiaoXiActivity.class));
                else startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case R.id.rl_setting:
                if(Constant.user != null)
                    startActivity(new Intent(getActivity(), SettingActivity.class));
                else startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case R.id.ll_tipoff:
                if(Constant.user != null)
                {
                    if(Constant.user.getIsCentification().equals("0") || Constant.user.getIsCentification().equals("2"))
                        startActivity(new Intent(getActivity(), EnterpriseCertificationActivity.class));
                    else if(Constant.user.getIsCentification().equals("1"))
                        CustomToast.showToast(getActivity(),"已认证，到别处看看吧！",1000);
                    else if(Constant.user.getIsCentification().equals("3"))
                        finish1();
                }
                else startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case R.id.ll_card:
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
                else startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case R.id.btn_login:
                startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case R.id.rl_funds:
                if(Constant.user != null)
                    startActivity(new Intent(getActivity(), FundsActivity.class));
                else startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case R.id.rl_kucun:
                if(Constant.user != null){
                    Intent intent = new Intent(getActivity(), InventoryActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("open", false);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case R.id.img_tx:
                if(Constant.user != null)
                    startActivity(new Intent(getActivity(), EditDataActivity.class));
                else startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case R.id.rl_order:
                if(Constant.user != null)
                    startActivity(new Intent(getActivity(), AllOrderActivity.class));
                else startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
        }
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

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat","platform"+platform);
            if(platform.name().equals("WEIXIN_FAVORITE")){
                Toast.makeText(getActivity()," 收藏成功",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getActivity(), "分享成功", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(getActivity()," 分享失败", Toast.LENGTH_SHORT).show();
            if(t!=null){
                Log.d("throw","throw:"+t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(getActivity()," 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(getActivity()).onActivityResult(requestCode, resultCode, data);
    }
}