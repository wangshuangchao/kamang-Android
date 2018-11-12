package com.mugua.enterprise.util;

/**
 * Created by Administrator on 2017/5/22.
 */

import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;
import android.widget.TextView;

import com.mugua.enterprise.R;

/**
 * Created by Jackie on 2015/11/30.
 */
public class CountDownTimerUtils1 extends CountDownTimer {

    public CountDownTimerUtils1(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    @Override
    public void onTick(long millisUntilFinished) {
        if((millisUntilFinished / 1000) > 0)
            Constant.open = true;
    }

    @Override
    public void onFinish() {
        Constant.open = false;
    }
}
