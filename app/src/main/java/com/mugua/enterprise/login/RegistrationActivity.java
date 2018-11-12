package com.mugua.enterprise.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.log.LocalUserInfo;
import com.google.gson.Gson;
import com.mugua.enterprise.BeasActivity;
import com.mugua.enterprise.R;
import com.mugua.enterprise.bean.LoginBean;
import com.mugua.enterprise.fragment.HomeFragment;
import com.mugua.enterprise.fragment.MeFragment;
import com.mugua.enterprise.util.Canonical;
import com.mugua.enterprise.util.Constant;
import com.mugua.enterprise.util.CountDownTimerUtils;
import com.mugua.enterprise.util.DialogActivity;
import com.mugua.enterprise.util.Okhttp;
import com.mugua.enterprise.util.httppost.BaseModel;
import com.mugua.enterprise.util.httppost.IRequestCallBack;
import com.mugua.enterprise.util.toast.CustomToast;
import com.mugua.jiguang.chat.database.UserEntry;
import com.mugua.jiguang.chat.utils.SharePreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

/**
 * Created by Lenovo on 2017/12/6.
 */

public class RegistrationActivity extends BeasActivity implements View.OnClickListener{
    public static RegistrationActivity instance = null;
    @BindView( R.id.rl_back )
    public RelativeLayout rl_back ;

    @BindView( R.id.ing_xieyi )
    public RelativeLayout ing_xieyi ;

    @BindView( R.id.btn_longin )
    public Button btn_longin ;

    @BindView( R.id.btn_yzm )
    public Button btn_yzm ;

    @BindView( R.id.e_zh )
    public EditText e_zh ;

    @BindView( R.id.e_mima )
    public EditText e_mima ;

    @BindView( R.id.e_yzm )
    public EditText e_yzm ;

    @BindView( R.id.tv_title )
    public TextView tv_title ;

    private String uid,openid,shu;
    private int type;

    private DialogActivity dialogActivity;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        instance = this;
        //绑定fragment
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        tv_title.setText(bundle.getString("tile"));

        shu = bundle.getString("shu");
        if(shu.equals("0"))
        {
            type = bundle.getInt("type");
            uid = bundle.getString("uid");
            openid = bundle.getString("openid");
            btn_longin.setText("登录");
        }

        dialogActivity = new DialogActivity(this);
        ing_xieyi.setSelected(true);
        rl_back.setOnClickListener(this);
        btn_longin.setOnClickListener(this);
        btn_yzm.setOnClickListener(this);
        ing_xieyi.setOnClickListener(this);
    }

    private String www,url;
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ing_xieyi:
                if(ing_xieyi.isSelected() == false) ing_xieyi.setSelected(true);
                else ing_xieyi.setSelected(false);
                break;
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_longin:
                if(e_zh.getText().toString().equals("")) {
                    CustomToast.showToast(getApplicationContext(), "请输入手机号");
                    return;
                }else
                {
                    if(Canonical.isMobileNO(e_zh.getText().toString()) == false)
                    {
                        CustomToast.showToast(getApplicationContext(), "手机号不正确");
                        return;
                    }
                }
                if(e_mima.getText().toString().equals("")) {
                    CustomToast.showToast(getApplicationContext(), "请输入密码");
                    return;
                }
                if(e_yzm.getText().toString().equals("")) {
                    CustomToast.showToast(getApplicationContext(), "请输入验证码");
                    return;
                }
                if(ing_xieyi.isSelected() == false)
                {
                    CustomToast.showToast(getApplicationContext(), "请同意咔芒隐私政策");
                    return;
                }
                dialogActivity.showProgressDialog("正在注册，请稍后！");
                if(shu.equals("0")) {
                    url = Constant.URL_fastLogin;
                    www = "type=" + type + "&userPhone=" + e_zh.getText().toString() + "&userPassword=" + e_mima.getText().toString() +
                            "&code=" + e_yzm.getText().toString() + "&qq=" + uid + "&wechat=" + openid;
                }
                else {
                    url = Constant.URL_regist;
                    www = "userPhone=" + e_zh.getText().toString() + "&userPassword=" + e_mima.getText().toString() + "&code=" + e_yzm.getText().toString();
                }

                BaseModel baseModel = new BaseModel(url);
                baseModel.send(www, new IRequestCallBack() {
                    @Override
                    public void onRequestBack(String text) {
                        JSONObject jsonobject = null;
                        try {
                            jsonobject = new JSONObject(text);
                            if (jsonobject.getString("code").equals("1000"))
                            {
                                if(shu.equals("0"))
                                {
                                    JSONObject jsonObject = jsonobject.getJSONObject("data");
                                    String ww = jsonObject.getString("userPhone");
                                    JMessageClient.logout();
                                    //检测账号是否登陆
                                    UserInfo myInfo = JMessageClient.getMyInfo();
                                    if (myInfo == null) {
                                        jiguanglogin(ww,ww,jsonobject);
                                    }else {
                                        data(jsonobject);
                                    }

                                }else {
                                    final String userId = e_zh.getText().toString().trim();
                                    final String password = e_zh.getText().toString().trim();
                                    final JSONObject finalJsonobject = jsonobject;
                                    JMessageClient.register(userId, password, new BasicCallback() {
                                        @Override
                                        public void gotResult(int i, String s) {
                                            if (i == 0) {
                                                SharePreferenceManager.setRegisterName(userId);
                                                SharePreferenceManager.setRegistePass(password);
                                                try {
                                                    CustomToast.showToast(getApplicationContext(), finalJsonobject.getString("msg"),1000);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                                String nickName = e_zh.getText().toString().trim();
                                                UserInfo myUserInfo = JMessageClient.getMyInfo();
                                                if (myUserInfo != null) {
                                                    myUserInfo.setNickname(nickName);
                                                }
                                                //注册时候更新昵称
                                                JMessageClient.updateMyInfo(UserInfo.Field.nickname,myUserInfo , new BasicCallback() {
                                                    @Override
                                                    public void gotResult(final int status, String desc) {
                                                        //更新跳转标志
                                                        SharePreferenceManager.setCachedFixProfileFlag(false);
                                                    }
                                                });
                                                dialogActivity.dissmissProgressDialog();
                                                finish();
                                            } else {
                                                if(s.equals("user exist"))
                                                {
                                                    try {
                                                        CustomToast.showToast(getApplicationContext(), finalJsonobject.getString("msg"),1000);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    finish();
                                                }
                                                dialogActivity.dissmissProgressDialog();
                                            }
                                        }
                                    });
                                }
                            }
                            else {
                                dialogActivity.dissmissProgressDialog();
                                CustomToast.showToast(getApplicationContext(), jsonobject.getString("msg"), 1000);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case R.id.btn_yzm:
                if(shu.equals("0")) www = "?phone="+e_zh.getText().toString()+"&type=2";
                else www = "?phone="+e_zh.getText().toString()+"&type=0";
                try {
                    Okhttp.doGet(this, Constant.URL_yzm+www, new IRequestCallBack() {
                        @Override
                        public void onRequestBack(String text) {
                            JSONObject jsonobject = null;
                            try {
                                jsonobject = new JSONObject(text);
                                if (jsonobject.getString("code").equals("1000"))
                                {
                                    CustomToast.showToast(getApplicationContext(),jsonobject.getString("msg"),1000);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(btn_yzm,btn_yzm, 60000, 1000);
                mCountDownTimerUtils.start();
                break;
        }
    }

    private void jiguanglogin(String userId, final String password, final JSONObject jsonobject)
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
                    data(jsonobject);
                } else {
                    data(jsonobject);
                }
            }
        });
    }
    private void data(JSONObject jsonobject)
    {
        try {
            CustomToast.showToast(getApplicationContext(),jsonobject.getString("msg"),1000);
            Gson gson = new Gson();
            LoginBean loginBean = gson.fromJson(jsonobject.getString("data"),LoginBean.class);
            Constant.user = loginBean;
            LocalUserInfo.getInstance(RegistrationActivity.this).setUserInfo("id", loginBean.getId());
            LocalUserInfo.getInstance(RegistrationActivity.this).setUserInfo("open", "true");
//            LocalUserInfo.getInstance(RegistrationActivity.this).setUserInfo("userPhone", e_zh.getText().toString());
//            LocalUserInfo.getInstance(RegistrationActivity.this).setUserInfo("userPassword", e_mima.getText().toString());
            dialogActivity.dissmissProgressDialog();
            LoginActivity.instance.finish();
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
