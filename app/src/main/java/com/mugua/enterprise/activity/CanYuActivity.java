package com.mugua.enterprise.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mugua.enterprise.BeasActivity;
import com.mugua.enterprise.R;
import com.mugua.enterprise.fragment.HomeFragment;
import com.mugua.enterprise.fragment.MeFragment;
import com.mugua.enterprise.util.Constant;
import com.mugua.enterprise.util.ImageUtils;
import com.mugua.enterprise.util.Okhttp;
import com.mugua.enterprise.util.httppost.BaseModel;
import com.mugua.enterprise.util.httppost.IRequestCallBack;
import com.mugua.enterprise.util.toast.CustomToast;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.ShareBoardConfig;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.Log;
import com.umeng.socialize.utils.ShareBoardlistener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by Lenovo on 2018/1/27.
 */

public class CanYuActivity extends BeasActivity implements View.OnClickListener {
    public static CanYuActivity instance = null;
    @BindView( R.id.rl_back )
    public RelativeLayout rl_back ;

    @BindView( R.id.btn_canyu )
    public Button btn_canyu ;

    @BindView( R.id.e_content )
    public EditText e_content ;

    @BindView( R.id.tv_shu )
    public TextView tv_shu ;

    @BindView( R.id.img_tump )
    public ImageView img_tump ;
    private ShareBoardConfig config;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canyu);
        instance = this;
        //绑定fragment
        ButterKnife.bind(this);

        config = new ShareBoardConfig();
        config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_CIRCULAR);
        config.setCancelButtonVisibility(true);
        config.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                HuoDongActivity.instance.finish();
                finish();
            }
        });

        tv_shu.setText(0+"/"+300);
        e_content.setFilters(new InputFilter[]{new InputFilter.LengthFilter(300)});
        e_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                Editable editable = e_content.getText();
                int len = editable.length();
                if(len > 300) tv_shu.setText(300+"/"+300);
                else tv_shu.setText(len+"/"+300);
            }
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count,int after) {
            }
            @Override
            public void afterTextChanged(Editable edit) {
            }
        });

        rl_back.setOnClickListener(this);
        btn_canyu.setOnClickListener(this);
        img_tump.setOnClickListener(this);
    }

    private void Data()
    {
        try {
            Okhttp.doGet(this, Constant.URL_share+Constant.user.getId(), new IRequestCallBack() {
                @Override
                public void onRequestBack(String text) {
                    JSONObject jsonobject = null;
                    try {
                        jsonobject = new JSONObject(text);
                        if (jsonobject.getString("code").equals("1000"))
                        {
                            JSONObject jsonObject = jsonobject.getJSONObject("data");
                            UMWeb web = new UMWeb(jsonObject.getString("url"));//连接地址
                            web.setTitle(jsonObject.getString("title"));//标题
                            web.setDescription(jsonObject.getString("content"));//描述
                            if (TextUtils.isEmpty(jsonObject.getString("photo"))) {
                                web.setThumb(new UMImage(CanYuActivity.this, R.mipmap.guanyu_tu));  //本地缩略图
                            } else {
                                web.setThumb(new UMImage(CanYuActivity.this, jsonObject.getString("photo")));  //网络缩略图
                            }
                            new ShareAction(CanYuActivity.this)
                                    .setDisplayList(SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE)
                                    .withMedia(web)
                                    .setCallback(umShareListener)
                                    .open(config);
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

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_canyu:
                renzheng();
                break;
            case R.id.img_tump:
                Width = 280;Height = 280;
                ImageUtils.takeOrChoosePhoto(this, ImageUtils.TAKE_OR_CHOOSE_PHOTO);
                break;
        }
    }

    private int Width,Height;
    private Bitmap bitmap1,bitmap2;
    private boolean open = false;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(open)
                UMShareAPI.get(getApplicationContext()).onActivityResult(requestCode, resultCode, data);
            else {
                switch (requestCode) {
                    case ImageUtils.TAKE_OR_CHOOSE_PHOTO:
                        //获取到了原始图片
                        File f = ImageUtils.getPhotoFromResult(this, data);
                        if(f == null)
                            f = ImageUtils.mCurrentPhotoFile;
                        String ww = String.valueOf(f);
                        bitmap1 = BitmapFactory.decodeFile(ww);
                        int w = bitmap1.getWidth();
                        int h = bitmap1.getHeight();
                        Matrix matrix = new Matrix();
                        float scaleWidth = ((float) Width / w);
                        float scaleHeight = ((float) Height / h);
                        matrix.postScale(scaleWidth, scaleHeight);
                        bitmap2 = Bitmap.createBitmap(bitmap1, 0, 0, w, h, matrix, true);

                        File file = new File(ww);
                        if(bitmap1 != null && !ww.equals("")) {
                            Luban.get(this)
                                    .load(file)                     // 传入要压缩的图片
                                    .putGear(Luban.THIRD_GEAR)      // 设定压缩档次,默认三挡自己可以选择
                                    .setCompressListener(new OnCompressListener() { // 设置回调

                                        @Override
                                        public void onStart() {
                                            // 压缩开始前调用,可以在方法内启动 loading UI
                                        }

                                        @Override
                                        public void onSuccess(File file) {
                                            // 压缩成功后调用,返回压缩后的图片文件
                                            uploadImg(file);
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                        }
                                    }).launch();
                        }
                        break;
                }
            }
        }
    }

    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private OkHttpClient client;
    private Request request;
    private boolean jia = false;
    private void uploadImg(final File file) {

        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        // mImgUrls为存放图片的url集合
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (file != null) {
            builder.addFormDataPart("file", file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file));
        }

        MultipartBody requestBody = builder.build();
        //构建请求
        request = new Request.Builder()
                .header("Content-Encoding", "gzip")
                .url(Constant.URL_upload)//地址
                .post(requestBody)//添加请求体
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(jia == false)
                    uploadImg(file);
                CustomToast.showToast(getApplicationContext(), "重新上传", 1000);
                jia = true;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String response1 = response.body().string();
                new Handler(getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        photourl = response1;
                        img_tump.setImageBitmap(bitmap2);
                    }
                }, 100);
            }
        });
    }

    private String photourl = "";
    public void renzheng()
    {
        if(photourl.equals(""))
        {
            CustomToast.showToast(getApplicationContext(),"请上传图片",1000);
            return;
        }
        if(e_content.getText().toString().equals(""))
        {
            CustomToast.showToast(getApplicationContext(),"请填写故事",1000);
            return;
        }
        String www = "uid="+ Constant.user.getId()+"&uphone="+Constant.user.getUserPhone()+
                "&photourl="+ photourl+"&story="+e_content.getText().toString();
        BaseModel baseModel = new BaseModel(Constant.URL_vote);
        baseModel.send(www, new IRequestCallBack() {
            @Override
            public void onRequestBack(String text) {
                JSONObject jsonobject = null;
                try {
                    jsonobject = new JSONObject(text);
                    if (jsonobject.getString("code").equals("1000"))
                    {
                        open = true;
                        Data();
                        CustomToast.showToast(getApplicationContext(),jsonobject.getString("msg"),1000);
                    }
                    else CustomToast.showToast(getApplicationContext(),jsonobject.getString("msg"),1000);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {
            Toast.makeText(getApplicationContext()," 收藏成功",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat","platform"+platform);
            if(platform.name().equals("WEIXIN_FAVORITE")){
                Toast.makeText(getApplicationContext()," 收藏成功",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(), " 分享成功", Toast.LENGTH_SHORT).show();
            }
            HuoDongActivity.instance.finish();
            finish();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(getApplicationContext()," 分享失败", Toast.LENGTH_SHORT).show();
            if(t!=null){
                Log.d("throw","throw:"+t.getMessage());
            }
            HuoDongActivity.instance.finish();
            finish();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(getApplicationContext()," 分享取消了", Toast.LENGTH_SHORT).show();
            HuoDongActivity.instance.finish();
            finish();
        }
    };
}
