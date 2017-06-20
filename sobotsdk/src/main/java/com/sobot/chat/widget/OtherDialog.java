package com.sobot.chat.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.sobot.chat.activity.SobotChatActivity;
import com.sobot.chat.utils.ResourceUtils;

/**
 * 
 * Create custom Dialog windows for your application Custom dialogs rely on
 * custom layouts wich allow you to create and use your own look & feel.
 * 
 * Under GPL v3 : http://www.gnu.org/licenses/gpl-3.0.html
 * 
 * <a href="http://my.oschina.net/arthor" target="_blank"
 * rel="nofollow">@author</a> antoine vianey
 * 
 */
public class OtherDialog extends Dialog {

	private Context content;
	public Button button;
	public Button button2;
	private TextView title;
	public OnItemClick mOnItemClick = null;
	private SobotChatActivity sca;
	public OtherDialog(Context context,SobotChatActivity sca) {
		super(context);
		this.content = context;
		this.sca = sca;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(ResourceUtils.getIdByName(content, "layout", "sobot_other_dialog"));
		title = (TextView) findViewById(ResourceUtils.getIdByName(content,"id","sobot_message"));
		title.setText(ResourceUtils.getIdByName(content,"string","sobot_close_session"));
		button = (Button) findViewById(ResourceUtils.getIdByName(content, "id", "sobot_negativeButton"));
		button.setText(ResourceUtils.getIdByName(content,"string","sobot_cancel"));
		button2 = (Button) findViewById(ResourceUtils.getIdByName(content, "id", "sobot_positiveButton"));
		button2.setText(ResourceUtils.getIdByName(content, "string", "sobot_button_end_now"));
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mOnItemClick.OnClick(1);
			}
		});
		button2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mOnItemClick.OnClick(0);
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