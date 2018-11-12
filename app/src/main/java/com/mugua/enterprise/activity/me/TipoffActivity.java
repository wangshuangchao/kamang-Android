package com.mugua.enterprise.activity.me;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mugua.enterprise.BeasActivity;
import com.mugua.enterprise.R;
import com.zhy.autolayout.AutoLayoutActivity;
import com.zhy.autolayout.utils.AutoUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lenovo on 2017/11/22.
 */

public class TipoffActivity extends BeasActivity implements View.OnClickListener{
    public static TipoffActivity instance = null;
    @BindView( R.id.rl_back )
    public RelativeLayout rl_back ;

    @BindView( R.id.my_grid )
    public GridView my_grid ;

    @BindView( R.id.tv_xyb )
    public TextView tv_xyb ;

    private MyAdapter adapter;
    private int ji;
    private String shu[] = {"名片","视频","产品","工商信息","企业简介","其他"};
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipoff);
        instance = this;
        //绑定fragment
        ButterKnife.bind(this);
        rl_back.setOnClickListener(this);
        tv_xyb.setOnClickListener(this);

        adapter = new MyAdapter(this);
        my_grid.setAdapter(adapter);
        my_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ji = position;
                adapter.notifyDataSetChanged();
            }
        });
    }
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_xyb:
                finish();
                break;
        }
    }

    public class MyAdapter extends BaseAdapter {

        private Context context;
        public MyAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return shu.length;
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
                convertView = LayoutInflater.from(context).inflate(R.layout.tipoff_grid_item, parent, false);
                holder.tv_qian = (TextView)convertView.findViewById(R.id.tv_qian);
                convertView.setTag(holder);
                AutoUtils.autoSize(convertView);
            } else
                holder = (ViewHolder) convertView.getTag();

            if(ji == position)
            {
                if(holder.tv_qian.isSelected() == false)
                    holder.tv_qian.setSelected(true);
                else holder.tv_qian.setSelected(false);
            }

            holder.tv_qian.setText(shu[position]);
            return convertView;
        }
    }

    public class ViewHolder{
        private TextView tv_qian;
    }
}
