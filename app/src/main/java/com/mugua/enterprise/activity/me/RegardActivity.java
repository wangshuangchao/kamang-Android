package com.mugua.enterprise.activity.me;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mugua.enterprise.R;
import com.mugua.enterprise.activity.WebActivity;
import com.mugua.enterprise.util.Constant;
import com.zhy.autolayout.AutoLayoutActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lenovo on 2017/11/22.
 */

public class RegardActivity extends AutoLayoutActivity implements View.OnClickListener{
    public static RegardActivity instance = null;
    @BindView( R.id.rl_back )
    public RelativeLayout rl_back ;

    @BindView( R.id.btn_yinsi )
    public Button btn_yinsi ;

    @BindView( R.id.tv_edition )
    public TextView tv_edition ;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regard);
        instance = this;
        //绑定fragment
        ButterKnife.bind(this);
        PackageManager manager = getPackageManager();
        try {
            PackageInfo packageInfo = manager.getPackageInfo(getPackageName(), 0);
            tv_edition.setText("版本号：V"+packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        rl_back.setOnClickListener(this);
        btn_yinsi.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_yinsi:
                Intent intent = new Intent();
                intent.putExtra("title", "隐私策略");
                intent.putExtra("url", Constant.URL_goPrivacy);
                intent.setClass(RegardActivity.this, WebActivity.class);
                startActivity(intent);
                break;
        }
    }
}
