package com.sobot.chat.widget.kpswitch.widget.interfaces;

import android.view.View;
import android.view.ViewGroup;

import com.sobot.chat.widget.kpswitch.widget.data.PageEntity;


public interface PageViewInstantiateListener<T extends PageEntity> {

    View instantiateItem(ViewGroup container, int position, T pageEntity);
}