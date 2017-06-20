package com.stickercamera.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.skykai.stickercamera.R;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.stickercamera.app.view.VerticalSeekBar;

import cn.jarlen.photoedit.filters.FilterType;
import cn.jarlen.photoedit.filters.NativeFilter;
import cn.jarlen.photoedit.utils.FileUtils;

/**
 * 滤镜
 */
public class ImageFilterActivity extends Activity implements View.OnClickListener {
    private ImageView pictureShow;
    private String picturePath = null;
    private Bitmap pictureBitmap = null;
    private ImageButton backBtn, okBtn;
    private VerticalSeekBar mVerticalSeekBar;
    private TextView mSeekBarProgress;
    private NativeFilter nativeFilters = new NativeFilter();
    private int srcWidth, srcHeight;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_filter);
        //初始化布局方法
        initializateView();
        findDate();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }
    //初始化布局
    private void initializateView() {
        pictureShow = (ImageView) findViewById(R.id.pictureShow);
        mVerticalSeekBar = (VerticalSeekBar) findViewById(R.id.verticalSeekBar);
        mSeekBarProgress = (TextView) findViewById(R.id.verticalSeekBarProgressText);

        backBtn = (ImageButton) findViewById(R.id.btn_cancel);
        backBtn.setOnClickListener(this);

        okBtn = (ImageButton) findViewById(R.id.btn_ok);
        okBtn.setOnClickListener(this);
        //设置seekbar的最大值和滑动监听，调用 updatePicture方法进行图片编辑
        mVerticalSeekBar.setMax(100);

        mVerticalSeekBar
                .setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    int mProgress = 0;

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mSeekBarProgress.setText(mProgress + "%");

                        float degree = mProgress / 100.0f;

                        updatePicture(degree);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progress, boolean fromUser) {
                        mProgress = progress;

                    }
                });

        initFiltersView();
    }

    Bitmap newBitmap;

    private void findDate() {
        Intent filterIntent = getIntent();

        picturePath = filterIntent.getStringExtra("camera_path");
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inSampleSize = 1;
        pictureBitmap = BitmapFactory.decodeFile(picturePath, option);

        newBitmap = pictureBitmap;
        srcWidth = pictureBitmap.getWidth();
        srcHeight = pictureBitmap.getHeight();

        Log.i("jarlen", "srcWidth = " + srcWidth + " srcHeight = " + srcHeight);

        pictureShow.setImageBitmap(newBitmap);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                Intent cancelData = new Intent();
                setResult(RESULT_CANCELED, cancelData);
                recycle();
                this.finish();

                break;
            case R.id.btn_ok:

                FileUtils.writeImage(resultImg, picturePath, 100);
                Intent intent = new Intent();
                intent.putExtra("camera_path", picturePath);
                setResult(Activity.RESULT_OK, intent);
                recycle();
                this.finish();
                break;

            default:
                break;
        }

    }

    private int filterType = FilterType.FILTER4GRAY;
    Bitmap resultImg = null;
    //更新图片的方法，没有真正改变，只传值
    private void updatePicture(float degree) {
        int[] dataResult = null;
        int[] pix = new int[srcWidth * srcHeight];
        newBitmap.getPixels(pix, 0, srcWidth, 0, 0, srcWidth, srcHeight);
        //不同的滤镜效果
        switch (filterType) {
            case FilterType.FILTER4GRAY://灰效果

                dataResult = nativeFilters.gray(pix, srcWidth, srcHeight,
                        degree);

                break;
            case FilterType.FILTER4MOSATIC://马赛克效果

                int mosatic = (int) (degree * 30);
                dataResult = nativeFilters.mosatic(pix, srcWidth, srcHeight,
                        mosatic);
                break;

            case FilterType.FILTER4LOMO://LOMO 效果

                dataResult = nativeFilters.lomo(pix, srcWidth, srcHeight,
                        degree);
                break;
            case FilterType.FILTER4NOSTALGIC://怀旧效果

                dataResult = nativeFilters.nostalgic(pix, srcWidth,
                        srcHeight, degree);
                break;
            case FilterType.FILTER4COMICS://连环画效果 Comics
                dataResult = nativeFilters.comics(pix, srcWidth, srcHeight,
                        degree);
                break;
            case FilterType.FILTER4BlackWhite://黑白效果
//                dataResult = nativeFilters.ToBlackWhite(pix, srcWidth,
//                        srcHeight, degree);
                break;

            case FilterType.FILTER4NEGATIVE://反色(底片)效果
//                dataResult = nativeFilters.negative(pix, srcWidth, srcHeight,
//                        degree);
                break;
            case FilterType.FILTER4BROWN://流年风格
                dataResult = nativeFilters.brown(pix, srcWidth, srcHeight,
                        degree);
                break;

            case FilterType.FILTER4SKETCH_PENCIL://素描效果---铅笔画
                dataResult = nativeFilters.sketchPencil(pix, srcWidth,
                        srcHeight, degree);
                break;

            case FilterType.FILTER4OVEREXPOSURE://过度曝光
//                dataResult = nativeFilters.ToOverExposure(pix, srcWidth,
//                        srcHeight, degree);
                break;
            case FilterType.FILTER4WHITELOG://log曲线美白效果
//                dataResult = nativeFilters.ToWhiteLOG(pix, srcWidth, srcHeight,
//                        FilterType.BeitaOfWhiteLOG, degree);
                break;

            // //

            case FilterType.FILTER4SOFTNESS:
//                dataResult = nativeFilters.ToSoftness(pix, srcWidth, srcHeight,
//                        degree);
                break;

            case FilterType.FILTER4NiHong:
//                dataResult = nativeFilters.ToNiHong(pix, srcWidth, srcHeight,
//                        degree);
                break;

            case FilterType.FILTER4SKETCH:
//                dataResult = nativeFilters.ToSketch(pix, srcWidth, srcHeight,
//                        degree);
                break;
            // case FilterType.FILTER4CARVING :
            // dataResult = nativeFilters.ToCarving(pix, w, h, degree);
            // break;
            case FilterType.FILTER4RELIEF:
//                dataResult = nativeFilters.ToRelief(pix, srcWidth, srcHeight,
//                        degree);
                break;
            // case FilterType.FILTER4RUIHUA :
            // dataResult = nativeFilters.ToRuiHua(pix, w, h, degree);
            // break;

            default:
                break;
        }
        //创建画布，更新UI
        resultImg = Bitmap.createBitmap(dataResult, srcWidth, srcHeight,
                Bitmap.Config.ARGB_8888);

        // FilterType filterType = new FilterType();
        // Bitmap resultImg = filterType.createPencli(newBitmap);

        pictureShow.setImageBitmap(resultImg);

    }

    private TextView filterWhite, filterGray, filterMosatic, filterLOMO,
            filterNostalgic, filterComics, filterBlackWhite, filterNegative,
            filterBrown, filterSketchPencil, filterOverExposure,
            filterSoftness, filterNiHong, filterSketch, filterCarving,
            filterSelief, filterRuiHua;
    //更新滤镜之后的带效果图片
    private void initFiltersView() {
//        filterWhite = (TextView) findViewById(R.id.filterWhite);
//        filterWhite.setOnClickListener(filterOnClickListener);

        filterGray = (TextView) findViewById(R.id.filterGray);
        filterGray.setOnClickListener(filterOnClickListener);

        filterMosatic = (TextView) findViewById(R.id.filterMosatic);
        filterMosatic.setOnClickListener(filterOnClickListener);

        filterLOMO = (TextView) findViewById(R.id.filterLOMO);
        filterLOMO.setOnClickListener(filterOnClickListener);

        filterNostalgic = (TextView) findViewById(R.id.filterNostalgic);
        filterNostalgic.setOnClickListener(filterOnClickListener);

        filterComics = (TextView) findViewById(R.id.filterComics);
        filterComics.setOnClickListener(filterOnClickListener);

        filterBlackWhite = (TextView) findViewById(R.id.filterBlackWhite);
        filterBlackWhite.setOnClickListener(filterOnClickListener);

        filterNegative = (TextView) findViewById(R.id.filterNegative);
        filterNegative.setOnClickListener(filterOnClickListener);

        filterBrown = (TextView) findViewById(R.id.filterBrown);
        filterBrown.setOnClickListener(filterOnClickListener);

        filterSketchPencil = (TextView) findViewById(R.id.filterSketchPencil);
        filterSketchPencil.setOnClickListener(filterOnClickListener);

        filterOverExposure = (TextView) findViewById(R.id.filterOverExposure);
        filterOverExposure.setOnClickListener(filterOnClickListener);

        // ////

        filterSoftness = (TextView) findViewById(R.id.filterSoftness);
        filterSoftness.setOnClickListener(filterOnClickListener);

        filterNiHong = (TextView) findViewById(R.id.filterNiHong);
        filterNiHong.setOnClickListener(filterOnClickListener);

        filterSketch = (TextView) findViewById(R.id.filterSketch);
        filterSketch.setOnClickListener(filterOnClickListener);
    }

    private FilterOnClickListener filterOnClickListener = new FilterOnClickListener();
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("ImageFilter Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
    //点击按钮使用不同的滤镜
    private class FilterOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            // Log.i("jarlen", " view.getId = " + view.getId());
            switch (view.getId()) {
                case R.id.filterWhite:
                    filterType = FilterType.FILTER4WHITELOG;
                    break;
                case R.id.filterGray:
                    filterType = FilterType.FILTER4GRAY;
                    break;
                case R.id.filterBlackWhite:
                    filterType = FilterType.FILTER4BlackWhite;
                    break;
                case R.id.filterMosatic:
                    filterType = FilterType.FILTER4MOSATIC;
                    break;
                case R.id.filterComics:
                    filterType = FilterType.FILTER4COMICS;
                    break;
                case R.id.filterBrown:
                    filterType = FilterType.FILTER4BROWN;
                    break;
                case R.id.filterLOMO:
                    filterType = FilterType.FILTER4LOMO;
                    break;
                case R.id.filterNegative:
                    filterType = FilterType.FILTER4NEGATIVE;
                    break;
                case R.id.filterNostalgic:
                    filterType = FilterType.FILTER4NOSTALGIC;
                    break;
                case R.id.filterOverExposure:
                    filterType = FilterType.FILTER4OVEREXPOSURE;
                    break;
                case R.id.filterSketchPencil:
                    filterType = FilterType.FILTER4SKETCH_PENCIL;
                    break;

                // +6 by jarlen 2014/11/9
                case R.id.filterSoftness:
                    filterType = FilterType.FILTER4SOFTNESS;
                    break;
                case R.id.filterNiHong:
                    filterType = FilterType.FILTER4NiHong;
                    break;
                case R.id.filterSketch:
                    filterType = FilterType.FILTER4SKETCH;
                    break;
                // case R.id.filterCarving :
                // filterType = FilterType.FILTER4CARVING;
                // break;
                // case R.id.filterSelief :
                // filterType = FilterType.FILTER4RELIEF;
                // break;
                // case R.id.filterRuiHua :
                // filterType = FilterType.FILTER4RUIHUA;
                // break;

                default:
                    break;
            }

            updatePicture(1);
            mVerticalSeekBar.setProgress(100);
            mSeekBarProgress.setText(100 + "%");
            mVerticalSeekBar.setProgressAndThumb(mVerticalSeekBar.getMax());

        }

    }
    //关闭activity时清理缓存
    private void recycle() {
        if (pictureBitmap != null) {
            pictureBitmap.recycle();
            pictureBitmap = null;
        }

        if (newBitmap != null) {
            newBitmap.recycle();
            newBitmap = null;
        }

    }

}
