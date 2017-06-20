package com.sobot.chat.widget.kpswitch.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.sobot.chat.utils.ResourceUtils;

/**
 * 所有聊天面板view的基类
 */
public abstract class BaseChattingPanelView {
    private View rootView = null;
    protected Context context = null;

    public BaseChattingPanelView(Context context) {
        this.context = context;
        rootView = initView();
        rootView.setTag(getRootViewTag());
    }

    /**
     * 初始化view
     */
    public abstract View initView();

    /**
     * 初始化数据
     */
    public abstract void initData();

    /**
     * 获取view
     *
     * @return
     */
    public View getRootView() {
        return rootView;
    }

    public int getResId(String name) {
        return ResourceUtils.getIdByName(context, "id", name);
    }

    public int getResLayoutId(String name) {
        return ResourceUtils.getIdByName(context, "layout", name);
    }

    public int getResDrawableId(String name) {
        return ResourceUtils.getIdByName(context, "drawable", name);
    }

    /**
     * 获取根view的tag
     * @return
     */
    public abstract String getRootViewTag();

    /**
     * view的显示回调
     */
    public void onViewStart(Bundle bundle){}
}