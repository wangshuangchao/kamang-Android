package com.mugua.enterprise.util;

import android.content.Context;
import android.os.Handler;

import com.mugua.enterprise.util.httppost.IRequestCallBack;
import com.mugua.enterprise.util.toast.CustomToast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.os.Looper.getMainLooper;

/**
 * Created by Administrator on 2017/8/22.
 */

public class Okhttp {
    public static void doGet(final Context mContext, String url, final IRequestCallBack iRequestCallBack) throws IOException {
        //1.拿到okHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //2.构造Request
        Request.Builder builder = new Request.Builder();
        Request request = builder
                .url(url)
                .build();
        //3.将request封装为call
        Call call = okHttpClient.newCall(request);
        //异步执行
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                new Handler(getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CustomToast.showToast(mContext,"获取失败",1000);
                    }
                }, 100);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res = response.body().string();//拿到String字符串
                new Handler(getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (!res.equals("") && iRequestCallBack != null)
                                iRequestCallBack.onRequestBack(res);
                        } catch (Exception var3) {
                        }
                    }
                }, 100);
            }
        });
    }

//    //2.构造frombody
//    FormBody formBody = new FormBody.Builder().add("phone", "17310348891").add("password", "123123").build();

    public static void doPost(final Context mContext, String url, FormBody formBody,final IRequestCallBack iRequestCallBack) throws IOException {
        //1.拿到okHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //3.构造request
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(url).post(formBody).build();
        //4.将request封装为call
        Call call = okHttpClient.newCall(request);
        //5.执行call
        //        Response response = call.execute();同步执行
        //异步执行
        call.enqueue(new Callback() {
            private String msg;
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                new Handler(getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CustomToast.showToast(mContext,"获取失败",1000);
                    }
                }, 100);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res = response.body().string();//拿到String字符串
                new Handler(getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (!res.equals("") && iRequestCallBack != null)
                                iRequestCallBack.onRequestBack(res);
                        } catch (Exception var3) {
                        }
                    }
                }, 100);
            }
        });
    }
}
