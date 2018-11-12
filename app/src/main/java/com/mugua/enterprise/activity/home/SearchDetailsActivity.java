package com.mugua.enterprise.activity.home;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.log.LocalUserInfo;
import com.google.gson.Gson;
import com.mugua.enterprise.BeasActivity;
import com.mugua.enterprise.R;
import com.mugua.enterprise.activity.Showcasing1Activity;
import com.mugua.enterprise.activity.ShowcasingActivity;
import com.mugua.enterprise.activity.me.CardActivity;
import com.mugua.enterprise.activity.me.EnterpriseCertificationActivity;
import com.mugua.enterprise.bean.CheckBean;
import com.mugua.enterprise.bean.LoginBean;
import com.mugua.enterprise.fragment.HomeFragment;
import com.mugua.enterprise.login.LoginActivity;
import com.mugua.enterprise.util.Canonical;
import com.mugua.enterprise.util.CardDialog;
import com.mugua.enterprise.util.Constant;
import com.mugua.enterprise.util.MyTextViewgjz;
import com.mugua.enterprise.util.Okhttp;
import com.mugua.enterprise.util.TuActivity;
import com.mugua.enterprise.util.httppost.IRequestCallBack;
import com.mugua.enterprise.util.toast.CustomToast;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhy.autolayout.utils.AutoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lenovo on 2017/11/21.
 */

public class SearchDetailsActivity extends BeasActivity implements View.OnClickListener{
    public static SearchDetailsActivity instance = null;

    @BindView( R.id.tv_seach )
    public TextView tv_seach ;

    @BindView( R.id.tv_tiao )
    public MyTextViewgjz tv_tiao ;

    @BindView( R.id.ll_seach )
    public LinearLayout ll_seach ;

    @BindView( R.id.tv_back )
    public TextView tv_back ;

    @BindView( R.id.tv_mp )
    public TextView tv_mp ;

    @BindView( R.id.searchdetails_list )
    public ListView searchdetails_list ;
    private MyAdapter myadapter;
    private String gjz;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchdetails);
        instance = this;
        //绑定fragment
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        gjz = bundle.getString("keyword");
        tv_seach.setText(gjz);

        ll_seach.setOnClickListener(this);
        tv_back.setOnClickListener(this);
        tv_mp.setOnClickListener(this);
        searchdetails_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(datas.get(position).getType().equals("1"))
                {
                    Intent intent = new Intent(SearchDetailsActivity.this, ShowcasingActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("id", datas.get(position).getId());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(SearchDetailsActivity.this, Showcasing1Activity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("id", datas.get(position).getId());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            }
        });

        myadapter = new MyAdapter(this);
        searchdetails_list.setAdapter(myadapter);

        new Thread( new MyRunnable()).start();
    }

    List<CheckBean> datas=new ArrayList<>();
    private void data()
    {
        String www = "?keyword="+tv_seach.getText().toString()+ "&pageIndex=1";
        try {
            Okhttp.doGet(this, Constant.URL_search+www, new IRequestCallBack() {
                @Override
                public void onRequestBack(String text) {
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(text);
                        for (int i = 0; i < jsonArray.length();i++)
                        {
                            JSONObject temp = (JSONObject) jsonArray.get(i);
                            Gson gson = new Gson();
                            CheckBean checkBean = gson.fromJson(temp.toString(),CheckBean.class);
                            datas.add(checkBean);
                        }
                        String ww = "“"+datas.size()+"条”";
                        String www = "搜索到"+ww+"相关数据";
                        tv_tiao.setSpecifiedTextsColor(www, ww, Color.parseColor("#ff962c"));
                        //在adapter中添加数据
                        myadapter.setData(datas);
                        myadapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            data();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.ll_seach:
                startActivity(new Intent(SearchDetailsActivity.this,SearchActivity.class));
                finish();
                break;
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_mp:
                if(Constant.user != null)
                {
                    if(Constant.user.getIsCentification().equals("0") || Constant.user.getIsCentification().equals("2"))
                        startActivity(new Intent(SearchDetailsActivity.this, EnterpriseCertificationActivity.class));
                    else if(Constant.user.getIsCentification().equals("1"))
                    {
                        if(Constant.user.getExts().equals("0"))//未创建名片
                        {
                            Intent intent = new Intent(SearchDetailsActivity.this, CardActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("id", Constant.user.getId());
                            bundle.putString("kind", "0");
                            bundle.putString("title", "创建名片");
                            bundle.putString("btn", "创建名片");
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                        else {
                            Intent intent = new Intent(SearchDetailsActivity.this, CardActivity.class);
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
                    startActivity(new Intent(SearchDetailsActivity.this, LoginActivity.class));
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

    public class MyAdapter extends BaseAdapter {

        private Context context;
        private List<CheckBean> datas;
        public MyAdapter(Context context) {
            this.context = context;
        }

        public void setData(List<CheckBean> datas) {
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

            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.searchdetails_list_item, parent, false);
                holder.search_img = (ImageView) convertView.findViewById(R.id.search_img);
                holder.tv_name = (MyTextViewgjz) convertView.findViewById(R.id.tv_name);
                holder.tv_Legal = (TextView) convertView.findViewById(R.id.tv_Legal);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                holder.tv_state = (TextView) convertView.findViewById(R.id.tv_state);
                convertView.setTag(holder);
                AutoUtils.autoSize(convertView);
            } else
                holder = (ViewHolder) convertView.getTag();

            ImageLoader.getInstance().displayImage(datas.get(position).getPhotoUrl(), holder.search_img, TuActivity.tu(R.drawable.zwt));

            String name = datas.get(position).getCompany();
            holder.tv_name.setSpecifiedTextsColor(name, gjz, Color.parseColor("#ff962c"));

            String Legal = datas.get(position).getLegalPersonName();
            holder.tv_Legal.setText(Legal);

            String time = datas.get(position).getCreateTimes();
            holder.tv_time.setText(time);

            if(datas.get(position).getType().equals("1"))
            {
                String state = datas.get(position).getRegisteredAssets();
                holder.tv_state.setText(state);
            }else {
                String state = datas.get(position).getStatus();
                holder.tv_state.setText(state);
            }

            return convertView;
        }
    }

    public class ViewHolder{
        private MyTextViewgjz tv_name;
        private TextView tv_Legal;
        private TextView tv_time;
        private TextView tv_state;
        private ImageView search_img;
    }
}
