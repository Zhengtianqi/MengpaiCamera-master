package com.sobot.chat.widget.kpswitch;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.sobot.chat.widget.kpswitch.view.BaseChattingPanelView;
import com.sobot.chat.widget.kpswitch.view.CustomeViewFactory;

import java.util.HashMap;

/**
 * 聊天的面板集合类
 * 根据对应的btnid  显示不同的布局
 */
public class CustomeChattingPanel extends RelativeLayout {

    private HashMap<Integer, BaseChattingPanelView> map = new HashMap<>();
    private String instanceTag;

    public CustomeChattingPanel(Context context) {
        this(context, null);
    }

    public CustomeChattingPanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomeChattingPanel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setupView(Activity act, final int btnid, Bundle bundle) {
        int childCount = this.getChildCount();
        //找到tag对应的view  显示或隐藏掉view
        instanceTag = CustomeViewFactory.getInstanceTag(act, btnid);
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View childAt = this.getChildAt(i);
                String tag = childAt.getTag().toString();
                if (tag.equals(instanceTag)) {
                    childAt.setVisibility(View.VISIBLE);
                } else {
                    childAt.setVisibility(View.GONE);
                }
            }
        }

        BaseChattingPanelView baseChattingPanelView = map.get(btnid);
        if (baseChattingPanelView == null) {
            BaseChattingPanelView view = CustomeViewFactory.getInstance(act, btnid);
            map.put(btnid, view);
            this.addView(view.getRootView());
            view.initView();
            view.initData();

            view.onViewStart(bundle);
        } else {
            baseChattingPanelView.onViewStart(bundle);
        }
    }

    /**
     * 获取当前显示的面板的tag
     *
     * @return instanceTag
     */
    public String getPanelViewTag() {
        return instanceTag;
    }
}