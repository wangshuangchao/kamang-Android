package com.mugua.enterprise.Mall;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.pay.demo.PaymodeV2;
import com.alipay.sdk.pay.demo.PaymodeV2CallBack;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mugua.enterprise.BeasActivity;
import com.mugua.enterprise.R;
import com.mugua.enterprise.activity.me.AllOrderActivity;
import com.mugua.enterprise.bean.YlShangPinBean;
import com.mugua.enterprise.login.LoginActivity;
import com.mugua.enterprise.util.Canonical;
import com.mugua.enterprise.util.Constant;
import com.mugua.enterprise.util.DialogActivity;
import com.mugua.enterprise.util.GlideRoundTransform;
import com.mugua.enterprise.util.MD5;
import com.mugua.enterprise.util.Okhttp;
import com.mugua.enterprise.util.TuActivity;
import com.mugua.enterprise.util.httppost.IRequestCallBack;
import com.mugua.enterprise.util.toast.CustomToast;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.FormBody;

/**
 * Created by Lenovo on 2018/3/12.
 */

public class Order_Details extends BeasActivity implements View.OnClickListener{
    public static Order_Details instance = null;
    @BindView( R.id.rl_back )
    public RelativeLayout rl_back ;

    @BindView( R.id.img_jian )
    public ImageView img_jian ;

    @BindView( R.id.img_jia )
    public ImageView img_jia ;

    @BindView( R.id.tv_jiage )
    public TextView tv_jiage ;

    @BindView( R.id.e_shu )
    public EditText e_shu ;

    @BindView( R.id.rl_zhifuf )
    public RelativeLayout rl_zhifuf ;

    @BindView( R.id.rl_zhifuw )
    public RelativeLayout rl_zhifuw ;

    @BindView( R.id.img_xieyiw )
    public ImageView img_xieyiw ;

    @BindView( R.id.img_xieyif )
    public ImageView img_xieyif ;

    @BindView( R.id.tv_zj )
    public TextView tv_zj ;

    @BindView( R.id.tv_zhifu )
    public TextView tv_zhifu ;

    @BindView( R.id.btn_shdz )
    public RelativeLayout btn_shdz ;

    @BindView( R.id.tv_name )
    public TextView tv_name ;

    @BindView( R.id.tv_phone )
    public TextView tv_phone ;

    @BindView( R.id.tv_jianjie )
    public TextView tv_jianjie ;

    @BindView( R.id.ll_zs )
    public LinearLayout ll_zs ;

    @BindView( R.id.ll_zczs )
    public LinearLayout ll_zczs ;

    @BindView( R.id.sp_tu )
    public ImageView sp_tu ;

    @BindView( R.id.tv_spname )
    public TextView tv_spname ;

    private int data;
    private int shu = 1;
    private int shucwz;
    private double jiage = 0.00d;
    private List<YlShangPinBean> groups;
    private DialogActivity dialogActivity;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_details);
        instance = this;
        //绑定fragment
        ButterKnife.bind(this);
        dialogActivity = new DialogActivity(this);
        Bundle bundle = getIntent().getExtras();
        data = bundle.getInt("data");
        shu = bundle.getInt("shul");
        shucwz = bundle.getInt("shu");
        groups = (List<YlShangPinBean>) bundle.getSerializable("list");

        e_shu.setText(shu+"");
        tv_spname.setText(groups.get(shucwz).getName());
        jiage = Double.parseDouble(groups.get(shucwz).getPrice());
        Glide.with(this)
                .load(groups.get(shucwz).getPhoto1())
                .placeholder(sp_tu.getBackground())
                .error(R.drawable.zwt)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new GlideRoundTransform(this, 10))
                .into(sp_tu);

        tv_jiage.setText("￥"+String.format("%.2f", jiage));
        double jiage1 = jiage*shu;
        tv_zj.setText("￥"+String.format("%.2f", jiage1));

        e_shu.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                Editable editable = e_shu.getText();
                int len = editable.length();
                if(len > 0)
                {
                    shu = Integer.parseInt(editable.toString());
                    double jiage1 = jiage*shu;
                    tv_zj.setText("￥"+String.format("%.2f", jiage1));
                }
            }
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count,int after) {
            }
            @Override
            public void afterTextChanged(Editable edit) {
            }
        });
        img_xieyiw.setSelected(true);
        rl_back.setOnClickListener(this);
        img_jia.setOnClickListener(this);
        img_jian.setOnClickListener(this);
        e_shu.setOnClickListener(this);
        rl_zhifuw.setOnClickListener(this);
        rl_zhifuf.setOnClickListener(this);
        tv_zhifu.setOnClickListener(this);
        btn_shdz.setOnClickListener(this);
        ll_zczs.setOnClickListener(this);
    }

    private String yb = "";
    public void tdzdata(String name,String phone,String jianjie,String yb)
    {
        btn_shdz.setVisibility(View.GONE);
        ll_zs.setVisibility(View.VISIBLE);
        tv_name.setText(name);
        tv_phone.setText(phone);
        tv_jianjie.setText(jianjie);
        this.yb = yb;
    }

//    private InputMethodManager imm;
    private int stype = 1;
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.rl_back:
                finish();
                break;
            case R.id.ll_zczs:
                if(Constant.user != null)
                    startActivity(new Intent(Order_Details.this, AddressActivity.class));
                else startActivity(new Intent(Order_Details.this, LoginActivity.class));
                break;
            case R.id.btn_shdz:
                if(Constant.user != null)
                    startActivity(new Intent(Order_Details.this, AddressActivity.class));
                else startActivity(new Intent(Order_Details.this, LoginActivity.class));
                break;
            case R.id.tv_zhifu:
                if(tv_name.getText().equals(""))
                {
                    CustomToast.showToast(getApplicationContext(),"请选择地址",1000);
                    return;
                }

                if(Constant.user == null)
                {
                    CustomToast.showToast(getApplicationContext(),"请选择地址",1000);
                    return;
                }

                if(shu > Integer.parseInt(groups.get(shucwz).getNum()))
                {
                    CustomToast.showToast(getApplicationContext(),"超出库存");
                    return;
                }

                if(groups.get(shucwz).getIsLimitid().equals("1")){
                    if(shu > data)
                    {
                        CustomToast.showToast(getApplicationContext(),"超出限购数量",1000);
                        return;
                    }
                }

                String w = Canonical.getIPAddress(this);
                if(w.equals(""))
                    return;
                dialogActivity.showProgressDialog("下单中...");
                switch (stype)
                {
                    case 1://微信支付
                        Data1(w);
                        break;
                    case 2://支付宝支付
                        Data2(w);
                        break;
                }
                break;
            case R.id.rl_zhifuw:
                stype = 1;
                img_xieyiw.setSelected(true);
                img_xieyif.setSelected(false);
                break;
            case R.id.rl_zhifuf:
                stype = 2;
                img_xieyiw.setSelected(false);
                img_xieyif.setSelected(true);
                break;
            case R.id.e_shu:
                e_shu.setFocusable(true);
                e_shu.setFocusableInTouchMode(true);
                e_shu.requestFocus();
                e_shu.setSelection(e_shu.length());
//                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                break;
            case R.id.img_jia:
//                imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(e_shu.getWindowToken(), 0);
                String ejia = e_shu.getText().toString();
                if(!ejia.equals("")) {
//                    e_shu.setFocusable(false);
                    shu = Integer.parseInt(ejia);
                    if(shu < Integer.parseInt(groups.get(shucwz).getNum()))
                    {
                        if(groups.get(shucwz).getIsLimitid().equals("0"))
                        {
                            shu++;
                            e_shu.setText(shu+"");
                            double jiage1 = jiage*shu;
                            tv_zj.setText("￥"+String.format("%.2f", jiage1));
                        }else {
                            if(shu < data)
                            {
                                shu++;
                                e_shu.setText(shu+"");
                                double jiage1 = jiage*shu;
                                tv_zj.setText("￥"+String.format("%.2f", jiage1));
                            }else CustomToast.showToast(getApplicationContext(),"超出限购数量",1000);
                        }
                    }else CustomToast.showToast(getApplicationContext(),"超出库存",1000);
                }
                break;
            case R.id.img_jian:
//                imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(e_shu.getWindowToken(), 0);
                String ejian = e_shu.getText().toString();
                if(!ejian.equals("")) {
//                    e_shu.setFocusable(false);
                    shu = Integer.parseInt(ejian);
                    if (shu > 1)
                        shu--;
                    e_shu.setText(shu + "");
                    double jiage1 = jiage*shu;
                    tv_zj.setText("￥"+String.format("%.2f", jiage1));
                }
                break;
        }
    }
    private void Data2(String ip)
    {
        String str = tv_zj.getText().toString();
        str = str.substring(1,str.length());
        FormBody formBody = new FormBody.Builder()
                .add("goodsId",groups.get(shucwz).getId())
                .add("sellerid", groups.get(shucwz).getMerchant())
                .add("goodsName", groups.get(shucwz).getName())
                .add("goodsDetail", groups.get(shucwz).getDetail())
                .add("goodsNum", shu+"")
                .add("goodsPhoto", groups.get(shucwz).getPhoto1())
                .add("totalFee", str)
                .add("goodsPrice", groups.get(shucwz).getPrice())
                .add("receiver", tv_name.getText().toString())
                .add("receiverAddress", tv_jianjie.getText().toString())
                .add("receiverZipCode", yb)
                .add("receiverPhone", tv_phone.getText().toString())
                .add("userId", Constant.user.getId())
                .add("userName", Constant.user.getUserName())
                .add("ip", ip)
                .build();
        try {
            Okhttp.doPost(this, Constant.URL_zfbzfhq, formBody, new IRequestCallBack() {
                @Override
                public void onRequestBack(String text) {
                    JSONObject jsonobject = null;
                    try {
                        jsonobject = new JSONObject(text);
                        if (jsonobject.getString("code").equals("1000"))
                        {
                            dialogActivity.dissmissProgressDialog();
                            PaymodeV2 paymodeV2 = new PaymodeV2(Order_Details.this);
                            paymodeV2.payV2(jsonobject.getString("data"),new PaymodeV2CallBack(){

                                @Override
                                public void onRequestBack(String var1,String var2) {
                                    // TODO Auto-generated method stub
                                    if(var1.equals("支付成功"))
                                    {
                                        CustomToast.showToast(getApplicationContext(),"支付成功",1000);
                                        startActivity(new Intent(Order_Details.this, AllOrderActivity.class));
                                        finish();
                                    }else CustomToast.showToast(getApplicationContext(),"支付失败",1000);
                                }

                            });
                        }else CustomToast.showToast(getApplicationContext(),"下单失败",1000);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private PayReq req;
    private StringBuffer sb;
    private final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
    private void Data1(String ip)
    {
        sb=new StringBuffer();
        req=new PayReq();
//        private String goodsName;//商品名称
//        private String goodsDetail;//商品描述
//        private Integer goodsNum;//购买数量
//        private String goodsPhoto;//商品图片
//        private Double totalFee;//总金额
//        private Double goodsPrice;//商品价格
//        private String receiver;//收件人
//        private String receiverAddress;//收件人地址
//        private String receiverZipCode;//邮编
//        private String receiverPhone;//收件人电话
//        private String userId;//用户id
//        private String userName;//用户名称
//        ip//用户ip地址
        String str = tv_zj.getText().toString();
        str = str.substring(1,str.length());
        FormBody formBody = new FormBody.Builder()
                .add("goodsId",groups.get(shucwz).getId())
                .add("sellerid", groups.get(shucwz).getMerchant())
                .add("goodsName", groups.get(shucwz).getName())
                .add("goodsDetail", groups.get(shucwz).getDetail())
                .add("goodsNum", shu+"")
                .add("goodsPhoto", groups.get(shucwz).getPhoto1())
                .add("totalFee", str)
                .add("goodsPrice", groups.get(shucwz).getPrice())
                .add("receiver", tv_name.getText().toString())
                .add("receiverAddress", tv_jianjie.getText().toString())
                .add("receiverZipCode", yb)
                .add("receiverPhone", tv_phone.getText().toString())
                .add("userId", Constant.user.getId())
                .add("userName", Constant.user.getUserName())
                .add("ip", ip)
                .build();
        try {
            Okhttp.doPost(this, Constant.URL_wxzfhq, formBody, new IRequestCallBack() {
                @Override
                public void onRequestBack(String text) {
                    JSONObject jsonobject = null;
                    try {
                        jsonobject = new JSONObject(text);
                        if (jsonobject.getString("code").equals("1000"))
                        {
                            dialogActivity.dissmissProgressDialog();
                            JSONObject jsonObject = jsonobject.getJSONObject("data");
                            Constant.APP_ID = jsonObject.getString("appid");
                            Constant.MCH_ID = jsonObject.getString("mchId");
                            req.appId = jsonObject.getString("appid");
                            req.partnerId = jsonObject.getString("mchId");
                            if(!jsonObject.getString("prepayId").equals("") && jsonObject.getString("prepayId") != null)
                            {
                                req.prepayId = jsonObject.getString("prepayId");
                                req.packageValue = "prepay_id="+jsonObject.getString("prepayId");
                            }
                            req.nonceStr = jsonObject.getString("nonceStr");
                            req.timeStamp = String.valueOf(genTimeStamp());

                            List<NameValuePair> signParams = new LinkedList<NameValuePair>();
                            signParams.add(new BasicNameValuePair("appid", req.appId));
                            signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
                            signParams.add(new BasicNameValuePair("package", req.packageValue));
                            signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
                            signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
                            signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

                            req.sign = genAppSign(signParams);
                            sendPayReq();
                        }else CustomToast.showToast(getApplicationContext(),"下单失败",1000);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    /*
     * 调起微信支付
     */
    private void sendPayReq() {
        msgApi.registerApp(Constant.APP_ID);
        msgApi.sendReq(req);
    }

    private String genAppSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(Constant.API_KEY);

        this.sb.append("sign str\n"+sb.toString()+"\n\n");
        String appSign = MD5.getMessageDigest(sb.toString().getBytes());
        return appSign;
    }
}
