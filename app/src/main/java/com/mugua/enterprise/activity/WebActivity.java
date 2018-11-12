package com.mugua.enterprise.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mugua.enterprise.BeasActivity;
import com.mugua.enterprise.R;

public class WebActivity extends BeasActivity {
    private WebView webview;
    private LinearLayout linearLayout;
    private RelativeLayout rl_back;
    private TextView tv_title;
    private TextView tv;
    private Bundle bundle;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        bundle = getIntent().getExtras();

        linearLayout = (LinearLayout)findViewById(R.id.linearLayout);
        linearLayout.getBackground().setAlpha(210);
        tv = (TextView)this.findViewById(R.id.tv);
        tv.setText("加载中...");

        rl_back = (RelativeLayout)findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText(bundle.getString("title"));

        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setBlockNetworkImage(false);
//        webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

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
    }

    //改写物理按键——返回的逻辑
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
