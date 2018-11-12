package com.mugua.enterprise.activity.me;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.lidong.photopicker.PhotoPickerActivity;
import com.lidong.photopicker.PhotoPreviewActivity;
import com.lidong.photopicker.SelectModel;
import com.lidong.photopicker.intent.PhotoPickerIntent;
import com.lidong.photopicker.intent.PhotoPreviewIntent;
import com.mugua.enterprise.BeasActivity;
import com.mugua.enterprise.R;
import com.mugua.enterprise.util.BitmapAndStringUtils;
import com.mugua.enterprise.util.CheckUtils;
import com.mugua.enterprise.util.Constant;
import com.mugua.enterprise.util.FileSizeUtil;
import com.mugua.enterprise.util.GlideRoundTransform;
import com.mugua.enterprise.util.ImageYSshangchaun;
import com.mugua.enterprise.util.ImageYaSuo;
import com.mugua.enterprise.util.Tubian;
import com.mugua.enterprise.util.httppost.BaseModel;
import com.mugua.enterprise.util.httppost.IRequestCallBack;
import com.mugua.enterprise.util.toast.CustomToast;
import com.zhy.autolayout.utils.AutoUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
 * Created by Lenovo on 2017/12/14.
 */

public class ProductUploadActivity extends BeasActivity implements View.OnClickListener{
    public static ProductUploadActivity instance = null;
    private static final int REQUEST_CAMERA_CODE = 10;
    private static final int REQUEST_PREVIEW_CODE = 20;
    private ArrayList<String> imagePaths = new ArrayList<>();
    private ArrayList<String> imagePath = new ArrayList<>();
    private ArrayList<String> imagePathjl = new ArrayList<>();

    @BindView( R.id.rl_back )
    public RelativeLayout rl_back ;

    @BindView( R.id.e_jiage )
    public EditText e_jiage ;

    @BindView( R.id.e_xijie )
    public EditText e_xijie ;

    @BindView( R.id.e_title )
    public EditText e_title ;

    @BindView( R.id.btn_productupload )
    public Button btn_productupload ;

    private GridView gridView;
    private GridAdapter gridAdapter;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productupload);
        instance = this;
        //绑定fragment
        ButterKnife.bind(this);
        gridView = (GridView) findViewById(R.id.gridView);

        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        cols = cols < 4 ? 4 : cols;
        gridView.setNumColumns(cols);

        // preview
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String imgs = (String) parent.getItemAtPosition(position);
                if ("000000".equals(imgs)) {
                    PhotoPickerIntent intent = new PhotoPickerIntent(ProductUploadActivity.this);
                    intent.setSelectModel(SelectModel.MULTI);
                    intent.setShowCarema(true); // 是否显示拍照
                    intent.setMaxTotal(7); // 最多选择照片数量，默认为6
                    intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
                    startActivityForResult(intent, REQUEST_CAMERA_CODE);
                } else {
                    PhotoPreviewIntent intent = new PhotoPreviewIntent(ProductUploadActivity.this);
                    intent.setCurrentItem(position);
                    intent.setPhotoPaths(imagePaths);
                    startActivityForResult(intent, REQUEST_PREVIEW_CODE);
                }
            }
        });
        imagePaths.add("000000");
        gridAdapter = new GridAdapter(imagePaths);
        gridView.setAdapter(gridAdapter);

        e_jiage.setInputType(EditorInfo.TYPE_CLASS_PHONE);
//        e_jiage.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void onTextChanged(CharSequence text, int start, int before, int count) {
//                //text  输入框中改变后的字符串信息
//                //start 输入框中改变后的字符串的起始位置
//                //before 输入框中改变前的字符串的位置 默认为0
//                //count 输入框中改变后的一共输入字符串的数量
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence text, int start, int count,int after) {
//                //text  输入框中改变前的字符串信息
//                //start 输入框中改变前的字符串的起始位置
//                //count 输入框中改变前后的字符串改变数量一般为0
//                //after 输入框中改变后的字符串与起始位置的偏移量
//            }
//
//            @Override
//            public void afterTextChanged(Editable edit) {
//                //edit  输入结束呈现在输入框中的信息
//            }
//        });

        rl_back.setOnClickListener(this);
        btn_productupload.setOnClickListener(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            shu();
        }
        return false;
    }

    private void shu()
    {
        CardActivity.instance.shu--;
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.rl_back:
                shu();
                break;
            case R.id.btn_productupload:
                if(e_title.getText().toString().equals(""))
                {
                    CustomToast.showToast(getApplicationContext(), "标题不能为空", 1000);
                    return;
                }
                else if(e_xijie.getText().toString().equals(""))
                {
                    CustomToast.showToast(getApplicationContext(), "介绍不能为空", 1000);
                    return;
                }
                else if(e_jiage.getText().toString().equals(""))
                {
                    CustomToast.showToast(getApplicationContext(), "价格不能为空", 1000);
                    return;
                }
                else if(!CheckUtils.strIsNumber(e_jiage.getText().toString()))
                    CustomToast.showToast(getApplicationContext(), "价格输入错误", 1000);


                for (int i = 0;i < imagePathjl.size();i++)
                {
                    String substr = imagePathjl.get(i).substring(imagePathjl.get(i).length()-3,imagePathjl.get(i).length());
                    if(!substr.equals("jpg") && !substr.equals("png")) {
                        CustomToast.showToast(getApplicationContext(), "第" + (i+1) + "个图片格式错误");
                        return;
                    }
                }
                File file = new File(imagePathjl.get(0));
                if(imagePathjl != null && imagePathjl.size() > 0) {
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
                                    updown();
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

    private void uploadImgys(int shu)
    {
        File file = new File(imagePathjl.get(shu));
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

    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private OkHttpClient client;
    private Request request;
    private int shu,shu1;
    private String[] imagePathjlup = new String[7];
    private void uploadImg(File file) {

        client = new OkHttpClient.Builder()
                .connectTimeout(180, TimeUnit.SECONDS)
                .readTimeout(180, TimeUnit.SECONDS)
                .build();
        // mImgUrls为存放图片的url集合
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        if (file != null) {
            builder.addFormDataPart("file", file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file));
        }
        //添加其它信息
//        builder.addFormDataPart("nickname",tv_ncx.getText().toString());
//        builder.addFormDataPart("starttime", tv_xbx.getText().toString());


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
                dialog.dismiss();
                shu = 0;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String response1 = response.body().string();
                String substr = response1.substring(response1.length()-3,response1.length());
                if(!substr.equals("jpg") && !substr.equals("png")) {
                    if(shu1 < 3) {
                        uploadImgys(shu);
                        shu1++;
                    }
                    else
                    {
                        shu1 = 0;shu = 0;
                        dialog.dismiss();
                        CustomToast.showToast(getApplicationContext(), "上传失败", 1000);
                    }
                }
                else {
                    shu++;
                    if(shu <= imagePathjl.size()) {
                        imagePathjlup[shu - 1] = response1;
                        dialog.incrementProgressBy(1);
                    }
                    if(shu < imagePathjl.size()) {
                        uploadImgys(shu);
                    }
                    else
                    {
                        shu++;
                        updata(imagePathjlup);
                    }
                }
            }
        });
    }

    private ProgressDialog dialog;
    private void updown()
    {
        int daxiao = imagePathjl.size();
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
                        shu();
                    }
                });
        dialog.setMax(daxiao);
        dialog.setMessage("上传进度");
        dialog.show();
    }

    private void finish1()
    {
        dialog.dismiss();
        finish();
//        CardActivity.instance.init(imagePathjlup[0]);
        CardActivity.instance.init(imagePathjl.get(0));
    }

    private void updata(final String[] imagePathjlup)
    {
        BaseModel baseModel = new BaseModel(Constant.URL_addGoods);
        String www = "photo1="+imagePathjlup[0]+"&photo2="+imagePathjlup[1]+"&photo3="+imagePathjlup[2]+
                "&photo4="+imagePathjlup[3]+"&photo5="+imagePathjlup[4]+"&photo6="+imagePathjlup[5]+
                "&photo7="+imagePathjlup[6]+"&name="+e_title.getText().toString()+"&detail="+e_xijie.getText().toString()+
                "&price="+e_jiage.getText().toString();
        baseModel.send(www, new IRequestCallBack() {
            @Override
            public void onRequestBack(String text) {
                switch (CardActivity.instance.shu)
                {
                    case 1:
                        CardActivity.instance.merchandise1 = text;
                        CardActivity.instance.goodsPhoto1 = imagePathjlup[0];
                        break;
                    case 2:
                        CardActivity.instance.merchandise2 = text;
                        CardActivity.instance.goodsPhoto2 = imagePathjlup[0];
                        break;
                }
                if(shu == imagePathjl.size()+1)
                {
                    finish1();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                // 选择照片
                case REQUEST_CAMERA_CODE:
                    ArrayList<String> list = data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT);
                    loadAdpater(list);
                    break;
                // 预览
                case REQUEST_PREVIEW_CODE:
                    ArrayList<String> ListExtra = data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT);
                    loadAdpater(ListExtra);
                    break;
            }
        }
    }

    private void loadAdpater(ArrayList<String> paths) {
        if (imagePaths != null && imagePaths.size() > 0) {
            imagePath.clear();
            imagePaths.clear();
            imagePathjl.clear();
        }
        if (paths.contains("000000")) {
            paths.remove("000000");
        }
        imagePathjl.addAll(paths);
        paths.add("000000");
        imagePath.addAll(paths);
        imagePaths.addAll(paths);
        gridAdapter = new GridAdapter(imagePaths);
        gridView.setAdapter(gridAdapter);
    }

    private class GridAdapter extends BaseAdapter {
        private ArrayList<String> listUrls;
        private LayoutInflater inflater;

        public GridAdapter(ArrayList<String> listUrls) {
            this.listUrls = listUrls;
            if (listUrls.size() == 8) {
                listUrls.remove(listUrls.size() - 1);
            }
            inflater = LayoutInflater.from(ProductUploadActivity.this);
        }

        private void data()
        {
            shu = 0;imagePathjl.clear();
            for (int i = 0; i < imagePathjlup.length;i++)
                imagePathjlup[i] = "";
            if (imagePaths != null && imagePaths.size() > 0) {
                imagePaths.clear();
            }
            if (imagePath.contains("000000")) {
                imagePath.remove("000000");
            }
            imagePathjl.addAll(imagePath);
            imagePath.add("000000");
            imagePaths.addAll(imagePath);
            gridAdapter = new GridAdapter(imagePaths);
            gridView.setAdapter(gridAdapter);
        }

        public int getCount() {
            return listUrls.size();
        }

        @Override
        public String getItem(int position) {
            return listUrls.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.upload_item_image, parent, false);
                holder.image = (ImageView) convertView.findViewById(R.id.imageView);
                holder.image1 = (ImageView) convertView.findViewById(R.id.imageView1);
                convertView.setTag(holder);
                AutoUtils.autoSize(convertView);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final String path = listUrls.get(position);
            if (path.equals("000000")) {
                holder.image.setBackgroundResource(R.mipmap.add);
            } else {
                final ViewHolder finalHolder = holder;
                Glide.with(ProductUploadActivity.this)
                        .load(path)
                        .placeholder(R.mipmap.default_error)
                        .error(R.mipmap.default_error)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                if (finalHolder.image == null) {
                                    return false;
                                }
                                if (finalHolder.image.getScaleType() != ImageView.ScaleType.FIT_XY) {
                                    finalHolder.image.setScaleType(ImageView.ScaleType.FIT_XY);
                                }
                                ViewGroup.LayoutParams params = finalHolder.image.getLayoutParams();
                                int vw = finalHolder.image.getWidth() - finalHolder.image.getPaddingLeft() - finalHolder.image.getPaddingRight();
                                float scale = (float) vw / (float) resource.getIntrinsicWidth();
                                int vh = Math.round(resource.getIntrinsicHeight() * scale);
                                params.height = vh + finalHolder.image.getPaddingTop() + finalHolder.image.getPaddingBottom();
                                finalHolder.image.setLayoutParams(params);
                                return false;
                            }
                        })
                        .centerCrop()
                        .crossFade()
                        .transform(new GlideRoundTransform(ProductUploadActivity.this, 10))
                        .into(finalHolder.image);
            }

            holder.image1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!listUrls.get(position).equals("000000"))
                    {
                        imagePath.remove(position);
                        data();
                    }
                }
            });
            return convertView;
        }

        class ViewHolder {
            ImageView image;
            ImageView image1;
        }
    }
}
