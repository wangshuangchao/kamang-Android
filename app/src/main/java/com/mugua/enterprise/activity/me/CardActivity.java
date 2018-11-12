package com.mugua.enterprise.activity.me;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.mugua.enterprise.BeasActivity;
import com.mugua.enterprise.MainActivity;
import com.mugua.enterprise.R;
import com.mugua.enterprise.activity.HuoDongActivity;
import com.mugua.enterprise.activity.ShowcasingActivity;
import com.mugua.enterprise.bean.ShowcasingBean;
import com.mugua.enterprise.fragment.HomeFragment;
import com.mugua.enterprise.fragment.MeFragment;
import com.mugua.enterprise.util.CardDialog;
import com.mugua.enterprise.util.Constant;
import com.mugua.enterprise.util.Okhttp;
import com.mugua.enterprise.util.ShareDialog;
import com.mugua.enterprise.util.TuActivity;
import com.mugua.enterprise.util.httppost.BaseModel;
import com.mugua.enterprise.util.httppost.IRequestCallBack;
import com.mugua.enterprise.util.toast.CustomToast;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.utils.AutoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;
import static com.squareup.picasso.Picasso.Priority.HIGH;

/**
 * Created by Lenovo on 2017/11/22.
 */

public class CardActivity extends BeasActivity implements View.OnClickListener{
    public static CardActivity instance = null;
    private ArrayList<String> imagePath = new ArrayList<>();
    private ArrayList<String> imagePathjl = new ArrayList<>();
    public int shu = 0;
    @BindView( R.id.rl_back )
    public RelativeLayout rl_back ;

    @BindView( R.id.btn_card )
    public Button btn_card ;

    @BindView( R.id.img_vodie )
    public ImageView img_vodie ;

    @BindView( R.id.tv_dm )
    public EditText tv_dm ;

    @BindView( R.id.e_gsname )
    public TextView e_gsname;

    @BindView( R.id.e_frname )
    public TextView e_frname;

    @BindView( R.id.e_zczb )
    public EditText e_zczb;

    @BindView( R.id.tv_zctime )
    public TextView tv_zctime;

    @BindView( R.id.e_content )
    public EditText e_content;

    @BindView( R.id.e_zcdz )
    public EditText e_zcdz;

    @BindView( R.id.e_phone )
    public EditText e_phone;

    @BindView( R.id.e_xcy )
    public EditText e_xcy;

    @BindView( R.id.tv_title1 )
    public TextView tv_title;

    private GridView gridView;
    private GridAdapter gridAdapter;

    private String id,title;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        instance = this;
        //绑定fragment
        ButterKnife.bind(this);
        e_xcy.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
        e_frname.setText(Constant.user.getLegalPersonName());
        e_gsname.setText(Constant.user.getCompany());

        rl_back.setOnClickListener(this);

        gridView = (GridView) findViewById(R.id.gridView);
        imagePath.add("000000");
        gridAdapter = new GridAdapter(this,imagePath);
        gridView.setAdapter(gridAdapter);

        btn_card.setOnClickListener(this);
        img_vodie.setOnClickListener(this);
        tv_zctime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initTimePicker();
            }
        });

        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");
        title = bundle.getString("title");
        tv_title.setText(title);
        btn_card.setText(bundle.getString("btn"));
        if(bundle.getString("kind").equals("1"))
        {
            new Thread( new MyRunnable()).start();
        }
    }

    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            setdata();
        }
    }

    List<String> datas=new ArrayList<>();
    private void setdata()
    {
        String www = "?id="+id;
        try {
            Okhttp.doGet(this, Constant.URL_getCardInfo+www, new IRequestCallBack() {
                @Override
                public void onRequestBack(String text) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(text);
                        if(jsonObject.getString("code").equals("1000"))
                        {
                            Gson gson = new Gson();
                            ShowcasingBean showcasing1Bean = gson.fromJson(jsonObject.getString("data"),ShowcasingBean.class);
                            CustomToast.showToast(getApplicationContext(),jsonObject.getString("msg"),1000);

                            microblog = showcasing1Bean.getMicroblog();
                            if(microblog.equals("") || microblog.equals("null")) img_vodie.setBackgroundResource(R.mipmap.spzs);
                            else ImageLoader.getInstance().displayImage(microblog, img_vodie, TuActivity.tu(R.drawable.zwt));

                            tv_dm.setText(showcasing1Bean.getOrganizationCode());
                            e_zczb.setText(showcasing1Bean.getRegisteredAssets());
                            tv_zctime.setText(showcasing1Bean.getCreateTimes());
                            e_zcdz.setText(showcasing1Bean.getCompanyAddr());
                            e_phone.setText(showcasing1Bean.getLegalPersonPhone());
                            e_xcy.setText(showcasing1Bean.getScopeOfBusiness());
                            e_content.setText(showcasing1Bean.getIntroduceCompany());

                            merchandise1 = showcasing1Bean.getProductUrlOne();
                            merchandise2 = showcasing1Bean.getProductUrlTwo();
                            companyVidio = showcasing1Bean.getCompanyVidio();

                            if (imagePath.contains("000000")) {
                                imagePath.remove("000000");
                            }
                            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                            if(!jsonObject1.getString("goodsPhoto1").equals("") && jsonObject1.getString("goodsPhoto1") != null
                                    && !jsonObject1.getString("goodsPhoto1").equals("null")) {
                                goodsPhoto1 = jsonObject1.getString("goodsPhoto1");
                                datas.add(goodsPhoto1);
                            }
                            if(!jsonObject1.getString("goodsPhoto2").equals("") && jsonObject1.getString("goodsPhoto2") != null
                                    && !jsonObject1.getString("goodsPhoto2").equals("null")) {
                                goodsPhoto2 = jsonObject1.getString("goodsPhoto2");
                                datas.add(goodsPhoto2);
                            }
                            imagePathjl.addAll(datas);
                            datas.add("000000");
                            imagePath.addAll(datas);
                            gridAdapter.setdata(imagePath);
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

    public void data()
    {
        img_vodie.setImageBitmap(bitmap);
    }

    public String videoId = "";
    public String microblog = "";
    public String merchandise1 = "";
    public String merchandise2 = "";
    public String goodsPhoto1 = "";
    public String goodsPhoto2 = "";
    public Bitmap bitmap = null;
    private String companyVidio = "";
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.rl_back:
                finish();
                break;
            case R.id.img_vodie:
                startActivity(new Intent(CardActivity.this,AddVideoActivity.class));
                break;
            case R.id.btn_card:
                if(!videoId.equals(""))
                    companyVidio = "";
                else if(!companyVidio.equals(""))
                    videoId = "";

                if(tv_dm.getText().toString().equals("")) {
                    CustomToast.showToast(getApplicationContext(), "请输入信用代码", 1000);
                    return;
                }
                if(e_zczb.getText().toString().equals("")) {
                    CustomToast.showToast(getApplicationContext(), "请输入金额", 1000);
                    return;
                }
                if(tv_zctime.getText().toString().equals("")) {
                    CustomToast.showToast(getApplicationContext(), "请输入时间", 1000);
                    return;
                }
                if(e_content.getText().toString().equals("")) {
                    CustomToast.showToast(getApplicationContext(), "请输入简介内容", 1000);
                    return;
                }
                if(e_zcdz.getText().toString().equals("")) {
                    CustomToast.showToast(getApplicationContext(), "请输入地址", 1000);
                    return;
                }
                if(e_xcy.getText().toString().equals("")) {
                    CustomToast.showToast(getApplicationContext(), "请输入企业宣传语", 1000);
                    return;
                }

                BaseModel baseModel = new BaseModel(Constant.URL_bulidCard);
                String www = "id="+Constant.user.getId()+"&company="+e_gsname.getText().toString()+"&videoId="+videoId+
                        "&productUrlOne="+merchandise1+"&productUrlTwo="+merchandise2+"&companyVidio="+companyVidio+
                        "&microblog="+microblog+ "&goodsPhoto1="+goodsPhoto1+"&goodsPhoto2="+goodsPhoto2+
                        "&organizationCode="+tv_dm.getText().toString()+ "&legalPersonName="+e_frname.getText().toString()+
                        "&registeredAssets="+e_zczb.getText().toString()+ "&createTimes="+ tv_zctime.getText().toString()+
                        "&introduceCompany="+e_content.getText().toString()+"&companyAddr="+e_zcdz.getText().toString()+
                        "&legalPersonPhone="+e_phone.getText().toString()+"&scopeOfBusiness="+e_xcy.getText().toString();
                baseModel.send(www, new IRequestCallBack() {
                    @Override
                    public void onRequestBack(String text) {
                        if(text.equals("创建失败"))
                            CustomToast.showToast(getApplicationContext(),text,1000);
                        else {
                            CustomToast.showToast(getApplicationContext(), text, 1000);
                            finish1();
                        }
                    }
                });
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
                        if(Constant.open4 == true)
                        {
                            tankuang();
                        }else {
                            finish();
                        }
                    }
                });

                view.findViewById(R.id.tv_bg).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        if(Constant.open4 == true)
                        {
                            tankuang();
                        }else {
                            finish();
                        }
                    }
                });
            }
        }).show();
    }

    private void tankuang()
    {
        new ShareDialog(this, R.layout.hd_card, R.style.dialog, new ShareDialog.OnItemClickListener() {
            @Override
            public void onClick(Dialog dialog, View view) {
                view.findViewById(R.id.tv_bg).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();

                        Constant.open4 = false;
                        HuoDongActivity.instance.finish();
                        MainActivity.instance.showDetails(3);
                        MainActivity.instance.bottomBar.setNormalState(0);
                        MainActivity.instance.bottomBar.setSelectedState(3);
                    }
                });
            }
        }).show();
    }

    public void initTimePicker() {
        //控制时间范围(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
        //因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
        Calendar selectedDate = Calendar.getInstance();

        Calendar startDate = Calendar.getInstance();
        startDate.set(1900,1,1);

        Calendar endDate = Calendar.getInstance();
        endDate.set(2110,12,30);
        //时间选择器
        TimePickerView pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                // 这里回调过来的v,就是show()方法里面所添加的 View 参数，如果show的时候没有添加参数，v则为null
                tv_zctime.setText(getTime(date));
            }
        })
                .setType(TimePickerView.Type.ALL)
                .setLabel("", "", "", "", "", "") //设置空字符串以隐藏单位提示   hide label
                .setDividerColor(Color.DKGRAY)
                .setContentSize(20)
                .setDate(selectedDate)
                .setRangDate(startDate,endDate)
                .build();
        pvTime.show();
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    public void init(String bitmap)
    {
        imagePathjl.add(bitmap);

        imagePath.clear();
        imagePath.addAll(imagePathjl);
        imagePath.add("000000");
        gridAdapter = new GridAdapter(this,imagePath);
        gridView.setAdapter(gridAdapter);
    }

    private class GridAdapter extends BaseAdapter {
        private List<String> listUrls;
        private LayoutInflater inflater;
        private Context context;
        public GridAdapter(Context context,List<String> listUrls) {
            this.context = context;
            this.listUrls = listUrls;
            inflater = LayoutInflater.from(context);
        }

        public void setdata(List<String> listUrls)
        {
            shu = listUrls.size()-1;
            gridAdapter = new GridAdapter(context,listUrls);
            gridView.setAdapter(gridAdapter);
        }

        public void data()
        {
            if (imagePath.contains("000000")) {
                imagePath.remove("000000");
            }
            imagePath.add("000000");
            listUrls = imagePath;
            gridAdapter = new GridAdapter(context,imagePath);
            gridView.setAdapter(gridAdapter);
        }

        public int getCount() {
            return listUrls.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_image, parent, false);
                holder.image = (ImageView) convertView.findViewById(R.id.imageView);
                holder.image1 = (ImageView) convertView.findViewById(R.id.imageView1);
                convertView.setTag(holder);
                AutoUtils.autoSize(convertView);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final String path = listUrls.get(position);
            if (path.equals("000000")) {
                holder.image.setBackgroundResource(R.mipmap.add);
            } else {
                Glide.with(context)
                        .load(path)
                        .error(R.drawable.zwt)
                        .centerCrop()
                        .crossFade()
                        .into(holder.image);
            }

            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if ("000000".equals(listUrls.get(position)) && shu < 2) {
                        shu++;
                        startActivity(new Intent(context,ProductUploadActivity.class));
                    } else if("000000".equals(listUrls.get(position)) && shu >= 2){
                        CustomToast.showToast(context,"最多传2个产品");
                    }
                }
            });
            holder.image1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!listUrls.get(position).equals("000000"))
                    {
                        if(position == 0)
                            merchandise1 = merchandise2;
                        shu--;
                        imagePathjl.remove(position);
                        imagePath.remove(position);
                        data();
                    }
                }
            });
            return convertView;
        }
    }
    private class ViewHolder {
        ImageView image;
        ImageView image1;
    }
}
