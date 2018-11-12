package com.mugua.enterprise.activity.me;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mugua.enterprise.BeasActivity;
import com.mugua.enterprise.R;
import com.mugua.enterprise.fragment.MeFragment;
import com.mugua.enterprise.util.CircleImageView;
import com.mugua.enterprise.util.Constant;
import com.mugua.enterprise.util.DialogActivity;
import com.mugua.enterprise.util.GlideRoundTransform;
import com.mugua.enterprise.util.ImageAsyncTask;
import com.mugua.enterprise.util.ImageUtils;
import com.mugua.enterprise.util.TuActivity;
import com.mugua.enterprise.util.httppost.BaseModel;
import com.mugua.enterprise.util.httppost.IRequestCallBack;
import com.mugua.enterprise.util.toast.CustomToast;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import com.mugua.jiguang.chat.utils.SharePreferenceManager;
import com.mugua.jiguang.chat.utils.ThreadUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.os.Looper.getMainLooper;

/**
 * Created by Lenovo on 2017/12/5.
 */

public class EditDataActivity extends BeasActivity implements View.OnClickListener {
    public static EditDataActivity instance = null;
    @BindView( R.id.rl_back )
    public RelativeLayout rl_back ;

    @BindView( R.id.rl_editnc )
    public RelativeLayout rl_editnc ;

    @BindView( R.id.rl_edith )
    public RelativeLayout rl_edith ;

    @BindView( R.id.img_tu )
    public ImageView img_tu ;

    @BindView( R.id.tv_nc )
    public TextView tv_nc ;

    @BindView( R.id.tv_hangye )
    public TextView tv_hangye ;
    private DialogActivity dialogActivity;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editdata);
        instance = this;
        //绑定fragment
        ButterKnife.bind(this);
        dialogActivity = new DialogActivity(this);
        tv_nc.setText(Constant.user.getUserName());
        tv_hangye.setText(Constant.user.getCompanyType());
//        ImageAsyncTask imageAsyncTask = new ImageAsyncTask(Constant.user.getPhotoUrl());
//        imageAsyncTask.data(new ImageAsyncTask.OnCloseListener()
//        {
//            @Override
//            public void onClick(Bitmap bitmap) {
//                img_tu.setImageBitmap(bitmap);
//            }
//        });

        Glide.with(this)
                .load(Constant.user.getPhotoUrl())
                .placeholder(img_tu.getBackground())
                .error(R.mipmap.mr_tx1)
                .override(140,140)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new GlideRoundTransform(this, 0))
                .into(img_tu);

        rl_back.setOnClickListener(this);
        rl_editnc.setOnClickListener(this);
        rl_edith.setOnClickListener(this);
        img_tu.setOnClickListener(this);
    }

    public void setdata(String nicheng , String hangye)
    {
        if(!nicheng.equals(""))
            tv_nc.setText(nicheng);
        if(!hangye.equals(""))
            tv_hangye.setText(hangye);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Glide.clear(img_tu);
    }

    private Intent intent;
    private Bundle bundle;
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.img_tu:
                ImageUtils.takeOrChoosePhoto(this, ImageUtils.TAKE_OR_CHOOSE_PHOTO);
                break;
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_editnc:
                intent = new Intent(EditDataActivity.this, EditActivity.class);
                bundle = new Bundle();
                bundle.putString("tv_title", "企业名称");
                bundle.putString("e_content", "请输入公司简称");
                bundle.putInt("shu", 6);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.rl_edith:
                intent = new Intent(EditDataActivity.this, EditActivity.class);
                bundle = new Bundle();
                bundle.putString("tv_title", "编辑行业");
                bundle.putString("e_content", "请输入行业");
                bundle.putInt("shu", 20);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }

    private Bitmap bitmap;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
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
                    ImageUtils.doCropPhoto(this, f, 2,1);
                    break;
                case ImageUtils.PHOTO_PICKED_WITH_DATA:
                    bitmap = ImageUtils.getCroppedImage();
                    if(bitmap != null) {
                        dialogActivity.showProgressDialog("正在上传，请稍后！");
                        uploadImg(ImageUtils.mTempCropFile.getAbsolutePath());
                    }

                    break;
            }
        }
    }

    private void init(final String bitmappath)
    {
        String www = "id="+Constant.user.getId()+"&photoUrl="+bitmappath;
        BaseModel baseModel = new BaseModel(Constant.URL_turnLogo+"turnLogo");
        baseModel.send(www, new IRequestCallBack() {
            @Override
            public void onRequestBack(String text) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(text);
                    if(jsonObject.getString("code").equals("1000"))
                    {
                        CustomToast.showToast(getApplicationContext(),jsonObject.getString("msg"),1000);
                        img_tu.setImageBitmap(bitmap);
                        dialogActivity.dissmissProgressDialog();
                        setdata("","");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private OkHttpClient client;
    private Request request;
    private void uploadImg(final String path) {
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        // mImgUrls为存放图片的url集合
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        File file = new File(path);
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
                CustomToast.showToast(getApplicationContext(), "上传失败", 1000);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String response1 = response.body().string();
                //检测账号是否登陆
                final String avatarPath = path;
                UserInfo myInfo = JMessageClient.getMyInfo();
                if (myInfo != null) jgtx(avatarPath);
                init(response1);
            }
        });
    }

    private void jgtx(final String avatarPath)
    {
        //注册时更新头像
        if (avatarPath != null && !avatarPath.equals("")) {
            new Handler(getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    JMessageClient.updateUserAvatar(new File(avatarPath), new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage) {
                            if (responseCode == 0) {
                                SharePreferenceManager.setCachedAvatarPath(avatarPath);
                            }
                        }
                    });
                }
            }, 100);
        } else {
            SharePreferenceManager.setCachedAvatarPath(null);
        }
    }
}
