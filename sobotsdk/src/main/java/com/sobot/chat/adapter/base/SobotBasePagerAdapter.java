package com.sobot.chat.adapter.base;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by jinxl on 2017/4/10.
 */

public class SobotBasePagerAdapter<T> extends PagerAdapter {
    protected ArrayList<T> list;
    protected Context context;

    public SobotBasePagerAdapter(Context context, ArrayList<T> list) {
        super();
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//		super.destroyItem(container, position, object);
        container.removeView((View) object);
    }

}
