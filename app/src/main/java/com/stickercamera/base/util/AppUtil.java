package com.stickercamera.base.util;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 */
public class AppUtil {

    public static int getScreenWidth(Context context) {

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getScreenHeight(Context context) {

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

}
