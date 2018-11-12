package com.mugua.enterprise.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.mugua.enterprise.BeasActivity;
import com.mugua.enterprise.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lenovo on 2017/12/5.
 */

public class FundsActivity extends BeasActivity implements View.OnClickListener {
    public static FundsActivity instance = null;
    @BindView( R.id.rl_back )
    public RelativeLayout rl_back ;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_funds);
        instance = this;
        //绑定fragment
        ButterKnife.bind(this);

        rl_back.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.rl_back:
                finish();
                break;
        }
    }
}
