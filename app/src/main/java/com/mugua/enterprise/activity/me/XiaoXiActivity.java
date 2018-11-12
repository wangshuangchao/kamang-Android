package com.mugua.enterprise.activity.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mugua.enterprise.BeasActivity;
import com.mugua.enterprise.R;
import com.mugua.enterprise.util.Constant;
import com.zhy.autolayout.utils.AutoUtils;

import org.json.JSONObject;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jiguang.api.JCoreInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import com.mugua.jiguang.chat.controller.MainController;
import com.mugua.jiguang.chat.database.UserEntry;
import com.mugua.jiguang.chat.utils.SharePreferenceManager;
import com.mugua.jiguang.chat.view.MainView;

/**
 * Created by Lenovo on 2017/11/21.
 */

public class XiaoXiActivity extends BeasActivity implements View.OnClickListener{
    public static XiaoXiActivity instance = null;
    @BindView( R.id.rl_back )
    public RelativeLayout rl_back ;

    @BindView( R.id.ll_xiaoxi )
    public LinearLayout ll_xiaoxi ;

    private MainController mMainController;
    private MainView mMainView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xiaoxi);
        instance = this;
        //绑定fragment
        ButterKnife.bind(this);
        mMainView = (MainView) findViewById(R.id.main_view);
        rl_back.setOnClickListener(this);
        ll_xiaoxi.setOnClickListener(this);

        //检测账号是否登陆
        UserInfo myInfo = JMessageClient.getMyInfo();
        if (myInfo == null) {
            jiguanglogin(Constant.user.getUserPhone(),Constant.user.getUserPhone());
        }else {
            mMainView.initModule();
            mMainController = new MainController(mMainView, XiaoXiActivity.this);

            mMainView.setOnPageChangeListener(mMainController);
        }
    }


    private void jiguanglogin(String userId, final String password)
    {
        JMessageClient.login(userId, password, new BasicCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage) {
                if (responseCode == 0) {
                    SharePreferenceManager.setCachedPsw(password);
                    UserInfo myInfo = JMessageClient.getMyInfo();
                    File avatarFile = myInfo.getAvatarFile();
                    //登陆成功,如果用户有头像就把头像存起来,没有就设置null
                    if (avatarFile != null) {
                        SharePreferenceManager.setCachedAvatarPath(avatarFile.getAbsolutePath());
                    } else {
                        SharePreferenceManager.setCachedAvatarPath(null);
                    }
                    String username = myInfo.getUserName();
                    String appKey = myInfo.getAppKey();
                    String userid = String.valueOf(myInfo.getUserID());
                    UserEntry user = UserEntry.getUser(username, appKey);
                    if (null == user) {
                        user = new UserEntry(username, appKey);
                        user.save();
                    }
                    mMainView.initModule();
                    mMainController = new MainController(mMainView, XiaoXiActivity.this);

                    mMainView.setOnPageChangeListener(mMainController);
                } else {
                }
            }
        });
    }

    public FragmentManager getSupportFragmentManger() {
        return getSupportFragmentManager();
    }

    @Override
    protected void onPause() {
        JCoreInterface.onPause(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        JCoreInterface.onResume(this);
        if(mMainController != null)
            mMainController.sortConvList();
        super.onResume();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.rl_back:
                finish();
                break;
            case R.id.ll_xiaoxi:
                startActivity(new Intent(XiaoXiActivity.this,SystemXiaoXiActivity.class));
                break;
        }
    }
}
