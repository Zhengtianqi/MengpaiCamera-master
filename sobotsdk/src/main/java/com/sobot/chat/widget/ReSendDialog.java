package com.sobot.chat.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.sobot.chat.utils.ResourceUtils;

/**
 * 自定义退出对话框
 */
public class ReSendDialog extends Dialog {

	private Context content;
	public Button button;
	public Button button2;
	public OnItemClick mOnItemClick = null;
	public ReSendDialog(Context context) {
		super(context);
		this.content = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(ResourceUtils.getIdByName(content, "layout", "sobot_resend_message_dialog"));
		button = (Button) findViewById(ResourceUtils.getIdByName(content, "id", "sobot_negativeButton"));
		button2 = (Button) findViewById(ResourceUtils.getIdByName(content, "id", "sobot_positiveButton"));
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mOnItemClick.OnClick(0);
			}
		});
		button2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mOnItemClick.OnClick(1);
			}
		});
	}

	public void setOnClickListener(OnItemClick onItemClick) {
		mOnItemClick = onItemClick;
	}

	public interface OnItemClick{
		void OnClick(int type);
	}
}