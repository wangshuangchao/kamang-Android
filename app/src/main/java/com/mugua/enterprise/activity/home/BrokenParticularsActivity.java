package com.mugua.enterprise.activity.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mugua.enterprise.BeasActivity;
import com.mugua.enterprise.R;
import com.mugua.enterprise.bean.BrokenParticularsBean;
import com.mugua.enterprise.util.Constant;
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
 * Created by Lenovo on 2017/12/5.
 */

public class BrokenParticularsActivity extends BeasActivity implements View.OnClickListener{
    public static BrokenParticularsActivity instance = null;
    @BindView( R.id.rl_back )
    public RelativeLayout rl_back ;

    @BindView( R.id.my_scrollview )
    public ScrollView my_scrollview ;

    @BindView( R.id.tv_name )
    public TextView tv_name ;

    @BindView( R.id.my_list )
    public ListView my_list ;

    private MyAdapter myAdapter;
    private String gjz;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brokenparticulars);
        instance = this;
        //绑定fragment
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        gjz = bundle.getString("name");

        new Thread( new MyRunnable()).start();
        myAdapter = new MyAdapter(this);
        my_list.setAdapter(myAdapter);
        rl_back.setOnClickListener(this);
    }

    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            Data();
        }
    }

    List<BrokenParticularsBean> datas = new ArrayList<>();
    private void Data()
    {
        try {
            Okhttp.doGet(this, Constant.URL_getdetail+"?name="+gjz, new IRequestCallBack() {
                @Override
                public void onRequestBack(String text) {
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(text);
                        for (int i = 0; i < jsonArray.length();i++)
                        {
                            JSONObject temp = (JSONObject) jsonArray.get(i);
                            Gson gson = new Gson();
                            BrokenParticularsBean hitokotoBean = gson.fromJson(temp.toString(),BrokenParticularsBean.class);
                            datas.add(hitokotoBean);
                        }
                        tv_name.setText(datas.get(0).getCompany());
                        //在adapter中添加数据
                        myAdapter.setData(datas);
                        myAdapter.notifyDataSetChanged();
                        my_scrollview.smoothScrollTo(0, 0);
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
            case R.id.rl_back:
                finish();
                break;
        }
    }

    public class MyAdapter extends BaseAdapter {

        private Context context;
        private List<BrokenParticularsBean> itemdata;
        public MyAdapter(Context context) {
            this.context = context;
        }

        public void setData(List<BrokenParticularsBean> itemdata) {
            this.itemdata = itemdata;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            if(itemdata == null) return 0;
            else return itemdata.size();
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
                convertView = LayoutInflater.from(context).inflate(R.layout.brokenparticulars_list_item, parent, false);
                holder.tv_jllx = (TextView)convertView.findViewById(R.id.tv_jllx);
                holder.tv_zuzhi = (TextView)convertView.findViewById(R.id.tv_zuzhi);
                holder.tv_faren = (TextView)convertView.findViewById(R.id.tv_faren);
                holder.tv_shtydm = (TextView)convertView.findViewById(R.id.tv_shtydm);
                holder.tv_liantime = (TextView)convertView.findViewById(R.id.tv_liantime);
                holder.tv_zxbh = (TextView)convertView.findViewById(R.id.tv_zxbh);
                holder.tv_anhao = (TextView)convertView.findViewById(R.id.tv_anhao);
                holder.tv_zxfy = (TextView)convertView.findViewById(R.id.tv_zxfy);
                convertView.setTag(holder);
                AutoUtils.autoSize(convertView);
            } else
                holder = (ViewHolder) convertView.getTag();

            holder.tv_jllx.setText(itemdata.get(position).getRecordType());
            holder.tv_zuzhi.setText(itemdata.get(position).getOriganizationCode());
            holder.tv_faren.setText(itemdata.get(position).getOpername());
            holder.tv_shtydm.setText(itemdata.get(position).getCreditcode());
            holder.tv_liantime.setText(itemdata.get(position).getLineHour());
            holder.tv_zxbh.setText(itemdata.get(position).getLineNumber());
            holder.tv_anhao.setText(itemdata.get(position).getTableNumber());
            holder.tv_zxfy.setText(itemdata.get(position).getLineCourt());

            return convertView;
        }
    }

    public class ViewHolder{
        private TextView tv_jllx;
        private TextView tv_zuzhi;
        private TextView tv_faren;
        private TextView tv_shtydm;
        private TextView tv_liantime;
        private TextView tv_zxbh;
        private TextView tv_anhao;
        private TextView tv_zxfy;
    }
}
