package com.mugua.enterprise.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;

/**
 * Created by Administrator on 2017/4/11.
 */

public class DialogActivity{
    private Context context;
    public DialogActivity(Context context)
    {
        this.context = context;
    }
    /**
     * 显示进度框
     */
    private ProgressDialog progDialog = null;// 进度条
    public void showProgressDialog(String text) {
        if (progDialog == null)
            progDialog = new ProgressDialog(context);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条风格，风格为圆形，旋转的
        progDialog.setIndeterminate(false);// 设置ProgressDialog 的进度条是否不明确 false 就是不设置为不明确
        progDialog.setCancelable(false); // 设置ProgressDialog 是否可以按退回键取消
        progDialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
//        progDialog.setTitle("提示");// 设置ProgressDialog 标题
        progDialog.setMessage(text);//// 设置ProgressDialog提示信
        progDialog.show();
        new Thread( new MyRunnable()).start();
    }

    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            new Handler(context.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    progDialog.dismiss();
                }
            }, 30000);
        }
    }
    /**
     * 隐藏进度框
     */
    public void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }
}
