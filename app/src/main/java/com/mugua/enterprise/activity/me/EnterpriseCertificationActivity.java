package com.mugua.enterprise.activity.me;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mugua.enterprise.BeasActivity;
import com.mugua.enterprise.R;
import com.mugua.enterprise.fragment.HomeFragment;
import com.mugua.enterprise.fragment.MeFragment;
import com.mugua.enterprise.util.CardDialog;
import com.mugua.enterprise.util.Constant;
import com.mugua.enterprise.util.ImageUtils;
import com.mugua.enterprise.util.httppost.BaseModel;
import com.mugua.enterprise.util.httppost.IRequestCallBack;
import com.mugua.enterprise.util.toast.CustomToast;

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

import static android.os.Looper.getMainLooper;

/**
 * Created by Lenovo on 2017/12/18.
 */

public class EnterpriseCertificationActivity extends BeasActivity implements View.OnClickListener {
    public static EnterpriseCertificationActivity instance = null;
    @BindView( R.id.rl_back )
    public RelativeLayout rl_back ;

    @BindView( R.id.img_yyzz )
    public ImageView img_yyzz ;

    @BindView( R.id.img_sfzzm )
    public ImageView img_sfzzm ;

    @BindView( R.id.img_sfzfm )
    public ImageView img_sfzfm ;

    @BindView( R.id.ing_xieyi )
    public RelativeLayout ing_xieyi ;

    @BindView( R.id.btn_submit )
    public Button btn_submit ;

    @BindView( R.id.e_gsname )
    public EditText e_gsname ;

    @BindView( R.id.e_faren )
    public EditText e_faren ;

    @BindView( R.id.e_farensfzh )
    public EditText e_farensfzh ;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterprisecertification);
        instance = this;
        //绑定fragment
        ButterKnife.bind(this);

        ing_xieyi.setSelected(true);
        ing_xieyi.setOnClickListener(this);
        rl_back.setOnClickListener(this);
        img_yyzz.setOnClickListener(this);
        img_sfzzm.setOnClickListener(this);
        img_sfzfm.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
    }

    public void renzheng()
    {
        String www = "id="+ Constant.user.getId()+"&company="+e_gsname.getText().toString()+
                "&legalPersonName="+ e_faren.getText().toString()+"&legalPersonId="+e_farensfzh.getText().toString()+
                "&businessLicensePhotoUrl="+ imagePathyyzz+"&legalPersonPhotoUrl="+imagePathsfzzm+
                "&ext="+ imagePathsfzfm+"&isCentification=3";
        BaseModel baseModel = new BaseModel(Constant.URL_identification);
        baseModel.send(www, new IRequestCallBack() {
            @Override
            public void onRequestBack(String text) {
                JSONObject jsonobject = null;
                try {
                    jsonobject = new JSONObject(text);
                    if (jsonobject.getString("code").equals("1000"))
                    {
                        CustomToast.showToast(getApplicationContext(),jsonobject.getString("msg"),1000);
                        finish1();
                    }
                    else CustomToast.showToast(getApplicationContext(),jsonobject.getString("msg"),1000);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
                        finish();
                    }
                });

                view.findViewById(R.id.tv_bg).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        finish();
                    }
                });
            }
        }).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_submit:
                if(e_gsname.getText().toString().equals(""))
                {
                    CustomToast.showToast(getApplicationContext(),"请输入公司名称",1000);
                    return;
                }

                if(e_faren.getText().toString().equals(""))
                {
                    CustomToast.showToast(getApplicationContext(),"请输入法人姓名",1000);
                    return;
                }

                if(e_farensfzh.getText().toString().equals(""))
                {
                    CustomToast.showToast(getApplicationContext(),"请输入法人身份证号码",1000);
                    return;
                }

                if(imagePathyyzz.equals(""))
                {
                    CustomToast.showToast(getApplicationContext(),"请上传营业执照",1000);
                    return;
                }

                if(imagePathsfzzm.equals(""))
                {
                    CustomToast.showToast(getApplicationContext(),"请上传身份证正面",1000);
                    return;
                }

                if(imagePathsfzfm.equals(""))
                {
                    CustomToast.showToast(getApplicationContext(),"请上传身份证反面",1000);
                    return;
                }
                if(ing_xieyi.isSelected() == false)
                {
                    CustomToast.showToast(getApplicationContext(), "请同意咔芒隐私政策");
                    return;
                }
                renzheng();
                break;
            case R.id.ing_xieyi:
                if(ing_xieyi.isSelected() == false) ing_xieyi.setSelected(true);
                else ing_xieyi.setSelected(false);
                break;
            case R.id.rl_back:
                finish();
                break;
            case R.id.img_yyzz:
                open = 1;
                Width = 240;Height = 240;
                ImageUtils.takeOrChoosePhoto(this, ImageUtils.TAKE_OR_CHOOSE_PHOTO);
                break;
            case R.id.img_sfzzm:
                open = 2;
                Width = 320;Height = 240;
                ImageUtils.takeOrChoosePhoto(this, ImageUtils.TAKE_OR_CHOOSE_PHOTO);
                break;
            case R.id.img_sfzfm:
                open = 3;
                Width = 320;Height = 240;
                ImageUtils.takeOrChoosePhoto(this, ImageUtils.TAKE_OR_CHOOSE_PHOTO);
                break;
        }
    }

    private Bitmap bitmap1;
    private Bitmap bitmap2;
    private String imagePathyyzz = "";
    private String imagePathsfzzm = "";
    private String imagePathsfzfm = "";
    private int open;
    private int Width,Height;
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
                        switch (open)
                        {
                            case 1:
                                imagePathyyzz = response1;
                                img_yyzz.setImageBitmap(bitmap2);
                                break;
                            case 2:
                                imagePathsfzzm = response1;
                                img_sfzzm.setImageBitmap(bitmap2);
                                break;
                            case 3:
                                imagePathsfzfm = response1;
                                img_sfzfm.setImageBitmap(bitmap2);
                                break;
                        }
                    }
                }, 100);
            }
        });
    }
}
