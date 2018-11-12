package com.mugua.videoplayer;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mugua.enterprise.R;
import com.bumptech.glide.Glide;
import com.mugua.enterprise.util.GlideRoundTransform;
import com.mugua.videoplayer.video.PreViewGSYVideoPlayer;
import com.mugua.videoplayer.view.ScrollWebView;
import com.shuyu.gsyvideoplayer.GSYBaseActivityDetail;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by shuyu on 2016/12/26.
 */

public class WebDetailActivity extends GSYBaseActivityDetail {

    @BindView(R.id.scroll_webView)
    ScrollWebView webView;
    @BindView(R.id.web_player)
    PreViewGSYVideoPlayer webPlayer;
    @BindView(R.id.web_top_layout)
    NestedScrollView webTopLayout;
    @BindView(R.id.web_top_layout_video)
    RelativeLayout webTopLayoutVideo;

    private boolean isSamll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_detail);
        ButterKnife.bind(this);


        resolveNormalVideoUI();

        initVideoBuilderMode();


        webPlayer.setLockClickListener(new LockClickListener() {
            @Override
            public void onClick(View view, boolean lock) {
                if (orientationUtils != null) {
                    //配合下方的onConfigurationChanged
                    orientationUtils.setEnable(!lock);
                }
            }
        });


        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.loadUrl("https://www.baidu.com");

        webTopLayout.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (!webPlayer.isIfCurrentIsFullscreen() && scrollY >= 0 && isPlay) {
                    if (scrollY > webPlayer.getHeight()) {
                        //如果是小窗口就不需要处理
                        if (!isSamll) {
                            isSamll = true;
                            int size = CommonUtil.dip2px(WebDetailActivity.this, 150);
                            webPlayer.showSmallVideo(new Point(size, size), true, true);
                            orientationUtils.setEnable(false);
                        }
                    } else {
                        if (isSamll) {
                            isSamll = false;
                            orientationUtils.setEnable(true);
                            //必须
                            webTopLayoutVideo.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    webPlayer.hideSmallVideo();
                                }
                            }, 50);
                        }
                    }
                    webTopLayoutVideo.setTranslationY((scrollY <= webTopLayoutVideo.getHeight()) ? -scrollY : -webTopLayoutVideo.getHeight());
                }
            }
        });

    }

    @Override
    public GSYBaseVideoPlayer getGSYVideoPlayer() {
        return webPlayer;
    }

    @Override
    public GSYVideoOptionBuilder getGSYVideoOptionBuilder() {
        String url = "http://123.56.5.178:8888/group1/M00/00/00/rBGp8FobwtmAT1bfAFpKsXmDqCg018.mp4";
        //String url = "https://d131x7vzzf85jg.cloudfront.net/upload/documents/paper/b2/61/00/00/20160420_115018_b544.mp4";
        //增加封面。内置封面可参考SampleCoverVideo
        ImageView imageView = new ImageView(this);
        loadCover(imageView, url);
        return new GSYVideoOptionBuilder()
                .setThumbImageView(imageView)
                .setUrl(url)
                .setCacheWithPlay(false)
                .setVideoTitle("测试视频")
                .setIsTouchWiget(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setShowFullAnimation(false)
                .setNeedLockFull(true);
    }

    @Override
    public void clickForFullScreen() {

    }


    private void loadCover(ImageView imageView, String url) {

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(R.mipmap.xxx1);

        Glide.with(this.getApplicationContext())
                .load(url)
                .placeholder(com.mugua.videoplayer.R.mipmap.xxx1)
                .error(com.mugua.videoplayer.R.mipmap.xxx2)
                .centerCrop()
                .crossFade()
                .transform(new GlideRoundTransform(this.getApplicationContext(), 10))
                .into(imageView);
    }


    private void resolveNormalVideoUI() {
        //增加title
        webPlayer.getTitleTextView().setVisibility(View.GONE);
        webPlayer.getBackButton().setVisibility(View.GONE);
    }
}
