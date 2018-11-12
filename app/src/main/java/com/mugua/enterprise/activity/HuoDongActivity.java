package com.mugua.enterprise.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mugua.enterprise.BeasActivity;
import com.mugua.enterprise.R;
import com.mugua.enterprise.activity.me.CardActivity;
import com.mugua.enterprise.activity.me.EnterpriseCertificationActivity;
import com.mugua.enterprise.bean.HomeBean;
import com.mugua.enterprise.fragment.HomeFragment;
import com.mugua.enterprise.fragment.MeFragment;
import com.mugua.enterprise.login.LoginActivity;
import com.mugua.enterprise.util.CardDialog;
import com.mugua.enterprise.util.Constant;
import com.mugua.enterprise.util.Okhttp;
import com.mugua.enterprise.util.ScreenUtils;
import com.mugua.enterprise.util.httppost.IRequestCallBack;
import com.squareup.picasso.Picasso;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.utils.Log;
import com.zhy.autolayout.AutoLayoutActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;
import static com.squareup.picasso.Picasso.Priority.HIGH;

/**
 * Created by Lenovo on 2018/1/27.
 */

public class HuoDongActivity extends BeasActivity implements View.OnClickListener {
    public static HuoDongActivity instance = null;
    @BindView( R.id.rl_back )
    public RelativeLayout rl_back ;
    @BindView( R.id.rl_fx )
    public RelativeLayout rl_fx ;

    @BindView( R.id.tv_cyhd )
    public TextView tv_cyhd ;

//    @BindView( R.id.tu )
//    public ImageView tu ;
    private HomeBean dataBean;

    private WebView webview;
    private LinearLayout linearLayout;
    private TextView tv_title;
    private TextView tv;
    private Bundle bundle;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huodong);
        instance = this;
        //绑定fragment
        ButterKnife.bind(this);

        bundle = getIntent().getExtras();
        linearLayout = (LinearLayout)findViewById(R.id.linearLayout);
        linearLayout.getBackground().setAlpha(210);
        tv = (TextView)this.findViewById(R.id.tv);
        tv.setText("加载中...");
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText(bundle.getString("title"));

        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setBlockNetworkImage(false);
        webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        //WebView加载web资源
        webview.loadUrl(bundle.getString("url"));
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });

        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO Auto-generated method stub
                if (newProgress == 100) {
                    // 网页加载完成
                    linearLayout.setVisibility(View.GONE);
                } else {
                    // 加载中
                    linearLayout.setVisibility(View.VISIBLE);
                }

            }
        });

//        int w = ScreenUtils.getScreenWidth(this);
//        Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.mipmap.huodong);
//        if (tu.getScaleType() != ImageView.ScaleType.FIT_XY) {
//            tu.setScaleType(ImageView.ScaleType.FIT_XY);
//        }
//        ViewGroup.LayoutParams params = tu.getLayoutParams();
//        params.width = w;
//        params.height = bmp.getHeight()/bmp.getWidth()*w;
//        tu.setLayoutParams(params);
//        tu.setBackgroundResource(R.mipmap.huodong);

        dataBean = HomeFragment.instance.homeBean;
        rl_back.setOnClickListener(this);
        rl_fx.setOnClickListener(this);
        tv_cyhd.setOnClickListener(this);
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
                    }
                });

                view.findViewById(R.id.tv_bg).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        }).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_cyhd:
                if(Constant.user != null) {
                    if(Constant.user.getIsCentification().equals("0") || Constant.user.getIsCentification().equals("2"))
                        startActivity(new Intent(HuoDongActivity.this, EnterpriseCertificationActivity.class));
                    else if(Constant.user.getIsCentification().equals("1"))
                    {
                        if(Constant.user.getExts().equals("0"))//未创建名片
                        {
                            Constant.open4 = true;
                            Intent intent = new Intent(HuoDongActivity.this, CardActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("id", Constant.user.getId());
                            bundle.putString("kind", "0");
                            bundle.putString("title", "创建名片");
                            bundle.putString("btn", "创建名片");
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                        else {
                            Constant.open4 = true;
                            Intent intent = new Intent(HuoDongActivity.this, CardActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("id", Constant.user.getId());
                            bundle.putString("kind", "1");
                            bundle.putString("title", "修改名片");
                            bundle.putString("btn", "确认修改");
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }
                    else if(Constant.user.getIsCentification().equals("3"))
                    {
                        finish1();
                    }
                }
                else {
                    startActivity(new Intent(HuoDongActivity.this, LoginActivity.class));
                }
                break;
            case R.id.rl_fx:
                UMWeb web = new UMWeb(dataBean.activity.getUrl());//连接地址
                web.setTitle(dataBean.activity.getTitle());//标题
                web.setDescription(dataBean.activity.getContent());//描述
                if (TextUtils.isEmpty(dataBean.activity.getPhoto())) {
                    web.setThumb(new UMImage(this, R.mipmap.guanyu_tu));  //本地缩略图
                } else {
                    web.setThumb(new UMImage(this, dataBean.activity.getPhoto()));  //网络缩略图
                }
                new ShareAction(this)
                        .setDisplayList(SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE)
                        .withMedia(web)
                        .setCallback(umShareListener)
                        .open();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return false;
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat","platform"+platform);
            if(platform.name().equals("WEIXIN_FAVORITE")){
                Toast.makeText(getApplicationContext()," 收藏成功",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(), " 分享成功", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(getApplicationContext()," 分享失败", Toast.LENGTH_SHORT).show();
            if(t!=null){
                Log.d("throw","throw:"+t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(getApplicationContext()," 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(getApplicationContext()).onActivityResult(requestCode, resultCode, data);
    }
}
