package com.stickercamera.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.github.skykai.stickercamera.R;

import java.util.Timer;
import java.util.TimerTask;

import cn.jarlen.photoedit.operate.OperateUtils;
import cn.jarlen.photoedit.scrawl.DrawAttribute;
import cn.jarlen.photoedit.scrawl.DrawingBoardView;
import cn.jarlen.photoedit.scrawl.ScrawlTools;
import cn.jarlen.photoedit.utils.FileUtils;

/**
 * 涂鸦
 */
public class DrawBaseActivity extends Activity implements Toolbar.OnMenuItemClickListener, View.OnClickListener {

    private Toolbar mToolbar;
    private DrawingBoardView drawView;

    ScrawlTools casualWaterUtil = null;
    private LinearLayout drawLayout;
    String mPath;
    private ImageButton cancelBtn, okBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_draw);
        //设置toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.inflateMenu(R.menu.menu_draw);
        mToolbar.setOnMenuItemClickListener(this);

        drawView = (DrawingBoardView) findViewById(R.id.drawView);
        drawLayout = (LinearLayout) findViewById(R.id.drawLayout);

        cancelBtn = (ImageButton) findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(this);

        okBtn = (ImageButton) findViewById(R.id.btn_ok);
        okBtn.setOnClickListener(this);
        //获取上页传过来的图片
        Intent intent = getIntent();

        mPath = intent.getStringExtra("camera_path");

        timer.schedule(task, 10, 1000);// 从firstTime时刻开始，每隔period毫秒执行一次。
    }

    final Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (drawLayout.getWidth() != 0) {
                    // Log.i("jarlen", drawLayout.getWidth() + "");
                    // Log.i("jarlen", drawLayout.getHeight() + "");
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

    private void compressed() {
        //设置要添加的View的高和宽
        OperateUtils operateUtils = new OperateUtils(this);
        Bitmap bit = BitmapFactory.decodeFile(mPath);
        Bitmap resizeBmp = operateUtils.compressionFiller(bit, drawLayout);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                resizeBmp.getWidth(), resizeBmp.getHeight());
        drawView.setLayoutParams(layoutParams);
        //创建画笔和绘图的图片
        casualWaterUtil = new ScrawlTools(this, drawView, resizeBmp);

        Bitmap paintBitmap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.crayon);
        //运用导入的EditPhoto的module进行图片的绘制
        casualWaterUtil.creatDrawPainter(DrawAttribute.DrawStatus.PEN_WATER,
                paintBitmap, 0xffadb8bd);
    }
    //菜单点击事件
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_paint_one: //画笔种类，第一种画笔
                Bitmap paintBitmap1 = BitmapFactory.decodeResource(
                        this.getResources(), R.drawable.marker);
                casualWaterUtil.creatDrawPainter(
                        DrawAttribute.DrawStatus.PEN_WATER, paintBitmap1,
                        0xffadb8bd);
                break;
            case R.id.action_paint_two: //画笔种类，第二种画笔
                Bitmap paintBitmap2 = BitmapFactory.decodeResource(
                        this.getResources(), R.drawable.crayon);
                casualWaterUtil.creatDrawPainter(
                        DrawAttribute.DrawStatus.PEN_CRAYON, paintBitmap2,
                        0xffadb8bd);
                break;
            case R.id.action_size://画笔大小
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inSampleSize = 2;
                Bitmap paintBitmap3 = BitmapFactory.decodeResource(
                        this.getResources(), R.drawable.marker, option);
                casualWaterUtil.creatDrawPainter(
                        DrawAttribute.DrawStatus.PEN_WATER, paintBitmap3,
                        0xffadb8bd);
                break;
            case R.id.action_eraser://橡皮
                Bitmap paintBitmap6 = BitmapFactory.decodeResource(
                        this.getResources(), R.drawable.eraser);

                casualWaterUtil.creatDrawPainter(
                        DrawAttribute.DrawStatus.PEN_ERASER, paintBitmap6,
                        0xffadb8bd);
                break;
            case R.id.action_color://颜色
                Bitmap paintBitmap4 = BitmapFactory.decodeResource(
                        this.getResources(), R.drawable.marker);
                casualWaterUtil.creatDrawPainter(
                        DrawAttribute.DrawStatus.PEN_WATER, paintBitmap4,
                        0xff002200);
                break;

            case R.id.action_pic://贴图
                int[] res = new int[]{R.drawable.stamp0star,
                        R.drawable.stamp1star, R.drawable.stamp2star,
                        R.drawable.stamp3star};

                casualWaterUtil.creatStampPainter(
                        DrawAttribute.DrawStatus.PEN_STAMP, res, 0xff00ff00);
                break;

            default:

                break;
        }
        return false;
    }
//确定和取消按钮，同EnhanceActivity注释
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_cancel:
                Intent cancelData = new Intent();
                setResult(RESULT_CANCELED, cancelData);

                this.finish();

                break;
            case R.id.btn_ok:
                Bitmap bit = casualWaterUtil.getBitmap();

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
