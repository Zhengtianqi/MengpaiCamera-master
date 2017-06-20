package com.sobot.chat.adapter.base;

import android.content.Context;
import android.widget.BaseAdapter;

import com.sobot.chat.utils.ResourceUtils;

import java.util.List;

public abstract class SobotBaseAdapter<T> extends BaseAdapter {

	protected List<T> list;
	protected Context context;

	public SobotBaseAdapter(Context context, List<T> list) {
		super();
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public String getResString(String name){
		return context.getResources().getString(getResStringId(name));
	}

	public int getResStringId(String name) {
		return ResourceUtils.getIdByName(context, "string", name);
	}
}