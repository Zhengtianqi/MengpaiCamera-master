package com.sobot.chat.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

/**
 * 关于动画的工具类
 */
public class AnimationUtil {
    //旋转动画
    public static void rotate(View v){
        //创建旋转动画 对象   fromDegrees:旋转开始的角度  toDegrees:结束的角度
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        //设置动画的显示时间
        rotateAnimation.setDuration(1000);
        //设置动画重复播放几次
        rotateAnimation.setRepeatCount(RotateAnimation.INFINITE);
        //设置动画插值器
        rotateAnimation.setInterpolator(new LinearInterpolator());
        //设置动画重复播放的方式,翻转播放
        rotateAnimation.setRepeatMode(Animation.RESTART);
        //拿着imageview对象来运行动画效果
        v.setAnimation(rotateAnimation);
    }
}