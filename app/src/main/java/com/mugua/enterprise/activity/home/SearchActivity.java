package com.mugua.enterprise.activity.home;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.mugua.enterprise.BeasActivity;
import com.mugua.enterprise.R;
import com.mugua.enterprise.fragment.HomeFragment;
import com.zhy.autolayout.utils.AutoUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lenovo on 2017/11/21.
 */

public class SearchActivity extends BeasActivity implements View.OnClickListener{
    public static SearchActivity instance = null;

    @BindView( R.id.search_list )
    public ListView search_list ;

    @BindView( R.id.tv_seach )
    public EditText tv_seach ;

    @BindView( R.id.tv_back )
    public TextView tv_back ;

    @BindView( R.id.ll_qx )
    public LinearLayout ll_qx ;

    private SQLiteDatabase mSQLiteDataBase;
    private String[] titles;
    private MyAdapter myadapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        instance = this;
        //绑定fragment
        ButterKnife.bind( this ) ;

        mSQLiteDataBase = this.openOrCreateDatabase("/sdcard/update/cc.db",MODE_PRIVATE, null);
        String CREATE_TABLE = "create table if not exists table1 (_id INTEGER PRIMARY KEY AUTOINCREMENT,content TEXT);";
        mSQLiteDataBase.execSQL(CREATE_TABLE);

        tv_seach.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                Editable editable = tv_seach.getText();
                if(editable.length() == 0)
                    tv_back.setText("取消");
                else tv_back.setText("搜索");
            }
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count,int after) {
            }
            @Override
            public void afterTextChanged(Editable edit) {
            }
        });

        tv_seach.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    // 先隐藏键盘
                    ((InputMethodManager)tv_seach.getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow
                            (getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                    String keyword = tv_seach.getText().toString();

                    if(!keyword.equals("")){
                        mSQLiteDataBase.delete("table1", "content='"+keyword+"'" , null);
                        ContentValues cv = new ContentValues();
                        cv.put("content", tv_seach.getText().toString());
                        mSQLiteDataBase.insert("table1", null, cv);

                        Intent intent = new Intent(SearchActivity.this, SearchDetailsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("keyword", keyword);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }
                    return true;
                }
                return false;
            }
        });

        search_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(SearchActivity.this, SearchDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("keyword", titles[i]);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

        getData("SELECT * FROM table1 order by _id desc");

        tv_back.setOnClickListener(this);
        ll_qx.setOnClickListener(this);
    }

    /* 查询数据 */
    public void getData(String where) {
        Cursor cur = mSQLiteDataBase.rawQuery(where, null);
        if (cur != null) {
            if (cur.moveToFirst()) {
                titles = new String [cur.getCount()];
                int shu = 0;
                do {
                    int _id = cur.getInt(cur.getColumnIndex("_id"));
                    String content1 = cur.getString(cur.getColumnIndex("content"));

                    titles [shu] = content1;
                    shu++;
                } while (cur.moveToNext());
                myadapter = new MyAdapter(this,titles);
                search_list.setAdapter(myadapter);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.tv_back:
                if(tv_back.getText().equals("搜索"))
                {
                    // 先隐藏键盘
                    ((InputMethodManager)tv_seach.getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow
                            (getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                    String keyword = tv_seach.getText().toString();

                    if(!keyword.equals("")){
                        mSQLiteDataBase.delete("table1", "content='"+keyword+"'" , null);
                        ContentValues cv = new ContentValues();
                        cv.put("content", tv_seach.getText().toString());
                        mSQLiteDataBase.insert("table1", null, cv);

                        Intent intent = new Intent(SearchActivity.this, SearchDetailsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("keyword", keyword);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }
                }else finish();
                break;
            case R.id.ll_qx:
                mSQLiteDataBase.delete("table1", "" , null);
                myadapter.data(0);
                break;
        }
    }

    public class MyAdapter extends BaseAdapter {

        private Context context;
        private String[] titles;
        private int shu;
        public MyAdapter(Context context,String[] titles) {
            this.context = context;
            this.titles = titles;
            shu = titles.length;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return shu;
        }

        public void data(int shu)
        {
            this.shu = shu;
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
                convertView = LayoutInflater.from(context).inflate(R.layout.seach_list_item, parent, false);
                holder.view_1 = (View) convertView.findViewById(R.id.view_1);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                convertView.setTag(holder);
                AutoUtils.autoSize(convertView);
            } else
                holder = (ViewHolder1) convertView.getTag();

            holder.tv_name.setText(titles[position]);
            if(position == shu-1)
                holder.view_1.setVisibility(View.GONE);
            else
                holder.view_1.setVisibility(View.VISIBLE);
            return convertView;
        }
    }

    public class ViewHolder1{
        private View view_1;
        private TextView tv_name;
    }
}
