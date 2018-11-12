package com.mugua.enterprise.util.pickerview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.mugua.enterprise.R;
import com.mugua.enterprise.util.pickerview.bean.CardBean;
import com.mugua.enterprise.util.pickerview.bean.ProvinceBean;


import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener {

    private ArrayList<ProvinceBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
 /*   private ArrayList<ArrayList<ArrayList<IPickerViewData>>> options3Items = new ArrayList<>();*/
    private Button btn_Time, btn_Options,btn_CustomOptions,btn_CustomTime,btn_no_linkage;

    private TimePickerView pvTime,pvCustomTime;
    private OptionsPickerView pvOptions,pvCustomOptions, pvNoLinkOptions;
    private ArrayList<CardBean> cardItem = new ArrayList<>();

    private List<String> food = new ArrayList<>();
    private List<String> clothes = new ArrayList<>();
    private List<String> computer = new ArrayList<>();
    PickerViewUtil pickerviewutil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_shi);

        initOptionData();
        pickerviewutil = new PickerViewUtil(this);

        btn_Time = (Button) findViewById(R.id.btn_Time);
        btn_Options = (Button) findViewById(R.id.btn_Options);
        btn_CustomOptions = (Button) findViewById(R.id.btn_CustomOptions);
        btn_CustomTime = (Button) findViewById(R.id.btn_CustomTime);
        btn_no_linkage = (Button) findViewById(R.id.btn_no_linkage);

        btn_Time.setOnClickListener(this);
        btn_Options.setOnClickListener(this);
        btn_CustomOptions.setOnClickListener(this);
        btn_CustomTime.setOnClickListener(this);
        btn_no_linkage.setOnClickListener(this);

        findViewById(R.id.btn_GotoJsonData).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_Time://弹出时间选择器
                pickerviewutil.initTimePicker();
                break;
            case R.id.btn_Options://弹出条件选择器
                pickerviewutil.initOptionPicker(options1Items,options2Items);
                break;
            case R.id.btn_CustomOptions://弹出自定义条件选择器
                pickerviewutil.initCustomTimePicker();
                break;
            case R.id.btn_CustomTime://弹出自定义时间选择器
                pickerviewutil.initCustomOptionPicker(cardItem);
                break;
            case R.id.btn_no_linkage://不联动数据选择器
                pickerviewutil.initNoLinkOptionsPicker(food,clothes,computer);// 不联动的多级选项
                break;
            case R.id.btn_GotoJsonData://跳转到 省市区解析示例页面
                startActivity(new Intent(MainActivity.this,JsonDataActivity.class));
                break;
        }
    }

    private void initOptionData() {

        getCardData();
        getNoLinkData();

        //选项1
        options1Items.add(new ProvinceBean(0,"广东","描述部分","其他数据"));
        options1Items.add(new ProvinceBean(1,"湖南","描述部分","其他数据"));
        options1Items.add(new ProvinceBean(2,"广西","描述部分","其他数据"));

        //选项2
        ArrayList<String> options2Items_01 = new ArrayList<>();
        options2Items_01.add("广州");
        options2Items_01.add("佛山");
        options2Items_01.add("东莞");
        options2Items_01.add("珠海");
        ArrayList<String> options2Items_02 = new ArrayList<>();
        options2Items_02.add("长沙");
        options2Items_02.add("岳阳");
        options2Items_02.add("株洲");
        options2Items_02.add("衡阳");
        ArrayList<String> options2Items_03 = new ArrayList<>();
        options2Items_03.add("桂林");
        options2Items_03.add("玉林");
        options2Items.add(options2Items_01);
        options2Items.add(options2Items_02);
        options2Items.add(options2Items_03);

        /*--------数据源添加完毕---------*/
    }

    private void getCardData() {
        for (int i = 0; i < 5; i++) {
            cardItem.add(new CardBean(i, "No.ABC12345 " + i));
        }
    }

    private void getNoLinkData() {
        food.add("KFC");
        food.add("MacDonald");
        food.add("Pizza hut");

        clothes.add("Nike");
        clothes.add("Adidas");
        clothes.add("Anima");

        computer.add("ASUS");
        computer.add("Lenovo");
        computer.add("Apple");
        computer.add("HP");
    }


}
