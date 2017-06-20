package com.sobot.chat.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

public class BorderImageView extends ImageView {
	private int color;

	public BorderImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		color = Color.parseColor("#dcdcdc");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ImageView#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {

		super.onDraw(canvas);
		// 画边框
		Rect rec = canvas.getClipBounds();
//		rec.bottom--;
//		rec.right--;
		Paint paint = new Paint();
		paint.setColor(color);
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawRect(rec, paint);
	}
}