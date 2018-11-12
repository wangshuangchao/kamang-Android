package com.mugua.enterprise.activity.me;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.mugua.enterprise.BeasActivity;
import com.mugua.enterprise.R;
import com.mugua.enterprise.activity.HuoDongActivity;
import com.mugua.enterprise.fragment.HomeFragment;
import com.mugua.enterprise.util.Constant;
import com.mugua.enterprise.util.toast.CustomToast;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lenovo on 2018/4/4.
 */

public class MoreActivity extends BeasActivity implements View.OnClickListener{
    public static MoreActivity instance = null;
    @BindView( R.id.rl_back )
    public RelativeLayout rl_back ;

    @BindView( R.id.rl_fbw )
    public RelativeLayout rl_fbw ;

    @BindView( R.id.rl_hd )
    public RelativeLayout rl_hd ;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        instance = this;
        //绑定fragment
        ButterKnife.bind(this);

        rl_back.setOnClickListener(this);
        rl_fbw.setOnClickListener(this);
        rl_hd.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_fbw:
                startActivity(new Intent(MoreActivity.this, MyreleaseActivity.class));
                finish();
                break;
            case R.id.rl_hd:
                if(HomeFragment.instance.homeBean.activity.getExists().equals("1"))
                {
                    Intent intent = new Intent();
                    intent.putExtra("title", "免费送名片");
                    intent.putExtra("url", HomeFragment.instance.homeBean.activity.getUrl());
                    intent.setClass(MoreActivity.this, HuoDongActivity.class);
                    startActivity(intent);
                }else CustomToast.showToast(getApplicationContext(),"暂无活动");
                break;
        }
    }
}
