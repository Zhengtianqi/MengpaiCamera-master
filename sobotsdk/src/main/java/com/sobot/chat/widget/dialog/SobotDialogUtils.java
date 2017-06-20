package com.sobot.chat.widget.dialog;

import android.app.Activity;
import android.content.Context;

/**
 * Created by jinxl on 2017/4/10.
 */

public class SobotDialogUtils {
    public static SobotLoadingDialog progressDialog;

    public static void startProgressDialog(Context context) {
        if (progressDialog == null) {
            progressDialog = SobotLoadingDialog.createDialog(context);
        } else {
            progressDialog.setText(context,"");
        }
        progressDialog.show();
    }

    public static void startProgressDialog(Context context,String str) {
        if (progressDialog == null) {
            progressDialog = SobotLoadingDialog.createDialog(context,str);
        } else {
            progressDialog.setText(context,str);
        }
        progressDialog.show();
    }

    public static void stopProgressDialog(Context context) {
        if (progressDialog != null && context != null) {
            Activity act = (Activity) context;
            if(!act.isFinishing()){
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }
}
