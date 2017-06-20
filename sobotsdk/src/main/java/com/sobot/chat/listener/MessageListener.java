package com.sobot.chat.listener;

public interface MessageListener {
	void onReceiveMessage(int noReadNum,String content);
}