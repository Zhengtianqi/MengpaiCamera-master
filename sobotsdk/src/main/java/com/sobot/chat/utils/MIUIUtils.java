package com.sobot.chat.utils;

import android.os.Build;

public class MIUIUtils {

    public static String getAndroidDisplayVersion() {
        String androidDisplay = null;
        androidDisplay = Build.MANUFACTURER;
        return androidDisplay;
    }
}