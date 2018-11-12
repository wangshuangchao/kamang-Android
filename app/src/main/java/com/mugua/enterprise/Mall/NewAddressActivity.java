package com.mugua.enterprise.Mall;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.google.gson.Gson;
import com.mugua.enterprise.BeasActivity;
import com.mugua.enterprise.R;
import com.mugua.enterprise.bean.AddressBean;
import com.mugua.enterprise.util.Constant;
import com.mugua.enterprise.util.Okhttp;
import com.mugua.enterprise.util.httppost.IRequestCallBack;
import com.mugua.enterprise.util.pickerview.GetJsonDataUtil;
import com.mugua.enterprise.util.pickerview.bean.JsonBean;
import com.mugua.enterprise.util.toast.CustomToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.FormBody;

/**
 * Created by Lenovo on 2018/3/27.
 */

public class NewAddressActivity extends BeasActivity implements View.OnClickListener{
    public static NewAddressActivity instance = null;
    @BindView(R.id.rl_order)
    public RelativeLayout rl_order;

    @BindView(R.id.rl_back)
    public RelativeLayout rl_back;

    @BindView(R.id.e_name)
    public EditText e_name;

    @BindView(R.id.e_sjh)
    public EditText e_sjh;

    @BindView(R.id.tv_address)
    public TextView tv_address;

    @BindView(R.id.e_content)
    public EditText e_content;

    @BindView(R.id.e_yb)
    public EditText e_yb;

    @BindView(R.id.btn_bc)
    public TextView btn_bc;

    @BindView(R.id.tv_tile)
    public TextView tv_tile;

    private AddressBean data;
    private boolean open;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_address);
        instance = this;
        //绑定fragment
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        open = bundle.getBoolean("open");
        if(open == true)
        {
            data = (AddressBean) bundle.getSerializable("list");
            e_sjh.setText(data.getPhone());
            e_name.setText(data.getName());
            e_content.setText(data.getAddress());
            tv_address.setText(data.getArea());
            e_yb.setText(data.getPostcode());
            tv_tile.setText("修改地址");
        }

        mHandler.sendEmptyMessage(MSG_LOAD_DATA);

        rl_back.setOnClickListener(this);
        btn_bc.setOnClickListener(this);
        rl_order.setOnClickListener(this);
    }

    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            if(tv_tile.getText().toString().equals("修改地址"))
                Data1();
            else Data();
        }
    }

    public void Data1()
    {
        FormBody formBody = new FormBody.Builder()
                .add("id", data.getId())
                .add("uid", Constant.user.getId())
                .add("name", e_name.getText().toString())
                .add("phone", e_sjh.getText().toString())
                .add("area", tv_address.getText().toString())
                .add("address", e_content.getText().toString())
                .add("postcode", e_yb.getText().toString())
                .build();
        try {
            Okhttp.doPost(this, Constant.URL_updateAddr, formBody, new IRequestCallBack() {
                @Override
                public void onRequestBack(String text) {
                    JSONObject jsonobject = null;
                    try {
                        jsonobject = new JSONObject(text);
                        if (jsonobject.getString("code").equals("1000"))
                        {
                            CustomToast.showToast(getApplicationContext(),jsonobject.getString("msg"),1000);
                            AddressActivity.instance.Data();
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

    public void Data()
    {
        FormBody formBody = new FormBody.Builder()
                .add("uid", Constant.user.getId())
                .add("name", e_name.getText().toString())
                .add("phone", e_sjh.getText().toString())
                .add("area", tv_address.getText().toString())
                .add("address", e_content.getText().toString())
                .add("postcode", e_yb.getText().toString())
                .build();
        try {
            Okhttp.doPost(this, Constant.URL_addAddr, formBody, new IRequestCallBack() {
                @Override
                public void onRequestBack(String text) {
                    JSONObject jsonobject = null;
                    try {
                        jsonobject = new JSONObject(text);
                        if (jsonobject.getString("code").equals("1000"))
                        {
                            CustomToast.showToast(getApplicationContext(),jsonobject.getString("msg"),1000);
                            AddressActivity.instance.Data();
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

    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private Thread thread;
    private static final int MSG_LOAD_DATA = 0x0001;
    private static final int MSG_LOAD_SUCCESS = 0x0002;
    private static final int MSG_LOAD_FAILED = 0x0003;
    private boolean isLoaded = false;
    private void ShowPickerView() {// 弹出选择器

        OptionsPickerView pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = options1Items.get(options1).getPickerViewText()+" "+
                        options2Items.get(options1).get(options2)+" "+
                        options3Items.get(options1).get(options2).get(options3);
                tv_address.setText(tx);
            }
        })

                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .setOutSideCancelable(false)// default is true
                .build();

        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(options1Items, options2Items,options3Items);//三级选择器
        pvOptions.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_bc:
                if(TextUtils.isEmpty(e_name.getText()))
                {
                    CustomToast.showToast(getApplicationContext(),"请填写收货人名称",1000);
                    return;
                }
                if(TextUtils.isEmpty(e_sjh.getText()))
                {
                    CustomToast.showToast(getApplicationContext(),"请填写收货人手机号",1000);
                    return;
                }
                if(tv_address.getText().equals("请选择")&&TextUtils.isEmpty(e_content.getText()))
                {
                    CustomToast.showToast(getApplicationContext(),"请填写收货人地址以及详细地址",1000);
                    return;
                }
                if(TextUtils.isEmpty(e_yb.getText()))
                {
                    CustomToast.showToast(getApplicationContext(),"请填写邮编",1000);
                    return;
                }
                new Thread( new MyRunnable()).start();
                break;
            case R.id.rl_order:
                if (isLoaded){
                    ShowPickerView();
                }else {
                    Toast.makeText(getApplicationContext(),"数据暂未解析成功，请等待",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_DATA:
                    if (thread==null){//如果已创建就不再重新创建子线程了

                        thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 写子线程中的操作,解析省市区数据
                                initJsonData();
                            }
                        });
                        thread.start();
                    }
                    break;

                case MSG_LOAD_SUCCESS:
                    isLoaded = true;
                    break;

                case MSG_LOAD_FAILED:
                    CustomToast.showToast(getApplicationContext(),"解析数据失败",1000);
                    break;

            }
        }
    };

    private ArrayList<String> CityList;
    private ArrayList<ArrayList<String>> Province_AreaList;
    private ArrayList<String> City_AreaList;
    private void initJsonData() {//解析数据

        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        String JsonData = new GetJsonDataUtil().getJson(this,"province.json");//获取assets目录下的json文件数据

        ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体

        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;

        for (int i=0;i<jsonBean.size();i++){//遍历省份
            CityList = new ArrayList<>();//该省的城市列表（第二级）
            Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c=0; c<jsonBean.get(i).getCityList().size(); c++){//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//添加城市

                City_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        ||jsonBean.get(i).getCityList().get(c).getArea().size()==0) {
                    City_AreaList.add("");
                }else {

                    for (int d=0; d < jsonBean.get(i).getCityList().get(c).getArea().size(); d++) {//该城市对应地区所有数据
                        String AreaName = jsonBean.get(i).getCityList().get(c).getArea().get(d);

                        City_AreaList.add(AreaName);//添加该城市所有地区数据
                    }
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            options2Items.add(CityList);

            /**
             * 添加地区数据
             */
            options3Items.add(Province_AreaList);
        }

        mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);

    }


    public ArrayList<JsonBean> parseData(String result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
        }
        return detail;
    }
}
