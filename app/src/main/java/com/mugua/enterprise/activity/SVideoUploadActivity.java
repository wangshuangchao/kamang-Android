package com.mugua.enterprise.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.vod.upload.VODSVideoUploadCallback;
import com.alibaba.sdk.android.vod.upload.VODSVideoUploadClient;
import com.alibaba.sdk.android.vod.upload.VODSVideoUploadClientImpl;
import com.alibaba.sdk.android.vod.upload.common.utils.StringUtil;
import com.alibaba.sdk.android.vod.upload.model.SvideoInfo;
import com.alibaba.sdk.android.vod.upload.session.VodHttpClientConfig;
import com.alibaba.sdk.android.vod.upload.session.VodSessionCreateInfo;
import com.mugua.enterprise.R;
import com.mugua.enterprise.util.Constant;
import com.mugua.enterprise.util.Okhttp;
import com.mugua.enterprise.util.httppost.IRequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * 短视频上传场景示例
 * Created by Mulberry on 2017/11/24.
 */
public class SVideoUploadActivity extends Activity {

    private static final String TAG = "VOD_UPLOAD";
    Button btn_upload;
    Button btn_cancel;
    Button btn_resume;
    Button btn_pause;
    TextView tv_progress;
    private long  progress;

    private String videoPath;//视频路径
    private String imagePath="/sdcard/2.jpg";//封面图片路径

    //以下四个值由开发者的服务端提供,参考文档：https://help.aliyun.com/document_detail/28756.html（STS介绍）
    // AppServer STS SDK参考：https://help.aliyun.com/document_detail/28788.html
    private String accessKeyId = "";//子accessKeyId
    private String accessKeySecret = "";//子accessKeySecret
    private String securityToken = "";//STS 的securityToken
    private String expriedTime = "";//STS的过期时间

    private String requestID = "B829820D-12B5-4DA2-A2D8-777130FFCAA7";//传空或传递访问STS返回的requestID

    VODSVideoUploadClient vodsVideoUploadClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.svideo_upload);
        videoPath = Constant.path;
        try {
            Okhttp.doGet(SVideoUploadActivity.this, "http://192.168.1.110:9000/videoUpload/getCredentials?roleSessionName=15633000379", new IRequestCallBack() {
                @Override
                public void onRequestBack(String text) {
                    JSONObject jsonobject = null;
                    try {
                        jsonobject = new JSONObject(text);
                        accessKeyId = jsonobject.getString("accessKeyId");
                        accessKeySecret = jsonobject.getString("accessKeySecret");
                        securityToken = jsonobject.getString("securityToken");
                        expriedTime = jsonobject.getString("expiration");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        btn_upload = (Button)findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(StringUtil.isEmpty(accessKeyId)) {
                    Toast.makeText(v.getContext(),"The specified parameter accessKeyId cannot be null",Toast.LENGTH_LONG).show();
                    return;
                } else if(StringUtil.isEmpty(accessKeySecret)) {
                    Toast.makeText(v.getContext(),"The specified parameter \"accessKeySecret\" cannot be null",Toast.LENGTH_LONG).show();
                    return;
                } else if(StringUtil.isEmpty(securityToken)) {
                    Toast.makeText(v.getContext(),"The specified parameter \"securityToken\" cannot be null",Toast.LENGTH_LONG).show();
                    return;
                } else if(StringUtil.isEmpty(expriedTime)) {
                    Toast.makeText(v.getContext(),"The specified parameter \"expriedTime\" cannot be null",Toast.LENGTH_LONG).show();
                    return;
                } else if(!(new File(videoPath)).exists()) {
                    Toast.makeText(v.getContext(),"The specified parameter \"videoPath\" file not exists",Toast.LENGTH_LONG).show();
                    return;
                } else if(!(new File(imagePath)).exists()) {
                    Toast.makeText(v.getContext(),"The specified parameter \"imagePath\" file not exists",Toast.LENGTH_LONG).show();
                    return;
                }

                //参数请确保存在，如不存在SDK内部将会直接将错误throw Exception
                // 文件路径保证存在之外因为Android 6.0之后需要动态获取权限，请开发者自行实现获取"文件读写权限".
                VodHttpClientConfig vodHttpClientConfig = new VodHttpClientConfig.Builder()
                        .setMaxRetryCount(2)
                        .setConnectionTimeout(15 * 1000)
                        .setSocketTimeout(15 * 1000)
                        .build();

                SvideoInfo svideoInfo = new SvideoInfo();
                svideoInfo.setTitle(new File(videoPath).getName());
                svideoInfo.setDesc("");
                svideoInfo.setCateId(1);

                VodSessionCreateInfo vodSessionCreateInfo =new  VodSessionCreateInfo.Builder()
                        .setImagePath(imagePath)
                        .setVideoPath(videoPath)
                        .setAccessKeyId(accessKeyId)
                        .setAccessKeySecret(accessKeySecret)
                        .setSecurityToken(securityToken)
//                        .setRequestID(requestID)
                        .setExpriedTime(expriedTime)
                        .setIsTranscode(true)
                        .setSvideoInfo(svideoInfo)
                        .setVodHttpClientConfig(vodHttpClientConfig)
                        .build();
                vodsVideoUploadClient.uploadWithVideoAndImg(vodSessionCreateInfo, new VODSVideoUploadCallback() {
                    @Override
                    public void onUploadSucceed(String videoId, String imageUrl) {
                        Log.d(TAG,"onUploadSucceed"+ "videoId:"+ videoId + "imageUrl" + imageUrl);
                    }

                    @Override
                    public void onUploadFailed(String code, String message) {
                        Log.d(TAG,"onUploadFailed" + "code" + code + "message" + message);
                    }

                    @Override
                    public void onUploadProgress(long uploadedSize, long totalSize) {
                        Log.d(TAG,"onUploadProgress" + uploadedSize * 100 / totalSize);
                        progress = uploadedSize * 100 / totalSize;
                        handler.sendEmptyMessage(0);
                    }

                    @Override
                    public void onSTSTokenExpried() {
                        Log.d(TAG,"onSTSTokenExpried");
                        //STS token过期之后刷新STStoken，如正在上传将会断点续传
                        vodsVideoUploadClient.refreshSTSToken(accessKeyId,accessKeySecret,securityToken,expriedTime);
                    }

                    @Override
                    public void onUploadRetry(String code, String message) {
                        //上传重试的提醒
                        Log.d(TAG,"onUploadRetry" + "code" + code + "message" + message);
                    }

                    @Override
                    public void onUploadRetryResume() {
                        //上传重试成功的回调.告知用户重试成功
                        Log.d(TAG,"onUploadRetryResume");
                    }
                });
            }
        });

        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vodsVideoUploadClient.cancel();


            }
        });
        btn_resume = (Button) findViewById(R.id.btn_resume);
        btn_resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vodsVideoUploadClient.resume();
            }
        });

        btn_pause = (Button) findViewById(R.id.btn_pause);
        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vodsVideoUploadClient.pause();
            }
        });

        tv_progress = (TextView) findViewById(R.id.tv_progress);

        vodsVideoUploadClient = new VODSVideoUploadClientImpl(this.getApplicationContext());
        vodsVideoUploadClient.init();

    }

    @Override
    protected void onResume() {
        super.onResume();
        vodsVideoUploadClient.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        vodsVideoUploadClient.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vodsVideoUploadClient.release();
    }

    // UI只允许在主线程更新。
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //更新进度
            tv_progress.setText("进度：" + String.valueOf(progress) );
        }
    };

}
