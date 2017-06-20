package com.sobot.chat.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sobot.chat.SobotApi;
import com.sobot.chat.activity.base.SobotBaseActivity;
import com.sobot.chat.adapter.base.SobotMsgAdapter;
import com.sobot.chat.api.apiUtils.GsonUtil;
import com.sobot.chat.api.apiUtils.ZhiChiConstants;
import com.sobot.chat.api.enumtype.CustomerState;
import com.sobot.chat.api.model.CommonModel;
import com.sobot.chat.api.model.CommonModelBase;
import com.sobot.chat.api.model.ConsultingContent;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.api.model.ZhiChiCidsModel;
import com.sobot.chat.api.model.ZhiChiGroup;
import com.sobot.chat.api.model.ZhiChiGroupBase;
import com.sobot.chat.api.model.ZhiChiHistoryMessage;
import com.sobot.chat.api.model.ZhiChiHistoryMessageBase;
import com.sobot.chat.api.model.ZhiChiInitModeBase;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.api.model.ZhiChiPushMessage;
import com.sobot.chat.api.model.ZhiChiReplyAnswer;
import com.sobot.chat.core.channel.Const;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.core.http.callback.StringResultCallBack;
import com.sobot.chat.listener.NoDoubleClickListener;
import com.sobot.chat.server.SobotSessionServer;
import com.sobot.chat.utils.AnimationUtil;
import com.sobot.chat.utils.AudioTools;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.ExtAudioRecorder;
import com.sobot.chat.utils.IntenetUtil;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.NotificationUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.TimeTools;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConfig;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.viewHolder.RichTextMessageHolder;
import com.sobot.chat.viewHolder.VoiceMessageHolder;
import com.sobot.chat.voice.AudioPlayCallBack;
import com.sobot.chat.voice.AudioPlayPresenter;
import com.sobot.chat.widget.ClearHistoryDialog;
import com.sobot.chat.widget.ContainsEmojiEditText;
import com.sobot.chat.widget.DropdownListView;
import com.sobot.chat.widget.emoji.DisplayRules;
import com.sobot.chat.widget.emoji.Emojicon;
import com.sobot.chat.widget.emoji.InputHelper;
import com.sobot.chat.widget.gif.GifView;
import com.sobot.chat.widget.gif.GifView.GifImageType;
import com.sobot.chat.widget.kpswitch.CustomeChattingPanel;
import com.sobot.chat.widget.kpswitch.util.KPSwitchConflictUtil;
import com.sobot.chat.widget.kpswitch.util.KeyboardUtil;
import com.sobot.chat.widget.kpswitch.view.CustomeViewFactory;
import com.sobot.chat.widget.kpswitch.widget.KPSwitchPanelLinearLayout;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

@SuppressLint({ "HandlerLeak", "ShowToast", "SimpleDateFormat", "SdCardPath",
		"NewApi", "SetJavaScriptEnabled" })
public class SobotChatActivity extends SobotBaseActivity implements
		DropdownListView.OnRefreshListenerHeader, SensorEventListener {

	private boolean isSessionOver = false;//表示此会话是否结束
	private Information info;/*用户传入的实体对象*/
	private TextView textReConnect;
	private GifView loading_anim_view;
	private TextView txt_loading;
	private ImageView icon_nonet;
	private Button btn_reconnect;

	private RelativeLayout chat_main; // 聊天主窗口;
	private FrameLayout welcome; // 欢迎窗口;
	private int queueNum = 0;//排队的人数
	private DropdownListView lv_message;/* 带下拉的ListView */
	private ContainsEmojiEditText et_sendmessage;// 当前用户输入的信息
	private Button btn_send; // 发送消息按钮
	private ImageButton btn_set_mode_rengong; // 转人工button
	private Button btn_upload_view; // 上传图片
	private ImageButton btn_emoticon_view; // 表情面板
	private TextView voice_time_long;/*显示语音时长*/
	private LinearLayout voice_top_image;
	private ImageView image_endVoice;
	private ImageView mic_image;
	private ImageView mic_image_animate; // 图片的动画
	private ImageView recording_timeshort;// 语音太短的图片
	private ImageButton btn_model_edit; // 编辑模式
	private ImageButton btn_model_voice;// 语音模式
	private TextView txt_speak_content; // 发送语音的文字
	private AnimationDrawable animationDrawable;/* 语音的动画 */
	public KPSwitchPanelLinearLayout mPanelRoot; // 聊天下面的面板
	private LinearLayout btn_press_to_speak; // 说话view ;
	private RelativeLayout edittext_layout; // 输入框view;
	private LinearLayout recording_container;// 语音上滑的动画
	private TextView recording_hint;// 上滑的显示文本；
	private boolean isNoMoreHistoryMsg = false;
	private boolean isActivityStart = false;//标识当前Act是否显示
	private boolean isNeedShowEvaluate = false;//标识当前是否需要显示评价窗口
	/* 有没有超过一分钟 *//* 客户有没有说过话 */
	private boolean isComment = false;/* 判断用户是否评价过 */
	public int currentPanelId = 0;//切换聊天面板时 当前点击的按钮id 为了能切换到对应的view上

	// 消息列表展示
	private List<ZhiChiMessageBase> messageList = new ArrayList<ZhiChiMessageBase>();
	private int currentVoiceLong = 0;
	public static final String mVoicePath = ZhiChiConstant.voicePositionPath;

	AudioPlayPresenter mAudioPlayPresenter = null;
	AudioPlayCallBack mAudioPlayCallBack = null;

	private String mFileName = null;
	/* 用于语音播放 */
	@SuppressWarnings("unused")
	private MediaPlayer mPlayer = null;
	/* 用于完成录音 */
	@SuppressWarnings("unused")
	private MediaRecorder mRecorder = null;
	private MyMessageReceiver receiver;

	private ExtAudioRecorder extAudioRecorder;
	private int queueTimes = 0;//收到排队顺序变化提醒的次数

	/* 听筒模式转换 */
	private AudioManager audioManager = null; // 声音管理器
	private SensorManager _sensorManager = null; // 传感器管理器
	private Sensor mProximiny = null; // 传感器实例
	private float f_proximiny; // 当前传感器距离
	private int minRecordTime = 60;// 允许录音时间
	private int recordDownTime = minRecordTime - 10;// 允许录音时间 倒计时
	boolean isCutVoice;
	private String voiceMsgId = "";//  语音消息的Id
	private RelativeLayout sobot_ll_restart_talk; // 开始新会话布局ID
	private ImageView image_reLoading;
	private List<ZhiChiGroupBase> list_group;
	private boolean isCustomPushEvaluate = false;
	private TextView sobot_tv_satisfaction, notReadInfo, sobot_tv_message,
			sobot_txt_restart_talk;
	private LinearLayout sobot_ll_bottom;
	public boolean isFirst = true;// 是否是第一次加载

	private int showTimeVisiableCustomBtn = 0;/*用户设置几次显示转人工按钮*/

	private Timer inputtingListener = null;//用于监听正在输入的计时器
	private boolean isSendInput = false;//防止同时发送正在输入
	private String lastInputStr = "";
	TimerTask inputTimerTask = null;
	private Bundle informationBundle;
	private int mUnreadNum = 0;//未读消息数
	private int logCollectTime = 0;//日志上传次数

	//以下参数为历史记录需要的接口
	private List<String> cids = new ArrayList<>();//cid的列表
	private int currentCidPosition = 0;//当前查询聊天记录所用的cid位置
	private boolean getCidsFinish = false;//表示查询cid的接口是否结束
	private boolean isInGethistory = false;//表示是否正在查询历史记录
	private boolean isConnCustomerService = false;//控制同一时间 只能调一次转人工接口

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(ResourceUtils.getIdByName(this, "layout", "sobot_chat_activity"));
		try {
			LogUtils.setSaveDir(CommonUtils.getPrivatePath(getApplicationContext()));
			initBundleData(savedInstanceState);
			initView();
			initData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initView() {
		//loading 层
		relative.setVisibility(View.GONE);
		if (!TextUtils.isEmpty(info.getColor())) {
			relative.setBackgroundColor(Color.parseColor(info.getColor()));
		}

		if (info != null && info.getTitleImgId() != 0){
			relative.setBackgroundResource(info.getTitleImgId());
		}

		notReadInfo = (TextView) findViewById(getResId("notReadInfo"));
		chat_main = (RelativeLayout) findViewById(getResId("sobot_chat_main"));
		welcome = (FrameLayout) findViewById(getResId("sobot_welcome"));
		txt_loading = (TextView) findViewById(getResId("sobot_txt_loading"));
		textReConnect = (TextView) findViewById(getResId("sobot_textReConnect"));
		loading_anim_view = (GifView) findViewById(getResId("sobot_image_view"));
		loading_anim_view.setGifImageType(GifImageType.COVER);
		loading_anim_view.setGifImage(getResDrawableId("sobot_loding"));
		loading_anim_view.startGifView();
		image_reLoading = (ImageView) findViewById(getResId("sobot_image_reloading"));
		icon_nonet = (ImageView) findViewById(getResId("sobot_icon_nonet"));
		btn_reconnect = (Button) findViewById(getResId("sobot_btn_reconnect"));
		btn_reconnect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				textReConnect.setVisibility(View.GONE);
				icon_nonet.setVisibility(View.GONE);
				btn_reconnect.setVisibility(View.GONE);
				loading_anim_view.setVisibility(View.VISIBLE);
				txt_loading.setVisibility(View.VISIBLE);
				customerInit();
			}
		});

		lv_message = (DropdownListView) findViewById(getResId("sobot_lv_message"));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO){
			lv_message.setOverScrollMode(View.OVER_SCROLL_NEVER);
		}
		et_sendmessage = (ContainsEmojiEditText) findViewById(getResId("sobot_et_sendmessage"));
		et_sendmessage.setVisibility(View.VISIBLE);
		et_sendmessage.setTextColor(Color.parseColor("#000000"));
		btn_send = (Button) findViewById(getResId("sobot_btn_send"));
		btn_set_mode_rengong = (ImageButton) findViewById(getResId("sobot_btn_set_mode_rengong"));
		btn_upload_view = (Button) findViewById(getResId("sobot_btn_upload_view"));
		btn_emoticon_view = (ImageButton) findViewById(getResId("sobot_btn_emoticon_view"));
		btn_model_edit = (ImageButton) findViewById(getResId("sobot_btn_model_edit"));
		btn_model_voice = (ImageButton) findViewById(getResId("sobot_btn_model_voice"));
		mPanelRoot = (KPSwitchPanelLinearLayout) findViewById(getResId("sobot_panel_root"));
		btn_press_to_speak = (LinearLayout) findViewById(getResId("sobot_btn_press_to_speak"));
		edittext_layout = (RelativeLayout) findViewById(getResId("sobot_edittext_layout"));
		recording_hint = (TextView) findViewById(getResId("sobot_recording_hint"));
		recording_container = (LinearLayout) findViewById(getResId("sobot_recording_container"));

		// 开始语音的布局的信息
		voice_top_image = (LinearLayout) findViewById(getResId("sobot_voice_top_image"));
		// 停止语音
		image_endVoice = (ImageView) findViewById(getResId("sobot_image_endVoice"));
		// 动画的效果
		mic_image_animate = (ImageView) findViewById(getResId("sobot_mic_image_animate"));
		// 时长的界面
		voice_time_long = (TextView) findViewById(getResId("sobot_voiceTimeLong"));
		txt_speak_content = (TextView) findViewById(getResId("sobot_txt_speak_content"));
		txt_speak_content.setText(getResString("sobot_press_say"));
		recording_timeshort = (ImageView) findViewById(getResId("sobot_recording_timeshort"));
		mic_image = (ImageView) findViewById(getResId("sobot_mic_image"));

		sobot_ll_restart_talk = (RelativeLayout) findViewById(getResId("sobot_ll_restart_talk"));
		sobot_txt_restart_talk = (TextView) findViewById(getResId("sobot_txt_restart_talk"));
		sobot_tv_message = (TextView) findViewById(getResId("sobot_tv_message"));
		sobot_tv_satisfaction = (TextView) findViewById(getResId("sobot_tv_satisfaction"));
		sobot_ll_bottom = (LinearLayout) findViewById(getResId("sobot_ll_bottom"));
		//监听聊天的面板
		KeyboardUtil.attach(this, mPanelRoot,
				new KeyboardUtil.OnKeyboardShowingListener() {
					@Override
					public void onKeyboardShowing(boolean isShowing) {
						resetEmoticonBtn();
						if (isShowing){
							lv_message.setSelection(messageAdapter.getCount());
						}
					}
				});
		showRightView(getResDrawableId("sobot_delete_hismsg_selector"),"",true);
	}

	private void initData() {
		initBrocastReceiver();
		initListener();
		setupListView();
		initAudioManager();
		loadUnreadNum();
		initSdk(false);
		startService(new Intent(this, SobotSessionServer.class));
	}

	@Override
	protected void onResume() {
		super.onResume();
		_sensorManager.registerListener(this, mProximiny, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 取消注册传感器
		_sensorManager.unregisterListener(this);
	}

	@Override
	protected void onDestroy() {
		if (initModel != null){
			if (!isSessionOver){
				//保存会话信息
				saveCache();
			}else{
				//清除会话信息
				clearCache();
			}
			SharedPreferencesUtil.removeKey(getApplicationContext(),"sobot_unread_count");
		}
		if (loading_anim_view != null){
			loading_anim_view.stopGifView();
		}
		hideReLoading();
		// 取消广播接受者
		unregisterReceiver(receiver);
		// 停止用户的定时任务
		stopUserInfoTimeTask();
		// 停止客服的定时任务
		stopCustomTimeTask();
		AudioTools.getInstance().stop();
		super.onDestroy();
	}

	/**
	 * 重置内存中保存的数据
	 */
	private void clearCache() {
		SobotMsgManager.getInstance(getApplication()).getConfig().clearCache();
	}

	//保存当前的数据，进行会话保持
	private void saveCache() {
		//清除“以下为未读消息”
		deleteUnReadUi();
		ZhiChiConfig config = SobotMsgManager.getInstance(getApplication()).getConfig();
		config.isShowUnreadUi = true;
		config.setMessageList(messageList);
		config.setInitModel(initModel);
		config.current_client_model = current_client_model;
		config.cids = cids;
		config.currentCidPosition = currentCidPosition;
		config.getCidsFinish = getCidsFinish;
		config.activityTitle = getActivityTitle();
		config.customerState = customerState;
		config.remindRobotMessageTimes=remindRobotMessageTimes;
		config.isAboveZero=isAboveZero;
		config.isComment=isComment;
		config.adminFace=getAdminFace();
		config.customTimeTask=customTimeTask;
		config.userInfoTimeTask=userInfoTimeTask;
		config.currentUserName=currentUserName;
		config.isNoMoreHistoryMsg=isNoMoreHistoryMsg;
		config.showTimeVisiableCustomBtn = showTimeVisiableCustomBtn;
		config.queueNum = queueNum;

	}

	@SuppressWarnings("deprecation")
	private void initBundleData(Bundle savedInstanceState) {
		if(savedInstanceState == null ){
			informationBundle = getIntent().getBundleExtra("informationBundle");
		}else{
			informationBundle = savedInstanceState.getBundle("informationBundle");
		}
		if(informationBundle != null){
			info = (Information) informationBundle.getSerializable("info");
		}

		if(info == null){
			ToastUtil.showToast(getApplicationContext(),getResString("sobot_init_data_is_null"));
			finish();
			return;
		}

		if (TextUtils.isEmpty(info.getAppkey())) {
			Toast.makeText(SobotChatActivity.this, getResString("sobot_appkey_is_null"), Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		//保存自定义配置
		ChatUtils.saveOptionSet(getApplicationContext(),info);
		//设置导航栏返回按钮
		showLeftView(getResString("sobot_back"),getResDrawableId("sobot_btn_back_selector"));
	}

	/* 初始化广播接受者 */
	private void initBrocastReceiver() {
		if (receiver == null) {
			receiver = new MyMessageReceiver();
		}
		// 创建过滤器，并指定action，使之用于接收同action的广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); // 检测网络的状态
		filter.addAction(ZhiChiConstants.receiveMessageBrocast);
		filter.addAction(ZhiChiConstants.chat_remind_post_msg);
		filter.addAction(ZhiChiConstants.sobot_click_cancle);
		filter.addAction(ZhiChiConstants.dcrc_comment_state);/* 人工客服评论成功 */
		filter.addAction(ZhiChiConstants.sobot_close_now);/* 立即结束 */
		filter.addAction(ZhiChiConstants.sobot_close_now_clear_cache);// 立即结束不留缓存
		filter.addAction(ZhiChiConstants.SOBOT_CHANNEL_STATUS_CHANGE);/* 接收通道状态变化 */
		// 注册广播接收器
		registerReceiver(receiver, filter);
	}

	private void initListener() {
		notReadInfo.setOnClickListener(this);
		sobot_tv_left.setOnClickListener(this);
		btn_send.setOnClickListener(this);
		btn_upload_view.setOnClickListener(this);
		btn_emoticon_view.setOnClickListener(this);
		btn_model_edit.setOnClickListener(this);
		btn_model_voice.setOnClickListener(this);
		btn_set_mode_rengong.setOnClickListener(new NoDoubleClickListener() {
			@Override
			public void onNoDoubleClick(View v) {
				doClickTransferBtn();
			}
		});

		lv_message.setDropdownListScrollListener(new DropdownListView.DropdownListScrollListener() {
			@Override
			public void onScroll(AbsListView arg0, int firstVisiableItem, int arg2, int arg3) {
				if(notReadInfo.getVisibility() == View.VISIBLE){
					if (messageList.get(firstVisiableItem) != null && messageList.get(firstVisiableItem).getAnswer() != null
							&& ZhiChiConstant.sobot_remind_type_below_unread == messageList.get(firstVisiableItem).getAnswer().getRemindType()){
							notReadInfo.setVisibility(View.GONE);
					}
				}
			}
		});

		et_sendmessage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				resetBtnUploadAndSend();
			}
		});

		et_sendmessage.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean isFocused) {
				if (isFocused) {
					int length = et_sendmessage.getText().toString().trim().length();
					if (length != 0) {
						btn_send.setVisibility(View.VISIBLE);
						btn_upload_view.setVisibility(View.GONE);
					}
					//根据是否有焦点切换实际的背景
					edittext_layout.setBackgroundResource(getResDrawableId("sobot_chatting_bottom_bg_focus"));
				}else{
					edittext_layout.setBackgroundResource(getResDrawableId("sobot_chatting_bottom_bg_blur"));
				}
			}
		});

		et_sendmessage.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				resetBtnUploadAndSend();
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

			@Override
			public void afterTextChanged(Editable arg0) { }
		});

		btn_press_to_speak.setOnTouchListener(new PressToSpeakListen());
		lv_message.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
					hidePanelAndKeyboard(mPanelRoot);
				}
				return false;
			}
		});

		// 开始新会话
		sobot_txt_restart_talk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				initSdk(true);
			}
		});

		sobot_tv_message.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startToPostMsgActivty(false);
			}
		});

		sobot_tv_satisfaction.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				submitEvaluation();
			}
		});
	}

	/**
	 * 点击了转人工按钮
	 */
	public void doClickTransferBtn() {
		//转人工按钮
		hidePanelAndKeyboard(mPanelRoot);
		doEmoticonBtn2Blur();
		transfer2Custom();
	}

	/**
	 * 满意度评价
	 * 首先判断是否评价过 评价过 弹您已完成提示 未评价 判断是否达到可评价标准
	 */
	public void submitEvaluation(){
		if (isComment) {
			showHint(getResString("sobot_completed_the_evaluation"));
		} else {
			if (isUserBlack()){
				showHint(getResString("sobot_unable_to_evaluate"));
			} else if (isAboveZero) {
				ChatUtils.showEvaluateDialog(SobotChatActivity.this,false,initModel,current_client_model,1);
			} else {
				showHint(getResString("sobot_after_consultation_to_evaluate_custome_service"));
			}
		}
	}

	//切换键盘和面板的方法
	public void switchPanelAndKeyboard(final View panelLayout,final View switchPanelKeyboardBtn, final View focusView) {
		if(currentPanelId == 0 || currentPanelId == switchPanelKeyboardBtn.getId()){
			//没选中的时候或者  点击是自身的时候正常切换面板和键盘
			boolean switchToPanel = panelLayout.getVisibility() != View.VISIBLE;
			if (!switchToPanel) {
				KPSwitchConflictUtil.showKeyboard(panelLayout, focusView);
			} else {
				KPSwitchConflictUtil.showPanel(panelLayout);
				setPanelView(panelLayout, switchPanelKeyboardBtn.getId());
			}
		} else {
			//之前选过  但是现在点击的不是自己的时候  显示自己的面板
			KPSwitchConflictUtil.showPanel(panelLayout);
			setPanelView(panelLayout, switchPanelKeyboardBtn.getId());
		}
		currentPanelId = switchPanelKeyboardBtn.getId();
	}

	/*
	 * 切换键盘和面板的方法   考虑了当键盘为按住说话时的情况 一般都用这个就行
	 * 参数是按下的那个按钮
	 */
	public void pressSpeakSwitchPanelAndKeyboard(final View switchPanelKeyboardBtn){
		if (btn_press_to_speak.isShown()) {
			btn_model_edit.setVisibility(View.GONE);
			btn_model_voice.setVisibility(info.isUseVoice()?View.VISIBLE:View.GONE);
			btn_press_to_speak.setVisibility(View.GONE);
			edittext_layout.setVisibility(View.VISIBLE);

			et_sendmessage.requestFocus();
			KPSwitchConflictUtil.showPanel(mPanelRoot);
			setPanelView(mPanelRoot, switchPanelKeyboardBtn.getId());
			currentPanelId = switchPanelKeyboardBtn.getId();
		}else{
			//切换更多方法的面板
			switchPanelAndKeyboard(mPanelRoot, switchPanelKeyboardBtn, et_sendmessage);
		}
	}

	/**
	 * 设置聊天面板的view
	 * @param panelLayout
	 * @param btnId
	 */
	private void setPanelView(final View panelLayout,int btnId){
		if(panelLayout instanceof KPSwitchPanelLinearLayout){
			KPSwitchPanelLinearLayout tmpView = (KPSwitchPanelLinearLayout) panelLayout;
			View childView = tmpView.getChildAt(0);
			if(childView != null && childView instanceof CustomeChattingPanel){
				CustomeChattingPanel customeChattingPanel = (CustomeChattingPanel) childView;
				Bundle bundle = new Bundle();
				bundle.putInt("current_client_model",current_client_model);
				customeChattingPanel.setupView(this,btnId,bundle);
			}
		}
	}

	/**
	 * 获取当前显示的聊天面板的tag
	 * @param panelLayout
	 */
	private String getPanelViewTag(final View panelLayout){
		String str = "";
		if(panelLayout instanceof KPSwitchPanelLinearLayout){
			KPSwitchPanelLinearLayout tmpView = (KPSwitchPanelLinearLayout) panelLayout;
			View childView = tmpView.getChildAt(0);
			if(childView != null && childView instanceof CustomeChattingPanel){
				CustomeChattingPanel customeChattingPanel = (CustomeChattingPanel) childView;
				str = customeChattingPanel.getPanelViewTag();
			}
		}
		return  str;
	}

	/**
	 * 隐藏键盘和面板
	 * @param layout
	 */
	public void hidePanelAndKeyboard(KPSwitchPanelLinearLayout layout){
		KPSwitchConflictUtil.hidePanelAndKeyboard(layout);
		doEmoticonBtn2Blur();
		currentPanelId = 0;
	}

	/*
	 * 弹出提示
	 */
	private void showHint(String content) {
		ZhiChiMessageBase zhichiMessageBase = new ZhiChiMessageBase();
		ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
		zhichiMessageBase.setSenderType(ZhiChiConstant.message_sender_type_remide_info + "");
		reply.setMsg(content);
		reply.setRemindType(ZhiChiConstant.sobot_remind_type_evaluate);
		zhichiMessageBase.setAnswer(reply);
		zhichiMessageBase.setAction(ZhiChiConstant.action_remind_no_service);
		updateUiMessage(messageAdapter,zhichiMessageBase);
	}

	/**
	 * 发出离线提醒
	 * @param initModel
	 * @param outLineType 下线类型
     */
	private void showOutlineTip(ZhiChiInitModeBase initModel,int outLineType){
		ZhiChiMessageBase base = new ZhiChiMessageBase();
		ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
		base.setSenderType(ZhiChiConstant.message_sender_type_remide_info + "");
		reply.setMsg(ChatUtils.getMessageContentByOutLineType(getApplicationContext(), initModel, outLineType));
		reply.setRemindType(ZhiChiConstant.sobot_remind_type_outline);
		base.setAnswer(reply);
		if (1 == outLineType) {
			base.setAction(ZhiChiConstant.sobot_outline_leverByManager);
		} else if (2 == outLineType) {
			base.setAction(ZhiChiConstant.sobot_outline_leverByManager);
		} else if (3 == outLineType) {
			base.setAction(ZhiChiConstant.sobot_outline_leverByManager);
			if(initModel != null){
				initModel.setIsblack("1");
			}
		} else if (4 == outLineType) {
			base.setAction(ZhiChiConstant.action_remind_past_time);
		} else if (6 == outLineType) {
			base.setAction(ZhiChiConstant.sobot_outline_leverByManager);
		}
		// 提示会话结束
		updateUiMessage(messageAdapter, base);
	}

	private void setupListView() {
		messageAdapter = new SobotMsgAdapter(SobotChatActivity.this, messageList);
		lv_message.setAdapter(messageAdapter);
		lv_message.setPullRefreshEnable(true);// 设置下拉刷新列表
		lv_message.setOnRefreshListenerHead(this);
	}

	// 设置听筒模式或者是正常模式的转换
	private void initAudioManager() {
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		_sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mProximiny = _sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
	}

	/**
	 * 调用初始化接口
	 */
	private void customerInit() {

		if (info.getInitModeType() == ZhiChiConstant.type_robot_only){
			ChatUtils.userLogout(getApplicationContext());
		}

		zhiChiApi.sobotInit(info, new StringResultCallBack<ZhiChiInitModeBase>() {
					@Override
					public void onSuccess(ZhiChiInitModeBase result) {
						initModel = result;
						if(info.getInitModeType() > 0){
							initModel.setType(info.getInitModeType()+"");
						}
						type = Integer.parseInt(initModel.getType());
						SharedPreferencesUtil.saveIntData(getApplicationContext(),
								ZhiChiConstant.initType, type);
						//初始化查询cid
						queryCids();
						//拉取历史纪录
						getHistoryMessage(true);
						//设置初始layout,无论什么模式都是从机器人的UI变过去的
						showRobotLayout();

						if (!TextUtils.isEmpty(initModel.getUid())){
							SharedPreferencesUtil.saveStringData(getApplicationContext(), Const.SOBOT_CID,initModel.getUid());
						}
						SharedPreferencesUtil.saveIntData(getApplicationContext(),
								ZhiChiConstant.sobot_msg_flag, initModel.getMsgFlag());
						SharedPreferencesUtil.saveStringData(getApplicationContext(),
								"lastCid",initModel.getCid());
						SharedPreferencesUtil.saveStringData(getApplicationContext(),
								ZhiChiConstant.sobot_last_current_partnerId,info.getUid());
						SharedPreferencesUtil.saveStringData(getApplicationContext(),
								ZhiChiConstant.sobot_last_current_appkey,info.getAppkey());

                        SharedPreferencesUtil.saveStringData(getApplicationContext(),ZhiChiConstant.SOBOT_RECEPTIONISTID, TextUtils.isEmpty(info.getReceptionistId())?"":info.getReceptionistId());
                        SharedPreferencesUtil.saveStringData(getApplicationContext(),ZhiChiConstant.SOBOT_ROBOT_CODE, TextUtils.isEmpty(info.getRobotCode())?"":info.getRobotCode());
						SharedPreferencesUtil.saveBooleanData(getApplicationContext(),ZhiChiConstant.SOBOT_POSTMSG_TELSHOWFLAG,initModel.isTelShowFlag());
						SharedPreferencesUtil.saveBooleanData(getApplicationContext(),ZhiChiConstant.SOBOT_POSTMSG_TELFLAG,initModel.isTelFlag());
						SharedPreferencesUtil.saveBooleanData(getApplicationContext(),ZhiChiConstant.SOBOT_POSTMSG_EMAILFLAG,initModel.isEmailFlag());
						SharedPreferencesUtil.saveBooleanData(getApplicationContext(),ZhiChiConstant.SOBOT_POSTMSG_EMAILSHOWFLAG,initModel.isEmailShowFlag());
						SharedPreferencesUtil.saveBooleanData(getApplicationContext(),ZhiChiConstant.SOBOT_POSTMSG_ENCLOSURESHOWFLAG,initModel.isEnclosureShowFlag());
						SharedPreferencesUtil.saveBooleanData(getApplicationContext(),ZhiChiConstant.SOBOT_POSTMSG_ENCLOSUREFLAG,initModel.isEnclosureFlag());
						SharedPreferencesUtil.saveStringData(getApplicationContext(),ZhiChiConstant.SOBOT_POSTMSG_TICKETSTARTWAY,initModel.getTicketStartWay());

						initModel.setColor(info.getColor());

						if (type == ZhiChiConstant.type_robot_only){
							remindRobotMessage(handler);
						} else if (type == ZhiChiConstant.type_robot_first) {
							//机器人优先
							if(initModel.getUstatus() == ZhiChiConstant.ustatus_online || initModel.getUstatus() == ZhiChiConstant.ustatus_queue){
								//机器人优先 时需要判断  是否需要保持会话
								if(initModel.getUstatus() == ZhiChiConstant.ustatus_queue){
									remindRobotMessage(handler);
								}
								//机器人会话保持
								connectCustomerService("","");
							}else{
								//仅机器人或者机器人优先，不需要保持会话
								remindRobotMessage(handler);
							}
						} else {
							if (type == ZhiChiConstant.type_custom_only) {
								//仅人工客服
								if (isUserBlack()) {
									showLeaveMsg();
								} else {
									transfer2Custom();
								}
							} else if (type == ZhiChiConstant.type_custom_first) {
								//客服优先
								transfer2Custom();
							}
						}
					}

					@Override
					public void onFailure(Exception e, String des) {
                        if(e instanceof IllegalArgumentException){
                            if(LogUtils.isDebug){
								ToastUtil.showLongToast(getApplicationContext(),des);
							}
                            finish();
                        }else{
						    showInitError();
                        }
					}
				});

	}

	class PressToSpeakListen implements View.OnTouchListener {
		@SuppressLint({ "ClickableViewAccessibility", "Wakelock" })
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			isCutVoice = false;
			// 获取说话位置的点击事件
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				voiceMsgId = System.currentTimeMillis() + "";
				// 在这个点击的位置
				btn_upload_view.setClickable(false);
				btn_model_edit.setClickable(false);
				btn_upload_view.setEnabled(false);
				btn_model_edit.setEnabled(false);
				if (Build.VERSION.SDK_INT >= 11){
					btn_upload_view.setAlpha(0.4f);
					btn_model_edit.setAlpha(0.4f);
				}
				stopVoiceTimeTask();
				v.setPressed(true);
				voice_time_long.setText("00" + "''");
				voiceTimeLongStr = "00:00";
				voiceTimerLong = 0;
				currentVoiceLong = 0;
				recording_container.setVisibility(View.VISIBLE);
				voice_top_image.setVisibility(View.VISIBLE);
				mic_image.setVisibility(View.VISIBLE);
				mic_image_animate.setVisibility(View.VISIBLE);
				voice_time_long.setVisibility(View.VISIBLE);
				recording_timeshort.setVisibility(View.GONE);
				image_endVoice.setVisibility(View.GONE);
				txt_speak_content.setText(getResString("sobot_up_send"));
				// 设置语音的定时任务
				startVoice();
				return true;
				// 第二根手指按下
			case MotionEvent.ACTION_POINTER_DOWN:
				return true;
			case MotionEvent.ACTION_POINTER_UP:
				return true;
			case MotionEvent.ACTION_MOVE: {
				if (!is_startCustomTimerTask) {
					noReplyTimeUserInfo = 0;
				}

				if (event.getY() < 10) {
					// 取消界面的显示
					voice_top_image.setVisibility(View.GONE);
					image_endVoice.setVisibility(View.VISIBLE);
					mic_image.setVisibility(View.GONE);
					mic_image_animate.setVisibility(View.GONE);
					recording_timeshort.setVisibility(View.GONE);
					txt_speak_content.setText(getResString("sobot_up_send_calcel"));
					recording_hint.setText(getResString("sobot_release_to_cancel"));
					recording_hint.setBackgroundResource(getResDrawableId("sobot_recording_text_hint_bg"));
				} else {
					if (voiceTimerLong != 0) {
						txt_speak_content.setText(getResString("sobot_up_send"));
						voice_top_image.setVisibility(View.VISIBLE);
						mic_image_animate.setVisibility(View.VISIBLE);
						image_endVoice.setVisibility(View.GONE);
						mic_image.setVisibility(View.VISIBLE);
						recording_timeshort.setVisibility(View.GONE);
						recording_hint.setText(getResString("sobot_move_up_to_cancel"));
						recording_hint.setBackgroundResource(getResDrawableId("sobot_recording_text_hint_bg1"));
					}
				}
				return true;
			}
			case MotionEvent.ACTION_UP:
				// 手指抬起的操作
				int toLongOrShort = 0;
				btn_upload_view.setClickable(true);
				btn_model_edit.setClickable(true);
				btn_upload_view.setEnabled(true);
				btn_model_edit.setEnabled(true);
				if (Build.VERSION.SDK_INT >= 11){
					btn_upload_view.setAlpha(1f);
					btn_model_edit.setAlpha(1f);
				}
				v.setPressed(false);
				txt_speak_content.setText(getResString("sobot_press_say"));
				stopVoiceTimeTask();
				stopVoice();
				if (recording_container.getVisibility() == View.VISIBLE
						&& !isCutVoice) {
					hidePanelAndKeyboard(mPanelRoot);
					if(animationDrawable != null){
						animationDrawable.stop();
					}
					voice_time_long.setText("00" + "''");
					voice_time_long.setVisibility(View.INVISIBLE);
					if (event.getY() < 0) {
						recording_container.setVisibility(View.GONE);
						sendVoiceMap(2,voiceMsgId);
						return true;
						// 取消发送语音
					} else {
						// 发送语音
						if (currentVoiceLong < 1 * 1000) {
							voice_top_image.setVisibility(View.VISIBLE);
							recording_hint.setText(getResString("sobot_voice_can_not_be_less_than_one_second"));
							recording_hint.setBackgroundResource(getResDrawableId("sobot_recording_text_hint_bg"));
							recording_timeshort.setVisibility(View.VISIBLE);
							voice_time_long.setVisibility(View.VISIBLE);
							voice_time_long.setText("00:00");
							mic_image.setVisibility(View.GONE);
							mic_image_animate.setVisibility(View.GONE);
							toLongOrShort = 0;
							sendVoiceMap(2,voiceMsgId);
						} else if (currentVoiceLong < minRecordTime * 1000) {
							recording_container.setVisibility(View.GONE);
							sendVoiceMap(1,voiceMsgId);
							return true;
						} else if (currentVoiceLong > minRecordTime * 1000) {
							toLongOrShort = 1;
							voice_top_image.setVisibility(View.VISIBLE);
							recording_hint.setText(getResString("sobot_voiceTooLong"));
							recording_hint.setBackgroundResource(getResDrawableId("sobot_recording_text_hint_bg"));
							recording_timeshort.setVisibility(View.VISIBLE);
							mic_image.setVisibility(View.GONE);
							mic_image_animate.setVisibility(View.GONE);
						} else {
							sendVoiceMap(2,voiceMsgId);
						}
					}
					currentVoiceLong = 0;
					closeVoiceWindows(toLongOrShort);
				} else {
					sendVoiceMap(2,voiceMsgId);
				}
				voiceTimerLong = 0;
				restartMyTimeTask(handler);
				// mFileName
				return true;
			default:
				closeVoiceWindows(2);
				return true;
			}
		}
	}

	/**
	 * 按住说话动画开始
	 */
	private void startMicAnimate(){
		mic_image_animate.setBackgroundResource(getResDrawableId("sobot_voice_animation"));
		animationDrawable = (AnimationDrawable) mic_image_animate.getBackground();
		mic_image_animate.post(new Runnable() {
			@Override
			public void run() {
				animationDrawable.start();
			}
		});
		recording_hint.setText(getResString("sobot_move_up_to_cancel"));
		recording_hint.setBackgroundResource(getResDrawableId("sobot_recording_text_hint_bg1"));
	}

	private void removeMicAnimate(){
		handler.post(new Runnable() {
			@Override
			public void run() {
				for (int i = messageList.size() - 1; i > 0 ; i--) {
					ZhiChiMessageBase info = messageList.get(i);
					if (!TextUtils.isEmpty(info.getSenderType()) && Integer.parseInt(info.getSenderType()) == ZhiChiConstant
							.message_sender_type_send_voice && info.getSendSuccessState() == 4){
						messageList.remove(i);
						break;
					}
				}
				messageAdapter.notifyDataSetChanged();
			}
		});
	}

	public void closeVoiceWindows(int toLongOrShort) {
		Message message = handler.obtainMessage();
		message.what = ZhiChiConstant.hander_close_voice_view;
		message.arg1 = toLongOrShort;
		handler.sendMessageDelayed(message,500);
	}

	// 当时间超过1秒的时候自动发送
	public void voiceCuttingMethod() {
		stopVoice();
		sendVoiceMap(1,voiceMsgId);
		voice_time_long.setText("00" + "''");
	}

	/**
	 * 开始录音
	 */
	private void startVoice() {
		try {
			stopVoice();
			mFileName = mVoicePath + UUID.randomUUID().toString() + ".wav";
			String state = android.os.Environment.getExternalStorageState();
			if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
				LogUtils.i("sd卡被卸载了");
			}
			File directory = new File(mFileName).getParentFile();
			if (!directory.exists() && !directory.mkdirs()) {
				LogUtils.i("文件夹创建失败");
			}
			extAudioRecorder = ExtAudioRecorder.getInstanse(false);
			extAudioRecorder.setOutputFile(mFileName);
			extAudioRecorder.prepare();
			extAudioRecorder.start(new ExtAudioRecorder.AudioRecorderListener() {
				@Override
				public void onHasPermission() {
					startMicAnimate();
					startVoiceTimeTask(handler);
					sendVoiceMap(0,voiceMsgId);
				}

				@Override
				public void onNoPermission() {
					ToastUtil.showToast(getApplicationContext(), getResString("sobot_no_record_audio_permission"));
				}
			});
		} catch (Exception e) {
			LogUtils.i("prepare() failed");
		}
	}

	/* 停止录音 */
	private void stopVoice() {
		/* 布局的变化 */
		try {
			if (extAudioRecorder != null) {
				stopVoiceTimeTask();
				extAudioRecorder.stop();
				extAudioRecorder.release();
			}
		} catch (Exception e) {
			mRecorder = null;
		}
	}

	/**
	 * 发送语音的方式
	 * @param type 0：正在录制语音。  1：发送语音。2：取消正在录制的语音显示
	 * @param voiceMsgId  语音消息ID
     */
	private void sendVoiceMap(int type,String voiceMsgId) {
		// 发送语音的界面
		if (type == 0) {
			sendVoiceMessageToHandler(voiceMsgId, mFileName, voiceTimeLongStr, 4, SEND_VOICE, handler);
		}else if(type == 2){
			sendVoiceMessageToHandler(voiceMsgId, mFileName, voiceTimeLongStr, 0, CANCEL_VOICE, handler);
		} else {
			sendVoiceMessageToHandler(voiceMsgId, mFileName, voiceTimeLongStr, 2, UPDATE_VOICE, handler);
			// 发送http 返回发送成功的按钮
			sendVoice(voiceMsgId, voiceTimeLongStr, initModel.getCid(), initModel.getUid(), mFileName, handler);
			lv_message.setSelection(messageAdapter.getCount());
		}
		gotoLastItem();
	}

	/* 处理消息 */
	public Handler handler = new Handler() {

		@SuppressWarnings("unchecked")
		public void handleMessage(final android.os.Message msg) {
			switch (msg.what) {
			case ZhiChiConstant.hander_my_senderMessage:/* 我的文本消息 */
				updateUiMessage(messageAdapter, msg);
				lv_message.setSelection(messageAdapter.getCount());
				break;
			case ZhiChiConstant.hander_my_update_senderMessageStatus:
				updateTextMessageStatus(messageAdapter, msg);
				lv_message.setSelection(messageAdapter.getCount());
				break;
			case ZhiChiConstant.hander_robot_message:
				ZhiChiMessageBase zhiChiMessageBasebase = (ZhiChiMessageBase) msg.obj;
				if(type == ZhiChiConstant.type_robot_first || type == ZhiChiConstant.type_custom_first ){
					//智能客服模式下，特定问题类型的机器人回答语下显示“转人工”按钮。
					if(initModel != null && ChatUtils.checkManualType(initModel.getManualType(),
							zhiChiMessageBasebase.getAnswerType())){
						//如果此项在工作台上勾选 那就显示转人工按钮
						zhiChiMessageBasebase.setShowTransferBtn(true);
					}
				}

				if(ZhiChiConstant.type_answer_direct.equals(zhiChiMessageBasebase.getAnswerType())
						|| ZhiChiConstant.type_answer_wizard.equals(zhiChiMessageBasebase.getAnswerType())
						|| "11".equals(zhiChiMessageBasebase.getAnswerType())
						|| "12".equals(zhiChiMessageBasebase.getAnswerType())
						|| "14".equals(zhiChiMessageBasebase.getAnswerType())){
					if(initModel != null && initModel.isRealuateFlag()){
						//顶踩开关打开 显示顶踩按钮
						zhiChiMessageBasebase.setRevaluateState(1);
					}
				}

                updateUiMessage(messageAdapter,zhiChiMessageBasebase);
				if (SobotMsgManager.getInstance(getApplication()).getConfig().getInitModel() != null) {
					//机器人接口比较慢的情况下 用户销毁了view 依旧需要保存好机器人回答
					SobotMsgManager.getInstance(getApplication()).getConfig().addMessage(zhiChiMessageBasebase);
				}
				// 智能转人工：机器人优先时，如果未知问题或者向导问题则显示转人工
				if (type == ZhiChiConstant.type_robot_first && (ZhiChiConstant.type_answer_unknown.equals(zhiChiMessageBasebase
						.getAnswerType()) || ZhiChiConstant.type_answer_guide.equals(zhiChiMessageBasebase
						.getAnswerType()))) {
					showTransferCustomer();
				}
				gotoLastItem();
				break;
			case ZhiChiConstant.message_type_wo_sendImage: // 我发送图片 更新ui
				// 加载更过view隐藏
				updateUiMessage(messageAdapter, msg);
				break;
			case ZhiChiConstant.message_type_send_voice: // 发送语音
				updateUiMessage(messageAdapter, msg);
				break;
			// 修改语音的发送状态
			case ZhiChiConstant.message_type_update_voice:
				updateVoiceStatusMessage(messageAdapter, msg);
				break;
			case ZhiChiConstant.message_type_cancel_voice://取消未发送的语音
				cancelUiVoiceMessage(messageAdapter, msg);
				break;
			case ZhiChiConstant.hander_sendPicStatus_success:
				setTimeTaskMethod(handler);
				String id = (String) msg.obj;
				updateUiMessageStatus(messageAdapter, id, ZhiChiConstant.result_success_code, 0);
				break;
			case ZhiChiConstant.hander_sendPicStatus_fail:
				String resultId = (String) msg.obj;
				updateUiMessageStatus(messageAdapter, resultId, ZhiChiConstant.result_fail_code, 0);
				break;
			case ZhiChiConstant.hander_sendPicIsLoading:
				String loadId = (String) msg.obj;
				int uploadProgress = msg.arg1;
				updateUiMessageStatus(messageAdapter, loadId,
						ZhiChiConstant.hander_sendPicIsLoading, uploadProgress);
				break;
			case ZhiChiConstant.hander_timeTask_custom_isBusying: // 客服的定时任务
				// --客服忙碌
				updateUiMessage(messageAdapter, msg);
				LogUtils.i("客服的定时任务:" + noReplyTimeCustoms);
				stopCustomTimeTask();
				break;
			case ZhiChiConstant.hander_timeTask_userInfo:// 客户的定时任务
				updateUiMessage(messageAdapter, msg);
				stopUserInfoTimeTask();
				LogUtils.i("客户的定时任务的时间  停止定时任务：" + noReplyTimeUserInfo);
				break;
			case ZhiChiConstant.voiceIsRecoding:
				// 录音的时间超过一分钟的时间切断进行发送语音
				if (voiceTimerLong >= minRecordTime * 1000) {
					isCutVoice = true;
					voiceCuttingMethod();
					voiceTimerLong = 0;
					recording_hint.setText(getResString("sobot_voiceTooLong"));
					recording_hint.setBackgroundResource(getResDrawableId("sobot_recording_text_hint_bg"));
					recording_timeshort.setVisibility(View.VISIBLE);
					mic_image.setVisibility(View.GONE);
					mic_image_animate.setVisibility(View.GONE);
					closeVoiceWindows(2);
					btn_press_to_speak.setPressed(false);
					currentVoiceLong = 0;
				} else {
					final int time = Integer.parseInt(msg.obj.toString());
//					LogUtils.i("录音定时任务的时长：" + time);
					currentVoiceLong = time;
					if (time < recordDownTime * 1000) {
						if (time % 1000 == 0) {
							voiceTimeLongStr = TimeTools.instance.calculatTime(time);
							voice_time_long.setText(voiceTimeLongStr.substring(3) + "''");
						}
					} else if (time < minRecordTime * 1000) {
						if (time % 1000 == 0) {
							voiceTimeLongStr = TimeTools.instance.calculatTime(time);
							voice_time_long.setText(getResString("sobot_count_down") + (minRecordTime * 1000 - time) / 1000);
						}
					} else {
						voice_time_long.setText(getResString("sobot_voiceTooLong"));
					}
				}
				break;
			case ZhiChiConstant.hander_close_voice_view:
				int longOrShort = msg.arg1;
				txt_speak_content.setText(getResString("sobot_press_say"));
				currentVoiceLong = 0;
				recording_container.setVisibility(View.GONE);

				if (longOrShort == 0){
					for (int i = messageList.size() - 1; i > 0 ; i--) {
						if (!TextUtils.isEmpty(messageList.get(i).getSenderType()) &&
								Integer.parseInt(messageList.get(i).getSenderType()) == 8){
							messageList.remove(i);
							break;
						}
					}
				}
				break;
			default:
				break;
			}
		}
	};

	//仅人工时排队UI更新
	private void onlyCustomPaidui() {
		sobot_ll_bottom.setVisibility(View.VISIBLE);

		btn_set_mode_rengong.setVisibility(View.GONE);
		btn_set_mode_rengong.setClickable(false);

		btn_upload_view.setVisibility(View.VISIBLE);
		btn_upload_view.setClickable(false);
		btn_upload_view.setEnabled(false);

		showEmotionBtn();
		btn_emoticon_view.setClickable(false);
		btn_emoticon_view.setEnabled(false);

        btn_model_voice.setVisibility(info.isUseVoice()?View.VISIBLE:View.GONE);
		btn_model_voice.setClickable(false);
		btn_model_voice.setEnabled(false);

		if (Build.VERSION.SDK_INT >= 11){
			btn_upload_view.setAlpha(0.4f);
			btn_model_voice.setAlpha(0.4f);
		}

		edittext_layout.setVisibility(View.GONE);
		btn_press_to_speak.setClickable(false);
		btn_press_to_speak.setEnabled(false);
		btn_press_to_speak.setVisibility(View.VISIBLE);
		txt_speak_content.setText(getResString("sobot_in_line"));

		if (sobot_ll_restart_talk.getVisibility() == View.VISIBLE) {
			sobot_ll_restart_talk.setVisibility(View.GONE);
		}
	}

	private void createConsultingContent() {
		ConsultingContent consultingContent = info.getConsultingContent();
		if(consultingContent != null && !TextUtils.isEmpty(consultingContent.getSobotGoodsTitle()) && !TextUtils.isEmpty(consultingContent.getSobotGoodsFromUrl())){
			ZhiChiMessageBase zhichiMessageBase = new ZhiChiMessageBase();
			zhichiMessageBase.setSenderType(ZhiChiConstant.message_sender_type_consult_info + "");
			if(!TextUtils.isEmpty(consultingContent.getSobotGoodsImgUrl())){
				zhichiMessageBase.setPicurl(consultingContent.getSobotGoodsImgUrl());
			}
			ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
			zhichiMessageBase.setAnswer(reply);
			zhichiMessageBase.setT(consultingContent.getSobotGoodsTitle());
			zhichiMessageBase.setUrl(consultingContent.getSobotGoodsFromUrl());
			zhichiMessageBase.setCid(initModel == null?"":initModel.getCid());
			zhichiMessageBase.setAname(consultingContent.getSobotGoodsLable());
			zhichiMessageBase.setReceiverFace(consultingContent.getSobotGoodsDescribe());

			zhichiMessageBase.setAction(ZhiChiConstant.action_consultingContent_info);
			updateUiMessage(messageAdapter, zhichiMessageBase);
			handler.post(new Runnable() {
				@Override
				public void run() {
					lv_message.setSelection(messageAdapter.getCount());
				}
			});
		} else {
			if (messageAdapter != null){
				messageAdapter.removeConsulting();
			}
		}
	}

	/**
	 * 根据输入框里的内容切换显示  发送按钮还是加号（更多方法）
	 */
	private void resetBtnUploadAndSend(){
		if (et_sendmessage.getText().toString().length() > 0) {
			btn_upload_view.setVisibility(View.GONE);
			btn_send.setVisibility(View.VISIBLE);
		} else {
			btn_send.setVisibility(View.GONE);
			btn_upload_view.setVisibility(View.VISIBLE);
			btn_upload_view.setEnabled(true);
			btn_upload_view.setClickable(true);
			if (Build.VERSION.SDK_INT >= 11){
				btn_upload_view.setAlpha(1f);
			}
		}
	}

	@Override
	public void forwordMethod() {
		hidePanelAndKeyboard(mPanelRoot);
		ClearHistoryDialog clearHistoryDialog = new ClearHistoryDialog(this);
		clearHistoryDialog.setCanceledOnTouchOutside(true);
		clearHistoryDialog.setOnClickListener(new ClearHistoryDialog.DialogOnClickListener() {
			@Override
			public void onSure() {
				clearHistory();
			}
		});
		clearHistoryDialog.show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			close();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void close() {
		//按返回按钮的时候 如果面板显示就隐藏面板  如果面板已经隐藏那么就是用户想退出
		if (mPanelRoot.getVisibility() == View.VISIBLE) {
			hidePanelAndKeyboard(mPanelRoot);
		}else{
			if (info.isShowSatisfaction()) {
				showCommentOrFinish();
			} else {
				finish();
			}
		}
	}

	/**
	 * 转人工成功的方法
	 */
	private void connCustomerServiceSuccess(ZhiChiMessageBase base){
		if (base == null){
			return;
		}
		//开启通道
		zhiChiApi.connChannel(base.getWslinkBak(), base.getWslinkDefault(),initModel.getUid(),
				base.getPuid(),info.getAppkey());
		createCustomerService(base.getAname(),base.getAface());
	}

	/**
	 * 建立与客服的对话
	 * @param name 客服的名称
	 * @param face  客服的头像
     */
	private void createCustomerService(String name,String face){
		//改变变量
		current_client_model = ZhiChiConstant.client_model_customService;
		customerState = CustomerState.Online;
		isAboveZero = false;
		isComment = false;// 转人工时 重置为 未评价
		queueNum = 0;
		currentUserName = TextUtils.isEmpty(name)?"":name;
		//显示被xx客服接入
		updateUiMessage(messageAdapter, ChatUtils.getServiceAcceptTip(getApplicationContext(),name));
		//显示人工欢迎语
		updateUiMessage(messageAdapter, ChatUtils.getServiceHelloTip(name,face,initModel.getAdminHelloWord()));
		//显示标题
		showLogicTitle(name,false);
		//创建咨询项目
		createConsultingContent();
		gotoLastItem();
		//设置底部键盘
		setBottomView(ZhiChiConstant.bottomViewtype_customer);
		SobotMsgManager.getInstance(getApplication()).getConfig().bottomViewtype = ZhiChiConstant.bottomViewtype_customer;

		// 启动计时任务
		restartInputListener();
		stopUserInfoTimeTask();
		is_startCustomTimerTask = false;
		startUserInfoTimeTask(handler);
		hideItemTransferBtn();
	}

	/**
	 * 连接客服时，需要排队
	 * 显示排队的处理逻辑
	 * @param num 当前排队的位置
     */
	private void createCustomerQueue(String num){
		if (customerState == CustomerState.Queuing && !TextUtils.isEmpty(num)
				&& Integer.parseInt(num) > 0) {
			stopUserInfoTimeTask();
			stopCustomTimeTask();
			stopInputListener();

			queueNum = Integer.parseInt(num);
			//显示当前排队的位置
			showInLineHint();

			if (type == ZhiChiConstant.type_custom_only) {
				showLogicTitle(getResString("sobot_in_line_title"),false);
				setBottomView(ZhiChiConstant.bottomViewtype_onlycustomer_paidui);
				SobotMsgManager.getInstance(getApplication()).getConfig().bottomViewtype = ZhiChiConstant.bottomViewtype_onlycustomer_paidui;
			} else {
				showLogicTitle(initModel.getRobotName(),false);
				setBottomView(ZhiChiConstant.bottomViewtype_paidui);
				SobotMsgManager.getInstance(getApplication()).getConfig().bottomViewtype = ZhiChiConstant.bottomViewtype_paidui;
			}

			queueTimes = queueTimes + 1;
			if (type == ZhiChiConstant.type_custom_first) {
				if (queueTimes == 1) {
					//如果当前为人工优先模式那么在第一次收到
					remindRobotMessage(handler);
				}
			}
		}
	}

	/**
	 * 转人工失败
     */
	private void connCustomerServiceFail(){
		if (type == 2) {
			showLeaveMsg();
		} else {
			showLogicTitle(initModel.getRobotName(),false);
			showCustomerOfflineTip();
			if (type == ZhiChiConstant.type_custom_first && current_client_model ==
					ZhiChiConstant.client_model_robot) {
				remindRobotMessage(handler);
			}
		}
		gotoLastItem();
	}

	/**
	 * 转人工 用户是黑名单
     */
	private void connCustomerServiceBlack() {
		showLogicTitle(initModel.getRobotName(),false);
		showCustomerUanbleTip();
		if (type == ZhiChiConstant.type_custom_first) {
			remindRobotMessage(handler);
		}
	}

	/**
	 * 显示客服不在线的提示
	 */
	private void showCustomerOfflineTip(){
		ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
		reply.setMsgType(null);
		reply.setMsg(initModel.getAdminNonelineTitle());
		reply.setRemindType(ZhiChiConstant.sobot_remind_type_customer_offline);
		ZhiChiMessageBase base = new ZhiChiMessageBase();
		base.setSenderType(ZhiChiConstant.message_sender_type_remide_info + "");
		base.setAnswer(reply);
		base.setAction(ZhiChiConstant.action_remind_info_post_msg);
		updateUiMessage(messageAdapter, base);
	}

	/**
	 * 显示无法转接客服
	 */
	private void showCustomerUanbleTip(){
		ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
		reply.setMsgType(null);
		reply.setMsg(getResString("sobot_unable_transfer_to_customer_service"));
		reply.setRemindType(ZhiChiConstant.sobot_remind_type_unable_to_customer);
		ZhiChiMessageBase base = new ZhiChiMessageBase();
		base.setSenderType(ZhiChiConstant.message_sender_type_remide_info + "");
		base.setAnswer(reply);
		base.setAction(ZhiChiConstant.action_remind_info_post_msg);
		updateUiMessage(messageAdapter, base);
	}

	/**
	 * 隐藏条目中的转人工按钮
	 */
	public void hideItemTransferBtn(){
		// 把机器人回答中的转人工按钮都隐藏掉
		runOnUiThread(new Runnable() {

			@Override
			public void run() {

				for (int i = 0, count = lv_message.getChildCount(); i < count; i++) {
					View child = lv_message.getChildAt(i);
					if (child == null || child.getTag() == null || !(child.getTag() instanceof RichTextMessageHolder)) {
						continue;
					}
					RichTextMessageHolder holder = (RichTextMessageHolder) child.getTag();
					holder.hideTransferBtn();
				}
			}
		});
	}

	@Override
	public void onClick(final View view) {

		if (view == notReadInfo) {
			for (int i = messageList.size() - 1; i >= 0; i--) {
				if (messageList.get(i).getAnswer() != null && ZhiChiConstant.
						sobot_remind_type_below_unread == messageList.get(i).getAnswer().getRemindType()){
					lv_message.setSelection(i);
					break;
				}
			}
			notReadInfo.setVisibility(View.GONE);
		}

		if (view == btn_send) {// 发送消息按钮
			//获取发送内容
			final String message_result = et_sendmessage.getText().toString().trim();
			if (!TextUtils.isEmpty(message_result)) {
				resetEmoticonBtn();
				try {
					et_sendmessage.setText("");
					sendMsg(message_result);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (view == btn_upload_view) {// 显示上传view
			pressSpeakSwitchPanelAndKeyboard(btn_upload_view);
			doEmoticonBtn2Blur();
			gotoLastItem();
		}

		if(view == btn_emoticon_view){//显示表情面板
			// 切换表情面板
			pressSpeakSwitchPanelAndKeyboard(btn_emoticon_view);
			//切换表情按钮的状态
			switchEmoticonBtn();
			gotoLastItem();
		}

		if (view == btn_model_edit) {// 从编辑模式转换到语音
			doEmoticonBtn2Blur();
			// 软件盘的处理
			KPSwitchConflictUtil.showKeyboard(mPanelRoot, et_sendmessage);
			editModelToVoice(View.GONE, "123");// 编辑模式隐藏 ，语音模式显示
		}

		if (view == btn_model_voice) { // 从语音转换到编辑模式
			doEmoticonBtn2Blur();
			has_record_audio_permission=CommonUtils.checkPermission(SobotChatActivity.this,
					Manifest.permission.RECORD_AUDIO, ZhiChiConstant.SOBOT_RECORD_AUDIO_REQUEST_CODE);
			has_write_external_storage_permission=CommonUtils.checkPermission(SobotChatActivity.this,
					Manifest.permission.WRITE_EXTERNAL_STORAGE, ZhiChiConstant.SOBOT_WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
			if(!has_record_audio_permission || !has_write_external_storage_permission){
				return;
			}
			try {
				mFileName = mVoicePath + UUID.randomUUID().toString() + ".wav";
				String state = android.os.Environment.getExternalStorageState();
				if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
					LogUtils.i("SD Card is not mounted,It is  " + state + ".");
				}
				File directory = new File(mFileName).getParentFile();
				if (!directory.exists() && !directory.mkdirs()) {
					LogUtils.i("Path to file could not be created");
				}
				extAudioRecorder = ExtAudioRecorder.getInstanse(false);
				extAudioRecorder.setOutputFile(mFileName);
				extAudioRecorder.prepare();
				extAudioRecorder.start(new ExtAudioRecorder.AudioRecorderListener() {
					@Override
					public void onHasPermission() {
						hidePanelAndKeyboard(mPanelRoot);
						editModelToVoice(View.VISIBLE, "");// 编辑模式显示
						if (btn_press_to_speak.getVisibility() == View.VISIBLE){
							btn_press_to_speak.setVisibility(View.VISIBLE);
							btn_press_to_speak.setClickable(true);
							btn_press_to_speak.setOnTouchListener(new PressToSpeakListen());
							btn_press_to_speak.setEnabled(true);
							txt_speak_content.setText(getResString("sobot_press_say"));
							txt_speak_content.setVisibility(View.VISIBLE);
						}
					}

					@Override
					public void onNoPermission() {
						ToastUtil.showToast(getApplicationContext(), getResString("sobot_no_record_audio_permission"));
					}
				});
				stopVoice();
			} catch (Exception e) {
				LogUtils.i("prepare() failed");
			}

		}

		if (view == sobot_tv_left) {
			hidePanelAndKeyboard(mPanelRoot);
			close();
		}
	}

	/**
	 * 发送消息的方法
	 * @param content
     */
	private void sendMsg(String content) {
		if(initModel == null){
			return;
		}
		// 通知Handler更新 我的消息ui
		String msgId = System.currentTimeMillis() + "";
		sendTextMessageToHandler(msgId, content, handler, 2, false);

		LogUtils.i("当前发送消息模式：" + current_client_model);
		setTimeTaskMethod(handler);
		Map<String,String> map = new HashMap<>();
		map.put("content","当前模式："+current_client_model+"---content:"+content);
		map.put("title","sendMessageWithLogic");
		map.put("uid",initModel.getUid());
		map.put("companyid",initModel.getCompanyId());
		LogUtils.i2Local(map);
		sendMessageWithLogic(msgId, content, initModel, handler, current_client_model,0,"");
	}

	/**
	 * 提供给聊天面板执行的方法
	 * 满意度
	 */
	public void btnSatisfaction(){
		lv_message.setSelection(messageAdapter.getCount());
		//满意度逻辑 点击时首先判断是否评价过 评价过 弹您已完成提示 未评价 判断是否达到可评价标准
		hidePanelAndKeyboard(mPanelRoot);
		submitEvaluation();
	}

	/**
	 * 判断用户是否为黑名单
	 * @return
     */
	private boolean isUserBlack(){
		if("1".equals(initModel.getIsblack())){
			return true;
		}
		return false;
	}

	/**
	 * 提供给聊天面板执行的方法
	 * 图库
	 */
	public void btnPicture(){
		hidePanelAndKeyboard(mPanelRoot);
		selectPicFromLocal();
		lv_message.setSelection(messageAdapter.getCount());
	}

	/**
	 * 提供给聊天面板执行的方法
	 * 照相
	 */
	public void btnCameraPicture(){
		hidePanelAndKeyboard(mPanelRoot);
		selectPicFromCamera(); // 拍照 上传
		lv_message.setSelection(messageAdapter.getCount());
	}

	/*
	 * 发送咨询内容
	 *
	 */
	public void sendConsultingContent(){
		if(customerState == CustomerState.Online && current_client_model == ZhiChiConstant
				.client_model_customService){
			final String title = info.getConsultingContent().getSobotGoodsTitle().trim();
			final String describe = TextUtils.isEmpty(info.getConsultingContent().getSobotGoodsDescribe())?"":info.getConsultingContent().getSobotGoodsDescribe().trim();
			final String lable = TextUtils.isEmpty(info.getConsultingContent().getSobotGoodsLable())?"":info.getConsultingContent().getSobotGoodsLable().trim();
			final String fromUrl = info.getConsultingContent().getSobotGoodsFromUrl().trim();
			if (!TextUtils.isEmpty(fromUrl) && !TextUtils.isEmpty(title)) {
				String content = getResString("sobot_consulting_title") + title + "\n"+
						(!TextUtils.isEmpty(describe)?getResString("sobot_consulting_describe") + describe + "\n":"")
						+(!TextUtils.isEmpty(lable)?getResString("sobot_consulting_lable") + lable + "\n":"")
						+ getResString("sobot_consulting_fromurl") + fromUrl;
				sendMsg(content);
			}
		}
	}

	public void showCommentOrFinish() {
		if (!isCustomPushEvaluate) {
			if (isAboveZero && !isComment) {
				// 退出时 之前没有评价过的话 才能 弹评价框
				ChatUtils.showEvaluateDialog(SobotChatActivity.this,true,initModel,
						current_client_model,1);
			} else {
				finish();
			}
		} else {
			finish();
		}
	}

	@Override
	public void onRefresh() {
		getHistoryMessage(false);
	}

	public void clearHistory(){
		zhiChiApi.deleteHisMsg(initModel.getUid(), new StringResultCallBack<CommonModelBase>() {
			@Override
			public void onSuccess(CommonModelBase modelBase) {
				messageList.clear();
				cids.clear();
				messageAdapter.notifyDataSetChanged();
				lv_message.setPullRefreshEnable(true);// 设置下拉刷新列表
			}

			@Override
			public void onFailure(Exception e, String des) {}
		});
	}

	/**
	 * 获取聊天记录
	 * @param isFirst 第一次查询历史记录
     */
	public void getHistoryMessage(final boolean isFirst) {
		if (initModel == null)
			return;

		if((!getCidsFinish && !isFirst) || isInGethistory){
			//1.查询cid接口没有结束时 又不是第一次查询历史记录  那么 直接什么也不做就返回
			//2.如果查询历史记录的接口正在跑   那么什么也不做
			onLoad();
		} else {
			String currentCid = ChatUtils.getCurrentCid(initModel,cids,currentCidPosition);
			if("-1".equals(currentCid)){
				showNoHistory();
				onLoad();
				return;
			}
			isInGethistory = true;
			zhiChiApi.getChatDetailByCid(initModel.getUid(), currentCid, new StringResultCallBack<ZhiChiHistoryMessage>() {
						@Override
						public void onSuccess(ZhiChiHistoryMessage zhiChiHistoryMessage) {
							isInGethistory = false;
							onLoad();
							currentCidPosition++;
							List<ZhiChiHistoryMessageBase> data = zhiChiHistoryMessage.getData();
							if(data != null && data.size() > 0){
								showData(data);
							} else {
								//没有数据的时候继续拉
								getHistoryMessage(false);
							}
						}

						@Override
						public void onFailure(Exception e, String des) {
							isInGethistory = false;
							mUnreadNum = 0;
							updateFloatUnreadIcon();
							onLoad();
						}
					});
		}
	}

	private void showData(List<ZhiChiHistoryMessageBase> result){
		List<ZhiChiMessageBase> msgLists = new ArrayList<>();
		List<ZhiChiMessageBase> msgList;
		for (int i = 0; i < result.size(); i++) {
			ZhiChiHistoryMessageBase historyMsg = result.get(i);
			msgList = historyMsg.getContent();

			for (ZhiChiMessageBase base : msgList) {
				base.setSugguestionsFontColor(1);
				if (base.getSdkMsg() != null) {
					ZhiChiReplyAnswer answer = base.getSdkMsg().getAnswer();
					if (answer != null){
						if (answer.getMsgType() == null){
							answer.setMsgType("0");
						}

						if (!TextUtils.isEmpty(answer.getMsg()) && answer.getMsg().length() > 4){
							String msg = answer.getMsg().replace("&lt;/p&gt;","<br>");
							if (msg.endsWith("<br>")){
								msg = msg.substring(0, msg.length()-4);
							}
							answer.setMsg(msg);
						}
					}
					if (ZhiChiConstant.message_sender_type_robot == Integer
							.parseInt(base.getSenderType())) {
						base.setSenderName(TextUtils.isEmpty(base.getSenderName()) ? initModel
								.getRobotName() : base.getSenderName());
						base.setSenderFace(TextUtils.isEmpty(base.getSenderFace()) ? initModel
								.getRobotLogo() : base.getSenderFace());
					}
					base.setAnswer(answer);
					base.setSugguestions(base.getSdkMsg()
							.getSugguestions());
					base.setStripe(base.getSdkMsg().getStripe());
					base.setAnswerType(base.getSdkMsg()
							.getAnswerType());
				}
			}
			msgLists.addAll(msgList);
		}

		if (msgLists.size() > 0) {
			if(mUnreadNum > 0){
				ZhiChiMessageBase unreadMsg = ChatUtils.getUnreadMode(getApplicationContext());
				unreadMsg.setCid(msgLists.get(msgLists.size()-1).getCid());
				msgLists.add((msgLists.size() - mUnreadNum) < 0 ? 0:(msgLists.size() - mUnreadNum)
						,unreadMsg);
				updateFloatUnreadIcon();
				mUnreadNum = 0;
			}
			messageAdapter.addData(msgLists);
			messageAdapter.notifyDataSetChanged();
			lv_message.setSelection(msgLists.size());
		}
	}

	/**
	 * 显示没有更多历史记录
	 */
	private void showNoHistory(){
		ZhiChiMessageBase base = new ZhiChiMessageBase();

		base.setSenderType(ZhiChiConstant.message_sender_type_remide_info + "");

		ZhiChiReplyAnswer reply1 = new ZhiChiReplyAnswer();
		reply1.setRemindType(ZhiChiConstant.sobot_remind_type_nomore);
		reply1.setMsg(getResString("sobot_no_more_data"));
		base.setAnswer(reply1);
		// 更新界面的操作
		updateUiMessageBefore(messageAdapter, base);
		lv_message.setSelection(0);

		lv_message.setPullRefreshEnable(false);// 设置下拉刷新列表
		isNoMoreHistoryMsg = true;
		mUnreadNum = 0;
	}

	private void onLoad() {
		lv_message.onRefreshCompleteHeader();
	}

	// 键盘编辑模式转换为语音模式
	private void editModelToVoice(int typeModel, String str) {
		btn_model_edit.setVisibility(View.GONE == typeModel ? View.GONE
				: View.VISIBLE); // 键盘编辑隐藏
		btn_model_voice.setVisibility(View.VISIBLE != typeModel ? View.VISIBLE
				: View.GONE);// 语音模式开启
		btn_press_to_speak.setVisibility(View.GONE != typeModel ? View.VISIBLE
				: View.GONE);
		edittext_layout.setVisibility(View.VISIBLE == typeModel ? View.GONE
				: View.VISIBLE);

		if (!TextUtils.isEmpty(et_sendmessage.getText().toString()) && str.equals("123")) {
			btn_send.setVisibility(View.VISIBLE);
			btn_upload_view.setVisibility(View.GONE);
		} else {
			btn_send.setVisibility(View.GONE);
			btn_upload_view.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 返回数据
	 */
	@SuppressWarnings("unused")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LogUtils.i("多媒体返回的结果：" + requestCode + "--" + resultCode + "--" + data);

		if (resultCode == RESULT_OK) {
			if (requestCode == ZhiChiConstant.REQUEST_CODE_picture) { // 发送本地图片
				if (data != null && data.getData() != null) {
					Uri selectedImage = data.getData();
					// 通知handler更新图片
					ChatUtils.sendPicByUri(SobotChatActivity.this, handler, selectedImage, initModel, lv_message,messageAdapter);
				} else {
					ToastUtil.showLongToast(getApplicationContext(),getResString("sobot_did_not_get_picture_path"));
				}
			} else if (requestCode == ZhiChiConstant.REQUEST_CODE_makePictureFromCamera) {
				if (cameraFile != null && cameraFile.exists()) {
					LogUtils.i("cameraFile.getAbsolutePath()------>>>>" + cameraFile.getAbsolutePath());
					String id = System.currentTimeMillis() + "";
					ChatUtils.sendPicLimitBySize(cameraFile.getAbsolutePath(), initModel.getCid(),
							initModel.getUid(), handler, getApplicationContext(), lv_message,messageAdapter);
				} else {
					ToastUtil.showLongToast(getApplicationContext(),getResString("sobot_pic_select_again"));
				}
			}
			hidePanelAndKeyboard(mPanelRoot);
		}
		if (resultCode == ZhiChiConstant.RESOULT_COCE_TO_GRROUP) {
			if(data != null){
				int groupIndex = data.getIntExtra("groupIndex",-1);
				LogUtils.i("groupIndex-->" + groupIndex);
				if (groupIndex >= 0) {
					connectCustomerService(list_group.get(groupIndex).getGroupId(),list_group.get(groupIndex).getGroupName());
				}
			}
		}
	}

	/* 返回按钮 */
	@Override
	public void onBackPressed() {
		close();
	}

	/**
	 * 广播接受者：
	 */
	public class MyMessageReceiver extends BroadcastReceiver {
		@SuppressWarnings("deprecation")
		@Override
		public void onReceive(Context context, Intent intent) {
			LogUtils.i("广播是  :" + intent.getAction());
			if (ZhiChiConstants.receiveMessageBrocast.endsWith(intent.getAction())) {
				// 接受下推的消息
				ZhiChiPushMessage pushMessage = (ZhiChiPushMessage) intent
						.getExtras().getSerializable(ZhiChiConstants.ZHICHI_PUSH_MESSAGE);
				if(pushMessage == null){
					return;
				}
				ZhiChiMessageBase base = new ZhiChiMessageBase();
				base.setSenderName(pushMessage.getAname());

				if (ZhiChiConstant.push_message_createChat == pushMessage.getType()) {
					setAdminFace(pushMessage.getAface());
					if (type == 2 || type == 3 || type == 4) {
						createCustomerService(pushMessage.getAname(),pushMessage.getAface());
					}
				} else if (ZhiChiConstant.push_message_paidui == pushMessage.getType()) {
					// 排队的消息类型
					createCustomerQueue(pushMessage.getCount());
				} else if (ZhiChiConstant.push_message_receverNewMessage == pushMessage.getType()) {
					// 接收到新的消息
					if (customerState == CustomerState.Online) {
						base.setSender(pushMessage.getAname());
						base.setSenderName(pushMessage.getAname());
						base.setSenderFace(pushMessage.getAface());
						base.setSenderType(ZhiChiConstant.message_sender_type_service + "");
						ZhiChiReplyAnswer reply = null;
						if(TextUtils.isEmpty(pushMessage.getMsgType())){
							return;
						}
						if ("7".equals(pushMessage.getMsgType())) {
							reply = GsonUtil.jsonToZhiChiReplyAnswer(pushMessage.getContent());
						} else {
							reply = new ZhiChiReplyAnswer();
							reply.setMsgType(pushMessage.getMsgType() + "");
							reply.setMsg(pushMessage.getContent());
						}

						base.setAnswer(reply);
						// 更新界面的操作
						updateUiMessage(messageAdapter, base);
						stopCustomTimeTask();
						startUserInfoTimeTask(handler);
					}else{
						int localUnreadNum = SharedPreferencesUtil.getIntData(getApplicationContext(),
								"sobot_unread_count",0);
						localUnreadNum++;
						SharedPreferencesUtil.saveIntData(getApplicationContext(),"sobot_unread_count",localUnreadNum);
					}
				} else if (ZhiChiConstant.push_message_outLine == pushMessage.getType()) {
					// 用户被下线
					customerServiceOffline(initModel,Integer.parseInt(pushMessage.getStatus()));
				} else if (ZhiChiConstant.push_message_transfer == pushMessage.getType()) {
					LogUtils.i("用户被转接--->"+pushMessage.getName());
					//替换标题
					showLogicTitle(pushMessage.getName(),false);
					setAdminFace(pushMessage.getFace());
					currentUserName = pushMessage.getName();
				} else if (ZhiChiConstant.push_message_custom_evaluate == pushMessage.getType()){
					LogUtils.i("客服推送满意度评价.................");
					if (isActivityStart){
						if (isAboveZero && !isComment) {
							// 满足评价条件，并且之前没有评价过的话 才能 弹评价框  commentType 评价类型 主动评价1 邀请评价0
							ChatUtils.showEvaluateDialog(SobotChatActivity.this,false,initModel,current_client_model,0);
						}
					} else {
						isNeedShowEvaluate = true;
					}
				}
			} else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
				if (!CommonUtils.isNetWorkConnected(getApplicationContext())) {
					//没有网络
					if (welcome.getVisibility() != View.VISIBLE){
						setShowNetRemind(true);
					}
				} else {
					// 有网络
					setShowNetRemind(false);
					if(IntenetUtil.isWifiConnected(getApplicationContext()) && logCollectTime == 0){
						logCollectTime++;
						zhiChiApi.logCollect(getApplicationContext(),info.getAppkey());
					}
				}
			} else if (ZhiChiConstants.chat_remind_post_msg.equals(intent.getAction())) {
				startToPostMsgActivty(false);
			} else if (ZhiChiConstants.sobot_click_cancle.equals(intent.getAction())) {
				//打开技能组后点击了取消
				if (type == 4) {
					SobotChatActivity.this.finish();
				}
			} else if (ZhiChiConstants.dcrc_comment_state.equals(intent.getAction())) {
				//评价完客户后所需执行的逻辑
				isComment = intent.getBooleanExtra("commentState", false);
				boolean isFinish = intent.getBooleanExtra("isFinish", false);

				//配置用户提交人工满意度评价后释放会话
				if(ChatUtils.isEvaluationCompletedExit(getApplicationContext(),isComment,current_client_model)){
					//如果是人工并且评价完毕就释放会话
					customerServiceOffline(initModel,1);
					ChatUtils.userLogout(getApplicationContext());
				}
				ChatUtils.showThankDialog(SobotChatActivity.this,handler,isFinish);
			} else if (ZhiChiConstants.sobot_close_now.equals(intent.getAction())){
				finish();
			} else if (ZhiChiConstants.sobot_close_now_clear_cache.equals(intent.getAction())){
				isSessionOver = true;
				finish();
			} else if (ZhiChiConstants.SOBOT_CHANNEL_STATUS_CHANGE.equals(intent.getAction())){
				if(customerState == CustomerState.Online || customerState == CustomerState.Queuing){
					int connStatus = intent.getIntExtra("connStatus", Const.CONNTYPE_IN_CONNECTION);
					LogUtils.i("connStatus:"+connStatus);
					switch (connStatus){
						case Const.CONNTYPE_IN_CONNECTION:
							sobot_container_conn_status.setVisibility(View.VISIBLE);
							sobot_title_conn_status.setText(getResString("sobot_conntype_in_connection"));
							mTitleTextView.setVisibility(View.GONE);
							sobot_conn_loading.setVisibility(View.VISIBLE);
							break;
						case Const.CONNTYPE_CONNECT_SUCCESS:
							sobot_container_conn_status.setVisibility(View.GONE);
							sobot_title_conn_status.setText(getResString("sobot_conntype_connect_success"));
							mTitleTextView.setVisibility(View.VISIBLE);
							sobot_conn_loading.setVisibility(View.GONE);
							break;
						case Const.CONNTYPE_UNCONNECTED:
							sobot_container_conn_status.setVisibility(View.VISIBLE);
							sobot_title_conn_status.setText(getResString("sobot_conntype_unconnected"));
							mTitleTextView.setVisibility(View.GONE);
							sobot_conn_loading.setVisibility(View.GONE);
							break;
					}
				}else{
					mTitleTextView.setVisibility(View.VISIBLE);
					sobot_container_conn_status.setVisibility(View.GONE);
				}
			}
		}
	}

	/**
	 * 显示下线的逻辑
	 * @param initModel
	 * @param outLineType  下线的类型
     */
	@Override
	public void customerServiceOffline(ZhiChiInitModeBase initModel, int outLineType) {
        if(initModel == null){
            return;
        }
		queueNum = 0;
		stopInputListener();
		stopUserInfoTimeTask();
		stopCustomTimeTask();
		customerState = CustomerState.Offline;

		//设置提醒
		showOutlineTip(initModel,outLineType);
		//更改底部键盘
		setBottomView(ZhiChiConstant.bottomViewtype_outline);
		SobotMsgManager.getInstance(getApplication()).getConfig().bottomViewtype = ZhiChiConstant.bottomViewtype_outline;

		if(Integer.parseInt(initModel.getType()) == ZhiChiConstant.type_custom_only) {
			if(1 == outLineType){
				//如果在排队中 客服离开，那么提示无客服
				showLogicTitle(getResString("sobot_no_access"),false);
			}
		}

		if (6 == outLineType) {
			//打开新窗口
			SobotApi.disSobotChannel(getApplicationContext());
		}
		isSessionOver = true;
	}

	/**
	 * 初始化sdk
	 * @param isReConnect 是否是重新接入
	 **/
	private void initSdk(boolean isReConnect) {
		if(isReConnect){
			current_client_model = ZhiChiConstant.client_model_robot;
			showTimeVisiableCustomBtn = 0;
			messageList.clear();
			messageAdapter.notifyDataSetChanged();
			cids.clear();
			currentCidPosition = 0;
			getCidsFinish = false;
			isNoMoreHistoryMsg = false;
			isFirst = true;
			isAboveZero = false;
			isComment = false;// 重新开始会话时 重置为 没有评价过
			customerState = CustomerState.Offline;
			remindRobotMessageTimes = 0;
			queueTimes = 0;
			isSessionOver = false;

			sobot_txt_restart_talk.setVisibility(View.GONE);
			sobot_tv_message.setVisibility(View.GONE);
			sobot_tv_satisfaction.setVisibility(View.GONE);
			image_reLoading.setVisibility(View.VISIBLE);
			AnimationUtil.rotate(image_reLoading);

			lv_message.setPullRefreshEnable(true);// 设置下拉刷新列表

			String last_current_dreceptionistId = SharedPreferencesUtil.getStringData(
					getApplicationContext(),ZhiChiConstant.SOBOT_RECEPTIONISTID,"");
			info.setReceptionistId(last_current_dreceptionistId);
			customerInit();
		} else {
			//检查配置项是否发生变化
			if(ChatUtils.checkConfigChange(getApplicationContext(),info)){
				resetUser();
			} else {
				doKeepsessionInit();
			}
		}
	}

	/**
	 * 重置用户
	 */
	private void resetUser(){
		zhiChiApi.disconnChannel();
		clearCache();
		SharedPreferencesUtil.saveStringData(getApplicationContext(),
				ZhiChiConstant.sobot_last_login_group_id, TextUtils.isEmpty(info.getSkillSetId())?"":info.getSkillSetId());
		customerInit();
	}

	/**
	 * 会话保持初始化的逻辑
	 */
	private void doKeepsessionInit(){
		List<ZhiChiMessageBase> tmpList = SobotMsgManager.getInstance(getApplication()).getConfig().getMessageList();
		if(tmpList != null && SobotMsgManager.getInstance(getApplication()).getConfig().getInitModel() != null){
			//有数据
			int lastType =  SharedPreferencesUtil.getIntData(getApplicationContext(),
					ZhiChiConstant.initType, -1);
			if(info.getInitModeType() < 0 || lastType == info.getInitModeType()){
				if(!TextUtils.isEmpty(info.getSkillSetId())){
					//判断是否是上次的技能组
					String lastUseGroupId = SharedPreferencesUtil.getStringData(getApplicationContext(), ZhiChiConstant.sobot_last_login_group_id, "");
					if(lastUseGroupId.equals(info.getSkillSetId())){
						keepSession(tmpList);
					} else {
						resetUser();
					}
				}else{
					keepSession(tmpList);
				}
			}else{
				resetUser();
			}
		} else {
			resetUser();
		}
	}

	//保持会话
	private void keepSession(List<ZhiChiMessageBase> tmpList) {
		ZhiChiConfig config = SobotMsgManager.getInstance(getApplication()).getConfig();
		initModel =  config.getInitModel();
		updateFloatUnreadIcon();
		mUnreadNum = 0;
		messageAdapter.addData(tmpList);
		messageAdapter.notifyDataSetChanged();
		current_client_model = config.current_client_model;
		type = Integer.parseInt(initModel.getType());
		SharedPreferencesUtil.saveIntData(getApplicationContext(),
				ZhiChiConstant.initType, type);
		LogUtils.i("sobot----type---->" + type);
		initModel.setColor(info.getColor());
		showLogicTitle(config.activityTitle,false);
		customerState = config.customerState;
		remindRobotMessageTimes = config.remindRobotMessageTimes;
		isComment = config.isComment;
		isAboveZero = config.isAboveZero;
		currentUserName=config.currentUserName;
		isNoMoreHistoryMsg=config.isNoMoreHistoryMsg;
		currentCidPosition = config.currentCidPosition ;
		getCidsFinish = config.getCidsFinish ;
		if(config.cids != null ){
			cids.addAll(config.cids);
		}
		showTimeVisiableCustomBtn = config.showTimeVisiableCustomBtn;
		queueNum = config.queueNum;
		if(isNoMoreHistoryMsg){
			lv_message.setPullRefreshEnable(false);// 设置下拉刷新列表
		}
		setAdminFace(config.adminFace);
		setBottomView(config.bottomViewtype);
		if(config.userInfoTimeTask){
			stopUserInfoTimeTask();
			startUserInfoTimeTask(handler);
		}
		if(config.customTimeTask){
			stopCustomTimeTask();
			startCustomTimeTask(handler);
		}
		if(customerState == CustomerState.Online &&current_client_model == ZhiChiConstant.client_model_customService){
			createConsultingContent();
		}
		lv_message.setSelection(messageAdapter.getCount());
		config.clearMessageList();
		config.clearInitModel();
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		/* 获取当前手机品牌 过滤掉小米手机 */
		String phoneName = android.os.Build.MODEL.substring(0, 2);
		// LogUtils.i("当前手机品牌是" + phoneName + phoneName.length());
		// 模式的转化
		f_proximiny = event.values[0];
		// LogUtils.i("监听模式的转换：" + f_proximiny + " 听筒的模式："
		// + mProximiny.getMaximumRange());
		if (!phoneName.trim().equals("MI")) {
			if (f_proximiny != 0.0) {
				audioManager.setSpeakerphoneOn(true);// 打开扬声器
				audioManager.setMode(AudioManager.MODE_NORMAL);
				// LogUtils.i("监听模式的转换：" + "正常模式");
			} else {
				audioManager.setSpeakerphoneOn(false);// 关闭扬声器
				setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
				// 把声音设定成Earpiece（听筒）出来，设定为正在通话中
				audioManager.setMode(AudioManager.MODE_IN_CALL);
				// LogUtils.i("监听模式的转换：" + "听筒模式");
			}
		}
	}

	/**
	 * 显示机器人的布局
	 */
	private void showRobotLayout(){
		showLogicTitle(initModel.getRobotName(),false);
		if (type == 1) {
			//仅机器人
			setBottomView(ZhiChiConstant.bottomViewtype_onlyrobot);
			SobotMsgManager.getInstance(getApplication()).getConfig().bottomViewtype=ZhiChiConstant.bottomViewtype_onlyrobot;
		} else if(type == 3 || type == 4){
			//机器人优先
			setBottomView(ZhiChiConstant.bottomViewtype_robot);
			SobotMsgManager.getInstance(getApplication()).getConfig().bottomViewtype=ZhiChiConstant.bottomViewtype_robot;
		}
	}

	/**
	 * 转人工方法
     */
	public void connectCustomerService(String groupId,String groupName) {
		if(isConnCustomerService){
			return;
		}
		isConnCustomerService = true;
		boolean currentFlag = (customerState == CustomerState.Queuing || customerState == CustomerState.Online);
		zhiChiApi.connnect(info.getReceptionistId(),info.getTranReceptionistFlag(),initModel.getUid(), initModel.getCid(), groupId, groupName,currentFlag,
				new StringResultCallBack<ZhiChiMessageBase>() {
					@Override
					public void onSuccess(ZhiChiMessageBase zhichiMessageBase) {
						isConnCustomerService = false;
						int status = Integer.parseInt(zhichiMessageBase.getStatus());
						setAdminFace(zhichiMessageBase.getAface());
						LogUtils.i("status---:" + status);
						if (status != 0) {
							if (status == ZhiChiConstant.transfer_robot_customServeive){
								//机器人超时下线转人工
								customerServiceOffline(initModel,4);
							} else if (status == ZhiChiConstant.transfer_robot_custom_status){
								//如果设置指定客服的id。并且设置不是必须转入，服务器返回status=6.这个时候要设置receptionistId为null
								//为null以后继续转人工逻辑。如果技能组开启就弹技能组，如果技能组没有开启，就直接转人工
								showLogicTitle(initModel.getRobotName(),false);
								info.setReceptionistId(null);
								transfer2Custom();
							} else {
								if (ZhiChiConstant.transfer_customeServeive_success == status) {
									connCustomerServiceSuccess(zhichiMessageBase);
								} else if (ZhiChiConstant.transfer_customeServeive_fail == status) {
									connCustomerServiceFail();
								} else if (ZhiChiConstant.transfer_customeServeive_isBalk == status) {
									connCustomerServiceBlack();
								} else if (ZhiChiConstant.transfer_customeServeive_already == status) {
									connCustomerServiceSuccess(zhichiMessageBase);
								}
							}
						} else {
							LogUtils.i("转人工--排队");
							//开启通道
							zhiChiApi.connChannel(zhichiMessageBase.getWslinkBak(),
									zhichiMessageBase.getWslinkDefault(),initModel.getUid(),zhichiMessageBase.getPuid(),info.getAppkey());
							customerState = CustomerState.Queuing;
							createCustomerQueue(zhichiMessageBase.getCount()+"");
						}
					}

					@Override
					public void onFailure(Exception e, String des) {
						isConnCustomerService = false;
						if(type == 2 && SobotMsgManager.getInstance(getApplication()).getConfig().bottomViewtype == ZhiChiConstant.bottomViewtype_custom_only_msgclose){
							setBottomView(ZhiChiConstant.bottomViewtype_custom_only_msgclose);
							showLogicTitle(getResString("sobot_no_access"),false);
						}
//						ToastUtil.showToast(getApplicationContext(),des);
					}
				});
	}

	/**
	 * 重置输入预知
	 */
	private void restartInputListener(){
		stopInputListener();
		startInputListener();
	}

	/**
	 * 显示排队提醒
	 */
	private void showInLineHint(){
		// 更新界面的操作
		updateUiMessage(messageAdapter, ChatUtils.getInLineHint(getApplicationContext(),queueNum));
		gotoLastItem();
	}

	/**
	 * 获取技能组
	 */
	private void getGroupInfo() {
		zhiChiApi.getGroupList(info.getAppkey(),initModel.getUid(), new StringResultCallBack<ZhiChiGroup>() {
			@Override
			public void onSuccess(ZhiChiGroup zhiChiGroup) {
				boolean hasOnlineCustom = false;
				if (ZhiChiConstant.groupList_ustatus_time_out.equals(zhiChiGroup.getUstatus())){
					customerServiceOffline(initModel,4);
				} else {
					list_group = zhiChiGroup.getData();
					if (list_group != null && list_group.size() > 0) {
						for (int i = 0; i < list_group.size(); i++) {
							if (list_group.get(i).isOnline().equals("true")) {
								hasOnlineCustom = true;
								break;
							}
						}
						if (hasOnlineCustom) {
							if (list_group.size() >= 2) {
								if (initModel.getUstatus() == ZhiChiConstant.ustatus_online || initModel.getUstatus() == ZhiChiConstant.ustatus_queue) {
									// 会话保持直接转人工
									connectCustomerService("","");
								} else {
									if (!TextUtils.isEmpty(info.getSkillSetId())) {
										//指定技能组
										transfer2CustomBySkillId();
									} else {
										Intent intent = new Intent(SobotChatActivity.this, SobotSkillGroupActivity.class);
										intent.putExtra("grouplist", (Serializable) list_group);
										intent.putExtra("uid", initModel.getUid());
										intent.putExtra("type", type);
										intent.putExtra("appkey", info.getAppkey());
										intent.putExtra("companyId", initModel.getCompanyId());
										intent.putExtra("msgTmp", initModel.getMsgTmp());
										intent.putExtra("msgTxt", initModel.getMsgTxt());
										intent.putExtra("msgFlag", initModel.getMsgFlag());
										startActivityForResult(intent, ZhiChiConstant.REQUEST_COCE_TO_GRROUP);
									}
								}
							} else {
								//只有一个技能组
								connectCustomerService(list_group.get(0).getGroupId(),list_group.get(0).getGroupName());
							}
						} else {
							//技能组没有客服在线
							connectCustomerService("","");
						}
					} else {
						//没有设置技能组
						connectCustomerService("","");
					}
				}
			}

			@Override
			public void onFailure(Exception e, String des) {}
		});
	}

	public void startToPostMsgActivty(boolean FLAG_EXIT_SDK) {
		if (initModel == null){
			return;
		}
		Intent intent = new Intent(getApplicationContext(), SobotPostMsgActivity.class);
		intent.putExtra("uid", initModel.getUid());
		intent.putExtra("companyId", initModel.getCompanyId());
		intent.putExtra(ZhiChiConstant.FLAG_EXIT_SDK, FLAG_EXIT_SDK);
		intent.putExtra("msgTmp", initModel.getMsgTmp());
		intent.putExtra("msgTxt", initModel.getMsgTxt());
		startActivity(intent);
		overridePendingTransition(ResourceUtils.getIdByName(getApplicationContext(), "anim", "push_left_in"),
				ResourceUtils.getIdByName(getApplicationContext(), "anim", "push_left_out"));
	}

	/*发送0、机器人问答 1、文本  2、语音  3、图片*/
	public void sendMessageToRobot(ZhiChiMessageBase base,int type, int questionFlag, String docId){

		/*图片消息*/
		if(type == 3){
			// 根据图片的url 上传图片 更新上传图片的进度
			messageAdapter.updatePicStatusById(base.getId(), base.getMysendMessageState());
			messageAdapter.notifyDataSetChanged();
			ChatUtils.sendPicture(getApplicationContext(),initModel.getCid(), initModel.getUid(),
					base.getContent(), handler, base.getId(), lv_message,messageAdapter);
		}

		/*语音消息*/
		else if (type == 2){
			// 语音的重新上传
			sendVoiceMessageToHandler(base.getId(),  base.getContent(), base.getAnswer()
					.getDuration(), 2, UPDATE_VOICE, handler);
			sendVoice(base.getId(), base.getAnswer().getDuration(), initModel.getCid(),
					initModel.getUid(), base.getContent(), handler);
		}

		/*文本消息*/
		else if (type == 1){
			// 消息的转换
			sendTextMessageToHandler(base.getId(), base.getContent(), handler, 2, true);
			ZhiChiReplyAnswer answer = new ZhiChiReplyAnswer();
			answer.setMsgType(ZhiChiConstant.message_type_text + "");
			answer.setMsg(base.getContent());
			base.setAnswer(answer);
			base.setSenderType(ZhiChiConstant.message_sender_type_customer + "");
			sendMessageWithLogic(base.getId(), base.getContent(), initModel, handler, current_client_model, questionFlag, docId);
		}

		/*机器人问答*/
		else if (type == 0){

			if(!isSessionOver){
				// 消息的转换
				ZhiChiReplyAnswer answer = new ZhiChiReplyAnswer();
				answer.setMsgType(ZhiChiConstant.message_type_text + "");
				answer.setMsg(base.getContent());
				base.setAnswer(answer);
				base.setSenderType(ZhiChiConstant.message_sender_type_customer + "");
				if (base.getId() == null || TextUtils.isEmpty(base.getId())) {
					updateUiMessage(messageAdapter, base);
				}
				sendMessageWithLogic(base.getId(), base.getContent(), initModel, handler, current_client_model,  questionFlag, docId);
			}else{
				showOutlineTip(initModel,1);
			}
		}
		gotoLastItem();
	}

	/**
	 * 设置底部键盘UI
	 * @param viewType
     */
	public void setBottomView(int viewType){
		welcome.setVisibility(View.GONE);
		loading_anim_view.stopGifView();
		relative.setVisibility(View.VISIBLE);
		chat_main.setVisibility(View.VISIBLE);
		et_sendmessage.setVisibility(View.VISIBLE);
		sobot_ll_restart_talk.setVisibility(View.GONE);
		sobot_ll_bottom.setVisibility(View.VISIBLE);

		btn_press_to_speak.setVisibility(View.GONE);
		edittext_layout.setVisibility(View.VISIBLE);
		hideReLoading();
		if (isUserBlack()) {
			sobot_ll_restart_talk.setVisibility(View.GONE);
			sobot_ll_bottom.setVisibility(View.VISIBLE);
			btn_model_voice.setVisibility(View.GONE);
			btn_emoticon_view.setVisibility(View.GONE);
		}
		sobot_tv_satisfaction.setVisibility(View.VISIBLE);
		sobot_txt_restart_talk.setVisibility(View.VISIBLE);
		sobot_tv_message.setVisibility(View.VISIBLE);

		LogUtils.i("setBottomView:"+viewType);
		switch(viewType){
			case ZhiChiConstant.bottomViewtype_onlyrobot:
				// 仅机器人
				if (image_reLoading.getVisibility() == View.VISIBLE) {
					sobot_ll_bottom.setVisibility(View.VISIBLE);/* 底部聊天布局 */
					edittext_layout.setVisibility(View.VISIBLE);/* 文本输入框布局 */
					btn_model_voice.setVisibility(View.GONE);
					sobot_ll_restart_talk.setVisibility(View.GONE);

					if (btn_press_to_speak.getVisibility() == View.VISIBLE) {
						btn_press_to_speak.setVisibility(View.GONE);
					}
					btn_set_mode_rengong.setClickable(false);
					btn_set_mode_rengong.setVisibility(View.GONE);
				}
				btn_emoticon_view.setVisibility(View.GONE);
				btn_upload_view.setVisibility(View.VISIBLE);
				break;
			case ZhiChiConstant.bottomViewtype_robot:
				//机器人对话框
				if (info.isArtificialIntelligence() && type == ZhiChiConstant.type_robot_first){
					//智能转人工只适用于机器人优先
					if (showTimeVisiableCustomBtn >= info.getArtificialIntelligenceNum()){
						btn_set_mode_rengong.setVisibility(View.VISIBLE);
					} else {
						btn_set_mode_rengong.setVisibility(View.GONE);
					}
				} else {
					btn_set_mode_rengong.setVisibility(View.VISIBLE);
				}

				btn_set_mode_rengong.setClickable(true);
				if (Build.VERSION.SDK_INT >= 11)
					btn_set_mode_rengong.setAlpha(1f);
				if (image_reLoading.getVisibility() == View.VISIBLE) {
					sobot_ll_bottom.setVisibility(View.VISIBLE);/* 底部聊天布局 */
					edittext_layout.setVisibility(View.VISIBLE);/* 文本输入框布局 */
					btn_model_voice.setVisibility(View.GONE);
					sobot_ll_restart_talk.setVisibility(View.GONE);

					if (btn_press_to_speak.getVisibility() == View.VISIBLE) {
						btn_press_to_speak.setVisibility(View.GONE);
					}
					btn_set_mode_rengong.setClickable(true);
					btn_set_mode_rengong.setEnabled(true);
				}
				btn_upload_view.setVisibility(View.VISIBLE);
				btn_emoticon_view.setVisibility(View.GONE);
				break;
			case ZhiChiConstant.bottomViewtype_customer:
				//人工对话框
				btn_set_mode_rengong.setVisibility(View.GONE);
				btn_upload_view.setVisibility(View.VISIBLE);
				showEmotionBtn();
                btn_model_voice.setVisibility(info.isUseVoice()?View.VISIBLE:View.GONE);
				btn_model_voice.setEnabled(true);
				btn_model_voice.setClickable(true);
				btn_upload_view.setEnabled(true);
				btn_upload_view.setClickable(true);
				btn_emoticon_view.setClickable(true);
				btn_emoticon_view.setEnabled(true);
				if (Build.VERSION.SDK_INT >= 11){
					btn_model_voice.setAlpha(1f);
					btn_upload_view.setAlpha(1f);
				}

				edittext_layout.setVisibility(View.VISIBLE);
				sobot_ll_bottom.setVisibility(View.VISIBLE);
				btn_press_to_speak.setVisibility(View.GONE);
				btn_press_to_speak.setClickable(true);
				btn_press_to_speak.setEnabled(true);
				txt_speak_content.setText(getResString("sobot_press_say"));
				break;
			case ZhiChiConstant.bottomViewtype_onlycustomer_paidui:
				//仅人工排队中
				onlyCustomPaidui();

				hidePanelAndKeyboard(mPanelRoot);
				if(lv_message.getLastVisiblePosition()!=messageAdapter.getCount()){
					lv_message.setSelection(messageAdapter.getCount());
				}
				break;
			case ZhiChiConstant.bottomViewtype_outline:
				//被提出
				hideReLoading();
				hidePanelAndKeyboard(mPanelRoot);/*隐藏键盘*/
				sobot_ll_bottom.setVisibility(View.GONE);
				sobot_ll_restart_talk.setVisibility(View.VISIBLE);
				sobot_tv_satisfaction.setVisibility(View.VISIBLE);
				sobot_txt_restart_talk.setVisibility(View.VISIBLE);
				btn_model_edit.setVisibility(View.GONE);
				sobot_tv_message.setVisibility(initModel.getMsgFlag() == ZhiChiConstant.sobot_msg_flag_close?View
						.GONE:View.VISIBLE);
				btn_model_voice.setVisibility(View.GONE);
				lv_message.setSelection(messageAdapter.getCount());
				break;
			case ZhiChiConstant.bottomViewtype_paidui:
				//智能模式下排队中
				btn_set_mode_rengong.setVisibility(View.VISIBLE);
				btn_emoticon_view.setVisibility(View.GONE);
				if (image_reLoading.getVisibility() == View.VISIBLE) {
					sobot_ll_bottom.setVisibility(View.VISIBLE);/* 底部聊天布局 */
					edittext_layout.setVisibility(View.VISIBLE);/* 文本输入框布局 */
					btn_model_voice.setVisibility(View.GONE);
					sobot_ll_restart_talk.setVisibility(View.GONE);

					if (btn_press_to_speak.getVisibility() == View.VISIBLE) {
						btn_press_to_speak.setVisibility(View.GONE);
					}
				}
				break;
			case ZhiChiConstant.bottomViewtype_custom_only_msgclose:
				sobot_ll_restart_talk.setVisibility(View.VISIBLE);

				sobot_ll_bottom.setVisibility(View.GONE);
				if (image_reLoading.getVisibility() == View.VISIBLE){
					sobot_txt_restart_talk.setVisibility(View.VISIBLE);
					sobot_txt_restart_talk.setClickable(true);
					sobot_txt_restart_talk.setEnabled(true);
				}
				if (initModel.getMsgFlag() == ZhiChiConstant.sobot_msg_flag_close){
					//留言关闭
					sobot_tv_satisfaction.setVisibility(View.INVISIBLE);
					sobot_tv_message.setVisibility(View.INVISIBLE);
				} else {
					sobot_tv_satisfaction.setVisibility(View.GONE);
					sobot_tv_message.setVisibility(View.VISIBLE);
				}
				break;
		}
	}

	/**
	 * 机器人智能转人工时，判断是否应该显示转人工按钮
	 */
	private void showTransferCustomer(){
		showTimeVisiableCustomBtn++;
		SobotMsgManager.getInstance(getApplication()).getConfig().showTimeVisiableCustomBtn++;
		if (showTimeVisiableCustomBtn >= info.getArtificialIntelligenceNum()){
			btn_set_mode_rengong.setVisibility(View.VISIBLE);
		}
	}

	//开启正在输入的监听
	private void startInputListener(){
		inputtingListener = new Timer();
		inputTimerTask = new TimerTask() {
			@Override
			public void run() {
				//人工模式并且没有发送的时候
				if(customerState == CustomerState.Online && current_client_model == ZhiChiConstant.client_model_customService && !isSendInput){
					//获取对话
					String content = et_sendmessage.getText().toString().trim();
					if(!TextUtils.isEmpty(content) && !content.equals(lastInputStr)){
						lastInputStr = content;
						isSendInput = true;
						//发送接口
						zhiChiApi.input(initModel.getUid(), content, new StringResultCallBack<CommonModel>() {
							@Override
							public void onSuccess(CommonModel result) { isSendInput = false; }

							@Override
							public void onFailure(Exception e, String des) { isSendInput = false; }
						});
					}
				}
			}
		};
		// 500ms进行定时任务
		inputtingListener.schedule(inputTimerTask, 0, initModel.getInputTime() * 1000);
	}

	private void stopInputListener(){
		if(inputtingListener != null){
			inputtingListener.cancel();
			inputtingListener = null;
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		isActivityStart = true;
		if (isNeedShowEvaluate){
			if (isAboveZero && !isComment) {
				// 满足评价条件，并且之前没有评价过的话 才能 弹评价框
				ChatUtils.showEvaluateDialog(SobotChatActivity.this,false,initModel,current_client_model,0);
			}
			isNeedShowEvaluate = false;
		}

		if(initModel != null && customerState == CustomerState.Online && current_client_model == ZhiChiConstant
				.client_model_customService){
			restartInputListener();
		}
		NotificationUtils.cancleAllNotification(getApplicationContext());
		//重新恢复连接
		if(customerState == CustomerState.Online || customerState == CustomerState.Queuing){
			zhiChiApi.reconnectChannel();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		isActivityStart = false;
		stopInputListener();
	}

	/**
	 * 仅人工的无客服在线的逻辑
	 */
	private void showLeaveMsg(){
        LogUtils.i("仅人工，无客服在线");
		showLogicTitle(getResString("sobot_no_access"),false);
        setBottomView(ZhiChiConstant.bottomViewtype_custom_only_msgclose);
		SobotMsgManager.getInstance(getApplication()).getConfig().bottomViewtype = ZhiChiConstant.bottomViewtype_custom_only_msgclose;
		if (isUserBlack()){
			showCustomerUanbleTip();
		} else {
			showCustomerOfflineTip();
		}
		isSessionOver = true;
	}

	/**
	 * 输入表情的方法
	 * @param item
     */
	public void inputEmoticon(Emojicon item){
		InputHelper.input2OSC(et_sendmessage,item);
	}

	/**
	 * 输入框删除的方法
	 */
	public void backspace(){
		InputHelper.backspace(et_sendmessage);
	}

	/**
	 * 切换表情按钮焦点
     */
	public void switchEmoticonBtn(){
		boolean flag = btn_emoticon_view.isSelected();
		if(flag){
			doEmoticonBtn2Blur();
		}else{
			doEmoticonBtn2Focus();
		}
	}

	/**
	 * 使表情按钮获取焦点
	 */
	public void doEmoticonBtn2Focus(){
		btn_emoticon_view.setSelected(true);
	}

	/**
	 * 使表情按钮失去焦点
	 */
	public void doEmoticonBtn2Blur(){
		btn_emoticon_view.setSelected(false);
	}

	/**
	 * 隐藏重新开始会话的菊花
	 */
	public void hideReLoading(){
		image_reLoading.clearAnimation();
		image_reLoading.setVisibility(View.GONE);
	}

	/**
	 * 重置表情按钮的焦点键盘
	 */
	public void resetEmoticonBtn(){
		String panelViewTag = getPanelViewTag(mPanelRoot);
		String instanceTag = CustomeViewFactory.getInstanceTag(getApplicationContext(), btn_emoticon_view.getId());
		if(mPanelRoot.getVisibility() == View.VISIBLE && instanceTag.equals(panelViewTag)){
			doEmoticonBtn2Focus();
		}else{
			doEmoticonBtn2Blur();
		}
	}

	protected void onSaveInstanceState(Bundle outState) {
		//销毁前缓存数据
		outState.putBundle("informationBundle",informationBundle);
		super.onSaveInstanceState(outState);
	}

	/**
	 * 获取客户传入的技能组id 直接转人工
	 */
	private void transfer2CustomBySkillId(){
		connectCustomerService(info.getSkillSetId(),info.getSkillSetName());
	}

	/**
	 * 显示表情按钮   如果没有表情资源则不会显示此按钮
	 */
	private void showEmotionBtn(){
		Map<String, Integer> mapAll = DisplayRules.getMapAll(getApplicationContext());
		if(mapAll.size() > 0){
			btn_emoticon_view.setVisibility(View.VISIBLE);
		} else {
			btn_emoticon_view.setVisibility(View.GONE);
		}
	}

	/**
	 * 转人工按钮的逻辑封装
	 * 如果用户传入了skillId 那么就用这个id直接转人工
	 * 如果没有传  那么就检查技能组开关是否打开
	 */
	private void transfer2Custom(){
		if(isUserBlack()){
			connectCustomerService("","");
		} else if (!TextUtils.isEmpty(info.getSkillSetId())){
			//预设技能组转人工
			transfer2CustomBySkillId();
		} else {
			if (initModel.getGroupflag().equals(
					ZhiChiConstant.groupflag_on) && TextUtils.isEmpty(info.getReceptionistId())) {
				//如果技能组开启，此时没有指定客服，那么拉取技能组数据
				getGroupInfo();
			} else {
				//没有预设技能组，技能组关闭  直接转人工
				connectCustomerService("","");
			}
		}
	}

	private void gotoLastItem(){
		handler.post(new Runnable() {
			@Override
			public void run() {
				lv_message.setSelection(messageAdapter.getCount());
			}
		});
	}

	private void deleteUnReadUi(){
		//清除“以下为未读消息”
		for (int i = messageList.size() - 1; i >= 0; i--) {
			if (messageList.get(i).getAnswer() != null && ZhiChiConstant
					.sobot_remind_type_below_unread == messageList.get(i).getAnswer().getRemindType()) {
				messageList.remove(i);
				break;
			}
		}
	}

	/**
	 * 获取未读消息
	 */
	private void loadUnreadNum(){
		mUnreadNum = SharedPreferencesUtil.getIntData(getApplicationContext(), "sobot_unread_count",0);
		SharedPreferencesUtil.saveIntData(getApplicationContext(),"sobot_unread_count",0);
	}

	/**
	 * 根据未读消息数更新右上角UI  “XX条未读消息”
	 */
	private void updateFloatUnreadIcon(){
		if (mUnreadNum >= 10){
			notReadInfo.setVisibility(View.VISIBLE);
			notReadInfo.setText(mUnreadNum + getResString("sobot_new_msg"));
		} else {
			notReadInfo.setVisibility(View.GONE);
		}
	}

	/**
	 * 根据逻辑判断显示当前的title
	 * 根据客服传入的title显示模式显示聊天页面的标题
	 * @param title 此处传如的值为默认需要显示的昵称 或者提示等等
	 * @param ignoreLogic 表示忽略逻辑直接显示
	 */
	private void showLogicTitle(String title,boolean ignoreLogic){
		String str = ChatUtils.getLogicTitle(getApplicationContext(),ignoreLogic, title, initModel.getCompanyName());
		if(!TextUtils.isEmpty(str)){
			setTitle(str);
		}
	}

	/**
	 * 初始化查询cid的列表
	 */
	private void queryCids() {
		//如果initmodel 或者  querycid的接口已经完成了 那么就不再重复查了
		if(initModel == null || getCidsFinish){
			return;
		}
		long time = SharedPreferencesUtil.getLongData(getApplicationContext(), ZhiChiConstant.SOBOT_CHAT_HIDE_HISTORYMSG_TIME, 0);
		getCidsFinish = false;
		// 初始化查询cid的列表
		zhiChiApi.queryCids(initModel.getUid(),time, new StringResultCallBack<ZhiChiCidsModel>() {

			@Override
			public void onSuccess(ZhiChiCidsModel data) {
				getCidsFinish = true;
				cids = data.getCids();
				if(cids != null){
					boolean hasRepeat = false;
					for (int i = 0; i < cids.size(); i++) {
						if(cids.get(i).equals(initModel.getCid())){
							hasRepeat = true;
							break;
						}
					}
					if(!hasRepeat){
						cids.add(initModel.getCid());
					}
					Collections.reverse(cids);
				}
			}

			@Override
			public void onFailure(Exception e, String des) {
				getCidsFinish = true;
			}
		});
	}

	private void showInitError(){
		setTitle(getResString("sobot_prompt"));
		loading_anim_view.setVisibility(View.GONE);
		txt_loading.setVisibility(View.GONE);
		textReConnect.setVisibility(View.VISIBLE);
		icon_nonet.setVisibility(View.VISIBLE);
		btn_reconnect.setVisibility(View.VISIBLE);
		et_sendmessage.setVisibility(View.GONE);
		relative.setVisibility(View.GONE);
		welcome.setVisibility(View.VISIBLE);
	}

	// 点击播放录音及动画
	public void clickAudioItem(ZhiChiMessageBase message) {
		if(mAudioPlayPresenter == null){
			mAudioPlayPresenter = new AudioPlayPresenter(SobotChatActivity.this);
		}
		if (mAudioPlayCallBack == null) {
			mAudioPlayCallBack = new AudioPlayCallBack() {
				@Override
				public void onPlayStart(ZhiChiMessageBase mCurrentMsg) {
					showVoiceAnim(mCurrentMsg,true);
				}

				@Override
				public void onPlayEnd(ZhiChiMessageBase mCurrentMsg) {
					showVoiceAnim(mCurrentMsg,false);
				}
			};
		}
		mAudioPlayPresenter.clickAudio(message,mAudioPlayCallBack);
	}

	public void showVoiceAnim(final ZhiChiMessageBase info, final boolean isShow) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (info == null) {
					return;
				}
				for (int i = 0, count = lv_message.getChildCount(); i < count; i++) {
					View child = lv_message.getChildAt(i);
					if (child == null || child.getTag() == null || !(child.getTag() instanceof VoiceMessageHolder)) {
						continue;
					}
					VoiceMessageHolder holder = (VoiceMessageHolder) child.getTag();
					holder.stopAnim();
					if (holder.message == info) {
						if (isShow) {
							holder.startAnim();
						}
					}
				}
			}
		});
	}

	/**
	 * 调用顶踩接口
	 * @param revaluateFlag true 顶  false 踩
	 * @param message 顶踩用的 model
	 */
	public void doRevaluate(final boolean revaluateFlag,final ZhiChiMessageBase message){
		if(isSessionOver){
			showOutlineTip(initModel,1);
			return;
		}
		zhiChiApi.rbAnswerComment(initModel.getUid(), initModel.getCid(), initModel.getCurrentRobotFlag(),
				message.getDocId(), message.getDocName(), revaluateFlag, new StringResultCallBack<CommonModelBase>() {
					@Override
					public void onSuccess(CommonModelBase data) {
						if (ZhiChiConstant.client_sendmsg_to_custom_fali.equals(data.getStatus())) {
							customerServiceOffline(initModel,1);
						} else if(ZhiChiConstant.client_sendmsg_to_custom_success.equals(data.getStatus())){
							//改变顶踩按钮的布局
							message.setRevaluateState(revaluateFlag?2:3);
							resetRevaluateBtn(message);
						}
					}

					@Override
					public void onFailure(Exception e, String des) {
						ToastUtil.showToast(getApplicationContext(),"网络错误");
					}
				});
	}

	/**
	 * 单项更新 顶踩 按钮
	 * @param message
	 */
	private void resetRevaluateBtn(final ZhiChiMessageBase message){
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (message == null) {
					return;
				}
				for (int i = 0, count = lv_message.getChildCount(); i < count; i++) {
					View child = lv_message.getChildAt(i);
					if (child == null || child.getTag() == null || !(child.getTag() instanceof RichTextMessageHolder)) {
						continue;
					}
					RichTextMessageHolder holder = (RichTextMessageHolder) child.getTag();
					holder.resetRevaluateBtn();
				}
			}
		});
	}
}