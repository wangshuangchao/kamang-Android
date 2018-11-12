package com.mugua.enterprise.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.log.LocalUserInfo;
import com.mugua.enterprise.MainActivity;
import com.mugua.enterprise.R;

import java.lang.reflect.Method;

import cn.jpush.im.android.api.JMessageClient;

/**
 * Created by Administrator on 2017/2/24.
 */

public class SplashActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if(LocalUserInfo.getInstance(getApplicationContext()).getUserInfo("open2").equals("false") ||
                        LocalUserInfo.getInstance(getApplicationContext()).getUserInfo("open2").equals("")) {
                    swapEnvironment(SplashActivity.this.getApplicationContext(), false);
                    startActivity(new Intent(SplashActivity.this, YinDaoActivity.class));
                    overridePendingTransition(R.anim.in, R.anim.out);
                    finish();
                }else {
                    swapEnvironment(SplashActivity.this.getApplicationContext(), false);
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    overridePendingTransition(R.anim.in, R.anim.out);
                    finish();
                }
            }
        }, 2000);
    }

    public static void swapEnvironment(Context context, boolean isTest) {
        try {
            Method method = JMessageClient.class.getDeclaredMethod("swapEnvironment", Context.class, Boolean.class);
            method.invoke(null, context, isTest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}