package com.mugua.videoplayer.video;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mugua.enterprise.R;
import com.mugua.enterprise.util.GlideRoundTransform;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;

/**
 * 带封面
 * Created by guoshuyu on 2017/9/3.
 */

public class SampleCoverVideo extends StandardGSYVideoPlayer {

    ImageView mCoverImage;

    String mUrl;

    int mDefaultRes;

    public SampleCoverVideo(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public SampleCoverVideo(Context context) {
        super(context);
    }

    public SampleCoverVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        mCoverImage = (ImageView) findViewById(R.id.thumbImage);
    }

    @Override
    public int getLayoutId() {
        return R.layout.video_layout_cover;
    }

    public void loadCoverImage(String url, int res) {
        mUrl = url;
        mDefaultRes = res;
        Glide.with(getContext().getApplicationContext())
                .load(url)
                .placeholder(res)
                .error(res)
                .centerCrop()
                .crossFade()
                .transform(new GlideRoundTransform(getContext().getApplicationContext(), 10))
                .into(mCoverImage);
    }

    @Override
    public GSYBaseVideoPlayer startWindowFullscreen(Context context, boolean actionBar, boolean statusBar) {
        GSYBaseVideoPlayer gsyBaseVideoPlayer = super.startWindowFullscreen(context, actionBar, statusBar);
        SampleCoverVideo sampleCoverVideo = (SampleCoverVideo) gsyBaseVideoPlayer;
        sampleCoverVideo.loadCoverImage(mUrl, mDefaultRes);
        return gsyBaseVideoPlayer;
    }
}
