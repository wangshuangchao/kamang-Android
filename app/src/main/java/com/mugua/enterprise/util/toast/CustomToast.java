package com.mugua.enterprise.util.toast;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/8/23.
 */
public class CustomToast {
    private static Toast mToast;
    private static Handler mHandler = new Handler();
    private static Runnable r = new Runnable() {
        public void run() {
            mToast.cancel();
        }
    };

    public static void showToast(Context mContext, String text, int duration,int kind) {

        mHandler.removeCallbacks(r);
        if (mToast != null)
            mToast.setText(text);
        else
            mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
        mHandler.postDelayed(r, duration);
        switch (kind)
        {
            case 1:
                mToast.setGravity(Gravity.CENTER, 0, 0);
                break;
            case 2:
                mToast.setGravity(Gravity.TOP, 0, -100);
                break;
            case 3:
                mToast.setGravity(Gravity.BOTTOM, 0, 100);
                break;
        }
        mToast.show();
    }

    public static void showToast(Context mContext, String text, int duration) {

        mHandler.removeCallbacks(r);
        if (mToast != null)
            mToast.setText(text);
        else
            mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
        mHandler.postDelayed(r, duration);
        mToast.show();
    }

    public static void showToast(Context mContext, int resId, int duration) {
        showToast(mContext, mContext.getResources().getString(resId), duration);
    }

    //预防点次数过多，一次一次的出现
    public static Toast toast;
    public static void showToast(Context context, String content) {
        if (toast == null) {
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();

    }

}
/*显示Toast代码：CustomToast.showToast(getBaseContext(), "提示信息", 1000);
        因为一般提示信息都是放在strings.xml中，所以为了方便使用，又写了个方法：
public static void showToast(Context mContext, int resId, int duration) {
        showToast(mContext, mContext.getResources().getString(resId), duration);
        }*/
