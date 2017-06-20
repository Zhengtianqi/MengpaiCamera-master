package com.sobot.chat.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.chat.activity.base.SobotBaseActivity;
import com.sobot.chat.adapter.base.SobotPicListAdapter;
import com.sobot.chat.api.ResultCallBack;
import com.sobot.chat.api.model.CommonModelBase;
import com.sobot.chat.api.model.ZhiChiMessage;
import com.sobot.chat.api.model.ZhiChiUploadAppFileModelResult;
import com.sobot.chat.application.MyApplication;
import com.sobot.chat.core.http.callback.StringResultCallBack;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.EmojiFilter;
import com.sobot.chat.utils.HtmlTools;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ScreenUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.widget.ThankDialog;
import com.sobot.chat.widget.dialog.SobotDialogUtils;
import com.sobot.chat.widget.dialog.SobotSelectPicDialog;
import com.sobot.chat.widget.kpswitch.util.KeyboardUtil;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("HandlerLeak")
public class SobotPostMsgActivity extends SobotBaseActivity implements
		OnClickListener,View.OnTouchListener {

	private EditText sobot_et_email,sobot_et_content,sobot_leavemsg_nikename,sobot_leavemsg_phone;
	private TextView sobot_tv_post_msg1,sobot_enclosure_hint;
	private ImageView sobot_img_clear_nikename,sobot_img_clear_email,sobot_img_clear_phone;
	private View sobot_frist_line,sobot_second_line;
	private GridView sobot_post_msg_pic;
	private LinearLayout sobot_enclosure_container;
	private List<ZhiChiUploadAppFileModelResult> pic_list = new ArrayList<>();
	private SobotPicListAdapter adapter;
	private SobotSelectPicDialog menuWindow;

//	private ZhiChiApiImpl zhiChiApi;
	private RelativeLayout sobot_post_msg_layout;
	private String uid = "";
	private String companyId = "";
	private String msgTmp = "";
	private String msgTxt = "";
	private boolean flag_exit_sdk,isShowNikeNameTv ,isShowNikeName,
			isEmailFocus = false,isPhoneFocus = false,isNameFocus = false;
	private boolean telFlag;
	private boolean telShowFlag;
	private boolean emailFlag;
	private boolean emailShowFlag;
	private boolean enclosureShowFlag;
	private boolean enclosureFlag;
	private String ticketStartWay;

	private int flag_exit_type = -1;
	private ThankDialog d;

	public Handler handler = new Handler() {
		public void handleMessage(final android.os.Message msg) {
			switch (msg.what) {
			case 1:
				if (flag_exit_type == 1) {
					finishPageOrSDK(true);
				} else if (flag_exit_type == 2) {
					setResult(200);
					finishPageOrSDK(false);
				} else {
					finishPageOrSDK(flag_exit_sdk);
				}
				break;
			}
		}
	};

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(ResourceUtils.getIdByName(this, "layout", "sobot_activity_post_msg"));
//		zhiChiApi = new ZhiChiApiImpl(getApplicationContext());
		initDate();
		getMySharedPreferences();
		initMyView();
		setListener();
		initBundleData(savedInstanceState);
		msgFilter();
		editTextSetHint();
	}

	private void initBundleData(Bundle savedInstanceState) {
		if(savedInstanceState == null){
			if (getIntent() != null) {
				uid = getIntent().getStringExtra("uid");
				companyId = getIntent().getStringExtra("companyId");
				flag_exit_type = getIntent().getIntExtra(
						ZhiChiConstant.FLAG_EXIT_TYPE, -1);
				flag_exit_sdk = getIntent().getBooleanExtra(
						ZhiChiConstant.FLAG_EXIT_SDK, false);
				msgTmp = getIntent().getStringExtra("msgTmp").replaceAll("\n",
						"<br/>");
				msgTxt = getIntent().getStringExtra("msgTxt").replaceAll("\n",
						"<br/>");
			}
		} else {
			uid = savedInstanceState.getString("uid");
			companyId = savedInstanceState.getString("companyId");
			flag_exit_type = savedInstanceState.getInt(ZhiChiConstant.FLAG_EXIT_TYPE, -1);
			flag_exit_sdk = savedInstanceState.getBoolean(ZhiChiConstant.FLAG_EXIT_SDK, false);

			msgTmp = savedInstanceState.getString("msgTmp");
			msgTxt = savedInstanceState.getString("msgTxt");
			if(!TextUtils.isEmpty(msgTmp)){
				msgTmp = msgTmp.replaceAll("\n", "<br/>");
			}

			if(!TextUtils.isEmpty(msgTxt)){
				msgTxt = msgTxt.replaceAll("\n", "<br/>");
			}
		}
	}

	@Override
	public void forwordMethod() {
		postMsg();
	}

	@SuppressWarnings("deprecation")
	public void showHint(String content, final boolean flag) {
		if(!isFinishing()) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(sobot_et_email.getWindowToken(), 0); // 强制隐藏键盘
			imm.hideSoftInputFromWindow(sobot_et_content.getWindowToken(), 0); // 强制隐藏键盘
			if(d != null){
				d.dismiss();
			}
			ThankDialog.Builder customBuilder = new ThankDialog.Builder(
					SobotPostMsgActivity.this);
			customBuilder.setMessage(content);
            d = customBuilder.create();
			d.show();

			WindowManager.LayoutParams lp = d.getWindow().getAttributes();
			float dpToPixel = ScreenUtils.dpToPixel(
					getApplicationContext(), 1);
			lp.width = (int) (dpToPixel * 200); // 设置宽度
			d.getWindow().setAttributes(lp);
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if(!isFinishing()) {
						if(d != null){
							d.dismiss();
						}
						if (flag) {
							handler.sendEmptyMessage(1);
						}
					}
				}
			},2000);
		}
	}

	@Override
	public void onClick(View view) {
		if (view == sobot_tv_left) {
			if (flag_exit_type == 1 || flag_exit_type == 2) {
				finishPageOrSDK(false);
			} else {
				finishPageOrSDK(flag_exit_sdk);
			}
		}

		if (view == sobot_img_clear_nikename){
			sobot_leavemsg_nikename.setText("");
			sobot_img_clear_nikename.setVisibility(View.GONE);
		}

		if (view == sobot_img_clear_email){
			sobot_et_email.setText("");
			sobot_img_clear_email.setVisibility(View.GONE);
		}

		if (view == sobot_img_clear_phone){
			sobot_leavemsg_phone.setText("");
			sobot_img_clear_phone.setVisibility(View.GONE);
		}
	}

	@Override
	public void onBackPressed() {
		if (flag_exit_type == 1 || flag_exit_type == 2) {
			finishPageOrSDK(false);
		} else {
			finishPageOrSDK(flag_exit_sdk);
		}
	}

	private void postMsg() {
		String userName = "",userPhone = "", userEamil = "";

		if (emailFlag){
			if (!TextUtils.isEmpty(sobot_et_email.getText().toString().trim())
					&& ScreenUtils.isEmail(sobot_et_email.getText().toString().trim())){
				userEamil = sobot_et_email.getText().toString().trim();
			} else {
				showHint(getResString("sobot_email_dialog_hint"), false);
				return;
			}
		}

		if (telFlag){
			if (!TextUtils.isEmpty(sobot_leavemsg_phone.getText().toString().trim())
					&& ScreenUtils.isMobileNO(sobot_leavemsg_phone.getText().toString().trim())){
				userPhone = sobot_leavemsg_phone.getText().toString();
			} else {
				showHint(getResString("sobot_phone_dialog_hint"), false);
				return;
			}
		} else {
			if (!TextUtils.isEmpty(sobot_leavemsg_phone.getText().toString().trim())
					&& ScreenUtils.isMobileNO(sobot_leavemsg_phone.getText().toString().trim())){
				userPhone = sobot_leavemsg_phone.getText().toString();
			} else {
				showHint(getResString("sobot_phone_dialog_hint"), false);
				return;
			}
		}

		if (isShowNikeNameTv) {
			userName = EmojiFilter.removeNonBmpUnicode(sobot_leavemsg_nikename.getText().toString());
			if (TextUtils.isEmpty(userName)){
				if (isShowNikeName){
					sobot_leavemsg_nikename.setText("");
					return;
				} else {
					sobot_leavemsg_nikename.setText(userName);
				}
			}
		}

		zhiChiApi.postMsg(uid, sobot_et_content.getText().toString(),
				userEamil, userPhone, companyId,userName,getFileStr(),
				new StringResultCallBack<CommonModelBase>() {
					@Override
					public void onSuccess(CommonModelBase base) {
						if (Integer.parseInt(base.getStatus()) == 0){
							showHint(base.getMsg(),false);
						} else if (Integer.parseInt(base.getStatus()) == 1){
							showHint(getResString("sobot_leavemsg_success_hint"),true);
						}
					}

					@Override
					public void onFailure(Exception e, String des) {}
				});
	}

	private void setListener() {
		sobot_img_clear_nikename.setOnClickListener(this);
		sobot_img_clear_email.setOnClickListener(this);
		sobot_img_clear_phone.setOnClickListener(this);
		sobot_post_msg_layout.setOnTouchListener(this);

		sobot_leavemsg_nikename.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				isNameFocus = hasFocus;
				if (hasFocus) {
					if (sobot_leavemsg_nikename.getText().toString().trim().length() != 0) {
						sobot_img_clear_nikename.setVisibility(View.VISIBLE);
					}
				} else {
					sobot_img_clear_nikename.setVisibility(View.GONE);
				}
			}
		});

		sobot_leavemsg_phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				isPhoneFocus = hasFocus;
				if (hasFocus) {
					if (sobot_leavemsg_phone.getText().toString().trim().length() != 0) {
						sobot_img_clear_phone.setVisibility(View.VISIBLE);
					}
				} else {
					sobot_img_clear_phone.setVisibility(View.GONE);
				}
			}
		});

		sobot_et_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				isEmailFocus = hasFocus;
				if (hasFocus) {
					if (sobot_et_email.getText().toString().trim().length() != 0) {
						sobot_img_clear_email.setVisibility(View.VISIBLE);
					}
				} else {
					sobot_img_clear_email.setVisibility(View.GONE);
				}
			}
		});

		sobot_leavemsg_nikename.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
									  int arg3) {
				check();
				if (!TextUtils.isEmpty(arg0.toString()) && EmojiFilter.hasEmojiStr(arg0.toString())){
					sobot_leavemsg_nikename.setText(EmojiFilter.removeNonBmpUnicode(arg0.toString()));
					sobot_leavemsg_nikename.setSelection(arg1);
				}

				if (isNameFocus && arg0.toString().length() != 0){
					sobot_img_clear_nikename.setVisibility(View.VISIBLE);
				} else {
					sobot_img_clear_nikename.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
										  int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				check();
			}
		});

		sobot_leavemsg_phone.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
									  int arg3) {
				check();
				if (isPhoneFocus && arg0.toString().length() != 0){
					sobot_img_clear_phone.setVisibility(View.VISIBLE);
				} else {
					sobot_img_clear_phone.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
										  int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				check();
			}
		});

		sobot_et_email.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				check();
				if (isEmailFocus && arg0.toString().length() != 0){
					sobot_img_clear_email.setVisibility(View.VISIBLE);
				} else {
					sobot_img_clear_email.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				check();
			}
		});

		sobot_et_content.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				check();
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				check();
			}
		});
	}

	private boolean checkInput(){
		if (emailFlag){
			if (TextUtils.isEmpty(sobot_et_email.getText().toString().trim())
					|| !ScreenUtils.isEmail(sobot_et_email.getText().toString().trim())){
				return false;
			}
		}

		if (telFlag){
			if (TextUtils.isEmpty(sobot_leavemsg_phone.getText().toString().trim())
					||!ScreenUtils.isMobileNO(sobot_leavemsg_phone.getText().toString().trim())){
				return false;
			}
		}

		if (isShowNikeName){
			if (TextUtils.isEmpty(sobot_leavemsg_nikename.getText().toString().trim())){
				return false;
			}
		}

		if(enclosureShowFlag && enclosureFlag){
			if (TextUtils.isEmpty(getFileStr())){
				return false;
			}
		}

		return true;
	}

	private void check() {
		if (checkInput()){
			if (Build.VERSION.SDK_INT >= 11){
				sobot_tv_right.setAlpha(1f);
			}
			sobot_tv_right.setClickable(true);
		} else {
			if (Build.VERSION.SDK_INT >= 11){
				sobot_tv_right.setAlpha(0.5f);
			}
			sobot_tv_right.setClickable(false);
		}
	}

	private void finishPageOrSDK(boolean flag) {
		if (!flag) {
			finish();
			overridePendingTransition(ResourceUtils.getIdByName(
					getApplicationContext(), "anim", "push_right_in"),
					ResourceUtils.getIdByName(getApplicationContext(), "anim",
							"push_right_out"));
		} else {
			MyApplication.getInstance().exit();
		}
	}

	@Override
	protected void onDestroy() {
		SobotDialogUtils.stopProgressDialog(this);
		if(d!=null){
			d.dismiss();
		}
		super.onDestroy();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
//		return mGestureDetector.onTouchEvent(event);
		return false;
	}

	protected void onSaveInstanceState(Bundle outState) {
		//被摧毁前缓存一些数据
		outState.putString("uid",uid);
		outState.putString("companyId",companyId);
		outState.putInt("flag_exit_type",flag_exit_type);
		outState.putBoolean("flag_exit_sdk",flag_exit_sdk);
		outState.putString("msgTmp",msgTmp);
		outState.putString("msgTxt",msgTxt);
		super.onSaveInstanceState(outState);
	}

	//根据初始化接口返回的数据，对一些view设置他们的图标颜色是否可以点击等等
	private void initDate(){
		showRightView(0,getResString("sobot_submit"), true);
		Drawable drawable = getResources().getDrawable(getResDrawableId("sobot_btn_back_selector"));
		if(drawable != null){
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		}
		sobot_tv_left.setCompoundDrawables(drawable, null, null, null);
		sobot_tv_left.setText(getResString("sobot_back"));
		sobot_tv_left.setOnClickListener(this);

		setTitle(getResString("sobot_str_bottom_message"));
		setShowNetRemind(false);
		String bg_color = SharedPreferencesUtil.getStringData(this,
				"robot_current_themeColor", "");
		if (bg_color != null && bg_color.trim().length() != 0) {
			relative.setBackgroundColor(Color.parseColor(bg_color));
		}

		int robot_current_themeImg = SharedPreferencesUtil.getIntData(this, "robot_current_themeImg", 0);
		if (robot_current_themeImg != 0){
			relative.setBackgroundResource(robot_current_themeImg);
		}

		if (Build.VERSION.SDK_INT >= 11) {
			sobot_tv_right.setAlpha(0.5f);
		}
		sobot_tv_right.setClickable(false);
	}

	//获取本地数据，赋值
	private void getMySharedPreferences(){
		isShowNikeNameTv = SharedPreferencesUtil.getBooleanData(this,"sobot_postMsg_nike_nameShowFlag",false);//控制textView是否显示
		isShowNikeName = SharedPreferencesUtil.getBooleanData(this,"sobot_postMsg_nike_nameFlag",false);//控制是否必填

		telShowFlag = SharedPreferencesUtil.getBooleanData(this,ZhiChiConstant.SOBOT_POSTMSG_TELSHOWFLAG,false);//控制textView是否显示
		telFlag = SharedPreferencesUtil.getBooleanData(this,ZhiChiConstant.SOBOT_POSTMSG_TELFLAG,false);//控制是否必填

		emailShowFlag = SharedPreferencesUtil.getBooleanData(this,ZhiChiConstant.SOBOT_POSTMSG_EMAILSHOWFLAG,false);//控制textView是否显示
		emailFlag = SharedPreferencesUtil.getBooleanData(this,ZhiChiConstant.SOBOT_POSTMSG_EMAILFLAG,false);//控制是否必填

		enclosureShowFlag = SharedPreferencesUtil.getBooleanData(this,ZhiChiConstant.SOBOT_POSTMSG_ENCLOSURESHOWFLAG,false);//控制textView是否显示
		enclosureFlag = SharedPreferencesUtil.getBooleanData(this,ZhiChiConstant.SOBOT_POSTMSG_ENCLOSUREFLAG,false);//控制是否必填
		ticketStartWay = SharedPreferencesUtil.getStringData(this,ZhiChiConstant.SOBOT_POSTMSG_TICKETSTARTWAY,"1");
	}

	//初始化View
	private void initMyView(){
		sobot_leavemsg_nikename = (EditText) findViewById(getResId("sobot_leavemsg_nikename"));
		sobot_leavemsg_phone = (EditText) findViewById(getResId("sobot_leavemsg_phone"));
		sobot_et_email = (EditText) findViewById(getResId("sobot_et_email"));
		sobot_frist_line = findViewById(getResId("sobot_frist_line"));
		sobot_second_line = findViewById(getResId("sobot_second_line"));
		sobot_et_content = (EditText) findViewById(getResId("sobot_et_content"));
		sobot_tv_post_msg1 = (TextView) findViewById(getResId("sobot_tv_post_msg1"));
		sobot_enclosure_hint = (TextView) findViewById(getResId("sobot_enclosure_hint"));
		sobot_post_msg_layout =(RelativeLayout) findViewById(getResId("sobot_post_msg_layout"));
		sobot_img_clear_nikename = (ImageView) findViewById(getResId("sobot_img_clear_nikename"));
		sobot_img_clear_email = (ImageView) findViewById(getResId("sobot_img_clear_email"));
		sobot_img_clear_phone = (ImageView) findViewById(getResId("sobot_img_clear_phone"));
		sobot_enclosure_container = (LinearLayout) findViewById(getResId("sobot_enclosure_container"));

		sobot_leavemsg_nikename.setText(SharedPreferencesUtil.getStringData(this,"sobot_user_nikename",""));
		sobot_leavemsg_phone.setText(SharedPreferencesUtil.getStringData(this,"sobot_user_phone",""));

		if ("1".equals(ticketStartWay)){
			sobot_et_email.setVisibility(View.VISIBLE);

			if (telShowFlag){
				sobot_leavemsg_phone.setVisibility(View.VISIBLE);
			} else {
				sobot_leavemsg_phone.setVisibility(View.GONE);
			}
		} else {
			sobot_leavemsg_phone.setVisibility(View.VISIBLE);

			if (emailShowFlag){
				sobot_et_email.setVisibility(View.VISIBLE);
			} else {
				sobot_et_email.setVisibility(View.GONE);
			}
		}

		if (isShowNikeNameTv){
			sobot_leavemsg_nikename.setVisibility(View.VISIBLE);
		} else {
			sobot_leavemsg_nikename.setVisibility(View.GONE);
		}

		if (emailShowFlag && telShowFlag && isShowNikeNameTv){
			sobot_frist_line.setVisibility(View.VISIBLE);
			sobot_second_line.setVisibility(View.VISIBLE);
		} else if (emailShowFlag && telShowFlag){
			sobot_frist_line.setVisibility(View.VISIBLE);
			sobot_second_line.setVisibility(View.GONE);
		} else if (telShowFlag && isShowNikeNameTv){
			sobot_frist_line.setVisibility(View.GONE);
			sobot_second_line.setVisibility(View.VISIBLE);
		} else if (emailShowFlag && isShowNikeNameTv){
			sobot_frist_line.setVisibility(View.VISIBLE);
			sobot_second_line.setVisibility(View.GONE);
		} else {
			sobot_frist_line.setVisibility(View.GONE);
			sobot_second_line.setVisibility(View.GONE);
		}

		if(enclosureShowFlag){
			sobot_enclosure_container.setVisibility(View.VISIBLE);
			initPicListView();
		}else{
			sobot_enclosure_container.setVisibility(View.GONE);
		}
	}

	/**
	 * 初始化图片选择的控件
	 */
	private void initPicListView() {
		sobot_post_msg_pic = (GridView) findViewById(getResId("sobot_post_msg_pic"));
		adapter = new SobotPicListAdapter(SobotPostMsgActivity.this,pic_list);
		sobot_post_msg_pic.setAdapter(adapter);
		sobot_post_msg_pic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				KeyboardUtil.hideKeyboard(view);
				if(pic_list.get(position).getViewState() == 0){
					menuWindow = new SobotSelectPicDialog(SobotPostMsgActivity.this, itemsOnClick);
					menuWindow.show();
				} else{
					LogUtils.i("当前选择图片位置：" + position);
					Intent intent = new Intent(SobotPostMsgActivity.this, SobotPhotoListActivity.class);
					intent.putExtra(ZhiChiConstant.SOBOT_KEYTYPE_PIC_LIST, adapter.getPicList());
					intent.putExtra(ZhiChiConstant.SOBOT_KEYTYPE_PIC_LIST_CURRENT_ITEM, position);
					startActivityForResult(intent, ZhiChiConstant.SOBOT_KEYTYPE_DELETE_FILE_SUCCESS);
				}
			}
		});
		adapter.restDataView();
	}

	//对msg过滤
	private void msgFilter(){
		if (msgTmp.startsWith("<br/>")) {
			msgTmp = msgTmp.substring(5, msgTmp.length());
		}

		if (msgTmp.endsWith("<br/>")) {
			msgTmp = msgTmp.substring(0, msgTmp.length() - 5);
		}

		if (msgTxt.startsWith("<br/>")) {
			msgTxt = msgTxt.substring(5, msgTxt.length());
		}

		if (msgTxt.endsWith("<br/>")) {
			msgTxt = msgTxt.substring(0, msgTxt.length() - 5);
		}
		sobot_et_content.setHint(Html.fromHtml(msgTmp));
		HtmlTools.getInstance(getApplicationContext()).setRichText(sobot_tv_post_msg1, msgTxt,
				ResourceUtils.getIdByName(this, "color", "sobot_postMsg_url_color"));
		sobot_post_msg_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(sobot_post_msg_layout.getWindowToken(), 0); // 强制隐藏键盘
			}
		});
	}

	//设置editText的hint提示字体
	private void editTextSetHint(){
		if (isShowNikeName){
			sobot_leavemsg_nikename.setHint(getResString("sobot_post_msg_hint_nikename") + getResString("sobot_required"));
		} else {
			sobot_leavemsg_nikename.setHint(getResString("sobot_post_msg_hint_nikename") + getResString("sobot_optional"));
		}

		if (emailFlag){
			sobot_et_email.setHint(getResString("sobot_post_msg_hint_email") + getResString("sobot_required"));
		} else {
			sobot_et_email.setHint(getResString("sobot_post_msg_hint_email") + getResString("sobot_optional"));
		}

		if (telFlag){
			sobot_leavemsg_phone.setHint(getResString("sobot_post_msg_hint_phone") + getResString("sobot_required"));
		} else {
			sobot_leavemsg_phone.setHint(getResString("sobot_post_msg_hint_phone") + getResString("sobot_optional"));
		}

		if (enclosureFlag){
			sobot_enclosure_hint.setHint(getResString("sobot_post_msg_hint_enclosure") + getResString("sobot_required"));
		} else {
			sobot_enclosure_hint.setHint(getResString("sobot_post_msg_hint_enclosure") + getResString("sobot_optional"));
		}
	}

	// 为弹出窗口popupwindow实现监听类
	private View.OnClickListener itemsOnClick = new View.OnClickListener() {
		public void onClick(View v) {
			menuWindow.dismiss();
			if(v.getId() == getResId("btn_take_photo")){
				LogUtils.i("拍照");
				selectPicFromCamera();
			}
			if(v.getId() == getResId("btn_pick_photo")){
				LogUtils.i("选择照片");
				selectPicFromLocal();
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LogUtils.i("多媒体返回的结果：" + requestCode + "--" + resultCode + "--" + data);

		if (resultCode == RESULT_OK) {
			if (requestCode == ZhiChiConstant.REQUEST_CODE_picture) { // 发送本地图片
				if (data != null && data.getData() != null) {
					Uri selectedImage = data.getData();
					SobotDialogUtils.startProgressDialog(SobotPostMsgActivity.this);
					ChatUtils.sendPicByUriPost(this,selectedImage, sendFileListener);
				} else {
					ToastUtil.showLongToast(getApplicationContext(), getResString("sobot_did_not_get_picture_path"));
				}
			} else if (requestCode == ZhiChiConstant.REQUEST_CODE_makePictureFromCamera) {
				if (cameraFile != null && cameraFile.exists()) {
					SobotDialogUtils.startProgressDialog(SobotPostMsgActivity.this);
					ChatUtils.sendPicByFilePath(this, cameraFile.getAbsolutePath(), sendFileListener);
				} else {
					ToastUtil.showLongToast(getApplicationContext(), getResString("sobot_pic_select_again"));
				}
			}
		}

		if (data != null) {
			switch (requestCode) {
				case ZhiChiConstant.SOBOT_KEYTYPE_DELETE_FILE_SUCCESS://图片预览
					List<ZhiChiUploadAppFileModelResult> tmpList = (List<ZhiChiUploadAppFileModelResult>) data.getExtras().getSerializable(ZhiChiConstant.SOBOT_KEYTYPE_PIC_LIST);
					adapter.addDatas(tmpList);
					check();
					break;
				default:
					break;
			}
		}
	}

	private ChatUtils.SobotSendFileListener sendFileListener = new ChatUtils.SobotSendFileListener() {
		@Override
		public void onSuccess(String filePath) {
			zhiChiApi.fileUploadForPostMsg(companyId, filePath, new ResultCallBack<ZhiChiMessage>() {
				@Override
				public void onSuccess(ZhiChiMessage zhiChiMessage) {
					SobotDialogUtils.stopProgressDialog(SobotPostMsgActivity.this);
					if(zhiChiMessage.getData() != null){
						ZhiChiUploadAppFileModelResult item = new ZhiChiUploadAppFileModelResult();
						item.setFileUrl(zhiChiMessage.getData().getUrl());
						item.setViewState(1);
						adapter.addData(item);
						check();
					}
				}

				@Override
				public void onFailure(Exception e, String des) {
					SobotDialogUtils.stopProgressDialog(SobotPostMsgActivity.this);
					ToastUtil.showToast(getApplicationContext(),des);
				}

				@Override
				public void onLoading(long total, long current, boolean isUploading) {

				}
			});
		}

		@Override
		public void onError() {
			SobotDialogUtils.stopProgressDialog(SobotPostMsgActivity.this);
		}
	};


	public String getFileStr() {
		String tmpStr = "";
		if (!enclosureShowFlag){
			return tmpStr;
		}

		ArrayList<ZhiChiUploadAppFileModelResult> tmpList = adapter.getPicList();
		for (int i = 0; i < tmpList.size(); i++) {
			tmpStr += tmpList.get(i).getFileUrl()+";";
		}
		return tmpStr;
	}
}