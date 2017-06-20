package com.stickercamera.app.ui;

import android.app.Application;

import com.tencent.bugly.crashreport.CrashReport;


public class PicApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "fab25e4f00", false);
    }
}
