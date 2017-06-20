package com.sobot.chat.viewHolder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sobot.chat.activity.SobotChatActivity;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.HtmlTools;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.viewHolder.base.MessageHolderBase;

/**
 * 文本消息
 * Created by jinxl on 2017/3/17.
 */
public class TextMessageHolder extends MessageHolderBase {
    TextView msg; // 聊天的消息内容
    public TextMessageHolder(Context context, View convertView){
        super(context,convertView);
        msg = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_msg"));
    }

    @Override
    public void bindData(final Context context,final ZhiChiMessageBase message) {
        if (message.getAnswer() != null && !TextUtils.isEmpty(message.getAnswer().getMsg())) {// 纯文本消息
            msg.setVisibility(View.VISIBLE);
            HtmlTools.getInstance(context).setRichText(msg, message.getAnswer().getMsg(),
                    isRight ? ResourceUtils.getIdByName(context, "color","sobot_color_rlink") : ResourceUtils.getIdByName(context, "color","sobot_color_link"));
            if(isRight){
                try {
                    msgStatus.setClickable(true);
                    if (message.getSendSuccessState() == 1) {// 成功的状态
                        msgStatus.setVisibility(View.GONE);
                        frameLayout.setVisibility(View.GONE);
                        msgProgressBar.setVisibility(View.GONE);
                    }

                    if (message.getSendSuccessState() == 0) {
                        frameLayout.setVisibility(View.VISIBLE);
                        msgStatus.setVisibility(View.VISIBLE);
                        msgProgressBar.setVisibility(View.GONE);
                        msgStatus.setOnClickListener(new ReSendTextLisenter(context,message
                                .getId(), message.getAnswer().getMsg(), msgStatus));
                    } else if (message.getSendSuccessState() == 0) {
                        frameLayout.setVisibility(View.VISIBLE);
                        msgStatus.setVisibility(View.VISIBLE);
                        msgProgressBar.setVisibility(View.GONE);
                        msgStatus.setOnClickListener(new ReSendTextLisenter(context,message
                                .getId(), message.getAnswer().getMsg(),msgStatus));
                    } else if (message.getSendSuccessState() == 2) {
                        frameLayout.setVisibility(View.VISIBLE);
                        msgProgressBar.setVisibility(View.VISIBLE);
                        msgStatus.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else {
            msg.setText(CommonUtils.getResString(context,ResourceUtils.getIdByName(context, "string", "sobot_data_wrong_hint")));
        }
        msg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!TextUtils.isEmpty(message.getAnswer().getMsg())){
                    ToastUtil.showCopyPopWindows(context,view, message.getAnswer().getMsg().replace("&amp;","&"), 30,0);
                }
                return false;
            }
        });
    }

    public static class ReSendTextLisenter implements View.OnClickListener {

        private String id;
        private String msgContext;
        private ImageView msgStatus;
        private Context context;

        public ReSendTextLisenter(final Context context,String id, String msgContext,ImageView
                msgStatus) {
            super();
            this.context=context;
            this.id = id;
            this.msgContext = msgContext;
            this.msgStatus = msgStatus;
        }

        @Override
        public void onClick(View arg0) {
            if (msgStatus != null) {
                msgStatus.setClickable(false);
            }
            showReSendTextDialog(context,id, msgContext,msgStatus);
        }
    }

    @SuppressWarnings("deprecation")
    private static void showReSendTextDialog(final Context context,final String mid,
                                             final String mmsgContext, final ImageView msgStatus) {
        showReSendDialog(context,msgStatus,new ReSendListener(){

            @Override
            public void onReSend() {
                sendTextBrocast(context, mid, mmsgContext);
            }
        });
    }

    public static void sendTextBrocast(Context context, String id, String msgContent) {
        if (context != null){
            ZhiChiMessageBase msgObj = new ZhiChiMessageBase();
            msgObj.setContent(msgContent);
            msgObj.setId(id);
            ((SobotChatActivity) context).sendMessageToRobot(msgObj,1, 3,"");
        }
    }
}
