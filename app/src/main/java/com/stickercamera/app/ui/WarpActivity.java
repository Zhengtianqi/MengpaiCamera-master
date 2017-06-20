package com.stickercamera.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.github.skykai.stickercamera.R;

import cn.jarlen.photoedit.utils.FileUtils;
import cn.jarlen.photoedit.warp.Picwarp;
import cn.jarlen.photoedit.warp.WarpView;

/**
 * 图像变形
 */
public class WarpActivity extends Activity implements View.OnClickListener {

    private String TAG = WarpActivity.class.getSimpleName();
    private boolean debug = true;

    String pathName = Environment.getExternalStorageDirectory()
            + "/DCIM/Camera/test.jpg";

    private WarpView image;
    boolean mSaving; // Whether the "save" button is already clicked.
    private Picwarp warp = new Picwarp();

    private ImageButton cancelBtn, okBtn;

    private String warpPicturePath;
    Bitmap pictureBitmap,newBitmap;
    private static final int scale = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_warp);
        //初始化布局方法
        initView();
        initDate();
    }
    //初始化布局
    private void initView()
    {
        Intent warpIntent = getIntent();
        warpPicturePath = warpIntent.getStringExtra("camera_path");

        image = (WarpView) findViewById(R.id.warp_image);

        cancelBtn = (ImageButton) findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(this);

        okBtn = (ImageButton) findViewById(R.id.btn_ok);
        okBtn.setOnClickListener(this);

    }
    //初始化图片
    private void initDate()
    {
        pictureBitmap = BitmapFactory
                .decodeFile(warpPicturePath);

        newBitmap = pictureBitmap;
        warp.initArray();
        //此方法可以调用到editphoto的onDraw（）方法，进行绘图
        image.setWarpBitmap(newBitmap);
    }
    //垃圾回收
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (debug)
            Log.d(TAG, "onDestroy");

        if (newBitmap != null)
        {
            newBitmap.recycle();
            newBitmap = null;
            System.gc();
        }
    }
    //确定和取消按钮，同EnhanceActivity注释
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

                Bitmap bit = image.getWrapBitmap();
                FileUtils.writeImage(bit, warpPicturePath, 100);

                Intent okData = new Intent();
                okData.putExtra("camera_path", warpPicturePath);
                setResult(RESULT_OK, okData);

                recycle();
                this.finish();
                break;

            default :
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        //设置菜单，因为少没有放在xml资源文件中
        menu.add(0, 1, 1, "重置");
//		menu.add(0, 2, 2, "保存");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case 1 :
                //图片重置，调用editphoto中的方法
                image.setWarpBitmap(pictureBitmap);
                image.invalidate();

                break;
            default :
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //内存回收机制，当点击取消按钮时候销毁本activity的图片
    private void recycle()
    {
        if (newBitmap != null)
        {
            newBitmap.recycle();
            newBitmap = null;
        }

        if(pictureBitmap != null)
        {
            pictureBitmap.recycle();
            pictureBitmap = null;
        }
    }
}
