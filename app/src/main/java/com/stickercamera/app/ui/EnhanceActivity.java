package com.stickercamera.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.github.skykai.stickercamera.R;

import cn.jarlen.photoedit.enhance.PhotoEnhance;
import cn.jarlen.photoedit.utils.FileUtils;

/**
 * 增强
 */
public class EnhanceActivity extends Activity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {


    private ImageButton cancelBtn, okBtn;

    private ImageView pictureShow;

    private SeekBar saturationSeekBar, brightnessSeekBar, contrastSeekBar;

    private String imgPath;
    private Bitmap bitmapSrc;

    private PhotoEnhance pe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_enhance);
        //获取上个activity穿过来的照片路径
        Intent intent = getIntent();
        imgPath = intent.getStringExtra("camera_path");
        bitmapSrc = BitmapFactory.decodeFile(imgPath);
        //初始化布局方法
        initView();
        //把上个activity传过来的图片显示在当前页面
        pictureShow.setImageBitmap(bitmapSrc);
    }
//初始化组件，包括确定按钮和取消按钮，还有饱和度，亮度，对比度调节拖动按钮seekbar
    private void initView()
    {
        cancelBtn = (ImageButton) findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(this);
        okBtn = (ImageButton) findViewById(R.id.btn_ok);
        okBtn.setOnClickListener(this);

        pictureShow = (ImageView) findViewById(R.id.enhancePicture);

        saturationSeekBar = (SeekBar) findViewById(R.id.saturation);
        saturationSeekBar.setMax(255);
        saturationSeekBar.setProgress(128);
        saturationSeekBar.setOnSeekBarChangeListener(this);

        brightnessSeekBar = (SeekBar) findViewById(R.id.brightness);
        brightnessSeekBar.setMax(255);
        brightnessSeekBar.setProgress(128);
        brightnessSeekBar.setOnSeekBarChangeListener(this);

        contrastSeekBar = (SeekBar) findViewById(R.id.contrast);
        contrastSeekBar.setMax(255);
        contrastSeekBar.setProgress(128);
        contrastSeekBar.setOnSeekBarChangeListener(this);

        pe = new PhotoEnhance(bitmapSrc);

    }
    private int pregress = 0;
    private Bitmap bit = null;
    //
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser)
    {

        pregress = progress;
    }
    //拖动开始，图像没有变化 没有方法
    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {
        // TODO Auto-generated method stub

    }
    //拖动结束，调用editPhoto的module改变图片的饱和度，亮度，对比度
    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
        // TODO Auto-generated method stub

        int type = 0;

        switch (seekBar.getId())
        {
            case R.id.saturation :
                pe.setSaturation(pregress);
                type = pe.Enhance_Saturation;

                break;
            case R.id.brightness :
                pe.setBrightness(pregress);
                type = pe.Enhance_Brightness;

                break;

            case R.id.contrast :
                pe.setContrast(pregress);
                type = pe.Enhance_Contrast;

                break;

            default :
                break;
        }

        bit = pe.handleImage(type);
        pictureShow.setImageBitmap(bit);

    }
    //确认按钮和取消按钮
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {

            case R.id.btn_ok :
            //确认，保存图片，intent传值给首页
                FileUtils.writeImage(bit, imgPath, 100);
                Intent okData = new Intent();
                okData.putExtra("camera_path", imgPath);
                setResult(RESULT_OK, okData);
                recycle();
                this.finish();
                break;

            case R.id.btn_cancel :
                //取消，销毁
                Intent cancelData = new Intent();
                setResult(RESULT_CANCELED, cancelData);
                recycle();
                this.finish();
                break;

            default :
                break;
        }

    }
    //销毁图片，垃圾回收
    private void recycle()
    {
        if (bitmapSrc != null)
        {
            bitmapSrc.recycle();//图片占用内存回收
            bitmapSrc = null;
        }

        if (bit != null)
        {
            bit.recycle();
            bit = null;
        }
    }
}
