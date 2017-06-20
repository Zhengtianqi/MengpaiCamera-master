package com.sobot.chat.viewHolder;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.utils.HtmlTools;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.viewHolder.base.MessageHolderBase;

/**
 * 提醒消息
 * Created by jinxl on 2017/3/17.
 */
public class RemindMessageHolder extends MessageHolderBase {
    TextView center_Remind_Info; // 中间提醒消息
    TextView center_Remind_Info1; // 已无更多记录
    RelativeLayout rl_not_read; //以下为新消息

    public RemindMessageHolder(Context context, View convertView) {
        super(context, convertView);
        center_Remind_Info = (TextView) convertView
                .findViewById(ResourceUtils.getIdByName(context, "id","sobot_center_Remind_note"));
        center_Remind_Info1 = (TextView) convertView
                .findViewById(ResourceUtils.getIdByName(context, "id","sobot_center_Remind_note1"));
        rl_not_read = (RelativeLayout) convertView
                .findViewById(ResourceUtils.getIdByName(context, "id","rl_not_read"));
    }

    @Override
    public void bindData(Context context, ZhiChiMessageBase message) {

        if (message.getAnswer() != null && !TextUtils.isEmpty(message.getAnswer().getMsg())) {
            if(message.getAnswer().getRemindType()== ZhiChiConstant.sobot_remind_type_nomore){
                rl_not_read.setVisibility(View.GONE);
                center_Remind_Info.setVisibility(View.GONE);
                center_Remind_Info1.setVisibility(View.VISIBLE);
                center_Remind_Info1.setText(message.getAnswer().getMsg());
            } else if(message.getAnswer().getRemindType()==ZhiChiConstant.sobot_remind_type_below_unread){
                rl_not_read.setVisibility(View.VISIBLE);
                center_Remind_Info.setVisibility(View.GONE);
                center_Remind_Info1.setVisibility(View.GONE);
            } else {
                rl_not_read.setVisibility(View.GONE);
                center_Remind_Info.setVisibility(View.VISIBLE);
                center_Remind_Info1.setVisibility(View.GONE);
                int remindType = message.getAnswer().getRemindType();
                if (ZhiChiConstant.action_remind_info_post_msg.equals(message.getAction())) {
                    if(remindType == ZhiChiConstant.sobot_remind_type_customer_offline || remindType == ZhiChiConstant.sobot_remind_type_unable_to_customer){
                        //暂无客服在线   和 暂时无法转接人工客服
                        if(message.isShake()){
                            center_Remind_Info.setAnimation(shakeAnimation(5));
                        }
                        setRemindPostMsg(context,center_Remind_Info,message);
                    }
                } else if (ZhiChiConstant.action_remind_info_paidui.equals(message.getAction())) {
                    if(remindType == ZhiChiConstant.sobot_remind_type_paidui_status){
                        //您在队伍中的第...
                        if(message.isShake()){
                            center_Remind_Info.setAnimation(shakeAnimation(5));
                        }
                        setRemindPostMsg(context,center_Remind_Info,message);
                    }
                } else if (ZhiChiConstant.action_remind_connt_success.equals(message.getAction())) {
                    if(remindType == ZhiChiConstant.sobot_remind_type_accept_request){
                        //接受了您的请求
                        center_Remind_Info.setText(Html.fromHtml(message.getAnswer().getMsg()));
                    }
                } else if (ZhiChiConstant.sobot_outline_leverByManager.equals(message
                        .getAction()) || ZhiChiConstant.action_remind_past_time.equals(message.getAction())) {
                    //结束了本次会话  有事离开 超时下线 ....的提醒
                    HtmlTools.getInstance(context).setRichText(center_Remind_Info,message
                            .getAnswer().getMsg(), ResourceUtils.getIdByName(context, "color","sobot_color_link_remind"));
                } else if(remindType == ZhiChiConstant.sobot_remind_type_evaluate ||remindType == ZhiChiConstant.sobot_remind_type_accept_request){
                    center_Remind_Info.setText(message.getAnswer().getMsg());
                }
            }

            if(message.isShake()){
                center_Remind_Info.setAnimation(shakeAnimation(5));
                message.setShake(false);
            }
        }
    }

    private void setRemindPostMsg(Context context,TextView remindInfo, ZhiChiMessageBase message) {
        int isLeaveMsg = SharedPreferencesUtil.getIntData(context,ZhiChiConstant.sobot_msg_flag, ZhiChiConstant.sobot_msg_flag_open);
        String postMsg = context.getResources().getString(ResourceUtils.getIdByName(context, "string", "sobot_you_can"))+"<a href='sobot:SobotPostMsgActivity'>"+context.getResources().getString(ResourceUtils.getIdByName(context, "string", "sobot_leavemsg"))+"</a>";
        String content = message.getAnswer().getMsg();
        if (isLeaveMsg == ZhiChiConstant.sobot_msg_flag_open){
            content = content + postMsg;
        }
        HtmlTools.getInstance(context).setRichText(remindInfo,content,ResourceUtils.getIdByName
                (context, "color", "sobot_color_link_remind"));
        remindInfo.setEnabled(true);
        message.setShake(false);
    }

    public static Animation shakeAnimation(int counts) {
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }
}
