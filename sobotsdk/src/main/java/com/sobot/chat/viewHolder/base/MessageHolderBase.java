package com.sobot.chat.viewHolder.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sobot.chat.activity.SobotPhotoActivity;
import com.sobot.chat.adapter.base.SobotMsgAdapter;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.utils.BitmapUtil;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.widget.ReSendDialog;

/**
 * view基类
 * Created by jinxl on 2017/3/17.
 */
public abstract class MessageHolderBase {
    public boolean isRight = false;

    public TextView name; // 用户姓名
    public ImageView imgHead;// 头像
    public TextView reminde_time_Text;//时间提醒

    public FrameLayout frameLayout;
    public ImageView msgStatus;// 消息发送的状态
    public ProgressBar msgProgressBar; // 重新发送的进度条的信信息；

    public MessageHolderBase(Context context, View convertView) {
        reminde_time_Text = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id","sobot_reminde_time_Text"));
        imgHead = (ImageView) convertView.findViewById(ResourceUtils.getIdByName(context, "id","sobot_imgHead"));
        name = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id","sobot_name"));
        frameLayout = (FrameLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id","sobot_frame_layout"));
        msgProgressBar = (ProgressBar) convertView.findViewById(ResourceUtils.getIdByName(context, "id","sobot_msgProgressBar"));// 重新发送的进度条信息
        // 消息的状态
        msgStatus = (ImageView) convertView.findViewById(ResourceUtils.getIdByName(context, "id","sobot_msgStatus"));
    }

    public abstract void bindData(Context context, final ZhiChiMessageBase message);

    /**
     * 根据收发消息的标识，设置客服客户的头像
     *
     * @param itemType
     */
    public void initNameAndFace(int itemType, Context context, final ZhiChiMessageBase message,
                                String senderface, String sendername) {
        switch (itemType) {
            case SobotMsgAdapter.MSG_TYPE_IMG_R:
            case SobotMsgAdapter.MSG_TYPE_TXT_R:
            case SobotMsgAdapter.MSG_TYPE_AUDIO_R:
                this.isRight = true;
                int defId = ResourceUtils.getIdByName(context, "drawable", "sobot_chatting_default_head");
                imgHead.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(senderface)) {
                    BitmapUtil.displayRound(context, defId, imgHead, defId);
                } else {
                    BitmapUtil.displayRound(context, CommonUtils.encode(senderface), imgHead, defId);
                }

                name.setVisibility(TextUtils.isEmpty(sendername) ? View.GONE : View.VISIBLE);
                name.setText(sendername);
                break;
            case SobotMsgAdapter.MSG_TYPE_TXT_L:
            case SobotMsgAdapter.MSG_TYPE_RICH:
            case SobotMsgAdapter.MSG_TYPE_IMG_L:
                this.isRight = false;
                //昵称、头像显示
                name.setVisibility(TextUtils.isEmpty(message.getSenderName()) ? View.GONE : View.VISIBLE);
                name.setText(message.getSenderName());
                BitmapUtil.displayRound(context, CommonUtils.encode(message.getSenderFace()),
                        imgHead, ResourceUtils.getIdByName(context, "drawable", "sobot_avatar_robot"));
                break;
            default:
                break;
        }
    }

    public boolean isRight() {
        return isRight;
    }

    public void setRight(boolean right) {
        isRight = right;
    }

    /**
     * 显示重新发送dialog
     * @param msgStatus
     * @param reSendListener
     */
    public static void showReSendDialog(Context context,final ImageView msgStatus,final ReSendListener reSendListener ){
        int width = context.getResources().getDisplayMetrics().widthPixels;
        int widths = 0;
        if (width == 480) {
            widths = 80;
        } else {
            widths = 120;
        }
        final ReSendDialog reSendDialog = new ReSendDialog(context);
        reSendDialog.setOnClickListener(new ReSendDialog.OnItemClick() {
            @Override
            public void OnClick(int type) {
                if (type == 0) {// 0：确定 1：取消
                    reSendListener.onReSend();
                }
                reSendDialog.dismiss();
            }
        });
        reSendDialog.show();
        msgStatus.setClickable(true);
        WindowManager windowManager = ((Activity)context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = reSendDialog.getWindow()
                .getAttributes();
        lp.width = (int) (display.getWidth() - widths); // 设置宽度
        reSendDialog.getWindow().setAttributes(lp);
    }

    public interface ReSendListener{
        void onReSend();
    }

    // 图片的事件监听
    public static class ImageClickLisenter implements View.OnClickListener {
        private Context context;
        private String imageUrl;
        private boolean isRight;

        public ImageClickLisenter(Context context,String imageUrl) {
            super();
            this.imageUrl = imageUrl;
            this.context = context;
        }

        // isRight: 我发送的图片显示时，gif当一般图片处理
        public ImageClickLisenter(Context context,String imageUrl, boolean isRight) {
            this(context,imageUrl);
            this.isRight = isRight;
        }

        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent(context, SobotPhotoActivity.class);
            intent.putExtra("imageUrL", imageUrl);
            if (isRight) {
                intent.putExtra("isRight", isRight);
            }
            context.startActivity(intent);
        }
    }
}

