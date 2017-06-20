package com.sobot.chat.server;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.sobot.chat.api.apiUtils.GsonUtil;
import com.sobot.chat.api.apiUtils.ZhiChiConstants;
import com.sobot.chat.api.enumtype.CustomerState;
import com.sobot.chat.api.model.ZhiChiInitModeBase;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.api.model.ZhiChiPushMessage;
import com.sobot.chat.api.model.ZhiChiReplyAnswer;
import com.sobot.chat.core.channel.Const;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.NotificationUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.ZhiChiConfig;
import com.sobot.chat.utils.ZhiChiConstant;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jinxl on 2016/9/13.
 */
public class SobotSessionServer extends Service {

    private MyMessageReceiver receiver;
    private int tmpNotificationId = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i("SobotSessionServer  ---> onCreate");
        initBrocastReceiver();
    }

    /* 初始化广播接受者 */
    private void initBrocastReceiver() {
        if (receiver == null) {
            receiver = new MyMessageReceiver();
        }
        // 创建过滤器，并指定action，使之用于接收同action的广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(ZhiChiConstants.receiveMessageBrocast);
        // 注册广播接收器
        registerReceiver(receiver, filter);
    }

    public class MyMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ZhiChiConstants.receiveMessageBrocast.equals(intent.getAction())) {
                if (!CommonUtils.getRunningActivityName(getApplicationContext().getApplicationContext()).contains(
                        "SobotChatActivity")) {
                    // 接受下推的消息
                    ZhiChiPushMessage pushMessage = (ZhiChiPushMessage) intent.getExtras().getSerializable(ZhiChiConstants.ZHICHI_PUSH_MESSAGE);
                    receiveMessage(pushMessage);
                }
            }
        }
    }

    private void receiveMessage(ZhiChiPushMessage pushMessage) {
        if(pushMessage == null){
            return;
        }
        // 接受下推的消息
        ZhiChiMessageBase base = new ZhiChiMessageBase();
        base.setSenderName(pushMessage.getAname());
        ZhiChiConfig config = SobotMsgManager.getInstance(getApplication()).getConfig();
        if (ZhiChiConstant.push_message_createChat == pushMessage.getType()) {
            if (config.getInitModel() != null) {
                config.adminFace = pushMessage.getAface();
                int type = Integer.parseInt(config.getInitModel().getType());
                if (type == 2 || type == 3 || type == 4) {
                    createCustomerService(pushMessage.getAname(),pushMessage.getAface());
                }
            }
        } else if (ZhiChiConstant.push_message_receverNewMessage == pushMessage
                .getType()) {// 接收到新的消息
            if (config.getInitModel() != null) {
                if (config.customerState == CustomerState.Online) {
                    base.setSender(pushMessage.getAname());
                    base.setSenderName(pushMessage.getAname());
                    base.setSenderFace(pushMessage.getAface());
                    base.setSenderType(ZhiChiConstant.message_sender_type_service + "");
                    ZhiChiReplyAnswer reply = null;
                    if(TextUtils.isEmpty(pushMessage.getMsgType())){
                        return;
                    }
                    if ("7".equals(pushMessage.getMsgType())) {
                        reply = GsonUtil.jsonToZhiChiReplyAnswer(pushMessage
                                .getContent());
                    } else {
                        reply = new ZhiChiReplyAnswer();
                        reply.setMsgType(pushMessage.getMsgType() + "");
                        reply.setMsg(pushMessage.getContent());
                    }
                    base.setAnswer(reply);
                    // 更新界面的操作
                    //添加“以下为未读消息”
                    if (config.isShowUnreadUi) {
                        config.addMessage(ChatUtils.getUnreadMode(getApplicationContext()));
                        config.isShowUnreadUi = false;
                    }
                    config.addMessage(base);
                    if (config.customerState == CustomerState.Online) {
                        config.customTimeTask = false;
                        config.userInfoTimeTask = true;
                    }
                }
            }

            if (!CommonUtils.getRunningActivityName(getApplicationContext().getApplicationContext()).contains(
                    "SobotChatActivity") || CommonUtils.isApplicationBroughtToBackground(getApplicationContext())|| CommonUtils.isScreenLock(getApplicationContext())) {

                String content;
                int msgType = -1;
                try {
                    JSONObject jsonObject = new JSONObject(pushMessage.getContent());
                    content = jsonObject.optString("msg");
                    msgType = jsonObject.optInt("msgType");
                } catch (JSONException e) {
                    content = "";
                    e.printStackTrace();
                }
                if (msgType != -1 && !TextUtils.isEmpty(content)) {
                    String notificationContent = content;
                    if (msgType == ZhiChiConstant.message_type_textAndPic || msgType ==
                            ZhiChiConstant.message_type_textAndText) {
                        content = "[富文本]";
                        notificationContent = "您收到了一条新消息";
                    } else if (msgType == ZhiChiConstant.message_type_pic) {
                        content = "[图片]";
                        notificationContent = "[图片]";
                    }
                    if (!CommonUtils.getRunningActivityName(getApplicationContext().getApplicationContext()).contains(
                            "SobotChatActivity")){
                        int localUnreadNum = SharedPreferencesUtil.getIntData(getApplicationContext(),
                                "sobot_unread_count", 0);
                        localUnreadNum++;
                        SharedPreferencesUtil.saveIntData(getApplicationContext(), "sobot_unread_count", localUnreadNum);
                        Intent intent = new Intent();
                        intent.setAction(ZhiChiConstant.sobot_unreadCountBrocast);
                        intent.putExtra("noReadCount", localUnreadNum);
                        intent.putExtra("content", content);
                        CommonUtils.sendLocalBroadcast(getApplicationContext(), intent);
                    }
                    showNotification(notificationContent);
                }
            }
        } else if (ZhiChiConstant.push_message_paidui == pushMessage.getType()) {
            // 排队的消息类型
            if (config.getInitModel() != null) {
                createCustomerQueue(pushMessage.getCount());
            }
        } else if (ZhiChiConstant.push_message_outLine == pushMessage.getType()) {// 用户被下线
            config.clearCache();
            showNotification("您好，本次会话已结束");
        }  else if (ZhiChiConstant.push_message_transfer == pushMessage.getType()) {
            LogUtils.i("用户被转接--->"+pushMessage.getName());
            //替换标题
            config.activityTitle = pushMessage.getName(); // 设置后台推送消息的对象
            config.adminFace = pushMessage.getFace();
            config.currentUserName = pushMessage.getName();
        }
    }

    /**
     * 连接客服时，需要排队
     * 显示排队的处理逻辑
     * @param num 当前排队的位置
     */
    private void createCustomerQueue(String num){
        ZhiChiConfig config = SobotMsgManager.getInstance(getApplication()).getConfig();
        if (config.customerState == CustomerState.Queuing && !TextUtils
                .isEmpty(num) && Integer.parseInt(num) > 0) {
            ZhiChiInitModeBase initModel = config.getInitModel();
            if(initModel == null){
                return;
            }
            int type = Integer.parseInt(initModel.getType());
            config.queueNum = Integer.parseInt(num);
            //显示当前排队的位置
            config.addMessage(ChatUtils.getInLineHint(getApplicationContext(),config.queueNum));

            if (type == ZhiChiConstant.type_custom_only) {
                //显示标题
                config.activityTitle = ChatUtils.getLogicTitle(getApplicationContext(),false, getResString("sobot_in_line_title"),
                        initModel.getCompanyName());
                config.bottomViewtype = ZhiChiConstant.bottomViewtype_onlycustomer_paidui;
            } else {
                config.activityTitle = ChatUtils.getLogicTitle(getApplicationContext(),false, initModel.getRobotName(),
                        initModel.getCompanyName());
                config.bottomViewtype = ZhiChiConstant.bottomViewtype_paidui;
            }
        }
    }

    /**
     * 建立与客服的对话
     * @param name 客服的名称
     * @param face  客服的头像
     */
    private void createCustomerService(String name,String face){
        ZhiChiConfig config = SobotMsgManager.getInstance(getApplication()).getConfig();
        ZhiChiInitModeBase initModel = config.getInitModel();
        if(initModel == null){
            return;
        }
        //仅机器人模式不用显示人工
        //改变变量
        config.current_client_model = ZhiChiConstant.client_model_customService;
        config.customerState = CustomerState.Online;
        config.isAboveZero = false;
        config.isComment = false;// 转人工时 重置为 未评价
        config.queueNum = 0;
        config.currentUserName = TextUtils.isEmpty(name)?"":name;
        //显示被xx客服接入
        config.addMessage(ChatUtils.getServiceAcceptTip(getApplicationContext(),name));
        //显示人工欢迎语
        config.addMessage(ChatUtils.getServiceHelloTip(name,face,initModel.getAdminHelloWord()));
        //显示标题
        config.activityTitle = ChatUtils.getLogicTitle(getApplicationContext(),false, name,
                initModel.getCompanyName());
        //设置底部键盘
        config.bottomViewtype = ZhiChiConstant.bottomViewtype_customer;

        // 启动计时任务
        config.userInfoTimeTask = true;
        config.customTimeTask = false;

        // 把机器人回答中的转人工按钮都隐藏掉
        config.hideItemTransferBtn();

        if (!CommonUtils.getRunningActivityName(getApplicationContext().getApplicationContext()).contains(
                "SobotChatActivity") || CommonUtils.isApplicationBroughtToBackground(getApplicationContext())|| CommonUtils.isScreenLock(getApplicationContext())) {
            showNotification(String.format(getResString("sobot_service_accept"), config.currentUserName));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 取消广播接受者
        unregisterReceiver(receiver);
        LogUtils.i("SobotSessionServer  ---> onDestroy");
    }

    public String getResString(String name) {
        return getResources().getString(getResStringId(name));
    }

    public int getResStringId(String name) {
        return ResourceUtils.getIdByName(getApplicationContext(), "string", name);
    }

    /**
     * 显示通知栏
     *
     * @param content
     */
    private void showNotification(String content) {
        boolean notification_flag = SharedPreferencesUtil.getBooleanData(getApplicationContext(), Const
                .SOBOT_NOTIFICATION_FLAG, false);

        if (notification_flag) {
            String notificationTitle = "客服提示";
            NotificationUtils.createNotification(getApplicationContext(), null, notificationTitle, content, content, getNotificationId());
        }
    }


    /**
     * 获取通知的id  如果id涨到了999那么重置为0，从1开始发送
     *
     * @return
     */
    private int getNotificationId() {
        if (tmpNotificationId == 999) {
            tmpNotificationId = 0;
        }
        tmpNotificationId++;
        return tmpNotificationId;
    }
}