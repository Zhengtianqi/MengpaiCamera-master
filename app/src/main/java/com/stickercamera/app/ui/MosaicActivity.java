package com.stickercamera.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.github.skykai.stickercamera.R;

import cn.jarlen.photoedit.mosaic.DrawMosaicView;
import cn.jarlen.photoedit.mosaic.MosaicUtil;
import cn.jarlen.photoedit.utils.FileUtils;

/**
 * 马赛克
 */
public class MosaicActivity extends Activity implements Toolbar.OnMenuItemClickListener, View.OnClickListener {

    private Toolbar mToolbar;

    private DrawMosaicView mosaic;

    String mPath;
    private int mWidth, mHeight;

    Bitmap srcBitmap = null;

    private ImageButton cancelBtn, okBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_mosaic);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.inflateMenu(R.menu.menu_mosaic);
        mToolbar.setOnMenuItemClickListener(this);
        //初始化布局方法
        initView();
        //获取上个activity穿过来的照片路径
        Intent intent = getIntent();
        mPath = intent.getStringExtra("camera_path");
        mosaic.setMosaicBackgroundResource(mPath);
        srcBitmap = BitmapFactory.decodeFile(mPath);

        mWidth = srcBitmap.getWidth();
        mHeight = srcBitmap.getHeight();
        Bitmap bit = MosaicUtil.getMosaic(srcBitmap);
        //设置马赛克样式资源
        mosaic.setMosaicResource(bit);
        //设置画刷的宽度
        mosaic.setMosaicBrushWidth(10);
    }
    //初始化布局
    private void initView()
    {
        mosaic = (DrawMosaicView) findViewById(R.id.mosaic);

        cancelBtn = (ImageButton) findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(this);

        okBtn = (ImageButton) findViewById(R.id.btn_ok);
        okBtn.setOnClickListener(this);

    }

    int size = 5;
    //菜单点击事件
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_base://基本
                Bitmap bitmapMosaic = MosaicUtil.getMosaic(srcBitmap);
                mosaic.setMosaicResource(bitmapMosaic);//调用editphoto的module中方法改变图片参数，下同
                break;
            case R.id.action_ground_glass://毛玻璃
                Bitmap bitmapBlur = MosaicUtil.getBlur(srcBitmap);
                mosaic.setMosaicResource(bitmapBlur);
                break;
            case R.id.action_flower://花色
                Bitmap bit = BitmapFactory.decodeResource(this.getResources(),
                        R.drawable.hi4);
                bit = FileUtils.ResizeBitmap(bit, mWidth, mHeight);
                mosaic.setMosaicResource(bit);
                break;
            case R.id.action_size://大小
                if (size >= 30)
                {
                    size = 5;
                } else
                {
                    size += 5;
                }
                mosaic.setMosaicBrushWidth(size);
                break;
            case R.id.action_eraser:
                mosaic.setMosaicType(MosaicUtil.MosaicType.ERASER);
                break;
            default:
                break;
        }


        return false;
    }
    //确定和取消按钮，同EnhanceActivity注释
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_cancel:
                Intent cancelData = new Intent();
                setResult(RESULT_CANCELED, cancelData);

                recycle();
                this.finish();
                break;
            case R.id.btn_ok:
                Bitmap bit = mosaic.getMosaicBitmap();

                FileUtils.writeImage(bit, mPath, 100);

                Intent okData = new Intent();
                okData.putExtra("camera_path", mPath);
                setResult(RESULT_OK, okData);
                recycle();
                MosaicActivity.this.finish();
                break;
            default:

                break;
        }
    }
    //垃圾回收，图片资源内存回收
    private void recycle()
    {
        if (srcBitmap != null)
        {
            srcBitmap.recycle();
            srcBitmap = null;
        }
    }
}
