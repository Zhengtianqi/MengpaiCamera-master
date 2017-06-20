package com.stickercamera.app.ui;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.common.util.DataUtils;
import com.common.util.StringUtils;
import com.customview.LabelView;
import com.github.skykai.stickercamera.R;
import com.melnykov.fab.FloatingActionButton;
import com.stickercamera.App;
import com.stickercamera.AppConstants;
import com.stickercamera.app.camera.CameraManager;
import com.stickercamera.app.model.FeedItem;
import com.stickercamera.app.model.TagItem;
import com.stickercamera.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by 郑天祺 on 2017/6/1.
 */

public class StickerCamera extends BaseActivity {
    //使用ButterKnife进行绑定
    @InjectView(R.id.fab)
    FloatingActionButton fab;
    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private List<FeedItem> feedList;
    private PictureAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化ButterKnife;
        ButterKnife.inject(this);

        //EventBus是一款针对Android优化的发布/订阅事件总线。主要功能是替代Intent,Handler,BroadCast在Fragment，Activity，Service，线程之间传递消息.优点是开销小，代码更优雅。
        /*EventBus.getDefault().register(this);*///让EventBus扫描当前类，把所有onEvent开头的方法记录下来
        //初始化布局方法
        initView();

        //判断，如果没有照片则打开相机
        String str = DataUtils.getStringPreferences(App.getApp(), AppConstants.FEED_INFO);
        if (StringUtils.isNotEmpty(str)) {
            feedList = JSON.parseArray(str, FeedItem.class);
        }
        if (feedList == null) {
            CameraManager.getInst().openCamera(StickerCamera.this);
        } else {
            mAdapter.setList(feedList);
        }

    }
    //解除注册
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    //初始化UI，隐藏两个按钮
    private void initView() {
        titleBar.hideLeftBtn();
        titleBar.hideRightBtn();
        //设置recycler的位置 绑定试图适配器，底部悬浮按钮的点击事件，点击后打开自定义相机。在包.app.camera,网上的相机 可以改参数。
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PictureAdapter();
        mRecyclerView.setAdapter(mAdapter);
        fab.setOnClickListener(v -> CameraManager.getInst().openCamera(StickerCamera.this));
    }

    //通过xml初始化菜单栏
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    //设置菜单栏点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //照片适配器，按照提供的方法修改参数即可
    //作用：把图片绑定到RecyclerView中
    public class PictureAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<FeedItem> items = new ArrayList<FeedItem>();

        public void setList(List<FeedItem> list) {
            if (items.size() > 0) {
                items.clear();
            }
            items.addAll(list);
        }
        //onCreateViewHolder:主要返回的是拿到布局，进而返回ViewHolder
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_picture, parent, false);
            return new ViewHolder(v);
        }
        //onBindViewHolder：拿到ViewHolder中的控件对其赋值操作
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            FeedItem feedItem = items.get(position);
            holder.picture.setImageBitmap(BitmapFactory.decodeFile(feedItem.getImgPath()));
            holder.setTagList(feedItem.getTagList());

        }
        //getItemCount()：返回数组的size()
        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public void onViewRecycled(ViewHolder holder) {
            // 将标签移除,避免回收使用时标签重复
            holder.pictureLayout.removeViews(1, holder.pictureLayout.getChildCount() - 1);
            super.onViewRecycled(holder);
        }
        //onViewAttachedToWindow：当适配器创建的view（即列表项view）被窗口分离（即滑动离开了当前窗口界面）就会被调用）
        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            // 这里可能有问题 延迟200毫秒加载是为了等pictureLayout已经在屏幕上显示getWidth才为具体的值
            holder.pictureLayout.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    for (TagItem feedImageTag : holder.getTagList()) {
                        LabelView tagView = new LabelView(StickerCamera.this);
                        tagView.init(feedImageTag);
                        tagView.draw(holder.pictureLayout,
                                (int) (feedImageTag.getX() * ((double) holder.pictureLayout.getWidth() / (double) 1242)),
                                (int) (feedImageTag.getY() * ((double) holder.pictureLayout.getWidth() / (double) 1242)),
                                feedImageTag.isLeft());
                        tagView.wave();
                    }
                }
            }, 200);
        }
    }
    //ViewHolder出现在适配器里
    public static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.pictureLayout)
        RelativeLayout pictureLayout;
        @InjectView(R.id.picture)
        ImageView picture;

        private List<TagItem> tagList = new ArrayList<>();

        public List<TagItem> getTagList() {
            return tagList;
        }
        //为的是listview滚动的时候快速设置值，而不必每次都重新创建很多对象，从而提升性能
        public void setTagList(List<TagItem> tagList) {
            if (this.tagList.size() > 0) {
                this.tagList.clear();
            }
            this.tagList.addAll(tagList);
        }

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

}
