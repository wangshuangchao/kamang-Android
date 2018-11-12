package com.mugua.enterprise.activity.me;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mugua.enterprise.BeasActivity;
import com.mugua.enterprise.R;
import com.mugua.enterprise.bean.AllorderBean;
import com.mugua.enterprise.util.Constant;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.FormBody;

/**
 * Created by Lenovo on 2017/12/18.
 */

public class AllOrderActivity extends BeasActivity implements View.OnClickListener {
    public static AllOrderActivity instance = null;
    @BindView( R.id.rl_back )
    public RelativeLayout rl_back ;

    @BindView( R.id.order_list )
    public ListView order_list ;
    private MylistAdapter mylistAdapter;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allorder);
        instance = this;
        //绑定fragment
        ButterKnife.bind(this);

        data();

        mylistAdapter = new MylistAdapter(this);
        order_list.setAdapter(mylistAdapter);

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

    public List<AllorderBean> datas = new ArrayList<>();
    public void data()
    {
        datas.clear();
        String www = "?userId="+ Constant.user.getId();
        try {
            Okhttp.doGet(this, Constant.URL_getAll+www, new IRequestCallBack() {
                @Override
                public void onRequestBack(String text) {
                    JSONObject jsonobject = null;
                    try {
                        jsonobject = new JSONObject(text);
                        if (jsonobject.getString("code").equals("1000"))
                        {
                            JSONObject jsonObject = jsonobject.getJSONObject("data");
                            JSONArray jsonArray = jsonObject.getJSONArray("orderVos");
                            for (int i = 0; i < jsonArray.length();i++)
                            {
                                JSONObject temp = (JSONObject) jsonArray.get(i);
                                Gson gson = new Gson();
                                AllorderBean ylShangPinBean = gson.fromJson(temp.toString(),AllorderBean.class);
                                datas.add(ylShangPinBean);
                            }
                            mylistAdapter.data1(datas);
                        }
                        else {
                            datas.clear();
                            mylistAdapter.data1(datas);
                            CustomToast.showToast(getApplicationContext(),"无订单",1000);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class MylistAdapter extends BaseAdapter {

        private Context context;
        public List<AllorderBean> datas = new ArrayList<>();
        public MylistAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return datas != null ? datas.size() : 0;
        }

        public void data1(List<AllorderBean> datas)
        {
            this.datas = datas;
            notifyDataSetChanged();
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
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.order_list_item, parent, false);
                holder.tv_ddbh = (TextView) convertView.findViewById(R.id.tv_ddbh);
                holder.tv_ddname = (TextView) convertView.findViewById(R.id.tv_ddname);
                holder.tv_ddjg = (TextView) convertView.findViewById(R.id.tv_ddjg);
                holder.tv_delete = (TextView) convertView.findViewById(R.id.tv_delete);
                holder.img_tutx = (ImageView) convertView.findViewById(R.id.img_tutx);
//                holder.view_x = (View) convertView.findViewById(R.id.view_x);
                convertView.setTag(holder);
                AutoUtils.autoSize(convertView);
            } else
                holder = (ViewHolder) convertView.getTag();

            holder.tv_ddbh.setText("订单编号："+datas.get(position).getId());
            holder.tv_ddname.setText(datas.get(position).getGoodsName());
            holder.tv_ddjg.setText("￥"+datas.get(position).getTotalFee());
            ImageLoader.getInstance().displayImage(datas.get(position).getGoodsPhoto(), holder.img_tutx, TuActivity.tu(R.drawable.zwt));

            holder.tv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FormBody formBody = new FormBody.Builder()
                            .add("id", datas.get(position).getId())
                            .build();
                    try {
                        Okhttp.doPost(context, Constant.URL_deleteOrder, formBody, new IRequestCallBack() {
                            @Override
                            public void onRequestBack(String text) {
                                JSONObject jsonobject = null;
                                try {
                                    jsonobject = new JSONObject(text);
                                    if (jsonobject.getString("code").equals("1000"))
                                    {
                                        data();
                                        CustomToast.showToast(getApplicationContext(),jsonobject.getString("msg"),1000);
                                    }else CustomToast.showToast(getApplicationContext(),jsonobject.getString("msg"),1000);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

//            if(position == datas.size())
//                holder.view_x.setVisibility(View.GONE);
//            else
//                holder.view_x.setVisibility(View.VISIBLE);
            return convertView;
        }
    }

    public class ViewHolder{
        private TextView tv_ddbh;
        private TextView tv_ddjg;
        private TextView tv_ddname;
        private ImageView img_tutx;
        private TextView tv_delete;
//        private View view_x;
    }
}
