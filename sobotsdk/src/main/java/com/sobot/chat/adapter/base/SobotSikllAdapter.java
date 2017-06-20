package com.sobot.chat.adapter.base;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sobot.chat.api.model.ZhiChiGroupBase;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ZhiChiConstant;

import java.util.List;

@SuppressWarnings({"rawtypes"})
public class SobotSikllAdapter extends SobotBaseAdapter {
    private LayoutInflater mInflater;
    public TextView sobot_tv_skill_name;
    public TextView sobot_tv_status;
    public LinearLayout sobot_ll_skill;
    private int msgFlag = 0;

    @SuppressWarnings("unchecked")
    public SobotSikllAdapter(Context context, List<ZhiChiGroupBase> list, int msgFlag) {
        super(context, list);
        this.msgFlag = msgFlag;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(ResourceUtils
                    .getIdByName(context, "layout", "sobot_list_item_skill"), null);
        }
        sobot_ll_skill = (LinearLayout) convertView.findViewById(ResourceUtils
                .getIdByName(context, "id", "sobot_ll_skill"));
        sobot_tv_skill_name = (TextView) convertView.findViewById(ResourceUtils
                .getIdByName(context, "id", "sobot_tv_skill_name"));
        sobot_tv_status = (TextView) convertView.findViewById(ResourceUtils
                .getIdByName(context, "id", "sobot_tv_status"));
        ZhiChiGroupBase zhiChiSkillIModel = (ZhiChiGroupBase) list.get(position);
        if (zhiChiSkillIModel != null && !TextUtils.isEmpty(zhiChiSkillIModel.getGroupName())) {
            sobot_ll_skill.setVisibility(View.VISIBLE);
            sobot_tv_skill_name.setText(zhiChiSkillIModel.getGroupName());
            if (zhiChiSkillIModel.isOnline().equals("true")) {
                sobot_ll_skill.setEnabled(true);
                sobot_tv_skill_name.setTextColor(Color.parseColor("#000000"));
                sobot_tv_status.setVisibility(View.GONE);
            } else {
                sobot_tv_status.setVisibility(View.VISIBLE);
                if (msgFlag == ZhiChiConstant.sobot_msg_flag_open) {
                    sobot_tv_skill_name.setTextColor(Color.parseColor("#000000"));
                    sobot_ll_skill.setEnabled(true);
                    sobot_tv_status.setText(getResString("sobot_str_bottom_message"));
                } else {
                    sobot_tv_skill_name.setTextColor(context.getResources().getColor(ResourceUtils.getIdByName(context, "color", "sobot_color_item_skill_offline")));
                    sobot_ll_skill.setEnabled(false);
                    sobot_tv_status.setText(getResString("sobot_str_bottom_offline"));
                }
            }
        } else {
            sobot_ll_skill.setVisibility(View.INVISIBLE);
            sobot_tv_skill_name.setText("");
            sobot_tv_status.setText("");
            sobot_tv_status.setCompoundDrawables(null, null, null, null);
        }
        return convertView;
    }
}