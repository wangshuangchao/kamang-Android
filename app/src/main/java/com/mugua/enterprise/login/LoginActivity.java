package com.mugua.enterprise.login;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.log.LocalUserInfo;
import com.google.gson.Gson;
import com.mugua.enterprise.BeasActivity;
import com.mugua.enterprise.R;
import com.mugua.enterprise.activity.HuoDongActivity;
import com.mugua.enterprise.bean.LoginBean;
import com.mugua.enterprise.fragment.HomeFragment;
import com.mugua.enterprise.fragment.MeFragment;
import com.mugua.enterprise.util.Constant;
import com.mugua.enterprise.util.DialogActivity;
import com.mugua.enterprise.util.Okhttp;
import com.mugua.enterprise.util.httppost.IRequestCallBack;
import com.mugua.enterprise.util.toast.CustomToast;
import com.mugua.jiguang.chat.database.UserEntry;
import com.mugua.jiguang.chat.utils.SharePreferenceManager;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zhy.autolayout.AutoLayoutActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

/**
 * Created by Lenovo on 2017/12/4.
 */

public class LoginActivity extends BeasActivity implements View.OnClickListener{
    public static LoginActivity instance = null;
    @BindView( R.id.rl_back )
    public RelativeLayout rl_back ;

    @BindView( R.id.tv_wj )
    public TextView tv_wj ;

    @BindView( R.id.tv_zc )
    public TextView tv_zc ;

    @BindView( R.id.img_yan )
    public ImageView img_yan ;

    @BindView( R.id.e_zh )
    public EditText e_zh ;

    @BindView( R.id.e_mima )
    public EditText e_mima ;

    @BindView( R.id.image_QQ )
    public ImageView image_QQ ;

    @BindView( R.id.image_weixin )
    public ImageView image_weixin ;

    @BindView( R.id.image_daohangzc )
    public RelativeLayout image_daohangzc ;

    @BindView( R.id.image_daohang )
    public ImageView image_daohang ;

    @BindView( R.id.btn_longin )
    public Button btn_longin ;
    private DialogActivity dialogActivity;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        instance = this;
        //绑定fragment
        ButterKnife.bind(this);
        if(LocalUserInfo.getInstance(getApplicationContext()).getUserInfo("open3").equals("false") ||
                LocalUserInfo.getInstance(getApplicationContext()).getUserInfo("open3").equals("")) {
            image_daohangzc.setVisibility(View.VISIBLE);
        }else {
            image_daohangzc.setVisibility(View.GONE);
        }
        dialogActivity = new DialogActivity(this);
        rl_back.setOnClickListener(this);
        tv_wj.setOnClickListener(this);
        tv_zc.setOnClickListener(this);
        img_yan.setOnClickListener(this);
        btn_longin.setOnClickListener(this);
        image_QQ.setOnClickListener(this);
        image_weixin.setOnClickListener(this);
        image_daohang.setOnClickListener(this);
        image_daohangzc.setOnClickListener(this);
    }

    private boolean open;
    private int openkind;
    private IWXAPI api;
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.image_daohang:
                LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("open3", "true");
                image_daohangzc.setVisibility(View.GONE);
                break;
            case R.id.image_weixin:
                api = WXAPIFactory.createWXAPI(this, Constant.APP_ID, true);
                api.registerApp(Constant.APP_ID);
                if (!api.isWXAppInstalled()) {
                    CustomToast.showToast(LoginActivity.this,"您还未安装微信客户端",1000);
                    return;
                }
                openkind = 1;
                authorization(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.image_QQ:
                if(isQQClientAvailable(this) == false)
                {
                    CustomToast.showToast(LoginActivity.this,"您还未安装QQ客户端",1000);
                    dialogActivity.dissmissProgressDialog();
                    return;
                }
                openkind = 0;
                authorization(SHARE_MEDIA.QQ);
                break;
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_longin:
                dialogActivity.showProgressDialog("正在登陆，请稍后！");
                String www = "?userPhone="+e_zh.getText().toString()+"&userPassword="+ e_mima.getText().toString();
                try {
                    Okhttp.doGet(this, Constant.URL_login+www, new IRequestCallBack() {
                        @Override
                        public void onRequestBack(String text) {
                            JSONObject jsonobject = null;
                            try {
                                jsonobject = new JSONObject(text);
                                if (jsonobject.getString("code").equals("1000"))
                                {
                                    JMessageClient.logout();
                                    //检测账号是否登陆
                                    UserInfo myInfo = JMessageClient.getMyInfo();
                                    if (myInfo == null) {
                                        jiguanglogin(e_zh.getText().toString().trim(),e_zh.getText().toString().trim(),jsonobject);
                                    }else {
                                        data(jsonobject);
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_wj:
                startActivity(new Intent(LoginActivity.this,RetrievePasswordActivity.class));
                break;
            case R.id.tv_zc:
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("shu", "1");
                bundle.putString("tile", "登录");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.img_yan:
                if(open == false)
                {
                    open = true;
                    e_mima.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else
                {
                    open = false;
                    e_mima.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                e_mima.setSelection(e_mima.getText().length());
                break;
        }
    }

    /**
     * 判断 用户是否安装QQ客户端
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equalsIgnoreCase("com.tencent.qqlite") || pn.equalsIgnoreCase("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
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
            LocalUserInfo.getInstance(LoginActivity.this).setUserInfo("id", loginBean.getId());
            LocalUserInfo.getInstance(LoginActivity.this).setUserInfo("open", "true");
//            LocalUserInfo.getInstance(LoginActivity.this).setUserInfo("userPhone", e_zh.getText().toString());
//            LocalUserInfo.getInstance(LoginActivity.this).setUserInfo("userPassword", e_mima.getText().toString());
            dialogActivity.dissmissProgressDialog();
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //授权
    private void authorization(SHARE_MEDIA share_media) {
        UMShareAPI.get(this).getPlatformInfo(this, share_media, new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
                dialogActivity.showProgressDialog("正在登陆，请稍后！");
            }

            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
//                //sdk是6.4.4的,但是获取值的时候用的是6.2以前的(access_token)才能获取到值,未知原因
                if(openkind == 0) {
                    String uid = map.get("uid");
                    okhttpdata(uid,"",0);
                }else
                {
                    String openid = map.get("openid");
                    okhttpdata("",openid,1);
                }
//                String openid = map.get("openid");//微博没有
//                String unionid = map.get("unionid");//微博没有
//                String access_token = map.get("access_token");
//                String refresh_token = map.get("refresh_token");//微信,qq,微博都没有获取到
//                String expires_in = map.get("expires_in");
//                String name = map.get("name");
//                String gender = map.get("gender");
//                String iconurl = map.get("iconurl");
//                CustomToast.showToast(getApplicationContext(), "name=" + name + ",gender=" + gender, 1000);
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                dialogActivity.dissmissProgressDialog();
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {
                dialogActivity.dissmissProgressDialog();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    private String www = "";
    private void okhttpdata(final String uid, final String openid, final int type)
    {
        if(type == 0)
            www = "?type="+type+"&num="+ uid;
        else www = "?type="+type+"&num="+ openid;
        try {
            Okhttp.doGet(this, Constant.URL_judge+www, new IRequestCallBack() {
                @Override
                public void onRequestBack(String text) {
                    JSONObject jsonobject = null;
                    try {
                        jsonobject = new JSONObject(text);
                        if (jsonobject.getString("code").equals("1000"))
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
                        }
                        else {
                            dialogActivity.dissmissProgressDialog();
                            CustomToast.showToast(getApplicationContext(), jsonobject.getString("msg"), 1000);

                            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("shu", "0");
                            bundle.putInt("type", type);
                            bundle.putString("uid", uid);
                            bundle.putString("openid", openid);
                            bundle.putString("tile", "快速登录");
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
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
}
