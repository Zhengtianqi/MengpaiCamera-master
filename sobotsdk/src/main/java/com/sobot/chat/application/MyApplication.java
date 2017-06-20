package com.sobot.chat.application;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.LinkedList;
import java.util.List;

@SuppressLint("SetJavaScriptEnabled")
public class MyApplication extends Application {

	private List<Activity> activityList = new LinkedList<Activity>();
	private static MyApplication instance;

	// 单例模式获取唯一的MyApplication
	public static MyApplication getInstance() {
		if (null == instance) {
			instance = new MyApplication();
		}
		return instance;
	}

	// 添加activity到容器中
	public void addActivity(Activity aty) {
		activityList.add(aty);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		//监听当前进程的异常
		Thread.currentThread().setUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
	}

	/*退出时关闭所有的activity*/
	public void exit() {
		for (Activity activity : activityList) {
			activity.finish();
		}
	}

	public void deleteActivity(Activity aty) {
		activityList.remove(aty);
	}
	
	//异常的监听
	private class MyUncaughtExceptionHandler implements UncaughtExceptionHandler{
		//当有未捕获的异常的时候调用
		//Throwable : Error和Exception的父类
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			ex.printStackTrace();
			System.out.println(Environment.getExternalStorageDirectory().getAbsoluteFile()+File.separator+"sobot_log.txt");
			try {
				//将异常保存到文件中
				ex.printStackTrace(new PrintStream(new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+File.separator+"sobot_log.txt")));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}