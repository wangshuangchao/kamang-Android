package com.mugua.enterprise.util;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mugua.enterprise.R;
import com.mugua.enterprise.fragment.HomeFragment;
import com.mugua.enterprise.fragment.MeFragment;
import com.mugua.enterprise.util.httppost.IRequestCallBack;
import com.mugua.enterprise.util.toast.CustomToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class CardDialog extends Dialog{
    public static CardDialog instance = null;
    private Activity mContext;
    private OnCloseListener listener;
    private int data;
    private int xsopen;
    private String id;

    public CardDialog(Activity context, int data, OnCloseListener listener) {
        super(context);
        this.mContext = context;
        this.listener = listener;
        this.data = data;
    }

    public CardDialog(Activity context, int themeResId, int data, int xsopen, String id,boolean back,OnCloseListener listener) {
        super(context, themeResId);
        this.mContext = context;
        this.data = data;
        this.xsopen = xsopen;
        this.listener = listener;
        this.id = id;
        this.back = back;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(data, null);
        setContentView(view);
        setCanceledOnTouchOutside(true);
        if(xsopen == 2)
        {
            yhshuju(view);
        }
        else if(xsopen == 4)
        {
            final ImageView img_tupian = (ImageView)view.findViewById(R.id.img_tupian);
            final TextView tv_cy = (TextView)view.findViewById(R.id.tv_cy);
            ImageAsyncTask imageAsyncTask = new ImageAsyncTask(HomeFragment.instance.homeBean.activity.getPhoto());
            imageAsyncTask.data(new ImageAsyncTask.OnCloseListener() {
                @Override
                public void onClick(Bitmap bitmap) {
                    img_tupian.setImageBitmap(bitmap);
                    tv_cy.setVisibility(View.VISIBLE);
                }
            });
//            Glide.with(mContext)
//                    .load(HomeFragment.instance.homeBean.activity.getPhoto())
//                    .error(R.drawable.zwt)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .transform(new GlideRoundTransform(mContext, 20))
//                    .into(img_tupian);
        }
        listener.onClick(this, view);
    }

    private ImageView img_tx1;
    public RelativeLayout rl_2;
    public int width ,height;
    public Bitmap bitmap1;
    private void yhshuju(final View view)
    {
        String www = "?id="+id;
        try {
            Okhttp.doGet(mContext, Constant.URL_getCardShow+www, new IRequestCallBack() {
                @Override
                public void onRequestBack(String text) {
                    JSONObject jsonobject = null;
                    try {
                        jsonobject = new JSONObject(text);
                        if (jsonobject.getString("code").equals("1000"))
                        {
                            final JSONObject jsonObject = jsonobject.getJSONObject("data");

                            img_tx1 = (ImageView)view.findViewById(R.id.img_tx1);
                            ImageAsyncTask imageAsyncTask = new ImageAsyncTask(jsonObject.getString("photoUrl"));

                            TextView tv_localize = (TextView)view.findViewById(R.id.tv_localize);
                            tv_localize.setMaxLines(2);
                            tv_localize.setText(jsonObject.getString("companyAddr"));
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(360, ViewGroup.LayoutParams.WRAP_CONTENT);
                            lp.setMargins(24,0,0,0);//4个参数按顺序分别是左上右下
                            tv_localize.setLayoutParams(lp);

                            TextView tv_phone = (TextView)view.findViewById(R.id.tv_phone);
                            tv_phone.setText(jsonObject.getString("legalPersonPhone"));

                            TextView tv_username = (TextView)view.findViewById(R.id.tv_username);
                            tv_username.setText(jsonObject.getString("userName"));

                            TextView tv_company = (TextView)view.findViewById(R.id.tv_company);
                            tv_company.setText(jsonObject.getString("company"));

                            TextView tv_scope = (TextView)view.findViewById(R.id.tv_scope);
                            tv_scope.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
                            tv_scope.setEllipsize(TextUtils.TruncateAt.END);
                            tv_scope.setText(jsonObject.getString("scopeOfBusiness"));

                            ImageView img_ewm = (ImageView)view.findViewById(R.id.img_ewm);
                            Bitmap bitmap = ZXingUtils.createQRImage(jsonObject.getString("qrcode"),800,800);
                            img_ewm.setImageBitmap(bitmap);

                            rl_2 = (RelativeLayout) view.findViewById(R.id.rl_2);
                            width = Canonical.getDisplayWidth(mContext);
                            height = (int) (width*0.625);

                            imageAsyncTask.data(new ImageAsyncTask.OnCloseListener()
                            {
                                @Override
                                public void onClick(Bitmap bitmap) {
                                    img_tx1.setImageBitmap(bitmap);
                                    bitmap1 = Canonical.getViewBitmap(rl_2,width,height);
                                }
                            });

                            CustomToast.showToast(mContext,jsonobject.getString("msg"),1000);

                        }
                        else CustomToast.showToast(mContext,jsonobject.getString("msg"),1000);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean back;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(back == true) {
                MeFragment.instance.dialog1 = this;
                MeFragment.instance.renzheng();
            }
            else {
                dismiss();
                if(HomeFragment.instance != null)
                    HomeFragment.instance.fab.setVisibility(View.VISIBLE);
            }
        }
        return false;
    }

    public interface OnCloseListener{
        void onClick(Dialog dialog, View view);
    }
}
