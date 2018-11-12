package com.mugua.videoplayer;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.Matrix;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mugua.enterprise.R;
import com.mugua.enterprise.util.GlideRoundTransform;
import com.mugua.videoplayer.effect.BitmapIconEffect;
import com.mugua.videoplayer.effect.GSYVideoGLViewCustomRender;
import com.mugua.videoplayer.effect.PixelationEffect;
import com.mugua.videoplayer.utils.CommonUtil;
import com.mugua.videoplayer.video.SampleControlVideo;
import com.shuyu.gsyvideoplayer.GSYBaseActivityDetail;
import com.shuyu.gsyvideoplayer.GSYVideoGLView;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.effect.AutoFixEffect;
import com.shuyu.gsyvideoplayer.effect.BarrelBlurEffect;
import com.shuyu.gsyvideoplayer.effect.BlackAndWhiteEffect;
import com.shuyu.gsyvideoplayer.effect.BrightnessEffect;
import com.shuyu.gsyvideoplayer.effect.ContrastEffect;
import com.shuyu.gsyvideoplayer.effect.CrossProcessEffect;
import com.shuyu.gsyvideoplayer.effect.DocumentaryEffect;
import com.shuyu.gsyvideoplayer.effect.DuotoneEffect;
import com.shuyu.gsyvideoplayer.effect.FillLightEffect;
import com.shuyu.gsyvideoplayer.effect.GammaEffect;
import com.shuyu.gsyvideoplayer.effect.GaussianBlurEffect;
import com.shuyu.gsyvideoplayer.effect.GrainEffect;
import com.shuyu.gsyvideoplayer.effect.HueEffect;
import com.shuyu.gsyvideoplayer.effect.InvertColorsEffect;
import com.shuyu.gsyvideoplayer.effect.LamoishEffect;
import com.shuyu.gsyvideoplayer.effect.NoEffect;
import com.shuyu.gsyvideoplayer.effect.OverlayEffect;
import com.shuyu.gsyvideoplayer.effect.PosterizeEffect;
import com.shuyu.gsyvideoplayer.effect.SampleBlurEffect;
import com.shuyu.gsyvideoplayer.effect.SaturationEffect;
import com.shuyu.gsyvideoplayer.effect.SepiaEffect;
import com.shuyu.gsyvideoplayer.effect.SharpnessEffect;
import com.shuyu.gsyvideoplayer.effect.TemperatureEffect;
import com.shuyu.gsyvideoplayer.effect.TintEffect;
import com.shuyu.gsyvideoplayer.effect.VignetteEffect;
import com.shuyu.gsyvideoplayer.listener.GSYVideoShotListener;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;

import java.io.FileNotFoundException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 滤镜
 * Activity可以继承GSYBaseActivityDetail实现详情模式的页面
 * 或者参考DetailPlayer、DetailListPlayer实现
 * Created by guoshuyu on 2017/6/18.
 */

public class DetailFilterActivity extends GSYBaseActivityDetail {

    @BindView(R.id.post_detail_nested_scroll)
    NestedScrollView postDetailNestedScroll;

    @BindView(R.id.detail_player)
    SampleControlVideo detailPlayer;

    @BindView(R.id.activity_detail_player)
    RelativeLayout activityDetailPlayer;

    @BindView(R.id.change_filter)
    Button changeFilter;


    @BindView(R.id.jump)
    Button jump;

    @BindView(R.id.change_anima)
    Button anima;


    private int type = 0;

    private int backupRendType;

    private float deep = 0.8f;

    private String url = "http://123.56.5.178:8888/group1/M00/00/00/rBGp8FobwtmAT1bfAFpKsXmDqCg018.mp4";

    private Timer timer = new Timer();

    private TaskLocal mTimerTask;

    private TaskLocal2 mTimerTask2;

    private GSYVideoGLViewCustomRender mGSYVideoGLViewCustomRender;

    private BitmapIconEffect mCustomBitmapIconEffect;

    private int percentage = 1;

    private int percentageType = 1;

    private boolean moveBitmap = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_filter);
        ButterKnife.bind(this);

        backupRendType = GSYVideoType.getRenderType();

        //设置为GL播放模式，才能支持滤镜，注意此设置是全局的
        GSYVideoType.setRenderType(GSYVideoType.GLSURFACE);

        resolveNormalVideoUI();

        initVideoBuilderMode();

        detailPlayer.setLockClickListener(new LockClickListener() {
            @Override
            public void onClick(View view, boolean lock) {
                if (orientationUtils != null) {
                    //配合下方的onConfigurationChanged
                    orientationUtils.setEnable(!lock);
                }
            }
        });


        //自定义render需要在播放器开始播放之前，播放过程中不允许切换render

        //水印图效果
        /*Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        mGSYVideoGLViewCustomRender = new GSYVideoGLViewCustomRender();
        mCustomBitmapIconEffect = new BitmapIconEffect(bitmap, dp2px(50), dp2px(50), 0.6f);
        mGSYVideoGLViewCustomRender.setBitmapEffect(mCustomBitmapIconEffect);
        detailPlayer.setCustomGLRenderer(mGSYVideoGLViewCustomRender);*/

        //多窗口播放效果
        //detailPlayer.setEffectFilter(new GammaEffect(0.8f));
        //detailPlayer.setCustomGLRenderer(new GSYVideoGLViewCustomRender2());

        //图片穿孔透视播放
        //detailPlayer.setCustomGLRenderer(new GSYVideoGLViewCustomRender3());

        changeFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resolveTypeUI();
            }
        });

        //使用GL播放的话，用这种方式可以解决退出全屏黑屏的问题
        detailPlayer.setBackFromFullScreenListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailFilterActivity.this.onBackPressed();
            }
        });


        jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shotImage(v);
                //JumpUtils.gotoControl(DetailFilterActivity.this);
                //startActivity(new Intent(DetailControlActivity.this, MainActivity.class));
            }
        });

        anima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //画面旋转
                cancelTask();
                mTimerTask = new TaskLocal();
                timer.schedule(mTimerTask, 0, 50);
                percentageType++;
                if (percentageType > 4) {
                    percentageType = 1;
                }
                //水印图动起来
                /*cancelTask2();
                mTimerTask2 = new TaskLocal2();
                timer.schedule(mTimerTask2, 0, 400);

                moveBitmap = !moveBitmap;*/
            }
        });
    }

    @Override
    public GSYBaseVideoPlayer getGSYVideoPlayer() {
        return detailPlayer;
    }

    @Override
    public GSYVideoOptionBuilder getGSYVideoOptionBuilder() {
        //内置封面可参考SampleCoverVideo
        ImageView imageView = new ImageView(this);
        loadCover(imageView, url);
        return new GSYVideoOptionBuilder()
                .setThumbImageView(imageView)
                .setUrl(url)
                .setCacheWithPlay(true)
                .setVideoTitle(" ")
                .setIsTouchWiget(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setShowFullAnimation(false)
                .setNeedLockFull(true)
                .setSeekRatio(1);
    }

    @Override
    public void clickForFullScreen() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GSYVideoType.setRenderType(backupRendType);
        cancelTask();
    }

    /**
     * 视频截图
     */
    private void shotImage(final View v) {
        //获取截图
        detailPlayer.taskShotPic(new GSYVideoShotListener() {
            @Override
            public void getBitmap(Bitmap bitmap) {
                if (bitmap != null) {
                    try {
                        CommonUtil.saveBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        showToast("save fail ");
                        e.printStackTrace();
                        return;
                    }
                    showToast("save success ");
                } else {
                    showToast("get bitmap fail ");
                }
            }
        });

    }

    /**
     * 加载第三秒的帧数作为封面
     */
    private void loadCover(ImageView imageView, String url) {

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(R.mipmap.xxx1);

        Glide.with(this.getApplicationContext())
                .load(url)
                .placeholder(R.mipmap.xxx1)
                .error(R.mipmap.xxx2)
                .centerCrop()
                .crossFade()
                .transform(new GlideRoundTransform(this.getApplicationContext(), 10))
                .into(imageView);
    }

    private void resolveNormalVideoUI() {
        //增加title
        detailPlayer.getTitleTextView().setVisibility(View.GONE);
        detailPlayer.getBackButton().setVisibility(View.GONE);
    }

    /**
     * 切换滤镜
     */
    private void resolveTypeUI() {
        GSYVideoGLView.ShaderInterface effect = new NoEffect();
        switch (type) {
            case 0:
                effect = new AutoFixEffect(deep);
                break;
            case 1:
                effect = new PixelationEffect();
                break;
            case 2:
                effect = new BlackAndWhiteEffect();
                break;
            case 3:
                effect = new ContrastEffect(deep);
                break;
            case 4:
                effect = new CrossProcessEffect();
                break;
            case 5:
                effect = new DocumentaryEffect();
                break;
            case 6:
                effect = new DuotoneEffect(Color.BLUE, Color.YELLOW);
                break;
            case 7:
                effect = new FillLightEffect(deep);
                break;
            case 8:
                effect = new GammaEffect(deep);
                break;
            case 9:
                effect = new GrainEffect(deep);
                break;
            case 10:
                effect = new GrainEffect(deep);
                break;
            case 11:
                effect = new HueEffect(deep);
                break;
            case 12:
                effect = new InvertColorsEffect();
                break;
            case 13:
                effect = new LamoishEffect();
                break;
            case 14:
                effect = new PosterizeEffect();
                break;
            case 15:
                effect = new BarrelBlurEffect();
                break;
            case 16:
                effect = new SaturationEffect(deep);
                break;
            case 17:
                effect = new SepiaEffect();
                break;
            case 18:
                effect = new SharpnessEffect(deep);
                break;
            case 19:
                effect = new TemperatureEffect(deep);
                break;
            case 20:
                effect = new TintEffect(Color.GREEN);
                break;
            case 21:
                effect = new VignetteEffect(deep);
                break;
            case 22:
                effect = new NoEffect();
                break;
            case 23:
                effect = new OverlayEffect();
                break;
            case 24:
                effect = new SampleBlurEffect(4.0f);
                break;
            case 25:
                effect = new GaussianBlurEffect(6.0f, GaussianBlurEffect.TYPEXY);
                break;
            case 26:
                effect = new BrightnessEffect(deep);
                break;
        }
        detailPlayer.setEffectFilter(effect);
        type++;
        if (type > 25) {
            type = 0;
        }
    }


    private void cancelTask2() {
        if (mTimerTask2 != null) {
            mTimerTask2.cancel();
            mTimerTask2 = null;
        }
    }

    private void cancelTask() {
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }


    /**
     * 水印图动起来,播放前开始会崩溃哟
     */
    private class TaskLocal2 extends TimerTask {
        @Override
        public void run() {
            float[] transform = new float[16];
            //旋转到正常角度
            Matrix.setRotateM(transform, 0, 180f, 0.0f, 0, 1.0f);
            //调整大小比例
            Matrix.scaleM(transform, 0, mCustomBitmapIconEffect.getScaleW(), mCustomBitmapIconEffect.getScaleH(), 1);
            if (moveBitmap) {
                //调整位置
                Matrix.translateM(transform, 0, mCustomBitmapIconEffect.getPositionX(), mCustomBitmapIconEffect.getPositionY(), 0f);
            } else {
                float maxX = mCustomBitmapIconEffect.getMaxPositionX();
                float minX = mCustomBitmapIconEffect.getMinPositionX();
                float maxY = mCustomBitmapIconEffect.getMaxPositionY();
                float minY = mCustomBitmapIconEffect.getMinPositionY();
                float x = (float) Math.random() * (maxX - minX) + minX;
                float y = (float) Math.random() * (maxY - minY) + minY;
                //调整位置
                Matrix.translateM(transform, 0, x, y, 0f);
                mGSYVideoGLViewCustomRender.setCurrentMVPMatrix(transform);
            }
        }
    }


    /**
     * 设置GLRender的VertexShader的transformMatrix
     * 注意，这是android.opengl.Matrix
     */
    private class TaskLocal extends TimerTask {
        @Override
        public void run() {
            float[] transform = new float[16];
            switch (percentageType) {
                case 1:
                    //给予x变化
                    Matrix.setRotateM(transform, 0, 360 * percentage / 100, 1.0f, 0, 0.0f);
                    break;
                case 2:
                    //给予y变化
                    Matrix.setRotateM(transform, 0, 360 * percentage / 100, 0.0f, 1.0f, 0.0f);
                    break;
                case 3:
                    //给予z变化
                    Matrix.setRotateM(transform, 0, 360 * percentage / 100, 0.0f, 0, 1.0f);
                    break;
                case 4:
                    Matrix.setRotateM(transform, 0, 360, 0.0f, 0, 1.0f);
                    break;
            }
            //设置渲染transform
            detailPlayer.setMatrixGL(transform);
            percentage++;
            if (percentage > 100) {
                percentage = 1;
            }
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    private void showToast(final String tip) {
        detailPlayer.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DetailFilterActivity.this, tip, Toast.LENGTH_LONG).show();
            }
        });
    }
}
