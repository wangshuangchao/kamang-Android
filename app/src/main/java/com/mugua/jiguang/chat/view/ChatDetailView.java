package com.mugua.jiguang.chat.view;


import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;

import com.mugua.enterprise.R;
import com.mugua.jiguang.chat.adapter.GroupMemberGridAdapter;


public class ChatDetailView extends LinearLayout {


    private LinearLayout mGroupDescLL;
    private View mSplitLine1;
    private View mSplitLine2;
    private LinearLayout mGroupNameLL;
    private LinearLayout mMyNameLL;
    private LinearLayout mGroupNumLL;
    private LinearLayout mGroupChatRecordLL;
    private LinearLayout mGroupChatDelLL;
    private LinearLayout mChatFile;
    private ImageButton mReturnBtn;
    private TextView mTitle;
    private TextView mGroupDesc;
    private ImageButton mMenuBtn;
    private TextView mGroupName;
    private TextView mMyName;
    public static SlipButton mNoDisturbBtn;
    private SlipButton mBlockBtn;
    private RelativeLayout mBlockRl;
    private View mBlockLine;
    private Context mContext;
    private LinearLayout mTv_moreGroup;
    private RelativeLayout mGroupAvatarLL;
    private RelativeLayout mClear_rl;
    private ImageView mIv_groupAvatar;

    public ChatDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        // TODO Auto-generated constructor stub
    }

    public void initModule() {
        mGroupDescLL = (LinearLayout) findViewById(R.id.group_desc_ll);
        mGroupDesc = (TextView) findViewById(R.id.chat_detail_group_desc);
        mSplitLine1 = findViewById(R.id.all_member_split_line1);
        mSplitLine2 = findViewById(R.id.all_member_split_line2);
        mGroupNameLL = (LinearLayout) findViewById(R.id.group_name_ll);
        mGroupAvatarLL = (RelativeLayout) findViewById(R.id.rl_groupAvatar);
        mIv_groupAvatar = (ImageView) findViewById(R.id.iv_groupAvatar);
        mMyNameLL = (LinearLayout) findViewById(R.id.group_my_name_ll);
        mGroupNumLL = (LinearLayout) findViewById(R.id.group_num_ll);
        mGroupChatRecordLL = (LinearLayout) findViewById(R.id.group_chat_record_ll);
        mGroupChatDelLL = (LinearLayout) findViewById(R.id.group_chat_del_ll);
        mChatFile = (LinearLayout) findViewById(R.id.chat_file);
        mReturnBtn = (ImageButton) findViewById(R.id.return_btn);
        mTitle = (TextView) findViewById(R.id.title);
        mMenuBtn = (ImageButton) findViewById(R.id.right_btn);
        mGroupName = (TextView) findViewById(R.id.chat_detail_group_name);
        mMyName = (TextView) findViewById(R.id.chat_detail_my_name);
        mNoDisturbBtn = (SlipButton) findViewById(R.id.no_disturb_slip_btn);
        mBlockRl = (RelativeLayout) findViewById(R.id.block_rl);
        mBlockBtn = (SlipButton) findViewById(R.id.block_slip_btn);
        mBlockLine = findViewById(R.id.block_split_line);
        mTv_moreGroup = (LinearLayout) findViewById(R.id.tv_moreGroup);
        mClear_rl = (RelativeLayout) findViewById(R.id.clear_rl);


        mTitle.setText(mContext.getString(R.string.chat_detail_title));
        mMenuBtn.setVisibility(View.GONE);
    }

    public void setListeners(OnClickListener onClickListener) {
        mGroupDescLL.setOnClickListener(onClickListener);
        mGroupNameLL.setOnClickListener(onClickListener);
        mGroupAvatarLL.setOnClickListener(onClickListener);
        mMyNameLL.setOnClickListener(onClickListener);
        mGroupNumLL.setOnClickListener(onClickListener);
        mGroupChatRecordLL.setOnClickListener(onClickListener);
        mGroupChatDelLL.setOnClickListener(onClickListener);
        mReturnBtn.setOnClickListener(onClickListener);
        mTv_moreGroup.setOnClickListener(onClickListener);
        mClear_rl.setOnClickListener(onClickListener);
        mChatFile.setOnClickListener(onClickListener);
    }

    public void setOnChangeListener(SlipButton.OnChangedListener listener) {
        mNoDisturbBtn.setOnChangedListener(R.id.no_disturb_slip_btn, listener);
        mBlockBtn.setOnChangedListener(R.id.block_slip_btn, listener);
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setGroupName(String str) {
        mGroupName.setText(str);
    }

    public void setMyName(String str) {
        mMyName.setText(str);
    }

    public void setSingleView(boolean friend) {
        mGroupNameLL.setVisibility(View.GONE);
        mGroupAvatarLL.setVisibility(View.GONE);
        mGroupNumLL.setVisibility(View.GONE);
        mMyNameLL.setVisibility(View.GONE);
    }

    public void updateGroupName(String newName) {
        mGroupName.setText(newName);
    }

    public void setGroupDesc(String desc) {
        mGroupDesc.setText(desc);
    }

    public void dismissAllMembersBtn() {
        mSplitLine1.setVisibility(View.GONE);
        mSplitLine2.setVisibility(View.GONE);
        mGroupDescLL.setVisibility(View.GONE);
    }

    public void initNoDisturb(int status) {
        mNoDisturbBtn.setChecked(status == 1);
    }

    public void setNoDisturbChecked(boolean flag) {
        mNoDisturbBtn.setChecked(flag);
    }

    public void showBlockView(int status) {
        mBlockRl.setVisibility(VISIBLE);
        mBlockLine.setVisibility(VISIBLE);
        mBlockBtn.setChecked(status == 1);
    }

    public void setBlockChecked(boolean flag) {
        mBlockBtn.setChecked(flag);
    }

    public void isLoadMoreShow(boolean isLoad) {
        if (isLoad) {
            mTv_moreGroup.setVisibility(VISIBLE);
        } else {
            mTv_moreGroup.setVisibility(GONE);
        }
    }

    public void setGroupAvatar(File groupAvatar) {
        mIv_groupAvatar.setImageBitmap(BitmapFactory.decodeFile(groupAvatar.getAbsolutePath()));
    }
}
