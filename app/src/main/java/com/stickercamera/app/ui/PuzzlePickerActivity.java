package com.stickercamera.app.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.skykai.stickercamera.R;
import com.stickercamera.app.adapter.AllPicAdapter;
import com.stickercamera.app.adapter.SelectAdapter;
import com.stickercamera.app.entity.ImageBean;
import com.stickercamera.app.entity.ImageGroup;
import com.stickercamera.app.view.TopView;
import com.stickercamera.base.util.PicCursorUtil;

import java.util.ArrayList;

/**
 * 图片选择界面
 */
public class PuzzlePickerActivity extends Activity implements View.OnClickListener {

    private TopView topView;
    private TextView picChooseTv;
    private TextView startTv;
    private RecyclerView picAllRv;
    private RecyclerView picSelectRv;
    private final static int MIN_CHOOSE_PIC = 2;
    private final static int MAX_CHOOSE_PIC = 5;
    private int choosePicNum = 0;
    private Context context;
    private ImageGroup mCurrentGroup;
    private AllPicAdapter allPicAdapter;
    private ArrayList<ImageBean> choosePicList;
    private SelectAdapter selectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle_picker);

        init();
    }

    private void init() {

        context = PuzzlePickerActivity.this;
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        topView = (TopView) findViewById(R.id.top_view);
        picChooseTv = (TextView) findViewById(R.id.pic_choose_tv);
        startTv = (TextView) findViewById(R.id.start_tv);
        picAllRv = (RecyclerView) findViewById(R.id.pic_all_rv);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 4);
        picAllRv.setLayoutManager(gridLayoutManager);
        picSelectRv = (RecyclerView) findViewById(R.id.pic_select_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        picSelectRv.setLayoutManager(linearLayoutManager);
    }

    private void initData() {

        topView.setTitle("选择图片");

        setChoosePicText(0);
        mCurrentGroup = new ImageGroup();
        allPicAdapter = new AllPicAdapter(context, mCurrentGroup.getImageSets());
        picAllRv.setAdapter(allPicAdapter);
        new getCurrentGrop().execute();

        choosePicList = new ArrayList<>();
        selectAdapter = new SelectAdapter(context, choosePicList);
        picSelectRv.setAdapter(selectAdapter);
    }

    private void initEvent() {

        startTv.setOnClickListener(this);

        topView.setOnLeftClickListener(new TopView.OnLeftClickListener() {
            @Override
            public void leftClick() {
                finish();
            }
        });

        allPicAdapter.setOnRvItemClickListener(new AllPicAdapter.OnRvItemClickListener() {
            @Override
            public void onItemClick(int position) {
                addPic(position);
            }
        });

        selectAdapter.setOnRvItemClickListener(new SelectAdapter.OnRvItemClickListener() {
            @Override
            public void onItemClick(int position) {
                deletePic(position);
            }
        });
    }

    private void setChoosePicText(int picNum) {
        picChooseTv.setText(String.format("已选择%d张照片(最多选择5张)", picNum));
    }

    private void intentToPuzzle() {

        if (choosePicNum < MIN_CHOOSE_PIC) {
            Toast.makeText(context, "请选择至少" + MIN_CHOOSE_PIC + "张图片", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(context, PuzzleActivity.class);
            intent.putExtra("pics", choosePicList);
            startActivity(intent);
            finish();
        }
    }

    private void addPic(int position) {
        if (choosePicNum >= MAX_CHOOSE_PIC) {
            Toast.makeText(context, "图片不能超过" + MAX_CHOOSE_PIC + "张", Toast.LENGTH_SHORT).show();
            return;
        }

        ImageBean imageBean = mCurrentGroup.getImageSets().get(position);
        choosePicList.add(imageBean);
        selectAdapter.notifyDataSetChanged();
        choosePicNum++;
        setChoosePicText(choosePicNum);
        if (choosePicNum >= MAX_CHOOSE_PIC) {
            picSelectRv.smoothScrollToPosition(picSelectRv.getBottom());
        }
    }

    private void deletePic(int position) {
        choosePicList.remove(position);
        selectAdapter.notifyDataSetChanged();
        choosePicNum--;
        setChoosePicText(choosePicNum);
    }


    private class getCurrentGrop extends AsyncTask<String, Void, ImageGroup> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected ImageGroup doInBackground(String... strings) {
            return new ImageGroup("ALL", PicCursorUtil.getAllPhotos(context));
        }

        @Override
        protected void onPostExecute(ImageGroup imageGroup) {

            mCurrentGroup = imageGroup;
            allPicAdapter.taggle(mCurrentGroup.getImageSets());
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.start_tv:
                intentToPuzzle();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
