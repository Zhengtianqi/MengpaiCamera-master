package com.sobot.chat.viewHolder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sobot.chat.activity.SobotChatActivity;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.utils.BitmapUtil;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.viewHolder.base.MessageHolderBase;

/**
 * 商品咨询项目
 * Created by jinxl on 2017/3/17.
 */
public class ConsultMessageHolder extends MessageHolderBase {
    TextView tv_title;//   商品标题页title    商品描述  商品图片   发送按钮  商品标签
    TextView tv_describe;
    ImageView iv_pic;
    Button btn_sendBtn;
    TextView tv_lable;

    public ConsultMessageHolder(Context context, View convertView) {
        super(context, convertView);
        btn_sendBtn = (Button) convertView.findViewById(ResourceUtils.getIdByName(context, "id",
                "sobot_goods_sendBtn"));
        iv_pic = (ImageView) convertView.findViewById(ResourceUtils.getIdByName(context, "id",
                "sobot_goods_pic"));
        tv_title = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id",
                "sobot_goods_title"));
        tv_describe = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id",
                "sobot_goods_describe"));
        tv_lable = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id",
                "sobot_goods_label"));
    }

    @Override
    public void bindData(final Context context, final ZhiChiMessageBase message) {

        String title = message.getT();
        String picurl = message.getPicurl();
        final String url = message.getUrl();
        String lable = message.getAname();
        String describe = message.getReceiverFace();
        if (!TextUtils.isEmpty(picurl)) {
            iv_pic.setVisibility(View.VISIBLE);
            Drawable drawable = context.getResources().getDrawable(ResourceUtils.getIdByName(context, "drawable",
                    "sobot_icon_consulting_default_pic"));
            BitmapUtil.display(context, CommonUtils.encode(picurl), iv_pic, drawable, drawable);
        } else {
            iv_pic.setVisibility(View.GONE);
            iv_pic.setImageResource(ResourceUtils.getIdByName(context, "drawable",
                    "sobot_icon_consulting_default_pic"));
        }

        tv_title.setText(title);
        if (!TextUtils.isEmpty(lable)) {
            tv_lable.setVisibility(View.VISIBLE);
            tv_lable.setText(lable);
        } else {
            if (!TextUtils.isEmpty(picurl)) {
                tv_lable.setVisibility(View.INVISIBLE);
            } else {
                tv_lable.setVisibility(View.GONE);
            }
        }

        if (!TextUtils.isEmpty(describe)) {
            tv_describe.setVisibility(View.VISIBLE);
            tv_describe.setText(describe);
        } else {
            tv_describe.setVisibility(View.GONE);
        }

        btn_sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.i("发送连接---->" + url);
                ((SobotChatActivity) context).sendConsultingContent();
            }
        });
    }
}
