package com.mugua.enterprise.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.mugua.enterprise.BeasActivity;
import com.mugua.enterprise.R;
import com.mugua.enterprise.util.Constant;
import com.mugua.enterprise.util.CountDownTimerUtils;
import com.mugua.enterprise.util.Okhttp;
import com.mugua.enterprise.util.httppost.IRequestCallBack;
import com.mugua.enterprise.util.toast.CustomToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lenovo on 2017/12/6.
 */

public class RetrievePasswordActivity extends BeasActivity implements View.OnClickListener{
    public static RetrievePasswordActivity instance = null;

    @BindView( R.id.rl_back )
    public RelativeLayout rl_back ;

    @BindView( R.id.btn_yzm )
    public Button btn_yzm ;

    @BindView( R.id.btn_longin )
    public Button btn_longin ;

    @BindView( R.id.e_zh )
    public EditText e_zh ;

    @BindView( R.id.e_yzm )
    public EditText e_yzm ;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrievepassword);
        instance = this;
        //绑定fragment
        ButterKnife.bind(this);

        rl_back.setOnClickListener(this);
        btn_yzm.setOnClickListener(this);
        btn_longin.setOnClickListener(this);
    }
    private String yanzhengma,userPhone;
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_yzm:
                String www = "?phone="+e_zh.getText().toString()+"&type=1";
                try {
                    Okhttp.doGet(this, Constant.URL_yzm+www, new IRequestCallBack() {
                        @Override
                        public void onRequestBack(String text) {
                            JSONObject jsonobject = null;
                            try {
                                jsonobject = new JSONObject(text);
                                if (jsonobject.getString("code").equals("1000"))
                                {
                                    CustomToast.showToast(getApplicationContext(),jsonobject.getString("msg"),1000);
                                    yanzhengma = jsonobject.getString("data");
                                    userPhone = e_zh.getText().toString();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(btn_yzm,btn_yzm, 60000, 1000);
                mCountDownTimerUtils.start();
                break;
            case R.id.btn_longin:
                if(e_yzm.getText().toString().equals(yanzhengma) && !e_zh.getText().toString().equals(""))
                {
                    Intent intent = new Intent(RetrievePasswordActivity.this, EditPasswordActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("yzm", yanzhengma);
                    bundle.putString("userPhone", userPhone);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
        }
    }
}
