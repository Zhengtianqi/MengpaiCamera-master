package com.stickercamera.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.github.skykai.stickercamera.R;
import com.stickercamera.base.BaseActivity;

import cn.jarlen.photoedit.utils.FileUtils;
import cn.jarlen.photoedit.utils.PhotoUtils;

/**
 * 翻转
 */
public class RevolveActivity extends BaseActivity implements View.OnClickListener {

    private ImageView pictureShow;
    private Button revoleTest, unTest, fanTestUpDown, fanTestLeftRight;

    private String camera_path;
    private Bitmap srcBitmap, bit;

    private ImageButton cancelBtn, okBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_revolve);
        //初始化布局方法
        initView();

        Intent intent = getIntent();
        camera_path = intent.getStringExtra("camera_path");

        srcBitmap = BitmapFactory.decodeFile(camera_path);
        bit = srcBitmap;

        pictureShow.setImageBitmap(srcBitmap);
    }
    //初始化布局
    private void initView()
    {
        cancelBtn = (ImageButton) findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(this);

        okBtn = (ImageButton) findViewById(R.id.btn_ok);
        okBtn.setOnClickListener(this);

        pictureShow = (ImageView) findViewById(R.id.picture);

        revoleTest = (Button) findViewById(R.id.revoleTest);
        revoleTest.setOnClickListener(this);

        unTest = (Button) findViewById(R.id.unTest);
        unTest.setOnClickListener(this);

        fanTestUpDown = (Button) findViewById(R.id.fanTestUpDown);
        fanTestUpDown.setOnClickListener(this);

        fanTestLeftRight = (Button) findViewById(R.id.fanTestLeftRight);
        fanTestLeftRight.setOnClickListener(this);
    }
    //确定和取消按钮，同EnhanceActivity注释
    //翻转，左右反转，上下翻转，重置，调用PhotoUtils的图片翻转方法
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_cancel :

                Intent cancelData = new Intent();
                setResult(RESULT_CANCELED, cancelData);

                recycle();
                this.finish();
                break;

            case R.id.btn_ok :

                FileUtils.writeImage(bit, camera_path, 100);

                Intent intent = new Intent();
                intent.putExtra("camera_path", camera_path);
                setResult(Activity.RESULT_OK, intent);
                recycle();
                this.finish();
                break;

            case R.id.revoleTest ://翻转
                bit = PhotoUtils.rotateImage(bit, 90);//调用PhotoUtils的图片翻转方法，下同
                pictureShow.setImageBitmap(bit);//保存，下同
                break;

            case R.id.fanTestLeftRight ://左右反转
                bit = PhotoUtils.reverseImage(bit, -1, 1);
                pictureShow.setImageBitmap(bit);
                break;

            case R.id.fanTestUpDown ://上下翻转
                bit = PhotoUtils.reverseImage(bit, 1, -1);
                pictureShow.setImageBitmap(bit);

                break;
            case R.id.unTest ://重置
                bit = srcBitmap;
                pictureShow.setImageBitmap(bit);
                break;

            default :

                break;
        }

    }

    private void recycle()
    {
        if (srcBitmap != null)
        {
            srcBitmap.recycle();
            srcBitmap = null;
        }
        if (bit != null)
        {
            bit.recycle();
            bit = null;
        }
    }
}
