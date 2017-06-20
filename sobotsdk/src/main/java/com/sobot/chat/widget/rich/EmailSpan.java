package com.sobot.chat.widget.rich;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.ShareCompat;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.sobot.chat.utils.SobotOption;

public class EmailSpan extends ClickableSpan {

	private String email;
	private int color;

	public EmailSpan(Context context,String email, int color) {
		this.email = email;
		this.color = context.getResources().getColor(color);
	}

	@Override
	public void onClick(View widget) {
		if (SobotOption.hyperlinkListener != null){
			SobotOption.hyperlinkListener.onEmailClick(email);
			return;
		}
		try {
			ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder
					.from((Activity)widget.getContext());
			builder.setType("message/rfc822");
			builder.addEmailTo(email);
			builder.setSubject("");
			builder.setChooserTitle("");
			builder.startChooser();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateDrawState(TextPaint ds) {
		ds.setColor(color);
		ds.setUnderlineText(false); // 去掉下划线
	}
}