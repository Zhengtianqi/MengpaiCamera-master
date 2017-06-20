package com.sobot.chat.utils;

import android.media.MediaPlayer;
import android.media.MediaRecorder;

public class AudioTools 
  {
	private static MediaPlayer instance;
	private static MediaRecorder mediaRecorder;
	
	public static MediaPlayer getInstance(){
		if(instance==null){
			instance = new MediaPlayer();
		}
		return instance;
	}
	
	//停止的状态
	public static void stop(){
		if(instance!=null){
			if (AudioTools.getInstance().isPlaying()) {
				AudioTools.getInstance().stop();
			}
		}
	}
	// 判断是否正在播放
	public static boolean getIsPlaying(){
		if(instance!=null){
			return AudioTools.getInstance().isPlaying();
		}else{
			return false;
		}
	}
	
	public static MediaRecorder getMediaRecorder(){
		if(mediaRecorder == null){
			mediaRecorder = new MediaRecorder();
		}
		return mediaRecorder;
	}
}