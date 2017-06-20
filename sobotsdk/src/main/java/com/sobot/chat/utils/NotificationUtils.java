package com.sobot.chat.utils;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;

public class NotificationUtils {

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressWarnings("deprecation")
    public static void createNotification(Context context, Class<?> cls, String title, String content, String ticker, int id){
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent detailIntent = context.getPackageManager().getLaunchIntentForPackage(CommonUtils
                .getPackageName(context));
        detailIntent.setPackage((String)null);
        detailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0,
                detailIntent, 0);
        int smallicon = SharedPreferencesUtil.getIntData(context, ZhiChiConstant
                .SOBOT_NOTIFICATION_SMALL_ICON, ResourceUtils.getIdByName(context, "drawable", "sobot_logo_small_icon"));
        int largeicon = SharedPreferencesUtil.getIntData(context, ZhiChiConstant
                .SOBOT_NOTIFICATION_LARGE_ICON, ResourceUtils.getIdByName(context, "drawable", "sobot_logo_icon"));
        // 通过Notification.Builder来创建通知，注意API Level
        // API11之后才支持
//        int smallicon = ResourceUtils.getIdByName(context, "drawable", "sobot_logo_small_icon");
//        int largeicon = ResourceUtils.getIdByName(context, "drawable", "sobot_logo_icon");

        BitmapDrawable bd = (BitmapDrawable) context.getResources().getDrawable(largeicon);
        Bitmap bitmap = bd.getBitmap();
        Notification notify2 = new Notification.Builder(context)
                .setSmallIcon(smallicon) // 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap
                // icon)
                .setLargeIcon(bitmap)
                .setTicker(ticker)// 设置在status
                // bar上显示的提示文字
                .setContentTitle(title)// 设置在下拉status
                // bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
                .setContentText(content)// TextView中显示的详细内容
                .setContentIntent(pendingIntent2) // 关联PendingIntent
                //.setNumber(1) // 在TextView的右方显示的数字，可放大图片看，在最右侧。这个number同时也起到一个序列号的左右，如果多个触发多个通知（同一ID），可以指定显示哪一个。
                .getNotification(); // 需要注意build()是在API level
        // 16及之后增加的，在API11中可以使用getNotificatin()来代替
        notify2.flags |= Notification.FLAG_AUTO_CANCEL;
        /*String ss = SharedPreferencesUtil.getStringData(context,ConstantUtils.ALLOW_NOTIFICATION,"true");
        LogUtils.i("notification--------" + ss);
        if(SharedPreferencesUtil.getStringData(context,ConstantUtils.ALLOW_NOTIFICATION,"true").equals("true")) {
            LogUtils.i("notification--------info--open" + ss);
            if((SharedPreferencesUtil.getStringData(context,ConstantUtils.ALLOW_VIBRATE,"true").equals("true"))
                    && (SharedPreferencesUtil.getStringData(context,ConstantUtils.ALLOW_SOUND,"true").equals("true"))){
                notify2.defaults = Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND;
                LogUtils.i("notification--------all--open" + ss);
            }else if(SharedPreferencesUtil.getStringData(context,ConstantUtils.ALLOW_SOUND,"true").equals("true")) {
                notify2.defaults = Notification.DEFAULT_SOUND;
                LogUtils.i("notification--------sound--open" + ss);
            }else if(SharedPreferencesUtil.getStringData(context,ConstantUtils.ALLOW_VIBRATE,"true").equals("true")) {
                notify2.defaults = Notification.DEFAULT_VIBRATE;
                LogUtils.i("notification--------shake--open" + ss);
            }
        }*/
        notify2.defaults = Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND;
        manager.notify(id, notify2);
    }

    public static void cancleAllNotification(Context context){
        NotificationManager nm =(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();
    }
}