package com.mugua.enterprise.Mall;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mugua.enterprise.BeasActivity;
import com.mugua.enterprise.R;
import com.mugua.enterprise.bean.AddressBean;
import com.mugua.enterprise.util.Constant;
import com.mugua.enterprise.util.MyItemClickListener;
import com.mugua.enterprise.util.Okhttp;
import com.mugua.enterprise.util.httppost.IRequestCallBack;
import com.mugua.enterprise.util.toast.CustomToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.FormBody;

/**
 * Created by Lenovo on 2018/3/27.
 */

public class AddressActivity extends BeasActivity implements View.OnClickListener,MyItemClickListener {
    public static AddressActivity instance = null;
    @BindView( R.id.rl_back )
    public RelativeLayout rl_back ;

    @BindView( R.id.rl_newdz )
    public RelativeLayout rl_newdz ;

    @BindView( R.id.rl_gl_delete )
    public RelativeLayout rl_gl_delete ;

    @BindView( R.id.tv_gl_delete )
    public TextView tv_gl_delete ;

    @BindView( R.id.btn_delete )
    public TextView btn_delete ;

    @BindView( R.id.my_recyclerview )
    public RecyclerView my_recyclerview ;

    private MyAdapter mAdapter;
    private boolean open = true;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        instance = this;
        //绑定fragment
        ButterKnife.bind(this);

        new Thread( new MyRunnable()).start();

        mAdapter = new MyAdapter(this);
        my_recyclerview.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        my_recyclerview.setAdapter(mAdapter);

        rl_back.setOnClickListener(this);
        rl_newdz.setOnClickListener(this);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);
        rl_gl_delete.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
    }

    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            Data();
        }
    }

    public List<AddressBean> datas = new ArrayList<>();
    public void Data()
    {
        String www = "?uid="+Constant.user.getId();
        try {
            Okhttp.doGet(this, Constant.URL_getAddr+www, new IRequestCallBack() {
                @Override
                public void onRequestBack(String text) {
                    JSONObject jsonobject = null;
                    try {
                        jsonobject = new JSONObject(text);
                        datas.clear();
                        if(jsonobject.getString("code").equals("1000"))
                        {
                            JSONArray jsonObject = jsonobject.getJSONArray("data");
                            for (int i = 0; i < jsonObject.length();i++)
                            {
                                JSONObject temp = (JSONObject) jsonObject.get(i);
                                Gson gson = new Gson();
                                AddressBean ylShangPinBean = gson.fromJson(temp.toString(),AddressBean.class);
                                datas.add(ylShangPinBean);
                            }
                            for (int i = 0; i < datas.size(); i++) {
                                listitem.add(false);
                            }
                            //在adapter中添加数据
                            mAdapter.setdate(datas);
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

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_newdz:
                Intent intent = new Intent(AddressActivity.this, NewAddressActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("open", false);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.btn_delete:
                for (int i = 0;i < listitem.size();i++)
                {
                    if(listitem.get(i) == true) {
                        Data1(i);
                    }
                }
                break;
            case R.id.rl_gl_delete:
                if(open == true)
                {
                    open = false;
                    btn_delete.setVisibility(View.VISIBLE);
                    tv_gl_delete.setText("取消");
                }else {
                    open = true;
                    btn_delete.setVisibility(View.GONE);
                    tv_gl_delete.setText("管理");
                }
                mAdapter.setdate(datas);
                break;
        }
    }

    @Override
    public void onItemClick(View view, int postion) {
        if(!open)
        {
            if(view.isSelected())
            {
                listitem.remove(postion);
                listitem.add(postion,false);
            }else {
                listitem.clear();
                for (int i = 0;i < datas.size();i++)
                {
                    listitem.add(false);
                }
                listitem.remove(postion);
                listitem.add(postion,true);
            }
            mAdapter.notifyDataSetChanged();
        }else {
            Data(postion);
        }
    }

    public void Data1(final int postion)
    {
        FormBody formBody = new FormBody.Builder()
                .add("id", datas.get(postion).getId())
                .build();
        try {
            Okhttp.doPost(this, Constant.URL_deleteAddr, formBody, new IRequestCallBack() {
                @Override
                public void onRequestBack(String text) {
                    JSONObject jsonobject = null;
                    try {
                        jsonobject = new JSONObject(text);
                        if (jsonobject.getString("code").equals("1000"))
                        {
                            datas.remove(postion);
                            listitem.remove(postion);
                            mAdapter.setdate(datas);
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

    public void Data(final int postion)
    {
        FormBody formBody = new FormBody.Builder()
                .add("id", datas.get(postion).getId())
                .add("uid", datas.get(postion).getUid())
                .build();
        try {
            Okhttp.doPost(this, Constant.URL_updateDefault, formBody, new IRequestCallBack() {
                @Override
                public void onRequestBack(String text) {
                    JSONObject jsonobject = null;
                    try {
                        jsonobject = new JSONObject(text);
                        if (jsonobject.getString("code").equals("1000"))
                        {
                            Order_Details.instance.tdzdata(datas.get(postion).getName(),
                                    datas.get(postion).getPhone(),
                                    datas.get(postion).getArea()+" "+datas.get(postion).getAddress(),datas.get(postion).getPostcode());
                            finish();
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

    @Override
    public void onItemLongClick(View view, int postion) {
    }

    private List<Boolean> listitem = new ArrayList<>();
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>
    {
        private Context context;
        private List<AddressBean> mList = new ArrayList<>();
        private MyItemClickListener mLongClickListener;
        private MyItemClickListener mItemClickListener;

        private MyAdapter(Context context)
        {
            this.context = context;
        }

        public void setdate(List<AddressBean> mList) {
            this.mList = mList;
            notifyDataSetChanged();
        }

        public void setOnItemClickListener(MyItemClickListener listener){
            this.mItemClickListener = listener;
        }

        public void setOnItemLongClickListener(MyItemClickListener listener){
            this.mLongClickListener = listener;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            MyViewHolder holder = new MyRecycleViewHolderOne(LayoutInflater.from(context).inflate(R.layout.address_recycler_item, parent, false)
                        ,mItemClickListener,mLongClickListener);

            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position)
        {
            for (int i = 0;i < mList.size();i++)
            {
                holder.checkBox.setSelected(false);
            }
            holder.bindHolder(mList.get(position),position);
        }

        @Override
        public int getItemCount()
        {
            return mList.size();
        }

        public class MyRecycleViewHolderOne extends MyViewHolder {

            public MyRecycleViewHolderOne(View itemView, MyItemClickListener listener, MyItemClickListener longClickListener) {
                super(itemView, listener, longClickListener);
                btn_bj = (RelativeLayout)itemView.findViewById(R.id.btn_bj);
                img_mr = (ImageView)itemView.findViewById(R.id.img_mr);
                ll_bj = (LinearLayout)itemView.findViewById(R.id.ll_bj);
                checkBox = (ImageView)itemView.findViewById(R.id.checkBox);
                tv_name = (TextView)itemView.findViewById(R.id.tv_name);
                tv_phone = (TextView)itemView.findViewById(R.id.tv_phone);
                tv_jianjie = (TextView)itemView.findViewById(R.id.tv_jianjie);
            }
            @Override
            public void bindHolder(final AddressBean data, final int position) {
                if(!data.getIsDefault().equals("1"))
                    img_mr.setVisibility(View.GONE);
                else img_mr.setVisibility(View.VISIBLE);

                if(mList.size() == 1)
                    img_mr.setVisibility(View.VISIBLE);

                if(!open)
                {
                    checkBox.setVisibility(View.VISIBLE);
                    ll_bj.setVisibility(View.GONE);
                }else {
                    ll_bj.setVisibility(View.VISIBLE);
                    checkBox.setVisibility(View.GONE);
                }

                btn_bj.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, NewAddressActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("list", data);
                        bundle.putBoolean("open", true);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

                checkBox.setSelected(listitem.get(position));
                tv_name.setText(data.getName());
                tv_phone.setText(data.getPhone());
                tv_jianjie.setText(data.getArea()+" "+data.getAddress());
            }
        }

        public abstract class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener
        {
            public RelativeLayout btn_bj;
            public ImageView img_mr;
            public ImageView checkBox;
            public LinearLayout ll_bj;
            public TextView tv_name,tv_phone,tv_jianjie;
            private MyItemClickListener mListener;
            private MyItemClickListener mLongClickListener;
            public MyViewHolder(View view,MyItemClickListener listener,MyItemClickListener longClickListener)
            {
                super(view);
                this.mListener = listener;
                this.mLongClickListener = longClickListener;
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            /**
             * 点击监听
             */
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onItemClick(v,getPosition());
                }
            }

            /**
             * 长按监听
             */
            @Override
            public boolean onLongClick(View v) {
                if(mLongClickListener != null){
                    mLongClickListener.onItemLongClick(v, getPosition());
                }
                return true;
            }
            public abstract void bindHolder(AddressBean data, int position);
        }
    }
}
