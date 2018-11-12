package com.mugua.enterprise.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mugua.enterprise.BeasActivity;
import com.mugua.enterprise.R;
import com.mugua.enterprise.activity.me.CardActivity;
import com.mugua.enterprise.activity.me.EnterpriseCertificationActivity;
import com.mugua.enterprise.bean.Showcasing1Bean;
import com.mugua.enterprise.login.LoginActivity;
import com.mugua.enterprise.util.CardDialog;
import com.mugua.enterprise.util.Constant;
import com.mugua.enterprise.util.Okhttp;
import com.mugua.enterprise.util.TuActivity;
import com.mugua.enterprise.util.httppost.IRequestCallBack;
import com.mugua.enterprise.util.toast.CustomToast;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lenovo on 2017/12/20.
 */

public class Showcasing1Activity extends BeasActivity implements View.OnClickListener {
    public static Showcasing1Activity instance = null;
    @BindView( R.id.rl_back )
    public RelativeLayout rl_back ;

    @BindView( R.id.tv_name )
    public TextView tv_name ;

    @BindView( R.id.tv_fddbr )
    public TextView tv_fddbr ;

    @BindView( R.id.tv_cltime )
    public TextView tv_cltime ;

    @BindView( R.id.tv_qyzt )
    public TextView tv_qyzt ;

    @BindView( R.id.tv_zch )
    public TextView tv_zch ;

    @BindView( R.id.tv_shtyxydm )
    public TextView tv_shtyxydm ;

    @BindView( R.id.btn_longin )
    public Button btn_longin ;

    private String id;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showcasing1);
        instance = this;
        //绑定fragment
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");
        new Thread( new MyRunnable()).start();
        rl_back.setOnClickListener(this);
        btn_longin.setOnClickListener(this);
    }

    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            data();
        }
    }

    private void data()
    {
        String www = "?id="+id;
        try {
            Okhttp.doGet(this, Constant.URL_getDetailqcc+www, new IRequestCallBack() {
                @Override
                public void onRequestBack(String text) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(text);
                        if(jsonObject.getString("code").equals("1000"))
                        {
                            Gson gson = new Gson();
                            Showcasing1Bean showcasing1Bean = gson.fromJson(jsonObject.getString("data"),Showcasing1Bean.class);
                            CustomToast.showToast(getApplicationContext(),jsonObject.getString("msg"),1000);

                            tv_name.setText(showcasing1Bean.getName());
                            tv_fddbr.setText(showcasing1Bean.getOperName());
                            tv_cltime.setText(showcasing1Bean.getStartDate());
                            tv_qyzt.setText(showcasing1Bean.getStatus());
                            tv_zch.setText(showcasing1Bean.getNo());
                            tv_shtyxydm.setText(showcasing1Bean.getCreditCode());

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
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_longin:
                if(Constant.user != null)
                {
                    if(Constant.user.getIsCentification().equals("0") || Constant.user.getIsCentification().equals("2"))
                        startActivity(new Intent(Showcasing1Activity.this, EnterpriseCertificationActivity.class));
                    else if(Constant.user.getIsCentification().equals("1"))
                    {
                        if(Constant.user.getExts().equals("0"))//未创建名片
                        {
                            Intent intent = new Intent(Showcasing1Activity.this, CardActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("id", Constant.user.getId());
                            bundle.putString("kind", "0");
                            bundle.putString("title", "创建名片");
                            bundle.putString("btn", "创建名片");
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                        else {
                            Intent intent = new Intent(Showcasing1Activity.this, CardActivity.class);
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
                    startActivity(new Intent(Showcasing1Activity.this, LoginActivity.class));
                }
                break;
        }
    }

    private void finish1()
    {
        //弹出提示框
        new CardDialog(this, R.style.dialog, R.layout.wait_card,0,"",false,new CardDialog.OnCloseListener() {
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
}
