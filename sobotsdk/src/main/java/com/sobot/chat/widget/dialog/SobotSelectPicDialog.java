package com.sobot.chat.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ScreenUtils;

/**
 * Created by jinxl on 2017/4/10.
 */

public class SobotSelectPicDialog  extends Dialog {

    private View mView;
    private Button btn_take_photo, btn_pick_photo, btn_cancel;
    private LinearLayout coustom_pop_layout;
    private View.OnClickListener itemsOnClick;
    private Context context;
    private final int screenHeight;

    public SobotSelectPicDialog(Activity context, View.OnClickListener itemsOnClick) {
        super(context,ResourceUtils.getIdByName(context, "style", "sobot_clearHistoryDialogStyle"));
        this.context = context;
        this.itemsOnClick = itemsOnClick;
        screenHeight = ScreenUtils.getScreenHeight(context);
        // 修改Dialog(Window)的弹出位置
        Window window = getWindow();
        if(window != null){
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            setParams(context, layoutParams);
            window.setAttributes(layoutParams);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ResourceUtils.getIdByName(context, "layout", "sobot_take_pic_pop"));
        initView();
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!(event.getX() >= -10 && event.getY() >= -10)
                    || event.getY() <= (screenHeight - coustom_pop_layout.getHeight() - 20)) {//如果点击位置在当前View外部则销毁当前视图,其中10与20为微调距离
                dismiss();
            }
        }
        return true;
    }

    private void setParams(Context context, WindowManager.LayoutParams lay) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        Rect rect = new Rect();
        View view = getWindow().getDecorView();
        view.getWindowVisibleDisplayFrame(rect);
        lay.width = dm.widthPixels;
    }

    private void initView() {
        btn_take_photo = (Button) findViewById(ResourceUtils.getIdByName(context, "id","btn_take_photo"));
        btn_pick_photo = (Button) findViewById(ResourceUtils.getIdByName(context, "id","btn_pick_photo"));
        btn_cancel = (Button) findViewById(ResourceUtils.getIdByName(context, "id","btn_cancel"));
        coustom_pop_layout = (LinearLayout) findViewById(ResourceUtils.getIdByName(context, "id","pop_layout"));

        btn_take_photo.setOnClickListener(itemsOnClick);
        btn_pick_photo.setOnClickListener(itemsOnClick);
        btn_cancel.setOnClickListener(itemsOnClick);
    }
}
