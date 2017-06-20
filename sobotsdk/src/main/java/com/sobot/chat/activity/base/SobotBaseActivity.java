package com.sobot.chat.activity.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sobot.chat.adapter.base.SobotMsgAdapter;
import com.sobot.chat.api.ResultCallBack;
import com.sobot.chat.api.ZhiChiApi;
import com.sobot.chat.api.enumtype.CustomerState;
import com.sobot.chat.api.model.CommonModelBase;
import com.sobot.chat.api.model.ZhiChiInitModeBase;
import com.sobot.chat.api.model.ZhiChiMessage;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.api.model.ZhiChiReplyAnswer;
import com.sobot.chat.application.MyApplication;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.core.http.callback.StringResultCallBack;
import com.sobot.chat.listener.NoDoubleClickListener;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public abstract class SobotBaseActivity extends Activity implements
		OnClickListener {

	public boolean isAboveZero = false;
	public static final int SEND_VOICE = 0;
	public static final int UPDATE_VOICE = 1;
	public static final int CANCEL_VOICE = 2;
	public TextView mTitleTextView;
	public TextView sobot_title_conn_status;
	public LinearLayout sobot_container_conn_status;
	public ProgressBar sobot_conn_loading;
	public TextView sobot_tv_left;
	public TextView sobot_tv_right;
	private FrameLayout mContentLayout;
	public RelativeLayout relative;
	public RelativeLayout net_status_remide;
	private String adminFace = "";

	protected ZhiChiInitModeBase initModel;/*初始化成功服务器返回的实体对象*/
	protected File cameraFile;
	protected String currentUserName;
	/**
	 * 定时任务的处理 用户的定时任务
	 */
	protected int type = -1;
	protected Timer timerUserInfo;
	protected TimerTask taskUserInfo;
	protected int noReplyTimeUserInfo = 0; // 用户已经无应答的时间
	/**
	 * 客服的定时任务
	 */
	protected Timer timerCustom;
	protected TimerTask taskCustom;
	protected int noReplyTimeCustoms = 0;// 客服无应答的时间

	/**
	 * 录音的定时
	 */
	protected Timer voiceTimer;
	protected TimerTask voiceTimerTask;
	protected int voiceTimerLong = 0;
	protected String voiceTimeLongStr = "00";// 时间的定时的任务

	protected int current_client_model = ZhiChiConstant.client_model_robot;

	public boolean is_startCustomTimerTask = false;

	public CustomerState customerState = CustomerState.Offline;

	public boolean customTimeTask=false;
	public boolean userInfoTimeTask=false;
	public int remindRobotMessageTimes = 0;//机器人的提醒次数
	protected SobotMsgAdapter messageAdapter;

	public ZhiChiApi zhiChiApi;

	protected boolean has_camera_permission = false;
	protected boolean has_write_external_storage_permission = false;
	protected boolean has_record_audio_permission = false;

	public void setAdminFace(String str) {
		LogUtils.i("头像地址是" + str);
		this.adminFace = str;
	}

	public String getAdminFace(){
		return this.adminFace;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setupViews(); // 加载 activity_title 布局 ，并获取标题及两侧按钮
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		zhiChiApi = SobotMsgManager.getInstance(getApplicationContext()).getZhiChiApi();
		MyApplication.getInstance().addActivity(this);
	}

	@Override
	protected void onDestroy() {
		MyApplication.getInstance().deleteActivity(this);
		super.onDestroy();
	}
	
	@SuppressLint("NewApi")
	private void setupViews() {
		super.setContentView(ResourceUtils.getIdByName(this, "layout",
				"sobot_title_activity"));
		relative = (RelativeLayout) findViewById(getResId("sobot_layout_titlebar"));
		mTitleTextView = (TextView) findViewById(getResId("sobot_text_title"));
		sobot_title_conn_status = (TextView) findViewById(getResId("sobot_title_conn_status"));
		sobot_container_conn_status = (LinearLayout) findViewById(getResId("sobot_container_conn_status"));
		sobot_conn_loading = (ProgressBar) findViewById(getResId("sobot_conn_loading"));
		mContentLayout = (FrameLayout) findViewById(getResId("sobot_layout_content"));
		sobot_tv_left = (TextView) findViewById(getResId("sobot_tv_left"));
		net_status_remide = (RelativeLayout) findViewById(getResId("sobot_net_status_remide"));

		sobot_tv_right = (TextView) findViewById(getResId("sobot_tv_right"));
		sobot_tv_right.setOnClickListener(new NoDoubleClickListener() {
			@Override
			public void onNoDoubleClick(View v) {
				forwordMethod();
			}
		});
	}

	public void setShowNetRemind(boolean isShow) {
		net_status_remide.setVisibility(isShow ? View.VISIBLE : View.GONE);
	}

	public abstract void forwordMethod();

	/**
	 * 设置导航栏左边按钮
	 * 
	 * @param content
	 *            文字
	 * @param bgResId
	 *            背景
	 */
	protected void showLeftView(String content, int bgResId) {
		sobot_tv_left.setText(content);
		Drawable drawable = getResources().getDrawable(bgResId);
		if(drawable != null){
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
			sobot_tv_left.setCompoundDrawables(drawable, null, null, null);
		}
	}

	/**
	 * @param resourceId
	 * @param textId
	 * @param isShow
     */
	protected void showRightView(int resourceId, String textId, boolean isShow) {
		if (!TextUtils.isEmpty(textId)) {
			sobot_tv_right.setText(textId);
		} else {
			sobot_tv_right.setText("");
		}

		if (resourceId != 0) {
			Drawable img = getResources().getDrawable(resourceId);
			img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
			sobot_tv_right.setCompoundDrawables(null, null, img, null);
		} else {
			sobot_tv_right.setCompoundDrawables(null, null, null, null);
		}

		if (isShow) {
			sobot_tv_right.setVisibility(View.VISIBLE);
		} else {
			sobot_tv_right.setVisibility(View.GONE);
		}
	}

	// 设置标题内容
	@Override
	public void setTitle(int titleId) {
		mTitleTextView.setText(titleId);
	}

	// 设置标题内容
	@Override
	public void setTitle(CharSequence title) {
		mTitleTextView.setText(title);
	}
	
	// 获取标题内容
	public String getActivityTitle() {
		return mTitleTextView.getText().toString();
	}

	// 设置标题文字颜色
	@Override
	public void setTitleColor(int textColor) {
		mTitleTextView.setTextColor(textColor);
	}

	// 取出FrameLayout并调用父类removeAllViews()方法
	@Override
	public void setContentView(int layoutResID) {
		mContentLayout.removeAllViews();
		View.inflate(this, layoutResID, mContentLayout);
		onContentChanged();
	}

	@Override
	public void setContentView(View view) {
		mContentLayout.removeAllViews();
		mContentLayout.addView(view);
		onContentChanged();
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		mContentLayout.removeAllViews();
		mContentLayout.addView(view, params);
		onContentChanged();
	}

	@Override
	public abstract void onClick(View v);

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch(requestCode){
			case ZhiChiConstant.SOBOT_CAMERA_REQUEST_CODE:
				if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
					has_camera_permission = true;
				} else {
					has_camera_permission = false;
					ToastUtil.showToast(getApplicationContext(), getResString("sobot_no_camera_permission"));
				}
				break;
			case ZhiChiConstant.SOBOT_WRITE_EXTERNAL_STORAGE_REQUEST_CODE:
				if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
					has_write_external_storage_permission = true;
				}else{
					has_write_external_storage_permission = false;
					ToastUtil.showToast(getApplicationContext(), getResString("sobot_no_write_external_storage_permission"));
				}
				break;
			case ZhiChiConstant.SOBOT_RECORD_AUDIO_REQUEST_CODE:
				if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
					has_record_audio_permission = true;
				}else{
					has_record_audio_permission = false;
					ToastUtil.showToast(getApplicationContext(), getResString("sobot_no_record_audio_permission"));
				}
				break;
		}
	}

	// ##################### 更新界面的ui ###############################

	/**
	 * handler 消息实体message 更新ui界面
	 * 
	 * @param messageAdapter
	 * @param msg
	 */
	public void updateUiMessage(SobotMsgAdapter messageAdapter, Message msg) {
		ZhiChiMessageBase myMessage = (ZhiChiMessageBase) msg.obj;
		updateUiMessage(messageAdapter,myMessage);
	}

	public void updateTextMessageStatus(SobotMsgAdapter messageAdapter,
			Message msg) {
		ZhiChiMessageBase myMessage = (ZhiChiMessageBase) msg.obj;
		messageAdapter.updateVoiceStatusById(myMessage.getId(),
				myMessage.getSendSuccessState(),"");
		messageAdapter.notifyDataSetChanged();
	}

	public void updateVoiceStatusMessage(SobotMsgAdapter messageAdapter,
			Message msg) {
		ZhiChiMessageBase myMessage = (ZhiChiMessageBase) msg.obj;
		messageAdapter.updateVoiceStatusById(myMessage.getId(),
				myMessage.getSendSuccessState(),myMessage.getAnswer().getDuration());
		messageAdapter.notifyDataSetChanged();
	}

	public void cancelUiVoiceMessage(SobotMsgAdapter messageAdapter, Message msg){
		ZhiChiMessageBase myMessage = (ZhiChiMessageBase) msg.obj;
		messageAdapter.cancelVoiceUiById(myMessage.getId());
		messageAdapter.notifyDataSetChanged();
	}

	/**
	 * 通过消息实体 zhiChiMessage进行封装
	 * 
	 * @param messageAdapter
	 * @param zhichiMessage
	 */
	public void updateUiMessage(SobotMsgAdapter messageAdapter,
			ZhiChiMessageBase zhichiMessage) {
		messageAdapter.addData(zhichiMessage);
		messageAdapter.notifyDataSetChanged();
	}

	/**
	 * 通过消息实体 zhiChiMessage进行封装
	 * 
	 * @param messageAdapter
	 * @param zhichiMessage
	 */
	public void updateUiMessageBefore(SobotMsgAdapter messageAdapter,
			ZhiChiMessageBase zhichiMessage) {
		messageAdapter.addDataBefore(zhichiMessage);
		messageAdapter.notifyDataSetChanged();
	}

	/**
	 * 
	 * @param messageAdapter
	 * @param id
	 * @param status
	 * @param progressBar
	 */
	public void updateUiMessageStatus(SobotMsgAdapter messageAdapter,
			String id, int status, int progressBar) {
		messageAdapter.updateMsgInfoById(id, status, progressBar);
		messageAdapter.notifyDataSetChanged();
	}

	// ################### 发送 消息 通知handler #######################

	/**
	 * 文本通知
	 * @param id
	 * @param msgContent
	 * @param handler
	 * @param isSendStatus 0 失败  1成功  2 正在发送
     * @param isUpdate
     */
	public void sendTextMessageToHandler(String id, String msgContent,
			Handler handler, int isSendStatus, boolean isUpdate) {
		ZhiChiMessageBase myMessage = new ZhiChiMessageBase();
		myMessage.setId(id);
		ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
		if(!TextUtils.isEmpty(msgContent)){
			msgContent = msgContent.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace
					("\n","<br/>").replace("&lt;br/&gt;","<br/>");
			reply.setMsg(msgContent);
		}else{
			reply.setMsg(msgContent);
		}
		reply.setMsgType(ZhiChiConstant.message_type_text + "");
		myMessage.setAnswer(reply);
		myMessage.setSenderType(ZhiChiConstant.message_sender_type_customer + "");
		myMessage.setSendSuccessState(isSendStatus);
		Message handMyMessage = handler.obtainMessage();
		if (!isUpdate) {// 显示发送成功的状态
			handMyMessage.what = ZhiChiConstant.hander_my_senderMessage;

		} else {// 发送失败的状态
			handMyMessage.what = ZhiChiConstant.hander_my_update_senderMessageStatus;
		}

		handMyMessage.obj = myMessage;
		handler.sendMessage(handMyMessage);
	}

	// 发送语音消息
	/**
	 * 
	 * @param voiceMsgId
	 *            语音暂时产生唯一标识符
	 * @param voiceUrl
	 *            语音的地址
	 * @param voiceTimeLongStr
	 *            语音的时长
	 * @param isSendSuccess
	 * @param  state 发送状态
	 * @param handler
	 */
	public void sendVoiceMessageToHandler(String voiceMsgId, String voiceUrl,
			String voiceTimeLongStr, int isSendSuccess, int state,
			final Handler handler) {

		ZhiChiMessageBase zhichiMessage = new ZhiChiMessageBase();
		ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
		reply.setMsg(voiceUrl);
		reply.setDuration(voiceTimeLongStr);
		zhichiMessage.setAnswer(reply);
		zhichiMessage.setSenderType(ZhiChiConstant.message_sender_type_send_voice + "");
		zhichiMessage.setId(voiceMsgId);
		zhichiMessage.setSendSuccessState(isSendSuccess);
		// 设置语音的时长的操作

		Message message = handler.obtainMessage();
		if (state==UPDATE_VOICE) {// 更新界面布局
			message.what = ZhiChiConstant.message_type_update_voice;
		} else if(state==CANCEL_VOICE){
			message.what = ZhiChiConstant.message_type_cancel_voice;
		}else if(state==SEND_VOICE){
			message.what = ZhiChiConstant.message_type_send_voice;
		}

		message.obj = zhichiMessage;
		handler.sendMessage(message);
	}

	public void remindRobotMessage(final Handler handler) {
		// 修改提醒的信息
		remindRobotMessageTimes = remindRobotMessageTimes + 1;
		if (remindRobotMessageTimes == 1) {
			/* 首次的欢迎语 */
			ZhiChiMessageBase robot = new ZhiChiMessageBase();
			ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
			String msgHint = initModel.getRobotHelloWord().replace("\n", "<br/>");
			if (msgHint.startsWith("<br/>")) {
				msgHint = msgHint.substring(5, msgHint.length());
			}

			if (msgHint.endsWith("<br/>")) {
				msgHint = msgHint.substring(0, msgHint.length() - 5);
			}
			reply.setMsg(msgHint);
			reply.setMsgType(ZhiChiConstant.message_type_text + "");
			robot.setAnswer(reply);
			robot.setSenderFace(initModel.getRobotLogo());
			robot.setSender(initModel.getRobotName());
			robot.setSenderType(ZhiChiConstant.message_sender_type_robot + "");
			robot.setSenderName(initModel.getRobotName());
			Message message = handler.obtainMessage();
			message.what = ZhiChiConstant.hander_robot_message;
			message.obj = robot;
			handler.sendMessage(message);

			//获取机器人带引导与的欢迎语
			if (1== initModel.getGuideFlag()){

				zhiChiApi.robotGuide(initModel.getUid(), initModel.getCurrentRobotFlag(), new
						StringResultCallBack<ZhiChiMessageBase>() {
					@Override
					public void onSuccess(ZhiChiMessageBase robot) {
						if (current_client_model == ZhiChiConstant.client_model_robot){
							robot.setSenderFace(initModel.getRobotLogo());
							robot.setSenderType(ZhiChiConstant.message_sender_type_robot_guide + "");
							Message message = handler.obtainMessage();
							message.what = ZhiChiConstant.hander_robot_message;
							message.obj = robot;
							handler.sendMessage(message);
						}
					}

					@Override
					public void onFailure(Exception e, String des) { }
				});
			}
		}
	}

	/**
	 * 
	 * @param context
	 * @param initModel
	 * @param handler
	 * @param current_client_model
	 */
	public void sendMessageWithLogic(String msgId, String context,
			ZhiChiInitModeBase initModel, final Handler handler, int current_client_model,int questionFlag, String question) {
		if (ZhiChiConstant.client_model_robot == current_client_model) { // 客户和机械人进行聊天
			sendHttpRobotMessage(msgId, context, initModel.getUid(),
					initModel.getCid(), handler ,questionFlag , question);
			LogUtils.i("发送消息：(机器人模式)" + "content：" + context);
		} else if (ZhiChiConstant.client_model_customService == current_client_model) {
			sendHttpCustomServiceMessage(context, initModel.getUid(),
					initModel.getCid(), handler, msgId);
			LogUtils.i("发送消息：(客服模式)" + "uid:" + initModel.getUid()
					+ "---cid:" + initModel.getCid() + "---content:" + context);
		}
	}

	// 人与机械人进行聊天
	public void sendHttpRobotMessage(final String msgId, String requestText,
			String uid, String cid, final Handler handler,int questionFlag,String question) {
		zhiChiApi.chatSendMsgToRoot(initModel.getCurrentRobotFlag(),requestText, questionFlag,question, uid, cid,
				new StringResultCallBack<ZhiChiMessageBase>() {
					@Override
					public void onSuccess(ZhiChiMessageBase simpleMessage) {
						// 机械人的回答语
						sendTextMessageToHandler(msgId, null, handler, 1, true);
						String id = System.currentTimeMillis() + "";
						if (simpleMessage.getUstatus() == ZhiChiConstant.result_fail_code){
							//机器人超时下线
							customerServiceOffline(initModel,4);
						} else {
							isAboveZero = true;
							simpleMessage.setId(id);
							simpleMessage.setSenderName(initModel.getRobotName());
							simpleMessage.setSender(initModel.getRobotName());
							simpleMessage.setSenderFace(initModel.getRobotLogo());
							simpleMessage.setSenderType(ZhiChiConstant.message_sender_type_robot + "");
							Message message = handler.obtainMessage();
							message.what = ZhiChiConstant.hander_robot_message;
							message.obj = simpleMessage;
							handler.sendMessage(message);
						}
					}

					@Override
					public void onFailure(Exception e, String des) {
						LogUtils.i("text:" + des);
						// 显示信息发送失败
						sendTextMessageToHandler(msgId, null, handler, 0, true);
					}
				});
	}

	public void sendHttpCustomServiceMessage(final String content, final String uid,
											 String cid, final Handler handler, final String mid) {
		zhiChiApi.sendMsgToCoutom(content, uid, cid, new StringResultCallBack<CommonModelBase>() {
			@Override
			public void onSuccess(CommonModelBase commonModelBase) {
				if (ZhiChiConstant.client_sendmsg_to_custom_fali.equals(commonModelBase.getStatus())) {
					sendTextMessageToHandler(mid, null, handler, 0, true);
					customerServiceOffline(initModel,1);
				} else if(ZhiChiConstant.client_sendmsg_to_custom_success.equals(commonModelBase.getStatus())){
					if (!TextUtils.isEmpty(mid)) {
						if(!SobotMsgManager.getInstance(getApplicationContext()).isConnect()){
							zhiChiApi.reconnectChannel();
						}
						isAboveZero = true;
						// 当发送成功的时候更新ui界面
						sendTextMessageToHandler(mid, null, handler, 1, true);
					}
				}
			}

			@Override
			public void onFailure(Exception e, String des) {
				LogUtils.i("error:" + e.toString());
				Map<String,String> map = new HashMap<>();
				map.put("content","消息发送失败：---content:"+content+"    err:" + e.toString());
				map.put("title","sendMsg failure");
				map.put("uid",uid);
				LogUtils.i2Local(map);
				sendTextMessageToHandler(mid, null, handler, 0, true);
			}
		});
	}

	/**
	 * 发送语音消息
	 * 
	 * @param voiceMsgId
	 * @param voiceTimeLongStr
	 * @param cid
	 * @param uid
	 * @param filePath
	 * @param handler
	 */
	public void sendVoice(final String voiceMsgId, final String voiceTimeLongStr,
			String cid, String uid, final String filePath, final Handler handler) {
		LogUtils.i("sobot---" + filePath);
		zhiChiApi.sendFile(cid, uid, filePath, voiceTimeLongStr,
				new ResultCallBack<ZhiChiMessage>() {
					@Override
					public void onSuccess(ZhiChiMessage zhiChiMessage) {
						// 语音发送成功
						isAboveZero = true;
						restartMyTimeTask(handler);
						sendVoiceMessageToHandler(voiceMsgId, filePath, voiceTimeLongStr, 1, UPDATE_VOICE, handler);
					}

					@Override
					public void onFailure(Exception e, String des) {
						LogUtils.i("发送语音error:" + des + "exception:" + e);
						sendVoiceMessageToHandler(voiceMsgId, filePath, voiceTimeLongStr, 0, UPDATE_VOICE, handler);
					}

					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {

					}
				});
	}

	/**
	 * 用户的定时任务的处理
	 */
	public void startUserInfoTimeTask(final Handler handler) {
		if (current_client_model == ZhiChiConstant.client_model_customService) {
			stopUserInfoTimeTask();
			userInfoTimeTask=true;
			timerUserInfo = new Timer();
			taskUserInfo = new TimerTask() {
				@Override
				public void run() {
					// 需要做的事:发送消息
					sendHandlerUserInfoTimeTaskMessage(handler);
				}
			};
			timerUserInfo.schedule(taskUserInfo, 1000, 1000);
		}
	}

	public void stopUserInfoTimeTask() {
		userInfoTimeTask=false;
		if (timerUserInfo != null) {
			timerUserInfo.cancel();
			timerUserInfo = null;
		}
		if (taskUserInfo != null) {
			taskUserInfo.cancel();
			taskUserInfo = null;
		}
		noReplyTimeUserInfo = 0;
	}

	// ################# 处理定时任务 开始 #####################

	/**
	 * 客服的定时处理
	 */
	public void startCustomTimeTask(final Handler handler) {
		if (current_client_model == ZhiChiConstant.client_model_customService) {
			if (!is_startCustomTimerTask) {
				stopCustomTimeTask();
				customTimeTask=true;
				is_startCustomTimerTask = true;
				timerCustom = new Timer();
				taskCustom = new TimerTask() {
					@Override
					public void run() {
						// 需要做的事:发送消息
						sendHandlerCustomTimeTaskMessage(handler);
					}
				};
				timerCustom.schedule(taskCustom, 1000, 1000);
			}
		}
	}

	public void stopCustomTimeTask() {
		customTimeTask=false;
		is_startCustomTimerTask = false;
		if (timerCustom != null) {
			timerCustom.cancel();
			timerCustom = null;
		}
		if (taskCustom != null) {
			taskCustom.cancel();
			taskCustom = null;
		}
		noReplyTimeCustoms = 0;
	}

	/**
	 * 录音的时间控制
	 */
	public void startVoiceTimeTask(final Handler handler) {
		voiceTimerLong = 0;
		stopVoiceTimeTask();
		voiceTimer = new Timer();
		voiceTimerTask = new TimerTask() {
			@Override
			public void run() {
				// 需要做的事:发送消息
				sendVoiceTimeTask(handler);
			}
		};
		// 500ms进行定时任务
		voiceTimer.schedule(voiceTimerTask, 0, 500);

	}

	/**
	 * 发送声音的定时的任务
	 * 
	 * @param handler
	 */
	public void sendVoiceTimeTask(Handler handler) {
		Message message = handler.obtainMessage();
		message.what = ZhiChiConstant.voiceIsRecoding;
		voiceTimerLong = voiceTimerLong + 500;
		message.obj = voiceTimerLong;
		handler.sendMessage(message);
	}

	public void stopVoiceTimeTask() {
		if (voiceTimer != null) {
			voiceTimer.cancel();
			voiceTimer = null;
		}
		if (voiceTimerTask != null) {
			voiceTimerTask.cancel();
			voiceTimerTask = null;
		}
		voiceTimerLong = 0;
	}

	/**
	 * 客服的定时任务处理
	 */
	public void sendHandlerCustomTimeTaskMessage(Handler handler) {
		noReplyTimeCustoms++;
		// 用户和人工进行聊天 超长时间没有发起对话
		// LogUtils.i("  客服 ---的定时任务--监控--："+noReplyTimeCustoms );
		// 妹子忙翻了
		if (initModel != null){
			if (noReplyTimeCustoms == Integer.parseInt(initModel.getAdminTipTime()) * 60) {
				ZhiChiMessageBase result = new ZhiChiMessageBase();
				ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
				customTimeTask=false;
				// 发送我的语音的消息
				result.setSenderName(currentUserName); // 当前的用户
				result.setSenderType(ZhiChiConstant.message_sender_type_service + "");
				String msgHint = initModel.getAdminTipWord().replace("\n", "<br/>");
				if (msgHint.startsWith("<br/>")) {
					msgHint = msgHint.substring(5, msgHint.length());
				}

				if (msgHint.endsWith("<br/>")) {
					msgHint = msgHint.substring(0, msgHint.length() - 5);
				}
				reply.setMsg(msgHint);
				result.setSenderFace(adminFace);
				reply.setMsgType(ZhiChiConstant.message_type_text + "");
				result.setAnswer(reply);
				Message message = handler.obtainMessage();
				message.what = ZhiChiConstant.hander_timeTask_custom_isBusying;
				message.obj = result;
				if (SobotMsgManager.getInstance(getApplicationContext()).isConnect()) {
					// 当有通道连接的时候才提醒
					handler.sendMessage(message);
				}
				LogUtils.i("sobot---sendHandlerCustomTimeTaskMessage" + noReplyTimeCustoms);
			}
		}
	}

	/**
	 * 客户的定时任务处理
	 * 
	 * @param handler
	 */
	private void sendHandlerUserInfoTimeTaskMessage(Handler handler) {
		noReplyTimeUserInfo++;
		// LogUtils.i(" 客户的定时任务--监控--："+noReplyTimeUserInfo );
		// 用户几分钟没有说话
		if (current_client_model == ZhiChiConstant.client_model_customService) {
			if (initModel != null){
				if (noReplyTimeUserInfo == (Integer.parseInt(initModel
						.getUserOutTime()) * 60)) {
					userInfoTimeTask=false;
					// 进行消息的封装
					ZhiChiMessageBase base = new ZhiChiMessageBase();
					// 设置
					base.setSenderType(ZhiChiConstant.message_sender_type_service + "");
					ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
					reply.setMsgType(ZhiChiConstant.message_type_text + "");
					// 根据当前的模式
					base.setSenderName(currentUserName);
					String msgHint = initModel.getUserTipWord().replace("\n", "<br/>");
					if (msgHint.startsWith("<br/>")) {
						msgHint = msgHint.substring(5, msgHint.length());
					}

					if (msgHint.endsWith("<br/>")) {
						msgHint = msgHint.substring(0, msgHint.length() - 5);
					}
					reply.setMsg(msgHint);
					base.setAnswer(reply);
					base.setSenderFace(adminFace);
					if(SobotMsgManager.getInstance(getApplicationContext()).isConnect()){
						//通道连接中才发出自动回复
						Message message = handler.obtainMessage();
						message.what = ZhiChiConstant.hander_timeTask_userInfo;
						message.obj = base;
						handler.sendMessage(message);
					}
				}
			}
		}
	}

	// ################### 处理定时任务 结束 #######################
	/**
	 * 设置定时任务
	 */
	public void setTimeTaskMethod(Handler handler) {
		if (customerState == CustomerState.Online) {
			LogUtils.i(" 定时任务的计时的操作：" + current_client_model);
			// 断开我的计时任务
			if (current_client_model == ZhiChiConstant.client_model_customService) {
				if (!is_startCustomTimerTask) {
					stopUserInfoTimeTask();
					startCustomTimeTask(handler);
				}
			}
		} else {
			stopCustomTimeTask();
			stopUserInfoTimeTask();
		}
	}

	public void restartMyTimeTask(Handler handler) {
		if (customerState == CustomerState.Online) {
			// 断开我的计时任务
			if (current_client_model == ZhiChiConstant.client_model_customService) {
				if (!is_startCustomTimerTask) {
					stopUserInfoTimeTask();
					startCustomTimeTask(handler);
				}
			}
		}
	}

	public int getResId(String name) {
		return ResourceUtils.getIdByName(SobotBaseActivity.this, "id", name);
	}

	public int getResDrawableId(String name) {
		return ResourceUtils.getIdByName(SobotBaseActivity.this, "drawable", name);
	}

	public int getResLayoutId(String name) {
		return ResourceUtils.getIdByName(SobotBaseActivity.this, "layout", name);
	}

	public int getResStringId(String name) {
		return ResourceUtils.getIdByName(SobotBaseActivity.this, "string", name);
	}

	public String getResString(String name){
		return getResources().getString(getResStringId(name));
	}

	/**
	 * 通过照相上传图片
	 */
	public void selectPicFromCamera() {
		if (!CommonUtils.isExitsSdcard()) {
			Toast.makeText(getApplicationContext(), getResString("sobot_sdcard_does_not_exist"),
					Toast.LENGTH_SHORT).show();
			return;
		}

		has_camera_permission= CommonUtils.checkPermission(this, Manifest.permission.CAMERA,ZhiChiConstant.SOBOT_CAMERA_REQUEST_CODE);
		has_write_external_storage_permission=CommonUtils.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, ZhiChiConstant.SOBOT_WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
		if(!has_camera_permission || !has_write_external_storage_permission){
			return;
		}
		cameraFile = ChatUtils.openCamera(this);
	}

	/**
	 * 从图库获取图片
	 */
	public void selectPicFromLocal() {
		if(Build.VERSION.SDK_INT  >= 23){
			has_write_external_storage_permission=CommonUtils.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, ZhiChiConstant.SOBOT_WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
			if(!has_write_external_storage_permission){
				return;
			}
		}
		ChatUtils.openSelectPic(this);
	}

	/**
	 * 由子类实现
	 * @param initModel
	 * @param outLineType
     */
	public void customerServiceOffline(ZhiChiInitModeBase initModel, int outLineType) {}

}