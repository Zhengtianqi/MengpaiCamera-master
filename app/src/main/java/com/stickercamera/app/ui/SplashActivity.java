package com.stickercamera.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.skykai.stickercamera.R;

public class SplashActivity extends Activity implements Animation.AnimationListener {


    /**
     * Called when the activity is first created.
     */
    private RelativeLayout r;
    private TextView tiaoguo;
    private ImageView imageView = null;
    private Animation alphaAnimation = null;
    @Override
    public void onCreate(Bundle icicle) {
        //取消顶部标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(icicle);
        /** Called when the activity is first created. */
        setContentView(R.layout.activity_splash);
        r=(RelativeLayout)findViewById(R.id.bg) ;
        imageView = (ImageView) findViewById(R.id.image_logo);
        tiaoguo= (TextView) findViewById(R.id.text_tiao);
        alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.myanim);
        alphaAnimation.setFillEnabled(true); //启动Fill保持
        alphaAnimation.setFillAfter(true);  //设置动画的最后一帧是保持在View上面
        imageView.setAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(this);  //为动画设置监听
        tiaoguo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    public void onAnimationStart (Animation animation){

    }

    @Override
    public void onAnimationEnd (Animation animation){
        //动画结束时结束欢迎界面点击并转到软件的主界面
     /*   Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        this.finish();*/
        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onAnimationRepeat (Animation animation){

    }

    @Override
    public boolean onKeyDown ( int keyCode, KeyEvent event){
        //在欢迎界面屏蔽BACK键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return false;
    }
}
