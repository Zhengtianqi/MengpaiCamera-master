package com.stickercamera.app.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.skykai.stickercamera.R;
import com.stickercamera.base.BaseActivity;
import com.stickercamera.base.util.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import cn.jarlen.photoedit.operate.OperateUtils;
/**
 * 图片编辑
 */
public class EditCamera extends BaseActivity implements View.OnClickListener, Toolbar.OnMenuItemClickListener {

    private LinearLayout content_layout;
    private Button addPictureFromPhotoBtn;
    private ImageView pictureShow;
    private Class<?> intentClass;
    private int intentType = 0;

    /* 用来标识请求照相功能的activity */
    private static final int CAMERA_WITH_DATA = 3023;

    /* 用来标识请求gallery的activity */
    private static final int PHOTO_PICKED_WITH_DATA = 3021;

    /* 边框 */
    private static final int PHOTO_FRAME_WITH_DATA = 3024;

    /* 马赛克 */
    private static final int PHOTO_MOSAIC_WITH_DATA = 3025;

    /* 涂鸦 */
    private static final int PHOTO_DRAW_WITH_DATA = 3026;

    /* 剪切 */
    private static final int PHOTO_CROP_WITH_DATA = 3027;

    /* 滤镜 */
    private static final int PHOTO_FILTER_WITH_DATA = 3028;

    /* 增强 */
    private static final int PHOTO_ENHANCE_WITH_DATA = 3029;

    /* 旋转 */
    private static final int PHOTO_REVOLVE_WITH_DATA = 3030;

    /* 图像变形 */
    private static final int PHOTO_WARP_WITH_DATA = 3031;

    /* 添加水印图片 */
    private static final int PHOTO_ADD_WATERMARK_DATA = 3032;

    /* 添加文字 */
    private static final int PHOTO_ADD_TEXT_DATA = 3033;

    /*  测试接口 */
    private static final int PHOTO_TEST_TEXT_DATA = 3034;

    /* 照相机拍照得到的图片 */
    private File mCurrentPhotoFile;
    private String photoPath = null, tempPhotoPath, camera_path;

    private int scale = 2;
    int width = 0;

    OperateUtils operateUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoedit);
        //设置toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.edit_toolbar);

        toolbar.setNavigationIcon(R.drawable.tubiao1);//设置toolbar图标
        toolbar.setTitle("美化");//设置toolbar标题
        toolbar.inflateMenu(R.menu.base_toolbar_menu);//设置toolbar菜单
        toolbar.setOnMenuItemClickListener(this);//设置toolbar菜单点击事件
        //设置图片显示大小
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = metric.widthPixels; // 获取屏幕宽度（像素）

        pictureShow = (ImageView) findViewById(R.id.pictureShow);
        content_layout = (LinearLayout) findViewById(R.id.mainLayout);
        addPictureFromPhotoBtn = (Button) findViewById(R.id.addPictureFromPhoto);
        addPictureFromPhotoBtn.setOnClickListener(this);
        operateUtils = new OperateUtils(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addPictureFromPhoto:
                getPictureFromPhoto();
                break;
            default:
                break;
        }

    }

    /* 从相册中获取照片 */
    private void getPictureFromPhoto() {
        Intent openphotoIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//调用系统相册
        startActivityForResult(openphotoIntent, PHOTO_PICKED_WITH_DATA);//返回
    }
    /**
     * 根据路径获取图片并且压缩，适应view
     * @return Bitmap 压缩后的图片
     */
    //调用的CommonUtil工具类进行图片压缩
    private void compressed() {
        Bitmap resizeBmp = operateUtils.compressionFiller(photoPath, content_layout);//photoPath是图片路径，content_layout是适应的view
        pictureShow.setImageBitmap(resizeBmp);
        camera_path = SaveBitmap(resizeBmp, "saveTemp");
    }
    //新建一个线程来执行上compress压缩图片的方法
    final Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (content_layout.getWidth() != 0) {
                    Log.i("LinearLayoutW", content_layout.getWidth() + "");
                    Log.i("LinearLayoutH", content_layout.getHeight() + "");
                    // 取消定时器
                    timer.cancel();
                    compressed();
                }
            }
        }
    };
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        public void run() {
            Message message = new Message();
            message.what = 1;
            myHandler.sendMessage(message);
        }
    };

    // 将生成的图片保存到内存中，记录当前的图片路径
    public String SaveBitmap(Bitmap bitmap, String name) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File dir = new File(Constants.filePath);
            if (!dir.exists())
                dir.mkdir();
            File file = new File(Constants.filePath + name + ".jpg");
            FileOutputStream out;
            try {
                out = new FileOutputStream(file);
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
                    out.flush();
                    out.close();
                }
                return file.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case CAMERA_WITH_DATA://用来标识请求照相功能的activity

                photoPath = tempPhotoPath;
                if (content_layout.getWidth() == 0) {
                    timer.schedule(task, 10, 1000);
                } else {
                    compressed();
                }

                break;

            case PHOTO_PICKED_WITH_DATA:// 用来标识请求gallery的activity

                Uri selectedImage = data.getData();
                String[] filePathColumns = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePathColumns[0]);
                photoPath = c.getString(columnIndex);
                c.close();

                // 延迟每次延迟10 毫秒 隔1秒执行一次
                if (content_layout.getWidth() == 0) {
                    timer.schedule(task, 10, 1000);
                } else {
                    compressed();
                }
                break;
            //之后的是菜单点击事件，让选中几个的图片，把图片保存到当前activity
            case PHOTO_FRAME_WITH_DATA:
            case PHOTO_MOSAIC_WITH_DATA:
            case PHOTO_DRAW_WITH_DATA:
            case PHOTO_CROP_WITH_DATA:
            case PHOTO_FILTER_WITH_DATA:
            case PHOTO_ENHANCE_WITH_DATA:
            case PHOTO_REVOLVE_WITH_DATA:
            case PHOTO_WARP_WITH_DATA:
            case PHOTO_ADD_WATERMARK_DATA:
            case PHOTO_ADD_TEXT_DATA:
            case PHOTO_TEST_TEXT_DATA:

                String resultPath = data.getStringExtra("camera_path");
                Bitmap resultBitmap = BitmapFactory.decodeFile(resultPath);
                pictureShow.setImageBitmap(resultBitmap);
                break;

            default:
                break;
        }

    }
    //菜单的点击事件，将当期页面图片传值到下一个Activity
    @Override
    public boolean onMenuItemClick(MenuItem item) {
    //判断图片是否被选中
        if (photoPath == null) {
            Toast.makeText(EditCamera.this, "请选择图片", Toast.LENGTH_SHORT)
                    .show();
            return true;
        }
        //右上角菜单的点击事件
        switch (item.getItemId()) {
            case R.id.action_filter:
                intentClass = ImageFilterActivity.class;
                intentType = PHOTO_FILTER_WITH_DATA;
                break;
            case R.id.action_wrap:
                intentClass = WarpActivity.class;
                intentType = PHOTO_WARP_WITH_DATA;
                break;
            case R.id.action_crop:
                intentClass = ImageCropActivity.class;
                intentType = PHOTO_CROP_WITH_DATA;
                break;
            case R.id.action_draw:
                intentClass = DrawBaseActivity.class;
                intentType = PHOTO_DRAW_WITH_DATA;
                break;
            case R.id.action_frame:
                intentClass = PhotoFrameActivity.class;
                intentType = PHOTO_FRAME_WITH_DATA;
                break;
            case R.id.action_addtv:
                intentClass = AddTextActivity.class;
                intentType = PHOTO_ADD_TEXT_DATA;
                break;
            case R.id.action_addwm:
                intentClass = AddWatermarkActivity.class;
                intentType = PHOTO_ADD_WATERMARK_DATA;
                break;
            case R.id.action_mosaic:
                intentClass = MosaicActivity.class;
                intentType = PHOTO_MOSAIC_WITH_DATA;
                break;
            case R.id.action_enchance:
                intentClass = EnhanceActivity.class;
                intentType = PHOTO_ENHANCE_WITH_DATA;
                break;
            case R.id.action_rotate:
                intentClass = RevolveActivity.class;
                intentType = PHOTO_REVOLVE_WITH_DATA;
                break;
            default:
                intentClass = null;
                intentType = 0;
                break;
        }

        // 将图片路径photoPath传到所要调试的模块
        Intent photoFrameIntent = new Intent(EditCamera.this,
                intentClass);
        photoFrameIntent.putExtra("camera_path", camera_path);
        EditCamera.this.startActivityForResult(photoFrameIntent,
                intentType);

        return false;
    }
}
