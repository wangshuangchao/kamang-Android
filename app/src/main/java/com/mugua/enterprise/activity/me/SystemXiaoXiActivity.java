package com.mugua.enterprise.activity.me;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.mugua.enterprise.BeasActivity;
import com.mugua.enterprise.R;
import com.zhy.autolayout.utils.AutoUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lenovo on 2017/11/21.
 */

public class SystemXiaoXiActivity extends BeasActivity implements View.OnClickListener{
    public static SystemXiaoXiActivity instance = null;
    @BindView( R.id.rl_back )
    public RelativeLayout rl_back ;

    @BindView( R.id.systemxiaoxi_list )
    public ListView systemxiaoxi_list ;

    private MyAdapter myadapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_systemxiaoxi);
        instance = this;
        //绑定fragment
        ButterKnife.bind(this);
        rl_back.setOnClickListener(this);

        myadapter = new MyAdapter(this);
        systemxiaoxi_list.setAdapter(myadapter);
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
        public MyAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return 3;
        }

        public void data(int shu)
        {
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
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder1 holder = null;
            if (convertView == null) {
                holder = new ViewHolder1();
                convertView = LayoutInflater.from(context).inflate(R.layout.systemxiaoxi_list_item, parent, false);
                convertView.setTag(holder);
                AutoUtils.autoSize(convertView);
            } else
                holder = (ViewHolder1) convertView.getTag();
            return convertView;
        }
    }

    public class ViewHolder1{
    }
}
