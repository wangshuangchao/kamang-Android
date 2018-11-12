package com.mugua.enterprise.util;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.mugua.enterprise.R;

//new ShareDialog(AllDialogActivity.this, R.style.dialog, new ShareDialog.OnItemClickListener() {
//@Override
//public void onClick(Dialog dialog, int position) {
//        dialog.dismiss();
//        switch (position){
//        case 1:
//        Utils.toast(AllDialogActivity.this,"微信好友");
//        break;
//        case 2:
//        Utils.toast(AllDialogActivity.this,"朋友圈");
//        break;
//        case 3:
//        Utils.toast(AllDialogActivity.this,"QQ");
//        break;
//        case 4:
//        Utils.toast(AllDialogActivity.this,"微博");
//        break;
//        }
//        }
//        }).show();
/**
 * 分享页面
 */
public class ShareDialog extends Dialog {


    private Activity mContext;
    private OnItemClickListener mListener;


    public ShareDialog(Activity context) {
        super(context);
        this.mContext = context;
    }


    public ShareDialog(Activity context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }

    public ShareDialog(Activity context, int data, int themeResId, OnItemClickListener mListener) {
        super(context, themeResId);
        this.mContext = context;
        this.mListener = mListener;
        this.data = data;
    }

    protected ShareDialog(Activity context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    private int data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(mContext).inflate(data, null);
        setContentView(view);
        setCanceledOnTouchOutside(true);
        getWindow().setWindowAnimations(R.style.dialogWindowAnim); //设置窗口弹出动画

        //全屏处理
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        WindowManager wm = mContext.getWindowManager();

        lp.width = wm.getDefaultDisplay().getWidth(); //设置宽度
        getWindow().setAttributes(lp);
        mListener.onClick(this, view);
    }

    public interface OnItemClickListener{
        void onClick(Dialog dialog, View view);
    }
}
