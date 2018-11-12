package com.mugua.enterprise;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.example.log.LocalUserInfo;
import com.mugua.enterprise.banben.DownloadService;
import com.mugua.enterprise.banben.NotificationsUtils;
import com.mugua.enterprise.fragment.HitokotoFragment;
import com.mugua.enterprise.fragment.HomeFragment;
import com.mugua.enterprise.fragment.MaterialFragment;
import com.mugua.enterprise.fragment.MaterialFragment1;
import com.mugua.enterprise.fragment.MeFragment;
import com.mugua.enterprise.util.ACache;
import com.mugua.enterprise.util.Constant;
import com.mugua.enterprise.util.CountDownTimerUtils1;
import com.mugua.enterprise.util.Okhttp;
import com.mugua.enterprise.util.httppost.IRequestCallBack;
import com.mugua.jiguang.chat.database.UserEntry;
import com.mugua.jiguang.chat.utils.SharePreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

public class MainActivity extends BeasActivity {
    public static MainActivity instance = null;
    public BottomBar bottomBar;
    public CountDownTimerUtils1 mCountDownTimerUtils;
    private int currentVersionCode;//版本号
    private String appVersion;//版本名
    private MyApplication app;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCountDownTimerUtils = new CountDownTimerUtils1(3000000, 1000);
        instance = this;

        bottomBar = (BottomBar) findViewById(R.id.ll_bottom_bar);
        bottomBar.setOnItemChangedListener(new BottomBar.OnItemChangedListener() {

            @Override
            public void onItemChanged(int index) {

                showDetails(index);
            }
        });
        bottomBar.setSelectedState(0);

        //获取版本
        app = (MyApplication) getApplication();
        PackageManager manager = this.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            appVersion = info.versionName; // 版本名
            currentVersionCode = info.versionCode; // 版本号
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch blockd
            e.printStackTrace();
        }

        if(!NotificationsUtils.isNotificationEnabled(this))
        {
            new Thread( new MyRunnableup()).start();
        }else new Thread( new MyRunnableup()).start();

        if(Constant.open2 == false)
        {
            Constant.open2 = true;
            mCache = ACache.get(this);
            WifiManager wm = (WifiManager)getSystemService(Context.WIFI_SERVICE);
            imei = wm.getConnectionInfo().getMacAddress();
            imei = imei.replace(":","");
            new Thread( new MyRunnable()).start();
        }
    }

    private AlertDialog.Builder isExit3;
    public Handler mhandler1 = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            isExit3 = new AlertDialog.Builder(MainActivity.this);
            isExit3.setTitle("是否打开应用通知");
            isExit3.setMessage("打开通知可获取应用下载进度");
            isExit3.setPositiveButton("打开", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    // TODO Auto-generated method stub
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Intent intent = new Intent();
                        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                        intent.putExtra("app_package", getPackageName());
                        intent.putExtra("app_uid", getApplicationInfo().uid);
                        startActivity(intent);
                    } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener()
            {

                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                }
            });
            isExit3.setCancelable(false);
            isExit3.show();
        }
    };

    private class MyRunnableup implements Runnable {
        @Override
        public void run() {
            Data();
        }
    }

    private String updata;
    private void Data()
    {
        try {
            Okhttp.doGet(this, Constant.URL_updateInfo, new IRequestCallBack() {
                @Override
                public void onRequestBack(String text) {
                    JSONObject jsonobject = null;
                    try {
                        jsonobject = new JSONObject(text);
                        if(jsonobject.get("code").equals("1000"))
                        {
                            JSONObject jsonObject = jsonobject.getJSONObject("data");
                            updata = jsonObject.getString("sdkVersion");
                            Constant.apkUrl = jsonObject.getString("url");
                            int www = Integer.parseInt(updata);
                            if(www > currentVersionCode)
                            {
                                if(NotificationsUtils.isNotificationEnabled(getApplicationContext()) == false)
                                    mhandler1.sendMessage(mhandler1.obtainMessage());
                                mhandler.sendMessage(mhandler.obtainMessage());
                            }
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

    private AlertDialog.Builder isExit2;
    public Handler mhandler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            isExit2 = new AlertDialog.Builder(MainActivity.this);
            isExit2.setTitle("检测到新版本");
            isExit2.setMessage("是否下载更新?");
            isExit2.setPositiveButton("下载", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
//                    // TODO Auto-generated method stub
//                    //进入版本更新
                    app.setDownload(true);
                    Intent it = new Intent(MainActivity.this, DownloadService.class);
                    startService(it);
                    bindService(it, conn, Context.BIND_AUTO_CREATE);
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener()
            {

                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                }
            });
            isExit2.setCancelable(false);
            isExit2.show();
        }
    };

    private HomeFragment homeFragment;
    private HitokotoFragment applicationFragment;
    private MaterialFragment discoverFragment;
    private MeFragment meFragment;
    private int jl = -1;
    public void showDetails(int index) {
        if(jl == index)
            return;
        jl = index;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (index) {
            case 0:
                homeFragment = new HomeFragment();
                ft.replace(R.id.details, homeFragment);
                break;
            case 1:
                applicationFragment = new HitokotoFragment();
                ft.replace(R.id.details, applicationFragment);
                break;
            case 2:
                discoverFragment = new MaterialFragment();
                ft.replace(R.id.details, discoverFragment);
                break;
            case 3:
                meFragment = new MeFragment();
                ft.replace(R.id.details, meFragment);
                break;
        }
        ft.setTransition(ft.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);//这行代码可以返回之前的操作（横屏的情况下，即两边都显示的情况下）
        ft.commit();
    }

    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            if(LocalUserInfo.getInstance(getApplicationContext()).getUserInfo("open").equals("false") ||
                    LocalUserInfo.getInstance(getApplicationContext()).getUserInfo("open").equals(""))
            {
                String ww = mCache.getAsString("open");
                if(ww == null)
                {
                    JMessageClient.register(imei, "123789", new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            if (i == 0) {
                                SharePreferenceManager.setRegisterName(imei);
                                SharePreferenceManager.setRegistePass("123789");
                                mCache.put("open", "true");
                                data();
                            } else {
                                //检测账号是否登陆
                                mCache.put("open", "true");
                                UserInfo myInfo = JMessageClient.getMyInfo();
                                if (myInfo == null) {
                                    jiguanglogin(imei,"123789");
                                }
                            }
                        }
                    });
                }else {
                    //检测账号是否登陆
                    UserInfo myInfo = JMessageClient.getMyInfo();
                    if (myInfo == null) {
                        jiguanglogin(imei,"123789");
                    }
                }
            }
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
                } else {
                    jiguanglogin(imei,"123789");
                }
            }
        });
    }
    private ACache mCache;
    public String imei;
    private void data()
    {
        String nickName = "游客";
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
                if (status == 0) {
                    //检测账号是否登陆
                    UserInfo myInfo = JMessageClient.getMyInfo();
                    if (myInfo == null) {
                        jiguanglogin(imei,"123789");
                    }
                }
            }
        });
    }

    private AlertDialog isExit;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 创建??出对话框
            isExit = new AlertDialog.Builder(this).create();
            // 设置对话框标题
            isExit.setTitle("系统提示");
            // 设置对话框消息
            isExit.setMessage("确定要退出吗");
            // 添加选择按钮并注册监听
            isExit.setButton("确定", listener);
            isExit.setButton2("取消", listener);
            // 显示对话框
            isExit.show();
            isExit.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        }
        if (keyCode == KeyEvent.KEYCODE_HOME) {
        }
        return false;
    }

    /** 监听对话框里面的button点击事件 */
    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    MyApplication.getInstance().exit();
//                    android.os.Process.killProcess(android.os.Process.myPid());// 获取PID
//                    System.exit(0);// 常规java、c#的标准退出法，返回值为0代表正常退出
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    isExit.dismiss();//关闭对话框
                    break;
                default:
                    break;
            }
        }
    };

    public void stap()
    {
        binder.cancel();
        binder.cancelNotification();
        Intent it = new Intent(this, DownloadService.class);
        stopService(it);
    }


    public boolean isBinded;
    public DownloadService.DownloadBinder binder;
    public ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            isBinded = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            binder = (DownloadService.DownloadBinder) service;
            // 开始下载
            isBinded = true;
            binder.addCallback(callback);
            binder.start();

        }
    };

    private ICallbackResult callback = new ICallbackResult() {

        @Override
        public void OnBackResult(Object result) {
            // TODO Auto-generated method stub
            if ("finish".equals(result)) {
                Intent it = new Intent(MainActivity.this, DownloadService.class);
                stopService(it);
                return;
            }
        }
    };

    public interface ICallbackResult {
        void OnBackResult(Object result);
    }
}