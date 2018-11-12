package com.mugua.enterprise.activity.home;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mugua.enterprise.BeasActivity;
import com.mugua.enterprise.R;
import com.mugua.enterprise.activity.me.CardActivity;
import com.mugua.enterprise.activity.me.EnterpriseCertificationActivity;
import com.mugua.enterprise.bean.BrokenListBean;
import com.mugua.enterprise.login.LoginActivity;
import com.mugua.enterprise.util.Canonical;
import com.mugua.enterprise.util.CardDialog;
import com.mugua.enterprise.util.Constant;
import com.mugua.enterprise.util.MyTextViewgjz;
import com.mugua.enterprise.util.Okhttp;
import com.mugua.enterprise.util.httppost.IRequestCallBack;
import com.zhy.autolayout.utils.AutoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lenovo on 2017/12/7.
 */

public class BrokenListActivity extends BeasActivity implements View.OnClickListener{
    public static BrokenListActivity instance = null;
    @BindView( R.id.tv_back )
    public TextView tv_back ;

    @BindView( R.id.tv_seach )
    public EditText tv_seach ;

    @BindView( R.id.tv_mp )
    public TextView tv_mp ;

    @BindView( R.id.tv_wu )
    public TextView tv_wu ;

    @BindView( R.id.ll_mp )
    public LinearLayout ll_mp ;

    @BindView( R.id.searchdetails_list )
    public ListView searchdetails_list ;
    private String keyword = "";

    private MyAdapterBroken myadapterbroken;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brokenlist);
        instance = this;
        //绑定fragment
        ButterKnife.bind(this);

        myadapterbroken = new MyAdapterBroken(this);
        searchdetails_list.setAdapter(myadapterbroken);
        searchdetails_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(BrokenListActivity.this, BrokenParticularsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", datas.get(i).getCompany());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        tv_seach.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    // 先隐藏键盘
                    ((InputMethodManager)tv_seach.getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow
                            (getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                    keyword = tv_seach.getText().toString();

                    if(!keyword.equals("")){
                        Data1();
                    }
                    return true;
                }
                return false;
            }
        });

        new Thread( new MyRunnable()).start();
        tv_back.setOnClickListener(this);
        tv_mp.setOnClickListener(this);
    }

    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            Data();
        }
    }

    private List<BrokenListBean> datas=new ArrayList<>();
    private void Data()
    {
        try {
            Okhttp.doGet(this, Constant.URL_brokenlist, new IRequestCallBack() {
                @Override
                public void onRequestBack(String text) {
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(text);
                        for (int i = 0; i < jsonArray.length();i++)
                        {
                            JSONObject temp = (JSONObject) jsonArray.get(i);
                            Gson gson = new Gson();
                            BrokenListBean hitokotoBean = gson.fromJson(temp.toString(),BrokenListBean.class);
                            datas.add(hitokotoBean);
                        }
                        myadapterbroken.setdata(datas);
                        myadapterbroken.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void Data1()
    {
        datas.clear();
        try {
            Okhttp.doGet(this, Constant.URL_searchList+"?keyword="+keyword, new IRequestCallBack() {
                @Override
                public void onRequestBack(String text) {
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(text);
                        for (int i = 0; i < jsonArray.length();i++)
                        {
                            JSONObject temp = (JSONObject) jsonArray.get(i);
                            Gson gson = new Gson();
                            BrokenListBean hitokotoBean = gson.fromJson(temp.toString(),BrokenListBean.class);
                            datas.add(hitokotoBean);
                        }
                        if(datas.size() == 0)
                            tv_wu.setVisibility(View.VISIBLE);
                        else tv_wu.setVisibility(View.GONE);
                        myadapterbroken.setdatagjz(datas);
                        myadapterbroken.notifyDataSetChanged();
                        ll_mp.setVisibility(View.VISIBLE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_mp:
                if(Constant.user != null)
                {
                    if(Constant.user.getIsCentification().equals("0") || Constant.user.getIsCentification().equals("2"))
                        startActivity(new Intent(BrokenListActivity.this, EnterpriseCertificationActivity.class));
                    else if(Constant.user.getIsCentification().equals("1"))
                    {
                        if(Constant.user.getExts().equals("0"))//未创建名片
                        {
                            Intent intent = new Intent(BrokenListActivity.this, CardActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("id", Constant.user.getId());
                            bundle.putString("kind", "0");
                            bundle.putString("title", "创建名片");
                            bundle.putString("btn", "创建名片");
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                        else {
                            Intent intent = new Intent(BrokenListActivity.this, CardActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("id", Constant.user.getId());
                            bundle.putString("kind", "1");
                            bundle.putString("title", "修改名片");
                            bundle.putString("btn", "确认修改");
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }
                    else if(Constant.user.getIsCentification().equals("3"))
                    {
                        finish1();
                    }
                }
                else {
                    startActivity(new Intent(BrokenListActivity.this, LoginActivity.class));
                }
                break;
        }
    }

    private void finish1()
    {
        //弹出提示框
        new CardDialog(this, R.style.dialog, R.layout.wait_card,0,"",false,new CardDialog.OnCloseListener() {
            @Override
            public void onClick(final Dialog dialog, View view) {
                view.findViewById(R.id.img_gb).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                view.findViewById(R.id.tv_bg).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        }).show();
    }

    public class MyAdapterBroken extends BaseAdapter {
        private List<BrokenListBean> datas;
        private Context context;
        public MyAdapterBroken(Context context) {
            this.context = context;
        }

        private void setdata(List<BrokenListBean> datas)
        {
            this.datas = datas;
        }

        private void setdatagjz(List<BrokenListBean> datas)
        {
            this.datas = datas;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            if(datas == null) return 0;
           else return datas.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolderBroken holder = null;
            if (convertView == null) {
                holder = new ViewHolderBroken();
                convertView = LayoutInflater.from(context).inflate(R.layout.searchdetails_list_brokenitem, parent, false);
                holder.tv_name = (MyTextViewgjz) convertView.findViewById(R.id.tv_name);
                holder.view_1 = (View) convertView.findViewById(R.id.view_1);
                convertView.setTag(holder);
                AutoUtils.autoSize(convertView);
            } else
                holder = (ViewHolderBroken) convertView.getTag();

            if(!keyword.equals(""))
                holder.tv_name.setSpecifiedTextsColor(datas.get(position).getCompany(), keyword, Color.parseColor("#ff962c"));
            else holder.tv_name.setText(datas.get(position).getCompany());

            if(position == datas.size()-1)
                holder.view_1.setVisibility(View.GONE);
            else
                holder.view_1.setVisibility(View.VISIBLE);

            return convertView;
        }
    }

    public class ViewHolderBroken{
        private MyTextViewgjz tv_name;
        private View view_1;
    }
}
