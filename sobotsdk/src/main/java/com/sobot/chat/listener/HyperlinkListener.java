package com.sobot.chat.listener;

/**
 * 超链接点击的监听事件
 */
public interface HyperlinkListener {
    //url的点击事件
    void onUrlClick(String url);
    //邮箱的点击事件
    void onEmailClick(String email);
    //电话的点击事件
    void onPhoneClick(String phone);
}