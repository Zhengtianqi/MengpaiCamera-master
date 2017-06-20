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
import android.widget.Toast;

import com.github.skykai.stickercamera.R;

import cn.jarlen.photoedit.crop.CropImageType;
import cn.jarlen.photoedit.crop.CropImageView;
import cn.jarlen.photoedit.utils.FileUtils;

/**
 * 剪切
 */
public class ImageCropActivity extends Activity implements Toolbar.OnMenuItemClickListener, View.OnClickListener {

    private Toolbar mToolbar;

    private CropImageView cropImage;

    private String mPath = null;

    private ImageButton cancleBtn, okBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crop_image);
        //需要用到toolbar，菜单 初始化它
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.inflateMenu(R.menu.menu_crop);
        mToolbar.setOnMenuItemClickListener(this);
        //获取上个activity穿过来的照片路径
        Intent intent = getIntent();
        mPath = intent.getStringExtra("camera_path");
        Bitmap bit = BitmapFactory.decodeFile(mPath);
        cropImage = (CropImageView) findViewById(R.id.cropmageView);
        cancleBtn = (ImageButton) findViewById(R.id.btn_cancel);
        cancleBtn.setOnClickListener(this);
        okBtn = (ImageButton) findViewById(R.id.btn_ok);
        okBtn.setOnClickListener(this);

        Bitmap hh = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.crop_button);//第一个参数是要加载的位图资源文件的对象；第二个需要加载的位图资源的Id。

        cropImage.setCropOverlayCornerBitmap(hh);
        cropImage.setImageBitmap(bit);

        cropImage.setGuidelines(CropImageType.CROPIMAGE_GRID_ON_TOUCH);// 触摸时显示网格

        cropImage.setFixedAspectRatio(false);// 自由剪切

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_freedom://自由剪切
                cropImage.setFixedAspectRatio(false);
                break;
            case R.id.action_1_1://1:1剪切
                cropImage.setFixedAspectRatio(true);
                cropImage.setAspectRatio(10, 10);
                break;
            case R.id.action_3_2://3:2剪切
                cropImage.setFixedAspectRatio(true);
                cropImage.setAspectRatio(30, 20);
                break;

            case R.id.action_4_3://4：3剪切
                cropImage.setFixedAspectRatio(true);
                cropImage.setAspectRatio(40, 30);
                break;
            case R.id.action_16_9://16:9剪切
                cropImage.setFixedAspectRatio(true);
                cropImage.setAspectRatio(160, 90);
                break;
            case R.id.action_rotate://旋转
                cropImage.rotateImage(90);
                break;
            case R.id.action_up_down://上下旋转
                cropImage.reverseImage(CropImageType.REVERSE_TYPE.UP_DOWN);
                break;
            case R.id.action_left_right://左右旋转
                cropImage.reverseImage(CropImageType.REVERSE_TYPE.LEFT_RIGHT);
                break;
            case R.id.action_crop://执行剪切
                Bitmap cropImageBitmap = cropImage.getCroppedImage();
                Toast.makeText(
                        this,
                        "已保存到相册；剪切大小为 " + cropImageBitmap.getWidth() + " x "
                                + cropImageBitmap.getHeight(),
                        Toast.LENGTH_SHORT).show();
                FileUtils.saveBitmapToCamera(this, cropImageBitmap, "crop.jpg");
                break;
        }
        return false;
    }
    //确定与取消按钮，同 EnhanceActivity注释
    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btn_cancel:
                Intent cancelData = new Intent();
                setResult(RESULT_CANCELED, cancelData);
                this.finish();
                break;
            case R.id.btn_ok:
                Bitmap bit = cropImage.getCroppedImage();
                FileUtils.writeImage(bit, mPath, 100);

                Intent okData = new Intent();
                okData.putExtra("camera_path", mPath);
                setResult(RESULT_OK, okData);
                this.finish();
                break;
            default:

                break;
        }
    }
}
