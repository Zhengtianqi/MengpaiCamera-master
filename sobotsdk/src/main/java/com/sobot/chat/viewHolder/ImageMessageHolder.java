package com.sobot.chat.viewHolder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.chat.activity.SobotChatActivity;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.utils.BitmapUtil;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.viewHolder.base.MessageHolderBase;
import com.sobot.chat.widget.RoundProgressBar;

/**
 * 图片消息
 * Created by jinxl on 2017/3/17.
 */
public class ImageMessageHolder extends MessageHolderBase {
    ImageView image;
    ImageView pic_send_status;
    public ProgressBar pic_progress;
    public RoundProgressBar sobot_pic_progress_round;
    TextView isGif;
    RelativeLayout sobot_pic_progress_rl;

    public ImageMessageHolder(Context context, View convertView){
        super(context,convertView);
        isGif = (TextView) convertView.findViewById(ResourceUtils
                .getIdByName(context, "id", "sobot_pic_isgif"));
        image = (ImageView) convertView.findViewById(ResourceUtils
                .getIdByName(context, "id", "sobot_iv_picture"));

        pic_send_status = (ImageView) convertView
                .findViewById(ResourceUtils.getIdByName(context, "id",
                        "sobot_pic_send_status"));
        pic_progress = (ProgressBar) convertView
                .findViewById(ResourceUtils.getIdByName(context, "id",
                        "sobot_pic_progress"));
        sobot_pic_progress_round = (RoundProgressBar) convertView.findViewById(ResourceUtils.getIdByName(context, "id",
                "sobot_pic_progress_round"));

        sobot_pic_progress_rl = (RelativeLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_pic_progress_rl"));
    }

    @Override
    public void bindData(final Context context,final ZhiChiMessageBase message) {
        isGif.setVisibility(View.GONE);
        image.setVisibility(View.VISIBLE);
        if(isRight){
            sobot_pic_progress_round.setVisibility(View.VISIBLE);
            sobot_pic_progress_rl.setVisibility(View.VISIBLE);
            if (ZhiChiConstant.result_fail_code == message.getMysendMessageState()) {
                pic_send_status.setVisibility(View.VISIBLE);
                pic_progress.setVisibility(View.GONE);
                sobot_pic_progress_round.setVisibility(View.GONE);
                sobot_pic_progress_rl.setVisibility(View.GONE);
                // 点击重新发送按钮
                pic_send_status.setOnClickListener(new RetrySendImageLisenter(context,message
                        .getId(), message.getAnswer().getMsg(), pic_send_status));
            } else if (ZhiChiConstant.result_success_code == message.getMysendMessageState()) {
                pic_send_status.setVisibility(View.GONE);
                pic_progress.setVisibility(View.GONE);
                sobot_pic_progress_round.setVisibility(View.GONE);
                sobot_pic_progress_rl.setVisibility(View.GONE);
            } else if (ZhiChiConstant.hander_sendPicIsLoading == message
                    .getMysendMessageState()) {
                pic_progress.setVisibility(View.GONE);
                pic_send_status.setVisibility(View.GONE);
            } else {
                pic_send_status.setVisibility(View.GONE);
                pic_progress.setVisibility(View.GONE);
                sobot_pic_progress_round.setVisibility(View.GONE);
                sobot_pic_progress_rl.setVisibility(View.GONE);
            }
        }

        String picPath = message.getAnswer().getMsg();
        if(picPath.endsWith("gif") || picPath.endsWith("GIF")){
            isGif.setVisibility(View.VISIBLE);
        }else{
            isGif.setVisibility(View.GONE);
        }
        BitmapUtil.display(context, message.getAnswer().getMsg(), image);
        image.setOnClickListener(new ImageClickLisenter(context,message.getAnswer().getMsg(), isRight));
    }

    // 图片的重新发送监听
    public static class RetrySendImageLisenter implements View.OnClickListener {
        private String id;
        private String imageUrl;
        private ImageView img;

        private Context context;

        public RetrySendImageLisenter(final Context context,String id, String imageUrl,
                                      ImageView image) {
            super();
            this.id = id;
            this.imageUrl = imageUrl;
            this.img = image;
            this.context = context;
        }

        @Override
        public void onClick(View view) {

            if (img != null) {
                img.setClickable(false);
            }
            showReSendPicDialog(context, imageUrl, id,img);
        }
    }

    @SuppressWarnings("deprecation")
    private static void showReSendPicDialog(final Context context,final String mimageUrl, final String mid,final ImageView msgStatus) {

        showReSendDialog(context,msgStatus,new ReSendListener(){

            @Override
            public void onReSend() {
                // 获取图片的地址url
                // 上传url
                // 采用广播进行重发
                if (context != null) {
                    ZhiChiMessageBase msgObj = new ZhiChiMessageBase();
                    msgObj.setContent(mimageUrl);
                    msgObj.setId(mid);
                    msgObj.setMysendMessageState(ZhiChiConstant.hander_sendPicIsLoading);
                    ((SobotChatActivity) context).sendMessageToRobot(msgObj, 3, 3, "");
                }
            }
        });
    }
}
