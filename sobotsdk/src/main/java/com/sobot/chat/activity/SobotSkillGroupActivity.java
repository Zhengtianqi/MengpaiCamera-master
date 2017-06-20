package com.sobot.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.sobot.chat.adapter.base.SobotSikllAdapter;
import com.sobot.chat.api.ZhiChiApi;
import com.sobot.chat.api.apiUtils.ZhiChiConstants;
import com.sobot.chat.api.model.ZhiChiGroup;
import com.sobot.chat.api.model.ZhiChiGroupBase;
import com.sobot.chat.application.MyApplication;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.core.http.callback.StringResultCallBack;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ScreenUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.ZhiChiConstant;

import java.util.ArrayList;
import java.util.List;

public class SobotSkillGroupActivity extends Activity {

	private Button sobot_btn_cancle;
	private GridView sobot_gv_skill;
	private SobotSikllAdapter sobotSikllAdapter;
	private List<ZhiChiGroupBase> list_skill = new ArrayList<ZhiChiGroupBase>();
	private boolean flag_exit_sdk;
	private String uid = null;
	private String companyId = null;
	private String appkey = null;
	private String msgTmp = null;
	private String msgTxt = null;
	private ZhiChiApi zhiChiApi;
	private int mType = -1;
	private int msgFlag = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(ResourceUtils.getIdByName(this, "layout",
				"sobot_activity_skill_group"));
		SobotSkillGroupActivity.this.setFinishOnTouchOutside(false);
		sobot_btn_cancle = (Button) findViewById(ResourceUtils.getIdByName(
				this, "id", "sobot_btn_cancle"));
		sobot_gv_skill = (GridView) findViewById(ResourceUtils.getIdByName(
				this, "id", "sobot_gv_skill"));

		sobotSikllAdapter = new SobotSikllAdapter(this, list_skill,msgFlag);
		sobot_gv_skill.setAdapter(sobotSikllAdapter);

		sobot_gv_skill
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> adapterView,
							View view, int i, long l) {
						if (list_skill != null && list_skill.size() > 0) {
							if (!TextUtils.isEmpty(list_skill.get(i).getGroupName())) {
								if (list_skill.get(i).isOnline().endsWith("true")) {
									Intent intent = new Intent();
									intent.putExtra("groupIndex", i);
									setResult(ZhiChiConstant.RESOULT_COCE_TO_GRROUP, intent);
									finish();
								} else {
									if(msgFlag == ZhiChiConstant.sobot_msg_flag_open){
										//留言开关开启时才能点击去留言
										startToPostMsgActivty();
									}
								}
							}
						}
					}
				});

		sobot_btn_cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finishPageOrSDK();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		initData();
	}

	@Override
	protected void onDestroy() {
		MyApplication.getInstance().deleteActivity(this);
		super.onDestroy();
	}

	private void initData() {
		if (getIntent() != null) {
			uid = getIntent().getStringExtra("uid");
			companyId = getIntent().getStringExtra("companyId");
			appkey = getIntent().getStringExtra("appkey");
			flag_exit_sdk = getIntent().getBooleanExtra(
					ZhiChiConstant.FLAG_EXIT_SDK, false);
			mType = getIntent().getIntExtra("type", -1);
			msgTmp = getIntent().getStringExtra("msgTmp");
			msgTxt = getIntent().getStringExtra("msgTxt");
			msgFlag = getIntent().getIntExtra("msgFlag",0);
		}
		zhiChiApi = SobotMsgManager.getInstance(getApplicationContext()).getZhiChiApi();
		zhiChiApi.getGroupList(appkey,uid, new StringResultCallBack<ZhiChiGroup>() {
			@Override
			public void onSuccess(ZhiChiGroup zhiChiGroup) {

					list_skill = zhiChiGroup.getData();
					if (list_skill != null && list_skill.size() > 0) {
						if (list_skill.size() % 2 != 0) {
							list_skill.add(new ZhiChiGroupBase("", ""));// 奇数时，加一个空布局，仅为展示
						}
						sobotSikllAdapter = new SobotSikllAdapter(getApplicationContext(), list_skill,msgFlag);
						int row = 0;
						int count = list_skill.size();
						if (count > 0) {
							if (count <= 2) {
								row = 1;
							} else if (count <= 4) {
								row = 2;
							} else {
								row = 3;
							}
							float dpToPixel = ScreenUtils.dpToPixel(
									getApplicationContext(), 1);
							View view = sobotSikllAdapter.getView(0, null,
									sobot_gv_skill);
							view.measure(0, 0);
							int measuredHeight = view.getMeasuredHeight();
							LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) sobot_gv_skill
									.getLayoutParams();
							params.height = (int) (measuredHeight * row + (row - 1)
									* dpToPixel + dpToPixel);
							sobot_gv_skill.setLayoutParams(params);
						}

						sobot_gv_skill.setAdapter(sobotSikllAdapter);
					}
			}

			@Override
			public void onFailure(Exception e, String des) {}
		});
	}

	private void finishPageOrSDK() {
		int initType = SharedPreferencesUtil.getIntData(
				getApplicationContext(), ZhiChiConstant.initType, -1);
		if (initType == ZhiChiConstant.type_custom_only) {
			finish();
			sendCloseIntent(1);
		} else {
			if (!flag_exit_sdk) {
				finish();
				overridePendingTransition(ResourceUtils.getIdByName(
						getApplicationContext(), "anim", "push_right_in"),
						ResourceUtils.getIdByName(getApplicationContext(),
								"anim", "push_right_out"));
				sendCloseIntent(2);
			} else {
				MyApplication.getInstance().exit();
			}
		}
	}

	private void sendCloseIntent(int type){
		Intent intent = new Intent();
		if (type == 1){
			intent.setAction(ZhiChiConstants.sobot_close_now_clear_cache);
		} else {
			intent.setAction(ZhiChiConstants.sobot_click_cancle);
		}
		CommonUtils.sendLocalBroadcast(getApplicationContext(),intent);
	}
	@Override
	public void onBackPressed() {
		finishPageOrSDK();
	}

	private void startToPostMsgActivty() {
		Intent intent = new Intent(getApplicationContext(),
				SobotPostMsgActivity.class);
		intent.putExtra("uid", uid);
		intent.putExtra("companyId", companyId);
		intent.putExtra("msgTmp",msgTmp);
		intent.putExtra("msgTxt",msgTxt);
		if (mType == 2) {
			intent.putExtra(ZhiChiConstant.FLAG_EXIT_TYPE, 1);
			startActivity(intent);
		} else if (mType == 3 || mType == 1) {
			intent.putExtra(ZhiChiConstant.FLAG_EXIT_TYPE, 2);
			startActivityForResult(intent, 200);
		} else if (mType == 4) {
			intent.putExtra(ZhiChiConstant.FLAG_EXIT_TYPE, 2);
			startActivityForResult(intent, 200);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 200) {
			finish();
			overridePendingTransition(ResourceUtils.getIdByName(
					getApplicationContext(), "anim", "push_right_in"),
					ResourceUtils.getIdByName(getApplicationContext(), "anim",
							"push_right_out"));
		}
	}
}