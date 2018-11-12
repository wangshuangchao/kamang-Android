package com.mugua.enterprise.activity.me;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.log.LocalUserInfo;
import com.mugua.enterprise.R;
import com.mugua.enterprise.util.Canonical;
import com.mugua.enterprise.util.Constant;
import com.mugua.enterprise.util.httppost.BaseModel;
import com.mugua.enterprise.util.httppost.IRequestCallBack;
import com.mugua.enterprise.util.toast.CustomToast;
import com.zhy.autolayout.AutoLayoutActivity;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;

/**
 * Created by Lenovo on 2017/11/22.
 */

public class UserFeedbackActivity extends AutoLayoutActivity implements View.OnClickListener{
    public static UserFeedbackActivity instance = null;
    @BindView( R.id.rl_back )
    public RelativeLayout rl_back ;

    @BindView( R.id.rl_content )
    public RelativeLayout rl_content ;

    @BindView( R.id.e_name )
    public EditText e_name ;

    @BindView( R.id.e_content )
    public EditText e_content ;

    @BindView( R.id.btn_tijiao )
    public Button btn_tijiao ;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userfeedback);
        instance = this;
        //绑定fragment
        ButterKnife.bind(this);
        rl_back.setOnClickListener(this);
        rl_content.setOnClickListener(this);
        btn_tijiao.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_tijiao:
                if(e_name.getText().toString().equals(""))
                {
                    CustomToast.showToast(getApplicationContext(),"请输入标题");
                    return;
                }
                if(e_content.getText().toString().equals(""))
                {
                    CustomToast.showToast(getApplicationContext(),"请输入详细描述");
                    return;
                }
                String www = "?title="+e_name.getText().toString()+"&content="+ e_content.getText().toString()+
                        "&userid="+ Constant.user.getId()+"&userphone="+ Constant.user.getUserPhone();
                BaseModel baseModel = new BaseModel(Constant.URL_addFeedback);
                baseModel.send(www, new IRequestCallBack() {
                    @Override
                    public void onRequestBack(String text) {
                        JSONObject jsonobject = null;
                        try {
                            jsonobject = new JSONObject(text);
                            if (jsonobject.getString("code").equals("1000"))
                            {
                                CustomToast.showToast(getApplicationContext(), jsonobject.getString("msg"), 1000);
                                finish();
                            }
                            else {
                                CustomToast.showToast(getApplicationContext(), jsonobject.getString("msg"), 1000);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_content:
                Canonical.showSoftInputFromWindow(this,e_content);
                break;
        }
    }
}
