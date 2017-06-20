package com.stickercamera.app.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.skykai.stickercamera.R;
import com.stickercamera.app.view.TopView;

public class PintuActivity extends AppCompatActivity {

    private TopView toolBar;
    private Button picSelectBtn;
    private ImageView picShowImageView;
    private MyBroadCastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pintu);

        toolBar = (TopView) findViewById(R.id.top_view);
        picSelectBtn = (Button) findViewById(R.id.pic_select);
        picShowImageView = (ImageView) findViewById(R.id.pic_show);
        //设置toolbar
        toolBar.setTitle("拼图");
        toolBar.hide(toolBar.LEFT);

        picSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToPuzzle = new Intent(PintuActivity.this, PuzzlePickerActivity.class);
                startActivity(intentToPuzzle);
            }
        });
    }

    @Override
    protected void onStart() {
        if (broadcastReceiver == null) {
            broadcastReceiver = new MyBroadCastReceiver();
        }
        IntentFilter intentFilter = new IntentFilter("puzzle");
        registerReceiver(broadcastReceiver, intentFilter);
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        super.onDestroy();
    }

    private class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String picPath = intent.getStringExtra("picPath");
            if (picPath != null) {
                Glide.with(context)
                        .load(String.format("file://%s", picPath))
                        .crossFade()
                        .placeholder(R.mipmap.tubiao1)
                        .into(picShowImageView);

            }
        }
    }
}
