package com.mugua.enterprise.activity.me;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mugua.enterprise.BeasActivity;
import com.mugua.enterprise.R;
import com.mugua.enterprise.util.Canonical;
import com.mugua.enterprise.util.Constant;
import com.mugua.enterprise.util.Okhttp;
import com.mugua.enterprise.util.httppost.BaseModel;
import com.mugua.enterprise.util.httppost.IRequestCallBack;
import com.mugua.enterprise.util.toast.CustomToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import com.mugua.jiguang.chat.activity.FinishRegisterActivity;
import com.mugua.jiguang.chat.utils.SharePreferenceManager;

/**
 * Created by Lenovo on 2017/12/5.
 */

public class EditActivity extends BeasActivity implements View.OnClickListener {
    public static EditActivity instance = null;
    @BindView( R.id.rl_back )
    public RelativeLayout rl_back ;

    @BindView( R.id.e_content )
    public EditText e_content ;

    @BindView( R.id.tv_shu )
    public TextView tv_shu ;

    @BindView( R.id.rl_preservation )
    public RelativeLayout rl_preservation ;

    @BindView( R.id.tv_title )
    public TextView tv_title ;

    private int shu;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        instance = this;
        //绑定fragment
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        tv_title.setText(bundle.getString("tv_title"));
        e_content.setHint(bundle.getString("e_content"));
        shu = bundle.getInt("shu");
        tv_shu.setText(0+"/"+shu);
        rl_preservation.setOnClickListener(this);
        rl_back.setOnClickListener(this);
        e_content.setFilters(new InputFilter[]{new InputFilter.LengthFilter(shu)});
        e_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                Editable editable = e_content.getText();
                int len = editable.length();
                if(len > shu) tv_shu.setText(shu+"/"+shu);
                else tv_shu.setText(len+"/"+shu);
            }
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count,int after) {
            }
            @Override
            public void afterTextChanged(Editable edit) {
            }
        });
    }
    private String www1 = "";
    private String www2 = "";
    private boolean open;
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_preservation:
                if(e_content.getText().toString().equals(""))
                {
                    CustomToast.showToast(getApplicationContext(),"请输入内容",1000);
                    return;
                }
                if(tv_title.getText().toString().equals("企业名称"))
                {
                    open = false;
                    www1 ="turnName";
                    www2 = "id="+Constant.user.getId()+"&userName="+e_content.getText().toString();
                }
                else
                {
                    open = true;
                    www1 ="turnType";
                    www2 = "id="+Constant.user.getId()+"&companyType="+e_content.getText().toString();
                }

                if(www1.equals(""))
                    return;
                else if(www2.equals(""))
                    return;

                if(tv_title.getText().toString().equals("企业名称"))
                {
                    BaseModel baseModel = new BaseModel(Constant.URL_turnLogo+www1);
                    baseModel.send(www2, new IRequestCallBack() {
                        @Override
                        public void onRequestBack(String text) {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(text);
                                if(jsonObject.getString("code").equals("1000"))
                                {
                                    CustomToast.showToast(getApplicationContext(),jsonObject.getString("msg"),1000);

                                    String nickName = e_content.getText().toString().trim();
                                    UserInfo myUserInfo = JMessageClient.getMyInfo();
                                    if (myUserInfo != null) {
                                        myUserInfo.setNickname(nickName);
                                    }
                                    //注册时候更新昵称
                                    JMessageClient.updateMyInfo(UserInfo.Field.nickname,myUserInfo , new BasicCallback() {
                                        @Override
                                        public void gotResult(final int status, String desc) {
                                            //更新跳转标志
                                            SharePreferenceManager.setCachedFixProfileFlag(false);
                                        }
                                    });

                                    if(open == false) EditDataActivity.instance.setdata(e_content.getText().toString(),"");
                                    else EditDataActivity.instance.setdata("",e_content.getText().toString());
                                    finish();
                                }else CustomToast.showToast(getApplicationContext(),jsonObject.getString("msg"),1000);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }else {
                    BaseModel baseModel = new BaseModel(Constant.URL_turnLogo+www1);
                    baseModel.send(www2, new IRequestCallBack() {
                        @Override
                        public void onRequestBack(String text) {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(text);
                                if(jsonObject.getString("code").equals("1000"))
                                {
                                    CustomToast.showToast(getApplicationContext(),jsonObject.getString("msg"),1000);
                                    if(open == false) EditDataActivity.instance.setdata(e_content.getText().toString(),"");
                                    else EditDataActivity.instance.setdata("",e_content.getText().toString());
                                    finish();
                                }else CustomToast.showToast(getApplicationContext(),jsonObject.getString("msg"),1000);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                break;
        }
    }
}
