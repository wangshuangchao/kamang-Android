package com.mugua.enterprise.activity.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.mugua.enterprise.BeasActivity;
import com.mugua.enterprise.R;
import com.mugua.enterprise.bean.MyreleaseBean;
import com.mugua.enterprise.util.Constant;
import com.mugua.enterprise.util.GlideRoundTransform;
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
 * Created by Lenovo on 2018/4/4.
 */

public class MyreleaseActivity extends BeasActivity implements View.OnClickListener {
    public static MyreleaseActivity instance = null;
    @BindView( R.id.rl_back )
    public RelativeLayout rl_back ;

    @BindView( R.id.my_recyclerview )
    public RecyclerView my_recyclerview ;

    private MyAdapter mAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myrelease);
        instance = this;
        //绑定fragment
        ButterKnife.bind(this);

        data();

        mAdapter = new MyAdapter(this,datas);
        my_recyclerview.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        my_recyclerview.setAdapter(mAdapter);
        rl_back.setOnClickListener(this);
    }

    public List<MyreleaseBean> datas = new ArrayList<>();
    public void data()
    {
        datas.clear();
        String www = "?merchant="+ Constant.user.getId();
        try {
            Okhttp.doGet(this, Constant.URL_getByUid+www, new IRequestCallBack() {
                @Override
                public void onRequestBack(String text) {
                    JSONObject jsonobject = null;
                    try {
                        jsonobject = new JSONObject(text);
                        if (jsonobject.getString("code").equals("1000"))
                        {
                            JSONObject jsonObject = jsonobject.getJSONObject("data");
                            JSONArray jsonArray = jsonObject.getJSONArray("list");
                            for (int i = 0; i < jsonArray.length();i++)
                            {
                                JSONObject temp = (JSONObject) jsonArray.get(i);
                                Gson gson = new Gson();
                                MyreleaseBean myreleaseBean = gson.fromJson(temp.toString(),MyreleaseBean.class);
                                datas.add(myreleaseBean);
                            }
                            mAdapter.setdate(datas);
                        }
                        else
                        {
                            datas.clear();
                            mAdapter.setdate(datas);
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

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>
    {
        private Context context;
        private List<MyreleaseBean> mList;
        private MyAdapter(Context context,List<MyreleaseBean> model)
        {
            this.context = context;
            this.mList = model;
        }

        public void setdate(List<MyreleaseBean> model) {
            this.mList = model;
            notifyDataSetChanged();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.release_recy_item, parent, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position)
        {
            Glide.with(context)
                    .load(mList.get(position).getPhoto1())
                    .placeholder(holder.img_tutx.getDrawable())
                    .error(R.drawable.zwt)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transform(new GlideRoundTransform(context, 10))
                    .into(holder.img_tutx);
            holder.tv_nmae.setText(mList.get(position).getName());
            holder.tv_jiage.setText("￥"+mList.get(position).getPrice());

            if(mList.get(position).getIsRecommend().equals("0"))
                holder.tv_ddbh.setText("未通过审核");
            else holder.tv_ddbh.setText("已上架");

            holder.tv_editor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, InventoryActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("list", mList.get(position));
                    bundle.putBoolean("open", true);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            holder.tv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FormBody formBody = new FormBody.Builder()
                            .add("id", mList.get(position).getId())
                            .build();
                    try {
                        Okhttp.doPost(context, Constant.URL_deleteGoods, formBody, new IRequestCallBack() {
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
        }

        @Override
        public int getItemCount()
        {
            return mList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder
        {
            private ImageView img_tutx;
            private TextView tv_nmae;
            private TextView tv_jiage;
            private TextView tv_delete;
            private TextView tv_editor;
            private TextView tv_ddbh;
            public MyViewHolder(View view)
            {
                super(view);
                img_tutx = (ImageView) view.findViewById(R.id.img_tutx);
                tv_nmae = (TextView) view.findViewById(R.id.tv_ddname);
                tv_jiage = (TextView) view.findViewById(R.id.tv_ddjg);
                tv_delete = (TextView) view.findViewById(R.id.tv_delete);
                tv_editor = (TextView) view.findViewById(R.id.tv_editor);
                tv_ddbh = (TextView) view.findViewById(R.id.tv_ddbh);
            }
        }
    }
}
