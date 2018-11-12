package com.mugua.enterprise.banben;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mugua.enterprise.MainActivity;

/**
 * Created by Lenovo on 2018/1/17.
 */

public class NotificationBroadcastReceiver extends BroadcastReceiver {
    public static final String TYPE = "type"; //这个type是为了Notification更新信息的，这个不明白的朋友可以去搜搜，很多

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        int type = intent.getIntExtra(TYPE, -1);

        if (type != -1) {
            MainActivity.instance.stap();
        }

        if (action.equals("stop")) {
            //处理滑动清除和点击删除事件
            MainActivity.instance.stap();
        }
    }
}
