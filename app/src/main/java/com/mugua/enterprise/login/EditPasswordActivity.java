package com.mugua.enterprise.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mugua.enterprise.BeasActivity;
import com.mugua.enterprise.R;
import com.mugua.enterprise.util.Constant;
import com.mugua.enterprise.util.Okhttp;
import com.mugua.enterprise.util.httppost.IRequestCallBack;
import com.mugua.enterprise.util.toast.CustomToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.FormBody;

/**
 * Created by Lenovo on 2017/12/6.
 */

public class EditPasswordActivity extends BeasActivity implements View.OnClickListener{
    public static EditPasswordActivity instance = null;
    @BindView( R.id.rl_back )
    public RelativeLayout rl_back ;

    @BindView( R.id.btn_btn )
    public Button btn_btn ;

    @BindView( R.id.e_zh )
    public EditText e_zh ;

    @BindView( R.id.e_mima )
    public EditText e_mima ;

    private String yzm,zh;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editpassword);
        instance = this;
        //绑定fragment
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        yzm = bundle.getString("yzm");
        zh = bundle.getString("userPhone");
        rl_back.setOnClickListener(this);
        btn_btn.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_btn:
                if(!e_zh.getText().toString().equals(e_mima.getText().toString()))
                {
                    CustomToast.showToast(getApplicationContext(),"密码不一致",1000);
                    return;
                }
                FormBody formBody = new FormBody.Builder()
                        .add("userPhone", zh)
                        .add("newPwd", e_mima.getText().toString())
                        .add("code", yzm)
                        .build();
                try {
                    Okhttp.doPost(this, Constant.URL_forgetPassword, formBody, new IRequestCallBack() {
                        @Override
                        public void onRequestBack(String text) {
                            JSONObject jsonobject = null;
                            try {
                                jsonobject = new JSONObject(text);
                                if (jsonobject.getString("code").equals("1000"))
                                {
                                    CustomToast.showToast(getApplicationContext(),jsonobject.getString("msg"),1000);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                RetrievePasswordActivity.instance.finish();
                finish();
                break;
        }
    }
}
