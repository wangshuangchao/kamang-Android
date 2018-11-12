package com.mugua.enterprise.activity.me;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.log.LocalUserInfo;
import com.mugua.enterprise.BeasActivity;
import com.mugua.enterprise.MainActivity;
import com.mugua.enterprise.R;
import com.mugua.enterprise.fragment.MeFragment;
import com.mugua.enterprise.login.SplashActivity;
import com.mugua.enterprise.util.Constant;
import com.mugua.enterprise.util.DataCleanManager;
import com.mugua.enterprise.util.DialogActivity;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import com.mugua.jiguang.chat.database.UserEntry;
import com.mugua.jiguang.chat.utils.SharePreferenceManager;
import com.mugua.jiguang.chat.utils.ToastUtil;

/**
 * Created by Lenovo on 2017/11/21.
 */

public class SettingActivity extends BeasActivity implements View.OnClickListener{
    public static SettingActivity instance = null;
    @BindView( R.id.rl_back )
    public RelativeLayout rl_back ;

    @BindView( R.id.user_feedback )
    public TextView user_feedback ;

    @BindView( R.id.tv_kamang )
    public TextView tv_kamang ;

    @BindView( R.id.tv_size )
    public TextView tv_size ;

    @BindView( R.id.rl_1 )
    public RelativeLayout rl_1 ;

    @BindView( R.id.btn_btn )
    public Button btn_btn ;

    private DialogActivity dialogActivity;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        instance = this;
        //绑定fragment
        ButterKnife.bind(this);
        dialogActivity = new DialogActivity(this);

        try {
            tv_size.setText(DataCleanManager.getTotalCacheSize(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

        rl_back.setOnClickListener(this);
        user_feedback.setOnClickListener(this);
        tv_kamang.setOnClickListener(this);
        rl_1.setOnClickListener(this);
        btn_btn.setOnClickListener(this);
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
                    finish();
                } else {
                    ToastUtil.shortToast(getApplicationContext(), "登陆失败" + responseMessage);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_btn:
                Constant.user = null;
                LocalUserInfo.getInstance(this).setUserInfo("open", "false");
                MeFragment.instance.itin();
                JMessageClient.logout();
                WifiManager wm = (WifiManager)getSystemService(Context.WIFI_SERVICE);
                String imei = wm.getConnectionInfo().getMacAddress();
                imei = imei.replace(":","");
                jiguanglogin(imei,"123789");
                break;
            case R.id.user_feedback:
                startActivity(new Intent(SettingActivity.this,UserFeedbackActivity.class));
                break;
            case R.id.tv_kamang:
                startActivity(new Intent(SettingActivity.this,RegardActivity.class));
                break;
            case R.id.rl_1:
                dialogActivity.showProgressDialog("清理中,请稍后...");
                DataCleanManager.clearAllCache(this);
                new Handler(getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialogActivity.dissmissProgressDialog();
                        try {
                            tv_size.setText(DataCleanManager.getTotalCacheSize(SettingActivity.this));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 3000);
                break;
        }
    }
}
