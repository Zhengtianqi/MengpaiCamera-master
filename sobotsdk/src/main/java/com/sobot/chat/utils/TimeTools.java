package com.sobot.chat.utils;


/**
 * 将毫秒的时间转换为固定格式的时间
 */
public class TimeTools {
	public static TimeTools instance = null;

	static  {
		if (instance == null) {
			instance = new TimeTools();
		}
	}
	@SuppressWarnings("unused")
	public  String calculatTime(int milliSecondTime) {
		int hour = milliSecondTime / (60 * 60 * 1000);
		int minute = (milliSecondTime - hour * 60 * 60 * 1000) / (60 * 1000);
		int seconds = (milliSecondTime - hour * 60 * 60 * 1000 - minute * 60 * 1000) / 1000;
		if (seconds >= 60) {
			seconds = seconds % 60;
			minute += seconds / 60;
		}
		if (minute >= 60) {
			minute = minute % 60;
			hour += minute / 60;
		}

		String sh = "";
		String sm = "";
		String ss = "";
		if (hour < 10) {
			sh = "00:0" + String.valueOf(hour);
		} else {
			sh = String.valueOf(hour);
		}
		if (minute < 10) {
			sm = "0" + String.valueOf(minute);
		} else {
			sm = String.valueOf(minute);
		}
		if (seconds < 10) {
			ss = "00:0" + String.valueOf(seconds);
		} else {
			ss = "00:" + String.valueOf(seconds);
		}
		//return sh + ":" + sm + ":" + ss;
		//return sm + ":" + ss;
		return ss  ;
	}
}