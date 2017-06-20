package com.sobot.chat.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.ImageView;
import android.widget.TextView;

public class VersionUtils {

	/**
	 * 获取版本号
	 * 
	 * @return 当前应用的版本号
	 */
	public static int getVersion(Context context) {
		PackageManager manager = context.getPackageManager();
		PackageInfo info;
		try {
			info = manager.getPackageInfo(context.getPackageName(), 0);
			int version = info.versionCode;
			return version;
		} catch (NameNotFoundException e) {
			// e.printStackTrace();
		}
		return 0;

	}
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static void setBackground(Drawable imagebakground,TextView view){
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
	    	view.setBackground(imagebakground);
	    } else {
	    	view.setBackgroundDrawable(imagebakground);
	    }
	}
	
	@SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static void setBackground(Drawable imagebakground,ImageView view){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(imagebakground);
        } else {
            view.setBackgroundDrawable(imagebakground);
        }
    }
}