package com.mugua.enterprise.activity.me;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.vod.upload.VODSVideoUploadCallback;
import com.alibaba.sdk.android.vod.upload.VODSVideoUploadClient;
import com.alibaba.sdk.android.vod.upload.VODSVideoUploadClientImpl;
import com.alibaba.sdk.android.vod.upload.model.SvideoInfo;
import com.alibaba.sdk.android.vod.upload.session.VodHttpClientConfig;
import com.alibaba.sdk.android.vod.upload.session.VodSessionCreateInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mugua.enterprise.BeasActivity;
import com.mugua.enterprise.MainActivity;
import com.mugua.enterprise.R;
import com.mugua.enterprise.util.Canonical;
import com.mugua.enterprise.util.Constant;
import com.mugua.enterprise.util.CountDownTimerUtils1;
import com.mugua.enterprise.util.DialogActivity;
import com.mugua.enterprise.util.GlideRoundTransform;
import com.mugua.enterprise.util.ImageUtils;
import com.mugua.enterprise.util.Okhttp;
import com.mugua.enterprise.util.ShareDialog;
import com.mugua.enterprise.util.httppost.IRequestCallBack;
import com.mugua.enterprise.util.toast.CustomToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lenovo on 2017/12/15.
 */

public class AddVideoActivity extends BeasActivity implements View.OnClickListener {
    public static AddVideoActivity instance = null;
    VODSVideoUploadClient vodsVideoUploadClient;
    private long progress;

    @BindView( R.id.rl_back )
    public RelativeLayout rl_back ;

    @BindView( R.id.img_voide )
    public ImageView img_voide ;

    @BindView( R.id.img_fmvoide )
    public ImageView img_fmvoide ;

    @BindView( R.id.tv_progress )
    public TextView tv_progress ;

    @BindView( R.id.btn_addvideo )
    public Button btn_addvideo ;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addvideo);
        instance = this;
        //绑定fragment
        ButterKnife.bind(this);
        shuju();
        rl_back.setOnClickListener(this);
        img_voide.setOnClickListener(this);
        img_fmvoide.setOnClickListener(this);
        btn_addvideo.setOnClickListener(this);
    }

    private void shuju()
    {
        if(Constant.open == false)
        {
            try {
                Okhttp.doGet(AddVideoActivity.this, Constant.URL_roleSessionName+Constant.user.getUserPhone(), new IRequestCallBack() {
                    @Override
                    public void onRequestBack(String text) {
                        JSONObject jsonobject = null;
                        try {
                            jsonobject = new JSONObject(text);
                            Constant.accessKeyId = jsonobject.getString("accessKeyId");
                            Constant.accessKeySecret = jsonobject.getString("accessKeySecret");
                            Constant.securityToken = jsonobject.getString("securityToken");
                            Constant.expriedTime = jsonobject.getString("expiration");
                            if(!Constant.accessKeyId.equals("") && !Constant.accessKeySecret.equals("")
                                    && !Constant.securityToken.equals("") && !Constant.expriedTime.equals("")) {
                                MainActivity.instance.mCountDownTimerUtils.start();
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
        vodsVideoUploadClient = new VODSVideoUploadClientImpl(this);
        vodsVideoUploadClient.init();
    }

    private void updown()
    {
        if(bitmap != null) {
            // 进度条还有二级进度条的那种形式，这里就不演示了
            dialog = new ProgressDialog(this);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
            dialog.setCancelable(false);// 设置是否可以通过点击Back键取消
            dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "取消",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            vodsVideoUploadClient.cancel();
                            dialog.dismiss();
                        }
                    });
            dialog.setMax(100);
            dialog.setMessage("上传进度");
            dialog.show();
            data(imagePath, path);
        }
        else CustomToast.showToast(getApplicationContext(),"请上传封面图",1000);
    }

    private String path = "";
    private String path1;
    private ProgressDialog dialog;
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.img_fmvoide:
                ImageUtils.takeOrChoosePhoto(this, ImageUtils.TAKE_OR_CHOOSE_PHOTO);
                break;
            case R.id.btn_addvideo:
                if(isshipin == true)
                    updown();
                else CustomToast.showToast(getApplicationContext(),"请上传视频",1000);
                break;
            case R.id.rl_back:
                finish();
                break;
            case R.id.img_voide:
                new ShareDialog(AddVideoActivity.this,R.layout.add_video, R.style.dialog, new ShareDialog.OnItemClickListener() {
                    @Override
                    public void onClick(final Dialog dialog, View view) {
                        view.findViewById(R.id.tv_lz).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                // 激活系统的照相机进行录像
                                Intent intent = new Intent();
                                intent.setAction("android.media.action.VIDEO_CAPTURE");
                                intent.addCategory("android.intent.category.DEFAULT");
                                path = getSDPath();
                                if (path != null) {
                                    File dir = new File(path + "/DCIM/Camera");
                                    if (!dir.exists()) {
                                        dir.mkdir();
                                    }
                                    path1 = getDate() + ".mp4";
                                    path = dir + "/" + path1;
                                }
                                // 保存录像到指定的路径
                                File file = new File(path);
                                Uri uri = Uri.fromFile(file);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                                //设置视频录制的最长时间
                                intent.putExtra (MediaStore.EXTRA_DURATION_LIMIT,300);
                                //设置视频录制的画质
                                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                                startActivityForResult(intent, 110);
                            }
                        });
                        view.findViewById(R.id.tv_bd).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                Intent intent = new Intent();
                                intent.setType("video/*"); //选择视频
                                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                                startActivityForResult(intent, 111);
                            }
                        });
                        view.findViewById(R.id.tv_qx).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        view.findViewById(R.id.rl_qb).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                    }
                }).show();
                break;
        }
    }

    /**
     * 获取SD path
     *
     * @return
     */
    public String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
            return sdDir.toString();
        }

        return null;
    }

    /**
     * 获取系统时间
     *
     * @return
     */
    public String getDate() {
        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);           // 获取年份
        int month = ca.get(Calendar.MONTH);         // 获取月份
        int day = ca.get(Calendar.DATE);            // 获取日
        int minute = ca.get(Calendar.MINUTE);       // 分
        int hour = ca.get(Calendar.HOUR);           // 小时
        int second = ca.get(Calendar.SECOND);       // 秒

        String date = "" + year + (month + 1) + day + hour + minute + second;
        return date;
    }

    private Bitmap bitmap;
    private boolean isshipin;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 110:
                    File file = new File(path);
                    Uri localUri = Uri.fromFile(file);
                    Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
                    sendBroadcast(localIntent);

                    Glide.with(AddVideoActivity.this)
                            .load(file)
                            .placeholder(R.drawable.zwt)
                            .error(R.drawable.zwt)
                            .dontAnimate()
                            .transform(new GlideRoundTransform(AddVideoActivity.this, 10))
//                            .centerCrop()
//                            .crossFade()
                            .into(img_voide);
                    isshipin = true;
                    break;
                case 111:
                    //获取到了原始图片
                    File f1 = ImageUtils.getPhotoFromResult(this, data);
                    if(f1 == null)
                        f1 = ImageUtils.mCurrentPhotoFile;
                    Glide.with(AddVideoActivity.this)
                            .load(f1)
                            .placeholder(R.drawable.zwt)
                            .error(R.drawable.zwt)
                            .dontAnimate()
                            .transform(new GlideRoundTransform(AddVideoActivity.this, 10))
//                            .centerCrop()
//                            .crossFade()
                            .into(img_voide);
                    path = f1.getPath();
                    isshipin = true;
                    break;
                case ImageUtils.TAKE_OR_CHOOSE_PHOTO:
                    //获取到了原始图片
                    File f = ImageUtils.getPhotoFromResult(this, data);
                    if(f == null)
                        f = ImageUtils.mCurrentPhotoFile;
                    DisplayMetrics metric = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metric);
                    int width = metric.widthPixels;     // 屏幕宽度（像素）
                    int height = metric.heightPixels;   // 屏幕高度（像素）
                    //裁剪方法
                    ImageUtils.doCropPhoto(this, f, 1,1);
                    break;
                case ImageUtils.PHOTO_PICKED_WITH_DATA:
                    bitmap = ImageUtils.getCroppedImage();
                    if(bitmap != null) {
                        Glide.with(this)
                                .load(ImageUtils.mTempCropFile.getAbsolutePath())
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .transform(new GlideRoundTransform(AddVideoActivity.this, 10))
                                .into(img_fmvoide);
                        imagePath = ImageUtils.mTempCropFile.getAbsolutePath();
                    }

                    break;
            }
        }
    }

    private String imagePath;//封面图片路径
    public void data(String imagePath,String videoPath)
    {
        //参数请确保存在，如不存在SDK内部将会直接将错误throw Exception
        // 文件路径保证存在之外因为Android 6.0之后需要动态获取权限，请开发者自行实现获取"文件读写权限".
        VodHttpClientConfig vodHttpClientConfig = new VodHttpClientConfig.Builder()
                .setMaxRetryCount(2)
                .setConnectionTimeout(15 * 1000)
                .setSocketTimeout(15 * 1000)
                .build();

        SvideoInfo svideoInfo = new SvideoInfo();
        svideoInfo.setTitle(new File(videoPath).getName());
//        svideoInfo.setDesc("李勇");
        svideoInfo.setCateId(1);

        VodSessionCreateInfo vodSessionCreateInfo =new  VodSessionCreateInfo.Builder()
                .setImagePath(imagePath)
                .setVideoPath(videoPath)
                .setAccessKeyId(Constant.accessKeyId)
                .setAccessKeySecret(Constant.accessKeySecret)
                .setSecurityToken(Constant.securityToken)
                .setExpriedTime(Constant.expriedTime)
                .setIsTranscode(true)
                .setSvideoInfo(svideoInfo)
                .setVodHttpClientConfig(vodHttpClientConfig)
                .build();
        vodsVideoUploadClient.uploadWithVideoAndImg(vodSessionCreateInfo, new VODSVideoUploadCallback() {
            @Override
            public void onUploadSucceed(String videoId, String imageUrl) {
                finish();
                dialog.dismiss();
                CardActivity.instance.videoId = videoId;
                CardActivity.instance.microblog = imageUrl;
                CardActivity.instance.bitmap = bitmap;
                CardActivity.instance.data();
            }

            @Override
            public void onUploadFailed(String code, String message) {
            }

            @Override
            public void onUploadProgress(long uploadedSize, long totalSize) {
                progress = uploadedSize * 100 / totalSize;
                handler.sendEmptyMessage(0);
            }

            @Override
            public void onSTSTokenExpried() {
                //STS token过期之后刷新STStoken，如正在上传将会断点续传
                vodsVideoUploadClient.refreshSTSToken(Constant.accessKeyId,Constant.accessKeySecret,Constant.securityToken,Constant.expriedTime);
            }

            @Override
            public void onUploadRetry(String code, String message) {
                //上传重试的提醒
            }

            @Override
            public void onUploadRetryResume() {
                //上传重试成功的回调.告知用户重试成功
            }
        });
    }

    private long jlshuzi;
    // UI只允许在主线程更新。
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int ww = (int) (progress - jlshuzi);
            dialog.incrementProgressBy(ww);
            jlshuzi = progress;
            //更新进度
            tv_progress.setText("视频上传进度：" + String.valueOf(progress)+" %" );
        }
    };
}
