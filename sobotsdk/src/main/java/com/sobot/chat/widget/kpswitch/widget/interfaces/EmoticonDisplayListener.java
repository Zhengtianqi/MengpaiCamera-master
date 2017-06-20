package com.sobot.chat.widget.kpswitch.widget.interfaces;

import android.view.ViewGroup;

import com.sobot.chat.widget.kpswitch.widget.adpater.EmoticonsAdapter;

public interface EmoticonDisplayListener<T> {

    void onBindView(int position, ViewGroup parent, EmoticonsAdapter.ViewHolder viewHolder, T t, boolean isDelBtn);
}