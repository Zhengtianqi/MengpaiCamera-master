package com.sobot.chat.widget.kpswitch.view;

import android.content.Context;

import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.ResourceUtils;


/**
 * 创建view 的工厂类
 * 根据按钮的id来创建对应的view
 */
public class CustomeViewFactory {
    public static BaseChattingPanelView getInstance(Context context, int btnId) {
        BaseChattingPanelView baseView = null;
        LogUtils.i("BaseChattingPanelView");
        if (btnId != 0) {
            if (btnId == ResourceUtils.getIdByName(context, "id", "sobot_btn_upload_view")) {
                baseView = new ChattingPanelUploadView(context);
            } else if (btnId == ResourceUtils.getIdByName(context, "id", "sobot_btn_emoticon_view")) {
                baseView = new ChattingPanelEmoticonView(context);
            }
        }
        return baseView;
    }

    /**
     * 这里给的tag就是按钮对应的view的类名
     * @param context
     * @param btnId
     * @return
     */
    public static String getInstanceTag(Context context, int btnId) {
        String baseViewTag = null;
        if (btnId != 0) {
            if (btnId == ResourceUtils.getIdByName(context, "id", "sobot_btn_upload_view")) {
//                baseViewTag = new ChattingPanelUploadView(context);
                baseViewTag = "ChattingPanelUploadView";
            } else if (btnId == ResourceUtils.getIdByName(context, "id", "sobot_btn_emoticon_view")) {
//                baseViewTag = new ChattingPanelEmoticonView(context);
                baseViewTag = "ChattingPanelEmoticonView";
            }
        }
        return baseViewTag;
    }
}