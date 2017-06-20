package com.stickercamera.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.github.skykai.stickercamera.R;
import com.sobot.chat.SobotApi;
import com.sobot.chat.api.enumtype.SobotChatTitleDisplayMode;
import com.sobot.chat.api.model.Information;
import com.stickercamera.base.BaseActivity;

/**
 * Created by 郑天祺 on 2017/6/1.
 */

public class MainActivity extends BaseActivity implements View.OnClickListener{
    private ImageView button;
    private ImageView button1;
    private ImageView button2;
    private ImageView button3;
    private ImageView login_iv;
    String data="000";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        button=(ImageView)findViewById(R.id.imageView);
        button1=(ImageView)findViewById(R.id.imageView3);
        button2=(ImageView)findViewById(R.id.imageView4);
        button3=(ImageView)findViewById(R.id.imageView5);
        login_iv=(ImageView)findViewById(R.id.login_iv);

        button.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        login_iv.setOnClickListener(this);
        String data = getIntent().getStringExtra("name");
    }
    //四个跳转
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imageView :
                Intent i0 = new Intent();
                i0.setClass(MainActivity.this,EditCamera.class);
                startActivity(i0);
                break;
            case R.id.imageView3 :
                Intent i1 = new Intent();
                i1.setClass(MainActivity.this,PintuActivity.class);
                startActivity(i1);
                break;
            case R.id.imageView4 :
                Intent i = new Intent();
                i.setClass(MainActivity.this,StickerCamera.class);
                startActivity(i);
                break;
            case R.id.imageView5 :
                //智能客服初始化
                Information info = new Information();
                info.setAppkey("ea9b2f3794e64ec2b20a4cf11eff8915");//智齿客服申请的key
                //设置标题栏的背景图片和颜色
                info.setTitleImgId(R.drawable.bar3);
                info.setColor("#ff9e6a");
                info.setUname(data);

                SobotApi.setChatTitleDisplayMode(MainActivity.this, SobotChatTitleDisplayMode.Default,"萌拍小客服");
                SobotApi.startSobotChat(MainActivity.this,info);
                break;
            case R.id.login_iv:
                Intent i2 = new Intent();
                i2.setClass(MainActivity.this,LoginActivity.class);
                startActivity(i2);
                break;
        }
    }
}
