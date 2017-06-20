package com.stickercamera.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import com.github.skykai.stickercamera.R;

import cn.jarlen.photoedit.photoframe.PhotoFrame;
import cn.jarlen.photoedit.utils.FileUtils;

/**
 * 添加相框
 */
public class PhotoFrameActivity extends Activity implements View.OnClickListener {

    private PhotoFrame mImageFrame;
    private ImageView picture;

    private ImageView backBtn, okBtn;

    private Bitmap mBitmap;
    private Bitmap mTmpBmp;
    private String pathName = Environment.getExternalStorageDirectory()
            + "/DCIM/Camera/test.jpg";

    private String photoResPath;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_frame);
        //获取上个activity穿过来的内容
        Intent photoFrameIntent = getIntent();
        photoResPath = photoFrameIntent.getStringExtra("camera_path");

        BitmapFactory.Options mOption = new BitmapFactory.Options();
        mOption.inSampleSize = 1;
        //建立画布
        mBitmap = BitmapFactory.decodeFile(photoResPath, mOption);
        mTmpBmp = mBitmap;
        //初始化布局方法
        initView();
    }

    private void initView()
    {
        backBtn = (ImageView) findViewById(R.id.btn_cancel);
        backBtn.setOnClickListener(this);
        okBtn = (ImageView) findViewById(R.id.btn_ok);
        okBtn.setOnClickListener(this);
        picture = (ImageView) findViewById(R.id.picture);

        findViewById(R.id.photoRes_one).setOnClickListener(
                new PhotoFrameOnClickListener());
        findViewById(R.id.photoRes_two).setOnClickListener(
                new PhotoFrameOnClickListener());
        findViewById(R.id.photoRes_three).setOnClickListener(
                new PhotoFrameOnClickListener());

        reset();
        mImageFrame = new PhotoFrame(this, mBitmap);
    }

    /**
     * 重新设置一下图片
     */
    private void reset()
    {
        picture.setImageBitmap(mTmpBmp);
        picture.invalidate();
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

                FileUtils.writeImage(mTmpBmp, photoResPath, 100);

                Intent okData = new Intent();
                okData.putExtra("camera_path", photoResPath);
                setResult(RESULT_OK, okData);
                recycle();
                this.finish();
                break;

            default :
                break;
        }
    }
    //图片回收
    private void recycle()
    {
        mTmpBmp.recycle();
    }
    //三个图片的边框
    private class PhotoFrameOnClickListener implements View.OnClickListener
    {

        @Override
        public void onClick(View view)
        {
            switch (view.getId())
            {

                case R.id.photoRes_one ://从图片的各个方向添加边框

                    mImageFrame.setFrameType(PhotoFrame.FRAME_SMALL);
                    mImageFrame.setFrameResources(
                            R.drawable.frame_around1_left_top,
                            R.drawable.frame_around1_left,
                            R.drawable.frame_around1_left_bottom,
                            R.drawable.frame_around1_bottom,
                            R.drawable.frame_around1_right_bottom,
                            R.drawable.frame_around1_right,
                            R.drawable.frame_around1_right_top,
                            R.drawable.frame_around1_top);
                    mTmpBmp = mImageFrame.combineFrameRes();//把这些资源合成，小资源图片路径列表(说明: 图片顺序为:左上，左，左下，下，右下，右，右上，上)
                                                                     //下同
                    break;

                case R.id.photoRes_two :

                    mImageFrame.setFrameType(PhotoFrame.FRAME_SMALL);
                    mImageFrame.setFrameResources(
                            R.drawable.frame_around2_left_top,
                            R.drawable.frame_around2_left,
                            R.drawable.frame_around2_left_bottom,
                            R.drawable.frame_around2_bottom,
                            R.drawable.frame_around2_right_bottom,
                            R.drawable.frame_around2_right,
                            R.drawable.frame_around2_right_top,
                            R.drawable.frame_around2_top);
                    mTmpBmp = mImageFrame.combineFrameRes();

                    break;

                case R.id.photoRes_three :
                    mImageFrame.setFrameType(PhotoFrame.FRAME_BIG);
                    mImageFrame.setFrameResources(R.drawable.frame_big1);

                    mTmpBmp = mImageFrame.combineFrameRes();

                    break;

                default :
                    break;

            }
            reset();

        }

    }
}
