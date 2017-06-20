package com.sobot.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.sobot.chat.activity.SobotChatActivity;
import com.sobot.chat.api.ZhiChiApi;
import com.sobot.chat.api.enumtype.SobotChatTitleDisplayMode;
import com.sobot.chat.api.model.CommonModel;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.core.channel.Const;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.core.http.callback.StringResultCallBack;
import com.sobot.chat.listener.HyperlinkListener;
import com.sobot.chat.server.SobotSessionServer;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.NotificationUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.SobotOption;
import com.sobot.chat.utils.ZhiChiConstant;

/**
 * SobotChatApi接口输出类
 */
public class SobotApi {

	private static String Tag = SobotApi.class.getSimpleName();

	/**
	 * 打开客服界面
	 * @param context 上下文对象
	 * @param information 接入参数
     */
	public static void startSobotChat(Context context, Information information) {
		if (information == null || context == null){
			Log.e(Tag, "Information is Null!");
			return;
		}
		Intent intent = new Intent(context, SobotChatActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("info", information);
		intent.putExtra("informationBundle", bundle);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	/**
	 * 初始化消息链接
	 * @param context 上下文对象
	 */
	public static void initSobotChannel(Context context){
		if (context == null){
			return;
		}
		context = context.getApplicationContext();
		SobotMsgManager.getInstance(context).getZhiChiApi().reconnectChannel();
		context.startService(new Intent(context, SobotSessionServer.class));
	}

	/**
	 * 获取当前未读消息数
	 * @param context
	 * @return
     */
	public static int getUnreadMsg(Context context){
		if (context == null){
			return  0;
		} else {
			return SharedPreferencesUtil.getIntData(context,"sobot_unread_count",0);
		}
	}

	/**
	 * 断开与智齿服务器的链接
	 * @param context 上下文对象
	 */
	public static void disSobotChannel(Context context){
		if (context == null){
			return;
		}
		SobotMsgManager.getInstance(context).getZhiChiApi().disconnChannel();
		SobotMsgManager.getInstance(context).getConfig().clearCache();
	}

	/**
	 * 退出客服，用于用户退出登录时调用
	 * @param context 上下文对象
     */
	public static void exitSobotChat(final Context context){
		if (context == null){
			return;
		}
		disSobotChannel(context);
		context.stopService(new Intent(context, SobotSessionServer.class));

		String cid = SharedPreferencesUtil.getStringData(context,Const.SOBOT_CID,"");
		String uid = SharedPreferencesUtil.getStringData(context,Const.SOBOT_UID,"");
		SharedPreferencesUtil.removeKey(context,Const.SOBOT_WSLINKBAK);
		SharedPreferencesUtil.removeKey(context,Const.SOBOT_WSLINKDEFAULT);
		SharedPreferencesUtil.removeKey(context,Const.SOBOT_UID);
		SharedPreferencesUtil.removeKey(context,Const.SOBOT_CID);
		SharedPreferencesUtil.removeKey(context,Const.SOBOT_PUID);
		SharedPreferencesUtil.removeKey(context,Const.SOBOT_APPKEY);

		if (!TextUtils.isEmpty(cid) && !TextUtils.isEmpty(uid)){
			ZhiChiApi zhiChiApi = SobotMsgManager.getInstance(context).getZhiChiApi();
			zhiChiApi.out(cid, uid,	new StringResultCallBack<CommonModel>() {
				@Override
				public void onSuccess(CommonModel result) {
					LogUtils.i("下线成功");
				}

				@Override
				public void onFailure(Exception e, String des) {}
			});
		}
	}

	/**
	 * 设置是否开启消息提醒   默认不提醒
	 * @param context
	 * @param flag
	 * @param smallIcon 小图标的id 设置通知栏中的小图片，尺寸一般建议在24×24
	 * @param largeIcon 大图标的id
     */
	public static void setNotificationFlag(Context context,boolean flag,int smallIcon,int largeIcon){
		if (context == null){
			return;
		}
		SharedPreferencesUtil.saveBooleanData(context,Const.SOBOT_NOTIFICATION_FLAG,flag);
		SharedPreferencesUtil.saveIntData(context, ZhiChiConstant.SOBOT_NOTIFICATION_SMALL_ICON, smallIcon);
		SharedPreferencesUtil.saveIntData(context, ZhiChiConstant.SOBOT_NOTIFICATION_LARGE_ICON, largeIcon);
	}

	/**
	 * 清除所有通知
	 * @param context
     */
	public static void cancleAllNotification(Context context){
		if (context == null){
			return;
		}
		NotificationUtils.cancleAllNotification(context);
	}

	/**
	 * 设置超链接的点击事件监听
	 * @param hyperlinkListener
     */
	public static void setHyperlinkListener(HyperlinkListener hyperlinkListener){
		SobotOption.hyperlinkListener = hyperlinkListener;
	}

	/**
	 * 设置聊天界面标题显示模式
	 * @param context 上下文对象
	 * @param mode titile的显示模式
	 *              SobotChatTitleDisplayMode.Default:显示客服昵称(默认)
	 *              SobotChatTitleDisplayMode.ShowFixedText:显示固定文本
	 *              SobotChatTitleDisplayMode.ShowCompanyName:显示console设置的企业名称
	 * @param content 如果需要显示固定文本，需要传入此参数，其他模式可以不传
     */
	public static void setChatTitleDisplayMode(Context context, SobotChatTitleDisplayMode mode, String content){
		if (context == null){
			return;
		}
		SharedPreferencesUtil.saveIntData(context,ZhiChiConstant.SOBOT_CHAT_TITLE_DISPLAY_MODE,
				mode.getValue());
		SharedPreferencesUtil.saveStringData(context,ZhiChiConstant.SOBOT_CHAT_TITLE_DISPLAY_CONTENT,
				content);
	}

	/**
	 * 控制显示历史聊天记录的时间范围
	 * @param time  查询时间(例:100-表示从现在起前100分钟的会话)
     */
	public static void hideHistoryMsg(Context context,long time){
		if (context == null){
			return;
		}
		SharedPreferencesUtil.saveLongData(context,ZhiChiConstant.SOBOT_CHAT_HIDE_HISTORYMSG_TIME,
				time);
	}

	/**
	 * 配置用户提交人工满意度评价后释放会话
	 * @param context
	 * @param flag
	 */
	public static void setEvaluationCompletedExit(Context context,boolean flag){
		if (context == null){
			return;
		}
		SharedPreferencesUtil.saveBooleanData(context,ZhiChiConstant.SOBOT_CHAT_EVALUATION_COMPLETED_EXIT, flag);
	}
}