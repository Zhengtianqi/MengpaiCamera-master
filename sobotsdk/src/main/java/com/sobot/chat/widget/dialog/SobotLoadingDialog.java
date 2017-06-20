package com.sobot.chat.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.sobot.chat.utils.ResourceUtils;

/**
 * Created by jinxl on 2017/4/10.
 */

public class SobotLoadingDialog extends Dialog {

    private static SobotLoadingDialog customProgressDialog = null;
    public SobotLoadingDialog(Context context, int theme) {
        super(context, theme);
    }

    public static SobotLoadingDialog createDialog(Context context) {
        initView(context);
        return customProgressDialog;
    }

    private static void initView(Context context){
        customProgressDialog = new SobotLoadingDialog(context, ResourceUtils.getIdByName(context, "style", "sobot_dialog_Progress"));
        customProgressDialog.setContentView(ResourceUtils.getIdByName(context, "layout", "sobot_progress_dialog"));
        if(customProgressDialog.getWindow()!= null){
            customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        }
        customProgressDialog.setCanceledOnTouchOutside(false);
        customProgressDialog.setCancelable(false);
    }

    public static SobotLoadingDialog createDialog(Context context,String str) {
        initView(context);
        if(!TextUtils.isEmpty(str)){
            TextView textView = (TextView) customProgressDialog.findViewById(ResourceUtils.getIdByName(context, "id","id_tv_loadingmsg"));
            textView.setText(str);
        }
        return customProgressDialog;
    }

    public static void setText(Context context,String str){
        TextView textView = (TextView) customProgressDialog.findViewById(ResourceUtils.getIdByName(context, "id","id_tv_loadingmsg"));
        if(!TextUtils.isEmpty(str)){
            textView.setText(str);
        }else{
            textView.setText("请稍候");
        }
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        ImageView imageView = (ImageView) findViewById(ResourceUtils.getIdByName(getContext(), "id", "loadingImageView"));
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();

//        GifView image_view = (GifView) findViewById(R.id.loadingImageView);
//        image_view.setGifImageType(GifImageType.COVER);
//        image_view.setGifImage(R.drawable.loading);
    }
}
